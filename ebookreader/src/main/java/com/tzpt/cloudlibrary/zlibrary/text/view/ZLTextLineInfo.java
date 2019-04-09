package com.tzpt.cloudlibrary.zlibrary.text.view;

/**
 * 每行文字
 * Created by Administrator on 2017/4/8.
 */

public class ZLTextLineInfo {
    //每段文字游标
    final ZLTextParagraphCursor ParagraphCursor;
    //每段文字游标的长度ZLTextElement
    final int ParagraphCursorLength;

    //每行元素（HSpace、NBSpace、StyleClose、ZLTextControlElement、ZLTextImageElement、ZLTextVideoElement、ZLTextWord）开始的索引值
    final int StartElementIndex;
    //字符索引值
    final int StartCharIndex;
    //每行文字（单词）开始的索引值
    int RealStartElementIndex;
    //每行文字（字符）开始的索引值
    int RealStartCharIndex;
    //每行文字结束的索引值
    int EndElementIndex;
    int EndCharIndex;

    boolean isParagraghFirstLine;
    boolean isPageFirstLine;

    boolean IsVisible;
    //左缩进大小
    int LeftIndent;
    int Width;
    //每行高度
    int Height;
    int Descent;
    int VSpaceBefore; //段前距离
    int VSpaceAfter;  //段后距离
    boolean PreviousInfoUsed;
    int SpaceCounter;
    ZLTextStyle StartStyle;

    /**
     * 每行文字内容构造方法
     *
     * @param paragraphCursor 段落游标
     * @param elementIndex    单词索引值
     * @param charIndex       字符索引值
     * @param style           文本风格
     */
    ZLTextLineInfo(ZLTextParagraphCursor paragraphCursor, int elementIndex, int charIndex, ZLTextStyle style) {
        ParagraphCursor = paragraphCursor;
        ParagraphCursorLength = paragraphCursor.getParagraphLength();

        StartElementIndex = elementIndex;
        StartCharIndex = charIndex;
        RealStartElementIndex = elementIndex;
        RealStartCharIndex = charIndex;
        EndElementIndex = elementIndex;
        EndCharIndex = charIndex;

        StartStyle = style;
    }

    /**
     * 判断段落是否结束
     *
     * @return true 结束 false 反之
     */
    boolean isEndOfParagraph() {
        return EndElementIndex == ParagraphCursorLength;
    }

    void adjust(ZLTextLineInfo previous) {
        if (!PreviousInfoUsed && previous != null) {
            Height -= Math.min(previous.VSpaceAfter, VSpaceBefore);
            PreviousInfoUsed = true;
        }
    }

    public int length() {
        int count = 0;
        for (int i = RealStartElementIndex; i < EndElementIndex; i++) {
            ZLTextElement element = ParagraphCursor.getElement(i);
            if (element instanceof ZLTextWord) {
                count++;
            }
        }
        return count;
    }

    @Override
    public boolean equals(Object o) {
        ZLTextLineInfo info = (ZLTextLineInfo) o;
        return
                (ParagraphCursor == info.ParagraphCursor) &&
                        (StartElementIndex == info.StartElementIndex) &&
                        (StartCharIndex == info.StartCharIndex);
    }

    @Override
    public int hashCode() {
        return ParagraphCursor.hashCode() + StartElementIndex + 239 * StartCharIndex;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = RealStartElementIndex; i < EndElementIndex; i++) {
            ZLTextElement element = ParagraphCursor.getElement(i);
            if (element instanceof ZLTextWord) {
                sb.append(element.toString());
            }
        }
        return sb.toString();
    }
}
