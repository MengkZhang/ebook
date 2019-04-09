package com.tzpt.cloudlibrary.zlibrary.text.view.sytle;

import com.tzpt.cloudlibrary.zlibrary.text.model.ZLTextAlignmentType;
import com.tzpt.cloudlibrary.zlibrary.text.model.ZLTextMetrics;
import com.tzpt.cloudlibrary.zlibrary.text.view.ZLTextStyle;

/**
 * Created by Administrator on 2017/4/8.
 */

public class ZLTextNGStyle extends ZLTextDecoratedStyle {
    private final ZLTextNGStyleDescription mDescription;

    public ZLTextNGStyle(ZLTextStyle parent, ZLTextNGStyleDescription description) {
        super(parent);
        mDescription = description;
    }


    @Override
    protected int getFontSizeInternal(ZLTextMetrics metrics) {
        return mDescription.getFontSize(metrics, Parent.getFontSize(metrics));
    }

    @Override
    protected boolean isBoldInternal() {
        switch (mDescription.isBold()) {
            case TRUE:
                return true;
            case FALSE:
                return false;
            default:
                return Parent.isBold();
        }
    }

    @Override
    protected boolean isItalicInternal() {
        switch (mDescription.isItalic()) {
            case TRUE:
                return true;
            case FALSE:
                return false;
            default:
                return Parent.isItalic();
        }
    }

    @Override
    protected boolean isUnderlineInternal() {
        switch (mDescription.isUnderlined()) {
            case TRUE:
                return true;
            case FALSE:
                return false;
            default:
                return Parent.isUnderline();
        }
    }

    @Override
    protected boolean isStrikeThroughInternal() {
        switch (mDescription.isStrikedThrough()) {
            case TRUE:
                return true;
            case FALSE:
                return false;
            default:
                return Parent.isStrikeThrough();
        }
    }

    @Override
    protected int getLeftMarginInternal(ZLTextMetrics metrics, int fontSize) {
        return mDescription.getLeftMargin(metrics, Parent.getLeftMargin(metrics), fontSize);
    }

    @Override
    protected int getRightMarginInternal(ZLTextMetrics metrics, int fontSize) {
        return mDescription.getRightMargin(metrics, Parent.getRightMargin(metrics), fontSize);
    }

//    @Override
//    protected int getLeftPaddingInternal(ZLTextMetrics metrics, int fontSize) {
//        return mDescription.getLeftPadding(metrics, Parent.getLeftPadding(metrics), fontSize);
//    }
//
//    @Override
//    protected int getRightPaddingInternal(ZLTextMetrics metrics, int fontSize) {
//        return mDescription.getRightPadding(metrics, Parent.getRightPadding(metrics), fontSize);
//    }

    @Override
    protected int getFirstLineIndentInternal(ZLTextMetrics metrics, int fontSize) {
        return mDescription.getFirstLineIndent(metrics, Parent.getFirstLineIndent(metrics), fontSize);
    }

    @Override
    protected int getLineSpacePercentInternal() {
        return mDescription.getLineHeightOption(Parent.getLineSpacePercent());
    }

    @Override
    protected int getVerticalAlignInternal(ZLTextMetrics metrics, int fontSize) {
        return mDescription.getVerticalAlign(metrics, Parent.getVerticalAlign(metrics), fontSize);
    }

    @Override
    protected boolean isVerticallyAlignedInternal() {
        return mDescription.hasNonZeroVerticalAlign();
    }

    @Override
    protected int getSpaceBeforeInternal(ZLTextMetrics metrics, int fontSize) {
        return mDescription.getSpaceBefore(metrics, Parent.getSpaceBefore(metrics), fontSize);
    }

    @Override
    protected int getSpaceAfterInternal(ZLTextMetrics metrics, int fontSize) {
        return mDescription.getSpaceAfter(metrics, Parent.getSpaceAfter(metrics), fontSize);
    }

    @Override
    public byte getAlignment() {
        final byte defined = mDescription.getAlignment();
        if (defined != ZLTextAlignmentType.ALIGN_UNDEFINED) {
            return defined;
        }
        return Parent.getAlignment();
    }

    @Override
    public boolean allowHyphenations() {
        switch (mDescription.allowHyphenations()) {
            case TRUE:
                return true;
            case FALSE:
                return false;
            default:
                return Parent.allowHyphenations();
        }
    }

    @Override
    public String toString() {
        return "ZLTextNGStyle[" + mDescription.Name + "]";
    }
}
