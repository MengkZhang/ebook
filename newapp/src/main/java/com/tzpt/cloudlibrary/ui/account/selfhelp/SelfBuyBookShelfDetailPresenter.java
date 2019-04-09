package com.tzpt.cloudlibrary.ui.account.selfhelp;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.business_bean.BoughtBookBean;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 购书架详情
 * Created by tonyjia on 2018/8/17.
 */
public class SelfBuyBookShelfDetailPresenter extends RxPresenter<SelfBuyBookShelfDetailContract.View>
        implements SelfBuyBookShelfDetailContract.Presenter {

    @Override
    public void getSelfBuyBookShelfDetail(long buyBookId) {
        mView.showProgressDialog();
        Subscription subscription = DataRepository.getInstance().getSelfBuyBookDetail(buyBookId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BoughtBookBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.dismissProgressDialog();
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
                    public void onNext(BoughtBookBean boughtBookBean) {
                        if (mView != null) {
                            mView.dismissProgressDialog();
                            if (boughtBookBean != null) {
                                mView.setBuyBookDetail(boughtBookBean);
                            } else {
                                mView.setNetError();
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void praiseSelfBuyBook(long buyBookId, final boolean isPraised) {
        Subscription subscription = DataRepository.getInstance().praiseSelfBuyBook(buyBookId, isPraised ? 0 : 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
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
                                    mView.showMsgToast(R.string.network_fault);
                                }
                            } else {
                                mView.showMsgToast(R.string.network_fault);
                            }
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (mView != null) {
                            if (aBoolean) {
                                mView.praiseBuyBookSuccess(!isPraised, R.string.success);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void delNote(long id) {
        Subscription subscription = DataRepository.getInstance().delNote(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
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
                                    mView.showMsgToast(R.string.failure);
                                }
                            } else {
                                mView.showMsgToast(R.string.delete_fail);
                            }
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (mView != null) {
                            if (aBoolean) {
                                mView.delNoteSuccess();
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }
}
