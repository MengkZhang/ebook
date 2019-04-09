package com.tzpt.cloudlibrary.modle.remote.newdownload;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tzpt.cloudlibrary.modle.remote.newdownload.core.IdentifiedTask;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.Util;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.breakpoint.BreakpointInfo;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.download.DownloadStrategy;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Administrator on 2018/8/6.
 */

public class DownloadTask extends IdentifiedTask implements Comparable<DownloadTask> {
    public static final String IMG = "img";
    public static final String EPUB = "epub";
    public static final String VIDEO = "video";
    @NonNull
    private final String mUrl;
    private final Uri mUri;

    @Nullable
    private BreakpointInfo mInfo;

    private final int mReadBufferSize;
    private final int mFlushBufferSize;

    private final boolean mAutoCallbackToUIThread;

    private final boolean mIsTimelinessCheck;

    private volatile DownloadListener mListener;

    private final int mMinIntervalMillisCallbackProcess;
    private final AtomicLong mLastCallbackProcessTimestamp;
    private final String mFileType;
    private final boolean mIsCheckNet;

    @NonNull
    private final DownloadStrategy.FilenameHolder mFilenameHolder;
    @NonNull
    private final File mDirectoryFile;
    @Nullable
    private File mTargetFile;

    private Map<String, String> mHeaderMapFields;


    public DownloadTask(@NonNull String url, Uri uri, @NonNull String fileType, int readBufferSize,
                        int flushBufferSize, boolean autoCallbackToUIThread,
                        int minIntervalMillisCallbackProcess, @Nullable String filename,
                        boolean timelinessCheck, boolean fromStart, boolean isCheckNet) {
        this.mUrl = url;
        this.mUri = uri;
        this.mReadBufferSize = readBufferSize;
        this.mFlushBufferSize = flushBufferSize;
        this.mAutoCallbackToUIThread = autoCallbackToUIThread;
        this.mMinIntervalMillisCallbackProcess = minIntervalMillisCallbackProcess;
        this.mLastCallbackProcessTimestamp = new AtomicLong();
        this.mIsTimelinessCheck = timelinessCheck;
        this.mFileType = fileType;
        this.mIsCheckNet = isCheckNet;

        if (Util.isUriFileScheme(uri)) {
            final File file = new File(uri.getPath());
            if (file.exists() && file.isDirectory()) {
                mDirectoryFile = file;
            } else {
                // not exist or is file.
                if (file.exists()) {
                    // is file
                    if (!Util.isEmpty(filename) && !file.getName().equals(filename)) {
                        throw new IllegalArgumentException("Uri already provided filename!");
                    }
                    filename = file.getName();
                    mDirectoryFile = Util.getParentFile(file);
                } else {
                    // not exist
                    if (Util.isEmpty(filename)) {
                        // filename is not provided, so we use the filename on path
                        filename = file.getName();
                        mDirectoryFile = Util.getParentFile(file);
                    } else {
                        // filename is provided, so the path on file is directory
                        mDirectoryFile = file;
                    }
                }
            }
        } else {
            mDirectoryFile = new File(uri.getPath());
        }

        if (Util.isEmpty(filename)) {
            mFilenameHolder = new DownloadStrategy.FilenameHolder();
        } else {
            mFilenameHolder = new DownloadStrategy.FilenameHolder(filename);
            mTargetFile = new File(mDirectoryFile, filename);
        }


        PDownload.with().store().createAndInsert(this, fromStart);

    }

    public synchronized void addRequestHeader(String key, String value) {
        if (mHeaderMapFields == null)
            mHeaderMapFields = new HashMap<>();
        mHeaderMapFields.put(key, value);
    }

    @Nullable
    public Map<String, String> getHeaderMapFields() {
        return this.mHeaderMapFields;
    }

    public int getFlushBufferSize() {
        return this.mFlushBufferSize;
    }

    @NonNull
    public DownloadStrategy.FilenameHolder getFilenameHolder() {
        return mFilenameHolder;
    }

    public int getMinIntervalMillisCallbackProcess() {
        return mMinIntervalMillisCallbackProcess;
    }

    public boolean isAutoCallbackToUIThread() {
        return mAutoCallbackToUIThread;
    }

    public boolean isTimelinessCheck() {
        return mIsTimelinessCheck;
    }

    public DownloadListener getListener() {
        return this.mListener;
    }

    public int getReadBufferSize() {
        return this.mReadBufferSize;
    }

    public String getFileType() {
        return this.mFileType;
    }

    public boolean isCheckNet() {
        return mIsCheckNet;
    }

    @Nullable
    public BreakpointInfo getInfo() {
        if (mInfo == null)
            mInfo = PDownload.with().store().get(mUrl);
        return mInfo;
    }

    void setBreakpointInfo(@NonNull BreakpointInfo info) {
        this.mInfo = info;
    }

    @Nullable
    public File getFile() {
        final String filename = mFilenameHolder.get();
        if (filename == null)
            return null;
        if (mTargetFile == null)
            mTargetFile = new File(mDirectoryFile, filename);

        return mTargetFile;
    }

