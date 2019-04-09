package com.tzpt.cloudlibrary.bean;

/**
 * 保存读者信息
 * Created by ZhiqiangJia on 2017-08-31.
 */
public class SelfReaderInfoBean {

    public double mPenalty;                 //逾期罚金
    public double mUsableDeposit;           //可用押金
    public double mOccupyDeposit;           //占用押金
    public int mCanBorrowBookSum;           //可借图书数量 馆的借阅数-当前借书数
    public boolean mIsDepositType;          //当前馆的押金模式 true 押金模式 false 非押金模式
    public boolean mIsAgreementLib;         //当前馆是否有协议 true 有协议 false无协议
    public String mCurrentLibCode;          //当前图书馆馆号
    public boolean mHandleDeposit;          //是否执行押金流程
}
