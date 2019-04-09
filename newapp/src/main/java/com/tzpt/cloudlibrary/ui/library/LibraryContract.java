package com.tzpt.cloudlibrary.ui.library;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.business_bean.LibraryBean;

import java.util.List;
import java.util.Map;

/**
 * Created by ZhiqiangJia on 2017-08-03.
 */

public interface LibraryContract {

    interface View extends BaseContract.BaseView {

        void setPresenter(Presenter presenter);

        void setLibraryList(List<LibraryBean> libraryList, int totalCount, int limitTotalCount, boolean refresh);

        //网络故障
        void showNetError(boolean refresh);

        //没有图书馆列表
        void setLibraryListEmpty(boolean refresh);

        void showRefreshLoading();

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
         * @param info 街道信息
         */
        void setLocationInfo(String info);

        /**
         * 显示获取定位权限对话框
         */
        void showLocationPermission();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void setLibFilterType(int filterType);

        void setParameter(Map<String, Object> parameter);

        void getLibraryList(int pageNum);

        void setLibListType(boolean isSearch);

        boolean isSearchResultList();

        void mustShowProgressLoading();

        void saveHistoryTag(String searchContent);

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
