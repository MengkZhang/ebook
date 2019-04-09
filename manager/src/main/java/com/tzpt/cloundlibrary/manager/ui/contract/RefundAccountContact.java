package com.tzpt.cloundlibrary.manager.ui.contract;

import com.tzpt.cloundlibrary.manager.base.BaseContract;

/**
 * Created by Administrator on 2018/5/23.
 */

public interface RefundAccountContact {
    interface View extends BaseContract.BaseView {
        void sendVerifyMessageCode(boolean state, String msg);

        void showToastMsg(String msg);

        void showLoading(String msg);

        void dismissLoading();

        void noPermissionPrompt(int kickedOffline);

        void showFailedDialog(int msgId);

        void showSuccessDialog();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getCode();

        void changeRefundAccount(String code, String refundAccount, String refundName);
    }
}
