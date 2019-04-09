package com.tzpt.cloudlibrary.ui.paperbook;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.business_bean.BookBean;

import java.util.List;
import java.util.Map;

/**
 * 图书协议
 * Created by Administrator on 2017/6/5.
 */
public interface PaperBookContract {

    interface View extends BaseContract.BaseView {

        void showBookList(List<BookBean> list, int totalCount, int libraryTotalCount, int limitTotalCount, boolean refresh);

        void showBookListIsEmpty(boolean refresh);

        void setPresenter(Presenter presenter);

        //网络故障
        void showNetError(boolean refresh);

        void showRefreshLoading();

    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void setFilterType(int type);

        void setParameter(Map<String, String> parameters);

        void setPagerBookClassificationId(int classificationId);

//        void setSortType(int sortType);

        void getPaperBook(int pageNum);

        //是否排行榜列表
        boolean isRankingList();

        boolean isBorrowRanking();

        boolean isRecommendRanking();

        boolean isPraiseRanking();

        int getCurrentType();

        /**
         * 是否是(馆内/馆内搜索)图书列表
         *
         * @return true 是 false 不是
         */
        boolean isLibraryBookList();

        /**
         * 是否是搜索结果列表
         *
         * @return true搜索结果列表 false其他图书列表
         */
        boolean isSearchBookList();

        String getLibraryCode();

        void saveHistoryTag(String content);

        void mustShowProgressLoading();
    }
}
