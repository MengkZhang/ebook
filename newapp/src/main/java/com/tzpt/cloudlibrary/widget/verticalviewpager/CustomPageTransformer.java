package com.tzpt.cloudlibrary.widget.verticalviewpager;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by ZhiqiangJia on 2017-02-10.
 */
public class CustomPageTransformer implements ViewPager.PageTransformer {

    private static final float MIN_SCALE = 0.75f;//最小缩放比例
    private static final float MIN_ALPHA = 0.75f;//最小透明比例

    @Override
    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();//获取页面的宽度
        int pageHeight = view.getHeight();//获取页面的高度

        if (position < -1) { // [-Infinity,-1)负的无穷小到负1
            // This page is way off-screen to the left.
            view.setAlpha(0);//在左边 屏幕外设置透明度为0

        } else if (position <= 1) { // [-1,1]
            // Modify the default slide transition to shrink the page as
            // well
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));//缩放因子
            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
            float horzMargin = pageWidth * (1 - scaleFactor) / 2;
            if (position < 0) {
                view.setTranslationY(vertMargin - horzMargin / 2);
            } else {
                view.setTranslationY(-vertMargin + horzMargin / 2);
            }

            // Scale the page down (between MIN_SCALE and 1)
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);

            // Fade the page relative to its size.
            view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));

        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(0);
        }
    }
}
