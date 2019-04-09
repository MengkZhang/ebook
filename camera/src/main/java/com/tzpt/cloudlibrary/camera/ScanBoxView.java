package com.tzpt.cloudlibrary.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * 遮罩层
 * Created by Administrator on 2019/1/2.
 */

public class ScanBoxView extends View implements IFrameRect {

    private int mMoveStepDistance;
    private int mAnimDelayTime;
    private int mAnimTime;

    private Rect mFramingRect;                      //扫描框
    private float mScanLineTop;                     //扫描线顶部位置
    private Paint mPaint;                           //
    private int mMaskColor;                         //遮罩蒙版颜色
    private int mCornerColor;                       //四个直角的颜色
    private int mCornerLength;                      //四个直角的长度
    private int mCornerSize;                        //四个直角的大小
    private int mRectWidth;                         //扫描框矩形宽度
    private int mRectHeight;                        //扫描框矩形高度
    private int mTopOffset;                         //扫描框距离顶部偏移量
    private int mScanLineSize;                      //扫描线的宽度
    private int mScanLineColor;                     //扫描线的颜色，如果图片没有获取成功，可以使用这个
    private Bitmap mScanLineBitmap;                 //扫描线的资源图片
    private float mHalfCornerSize;

    public ScanBoxView(Context context) {
        this(context, null);
    }

