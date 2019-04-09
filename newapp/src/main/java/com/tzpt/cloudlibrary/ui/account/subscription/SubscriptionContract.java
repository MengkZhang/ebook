package com.tzpt.cloudlibrary.ui.account.subscription;

import com.tzpt.cloudlibrary.base.BaseContract;

import rx.functions.Action1;

/**
 * Created by Administrator on 2017/12/21.
 */

public interface SubscriptionContract {
    interface View extends BaseContract.BaseView {
        void setLocalEBookCount(int count);

        void setCurrentBorrowCountAndOverdueCount(int count, int overdueCount);

        void setBuyBookShelfCount(int count);

        void setVideoShelfCount(int count);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getLocalEBookCount();

        void getLocalVideoCount();

        void getLocalUserInfo();
    }
}
