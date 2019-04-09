package com.tzpt.cloundlibrary.manager.ui.presenter;

import android.text.TextUtils;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseResponseCode;
import com.tzpt.cloundlibrary.manager.base.RxPresenter;
import com.tzpt.cloundlibrary.manager.bean.ReaderInfo;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.modle.remote.exception.ApiException;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.ReaderPwdModifyVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.VerifyCodeVo;
import com.tzpt.cloundlibrary.manager.ui.contract.ReaderPwdContract;
import com.tzpt.cloundlibrary.manager.utils.MD5Util;
import com.tzpt.cloundlibrary.manager.utils.StringUtils;

import org.json.JSONObject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 读者密码管理
 * Created by Administrator on 2017/7/12.
 */
public class ReaderPwdPresenter extends RxPresenter<ReaderPwdContract.View> implements
        ReaderPwdContract.Presenter,
        BaseResponseCode {

    private String mIdCard;

    @Override
    public void getReaderInfo(String readerId) {

        Subscription subscription = DataRepository.getInstance().getReaderInfo(readerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ReaderInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            if (e instanceof ApiException) {
                                switch (((ApiException) e).getCode()) {
                                    case ERROR_CODE_KICK_OUT:
                                        mView.showDialogNoPermission(R.string.kicked_offline);
                                        break;
                                    case ERROR_CODE_1006:
                                        mView.showDialogNoPermission(R.string.operate_timeout);
                                        break;
                                    default:
                                        mView.showDialogGetInfoFailedRetry("网络请求失败！");
                                        break;
                                }
                            } else {
                                mView.showDialogGetInfoFailedRetry("网络请求失败！");
                            }
                        }
                    }

                    @Override
                    public void onNext(ReaderInfo readerInfo) {
                        if (mView != null) {
                            mView.setReaderName(readerInfo.mCardName);
                            mIdCard = readerInfo.mIdCard;
                            mView.setReaderIdCard(readerInfo.mIdCard);
                            mView.setReaderPhone(readerInfo.mPhone);
                            mView.setReaderGender(("MALE").equals(readerInfo.mGender) ? "男" : "女");
                            mView.setReaderHead("https://img.ytsg.cn/" + readerInfo.mIdCardImage);
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void getCode(String telNum, int isContinue) {
        if (TextUtils.isEmpty(telNum)) {
            mView.showToastMsg("手机号不能为空！");
            return;
        }
        if (!StringUtils.isMobileNumber(telNum)) {
            mView.showToastMsg("手机号码错误！");
            return;
        }
        Subscription subscription = DataRepository.getInstance().sendCode(telNum, isContinue)
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
                    public void onNext(VerifyCodeVo sendCodeVo) {
                        if (mView != null) {
                            if (null != sendCodeVo) {
                                if (sendCodeVo.status == CODE_SUCCESS) {
                                    mView.sendVerifyMessageCode(true, "短信发送成功！");
                                } else {
                                    if (null != sendCodeVo.data) {
                                        switch (sendCodeVo.data.errorCode) {
                                            case ERROR_CODE_3201:
//                                                mView.sendVerifyMessageCode(false, "手机号已注册！");
                                                String bundleIdCard = sendCodeVo.data.errorData;
                                                if (mIdCard.equals(bundleIdCard)) {
                                                    mView.showDialogModifyFailed(R.string.tel_is_same);
                                                } else {
                                                    mView.showDialogTelUnBundle(bundleIdCard);
                                                }

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
    public void modifyInfo(String pwd, String surePwd, String phone, String code) {
        if (null != mView) {
            if (TextUtils.isEmpty(mIdCard)) {
                mView.showToastMsg("身份证号码不能为空!");
                return;
            }
            if (TextUtils.isEmpty(pwd) && TextUtils.isEmpty(surePwd) && TextUtils.isEmpty(phone)) {
                mView.showToastMsg("输入不能为空!");
                return;
            }
            final JSONObject object = new JSONObject();
            try {
                //修改密码
                if (!TextUtils.isEmpty(pwd) || !TextUtils.isEmpty(surePwd)) {
                    if (pwd.length() < 6 || surePwd.length() < 6) {
                        mView.showToastMsg("密码为6位数字!");
                        return;
                    }
                    if (!pwd.equals(surePwd)) {
                        mView.showToastMsg("新密码不一致!");
                        return;
                    }
                    object.put("idPassword", MD5Util.MD5(pwd));
                }
                //修改密码
                if (!TextUtils.isEmpty(phone)) {
                    if (!StringUtils.isMobileNumber(phone)) {
                        mView.showToastMsg("手机号码错误！");
                        return;
                    }
                    if (TextUtils.isEmpty(code)) {
                        mView.showToastMsg("验证码不能为空!");
                        return;
                    }
                    object.put("phone", phone);
                    object.put("code", code);
                }
                object.put("idCard", mIdCard);
            } catch (Exception e) {
            }
            mView.showLoadingDialog("发送中...");
            Subscription subscription = DataRepository.getInstance().modifyReaderPwdOrPhone(object.toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(mModifyPwdObserver);
            addSubscrebe(subscription);
        }
    }

    private Observer<ReaderPwdModifyVo> mModifyPwdObserver = new Observer<ReaderPwdModifyVo>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            if (mView != null) {
                mView.dismissLoadingDialog();
                mView.showDialogModifyFailed(R.string.network_fault);
            }
        }

        @Override
        public void onNext(ReaderPwdModifyVo readerPwdModifyVo) {
            if (mView != null) {
                mView.dismissLoadingDialog();
                if (readerPwdModifyVo.status == CODE_SUCCESS) {
                    mView.showDialogModifySuccess(R.string.change_success);
                } else {
                    if (null != readerPwdModifyVo.data) {
                        //3105:验证码失效或者错误 3104; 手机号码错误 3101:修改失败
                        switch (readerPwdModifyVo.data.errorCode) {
                            case ERROR_CODE_KICK_OUT:
                                mView.showDialogNoPermission(R.string.kicked_offline);
                                break;
                            case ERROR_CODE_1006:
                                mView.showDialogNoPermission(R.string.operate_timeout);
                                break;
                            case ERROR_CODE_3101:
                                mView.showDialogModifyFailed(R.string.error_code_3001);
                                break;
                            case ERROR_CODE_3104://手机号码错误
                                mView.showDialogModifyFailed(R.string.phone_num_error);
                                break;
                            case ERROR_CODE_3105://验证码错误
                                mView.showDialogModifyFailed(R.string.verify_code_error);
                                break;
                            default:
                                mView.showDialogModifyFailed(R.string.error_code_3001);
                                break;
                        }
                    } else {
                        mView.showDialogModifyFailed(R.string.error_code_3001);
                    }
                }
            }
        }
    };
}
