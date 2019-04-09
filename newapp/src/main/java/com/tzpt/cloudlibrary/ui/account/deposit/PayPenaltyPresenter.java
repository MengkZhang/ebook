package com.tzpt.cloudlibrary.ui.account.deposit;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.business_bean.AliPayInfoBean;
import com.tzpt.cloudlibrary.business_bean.PenaltyDealResultBean;
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
 * 交罚金
 * Created by tonyjia on 2018/8/28.
 */
public class PayPenaltyPresenter extends RxPresenter<PayPenaltyContract.View> implements
        PayPenaltyContract.Presenter {

    private double mHandlePenalty;
    private String mLibCode;
    private int mRetryCount;
    private boolean mIsWXOrAli;
    private String mOrderNum;

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

    /**
     * 处理罚金接口
     */
    @Override
    public void handleUserPenalty(String libCode) {
        mLibCode = libCode;
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (TextUtils.isEmpty(readerId)) {
            mView.dismissProgressDialog();
            return;
        }
        Subscription subscription = DataRepository.getInstance().dealUserPenalty(readerId, libCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PenaltyDealResultBean>() {
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
                                    mView.showFinishMsgDialog(R.string.network_fault);
                                }
                            } else {
                                mView.showFinishMsgDialog(R.string.network_fault);
                            }
                        }
                    }

                    @Override
                    public void onNext(PenaltyDealResultBean penaltyDealResultBean) {
                        if (mView != null) {
                            mView.dismissProgressDialog();
                            if (penaltyDealResultBean != null) {
                                if (penaltyDealResultBean.mFailPenalty > 0) {
                                    mHandlePenalty = penaltyDealResultBean.mFailPenalty;
                                    mView.setPenaltyText(MoneyUtils.formatMoney(penaltyDealResultBean.mFailPenalty), true);
                                } else {
                                    if (penaltyDealResultBean.mSucceedPenalty > 0) {
                                        mView.showPayPenaltyDialog(MoneyUtils.formatMoney(penaltyDealResultBean.mSucceedPenalty));
                                    }
                                    mView.setPenaltyText("0.00", false);
                                }
                            } else {
                                mView.showFinishMsgDialog(R.string.network_fault);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);

    }

    /**
     * 请求微信支付
     */
    @Override
    public void requestWeChatPay(String userIP) {
        mRetryCount = 0;
        mView.showProgressDialog();
        Subscription subscription = DataRepository.getInstance().requestWXPayInfo(mHandlePenalty, userIP)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<WXPayInfoBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.dismissProgressDialog();
                            if (e instanceof ApiException) {
                                switch (((ApiException) e).getCode()) {
                                    case 6100:
                                    case 6101:
                                    case 6102:
                                    case 6112:
                                        mView.showFinishMsgDialog(R.string.pay_failed);
                                        break;
                                    case 7000:
                                    case 7001:
                                    case 2304:
                                    case 30100:
                                        mView.pleaseLoginTip();
                                        break;
                                    default:
                                        mView.showFinishMsgDialog(R.string.network_fault);
                                        break;
                                }
                            } else {
                                mView.showFinishMsgDialog(R.string.network_fault);
                            }
                        }
                    }

                    @Override
                    public void onNext(WXPayInfoBean wxPayInfoBean) {
                        if (mView != null) {
                            mView.dismissProgressDialog();
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
                                mView.showFinishMsgDialog(R.string.network_fault);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 请求支付宝支付
     */
    @Override
    public void requestAlipay(String userIP) {
        mRetryCount = 0;
        mView.showProgressDialog();
        Subscription subscription = DataRepository.getInstance().requestAliPayInfo(mHandlePenalty, userIP)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AliPayInfoBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.dismissProgressDialog();
                            if (e instanceof ApiException) {
                                switch (((ApiException) e).getCode()) {
                                    case 6100:
                                    case 6101:
                                    case 6112:
                                        mView.showFinishMsgDialog(R.string.pay_failed);
                                        break;
                                    case 7000:
                                    case 7001:
                                    case 2304:
                                    case 30100:
                                        mView.pleaseLoginTip();
                                        break;
                                    default:
                                        mView.showFinishMsgDialog(R.string.network_fault);
                                        break;
                                }
                            } else {
                                mView.showFinishMsgDialog(R.string.network_fault);
                            }
                        }
                    }

                    @Override
                    public void onNext(AliPayInfoBean aliPayInfoBean) {
                        if (mView != null) {
                            mView.dismissProgressDialog();
                            if (aliPayInfoBean != null) {
                                mView.aliPayReq(aliPayInfoBean.mPayParam);
                                mOrderNum = aliPayInfoBean.mOrderNum;
                            } else {
                                mView.showFinishMsgDialog(R.string.network_fault);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 请求微信支付状态
     */
    @Override
    public void requestWXPayResult() {
        mIsWXOrAli = true;
        mView.showProgressDialog();
        Subscription subscription = DataRepository.getInstance().requestWXPayResult(mOrderNum)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mObserver);
        addSubscrebe(subscription);
    }

    /**
     * 请求支付宝支付状态
     */
    @Override
    public void requestAliPayResult() {
        mIsWXOrAli = false;
        mView.showProgressDialog();
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
                mView.dismissProgressDialog();
                if (e instanceof ApiException) {
                    switch (((ApiException) e).getCode()) {
                        case 8000:
                        case 8001:
                            mView.showMsgDialog(R.string.pay_dealing);
                            break;
                        case 7000:
                        case 7001:
                        case 2304:
                        case 30100:
                            mView.pleaseLoginTip();
                            break;
                        default:
                            mView.showMsgDialog(R.string.network_fault);
                            break;
                    }
                } else {
                    mView.showMsgDialog(R.string.network_fault);
                }
            }
        }

        @Override
        public void onNext(Boolean aBoolean) {
            if (mView != null) {
                if (aBoolean) {
                    mView.dismissProgressDialog();
                    handleUserPenalty(mLibCode);
                } else {
                    if (mRetryCount < 3) {
                        mRetryCount++;
                        mHandler.sendEmptyMessageDelayed(1000, 1000);
                    } else {
                        mView.dismissProgressDialog();
                        mView.showMsgDialog(R.string.pay_failed);
                    }
                }
            }
        }
    };

    @Override
    public void releaseHandler() {
        mHandler.removeMessages(1000);
        mHandler = null;
    }
}
