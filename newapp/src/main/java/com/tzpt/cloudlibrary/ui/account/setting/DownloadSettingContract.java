package com.tzpt.cloudlibrary.ui.account.setting;

import com.tzpt.cloudlibrary.base.BaseContract;

public interface DownloadSettingContract {
    interface View extends BaseContract.BaseView {
        void setCheckBoxStatus(boolean isMobileNetRequire);

        void showDialog(int msgId, int okId, int cancelId);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        /**
         *
         */
        void checkMobileNetAble();

        /**
         * 是否允许运营商网络下载
         */
        void setMobileNetAble(boolean isAble);

        void changeMobileNetAble();
    }
}
