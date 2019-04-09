package com.tzpt.cloudlibrary.ui.library;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.bean.CommentBean;
import com.tzpt.cloudlibrary.business_bean.InformationCommentDetailBean;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.LibraryRepository;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 图书馆留言板
 * Created by ZhiqiangJia on 2018-01-10.
 */
public class LibraryMessageBoardPresenter extends RxPresenter<LibraryMessageBoardContract.View> implements
        LibraryMessageBoardContract.Presenter {

    @Override
    public boolean isLogin() {
        return UserRepository.getInstance().isLogin();
    }

    @Override
    public void getLibraryMessageBoardList(String libraryCode, final int pageNum, final long msgId) {
        Subscription subscription = LibraryRepository.getInstance().getMessageBoardList(libraryCode, msgId, pageNum)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<InformationCommentDetailBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.dismissDelProgressDialog();
                            if (e instanceof ApiException) {
                                switch (((ApiException) e).getCode()) {
                                    case 30506:
                                    case 30204:
                                        mView.showNoCommentDialog(msgId, true);
                                        break;
                                    default:
                                        mView.setNetError(pageNum == 1);
                                        break;
                                }
                            } else {
                                mView.setNetError(pageNum == 1);
                            }
                        }
                    }

                    @Override
                    public void onNext(InformationCommentDetailBean informationCommentDetailBean) {
                        if (null != mView) {
                            if (informationCommentDetailBean != null
                                    && informationCommentDetailBean.mReplyCommentList != null
                                    && informationCommentDetailBean.mReplyCommentList.size() > 0) {
                                mView.setLibraryMessageBoardList(informationCommentDetailBean.mReplyCommentList, informationCommentDetailBean.mTotalCount, pageNum == 1);
                                if (msgId > -1) {
                                    mView.setMessageHighLight(informationCommentDetailBean.mTargetIndex, informationCommentDetailBean.mCurrentPage);
                                }
                            } else {
                                mView.setLibraryMessageBoardListEmpty(pageNum == 1);
                            }
                            mView.dismissDelProgressDialog();
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void delMsg(final String libraryCode, final long msgId) {
        mView.showProgressDialog();
        Subscription subscription = LibraryRepository.getInstance().delMsg(msgId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.dismissDelProgressDialog();
                            if (e instanceof ApiException) {
                                if (((ApiException) e).getCode() == 30100) {
                                    mView.pleaseLoginTip();
                                } else {
                                    mView.showMessageTips(R.string.network_fault);
                                }
                            } else {
                                mView.showMessageTips(R.string.network_fault);
                            }
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (null != mView) {
                            if (aBoolean) {
                                mView.removeMsgBoard(msgId);
                                getLibraryMessageBoardList(libraryCode, 1, -1);
                            } else {
                                mView.dismissDelProgressDialog();
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void praise(long msgId, final int position) {
        Subscription subscription = LibraryRepository.getInstance().messagePraise(msgId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            if (e instanceof ApiException) {
                                switch (((ApiException) e).getCode()) {
                                    case 30100:
                                        mView.pleaseLoginTip();
                                        break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (null != mView) {
                            if (aBoolean) {
                                mView.praiseSuccess(position);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void replyMessage(String libraryCode, final long msgId, String content) {
        mView.showProgressDialog();
        Subscription subscription = LibraryRepository.getInstance().replyMessage(libraryCode, msgId, content)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CommentBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.dismissDelProgressDialog();
                            if (e instanceof ApiException) {
                                switch (((ApiException) e).getCode()) {
                                    case 30100:
                                        mView.pleaseLoginTip();
                                        break;
                                    case 30204:
                                        mView.showNoCommentDialog(msgId, false);
                                        break;
                                    default:
                                        mView.showMessageTips(R.string.publish_message_failure);
                                        break;
                                }
                            } else {
                                mView.showMessageTips(R.string.publish_message_failure);
                            }
                        }
                    }

                    @Override
                    public void onNext(CommentBean commentBean) {
                        if (mView != null) {
                            mView.dismissDelProgressDialog();
                            if (commentBean != null) {
                                mView.showMessageTips(R.string.publish_reply_success);
                                mView.replyMessageSuccess(commentBean, msgId);
                            } else {
                                mView.showMessageTips(R.string.publish_message_failure);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void refreshMessageBoardList(String libCode) {
        if (mView != null) {
            mView.showProgressDialog();
            getLibraryMessageBoardList(libCode, 1, -1);
        }
    }
}
