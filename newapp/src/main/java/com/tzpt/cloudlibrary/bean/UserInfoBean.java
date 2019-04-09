package com.tzpt.cloudlibrary.bean;

import java.io.Serializable;

/**
 * 用户信息
 * Created by ZhiqiangJia on 2017-08-18.
 */
public class UserInfoBean implements Serializable {
    public String mName;            //姓名
    public String mPhone;           //电话
    public String mHeadImg;         //头像
    public boolean mIsMan;          //是否男性
    public String mIdCard;          //身份证号码
    public String mCardName;        //身份证名字
    public String mReaderId;        //用户ID
    public String mNickName;        //昵称
    public String mToken;           //登录Token
    public boolean mIsBorrowOverdue;//是否存在即将逾期 true表示存在 false表示不存在
    //public boolean mIsOfficialReader;//是否是正式读者 0非限制(正式读者),1 限制(非正式读者)
    public boolean mIsChargeable;    //是否可以充值 true:可以充值; false:不可以充值
    public int mActionCount;        //我的报名数量
    public int mAppointCount;       //当前预约数量
    public int mBorrowOverdueSum;   //当前借阅逾期数量
    public int mOverdueUnReadSum;   //当前借阅未读逾期数量
    public int mBorrowSum;          //当前借阅数量
    public int mNoteCount;          //读书笔记数量
    public int mBorrowTotal;        //历史借阅数量
    public int mUnreadMsgCount;     //未读消息数量
    public int mUnreadOverdueMsgCount;//未读逾期消息数量
    public long mAttentionLibId;     //关注图书馆ID
    public String mAttentionLibCode;   //关注图书馆libCode
    public String mAttentionLibName;    //关注图书馆名字
}
