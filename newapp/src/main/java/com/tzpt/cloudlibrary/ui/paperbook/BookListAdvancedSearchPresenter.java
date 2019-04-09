package com.tzpt.cloudlibrary.ui.paperbook;

import android.text.TextUtils;

import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.bean.BookAdvancedSearchBean;

/**
 * 图书高级搜索
 * Created by ZhiqiangJia on 2017-09-25.
 */
public class BookListAdvancedSearchPresenter extends RxPresenter<BookListAdvancedSearchContract.View> implements
        BookListAdvancedSearchContract.Presenter {

    @Override
    public void checkAdvancedSearchParameters(String libCode, String bookGradeId, String bookGrade, String bookIsbn, String bookName,
                                              String bookCompany, String bookYear, String bookAuthor) {
        if (null != mView) {

        }
    }
}
