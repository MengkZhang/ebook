package com.tzpt.cloundlibrary.manager.ui.contract;

import com.tzpt.cloundlibrary.manager.base.BaseContract;
import com.tzpt.cloundlibrary.manager.bean.ReaderInfo;

/**
 * Created by Administrator on 2018/12/28.
 */

public interface ReturnBookManagementScanContract {
    interface View extends BaseContract.BaseView {
        void showCancelableDialog(int msgId);

        void showNoPermissionDialog(int kickOut);

        void showMsgDialog(int msgId);

        void showDialogGetReaderInfoFailed(int msgId);

        void setReaderNameNumber(String info);

        void setNoBackBookSum(String info);

        void setDepositOrPenalty(String info);

        void setTotalInfoDeposit(String size, String totalMoney, String penalty, double underPenalty);

        void returnBookSuccess(ReaderInfo readerInfo);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void getReturnBookList(ReaderInfo readerInfo);

        void returnBook(String barNumber, String readerId);

        void refreshReaderInfo();
    }
}
