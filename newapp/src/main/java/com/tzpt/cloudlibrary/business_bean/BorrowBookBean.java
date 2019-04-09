package com.tzpt.cloudlibrary.business_bean;

/**
 * 借阅图书信息
 * Created by Administrator on 2018/10/30.
 */

public class BorrowBookBean extends BaseBookBean {

    public long mBorrowerId;
    public boolean mIsPraised;           //是否点过赞
    //当前借阅
    public String mCurrentBookDateInfo;  //当前借阅期间信息
    public boolean mIsOverdue;           //当前借阅是否逾期
    public boolean mOneKeyToRenew;       //当前借阅是否可以一键续借
    public int mHasDays;                 //剩余借书天数
    public boolean mIsOverdueBuyTip;     //逾期购买提示

    //历史借阅-借书还书日期
    public String mHistoryBorrowDate;    ////历史借阅时间段：借阅时间 - 归还时间
    public String mHistoryBackDate;
    public boolean mIsBuy;              //已购买
    public boolean mIsLost;             //已赔书
    public int mBorrowState;            //借阅状态 5:在借 6:归还 7:赔偿 28:逾期购买
    public int mBorrowDays;             //借阅期限
}