    public ScanBoxView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDefault(context);
        initCustomAttrs(context, attrs);
        setIsBarcode();
    }

    private void initDefault(Context context) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mRectWidth = dp2px(context, 240);
        mRectHeight = dp2px(context, 240);
        mMaskColor = Color.parseColor("#7F000000");
        mCornerColor = Color.parseColor("#fe8108");
        mCornerLength = dp2px(context, 20);
        mCornerSize = dp2px(context, 4);
        mScanLineSize = dp2px(context, 4);
        mScanLineColor = Color.parseColor("#fe8108");
        mTopOffset = dp2px(context, 135);
        mScanLineBitmap = null;
        mAnimTime = 500;
        mMoveStepDistance = dp2px(context, 2);
    }


    public void initCustomAttrs(Context context, AttributeSet attrs) {
        @SuppressLint("CustomViewStyleable")
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScanBoxView);
        final int count = typedArray.getIndexCount();
        for (int i = 0; i < count; i++) {
            initCustomAttr(typedArray.getIndex(i), typedArray);
        }
        typedArray.recycle();
    }

    private void initCustomAttr(int attr, TypedArray typedArray) {
        if (attr == R.styleable.ScanBoxView_sbv_topOffset) {
            mTopOffset = typedArray.getDimensionPixelSize(attr, mTopOffset);
        } else if (attr == R.styleable.ScanBoxView_sbv_cornerSize) {
            mCornerSize = typedArray.getDimensionPixelSize(attr, mCornerSize);
        } else if (attr == R.styleable.ScanBoxView_sbv_cornerLength) {
            mCornerLength = typedArray.getDimensionPixelSize(attr, mCornerLength);
        } else if (attr == R.styleable.ScanBoxView_sbv_scanLineSize) {
            mScanLineSize = typedArray.getDimensionPixelSize(attr, mScanLineSize);
        } else if (attr == R.styleable.ScanBoxView_sbv_rectWidth) {
            mRectWidth = typedArray.getDimensionPixelSize(attr, mRectWidth);
        } else if (attr == R.styleable.ScanBoxView_sbv_maskColor) {
            mMaskColor = typedArray.getColor(attr, mMaskColor);
        } else if (attr == R.styleable.ScanBoxView_sbv_cornerColor) {
            mCornerColor = typedArray.getColor(attr, mCornerColor);
        } else if (attr == R.styleable.ScanBoxView_sbv_scanLineColor) {
            mScanLineColor = typedArray.getColor(attr, mScanLineColor);
        } else if (attr == R.styleable.ScanBoxView_sbv_customScanLineDrawable) {
            Drawable customScanLineDrawable = typedArray.getDrawable(attr);
            if (customScanLineDrawable != null) {
                mScanLineBitmap = ((BitmapDrawable) customScanLineDrawable).getBitmap();
            }
        } else if (attr == R.styleable.ScanBoxView_sbv_animTime) {
            mAnimTime = typedArray.getInteger(attr, mAnimTime);
        }
    }


    private void setIsBarcode() {
        mHalfCornerSize = 1.0f * mCornerSize / 2;

        mAnimDelayTime = (int) ((1.0f * mAnimTime * mMoveStepDistance) / mRectHeight);

        calFramingRect();
        postInvalidate();
    }

    private void calFramingRect() {
        int leftOffset = (getWidth() - mRectWidth) / 2;
        mFramingRect = new Rect(leftOffset, mTopOffset, leftOffset + mRectWidth, mTopOffset + mRectHeight);

        mScanLineTop = mFramingRect.top + mHalfCornerSize + 0.5f;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (mFramingRect == null) {
            return;
        }

        // 画遮罩层
        drawMask(canvas);
        // 画四个直角的线
        drawCornerLine(canvas);

        // 画扫描线
        drawScanLine(canvas);

        // 移动扫描线的位置
        moveScanLine();
    }

    /**
     * 画遮罩层
     *
     * @param canvas 画布
     */
    private void drawMask(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        if (mMaskColor != Color.TRANSPARENT) {
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mMaskColor);
            canvas.drawRect(0, 0, width, mFramingRect.top, mPaint);
            canvas.drawRect(0, mFramingRect.top, mFramingRect.left, mFramingRect.bottom + 1, mPaint);
            canvas.drawRect(mFramingRect.right + 1, mFramingRect.top, width, mFramingRect.bottom + 1, mPaint);
            canvas.drawRect(0, mFramingRect.bottom + 1, width, height, mPaint);
        }
    }

    /**
     * 画四个直角的线
     *
     * @param canvas 画布
     */
    private void drawCornerLine(Canvas canvas) {
        if (mHalfCornerSize > 0) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(mCornerColor);
            mPaint.setStrokeWidth(mCornerSize);
            canvas.drawLine(mFramingRect.left - mHalfCornerSize, mFramingRect.top, mFramingRect.left - mHalfCornerSize + mCornerLength, mFramingRect.top, mPaint);
            canvas.drawLine(mFramingRect.left, mFramingRect.top - mHalfCornerSize, mFramingRect.left, mFramingRect.top - mHalfCornerSize + mCornerLength, mPaint);
            canvas.drawLine(mFramingRect.right + mHalfCornerSize, mFramingRect.top, mFramingRect.right + mHalfCornerSize - mCornerLength, mFramingRect.top, mPaint);
            canvas.drawLine(mFramingRect.right, mFramingRect.top - mHalfCornerSize, mFramingRect.right, mFramingRect.top - mHalfCornerSize + mCornerLength, mPaint);

            canvas.drawLine(mFramingRect.left - mHalfCornerSize, mFramingRect.bottom, mFramingRect.left - mHalfCornerSize + mCornerLength, mFramingRect.bottom, mPaint);
            canvas.drawLine(mFramingRect.left, mFramingRect.bottom + mHalfCornerSize, mFramingRect.left, mFramingRect.bottom + mHalfCornerSize - mCornerLength, mPaint);
            canvas.drawLine(mFramingRect.right + mHalfCornerSize, mFramingRect.bottom, mFramingRect.right + mHalfCornerSize - mCornerLength, mFramingRect.bottom, mPaint);
            canvas.drawLine(mFramingRect.right, mFramingRect.bottom + mHalfCornerSize, mFramingRect.right, mFramingRect.bottom + mHalfCornerSize - mCornerLength, mPaint);
        }
    }

    /**
     * 画扫描线
     *
     * @param canvas 画布
     */
    private void drawScanLine(Canvas canvas) {
        if (mScanLineBitmap != null) {
            RectF lineRect = new RectF(mFramingRect.left + mHalfCornerSize, mScanLineTop, mFramingRect.right - mHalfCornerSize, mScanLineTop + mScanLineBitmap.getHeight());
            canvas.drawBitmap(mScanLineBitmap, null, lineRect, mPaint);
        } else {
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mScanLineColor);
            canvas.drawRect(mFramingRect.left + mHalfCornerSize, mScanLineTop, mFramingRect.right - mHalfCornerSize, mScanLineTop + mScanLineSize, mPaint);
        }
    }

    /**
     * 移动扫描线的位置
     */
    private void moveScanLine() {
        mScanLineTop += mMoveStepDistance;
        int scanLineSize = mScanLineSize;
        if (mScanLineBitmap != null) {
            scanLineSize = mScanLineBitmap.getHeight();
        }

        if (mScanLineTop + scanLineSize > mFramingRect.bottom - mHalfCornerSize) {
            mScanLineTop = mFramingRect.top + mHalfCornerSize + 0.5f;
        }
        postInvalidateDelayed(mAnimDelayTime, mFramingRect.left, mFramingRect.top, mFramingRect.right, mFramingRect.bottom);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calFramingRect();
    }

    @Override
    public Rect getFramingRect() {
        return mFramingRect;
    }

    private int dp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }
}
