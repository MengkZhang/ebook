package com.tzpt.cloudlibrary.widget.camera;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.util.AttributeSet;

import com.tzpt.cloudlibrary.R;

/**
 * 面部识别框
 * Created by tonyjia on 2018/3/16.
 */
public class FaceView extends android.support.v7.widget.AppCompatImageView {

    private Camera.Face[] mFaces;
    private Paint mLinePaint;
    private int mFaceBottom;

    public FaceView(Context context) {
        this(context, null);
    }

    public FaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public FaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint(context, attrs);
    }


    private void initPaint(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.face_view);
        mFaceBottom = array.getDimensionPixelSize(R.styleable.face_view_face_view_bottom, 0);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(Color.RED);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(5f);
        mLinePaint.setAlpha(180);
    }

    public void setFaces(Camera.Face[] faces) {
        this.mFaces = faces;
        invalidate();
    }

    public void clearFaces() {
        mFaces = null;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mFaces == null || mFaces.length < 1) {
            return;
        }
        if (mFaces.length >= 1) {
            int height = getHeight();
            int mWidth = getWidth();
            canvas.translate(mWidth / 2, height / 2);
            canvas.rotate(-0);
            for (Camera.Face face : mFaces) {
                int width = face.rect.right - face.rect.left;
                int needWidth = mWidth * width / 2000;
                int cx = -face.rect.centerY();
                int cy = -face.rect.centerX();

                int left = (int) (mWidth * cx / 2000f - needWidth);
                int top = (int) (height * cy / 2000f - needWidth);
                int right = (int) (mWidth * cx / 2000f + needWidth);
                int bottom = (int) (height * cy / 2000f + needWidth);

                int realBottomLocation = (height - bottom * 2);
                if (realBottomLocation >= mFaceBottom) {
                    canvas.drawRect(left, top, right, bottom, mLinePaint);
                }
            }
        }
    }

}
