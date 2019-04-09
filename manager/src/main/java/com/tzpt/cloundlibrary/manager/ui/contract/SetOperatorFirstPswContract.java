package com.tzpt.cloundlibrary.manager.ui.contract;

import com.tzpt.cloundlibrary.manager.base.BaseContract;

/**
 * 操作员首次登录修改密码
 * Created by tonyjia on 2018/12/11.
 */
public interface SetOperatorFirstPswContract {

    interface View extends BaseContract.BaseView {

        void changePwdSuccess();

        void changePwdFailed(int resId);

        void showLoadingProgress();

        void hideLoadingProgress();

        void pleaseLoginTip(int resId);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void saveOperatorPwd(String newPsw, String reNewPsw);

        void delOperatorToken();

    }
}
