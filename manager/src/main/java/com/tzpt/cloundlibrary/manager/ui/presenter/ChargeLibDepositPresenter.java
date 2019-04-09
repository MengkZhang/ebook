package com.tzpt.cloundlibrary.manager.ui.presenter;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseResponseCode;
import com.tzpt.cloundlibrary.manager.base.RxPresenter;
import com.tzpt.cloundlibrary.manager.bean.LibraryInfo;
import com.tzpt.cloundlibrary.manager.bean.ReaderInfo;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.modle.remote.exception.ApiException;
import com.tzpt.cloundlibrary.manager.ui.contract.ChargeLibDepositContract;
import com.tzpt.cloundlibrary.manager.utils.MoneyUtils;
import com.tzpt.cloundlibrary.manager.utils.StringUtils;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/12/18.
 */

public class ChargeLibDepositPresenter extends RxPresenter<ChargeLibDepositContract.View>
        implements ChargeLibDepositContract.Presenter, BaseResponseCode {

    @Override
    public void getReaderInfo(String readerId) {
        mView.showProgressLoading();
        Subscription subscription = DataRepository.getInstance().getReaderInfo(readerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ReaderInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.dismissProgressLoading();
                            if (e instanceof ApiException) {
                                switch (((ApiException) e).getCode()) {
                                    case ERROR_CODE_KICK_OUT:
                                        mView.setNoLoginPermission(R.string.kicked_offline);
                                        break;
                                    case ERROR_CODE_1006:
                                        mView.setNoLoginPermission(R.string.operate_timeout);
                                        break;
                                    default:
                                        mView.showDialogGetReaderInfoFailed(R.string.network_fault);
                                        break;
                                }
                            } else {
                                mView.showDialogGetReaderInfoFailed(R.string.network_fault);
                            }
                        }
                    }

                    @Override
                    public void onNext(ReaderInfo readerInfo) {
                        if (mView != null) {
                            mView.dismissProgressLoading();
                            mView.setReaderNameNumber(readerInfo.mCardName + " " + StringUtils.setIdCardNumberForReader(readerInfo.mIdCard));
                            mView.setBorrowableSum("可借" + readerInfo.mBorrowableSum);

                            //押金信息
                            LibraryInfo libraryInfo = DataRepository.getInstance().getLibraryInfo();
                            if (libraryInfo.mAgreementLevel == 1) {
                                mView.setDepositOrPenalty("可用共享押金" + MoneyUtils.formatMoney(readerInfo.getPlatformUsableDeposit()));
                            } else if (libraryInfo.mAgreementLevel == 2
                                    || libraryInfo.mAgreementLevel == 3) {
                                mView.setDepositOrPenalty("共享" + MoneyUtils.formatMoney(readerInfo.getPlatformUsableDeposit())
                                        + " 馆" + MoneyUtils.formatMoney(readerInfo.getOfflineUsableDeposit()));
                            } else {
                                mView.setDepositOrPenalty("可用馆押金" + MoneyUtils.formatMoney(readerInfo.getOfflineUsableDeposit()));
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void chargeLibDeposit(String readerId, double money) {
        mView.showProgressLoading();
        Subscription subscription = DataRepository.getInstance().chargeLibDeposit(readerId, money)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.dismissProgressLoading();
                            if (e instanceof ApiException) {
                                switch (((ApiException) e).getCode()) {
                                    case ERROR_CODE_KICK_OUT:
                                        mView.setNoLoginPermission(R.string.kicked_offline);
                                        break;
                                    case ERROR_CODE_1006:
                                        mView.setNoLoginPermission(R.string.operate_timeout);
                                        break;
                                    default:
                                        mView.showDialogGetReaderInfoFailed(R.string.network_fault);
                                        break;
                                }
                            } else {
                                mView.showDialogGetReaderInfoFailed(R.string.network_fault);
                            }
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (mView != null) {
                            if (aBoolean) {
                                mView.setChargeDepositSuccess();
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }
}
