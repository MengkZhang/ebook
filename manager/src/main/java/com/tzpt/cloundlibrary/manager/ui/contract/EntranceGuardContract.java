package com.tzpt.cloundlibrary.manager.ui.contract;

import com.tzpt.cloundlibrary.manager.base.BaseContract;

/**
 * Created by Administrator on 2017/7/12.
 */

public interface EntranceGuardContract {
    interface View extends BaseContract.BaseView {
        void setCodeEditTextHint(String hint);

        void setReaderName(String name);

        void setBorrowableSum(String sum);

        void setDepositOrPenalty(String info);

        void setEntranceStatePass();

        void setEntranceStateError(int msgId);

        void setEntranceStateLost(String readerInfo, String phone);

        void showDialogMsg(String msg);

        void showDialogMsg(int msgId);

        void noPermissionPrompt(int msgId);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getLoginLibraryInfo();

        void entranceCheck(String barNumber);
    }
}
