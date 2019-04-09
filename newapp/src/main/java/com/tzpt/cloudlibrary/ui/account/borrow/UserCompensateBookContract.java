package com.tzpt.cloudlibrary.ui.account.borrow;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.business_bean.BorrowBookBean;

/**
 * Created by tonyjia on 2018/5/25.
 */
public interface UserCompensateBookContract {

    interface View extends BaseContract.BaseView {

        void showProgressDialog();

        void postingProgressDialog();

        void dismissProgressDialog();

        void showNetError();

        void showErrorMsg(int msgId);

        void showErrorMsgFinish(int msgId);

        void setContentView();

        void setLostBookInfo(BorrowBookBean bean);

        void setDepositInfo(String canUseDeposit, String takeDeposit);

        void setCompensatePrice(String compensatePrice);

        void pleaseLoginTip();

        void compensateBooksSuccess();

        void setCompensateBookUI();

        void setChargeDepositUI(double price);

        void setPlatformDepositUI();

        void setLibraryDepositUI();

    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        /**
         * 获取书籍信息
         *
         * @param borrowId 借阅ID
         */
        void getBookInfo(long borrowId);

        void getUserDepositInfo(boolean firstLoading);

        /**
         * 赔书
         *
         * @param password 密码
         * @param borrowId 借阅ID
         */
        void compensateBook(String password, long borrowId);

    }
}
