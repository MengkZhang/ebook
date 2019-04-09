package com.tzpt.cloudlibrary.ui.account.interaction;

import android.text.TextUtils;

import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.bean.OverdueMsgBean;
import com.tzpt.cloudlibrary.business_bean.BaseListResultData;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseDataResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseResultEntityVo;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;

import org.greenrobot.eventbus.EventBus;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 逾期消息
 * Created by Administrator on 2018/3/30.
 */
public class OverdueMsgPresenter extends RxPresenter<OverdueMsgContract.View>
        implements OverdueMsgContract.Presenter {

    @Override
    public void getOverdueMsg(final int pageNum) {
        String idCard = UserRepository.getInstance().getLoginUserIdCard();

        if (!TextUtils.isEmpty(idCard)) {
            Subscription subscription = DataRepository.getInstance().getOverdueMsg(idCard, pageNum, 10)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseListResultData<OverdueMsgBean>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (mView != null) {
                                if (e instanceof ApiException) {
                                    if (((ApiException) e).getCode() == 30100) {
                                        mView.pleaseLoginTip();
                                    } else {
                                        mView.showNetError(pageNum == 1);
                                    }
                                } else {
                                    mView.showNetError(pageNum == 1);
                                }
                            }
                        }

                        @Override
                        public void onNext(BaseListResultData<OverdueMsgBean> overdueMsgBeanBaseListResultData) {
                            if (mView != null) {
                                if (overdueMsgBeanBaseListResultData != null
                                        && overdueMsgBeanBaseListResultData.mResultList != null
                                        && overdueMsgBeanBaseListResultData.mResultList.size() > 0) {
                                    mView.setMyMessageList(overdueMsgBeanBaseListResultData.mResultList,
                                            overdueMsgBeanBaseListResultData.mTotalCount,
                                            pageNum == 1);
                                } else {
                                    mView.showMyMessageListEmpty(pageNum == 1);
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        }

    }

    @Override
    public void readOverdueMsg(int id) {
        UserRepository.getInstance().readOverdueMsg();
        AccountMessage accountMessage = new AccountMessage();
        accountMessage.mIsRefreshUserInfo = true;
        EventBus.getDefault().post(accountMessage);

        Subscription subscription = DataRepository.getInstance().readOverdueMsg(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResultEntityVo<BaseDataResultVo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(BaseResultEntityVo<BaseDataResultVo> baseDataResultVoBaseResultEntityVo) {

                    }
                });
        addSubscrebe(subscription);
    }
}
