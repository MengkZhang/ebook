package com.tzpt.cloudlibrary.modle.remote.newdownload.core.connection;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.ProtocolException;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/8/6.
 */

public interface DownloadConnection {

    int NO_RESPONSE_CODE = 0;

    void addHeader(String name, String value);

    boolean setRequestMethod(@NonNull String method) throws ProtocolException;

    Connected execute() throws IOException;

    void release();

    Map<String, List<String>> getRequestProperties();

    String getRequestProperty(String key);

    interface Connected {
        int getResponseCode() throws IOException;

        InputStream getInputStream() throws IOException;

        @Nullable
        Map<String, List<String>> getResponseHeaderFields();

        @Nullable
        String getResponseHeaderField(String name);

        ResponseBody getResponseBody() throws IOException;
    }

    interface Factory {
        DownloadConnection create(String url) throws IOException;
    }
}
