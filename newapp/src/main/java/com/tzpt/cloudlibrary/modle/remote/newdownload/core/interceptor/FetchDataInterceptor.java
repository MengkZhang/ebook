package com.tzpt.cloudlibrary.modle.remote.newdownload.core.interceptor;

import android.support.annotation.NonNull;

import com.tzpt.cloudlibrary.modle.remote.newdownload.DownloadTask;
import com.tzpt.cloudlibrary.modle.remote.newdownload.PDownload;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.download.DownloadChain;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.exception.InterruptException;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.file.MultiPointOutputStream;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2018/8/6.
 */

public class FetchDataInterceptor implements Interceptor.Fetch {

    private final InputStream inputStream;
    private final byte[] readBuffer;
    private final MultiPointOutputStream outputStream;
    private final DownloadTask task;

    public FetchDataInterceptor(@NonNull InputStream inputStream,
                                @NonNull MultiPointOutputStream outputStream,
                                DownloadTask task) {
        this.inputStream = inputStream;
        this.readBuffer = new byte[task.getReadBufferSize()];
        this.outputStream = outputStream;
        this.task = task;
    }

    @Override
    public long interceptFetch(DownloadChain chain) throws IOException {
        if (chain.getCache().isInterrupt()) {
            throw InterruptException.SIGNAL;
        }
        if (chain.getTask().isCheckNet()) {
            PDownload.with().downloadStrategy().inspectNetworkOnWifi();
        }
        PDownload.with().downloadStrategy().inspectNetworkAvailable();
        // fetch
        int fetchLength = inputStream.read(readBuffer);
        if (fetchLength == -1) {
            return fetchLength;
        }

        // write to file
        outputStream.write(readBuffer, fetchLength);

        chain.getInfo().increaseCurrentOffset(fetchLength, true);
        chain.getStore().updateReadBytes(chain.getInfo());

        if (PDownload.with().callbackDispatcher().isFetchProcessMoment(task)) {
            if (!chain.getCache().isInterrupt()) {
                PDownload.with().callbackDispatcher().dispatch().fetchProgress(task);
            }
        }

        return fetchLength;
    }
}
