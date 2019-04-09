package com.tzpt.cloundlibrary.manager.ui.presenter;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseResponseCode;
import com.tzpt.cloundlibrary.manager.base.RxPresenter;
import com.tzpt.cloundlibrary.manager.bean.LibraryInfo;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.modle.remote.exception.ApiException;
import com.tzpt.cloundlibrary.manager.ui.contract.LibraryDepositContract;
import com.tzpt.cloundlibrary.manager.utils.MoneyUtils;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 押金
 * Created by ZhiqiangJia on 2017-11-21.
 */
public class LibraryDepositPresenter extends RxPresenter<LibraryDepositContract.View>
        implements LibraryDepositContract.Presenter,
        BaseResponseCode {

    @Override
    public void getLibraryDeposit() {
        if (null != mView) {
            mView.showProgressDialog();
            Subscription subscription = DataRepository.getInstance().getLibBalance()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Double>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (mView != null) {
                                mView.dismissProgressDialog();
                                if (e instanceof ApiException) {
                                    switch (((ApiException) e).getCode()) {
                                        case ERROR_CODE_KICK_OUT:
                                            mView.setNoPermissionDialog(R.string.kicked_offline);
                                            break;
                                        case ERROR_CODE_1006:
                                            mView.setNoPermissionDialog(R.string.operate_timeout);
                                            break;
                                        default:
                                            mView.setErrorDialog(R.string.network_fault);
                                            break;
                                    }
                                } else {
                                    mView.setErrorDialog(R.string.network_fault);
                                }
                            }
                        }

                        @Override
                        public void onNext(Double aDouble) {
                            if (mView != null) {
                                mView.dismissProgressDialog();
                                LibraryInfo libraryInfo = DataRepository.getInstance().getLibraryInfo();
                                mView.setAvailableBalance(MoneyUtils.formatMoney(aDouble), libraryInfo.mOperaterName.equals("admin"));
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }
}
