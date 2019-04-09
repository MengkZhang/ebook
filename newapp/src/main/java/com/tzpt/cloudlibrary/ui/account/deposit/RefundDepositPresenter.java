package com.tzpt.cloudlibrary.ui.account.deposit;

import android.text.TextUtils;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.business_bean.RefundDepositBean;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;
import com.tzpt.cloudlibrary.utils.MoneyUtils;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 退押金
 * Created by ZhiqiangJia on 2017-10-09.
 */
public class RefundDepositPresenter extends RxPresenter<RefundDepositContract.View> implements
        RefundDepositContract.Presenter {

    private RefundDepositBean mRefundDepositBean;

    @Override
    public void requestWithdrawInfo() {
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (TextUtils.isEmpty(readerId)){
            return;
        }
        mView.showProgressLoading();
        Subscription subscription = DataRepository.getInstance().dealUserPenaltyAuto(readerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Double>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            dealException(e);
                        }
                    }

                    @Override
                    public void onNext(Double aDouble) {
                        if (mView != null) {
                            if (aDouble > 0) {
                                mView.dismissProgressLoading();
                                mView.showHandlePenaltyDialog();
                            } else {
                                getRefundDepositInfo();
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 获取退押金信息
     */
    private void getRefundDepositInfo() {
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (TextUtils.isEmpty(readerId)) {
            return;
        }
        Subscription subscription = DataRepository.getInstance().getRefundDepositInfo(readerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RefundDepositBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            dealException(e);
                        }
                    }

                    @Override
                    public void onNext(RefundDepositBean refundDepositBean) {
                        if (mView != null) {
                            mView.dismissProgressLoading();
                            if (refundDepositBean != null) {
                                mRefundDepositBean = refundDepositBean;
                                mView.setAvailableBalance(MoneyUtils.formatMoney(refundDepositBean.mPlatformAvailableBalance),
                                        MoneyUtils.formatMoney(refundDepositBean.mLibAvailableBalance));
                            } else {
                                mView.showDialogTip(R.string.network_fault, true);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void submitRefundDeposit(String deposit, String userIp) {
        if (TextUtils.isEmpty(deposit)) {
            mView.showDialogTip(R.string.money_not_empty, false);
            return;
        }
        if (!MoneyUtils.isMoney(deposit)) {
            mView.showDialogTip(R.string.enter_correct_amount, false);
            return;
        }
        if (MoneyUtils.stringToDouble(deposit) < 1) {
            mView.showDialogTip(R.string.withdraw_deposit_more_than_one, false);
            return;
        }
        if (mRefundDepositBean.mNeedDealPenalty > 0) {
            mView.showHandlePenaltyDialog();
            return;
        }
        if (mRefundDepositBean.mNeedHandleFreePenalty > 0) {
            mView.showDialogTip("有罚金免单申请未完成，金额：" + MoneyUtils.formatMoney(mRefundDepositBean.mNeedHandleFreePenalty) + "元。请先处理罚金。", true);
            return;
        }
        if (mRefundDepositBean.mIsNoWithDrawable) {
            mView.showDialogTip(mRefundDepositBean.mNoWithdrawMsg, false);
            return;
        }
        double onlineDeposit = mRefundDepositBean.mPlatformAvailableBalance;
        if (MoneyUtils.stringToDouble(deposit) > onlineDeposit) {
            if (onlineDeposit > mRefundDepositBean.mMaxAmount && mRefundDepositBean.mMaxAmount > 0) {
                mView.withdrawDepositLimitTip(MoneyUtils.formatMoney(mRefundDepositBean.mMaxAmount), true);
            } else {
                mView.withdrawDepositLimitTip(MoneyUtils.formatMoney(onlineDeposit), false);
            }
            return;
        } else {
            if (onlineDeposit > mRefundDepositBean.mMaxAmount
                    && MoneyUtils.stringToDouble(deposit) > mRefundDepositBean.mMaxAmount
                    && mRefundDepositBean.mMaxAmount > 0) {
                mView.withdrawDepositLimitTip(MoneyUtils.formatMoney(mRefundDepositBean.mMaxAmount), true);
                return;
            }
        }
        mView.showLoadingDialog();
        Subscription subscription = DataRepository.getInstance().requestWithdraw(MoneyUtils.stringToDouble(deposit), userIp)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.dismissLoadingDialog();
                            if (e instanceof ApiException) {
                                switch (((ApiException) e).getCode()) {
                                    case 30100:
                                        mView.pleaseLoginTip();
                                        break;
                                    case 6106:
                                        mView.showDialogTip(R.string.withdraw_deposit_wx, false);
                                        break;
                                    default:
                                        mView.showDialogTip(R.string.withdraw_deposit_failed, false);
                                        break;
                                }
                            } else {
                                mView.showDialogTip(R.string.withdraw_deposit_failed, false);
                            }
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (mView != null) {
                            mView.dismissLoadingDialog();
                            if (aBoolean) {
                                mView.showSuccessDialog();
                            } else {
                                mView.showDialogTip(R.string.withdraw_deposit_failed, false);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void getOfflineDepositList() {
        mView.setOfflineDeposit(mRefundDepositBean.mOffLineDepositList);
    }

    private void dealException(Throwable e) {
        mView.dismissProgressLoading();
        if (e instanceof ApiException) {
            ApiException exception = (ApiException) e;
            switch (exception.getCode()) {
                case 30100:
                    mView.pleaseLoginTip();
                    break;
                default:
                    mView.showDialogTip(R.string.network_fault, true);
                    break;
            }
        } else {
            mView.showDialogTip(R.string.network_fault, true);
        }
    }
}
