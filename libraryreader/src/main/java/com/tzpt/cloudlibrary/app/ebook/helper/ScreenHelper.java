package com.tzpt.cloudlibrary.app.ebook.helper;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by ZhiqiangJia on 2017-02-15.
 */
public class ScreenHelper {
    /**
     * 设置全屏
     *
     * @param activity
     */
    public static void setActivityFullScreen(Activity activity) {
        //无title
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }
    public static void setActivityFullScreenHasTitle(Activity activity) {
        //全屏
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
