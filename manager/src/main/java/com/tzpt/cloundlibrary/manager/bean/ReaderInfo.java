package com.tzpt.cloundlibrary.manager.bean;

import java.io.Serializable;

/**
 * 读者信息
 * Created by Administrator on 2017/7/7.
 */
public class ReaderInfo implements Serializable {
    //借阅证号
    public String mBorrowCard;
    //可借数量
    public int mBorrowableSum;
    //在借数量
    public int mBorrowingNum;
    //姓名
    public String mCardName;
    //性别
    public String mGender;
    //读者id
    public String mReaderId;
    //身份证号码
    public String mIdCard;
    //身份证头像下载地址
    public String mIdCardImage;
    //逾期数量
    public int mOverdueNum;
    //手机号码
    public String mPhone;

    public double mApplyPenalty;//已申请免单未审核的罚金金额

    public double mNotApplyPenalty;//未申请免单的罚金金额

    public ReaderDepositInfo mLibraryDeposit;//当前馆下的馆押金

    public ReaderDepositInfo mOfflineDeposit;//线下押金(即当前馆对应客户下的所有馆押金)

    public ReaderDepositInfo mPlatformDeposit;//线上押金(即共享押金)

    public double getPlatformUsableDeposit() {
        if (mPlatformDeposit != null) {
            return mPlatformDeposit.mAvailableBalance;
        }
        return 0;
    }

    public double getLibraryUsableDeposit() {
        if (mLibraryDeposit != null) {
            return mLibraryDeposit.mAvailableBalance;
        }
        return 0;
    }

    public double getOfflineUsableDeposit() {
        if (mOfflineDeposit != null) {
            return mOfflineDeposit.mAvailableBalance;
        }
        return 0;
    }
}
