package com.tzpt.cloudlibrary.bean;

/**
 * Created by Administrator on 2017/10/23.
 */

public class BookMarkBean {
    private String mTocTitle;
    private String mContent;
    private String mProgress;
    private String mAddDate;

    private int mParagraphIndex;
    private int mElementIndex;
    private int mCharIndex;

    public String getTocTitle() {
        return mTocTitle;
    }

    public void setTocTitle(String tocTitle) {
        this.mTocTitle = tocTitle;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String mContent) {
        this.mContent = mContent;
    }

    public String getProgress() {
        return mProgress;
    }

    public void setProgress(String progress) {
        this.mProgress = progress;
    }

    public String getAddDate() {
        return mAddDate;
    }

    public void setAddDate(String addDate) {
        this.mAddDate = addDate;
    }

    public int getParagraphIndex() {
        return mParagraphIndex;
    }

    public void setParagraphIndex(int paragraphIndex) {
        this.mParagraphIndex = paragraphIndex;
    }

    public int getElementIndex() {
        return mElementIndex;
    }

    public void setElementIndex(int mElementIndex) {
        this.mElementIndex = mElementIndex;
    }

    public int getCharIndex() {
        return mCharIndex;
    }

    public void setCharIndex(int mCharIndex) {
        this.mCharIndex = mCharIndex;
    }
}
