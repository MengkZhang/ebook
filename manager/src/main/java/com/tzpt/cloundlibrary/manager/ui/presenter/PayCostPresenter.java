package com.tzpt.cloundlibrary.manager.ui.presenter;

import android.text.TextUtils;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseResponseCode;
import com.tzpt.cloundlibrary.manager.base.RxPresenter;
import com.tzpt.cloundlibrary.manager.bean.LibraryInfo;
import com.tzpt.cloundlibrary.manager.bean.OrderNumberInfo;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.AddDepositVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.ApplyPenaltyFreeVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.PayFineVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.VerifyLibraryOperatorPswVo;
import com.tzpt.cloundlibrary.manager.ui.contract.PayCostContract;
import com.tzpt.cloundlibrary.manager.utils.MD5Util;
import com.tzpt.cloundlibrary.manager.utils.MoneyUtils;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 交押金,罚金
 * Created by Administrator on 2017/7/8.
 */
public class PayCostPresenter extends RxPresenter<PayCostContract.View> implements
        PayCostContract.Presenter,
        BaseResponseCode {

    @Override
    public boolean checkPenaltyFreePermission() {
        return DataRepository.getInstance().getLibraryInfo().mPenaltyFreePermission;
    }

    @Override
    public void payCost(int flag, double payMoney, String pwd) {
//        String readerId = DataRepository.getInstance().getReaderInfo().mReaderId;
//        if (TextUtils.isEmpty(readerId)) {
//            mView.showErrorMsg("获取身份信息失败！");
//            return;
//        }
//        if (payMoney == 0) {
//            mView.showErrorMsg("金额不能为空！");
//            return;
//        }
//        if (TextUtils.isEmpty(pwd)) {
//            mView.showErrorMsg("密码不能为空！");
//            return;
//        }
//        OrderNumberInfo orderNumberInfo = DataRepository.getInstance().getOrderNumberInfo();
//        if (TextUtils.isEmpty(orderNumberInfo.mCodeNumber)) {
//            mView.showErrorMsg("单号不能为空！");
//            return;
//        }
//        //非押金模式(收罚金) 0
//        //押金模式(收罚金)  1
//        //押金模式(交押金)  2
//        if (flag == 2) {
//            payDeposit(readerId, orderNumberInfo.mCodeNumber, String.valueOf(payMoney), MD5Util.MD5(pwd));
//        } else {
//            payFine(readerId, orderNumberInfo.mCodeNumber, payMoney, MD5Util.MD5(pwd));
//        }
    }

    @Override
    public void applyFreeCharge(final String applyRemark, String pwd) {
//        if (TextUtils.isEmpty(applyRemark)) {
//            mView.showErrorMsg("免单理由不能为空！");
//            return;
//        }
//        final String idCardNum = DataRepository.getInstance().getReaderInfo().mIdCard;
//        if (TextUtils.isEmpty(idCardNum)) {
//            mView.showErrorMsg("获取身份信息失败！");
//            return;
//        }
//        final String orderNum = DataRepository.getInstance().getOrderNumberInfo().mCodeNumber;
//        if (TextUtils.isEmpty(orderNum)) {
//            mView.showErrorMsg("单号不能为空！");
//            return;
//        }
//        if (TextUtils.isEmpty(pwd)) {
//            mView.showErrorMsg("密码不能为空！");
//            return;
//        }
//        LibraryInfo libraryInfo = DataRepository.getInstance().getLibraryInfo();
//        if (null != libraryInfo) {
//            Subscription subscription = DataRepository.getInstance().checkOperatorPsw(libraryInfo.mHallCode, libraryInfo.mOperaterName, MD5Util.MD5(pwd))
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Observer<VerifyLibraryOperatorPswVo>() {
//                        @Override
//                        public void onCompleted() {
//
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                            if (mView != null) {
//                                mView.showErrorMsg(R.string.network_fault);
//                                mView.clearPswText();
//                            }
//                        }
//
//                        @Override
//                        public void onNext(VerifyLibraryOperatorPswVo verifyLibraryOperatorPswVo) {
//                            if (mView != null) {
//                                if (verifyLibraryOperatorPswVo.status == CODE_SUCCESS) {
//                                    if (verifyLibraryOperatorPswVo.data.value) {
//                                        applyPenaltyFree(applyRemark, idCardNum, orderNum);
//                                    } else {
//                                        mView.showErrorMsg(R.string.psw_error);
//                                        mView.clearPswText();
//                                    }
//                                } else {
//                                    switch (verifyLibraryOperatorPswVo.data.errorCode) {
//                                        case ERROR_CODE_KICK_OUT:
//                                            mView.setNoLoginPermission(R.string.kicked_offline);
//                                            break;
//                                        case ERROR_CODE_1006:
//                                            mView.setNoLoginPermission(R.string.operate_timeout);
//                                            break;
//                                        default:
//                                            mView.showErrorMsg(R.string.network_fault);
//                                            break;
//                                    }
//                                }
//
//                            }
//                        }
//                    });
//            addSubscrebe(subscription);
//        }
    }

    private void applyPenaltyFree(String applyRemark, String idCardNum, String orderNum) {
//        Subscription subscription = DataRepository.getInstance().applyPenaltyFree(applyRemark, idCardNum, orderNum)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<ApplyPenaltyFreeVo>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        if (mView != null) {
//                            mView.showErrorMsg(R.string.network_fault);
//                            mView.clearPswText();
//                        }
//                    }
//
//                    @Override
//                    public void onNext(ApplyPenaltyFreeVo applyPenaltyFreeVo) {
//                        if (mView != null) {
//                            if (applyPenaltyFreeVo.status == CODE_SUCCESS) {
                                mView.applyPenaltyFreeSuccess();
//                            } else {
//                                switch (applyPenaltyFreeVo.data.errorCode) {
//                                    case ERROR_CODE_KICK_OUT:
//                                        mView.setNoLoginPermission(R.string.kicked_offline);
//                                        break;
//                                    case ERROR_CODE_1006:
//                                        mView.setNoLoginPermission(R.string.operate_timeout);
//                                        break;
//                                    default:
//                                        mView.showErrorMsg(R.string.network_fault);
//                                        break;
//                                }
//                            }
//                        }
//
//                    }
//                });
//        addSubscrebe(subscription);

    }

    /**
     * 交罚金
     *
     * @param readerId   读者id
     * @param codeNumber 单号
     * @param payMoney   金额
     * @param pwd        密码
     */
    private void payFine(final String readerId, final String codeNumber, final double payMoney, String pwd) {
//        LibraryInfo libraryInfo = DataRepository.getInstance().getLibraryInfo();
//        if (null != libraryInfo) {
//            Subscription subscription = DataRepository.getInstance().checkOperatorPsw(libraryInfo.mHallCode, libraryInfo.mOperaterName, pwd)
//                    .flatMap(new Func1<VerifyLibraryOperatorPswVo, Observable<PayFineVo>>() {
//                        @Override
//                        public Observable<PayFineVo> call(VerifyLibraryOperatorPswVo verifyPswVo) {
//                            if (verifyPswVo.status == CODE_SUCCESS) {
//                                if (null != verifyPswVo.data && verifyPswVo.data.value) {
//                                    return DataRepository.getInstance().payFine(readerId, String.valueOf(payMoney), codeNumber);
//                                } else {
//                                    return DataRepository.getInstance().payFineError(CODE_SERVICE_ERROR, ERROR_CODE_3103);
//                                }
//                            } else {
//                                if (null != verifyPswVo.data) {
//                                    return DataRepository.getInstance().payFineError(CODE_SERVICE_ERROR, verifyPswVo.data.errorCode);
//                                } else {
//                                    return DataRepository.getInstance().payFineError(CODE_SERVICE_ERROR, ERROR_CODE_3103);
//                                }
//                            }
//                        }
//                    })
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Observer<PayFineVo>() {
//                        @Override
//                        public void onCompleted() {
//
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                            if (mView != null) {
//                                mView.showErrorMsg(R.string.network_fault);
//                                mView.clearPswText();
//                            }
//                        }
//
//                        @Override
//                        public void onNext(PayFineVo payFineVo) {
//                            if (mView != null) {
//                                if (payFineVo != null) {
//                                    if (payFineVo.status == CODE_SUCCESS) {
//                                        mView.payCostSuccess();
//                                    } else {
//                                        mView.clearPswText();
//                                        if (null != payFineVo.data) {
//                                            switch (payFineVo.data.errorCode) {
//                                                case ERROR_CODE_KICK_OUT:
//                                                    mView.setNoLoginPermission(R.string.kicked_offline);
//                                                    break;
//                                                case ERROR_CODE_1006:
//                                                    mView.setNoLoginPermission(R.string.operate_timeout);
//                                                    break;
//                                                case ERROR_CODE_3103://密码错误-验证密码错误
//                                                    mView.showErrorMsg(R.string.psw_error);
//                                                    break;
//                                                case ERROR_CODE_3400:
//                                                    if (null != payFineVo.data.errorData) {
//                                                        if (payFineVo.data.errorData.index == 1) {
//                                                            mView.showErrorMsg(R.string.money_not_enough);
//                                                        } else {
//                                                            mView.showErrorMsg(R.string.deposit_not_enough);
//                                                        }
//                                                    }
//                                                    break;
//                                                case ERROR_CODE_6112://交押金金额超限值
//                                                    mView.showErrorMsg(R.string.amount_the_deposit_exceeds_the_limit);
//                                                    break;
//                                                default:
//                                                    mView.showErrorMsg(R.string.pay_penalty_fail);
//                                                    break;
//                                            }
//                                        } else {
//                                            mView.showErrorMsg(R.string.network_fault);
//                                        }
//                                    }
//                                } else {
//                                    //如果为null,则密码错误
//                                    mView.showErrorMsg(R.string.psw_error);
//                                }
//                            }
//                        }
//                    });
//            addSubscrebe(subscription);
//        }
    }

    /**
     * 交押金
     *
     * @param readerId 身份证
     * @param orderNum 订单号
     * @param money    支付金额
     * @param pwd      操作员密码
     */
    private void payDeposit(final String readerId, final String orderNum, final String money, String pwd) {
//        LibraryInfo libraryInfo = DataRepository.getInstance().getLibraryInfo();
//        if (null != libraryInfo) {
//            Subscription subscription = DataRepository.getInstance().checkOperatorPsw(libraryInfo.mHallCode, libraryInfo.mOperaterName, pwd)
//                    .flatMap(new Func1<VerifyLibraryOperatorPswVo, Observable<AddDepositVo>>() {
//                        @Override
//                        public Observable<AddDepositVo> call(VerifyLibraryOperatorPswVo verifyPswVo) {
//                            if (verifyPswVo.status == CODE_SUCCESS) {
//                                if (null != verifyPswVo.data && verifyPswVo.data.value) {
//                                    return DataRepository.getInstance().addDeposit(readerId, orderNum, money);
//                                }
//                            }
//                            return DataRepository.getInstance().addDepositError(CODE_SERVICE_ERROR, ERROR_CODE_3103);
//                        }
//                    })
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Observer<AddDepositVo>() {
//                        @Override
//                        public void onCompleted() {
//
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                            if (mView != null) {
//                                mView.showErrorMsg(R.string.network_fault);
//                                mView.clearPswText();
//                            }
//                        }
//
//                        @Override
//                        public void onNext(AddDepositVo addDepositVo) {
//                            if (mView != null) {
//                                if (null != addDepositVo) {
//                                    if (addDepositVo.status == CODE_SUCCESS) {
//                                        //交押金，不需要自动处理罚金
//                                        mView.payCostSuccess();
//                                    } else {
//                                        mView.clearPswText();
//                                        if (null != addDepositVo.data) {
//                                            switch (addDepositVo.data.errorCode) {
//                                                case ERROR_CODE_KICK_OUT:
//                                                    mView.setNoLoginPermission(R.string.kicked_offline);
//                                                    break;
//                                                case ERROR_CODE_1006:
//                                                    mView.setNoLoginPermission(R.string.operate_timeout);
//                                                    break;
//                                                case ERROR_CODE_3103://密码错误-验证密码错误
//                                                    mView.showErrorMsg(R.string.psw_error);
//                                                    break;
//                                                case ERROR_CODE_3400:
//                                                    mView.showErrorMsg(R.string.money_not_enough);
//                                                    break;
//                                                case ERROR_CODE_6112://交押金金额超限值
//                                                    if (null != addDepositVo.data.errorData) {
//                                                        mView.showDepositMsg(R.string.current_max_num, MoneyUtils.formatMoney(addDepositVo.data.errorData.availablePay));
//                                                    } else {
//                                                        mView.showErrorMsg(R.string.amount_the_deposit_exceeds_the_limit);
//                                                    }
//                                                    break;
//                                                default:
//                                                    mView.showErrorMsg(R.string.pay_deposit_fail);
//                                                    break;
//                                            }
//                                        } else {
//                                            mView.showErrorMsg(R.string.network_fault);
//                                        }
//                                    }
//                                } else {
//                                    //如果为null,则密码错误
//                                    mView.showErrorMsg(R.string.psw_error);
//                                }
//                            }
//                        }
//                    });
//            addSubscrebe(subscription);
//        }
    }
}
