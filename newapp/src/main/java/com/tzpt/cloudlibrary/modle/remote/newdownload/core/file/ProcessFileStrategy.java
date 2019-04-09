package com.tzpt.cloudlibrary.modle.remote.newdownload.core.file;

import android.support.annotation.NonNull;
import android.util.Log;

import com.tzpt.cloudlibrary.modle.remote.newdownload.DownloadTask;
import com.tzpt.cloudlibrary.modle.remote.newdownload.PDownload;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.breakpoint.BreakpointInfo;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.breakpoint.DownloadStore;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2018/8/6.
 */

public class ProcessFileStrategy {
    private final FileLock fileLock = new FileLock();

    @NonNull
    public MultiPointOutputStream createProcessStream(@NonNull DownloadTask task,
                                                      @NonNull BreakpointInfo info,
                                                      @NonNull DownloadStore store) {
        return new MultiPointOutputStream(task, info, store);
    }

    public void completeProcessStream(@NonNull MultiPointOutputStream processOutputStream,
                                      @NonNull DownloadTask task) {

    }

    public void discardProcess(@NonNull DownloadTask task) throws IOException {
        // Remove target file.
        final File file = task.getFile();
        // Do nothing, because the filename hasn't found yet.
        if (file == null)
            return;

        if (file.exists() && !file.delete()) {
            throw new IOException("Delete file failed!");
        }
    }

    @NonNull
    public FileLock getFileLock() {
        return fileLock;
    }
}
