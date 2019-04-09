package com.tzpt.cloudlibrary.ui.library;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.bean.LightLibraryOpenTimeInfo;
import com.tzpt.cloudlibrary.business_bean.LibraryBean;

/**
 * Created by Administrator on 2017/11/8.
 */

public interface LibraryIntroduceContract {
    interface View extends BaseContract.BaseView {

        void showProgressDialog();

        void dismissProgressDialog();

        void showErrorMsg(int msgId);

        void showLibraryInfo(LibraryBean library);

        void setLibraryTodayTime(String status, String startTime, String endTime);

        void setLibraryLongTime(String startTime, String endTime);

        void setWeekInfo(String weekInfo);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void setLibraryCode(String libCode, int fromSearch);

        void getLibraryInfo();
    }

}
