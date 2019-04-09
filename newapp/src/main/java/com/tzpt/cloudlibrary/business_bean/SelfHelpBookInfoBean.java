package com.tzpt.cloudlibrary.business_bean;

/**
 * 自助借书图书
 * Created by Administrator on 2018/12/4.
 */

public class SelfHelpBookInfoBean extends BaseBookBean {
    public long mId;                        //ID
    public String mBelongLibraryHallCode;   //所属管号
    public String mStayLibraryHallCode;     //所在馆号
    public double mAttachPrice;             //溢价
    public int mDeposit;                    //是否需要押金 1押金模式
    public boolean mStatusSuccess;          //提交借阅后书籍的状态
    public String mStatusDesc;              //提交借阅后书籍的状态描述

    public DepositType mDepositType = DepositType.PLATFORM_DEPOSIT;//指定可以使用的押金方式

    public UsedDepositType mUsedDepositType = UsedDepositType.LIB_DEPOSIT;//使用的押金方式

    @Override
    public boolean equals(Object obj) {
        if (((SelfHelpBookInfoBean) obj).mId == this.mId) {
            return true;
        }
        if (mBelongLibraryHallCode != null) {
            if (((SelfHelpBookInfoBean) obj).mBelongLibraryHallCode.equals(this.mBelongLibraryHallCode)
                    && ((SelfHelpBookInfoBean) obj).mBook.mBarNumber.equals(this.mBook.mBarNumber)) {
                return true;
            }
        }
        return super.equals(obj);
    }
}
