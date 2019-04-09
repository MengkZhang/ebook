package com.tzpt.cloudlibrary.ui.account.borrow;

import com.tzpt.cloudlibrary.base.BaseContract;

/**
 * Created by Administrator on 2017/12/14.
 */

public interface BorrowContract {
    interface View extends BaseContract.BaseView {
        void setAppointCount(int count);

        void setCurrentBorrowCountAndOverdueCount(int count, int overdueCount);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getUserInfo();

        void getLocalUserInfo();
    }
}
