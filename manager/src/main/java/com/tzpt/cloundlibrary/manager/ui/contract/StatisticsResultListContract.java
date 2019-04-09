package com.tzpt.cloundlibrary.manager.ui.contract;

import com.tzpt.cloundlibrary.manager.base.BaseContract;
import com.tzpt.cloundlibrary.manager.bean.StatisticsConditionBean;
import com.tzpt.cloundlibrary.manager.bean.StatisticsItem;

import java.util.List;

/**
 * Created by Administrator on 2018/9/28.
 */

public interface StatisticsResultListContract {
    interface View extends BaseContract.BaseView {
        void showLoadingProgress();

        void setStatisticsResultColumnTitle(List<StatisticsItem> data);

        void setStatisticsResultList(List<List<StatisticsItem>> data, int totalSum, boolean refresh);

        void setStatisticsResultEmpty();

        void setStatisticsResultError(boolean refresh);

        void setTotalInfo(String info1, String info2);

        void noPermissionPrompt(int msgId);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getStatisticsResult(int type, int pageNumber);

        void delStatisticsCondition();
    }
}
