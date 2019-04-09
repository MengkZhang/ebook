package com.tzpt.cloundlibrary.manager.ui.contract;

import com.tzpt.cloundlibrary.manager.base.BaseContract;

/**
 * Created by Administrator on 2017/7/12.
 */

public interface EntranceGuardScanContract {

    interface View extends BaseContract.BaseView {
        void setReaderName(String name);

        void setBorrowableSum(String sum);

        void setDepositOrPenalty(String info);

        void setEntranceStatePass();

        void setEntranceStateError(int msgId);

        void setEntranceStateLost(String readerInfo, String phone);

        void showMsgDialog(int resId);

        void noPermissionPrompt(int resId);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void entranceCheck(String barNumber);
    }

}
