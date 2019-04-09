package com.tzpt.cloudlibrary.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.tzpt.cloudlibrary.R;

/**
 * Created by Administrator on 2018/11/9.
 */

public class TouchImageView extends ImageView {

    private PointF down = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;
    private Matrix matrix = new Matrix();
    private Matrix preMatrix = new Matrix();
    private Matrix savedMatrix = new Matrix();

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private static final int ZOOMCANCEL = 3;
    private int mode = NONE;

    private boolean isBig = false;

    private int widthScreen;
    private int heightScreen;

    private int touchImgWidth;
    private int touchImgHeight;

    private float defaultScale;

//    private long lastClickTime = 0;

    private Bitmap touchImg = null;

    private static final int DOUBLE_CLICK_TIME_SPACE = 300;
    private static final int DOUBLE_POINT_DISTANCE = 10;
    private static float MAX_SCALE = 3.0f;

    private int mRectangleParam;

    private int mCropSizeType;

    private int mMarginWidth = getResources().getDimensionPixelSize(R.dimen.crop_photo_margin) +
            getResources().getDimensionPixelSize(R.dimen.crop_photo_stroke_width) / 2;


    public TouchImageView(Context context) {
        super(context);
    }

    public TouchImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initImageView(int screenWidth, int screenHeight, int cropSizeType) {
        widthScreen = screenWidth;
        heightScreen = screenHeight;
        mCropSizeType = cropSizeType;

        touchImg = ((BitmapDrawable) getDrawable()).getBitmap();
        touchImgWidth = touchImg.getWidth();
        touchImgHeight = touchImg.getHeight();
        float scaleX;
        float scaleY;
        switch (cropSizeType) {
            case 1:
                mRectangleParam = 4;
                scaleX = (float) widthScreen / touchImgWidth;
                scaleY = (float) (widthScreen / 2) / touchImgHeight;
                break;
            case 2:
                mRectangleParam = 4;
                scaleX = (float) (widthScreen / 2) / touchImgWidth;
                scaleY = (float) (widthScreen / 2) / touchImgHeight;
                break;
            default:
                mRectangleParam = 2;
                scaleX = (float) (widthScreen - 2 * mMarginWidth) / touchImgWidth;
                scaleY = (float) (widthScreen - 2 * mMarginWidth) / touchImgHeight;
                break;
        }
        defaultScale = scaleY > scaleX ? scaleY : scaleX;

        float subX = (widthScreen - touchImgWidth * defaultScale) / 2;
        float subY = (heightScreen - touchImgHeight * defaultScale) / 2;
        setScaleType(ScaleType.MATRIX);
        preMatrix.reset();
        preMatrix.postScale(defaultScale, defaultScale);
        preMatrix.postTranslate(subX, subY);
        matrix.set(preMatrix);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (null != touchImg) {
            canvas.save();
            canvas.drawBitmap(touchImg, matrix, null);
            canvas.restore();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (touchImg == null) {
            return false;
        }

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = DRAG;
                down.x = event.getX();
                down.y = event.getY();
                savedMatrix.set(matrix);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > DOUBLE_POINT_DISTANCE) {
                    mode = ZOOM;
                    // oldRotation = rotation(event);
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == ZOOM) {
                    float newDist = spacing(event);
                    float scale = newDist / oldDist;
                    if (scale > 1.01 || scale < 0.99) {
                        preMatrix.set(savedMatrix);
                        preMatrix.postScale(scale, scale, mid.x, mid.y);// 缩放
                        if (canZoom()) {
                            matrix.set(preMatrix);
                            invalidate();
                        }
                    }
                } else if (mode == DRAG) {
                    if (1.0f < distance(event, down)) {
                        preMatrix.set(savedMatrix);
                        preMatrix.postTranslate(event.getX() - down.x, 0);
                        preMatrix.postTranslate(0, event.getY() - down.y);

                        savedMatrix.set(preMatrix);

                        matrix.set(preMatrix);
                        invalidate();
                        down.x = event.getX();
                        down.y = event.getY();
                        savedMatrix.set(matrix);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mode == ZOOMCANCEL) {
                    zoomBack();
                }
                mode = NONE;
                springback();
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = ZOOMCANCEL;
                break;
        }
        return true;
    }

    private void zoomBack() {
        preMatrix.set(matrix);
        float[] x = new float[4];
        float[] y = new float[4];
        getFourPoint(x, y);
        if ((x[0] > mMarginWidth && x[1] < widthScreen - mMarginWidth) ||
                (y[0] > heightScreen / 2 - (widthScreen - 2 * mMarginWidth) / mRectangleParam
                        && y[2] < heightScreen / 2 + (widthScreen - 2 * mMarginWidth) / mRectangleParam)) {
            float subX = (widthScreen - touchImgWidth * defaultScale) / 2;
            float subY = (heightScreen - touchImgHeight * defaultScale) / 2;
            setScaleType(ScaleType.MATRIX);
            preMatrix.reset();
            preMatrix.postScale(defaultScale, defaultScale);
            preMatrix.postTranslate(subX, subY);
            matrix.set(preMatrix);
            invalidate();
        }
    }

