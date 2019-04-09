package com.tzpt.cloudlibrary.ui.account.card;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.tzpt.cloudlibrary.CloudLibraryApplication;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.local.ZxingUtil;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 获取生成条形码和二维码
 * Created by ZhiqiangJia on 2017-10-16.
 */

public class UserIdentificationPresenter extends RxPresenter<UserIdentificationContract.View> implements
        UserIdentificationContract.Presenter {

    @Override
    public void getLoginInfo() {
        //设置用户昵称
        String cardName = UserRepository.getInstance().getLoginCardName();
        boolean isMan = UserRepository.getInstance().isMan();
        String cardNickName = formatCardName(cardName, isMan);
        String nickName = UserRepository.getInstance().getLoginNickName();

        mView.setUserNickName(!TextUtils.isEmpty(nickName) ? nickName : cardNickName);
        //设置手机号码
        String phone = UserRepository.getInstance().getLoginPhone();
        mView.setUserPhoneNumber(phone);

        //设置读者头像
        String headImage = UserRepository.getInstance().getLoginHeadImage();
        mView.setUserHeadImage(headImage, isMan);

    }

    @Override
    public void getTokenBar() {
        mView.showProgressView();
        Subscription subscription = UserRepository.getInstance().getUserTokenBarContent()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            if (e instanceof ApiException) {
                                switch (((ApiException) e).getCode()) {
                                    case 30100:
//                                        DataRepository.getInstance().quit();
                                        mView.pleaseLoginTip();
                                        break;
                                    default:
                                        mView.showError();
                                        break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onNext(String s) {
                        if (mView != null) {
                            getQCBitmap(s);
                            //开始1分钟计时
                            startMinuteTimer();
                            reportTokenCount(s);
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    //获取二维码图片
    private void getQCBitmap(String content) {
        getBarCodeBitmap(content);
        getQrCode(content);
    }

    //生成条形码
    private void getBarCodeBitmap(final String content) {
        Subscription subscription = ZxingUtil.generatedBarCodeBitmap(content)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Bitmap>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.showError();
                        }
                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        if (null != mView) {
                            mView.setBarCodeBitmap(bitmap);
                            mView.showContentView();
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    //生成二维码
    private void getQrCode(final String content) {
        Subscription subscription = ZxingUtil.generatedQrCodeBitmap(content)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Bitmap>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.showError();
                        }
                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        if (null != mView) {
                            mView.setQRBitmap(bitmap);
                            mView.showContentView();
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    //设置定时器
    private static final int count = 60;
    private Subscription mSubscriptionTime;

    //开始5分钟计时
    private void startMinuteTimer() {
        unSubscriptionTime();
        mSubscriptionTime = Observable.interval(0, 1, TimeUnit.SECONDS)
                .take(count + 1)
                .map(new Func1<Long, Long>() {
                    @Override
                    public Long call(Long aLong) {
                        return count - aLong;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {

                    }
                }).subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {
                        //计时完成
                        getTokenBar();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long aLong) {

                    }
                });
        addSubscrebe(mSubscriptionTime);
    }

    private void unSubscriptionTime() {
        if (null != mSubscriptionTime && !mSubscriptionTime.isUnsubscribed()) {
            mSubscriptionTime.unsubscribe();
        }
    }

    /**
     * 上报令牌次数
     */
    private void reportTokenCount(String barCode) {
        Subscription subscription = UserRepository.getInstance().reportTokenCount(barCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Void>() {
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
                                    default:
                                        mView.showError();
                                        break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onNext(Void aVoid) {

                    }
                });
        addSubscrebe(subscription);
    }


    @Override
    public void getUserImgStatus() {
        mView.showProgressDialog();
        Subscription subscription = UserRepository.getInstance().getFaceRecognitionImage()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.dismissProgressDialog();
                            if (e instanceof ApiException) {
                                switch (((ApiException) e).getCode()) {
                                    case 30100:
                                        mView.pleaseLoginTip();
                                        break;
                                    default:
                                        mView.showDialogTip(R.string.network_fault);
                                        break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onNext(String s) {
                        if (mView != null) {
                            mView.dismissProgressDialog();
                            mView.setFaceRecognitionImage(s);
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    //格式化读者姓名
    private String formatCardName(String cardName, boolean isMan) {
        if (TextUtils.isEmpty(cardName)) {
            cardName = "用户名";
        } else {
            if (cardName.length() >= 2) {
                //如果有复姓
                List<String> surnameList = Arrays.asList(CloudLibraryApplication.getAppContext().getResources().getStringArray(R.array.compound_surnames));
                String lastName = cardName.substring(0, 2);
                if (surnameList.contains(lastName)) {
                    cardName = lastName + (isMan ? "先生" : "女士");
                } else {
                    //设置单姓
                    cardName = setOnLastName(cardName, isMan);
                }
            } else {
                //设置单姓
                cardName = setOnLastName(cardName, isMan);
            }
        }
        return cardName;
    }


    /**
     * 设置单姓
     *
     * @param userName 名称
     * @param isMan    身份证号
     * @return 单姓称谓
     */
    private String setOnLastName(String userName, boolean isMan) {
        String newName = userName.substring(0, 1);
        return newName + (isMan ? "先生" : "女士");
    }

}