    public Uri getUri() {
        return mUri;
    }

    long getLastCallbackProcessTs() {
        return mLastCallbackProcessTimestamp.get();
    }

    void setLastCallbackProcessTs(long lastCallbackProcessTimestamp) {
        this.mLastCallbackProcessTimestamp.set(lastCallbackProcessTimestamp);
    }

    public void enqueue(DownloadListener listener) {
        this.mListener = listener;
        PDownload.with().downloadDispatcher().enqueue(this);
    }

    public void execute(DownloadListener listener) {
        this.mListener = listener;
        PDownload.with().downloadDispatcher().execute(this);
    }

//    public boolean pause() {
//        return PDownload.with().downloadDispatcher().pause(this);
//    }
//
//    public void cancel() {
//        PDownload.with().downloadDispatcher().cancel(this);
//    }

    @NonNull
    @Override
    public String getUrl() {
        return mUrl;
    }

    @NonNull
    @Override
    public File getParentFile() {
        return mDirectoryFile;
    }

    @Nullable
    @Override
    public String getFilename() {
        return mFilenameHolder.get();
    }

    @Override
    public int compareTo(@NonNull DownloadTask o) {
        return 0;
    }

    public static class Builder {
        @NonNull
        final String url;
        @NonNull
        final Uri uri;
        @NonNull
        final String fileType;

        public Builder(@NonNull String url, @NonNull File directoryFile, @NonNull String fileType) {
            this.url = url;
            this.uri = Uri.fromFile(directoryFile);
            this.fileType = fileType;
        }

        static final int DEFAULT_READ_BUFFER_SIZE = 4096/* byte **/;
        private int readBufferSize = DEFAULT_READ_BUFFER_SIZE;
        static final int DEFAULT_FLUSH_BUFFER_SIZE = 16384/* byte **/;
        private int flushBufferSize = DEFAULT_FLUSH_BUFFER_SIZE;

        static final boolean DEFAULT_AUTO_CALLBACK_TO_UI_THREAD = false;
        private boolean autoCallbackToUIThread = DEFAULT_AUTO_CALLBACK_TO_UI_THREAD;

        static final int DEFAULT_MIN_INTERVAL_MILLIS_CALLBACK_PROCESS = 3000/* millis **/;
        private int minIntervalMillisCallbackProcess = DEFAULT_MIN_INTERVAL_MILLIS_CALLBACK_PROCESS;

        static final boolean DEFAULT_TIMELINESS_CHECK = false;
        private boolean timelinessCheck = DEFAULT_TIMELINESS_CHECK;

        static final boolean DEFAULT_IS_CHECK_NET = true;
        private boolean isCheckNet = DEFAULT_IS_CHECK_NET;

        static final boolean DEFAULT_IS_FROM_START = false;
        private boolean isFromStart = DEFAULT_IS_FROM_START;

        private String filename;

        public Builder setAutoCallbackToUIThread(boolean autoCallbackToUIThread) {
            this.autoCallbackToUIThread = autoCallbackToUIThread;
            return this;
        }

        public Builder setMinIntervalMillisCallbackProcess(int minIntervalMillisCallbackProcess) {
            this.minIntervalMillisCallbackProcess = minIntervalMillisCallbackProcess;
            return this;
        }

        public Builder setReadBufferSize(int readBufferSize) {
            if (readBufferSize < 0) throw new IllegalArgumentException("Value must be positive!");

            this.readBufferSize = readBufferSize;
            return this;
        }

        public Builder setFlushBufferSize(int flushBufferSize) {
            if (flushBufferSize < 0) throw new IllegalArgumentException("Value must be positive!");

            this.flushBufferSize = flushBufferSize;
            return this;
        }

        public Builder setFilename(String filename) {
            this.filename = filename;
            return this;
        }

        public Builder setTimelinessCheck(boolean timelinessCheck) {
            this.timelinessCheck = timelinessCheck;
            return this;
        }

        public Builder setIsCheck(boolean isCheckNet) {
            this.isCheckNet = isCheckNet;
            return this;
        }

        public Builder setFromStart() {
            this.isFromStart = true;
            return this;
        }

        public DownloadTask build() {
            return new DownloadTask(url, uri, fileType, readBufferSize, flushBufferSize,
                    autoCallbackToUIThread, minIntervalMillisCallbackProcess,
                    filename, timelinessCheck, isFromStart, isCheckNet);
        }
    }


    public static class TaskHideWrapper {
        public static long getLastCallbackProcessTs(DownloadTask task) {
            return task.getLastCallbackProcessTs();
        }

        public static void setLastCallbackProcessTs(DownloadTask task,
                                                    long lastCallbackProcessTimestamp) {
            task.setLastCallbackProcessTs(lastCallbackProcessTimestamp);
        }

        public static void setBreakpointInfo(@NonNull DownloadTask task,
                                             @NonNull BreakpointInfo info) {
            task.setBreakpointInfo(info);
        }
    }

}
