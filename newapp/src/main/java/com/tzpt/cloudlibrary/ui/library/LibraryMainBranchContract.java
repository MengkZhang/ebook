package com.tzpt.cloudlibrary.ui.library;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.bean.LibraryMainBranchBean;

import java.util.List;

/**
 * 总分馆
 */
public interface LibraryMainBranchContract {

    interface View extends BaseContract.BaseView {
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
         * @param info 街道信息
         */
        void setLocationInfo(String info);

        /**
         * 显示获取定位权限对话框
         */
        void showLocationPermission();

        void setNetError();

        void setMainBranchLibraryList(List<LibraryMainBranchBean> branchLibraryList, int totalCount);

        void setMainBranchLibraryEmpty();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void setCurrentLibCode(String libCode);

        void getMainBranchLibraryList();

        /**
         * 获取定位信息
         */
        void getLocationInfo();

        /**
         * 开始定位
         */
        void startLocation();

        /**
         * 刷新定位信息
         */
        void refreshLocation();
    }
}
