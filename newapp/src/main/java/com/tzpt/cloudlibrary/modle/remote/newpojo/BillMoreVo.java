package com.tzpt.cloudlibrary.modle.remote.newpojo;

/**
 * Created by Administrator on 2018/5/30.
 */

public class BillMoreVo {
    public BaseResultEntityVo<BillInfoVo> mBillList;
    public BaseResultEntityVo<UserDepositVo> mUserDeposit;

    public BillMoreVo(BaseResultEntityVo<BillInfoVo> billList, BaseResultEntityVo<UserDepositVo> userDeposit){
        mBillList = billList;
        mUserDeposit = userDeposit;
    }
}
