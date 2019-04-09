package com.tzpt.cloudlibrary.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.Method;

/**
 * Created by ZhiqiangJia on 2017-08-18.
 */

public class DpPxUtils {

    public static float dipToPx(Context context, float dip) {
        return dip * getScreenDensity(context) + 0.5f;
    }

    public static int dp2px(float dpValue) {
        return (int) (0.5f + dpValue * Resources.getSystem().getDisplayMetrics().density);
    }

    public static float getScreenDensity(Context context) {
        try {
            DisplayMetrics dm = new DisplayMetrics();
            ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
            return dm.density;
        } catch (Exception e) {
            return DisplayMetrics.DENSITY_DEFAULT;
        }
    }

    public static float getScreenWidth(Context context) {
        try {
            DisplayMetrics dm = new DisplayMetrics();
            ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
            return dm.widthPixels;
        } catch (Exception e) {
            return 0;
        }
    }

    public static float getScreenHeight(Context context) {
        try {
            DisplayMetrics dm = new DisplayMetrics();
            ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
            return dm.heightPixels;
        } catch (Exception e) {
            return 0;
        }
    }

    public static int getWidthPixels() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getHeightPixels() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    /**
     * 屏幕宽
     *
     * @return
     */
    public static int getRealWidthPixels(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics dm = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(dm);
            return dm.widthPixels;
        } else {
            return getRealWidthOrHeightByTag(activity, true);
        }
    }

    /**
     * 屏幕高
     *
     * @return
     */
    public static int getRealHeightPixels(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics dm = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(dm);
            return dm.heightPixels;
        } else {
            return getRealWidthOrHeightByTag(activity, false);
        }
    }

    /**
     * 获取真实屏幕宽高
     *
     * @param activity
     * @param isWidth
     * @return
     */
    private static int getRealWidthOrHeightByTag(Activity activity, boolean isWidth) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isWidth) {
            return dm.widthPixels;
        } else {
            return dm.heightPixels;
        }
    }
}
