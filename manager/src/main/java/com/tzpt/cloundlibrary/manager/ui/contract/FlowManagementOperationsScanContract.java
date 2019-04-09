package com.tzpt.cloundlibrary.manager.ui.contract;

import com.tzpt.cloundlibrary.manager.base.BaseContract;

/**
 * Created by Administrator on 2017/7/15.
 */

public interface FlowManagementOperationsScanContract {
    interface View extends BaseContract.BaseView {
        void setHeaderLibraryInfo(StringBuilder info);

        void setHeaderUserInfo(StringBuilder info);

        void setBookNumber(String info);

        void setBookPrice(String info);

        void setScanTips(String smg);

        void setScanTips(int msgId);

        void noPermissionPrompt(int kickedOffline);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getHeadInfo();

        void getBookList();

        void operationFlowManageEditValueByFromType(String allBarNumber, int fromType);
    }
}
