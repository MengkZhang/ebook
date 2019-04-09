package com.tzpt.cloudlibrary.business_bean;

/**
 * 处理图书业务
 * Created by tonyjia on 2018/10/31.
 */
public class BookBean extends BaseBookBean {

    public String mHtmlUrl;         //分享URL
    public int mBorrowNum;          //借阅数量
    public int mPraiseNum;          //点赞数量
    public int mRecommendNum;       //推荐数量
    public int mShareNum;           //分享数量
    public boolean mHasNewBookFlag; //是否有新书标记
    public boolean mIsShowBorrowNum = true;//是否显示借阅量

}
