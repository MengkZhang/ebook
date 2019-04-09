package com.tzpt.cloudlibrary.ui.ebook;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.business_bean.EBookBean;

import java.util.List;
import java.util.Map;

/**
 * 电子书协议
 * Created by ZhiqiangJia on 2017-08-08.
 */
public interface EBookContract {

    interface View extends BaseContract.BaseView {

        void setPresenter(Presenter presenter);

        void setEBookList(List<EBookBean> eBookList, int totalCount, int limitTotalCount, boolean refresh);

        void setEBookListEmpty(boolean refresh);

        void setEBookListError(boolean refresh);

        void showRefreshLoading();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void setFilterType(int type);

        int getFilterType();

        void setParameter(Map<String, String> parameters);

        void getEBookList(int pageNum);

        void setEBookClassificationId(int parentClassifyId, int childClassifyId);

        String getLibraryCode();

        boolean isSearchResultList();

        void mustShowProgressLoading();

        void saveHistoryTag(String searchContent);

        boolean isRankEBookList();

        boolean isRecommendEBook();

    }
}
