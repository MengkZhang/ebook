package com.tzpt.cloudlibrary.modle.remote.newdownload.core.file;

import android.net.Uri;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.util.Log;

import com.tzpt.cloudlibrary.modle.remote.newdownload.DownloadTask;
import com.tzpt.cloudlibrary.modle.remote.newdownload.PDownload;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.Util;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.breakpoint.BreakpointInfo;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.breakpoint.DownloadStore;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.exception.PreAllocateException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2018/8/6.
 */

public class MultiPointOutputStream {

    private static final String TAG = "MultiPointOutputStream";
    private static final ExecutorService FILE_IO_EXECUTOR = new ThreadPoolExecutor(0,
            Integer.MAX_VALUE,
            60, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(),
            Util.threadFactory("PDownload file io", false));

    private DownloadOutputStream mOutputStream;

    private final int flushBufferSize;
    private final BreakpointInfo info;
    private final DownloadTask task;
    private final DownloadStore store;
    private final boolean supportSeek;

    MultiPointOutputStream(@NonNull final DownloadTask task,
                           @NonNull BreakpointInfo info,
                           @NonNull DownloadStore store) {
        this.task = task;
        this.flushBufferSize = task.getFlushBufferSize();
        this.info = info;
        this.store = store;

        this.supportSeek = PDownload.with().outputStreamFactory().supportSeek();
    }

    public void write(byte[] bytes, int length) throws IOException {
        outputStream().write(bytes, 0, length);
    }

    public void cancelAsync() {
        FILE_IO_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                cancel();
            }
        });
    }

    private void cancel() {
        try {
            close();
        } catch (IOException e) {
            // just ignored and print log.
        }
    }

    public void done() throws IOException {
        close();
    }


    public void inspectComplete() throws IOException {
        if (!Util.isCorrectFull(info.getCurrentOffset(), info.getTotalLength())) {
            throw new IOException("The current offset on block-info isn't update correct, "
                    + info.getCurrentOffset() + " != " + info.getTotalLength()
                    + " on " + 0);
        }
    }

    private synchronized void close() throws IOException {
        if (mOutputStream != null) {
            mOutputStream.close();
        }
    }

    private volatile boolean firstOutputStream = true;

    private synchronized DownloadOutputStream outputStream() throws IOException {
        DownloadOutputStream outputStream = mOutputStream;

        if (outputStream == null) {
            final Uri uri;
            final boolean isFileScheme = Util.isUriFileScheme(task.getUri());
            if (isFileScheme) {
                final File file = task.getFile();
                if (file == null) throw new FileNotFoundException("Filename is not ready!");

                final File parentFile = task.getParentFile();
                if (!parentFile.exists() && !parentFile.mkdirs()) {
                    throw new IOException("Create parent folder failed!");
                }

                if (file.createNewFile()) {
                    Util.d(TAG, "Create new file: " + file.getName());
                }

                uri = Uri.fromFile(file);
            } else {
                uri = task.getUri();
            }

            outputStream = PDownload.with().outputStreamFactory().create(
                    PDownload.with().context(),
                    uri,
                    flushBufferSize);
            if (supportSeek) {
                final long seekPoint = info.getTotalOffset();
                if (seekPoint > 0) {
                    // seek to target point
                    outputStream.seek(seekPoint);
                }
            }

            if (firstOutputStream) {
                // pre allocate length
                final long totalLength = info.getTotalLength();
                if (isFileScheme) {
                    final File file = task.getFile();
                    final long requireSpace = totalLength - file.length();
                    if (requireSpace > 0) {
                        inspectFreeSpace(new StatFs(file.getAbsolutePath()), requireSpace);
                        outputStream.setLength(totalLength);
                    }
                } else {
                    outputStream.setLength(totalLength);
                }
            }

            mOutputStream = outputStream;

            firstOutputStream = false;
        }

        return outputStream;
    }

    private void inspectFreeSpace(StatFs statFs, long requireSpace) throws PreAllocateException {
        final long freeSpace = Util.getFreeSpaceBytes(statFs);
        if (freeSpace < requireSpace) {
            throw new PreAllocateException(requireSpace, freeSpace);
        }
    }
}
