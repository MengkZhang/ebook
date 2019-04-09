package com.tzpt.cloudlibrary.ui.account.selfhelp;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.business_bean.BoughtBookBean;

import java.util.List;

/**
 * 购书架
 * Created by tonyjia on 2018/8/16.
 */
public interface SelfBuyBookShelfContract {

    interface View extends BaseContract.BaseView {

        void setNetError(boolean refresh);

        void setShelfBookList(List<BoughtBookBean> bookList, boolean refresh);

        void setShelfBookEmpty(boolean refresh);

        void setShelfBookTotalInfo(int totalCount, double totalBuyPrice, double totalFixedPrice);

        void hideBookTotalInfo();

        void showMsgToast(int resId);

        void praiseBuyBookSuccess(boolean isPraised, int position, int resId);

        void pleaseLoginTip();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void getSelfBuyBookShelfList(int pageNo);

        void praiseSelfBuyBook(long buyBookId, final boolean isPraised, final int position);
    }
}
