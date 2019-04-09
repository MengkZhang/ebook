package com.tzpt.cloundlibrary.manager.ui.contract;

import com.tzpt.cloundlibrary.manager.base.BaseContract;
import com.tzpt.cloundlibrary.manager.bean.BookInfoBean;
import com.tzpt.cloundlibrary.manager.bean.ReaderInfo;

import java.util.List;

/**
 * Created by Administrator on 2017/7/10.
 */

public interface ReturnBookManagementContract {
    interface View extends BaseContract.BaseView {
        void showProgressLoading();

        void dismissProgressLoading();

        void setBookTotalVisibility(int visibility);

        void setReturnDepositBtnText(String string);

        void setBarEditTextHint(String hint);

        void showCancelableDialog(String msg);

        void showCancelableDialog(int msgId);

        void showNoPermissionDialog(int kickOut);

        void showMsgDialog(int msgId);

        void setReaderNameNumber(String info);

        void setNoBackBookSum(String info);

        void setDepositOrPenalty(String info);

        void setTotalInfoDeposit(String size, String totalMoney, String penalty, double underPenalty);

        void setBookListVisibility(int visibility);

        void refreshBookList(List<BookInfoBean> list);

        void showDialogGetReaderInfoFailed(String msg);

        void exitReturnBook();

        void turnToDealPenalty(String readerId);

        void turnToScanActivity(ReaderInfo readerInfo);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void returnBook(String barNumber);

        void clearBookList();

        void delOrderNumber();

        void getReturnBookList(ReaderInfo readerInfo);

        void clickRightBtn();

        void refreshReaderInfo();

        void clickScanBtn();
    }
}
