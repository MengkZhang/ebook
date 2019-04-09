package com.tzpt.cloudlibrary.business_bean;

/**
 * 自助借书读者在当前馆信息
 * Created by Administrator on 2018/12/4.
 */

public class SelfHelpReaderBean {
    public double mPenalty;                 //逾期罚金
    public double mUsablePlatformDeposit;   //可用共享押金
    public double mUsableLibDeposit;        //可用馆押金
    public int mCanBorrowBookSum;           //可借图书数量 馆的借阅数-当前借书数
    public boolean mIsDepositType;          //当前馆的押金模式 true 押金模式 false 非押金模式
    public String mCurrentLibCode;          //当前图书馆馆号
    public boolean mHandleDeposit;          //是否执行押金流程

    public DepositType mDepositType = DepositType.PLATFORM_DEPOSIT;

    public DepositSequenceType mDepositSequenceType = DepositSequenceType.PLATFORM_DEPOSIT;

    //押金使用顺序
    public enum DepositSequenceType {
        PLATFORM_DEPOSIT, LIB_DEPOSIT //优先共享押金; 优先馆押金
    }
}
