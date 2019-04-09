package com.tzpt.cloundlibrary.manager.modle.remote.downloadmanager;

import android.content.Context;
import android.widget.Toast;

import com.tzpt.cloundlibrary.manager.modle.local.SharedPreferencesUtil;
import com.tzpt.cloundlibrary.manager.utils.ToastUtils;

/**
 * app 更新下载
 */
public class YTAppUpdateUtils {

    public static final String TAG = YTAppUpdateUtils.class.getSimpleName();
    private static final String KEY_DOWNLOAD_ID = "downloadId";

    /**
     * 下载APP
     *
     * @param context
     * @param url
     * @param title
     */
    public static void download(Context context, String url, String title) {
        start(context, url, title);
    }

    /**
     * 开始下载
     *
     * @param context
     * @param url
     * @param title
     */
    private static void start(Context context, String url, String title) {
        try {
            long id = YTAppDownloadManager.getInstance(context).startDownload(url,
                    title, "下载完成后点击打开");
            SharedPreferencesUtil.getInstance().putLong(KEY_DOWNLOAD_ID, id);
        } catch (Exception e) {
            ToastUtils.showSingleToast("下载失败！");
        }
    }
}


