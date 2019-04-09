package com.tzpt.cloudlibrary.app.ebook.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ZhiqiangJia on 2017-03-01.
 */
public class HelpUtils {

    /**
     * 获取时间
     *
     * @param time
     * @return
     */
    public static String getDateTime(String time) {
        if (null == time) return "";
        try {
            long newTime = Long.parseLong(time);
            Date d = new Date(newTime);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.format(d);
        } catch (Exception e) {
            return time;
        }
    }

    /**
     * 获取二位小数点
     *
     * @param value
     * @param bit
     * @return
     */
    public static String doubleToString(double value, int bit) {
        // 这里面的bd是自定义的变量，即最后取得小数点后若干位的数，2表示小数点后两位
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(bit, BigDecimal.ROUND_HALF_UP);
        return bd.toString();
    }

    /**
     * 计算除法保留2位小数
     *
     * @param a
     * @param b
     * @return
     */
    public static String calcDivision(long a, long b) {
        try {
            float num = (float) a / b;
            BigDecimal bd = new BigDecimal(num * 100.0);
            bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
            return bd.toString();
        } catch (Exception e) {
            return "0.00";
        }
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
