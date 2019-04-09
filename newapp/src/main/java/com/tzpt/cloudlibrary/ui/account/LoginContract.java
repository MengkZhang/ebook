package com.tzpt.cloudlibrary.ui.account;

import com.tzpt.cloudlibrary.base.BaseContract;

/**
 * 登录
 * Created by ZhiqiangJia on 2017-08-18.
 */
public interface LoginContract {

    interface View extends BaseContract.BaseView {

        void showProgressDialog();

        void dismissProgressDialog();

        void showToastMessage(int msgId);

        void loginSuccess();

        void setLoginCacheInfo(String account);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getLoginCacheInfo();

        void login(String account, String password);
    }

}
