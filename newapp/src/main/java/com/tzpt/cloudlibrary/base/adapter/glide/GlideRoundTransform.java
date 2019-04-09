package com.tzpt.cloudlibrary.base.adapter.glide;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;

import java.security.MessageDigest;

/**
 * Created by Administrator on 2017/6/12.
 */

public class GlideRoundTransform extends BitmapTransformation {
    private float mRadius;
    private CornerType mCornerType;

    public enum CornerType {
        ALL,
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT,
        TOP, BOTTOM, LEFT, RIGHT,
        TOP_LEFT_BOTTOM_RIGHT,
        TOP_RIGHT_BOTTOM_LEFT,
        TOP_LEFT_TOP_RIGHT_BOTTOM_RIGHT,
        TOP_RIGHT_BOTTOM_RIGHT_BOTTOM_LEFT,
    }

    public GlideRoundTransform(float radius, CornerType cornerType) {
        super();
        mRadius = dp2px(radius);//dp ->px
        mCornerType = cornerType;
    }

    private float dp2px(float dpValue) {
        return dpValue * Resources.getSystem().getDisplayMetrics().density;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        Bitmap bitmap = TransformationUtils.centerCrop(pool, toTransform, outWidth, outHeight);
        return roundCrop(pool, bitmap);
    }

    private Bitmap roundCrop(BitmapPool pool, Bitmap source) {
        if (source == null) {
            return null;
        }
        int width = source.getWidth();
        int height = source.getHeight();
        Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);


        if (result == null) {
            result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config
                    .ARGB_8888);
        }
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader
                .TileMode.CLAMP));
        paint.setAntiAlias(true);


        Path path = new Path();
        drawRoundRect(canvas, paint, path, width, height);

        return result;
    }

    private void drawRoundRect(Canvas canvas, Paint paint, Path path, int width, int height) {
        float[] rids;
        switch (mCornerType) {
            case ALL:
                rids = new float[]{mRadius, mRadius, mRadius, mRadius, mRadius, mRadius, mRadius, mRadius};
                drawPath(rids, canvas, paint, path, width, height);
                break;
            case TOP_LEFT:
                rids = new float[]{mRadius, mRadius, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
                drawPath(rids, canvas, paint, path, width, height);
                break;
            case TOP_RIGHT:
                rids = new float[]{0.0f, 0.0f, mRadius, mRadius, 0.0f, 0.0f, 0.0f, 0.0f};
                drawPath(rids, canvas, paint, path, width, height);
                break;
            case BOTTOM_RIGHT:
                rids = new float[]{0.0f, 0.0f, 0.0f, 0.0f, mRadius, mRadius, 0.0f, 0.0f};
                drawPath(rids, canvas, paint, path, width, height);
                break;
            case BOTTOM_LEFT:
                rids = new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, mRadius, mRadius};
                drawPath(rids, canvas, paint, path, width, height);
                break;
            case TOP:
                rids = new float[]{mRadius, mRadius, mRadius, mRadius, 0.0f, 0.0f, 0.0f, 0.0f};
                drawPath(rids, canvas, paint, path, width, height);
                break;
            case BOTTOM:
                rids = new float[]{0.0f, 0.0f, 0.0f, 0.0f, mRadius, mRadius, mRadius, mRadius};
                drawPath(rids, canvas, paint, path, width, height);
                break;
            case LEFT:
                rids = new float[]{mRadius, mRadius, 0.0f, 0.0f, 0.0f, 0.0f, mRadius, mRadius};
                drawPath(rids, canvas, paint, path, width, height);
                break;
            case RIGHT:
                rids = new float[]{0.0f, 0.0f, mRadius, mRadius, mRadius, mRadius, 0.0f, 0.0f};
                drawPath(rids, canvas, paint, path, width, height);
                break;
            case TOP_LEFT_BOTTOM_RIGHT:
                rids = new float[]{mRadius, mRadius, 0.0f, 0.0f, mRadius, mRadius, 0.0f, 0.0f};
                drawPath(rids, canvas, paint, path, width, height);
                break;
            case TOP_RIGHT_BOTTOM_LEFT:
                rids = new float[]{0.0f, 0.0f, mRadius, mRadius, 0.0f, 0.0f, mRadius, mRadius};
                drawPath(rids, canvas, paint, path, width, height);
                break;
            case TOP_LEFT_TOP_RIGHT_BOTTOM_RIGHT:
                rids = new float[]{mRadius, mRadius, mRadius, mRadius, mRadius, mRadius, 0.0f, 0.0f};
                drawPath(rids, canvas, paint, path, width, height);
                break;
            case TOP_RIGHT_BOTTOM_RIGHT_BOTTOM_LEFT:
                rids = new float[]{0.0f, 0.0f, mRadius, mRadius, mRadius, mRadius, mRadius, mRadius};
                drawPath(rids, canvas, paint, path, width, height);
                break;
            default:
                throw new RuntimeException("RoundedCorners type not belong to CornerType");


        }
    }


    /**
     * @param rids 圆角的半径，依次为左上角xy半径，右上角，右下角，左下角
     */
    private void drawPath(float[] rids, Canvas canvas, Paint paint, Path path, int width, int height) {
        path.addRoundRect(new RectF(0, 0, width, height), rids, Path.Direction.CW);
        canvas.drawPath(path, paint);
    }


    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {

    }

    @Override
    public boolean equals(Object o) {
        return o instanceof GlideRoundTransform;
    }
}
