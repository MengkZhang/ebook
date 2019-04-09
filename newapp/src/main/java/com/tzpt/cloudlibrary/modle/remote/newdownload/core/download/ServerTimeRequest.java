package com.tzpt.cloudlibrary.modle.remote.newdownload.core.download;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.tzpt.cloudlibrary.modle.remote.CloudLibraryApi;
import com.tzpt.cloudlibrary.modle.remote.newdownload.DownloadTask;
import com.tzpt.cloudlibrary.modle.remote.newdownload.PDownload;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.connection.DownloadConnection;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.exception.ServerCanceledException;
import com.tzpt.cloudlibrary.modle.remote.newpojo.ServerTimeVo;
import com.tzpt.cloudlibrary.utils.MD5Utils;

import java.io.IOException;

/**
 * Created by Administrator on 2018/8/24.
 */

public class ServerTimeRequest {
    @NonNull
    private final DownloadTask mTask;

    public ServerTimeRequest(@NonNull DownloadTask task) {
        mTask = task;
    }

    void executeRequest() throws IOException, JsonSyntaxException {
        if (mTask.isCheckNet()) {
            PDownload.with().downloadStrategy().inspectNetworkOnWifi();
        }
        PDownload.with().downloadStrategy().inspectNetworkAvailable();

        DownloadConnection connection = PDownload.with().connectionFactory().create(CloudLibraryApi.BASE_URL + "userApp/common/getTime");
        try {
            final DownloadConnection.Connected connected = connection.execute();
            final DownloadStrategy downloadStrategy = PDownload.with().downloadStrategy();
            if (downloadStrategy.isServerCanceled(connected.getResponseCode())) {
                throw new ServerCanceledException(connected.getResponseCode());
            } else {
                final Gson gson = new GsonBuilder().create();
                ServerTimeVo serverTimeVo = gson.fromJson(connected.getResponseBody().charStream(), ServerTimeVo.class);
                if (serverTimeVo.status == 200) {
                    String str = "YTSG" + String.valueOf(serverTimeVo.data.time);
                    mTask.addRequestHeader("auth", MD5Utils.MD5(str));
                    mTask.addRequestHeader("type", "2");
                } else {
                    throw new ServerCanceledException(serverTimeVo.status);
                }
            }
        } finally {
            connection.release();
        }
    }
}
