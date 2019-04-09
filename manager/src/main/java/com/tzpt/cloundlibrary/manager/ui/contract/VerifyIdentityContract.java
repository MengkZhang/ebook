package com.tzpt.cloundlibrary.manager.ui.contract;

import com.tzpt.cloundlibrary.manager.base.BaseContract;

/**
 * Created by Administrator on 2018/9/26.
 */

public interface VerifyIdentityContract {
    interface View extends BaseContract.BaseView {

        void showDialogNoPermission(int msgId);

        void showToastMsg(String msg);

        void sendVerifyMessageCode(boolean state, String msg);

        void showDialogVerifyFailed(int msgId);

        void verifySuccess(String hallCode, String userName, int id);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void sendCode(String hallCode, String phone);

        void verifyIdentity(String code, String hallCode, String phone);
    }
}
