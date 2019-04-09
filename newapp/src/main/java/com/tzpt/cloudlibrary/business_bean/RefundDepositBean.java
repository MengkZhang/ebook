package com.tzpt.cloudlibrary.business_bean;

import com.tzpt.cloudlibrary.bean.DepositBalanceBean;

import java.util.ArrayList;

/**
 * 退押金
 * Created by Administrator on 2018/12/6.
 */

public class RefundDepositBean {
    public ArrayList<DepositBalanceBean> mOffLineDepositList;//下载押金列表
    public double mPlatformAvailableBalance;    //线上可退押金
    public double mLibAvailableBalance;         //线下可退押金
    public boolean mIsNoWithDrawable;
    public String mNoWithdrawMsg;
    public double mMaxAmount;
    //    public double mPenalty;                     //罚金金额
    public double mNeedDealPenalty;             //必须处理的罚金金额
    public double mNeedHandleFreePenalty;       //申请免单的罚金
}
