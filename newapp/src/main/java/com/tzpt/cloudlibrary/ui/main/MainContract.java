package com.tzpt.cloudlibrary.ui.main;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.bean.AppVersionBean;

/**
 * Created by Administrator on 2017/12/10.
 */

public interface MainContract {
    interface View extends BaseContract.BaseView {
        void setUserRbStatus(int msgCount);

        void setAppVersionInfo(AppVersionBean bean);

        /**
         * 提示用户即将逾期消息
         *
         * @param borrowOverdueSum 逾期书籍
         */
        void setUserBorrowOverdueMsg(int borrowOverdueSum);

        void showAttentionLib(boolean isShow);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void checkAttentionLib();

        void checkVersionInfo();

        void getUserInfo();

        void getLocalInfo();

        void handleShortCutBadger(int count);

    }
}
