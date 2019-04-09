package com.tzpt.cloudlibrary.ui.account.collection;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.ui.account.borrow.BorrowContract;

/**
 * Created by Mengk on 2019/3/18
 * Describe :  收藏协议类
 */
public class CollectionContract {
    interface View extends BaseContract.BaseView {
        void setCollectionEBookCount(int count);

        void setCollectionVideoCount(int count);
    }

    interface Presenter extends BaseContract.BasePresenter<CollectionContract.View> {
        void getLocalUserInfo();
    }
}
