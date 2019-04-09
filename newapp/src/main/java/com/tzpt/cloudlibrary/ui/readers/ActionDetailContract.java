package com.tzpt.cloudlibrary.ui.readers;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.bean.ActionInfoBean;

import java.util.List;

/**
 * Created by Administrator on 2017/11/13.
 */

public interface ActionDetailContract {
    interface View extends BaseContract.BaseView {
        /**
         * 设置活动ID
         *
         * @param actionId 活动ID
         */
        void setActionId(int actionId, int currentCount);

        /**
         * 获取活动列表成功
         */
        void getActionListSuccess(List<ActionInfoBean> actionList);

        /**
         * 详情正在加载状态
         */
        void showLoadingStatus();

        /**
         * 显示活动详情信息
         *
         * @param info
         */
        void setActionDetailInfo(ActionInfoBean info);

        /**
         * 活动详情加载失败
         */
        void loadActionDetailFailed();

        /**
         * 活动报名成功
         */
        void applyActionSuccess();

        /**
         * Toast提示
         *
         * @param msg 提示语
         */
        void showToastMsg(String msg);

        /**
         * Toast提示
         *
         * @param resId 提示语
         */
        void showToastMsg(int resId);

        /**
         * 请登陆
         */
        void pleaseLogin();

        /**
         * 踢下线提示
         */
        void pleaseLoginTip();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        /**
         * 获取活动ID
         *
         * @param position 索引值
         */
        void getActionId(int position);

        /**
         * 获取活动详情
         *
         * @param actionId 活动ID
         * @param keyword  搜索关键字
         * @param libCode  馆号
         */
        void getActionDetailInfo(int actionId, int fromSearch, String keyword, String libCode);

        /**
         * 获取活动列表
         *
         * @param pageNum 页数
         */
        void getActionList(int type, int pageNum);

        /**
         * 报名活动
         *
         * @param activityId 活动ID
         */
        void toSignUp(int activityId);
    }
}
