package com.tzpt.cloudlibrary.zlibrary.text.view.sytle;

import com.tzpt.cloudlibrary.zlibrary.core.library.ZLibrary;
import com.tzpt.cloudlibrary.zlibrary.core.opstions.ZLBooleanOption;
import com.tzpt.cloudlibrary.zlibrary.core.opstions.ZLIntegerRangeOption;
import com.tzpt.cloudlibrary.zlibrary.core.opstions.ZLStringOption;
import com.tzpt.cloudlibrary.zlibrary.text.model.ZLTextAlignmentType;
import com.tzpt.cloudlibrary.zlibrary.text.model.ZLTextMetrics;
import com.tzpt.cloudlibrary.zlibrary.text.view.ZLTextStyle;

/**
 * 文本排版风格类型
 * Created by Administrator on 2017/4/8.
 */

public class ZLTextBaseStyle extends ZLTextStyle {
    private static final String GROUP = "Style";
    private static final String OPTIONS = "Options";

    public final ZLBooleanOption AutoHyphenationOption =
            new ZLBooleanOption(OPTIONS, "AutoHyphenation", true); // 自动断字,对英文的展示更好

    private final ZLBooleanOption BoldOption; //粗体
    private final ZLBooleanOption ItalicOption; //斜体
    private final ZLBooleanOption UnderlineOption; //下划线
    private final ZLBooleanOption StrikeThroughOption;//
    private final ZLIntegerRangeOption AlignmentOption;//
    private final ZLIntegerRangeOption LineSpaceOption;//

    private final ZLStringOption FontFamilyOption; //字体
    public final ZLIntegerRangeOption FontSizeOption;//字体大小

    public ZLTextBaseStyle(String prefix, String fontFamily, int fontSize) {
        super(null);
        FontFamilyOption = new ZLStringOption(GROUP, prefix + ":fontFamily", fontFamily);
        fontSize = fontSize * ZLibrary.Instance().getDisplayDPI() / 160;
        FontSizeOption = new ZLIntegerRangeOption(GROUP, prefix + ":fontSize", 5, Math.max(144, fontSize * 2), fontSize);
        BoldOption = new ZLBooleanOption(GROUP, prefix + ":bold", false);
        ItalicOption = new ZLBooleanOption(GROUP, prefix + ":italic", false);
        UnderlineOption = new ZLBooleanOption(GROUP, prefix + ":underline", false);
        StrikeThroughOption = new ZLBooleanOption(GROUP, prefix + ":strikeThrough", false);
        AlignmentOption = new ZLIntegerRangeOption(GROUP, prefix + ":alignment", 1, 4, ZLTextAlignmentType.ALIGN_JUSTIFY);
        LineSpaceOption = new ZLIntegerRangeOption(GROUP, prefix + ":lineSpacing", 5, 25, 20);//行间距
    }

    public int getFontSize() {
        return FontSizeOption.getValue();
    }

    @Override
    public int getFontSize(ZLTextMetrics metrics) {
        return getFontSize();
    }

    @Override
    public boolean isBold() {
        return BoldOption.getValue();
    }

    @Override
    public boolean isItalic() {
        return ItalicOption.getValue();
    }

    @Override
    public boolean isUnderline() {
        return UnderlineOption.getValue();
    }

    @Override
    public boolean isStrikeThrough() {
        return StrikeThroughOption.getValue();
    }

    @Override
    public int getLeftMargin(ZLTextMetrics metrics) {
        return 10;
    }

    @Override
    public int getRightMargin(ZLTextMetrics metrics) {
        return 10;
    }

//    @Override
//    public int getLeftPadding(ZLTextMetrics metrics) {
//        return 0;
//    }
//
//    @Override
//    public int getRightPadding(ZLTextMetrics metrics) {
//        return 0;
//    }

    @Override
    public int getFirstLineIndent(ZLTextMetrics metrics) {
        return 0;
    }

    @Override
    public int getLineSpacePercent() {
        return 20 * 10;
    }

    @Override
    public int getVerticalAlign(ZLTextMetrics metrics) {
        return 0;
    }

    @Override
    public boolean isVerticallyAligned() {
        return false;
    }

    @Override
    public int getSpaceBefore(ZLTextMetrics metrics) {
        return 0;
    }

    @Override
    public int getSpaceAfter(ZLTextMetrics metrics) {
        return 0;
    }

    @Override
    public byte getAlignment() {
        return (byte) AlignmentOption.getValue();
    }

    @Override
    public boolean allowHyphenations() {
        return true;
    }
}
