package com.tzpt.cloudlibrary.modle.remote.newdownload.core.download;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.tzpt.cloudlibrary.modle.remote.newdownload.DownloadListener;
import com.tzpt.cloudlibrary.modle.remote.newdownload.DownloadTask;
import com.tzpt.cloudlibrary.modle.remote.newdownload.PDownload;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.Util;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.breakpoint.BreakpointInfo;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.breakpoint.DownloadStore;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.connection.DownloadConnection;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.exception.InterruptException;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.file.MultiPointOutputStream;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.interceptor.BreakpointInterceptor;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.interceptor.FetchDataInterceptor;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.interceptor.Interceptor;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.interceptor.RetryInterceptor;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.interceptor.connect.CallServerInterceptor;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.interceptor.connect.HeaderInterceptor;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.interceptor.connect.RedirectInterceptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Administrator on 2018/8/6.
 */

public class DownloadChain implements Runnable {

    private static final ExecutorService EXECUTOR = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
            60, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(),
            Util.threadFactory("PDownload Cancel Block", false));

    private static final String TAG = "DownloadChain";

    @NonNull
    private final DownloadTask task;
    @NonNull
    private final BreakpointInfo info;
    @NonNull
    private final DownloadCache cache;

    private final List<Interceptor.Connect> connectInterceptorList = new ArrayList<>();
    private final List<Interceptor.Fetch> fetchInterceptorList = new ArrayList<>();
    private int connectIndex = 0;
    private int fetchIndex = 0;

    private long responseContentLength;
    private volatile DownloadConnection connection;

    //    private long noCallbackIncreaseBytes;
    private volatile Thread currentThread;

    private DownloadStore store;

    static DownloadChain createChain(DownloadTask task,
                                     @NonNull BreakpointInfo info,
                                     @NonNull DownloadCache cache,
                                     DownloadStore store) {
        return new DownloadChain(task, info, cache, store);
    }

    private DownloadChain(@NonNull DownloadTask task, @NonNull BreakpointInfo info,
                          @NonNull DownloadCache cache, DownloadStore store) {
        this.task = task;
        this.cache = cache;
        this.info = info;
        this.store = store;
    }

    public long getResponseContentLength() {
        return responseContentLength;
    }

    public void setResponseContentLength(long responseContentLength) {
        this.responseContentLength = responseContentLength;
    }

    public void cancel() {
        if (finished.get() || this.currentThread == null) return;

        currentThread.interrupt();
    }

    @NonNull
    public DownloadTask getTask() {
        return task;
    }

    @NonNull
    public BreakpointInfo getInfo() {
        return this.info;
    }

    @NonNull
    public DownloadStore getStore() {
        return this.store;
    }

    public synchronized void setConnection(@NonNull DownloadConnection connection) {
        this.connection = connection;
    }

    @NonNull
    public DownloadCache getCache() {
        return cache;
    }

    public void setRedirectLocation(String location) {
        this.cache.setRedirectLocation(location);
    }

    public MultiPointOutputStream getOutputStream() {
        return this.cache.getOutputStream();
    }

    @Nullable
    public synchronized DownloadConnection getConnection() {
        return this.connection;
    }

    @NonNull
    public synchronized DownloadConnection getConnectionOrCreate() throws IOException {
        if (cache.isInterrupt())
            throw InterruptException.SIGNAL;

        if (connection == null) {
            final String url;
            final String redirectLocation = cache.getRedirectLocation();
            if (redirectLocation != null) {
                url = redirectLocation;
            } else {
                url = info.getUrl();
            }

            connection = PDownload.with().connectionFactory().create(url);
        }
        return connection;
    }

//    public void increaseCallbackBytes(long increaseBytes) {
//        this.noCallbackIncreaseBytes += increaseBytes;
//    }
//
//    public void flushNoCallbackIncreaseBytes() {
//        if (noCallbackIncreaseBytes == 0)
//            return;
//        noCallbackIncreaseBytes = 0;
//    }

    private void start() throws IOException {
        final DownloadListener listener = PDownload.with().callbackDispatcher().dispatch();
        // connect chain
        final RetryInterceptor retryInterceptor = new RetryInterceptor();
        final BreakpointInterceptor breakpointInterceptor = new BreakpointInterceptor();
        connectInterceptorList.add(retryInterceptor);
        connectInterceptorList.add(breakpointInterceptor);
        connectInterceptorList.add(new RedirectInterceptor());
        connectInterceptorList.add(new HeaderInterceptor());
        connectInterceptorList.add(new CallServerInterceptor());

        connectIndex = 0;
        final DownloadConnection.Connected connected = processConnect();
        if (cache.isInterrupt()) {
            throw InterruptException.SIGNAL;
        }

        listener.fetchStart(task);
        // fetch chain
        final FetchDataInterceptor fetchDataInterceptor =
                new FetchDataInterceptor(connected.getInputStream(), getOutputStream(), task);
        fetchInterceptorList.add(retryInterceptor);
        fetchInterceptorList.add(breakpointInterceptor);
        fetchInterceptorList.add(fetchDataInterceptor);

        fetchIndex = 0;
        processFetch();
        listener.fetchEnd(task);
//        store.updateStatus(task);
    }

    public void resetConnectForRetry() {
        connectIndex = 1;
        releaseConnection();
    }

    public synchronized void releaseConnection() {
        if (connection != null) {
            connection.release();
        }
        connection = null;
    }

    public DownloadConnection.Connected processConnect() throws IOException {
        if (cache.isInterrupt())
            throw InterruptException.SIGNAL;
        return connectInterceptorList.get(connectIndex++).interceptConnect(this);
    }

    public long processFetch() throws IOException {
        if (cache.isInterrupt())
            throw InterruptException.SIGNAL;
        return fetchInterceptorList.get(fetchIndex++).interceptFetch(this);
    }

    public long loopFetch() throws IOException {
        if (fetchIndex == fetchInterceptorList.size()) {
            // last one is fetch data interceptor
            fetchIndex--;
        }
        return processFetch();
    }

    private final AtomicBoolean finished = new AtomicBoolean(false);

    private boolean isFinished() {
        return finished.get();
    }

    @Override
    public void run() {
        if (isFinished()) {
            throw new IllegalAccessError("The chain has been finished!");
        }
        this.currentThread = Thread.currentThread();

        try {
            start();
        } catch (IOException ignored) {
            // interrupt.
            Log.e(TAG, "IOException*******" + ignored.getMessage());
        } finally {
            finished.set(true);
            releaseConnectionAsync();
        }
    }


    private void releaseConnectionAsync() {
        EXECUTOR.execute(releaseConnectionRunnable);
    }

    private final Runnable releaseConnectionRunnable = new Runnable() {
        @Override
        public void run() {
            releaseConnection();
        }
    };
}
