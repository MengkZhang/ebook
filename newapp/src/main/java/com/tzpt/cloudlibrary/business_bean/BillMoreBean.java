package com.tzpt.cloudlibrary.business_bean;

import com.tzpt.cloudlibrary.bean.DepositBalanceBean;
import com.tzpt.cloudlibrary.bean.UserDepositBean;

import java.util.List;

public class BillMoreBean {
    public BaseListResultData<UserDepositBean> mBillList;
    public List<DepositBalanceBean> mDepositList;

    public BillMoreBean(BaseListResultData<UserDepositBean> billList, List<DepositBalanceBean> depositList){
        mBillList = billList;
        mDepositList = depositList;
    }
}
