package com.tzpt.cloundlibrary.manager.ui.contract;

import com.tzpt.cloundlibrary.manager.base.BaseContract;

/**
 * Created by Administrator on 2017/6/23.
 */

public interface HomePageContract {
    interface View extends BaseContract.BaseView {

        void setLibraryName(String name, String operatorInfo, boolean isNormal);

        void setNoLoginPermission(int msgId);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void getLoginInfo();

        void delLibraryInfo();

        boolean checkPermission(int checkType);
    }
}
