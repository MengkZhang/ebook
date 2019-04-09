package com.tzpt.cloundlibrary.manager.modle.remote.downloadmanager;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import com.tzpt.cloundlibrary.manager.modle.local.SharedPreferencesUtil;

/**
 * APP 下载安装类
 */
public class YTAppDownloadManager {

    private DownloadManager dm;
    private static YTAppDownloadManager instance;
    public static String APP_NAME = "mms_update.apk";
    public static String APP_DOWNLOAD_KEY = "ytsg_app_name";

    private YTAppDownloadManager(Context context) {
        dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public static YTAppDownloadManager getInstance(Context context) {
        if (instance == null) {
            instance = new YTAppDownloadManager(context);
        }
        return instance;
    }


    /**
     * 开始下载
     *
     * @param uri         链接地址
     * @param title       标题
     * @param description 描述
     * @return download id
     */
    public long startDownload(String uri, String title, String description) throws Exception {
        try {
            long nowTimes = System.currentTimeMillis();
            StringBuffer nameBuffer = new StringBuffer();
            nameBuffer.append("mms")
                    .append(nowTimes)
                    .append(".apk");
            APP_NAME = nameBuffer.toString();
        } catch (Exception e) {
            //如果解析出错，则使用以前名称
            APP_NAME = "mms_update.apk";
        }
        SharedPreferencesUtil.getInstance().putString(APP_DOWNLOAD_KEY, APP_NAME);
        DownloadManager.Request req = new DownloadManager.Request(Uri.parse(uri));
        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, APP_NAME);
        req.setVisibleInDownloadsUi(true);
        req.setAllowedOverRoaming(false);
        // 设置一些基本显示信息
        req.setTitle(title);
        req.setDescription(description);
        req.setMimeType("application/vnd.android.package-archive");
        req.allowScanningByMediaScanner();
        return dm.enqueue(req);
    }


    public DownloadManager getDm() {
        return dm;
    }

    /**
     * 删除IDs
     */
    public void removeIds(long... id) {
        try {
            if (null != dm) {
                dm.remove(id);
            }
        } catch (Exception e) {
        }
    }
}
