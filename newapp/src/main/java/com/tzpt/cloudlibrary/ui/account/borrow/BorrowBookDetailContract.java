package com.tzpt.cloudlibrary.ui.account.borrow;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.business_bean.BorrowBookBean;
import com.tzpt.cloudlibrary.business_bean.BoughtBookBean;
import com.tzpt.cloudlibrary.business_bean.LibraryBean;
import com.tzpt.cloudlibrary.business_bean.ReadNoteBean;

import java.util.List;

/**
 * 借阅详情
 * Created by ZhiqiangJia on 2017-08-25.
 */
public interface BorrowBookDetailContract {

    interface View extends BaseContract.BaseView {
        void dismissProgressDialog();

        void setBookBaseInfo(BorrowBookBean bean);

        void setBuyBookDetail(BoughtBookBean bean);

        void setLibraryList(List<LibraryBean> libraryBeanList);

        void setUserReadingNote(List<ReadNoteBean> readNotes);

        void setUserReadingNoteEmpty();

        void operatePraiseSuccess();

        void showToastMsg(String msg);

        void showToastMsg(int resId);

        void showNetError();

        void pleaseLoginTip();

        void delNoteSuccess();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        /**
         * 获取书籍详情
         *
         * @param borrowerBookId 借书ID
         */
        void getBorrowBookDetail(long borrowerBookId);

        /**
         * 续借
         *
         * @param borrowBookId 借书ID
         */
        void renewBorrowBook(long borrowBookId);

        /**
         * 点赞/取消点赞
         *
         * @param borrowBookId 借书ID
         * @param boughtId     购书ID
         * @param isPraised    是否已经点赞
         */
        void praiseBook(long borrowBookId, long boughtId);

        /**
         * 获取书籍详情
         *
         * @param buyBookId 购书ID
         */
        void getSelfBuyBookShelfDetail(long buyBookId);

        /**
         * 点赞/取消点赞
         *
         * @param buyBookId 购书ID
         * @param isPraised 是否已经点赞
         */
        void praiseSelfBuyBook(long buyBookId, final boolean isPraised);

        /**
         * 删除笔记
         *
         * @param id 笔记ID
         */
        void delNote(long id);
    }
}
