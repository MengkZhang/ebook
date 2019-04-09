package com.tzpt.cloudlibrary.ui.account.deposit;

import com.tzpt.cloudlibrary.base.BaseContract;

/**
 * 退押金认证
 * Created by ZhiqiangJia on 2017-10-10.
 */
public interface RefundDepositAuthenticationContract {

    interface View extends BaseContract.BaseView {

        void showErrorMsg(int msg);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void setFromType(int fromType);

        void startAuthentication(String account, String name);
    }
}
