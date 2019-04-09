package com.tzpt.cloundlibrary.manager.bean;

/**
 * 借书还书图书信息
 * Created by Administrator on 2017/7/8.
 */
public class BookInfoBean {
    public long mId;
    public String mBelongLibraryHallCode;    //馆号
    public String mBarNumber;                //条码号
    public String mProperTitle;              //书名
    public double mPrice;                    //单价
    public int mDeposit;                     //0 非押金模式  1 押金模式
    public double mAttachPrice;              //溢价
    public boolean mColorIsRed = false;      //条码号是否为红色
    public double mPenalty;                  //还书时的罚金
    public long mPenaltyId;                  //还书时罚金的ID
    public boolean mHandlePenalty = false;   //罚金是否处理了 false未处理 true已处理或者本书没有罚金
    public String mBookState;                //图书当前状态 1在馆 2在借 25流出
    public long mBorrowId;                   //借阅ID

    public boolean mCompensateChoosed = false;//陪书是否选中

    public UsedDepositType mUsedDepositType = UsedDepositType.LIB_DEPOSIT;

    @Override
    public boolean equals(Object obj) {
        if (((BookInfoBean) obj).mId == this.mId) {
            return true;
        }
        if (mBelongLibraryHallCode != null) {
            if (((BookInfoBean) obj).mBelongLibraryHallCode.equals(this.mBelongLibraryHallCode)
                    && ((BookInfoBean) obj).mBarNumber.equals(this.mBarNumber)) {
                return true;
            }
        }
        if (((BookInfoBean) obj).mBarNumber.equals(this.mBarNumber)) {
            return true;
        }
        return super.equals(obj);
    }
}
