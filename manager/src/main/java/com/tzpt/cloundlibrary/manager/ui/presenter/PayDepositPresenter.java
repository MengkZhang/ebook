package com.tzpt.cloundlibrary.manager.ui.presenter;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseResponseCode;
import com.tzpt.cloundlibrary.manager.base.RxPresenter;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.AliPayInfoVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.PayResultVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.WXPayInfoVo;
import com.tzpt.cloundlibrary.manager.ui.contract.PayDepositContract;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 交押金
 * Created by ZhiqiangJia on 2017-10-24.
 */

public class PayDepositPresenter extends RxPresenter<PayDepositContract.View> implements
        PayDepositContract.Presenter, BaseResponseCode {

    private String mOrderNum;
    private int mRetryCount;
    private boolean mIsWXOrAli;//true表示微信 false表示支付宝

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
    public void requestWXPayInfo(double payMoney, String userIP) {
        mRetryCount = 0;
        mView.showProgressLoading();
        Subscription subscription = DataRepository.getInstance().requestWXPayInfo(payMoney, userIP)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<WXPayInfoVo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.dismissProgressLoading();
                            mView.showDialogTip(R.string.network_fault, false);
                        }
                    }

                    @Override
                    public void onNext(WXPayInfoVo wxPayInfoVo) {
                        if (mView != null) {
                            if (wxPayInfoVo.status == CODE_SUCCESS) {
                                mView.wxPayReq(wxPayInfoVo.data.appid, wxPayInfoVo.data.partnerid,
                                        wxPayInfoVo.data.prepayid, wxPayInfoVo.data.packageName,
                                        wxPayInfoVo.data.noncestr, wxPayInfoVo.data.timestamp,
                                        wxPayInfoVo.data.sign);
                                mOrderNum = wxPayInfoVo.data.orderNum;
                            } else {
                                mView.dismissProgressLoading();
                                if (wxPayInfoVo.data != null) {
                                    switch (wxPayInfoVo.data.errorCode) {
                                        case ERROR_CODE_KICK_OUT:
                                        case ERROR_CODE_7020:
                                        case ERROR_CODE_7021:
                                        case ERROR_CODE_7022:
                                        case ERROR_CODE_2303:
                                            mView.showNoPermissionDialog(R.string.kicked_offline);
                                            break;
                                        case ERROR_CODE_1006:
                                            mView.showNoPermissionDialog(R.string.operate_timeout);
                                            break;
                                        case ERROR_CODE_6100:
                                        case ERROR_CODE_6101:
                                        case ERROR_CODE_6102:
                                            mView.showDialogTip(R.string.pay_failed, false);
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
        mRetryCount = 0;
        mView.showProgressLoading();
        Subscription subscription = DataRepository.getInstance().requestAliPayInfo(payMoney, userIP)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AliPayInfoVo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.dismissProgressLoading();
                            mView.showDialogTip(R.string.network_fault, false);
                        }
                    }

                    @Override
                    public void onNext(AliPayInfoVo aliPayInfoVo) {
                        if (mView != null) {
                            if (aliPayInfoVo.status == CODE_SUCCESS) {
                                mOrderNum = aliPayInfoVo.data.orderNum;
                                mView.aliPayReq(aliPayInfoVo.data.payParam);
                            } else {
                                mView.dismissProgressLoading();
                                if (aliPayInfoVo.data != null) {
                                    switch (aliPayInfoVo.data.errorCode) {
                                        case ERROR_CODE_KICK_OUT:
                                        case ERROR_CODE_7020:
                                        case ERROR_CODE_7021:
                                        case ERROR_CODE_7022:
                                        case ERROR_CODE_2303:
                                            mView.showNoPermissionDialog(R.string.kicked_offline);
                                            break;
                                        case ERROR_CODE_1006:
                                            mView.showNoPermissionDialog(R.string.operate_timeout);
                                            break;
                                        case ERROR_CODE_6100:
                                        case ERROR_CODE_6101:
                                            mView.showDialogTip(R.string.pay_failed, false);
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

    //支付结果查询观察者
    private Observer<PayResultVo> mObserver = new Observer<PayResultVo>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            if (mView != null) {
                mView.dismissProgressLoading();
                mView.showDialogTip(R.string.network_fault, false);
            }
        }

        @Override
        public void onNext(PayResultVo payResultVo) {
            if (mView != null) {
                if (payResultVo.status == CODE_SUCCESS) {
                    if (payResultVo.data.value) {
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
                } else {
                    mView.dismissProgressLoading();
                    if (payResultVo.data != null) {
                        switch (payResultVo.data.errorCode) {
                            case ERROR_CODE_8000:
                            case ERROR_CODE_8001:
                                mView.showDialogTip(R.string.pay_dealing, false);
                                break;
                            case ERROR_CODE_KICK_OUT:
                            case ERROR_CODE_7020:
                            case ERROR_CODE_7021:
                            case ERROR_CODE_7022:
                            case ERROR_CODE_2303:
                                mView.showNoPermissionDialog(R.string.kicked_offline);
                                break;
                            case ERROR_CODE_1006:
                                mView.showNoPermissionDialog(R.string.operate_timeout);
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
        }
    };
}
