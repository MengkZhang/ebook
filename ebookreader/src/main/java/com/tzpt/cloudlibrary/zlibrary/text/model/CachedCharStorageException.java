package com.tzpt.cloudlibrary.zlibrary.text.model;

/**
 * Created by Administrator on 2017/4/9.
 */

public final class CachedCharStorageException extends RuntimeException {
    private static final long serialVersionUID = -6373408730045821053L;

    public CachedCharStorageException(String message) {
        super(message);
    }

    public CachedCharStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
