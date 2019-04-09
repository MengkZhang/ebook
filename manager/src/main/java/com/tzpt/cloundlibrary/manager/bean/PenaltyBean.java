package com.tzpt.cloundlibrary.manager.bean;

/**
 * Created by Administrator on 2018/12/14.
 */

public class PenaltyBean {
    public double mPenalty;                  //还书时的罚金
    public long mPenaltyId;                  //还书时罚金的ID
    public String mReturnHallCode;           //馆号 for return book
    public String mBelongLibraryHallCode;    //馆号
    public String mBarNumber;                //条码号
    public String mProperTitle;              //书名
    public double mPrice;                    //单价
    public double mAttachPrice;              //溢价
}
