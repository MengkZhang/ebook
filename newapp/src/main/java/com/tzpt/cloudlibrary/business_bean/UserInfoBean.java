package com.tzpt.cloudlibrary.business_bean;

import com.tzpt.cloudlibrary.base.data.User;

/**
 * Created by Administrator on 2018/11/15.
 */

public class UserInfoBean {
    public User mUser;
//    public String mToken;               //登录Token
    public boolean mIsBorrowOverdue;    //是否存在即将逾期 true表示存在 false表示不存在
    public int mUpcomingOverdueCount;   //即将逾期书籍数量
    //public boolean mIsOfficialReader; //是否是正式读者 0非限制(正式读者),1 限制(非正式读者)
    public boolean mIsChargeable;       //是否可以充值 true:可以充值; false:不可以充值
    public int mActionCount;            //我的报名数量
    public int mAppointCount;           //当前预约数量
    public int mBorrowOverdueSum;       //当前借阅逾期数量   当前借阅+历史借阅书籍数量/逾期书籍
    public int mOverdueUnReadSum;       //当前借阅未读逾期数量
    public int mBorrowSum;              //当前借阅数量
    public int mNoteCount;              //读书笔记数量
    public int mBorrowTotal;            //历史借阅数量
    public int mUnreadMsgCount;         //未读消息数量
    public int mUnreadOverdueMsgCount;  //未读逾期消息数量
    public long mAttentionLibId;        //关注图书馆ID
    public String mAttentionLibCode;    //关注图书馆libCode
    public String mAttentionLibName;    //关注图书馆名字
    public String mFaceRecognitionImg;  //面部识别图片地址

    public int mBuyBookShelfSum;         //购书架显示：总购书数量
    public int mCollectionEBookSum;      //收藏：收藏的电子书
    public int mCollectionVideoSum;      //收藏：收藏的视频


}
