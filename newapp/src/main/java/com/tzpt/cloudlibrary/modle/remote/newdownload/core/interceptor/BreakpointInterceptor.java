package com.tzpt.cloudlibrary.modle.remote.newdownload.core.interceptor;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.util.Log;

import com.tzpt.cloudlibrary.modle.remote.newdownload.core.Util;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.breakpoint.BreakpointInfo;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.connection.DownloadConnection;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.download.DownloadChain;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.exception.InterruptException;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.file.MultiPointOutputStream;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.tzpt.cloudlibrary.modle.remote.newdownload.core.Util.CHUNKED_CONTENT_LENGTH;
import static com.tzpt.cloudlibrary.modle.remote.newdownload.core.Util.CONTENT_LENGTH;
import static com.tzpt.cloudlibrary.modle.remote.newdownload.core.Util.CONTENT_RANGE;

/**
 * Created by Administrator on 2018/8/6.
 */

public class BreakpointInterceptor implements Interceptor.Connect, Interceptor.Fetch {

    @NonNull
    @Override
    public DownloadConnection.Connected interceptConnect(DownloadChain chain) throws IOException {
        final DownloadConnection.Connected connected = chain.processConnect();

        if (chain.getCache().isInterrupt()) {
            throw InterruptException.SIGNAL;
        }

        return connected;
    }

    @Override
    public long interceptFetch(DownloadChain chain) throws IOException {
        final long contentLength = chain.getResponseContentLength();
        final boolean isNotChunked = contentLength != CHUNKED_CONTENT_LENGTH;

        long fetchLength = 0;
        long processFetchLength;

        final MultiPointOutputStream outputStream = chain.getOutputStream();

        try {
            while (true) {
                processFetchLength = chain.loopFetch();
                if (processFetchLength == -1) {
                    break;
                }

                fetchLength += processFetchLength;
            }
        } finally {
            // finish
            Log.e("BreakpointInterceptor", "********************interceptFetch");
            if (!chain.getCache().isUserCanceled())
                outputStream.done();
        }

        if (isNotChunked) {
            // local persist data check.
            outputStream.inspectComplete();

            // response content length check.
            if (fetchLength != contentLength) {
                throw new IOException("Fetch-length isn't equal to the response content-length, "
                        + fetchLength + "!= " + contentLength);
            }
        }

        return fetchLength;
    }
}
