package com.tzpt.cloundlibrary.manager.ui.contract;

import com.tzpt.cloundlibrary.manager.base.BaseContract;

/**
 * Created by Administrator on 2018/9/26.
 */

public interface SetPwdContract {
    interface View extends BaseContract.BaseView {
//        void noPermissionPrompt(int msgId);

        void showDialogTip(String msg);

        void showDialogTip(int msgId);

        void showLoadingProgress(String msg);

        void hideLoadingProgress();

        void resetPwdSuccess();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void setNewPwd(int id, String newPwd1, String newPwd2);
    }
}
