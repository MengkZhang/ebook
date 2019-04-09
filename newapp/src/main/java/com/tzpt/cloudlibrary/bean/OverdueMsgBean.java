package com.tzpt.cloudlibrary.bean;

/**
 * Created by Administrator on 2018/3/29.
 */

public class OverdueMsgBean {
    public int mId;
    public long mBorrowId;
    public String mMsgContent;
    public String mCreateTime;
    public int mState;//1未查看,2查看
    public boolean mIsHistory;
}
