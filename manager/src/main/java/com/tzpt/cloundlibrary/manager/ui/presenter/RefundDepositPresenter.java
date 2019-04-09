package com.tzpt.cloundlibrary.manager.ui.presenter;

import android.text.TextUtils;
import android.view.View;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseResponseCode;
import com.tzpt.cloundlibrary.manager.base.RxPresenter;
import com.tzpt.cloundlibrary.manager.bean.BookInfoBean;
import com.tzpt.cloundlibrary.manager.bean.LostBookInfo;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.modle.remote.exception.ApiException;
import com.tzpt.cloundlibrary.manager.ui.contract.RefundDepositContract;
import com.tzpt.cloundlibrary.manager.utils.MD5Util;
import com.tzpt.cloundlibrary.manager.utils.MoneyUtils;
import com.tzpt.cloundlibrary.manager.utils.StringUtils;

import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 退押金
 * Created by Administrator on 2017/7/11.
 */
public class RefundDepositPresenter extends RxPresenter<RefundDepositContract.View> implements
        RefundDepositContract.Presenter, BaseResponseCode {

    private double mCanRefundDeposit;

    private String mReaderId;

    private String mIDCard;

    @Override
    public void getBorrowBookList(String readerId) {
        mView.showLoading();
        Subscription subscription = DataRepository.getInstance().getBorrowingBookList(readerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LostBookInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.dismissLoading();
                            if (e instanceof ApiException) {
                                switch (((ApiException) e).getCode()) {
                                    case ERROR_CODE_KICK_OUT:
                                        mView.noPermissionDialog(R.string.kicked_offline);
                                        break;
                                    case ERROR_CODE_1006:
                                        mView.noPermissionDialog(R.string.operate_timeout);
                                        break;
                                    default:
                                        mView.showMsgNoCancelableFinish(R.string.network_fault);
                                        break;
                                }
                            } else {
                                mView.showMsgNoCancelableFinish(R.string.network_fault);
                            }
                        }
                    }

                    @Override
                    public void onNext(LostBookInfo lostBookInfo) {
                        if (mView != null) {
                            mView.dismissLoading();
                            mReaderId = lostBookInfo.mReaderInfo.mReaderId;
                            mIDCard = lostBookInfo.mReaderInfo.mIdCard;
                            mView.setReaderNameNumber(lostBookInfo.mReaderInfo.mCardName + " "
                                    + StringUtils.setIdCardNumberForReader(lostBookInfo.mReaderInfo.mIdCard));

                            mView.setBorrowableSum("未还" + lostBookInfo.mReaderInfo.mBorrowingNum);

                            mCanRefundDeposit = lostBookInfo.mReaderInfo.getLibraryUsableDeposit();
                            mView.setDepositOrPenalty("可退馆押金" + MoneyUtils.formatMoney(mCanRefundDeposit));
                            if (mCanRefundDeposit <= 0) {
                                String tips = "可退押金不足,不可退押金！";
                                mView.setRefundOperatorVisibility(false, tips);
                            }

                            if (lostBookInfo.mBookList.size() > 0) {
                                mView.refreshBookList(lostBookInfo.mBookList);
                                refreshTotalInfo(lostBookInfo.mBookList);
                            }

                            if (lostBookInfo.mReaderInfo.mNotApplyPenalty > 0) {
                                mView.showMsgNoCancelableFinish("欠罚金" + MoneyUtils.formatMoney(lostBookInfo.mReaderInfo.mNotApplyPenalty) + "元,不可退押金！");
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void refundDeposit(final String money, final String pwd) {
        if (mCanRefundDeposit <= 0) {
            String tips = "可退押金不足,不可退押金！";
            mView.setRefundOperatorVisibility(false, tips);
            return;
        }

        if (TextUtils.isEmpty(money)) {
            mView.showMsgNoCancelable("金额不能为空！");
            return;
        }

        if (!StringUtils.patternMoney(money)) {
            mView.showMsgNoCancelable("金额错误！");
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            mView.showMsgNoCancelable("密码不能为空！");
            return;
        }
        if (pwd.length() < 6) {
            mView.showMsgNoCancelable("密码错误！");
            return;
        }
        final double refundAmount = MoneyUtils.stringToDouble(money);
        if (refundAmount > mCanRefundDeposit) {
            String msg = "您最多能退" + MoneyUtils.formatMoney(mCanRefundDeposit) + "元！";
            mView.showMsgNoCancelable(msg);
            return;
        }
        if (refundAmount == 0) {
            mView.showMsgNoCancelable("金额错误！");
            return;
        }
        mView.showLoading();
        Subscription subscription = DataRepository.getInstance().refundDeposit(mIDCard, MD5Util.MD5(pwd), mReaderId, refundAmount)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.dismissLoading();
                            if (e instanceof ApiException) {
                                switch (((ApiException) e).getCode()) {
                                    case ERROR_CODE_KICK_OUT:
                                        mView.noPermissionDialog(R.string.kicked_offline);
                                        break;
                                    case ERROR_CODE_1006:
                                        mView.noPermissionDialog(R.string.operate_timeout);
                                        break;
                                    case CODE_SERVICE_ERROR:
                                        mView.showMsgNoCancelable(R.string.error_code_500);
                                        break;
                                    case ERROR_CODE_3108:
                                    case ERROR_CODE_2303:
                                    case ERROR_CODE_2304:
                                        mView.showMsgNoCancelable(R.string.network_fault);
                                        break;
                                    case ERROR_CODE_3400:
                                        mView.showMsgNoCancelable(R.string.deposit_not_enough);
                                        //请求读者信息
                                        getBorrowBookList(mReaderId);
                                        break;
                                    case ERROR_CODE_3103://密码错误-验证密码错误
                                        mView.showMsgNoCancelable(R.string.psw_error);
                                        break;
                                    case ERROR_CODE_2317://读者有未处理罚金
                                        mView.showMsgNoCancelableFinish(R.string.return_deposit_fail);
                                        break;
                                    case ERROR_CODE_6105://图书馆没有线上交易记录
                                        mView.showMsgNoCancelable(R.string.money_not_enough);
                                        break;
                                    case ERROR_CODE_UNKNOWN:
                                    case ERROR_CODE_PARSE:
                                    case ERROR_CODE_NETWORK:
                                    case ERROR_CODE_HTTP:
                                        mView.showMsgNoCancelable(R.string.network_fault);
                                        break;
                                    default:
                                        mView.showMsgNoCancelable(R.string.return_deposit_fail);
                                        break;
                                }
                            } else {
                                mView.showMsgNoCancelable(R.string.network_fault);
                            }
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (mView != null) {
                            mView.dismissLoading();
                            mView.showRefundDepositSuccess("退押金成功！\n金额：" + MoneyUtils.formatMoney(refundAmount) + "元");
                            //重新获取读者信息
                            getBorrowBookList(mReaderId);
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void refreshTotalInfo(List<BookInfoBean> list) {
        if (list.size() > 0) {
            int borrowBookSum = list.size();
            double totalPrice = 0.00;
            double attachPriceTotal = 0.00;
            for (BookInfoBean book : list) {
                totalPrice = MoneyUtils.add(totalPrice, book.mPrice);
                attachPriceTotal = MoneyUtils.add(attachPriceTotal, book.mAttachPrice);
            }
            mView.setTotalInfo("数量 " + borrowBookSum, "金额 " + MoneyUtils.addToStr(totalPrice, attachPriceTotal));
            mView.setTotalInfoVisibility(View.VISIBLE);
        } else {
            //合计信息不展示
            mView.setTotalInfoVisibility(View.GONE);
        }
    }
}
