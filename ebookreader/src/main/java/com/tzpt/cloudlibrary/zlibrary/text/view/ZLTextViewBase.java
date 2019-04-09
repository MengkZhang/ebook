package com.tzpt.cloudlibrary.zlibrary.text.view;

import android.graphics.Typeface;

import com.tzpt.cloudlibrary.zlibrary.core.filesystem.ZLFile;
import com.tzpt.cloudlibrary.zlibrary.core.library.ZLibrary;
import com.tzpt.cloudlibrary.zlibrary.core.util.ZLColor;
import com.tzpt.cloudlibrary.zlibrary.core.view.ZLPaintContext;
import com.tzpt.cloudlibrary.zlibrary.core.view.ZLView;
import com.tzpt.cloudlibrary.zlibrary.text.model.ZLTextMetrics;
import com.tzpt.cloudlibrary.zlibrary.text.view.sytle.ZLTextNGStyle;
import com.tzpt.cloudlibrary.zlibrary.text.view.sytle.ZLTextNGStyleDescription;
import com.tzpt.cloudlibrary.zlibrary.text.view.sytle.ZLTextStyleCollection;

/**
 * 提供展示样式参数
 * Created by Administrator on 2017/4/8.
 */

public abstract class ZLTextViewBase extends ZLView {
    public enum ImageFitting {
        none, covers, all
    }

    private ZLTextStyle mTextStyle;
    private int mWordHeight = -1;
    private ZLTextMetrics mMetrics; // 度量

    private Typeface mTypeface;

    void resetMetrics() {
        mMetrics = null;
    }

    protected ZLTextMetrics metrics() {
        // this local variable is used to guarantee null will not
        // be returned from this method enen in multi-thread environment
        ZLTextMetrics m = mMetrics;
        if (m == null) {
            m = new ZLTextMetrics(
                    ZLibrary.Instance().getDisplayDPI(),
                    //screen area width
                    ZLibrary.Instance().getScreenWidth(),
                    //screen area height
                    ZLibrary.Instance().getScreenHeight(),
                    getTextStyleCollection().getBaseStyle().getFontSize()
            );
            mMetrics = m;
        }
        return m;
    }

    /**
     * 获取字的高度，文字高度+行间距
     *
     * @return 字的高度
     */
    final int getWordHeight() {
        if (mWordHeight == -1) {
            final ZLTextStyle textStyle = mTextStyle;
            mWordHeight = getContext().getStringHeight() * textStyle.getLineSpacePercent() / 100 + textStyle.getVerticalAlign(metrics());
        }
        return mWordHeight;
    }

    public abstract ZLTextStyleCollection getTextStyleCollection();

    public abstract ImageFitting getImageFitting();

    public abstract int getLeftMargin();

    public abstract int getRightMargin();

    public abstract int getTopMargin();

    public abstract int getBottomMargin();

    public abstract int getSpaceBetweenColumns();

    public abstract boolean twoColumnView();

    public abstract ZLFile getWallpaperFile();

    public abstract ZLColor getBackgroundColor();

    public abstract ZLColor getTextColor();

    /**
     * 图片的绘制区域
     *
     * @return 图片的绘制区域
     */
    ZLPaintContext.Size getTextAreaSize() {
        // 绘制区域的上下左右边距
        return new ZLPaintContext.Size(getTextColumnWidth(), getTextAreaHeight());
    }

    int getTextAreaHeight() { // 文字显示区域
        return getContextHeight() - getTopMargin() - getBottomMargin();
    }

    protected int getColumnIndex(int x) { // 文字选择相关
//        if (!twoColumnView()) {
//            return -1;
//        }
        return 2 * x <= getContextWidth() + getLeftMargin() - getRightMargin() ? 0 : 1;
    }

    int getTextColumnWidth() {
//        return twoColumnView()
//                ? (getContextWidth() - getLeftMargin() - getSpaceBetweenColumns() - getRightMargin()) / 2
//                : getContextWidth() - getLeftMargin() - getRightMargin();
        return getContextWidth() - getLeftMargin() - getRightMargin();
    }

