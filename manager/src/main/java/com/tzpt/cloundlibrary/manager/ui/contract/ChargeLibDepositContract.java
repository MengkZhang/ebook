package com.tzpt.cloundlibrary.manager.ui.contract;

import com.tzpt.cloundlibrary.manager.base.BaseContract;

/**
 * Created by Administrator on 2018/12/18.
 */

public interface ChargeLibDepositContract {
    interface View extends BaseContract.BaseView {
        void showProgressLoading();

        void dismissProgressLoading();

        void setNoLoginPermission(int msgId);

        void showDialogGetReaderInfoFailed(int msgId);

        void setReaderNameNumber(String info);

        void setBorrowableSum(String info);

        void setDepositOrPenalty(String info);

        void setChargeDepositSuccess();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getReaderInfo(String readerId);

        void chargeLibDeposit(String readerId, double money);
    }
}
