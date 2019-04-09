package com.tzpt.cloudlibrary.zlibrary.ui.android.error;

import android.content.Context;
import android.content.pm.PackageInfo;

/**
 * Created by Administrator on 2017/4/8.
 */

public class ErrorUtil {
    private final Context myContext;

    public ErrorUtil(Context context) {
        myContext = context;
    }

    public String getVersionName() {
        try {
            final PackageInfo info = myContext.getPackageManager().getPackageInfo(myContext.getPackageName(), 0);
            return info.versionName + " (" + info.versionCode + ")";
        } catch (Exception e) {
            return "";
        }
    }
}
