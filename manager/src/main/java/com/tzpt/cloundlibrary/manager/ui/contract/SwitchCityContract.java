package com.tzpt.cloundlibrary.manager.ui.contract;


import com.tzpt.cloundlibrary.manager.base.BaseContract;
import com.tzpt.cloundlibrary.manager.bean.LocationBean;
import com.tzpt.cloundlibrary.manager.bean.SwitchCityBean;

import java.util.List;

/**
 * 切换城市
 * Created by ZhiqiangJia on 2017-09-07.
 */
public interface SwitchCityContract {


    interface View extends BaseContract.BaseView {

        void setCityList(List<SwitchCityBean> switchProvinceList, int type, String supName);

        void setCityListEmpty();

        void setErrorMsg(int msgId);

        void setLocationAddressSuccess(LocationBean bean);

        void setLocationFailed(int msgId);

        void showDialogForPermissions(int msgId);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void getCurrentLocation();

        void getProvince();

        void getCity(String provinceCode, String provinceName);

        void getDistrict(String city, int type);

        void clearTempCityData();
    }
}
