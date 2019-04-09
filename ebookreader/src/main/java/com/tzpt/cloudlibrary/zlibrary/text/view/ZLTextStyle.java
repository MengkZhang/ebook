package com.tzpt.cloudlibrary.zlibrary.text.view;

import com.tzpt.cloudlibrary.zlibrary.text.model.ZLTextMetrics;

/**
 * Created by Administrator on 2017/4/8.
 */

public abstract class ZLTextStyle {
    public final ZLTextStyle Parent;

    protected ZLTextStyle(ZLTextStyle parent) {
        Parent = parent != null ? parent : this;
    }

    public abstract int getFontSize(ZLTextMetrics metrics);

    public abstract boolean isBold();

    public abstract boolean isItalic();

    public abstract boolean isUnderline();

    public abstract boolean isStrikeThrough();

//    public final int getLeftIndent(ZLTextMetrics metrics) {
//        return 0;
//    }
//
//    public final int getRightIndent(ZLTextMetrics metrics) {
//        return 0;
//    }

    public abstract int getLeftMargin(ZLTextMetrics metrics);

    public abstract int getRightMargin(ZLTextMetrics metrics);

//    public abstract int getLeftPadding(ZLTextMetrics metrics);
//
//    public abstract int getRightPadding(ZLTextMetrics metrics);

    public abstract int getFirstLineIndent(ZLTextMetrics metrics);

    public abstract int getLineSpacePercent();

    public abstract int getVerticalAlign(ZLTextMetrics metrics);

    public abstract boolean isVerticallyAligned();

    /**
     * 获取段前距离
     *
     * @param metrics ZLTextMetrics
     * @return 段前距离
     */
    public abstract int getSpaceBefore(ZLTextMetrics metrics);

    /**
     * 获取段后距离
     *
     * @param metrics ZLTextMetrics
     * @return 段前距离
     */
    public abstract int getSpaceAfter(ZLTextMetrics metrics);

    public abstract byte getAlignment();

    public abstract boolean allowHyphenations();
}
