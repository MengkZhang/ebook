package com.tzpt.cloundlibrary.manager.ui.contract;

import com.tzpt.cloundlibrary.manager.base.BaseContract;
import com.tzpt.cloundlibrary.manager.bean.LibraryDepositDetailBean;

import java.util.List;

/**
 * 本馆押金明细
 * Created by ZhiqiangJia on 2017-10-24.
 */
public interface LibraryDepositDetailContract {

    interface View extends BaseContract.BaseView {

        void setLibraryDepositDetailList(List<LibraryDepositDetailBean> libraryDepositDetailList, int totalCount, boolean refresh);

        void setLibraryDepositDetailListEmpty(boolean refresh);

        void setLibraryTotalDeposit(String totalDeposit);

        void setNoPermissionDialog(int kickedOffline);

        void showNetError(boolean refresh);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void getLibraryDepositDetailList(int pageNum);

    }
}
