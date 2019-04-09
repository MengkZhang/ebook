package com.tzpt.cloudlibrary;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

/**
 * 友盟统计工具类
 * Created by Administrator on 2017/12/18.
 */

public class UmengHelper {

    /**
     * 使用友盟统计debug模式
     */
    public static void setUmengDebugMode() {
        MobclickAgent.setDebugMode(false);
    }

    /**
     * 设置统计类型
     * @param context
     */
    public static void setScenarioType(Context context) {
        MobclickAgent.setScenarioType(context, MobclickAgent.EScenarioType.E_UM_NORMAL);
    }
    // session的统计
    public static void setUmengResume(Context context) {
        MobclickAgent.onResume(context);
    }

    public static void setUmengPause(Context context) {
        MobclickAgent.onPause(context);
    }

    public static void setPageStart(String pageTag) {
        MobclickAgent.onPageStart(pageTag);
    }
    public static void setPageEnd(String pageTag) {
        MobclickAgent.onPageEnd(pageTag);
    }
    /**
     * 统计开始
     * 统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。)
     *
     * @param pageTag pageTag为页面名称，可自定义
     * @param context
     */
    public static void setUmengResume(String pageTag, Context context) {
        MobclickAgent.onPageStart(pageTag); //统计页面跳转
        MobclickAgent.onResume(context);    //统计时长
    }

    /**
     * 统计暂停
     * （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。
     *
     * @param pageTag pageTag为页面名称，可自定义
     * @param context
     */
    public static void setUmengPause(String pageTag, Context context) {
        MobclickAgent.onPageEnd(pageTag);   //统计页面跳转
        MobclickAgent.onPause(context);     //统计时长
    }

    /**
     * 在程序入口处，调用 MobclickAgent.openActivityDurationTrack(false)
     * 禁止默认的页面统计方式，这样将不会再自动统计Activity
     */
    public static void setOpenActivityDurationTrackFalse() {
        MobclickAgent.openActivityDurationTrack(false);
    }

    //代码中配置Appkey和Channel
    public static void configUmeng(MobclickAgent.UMAnalyticsConfig config) {
        MobclickAgent.startWithConfigure(config);
    }

    public static void configUmeng(Context context, String appkey, String channelId) {
        new MobclickAgent.UMAnalyticsConfig(context, appkey, channelId);

    }

    /**
     * 设置日志加密
     */
    public static void setLogEnableEncrypt() {
        MobclickAgent.enableEncrypt(true);//6.0.0版本及以后
    }

    /**
     *
     * @param context
     */
    public static void onKillProcess(Context context) {
        MobclickAgent.onKillProcess(context);
    }
}
