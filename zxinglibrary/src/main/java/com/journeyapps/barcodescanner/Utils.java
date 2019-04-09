package com.journeyapps.barcodescanner;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by ZhiqiangJia on 2017-04-06.
 */

public class Utils {
    /**
     * 获取屏幕的宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }
    public static int getScreenHeigth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }
}
