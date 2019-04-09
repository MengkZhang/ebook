package com.tzpt.cloudlibrary.ui.account.deposit;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.business_bean.AliPayInfoBean;
import com.tzpt.cloudlibrary.business_bean.PayDepositBean;
import com.tzpt.cloudlibrary.business_bean.WXPayInfoBean;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;
import com.tzpt.cloudlibrary.utils.MoneyUtils;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ZhiqiangJia on 2017-10-09.
 */
public class PayDepositPresenter extends RxPresenter<PayDepositContract.View> implements
        PayDepositContract.Presenter {
    private String mOrderNum;
    private int mRetryCount;
    private boolean mIsWXOrAli;//true表示微信 false表示支付宝
    private double mLimitMoney;//最大可充金额

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1000) {
                if (mIsWXOrAli) {
                    requestWXPayResult();
                } else {
                    requestAliPayResult();
                }

            }
        }
    };

    @Override
    public void requestUserDeposit() {
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (TextUtils.isEmpty(readerId)) {
            return;
        }
        mView.showLoading();
        Subscription subscription = DataRepository.getInstance().getPayDepositInfo(readerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PayDepositBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.dismissLoading();
                            if (e instanceof ApiException) {
                                switch (((ApiException) e).getCode()) {
                                    case 30100:
                                        mView.pleaseLoginTip();
                                        break;
                                    default:
                                        mView.showDialogTip(R.string.network_fault, false);
                                        break;
                                }
                            } else {
                                mView.showDialogTip(R.string.network_fault, false);
                            }
                        }
                    }

                    @Override
                    public void onNext(PayDepositBean payDepositBean) {
                        if (mView != null) {
                            mView.dismissLoading();
                            mLimitMoney = payDepositBean.mLimitMoney;
                            mView.showUserDeposit(payDepositBean.mPenalty, payDepositBean.mActiveDeposit,
                                    payDepositBean.mUsedDeposit, payDepositBean.mDepositList,
                                    payDepositBean.mPenaltyList);
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void requestWXPayInfo(double payMoney, String userIP) {
        if (mLimitMoney >= 0 && mLimitMoney < payMoney) {
            mView.showLimitMoneyTip(MoneyUtils.formatMoney(mLimitMoney));
            return;
        }
        mRetryCount = 0;
        mView.showProgressLoading();
        Subscription subscription = DataRepository.getInstance().requestWXPayInfo(payMoney, userIP)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<WXPayInfoBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.dismissProgressLoading();
                            if (e instanceof ApiException) {
                                switch (((ApiException) e).getCode()) {
                                    case 6100:
                                    case 6101:
                                    case 6102:
                                        mView.showDialogTip(R.string.pay_failed, false);
                                        break;
                                    case 6112:
                                        mView.showLimitMoneyTip(e.getMessage());
                                        break;
                                    case 7000:
                                    case 7001:
                                    case 2304:
                                    case 30100:
                                        mView.pleaseLoginTip();
                                        break;
                                    default:
                                        mView.showDialogTip(R.string.network_fault, false);
                                        break;
                                }
                            } else {
                                mView.showDialogTip(R.string.network_fault, false);
                            }
                        }
                    }

                    @Override
                    public void onNext(WXPayInfoBean wxPayInfoBean) {
                        if (mView != null) {
                            mView.dismissProgressLoading();
                            if (wxPayInfoBean != null) {
                                mView.wxPayReq(wxPayInfoBean.mAppid,
                                        wxPayInfoBean.mPartnerid,
                                        wxPayInfoBean.mPrepayid,
                                        wxPayInfoBean.mPackageName,
                                        wxPayInfoBean.mNoncestr,
                                        wxPayInfoBean.mTimestamp,
                                        wxPayInfoBean.mSign);
                                mOrderNum = wxPayInfoBean.mOrderNum;
                            } else {
                                mView.showDialogTip(R.string.network_fault, false);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void requestWXPayResult() {
        mIsWXOrAli = true;
        mView.showProgressLoading();
        Subscription subscription = DataRepository.getInstance().requestWXPayResult(mOrderNum)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mObserver);
        addSubscrebe(subscription);
    }

    @Override
    public void requestAliPayInfo(double payMoney, String userIP) {
        if (mLimitMoney >= 0 && mLimitMoney < payMoney) {
            mView.showLimitMoneyTip(MoneyUtils.formatMoney(mLimitMoney));
            return;
        }
        mRetryCount = 0;
        mView.showProgressLoading();
        Subscription subscription = DataRepository.getInstance().requestAliPayInfo(payMoney, userIP)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AliPayInfoBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.dismissProgressLoading();
                            if (e instanceof ApiException) {
                                switch (((ApiException) e).getCode()) {
                                    case 6100:
                                    case 6101:
                                        mView.showDialogTip(R.string.pay_failed, false);
                                        break;
                                    case 6112:
//                                            mView.showDialogTip(R.string.pay_deposit_limit, false);
                                        mView.showLimitMoneyTip(e.getMessage());
                                        break;
                                    case 7000:
                                    case 7001:
                                    case 2304:
                                    case 30100:
                                        mView.pleaseLoginTip();
                                        break;
                                    default:
                                        mView.showDialogTip(R.string.network_fault, false);
                                        break;
                                }
                            } else {
                                mView.showDialogTip(R.string.network_fault, false);
                            }
                        }
                    }

                    @Override
                    public void onNext(AliPayInfoBean aliPayInfoBean) {
                        if (mView != null) {
                            mView.dismissProgressLoading();
                            if (aliPayInfoBean != null) {
                                mView.aliPayReq(aliPayInfoBean.mPayParam);
                                mOrderNum = aliPayInfoBean.mOrderNum;
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void requestAliPayResult() {
        mIsWXOrAli = false;
        mView.showProgressLoading();
        Subscription subscription = DataRepository.getInstance().requestAliPayResult(mOrderNum)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mObserver);
        addSubscrebe(subscription);
    }

    private Observer<Boolean> mObserver = new Observer<Boolean>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            if (mView != null) {
                mView.dismissProgressLoading();
                if (e instanceof ApiException) {
                    switch (((ApiException) e).getCode()) {
                        case 8000:
                        case 8001:
                            mView.showDialogTip(R.string.pay_dealing, false);
                            break;
                        case 7000:
                        case 7001:
                        case 2304:
                        case 30100:
                            mView.pleaseLoginTip();
                            break;
                        default:
                            mView.showDialogTip(R.string.network_fault, false);
                            break;
                    }
                } else {
                    mView.showDialogTip(R.string.network_fault, false);
                }
            }
        }

        @Override
        public void onNext(Boolean aBoolean) {
            if (mView != null) {
                if (aBoolean) {
                    mView.dismissProgressLoading();
                    mView.showDialogTip(R.string.pay_success, true);
                } else {
                    if (mRetryCount < 3) {
                        mRetryCount++;
                        mHandler.sendEmptyMessageDelayed(1000, 1000);
                    } else {
                        mView.dismissProgressLoading();
                        mView.showDialogTip(R.string.pay_failed, false);
                    }
                }
            }
        }
    };
}
