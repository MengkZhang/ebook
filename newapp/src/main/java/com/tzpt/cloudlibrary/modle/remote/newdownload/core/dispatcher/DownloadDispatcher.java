package com.tzpt.cloudlibrary.modle.remote.newdownload.core.dispatcher;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tzpt.cloudlibrary.modle.remote.newdownload.DownloadListener;
import com.tzpt.cloudlibrary.modle.remote.newdownload.DownloadTask;
import com.tzpt.cloudlibrary.modle.remote.newdownload.PDownload;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.Util;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.cause.EndCause;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.download.DownloadCall;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Administrator on 2018/8/6.
 */

public class DownloadDispatcher {

    private static final String TAG = "DownloadDispatcher";

    private int mMaxParallelRunningCount = 3;

    private final List<DownloadCall> mReadyAsyncCalls;

    private final List<DownloadCall> mRunningAsyncCalls;

    private final List<DownloadCall> mRunningSyncCalls;

    // store task has been cancelled but didn't remove from runningAsyncCalls list yet.
    private final List<DownloadCall> mFinishingCalls;

    // for the case of tasks has been cancelled but didn't remove from runningAsyncCalls list yet.
    private final AtomicInteger mFlyingCanceledAsyncCallCount = new AtomicInteger();

    @Nullable
    private volatile ExecutorService mExecutorService;

    // for avoiding processCalls when doing enqueue/cancel operation
    private final AtomicInteger mSkipProceedCallCount = new AtomicInteger();

    public DownloadDispatcher() {
        this(new ArrayList<DownloadCall>(), new ArrayList<DownloadCall>(),
                new ArrayList<DownloadCall>(), new ArrayList<DownloadCall>());
    }

    private DownloadDispatcher(List<DownloadCall> readyAsyncCalls,
                               List<DownloadCall> runningAsyncCalls,
                               List<DownloadCall> runningSyncCalls,
                               List<DownloadCall> finishingCalls) {
        this.mReadyAsyncCalls = readyAsyncCalls;
        this.mRunningAsyncCalls = runningAsyncCalls;
        this.mFinishingCalls = finishingCalls;
        this.mRunningSyncCalls = runningSyncCalls;
    }

    private synchronized ExecutorService getExecutorService() {
        if (mExecutorService == null) {
            mExecutorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                    60, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(),
                    Util.threadFactory("PDownload Download", false));
        }

