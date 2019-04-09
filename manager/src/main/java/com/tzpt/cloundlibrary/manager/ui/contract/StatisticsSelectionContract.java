package com.tzpt.cloundlibrary.manager.ui.contract;

import com.tzpt.cloundlibrary.manager.base.BaseContract;
import com.tzpt.cloundlibrary.manager.bean.SingleSelectionConditionBean;
import com.tzpt.cloundlibrary.manager.bean.StatisticsConditionBean;

import java.util.List;

/**
 * 统计分析-选项协议
 * Created by ZhiqiangJia on 2017-07-14.
 */
public interface StatisticsSelectionContract {

    interface View extends BaseContract.BaseView {

        void showProgressLoading();

        void dismissProgressLoading();

        void showMsgDialog(String msg);

        void showMsgDialog(int msgId);

        /**
         * 初始化设置选项列表
         *
         * @param optionList 选项列表
         */
        void setStatisticsConditionList(List<StatisticsConditionBean> optionList);

        /**
         * 初始化单选列表
         *
         * @param list 单选列表
         */
        void setSingleSelectionCondition(List<SingleSelectionConditionBean> list);

        /**
         * 重新展示数据
         *
         * @param beginCondition 起始条件
         */
        void setNewStartContent(String beginCondition);

        /**
         * 重新展示数据
         *
         * @param endCondition 结束条件
         */
        void setNewEndContent(String endCondition);

        void setNoLoginPermission(int kickedOffline);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        /**
         * 初始化选项数据
         *
         * @param fromType
         */
        void initOptionDataByFromType(int fromType);

        void saveStatisticsCondition(StatisticsConditionBean condition);

        /**
         * 清空缓存列表
         */
        void clearTempData();
    }
}
