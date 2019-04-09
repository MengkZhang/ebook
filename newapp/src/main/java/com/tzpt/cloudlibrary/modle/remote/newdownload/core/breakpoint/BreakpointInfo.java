package com.tzpt.cloudlibrary.modle.remote.newdownload.core.breakpoint;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tzpt.cloudlibrary.modle.remote.newdownload.SpeedCalculator;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.Util;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.download.DownloadStrategy;

import java.io.File;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Administrator on 2018/8/6.
 */

public class BreakpointInfo {
    private final String url;
    @NonNull
    private final File parentFile;
    @Nullable
    private File targetFile;

    private final DownloadStrategy.FilenameHolder filenameHolder;

    @IntRange(from = 0)
    private long contentLength;

    private AtomicLong currentOffset;

    private SpeedCalculator mSpeedCalculator;

    public BreakpointInfo(@NonNull String url, @NonNull File parentFile,
                          @Nullable String filename) {
        this.url = url;
        this.parentFile = parentFile;

        contentLength = 0;
        currentOffset = new AtomicLong(0);

        if (Util.isEmpty(filename)) {
            filenameHolder = new DownloadStrategy.FilenameHolder();
        } else {
            filenameHolder = new DownloadStrategy.FilenameHolder(filename);
            targetFile = new File(parentFile, filename);
        }

        mSpeedCalculator = new SpeedCalculator();
    }

    public long getCurrentOffset() {
        return this.currentOffset.get();
    }

    public void setContentLength(long length) {
        this.contentLength = length;
    }

    public long getRangeRight() {
        return contentLength - 1;
    }

    public void increaseCurrentOffset(@IntRange(from = 1) long increaseLength, boolean isFetchData) {
        this.currentOffset.addAndGet(increaseLength);
        if (isFetchData) {
            mSpeedCalculator.downloading(increaseLength);
        }
    }

    public String getSpeed() {
        return mSpeedCalculator.speed();
    }

    public long getTotalOffset() {
        return currentOffset.get();
    }

    public long getTotalLength() {
        return contentLength;
    }

    public String getUrl() {
        return url;
    }

    public DownloadStrategy.FilenameHolder getFilenameHolder() {
        return filenameHolder;
    }

    @Nullable
    public File getFile() {
        final String filename = this.filenameHolder.get();
        if (filename == null) return null;
        if (targetFile == null) targetFile = new File(parentFile, filename);

        return targetFile;
    }
}
