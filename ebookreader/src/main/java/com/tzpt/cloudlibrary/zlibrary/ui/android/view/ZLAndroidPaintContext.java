package com.tzpt.cloudlibrary.zlibrary.ui.android.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;

import com.tzpt.cloudlibrary.zlibrary.core.filesystem.ZLFile;
import com.tzpt.cloudlibrary.zlibrary.core.image.ZLImageData;
import com.tzpt.cloudlibrary.zlibrary.core.util.ZLColor;
import com.tzpt.cloudlibrary.zlibrary.core.view.ZLPaintContext;
import com.tzpt.cloudlibrary.zlibrary.ui.android.image.ZLAndroidImageData;
import com.tzpt.cloudlibrary.zlibrary.ui.android.library.ZLAndroidLibrary;
import com.tzpt.cloudlibrary.zlibrary.ui.android.util.ZLAndroidColorUtil;

/**
 * 在Canvas上进行内容绘制，包括文字和图片等信息
 * Created by Administrator on 2017/4/8.
 */

public final class ZLAndroidPaintContext extends ZLPaintContext {
    private float dp_1 = ZLAndroidLibrary.Instance().getDPI();
    private float sp_1 = ZLAndroidLibrary.Instance().getSP();

    private final Canvas mCanvas;//画布
    private final Paint mTextPaint = new Paint(); // 文字画笔
    //    private final Paint mLinePaint = new Paint();
    private final Paint mFillPaint = new Paint();
    private final Paint mFooterPaint = new Paint();
    private final Paint mTopPaint = new Paint();
//    private final Paint mOutlinePaint = new Paint();

    /**
     * 画笔信息
     */
    private Paint mBatteryPaint;
    private Paint mPowerPaint;
    private float mBatteryStroke = 2f;

    /**
     * 电池参数
     */
    private float mBatteryHeight = 10 * dp_1; // 电池的高度
    private float mBatteryWidth = 21 * dp_1; // 电池的宽度
    private float mCapHeight = 6 * dp_1; // 电池头高度
    private float mCapWidth = 2 * dp_1; // 电池头宽度
    /**
     * 电池电量
     */
    private float mPowerPadding = (float) (0.6 * dp_1);
    private float mPowerHeight = mBatteryHeight - mBatteryStroke - mPowerPadding * 2; // 电池身体的高度
    private float mPowerWidth = mBatteryWidth - mBatteryStroke - mPowerPadding * 2;// 电池身体的总宽度

    /**
     * 整个页面的几何区域
     */
    static final class Geometry {
        //屏幕大小
        final Size ScreenSize;
        //区域大小
        final Size AreaSize;
        final int LeftMargin;
        final int TopMargin;

        Geometry(int screenWidth, int screenHeight, int width, int height, int leftMargin, int topMargin) {
            ScreenSize = new Size(screenWidth, screenHeight);
            AreaSize = new Size(width, height);
            LeftMargin = leftMargin;
            TopMargin = topMargin;
        }
    }

    private final Geometry mGeometry;

    private int mTextColor;//正文，页眉，页脚，电量等的颜色值

