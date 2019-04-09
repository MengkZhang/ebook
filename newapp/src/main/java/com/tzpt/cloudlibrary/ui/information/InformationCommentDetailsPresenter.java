package com.tzpt.cloudlibrary.ui.information;

import android.text.TextUtils;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.business_bean.InformationCommentDetailBean;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.InformationRepository;
import com.tzpt.cloudlibrary.modle.LibraryRepository;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 评论和留言详情
 * Created by ZhiqiangJia on 2018-01-23.
 */
public class InformationCommentDetailsPresenter extends RxPresenter<InformationCommentDetailsContract.View> implements
        InformationCommentDetailsContract.Presenter {

    @Override
    public void getCommentDetailList(boolean isLibMsgBoard, long commentId, final long replyId, final int pageNo) {

        Observable<InformationCommentDetailBean> commentObservable;
        if (isLibMsgBoard) {
            //留言板回复列表
            commentObservable = LibraryRepository.getInstance().getMessageBoardCommentList(commentId, replyId, pageNo);
        } else {
            //资讯回复列表
            commentObservable = InformationRepository.getInstance().getCommentList(commentId, replyId, pageNo);
        }
        Subscription subscription = commentObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<InformationCommentDetailBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            if (e instanceof ApiException) {
                                switch (((ApiException) e).getCode()) {
                                    case 30506:
                                    case 30204:
                                        mView.showNoCommentDialog(-1, true);
                                        break;
                                    case 30100:
                                        mView.pleaseLoginTip();
                                        break;
                                    default:
                                        mView.setNetError(pageNo == 1);
                                        break;
                                }
                            } else {
                                mView.setNetError(pageNo == 1);
                            }

                        }
                    }

                    @Override
                    public void onNext(InformationCommentDetailBean informationCommentDetailBean) {
                        if (mView != null) {
                            if (informationCommentDetailBean != null) {

                                mView.setDiscussCommentList(informationCommentDetailBean.mReplyCommentList, informationCommentDetailBean.mTotalCount, pageNo == 1);
                                if (replyId > 0 && pageNo == 1) {
                                    mView.setDiscussLocationAndCurrentPage(informationCommentDetailBean.mTargetIndex, informationCommentDetailBean.mCurrentPage);
                                }
                                if (pageNo == 1) {
                                    mView.showCommentView();
                                }
                            } else {
                                mView.setDiscussCommentListEmpty(pageNo == 1);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void delOwnMsg(boolean isLibMsgBoard, long commentId) {
        if (commentId <= 0) {
            return;
        }
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (!TextUtils.isEmpty(readerId)) {
            Observable<Boolean> delOwnMsgObservable;
            if (isLibMsgBoard) {
                delOwnMsgObservable = LibraryRepository.getInstance().delOwnMsg(commentId, readerId);
            } else {
                delOwnMsgObservable = InformationRepository.getInstance().delReaderMsg(commentId, readerId);
            }
            mView.showMsgProgressDialog("删除中...");
            Subscription subscription = delOwnMsgObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (null != mView) {
                                mView.dismissMsgProgressDialog();
                                if (e instanceof ApiException) {
                                    switch (((ApiException) e).getCode()) {
                                        case 30100:
                                            mView.pleaseLoginTip();
                                            break;
                                        case 30204:
                                        case 30506:
                                            mView.showNoCommentDialog(-1, true);
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
                                mView.dismissMsgProgressDialog();
                                mView.delReaderMsgSuccess(true, -1);
                            }

                        }
                    });
            addSubscrebe(subscription);
        }
    }

    @Override
    public void delReplyOwnMsg(boolean isLibMsgBoard, final long replyId) {
        if (replyId <= 0) {
            return;
        }
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (!TextUtils.isEmpty(readerId)) {

            Observable<Boolean> delOwnReplyMsgObservable;
            if (isLibMsgBoard) {
                delOwnReplyMsgObservable = LibraryRepository.getInstance().delOwnReplyMsg(replyId, readerId);
            } else {
                delOwnReplyMsgObservable = InformationRepository.getInstance().delReplyReaderMsg(replyId, readerId);
            }
            mView.showMsgProgressDialog("删除中...");
            Subscription subscription = delOwnReplyMsgObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (mView != null) {
                                mView.dismissMsgProgressDialog();
                                if (e instanceof ApiException) {
                                    switch (((ApiException) e).getCode()) {
                                        case 30100:
                                            mView.pleaseLoginTip();
                                            break;
                                        case 30204:
                                        case 30506:
                                            mView.showNoCommentDialog(-1, true);
                                            break;
                                        case 30507:
                                        case 30206:
                                            mView.showNoCommentDialog(replyId, false);
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
                            if (null != mView) {
                                mView.dismissMsgProgressDialog();
                                mView.delReaderMsgSuccess(false, replyId);
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }

    @Override
    public void replyComment(final boolean isLibMsgBoard, String libCode, final long commentId, String content) {
        if (TextUtils.isEmpty(content)) {
            mView.showToastMsg(R.string.content_not_empty);
            return;
        }
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (!TextUtils.isEmpty(readerId)) {
            Observable<Void> replyObservable;
            if (isLibMsgBoard) {
                replyObservable = LibraryRepository.getInstance().replyMessageBoard(readerId, commentId, -1, libCode, content);
            } else {
                replyObservable = InformationRepository.getInstance().replyMsg(commentId, content, readerId, -1);
            }
            mView.showMsgProgressDialog("发送中...");
            Subscription subscription = replyObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Void>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (mView != null) {
                                mView.dismissMsgProgressDialog();
                                if (e instanceof ApiException) {
                                    switch (((ApiException) e).getCode()) {
                                        case 30100:
                                            mView.pleaseLoginTip();
                                            break;
                                        case 30506:
                                        case 30204:
                                            mView.showNoCommentDialog(-1, true);
                                            break;
                                        case 1002:
                                            mView.showToastMsg(R.string.network_fault);
                                            break;
                                        default:
                                            mView.showToastMsg(isLibMsgBoard ? R.string.publish_message_failure : R.string.publish_discuss_failure);
                                            break;
                                    }
                                } else {
                                    mView.showToastMsg(R.string.network_fault);
                                }
                            }
                        }

                        @Override
                        public void onNext(Void v) {
                            if (null != mView) {
                                mView.dismissMsgProgressDialog();
                                mView.publishReaderMsgSuccess(R.string.publish_reply_success);
                                getCommentDetailList(isLibMsgBoard, commentId, -1, 1);
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }

    @Override
    public void replyRepliedComment(final boolean isLibMsgBoard, String libCode, final long commentId, final long replyId, String content) {
        if (TextUtils.isEmpty(content)) {
            mView.showToastMsg(R.string.content_not_empty);
            return;
        }
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (!TextUtils.isEmpty(readerId)) {
            Observable<Void> commentObservable;
            if (isLibMsgBoard) {
                commentObservable = LibraryRepository.getInstance().replyMessageBoard(readerId, -1, replyId, libCode, content);
            } else {
                commentObservable = InformationRepository.getInstance().replyMsg(-1, content, readerId, replyId);
            }

            mView.showMsgProgressDialog("发送中...");
            Subscription subscription = commentObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Void>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (mView != null) {
                                mView.dismissMsgProgressDialog();
                                if (e != null && e instanceof ApiException) {
                                    switch (((ApiException) e).getCode()) {
                                        case 30100:
                                            mView.pleaseLoginTip();
                                            break;
                                        case 30506:
                                        case 30204:
                                            mView.showNoCommentDialog(-1, true);
                                            break;
                                        case 30507:
                                        case 30206:
                                            mView.showNoCommentDialog(replyId, false);
                                            break;
                                        case 1002:
                                            mView.showToastMsg(R.string.network_fault);
                                            break;
                                        default:
                                            mView.showToastMsg(R.string.publish_message_failure);
                                            break;
                                    }
                                } else {
                                    mView.showToastMsg(R.string.network_fault);
                                }
                            }
                        }

                        @Override
                        public void onNext(Void v) {
                            if (null != mView) {
                                mView.dismissMsgProgressDialog();
                                mView.publishReaderMsgSuccess(R.string.publish_reply_success);
                                getCommentDetailList(isLibMsgBoard, commentId, -1, 1);
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }

    @Override
    public void praiseReaderMsg(boolean isLibMsgBoard, long commentId, final int praisedCount) {
        if (commentId <= 0) {
            return;
        }
        mView.setCommentPraiseStatus(commentId, praisedCount);
        Observable<Boolean> praiseObservable;
        if (isLibMsgBoard) {
            //留言点赞
            praiseObservable = LibraryRepository.getInstance().messagePraise(commentId);
        } else {
            //评论点赞
            praiseObservable = InformationRepository.getInstance().commentPraise(commentId);
        }
        Subscription subscription = praiseObservable
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

                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void replyReaderPraise(boolean isLibMsgBoard, final long replyId, final int praisedCount) {
        mView.setCommentPraiseStatus(replyId, praisedCount);
        Observable<Boolean> replyPraiseObservable;
        if (isLibMsgBoard) {
            //留言回复点赞
            String readerId = UserRepository.getInstance().getLoginReaderId();
            replyPraiseObservable = LibraryRepository.getInstance().replyMsgPraise(replyId, readerId == null ? 0 : Long.parseLong(readerId));
        } else {
            //资讯评论回复点赞
            replyPraiseObservable = InformationRepository.getInstance().replyPraise(replyId);
        }
        Subscription subscription = replyPraiseObservable
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

                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public boolean isLogin() {
        return UserRepository.getInstance().isLogin();
    }

    @Override
    public long getNewsId() {
        return InformationRepository.getInstance().getNewsId();
    }
}
