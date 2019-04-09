package com.tzpt.cloudlibrary.ui.ebook;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.business_bean.EBookBean;
import com.tzpt.cloudlibrary.business_bean.LibraryBean;

/**
 * 电子书详情协议
 * Created by ZhiqiangJia on 2017-08-16.
 */
public interface EBookDetailContract {

    interface View extends BaseContract.BaseView {

        void showProgressDialog();

        void dismissProgressDialog();

        void showErrorMsg(int msgId);

        void setEBookDetailInfo(EBookBean bean);

        void setEBookDetailShareInfo(int readCount, int collectNum, int shareNum);

        void setEBookBelongLib(String libCode, String libName);

        void hideEBookBelongLib();

        void showNetError();

        void setEBookCollectionStatus(boolean collection);

        void collectEBookSuccess(boolean collection);

        void showCollectProgress(int resId);

        void dismissCollectProgress();

        void showNoLoginDialog();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        boolean isLogin();

        void getEBookDetail(String id, int fromSearch, String libCode);

        void reportEBookRead(String ebookId, String libCode);

        void collectionOrCancelEBook();

        void collectionEBook();

        void reportEBookShare();

        void setCollectionStatus(boolean isCollection);
    }
}
