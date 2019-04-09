package com.tzpt.cloudlibrary.ui.account.borrow;

import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.business_bean.UserInfoBean;
import com.tzpt.cloudlibrary.modle.UserRepository;

import rx.functions.Action1;

/**
 * Created by Administrator on 2017/12/14.
 */

public class BorrowPresenter extends RxPresenter<BorrowContract.View> implements BorrowContract.Presenter {

    BorrowPresenter() {
//        UserRepository.getInstance().registerRxBus(UserInfoBean.class, new Action1<UserInfoBean>() {
//            @Override
//            public void call(UserInfoBean userInfoBean) {
//                if (mView != null) {
//                    mView.setCurrentBorrowCountAndOverdueCount(userInfoBean.mBorrowSum, userInfoBean.mBorrowOverdueSum);
//                    mView.setAppointCount(userInfoBean.mAppointCount);
//                }
//            }
//        });
    }

    @Override
    public void getUserInfo() {
//        UserRepository.getInstance().refreshUserInfo();
//        boolean isLogin = UserRepository.getInstance().isLogin();
//        if (isLogin) {
//            String idCard =
//            if (!TextUtils.isEmpty(idCard)) {
//                Subscription subscription = DataRepository.getInstance().getUserInfo(idCard)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new Observer<BaseResultEntityVo<UserInfoVo>>() {
//                            @Override
//                            public void onCompleted() {
//
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//
//                            }
//
//                            @Override
//                            public void onNext(BaseResultEntityVo<UserInfoVo> userInfoVo) {
//                                if (mView != null) {
//                                    if (userInfoVo.data != null) {
//                                        int userBorrowSum = userInfoVo.data.borrowSum;              //当前借阅数量
//                                        int userBorrowOverdueSum = userInfoVo.data.borrowOverdueSum;//当前借阅逾期数量
//                                        mView.setCurrentBorrowCountAndOverdueCount(userBorrowSum, userBorrowOverdueSum);
//
//                                        int appointCount = userInfoVo.data.appointCount;            //当前预约数量
//                                        mView.setAppointCount(appointCount);
//                                    }
//                                }
//                            }
//                        });
//                addSubscrebe(subscription);
//            }
//        }
    }

    /**
     * 出现错误获取本地数据
     */
    @Override
    public void getLocalUserInfo() {
        int appointCount = UserRepository.getInstance().getUserAppointCount();
        mView.setAppointCount(appointCount);                                            //当前预约数量

        int borrowSum = UserRepository.getInstance().getUserBorrowSum();                //当前借阅数量
        int borrowOverdueSum = UserRepository.getInstance().getUserBorrowOverdueSum();  //当前借阅逾期数量
        mView.setCurrentBorrowCountAndOverdueCount(borrowSum, borrowOverdueSum);
    }

    @Override
    public void detachView() {
        super.detachView();
//        UserRepository.getInstance().unregisterRxBus();
    }
}