        return mExecutorService;
    }

    public void enqueue(DownloadTask task) {
        mSkipProceedCallCount.incrementAndGet();
        enqueueLocked(task);
        mSkipProceedCallCount.decrementAndGet();
    }

    private synchronized void enqueueLocked(DownloadTask task) {
//        if (inspectCompleted(task))
//            return;
        if (inspectForConflict(task))
            return;

//        final int originReadyAsyncCallSize = mReadyAsyncCalls.size();
        enqueueIgnorePriority(task);
//        if (originReadyAsyncCallSize != mReadyAsyncCalls.size())
//            Collections.sort(mReadyAsyncCalls);//TODO 不需要排序
    }

    public void execute(DownloadTask task) {
        Util.d(TAG, "execute: " + task);
        final DownloadCall call;

        synchronized (this) {
            if (inspectForConflict(task)) return;

            call = DownloadCall.create(task, false, PDownload.with().store());
            mRunningSyncCalls.add(call);
        }
        getExecutorService().execute(call);
    }

    private synchronized void enqueueIgnorePriority(DownloadTask task) {
        final DownloadCall call = DownloadCall.create(task, true, PDownload.with().store());
        PDownload.with().callbackDispatcher().dispatch().taskWait(task);
        if (runningAsyncSize() < mMaxParallelRunningCount) {
            mRunningAsyncCalls.add(call);
            getExecutorService().execute(call);
        } else {
            mReadyAsyncCalls.add(call);
        }
    }

    public boolean pause(String url) {
        mSkipProceedCallCount.incrementAndGet();
        final boolean result = cancelLocked(url, false);
        mSkipProceedCallCount.decrementAndGet();
        processCalls();
        return result;
    }

    public void pause(List<String> urls) {
        mSkipProceedCallCount.incrementAndGet();
        cancelLocked(urls, false);
        mSkipProceedCallCount.decrementAndGet();
        processCalls();
    }

    public boolean cancel(String url) {
        mSkipProceedCallCount.incrementAndGet();
        final boolean result = cancelLocked(url, true);
        mSkipProceedCallCount.decrementAndGet();
        processCalls();
        return result;
    }

    public void cancel(List<String> urls) {
        mSkipProceedCallCount.incrementAndGet();
        cancelLocked(urls, true);
        mSkipProceedCallCount.decrementAndGet();
        processCalls();
    }

    private synchronized boolean cancelLocked(String url, boolean del) {
        final List<DownloadCall> needCallbackCalls = new ArrayList<>();
        final List<DownloadCall> needCancelCalls = new ArrayList<>();

        try {
            filterCanceledCalls(url, needCallbackCalls, needCancelCalls);
        } finally {
            handleCanceledCalls(needCallbackCalls, needCancelCalls, del);
        }

        return needCallbackCalls.size() > 0 || needCancelCalls.size() > 0;
    }

    private synchronized void cancelLocked(List<String> urls, boolean del) {
        final List<DownloadCall> needCallbackCalls = new ArrayList<>();
        final List<DownloadCall> needCancelCalls = new ArrayList<>();
        try {
            for (String url : urls) {
                filterCanceledCalls(url, needCallbackCalls, needCancelCalls);
            }
        } finally {
            handleCanceledCalls(needCallbackCalls, needCancelCalls, del);
        }
    }

    private synchronized void filterCanceledCalls(@NonNull String url,
                                                  @NonNull List<DownloadCall> needCallbackCalls,
                                                  @NonNull List<DownloadCall> needCancelCalls) {
        for (Iterator<DownloadCall> i = mReadyAsyncCalls.iterator(); i.hasNext(); ) {
            DownloadCall call = i.next();
            if (call.task.getUrl().equals(url)) {
                if (call.isCanceled() || call.isFinishing())
                    return;
                i.remove();
                needCallbackCalls.add(call);
                return;
            }
        }

        for (DownloadCall call : mRunningAsyncCalls) {
            if (call.task.getUrl().equals(url)) {
                needCallbackCalls.add(call);
                needCancelCalls.add(call);
                return;
            }
        }

        for (DownloadCall call : mRunningSyncCalls) {
            if (call.task.getUrl().equals(url)) {
                needCallbackCalls.add(call);
                needCancelCalls.add(call);
                return;
            }
        }
    }

    private synchronized void handleCanceledCalls(@NonNull List<DownloadCall> needCallbackCalls,
                                                  @NonNull List<DownloadCall> needCancelCalls,
                                                  boolean del) {
        if (!needCancelCalls.isEmpty()) {
            for (DownloadCall call : needCancelCalls) {
                if (!call.cancel(del)) {
                    if (del) {
                        PDownload.with().store().del(call.task.getUrl());
                    }
                    needCallbackCalls.remove(call);
                }
            }
        }

        if (!needCallbackCalls.isEmpty()) {
            if (needCallbackCalls.size() <= 1) {
                final DownloadCall call = needCallbackCalls.get(0);
                if (!del) {
                    PDownload.with().callbackDispatcher().dispatch().taskEnd(call.task, EndCause.CANCELED, null);
                } else {
                    PDownload.with().store().del(call.task.getUrl());
                }
            } else {
                List<DownloadTask> callbackCanceledTasks = new ArrayList<>();
                for (DownloadCall call : needCallbackCalls) {
                    callbackCanceledTasks.add(call.task);
                }
                PDownload.with().callbackDispatcher().endTasksWithCanceled(callbackCanceledTasks, del);
            }
        }
    }

    @Nullable
    public synchronized DownloadTask findSameTask(DownloadTask task) {
        for (DownloadCall call : mReadyAsyncCalls) {
            if (call.isCanceled()) continue;
            if (call.equalsTask(task))
                return call.task;
        }

        for (DownloadCall call : mRunningAsyncCalls) {
            if (call.isCanceled()) continue;
            if (call.equalsTask(task))
                return call.task;
        }

        for (DownloadCall call : mRunningSyncCalls) {
            if (call.isCanceled()) continue;
            if (call.equalsTask(task)) return call.task;
        }
        return null;
    }

    public synchronized void flyingCanceled() {
        mFlyingCanceledAsyncCallCount.incrementAndGet();
    }

    public synchronized void finish(DownloadCall call) {
        final boolean asyncExecuted = call.mAsyncExecuted;
        final Collection<DownloadCall> calls;
        if (mFinishingCalls.contains(call)) {
            calls = mFinishingCalls;
        } else if (asyncExecuted) {
            calls = mRunningAsyncCalls;
        } else {
            calls = mRunningSyncCalls;
        }

        if (!calls.remove(call))
            throw new AssertionError("Call wasn't in-flight!");
        if (asyncExecuted && call.isCanceled())
            mFlyingCanceledAsyncCallCount.decrementAndGet();
        if (asyncExecuted)
            processCalls();
    }

    private synchronized boolean isFileConflictAfterRun(@NonNull DownloadTask task) {
        final File file = task.getFile();
        if (file == null) return false;

        // Other one is running, cancel the current task.
        for (DownloadCall syncCall : mRunningSyncCalls) {
            if (syncCall.isCanceled() || syncCall.task == task) continue;

            final File otherFile = syncCall.task.getFile();
            if (otherFile != null && file.equals(otherFile)) {
                return true;
            }
        }

        for (DownloadCall asyncCall : mRunningAsyncCalls) {
            if (asyncCall.isCanceled() || asyncCall.task == task) continue;

            final File otherFile = asyncCall.task.getFile();
            if (otherFile != null && file.equals(otherFile)) {
                return true;
            }
        }

        return false;
    }

    private boolean inspectForConflict(@NonNull DownloadTask task) {
        return inspectForConflict(task, mReadyAsyncCalls)
                || inspectForConflict(task, mRunningAsyncCalls)
                || inspectForConflict(task, mRunningSyncCalls);
    }

    /**
     * 检查任务是否冲突
     *
     * @param task  新建任务
     * @param calls 任务列表
     * @return true 冲突 false 不冲突
     */
    private boolean inspectForConflict(@NonNull DownloadTask task,
                                       @NonNull Collection<DownloadCall> calls) {
        final DownloadListener listener = PDownload.with().callbackDispatcher().dispatch();
        final Iterator<DownloadCall> iterator = calls.iterator();
        while (iterator.hasNext()) {
            DownloadCall call = iterator.next();
            if (call.isCanceled())
                continue;

            if (call.equalsTask(task)) {
                if (call.isFinishing()) {
                    mFinishingCalls.add(call);
                    iterator.remove();
                    return false;
                }

                listener.taskEnd(task, EndCause.SAME_TASK_BUSY, null);
                return true;
            }

            final File file = call.getFile();
            final File taskFile = task.getFile();
            if (file != null && taskFile != null && file.equals(taskFile)) {
                listener.taskEnd(task, EndCause.FILE_BUSY, null);
                return true;
            }
        }

        return false;
    }

    private synchronized void processCalls() {
        if (mSkipProceedCallCount.get() > 0)
            return;
        if (runningAsyncSize() >= mMaxParallelRunningCount)
            return;
        if (mReadyAsyncCalls.isEmpty())
            return;

        for (Iterator<DownloadCall> i = mReadyAsyncCalls.iterator(); i.hasNext(); ) {
            DownloadCall call = i.next();

            i.remove();

            final DownloadTask task = call.task;
            if (isFileConflictAfterRun(task)) {
                PDownload.with().callbackDispatcher().dispatch().taskEnd(task, EndCause.FILE_BUSY, null);
                continue;
            }

            mRunningAsyncCalls.add(call);
            getExecutorService().execute(call);

            if (runningAsyncSize() >= mMaxParallelRunningCount)
                return;
        }
    }

    private int runningAsyncSize() {
        return mRunningAsyncCalls.size() - mFlyingCanceledAsyncCallCount.get();
    }
}
