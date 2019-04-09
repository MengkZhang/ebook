package com.tzpt.cloudlibrary.modle.remote.newdownload.core.connection;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.ProtocolException;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/8/24.
 */

public class DownloadOkHttp3Connection implements DownloadConnection, DownloadConnection.Connected {
    @NonNull
    private final OkHttpClient mOkClient;
    @NonNull
    private final Request.Builder mRequestBuilder;

    private Request mRequest;
    private Response mResponse;

    DownloadOkHttp3Connection(@NonNull OkHttpClient client, @NonNull String url) {
        this.mOkClient = client;
        this.mRequestBuilder = new Request.Builder().url(url);
    }

    @Override
    public void addHeader(String name, String value) {
        this.mRequestBuilder.addHeader(name, value);
    }

    @Override
    public boolean setRequestMethod(@NonNull String method) throws ProtocolException {
        this.mRequestBuilder.method(method, null);
        return true;
    }

    @Override
    public Connected execute() throws IOException {
        mRequest = mRequestBuilder.build();
        mResponse = mOkClient.newCall(mRequest).execute();
        return this;
    }

    @Override
    public void release() {
        mRequest = null;
        if (mResponse != null)
            mResponse.close();
        mResponse = null;
    }

    @Override
    public Map<String, List<String>> getRequestProperties() {
        if (mRequest != null) {
            return mRequest.headers().toMultimap();
        } else {
            return mRequestBuilder.build().headers().toMultimap();
        }
    }

    @Override
    public String getRequestProperty(String key) {
        if (mRequest != null) {
            return mRequest.header(key);
        } else {
            return mRequestBuilder.build().header(key);
        }
    }

    @Override
    public int getResponseCode() throws IOException {
        if (mResponse == null) throw new IOException("Please invoke execute first!");
        return mResponse.code();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (mResponse == null) throw new IOException("Please invoke execute first!");
        final ResponseBody body = mResponse.body();
        if (body == null) throw new IOException("no body found on response!");
        return body.byteStream();
    }

    @Override
    public ResponseBody getResponseBody() throws IOException {
        if (mResponse == null) throw new IOException("Please invoke execute first!");
        final ResponseBody body = mResponse.body();
        if (body == null) throw new IOException("no body found on response!");
        return body;
    }

    @Nullable
    @Override
    public Map<String, List<String>> getResponseHeaderFields() {
        return mResponse == null ? null : mResponse.headers().toMultimap();
    }

    @Nullable
    @Override
    public String getResponseHeaderField(String name) {
        return mResponse == null ? null : mResponse.header(name);
    }

    public static class Factory implements DownloadConnection.Factory {
        private OkHttpClient.Builder clientBuilder;
        private volatile OkHttpClient client;

        public Factory setBuilder(@NonNull OkHttpClient.Builder builder) {
            this.clientBuilder = builder;
            return this;
        }

        @NonNull
        public OkHttpClient.Builder builder() {
            if (clientBuilder == null) clientBuilder = new OkHttpClient.Builder();
            return clientBuilder;
        }

        @Override
        public DownloadConnection create(String url) throws IOException {
            if (client == null) {
                synchronized (Factory.class) {
                    if (client == null) {
                        client = clientBuilder != null ? clientBuilder.build() : new OkHttpClient();
                        clientBuilder = null;
                    }
                }
            }

            return new DownloadOkHttp3Connection(client, url);
        }
    }
}
