package com.tzpt.cloudlibrary.modle.remote.newdownload.core.exception;

import java.io.IOException;

/**
 * Created by Administrator on 2018/8/6.
 */

public class ServerCanceledException extends IOException {
    private final int responseCode;

    public ServerCanceledException(int responseCode) {
        super("Response code can't handled on internal " + responseCode);
        this.responseCode = responseCode;
    }

    public int getResponseCode() {
        return responseCode;
    }
}
