package com.tzpt.cloundlibrary.manager.ui.presenter;

import android.text.TextUtils;

import com.tzpt.cloundlibrary.manager.ManagerApplication;
import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseResponseCode;
import com.tzpt.cloundlibrary.manager.base.RxPresenter;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.SetFirstOperatorPswVo;
import com.tzpt.cloundlibrary.manager.ui.contract.SetOperatorFirstPswContract;
import com.tzpt.cloundlibrary.manager.utils.MD5Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 操作员首次登录修改密码
 * Created by tonyjia on 2018/12/11.
 */
public class SetOperatorFirstPswPresenter extends RxPresenter<SetOperatorFirstPswContract.View> implements
        SetOperatorFirstPswContract.Presenter,
        BaseResponseCode {

    @Override
    public void saveOperatorPwd(String newPsw, String reNewPsw) {
        if (TextUtils.isEmpty(newPsw)) {
            mView.changePwdFailed(R.string.new_psw_not_empty);
            return;
        }
        if (TextUtils.isEmpty(reNewPsw)) {
            mView.changePwdFailed(R.string.new_psw_not_empty);
            return;
        }
        if (!checkPsw(newPsw) || !checkPsw(reNewPsw)) {
            mView.changePwdFailed(R.string.new_psw_limit_error);
            return;
        }
        if (!newPsw.equals(reNewPsw)) {
            mView.changePwdFailed(R.string.new_psw_not_same);
            return;
        }
        mView.showLoadingProgress();
        Subscription subscription = DataRepository.getInstance().setFirstPassword(MD5Util.MD5(newPsw))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SetFirstOperatorPswVo>() {
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
                    public void onNext(SetFirstOperatorPswVo setFirstOperatorPswVo) {
                        if (mView != null) {
                            mView.hideLoadingProgress();
                            if (setFirstOperatorPswVo.status == 200) {
                                mView.changePwdSuccess();
                            } else {
                                if (setFirstOperatorPswVo.data != null) {
                                    switch (setFirstOperatorPswVo.data.errorCode) {
                                        case CODE_SERVICE_ERROR://无法通过token获取用户信息
                                            mView.changePwdFailed(R.string.error_code_500);
                                            break;
                                        case ERROR_CODE_1002://用户不存在
                                            mView.changePwdFailed(R.string.account_is_not_exists);
                                            break;
                                        case ERROR_CODE_1014://用户无效
                                            mView.changePwdFailed(R.string.account_is_lost);
                                            break;
                                        case ERROR_CODE_KICK_OUT://被踢下线
                                            mView.pleaseLoginTip(R.string.kicked_offline);
                                            break;
                                        case ERROR_CODE_1017://用户不是第一次登录
                                        default:
                                            mView.changePwdFailed(R.string.network_fault);
                                            break;
                                    }
                                }
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 清除操作token信息
     */
    @Override
    public void delOperatorToken() {
        ManagerApplication.TOKEN = "";
        DataRepository.getInstance().clearToken();
    }

    private boolean checkPsw(String psw) {
        Pattern pattern = Pattern
                .compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,20}$");
        Matcher matcher = pattern.matcher(psw);
        return matcher.matches();
    }

}
