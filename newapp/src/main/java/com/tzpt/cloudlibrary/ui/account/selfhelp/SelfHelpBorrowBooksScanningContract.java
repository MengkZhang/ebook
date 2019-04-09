package com.tzpt.cloudlibrary.ui.account.selfhelp;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.bean.SelfReaderInfoBean;

public interface SelfHelpBorrowBooksScanningContract {

    interface View extends BaseContract.BaseView {

        void updateBookTotalInfo(String sum, String money, String deposit, boolean isShowDeposit);

        void setPenaltyOrDepositInfo(String moneyInfo);

        void setBorrowableBookSum(int count);


    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void checkReaderAndBookInfo();

    }
}