    private void springback() {
        preMatrix.set(matrix);
        float[] x = new float[4];
        float[] y = new float[4];
        getFourPoint(x, y);
        if (mCropSizeType == 2) {
            if (x[0] > widthScreen / 4) {
                preMatrix.postTranslate(widthScreen / 4 - x[0], 0);
                matrix.set(preMatrix);
                invalidate();
            } else if (x[1] < widthScreen - widthScreen / 4) {
                preMatrix.postTranslate(widthScreen - widthScreen / 4 - x[1], 0);
                matrix.set(preMatrix);
                invalidate();
            }
        } else {
            if (x[0] > mMarginWidth) {
                preMatrix.postTranslate(-x[0] + mMarginWidth, 0);
                matrix.set(preMatrix);
                invalidate();
            } else if (x[1] < widthScreen - mMarginWidth) {
                preMatrix.postTranslate(widthScreen - x[1] - mMarginWidth, 0);
                matrix.set(preMatrix);
                invalidate();
            }
        }

        // if (x[1] - x[0] > widthScreen) {
        //
        // } else if (x[1] - x[0] < widthScreen - 1f) {
        // preMatrix.postTranslate((widthScreen - (x[1] - x[0])) / 2 - x[0], 0);
        // matrix.set(preMatrix);
        // invalidate();
        // }

        if (y[0] > heightScreen / 2 - (widthScreen - 2 * mMarginWidth) / mRectangleParam) {
            preMatrix.postTranslate(0, -y[0] + heightScreen / 2 - (widthScreen - 2 * mMarginWidth) / mRectangleParam);
            matrix.set(preMatrix);
            invalidate();
        } else if (y[2] < heightScreen / 2 + (widthScreen - 2 * mMarginWidth) / mRectangleParam) {
            preMatrix.postTranslate(0, heightScreen / 2 + (widthScreen - 2 * mMarginWidth) / mRectangleParam - y[2]);
            matrix.set(preMatrix);
            invalidate();
        }
        // if (y[2] - y[0] > heightScreen) {
        //
        // } else if (y[2] - y[0] < heightScreen - 1f) {
        // preMatrix.postTranslate(0, (heightScreen - (y[2] - y[0])) / 2 - y[0]);
        // matrix.set(preMatrix);
        // invalidate();
        // }
    }

    private void getFourPoint(float[] x, float[] y) {
        float[] f = new float[9];
        preMatrix.getValues(f);
        // 图片4个顶点的坐标
        x[0] = f[Matrix.MSCALE_X] * 0 + f[Matrix.MSKEW_X] * 0 + f[Matrix.MTRANS_X];
        y[0] = f[Matrix.MSKEW_Y] * 0 + f[Matrix.MSCALE_Y] * 0 + f[Matrix.MTRANS_Y];
        x[1] = f[Matrix.MSCALE_X] * touchImg.getWidth() + f[Matrix.MSKEW_X] * 0 + f[Matrix.MTRANS_X];
        y[1] = f[Matrix.MSKEW_Y] * touchImg.getWidth() + f[Matrix.MSCALE_Y] * 0 + f[Matrix.MTRANS_Y];
        x[2] = f[Matrix.MSCALE_X] * 0 + f[Matrix.MSKEW_X] * touchImg.getHeight() + f[Matrix.MTRANS_X];
        y[2] = f[Matrix.MSKEW_Y] * 0 + f[Matrix.MSCALE_Y] * touchImg.getHeight() + f[Matrix.MTRANS_Y];
        x[3] = f[Matrix.MSCALE_X] * touchImg.getWidth() + f[Matrix.MSKEW_X] * touchImg.getHeight() + f[Matrix.MTRANS_X];
        y[3] = f[Matrix.MSKEW_Y] * touchImg.getWidth() + f[Matrix.MSCALE_Y] * touchImg.getHeight() + f[Matrix.MTRANS_Y];
    }

    private boolean canZoom() {
        float[] x = new float[4];
        float[] y = new float[4];
        getFourPoint(x, y);
        // 图片现宽度
        double width = Math.sqrt((x[0] - x[1]) * (x[0] - x[1]) + (y[0] - y[1]) * (y[0] - y[1]));
        double height = Math.sqrt((x[0] - x[2]) * (x[0] - x[2]) + (y[0] - y[2]) * (y[0] - y[2]));
        // 缩放比率判断
        if (width < touchImgWidth * defaultScale - 41) {
            return true;
        }

        if (width > touchImgWidth * MAX_SCALE + 39) {
            return false;
        }

        // 出界判断
        if (width < widthScreen - 2 * mMarginWidth && height < heightScreen - 2 * mMarginWidth) {
            return true;
        }
        return true;
    }

    // 触碰两点间距离
    private static float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        if (x < 0) {
            x = -x;
        }
        float y = event.getY(0) - event.getY(1);
        if (y < 0) {
            y = -y;
        }
        return (float) Math.sqrt(x * x + y * y);
    }

    // 取手势中心点
    private static void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    // 取两点之间的距离
    private static float distance(MotionEvent point2, PointF point1) {
        float x = point1.x - point2.getX();
        if (x < 0) {
            x = -x;
        }
        float y = point1.y - point2.getY();
        if (y < 0) {
            y = -y;
        }
        return (float) Math.sqrt(x * x + y * y);
    }

    private void changeSize(float x, float y) {
        if (isBig) {
            float subX = (widthScreen - touchImgWidth * defaultScale) / 2;
            float subY = (heightScreen - touchImgHeight * defaultScale) / 2;
            preMatrix.reset();
            preMatrix.postScale(defaultScale, defaultScale);
            preMatrix.postTranslate(subX, subY);
            matrix.set(preMatrix);
            invalidate();

            isBig = false;
        } else {
            float transX = (widthScreen - touchImgWidth * MAX_SCALE) / 2;
            float transY = (heightScreen - touchImgHeight * MAX_SCALE) / 2;
            preMatrix.reset();
            preMatrix.postScale(MAX_SCALE, MAX_SCALE);
            preMatrix.postTranslate(transX, transY);
            matrix.set(preMatrix);
            invalidate();

            isBig = true;
        }
    }

    public void release() {
        if (touchImg != null) {
            touchImg.recycle();
            touchImg = null;
        }

        down = null;
        mid = null;
        matrix = null;
        preMatrix = null;
        savedMatrix = null;
    }

}
