package com.tzpt.cloudlibrary.ui.account;

import android.text.TextUtils;

import com.tzpt.cloudlibrary.CloudLibraryApplication;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.business_bean.UserInfoBean;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;
import com.tzpt.cloudlibrary.utils.IdCardUtils;
import com.tzpt.cloudlibrary.utils.PhoneNumberUtils;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 登录
 * Created by ZhiqiangJia on 2017-08-18.
 */
public class LoginPresenter extends RxPresenter<LoginContract.View> implements
        LoginContract.Presenter {
    @Override
    public void getLoginCacheInfo() {
        String account = UserRepository.getInstance().getLoginUserIdCard();
        if (!TextUtils.isEmpty(account)) {
            if (account.length() >= 18) {
                String myIdCard = account.substring(10, 16);
                String newIdCard = account.replace(myIdCard, "**");
                mView.setLoginCacheInfo(newIdCard);
            } else {
                mView.setLoginCacheInfo(account);
            }
        }
    }

    @Override
    public void login(String account, final String password) {
        if (TextUtils.isEmpty(account)) {
            mView.showToastMessage(R.string.error_field_required);
            return;
        }
        //判断是否有星号，获取老账号
        String idCardCache = UserRepository.getInstance().getLoginUserIdCard();
        if (!TextUtils.isEmpty(idCardCache)) {
            String lastNumberIndex = account.substring(account.length() - 4, account.length() - 2);
            if (account.length() == 14 && lastNumberIndex.contains("**")) {
                if (!TextUtils.isEmpty(idCardCache) && idCardCache.length() == 18) {
                    //匹配前半段
                    String firstHalfOldIdCardNumber = idCardCache.substring(0, 10);
                    String firstCurrentIdCardNumber = account.substring(0, 10);
                    //匹配后半段
                    String lastHalfOldIdCardNumber = idCardCache.substring(idCardCache.length() - 2, idCardCache.length());
                    String lastCurrentIdCardNumber = account.substring(account.length() - 2, account.length());
                    if (firstHalfOldIdCardNumber.equals(firstCurrentIdCardNumber) && lastHalfOldIdCardNumber.equals(lastCurrentIdCardNumber)) {
                        account = idCardCache;
                    }
                }
            }
        }
        //验证用户名
        if (account.length() <= 11 && !PhoneNumberUtils.isMobileNumber(account)) {
            mView.showToastMessage(R.string.error_invalid_email);
            return;
        }
        //验证用户名
        if (account.length() > 11 && account.length() <= 18 && !IdCardUtils.verifyIdCardNumber(account)) {
            mView.showToastMessage(R.string.error_invalid_email);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            mView.showToastMessage(R.string.error_incorrect_password);
            return;
        }
        if (password.length() != 6) {
            mView.showToastMessage(R.string.error_invalid_password);
            return;
        }
        String phone;
        String idCard;
        //手机号码
        if (account.length() == 11 && PhoneNumberUtils.isMobileNumber(account)) {
            phone = account;
            idCard = null;
        } else if (account.length() == 18 && IdCardUtils.verifyIdCardNumber(account)) {
            idCard = account;
            phone = null;
        } else {
            return;
        }
        mView.showProgressDialog();
        Subscription subscription = UserRepository.getInstance().login(phone, idCard, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserInfoBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null && e instanceof ApiException) {
                            mView.dismissProgressDialog();
                            switch (((ApiException) e).getCode()) {
                                case 30102://用户未注册
                                    mView.showToastMessage(R.string.id_card_not_register);
                                    break;
                                case 30103://用户名或密码错误
                                    mView.showToastMessage(R.string.password_error);
                                    break;
                                case 30101://必须参数不能为空
                                    mView.showToastMessage(R.string.account_or_pwd_error);
                                    break;
                                default:
                                    //身份验证失败
                                    mView.showToastMessage(R.string.login_failure);
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onNext(UserInfoBean userInfoBean) {
                        if (mView != null){
                            mView.dismissProgressDialog();
                            mView.loginSuccess();
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 设置单姓
     *
     * @param userName 名称
     * @param isMan    身份证号
     * @return 单姓称谓
     */
    private String setOnLastName(String userName, boolean isMan) {
        //获取姓氏及性别,设置先生女士-暂时取单字
        String newName = userName.substring(0, 1);
        return newName + (isMan ? "先生" : "女士");
    }

}
