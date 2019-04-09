package com.tzpt.cloudlibrary.modle.remote.newdownload;

/**
 * Created by Administrator on 2018/8/27.
 */

public class DownloadStatus {

    private DownloadStatus() {
        throw new RuntimeException("DownloadStatus cannot be initialized!");
    }

    // 未下载
    public static final int NORMAL = 0;
    // 等待下载
    public static final int WAIT = 1;
    // 正在建立连接
    public static final int CONNECTING = 2;
    // 开始下载
    public static final int START = 3;
    // 正在下载
    public static final int DOWNLOADING = 4;
    // 停止下载
    public static final int STOP = 5;
    // 下载失败
    public static final int ERROR = 6;
    // 下载完成
    public static final int COMPLETE = 7;
    // 取消下载
    public static final int CANCEL = 8;
    //
    public static final int MOBILE_NET_ERROR = 9;

    public static final int NO_NET_ERROR = 10;
}
