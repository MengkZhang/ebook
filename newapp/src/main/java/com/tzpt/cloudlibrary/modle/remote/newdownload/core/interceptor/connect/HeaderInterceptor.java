package com.tzpt.cloudlibrary.modle.remote.newdownload.core.interceptor.connect;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tzpt.cloudlibrary.modle.remote.newdownload.DownloadTask;
import com.tzpt.cloudlibrary.modle.remote.newdownload.PDownload;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.Util;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.breakpoint.BreakpointInfo;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.connection.DownloadConnection;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.download.DownloadChain;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.download.DownloadStrategy;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.exception.InterruptException;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.interceptor.Interceptor;

import java.io.IOException;
import java.util.Map;

import static com.tzpt.cloudlibrary.modle.remote.newdownload.core.Util.CHUNKED_CONTENT_LENGTH;
import static com.tzpt.cloudlibrary.modle.remote.newdownload.core.Util.CONTENT_LENGTH;
import static com.tzpt.cloudlibrary.modle.remote.newdownload.core.Util.CONTENT_RANGE;
import static com.tzpt.cloudlibrary.modle.remote.newdownload.core.Util.RANGE;

/**
 * Created by Administrator on 2018/8/6.
 */

public class HeaderInterceptor implements Interceptor.Connect {
    private static final String TAG = "HeaderInterceptor";

    @NonNull
    @Override
    public DownloadConnection.Connected interceptConnect(DownloadChain chain) throws IOException {
        final BreakpointInfo info = chain.getInfo();
        final DownloadConnection connection = chain.getConnectionOrCreate();
        final DownloadTask task = chain.getTask();

        // add user customize header
        final Map<String, String> userHeader = task.getHeaderMapFields();
        if (userHeader != null) Util.addUserRequestHeaderField(userHeader, connection);

        Util.addDefaultUserAgent(connection);

        String range = "bytes=" + info.getTotalOffset() + "-";
//        range += info.getRangeRight();

        connection.addHeader(RANGE, range);

        if (chain.getCache().isInterrupt()) {
            throw InterruptException.SIGNAL;
        }

        PDownload.with().callbackDispatcher().dispatch().connectStart(task);

        DownloadConnection.Connected connected = chain.processConnect();

        PDownload.with().callbackDispatcher().dispatch().connectEnd(task);
        if (chain.getCache().isInterrupt()) {
            throw InterruptException.SIGNAL;
        }

        // if precondition failed.
//        final DownloadStrategy strategy = PDownload.with().downloadStrategy();
//        final DownloadStrategy.ResumeAvailableResponseCheck responseCheck =
//                strategy.resumeAvailableResponseCheck(connected, info);
//        responseCheck.inspect();

        final long contentLength;
        final String contentLengthField = connected.getResponseHeaderField(CONTENT_LENGTH);
        if (contentLengthField == null || contentLengthField.length() == 0) {
            final String contentRangeField = connected.getResponseHeaderField(CONTENT_RANGE);
            contentLength = Util.parseContentLengthFromContentRange(contentRangeField);
        } else {
            contentLength = Util.parseContentLength(contentLengthField);
        }
        if (info.getTotalLength() == 0) {
            final long instanceLength = parseContentRangeFoInstanceLength(
                    connected.getResponseHeaderField(CONTENT_RANGE));
            info.setContentLength(instanceLength);
            chain.getStore().updateContentLength(info);
        }

        chain.setResponseContentLength(contentLength);
        return connected;
    }

    private static long parseContentRangeFoInstanceLength(@Nullable String contentRange) {
        if (contentRange == null)
            return CHUNKED_CONTENT_LENGTH;

        final String[] session = contentRange.split("/");
        if (session.length >= 2) {
            try {
                return Long.parseLong(session[1]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }
}
