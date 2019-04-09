package com.tzpt.cloundlibrary.manager.ui.contract;

import com.tzpt.cloundlibrary.manager.base.BaseContract;
import com.tzpt.cloundlibrary.manager.bean.FlowManageDetailBookInfoBean;
import com.tzpt.cloundlibrary.manager.bean.FlowManageListBean;

import java.util.List;

/**
 * 流出管理操作 新增0，修改1，清点删单2 ，直接删单3，撤回4
 * Created by ZhiqiangJia on 2017-07-12.
 */
public interface FlowManagementOperationContract {


    interface View extends BaseContract.BaseView {

        void showProgressLoading();

        void dismissProgressLoading();

        void showMsgDialog(int msgId);

        void complete();

        void setCodeEditTextHint(String hint);

        void setHeadInfo(CharSequence libraryText, CharSequence conPersonInfo);

        /**
         * 撤回弹出框
         */
        void showReCallDialog(String circulateId);

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

        void setFlowDetailBookInfoListEmpty(boolean refresh, boolean deleteSingleCountBook);

        /**
         * 发送成功
         */
        void sendSuccess();

        /**
         * 直接删单成功
         */
        void directDeleteSingleSuccess();

        /**
         * 撤回成功
         */
        void reCallSuccess();

        /**
         * 没有新书信息
         */
        void setNoFlowManageNewBookInfo();

        void refreshBookList(List<FlowManageDetailBookInfoBean> list);

        void noPermissionPrompt(int kickedOffline);

        void showTotalLayout(boolean showTotalLayout, boolean showBtn);

    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        boolean isRefresh();

        void setFromType(int fromType);

        /**
         * 获取头部信息
         */
        void setManageListBean(FlowManageListBean bean);

        /**
         * 获取流出管理详情列表
         */
        void getFlowManageDetail(int pageNumber);

        /**
         * 操作流出管理清单
         *
         * @param fromType 新增0，修改1，直接删单3，撤回4
         */
        void operationFlowManageSingle(int fromType);

        /**
         * 撤回清单
         */
        void reCallSingleByCirculateId();

        /**
         * 从流出单删除书
         *
         * @param bookId 书籍ID
         */
        void deleteFlowManageBookInfo(int fromType, String bookId);

        /**
         * 操作输入
         *
         * @param allBarNumber 馆号 条码号
         * @param fromType     新增 ，修改，清单删单
         */
        void operationFlowManageEditValueByFromType(String allBarNumber, int fromType);

        /**
         * 移除内存数据
         */
        void delOrderNumber();

        /**
         * 移除内存数据
         */
        void delDetailBookList();

        /**
         * 移除内存数据
         */
        void delFlowManageListBean();

        /**
         * 获取内存里面的数据
         */
        void getBookList();

    }
}
