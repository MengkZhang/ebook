package com.tzpt.cloudlibrary.ui.account;

import com.tzpt.cloudlibrary.base.BaseContract;

/**
 * 更改手机号 -0 更改手机号码 1 绑定手机号码
 * Created by ZhiqiangJia on 2017-08-30.
 */
public interface ChangePhoneNumberContract {

    interface View extends BaseContract.BaseView {

        void showProgressDialog();

        void dismissProgressDialog();

        void showToastMsg(int msgId);

        void showToastMsg(String msg);

        void setPhoneEditAble(boolean able);

        void setSendCodeStyle(int type);

        void setSendCodeTime(long time);

        void sendVerifyMessageCode(boolean state);

        void changePhoneSuccess();

        void setOldTelNum(String tel);

        void pleaseLoginTip();

        void setBindingPhoneTips(String idCard, String phone);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getLoginInfo();

        void setFromType(int fromType);

        void sendVerifyCode(String oldPhone, String newPhone, String bindingPhone);

        void submitVerifyCodeAndChangePhoneNumber(String newPhone, String bindingPhone, String code);

        void sendVerifyCodeByPhoneNumber(String phone);
    }
}
