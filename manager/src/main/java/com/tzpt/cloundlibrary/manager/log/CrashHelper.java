package com.tzpt.cloundlibrary.manager.log;

import android.text.TextUtils;

import com.tzpt.cloundlibrary.manager.modle.local.SharedPreferencesUtil;

/**
 * 处理异常信息
 * Created by ZhiqiangJia on 2016-09-26.
 */
public class CrashHelper {

    public static final String CRASH_INFO = "crash_info";

    /**
     * 设置异常信息
     *
     * @param message
     */
    public static void saveCrashInfo(String message) {
        if (null == message || TextUtils.isEmpty(message)) {
            return;
        }
        SharedPreferencesUtil.getInstance().remove(CRASH_INFO);
        SharedPreferencesUtil.getInstance().putString(CRASH_INFO, message);
    }

    /**
     * 获取异常信息
     *
     * @return
     */
    public static String getCrashInfo() {
        return SharedPreferencesUtil.getInstance().getString(CRASH_INFO, "");
    }

    /**
     * 清除异常信息
     */
    public static void clearCrashInfo() {
        SharedPreferencesUtil.getInstance().remove(CRASH_INFO);
        SharedPreferencesUtil.getInstance().putString(CRASH_INFO, "");
    }
}
