package com.tzpt.cloudlibrary.zlibrary.core.view;

import android.graphics.Typeface;

import com.tzpt.cloudlibrary.zlibrary.core.filesystem.ZLFile;
import com.tzpt.cloudlibrary.zlibrary.core.image.ZLImageData;
import com.tzpt.cloudlibrary.zlibrary.core.util.ZLColor;

/**
 * 内容绘制的基类
 * Created by Administrator on 2017/4/7.
 */

public abstract class ZLPaintContext {
    protected ZLPaintContext() {
    }

    abstract public void setBackground(ZLFile wallpaperFile);

    abstract public void setBackground(ZLColor color);

    abstract public ZLColor getBackgroundColor();

    private boolean mResetFont = true;
    private Typeface mTypeface;
    private int mFontSize;
    private boolean mFontIsBold;
    private boolean mFontIsItalic;
    private boolean mFontIsUnderlined;
    private boolean mFontIsStrikedThrough;

    public final void setFont(Typeface typeface, int size, boolean bold, boolean italic, boolean underline, boolean strikeThrough) {
        if (typeface != null) {
            mTypeface = typeface;
            mResetFont = true;
        }
        if (mFontSize != size) {
            mFontSize = size;
            mResetFont = true;
        }
        if (mFontIsBold != bold) {
            mFontIsBold = bold;
            mResetFont = true;
        }
        if (mFontIsItalic != italic) {
            mFontIsItalic = italic;
            mResetFont = true;
        }
        if (mFontIsUnderlined != underline) {
            mFontIsUnderlined = underline;
            mResetFont = true;
        }
        if (mFontIsStrikedThrough != strikeThrough) {
            mFontIsStrikedThrough = strikeThrough;
            mResetFont = true;
        }
        if (mResetFont) {
            mResetFont = false;
            setFontInternal(mTypeface, size, bold, italic, underline, strikeThrough);
            mSpaceWidth = -1;
            myStringHeight = -1;
            myDescent = -1;
//            myCharHeights.clear();
        }
    }

    abstract protected void setFontInternal(Typeface typeface, int size, boolean bold, boolean italic, boolean underline, boolean strikeThrough);

    abstract public void setTextColor(ZLColor color);

    abstract public void setLineColor(ZLColor color);

    abstract public void setLineWidth(int width);

    final public void setFillColor(ZLColor color) {
        setFillColor(color, 0xFF);
    }

    abstract public void setFillColor(ZLColor color, int alpha);

    abstract public int getWidth();

    abstract public int getHeight();

    abstract public int getStringWidth(char[] string, int offset, int length);

    private int mSpaceWidth = -1;

    public final int getSpaceWidth() {
        int spaceWidth = mSpaceWidth;
        if (spaceWidth == -1) {
            spaceWidth = getSpaceWidthInternal();
            mSpaceWidth = spaceWidth;
        }
        return spaceWidth;
    }

    abstract protected int getSpaceWidthInternal();

    private int myStringHeight = -1;

    public final int getStringHeight() {
        int stringHeight = myStringHeight;
        if (stringHeight == -1) {
            stringHeight = getStringHeightInternal();
            myStringHeight = stringHeight;
        }
        return stringHeight;
    }

    abstract protected int getStringHeightInternal();
//
//    private Map<Character, Integer> myCharHeights = new TreeMap<Character, Integer>();
//
//    public final int getCharHeight(char chr) {
//        final Integer h = myCharHeights.get(chr);
//        if (h != null) {
//            return h;
//        }
//        final int he = getCharHeightInternal(chr);
//        myCharHeights.put(chr, he);
//        return he;
//    }

//    protected abstract int getCharHeightInternal(char chr);

    private int myDescent = -1;

    public final int getDescent() {
        int descent = myDescent;
        if (descent == -1) {
            descent = getDescentInternal();
            myDescent = descent;
        }
        return descent;
    }

    abstract protected int getDescentInternal();

//    public final void drawString(int x, int y, String string) {
//        drawString(x, y, string.toCharArray(), 0, string.length());
//    }

    abstract public void drawString(float x, float y, char[] string, int offset, int length);

    /**
     * 绘制区域
     */
    public static final class Size {
        public final int Width;
        public final int Height;

        public Size(int w, int h) {
            Width = w;
            Height = h;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if (!(other instanceof Size)) {
                return false;
            }
            final Size s = (Size) other;
            return Width == s.Width && Height == s.Height;
        }

        @Override
        public String toString() {
            return "ZLPaintContext.Size[" + Width + "x" + Height + "]";
        }
    }

    public enum ScalingType {
        OriginalSize,
        IntegerCoefficient,
        FitMaximum
    }

    public enum ColorAdjustingMode {
        NONE,
        DARKEN_TO_BACKGROUND,
        LIGHTEN_TO_BACKGROUND
    }

    abstract public Size imageSize(ZLImageData image, Size maxSize, ScalingType scaling);

    abstract public void drawImage(float x, float y, ZLImageData image, Size maxSize, ScalingType scaling);

    abstract public void drawLine(float x0, float y0, float x1, float y1);

    abstract public void drawFooter(String time, String pagePre, float power, String tocTitle);

    abstract public void fillRectangle(float x0, float y0, float x1, float y1);

    abstract public void drawPolygonalLine(int[] xs, int[] ys);

    abstract public void fillPolygon(float[] xs, float[] ys);

    abstract public void drawOutline(float[] xs, float[] ys);

    abstract public void fillCircle(float x, float y, int radius);
}
