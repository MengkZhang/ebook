package com.tzpt.cloudlibrary.ui.information;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.bean.InformationBean;

import java.util.List;

/**
 * 资讯
 * Created by ZhiqiangJia on 2017-08-17.
 */
public interface InformationContract {

    interface View extends BaseContract.BaseView {
        void setPresenter(Presenter presenter);

        void setInformationList(List<InformationBean> beanList, int totalCount, boolean refresh);

        void setInformationEmpty(boolean refresh);

        void setNetError(boolean refresh);

        void showRefreshLoading();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void setSearchParameter(String keyword, String libraryCode, String source, String title, String type, String industryId, int fromSearch);

        void getInformationList(int pageNum);

        void removeInformationList();

        void saveInfoListCache(List<InformationBean> beanList);

        String getKeyword();

        String getLibCode();

        String getSource();

        String getCategoryId();

        String getSearchTitle();

        String getSearchIndustryId();

        int getFromSearch();

        void mustShowProgressLoading();

        void saveHistoryTag(String searchContent);
    }
}
