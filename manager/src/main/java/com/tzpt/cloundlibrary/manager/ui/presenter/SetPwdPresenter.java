package com.tzpt.cloundlibrary.manager.ui.presenter;

import android.text.TextUtils;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseResponseCode;
import com.tzpt.cloundlibrary.manager.base.RxPresenter;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.ResetPwdVo;
import com.tzpt.cloundlibrary.manager.ui.contract.SetPwdContract;
import com.tzpt.cloundlibrary.manager.utils.MD5Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/9/26.
 */

public class SetPwdPresenter extends RxPresenter<SetPwdContract.View>
        implements SetPwdContract.Presenter, BaseResponseCode {

    @Override
    public void setNewPwd(int id, String newPwd1, String newPwd2) {
        if (TextUtils.isEmpty(newPwd1)) {
            mView.showDialogTip("新密码不能为空！");
            return;
        }
        if (TextUtils.isEmpty(newPwd2)) {
            mView.showDialogTip("新密码不能为空！");
            return;
        }
        if (!checkPsw(newPwd1) || !checkPsw(newPwd2)) {
            mView.showDialogTip("密码不少于8位,且包含大写,小写字母和数字！");
            return;
        }

        if (!newPwd1.equals(newPwd2)) {
            mView.showDialogTip("新密码不一致！");
            return;
        }

        mView.showLoadingProgress("发送中...");
        Subscription subscription = DataRepository.getInstance().resetPwd(id, MD5Util.MD5(newPwd1))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResetPwdVo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.hideLoadingProgress();
                            mView.showDialogTip(R.string.network_fault);
                        }
                    }

                    @Override
                    public void onNext(ResetPwdVo resetPwdVo) {
                        if (mView != null) {
                            mView.hideLoadingProgress();
                            if (resetPwdVo.status == CODE_SUCCESS) {
                                mView.resetPwdSuccess();
                            } else {
                                switch (resetPwdVo.data.errorCode) {
                                    case ERROR_CODE_1008:
                                        mView.showDialogTip("修改失败！");
                                        break;
                                    case ERROR_CODE_1002:
                                        mView.showDialogTip("用户不存在！");
                                        break;
                                    default:
                                        mView.showDialogTip(R.string.error_code_500);
                                        break;
                                }
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private boolean checkPsw(String psw) {
        Pattern pattern = Pattern
                .compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,20}$");
        Matcher matcher = pattern.matcher(psw);
        return matcher.matches();
    }
}
