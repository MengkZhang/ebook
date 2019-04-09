package com.tzpt.cloudlibrary.ui.account.deposit;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.bean.UserLibraryDepositBean;

import java.util.List;

/**
 * 馆押金明细
 * Created by ZhiqiangJia on 2017-08-24.
 */
public interface UserLibraryDepositContract {

    interface View extends BaseContract.BaseView {

        void setNetError();

        void setLibraryDepositList(List<UserLibraryDepositBean> libraryDepositBeanList, int totalCount, boolean refresh);

        void setLibraryDepositListEmpty(boolean refresh);

        void pleaseLoginTip();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void getUserLibraryDepositList(int pageNum);
    }
}
