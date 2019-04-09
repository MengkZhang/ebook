package com.tzpt.cloudlibrary.modle.remote.newdownload.core.download;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.tzpt.cloudlibrary.modle.remote.newdownload.DownloadTask;
import com.tzpt.cloudlibrary.modle.remote.newdownload.PDownload;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.Util;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.breakpoint.BreakpointInfo;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.cause.ResumeFailedCause;

import java.io.File;

import static com.tzpt.cloudlibrary.modle.remote.newdownload.core.cause.ResumeFailedCause.FILE_NOT_EXIST;
import static com.tzpt.cloudlibrary.modle.remote.newdownload.core.cause.ResumeFailedCause.INFO_DIRTY;
import static com.tzpt.cloudlibrary.modle.remote.newdownload.core.cause.ResumeFailedCause.OUTPUT_STREAM_NOT_SUPPORT;

/**
 * Created by Administrator on 2018/8/6.
 */

public class BreakpointLocalCheck {
    private boolean dirty;
    private boolean fileExist;
    private boolean infoRight;
    private boolean outputStreamSupport;

    private final DownloadTask task;
    private final BreakpointInfo info;

    BreakpointLocalCheck(@NonNull DownloadTask task, @NonNull BreakpointInfo info) {
        this.task = task;
        this.info = info;
    }

    boolean isDirty() {
        return dirty;
    }

    /**
     * 文件是否存在
     *
     * @return true 存在 false 不存在
     */
    private boolean isFileExistToResume() {
        final Uri uri = task.getUri();
        if (Util.isUriContentScheme(uri)) {
            return Util.getSizeFromContentUri(uri) > 0;
        } else {
            final File file = task.getFile();
            return file != null && file.exists();
        }
    }

    /**
     * 下载信息是否正确
     *
     * @return true 正确 false 不正确
     */
    private boolean isInfoRightToResume() {
        if (info.getFile() == null)
            return false;
        final File fileOnTask = task.getFile();
        if (!info.getFile().equals(fileOnTask))
            return false;
        if (info.getFile().length() > info.getTotalLength())
            return false;
        if (info.getTotalLength() <= 0)
            return false;

        return true;
    }

    private boolean isOutputStreamSupportResume() {
        final boolean supportSeek = PDownload.with().outputStreamFactory().supportSeek();
        if (supportSeek)
            return true;

        return true;
    }


    public void check() {
        fileExist = isFileExistToResume();
        infoRight = isInfoRightToResume();
        outputStreamSupport = isOutputStreamSupportResume();
        dirty = !infoRight || !fileExist || !outputStreamSupport;
    }

    @Override
    public String toString() {
        return "fileExist[" + fileExist + "] "
                + "infoRight[" + infoRight + "] "
                + "outputStreamSupport[" + outputStreamSupport + "] "
                + super.toString();
    }
}
