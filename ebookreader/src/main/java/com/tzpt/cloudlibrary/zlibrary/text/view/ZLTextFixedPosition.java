package com.tzpt.cloudlibrary.zlibrary.text.view;

/**
 * 段落、元素、字符索引值，可以用于定位文章位置
 * Created by Administrator on 2017/4/8.
 */

public class ZLTextFixedPosition extends ZLTextPosition  {
    private final int ParagraphIndex;
    private final int ElementIndex;
    private final int CharIndex;

    public ZLTextFixedPosition(int paragraphIndex, int elementIndex, int charIndex) {
        ParagraphIndex = paragraphIndex;
        ElementIndex = elementIndex;
        CharIndex = charIndex;
    }

    public ZLTextFixedPosition(ZLTextPosition position) {
        ParagraphIndex = position.getParagraphIndex();
        ElementIndex = position.getElementIndex();
        CharIndex = position.getCharIndex();
    }

    public final int getParagraphIndex() {
        return ParagraphIndex;
    }

    public final int getElementIndex() {
        return ElementIndex;
    }

    public final int getCharIndex() {
        return CharIndex;
    }
//
//    public static class WithTimestamp extends ZLTextFixedPosition {
//        public final long Timestamp;
//
//        public WithTimestamp(int paragraphIndex, int elementIndex, int charIndex, Long stamp) {
//            super(paragraphIndex, elementIndex, charIndex);
//            Timestamp = stamp != null ? stamp : -1;
//        }
//
//        @Override
//        public String toString() {
//            return super.toString() + "; timestamp = " + Timestamp;
//        }
//    }
}
