package com.tzpt.cloundlibrary.manager.ui.contract;

import com.tzpt.cloundlibrary.manager.base.BaseContract;
import com.tzpt.cloundlibrary.manager.bean.ReaderInfo;

/**
 * 借书扫描提示
 * Created by Administrator on 2017/7/13.
 */
public interface BorrowBookManagementScanContract {
    interface View extends BaseContract.BaseView {
        void setReaderNameNumber(String info);

        void setBorrowableSum(String info);

        void setDepositOrPenalty(String info);

        void setTotalInfo(String sumInfo, String moneyInfo);

        void setTotalInfoDeposit(String sumInfo, String moneyInfo, String depositMoneyInfo);

        void enteringBookTips(int msgId);

        void setNoLoginPermission(int kickedOffline);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getReaderInfo(ReaderInfo readerInfo);

        void getBookInfo(String barNumber, ReaderInfo readerInfo);
    }
}
