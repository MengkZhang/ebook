package com.tzpt.cloudlibrary.business_bean;

/**
 * 电子书
 * Created by tonyjia on 2018/10/31.
 */
public class EBookBean extends BaseBookBean {

    public int mReadCount;                      //阅读数量
    public int mShareNum;                       //分享数量
    public int mCollectNum;                     //收藏数量
    public String mShareUrl;                    //分享网页
    public boolean mHasNewEBookFlag = false;    //是否有新标签
    public boolean mIsSelected = false;         //是否已经选中
    public String mRecommendReason;             //推荐原因
    //public String mRecommendUnit;             //推荐单位

}
