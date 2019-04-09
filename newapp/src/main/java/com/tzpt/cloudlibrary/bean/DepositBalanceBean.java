package com.tzpt.cloudlibrary.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2018/5/25.
 */

public class DepositBalanceBean implements Parcelable {
    public String mName;                //馆名
    public String mLibCode;             //馆号
    public int mIsUnusual;              //馆是否不正常 1
    public double mUsableDeposit;       //可用押金
    public double mDepositBalance;      //押金总额
    public double mOccupyDeposit;       //占用押金
    public double mPenalty;             //罚金
    public int mPenaltyHandleType;      //罚金处理方式

    public DepositBalanceBean() {

    }

    protected DepositBalanceBean(Parcel in) {
        mName = in.readString();
        mLibCode = in.readString();
        mIsUnusual = in.readInt();
        mUsableDeposit = in.readDouble();
        mDepositBalance = in.readDouble();
        mOccupyDeposit = in.readDouble();
        mPenalty = in.readDouble();
        mPenaltyHandleType = in.readInt();
    }

    public static final Creator<DepositBalanceBean> CREATOR = new Creator<DepositBalanceBean>() {
        @Override
        public DepositBalanceBean createFromParcel(Parcel in) {
            return new DepositBalanceBean(in);
        }

        @Override
        public DepositBalanceBean[] newArray(int size) {
            return new DepositBalanceBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mLibCode);
        dest.writeInt(mIsUnusual);
        dest.writeDouble(mUsableDeposit);
        dest.writeDouble(mDepositBalance);
        dest.writeDouble(mOccupyDeposit);
        dest.writeDouble(mPenalty);
        dest.writeInt(mPenaltyHandleType);
    }
}
