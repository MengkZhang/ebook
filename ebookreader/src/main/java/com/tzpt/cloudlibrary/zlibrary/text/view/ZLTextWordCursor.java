package com.tzpt.cloudlibrary.zlibrary.text.view;

import com.tzpt.cloudlibrary.zlibrary.text.model.ZLTextMark;
import com.tzpt.cloudlibrary.zlibrary.text.model.ZLTextModel;

/**
 * 第几段            This is CBReader.
 * 第几个单词        CBReader
 * 第几个字母        R
 * Created by Administrator on 2017/4/8.
 */

public final class ZLTextWordCursor extends ZLTextPosition {
    private ZLTextParagraphCursor mParagraphCursor;
    private int mElementIndex;
    private int mCharIndex;

    public ZLTextWordCursor() {
    }

    public ZLTextWordCursor(ZLTextWordCursor cursor) {
        setCursor(cursor);
    }

    public void setCursor(ZLTextWordCursor cursor) {
        mParagraphCursor = cursor.mParagraphCursor;
        mElementIndex = cursor.mElementIndex;
        mCharIndex = cursor.mCharIndex;
    }

    public void setCursor(ZLTextParagraphCursor paragraphCursor) {
        mParagraphCursor = paragraphCursor;
        mElementIndex = 0;
        mCharIndex = 0;
    }

    public boolean isNull() {
        return mParagraphCursor == null;
    }

    /**
     * 判断段落是否是起始
     *
     * @return true 是 false 反之
     */
    public boolean isStartOfParagraph() {
        return mElementIndex == 0 && mCharIndex == 0;
    }

    /**
     * 判断书籍是否是起始
     *
     * @return true 是 false 反之
     */
    public boolean isStartOfText() {
        return isStartOfParagraph() && mParagraphCursor.isFirst();
    }

    /**
     * 判断段落是否结束
     *
     * @return true 结束 false 反之
     */
    public boolean isEndOfParagraph() {
        return mParagraphCursor != null &&
                mElementIndex == mParagraphCursor.getParagraphLength();
    }

    /**
     * 判断书籍是否结束
     *
     * @return true 结束 false 反之
     */
    public boolean isEndOfText() {
        return isEndOfParagraph() && mParagraphCursor.isLast();
    }

    @Override
    public int getParagraphIndex() {
        return mParagraphCursor != null ? mParagraphCursor.mIndex : 0;
    }

    @Override
    public int getElementIndex() {
        return mElementIndex;
    }

    @Override
    public int getCharIndex() {
        return mCharIndex;
    }

    /**
     * 获取当前游标位置的元素
     *
     * @return ZLTextElement
     */
    public ZLTextElement getElement() {
        return mParagraphCursor.getElement(mElementIndex);
    }

    /**
     * 获取段落游标
     *
     * @return ZLTextParagraphCursor
     */
    public ZLTextParagraphCursor getParagraphCursor() {
        return mParagraphCursor;
    }

    /**
     * 获取书籍标签
     *
     * @return ZLTextMark
     */
    public ZLTextMark getMark() {
        if (mParagraphCursor == null) {
            return null;
        }
        final ZLTextParagraphCursor paragraph = mParagraphCursor;
        int paragraphLength = paragraph.getParagraphLength();
        int wordIndex = mElementIndex;
        while ((wordIndex < paragraphLength) && (!(paragraph.getElement(wordIndex) instanceof ZLTextWord))) {
            wordIndex++;
        }
        if (wordIndex < paragraphLength) {
            return new ZLTextMark(paragraph.mIndex, ((ZLTextWord) paragraph.getElement(wordIndex)).getParagraphOffset(), 0);
        }
        return new ZLTextMark(paragraph.mIndex + 1, 0, 0);
    }

    public void nextWord() {
        mElementIndex++;
        mCharIndex = 0;
    }

    public void previousWord() {
        mElementIndex--;
        mCharIndex = 0;
    }

    /**
     * 判断是否还有下一段，并且移动到段落起始位置
     *
     * @return true 有 false 没有
     */
    public boolean nextParagraph() {
        if (!isNull()) {
            if (!mParagraphCursor.isLast()) {
                mParagraphCursor = mParagraphCursor.next();
                moveToParagraphStart();
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否还有上一段，并且移动到段落的起始位置
     *
     * @return true 有 false 没有
     */
    public boolean previousParagraph() {
        if (!isNull()) {
            if (!mParagraphCursor.isFirst()) {
                mParagraphCursor = mParagraphCursor.previous();
                moveToParagraphStart();
                return true;
            }
        }
        return false;
    }

    /**
     * 移动到段落的起始位置
     */
    public void moveToParagraphStart() {
        if (!isNull()) {
            mElementIndex = 0;
            mCharIndex = 0;
        }
    }

    /**
     * 移动到段落的结束位置
     */
    public void moveToParagraphEnd() {
        if (!isNull()) {
            mElementIndex = mParagraphCursor.getParagraphLength();
            mCharIndex = 0;
        }
    }

    /**
     * 移动到指定的段落
     *
     * @param paragraphIndex 段落索引值
     */
    public void moveToParagraph(int paragraphIndex) {
        if (!isNull() && (paragraphIndex != mParagraphCursor.mIndex)) {
            final ZLTextModel model = mParagraphCursor.mModel;
            paragraphIndex = Math.max(0, Math.min(paragraphIndex, model.getParagraphsNumber() - 1));
            mParagraphCursor = mParagraphCursor.mCursorManager.get(paragraphIndex);
            moveToParagraphStart();
        }
    }

    /**
     * 移动单词和字符的索引值
     * @param wordIndex 单词索引值
     * @param charIndex 字符索引值
     */
    public void moveTo(int wordIndex, int charIndex) {
        if (!isNull()) {
            if (wordIndex == 0 && charIndex == 0) {
                mElementIndex = 0;
                mCharIndex = 0;
            } else {
                wordIndex = Math.max(0, wordIndex);
                int size = mParagraphCursor.getParagraphLength();
                if (wordIndex > size) {
                    mElementIndex = size;
                    mCharIndex = 0;
                } else {
                    mElementIndex = wordIndex;
                    setCharIndex(charIndex);
                }
            }
        }
    }

    public void setCharIndex(int charIndex) {
        charIndex = Math.max(0, charIndex);
        mCharIndex = 0;
        if (charIndex > 0) {
            ZLTextElement element = mParagraphCursor.getElement(mElementIndex);
            if (element instanceof ZLTextWord) {
                if (charIndex <= ((ZLTextWord) element).Length) {
                    mCharIndex = charIndex;
                }
            }
        }
    }

    public void reset() {
        mParagraphCursor = null;
        mElementIndex = 0;
        mCharIndex = 0;
    }

    public void rebuild() {
        if (!isNull()) {
            mParagraphCursor.clear();
            mParagraphCursor.fill();
            moveTo(mElementIndex, mCharIndex);
        }
    }

    @Override
    public String toString() {
        return super.toString() + " (" + mParagraphCursor + "," + mElementIndex + "," + mCharIndex + ")";
    }
}
