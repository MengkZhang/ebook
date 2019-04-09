package com.tzpt.cloudlibrary.ui.account.selfhelp;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.bean.SelfBookInfoBean;

import java.util.List;

/**
 * 自助购书
 * Created by tonyjia on 2018/8/14.
 */
public interface SelfHelpBuyBookContract {

    interface View extends BaseContract.BaseView {

        void showProgressDialog();

        void dismissProgressDialog();

        void showDialogTips(int resId);

        void showChargeDialogTips(int resId);

        void pleaseLoginTip();

        void setBookList(List<SelfBookInfoBean> bookList);

        void setBookEmpty();

        void showBookTotalInfo(String bookSum, String totalPrice, double totalBookPrice);

        void selfBuyBookSuccess(int successBookSum);

        void setEditStatus(boolean editStatus);

        void turnToScan(int boughtNum, double totalPrice);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void getLocalBookInfo();

        void getBookInfo(String barNumber);

        void confirmReaderPsw(String totalPrice, String readerPsw);

        void removeDataByIndex(int position);

        void clearTempBook();

        void getBookInfoTurnToScan();
    }
}
