package com.tzpt.cloundlibrary.manager.ui.presenter;

import android.text.TextUtils;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseResponseCode;
import com.tzpt.cloundlibrary.manager.base.RxPresenter;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.VerifyCodeVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.VerifyIdentityVo;
import com.tzpt.cloundlibrary.manager.ui.contract.VerifyIdentityContract;
import com.tzpt.cloundlibrary.manager.utils.StringUtils;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/9/26.
 */

public class VerifyIdentityPresenter extends RxPresenter<VerifyIdentityContract.View>
        implements VerifyIdentityContract.Presenter, BaseResponseCode {

    @Override
    public void sendCode(String hallCode, String phone) {
        if (TextUtils.isEmpty(hallCode)) {
            mView.showToastMsg("馆号不能为空！");
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            mView.showToastMsg("手机号不能为空！");
            return;
        }
        if (!StringUtils.isMobileNumber(phone)) {
            mView.showToastMsg("手机号码错误！");
            return;
        }
        Subscription subscription = DataRepository.getInstance().sendVerifyForgetPwd(hallCode, phone)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<VerifyCodeVo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.sendVerifyMessageCode(false, "网络请求失败！");
                        }
                    }

                    @Override
                    public void onNext(VerifyCodeVo verifyCodeVo) {
                        if (mView != null) {
                            if (null != verifyCodeVo) {
                                if (verifyCodeVo.status == CODE_SUCCESS) {
                                    mView.sendVerifyMessageCode(true, "短信发送成功！");
                                } else {
                                    if (null != verifyCodeVo.data) {
                                        switch (verifyCodeVo.data.errorCode) {
                                            case ERROR_CODE_1001://馆不存在
                                                mView.sendVerifyMessageCode(false, "馆不存在！");
                                                break;
                                            case ERROR_CODE_1002://手机号错误
                                                mView.sendVerifyMessageCode(false, "手机号码错误！");
                                                break;
                                            case ERROR_CODE_1014://无效用户
                                                mView.sendVerifyMessageCode(false, "无效用户！");
                                                break;
                                            case ERROR_CODE_3202:
                                                mView.sendVerifyMessageCode(false, "操作频繁！");
                                                break;
                                            case ERROR_CODE_KICK_OUT:
                                                mView.showDialogNoPermission(R.string.kicked_offline);
                                                break;
                                            case ERROR_CODE_1006:
                                                mView.showDialogNoPermission(R.string.operate_timeout);
                                                break;
                                            default:
                                                mView.sendVerifyMessageCode(false, "发送验证码失败！");
                                                break;
                                        }
                                    } else {
                                        mView.sendVerifyMessageCode(false, "发送验证码失败！");
                                    }
                                }
                            } else {
                                mView.sendVerifyMessageCode(false, "发送验证码失败！");
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void verifyIdentity(String code, String hallCode, String phone) {
        if (TextUtils.isEmpty(hallCode)) {
            mView.showToastMsg("馆号不能为空！");
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            mView.showToastMsg("手机号不能为空！");
            return;
        }
        if (!StringUtils.isMobileNumber(phone)) {
            mView.showToastMsg("手机号码错误！");
            return;
        }
        if (TextUtils.isEmpty(code)) {
            mView.showToastMsg("验证码不能为空！");
            return;
        }
        Subscription subscription = DataRepository.getInstance().verifyIdentity(code, hallCode, phone)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<VerifyIdentityVo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.showDialogVerifyFailed(R.string.validation_fails);
                        }
                    }

                    @Override
                    public void onNext(VerifyIdentityVo verifyIdentityVo) {
                        if (mView != null) {
                            if (verifyIdentityVo.status == CODE_SUCCESS) {
                                mView.verifySuccess(verifyIdentityVo.data.hallCode, verifyIdentityVo.data.username, verifyIdentityVo.data.id);
                            } else {
                                switch (verifyIdentityVo.data.errorCode) {
                                    case ERROR_CODE_1001://馆不存在
                                        mView.showDialogVerifyFailed(R.string.library_is_not_exists);
                                        break;
                                    case ERROR_CODE_1002://用户不存在
                                        mView.showDialogVerifyFailed(R.string.account_is_not_exists);
                                        break;
                                    case ERROR_CODE_1014://无效用户
                                        mView.showDialogVerifyFailed(R.string.account_is_lost);
                                        break;
                                    case ERROR_CODE_3104:
                                        mView.showDialogVerifyFailed(R.string.phone_num_error);
                                        break;
                                    case ERROR_CODE_3105:
                                        mView.showDialogVerifyFailed(R.string.verify_code_error);
                                        break;
                                    default:
                                        mView.showDialogVerifyFailed(R.string.validation_fails);
                                        break;
                                }
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }
}
