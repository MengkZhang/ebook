package com.tzpt.cloudlibrary.modle.remote.newdownload;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tzpt.cloudlibrary.modle.remote.newdownload.core.breakpoint.BreakpointInfo;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.cause.EndCause;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.cause.ResumeFailedCause;

/**
 * Created by Administrator on 2018/8/6.
 */

public interface DownloadMonitor {
    void taskStart(DownloadTask task);

    void taskDownloadFromBreakpoint(@NonNull DownloadTask task, @NonNull BreakpointInfo info);

    void taskDownloadFromBeginning(@NonNull DownloadTask task, @NonNull BreakpointInfo info,
                                   @Nullable ResumeFailedCause cause);
    void taskEnd(DownloadTask task, EndCause cause, @Nullable Exception realCause);
}