    /**
     * 获取绘制样式
     *
     * @return 绘制样式
     */
    final ZLTextStyle getTextStyle() {
        if (mTextStyle == null) {
            resetTextStyle();
        }
        return mTextStyle;
    }

    public void setTypeface(Typeface typeface) {
        mTypeface = typeface;
    }

    /**
     * 设置展示样式
     * 遇到ZLTextControlElement会刷新TextStyle
     *
     * @param style 展示样式
     */
    final void setTextStyle(ZLTextStyle style) {
        if (mTextStyle != style) {
            mTextStyle = style;
            mWordHeight = -1;
        }
        getContext().setFont(mTypeface, style.getFontSize(metrics()), style.isBold(), style.isItalic(), style.isUnderline(), style.isStrikeThrough());
    }

    final void resetTextStyle() {
        setTextStyle(getTextStyleCollection().getBaseStyle());
    }

    /**
     * 是否改变绘制样式
     *
     * @param element 元素
     * @return true 表示需要改变绘制样式 false 反之
     */
    boolean isStyleChangeElement(ZLTextElement element) {
        return element == ZLTextElement.StyleClose ||
                element instanceof ZLTextControlElement;
    }

    /**
     * 申请改变绘制样式
     *
     * @param element 元素
     */
    void applyStyleChangeElement(ZLTextElement element) {
        if (element == ZLTextElement.StyleClose) {
            applyStyleClose();
        } else if (element instanceof ZLTextControlElement) {
            applyControl((ZLTextControlElement) element);
        }
    }

    void applyStyleChanges(ZLTextParagraphCursor cursor, int index, int end) {
        for (; index != end; ++index) {
            applyStyleChangeElement(cursor.getElement(index));
        }
    }

    private void applyControl(ZLTextControlElement control) {
        if (control.IsStart) {
            final ZLTextNGStyleDescription description =
                    getTextStyleCollection().getDescription(control.Kind);
            if (description != null) {
                setTextStyle(new ZLTextNGStyle(mTextStyle, description));
            }
        } else {
            setTextStyle(mTextStyle.Parent);
        }
    }

    private void applyStyleClose() {
        setTextStyle(mTextStyle.Parent);
    }

    final ZLPaintContext.ScalingType getScalingType(ZLTextImageElement imageElement) {
        switch (getImageFitting()) {
            default:
            case none:
                return ZLPaintContext.ScalingType.FitMaximum;
            case covers:
                return imageElement.IsCover // 如果是
                        ? ZLPaintContext.ScalingType.FitMaximum // 图片最大化显示
                        : ZLPaintContext.ScalingType.IntegerCoefficient;
            case all:
                return ZLPaintContext.ScalingType.FitMaximum;
        }
    }

    /**
     * 获取元素宽度
     *
     * @param element ZLTextElement
     * @param charIndex 字符的索引值
     * @return 宽度
     */
    final int getElementWidth(ZLTextElement element, int charIndex) {
        if (element instanceof ZLTextWord) {
            return getWordWidth((ZLTextWord) element, charIndex);
        } else if (element instanceof ZLTextImageElement) {
            final ZLTextImageElement imageElement = (ZLTextImageElement) element;
            final ZLPaintContext.Size size = getContext().imageSize(
                    imageElement.ImageData,
                    getTextAreaSize(),
                    getScalingType(imageElement)
            );
            return size != null ? size.Width : 0;
        } else if (element instanceof ZLTextVideoElement) {
            return 0;
        } else if (element == ZLTextElement.NBSpace) {
            return getContext().getSpaceWidth();
        }
        return 0;
    }

