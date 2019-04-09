package com.tzpt.cloudlibrary.ui.account.interaction;

import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.modle.UserRepository;

/**
 * 我的消息
 * Created by ZhiqiangJia on 2018-01-18.
 */
public class MyMessagePresenter extends RxPresenter<MyMessageContract.View> implements
        MyMessageContract.Presenter {

    @Override
    public void getUserUnreadMsgCount() {
        //获取未读消息数量，未读逾期消息数量
        int unReadMsgCount = UserRepository.getInstance().getUserUnreadMsgCount();
        int unReadOverdueMsgCount = UserRepository.getInstance().getUserUnreadOverdueMsgCount();
        mView.setNormalAndOverdueMsgCount(unReadMsgCount, unReadOverdueMsgCount);

    }
}
