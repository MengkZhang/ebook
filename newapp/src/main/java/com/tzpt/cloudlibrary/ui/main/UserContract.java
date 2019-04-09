package com.tzpt.cloudlibrary.ui.main;

import com.tzpt.cloudlibrary.base.BaseContract;

/**
 * 用户信息
 * Created by ZhiqiangJia on 2017-08-18.
 */
public interface UserContract {

    interface View extends BaseContract.BaseView {

        void setRefreshUserInfo();

        void setUserNickName(String readerNickName);

        void setUserHeadImage(String headImage, boolean isMan);

        void setUserPhone(String phone);

        /**
         * 未读消息数量(包含未读消息数量,未读逾期消息数量)
         *
         * @param unreadMsgCount
         */
        void setUserUnreadMsgCount(int unreadMsgCount);

        void setUserBorrowOverdueSum(int borrowOverdueSum);

        void setNetError();

        void pleaseLoginTip();

        void setUserBorrowOverdueMsg(int borrowOverdueSum);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        /**
         * 是否登录
         *
         * @return true登录 false未登录
         */
        boolean isLogin();

        /**
         * 获取用户信息
         */
        void getUserInfo();

        /**
         * 获取本地缓存用户信息
         */
        void getLocalUserInfo();

        void quit();

    }

}
