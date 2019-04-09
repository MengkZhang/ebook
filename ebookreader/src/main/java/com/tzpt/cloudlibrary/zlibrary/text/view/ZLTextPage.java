package com.tzpt.cloudlibrary.zlibrary.text.view;

import java.util.ArrayList;

/**
 * 书籍每一页的数据结构
 * Created by Administrator on 2017/4/8.
 */

public class ZLTextPage {
    final ZLTextWordCursor mStartCursor = new ZLTextWordCursor();//当前页的起始坐标点
    final ZLTextWordCursor mEndCursor = new ZLTextWordCursor();//当前页的结束坐标点
    final ArrayList<ZLTextLineInfo> mLineInfos = new ArrayList<>(); // 一行一行的文字
    int mPaintState = PaintStateEnum.NOTHING_TO_PAINT;//每一个页数据的初始状态

    /**
     * 屏幕上每一个数据（文字、图片、间隔符等）的坐标
     */
    final ZLTextElementAreaVector mTextElementMap = new ZLTextElementAreaVector();

    private int mColumnWidth;
    private int mHeight;

    void setSize(int columnWidth, int height) {
        if (mColumnWidth == columnWidth && mHeight == height) {
            return;
        }
        mColumnWidth = columnWidth;
        mHeight = height;

        if (mPaintState != PaintStateEnum.NOTHING_TO_PAINT) {
            mLineInfos.clear();
//            if (keepEndNotStart) {
//                if (!mEndCursor.isNull()) {
//                    Log.e("ZLTextPage", "++++++++++++++++++39");
//                    mStartCursor.reset();
//                    mPaintState = PaintStateEnum.END_IS_KNOWN;
//                } else if (!mStartCursor.isNull()) {
//                    Log.e("ZLTextPage", "++++++++++++++++++43");
//                    mEndCursor.reset();
//                    mPaintState = PaintStateEnum.START_IS_KNOWN;
//                }
//            } else {
//                if (!mStartCursor.isNull()) {
//                    Log.e("ZLTextPage", "++++++++++++++++++49");
//                    mEndCursor.reset();
//                    mPaintState = PaintStateEnum.START_IS_KNOWN;
//                } else if (!mEndCursor.isNull()) {
//                    Log.e("ZLTextPage", "++++++++++++++++++53");
//                    mStartCursor.reset();
//                    mPaintState = PaintStateEnum.END_IS_KNOWN;
//                }
//            }
        }
    }

    /**
     * 重置
     */
    void reset() {
        mStartCursor.reset();
        mEndCursor.reset();
        mLineInfos.clear();
        mPaintState = PaintStateEnum.NOTHING_TO_PAINT;
    }

    /**
     * 移动起始坐标点到指定的段落
     *
     * @param cursor 段落游标
     */
    void moveStartCursor(ZLTextParagraphCursor cursor) {
        mStartCursor.setCursor(cursor);
        mEndCursor.reset();
        mLineInfos.clear();
        mPaintState = PaintStateEnum.START_IS_KNOWN;
    }

    /**
     * 移动起始坐标点到指定的位置
     *
     * @param paragraphIndex 段落索引
     * @param wordIndex      单词索引
     * @param charIndex      字符索引
     */
    void moveStartCursor(int paragraphIndex, int wordIndex, int charIndex) {
        if (mStartCursor.isNull()) {
            mStartCursor.setCursor(mEndCursor);
        }
        mStartCursor.moveToParagraph(paragraphIndex);
        mStartCursor.moveTo(wordIndex, charIndex);
        mEndCursor.reset();
        mLineInfos.clear();
        mPaintState = PaintStateEnum.START_IS_KNOWN;
    }

    /**
     * 移动结束坐标点到指定的段落
     *
     * @param paragraphIndex 段落索引
     */
    void moveEndCursor(int paragraphIndex) {
        if (mEndCursor.isNull()) {
            mEndCursor.setCursor(mStartCursor);
        }
        mEndCursor.moveToParagraph(paragraphIndex);
        if (paragraphIndex > 0) {
            mEndCursor.previousParagraph();
            mEndCursor.moveToParagraphEnd();
        } else {
            mEndCursor.moveTo(0, 0);
        }
        mStartCursor.reset();
        mLineInfos.clear();
        mPaintState = PaintStateEnum.END_IS_KNOWN;
    }

    /**
     * 页面文字显示区域的宽度
     *
     * @return 宽度
     */
    int getTextWidth() {
        return mColumnWidth;
    }

    /**
     * 页面文字显示区域的高度
     *
     * @return 高度
     */
    int getTextHeight() {
        return mHeight;
    }
}
