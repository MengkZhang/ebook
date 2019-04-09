package com.tzpt.cloudlibrary.ui.account.setting;

import com.tzpt.cloudlibrary.base.BaseContract;

/**
 * Created by ZhiqiangJia on 2017-08-24.
 */

public interface ChangePasswordContract {

    interface View extends BaseContract.BaseView {

        void showProgressDialog();

        void dismissProgressDialog();

        void showToastMsg(int msgId);

        void changePasswordSuccess();

        void pleaseLoginTip();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void changePsw(String oldPassword, String newPassword, String repeatPsw);
    }
}
