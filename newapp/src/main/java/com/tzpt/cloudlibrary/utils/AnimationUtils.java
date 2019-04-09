package com.tzpt.cloudlibrary.utils;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * 动画工具类
 * Created by ZhiqiangJia on 2017-07-28.
 */
public class AnimationUtils {

    /**
     * View渐隐动画效果
     */

    public static void setHideAnimation(View view) {
        if (null == view) {
            return;
        }
        AlphaAnimation hideAnimation = new AlphaAnimation(1.0f, 0.0f);
        hideAnimation.setDuration(600);
        hideAnimation.setFillAfter(true);
        view.startAnimation(hideAnimation);
    }

    /**
     * View渐现动画效果
     */

    public static void setShowAnimation(View view) {
        if (null == view) {
            return;
        }
        AlphaAnimation showAnimation = new AlphaAnimation(0.0f, 1.0f);
        showAnimation.setDuration(600);
        showAnimation.setFillAfter(false);
        view.startAnimation(showAnimation);
    }

    public static void showAnimation(View view) {

        //相对于自己的高度往上平移
        TranslateAnimation translate = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f);
        translate.setDuration(500);//动画时间500毫秒
        translate.setFillAfter(true);//动画出来控件可以点击
        view.startAnimation(translate);//开始动画
        view.setVisibility(View.VISIBLE);//设置可见
    }

    public static void hideAnimation(View view) {
        //相对于自己的高度往下平移
        TranslateAnimation translate = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        translate.setDuration(500);
        translate.setFillAfter(false);//设置动画结束后控件不可点击
        view.startAnimation(translate);
        view.setVisibility(View.GONE);//隐藏不占位置
    }
}
