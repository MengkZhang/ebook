package com.tzpt.cloundlibrary.manager.ui.contract;

import com.tzpt.cloundlibrary.manager.base.BaseContract;

/**
 * 本馆押金
 * Created by ZhiqiangJia on 2017-11-21.
 */
public interface LibraryDepositContract {

    interface View extends BaseContract.BaseView {

        void setNoPermissionDialog(int kickOut);

        void setErrorDialog(int msgId);

        void showProgressDialog();

        void dismissProgressDialog();

        void setAvailableBalance(String availableBalance, boolean showRefundBtn);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void getLibraryDeposit();

    }
}
