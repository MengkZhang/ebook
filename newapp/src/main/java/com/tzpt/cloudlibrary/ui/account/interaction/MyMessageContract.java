package com.tzpt.cloudlibrary.ui.account.interaction;

import com.tzpt.cloudlibrary.base.BaseContract;

/**
 * 我的消息
 * Created by ZhiqiangJia on 2018-01-18.
 */
public interface MyMessageContract {

    interface View extends BaseContract.BaseView {
        void setNormalAndOverdueMsgCount(int normalMsgCount, int overdueMsgCount);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getUserUnreadMsgCount();
    }
}
