package com.tzpt.cloudlibrary.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.tzpt.cloudlibrary.R;

/**
 * Created by Administrator on 2018/11/9.
 */

public class PicCropViewFinder extends View {
    private int mHeight;
    private int mWidth;
    private int mMarginWidth = getResources().getDimensionPixelSize(R.dimen.crop_photo_margin);
    private int mStrokeWidth = getResources().getDimensionPixelSize(R.dimen.crop_photo_stroke_width);


    public PicCropViewFinder(Context context) {
        super(context);
    }

    public PicCropViewFinder(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PicCropViewFinder(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mHeight = getHeight();
        mWidth = getWidth();

        @SuppressLint("DrawAllocation")
        Paint p = new Paint();
        p.setColor(Color.WHITE);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(mStrokeWidth);
        canvas.drawRect(mMarginWidth, mHeight / 2 - (mWidth - 2 * mMarginWidth) / 2, mWidth - mMarginWidth, mHeight / 2 + (mWidth - 2 * mMarginWidth) / 2, p);// 裁剪框

//        Paint p1 = new Paint();
//        p1.setColor(Color.WHITE);
//        p1.setStyle(Paint.Style.FILL);
//        canvas.drawRect(mMarginWidth + (mWidth - 2 * mMarginWidth) / 3, mHeight / 2 - (mWidth - 2 * mMarginWidth) / 2, mMarginWidth + (mWidth - 2 * mMarginWidth) / 3 + mStrokeWidth / 2, mHeight / 2 + (mWidth - 2 * mMarginWidth) / 2, p1);// 裁剪框分割线
//        canvas.drawRect(mWidth - mMarginWidth - (mWidth - 2 * mMarginWidth) / 3, mHeight / 2 - (mWidth - 2 * mMarginWidth) / 2, mWidth - mMarginWidth - (mWidth - 2 * mMarginWidth) / 3 + mStrokeWidth / 2, mHeight / 2 + (mWidth - 2 * mMarginWidth) / 2, p1);// 裁剪框分割线
//        canvas.drawRect(mMarginWidth, mHeight / 2 - (mWidth - 2 * mMarginWidth) / 2 + (mWidth - 2 * mMarginWidth) / 3, mWidth - mMarginWidth, mHeight / 2 - (mWidth - 2 * mMarginWidth) / 2 + (mWidth - 2 * mMarginWidth) / 3 + mStrokeWidth / 2, p1);// 裁剪框分割线
//        canvas.drawRect(mMarginWidth, mHeight / 2 + (mWidth - 2 * mMarginWidth) / 2 - (mWidth - 2 * mMarginWidth) / 3, mWidth - mMarginWidth, mHeight / 2 + (mWidth - 2 * mMarginWidth) / 2 - (mWidth - 2 * mMarginWidth) / 3 + mStrokeWidth / 2, p1);// 裁剪框分割线

        @SuppressLint("DrawAllocation")
        Paint p2 = new Paint();
        p2.setColor(Color.BLACK);
        p2.setAlpha(100);
        canvas.drawRect(0, mHeight / 2 + (mWidth - 2 * mMarginWidth) / 2 + mStrokeWidth / 2, mWidth, mHeight, p2);// 裁剪框下方区域
        canvas.drawRect(0, 0, mWidth, mHeight / 2 - (mWidth - 2 * mMarginWidth) / 2 - mStrokeWidth / 2, p2);// 裁剪框上方区域
        canvas.drawRect(0, mHeight / 2 - (mWidth - 2 * mMarginWidth) / 2 - mStrokeWidth / 2, mMarginWidth - mStrokeWidth / 2 - mStrokeWidth / 2, mHeight / 2 + (mWidth - 2 * mMarginWidth) / 2 + mStrokeWidth / 2, p2);//裁剪框左方区域
        canvas.drawRect(mWidth - mMarginWidth + mStrokeWidth / 2, mHeight / 2 - (mWidth - 2 * mMarginWidth) / 2 - mStrokeWidth / 2, mWidth, mHeight / 2 + (mWidth - 2 * mMarginWidth) / 2 + mStrokeWidth / 2, p2);//裁剪框右方区域

    }

    public int getFinderHeight() {
        return mHeight;
    }

    public int getFinderWith() {
        return mWidth;
    }
}
