package com.tzpt.cloudlibrary.ui.account;

import com.tzpt.cloudlibrary.base.BaseContract;

/**
 * 更新昵称
 * Created by tonyjia on 2018/11/6.
 */
public interface UserNickNameContract {

    interface View extends BaseContract.BaseView {

        void showProgressDialog();

        void dismissProgressDialog();

        void updateUserNickNameSuccess(String nickName);

        void showMsgTipsDialog(int msgId);

        void pleaseLoginTips();

    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void saveUserNickName(String nickName);
    }
}
