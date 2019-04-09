package com.tzpt.cloundlibrary.manager.ui.presenter;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseResponseCode;
import com.tzpt.cloundlibrary.manager.base.RxPresenter;
import com.tzpt.cloundlibrary.manager.bean.EntranceGuardResult;
import com.tzpt.cloundlibrary.manager.bean.LibraryInfo;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.modle.remote.exception.ApiException;
import com.tzpt.cloundlibrary.manager.ui.contract.EntranceGuardScanContract;
import com.tzpt.cloundlibrary.manager.utils.MoneyUtils;
import com.tzpt.cloundlibrary.manager.utils.StringUtils;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 门禁检查
 * Created by Administrator on 2017/7/12.
 */
public class EntranceGuardScanPresenter extends RxPresenter<EntranceGuardScanContract.View>
        implements EntranceGuardScanContract.Presenter,
        BaseResponseCode {

    @Override
    public void entranceCheck(String barNumber) {
        Subscription subscription = DataRepository.getInstance().entranceCheck(barNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<EntranceGuardResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            if (e instanceof ApiException) {
                                switch (((ApiException) e).getCode()) {
                                    case ERROR_CODE_KICK_OUT:
                                        mView.noPermissionPrompt(R.string.kicked_offline);
                                        break;
                                    case ERROR_CODE_1006:
                                        mView.noPermissionPrompt(R.string.operate_timeout);
                                        break;
                                    case ERROR_CODE_2207:
                                        mView.setEntranceStateError(R.string.bar_code_error);
                                        break;
                                    case ERROR_CODE_2209:
                                        mView.setEntranceStateError(R.string.guard_have_dish_deficient);
                                        break;
                                    case ERROR_CODE_2211:
                                        mView.setEntranceStateError(R.string.guard_have_lost);
                                        break;
                                    case ERROR_CODE_2212:
                                        mView.setEntranceStateError(R.string.guard_have_stuck_between_old);
                                        break;
                                    case ERROR_CODE_2217:
                                        mView.setEntranceStateError(R.string.guard_have_not_borrow);
                                        break;
                                    case ERROR_CODE_2218:
                                        mView.setEntranceStateError(R.string.guard_have_been_sale);
                                        break;
                                    case ERROR_CODE_UNKNOWN:
                                    case ERROR_CODE_PARSE:
                                    case ERROR_CODE_NETWORK:
                                    case ERROR_CODE_HTTP:
                                        mView.showMsgDialog(R.string.network_fault);
                                        break;
                                    default:
                                        mView.setEntranceStateError(R.string.bar_code_error);
                                        break;
                                }
                            } else {
                                mView.showMsgDialog(R.string.network_fault);
                            }
                        }
                    }

                    @Override
                    public void onNext(EntranceGuardResult entranceGuardResult) {
                        if (mView != null) {
                            if ("1".equals(entranceGuardResult.mResult)) {
                                mView.setEntranceStateLost((entranceGuardResult.mReaderInfo.mCardName + " " + StringUtils.setIdCardNumberForReader(entranceGuardResult.mReaderInfo.mIdCard))
                                        , entranceGuardResult.mReaderInfo.mPhone);
                            } else if ("2".equals(entranceGuardResult.mResult)) {
                                mView.setEntranceStatePass();
                                mView.setReaderName(entranceGuardResult.mReaderInfo.mCardName + " " + StringUtils.setIdCardNumberForReader(entranceGuardResult.mReaderInfo.mIdCard));
                                mView.setBorrowableSum("可借" + entranceGuardResult.mReaderInfo.mBorrowableSum);
                                //押金信息
                                LibraryInfo libraryInfo = DataRepository.getInstance().getLibraryInfo();
                                if (libraryInfo.mDeposit == 0) {
                                    if (entranceGuardResult.mReaderInfo.mNotApplyPenalty > 0) {
                                        mView.setDepositOrPenalty("欠罚金" + MoneyUtils.formatMoney(entranceGuardResult.mReaderInfo.mNotApplyPenalty));
                                    } else {
                                        mView.setDepositOrPenalty("欠罚金0.00");
                                    }
                                } else {
                                    if (libraryInfo.mAgreementLevel == 1) {
                                        mView.setDepositOrPenalty("可用共享押金" + MoneyUtils.formatMoney(entranceGuardResult.mReaderInfo.getPlatformUsableDeposit()));
                                    } else if (libraryInfo.mAgreementLevel == 2
                                            || libraryInfo.mAgreementLevel == 3) {
                                        mView.setDepositOrPenalty("共享" + MoneyUtils.formatMoney(entranceGuardResult.mReaderInfo.getPlatformUsableDeposit())
                                                + " 馆" + MoneyUtils.formatMoney(entranceGuardResult.mReaderInfo.getOfflineUsableDeposit()));
                                    } else {
                                        mView.setDepositOrPenalty("可用馆押金" + MoneyUtils.formatMoney(entranceGuardResult.mReaderInfo.getOfflineUsableDeposit()));
                                    }
                                }
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }
}
