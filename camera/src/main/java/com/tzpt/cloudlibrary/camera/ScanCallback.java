package com.tzpt.cloudlibrary.camera;

/**
 * Created by Administrator on 2018/10/17.
 */

public interface ScanCallback {
    /**
     * Content is not empty when the callback.
     *
     * @param content qr code content, is not null.
     */
    void onScanResult(String content);
}
