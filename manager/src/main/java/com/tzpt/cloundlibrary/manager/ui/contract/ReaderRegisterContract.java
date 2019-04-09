package com.tzpt.cloundlibrary.manager.ui.contract;

import android.graphics.Bitmap;

import com.tzpt.cloundlibrary.manager.base.BaseContract;

/**
 * 读者注册界面
 * Created by Administrator on 2017/7/6.
 */

public interface ReaderRegisterContract {
    interface View extends BaseContract.BaseView {
        void sendVerifyMessageCode(boolean state, String msg);

        void showDialogTelUnBundle(String bundleIdCard);

        void registerSuccess(String readerId);

        void showDialogRegisterFailed(int msgId);

        void showToastMsg(String msg);

        void showLoading(String msg);

        void dismissLoading();

        void noPermissionPrompt(int kickedOffline);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getCode(String telNum, int isContinue);

        void register(String number, String code, String name, Bitmap headImage, String pwd,
                      String surePwd, String telNum, String gender, String bundleTel);
    }
}
