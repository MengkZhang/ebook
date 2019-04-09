package com.tzpt.cloudlibrary.ui.account.interaction;

import com.tzpt.cloudlibrary.base.BaseContract;

/**
 * Created by Administrator on 2017/12/14.
 */

public interface InteractionContract {
    interface View extends BaseContract.BaseView {
        void setRegisterCount(int count);

        void setMsgCount(int count);

        void setReservationCount(int count);

    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getUserInfo();

        boolean isLogin();
    }
}
