package com.tzpt.cloudlibrary.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 资讯
 * Created by ZhiqiangJia on 2017-08-17.
 */

public class InformationBean implements Parcelable {
    public long mId;             //资讯ID
    public String mContent;     //资讯内容
    public String mTitle;       //资讯标题
    public String mCreateDate;  //创建时间
    public String mSource;      //来源
    public String mIsPraise;    //是否点赞 null:未传入身份证号或设备ID 1:已点赞 0:未点赞
    public int mReadCount;      //阅读量
    public String mImage;       //图片
    public String mUrl;         //资讯网页url
    public String mSummary;     //简介
    public int mPraiseCount;    //点赞量
    public String mVideoUrl;    //视频地址
    public String mVideoDuration;//视频描述
    public String mShareUrl;     //分享地址
    public String mCreateTime;    //创建时间

    public InformationBean() {
    }

    protected InformationBean(Parcel in) {
        mId = in.readLong();
        mContent = in.readString();
        mTitle = in.readString();
        mCreateDate = in.readString();
        mSource = in.readString();
        mIsPraise = in.readString();
        mReadCount = in.readInt();
        mImage = in.readString();
        mUrl = in.readString();
        mSummary = in.readString();
        mPraiseCount = in.readInt();
        mVideoUrl = in.readString();
        mVideoDuration = in.readString();
        mShareUrl = in.readString();
        mCreateTime = in.readString();
    }

    public static final Creator<InformationBean> CREATOR = new Creator<InformationBean>() {
        @Override
        public InformationBean createFromParcel(Parcel in) {
            return new InformationBean(in);
        }

        @Override
        public InformationBean[] newArray(int size) {
            return new InformationBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mContent);
        dest.writeString(mTitle);
        dest.writeString(mCreateDate);
        dest.writeString(mSource);
        dest.writeString(mIsPraise);
        dest.writeInt(mReadCount);
        dest.writeString(mImage);
        dest.writeString(mUrl);
        dest.writeString(mSummary);
        dest.writeInt(mPraiseCount);
        dest.writeString(mVideoUrl);
        dest.writeString(mVideoDuration);
        dest.writeString(mShareUrl);
        dest.writeString(mCreateTime);
    }
}
