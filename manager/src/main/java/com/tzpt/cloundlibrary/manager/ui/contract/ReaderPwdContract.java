package com.tzpt.cloundlibrary.manager.ui.contract;

import com.tzpt.cloundlibrary.manager.base.BaseContract;

/**
 * Created by Administrator on 2017/7/12.
 */

public interface ReaderPwdContract {
    interface View extends BaseContract.BaseView {
        void setReaderName(String name);

        void setReaderIdCard(String idCard);

        void setReaderGender(String gender);

        void setReaderHead(String headImg);

        void setReaderPhone(String phone);

        void showDialogGetInfoFailedRetry(String msg);

        void sendVerifyMessageCode(boolean state, String msg);

        void showDialogTelUnBundle(String bundleIdCard);

        void showToastMsg(String msg);

        void showDialogModifySuccess(int msgId);

        void showDialogModifyFailed(int msgId);

        void showDialogNoPermission(int msgId);

        void showLoadingDialog(String tips);

        void dismissLoadingDialog();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getReaderInfo(String readerId);

        void getCode(String telNum, int isContinue);

        void modifyInfo(String pwd, String surePwd, String phone, String code);
    }
}
