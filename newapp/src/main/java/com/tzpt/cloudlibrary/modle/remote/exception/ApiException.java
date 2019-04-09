package com.tzpt.cloudlibrary.modle.remote.exception;

/**
 * Created by Administrator on 2018/6/26.
 */

public class ApiException extends Exception {
    private int code;
    private String displayMessage;

    public ApiException(Throwable throwable, int code) {
        super(throwable);
        this.code = code;

    }

    public void setDisplayMessage(String displayMessage) {
        this.displayMessage = displayMessage;
    }

    public String getDisplayMessage() {
        return displayMessage;
    }

    public int getCode() {
        return code;
    }
}
