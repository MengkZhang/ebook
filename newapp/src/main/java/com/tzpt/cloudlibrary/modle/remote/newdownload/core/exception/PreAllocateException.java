package com.tzpt.cloudlibrary.modle.remote.newdownload.core.exception;

import java.io.IOException;

/**
 * Created by Administrator on 2018/8/6.
 */

public class PreAllocateException extends IOException {
    private final long requireSpace;
    private final long freeSpace;

    public PreAllocateException(long requireSpace, long freeSpace) {
        super("There is Free space less than Require space: " + freeSpace + " < " + requireSpace);
        this.requireSpace = requireSpace;
        this.freeSpace = freeSpace;
    }

    public long getRequireSpace() {
        return requireSpace;
    }

    public long getFreeSpace() {
        return freeSpace;
    }
}
