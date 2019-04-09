package com.tzpt.cloudlibrary.modle.remote.newdownload.core.exception;

import java.io.IOException;

/**
 * Created by Administrator on 2018/8/6.
 */

public class InterruptException extends IOException {
    private InterruptException() {
        super("Interrupted");
    }

    public static final InterruptException SIGNAL = new InterruptException() {

        @Override public void printStackTrace() {
            throw new IllegalAccessError("Stack is ignored for signal");
        }
    };
}
