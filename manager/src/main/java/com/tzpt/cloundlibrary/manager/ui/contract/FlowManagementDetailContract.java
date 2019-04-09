package com.tzpt.cloundlibrary.manager.ui.contract;

import com.tzpt.cloundlibrary.manager.base.BaseContract;
import com.tzpt.cloundlibrary.manager.bean.FlowManageDetailBookInfoBean;
import com.tzpt.cloundlibrary.manager.bean.FlowManageListBean;

import java.util.List;

/**
 * 流出管理详情协议
 * Created by ZhiqiangJia on 2017-07-11.
 */
public interface FlowManagementDetailContract {

    interface View extends BaseContract.BaseView {

        void showProgressLoading();

        void dismissProgressLoading();

        void showMsgDialog(String msg);

        void showMsgDialog(int msgId);

        void complete();


        /**
         * 合计信息
         *
         * @param totalSum 数量
         * @param priceSum 金额
         */
        void setFlowDetailTotalSumInfo(int totalSum, String priceSum);


        /**
         * 设置详情图书列表
         *
         * @param flowManageDetailBookInfoList 图书列表
         * @param totalSum                     数量
         * @param refresh                      是否刷新
         */
        void setFlowDetailBookInfoList(List<FlowManageDetailBookInfoBean> flowManageDetailBookInfoList, int totalSum, boolean refresh);

        void setFlowDetailBookInfoListEmpty(boolean refresh);


        void noPermissionPrompt(int kickedOffline);

        void setHeadInfo(CharSequence libraryText, CharSequence conPersonInfo, String auditContactName, String auditDate);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void setFlowManageListBeanIdAndState(String id, int outState);


        /**
         * 获取流出管理详情列表
         */
        void getFlowManageDetail(int pageNumber);

    }

}
