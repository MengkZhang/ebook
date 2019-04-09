package com.tzpt.cloudlibrary.zlibrary.text.view.sytle;

import com.tzpt.cloudlibrary.reader.util.Boolean3;
import com.tzpt.cloudlibrary.zlibrary.core.opstions.ZLStringOption;
import com.tzpt.cloudlibrary.zlibrary.text.model.ZLTextAlignmentType;
import com.tzpt.cloudlibrary.zlibrary.text.model.ZLTextMetrics;
import com.tzpt.cloudlibrary.zlibrary.text.model.ZLTextStyleEntry;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/8.
 */

public class ZLTextNGStyleDescription {
    public final String Name;

    public final ZLStringOption FontFamilyOption = null;//规定元素的字体系列
    private final ZLStringOption FontSizeOption;//设置字体的尺寸
    private final ZLStringOption FontWeightOption;
    private final ZLStringOption FontStyleOption;
    private final ZLStringOption TextDecorationOption;
    private final ZLStringOption HyphenationOption;
    private final ZLStringOption MarginTopOption;
    private final ZLStringOption MarginBottomOption;
    private final ZLStringOption MarginLeftOption;
    private final ZLStringOption MarginRightOption;
    private final ZLStringOption TextIndentOption;
    private final ZLStringOption AlignmentOption;
    private final ZLStringOption VerticalAlignOption;
    private final ZLStringOption LineHeightOption;

    private static ZLStringOption createOption(String selector, String name, Map<String, String> valueMap) {
        return new ZLStringOption("Style", selector + "::" + name, valueMap.get(name));
    }

    ZLTextNGStyleDescription(String selector, Map<String, String> valueMap) {
        Name = valueMap.get("kooreader-name");

        FontSizeOption = createOption(selector, "font-size", valueMap);
        FontWeightOption = createOption(selector, "font-weight", valueMap);
        FontStyleOption = createOption(selector, "font-style", valueMap);
        TextDecorationOption = createOption(selector, "text-decoration", valueMap);
        HyphenationOption = createOption(selector, "hyphens", valueMap);
        MarginTopOption = createOption(selector, "margin-top", valueMap);
        MarginBottomOption = createOption(selector, "margin-bottom", valueMap);
        MarginLeftOption = createOption(selector, "margin-left", valueMap);
        MarginRightOption = createOption(selector, "margin-right", valueMap);
        TextIndentOption = createOption(selector, "text-indent", valueMap);
        AlignmentOption = createOption(selector, "text-align", valueMap);
        VerticalAlignOption = createOption(selector, "vertical-align", valueMap);
        LineHeightOption = createOption(selector, "line-height", valueMap);
    }

    int getFontSize(ZLTextMetrics metrics, int parentFontSize) {
        final ZLTextStyleEntry.Length length = parseLength(FontSizeOption.getValue());
        if (length == null) {
            return parentFontSize;
        }
        return ZLTextStyleEntry.compute(
                length, metrics, parentFontSize, ZLTextStyleEntry.Feature.LENGTH_FONT_SIZE
        );
    }

    int getLineHeightOption(int base) {
        final String lineHeight = LineHeightOption.getValue();
        if (!lineHeight.matches("[1-9][0-9]*%")) {
            return base;
        }
        return Integer.valueOf(lineHeight.substring(0, lineHeight.length() - 1));
    }

    int getVerticalAlign(ZLTextMetrics metrics, int base, int fontSize) {
        final ZLTextStyleEntry.Length length = parseLength(VerticalAlignOption.getValue());
        if (length == null) {
            return base;
        }
        return ZLTextStyleEntry.compute(
                // TODO: add new length for vertical alignment
                length, metrics, fontSize, ZLTextStyleEntry.Feature.LENGTH_FONT_SIZE
        );
    }

    boolean hasNonZeroVerticalAlign() {
        final ZLTextStyleEntry.Length length = parseLength(VerticalAlignOption.getValue());
        return length != null && length.Size != 0;
    }

    int getLeftMargin(ZLTextMetrics metrics, int base, int fontSize) {
        final ZLTextStyleEntry.Length length = parseLength(MarginLeftOption.getValue());
        if (length == null) {
            return base;
        }
        return base + ZLTextStyleEntry.compute(
                length, metrics, fontSize, ZLTextStyleEntry.Feature.LENGTH_MARGIN_LEFT
        );
    }

    int getRightMargin(ZLTextMetrics metrics, int base, int fontSize) {
        final ZLTextStyleEntry.Length length = parseLength(MarginRightOption.getValue());
        if (length == null) {
            return base;
        }
        return base + ZLTextStyleEntry.compute(
                length, metrics, fontSize, ZLTextStyleEntry.Feature.LENGTH_MARGIN_RIGHT
        );
    }

//    int getLeftPadding(ZLTextMetrics metrics, int base, int fontSize) {
//        return base;
//    }
//
//    int getRightPadding(ZLTextMetrics metrics, int base, int fontSize) {
//        return base;
//    }

    int getFirstLineIndent(ZLTextMetrics metrics, int base, int fontSize) {
        final ZLTextStyleEntry.Length length = parseLength(TextIndentOption.getValue());
        if (length == null) {
            return base;
        }
        return ZLTextStyleEntry.compute(
                length, metrics, fontSize, ZLTextStyleEntry.Feature.LENGTH_FIRST_LINE_INDENT
        );
    }

