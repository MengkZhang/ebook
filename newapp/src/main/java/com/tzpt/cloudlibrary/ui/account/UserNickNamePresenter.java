package com.tzpt.cloudlibrary.ui.account;

import android.text.TextUtils;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;
import com.tzpt.cloudlibrary.utils.StringUtils;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 更新昵称
 * Created by tonyjia on 2018/11/6.
 */
public class UserNickNamePresenter extends RxPresenter<UserNickNameContract.View> implements
        UserNickNameContract.Presenter {

    @Override
    public void saveUserNickName(final String nickName) {
        if (TextUtils.isEmpty(nickName)) {
            mView.showMsgTipsDialog(R.string.nick_name_not_empty);
            return;
        }
        if (!StringUtils.isVerifyNickName(nickName)) {
            mView.showMsgTipsDialog(R.string.error_nick_name);
            return;
        }
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (!TextUtils.isEmpty(readerId)) {
            mView.showProgressDialog();
            Subscription subscription = UserRepository.getInstance().updateNickName(readerId, nickName)
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
                                            mView.pleaseLoginTips();
                                            break;
                                        case 30117:
                                            mView.showMsgTipsDialog(R.string.error_nick_name_to_retry);
                                            break;
                                        default:
                                            mView.showMsgTipsDialog(R.string.network_fault);
                                            break;
                                    }
                                } else {
                                    mView.showMsgTipsDialog(R.string.network_fault);
                                }
                            }
                        }

                        @Override
                        public void onNext(Boolean aBoolean) {
                            if (mView != null) {
                                mView.dismissProgressDialog();
                                if (aBoolean) {
                                    mView.showMsgTipsDialog(R.string.change_success);
                                    mView.updateUserNickNameSuccess(nickName);
                                } else {
                                    mView.showMsgTipsDialog(R.string.network_fault);
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }
}
