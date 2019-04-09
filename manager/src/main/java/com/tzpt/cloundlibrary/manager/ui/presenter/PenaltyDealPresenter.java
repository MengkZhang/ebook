package com.tzpt.cloundlibrary.manager.ui.presenter;

import android.text.TextUtils;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseResponseCode;
import com.tzpt.cloundlibrary.manager.base.RxPresenter;
import com.tzpt.cloundlibrary.manager.bean.LibraryInfo;
import com.tzpt.cloundlibrary.manager.bean.PenaltyBean;
import com.tzpt.cloundlibrary.manager.bean.PenaltyDealInfo;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.modle.remote.exception.ApiException;
import com.tzpt.cloundlibrary.manager.ui.contract.PenaltyDealContract;
import com.tzpt.cloundlibrary.manager.utils.MD5Util;
import com.tzpt.cloundlibrary.manager.utils.MoneyUtils;
import com.tzpt.cloundlibrary.manager.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/12/14.
 */

public class PenaltyDealPresenter extends RxPresenter<PenaltyDealContract.View>
        implements PenaltyDealContract.Presenter, BaseResponseCode {

    private PenaltyDealInfo mPenaltyDealInfo;

    @Override
    public void getLibraryInfo() {
        LibraryInfo libraryInfo = DataRepository.getInstance().getLibraryInfo();
        mView.setRightBtnStatus(libraryInfo.mAgreementLevel);
    }

    @Override
    public void getPenaltyList(String readerId) {
        autoDealPenalty(readerId);
    }

    @Override
    public void applyPenaltyFree(String applyRemark, String pwd) {
        if (TextUtils.isEmpty(applyRemark)) {
            mView.showDialogTip("免单理由不能为空！");
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            mView.showDialogTip("密码不能为空！");
            return;
        }
        mView.showProgressLoading();
        List<Long> ids = new ArrayList<>();
        for (PenaltyBean info : mPenaltyDealInfo.mPenaltyList) {
            ids.add(info.mPenaltyId);
        }
        Subscription subscription = DataRepository.getInstance().applyPenaltyFree(applyRemark,
                ids, mPenaltyDealInfo.mReaderInfo.mReaderId, MD5Util.MD5(pwd))
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
                            mView.dismissProgressLoading();
                            if (aBoolean) {
                                mView.applyChargeFreePenaltySuccess(mPenaltyDealInfo.mReaderInfo.mReaderId);
                            } else {
                                getPenaltyListInfo(mPenaltyDealInfo.mReaderInfo.mReaderId);
                                mView.showDialogTip(R.string.network_fault);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void chargePenalty(String pwd) {
        if (TextUtils.isEmpty(pwd)) {
            mView.showDialogTip("密码不能为空！");
            return;
        }
        mView.showProgressLoading();
        LibraryInfo libraryInfo = DataRepository.getInstance().getLibraryInfo();
        if (libraryInfo.mAgreementLevel == 2) {
            //上交罚金界面
            Subscription subscription = DataRepository.getInstance().checkOperatorPsw(MD5Util.MD5(pwd))
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
                                        case ERROR_CODE_3103://密码错误-验证密码错误
                                            mView.showDialogTip(R.string.psw_error);
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
                                mView.dismissProgressLoading();
                                if (aBoolean) {
                                    double penalty = MoneyUtils.sub(mPenaltyDealInfo.mReaderInfo.mNotApplyPenalty,
                                            mPenaltyDealInfo.mReaderInfo.getPlatformUsableDeposit());
                                    mView.turnToSubstituteChargePenalty(mPenaltyDealInfo.mReaderInfo.mReaderId, penalty);
                                } else {
                                    mView.showDialogTip(R.string.psw_error);
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        } else {
            //直接收
            Subscription subscription = DataRepository.getInstance().chargePenalty(MD5Util.MD5(pwd),
                    mPenaltyDealInfo.mReaderInfo.mReaderId, mPenaltyDealInfo.mReaderInfo.mNotApplyPenalty)
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
                                        case ERROR_CODE_3103://密码错误-验证密码错误
                                            mView.showDialogTip(R.string.psw_error);
                                            break;
                                        case ERROR_CODE_3400:
                                            mView.showDialogTip(R.string.deposit_not_enough);
                                            break;
                                        case ERROR_CODE_6112://交押金金额超限值
                                            mView.showDialogTip(R.string.amount_the_deposit_exceeds_the_limit);
                                            break;
                                        case ERROR_CODE_UNKNOWN:
                                        case ERROR_CODE_PARSE:
                                        case ERROR_CODE_NETWORK:
                                        case ERROR_CODE_HTTP:
                                            mView.showDialogTip(R.string.network_fault);
                                            break;
                                        default:
                                            mView.showDialogTip(R.string.pay_penalty_fail);
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
                                mView.dismissProgressLoading();
                                if (aBoolean) {
                                    autoDealPenalty(mPenaltyDealInfo.mReaderInfo.mReaderId);
                                } else {
                                    mView.showDialogTip(R.string.pay_penalty_fail);
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        }

    }

    @Override
    public void clickRightBtn() {
        LibraryInfo libraryInfo = DataRepository.getInstance().getLibraryInfo();
        double penalty;
        if (libraryInfo.mAgreementLevel == 2) {
            penalty = MoneyUtils.sub(mPenaltyDealInfo.mReaderInfo.mNotApplyPenalty,
                    mPenaltyDealInfo.mReaderInfo.getPlatformUsableDeposit());
        } else {
            penalty = mPenaltyDealInfo.mReaderInfo.mNotApplyPenalty;
        }
        if (libraryInfo.mAgreementLevel == 1) {
            mView.exitDealPenalty();
        } else if (libraryInfo.mAgreementLevel == 2) {
            mView.showSubstituteChargePenalty(penalty);
        } else {
            mView.showCashChargePenalty(penalty);
        }
    }

    /**
     * 自动处理罚金
     *
     * @param readerId 读者ID
     */
    private void autoDealPenalty(final String readerId) {
        mView.showProgressLoading();
        Subscription subscription = DataRepository.getInstance().autoDealAllPenalty(readerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Double>() {
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
                                        mView.showDialogTipAndFinish(R.string.network_fault);
                                        break;
                                }
                            } else {
                                mView.showDialogTipAndFinish(R.string.network_fault);
                            }
                        }
                    }

                    @Override
                    public void onNext(Double aDouble) {
                        if (mView != null) {
                            if (aDouble > 0) {
                                getPenaltyListInfo(readerId);
                            } else {
                                mView.dismissProgressLoading();
                                mView.dealPenaltySuccess(readerId);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 获取罚金列表
     *
     * @param readerId 读者ID
     */
    private void getPenaltyListInfo(String readerId) {
        Subscription subscription = DataRepository.getInstance().getPenaltyList(readerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PenaltyDealInfo>() {
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
                                        mView.showDialogTipAndFinish(R.string.network_fault);
                                        break;
                                }
                            } else {
                                mView.showDialogTipAndFinish(R.string.network_fault);
                            }
                        }
                    }

                    @Override
                    public void onNext(PenaltyDealInfo penaltyDealInfo) {
                        if (mView != null) {
                            mView.dismissProgressLoading();
                            mPenaltyDealInfo = penaltyDealInfo;
                            mView.setReaderNameNumber(penaltyDealInfo.mReaderInfo.mCardName + " "
                                    + StringUtils.setIdCardNumberForReader(penaltyDealInfo.mReaderInfo.mIdCard));
                            mView.setNoBackSum("未还" + penaltyDealInfo.mReaderInfo.mBorrowingNum);
                            //押金信息
                            LibraryInfo libraryInfo = DataRepository.getInstance().getLibraryInfo();
                            if (libraryInfo.mAgreementLevel == 1) {
                                mView.setDepositInfo("可用共享押金" + MoneyUtils.formatMoney(penaltyDealInfo.mReaderInfo.getPlatformUsableDeposit()));
                            } else if (libraryInfo.mAgreementLevel == 2
                                    || libraryInfo.mAgreementLevel == 3) {
                                mView.setDepositInfo("共享" + MoneyUtils.formatMoney(penaltyDealInfo.mReaderInfo.getPlatformUsableDeposit())
                                        + " 馆" + MoneyUtils.formatMoney(penaltyDealInfo.mReaderInfo.getOfflineUsableDeposit()));
                            } else {
                                mView.setDepositInfo("可用馆押金" + MoneyUtils.formatMoney(penaltyDealInfo.mReaderInfo.getOfflineUsableDeposit()));
                            }

                            if (penaltyDealInfo.mPenaltyList.size() > 0) {
                                mView.setPenaltyList(penaltyDealInfo.mPenaltyList);
                                double totalPrice = 0;
                                double totalAttachPrice = 0;
                                for (PenaltyBean info : penaltyDealInfo.mPenaltyList) {
                                    totalPrice = MoneyUtils.add(totalPrice, info.mPrice);
                                    totalAttachPrice = MoneyUtils.add(totalAttachPrice, info.mAttachPrice);
                                }

                                mView.setTotalInfo(penaltyDealInfo.mPenaltyList.size(),
                                        MoneyUtils.add(totalPrice, totalAttachPrice), penaltyDealInfo.mReaderInfo.mNotApplyPenalty);
                            } else {
                                mView.setTotalInfo(0, 0, 0);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }
}
