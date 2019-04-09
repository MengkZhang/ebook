package com.tzpt.cloudlibrary.ui.map;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.bean.SwitchCityBean;

import java.util.List;

/**
 * 切换城市
 * Created by ZhiqiangJia on 2017-09-07.
 */
public interface SwitchCityContract {


    interface View extends BaseContract.BaseView {
        /**
         * 设置界面数据状态，清空数据展示loading状态
         *
         * @param type  当前展示行政类型
         * @param title 标题文字
         */
        void changeDataList(int type, String title);

        /**
         * 展示数据
         *
         * @param dataList 行政单位列表
         */
        void setDataList(List<SwitchCityBean> dataList);

        void setErrorMsg(int msgId);

        /**
         * 正在定位中
         */
        void setGPSLoadingStatus();

        /**
         * GPS信号弱提示
         */
        void setGPSLowStatus();

        /**
         * 没有定位权限提示
         */
        void setNoLocationPermissionStatus();

        /**
         * 定位成功，显示位置信息
         *
         * @param info 省市区信息
         */
        void setLocationInfo(String info);

        void finishActivity();

        /**
         * 显示获取定位权限对话框
         */
        void showLocationPermission();


    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void getProvince();

        void getCity(String provinceName, String provinceCode);

        void getCity();

        void getDistrict(String cityName, String cityCode);

        void getDistrict();

        void getLastDistrict();

        void getLastDistrict(boolean isLibAdvancedSearchArea, String lastAreaTitle, String adCode);

        void clearTempCityData();

        void dealSelectedArea(String area, String adCode, int level);

        String getLastAreaTitle(String area, int level);

        /**
         * 获取定位信息
         */
        void getLocationInfo();

        void startLocation();

        void selectLocationArea();

    }
}
