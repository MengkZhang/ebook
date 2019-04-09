package com.tzpt.cloudlibrary.ui.account;

import android.text.TextUtils;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;
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
 * 更改手机号
 * Created by ZhiqiangJia on 2017-08-30.
 */
public class ChangePhoneNumberPresenter extends RxPresenter<ChangePhoneNumberContract.View> implements
        ChangePhoneNumberContract.Presenter {

    private int mFromType;
    private static final int count = 60;

    @Override
    public void getLoginInfo() {
        String tel = UserRepository.getInstance().getLoginPhone();
        mView.setOldTelNum(tel);
    }

    @Override
    public void setFromType(int fromType) {
        this.mFromType = fromType;
    }

    @Override
    public void sendVerifyCode(String oldPhone, String newPhone, String bindingPhone) {
        if (null != mView) {
            // 0 修改手机号码 1 绑定手机号码
            switch (mFromType) {
                case 0:
                    if (TextUtils.isEmpty(newPhone)) {
                        mView.showToastMsg(R.string.phone_number_can_not_empty);
                        return;
                    }
                    if (!PhoneNumberUtils.isMobileNumber(newPhone)) {
                        mView.showToastMsg(R.string.phone_number_wrong);
                        return;
                    }
                    if (newPhone.equals(oldPhone)) {
                        mView.showToastMsg(R.string.repeat_original_number);
                        return;
                    }
                    mView.setPhoneEditAble(false);
                    preUserInfo(newPhone);
                    break;
                case 1:
                    //bindingPhone
                    if (TextUtils.isEmpty(bindingPhone)) {
                        mView.showToastMsg(R.string.phone_number_can_not_empty);
                        return;
                    }
                    if (!PhoneNumberUtils.isMobileNumber(bindingPhone)) {
                        mView.showToastMsg(R.string.phone_number_wrong);
                        return;
                    }
                    mView.setPhoneEditAble(false);
                    preUserInfo(bindingPhone);
                    break;
            }
        }
    }


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

    //使用手机号码验证是否绑定身份证
    private void preUserInfo(final String phone) {
        mView.showProgressDialog();
        Subscription subscription = UserRepository.getInstance().preUserInfo(phone)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.dismissProgressDialog();
                            if (e instanceof ApiException) {
                                switch (((ApiException) e).getCode()) {
                                    case 30100:
                                        mView.pleaseLoginTip();
                                        break;
                                    case 30108:
                                        mView.showToastMsg(R.string.phone_number_wrong);
                                        break;
                                }
                            } else {
                                mView.showToastMsg(R.string.network_fault);
                            }
                        }
                    }

                    @Override
                    public void onNext(String s) {
                        if (null != mView) {
                            mView.dismissProgressDialog();
                            if (s != null && !s.equals("")) {
                                //提示用户新手机已绑定账号，是否需要绑定
                                String myIdCard = s.substring(6, 14);
                                String newIdCard = s.replace(myIdCard, "****");
                                mView.setBindingPhoneTips(newIdCard, phone);
                            } else {
                                //发送验证码到手机
                                sendVerifyCodeByPhoneNumber(phone);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 发送验证码
     *
     * @param phone 手机号码
     */
    @Override
    public void sendVerifyCodeByPhoneNumber(String phone) {
        mView.showProgressDialog();
        Subscription subscription = UserRepository.getInstance().getVerifyCode(phone)
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
                                    case 30100:
                                        mView.pleaseLoginTip();
                                        break;
                                    case 30111:
                                        mView.showToastMsg(R.string.phone_num_exist);
                                        mView.sendVerifyMessageCode(false);
                                        break;
                                    case 30112:
                                    default:
                                        mView.showToastMsg(R.string.send_verify_code_failed);
                                        mView.sendVerifyMessageCode(false);
                                        break;
                                }
                            } else {
                                mView.showToastMsg(R.string.send_verify_code_failed);
                                mView.sendVerifyMessageCode(false);
                            }
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (mView != null) {
                            mView.dismissProgressDialog();
                            if (aBoolean) {
                                mView.showToastMsg(R.string.send_verify_code_success);
                                mView.sendVerifyMessageCode(true);
                                startTime();
                            } else {
                                mView.showToastMsg(R.string.send_verify_code_failed);
                                mView.sendVerifyMessageCode(false);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void submitVerifyCodeAndChangePhoneNumber(String newPhone, String bindingPhone, String code) {
        String phone = null;
        switch (mFromType) {
            case 0:
                phone = newPhone;
                break;
            case 1:
                phone = bindingPhone;
                break;
        }
        if (TextUtils.isEmpty(phone)) {
            mView.showToastMsg(R.string.phone_number_can_not_empty);
            return;
        }
        if (TextUtils.isEmpty(code)) {
            mView.showToastMsg(R.string.code_can_not_empty);
            return;
        }
        verifyCode(phone, code);
    }

    private void verifyCode(final String phone, String code) {
        mView.showProgressDialog();
        final String idCard = UserRepository.getInstance().getLoginUserIdCard();
        if (TextUtils.isEmpty(idCard)) {
            return;
        }
        Subscription subscription = UserRepository.getInstance().checkVerifyCode(code, idCard, phone)
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
                                    case 30100:
                                        mView.pleaseLoginTip();
                                        break;
                                    case 30106://验证码失效
                                        mView.showToastMsg(R.string.verify_code_invalid);
                                        break;
                                    case 30105://手机号与接收验证码的不一致
                                        mView.showToastMsg(R.string.phone_number_wrong);
                                        break;
                                    default:
                                        if (mFromType == 0) {
                                            mView.showToastMsg(R.string.modify_binding_phone_num_failed);
                                        } else {
                                            mView.showToastMsg(R.string.binding_phone_num_failed);
                                        }
                                        break;
                                }
                            } else {
                                mView.showToastMsg(R.string.network_fault);
                                mView.sendVerifyMessageCode(false);
                            }
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (mView != null) {
                            mView.dismissProgressDialog();
                            if (aBoolean) {
                                if (mFromType == 0) {
                                    mView.showToastMsg(R.string.modify_binding_phone_num_success);
                                    mView.changePhoneSuccess();
                                } else {
                                    mView.showToastMsg(R.string.binding_phone_num_success);
                                    mView.changePhoneSuccess();
                                }
                            } else {
                                if (mFromType == 0) {
                                    mView.showToastMsg(R.string.modify_binding_phone_num_failed);
                                } else {
                                    mView.showToastMsg(R.string.binding_phone_num_failed);
                                }
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }
}
