package com.tzpt.cloundlibrary.manager.ui.contract;

import com.tzpt.cloundlibrary.manager.base.BaseContract;
import com.tzpt.cloundlibrary.manager.bean.BookInfoBean;
import com.tzpt.cloundlibrary.manager.bean.ReaderInfo;

import java.util.List;

/**
 * Created by Administrator on 2017/7/7.
 */

public interface BorrowBookManagementContract {

    interface View extends BaseContract.BaseView {

        void showProgressLoading();

        void dismissProgressLoading();

        void setReaderNameNumber(String info);

        void setBorrowableSum(String info);

        void setDepositOrPenalty(String info);

        void showDialogGetReaderInfoFailed(int msgId);

        void showCancelableDialog(int msgId);

        void setBook(BookInfoBean book);

        void setTotalInfo(String sumInfo, String moneyInfo);

        void setTotalInfoDeposit(String sumInfo, String moneyInfo, String depositMoneyInfo);

        void showToastTip(int msgId);

        void setBookListVisibility(int visibility);

        void showDepositDeficiencyDialog();

        void showBookLockedDialog(String msg);

        void refreshBookList(List<BookInfoBean> list);

        void clearBookList();

        void borrowBookSuccess();

        void setNoLoginPermission(int kickedOffline);

        void showOverdueNumTipDialog();

        void turnToScanActivity(ReaderInfo readerInfo);
    }


    interface Presenter extends BaseContract.BasePresenter<View> {
//        void getLoginLibraryInfo();

//        void getOrderCode(String readerId);

        void getReaderInfo(String number);

        void getBookInfo(String barNumber);

        void removeBook(BookInfoBean book);

        void clearBookList();

        void delReaderInfo();

        void delOrderNumber();

        void borrowBook();

        void getBookListFromScan();

        void clickScanBtn();
    }
}
