package com.tzpt.cloudlibrary.modle.remote.newdownload.core.exception;

import java.io.IOException;

/**
 * Created by Administrator on 2018/8/7.
 */

public class RetryException extends IOException {
    public RetryException(String message) {
        super(message);
    }
}
