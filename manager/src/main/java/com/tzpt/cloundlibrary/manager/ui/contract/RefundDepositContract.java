package com.tzpt.cloundlibrary.manager.ui.contract;

import com.tzpt.cloundlibrary.manager.base.BaseContract;
import com.tzpt.cloundlibrary.manager.bean.BookInfoBean;

import java.util.List;

/**
 * 退押金
 * Created by Administrator on 2017/7/11.
 */
public interface RefundDepositContract {
    interface View extends BaseContract.BaseView {
        void showLoading();

        void dismissLoading();

        void setReaderNameNumber(String info);

        void setBorrowableSum(String info);

        void setDepositOrPenalty(String info);

        void setRefundOperatorVisibility(boolean visibility, String tips);

        void setTotalInfo(String sumInfo, String moneyInfo);

        void setTotalInfoVisibility(int visibility);

        void refreshBookList(List<BookInfoBean> list);

        void showMsgNoCancelable(String msg);

        void showMsgNoCancelable(int msgId);

        void showRefundDepositSuccess(String msg);

        void noPermissionDialog(int kickedOffline);

        void showMsgNoCancelableFinish(int msgId);

        void showMsgNoCancelableFinish(String msg);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getBorrowBookList(String readerId);

        void refundDeposit(String money, String pwd);
    }
}
