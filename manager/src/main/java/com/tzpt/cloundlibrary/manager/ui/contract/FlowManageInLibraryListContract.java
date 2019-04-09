package com.tzpt.cloundlibrary.manager.ui.contract;

import com.tzpt.cloundlibrary.manager.base.BaseContract;
import com.tzpt.cloundlibrary.manager.bean.SameRangeLibraryBean;

import java.util.List;

/**
 * 流出管理新增流向馆列表协议
 * Created by ZhiqiangJia on 2017-07-13.
 */
public interface FlowManageInLibraryListContract {

    interface View extends BaseContract.BaseView {

        void showProgressLoading();

        void dismissProgressLoading();

        void showErrorView(boolean fresh);

        void complete();

        /**
         * 设置流入馆列表
         *
         * @param sameRangeLibraryBeanList 流入馆列表
         * @param totalSum                 总数量
         * @param refresh                  是否刷新
         */
        void setFlowManageInLibraryList(List<SameRangeLibraryBean> sameRangeLibraryBeanList, int totalSum, boolean refresh);

        void setFlowManageInLibraryListEmpty(boolean refresh);

        void noPermissionPrompt(int kickedOffline);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        /**
         * 模糊搜索流通范围的图书馆列表-流入馆列表
         *
         * @param pageNumber 页码
         * @param condition  内容
         */
        void searchFlowManageInLibraryList(int pageNumber, String condition);

        void clearTempInLibraryList();

    }
}
