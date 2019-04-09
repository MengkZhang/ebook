package com.tzpt.cloundlibrary.manager.modle.remote.downloadmanager;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import com.tzpt.cloundlibrary.manager.modle.local.SharedPreferencesUtil;

import java.io.File;

/**
 * 下载完成接收广播
 */
public class YTAppInstallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            long downloadApkId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            long id = SharedPreferencesUtil.getInstance().getLong("downloadId", -1L);
            if (downloadApkId == id) {
                installAPK(context);
            }
        }
    }

    /**
     * 安装APP
     */
    private void updateApp(Context mContext) {
        String filename = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/" + Environment.DIRECTORY_DOWNLOADS
                + "/" + YTAppDownloadManager.APP_NAME;
        File file = new File(filename);
        if (file.exists()) {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            String type = "application/vnd.android.package-archive";
            intent.setDataAndType(Uri.fromFile(file), type);
            mContext.startActivity(intent);
        }
    }

    /**
     * 7.0兼容
     */
    private void installAPK(Context mContext) {
        //获取APP的名称
        String appName = SharedPreferencesUtil.getInstance().getString(YTAppDownloadManager.APP_DOWNLOAD_KEY, YTAppDownloadManager.APP_NAME);
        File apkFile =
                new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), appName);
        if (apkFile.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                Uri apkUri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".provider", apkFile);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            }
            mContext.startActivity(intent);
        }
    }
}
