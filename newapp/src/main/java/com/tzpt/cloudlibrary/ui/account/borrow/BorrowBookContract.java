package com.tzpt.cloudlibrary.ui.account.borrow;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.business_bean.BorrowBookBean;

import java.util.List;

/**
 * Created by ZhiqiangJia on 2017-08-24.
 */
public interface BorrowBookContract {

    interface View extends BaseContract.BaseView {

        void setNetError(int pageNum);

        void setBorrowBookList(List<BorrowBookBean> borrowBookBeanList, int totalCount, boolean refresh);

        void setBorrowBookEmpty(boolean refresh);

        void praiseSuccess(int position);

        void showToastMsg(String msg);

        void showToastMsg(int resId);

        void pleaseLoginTip();

        void oneKeyContinueSuccess(int position);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        /**
         * 当前借阅数据
         *
         * @param pageNum 页数
         */
        void getBorrowingBookList(int pageNum);

        /**
         * 历史借阅数据
         *
         * @param pageNum 页数
         */
        void getHistoryBorrowBookList(int pageNum, int type);

        void oneKeyToBorrowBook(long mBorrowerId,int position);

        /**
         * 点赞/取消点赞
         *
         * @param borrowBookId 借书ID
         * @param isPraised    是否已经点赞
         * @param position     列表index
         */
        void praiseBook(long borrowBookId, int position);

    }
}
