package com.tzpt.cloudlibrary.modle.remote.newdownload.core.exception;

import java.io.IOException;

/**
 * Created by Administrator on 2018/8/6.
 */

public class FileBusyAfterRunException extends IOException {
    private FileBusyAfterRunException() {
        super("File busy after run");
    }

    public static final FileBusyAfterRunException SIGNAL = new FileBusyAfterRunException() {
    };
}
