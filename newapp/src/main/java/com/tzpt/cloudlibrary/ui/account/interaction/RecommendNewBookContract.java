package com.tzpt.cloudlibrary.ui.account.interaction;

import com.tzpt.cloudlibrary.base.BaseContract;

/**
 * 推荐新书
 */
public interface RecommendNewBookContract {

    interface View extends BaseContract.BaseView {

        void showProgressDialog();

        void dismissProgressDialog();

        void showErrorMsg(int msgId);

        void recommendBookSuccess();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void requestForRecommendBook(String isbn);
    }

}
