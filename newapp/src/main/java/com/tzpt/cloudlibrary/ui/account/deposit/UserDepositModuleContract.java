package com.tzpt.cloudlibrary.ui.account.deposit;

import com.tzpt.cloudlibrary.base.BaseContract;

/**
 * Created by Administrator on 2017/11/18.
 */

public interface UserDepositModuleContract {
    interface View extends BaseContract.BaseView {
//        void showProgressLoading();

//        void dismissProgressLoading();

//        void showDialogTip(int resId, boolean finish);

//        void showUserDeposit(String usableDeposit, String occupyDeposit);

//        void pleaseLoginTip();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
       // void getUserDeposit();

        //boolean getChargeable();

        String getReaderId();
    }
}
