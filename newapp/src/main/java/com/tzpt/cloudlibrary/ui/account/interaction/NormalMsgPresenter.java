package com.tzpt.cloudlibrary.ui.account.interaction;

import android.text.TextUtils;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.bean.CommentBean;
import com.tzpt.cloudlibrary.business_bean.BaseListResultData;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.InformationRepository;
import com.tzpt.cloudlibrary.modle.LibraryRepository;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;

import org.greenrobot.eventbus.EventBus;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 一般消息
 * Created by Administrator on 2018/3/29.
 */
public class NormalMsgPresenter extends RxPresenter<NormalMsgContract.View> implements NormalMsgContract.Presenter {

    @Override
    public boolean isLogin() {
        return UserRepository.getInstance().isLogin();
    }

    @Override
    public void getMyMessageList(final int pageNo) {
        if (pageNo == 1) {
            readNormalMsg();
        }
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (!TextUtils.isEmpty(readerId)) {
            Subscription subscription = DataRepository.getInstance().getMyMessageList(pageNo, 20, readerId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseListResultData<CommentBean>>() {
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
                                        mView.showNetError(pageNo == 1);
                                    }
                                } else {
                                    mView.showNetError(pageNo == 1);
                                }
                            }
                        }

                        @Override
                        public void onNext(BaseListResultData<CommentBean> commentBeanBaseListResultData) {
                            if (mView != null) {
                                if (commentBeanBaseListResultData != null
                                        && commentBeanBaseListResultData.mResultList != null
                                        && commentBeanBaseListResultData.mResultList.size() > 0) {
                                    mView.setMyMessageList(commentBeanBaseListResultData.mResultList,
                                            commentBeanBaseListResultData.mTotalCount,
                                            pageNo == 1);
                                } else {
                                    mView.setMyMessageListEmpty(pageNo == 1);
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        } else {
            mView.showNetError(pageNo == 1);
        }
    }

    @Override
    public void publishMessage(String libCode, String content, long replyId) {
        if (TextUtils.isEmpty(content)) {
            mView.showMsgDialog(R.string.content_not_empty);
            return;
        }
        if (replyId <= 0) {
            mView.showMsgDialog(R.string.network_fault);
            return;
        }

        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (!TextUtils.isEmpty(readerId)) {
            mView.showSendProgressDialog();
            if (TextUtils.isEmpty(libCode)) {
                //发布资讯评论的回复
                Subscription subscription = InformationRepository.getInstance().replyMsg(-1, content, readerId, replyId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(mReplyObserver);
                addSubscrebe(subscription);

            } else {
                //发布留言的回复
                //馆号不能为空
                Subscription subscription = LibraryRepository.getInstance().replyMessageBoard(readerId, -1, replyId, libCode, content)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(mReplyObserver);
                addSubscrebe(subscription);
            }
        }
    }

    private Observer<Void> mReplyObserver = new Observer<Void>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            if (mView != null) {
                mView.dismissSendProgressDialog();
                if (e instanceof ApiException) {
                    switch (((ApiException) e).getCode()) {
                        case 30100:
                            mView.pleaseLoginTip();
                            break;
                        case 30506:
                        case 30507:
                            mView.showNoCommentDialog();
                            break;
                        default:
                            mView.publishMsgFailure();
                            break;
                    }
                } else {
                    mView.showMsgDialog(R.string.network_fault);
                }
            }
        }

        @Override
        public void onNext(Void v) {
            if (null != mView) {
                mView.dismissSendProgressDialog();
                mView.publishMsgSuccess();
                getMyMessageList(1);
            }
        }
    };

    private void readNormalMsg() {
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (!TextUtils.isEmpty(readerId)) {
            Subscription subscription = DataRepository.getInstance().readMsg(readerId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(Boolean aBoolean) {
                            if (mView != null) {
                                if (aBoolean) {
                                    AccountMessage accountMessage = new AccountMessage();
                                    accountMessage.mIsRefreshUserInfo = true;
                                    EventBus.getDefault().post(accountMessage);
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }
}
