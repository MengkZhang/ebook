package com.tzpt.cloudlibrary.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.tzpt.cloudlibrary.CloudLibraryApplication;

/**
 * APP版本信息
 * Created by ZhiqiangJia on 2017-08-22.
 */
public class VersionUtils {
    /**
     * 获取版本信息
     *
     * @return
     */
    public static String getVersionInfo() {
        try {
//            PackageManager pm = Utils.getContext().getPackageManager();
            PackageManager pm = CloudLibraryApplication.getAppContext().getPackageManager();
//            PackageInfo info = pm.getPackageInfo(Utils.getContext().getPackageName(), 0);
            PackageInfo info = pm.getPackageInfo(CloudLibraryApplication.getAppContext().getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "1.0";
        }
    }

    /**
     * 获取本地版本号
     */
    public static int getVersionNumber() {
        try {
//            PackageManager pm = Utils.getContext().getPackageManager();//包管理器
            PackageManager pm = CloudLibraryApplication.getAppContext().getPackageManager();//包管理器
//            PackageInfo info = pm.getPackageInfo(Utils.getContext().getPackageName(), 0);//获取包信息
            PackageInfo info = pm.getPackageInfo(CloudLibraryApplication.getAppContext().getPackageName(), 0);//获取包信息
            return info.versionCode;//获取版本号
        } catch (Exception e) {
            return 1;
        }
    }

}
