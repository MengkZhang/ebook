package com.tzpt.cloudlibrary.zlibrary.text.view;

/**
 * Created by Administrator on 2017/4/8.
 */

public final class ZLTextElementArea extends ZLTextFixedPosition {
    final float XStart;
    final float XEnd;
    final float YStart;
    final float YEnd;
    private final int ColumnIndex;

    final int Length;
    final boolean AddHyphenationSign;
    final boolean ChangeStyle;
    final ZLTextStyle Style;
    final ZLTextElement Element;

    private final boolean mIsLastInElement;

    /**
     * @param paragraphIndex     段落索引值
     * @param elementIndex       元素索引值
     * @param charIndex          字符索引值
     * @param length             element的长度
     * @param lastInElement      是否是最后一个element
     * @param addHyphenationSign 是否是断字
     * @param changeStyle        是否改变element的样式（ZLTextStyleElement、ZLTextControlElement）
     * @param style              changeStyle为true的时候使用该style
     * @param element            单词、文字、数字（符号+element）
     * @param xStart
     * @param xEnd
     * @param yStart
     * @param yEnd
     * @param columnIndex
     */
    ZLTextElementArea(int paragraphIndex, int elementIndex, int charIndex, int length,
                      boolean lastInElement, boolean addHyphenationSign, boolean changeStyle,
                      ZLTextStyle style, ZLTextElement element, float xStart, float xEnd, float yStart,
                      float yEnd, int columnIndex) {
        super(paragraphIndex, elementIndex, charIndex);

        XStart = xStart;
        XEnd = xEnd;
        YStart = yStart;
        YEnd = yEnd;
        ColumnIndex = columnIndex;

        Length = length;
        mIsLastInElement = lastInElement;

        AddHyphenationSign = addHyphenationSign;
        ChangeStyle = changeStyle;
        Style = style;
        Element = element;
    }

    boolean contains(int x, int y) {
        return (y >= YStart) && (y <= YEnd) && (x >= XStart) && (x <= XEnd);
    }

    boolean isFirstInElement() {
        return getCharIndex() == 0;
    }

    boolean isLastInElement() {
        return mIsLastInElement;
    }
}
