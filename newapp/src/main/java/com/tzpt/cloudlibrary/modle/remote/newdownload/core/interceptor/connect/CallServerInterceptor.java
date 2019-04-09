package com.tzpt.cloudlibrary.modle.remote.newdownload.core.interceptor.connect;

import android.support.annotation.NonNull;

import com.tzpt.cloudlibrary.modle.remote.newdownload.PDownload;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.connection.DownloadConnection;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.download.DownloadChain;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.interceptor.Interceptor;

import java.io.IOException;

/**
 * Created by Administrator on 2018/8/6.
 */

public class CallServerInterceptor implements Interceptor.Connect {
    @NonNull
    @Override
    public DownloadConnection.Connected interceptConnect(DownloadChain chain) throws IOException {
        if (chain.getTask().isCheckNet()) {
            PDownload.with().downloadStrategy().inspectNetworkOnWifi();
        }
        PDownload.with().downloadStrategy().inspectNetworkAvailable();


        return chain.getConnectionOrCreate().execute();
    }
}
