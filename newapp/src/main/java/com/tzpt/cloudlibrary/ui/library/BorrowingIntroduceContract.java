package com.tzpt.cloudlibrary.ui.library;

import com.tzpt.cloudlibrary.base.BaseContract;

/**
 * 图书馆借阅须知
 * Created by tonyjia on 2018/8/27.
 */
public interface BorrowingIntroduceContract {

    interface View extends BaseContract.BaseView {
        void showProgress();

        void setNetError();

        void setLibIntroduce(String url);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void getLibIntroduce(String libCode);

    }
}
