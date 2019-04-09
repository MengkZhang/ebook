package com.tzpt.cloudlibrary.ui.account.setting;

import android.text.TextUtils;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;
import com.tzpt.cloudlibrary.utils.MD5Utils;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 修改密码
 * Created by ZhiqiangJia on 2017-08-24.
 */
public class ChangePasswordPresenter extends RxPresenter<ChangePasswordContract.View> implements
        ChangePasswordContract.Presenter {
    @Override
    public void changePsw(String oldPassword, String newPassword, String repeatPsw) {
        if (TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(repeatPsw)) {
            mView.showToastMsg(R.string.error_incorrect_password);
            return;
        }
        if (!isPasswordValid(oldPassword) || !isPasswordValid(newPassword) || !isPasswordValid(repeatPsw)) {
            mView.showToastMsg(R.string.error_invalid_password);
            return;
        }
        if (!repeatPsw.equals(newPassword)) {
            mView.showToastMsg(R.string.error_compare_new_password);
            return;
        }
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (TextUtils.isEmpty(readerId)) {
            return;
        }
        mView.showProgressDialog();
        Subscription subscription = DataRepository.getInstance().modifyPwd(MD5Utils.MD5(newPassword), readerId, MD5Utils.MD5(oldPassword))
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
                                    case 30107:
                                        mView.showToastMsg(R.string.password_error);
                                        break;
                                    case 1000://读者信息异常
                                        mView.showToastMsg(R.string.change_failure);
                                        break;
                                    case 30104:
                                        mView.showToastMsg(R.string.change_failure);
                                        break;
                                    case 30100:
                                        mView.pleaseLoginTip();
                                        break;
                                    default:
                                        mView.showToastMsg(R.string.change_failure);
                                        break;
                                }
                            } else {
                                mView.showToastMsg(R.string.change_failure);
                            }
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (mView != null) {
                            mView.dismissProgressDialog();
                            if (aBoolean) {
                                mView.showToastMsg(R.string.change_success);
                                mView.changePasswordSuccess();
                            } else {
                                mView.showToastMsg(R.string.change_failure);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private boolean isPasswordValid(String password) {
        return password.length() == 6;
    }
}
