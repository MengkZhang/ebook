package com.tzpt.cloundlibrary.manager.ui.contract;

import com.tzpt.cloundlibrary.manager.base.BaseContract;
import com.tzpt.cloundlibrary.manager.bean.BookInfoBean;

import java.util.List;

/**
 * 流入详情列表
 * Created by ZhiqiangJia on 2017-07-14.
 */
public interface IntoManagementDetailContract {

    interface View extends BaseContract.BaseView {
        void showProgressLoading();

        void dismissProgressLoading();

        void showMsgDialog(int msgId);

        /**
         * 合计信息
         *
         * @param totalSum 数量
         * @param priceSum 总价格
         */
        void setIntoDetailTotalSumInfo(int totalSum, String priceSum);

        /**
         * 设置流入列表
         *
         * @param bookInfoBeanList             流入列表
         * @param totalSum                     数量
         * @param refresh                      是否刷新
         */
        void setIntoDetailBookInfoList(List<BookInfoBean> bookInfoBeanList, int totalSum, boolean refresh);

        void setIntoDetailBookInfoListEmpty(boolean refresh);

        void complete();

        /**
         * 拒收成功
         */
        void rejectionThisSingleSuccess();

        /**
         * 签收成功
         */
        void signThisSingleSuccess();

        void noPermissionPrompt(int kickedOffline);

        void setHeadInfo(CharSequence libraryInfo, CharSequence libraryUser);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void setIntoManagementListBeanId(String id);

        /**
         * 获取流入列表详情
         *
         * @param pageNumber 页码
         */
        void getIntoManageSingDetail(int pageNumber);

        /**
         * 操作清单
         *
         * @param operationType 0签收 1拒收
         */
        void operationThisSingle(int operationType);

    }
}
