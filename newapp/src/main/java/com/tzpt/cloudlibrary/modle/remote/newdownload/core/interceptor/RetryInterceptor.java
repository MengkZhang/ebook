package com.tzpt.cloudlibrary.modle.remote.newdownload.core.interceptor;

import android.support.annotation.NonNull;

import com.tzpt.cloudlibrary.modle.remote.newdownload.core.connection.DownloadConnection;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.download.DownloadCache;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.download.DownloadChain;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.exception.InterruptException;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.exception.RetryException;

import java.io.IOException;

/**
 * Created by Administrator on 2018/8/6.
 */

public class RetryInterceptor implements Interceptor.Connect, Interceptor.Fetch {

    @NonNull @Override
    public DownloadConnection.Connected interceptConnect(DownloadChain chain) throws IOException {
        final DownloadCache cache = chain.getCache();

        while (true) {
            try {
                if (cache.isInterrupt()) {
                    throw InterruptException.SIGNAL;
                }
                return chain.processConnect();
            } catch (IOException e) {
                if (e instanceof RetryException) {
                    chain.resetConnectForRetry();
                    continue;
                }

                chain.getCache().catchException(e);
                throw e;
            }
        }
    }

    @Override
    public long interceptFetch(DownloadChain chain) throws IOException {
        try {
            return chain.processFetch();
        } catch (IOException e) {
            chain.getCache().catchException(e);
            throw e;
        }
    }

}
