package com.tzpt.cloudlibrary.modle.remote.newdownload.core.download;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.JsonParseException;
import com.tzpt.cloudlibrary.modle.remote.newdownload.DownloadTask;
import com.tzpt.cloudlibrary.modle.remote.newdownload.PDownload;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.NamedRunnable;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.Util;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.breakpoint.BreakpointInfo;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.breakpoint.DownloadStore;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.cause.EndCause;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.file.MultiPointOutputStream;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.file.ProcessFileStrategy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2018/8/3.
 */

public class DownloadCall extends NamedRunnable implements Comparable<DownloadCall> {
    private static final ExecutorService EXECUTOR = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
            60, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(),
            Util.threadFactory("PDownload Block", false));

    private static final String TAG = "DownloadCall";

    private static final int MAX_COUNT_RETRY_FOR_PRECONDITION_FAILED = 1;
    public final DownloadTask task;

    @Nullable
    private volatile DownloadCache cache;
    private volatile boolean canceled;
    private volatile boolean finishing;

    private volatile Thread currentThread;
    @NonNull
    private final DownloadStore store;

    public final boolean mAsyncExecuted;

    private DownloadCall(DownloadTask task, boolean asyncExecuted, @NonNull DownloadStore store) {
        super("download call: " + task.getFilename());
        this.task = task;
        this.store = store;
        this.mAsyncExecuted = asyncExecuted;
    }

    public static DownloadCall create(DownloadTask task, boolean asyncExecuted, DownloadStore store) {
        return new DownloadCall(task, asyncExecuted, store);
    }

    public boolean cancel(boolean del) {
        synchronized (this) {
            if (canceled)
                return false;
            if (finishing)
                return false;
            this.canceled = true;
        }

        PDownload.with().downloadDispatcher().flyingCanceled();

        final DownloadCache cache = this.cache;
        if (cache != null)
            cache.setUserCanceled();

        if (currentThread != null) {
            currentThread.interrupt();
        }

        if (cache != null)
            cache.getOutputStream().cancelAsync();

        if (del) {
            if (this.task.getFile() != null) {
                this.task.getFile().delete();
            }
        }
        return true;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public boolean isFinishing() {
        return finishing;
    }

    @Override
    public void execute() throws InterruptedException {
        currentThread = Thread.currentThread();

        boolean retry;
        int retryCount = 0;

        // ready param
        final PDownload pDownload = PDownload.with();
        final ProcessFileStrategy fileStrategy = pDownload.processFileStrategy();

        // inspect task start
        inspectTaskStart();
        do {
            // 0. check basic param before start
            if (task.getUrl().length() <= 0) {
                this.cache = new DownloadCache.PreError(new IOException("unexpected url: " + task.getUrl()));
                break;
            }

            if (canceled)
                break;

            // 1. create basic info if not exist
            final BreakpointInfo info = store.get(task.getUrl());
            if (info != null) {
                DownloadTask.TaskHideWrapper.setBreakpointInfo(task, info);
            } else {
                break;
            }

            if (canceled)
                break;

            // ready cache.
            final DownloadCache cache = createCache(info);
            this.cache = cache;

            if (task.isTimelinessCheck()) {
                final ServerTimeRequest serverTimeRequest = createServerTimeRequest();
                try {
                    serverTimeRequest.executeRequest();
                } catch (IOException e) {
                    cache.catchException(e);
                    break;
                } catch (JsonParseException e) {
                    cache.catchException(new IOException("Gson parse failed"));
                    break;
                }
            }

            try {
                final BreakpointLocalCheck localCheck = createLocalCheck(info);
                localCheck.check();
                if (localCheck.isDirty()) {
                    // 6. local file error download from beginning
                    fileStrategy.discardProcess(task);
                }
            } catch (IOException e) {
                cache.setUnknownError(e);
                break;
            }

            // 7. start with cache and info.
            start(cache, info);

            if (canceled)
                break;

            // 8. retry if precondition failed.
            if (cache.isPreconditionFailed()
                    && retryCount++ < MAX_COUNT_RETRY_FOR_PRECONDITION_FAILED) {
//                store.remove(task.getId());
                retry = true;
            } else {
                retry = false;
            }
        } while (retry);

        // finish
        finishing = true;

        final DownloadCache cache = this.cache;
        if (canceled || cache == null)
            return;

        final EndCause cause;
        Exception realCause = null;
        if (cache.isServerCanceled()
                || cache.isUnknownError()
                || cache.isPreconditionFailed()) {
            // error
            cause = EndCause.ERROR;
            realCause = cache.getRealCause();
        } else if (cache.isFileBusyAfterRun()) {
            cause = EndCause.FILE_BUSY;
        } else if (cache.isPreAllocateFailed()) {
            cause = EndCause.PRE_ALLOCATE_FAILED;
            realCause = cache.getRealCause();
        } else if (cache.isWifiRequire()) {
            cause = EndCause.WIFI_REQUIRE;
            realCause = cache.getRealCause();
        } else {
            cause = EndCause.COMPLETED;
        }
        inspectTaskEnd(cache, cause, realCause);
    }

    private void inspectTaskStart() {
        PDownload.with().callbackDispatcher().dispatch().taskStart(task);
    }

    private void inspectTaskEnd(DownloadCache cache, @NonNull EndCause cause, @Nullable Exception realCause) {
        // non-cancel handled on here
        if (cause == EndCause.CANCELED) {
            throw new IllegalAccessError("can't recognize cancelled  on here");
        }

        synchronized (this) {
            if (canceled)
                return;
            finishing = true;
        }

        if (cause == EndCause.COMPLETED) {
            PDownload.with().processFileStrategy().completeProcessStream(cache.getOutputStream(), task);
        }

        PDownload.with().callbackDispatcher().dispatch().taskEnd(task, cause, realCause);
    }

    // this method is convenient for unit-test.
    private DownloadCache createCache(@NonNull BreakpointInfo info) {
        final MultiPointOutputStream outputStream = PDownload.with().processFileStrategy()
                .createProcessStream(task, info, store);
        return new DownloadCache(outputStream);
    }

    private void start(final DownloadCache cache, BreakpointInfo info) throws InterruptedException {
        if (canceled) {
            return;
        }
        ArrayList<Future> futures = new ArrayList<>();
        try {
            futures.add(submitChain(DownloadChain.createChain(task, info, cache, store)));

            for (Future future : futures) {
                if (!future.isDone()) {
                    try {
                        future.get();
                    } catch (CancellationException | ExecutionException ignore) {
                    }
                }
            }
        } catch (Throwable t) {
            for (Future future : futures) {
                future.cancel(true);
            }
            throw t;
        }
    }

    @Override
    protected void interrupted(InterruptedException e) {
    }

    @Override
    protected void finished() {
        PDownload.with().downloadDispatcher().finish(this);
    }

    @NonNull
    private BreakpointLocalCheck createLocalCheck(@NonNull BreakpointInfo info) {
        return new BreakpointLocalCheck(task, info);
    }

    private ServerTimeRequest createServerTimeRequest() {
        return new ServerTimeRequest(task);
    }

    private Future<?> submitChain(DownloadChain chain) {
        return EXECUTOR.submit(chain);
    }

    public boolean equalsTask(@NonNull DownloadTask task) {
        return this.task.equals(task);
    }

    @Nullable
    public File getFile() {
        return this.task.getFile();
    }

    @Override
    public int compareTo(@NonNull DownloadCall o) {
        return 0;
    }
}
