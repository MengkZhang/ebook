package com.tzpt.cloudlibrary.zlibrary.core.drm;

/**
 * Created by Administrator on 2017/4/7.
 */

public abstract class EncryptionMethod {
    public static final String EMBEDDING = "embedding";

    public static boolean isSupported(String method) {
        return EMBEDDING.equals(method);
    }
}
