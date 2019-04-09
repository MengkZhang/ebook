package com.tzpt.cloudlibrary.ui.bookstore;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.business_bean.LibraryBean;

import java.util.List;
import java.util.Map;

/**
 * 书店
 * Created by ZhiqiangJia on 2017-12-06.
 */
public interface BookStoreContract {

    interface View extends BaseContract.BaseView {

        void setPresenter(Presenter presenter);

        void setBookStoreList(List<LibraryBean> beanList, int totalCount, int limitTotalCount, boolean refresh);

        void showNetError(boolean refresh);

        void setBookStoreListEmpty(boolean refresh);

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

        void setBookStoreFilterType(int filterType);

        void setParameter(Map<String, Object> parameter);

        void getBookStoreList(int pageNum);

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
