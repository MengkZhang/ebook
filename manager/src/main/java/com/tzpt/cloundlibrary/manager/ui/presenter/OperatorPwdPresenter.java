package com.tzpt.cloundlibrary.manager.ui.presenter;

import android.text.TextUtils;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseResponseCode;
import com.tzpt.cloundlibrary.manager.base.RxPresenter;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.ChangeOperatorPwdVo;
import com.tzpt.cloundlibrary.manager.ui.contract.OperatorPwdContract;
import com.tzpt.cloundlibrary.manager.utils.MD5Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 修改操作员密码
 * Created by Administrator on 2017/7/3.
 */
public class OperatorPwdPresenter extends RxPresenter<OperatorPwdContract.View> implements
        OperatorPwdContract.Presenter,
        BaseResponseCode {

    @Override
    public void getOperatorName() {
        mView.setOperatorName(DataRepository.getInstance().getLibraryInfo().mOperaterName);
    }

    private boolean checkPsw(String psw) {
        Pattern pattern = Pattern
                .compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,20}$");
        Matcher matcher = pattern.matcher(psw);
        return matcher.matches();
    }

    @Override
    public void changeOperatorPwd(String oldPwd, String newPwd, String sureNewPwd) {
        if (TextUtils.isEmpty(oldPwd)) {
            mView.changePwdFailed("旧密码不能为空！");
            return;
        }
        if (TextUtils.isEmpty(newPwd)) {
            mView.changePwdFailed("新密码不能为空！");
            return;
        }
        if (TextUtils.isEmpty(sureNewPwd)) {
            mView.changePwdFailed("新密码不能为空！");
            return;
        }
        if (!checkPsw(newPwd) || !checkPsw(sureNewPwd)) {
            mView.changePwdFailed("长度不少于8位，必须包含大小写字母和数字！");
            return;
        }
        if (!newPwd.equals(sureNewPwd)) {
            mView.changePwdFailed("新密码不一致！");
            return;
        }

        mView.showLoadingProgress("发送中...");
        Subscription subscription = DataRepository.getInstance().changeOperatorPwd(MD5Util.MD5(oldPwd), MD5Util.MD5(newPwd))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ChangeOperatorPwdVo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.hideLoadingProgress();
                            mView.changePwdFailed(R.string.network_fault);
                        }
                    }

                    @Override
                    public void onNext(ChangeOperatorPwdVo changeOperatorPwdVo) {
                        if (mView != null) {
                            mView.hideLoadingProgress();
                            if (changeOperatorPwdVo.status == CODE_SUCCESS) {
                                mView.changePwdSuccess("修改成功！");
                            } else {
                                switch (changeOperatorPwdVo.data.errorCode) {
                                    case ERROR_CODE_KICK_OUT:
                                        mView.noPermissionPrompt(R.string.kicked_offline);
                                        break;
                                    case ERROR_CODE_1006:
                                        mView.noPermissionPrompt(R.string.operate_timeout);
                                        break;
                                    case ERROR_CODE_1003:
                                        mView.changePwdFailed("原密码错误！");
                                        break;
                                    case ERROR_CODE_1002:
                                        mView.changePwdFailed("用户不存在！");
                                        break;
                                    default:
                                        mView.changePwdFailed(R.string.error_code_500);
                                        break;
                                }
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }
}
