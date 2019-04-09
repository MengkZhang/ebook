package com.tzpt.cloudlibrary.ui.account.interaction;

import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.modle.UserRepository;

/**
 * 互动消息
 * Created by Administrator on 2017/12/14.
 */
public class InteractionPresenter extends RxPresenter<InteractionContract.View>
        implements InteractionContract.Presenter {

    @Override
    public void getUserInfo() {
        if (isLogin()) {
            //我的报名数量
            int userActionCount = UserRepository.getInstance().getUserActionCount();
            mView.setRegisterCount(userActionCount);
            //设置互动消息总数量
            int interactionMsgCount = UserRepository.getInstance().getInteractionMsgCount();
            mView.setMsgCount(interactionMsgCount);
            //我的预约数量
            int appointCount = UserRepository.getInstance().getUserAppointCount();
            mView.setReservationCount(appointCount);
        }
    }

    @Override
    public boolean isLogin() {
        return UserRepository.getInstance().isLogin();
    }

}
