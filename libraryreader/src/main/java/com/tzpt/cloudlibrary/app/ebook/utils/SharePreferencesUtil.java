package com.tzpt.cloudlibrary.app.ebook.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 临时存储变量
 */
public class SharePreferencesUtil {

    private final static String CONFIG_FILE_NAME = "tzpt_cloudlibrary_reader";

    /**
     * 获取SharedPreferences.Editor
     *
     * @return
     */
    private static SharedPreferences.Editor getEditor(Context mContext) {
        if (mContext != null) {
            SharedPreferences settings = mContext.getSharedPreferences(
                    CONFIG_FILE_NAME, 0);
            return settings.edit();
        }
        return null;
    }

    /**
     * 获取SharedPreferences
     *
     * @return
     */
    private static SharedPreferences getSettings(Context mContext) {
        if (mContext != null) {
            return mContext.getSharedPreferences(CONFIG_FILE_NAME, 0);
        }
        return null;
    }

    /**
     * 获取字符串
     *
     * @param configName
     * @param def
     * @return
     */
    public static String getString(Context mContext, String configName, String def) {
        return getSettings(mContext).getString(configName, def);
    }

    /**
     * 保存字符串
     *
     * @param configName
     * @param value
     * @return
     */
    public static boolean setString(Context mContext, String configName, String value) {
        return getEditor(mContext).putString(configName, value).commit();
    }

    /**
     * 布尔值处理
     *
     * @param configName
     * @param def
     * @return
     */
    public static boolean getBoolean(Context mContext, String configName, boolean def) {
        return getSettings(mContext).getBoolean(configName, def);
    }

    public static boolean setBoolean(Context mContext, String configName, boolean value) {
        return getEditor(mContext).putBoolean(configName, value).commit();
    }
}