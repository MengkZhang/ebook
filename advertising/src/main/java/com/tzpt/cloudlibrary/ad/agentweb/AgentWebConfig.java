package com.tzpt.cloudlibrary.ad.agentweb;

import android.content.Context;
import android.os.Build;
import android.webkit.CookieSyncManager;

public class AgentWebConfig {
    /**
     * DEBUG 模式 ， 如果需要查看日志请设置为 true
     */
    public static boolean DEBUG = false;
    /**
     * 当前操作系统是否低于 KITKAT
     */
    static final boolean IS_KITKAT_OR_BELOW_KITKAT = Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT;
    /**
     * 默认 WebView  类型 。
     */
    public static final int WEBVIEW_DEFAULT_TYPE = 1;
    /**
     * 使用 AgentWebView
     */
    public static final int WEBVIEW_AGENTWEB_SAFE_TYPE = 2;
    /**
     * 自定义 WebView
     */
    public static final int WEBVIEW_CUSTOM_TYPE = 3;

    static int WEBVIEW_TYPE = WEBVIEW_DEFAULT_TYPE;
    private static volatile boolean IS_INITIALIZED = false;

    static synchronized void initCookiesManager(Context context) {
        if (!IS_INITIALIZED) {
            createCookiesSyncInstance(context);
            IS_INITIALIZED = true;
        }
    }

    private static void createCookiesSyncInstance(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(context);
        }
    }
}
