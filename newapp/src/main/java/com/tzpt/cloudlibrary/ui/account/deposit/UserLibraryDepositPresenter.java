package com.tzpt.cloudlibrary.ui.account.deposit;

import android.text.TextUtils;

import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.bean.UserLibraryDepositBean;
import com.tzpt.cloudlibrary.business_bean.BaseListResultData;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 馆押金明细
 * Created by ZhiqiangJia on 2017-08-24.
 */
public class UserLibraryDepositPresenter extends RxPresenter<UserLibraryDepositContract.View> implements
        UserLibraryDepositContract.Presenter {

    @Override
    public void getUserLibraryDepositList(final int pageNum) {
        String idCard = UserRepository.getInstance().getLoginUserIdCard();
        if (TextUtils.isEmpty(idCard)) {
            return;
        }
        Subscription subscription = DataRepository.getInstance().getLibDeposit(idCard, pageNum, 10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseListResultData<UserLibraryDepositBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            if (e instanceof ApiException) {
                                if (((ApiException) e).getCode() == 30100) {
                                    mView.pleaseLoginTip();
                                } else {
                                    mView.setNetError();
                                }
                            } else {
                                mView.setNetError();
                            }
                        }
                    }

                    @Override
                    public void onNext(BaseListResultData<UserLibraryDepositBean> userLibraryDepositBeanBaseListResultData) {
                        if (mView != null) {
                            if (userLibraryDepositBeanBaseListResultData != null
                                    && userLibraryDepositBeanBaseListResultData.mResultList != null
                                    && userLibraryDepositBeanBaseListResultData.mResultList.size() > 0) {
                                mView.setLibraryDepositList(userLibraryDepositBeanBaseListResultData.mResultList,
                                        userLibraryDepositBeanBaseListResultData.mTotalCount,
                                        pageNum == 1);
                            } else {
                                mView.setLibraryDepositListEmpty(pageNum == 1);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }
}
