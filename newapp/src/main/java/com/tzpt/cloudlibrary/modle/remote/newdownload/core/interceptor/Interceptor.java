package com.tzpt.cloudlibrary.modle.remote.newdownload.core.interceptor;

import android.support.annotation.NonNull;

import com.tzpt.cloudlibrary.modle.remote.newdownload.core.connection.DownloadConnection;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.download.DownloadChain;

import java.io.IOException;

/**
 * Created by Administrator on 2018/8/6.
 */

public interface Interceptor {
    interface Connect {
        @NonNull
        DownloadConnection.Connected interceptConnect(DownloadChain chain)
                throws IOException;
    }

    interface Fetch {
        long interceptFetch(DownloadChain chain) throws IOException;
    }
}
