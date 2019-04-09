package com.tzpt.cloudlibrary.ui.account;

import com.tzpt.cloudlibrary.base.BaseContract;

/**
 * 读友会未注册读者登记
 * Created by ZhiqiangJia on 2017-10-10.
 */

public interface VisitorRegistrationContract {

    interface View extends BaseContract.BaseView {
        void registerSuccess();

        void showToastMsg(String msg);

        void showToastMsg(int resId);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void startVisitorRegistration(int actionId, String name, String idCard, String phone);
    }
}
