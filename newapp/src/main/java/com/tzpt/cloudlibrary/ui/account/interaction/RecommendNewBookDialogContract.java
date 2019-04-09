package com.tzpt.cloudlibrary.ui.account.interaction;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.business_bean.LibraryBean;

import java.util.List;

/**
 * 推荐新书
 * Created by ZhiqiangJia on 2017-09-04.
 */
public interface RecommendNewBookDialogContract {

    interface View extends BaseContract.BaseView {

        void showLoadingProgress();

        void showNetError();

        void setLibraryList(List<LibraryBean> libraryBeanList);

        void setLibraryListEmpty();

        void showErrorMsg(int resId);

        void recommendSuccess(String libCode);

        void pleaseLogin();

    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void getRecommendBookLibList(String isbn);

        void recommendNewBookToLibrary(String isbn, String libCode);

        void refreshTempRecommendLibraryInfo(String libCode);

        void searchLibByKeyWord(String isbn, String keyword);
    }
}
