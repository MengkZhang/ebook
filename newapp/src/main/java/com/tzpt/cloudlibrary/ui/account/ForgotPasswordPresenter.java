package com.tzpt.cloudlibrary.ui.account;

import android.text.TextUtils;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseResultEntityVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.ResetPwdResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.VerifyCodeVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.VerifyIDCardVo;
import com.tzpt.cloudlibrary.utils.IdCardUtils;
import com.tzpt.cloudlibrary.utils.MD5Utils;
import com.tzpt.cloudlibrary.utils.PhoneNumberUtils;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 忘记密码
 * Created by ZhiqiangJia on 2017-08-30.
 */

public class ForgotPasswordPresenter extends RxPresenter<ForgotPasswordContract.View> implements
        ForgotPasswordContract.Presenter {
    private static final int count = 60;
    private Subscription mSubscriptionTime;

    @Override
    public void getIDCard() {
        String idCard = UserRepository.getInstance().getLoginUserIdCard();
        mView.setIdCard(idCard);
    }

    @Override
    public void checkPhoneIsBinding(String idCard) {
        if (TextUtils.isEmpty(idCard)) {
            mView.showToastMsg(R.string.idcard_empty_tip);
            return;
        }
        if (!IdCardUtils.verifyIdCardNumber(idCard)) {
            mView.showToastMsg(R.string.idcard_error_tip);
            return;
        }
        mView.showProgressDialog();
        Subscription subscription = DataRepository.getInstance().verifyIDCard(idCard)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResultEntityVo<VerifyIDCardVo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.dismissProgressDialog();
                            mView.showToastMsg(R.string.network_fault);
                        }
                    }

                    @Override
                    public void onNext(BaseResultEntityVo<VerifyIDCardVo> verifyIDCardVoBaseResultEntityVo) {
                        if (mView != null) {
                            mView.dismissProgressDialog();
                            if (verifyIDCardVoBaseResultEntityVo.status == 200) {
                                if (verifyIDCardVoBaseResultEntityVo.data != null
                                        && !TextUtils.isEmpty(verifyIDCardVoBaseResultEntityVo.data.phone)) {
                                    mView.idCardBindingPhoneNumber(verifyIDCardVoBaseResultEntityVo.data.phone);
                                }
                            } else if (verifyIDCardVoBaseResultEntityVo.status == 417) {
                                switch (verifyIDCardVoBaseResultEntityVo.data.errorCode) {
                                    case 30105://未绑定手机
                                        mView.idCardNotBindingPhoneNumber();
                                        break;
                                    case 30102://用户未注册
                                        mView.idCardNotRegister();
                                        break;
                                }
                            } else {
                                mView.showToastMsg(R.string.network_fault);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);

    }

    @Override
    public void getVerifyCode(String phone) {
        if (!PhoneNumberUtils.isMobileNumber(phone)) {
            mView.showToastMsg(R.string.phone_number_wrong);
            return;
        }
        mView.showProgressDialog();
        Subscription subscription = DataRepository.getInstance().getVerifyCodeForgetPwd(phone)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResultEntityVo<com.tzpt.cloudlibrary.modle.remote.newpojo.VerifyCodeVo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.dismissProgressDialog();
                            mView.showToastMsg(R.string.network_fault);
                        }
                    }

                    @Override
                    public void onNext(BaseResultEntityVo<com.tzpt.cloudlibrary.modle.remote.newpojo.VerifyCodeVo> verifyCodeVoBaseResultEntityVo) {
                        if (mView != null) {
                            mView.dismissProgressDialog();
                            if (verifyCodeVoBaseResultEntityVo.status == 200) {
                                startTime();
                            } else {
                                mView.showToastMsg(R.string.network_fault);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void verifyCode(String phone, String code) {
        if (!PhoneNumberUtils.isMobileNumber(phone)) {
            mView.showToastMsg(R.string.phone_number_wrong);
            return;
        }
        if (TextUtils.isEmpty(code)) {
            mView.showToastMsg(R.string.code_can_not_empty);
            return;
        }
        Subscription subscription = DataRepository.getInstance().checkVerifyCodeForgetPwd(code, phone)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResultEntityVo<VerifyCodeVo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.showToastMsg(R.string.network_fault);
                        }
                    }

                    @Override
                    public void onNext(BaseResultEntityVo<VerifyCodeVo> verifyCodeVoBaseResultEntityVo) {
                        if (mView != null) {
                            if (verifyCodeVoBaseResultEntityVo.status == 200) {
                                mView.setVerifyCodeSuccess();
                            } else if (verifyCodeVoBaseResultEntityVo.status == 417) {
                                switch (verifyCodeVoBaseResultEntityVo.data.errorCode) {
                                    case 30106://验证码失效或者错误
                                        mView.showToastMsg(R.string.code_wrong);
                                        break;
                                    case 30105://手机号与接收验证码的不一致
                                        mView.showToastMsg(R.string.phone_code_not_fit);
                                        break;
                                }
                            } else {
                                mView.showToastMsg(R.string.network_fault);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void resetPsw(String idCard, String psw1, String psw2) {
        //TODO idCard有效验证
        if (TextUtils.isEmpty(psw1) || TextUtils.isEmpty(psw2)) {
            mView.showToastMsg(R.string.new_pwd_empty_tip);
            return;
        }
        if (psw1.length() < 6 || psw2.length() < 6) {
            mView.showToastMsg(R.string.error_invalid_password);
            return;
        }
        if (!psw1.equals(psw2)) {
            mView.showToastMsg(R.string.error_compare_new_password);
            return;
        }
        Subscription subscription = DataRepository.getInstance().resetForgetPwd(idCard, MD5Utils.MD5(psw1))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResultEntityVo<ResetPwdResultVo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.showToastMsg(R.string.network_fault);
                        }
                    }

                    @Override
                    public void onNext(BaseResultEntityVo<ResetPwdResultVo> resetPwdResultVoBaseResultEntityVo) {
                        if (mView != null) {
                            if (resetPwdResultVoBaseResultEntityVo.status == 200) {
                                mView.resetPswSuccess();
                            } else if (resetPwdResultVoBaseResultEntityVo.status == 417) {
                                switch (resetPwdResultVoBaseResultEntityVo.data.errorCode) {
                                    case 30106://验证码失效或者错误
                                        mView.showToastMsg(R.string.code_wrong);
                                        break;
                                    case 30105://手机号与接收验证码的不一致
                                        mView.showToastMsg(R.string.phone_code_not_fit);
                                        break;
                                }
                            } else {
                                mView.showToastMsg(R.string.network_fault);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    //开始计时
    private void startTime() {
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
                        //设置不可点击
                        if (null != mView) {
                            mView.setSendCodeStyle(0);
                        }
                    }
                }).subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {
                        //计时完成
                        if (null != mView) {
                            mView.setSendCodeStyle(1);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        //出现错误-网络故障等
                        if (null != mView) {
                            mView.showToastMsg(R.string.network_fault);
                            mView.setSendCodeStyle(2);
                        }
                    }

                    @Override
                    public void onNext(Long aLong) {
                        if (null != mView) {
                            mView.setSendCodeTime(aLong);
                        }
                    }
                });
        addSubscrebe(mSubscriptionTime);
    }

    @Override
    public void unSubscriptionTime() {
        if (null != mSubscriptionTime && !mSubscriptionTime.isUnsubscribed()) {
            mSubscriptionTime.unsubscribe();
        }
    }
}
