package com.tzpt.cloudlibrary.ui.account;

import android.text.TextUtils;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.bean.UserHeadBean;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;

import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 用户头像
 * Created by ZhiqiangJia on 2017-08-22.
 */
public class UserHeadPresenter extends RxPresenter<UserHeadContract.View> implements
        UserHeadContract.Presenter {
    @Override
    public void getUserHeadList() {
        Subscription subscription = UserRepository.getInstance().getUserHeadList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<UserHeadBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            if (e instanceof ApiException) {
                                switch (((ApiException) e).getCode()) {
                                    case 30100:
//                                        DataRepository.getInstance().quit();
//                                        UserRepository.getInstance().logout();
                                        mView.pleaseLoginTip();
                                        break;
                                    default:
                                        mView.showHeadImageError();
                                        break;
                                }
                            } else {
                                mView.showHeadImageError();
                            }
                        }
                    }

                    @Override
                    public void onNext(List<UserHeadBean> userHeadBeans) {
                        if (mView != null) {
                            if (userHeadBeans != null && userHeadBeans.size() > 0) {
                                mView.setUserHeadList(userHeadBeans);
                            } else {
                                mView.setUserHeadListEmpty();
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void submitUserHead(final String imagePath) {
        mView.showProgressDialog();
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (TextUtils.isEmpty(readerId)) {
            return;
        }
        Subscription subscription = UserRepository.getInstance().modifyHeadImg(readerId, imagePath, false)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.dismissProgressDialog();
                            if (e instanceof ApiException) {
                                switch (((ApiException) e).getCode()) {
                                    case 30100:
                                        mView.pleaseLoginTip();
                                        break;
                                    default:
                                        mView.showErrorMessage(R.string.network_fault);
                                        break;
                                }
                            } else {
                                mView.showErrorMessage(R.string.network_fault);
                            }
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (mView != null){
                            if (aBoolean){
                                mView.dismissProgressDialog();
                                mView.changeUserHeadSuccess();
                            }else{
                                mView.showErrorMessage(R.string.network_fault);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);

    }
}
