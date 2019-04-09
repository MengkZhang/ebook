package com.tzpt.cloudlibrary.ui.account;

import com.tzpt.cloudlibrary.base.BaseContract;

/**
 * 忘记密码
 * Created by ZhiqiangJia on 2017-08-30.
 */

public interface ForgotPasswordContract {

    interface View extends BaseContract.BaseView {

        void showProgressDialog();

        void dismissProgressDialog();

        void showToastMsg(String msg);

        void showToastMsg(int resId);

        void idCardBindingPhoneNumber(String phone);

        /**
         * 身份证未绑定手机号
         * @param message
         */
        void idCardNotBindingPhoneNumber();

        /**
         * 身份证未注册
         */
        void idCardNotRegister();

        void setSendCodeStyle(int type);

        void setSendCodeTime(long time);

        void setVerifyCodeSuccess();

        void resetPswSuccess();

        void setIdCard(String idCard);

        void pleaseLoginTip();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        /**
         * 获取身份证号码
         */
        void getIDCard();

        /**
         * 检查身份证是否绑定手机号
         *
         * @param idCard 身份证号码
         */
        void checkPhoneIsBinding(String idCard);

        /**
         * 获取验证码
         *
         * @param phone 绑定的手机号
         */
        void getVerifyCode(String phone);

        /**
         * 验证验证码
         *
         * @param phone 绑定的手机号
         * @param code  验证码
         */
        void verifyCode(String phone, String code);

        /**
         * 重置密码
         *
         * @param idCard 身份证号码
         * @param psw1   新密码
         * @param psw2   确认新密码
         */
        void resetPsw(String idCard, String psw1, String psw2);

        void unSubscriptionTime();
    }
}
