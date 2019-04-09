package com.tzpt.cloudlibrary.ui.account;

import com.tzpt.cloudlibrary.base.BaseContract;

/**
 * 用户注册
 * Created by JiaZhiqiang on 2018/2/27.
 */
public interface RegisterContract {

    interface View extends BaseContract.BaseView {

        void registerSuccess();

        void showMsgDialog(int msgId);

        void showProgressDialog(String tips);

        void dismissProgressDialog();

        void setSendCodeStyle(int type);

        void setSendCodeTime(Long time);

        void verifyMsgCodeSuccess();

        void checkIdCardSuccess();

        void loginSuccess();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void sendPhoneVerifyCode(String phone);

        void verifyMsgCode(String phone, String code);

        void checkIdCardAvailable(String idCard, String readerName, String nickName);

        void register(String phone, String idCard, String readerName, String psw, String rePsw, String nickName);

        void startLogin();
    }
}
