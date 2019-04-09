package com.tzpt.cloudlibrary.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 活动
 * Created by ZhiqiangJia on 2017-08-17.
 */
public class ActionInfoBean implements Parcelable {

    public int mId;                 //活动ID
    public String mAddress;         //活动地址
    public String mImage;           //活动图片
    public String mUrl;             //活动网页地址
    public String mTitle;           //活动标题
    public String mSponsor;         //主办方
    public String mStartDateTime;   //开始时间
    public String mSummary;         //摘要
    public int mAllowApplyNow;      //当前时间是否允许报名 0:不允许报名 1:允许报名
    public String mContactName;     //
    public String mContactPhone;    //
    public String mContent;         //内容
    public String mEndDateTime;     //结束时间
    public int mEnrollment;         //该活动是否可以报名 0:不可报名 1:可以报名
    public int mIsApply;            //是否已经报名 0:未报名 1:已报名
    public int mStatus;             //
    public String mShareUrl;        //活动分享地址
    public boolean mIsJoin;         //是否报名
    public int mApplyStatus;        //报名状态 (0,"可报名"), (1,"已报名"), (2,"报名截止"),(3,"活动结束"),(4,"活动不可以报名"),(5,"报名已满");

    public ActionInfoBean() {
    }

    protected ActionInfoBean(Parcel in) {
        mId = in.readInt();
        mAddress = in.readString();
        mImage = in.readString();
        mUrl = in.readString();
        mTitle = in.readString();
        mSponsor = in.readString();
        mStartDateTime = in.readString();
        mSummary = in.readString();
        mAllowApplyNow = in.readInt();
        mContactName = in.readString();
        mContactPhone = in.readString();
        mContent = in.readString();
        mEndDateTime = in.readString();
        mEnrollment = in.readInt();
        mIsApply = in.readInt();
        mStatus = in.readInt();
        mShareUrl = in.readString();
        mIsJoin = in.readByte() != 0;
        mApplyStatus = in.readInt();
    }

    public static final Creator<ActionInfoBean> CREATOR = new Creator<ActionInfoBean>() {
        @Override
        public ActionInfoBean createFromParcel(Parcel in) {
            return new ActionInfoBean(in);
        }

        @Override
        public ActionInfoBean[] newArray(int size) {
            return new ActionInfoBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mAddress);
        dest.writeString(mImage);
        dest.writeString(mUrl);
        dest.writeString(mTitle);
        dest.writeString(mSponsor);
        dest.writeString(mStartDateTime);
        dest.writeString(mSummary);
        dest.writeInt(mAllowApplyNow);
        dest.writeString(mContactName);
        dest.writeString(mContactPhone);
        dest.writeString(mContent);
        dest.writeString(mEndDateTime);
        dest.writeInt(mEnrollment);
        dest.writeInt(mIsApply);
        dest.writeInt(mStatus);
        dest.writeString(mShareUrl);
        dest.writeByte((byte) (mIsJoin ? 1 : 0));
        dest.writeInt(mApplyStatus);
    }
}
