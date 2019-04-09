package com.tzpt.cloudlibrary.modle.remote.newdownload;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tzpt.cloudlibrary.modle.remote.newdownload.core.cause.EndCause;

/**
 * Created by Administrator on 2018/8/6.
 */

public interface DownloadListener {
    /**
     * 等待下载
     *
     * @param fileInfo
     */
    void taskWait(DownloadTask task);

    void taskStart(@NonNull DownloadTask task);

    void connectTrialStart(@NonNull DownloadTask task);

    void connectTrialEnd(@NonNull DownloadTask task);

    void connectStart(@NonNull DownloadTask task);

    void connectEnd(@NonNull DownloadTask task);

    void fetchStart(@NonNull DownloadTask task);

    void fetchProgress(@NonNull DownloadTask task);

    void fetchEnd(@NonNull DownloadTask task);

    void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause);
}
