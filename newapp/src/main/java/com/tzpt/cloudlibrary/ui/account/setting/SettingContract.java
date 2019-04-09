package com.tzpt.cloudlibrary.ui.account.setting;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.bean.AppVersionBean;

/**
 * Created by Administrator on 2018/1/4.
 */

public interface SettingContract {
    interface View extends BaseContract.BaseView {
        void showLoading();

        void dismissLoading();

        void showDialogTip(int resId);

        void setAppVersionInfo(AppVersionBean bean);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void checkVersionInfo();

        boolean isLogin();
    }
}
