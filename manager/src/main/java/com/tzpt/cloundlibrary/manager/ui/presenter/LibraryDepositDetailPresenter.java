package com.tzpt.cloundlibrary.manager.ui.presenter;

import android.text.TextUtils;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseResponseCode;
import com.tzpt.cloundlibrary.manager.base.RxPresenter;
import com.tzpt.cloundlibrary.manager.bean.LibraryDepositDetailBean;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.LibraryDepositTransLogVo;
import com.tzpt.cloundlibrary.manager.ui.contract.LibraryDepositDetailContract;
import com.tzpt.cloundlibrary.manager.utils.DateUtils;
import com.tzpt.cloundlibrary.manager.utils.MoneyUtils;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 本馆押金明细
 * Created by ZhiqiangJia on 2017-10-24.
 */
public class LibraryDepositDetailPresenter extends RxPresenter<LibraryDepositDetailContract.View> implements
        LibraryDepositDetailContract.Presenter,
        BaseResponseCode {
    @Override
    public void getLibraryDepositDetailList(final int pageNum) {
        if (null != mView) {
            Subscription subscription = DataRepository.getInstance().getDepositTransLog(pageNum, 10)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<LibraryDepositTransLogVo>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (null != mView) {
                                mView.showNetError(pageNum == 1);
                            }
                        }

                        @Override
                        public void onNext(LibraryDepositTransLogVo transLogVo) {
                            if (null != mView) {
                                if (transLogVo.status == CODE_SUCCESS) {
                                    if (null != transLogVo.data) {
                                        List<LibraryDepositDetailBean> libraryDepositDetailBeanList = new ArrayList<>();
                                        if (null != transLogVo.data.resultList && transLogVo.data.resultList.size() > 0) {
                                            for (LibraryDepositTransLogVo.LibraryDepositTransLog item : transLogVo.data.resultList) {
                                                LibraryDepositDetailBean bean = new LibraryDepositDetailBean();
                                                bean.mDate = DateUtils.formatDateToYYMMDDHHMM(item.transTime);
                                                bean.mProjectAndOperator = TextUtils.isEmpty(item.transName) ? "" : item.transName + (TextUtils.isEmpty(item.operator) ? "" : "-" + item.operator);
                                                bean.mMoney = MoneyUtils.formatMoney(item.transMoney);
                                                if (item.transStatus != null &&
                                                        (item.transStatus.index == 2 || item.transStatus.index == 3)) {
                                                    bean.mStatus = item.payStatus;
                                                }
                                                bean.mRemark = item.cause;
                                                libraryDepositDetailBeanList.add(bean);
                                            }
                                            mView.setLibraryDepositDetailList(libraryDepositDetailBeanList, transLogVo.data.totalCount, pageNum == 1);
                                            mView.setLibraryTotalDeposit("金额 " + MoneyUtils.formatMoney(transLogVo.data.totalSumPrice));
                                        } else {
                                            mView.setLibraryDepositDetailListEmpty(pageNum == 1);
                                        }
                                    } else {
                                        mView.setLibraryDepositDetailListEmpty(pageNum == 1);
                                    }
                                } else {
                                    if (null != transLogVo.data) {
                                        switch (transLogVo.data.errorCode) {
                                            case ERROR_CODE_KICK_OUT:
                                                mView.setNoPermissionDialog(R.string.kicked_offline);
                                                break;
                                            case ERROR_CODE_1006:
                                                mView.setNoPermissionDialog(R.string.operate_timeout);
                                                break;
                                            default:
                                                mView.showNetError(pageNum == 1);
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }
}
