package com.tzpt.cloudlibrary.ui.account.borrow;

import android.text.TextUtils;
import android.view.View;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.business_bean.BaseListResultData;
import com.tzpt.cloudlibrary.business_bean.BorrowBookBean;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 借阅图书
 * Created by ZhiqiangJia on 2017-08-24.
 */
public class BorrowBookPresenter extends RxPresenter<BorrowBookContract.View> implements
        BorrowBookContract.Presenter {
    @Override
    public void getBorrowingBookList(final int pageNum) {
        String idCard = UserRepository.getInstance().getLoginUserIdCard();
        Subscription subscription = DataRepository.getInstance().getBorrowBookList(idCard, pageNum, 10, 5)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseListResultData<BorrowBookBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            if (e instanceof ApiException) {
                                if (((ApiException) e).getCode() == 30100) {
                                    mView.pleaseLoginTip();
                                } else {
                                    mView.setNetError(pageNum);
                                }
                            } else {
                                mView.setNetError(pageNum);
                            }
                        }
                    }

                    @Override
                    public void onNext(BaseListResultData<BorrowBookBean> borrowBookBeanBaseListResultData) {
                        if (mView != null) {
                            if (borrowBookBeanBaseListResultData != null
                                    && borrowBookBeanBaseListResultData.mResultList != null
                                    && borrowBookBeanBaseListResultData.mResultList.size() > 0) {
                                mView.setBorrowBookList(borrowBookBeanBaseListResultData.mResultList, borrowBookBeanBaseListResultData.mTotalCount, pageNum == 1);
                            } else {
                                mView.setBorrowBookEmpty(pageNum == 1);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void getHistoryBorrowBookList(final int pageNum, int type) {
        String idCard = UserRepository.getInstance().getLoginUserIdCard();
        Subscription subscription = DataRepository.getInstance().getHistoryBookList(idCard, pageNum, 10, type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseListResultData<BorrowBookBean>>() {
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
                                    mView.setNetError(pageNum);
                                }
                            } else {
                                mView.setNetError(pageNum);
                            }
                        }
                    }

                    @Override
                    public void onNext(BaseListResultData<BorrowBookBean> borrowBookBeanBaseListResultData) {
                        if (mView != null) {
                            if (borrowBookBeanBaseListResultData != null
                                    && borrowBookBeanBaseListResultData.mResultList != null
                                    && borrowBookBeanBaseListResultData.mResultList.size() > 0) {
                                mView.setBorrowBookList(borrowBookBeanBaseListResultData.mResultList, borrowBookBeanBaseListResultData.mTotalCount, pageNum == 1);
                            } else {
                                mView.setBorrowBookEmpty(pageNum == 1);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);

    }

    @Override
    public void oneKeyToBorrowBook(long mBorrowerId, final int position) {
        String idCard = UserRepository.getInstance().getLoginUserIdCard();
        if (!TextUtils.isEmpty(idCard)) {
            Subscription subscription = DataRepository.getInstance().renewBorrowBook(idCard, mBorrowerId)
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
                                        mView.showToastMsg("续借失败！");
                                    }
                                } else {
                                    mView.showToastMsg("续借失败！");
                                }
                            }
                        }

                        @Override
                        public void onNext(Boolean aBoolean) {
                            if (mView != null) {
                                if (aBoolean) {
                                    mView.showToastMsg("续借成功！");
//                                    getBorrowingBookList(1);
                                    //续借成功 不可能是重新请求第一页的数据 而是刷新当前item
                                    mView.oneKeyContinueSuccess(position);
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }

    @Override
    public void praiseBook(long borrowBookId, final int position) {
        Subscription subscription = DataRepository.getInstance().praiseBook(borrowBookId, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            if (e instanceof ApiException) {
                                if (((ApiException) e).getCode() == 30100) {
                                    mView.pleaseLoginTip();
                                } else {
                                    mView.showToastMsg(R.string.failure);
                                }
                            } else {
                                mView.showToastMsg(R.string.failure);
                            }
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (mView != null) {
                            if (aBoolean) {
                                mView.praiseSuccess(position);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }
}
