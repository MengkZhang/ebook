package com.tzpt.cloundlibrary.manager.ui.contract;

import com.tzpt.cloundlibrary.manager.base.BaseContract;

/**
 * Created by Administrator on 2017/7/3.
 */

public interface OperatorPwdContract {
    interface View extends BaseContract.BaseView {
        void changePwdSuccess(String msg);

        void changePwdFailed(String msg);

        void changePwdFailed(int msgId);

        void noPermissionPrompt(int msgId);

        void showLoadingProgress(String msg);

        void hideLoadingProgress();

        void setOperatorName(String name);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getOperatorName();

        void changeOperatorPwd(String oldPwd, String newPwd, String sureNewPwd);
    }
}
