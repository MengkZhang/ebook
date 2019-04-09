package com.tzpt.cloudlibrary.ui.account;

import android.text.TextUtils;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.business_bean.UserInfoBean;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;
import com.tzpt.cloudlibrary.utils.IdCardUtils;
import com.tzpt.cloudlibrary.utils.MD5Utils;
import com.tzpt.cloudlibrary.utils.PhoneNumberUtils;
import com.tzpt.cloudlibrary.utils.StringUtils;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 用户注册
 * Created by JiaZhiqiang on 2018/2/27.
 */
public class RegisterPresenter extends RxPresenter<RegisterContract.View> implements
        RegisterContract.Presenter {

    private String mIdCard;
    private String mPsw;

    @Override
    public void sendPhoneVerifyCode(String phone) {
        if (TextUtils.isEmpty(phone)) {
            mView.showMsgDialog(R.string.phone_number_can_not_empty);
            return;
        }
        if (!PhoneNumberUtils.isMobileNumber(phone)) {
            mView.showMsgDialog(R.string.phone_number_wrong);
            return;
        }
        mView.showProgressDialog("发送中...");
        Subscription subscription = UserRepository.getInstance().sendPhoneVerifyCode(phone)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.dismissProgressDialog();
                            if (e instanceof ApiException) {
                                switch (((ApiException) e).getCode()) {
                                    case 30111:
                                        mView.showMsgDialog(R.string.phone_num_exist);
                                        break;
                                    case 30112:
                                        mView.showMsgDialog(R.string.send_verify_code_failed);
                                        break;
                                    default:
                                        mView.showMsgDialog(R.string.network_fault);
                                        break;
                                }
                            } else {
                                mView.showMsgDialog(R.string.network_fault);
                            }
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (mView != null) {
                            mView.dismissProgressDialog();
                            if (aBoolean) {
                                startTime();
                            }
                        }
                    }
                });
        addSubscrebe(subscription);

    }

    private static final int count = 60;

    //开始计时
    private void startTime() {
        Subscription subscription = Observable.interval(0, 1, TimeUnit.SECONDS)
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
                })
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {
                        //计时完成
                        if (null != mView) {
                            mView.setSendCodeStyle(1);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        //出现错误
                        if (null != mView) {
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
        addSubscrebe(subscription);
    }

    @Override
    public void verifyMsgCode(String phone, String code) {
        if (TextUtils.isEmpty(phone)) {
            mView.showMsgDialog(R.string.phone_number_can_not_empty);
            return;
        }
        if (!PhoneNumberUtils.isMobileNumber(phone)) {
            mView.showMsgDialog(R.string.phone_number_wrong);
            return;
        }
        if (TextUtils.isEmpty(code)) {
            mView.showMsgDialog(R.string.code_can_not_empty);
            return;
        }
        mView.showProgressDialog("发送中...");
        Subscription subscription = UserRepository.getInstance().checkMsgCode(phone, code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.dismissProgressDialog();
                            if (e instanceof ApiException) {
                                switch (((ApiException) e).getCode()) {
                                    case 30106:
                                        mView.showMsgDialog(R.string.code_wrong);
                                        break;
                                    default:
                                        mView.showMsgDialog(R.string.network_fault);
                                        break;
                                }
                            } else {
                                mView.showMsgDialog(R.string.network_fault);
                            }
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (null != mView) {
                            mView.dismissProgressDialog();
                            if (aBoolean) {
                                mView.verifyMsgCodeSuccess();
                            } else {
                                mView.showMsgDialog(R.string.code_wrong);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void checkIdCardAvailable(String idCard, String readerName, String nickName) {
        if (TextUtils.isEmpty(nickName)) {
            mView.showMsgDialog(R.string.nick_name_not_empty);
            return;
        }
        if (!StringUtils.isVerifyNickName(nickName)) {
            mView.showMsgDialog(R.string.error_nick_name);
            return;
        }
        if (TextUtils.isEmpty(idCard)) {
            mView.showMsgDialog(R.string.idcard_empty_tip);
            return;
        }
        if (!IdCardUtils.verifyIdCardNumber(idCard)) {
            mView.showMsgDialog(R.string.error_id_card_info);
            return;
        }
        mView.showProgressDialog("发送中...");
        Subscription subscription = UserRepository.getInstance().checkUserInfo(idCard, nickName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.dismissProgressDialog();
                            if (e instanceof ApiException) {
                                switch (((ApiException) e).getCode()) {
                                    case 30110:
                                        mView.showMsgDialog(R.string.idcard_error_tip);
                                        break;
                                    case 30111://身份证已存在
                                        mView.showMsgDialog(R.string.idcard_already_registered);
                                        break;
                                    case 30117://昵称不合法
                                        mView.showMsgDialog(R.string.error_nick_name_to_retry);
                                        break;
                                    default://操作不成功
                                        mView.showMsgDialog(R.string.network_fault);
                                        break;
                                }
                            } else {
                                mView.showMsgDialog(R.string.network_fault);
                            }
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (mView != null) {
                            mView.dismissProgressDialog();
                            if (aBoolean) {
                                mView.checkIdCardSuccess();
                            } else {
                                mView.showMsgDialog(R.string.network_fault);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void register(String phone, final String idCard, String readerName, final String psw, String rePsw, String nickName) {
        if (TextUtils.isEmpty(psw) || TextUtils.isEmpty(rePsw)) {
            mView.showMsgDialog(R.string.error_incorrect_password);
            return;
        }
        if (psw.length() < 6 || rePsw.length() < 6) {
            mView.showMsgDialog(R.string.error_invalid_password);
            return;
        }
        if (!psw.equals(rePsw)) {
            mView.showMsgDialog(R.string.error_compare_new_password);
            return;
        }
        mView.showProgressDialog("注册中...");
        final Subscription subscription = UserRepository.getInstance().readerRegister(readerName, idCard, psw, phone, nickName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.dismissProgressDialog();
                            mView.showMsgDialog(R.string.network_fault);
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (mView != null) {
                            mView.dismissProgressDialog();
                            if (aBoolean) {
                                mIdCard = idCard;
                                mPsw = psw;
                                mView.registerSuccess();
                            } else {
                                mView.showMsgDialog(R.string.register_fail);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);

    }

    @Override
    public void startLogin() {
        if (TextUtils.isEmpty(mIdCard) || TextUtils.isEmpty(mPsw)) {
            return;
        }
        mView.showProgressDialog("登录中...");
        Subscription subscription = UserRepository.getInstance().login(null, mIdCard, MD5Utils.MD5(mPsw))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserInfoBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.dismissProgressDialog();
                            if (e instanceof ApiException) {
                                switch (((ApiException) e).getCode()) {
                                    case 30102://用户未注册
                                        mView.showMsgDialog(R.string.id_card_not_register);
                                        break;
                                    case 30103://用户名或密码错误
                                        mView.showMsgDialog(R.string.password_error);
                                        break;
                                    case 30101://必须参数不能为空
                                        mView.showMsgDialog(R.string.account_or_pwd_error);
                                        break;
                                    default:
                                        //身份验证失败
                                        mView.showMsgDialog(R.string.login_failure);
                                        break;
                                }
                            } else {
                                mView.showMsgDialog(R.string.network_fault);
                            }
                        }
                    }

                    @Override
                    public void onNext(UserInfoBean userInfoBean) {
                        if (mView != null) {
                            mView.dismissProgressDialog();
                            if (userInfoBean != null) {
                                mView.loginSuccess();
                            } else {
                                mView.showMsgDialog(R.string.login_failure);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);

    }
}