    ZLAndroidPaintContext(Canvas canvas, Geometry geometry) {
//        super(systemInfo);

        mCanvas = canvas;
        mGeometry = geometry;

        mTextPaint.setLinearText(false); // 线性文本
        mTextPaint.setAntiAlias(true); // 防锯齿
        mTextPaint.setFlags(mTextPaint.getFlags() | Paint.DEV_KERN_TEXT_FLAG);
        // 图像的抖动处理，当每个颜色值以低于8位表示时，对应图像做抖动处理可以实现在可显示颜色总数比较低（比如256色）时还保持较好的显示效果
        mTextPaint.setDither(true);
        // 将有助于文本在LCD屏幕上的显示效果
        mTextPaint.setSubpixelText(true);


        // Paint.Style.FILL    :填充内部
        // Paint.Style.FILL_AND_STROKE  ：填充内部和描边
        // Paint.Style.STROKE  ：仅描边
//        mLinePaint.setStyle(Paint.Style.STROKE);

        mFillPaint.setAntiAlias(true);
//        myFillPaint.setAntiAlias(AntiAliasOption.getValue());

//        mOutlinePaint.setAntiAlias(true);
//        mOutlinePaint.setDither(true);
//        mOutlinePaint.setStrokeWidth(4); // 画笔宽度
//        mOutlinePaint.setStyle(Paint.Style.STROKE);
//        // 这个方法一看就和path有关，顾名思义，它就是给path设置样式（效果）的。PathEffect这个路径效果类没有具体的实现，效果是由它的六个子类实现的
//        mOutlinePaint.setPathEffect(new CornerPathEffect(5));
//        mOutlinePaint.setMaskFilter(new EmbossMaskFilter(new float[]{1, 1, 1}, .4f, 6f, 3.5f));

        //设置电池画笔
        mBatteryPaint = new Paint();
        mBatteryPaint.setColor(Color.GRAY);
        mBatteryPaint.setAntiAlias(true);
        mBatteryPaint.setStyle(Paint.Style.STROKE);
        mBatteryPaint.setStrokeWidth(mBatteryStroke);


        // 设置电量画笔
        mPowerPaint = new Paint();
        mPowerPaint.setColor(Color.GRAY);
        mPowerPaint.setAntiAlias(true);
        mPowerPaint.setStyle(Paint.Style.FILL);
        mPowerPaint.setStrokeWidth(mBatteryStroke);

        mFooterPaint.setTextSize(11 * sp_1);
        mFooterPaint.setLinearText(false);
        mFooterPaint.setAntiAlias(true);
        mFooterPaint.setDither(true);
        mFooterPaint.setSubpixelText(true);
        mFooterPaint.setAlpha(199);

        mTopPaint.setTextSize(11 * sp_1);
        mTopPaint.setLinearText(false);
        mTopPaint.setAntiAlias(true);
        mTopPaint.setDither(true);
        mTopPaint.setSubpixelText(true);
        mTopPaint.setAlpha(199);
    }

    private static ZLFile ourWallpaperFile;
    private static Bitmap ourWallpaper;

