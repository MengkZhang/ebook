package com.tzpt.cloundlibrary.manager.ui.contract;

import android.content.Context;

import com.tzpt.cloundlibrary.manager.base.BaseContract;
import com.tzpt.cloundlibrary.manager.bean.UpdateAppBean;

/**
 * Created by Administrator on 2018/9/26.
 */

public interface MainContract {
    interface View extends BaseContract.BaseView {
        void showLoadingDialog();

        void hideLoadingDialog();

        void showDialogTip(String msg);

        void showDialogTip(int msgId);

        void showUpdateAppInfo(UpdateAppBean updateAppBean);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getLoginUserInfo(Context context);
    }
}
