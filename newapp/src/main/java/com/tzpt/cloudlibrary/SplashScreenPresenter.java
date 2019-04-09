package com.tzpt.cloudlibrary;

import android.text.TextUtils;
import android.util.Log;

import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.ui.map.LocationManager;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/3/27.
 */

public class SplashScreenPresenter extends RxPresenter<SplashScreenContract.View>
        implements SplashScreenContract.Presenter {

    @Override
    public void startLocation() {
        LocationManager.getInstance().startLocation();
    }

    @Override
    public void getLaunchImgData() {
        String adCode = LocationManager.getInstance().getLocationAdCode();
        DataRepository.getInstance().getLauncherBanner(adCode);
    }

    @Override
    public void startTimer(final String launchImg, final int time) {
        Subscription subscription = Observable.interval(0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Long, Integer>() {
                    @Override
                    public Integer call(Long increaseTime) {
                        return time - increaseTime.intValue();
                    }
                })
                .take(time)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        //开始计时
                    }
                })
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        if (mView != null) {
                            mView.timeToComplete();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer countDownTime) {
                        //延时1秒加载广告图
                        if (mView != null) {
                            if (TextUtils.isEmpty(launchImg)) {//没有广告图 直接不执行倒计时
                                mView.timeToComplete();
                            } else {//加载广告
                                mView.loadAdImg(launchImg);
                            }
//                            if (!TextUtils.isEmpty(launchImg) && time - countDownTime == 1) {
//                                mView.loadAdImg(launchImg);
//                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

}