    int getSpaceBefore(ZLTextMetrics metrics, int base, int fontSize) {
        final ZLTextStyleEntry.Length length = parseLength(MarginTopOption.getValue());
        if (length == null) {
            return base;
        }
        return ZLTextStyleEntry.compute(
                length, metrics, fontSize, ZLTextStyleEntry.Feature.LENGTH_SPACE_BEFORE
        );
    }

    int getSpaceAfter(ZLTextMetrics metrics, int base, int fontSize) {
        final ZLTextStyleEntry.Length length = parseLength(MarginBottomOption.getValue());
        if (length == null) {
            return base;
        }
        return ZLTextStyleEntry.compute(
                length, metrics, fontSize, ZLTextStyleEntry.Feature.LENGTH_SPACE_AFTER
        );
    }

    Boolean3 isBold() {
        final String fontWeight = FontWeightOption.getValue();
        if ("bold".equals(fontWeight)) {
            return Boolean3.TRUE;
        } else if ("normal".equals(fontWeight)) {
            return Boolean3.FALSE;
        } else {
            return Boolean3.UNDEFINED;
        }
    }

    Boolean3 isItalic() {
        final String fontStyle = FontStyleOption.getValue();
        if ("italic".equals(fontStyle) || "oblique".equals(fontStyle)) {
            return Boolean3.TRUE;
        } else if ("normal".equals(fontStyle)) {
            return Boolean3.FALSE;
        } else {
            return Boolean3.UNDEFINED;
        }
    }

    Boolean3 isUnderlined() {
        final String textDecoration = TextDecorationOption.getValue();
        if ("underline".equals(textDecoration)) {
            return Boolean3.TRUE;
        } else if ("".equals(textDecoration) || "inherit".equals(textDecoration)) {
            return Boolean3.UNDEFINED;
        } else {
            return Boolean3.FALSE;
        }
    }

    Boolean3 isStrikedThrough() {
        final String textDecoration = TextDecorationOption.getValue();
        if ("line-through".equals(textDecoration)) {
            return Boolean3.TRUE;
        } else if ("".equals(textDecoration) || "inherit".equals(textDecoration)) {
            return Boolean3.UNDEFINED;
        } else {
            return Boolean3.FALSE;
        }
    }

    byte getAlignment() {
        final String alignment = AlignmentOption.getValue();
        if (alignment.length() == 0) {
            return ZLTextAlignmentType.ALIGN_UNDEFINED;
        } else if ("center".equals(alignment)) {
            return ZLTextAlignmentType.ALIGN_CENTER;
        } else if ("left".equals(alignment)) {
            return ZLTextAlignmentType.ALIGN_LEFT;
        } else if ("right".equals(alignment)) {
            return ZLTextAlignmentType.ALIGN_RIGHT;
        } else if ("justify".equals(alignment)) {
            return ZLTextAlignmentType.ALIGN_JUSTIFY;
        } else {
            return ZLTextAlignmentType.ALIGN_UNDEFINED;
        }
    }

    Boolean3 allowHyphenations() {
        final String hyphen = HyphenationOption.getValue();
        if ("auto".equals(hyphen)) {
            return Boolean3.TRUE;
        } else if ("none".equals(hyphen)) {
            return Boolean3.FALSE;
        } else {
            return Boolean3.UNDEFINED;
        }
    }

    private static final Map<String, Object> ourCache = new HashMap<String, Object>();
    private static final Object ourNullObject = new Object();

    private static ZLTextStyleEntry.Length parseLength(String value) {
        if (value.length() == 0) {
            return null;
        }

        final Object cached = ourCache.get(value);
        if (cached != null) {
            return cached == ourNullObject ? null : (ZLTextStyleEntry.Length) cached;
        }

        ZLTextStyleEntry.Length length = null;
        try {
            if (value.endsWith("%")) {
                length = new ZLTextStyleEntry.Length(
                        Short.valueOf(value.substring(0, value.length() - 1)),
                        ZLTextStyleEntry.SizeUnit.PERCENT
                );
            } else if (value.endsWith("rem")) {
                length = new ZLTextStyleEntry.Length(
                        (short) (100 * Double.valueOf(value.substring(0, value.length() - 2))),
                        ZLTextStyleEntry.SizeUnit.REM_100
                );
            } else if (value.endsWith("em")) {
                length = new ZLTextStyleEntry.Length(
                        (short) (100 * Double.valueOf(value.substring(0, value.length() - 2))),
                        ZLTextStyleEntry.SizeUnit.EM_100
                );
            } else if (value.endsWith("ex")) {
                length = new ZLTextStyleEntry.Length(
                        (short) (100 * Double.valueOf(value.substring(0, value.length() - 2))),
                        ZLTextStyleEntry.SizeUnit.EX_100
                );
            } else if (value.endsWith("px")) {
                length = new ZLTextStyleEntry.Length(
                        Short.valueOf(value.substring(0, value.length() - 2)),
                        ZLTextStyleEntry.SizeUnit.PIXEL
                );
            } else if (value.endsWith("pt")) {
                length = new ZLTextStyleEntry.Length(
                        Short.valueOf(value.substring(0, value.length() - 2)),
                        ZLTextStyleEntry.SizeUnit.POINT
                );
            }
        } catch (Exception e) {
            // ignore
        }
        ourCache.put(value, length != null ? length : ourNullObject);
        return length;
    }
}
