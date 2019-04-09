package com.tzpt.cloudlibrary.ui.account.selfhelp;

import com.tzpt.cloudlibrary.base.BaseContract;

/**
 * 自助购书扫描协议
 * Created by tonyjia on 2018/8/14.
 */
public interface SelfHelpBuyBookScanningContract {

    interface View extends BaseContract.BaseView {

        void updateBookTotalInfo(String sum, String money);

    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void getBookTotalInfo();
    }
}
