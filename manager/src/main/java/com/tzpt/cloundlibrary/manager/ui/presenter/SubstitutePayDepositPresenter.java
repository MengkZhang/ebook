package com.tzpt.cloundlibrary.manager.ui.presenter;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseResponseCode;
import com.tzpt.cloundlibrary.manager.base.RxPresenter;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.modle.remote.exception.ApiException;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.AliPayInfoVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.PayResultVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.WXPayInfoVo;
import com.tzpt.cloundlibrary.manager.ui.contract.SubstitutePayDepositContract;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/12/17.
 */

public class SubstitutePayDepositPresenter extends RxPresenter<SubstitutePayDepositContract.View>
        implements SubstitutePayDepositContract.Presenter, BaseResponseCode {

    private String mOrderNum;
    private int mRetryCount;
    private boolean mIsWXOrAli;//true表示微信 false表示支付宝
    private String mReaderId;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1000) {
                if (mIsWXOrAli) {
                    requestWXPayResult(mReaderId);
                } else {
                    requestAliPayResult(mReaderId);
                }
            }
        }
    };

    @Override
    public void getLibBalanceInfo() {
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
                                        mView.showDialogTip(R.string.network_fault);
                                        break;
                                }
                            } else {
                                mView.showDialogTip(R.string.network_fault);
                            }
                        }
                    }

                    @Override
                    public void onNext(Double aDouble) {
                        if (mView != null) {
                            mView.dismissProgressDialog();
                            mView.setBalanceInfo(aDouble);
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void payDeposit(String readerId, double money) {
        Subscription subscription = DataRepository.getInstance().substituteChargeLibDeposit(readerId, money)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
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
                                        mView.showDialogTip(R.string.network_fault);
                                        break;
                                }
                            } else {
                                mView.showDialogTip(R.string.network_fault);
                            }
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (mView != null) {
                            mView.dismissProgressDialog();
                            mView.showDialogTip(R.string.pay_success);
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void requestWXPayInfo(double payMoney, String userIP, String readerId) {
        mRetryCount = 0;
        mView.showProgressDialog();
        Subscription subscription = DataRepository.getInstance().requestWXPayInfo(payMoney, userIP, readerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<WXPayInfoVo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.dismissProgressDialog();
                            mView.showDialogTip(R.string.network_fault);
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
                                mView.dismissProgressDialog();
                                if (wxPayInfoVo.data != null) {
                                    switch (wxPayInfoVo.data.errorCode) {
                                        case ERROR_CODE_KICK_OUT:
                                        case ERROR_CODE_7020:
                                        case ERROR_CODE_7021:
                                        case ERROR_CODE_7022:
                                        case ERROR_CODE_2303:
                                            mView.setNoPermissionDialog(R.string.kicked_offline);
                                            break;
                                        case ERROR_CODE_1006:
                                            mView.setNoPermissionDialog(R.string.operate_timeout);
                                            break;
                                        case ERROR_CODE_6100:
                                        case ERROR_CODE_6101:
                                        case ERROR_CODE_6102:
                                            mView.showDialogTip(R.string.pay_failed);
                                            break;
                                        default:
                                            mView.showDialogTip(R.string.network_fault);
                                            break;
                                    }
                                } else {
                                    mView.showDialogTip(R.string.network_fault);
                                }
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void requestWXPayResult(String readerId) {
        mIsWXOrAli = true;
        mReaderId = readerId;
        mView.showProgressDialog();
        Subscription subscription = DataRepository.getInstance().requestWXPayResult(mOrderNum, readerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mObserver);
        addSubscrebe(subscription);
    }

    @Override
    public void requestAliPayInfo(double payMoney, String userIP, String readerId) {
        mRetryCount = 0;
        mView.showProgressDialog();
        Subscription subscription = DataRepository.getInstance().requestAliPayInfo(payMoney, userIP, readerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AliPayInfoVo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.dismissProgressDialog();
                            mView.showDialogTip(R.string.network_fault);
                        }
                    }

                    @Override
                    public void onNext(AliPayInfoVo aliPayInfoVo) {
                        if (mView != null) {
                            if (aliPayInfoVo.status == CODE_SUCCESS) {
                                mOrderNum = aliPayInfoVo.data.orderNum;
                                mView.aliPayReq(aliPayInfoVo.data.payParam);
                            } else {
                                mView.dismissProgressDialog();
                                if (aliPayInfoVo.data != null) {
                                    switch (aliPayInfoVo.data.errorCode) {
                                        case ERROR_CODE_KICK_OUT:
                                        case ERROR_CODE_7020:
                                        case ERROR_CODE_7021:
                                        case ERROR_CODE_7022:
                                        case ERROR_CODE_2303:
                                            mView.setNoPermissionDialog(R.string.kicked_offline);
                                            break;
                                        case ERROR_CODE_1006:
                                            mView.setNoPermissionDialog(R.string.operate_timeout);
                                            break;
                                        case ERROR_CODE_6100:
                                        case ERROR_CODE_6101:
                                            mView.showDialogTip(R.string.pay_failed);
                                            break;
                                        default:
                                            mView.showDialogTip(R.string.network_fault);
                                            break;
                                    }
                                } else {
                                    mView.showDialogTip(R.string.network_fault);
                                }
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void requestAliPayResult(String readerId) {
        mIsWXOrAli = false;
        mReaderId = readerId;
        mView.showProgressDialog();
        Subscription subscription = DataRepository.getInstance().requestAliPayResult(mOrderNum, readerId)
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
                mView.dismissProgressDialog();
                mView.showDialogTip(R.string.network_fault);
            }
        }

        @Override
        public void onNext(PayResultVo payResultVo) {
            if (mView != null) {
                if (payResultVo.status == CODE_SUCCESS) {
                    if (payResultVo.data.value) {
                        mView.dismissProgressDialog();
                        mView.showDialogTip(R.string.pay_success);
                    } else {
                        if (mRetryCount < 3) {
                            mRetryCount++;
                            mHandler.sendEmptyMessageDelayed(1000, 1000);
                        } else {
                            mView.dismissProgressDialog();
                            mView.showDialogTip(R.string.pay_failed);
                        }
                    }
                } else {
                    mView.dismissProgressDialog();
                    if (payResultVo.data != null) {
                        switch (payResultVo.data.errorCode) {
                            case ERROR_CODE_8000:
                            case ERROR_CODE_8001:
                                mView.showDialogTip(R.string.pay_dealing);
                                break;
                            case ERROR_CODE_KICK_OUT:
                            case ERROR_CODE_7020:
                            case ERROR_CODE_7021:
                            case ERROR_CODE_7022:
                            case ERROR_CODE_2303:
                                mView.setNoPermissionDialog(R.string.kicked_offline);
                                break;
                            case ERROR_CODE_1006:
                                mView.setNoPermissionDialog(R.string.operate_timeout);
                                break;
                            default:
                                mView.showDialogTip(R.string.network_fault);
                                break;
                        }
                    } else {
                        mView.showDialogTip(R.string.network_fault);
                    }
                }
            }
        }
    };
}
