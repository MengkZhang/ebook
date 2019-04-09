package com.tzpt.cloudlibrary.zlibrary.ui.android.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.tzpt.cloudlibrary.zlibrary.core.application.ZLApplication;
import com.tzpt.cloudlibrary.zlibrary.core.view.ZLView;
import com.tzpt.cloudlibrary.zlibrary.ui.android.view.animation.BitmapManager;

/**
 * 书籍展示页面的图片内容管理类
 * Created by Administrator on 2017/4/10.
 */

public class BitmapManagerImpl implements BitmapManager {
    private Bitmap mBitmap;

    private int mWidth;
    private int mHeight;

    public void setSize(int w, int h) {
        if (mWidth != w || mHeight != h) {
            mWidth = w;
            mHeight = h;
            System.gc();
        }
    }

    /**
     * 画图
     *
     * @param index current next previous
     * @return 获取一张Bitmap
     */
    public Bitmap getBitmap(ZLView.PageIndex index) {
        if (mBitmap == null) {
            try {
                mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.RGB_565);
            } catch (OutOfMemoryError e) {
                System.gc();
                System.gc();
                mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.RGB_565);
            }
        }
        // 在Bitmap上绘制,传入一张空白的bitmap,和当前的index
        drawOnBitmap(mBitmap, index);
        return mBitmap;
    }

    private void drawOnBitmap(Bitmap bitmap, ZLView.PageIndex index) {
        final ZLView view = ZLApplication.Instance().getCurrentView();
        if (view == null) {
            return;
        }
        //准备画笔信息
        final ZLAndroidPaintContext context = new ZLAndroidPaintContext(
                new Canvas(bitmap),//新创建的画布，主要把文字、图片等内容画在画布上面。
                new ZLAndroidPaintContext.Geometry(
                        mWidth,
                        mHeight,
                        mWidth,
                        mHeight,
                        0,
                        0
                ));
        //他们的使命就是使用自己创建的canvas生成出一个Bitmap出来，然后返回给ZLAndroidWidget，然后ZLAndroidWidget的onDraw()方法就把这个Bitmap画到自己的画布(canvas)上！
        view.paint(context, index);
    }

    public void reset() {
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }
}
