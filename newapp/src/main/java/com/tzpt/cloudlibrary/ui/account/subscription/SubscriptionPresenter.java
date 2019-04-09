package com.tzpt.cloudlibrary.ui.account.subscription;

import android.util.Log;

import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.local.db.DBManager;
import com.tzpt.cloudlibrary.modle.local.db.VideoColumns;

import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/12/21.
 */

public class SubscriptionPresenter extends RxPresenter<SubscriptionContract.View>
        implements SubscriptionContract.Presenter {

    @Override
    public void getLocalEBookCount() {

        Subscription subscription = Observable.create(new Observable.OnSubscribe<Integer>() {

            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                subscriber.onNext(DBManager.getInstance().getBookCount());
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer count) {
                        if (mView != null) {
                            mView.setLocalEBookCount(count);
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 从本地数据库获取下载的视频的数量
     */
    @Override
    public void getLocalVideoCount() {
        Subscription subscription0 = Observable.create(new Observable.OnSubscribe<Integer>() {

            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                subscriber.onNext(DBManager.getInstance().getVideoCount());
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer count) {
                        if (mView != null) {
                            mView.setVideoShelfCount(count);//视频架数量
                        }
                    }
                });
        addSubscrebe(subscription0);
    }

    @Override
    public void getLocalUserInfo() {
        int borrowSum = UserRepository.getInstance().getUserBorrowSum();                //当前借阅数量
        int borrowOverdueSum = UserRepository.getInstance().getUserBorrowOverdueSum();  //当前借阅逾期数量
        mView.setCurrentBorrowCountAndOverdueCount(borrowSum, borrowOverdueSum);

        int buyBookShelfSum = UserRepository.getInstance().getUserBuyBookShelfSum();    //购书架数量
        mView.setBuyBookShelfCount(buyBookShelfSum);

    }

}
