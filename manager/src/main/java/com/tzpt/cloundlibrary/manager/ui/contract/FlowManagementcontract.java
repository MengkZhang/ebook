package com.tzpt.cloundlibrary.manager.ui.contract;

import com.tzpt.cloundlibrary.manager.base.BaseContract;
import com.tzpt.cloundlibrary.manager.bean.FlowManageListBean;

import java.util.List;

/**
 * 流出管理操作类
 * Created by ZhiqiangJia on 2017-07-10.
 */
public interface FlowManagementcontract {

    interface View extends BaseContract.BaseView {

        void complete();

        void showError(boolean refresh);

        /**
         * 设置合计信息
         *
         * @param totalSum   数量
         * @param totalPrice 金额
         */
        void setFlowManageTotalInfo(int totalSum, String totalPrice);

        /**
         * 设置流出管理列表
         *
         * @param flowManageListBeanList 流出管理列表
         * @param refresh                是否刷新
         */
        void setFlowManageListBeanList(List<FlowManageListBean> flowManageListBeanList, int totalSum, boolean refresh);

        void setFlowManageListBeanListEmpty(boolean refresh);

        void setNoLoginPermission(int kickedOffline);

    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        /**
         * 请求流出管理列表
         *
         * @param pageNumber 页码
         */
        void getFlowManagementList(int pageNumber);

        void delStatisticsCondition();
    }
}
