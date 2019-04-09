package com.tzpt.cloundlibrary.manager.ui.contract;

import com.tzpt.cloundlibrary.manager.base.BaseContract;

/**
 * 用户
 * Created by Administrator on 2017/7/27.
 */
public interface UserContract {
    interface View extends BaseContract.BaseView {
        void setMsgNoReadCount(int count);

        void noPermissionPrompt(int kickedOffline);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        boolean checkDepositPermission();

        boolean checkPswManagePermission();

        void getMsgNoReadCount();

        void delLibraryInfo();
    }
}
