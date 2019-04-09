package com.tzpt.cloudlibrary.ui.ranklist;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.business_bean.BookBean;
import com.tzpt.cloudlibrary.business_bean.EBookBean;

import java.util.List;

/**
 * Created by ZhiqiangJia on 2017-08-11.
 */

public interface RankListContract {

    interface View extends BaseContract.BaseView {

        void showRankProgress(boolean isEmpty);

        void showNetError();

        void setRankBookContentView();

        void setRankBookListEmpty();

        void setRankThumbsUpBookList(List<BookBean> thumbsUpRankBookList);

        void hideRankThumbsUpBookList();

        void setRankRecommendBookList(List<BookBean> rankRecommendBookList);

        void hideRankRecommendBookList();

        void setRankBorrowBookList(List<BookBean> borrowBookList);

        void hideRankBorrowBookList();

        void setRankReadingBookList(List<EBookBean> readingBookList);

        void hideRankReadingBookList();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void getRankList();
    }

}
