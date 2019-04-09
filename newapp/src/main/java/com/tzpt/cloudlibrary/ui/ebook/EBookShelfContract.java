package com.tzpt.cloudlibrary.ui.ebook;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.bean.LocalEBook;

import java.util.List;

/**
 * Created by Administrator on 2017/11/20.
 */

public interface EBookShelfContract {
    interface View extends BaseContract.BaseView {
        void showLocalEBook(List<LocalEBook> list);

        void showLocalEBookEmpty();

        void checkEditFunction(boolean editAble);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getLocalEBookList();

        void delLocalEBook(List<String> bookIdList);
    }
}
