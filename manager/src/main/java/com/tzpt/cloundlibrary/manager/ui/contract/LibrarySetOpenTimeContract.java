package com.tzpt.cloundlibrary.manager.ui.contract;

import com.tzpt.cloundlibrary.manager.base.BaseContract;
import com.tzpt.cloundlibrary.manager.bean.SearchAddressBean;

import java.util.List;

/**
 * Created by Administrator on 2017/7/16.
 */

public interface LibrarySetOpenTimeContract {
    interface View extends BaseContract.BaseView {
        void setTitle(String info);

        void setLibraryName(String name);

        void setNowTime(long nowTime);

        void setContactTel(String telNum);

        void setTodayAmAvailable(boolean isAm);

        void setWeekInfo(Integer[] week);

        void setLongAMOpenTimeStart(String time);

        void setLongAMOpenTimeEnd(String time);

        void setLongPMTimeStart(String time);

        void setLongPMTimeEnd(String time);

        void setTodayAMOpenTimeStart(String time);

        void setTodayAMOpenTimeEnd(String time);

        void setTodayPMOpenTimeStart(String time);

        void setTodayPMOpenTimeEnd(String time);

        void showDialogMsg(String msg);

        void showDialogMsg(int msgId);

        void showLoading();

        void hideLoading();

        void showProgressLoading();

        void hideProgressLoading();

        void showDialogSetSuccess(int msgId);

        void setLocationAddress(String content);

        void setLocationAddressNumber(String number);

        void showVerifyPasswordDialog();

        void showError(int msgId);

        void noPermissionPrompt(int kickedOffline);

        void showTimeoutDialog();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void verifyOpenTimePhoneNumber(String phoneNumber, String todayAMStart, String todayAMEnd, String todayPMStart, String todayPMEnd,
                                       String regularAMStart, String regularAMEnd);

        void getLibraryInfo();

        void getLibraryOpenTimeInfo();

        void setSearchAddressBean(SearchAddressBean bean);

        void setLightSelect(String pwd, String phone, String todayAMStart, String todayAMEnd, String todayPMStart, String todayPMEnd,
                            String regularAMStart, String regularAMEnd, String regularPMStart, String regularPMEnd, List<String> weeks,
                            String houseNumber);

    }
}
