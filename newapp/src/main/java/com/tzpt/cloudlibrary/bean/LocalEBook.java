package com.tzpt.cloudlibrary.bean;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/20.
 */

public class LocalEBook implements Serializable, Comparable<LocalEBook> {
    public String mId;
    public String mTitle;
    public String mAuthor;
    public String mFilePath;
    public String mDownloadUrl;
    public String mSize;
    public String mProgress;
    public String mCoverImg;
    public String mShareUrl;
    public String mShareContent;
    public String mDescContent;//书的简介描述内容
    public String mBelongLibCode;
    public boolean mIsSelected;
    public boolean mIsDeleteMode;
    public long mTimestamp;

    @Override
    public int compareTo(@NonNull LocalEBook another) {
        if (this.mTimestamp < another.mTimestamp) {
            return 1;
        } else {
            return -1;
        }
    }
}
