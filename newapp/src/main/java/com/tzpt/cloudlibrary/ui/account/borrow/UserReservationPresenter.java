package com.tzpt.cloudlibrary.ui.account.borrow;

import android.text.TextUtils;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.business_bean.BaseListResultData;
import com.tzpt.cloudlibrary.business_bean.OperateReservationBookResultBean;
import com.tzpt.cloudlibrary.business_bean.ReservationBookBean;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.PaperBookRepository;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 我的预约
 * Created by ZhiqiangJia on 2017-10-11.
 */
public class UserReservationPresenter extends RxPresenter<UserReservationContract.View> implements
        UserReservationContract.Presenter {

    @Override
    public void getUserReservationList(final int pageNum) {
        String idCard = UserRepository.getInstance().getLoginUserIdCard();
        if (TextUtils.isEmpty(idCard)) {
            return;
        }
        Subscription subscription = DataRepository.getInstance().getReservationBookList(pageNum, 10, idCard)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseListResultData<ReservationBookBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            if (e instanceof ApiException) {
                                if (((ApiException) e).getCode() == 30100) {
                                    mView.pleaseLoginTip();
                                } else {
                                    mView.setNetError(pageNum == 1);
                                }
                            } else {
                                mView.setNetError(pageNum == 1);
                            }
                        }
                    }

                    @Override
                    public void onNext(BaseListResultData<ReservationBookBean> reservationBookBeanBaseListResultData) {
                        if (mView != null) {
                            if (reservationBookBeanBaseListResultData != null
                                    && reservationBookBeanBaseListResultData.mResultList != null
                                    && reservationBookBeanBaseListResultData.mResultList.size() > 0) {
                                mView.setUserReservationList(reservationBookBeanBaseListResultData.mResultList,
                                        reservationBookBeanBaseListResultData.mTotalCount,
                                        pageNum == 1);
                            } else {
                                mView.setUserReservationEmpty(pageNum == 1);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void cancelReservation(final String isbn,final String libCode) {
        String idCard = UserRepository.getInstance().getLoginUserIdCard();
        if (TextUtils.isEmpty(idCard)) {
            return;
        }
        long readerId = UserRepository.getInstance().getLoginReaderIdL();
        //appointType 操作类型：1预约，0取消预约  图书ISBN号 馆号 读者ID
        Subscription subscription = PaperBookRepository.getInstance().operateReservationBook(0, isbn,libCode, readerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<OperateReservationBookResultBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            if (e instanceof ApiException) {
                                switch (((ApiException) e).getCode()) {
                                    case 30100:
                                        mView.pleaseLoginTip();
                                        break;
                                    case 30104:
                                        mView.showToastError("取消预约失败！");
                                        break;
                                    default:
                                        mView.showToastError(R.string.network_fault);
                                        break;
                                }
                            } else {
                                mView.showToastError(R.string.network_fault);
                            }
                        }
                    }

                    @Override
                    public void onNext(OperateReservationBookResultBean operateReservationBookResultBean) {
                        if (mView != null) {
                            if (operateReservationBookResultBean != null
                                    && operateReservationBookResultBean.mIsOpeateSuccess) {
                                getUserReservationList(1);
//                                DataRepository.getInstance().updateUserInfo();
//                                UserRepository.getInstance().refreshUserInfo();
//                                DataRepository.getInstance().delReservationBookId(String.valueOf(bookId));
                            } else {
                                mView.showToastError(R.string.network_fault);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }
}
