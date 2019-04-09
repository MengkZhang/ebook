package com.tzpt.cloudlibrary.zlibrary.text.view;

import com.tzpt.cloudlibrary.zlibrary.core.view.ZLPaintContext;

/**
 * Created by Administrator on 2017/4/8.
 */

public final class ZLTextWord extends ZLTextElement {
    //绘制文本字节数组
    public final char[] Data;
    //表示第一个要绘制的文字索引
    public final int Offset;
    //需要绘制的文字个数
    public final int Length;
    private int mWidth = -1;
    private Mark myMark;
    private int myParagraphOffset;

    class Mark {
        public final int Start;
        public final int Length;
        private Mark myNext;

        private Mark(int start, int length) {
            Start = start;
            Length = length;
            myNext = null;
        }

        public Mark getNext() {
            return myNext;
        }

        private void setNext(Mark mark) {
            myNext = mark;
        }
    }

    ZLTextWord(String word, int paragraphOffset) {
        this(word.toCharArray(), 0, word.length(), paragraphOffset);
    }

    ZLTextWord(char[] data, int offset, int length, int paragraphOffset) {
        Data = data;
        Offset = offset;
        Length = length;
        myParagraphOffset = paragraphOffset;
    }

    public boolean isASpace() {
        for (int i = Offset; i < Offset + Length; ++i) {
            if (!Character.isWhitespace(Data[i])) {
                return false;
            }
        }
        return true;
    }

    public Mark getMark() {
        return myMark;
    }

    public int getParagraphOffset() {
        return myParagraphOffset;
    }

    public void addMark(int start, int length) {
        Mark existingMark = myMark;
        Mark mark = new Mark(start, length);
        if ((existingMark == null) || (existingMark.Start > start)) {
            mark.setNext(existingMark);
            myMark = mark;
        } else {
            while ((existingMark.getNext() != null) && (existingMark.getNext().Start < start)) {
                existingMark = existingMark.getNext();
            }
            mark.setNext(existingMark.getNext());
            existingMark.setNext(mark);
        }
    }

    public int getWidth(ZLPaintContext context) {
        int width = mWidth;
        if (width <= 1) {
            width = context.getStringWidth(Data, Offset, Length);
            mWidth = width;
        }
        return width;
    }

    @Override
    public String toString() {
        return getString();
    }

    public String getString() {
        return new String(Data, Offset, Length);
    }
}
