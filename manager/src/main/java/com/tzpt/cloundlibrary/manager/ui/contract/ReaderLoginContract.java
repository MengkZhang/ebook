package com.tzpt.cloundlibrary.manager.ui.contract;

import com.tzpt.cloundlibrary.manager.base.BaseContract;
import com.tzpt.cloundlibrary.manager.bean.IDCardBean;

/**
 * 读者登录
 * Created by Administrator on 2017/7/6.
 */
public interface ReaderLoginContract {
    interface View extends BaseContract.BaseView {

        void showToastMsg(String msg);

        void showLoading(String msg);

        void dismissLoading();

        void readerLoginSuccess(String readerId);

        void readerLoginFailed(String msg);

        void readerLoginFailed(int msgId);

        void noPermissionDialog(int msgId);

        void noPermissionPrompt(int msgId);

        void showDialogForFinish(int msgId);

        void turnToDealPenalty(String readerId);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void readerLogin(String account, String pwd, boolean dealPenalty);
    }
}
