package com.tzpt.cloudlibrary.zlibrary.text.view.sytle;

import com.tzpt.cloudlibrary.zlibrary.text.model.ZLTextMetrics;
import com.tzpt.cloudlibrary.zlibrary.text.view.ZLTextStyle;

/**
 * Created by Administrator on 2017/4/8.
 */

public abstract class ZLTextDecoratedStyle extends ZLTextStyle {
//    protected final ZLTextBaseStyle BaseStyle;

    private boolean myIsItalic;
    private boolean myIsBold;
    private boolean myIsUnderline;
    private boolean myIsStrikeThrough;
    private int myLineSpacePercent;

    private boolean myIsNotCached = true;

    private int myFontSize;
    private int mySpaceBefore;//段前距离
    private int mySpaceAfter;//段后距离
    private int myVerticalAlign;
    private Boolean myIsVerticallyAligned;
    private int myLeftMargin;
    private int myRightMargin;
    //    private int myLeftPadding;
//    private int myRightPadding;
    private int myFirstLineIndent;
    private ZLTextMetrics myMetrics;

    protected ZLTextDecoratedStyle(ZLTextStyle base) {
        super(base);
//        BaseStyle = base instanceof ZLTextBaseStyle
//                ? (ZLTextBaseStyle) base
//                : ((ZLTextDecoratedStyle) base).BaseStyle;
    }

    private void initCache() {
        myIsItalic = isItalicInternal();
        myIsBold = isBoldInternal();
        myIsUnderline = isUnderlineInternal();
        myIsStrikeThrough = isStrikeThroughInternal();
        myLineSpacePercent = getLineSpacePercentInternal();

        myIsNotCached = false;
    }

    private void initMetricsCache(ZLTextMetrics metrics) {
        myMetrics = metrics;
        myFontSize = getFontSizeInternal(metrics);
        mySpaceBefore = getSpaceBeforeInternal(metrics, myFontSize);
        mySpaceAfter = getSpaceAfterInternal(metrics, myFontSize);
        myVerticalAlign = getVerticalAlignInternal(metrics, myFontSize);
        myLeftMargin = getLeftMarginInternal(metrics, myFontSize);
        myRightMargin = getRightMarginInternal(metrics, myFontSize);
//        myLeftPadding = getLeftPaddingInternal(metrics, myFontSize);
//        myRightPadding = getRightPaddingInternal(metrics, myFontSize);
        myFirstLineIndent = getFirstLineIndentInternal(metrics, myFontSize);
    }

    @Override
    public final int getFontSize(ZLTextMetrics metrics) {
        if (!metrics.equals(myMetrics)) {
            initMetricsCache(metrics);
        }
        return myFontSize;
    }

    protected abstract int getFontSizeInternal(ZLTextMetrics metrics);

    @Override
    public final int getSpaceBefore(ZLTextMetrics metrics) {
        if (!metrics.equals(myMetrics)) {
            initMetricsCache(metrics);
        }
        return 80;
    }

    protected abstract int getSpaceBeforeInternal(ZLTextMetrics metrics, int fontSize);

    @Override
    public final int getSpaceAfter(ZLTextMetrics metrics) {
        if (!metrics.equals(myMetrics)) {
            initMetricsCache(metrics);
        }
        return 0;
    }

    protected abstract int getSpaceAfterInternal(ZLTextMetrics metrics, int fontSize);

    @Override
    public final boolean isItalic() {
        if (myIsNotCached) {
            initCache();
        }
        return myIsItalic;
    }

    protected abstract boolean isItalicInternal();

    @Override
    public final boolean isBold() {
        if (myIsNotCached) {
            initCache();
        }
        return myIsBold;
    }

    protected abstract boolean isBoldInternal();

    @Override
    public final boolean isUnderline() {
        if (myIsNotCached) {
            initCache();
        }
        return myIsUnderline;
    }

    protected abstract boolean isUnderlineInternal();

    @Override
    public final boolean isStrikeThrough() {
        if (myIsNotCached) {
            initCache();
        }
        return myIsStrikeThrough;
    }

    protected abstract boolean isStrikeThroughInternal();

    @Override
    public final int getVerticalAlign(ZLTextMetrics metrics) {
        if (!metrics.equals(myMetrics)) {
            initMetricsCache(metrics);
        }
        return 0;
    }

    protected abstract int getVerticalAlignInternal(ZLTextMetrics metrics, int fontSize);

    @Override
    public boolean isVerticallyAligned() {
        if (myIsVerticallyAligned == null) {
            myIsVerticallyAligned = Parent.isVerticallyAligned() || isVerticallyAlignedInternal();
        }
        return myIsVerticallyAligned;
    }

    protected abstract boolean isVerticallyAlignedInternal();

    @Override
    public final int getLeftMargin(ZLTextMetrics metrics) {
        if (!metrics.equals(myMetrics)) {
            initMetricsCache(metrics);
        }
        return myLeftMargin;
    }

    protected abstract int getLeftMarginInternal(ZLTextMetrics metrics, int fontSize);

    @Override
    public final int getRightMargin(ZLTextMetrics metrics) {
        if (!metrics.equals(myMetrics)) {
            initMetricsCache(metrics);
        }
        return myRightMargin;
    }

    protected abstract int getRightMarginInternal(ZLTextMetrics metrics, int fontSize);

//    @Override
//    public final int getLeftPadding(ZLTextMetrics metrics) {
//        if (!metrics.equals(myMetrics)) {
//            initMetricsCache(metrics);
//        }
//        return myLeftPadding;
//    }

//    protected abstract int getLeftPaddingInternal(ZLTextMetrics metrics, int fontSize);

//    @Override
//    public final int getRightPadding(ZLTextMetrics metrics) {
//        if (!metrics.equals(myMetrics)) {
//            initMetricsCache(metrics);
//        }
//        return myRightPadding;
//    }

//    protected abstract int getRightPaddingInternal(ZLTextMetrics metrics, int fontSize);

    @Override
    public final int getFirstLineIndent(ZLTextMetrics metrics) {
        if (!metrics.equals(myMetrics)) {
            initMetricsCache(metrics);
        }
        return myFirstLineIndent;
    }

    protected abstract int getFirstLineIndentInternal(ZLTextMetrics metrics, int fontSize);

    @Override
    public final int getLineSpacePercent() {
        if (myIsNotCached) {
            initCache();
        }
        return 20 * 10;
    }

    protected abstract int getLineSpacePercentInternal();
}
