package com.tzpt.cloudlibrary.bean;

/**
 * Created by ZhiqiangJia on 2017-08-23.
 */

public class UserDepositBean {
    public String mOperationDate;           //操作日期
    public String mMoney;                   //金额
    public String mOperation;               //交易项目
    public String mRemark;                  //支付说明(平台,或客户名)
    public String mStatus;                  //支付状态
    public String mOrderNum;                //流水号
    public String mOperateOrder;            //单号
    public String mPayRemark;               //付款（退款）方式
    public double mDeductionMoney;          //罚金免单的金额
    public boolean mIsRefund;               //false付款/true退款
    public String mComment;                 //备注信息
    public int mTransactionType;            //交易类型1:交押金,2:退押金,3:赔书,4:罚金,5:购书,6:罚金免单
}
