package com.tzpt.cloudlibrary.ui.account.deposit;

import android.text.TextUtils;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;

/**
 * 退押金认证
 * Created by ZhiqiangJia on 2017-10-10.
 */
public class RefundDepositAuthenticationPresenter extends RxPresenter<RefundDepositAuthenticationContract.View> implements
        RefundDepositAuthenticationContract.Presenter {

    private int mFromType;

    @Override
    public void setFromType(int fromType) {
        this.mFromType = fromType;
    }

    @Override
    public void startAuthentication(String account, String name) {
        if (TextUtils.isEmpty(name)) {
            mView.showErrorMsg(R.string.name_not_empty);
            return;
        }
        if (TextUtils.isEmpty(account)) {
            mView.showErrorMsg(mFromType == 0 ? R.string.wechat_not_empty : R.string.alipay_not_empty);
            return;
        }
        switch (mFromType) {
            case 0:

                break;
            case 1:

                break;
        }
    }
}
