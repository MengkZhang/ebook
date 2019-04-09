package com.tzpt.cloudlibrary.ui.main;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import java.io.File;

/**
 * APP更新
 * Created by ZhiqiangJia on 2017-09-12.
 */
public class AppUpdateManager {
    public String mAppName = "ytsg_lastest.apk";
    private volatile static AppUpdateManager sInstance;
    private DownloadManager mDownloadManager;
    private long mDownloadId = -1;
    private Context mContext;

    private AppUpdateManager() {
    }

    public static AppUpdateManager getInstance() {

        if (sInstance == null) {
            synchronized (AppUpdateManager.class) {
                if (sInstance == null) {
                    sInstance = new AppUpdateManager();
                }
            }
        }
        return sInstance;
    }

    public void initContext(Context context) {
        this.mContext = context;
    }

    //下载APP
    public void downLoadApp(String url) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedOverRoaming(false);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setTitle("云图书馆");
        request.setDescription("下载完成后点击打开");
        request.setVisibleInDownloadsUi(true);

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(false);

        long nowTimes = System.currentTimeMillis();
        mAppName = "ytsg" + nowTimes + ".apk";
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, mAppName);
        mDownloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        mDownloadId = mDownloadManager.enqueue(request);
    }

    //下载到本地后执行安装
    public void installAPK(long downloadApkId) {
        //获取下载文件的Uri
        if (mDownloadId != -1 && downloadApkId == mDownloadId && null != mDownloadManager) {
            //7.0兼容-安装程序
            //获取APP的名称
            File apkFile =
                    new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), mAppName);
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

}
