package com.tzpt.cloundlibrary.manager.widget.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.tzpt.cloundlibrary.manager.R;

/**
 * 身份证扫描控件
 * Created by Administrator on 2018/12/17.
 */

public class IDCardScanBoxView extends View {
    private int mMoveStepDistance;

    private Rect mFramingRect;
    private float mScanLineLeft;
    private Paint mPaint;

    private int mMaskColor;
    private int mCornerColor;
    private int mCornerLength;
    private int mCornerSize;
    private int mRectWidth;
    private int mRectHeight;
    private int mTopOffset;
    private Bitmap mGridScanLineBitmap;
    private float mGridScanLineLeft;

    private float mHalfCornerSize;

    public IDCardScanBoxView(Context context) {
        this(context, null);
    }

    public IDCardScanBoxView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDefault(context);
        afterInitCustomAttrs();
    }

    private void initDefault(Context context) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mMaskColor = Color.parseColor("#7F000000");
        mCornerColor = Color.parseColor("#fe8108");
        mCornerLength = ScanBoxHelper.dp2px(context, 20);
        mCornerSize = ScanBoxHelper.dp2px(context, 5);
        mTopOffset = ScanBoxHelper.dp2px(context, 112.5f);
        mRectWidth = ScanBoxHelper.dp2px(context, 250);
        mMoveStepDistance = ScanBoxHelper.dp2px(context, 2);
    }

    private void afterInitCustomAttrs() {
        mGridScanLineBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.qrcode_default_grid_scan_line);

        mHalfCornerSize = 1.0f * mCornerSize / 2;

        setIsBarcode();
    }

    private void setIsBarcode() {
        mRectHeight = (int) (mRectWidth * 1.58);

        calFramingRect();

        postInvalidate();
    }


    private void calFramingRect() {
        int leftOffset = (getWidth() - mRectWidth) / 2;
        mFramingRect = new Rect(leftOffset, mTopOffset, leftOffset + mRectWidth, mTopOffset + mRectHeight);

        mGridScanLineLeft = mFramingRect.right - mHalfCornerSize - 0.5f;
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
     */
    private void drawScanLine(Canvas canvas) {
        if (mGridScanLineBitmap != null) {
            RectF dstGridRectFR = new RectF(mGridScanLineLeft,
                    mFramingRect.top + mHalfCornerSize,
                    mFramingRect.right - mHalfCornerSize - 0.5f,
                    mFramingRect.bottom - mHalfCornerSize);
            Rect srcGridRectR = new Rect(0, 0, (int) dstGridRectFR.width(), mGridScanLineBitmap.getHeight());

            canvas.drawBitmap(mGridScanLineBitmap, srcGridRectR, dstGridRectFR, mPaint);
        }
    }

    /**
     * 移动扫描线的位置
     */
    private void moveScanLine() {
        if (mGridScanLineBitmap == null) {
            mScanLineLeft += mMoveStepDistance;
            int scanLineSize = 0;
            if (mScanLineLeft + scanLineSize > mFramingRect.right - mHalfCornerSize) {
                mScanLineLeft = mFramingRect.left + mHalfCornerSize + 0.5f;
            }
        } else {
            mGridScanLineLeft -= mMoveStepDistance;
            if (mGridScanLineLeft < mFramingRect.left + mHalfCornerSize) {
                mGridScanLineLeft = mFramingRect.right - mHalfCornerSize - 0.5f;
            }
        }
        postInvalidate(mFramingRect.left, mFramingRect.top, mFramingRect.right, mFramingRect.bottom);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calFramingRect();
    }

}
