package com.tzpt.cloundlibrary.manager.ui.contract;

import com.tzpt.cloundlibrary.manager.base.BaseContract;

/**
 * 退押金
 * Created by ZhiqiangJia on 2017-10-24.
 */
public interface RefundPlatformDepositContract {

    interface View extends BaseContract.BaseView {
        void showProgressLoading();

        void dismissProgressLoading();

        void showNoPermissionDialog(int kickOut);

        void setRefundableDeposit(String deposit);

        void showDialogMsg(int msgId, boolean isFinish);

        void showDialogMsg(String msg, boolean isFinish);

        void noAliPayAccount(int msgId);

        void withdrawDepositLimitTip(String limitMoney);

        void showSuccessDialog(int msgId);

        void setRefundAccountTitle(boolean haveRefundAccount);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void requestRefundInfo();

        void submitRefundDeposit(String adminPwd, String deposit, String createIp);
    }

}
