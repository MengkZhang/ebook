package com.tzpt.cloundlibrary.manager.ui.presenter;

import android.text.TextUtils;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseResponseCode;
import com.tzpt.cloundlibrary.manager.base.RxPresenter;
import com.tzpt.cloundlibrary.manager.bean.LibraryInfo;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.RefundInfoVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.VerifyLibraryOperatorPswVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.WithdrawDepositVo;
import com.tzpt.cloundlibrary.manager.ui.contract.RefundPlatformDepositContract;
import com.tzpt.cloundlibrary.manager.utils.MD5Util;
import com.tzpt.cloundlibrary.manager.utils.MoneyUtils;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 提现
 * Created by ZhiqiangJia on 2017-10-24.
 */

public class RefundPlatformDepositPresenter extends RxPresenter<RefundPlatformDepositContract.View> implements
        RefundPlatformDepositContract.Presenter, BaseResponseCode {
    private boolean mIsNoWithdrawable;
    private String mNoWithdrawMsg;
    private double mRefundableDeposit;

    @Override
    public void requestRefundInfo() {
        mView.showProgressLoading();
        Subscription subscription = DataRepository.getInstance().requestRefundInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RefundInfoVo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.dismissProgressLoading();
                            mView.showDialogMsg(R.string.network_fault, true);
                        }
                    }

                    @Override
                    public void onNext(RefundInfoVo refundInfoVo) {
                        if (mView != null) {
                            mView.dismissProgressLoading();
                            if (refundInfoVo.status == CODE_SUCCESS) {
                                if (null != refundInfoVo.data) {
                                    LibraryInfo libraryInfo = DataRepository.getInstance().getLibraryInfo();
                                    mView.setRefundAccountTitle(libraryInfo.mHaveRefundAccount);

                                    mView.setRefundableDeposit(MoneyUtils.formatMoney(refundInfoVo.data.availableBalance));
                                    mIsNoWithdrawable = refundInfoVo.data.isLimit;
                                    if (refundInfoVo.data.maxAmount > 0
                                            && refundInfoVo.data.availableBalance > refundInfoVo.data.maxAmount) {
                                        mRefundableDeposit = refundInfoVo.data.maxAmount;
                                    } else {
                                        mRefundableDeposit = refundInfoVo.data.availableBalance;
                                    }
                                    mNoWithdrawMsg = refundInfoVo.data.limitMessage;
                                } else {
                                    mView.showDialogMsg(R.string.network_fault, true);
                                }
                            } else {
                                if (null != refundInfoVo.data) {
                                    switch (refundInfoVo.data.errorCode) {
                                        case ERROR_CODE_KICK_OUT:
                                            mView.showNoPermissionDialog(R.string.kicked_offline);
                                            break;
                                        case ERROR_CODE_1006:
                                            mView.showNoPermissionDialog(R.string.operate_timeout);
                                            break;
                                        default:
                                            mView.showDialogMsg(R.string.network_fault, true);
                                            break;
                                    }
                                } else {
                                    mView.showDialogMsg(R.string.network_fault, true);
                                }

                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void submitRefundDeposit(String adminPwd, final String deposit, final String createIp) {
        if (TextUtils.isEmpty(deposit)) {
            mView.showDialogMsg(R.string.money_not_empty, false);
            return;
        }
        if (TextUtils.isEmpty(adminPwd)) {
            mView.showDialogMsg(R.string.password_cannot_be_empty, false);
            return;
        }
        if (!MoneyUtils.isMoney(deposit)) {
            mView.showDialogMsg(R.string.enter_correct_amount, false);
            return;
        }
        if (MoneyUtils.stringToDouble(deposit) < 1) {
            mView.showDialogMsg(R.string.withdraw_deposit_more_than_one, false);
            return;
        }
        if (mIsNoWithdrawable) {
            mView.showDialogMsg(mNoWithdrawMsg, false);
            return;
        }
        if (MoneyUtils.stringToDouble(deposit) > mRefundableDeposit) {
            mView.withdrawDepositLimitTip(MoneyUtils.formatMoney(mRefundableDeposit));
            return;
        }
        mView.showProgressLoading();
        LibraryInfo libraryInfo = DataRepository.getInstance().getLibraryInfo();
        if (libraryInfo != null) {
            Subscription subscription = DataRepository.getInstance().checkOperatorPsw(libraryInfo.mHallCode, libraryInfo.mOperaterName, MD5Util.MD5(adminPwd))
                    .flatMap(new Func1<VerifyLibraryOperatorPswVo, Observable<WithdrawDepositVo>>() {
                        @Override
                        public Observable<WithdrawDepositVo> call(VerifyLibraryOperatorPswVo verifyPswVo) {
                            if (verifyPswVo.status == CODE_SUCCESS) {
                                if (null != verifyPswVo.data && verifyPswVo.data.value) {
                                    return DataRepository.getInstance().requestWithdrawDeposit(MoneyUtils.stringToDouble(deposit), createIp);
                                }
                            }
                            return DataRepository.getInstance().refundAdminDepositError(CODE_SERVICE_ERROR, ERROR_CODE_3103);
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<WithdrawDepositVo>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (mView != null) {
                                mView.dismissProgressLoading();
                                mView.showDialogMsg(R.string.network_fault, false);
                            }
                        }

                        @Override
                        public void onNext(WithdrawDepositVo withdrawDepositVo) {
                            if (mView != null) {
                                mView.dismissProgressLoading();
                                if (withdrawDepositVo.status == CODE_SUCCESS) {
                                    mView.showSuccessDialog(R.string.successful_withdrawal_application);
                                } else {
                                    if (null != withdrawDepositVo.data) {
                                        switch (withdrawDepositVo.data.errorCode) {
                                            case ERROR_CODE_KICK_OUT:
                                                mView.showNoPermissionDialog(R.string.kicked_offline);
                                                break;
                                            case ERROR_CODE_1006:
                                                mView.showNoPermissionDialog(R.string.operate_timeout);
                                                break;
                                            case ERROR_CODE_2500:
                                            case ERROR_CODE_2501:
                                                mView.showDialogMsg(R.string.limit_month, false);
                                                break;
                                            case ERROR_CODE_6105:
                                                mView.showDialogMsg(R.string.money_not_enough, false);
                                                break;
                                            case ERROR_CODE_6106:
                                                mView.showDialogMsg(R.string.withdraw_deposit_wx, false);
                                                break;
                                            case ERROR_CODE_2502:
                                                mView.showDialogMsg(R.string.limit_year, false);
                                                break;
                                            case ERROR_CODE_3400:
                                                mView.showDialogMsg(R.string.failure_withdrawal_application, false);
                                                break;
                                            case ERROR_CODE_6113:
                                                mView.noAliPayAccount(R.string.no_refund_account);
                                                break;
                                            case ERROR_CODE_3103:
                                                mView.showDialogMsg(R.string.psw_error, false);
                                                break;
                                            case ERROR_CODE_2503:
                                            default:
                                                mView.showDialogMsg(R.string.network_fault, false);
                                                break;
                                        }
                                    } else {
                                        mView.showDialogMsg(R.string.network_fault, false);
                                    }
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        }

    }
}
