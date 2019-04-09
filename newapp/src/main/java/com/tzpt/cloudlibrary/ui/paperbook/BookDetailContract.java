package com.tzpt.cloudlibrary.ui.paperbook;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.business_bean.BookBean;
import com.tzpt.cloudlibrary.business_bean.LibraryBean;

import java.util.List;

/**
 * 图书详情
 * Created by ZhiqiangJia on 2017-08-14.
 */
public interface BookDetailContract {

    interface View extends BaseContract.BaseView {

        void showProgressDialog();

        void dismissProgressDialog();

        void showLoadingView();

        void dismissLoadingView();

        void showErrorMsg(int msgId);

        void setBookBaseInfo(BookBean baseInfo);

        void setBookSummerInfo(String authorInfo, String summer, String catalog, String url);

        void setLibraryList(List<LibraryBean> libLists);

        void showNetError();

        void showEmptyError();

        void pleaseLogin();

        void pleaseLoginTip();

        void orderBookSuccess(boolean notRegistered);

        /**
         * 预约图书失败提示
         *
         * @param type 1.非本馆读者
         *             2.图书错误，非外借图书！
         *             3.图书预约时图书状态不是在馆(无可预约书籍)
         *             4.图书错误，已被预约
         *             5.超过预约次数
         *             6.预约失败
         */
        void orderBookFailed(int type);

        void showShareItem(int borrowNum, int praiseNum, int shareNum);

        void showShareItemEmpty();

        void setReservationBtnStatus(int status);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        /**
         * 根据ID获取书籍详情
         *
         * @param id                  书籍id
         * @param libCode             馆号
         * @param needLibList         是否需要所在馆列表
         * @param isInLibShelvingInfo 是否需要书架信息
         */
        void getBookDetail(String id, String libCode, boolean needLibList, boolean isInLibShelvingInfo, int fromSearch);

        /**
         * 判断是否登录
         *
         * @return false未登录 true登录
         */
        boolean isLoginStatus();

        /**
         * 预约书籍
         *
         * @param isbn    图书ISBN号
         * @param libCode 所在馆号
         */
        void orderBook(String isbn, String libCode);

        void reportBookShare();

    }
}
