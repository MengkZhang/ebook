package com.tzpt.cloudlibrary.base.data;

import java.io.Serializable;

/**
 * 图书馆
 * Created by Administrator on 2018/10/30.
 */

public class Library implements Serializable {
    public String mId;               //馆ID
    public String mAddress;          //地址
    public String mName;             //馆名
    public String mCode;             //馆号
//    public String mLighten;          //开放信息
    public String mLightTime;        //开放时间
    public String mPhone;            //电话号码

    public String mInLib;            //在馆图书数量
    public String mOutLib;           //在借图书数量
    public String mLogo;             //logo
    public String mLngLat;           //经纬度
    public String mMail;             //电子邮箱
    public String mNet;              //网址
    public String mIntroduceUrl;     //介绍地址
    public int mLibStatus;           //1.正常 2.屏蔽 3.停用
    public int mBookCount;           //馆藏书数量
    public int mEBookCount;          //电子书数量
    public int mVideoSetCount;       //视频数量
    public int mNewsCount;           //资讯数量
    public int mActivityCount;       //活动数量

    public int mBranchLibCount;      //分馆数量
    public int mSuperLibCount;       //上级馆数量
    public int mHeatCount;           //热度数量
    public String mOpenRestrictionStr;//馆开放描述
    public String mLevelName;         //图书馆等级描述

    public int mRegReaderCount;       //注册读者数量
    public int mVisitReaderCount;     //访问读者数量
    public int mServerReaderCount;    //服务读者数量


}
