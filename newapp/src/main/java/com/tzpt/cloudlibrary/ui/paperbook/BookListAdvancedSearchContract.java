package com.tzpt.cloudlibrary.ui.paperbook;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.bean.BookAdvancedSearchBean;

/**
 * 图书高级搜索
 * Created by ZhiqiangJia on 2017-09-25.
 */
public interface BookListAdvancedSearchContract {

    interface View extends BaseContract.BaseView {

        void toAdvancedSearchBookList(BookAdvancedSearchBean itemBean);

        void showErrorMsg(String msg);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void checkAdvancedSearchParameters(String libCode, String bookGradeId, String bookGrade,
                                           String bookIsbn, String bookName, String bookCompany,
                                           String bookYear, String bookAuthor);
    }
}
