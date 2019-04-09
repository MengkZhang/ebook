package com.tzpt.cloudlibrary.ui.account;

/**
 * 刷新账户信息传递消息-Event bus 传递消息
 * Created by ZhiqiangJia on 2017-08-22.
 */

public class AccountMessage {
    public boolean mIsLoginOut = false;
    public boolean mIsToUserCenter = false;
    public boolean success = false;
    public boolean mIsRefreshUserInfo = false;
//    public int mMsgCount;
//    public int mUnreadNormalMsgCount;
//    public int mUnreadOverdueMsgCount;
}
