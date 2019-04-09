package com.tzpt.cloudlibrary.ui.share;

import android.app.Activity;
import android.content.pm.PackageManager;

/**
 * Created by ZhiqiangJia on 2017-08-23.
 */
public class ShareConfig {

    //qq app key
    public static final String QQ_APP_KEY = "1105405811";
    //weixin app key
    public static final String WX_APP_KEY = "wx7490260081085f6b";
    //weibo app key
    public static final String WEIBO_APP_KEY = "73772033";
    //weibo 授权回调地址
    public static final String REDIRECT_URL = "http://m.ytsg.cn/app/";
    public static final String SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";

    /**
     * 是否安装了微信
     *
     * @param activity
     * @return
     */
    public static boolean isWebchatAvaliable(Activity activity) {
        //检测手机上是否安装了微信
        try {
            activity.getPackageManager().getPackageInfo("com.tencent.mm", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * 是否安装了QQ
     *
     * @param activity
     * @return
     */
    public static boolean isQQAvaliable(Activity activity) {
        try {
            activity.getPackageManager().getPackageInfo("com.tencent.mobileqq", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断是否安装了微博
     *
     * @param activity
     * @return
     */
    public static boolean isWeiBoAvaliable(Activity activity) {
        try {
            activity.getPackageManager().getPackageInfo("com.sina.weibo", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
