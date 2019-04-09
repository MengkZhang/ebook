package com.tzpt.cloundlibrary.manager.ui.presenter;

import android.text.TextUtils;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseResponseCode;
import com.tzpt.cloundlibrary.manager.base.RxPresenter;
import com.tzpt.cloundlibrary.manager.bean.LibraryInfo;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.ChangeRefundAccountVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.VerifyCodeVo;
import com.tzpt.cloundlibrary.manager.ui.contract.RefundAccountContact;
import com.tzpt.cloundlibrary.manager.utils.StringUtils;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/5/23.
 */

public class RefundAccountPresenter extends RxPresenter<RefundAccountContact.View>
        implements RefundAccountContact.Presenter, BaseResponseCode {

    @Override
    public void getCode() {
        mView.showLoading("发送中...");
        Subscription subscription = DataRepository.getInstance().getAdminVerifyCode()
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
                                            case ERROR_CODE_3202:
                                                mView.sendVerifyMessageCode(false, "操作频繁！");
                                                break;
                                            case ERROR_CODE_KICK_OUT:
                                                mView.noPermissionPrompt(R.string.kicked_offline);
                                                break;
                                            case ERROR_CODE_1006:
                                                mView.noPermissionPrompt(R.string.operate_timeout);
                                                break;
                                            case ERROR_CODE_3200:
                                                mView.sendVerifyMessageCode(false, "发送验证码失败！");
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
    public void changeRefundAccount(String code, String refundAccount, String refundName) {
        if (TextUtils.isEmpty(refundAccount)) {
            mView.showToastMsg("支付宝账号不能为空！");
            return;
        }
        if (!StringUtils.isMobileNumber(refundAccount) && !StringUtils.isEMail(refundAccount)) {
            mView.showToastMsg("请输入正确的支付宝账号！");
            return;
        }
        if (TextUtils.isEmpty(refundName)) {
            mView.showToastMsg("实名认证不能为空！");
            return;
        }
        if (TextUtils.isEmpty(code)) {
            mView.showToastMsg("验证码不能为空！");
            return;
        }
        mView.showLoading("设置中...");
        Subscription subscription = DataRepository.getInstance().changeRefundAccount(code, refundAccount, refundName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ChangeRefundAccountVo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.dismissLoading();
                            mView.showFailedDialog(R.string.network_fault);
                        }
                    }

                    @Override
                    public void onNext(ChangeRefundAccountVo changeRefundAccountVo) {
                        if (mView != null) {
                            mView.dismissLoading();
                            if (changeRefundAccountVo.status == 200) {
                                mView.showSuccessDialog();
                                LibraryInfo libraryInfo = DataRepository.getInstance().getLibraryInfo();
                                libraryInfo.mHaveRefundAccount = true;
                            } else {
                                if (null != changeRefundAccountVo.data) {
                                    switch (changeRefundAccountVo.data.errorCode) {
                                        case ERROR_CODE_KICK_OUT:
                                            mView.noPermissionPrompt(R.string.kicked_offline);
                                            break;
                                        case ERROR_CODE_1006:
                                            mView.noPermissionPrompt(R.string.operate_timeout);
                                            break;
                                        case ERROR_CODE_3105://验证码失效或错误
                                            mView.showFailedDialog(R.string.verify_code_error);
                                            break;
                                        case ERROR_CODE_3104:
                                            mView.showFailedDialog(R.string.phone_num_error);
                                            break;
                                        default:
                                            mView.showFailedDialog(R.string.change_refund_account_failed);
                                            break;
                                    }
                                } else {
                                    mView.showFailedDialog(R.string.change_refund_account_failed);
                                }
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }
}
