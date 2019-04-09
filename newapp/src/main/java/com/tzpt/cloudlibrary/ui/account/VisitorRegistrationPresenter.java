package com.tzpt.cloudlibrary.ui.account;

import android.text.TextUtils;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;
import com.tzpt.cloudlibrary.ui.readers.ApplyActionMessage;
import com.tzpt.cloudlibrary.utils.IdCardUtils;

import org.greenrobot.eventbus.EventBus;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 读友会未注册读者登记
 * Created by ZhiqiangJia on 2017-10-10.
 */
public class VisitorRegistrationPresenter extends RxPresenter<VisitorRegistrationContract.View> implements
        VisitorRegistrationContract.Presenter {

    @Override
    public void startVisitorRegistration(int actionId, String name, String idCard, String phone) {
        if (TextUtils.isEmpty(name)) {
            mView.showToastMsg("姓名不能为空！");
            return;
        }
        if (TextUtils.isEmpty(idCard)) {
            mView.showToastMsg("身份证不能为空！");
            return;
        }
        if (idCard.length() <= 18 && !IdCardUtils.verifyIdCardNumber(idCard)) {
            mView.showToastMsg("身份证号错误！");
            return;
        }
        Subscription subscription = DataRepository.getInstance().applyAction(actionId, idCard, name, phone)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            if (e instanceof ApiException) {
                                switch (((ApiException) e).getCode()) {
                                    case 30602:
                                        mView.showToastMsg("活动已经开始，不允许报名！");
                                        break;
                                    case 30603:
                                        mView.showToastMsg("已注册请登录报名！");
                                        break;
                                    case 30604:
                                        ApplyActionMessage apply1 = new ApplyActionMessage();
                                        apply1.mActionStatus = "已报名";
                                        EventBus.getDefault().post(apply1);
                                        mView.showToastMsg("已报名！");
                                        break;
                                    case 30605:// 报名截止
                                        ApplyActionMessage apply2 = new ApplyActionMessage();
                                        apply2.mActionStatus = "报名截止";
                                        EventBus.getDefault().post(apply2);
                                        mView.showToastMsg("报名截止！");
                                        break;
                                    case 30606:// 活动结束
                                        ApplyActionMessage apply3 = new ApplyActionMessage();
                                        apply3.mActionStatus = "已结束";
                                        EventBus.getDefault().post(apply3);
                                        mView.showToastMsg("已结束！");
                                        break;
//                                    case 30607:// 活动未开启报名
//                                        ApplyActionMessage apply4 = new ApplyActionMessage();
//                                        apply4.mActionStatus = "已报名";
//                                        EventBus.getDefault().post(apply4);
//                                        mView.showToastMsg("已报名！");
//                                        break;
                                    case 30608:// 名额已满
                                        ApplyActionMessage apply5 = new ApplyActionMessage();
                                        apply5.mActionStatus = "报名已满";
                                        EventBus.getDefault().post(apply5);
                                        mView.showToastMsg("报名已满！");
                                        break;
                                    default:
                                        mView.showToastMsg(R.string.network_fault);
                                        break;
                                }
                            } else {
                                mView.showToastMsg(R.string.network_fault);
                            }
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (mView != null) {
                            if (aBoolean) {
                                mView.showToastMsg("报名成功！");
                                mView.registerSuccess();
                                ApplyActionMessage apply = new ApplyActionMessage();
                                apply.mActionStatus = "已报名";
                                EventBus.getDefault().post(apply);
                            } else {
                                mView.showToastMsg(R.string.network_fault);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }
}
