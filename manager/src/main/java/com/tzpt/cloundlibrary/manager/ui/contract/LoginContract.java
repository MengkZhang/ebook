package com.tzpt.cloundlibrary.manager.ui.contract;

import android.content.Context;

import com.tzpt.cloundlibrary.manager.base.BaseContract;
import com.tzpt.cloundlibrary.manager.bean.LibraryInfo;
import com.tzpt.cloundlibrary.manager.bean.UpdateAppBean;

/**
 * Created by Administrator on 2017/6/21.
 */

public interface LoginContract {
    interface View extends BaseContract.BaseView {
        void loginSuccess();

        void loginFailed(String msg);

        void loginFailed(int msgId);

        void showLoginInfo(LibraryInfo info);

        void showLoadingDialog(String tips);

        void dismissLoadingDialog();

        void showUpdateAppInfo(UpdateAppBean updateAppBean);

        void setFirstLoginOperatorPswTip();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getLoginInfo();

        void login(String libName, String userName, String pwd);

        void updateAppInfo(Context context);

        void download(Context context,String url);
    }
}
