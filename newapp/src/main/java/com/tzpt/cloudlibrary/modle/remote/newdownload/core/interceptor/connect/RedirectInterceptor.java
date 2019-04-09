package com.tzpt.cloudlibrary.modle.remote.newdownload.core.interceptor.connect;

import android.support.annotation.NonNull;

import com.tzpt.cloudlibrary.modle.remote.newdownload.PDownload;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.connection.DownloadConnection;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.download.DownloadChain;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.exception.InterruptException;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.interceptor.Interceptor;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;

/**
 * Created by Administrator on 2018/8/6.
 */

public class RedirectInterceptor implements Interceptor.Connect {

    /**
     * How many redirects and auth challenges should we attempt? Chrome follows 21 redirects;
     * Firefox, curl, and wget follow 20; Safari follows 16; and HTTP/1.0 recommends 5.
     */
    static final int MAX_REDIRECT_TIMES = 10;

    /**
     * The target resource resides temporarily under a different URI and the user agent MUST NOT
     * change the request method if it performs an automatic redirection to that URI.
     */
    private static final int HTTP_TEMPORARY_REDIRECT = 307;
    /**
     * The target resource has been assigned a new permanent URI and any future references to this
     * resource ought to use one of the enclosed URIs.
     */
    private static final int HTTP_PERMANENT_REDIRECT = 308;

    @NonNull @Override
    public DownloadConnection.Connected interceptConnect(DownloadChain chain) throws IOException {
        int redirectCount = 0;

        String url;
        DownloadConnection connection;
        while (true) {

            if (chain.getCache().isInterrupt()) {
                throw InterruptException.SIGNAL;
            }

            final DownloadConnection.Connected connected = chain.processConnect();
            final int code = connected.getResponseCode();

            if (!isRedirect(code)) {
                return connected;
            }

            if (++redirectCount >= MAX_REDIRECT_TIMES) {
                throw new ProtocolException("Too many redirect requests: " + redirectCount);
            }

            url = connected.getResponseHeaderField("Location");
            if (url == null) {
                throw new ProtocolException(
                        "Response code is " + code + " but can't find Location field");
            }

            chain.releaseConnection();

            connection = PDownload.with().connectionFactory().create(url);
            chain.setConnection(connection);
            chain.setRedirectLocation(url);

        }
    }

    private static boolean isRedirect(int code) {
        return code == HttpURLConnection.HTTP_MOVED_PERM
                || code == HttpURLConnection.HTTP_MOVED_TEMP
                || code == HttpURLConnection.HTTP_SEE_OTHER
                || code == HttpURLConnection.HTTP_MULT_CHOICE
                || code == HTTP_TEMPORARY_REDIRECT
                || code == HTTP_PERMANENT_REDIRECT;
    }
}
