package com.tzpt.cloundlibrary.manager.ui.presenter;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Base64;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseResponseCode;
import com.tzpt.cloundlibrary.manager.base.RxPresenter;
import com.tzpt.cloundlibrary.manager.bean.LibraryInfo;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.PenaltyDealResultVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.RegisterVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.VerifyCodeVo;
import com.tzpt.cloundlibrary.manager.ui.contract.ReaderRegisterContract;
import com.tzpt.cloundlibrary.manager.utils.MD5Util;
import com.tzpt.cloundlibrary.manager.utils.StringUtils;

import java.io.ByteArrayOutputStream;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * 读者注册发送验证码
 * Created by Administrator on 2017/7/6.
 */
public class ReaderRegisterPresenter extends RxPresenter<ReaderRegisterContract.View> implements
        ReaderRegisterContract.Presenter,
        BaseResponseCode {

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
        mView.showLoading("发送中...");
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
                            mView.dismissLoading();
                            mView.sendVerifyMessageCode(false, "网络请求失败！");
                        }
                    }

                    @Override
                    public void onNext(VerifyCodeVo sendCodeVo) {
                        if (mView != null) {
                            mView.dismissLoading();
                            if (null != sendCodeVo) {
                                if (sendCodeVo.status == CODE_SUCCESS) {
                                    mView.sendVerifyMessageCode(true, "短信发送成功！");
                                } else {
                                    if (null != sendCodeVo.data) {
                                        switch (sendCodeVo.data.errorCode) {
                                            case ERROR_CODE_3201:
//                                                mView.sendVerifyMessageCode(false, "手机号已注册！");
                                                String bundleIdCard = sendCodeVo.data.errorData;
                                                mView.showDialogTelUnBundle(bundleIdCard);
                                                break;
                                            case ERROR_CODE_3202:
                                                mView.sendVerifyMessageCode(false, "操作频繁！");
                                                break;
                                            case ERROR_CODE_KICK_OUT:
                                                mView.noPermissionPrompt(R.string.kicked_offline);
                                                break;
                                            case ERROR_CODE_1006:
                                                mView.noPermissionPrompt(R.string.operate_timeout);
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
    public void register(String number, String code, String name, Bitmap headImage, String pwd,
                         String surePwd, String telNum, String gender, String bundleTel) {
        if (TextUtils.isEmpty(number)) {
            mView.showToastMsg("身份证号不能为空！");
            return;
        }
        if (headImage == null || headImage.isRecycled()) {
            mView.showToastMsg("头像不能为空！");
            return;
        }
        if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(surePwd)) {
            mView.showToastMsg("密码不能为空！");
            return;
        }
        if (pwd.length() < 6 || surePwd.length() < 6) {
            mView.showToastMsg("密码为6位数字！");
            return;
        }

        if (!pwd.equals(surePwd)) {
            mView.showToastMsg("密码不一致！");
            return;
        }
        if (TextUtils.isEmpty(name)) {
            mView.showToastMsg("姓名不能为空！");
            return;
        }
        if (!TextUtils.isEmpty(telNum)) {           //如果电话号码的值不为空
            if (TextUtils.isEmpty(bundleTel)) {     //如果没有绑定电话号码
                if (!StringUtils.isMobileNumber(telNum)) {
                    mView.showToastMsg("手机号错误！");
                    return;
                }
                if (TextUtils.isEmpty(code)) {
                    mView.showToastMsg("验证码不能为空！");
                    return;
                }
            } else {//如果绑定了电话号码
                if (!telNum.equals(bundleTel)) {    //如果绑定号码与输入号码不一致
                    if (!TextUtils.isEmpty(telNum) && !StringUtils.isMobileNumber(telNum)) {
                        mView.showToastMsg("手机号错误！");
                        return;
                    }
                    if (TextUtils.isEmpty(code)) {
                        mView.showToastMsg("验证码不能为空！");
                        return;
                    }
                } else {//如果绑定号码与输入号码一致
                    if (!TextUtils.isEmpty(code)) {//如果输入了验证码
                        telNum = bundleTel;
                    } else {
                        telNum = "";
                    }
                }
            }
        }
        LibraryInfo libraryInfo = DataRepository.getInstance().getLibraryInfo();
        if (null != libraryInfo && null != libraryInfo.mHallCode) {
            mView.showLoading("注册中...");
            String image = Bitmap2StrByBase64(headImage);
            Subscription subscription = DataRepository.getInstance().register(number, name, telNum, MD5Util.MD5(pwd), image, gender, libraryInfo.mHallCode, code)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<RegisterVo>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (mView != null) {
                                mView.dismissLoading();
                                mView.showDialogRegisterFailed(R.string.network_fault);
                            }
                        }

                        @Override
                        public void onNext(RegisterVo registerVo) {
                            if (mView != null) {
                                mView.dismissLoading();
                                if (registerVo == null) {
                                    mView.showDialogRegisterFailed(R.string.register_error);
                                } else {
                                    if (registerVo.status == CODE_SUCCESS) {
                                        if (null != registerVo.data && !TextUtils.isEmpty(registerVo.data.readerId)) {
                                            mView.registerSuccess(registerVo.data.readerId);
                                        } else {
                                            mView.showDialogRegisterFailed(R.string.register_error);
                                        }
                                    } else {
                                        if (null != registerVo.data) {
                                            switch (registerVo.data.errorCode) {
                                                case ERROR_CODE_KICK_OUT:
                                                    mView.noPermissionPrompt(R.string.kicked_offline);
                                                    break;
                                                case ERROR_CODE_1006:
                                                    mView.noPermissionPrompt(R.string.operate_timeout);
                                                    break;
                                                case ERROR_CODE_3105://验证码失效或者错误
                                                    mView.showDialogRegisterFailed(R.string.verify_code_error);
                                                    break;
                                                case ERROR_CODE_3106://读者新增失败
                                                case ERROR_CODE_3110://添加读者ACCOUNT失败
                                                    mView.showDialogRegisterFailed(R.string.register_error);
                                                    break;
                                                default:
                                                    mView.showDialogRegisterFailed(R.string.register_error);
                                                    break;
                                            }
                                        } else {
                                            mView.showDialogRegisterFailed(R.string.network_fault);
                                        }
                                    }
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);

        }
    }


    /**
     * 通过Base32将Bitmap转换成Base64字符串
     *
     */
    private String Bitmap2StrByBase64(Bitmap bit) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.JPEG, 40, bos);//参数100表示不压缩
        byte[] bytes = bos.toByteArray();
        return "data:image/png;base64," + Base64.encodeToString(bytes, Base64.DEFAULT);
    }

//    private void autoDealPenalty(String readerId){
//        Subscription subscription = DataRepository.getInstance().autoDealPenalty(readerId)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<PenaltyDealResultVo>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        if (null != mView) {
//                            mView.dismissLoading();
//                            mView.showDialogRegisterFailed(R.string.network_fault);
//                        }
//                    }
//
//                    @Override
//                    public void onNext(PenaltyDealResultVo penaltyDealResultVo) {
//                        if (null != mView) {
//                            mView.dismissLoading();
//                            if (penaltyDealResultVo.status == CODE_SUCCESS) {
//                                if (penaltyDealResultVo.data.failPenalty > 0) {
//                                    mView.turnToDealPenalty(readerId);
//                                } else {
//                                    mView.registerSuccess(readerId);
//                                }
//                            } else {
//                                if (null != penaltyDealResultVo.data) {
//                                    switch (penaltyDealResultVo.data.errorCode) {
//                                        case ERROR_CODE_KICK_OUT:
//                                            mView.noPermissionPrompt(R.string.kicked_offline);
//                                            break;
//                                        case ERROR_CODE_1006:
//                                            mView.noPermissionPrompt(R.string.operate_timeout);
//                                            break;
//                                        case ERROR_CODE_3108:
//                                            mView.showDialogRegisterFailed(R.string.reader_does_not_exist);
//                                            break;
//                                        case ERROR_CODE_3400://3400:余额不足
////                                            mView.readerLoginSuccess(readerId, idCard);
//                                            break;
//                                        default:
//                                            mView.showDialogRegisterFailed(R.string.network_fault);
//                                            break;
//                                    }
//                                } else {
//                                    mView.showDialogRegisterFailed(R.string.network_fault);
//                                }
//                            }
//                        }
//                    }
//                });
//        addSubscrebe(subscription);
//
//    }

}
