package com.tzpt.cloudlibrary.ui.readers;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.bean.ActionInfoBean;

import java.util.List;

/**
 * 活动
 * Created by tonyjia on 2018/3/21.
 */
public interface ActivityListContract {

    interface View extends BaseContract.BaseView {

        void setPresenter(Presenter presenter);

        void setOurReadersList(List<ActionInfoBean> beanList, int totalCount, boolean refresh, boolean isFromSearch);

        void setOurReadersEmpty(boolean refresh);

        void setNetError(boolean refresh);

        void pleaseLogin();

        void showRefreshLoading();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        int getFromSearchValue();

        String getKeyWord();

        String getLibCode();

        int getFromType();

        void setParameters(int fromType, boolean isFromSearch, String keyword, String libCode);

        void getActivityList(int pageNum);

        void saveLocalActionList(List<ActionInfoBean> list);

        void clearLocalActionList();

        void mustShowProgressLoading();

        void saveHistoryTag(String searchContent);
    }
}
