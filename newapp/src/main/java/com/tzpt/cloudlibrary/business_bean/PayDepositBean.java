package com.tzpt.cloudlibrary.business_bean;

import com.tzpt.cloudlibrary.bean.DepositBalanceBean;

import java.util.List;

/**
 * Created by Administrator on 2018/12/6.
 */

public class PayDepositBean {
    public List<DepositBalanceBean> mDepositList;   //押金列表
    public List<DepositBalanceBean> mPenaltyList;   //罚金列表
    public double mLimitMoney;                      //最大充值金额
    public double mPenalty;
    public double mDeposit;
    public double mUsedDeposit;
    public double mActiveDeposit;                   //押金余额
}