    @Override
    public void setBackground(ZLFile wallpaperFile) {
        if (!wallpaperFile.equals(ourWallpaperFile)) {
            ourWallpaperFile = wallpaperFile;
            ourWallpaper = null;
            try {
                ourWallpaper = BitmapFactory.decodeStream(wallpaperFile.getInputStream());
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        if (ourWallpaper != null) {
            final int w = ourWallpaper.getWidth();
            final int h = ourWallpaper.getHeight();
            final Geometry g = mGeometry;
            final Matrix m = new Matrix();
            m.preScale(1f * g.ScreenSize.Width / w, 1f * g.ScreenSize.Height / h);
            m.postTranslate(-g.LeftMargin, -g.TopMargin);
            mCanvas.drawBitmap(ourWallpaper, m, mFillPaint);
        } else {
            setBackground(new ZLColor(128, 128, 128));
        }
    }

    @Override
    public void setBackground(ZLColor color) {
        mFillPaint.setColor(ZLAndroidColorUtil.rgb(color));
        mCanvas.drawRect(0, 0, mGeometry.AreaSize.Width, mGeometry.AreaSize.Height, mFillPaint);
    }

    @Override
    public ZLColor getBackgroundColor() {
        return null;
    }

    public void fillPolygon(float[] xs, float[] ys) {
//        final Path path = new Path();
//        final int last = xs.length - 1;
//        path.moveTo(xs[last], ys[last]);
//        for (int i = 0; i <= last; ++i) {
//            path.lineTo(xs[i], ys[i]);
//        }
//        mCanvas.drawPath(path, mFillPaint);
    }

    public void drawPolygonalLine(int[] xs, int[] ys) { // 多边形
//        final Path path = new Path();
//        final int last = xs.length - 1;
//        path.moveTo(xs[last], ys[last]);
//        for (int i = 0; i <= last; ++i) {
//            path.lineTo(xs[i], ys[i]);
//        }
//        mCanvas.drawPath(path, mLinePaint);
    }

    public void drawOutline(float[] xs, float[] ys) {
////        LogUtil.i25("共有 " + xs.length + " 个点");
//        final int last = xs.length - 1;
//        float xStart = (xs[0] + xs[last]) / 2;
//        float yStart = (ys[0] + ys[last]) / 2;
//        float xEnd = xStart;
//        float yEnd = yStart;
//        if (xs[0] != xs[last]) {
//            if (xs[0] > xs[last]) {
//                xStart -= 2;
//                xEnd += 2;
//            } else {
//                xStart += 2;
//                xEnd -= 2;
//            }
//        } else {
//            if (ys[0] > ys[last]) {
//                yStart -= 2;
//                yEnd += 2;
//            } else {
//                yStart += 2;
//                yEnd -= 2;
//            }
//        }
//
//        final Path path = new Path();
//        path.moveTo(xStart, yStart);
//        for (int i = 0; i <= last; ++i) {
//            path.lineTo(xs[i], ys[i]);
//        }
//        path.lineTo(xEnd, yEnd);
////        myCanvas.drawLine(myLinePaint);
//        mCanvas.drawPath(path, mOutlinePaint);
    }

    @Override
    protected void setFontInternal(Typeface typeface, int size, boolean bold, boolean italic, boolean underline, boolean strikeThrought) {
//        myTextPaint.setTypeface(typeface);
        mTextPaint.setTextSize(size);
//        myTextPaint.setUnderlineText(underline);
        mTextPaint.setStrikeThruText(strikeThrought);

//        myFooterPaint.setTypeface(typeface);
//        myTopPaint.setTypeface(typeface);
    }

    @Override
    public void setTextColor(ZLColor color) {
        if (color != null) {
            mTextColor = ZLAndroidColorUtil.rgb(color);
            mTextPaint.setColor(mTextColor);
        }
    }

    @Override
    public void setLineColor(ZLColor color) {
//        if (color != null) {
//            mLinePaint.setColor(ZLAndroidColorUtil.rgb(color));
//            mOutlinePaint.setColor(ZLAndroidColorUtil.rgb(color));
//        }
    }

    @Override
    public void setLineWidth(int width) {
//        mLinePaint.setStrokeWidth(width);
    }

    @Override
    public void setFillColor(ZLColor color, int alpha) {
        if (color != null) {
            mFillPaint.setColor(ZLAndroidColorUtil.rgba(color, alpha));
        }
    }

    public int getWidth() {
        return mGeometry.AreaSize.Width;
    }

    public int getHeight() {
        return mGeometry.AreaSize.Height;
    }

    @Override
    public int getStringWidth(char[] string, int offset, int length) {
        boolean containsSoftHyphen = false;
        for (int i = offset; i < offset + length; ++i) {
            if (string[i] == (char) 0xAD) {
                containsSoftHyphen = true;
                break;
            }
        }
        if (!containsSoftHyphen) {
            return (int) (mTextPaint.measureText(new String(string, offset, length)) + 0.5f);
        } else {
            final char[] corrected = new char[length];
            int len = 0;
            for (int o = offset; o < offset + length; ++o) {
                final char chr = string[o];
                if (chr != (char) 0xAD) {
                    corrected[len++] = chr;
                }
            }
            return (int) (mTextPaint.measureText(corrected, 0, len) + 0.5f);
        }
    }

    @Override
    protected int getSpaceWidthInternal() {
        return (int) (mTextPaint.measureText(" ", 0, 1) + 0.5f);
    }


    @Override
    protected int getStringHeightInternal() {
        return (int) (mTextPaint.getTextSize() + 0.5f);
    }

    @Override
    protected int getDescentInternal() {
        return (int) (mTextPaint.descent() + 0.5f);
    }


    @Override
    public void drawString(float x, float y, char[] string, int offset, int length) {
        boolean containsSoftHyphen = false;
        for (int i = offset; i < offset + length; ++i) {
            if (string[i] == (char) 0xAD) {//soft hyphen（&shy;）自动断行
                containsSoftHyphen = true;
                break;
            }
        }
        if (!containsSoftHyphen) {
            //string 字节数组; offset 表示第一个要绘制的文字索引; length 需要绘制的文字个数
            mCanvas.drawText(string, offset, length, x, y, mTextPaint);
        } else {
            final char[] corrected = new char[length];
            int len = 0;
            for (int o = offset; o < offset + length; ++o) {
                final char chr = string[o];
                if (chr != (char) 0xAD) {
                    corrected[len++] = chr;
                }
            }
            mCanvas.drawText(corrected, 0, len, x, y, mTextPaint);
        }
    }

    @Override
    public Size imageSize(ZLImageData imageData, Size maxSize, ScalingType scaling) {
        final Bitmap bitmap = ((ZLAndroidImageData) imageData).getBitmap(maxSize, scaling);
        return (bitmap != null && !bitmap.isRecycled())
                ? new Size(bitmap.getWidth(), bitmap.getHeight()) : null;
    }

    @Override
    public void drawImage(float x, float y, ZLImageData imageData, Size maxSize, ScalingType scaling) {
        final Bitmap bitmap = ((ZLAndroidImageData) imageData).getBitmap(maxSize, scaling);
        if (bitmap != null && !bitmap.isRecycled()) {
            mCanvas.drawBitmap(bitmap, x, y - bitmap.getHeight(), mFillPaint);
        }
    }

    @Override
    public void drawLine(float x0, float y0, float x1, float y1) {
//        final Canvas canvas = mCanvas;
//        final Paint paint = mLinePaint;
//        paint.setAntiAlias(false);
//        canvas.drawLine(x0, y0, x1, y1, paint);
//        canvas.drawPoint(x0, y0, paint);
//        canvas.drawPoint(x1, y1, paint);
//        paint.setAntiAlias(true);
    }

    //画标题、作者、当前目录名、进度
    @Override
    public void drawFooter(String time, String pagePre, float power, String tocTitle) {
        mFooterPaint.setColor(mTextColor);
        mTopPaint.setColor(mTextColor);
        mBatteryPaint.setColor(mTextColor);
        mPowerPaint.setColor(mTextColor);

        //设置电池矩形
        //矩形
        RectF mBatteryRect = new RectF(mCapWidth, 0, mBatteryWidth, mBatteryHeight);

        //设置电池盖矩形
        RectF mCapRect = new RectF(0, (mBatteryHeight - mCapHeight) / 2, mCapWidth,
                (mBatteryHeight - mCapHeight) / 2 + mCapHeight);

        //设置电量矩形
        RectF mPowerRect = new RectF(mCapWidth + mBatteryStroke / 2 + mPowerPadding + mPowerWidth * ((100f - power) / 100f), // 需要调整左边的位置
                mPowerPadding + mBatteryStroke / 2, // 需要考虑到 画笔的宽度
                mBatteryWidth - mPowerPadding * 2,
                mBatteryStroke / 2 + mPowerPadding + mPowerHeight);

        mCanvas.save();
        mCanvas.translate(10, getHeight() - 13 * dp_1); //y 平移
        mCanvas.drawRoundRect(mBatteryRect, 2f, 2f, mBatteryPaint); // 画电池轮廓需要考虑 画笔的宽度 画圆角矩形
        mCanvas.drawRoundRect(mCapRect, 2f, 2f, mBatteryPaint);// 画电池盖
        mCanvas.drawRect(mPowerRect, mPowerPaint);// 画电量
        mCanvas.restore();

        if (tocTitle.length() > 80) {
            tocTitle = tocTitle.substring(0, 79) + "...";
        }

        mCanvas.drawText(tocTitle, 10, 13 * dp_1, mTopPaint);
        // myCanvas.drawText(bookAuthor, getWidth() - myTopPaint.measureText(bookAuthor) - rightMargin, 13 * dp_1, myTopPaint);

        //myCanvas.drawText(bookTOC, leftMargin, getHeight() - 13 * dp_1, myTopPaint);
        mCanvas.drawText(time, mBatteryWidth + 10 * dp_1, getHeight() - 5 * dp_1, mFooterPaint);

        mCanvas.drawText(pagePre, getWidth() - mFooterPaint.measureText(pagePre) - 10, getHeight() - 5 * dp_1, mFooterPaint);
    }

    @Override
    public void fillRectangle(float x0, float y0, float x1, float y1) {
//        if (x1 < x0) {
//            float swap = x1;
//            x1 = x0;
//            x0 = swap;
//        }
//        if (y1 < y0) {
//            float swap = y1;
//            y1 = y0;
//            y0 = swap;
//        }
//        mCanvas.drawRect(x0, y0, x1 + 1, y1 + 1, mFillPaint);
    }

    @Override
    public void fillCircle(float x, float y, int radius) {
//        mCanvas.drawCircle(x, y, radius, mFillPaint);
    }
}
