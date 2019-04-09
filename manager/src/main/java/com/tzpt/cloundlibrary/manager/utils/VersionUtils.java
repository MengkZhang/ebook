package com.tzpt.cloundlibrary.manager.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by ZhiqiangJia on 2017-10-19.
 */

public class VersionUtils {
    /**
     * 获取手机的版本信息
     *
     * @return
     */
    public static String getVersionInfo() {
        try {
            PackageManager pm = Utils.getContext().getPackageManager();
            PackageInfo info = pm.getPackageInfo(Utils.getContext().getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            return "1.0";
        }
    }
}