    /**
     * 获取元素高度
     *
     * @param element ZLTextElement
     * @return 高度
     */
    final int getElementHeight(ZLTextElement element) {
        if (element == ZLTextElement.NBSpace) {
            return getWordHeight();
        } else if (element instanceof ZLTextWord) {
            return getWordHeight();
        } else if (element instanceof ZLTextImageElement) {
            final ZLTextImageElement imageElement = (ZLTextImageElement) element;
            final ZLPaintContext.Size size = getContext().imageSize(
                    imageElement.ImageData,
                    getTextAreaSize(),
                    getScalingType(imageElement)
            );
            return (size != null ? size.Height : 0) +
                    Math.max(getContext().getStringHeight() * (mTextStyle.getLineSpacePercent() - 100) / 100, 3);
        } else if (element instanceof ZLTextVideoElement) {
            return Math.min(Math.min(200, getTextAreaHeight()), getTextColumnWidth() * 2 / 3);
        }
        return 0;
    }

    final int getElementDescent(ZLTextElement element) {
        return element instanceof ZLTextWord ? getContext().getDescent() : 0;
    }

    /**
     * 获取文字宽度
     *
     * @param word  文字
     * @param start 索引值
     * @return 文字宽度
     */
    final int getWordWidth(ZLTextWord word, int start) {
        return
                start == 0 ?
                        word.getWidth(getContext()) :
                        getContext().getStringWidth(word.Data, word.Offset + start, word.Length - start);
    }

    final int getWordWidth(ZLTextWord word, int start, int length) {
        return getContext().getStringWidth(word.Data, word.Offset + start, length);
    }

    private char[] myWordPartArray = new char[20];

    final int getWordWidth(ZLTextWord word, int start, int length, boolean addHyphenationSign) {
        if (length == -1) {
            if (start == 0) {
                return word.getWidth(getContext());
            }
            length = word.Length - start;
        }
        if (!addHyphenationSign) {
            return getContext().getStringWidth(word.Data, word.Offset + start, length);
        }
        char[] part = myWordPartArray;
        if (length + 1 > part.length) {
            part = new char[length + 1];
            myWordPartArray = part;
        }
        System.arraycopy(word.Data, word.Offset + start, part, 0, length);
        part[length] = '-';
        return getContext().getStringWidth(part, 0, length + 1);
    }

    int getAreaLength(ZLTextParagraphCursor paragraph, ZLTextElementArea area, int toCharIndex) {
        setTextStyle(area.Style);
        final ZLTextWord word = (ZLTextWord) paragraph.getElement(area.getElementIndex());
        int length = toCharIndex - area.getCharIndex();
        boolean selectHyphenationSign = false;
        if (length >= area.Length) {
            selectHyphenationSign = area.AddHyphenationSign;
            length = area.Length;
        }
        if (length > 0) {
            return getWordWidth(word, area.getCharIndex(), length, selectHyphenationSign);
        }
        return 0;
    }

    /**
     * 绘制文本
     *
     * @param x                  Canvas setText的x参数
     * @param y                  Canvas setText的y参数
     * @param word               绘制内容
     * @param start
     * @param length
     * @param addHyphenationSign 断字标识
     * @param color              文字颜色
     */
    final void drawWord(float x, float y, ZLTextWord word, int start, int length, boolean addHyphenationSign, ZLColor color) {
        final ZLPaintContext context = getContext();
        if (start == 0 && length == -1) {
            drawString(context, x, y, word.Data, word.Offset, word.Length, color);
        } else {
            if (length == -1) {
                length = word.Length - start;
            }
            if (!addHyphenationSign) {//断字标识
                drawString(context, x, y, word.Data, word.Offset + start, length, color);
            } else {
                char[] part = myWordPartArray;
                if (length + 1 > part.length) {
                    part = new char[length + 1];
                    myWordPartArray = part;
                }
                System.arraycopy(word.Data, word.Offset + start, part, 0, length);
                part[length] = '-';
                drawString(context, x, y, part, 0, length + 1, color);
            }
        }
    }

    // 绘制文字
    private void drawString(ZLPaintContext context, float x, float y, char[] str, int offset, int length, ZLColor color) {
        context.setTextColor(color);
        context.drawString(x, y, str, offset, length);
    }
}
