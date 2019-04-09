package com.tzpt.cloundlibrary.manager.ui.contract;

import com.tzpt.cloundlibrary.manager.base.BaseContract;
import com.tzpt.cloundlibrary.manager.bean.IntoManagementListBean;

import java.util.List;

/**
 * 流入管理列表协议
 * Created by ZhiqiangJia on 2017-07-14.
 */
public interface IntoManagementListContract {

    interface View extends BaseContract.BaseView {

        void showError(boolean refresh);

        /**
         * 设置合计信息
         *
         * @param totalAllSum   数量
         * @param totalAllPrice 价格
         */
        void setIntoManageTotalInfo(int totalAllSum, String totalAllPrice);

        /**
         * 设置流入列表
         *
         * @param flowManageListBeanList 流入列表
         * @param totalAllSum            数量
         * @param refresh                是否刷新
         */
        void setIntoManagementList(List<IntoManagementListBean> flowManageListBeanList, int totalAllSum, boolean refresh);

        void setIntoManagementListEmpty(boolean refresh);

        void complete();

        void setNoLoginPermission(int kickedOffline);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        /**
         * 获取流入列表
         *
         * @param pageNumber
         */
        void getIntoManageList(int pageNumber);

        void delStatisticsCondition();
    }
}
