package com.tzpt.cloudlibrary.ui.information;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.bean.CommentBean;
import com.tzpt.cloudlibrary.bean.InformationBean;
import com.tzpt.cloudlibrary.business_bean.BaseResultData;
import com.tzpt.cloudlibrary.business_bean.InformationCommentDetailBean;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.InformationRepository;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;
import com.tzpt.cloudlibrary.ui.map.LocationManager;

import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 资讯评论
 * Created by ZhiqiangJia on 2017-10-12.
 */

public class DisplayInformationDiscussPresenter extends RxPresenter<DisplayInformationDiscussContract.View> implements
        DisplayInformationDiscussContract.Presenter {

    @Override
    public void getInfoId(int position) {
        if (InformationRepository.getInstance().getInformationList() != null
                && InformationRepository.getInstance().getInformationList().size() > 0) {
            mView.setNewsIdCurrentCount(InformationRepository.getInstance().getInformationList().get(position).mId,
                    InformationRepository.getInstance().getInformationList().size());
        }
    }


    @Override
    public void getInformationDetail(long newsId, int fromSearch) {
        mView.showDetailLoadProgress();

        Subscription subscription = InformationRepository.getInstance().getNewsDetail(newsId, fromSearch)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<InformationBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.showDetailLoadError();
                        }
                    }

                    @Override
                    public void onNext(InformationBean informationBean) {
                        if (mView != null) {
                            mView.setInformationDetail(informationBean);
                            if (TextUtils.isEmpty(informationBean.mIsPraise) || informationBean.mIsPraise.equals("0")) {
                                mView.setPraiseBtnStatus(false);
                            } else {
                                mView.setPraiseBtnStatus(true);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void getDiscussList(final long commentId, final int pageNum, final long newsId) {
        Subscription subscription = InformationRepository.getInstance().getInformationDiscussList(commentId, pageNum, newsId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<InformationCommentDetailBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.setDiscussError();
                        }
                    }

                    @Override
                    public void onNext(InformationCommentDetailBean informationCommentDetailBean) {
                        if (mView != null) {
                            if (informationCommentDetailBean != null) {
                                mView.setDiscussList(informationCommentDetailBean.mReplyCommentList, informationCommentDetailBean.mTotalCount, pageNum == 1);
                                if (commentId > -1 && pageNum == 1) {
                                    mView.setDiscussLocationAndCurrentPage(informationCommentDetailBean.mTargetIndex, informationCommentDetailBean.mCurrentPage);
                                }
                            } else {
                                mView.setDiscussEmpty(pageNum == 1);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void getInfoList(String keyword, String libraryCode, String source, String title, String categoryId, String industryId, final int pageNum) {
        if (TextUtils.isEmpty(libraryCode)) {
            ArrayMap<String, String> mParameter = new ArrayMap<>();
            if (!TextUtils.isEmpty(keyword)) {
                mParameter.put("keyword", keyword);
            }
            if (!TextUtils.isEmpty(title)) {
                mParameter.put("title", title);
            }
            if (!TextUtils.isEmpty(source)) {
                mParameter.put("source", source);
            }
            if (!TextUtils.isEmpty(categoryId)) {
                mParameter.put("category", categoryId);
            }
            if (!TextUtils.isEmpty(industryId)) {
                mParameter.put("industry", industryId);
            }
            mParameter.put("pageNo", String.valueOf(pageNum));
            mParameter.put("pageCount", String.valueOf(20));

            mParameter.put("locationCode", LocationManager.getInstance().getLocationAdCode());

            Subscription subscription = InformationRepository.getInstance().getInformationList(mParameter)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseResultData<List<InformationBean>>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(BaseResultData<List<InformationBean>> listBaseResultData) {
                            if (mView != null) {
                                InformationRepository.getInstance().addInformationList(listBaseResultData.resultList, pageNum == 1);
                                mView.setLoadInfoListSuccess(listBaseResultData.resultList);
                            }
                        }
                    });
            addSubscrebe(subscription);
        } else {
            ArrayMap<String, String> mParameter = new ArrayMap<>();
            if (!TextUtils.isEmpty(keyword)) {
                mParameter.put("keyword", keyword);
            }
            if (!TextUtils.isEmpty(title)) {
                mParameter.put("title", title);
            }
            if (!TextUtils.isEmpty(source)) {
                mParameter.put("source", source);
            }
            if (!TextUtils.isEmpty(categoryId)) {
                mParameter.put("category", categoryId);
            }
            if (!TextUtils.isEmpty(industryId)) {
                mParameter.put("industry", industryId);
            }
            mParameter.put("pageNo", String.valueOf(pageNum));
            mParameter.put("pageCount", String.valueOf(20));


            Subscription subscription = InformationRepository.getInstance().getLibInformationList(libraryCode, mParameter)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseResultData<List<InformationBean>>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(BaseResultData<List<InformationBean>> listBaseResultData) {
                            if (mView != null) {
                                InformationRepository.getInstance().addInformationList(listBaseResultData.resultList, pageNum == 1);
                                mView.setLoadInfoListSuccess(listBaseResultData.resultList);
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }

    @Override
    public void operateNewsPraise(long newsId, final boolean isPraise) {
        mView.setPraiseBtnStatus(isPraise);
        mView.changePraiseCount(isPraise);
        Subscription subscription = InformationRepository.getInstance().operateNewsPraise(newsId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
        addSubscrebe(subscription);
    }

    @Override
    public void publishDiscuss(final long newsId, String content) {
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (TextUtils.isEmpty(readerId)) {
            return;
        }
        mView.showMsgProgressDialog("发送中...");
        mView.setSendDiscussBtnStatus(false);

        Subscription subscription = InformationRepository.getInstance().publishNewsDiscuss(content, Integer.valueOf(readerId), newsId)
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
                            mView.setSendDiscussBtnStatus(true);
                            if (e instanceof ApiException) {
                                if (((ApiException) e).getCode() == 30100) {
                                    mView.pleaseLoginTip();
                                } else {
                                    mView.showToastMsg(R.string.publish_discuss_failure);
                                }
                            } else {
                                mView.showToastMsg(R.string.publish_discuss_failure);
                            }
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (mView != null) {
                            mView.dismissMsgProgressDialog();
                            mView.setSendDiscussBtnStatus(true);
                            mView.publishDiscussSuccess();
                            getDiscussList(-1, 1, newsId);
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public boolean isLogin() {
        return UserRepository.getInstance().isLogin();
    }

    /**
     * 回复评论
     *
     * @param newsId    资讯ID
     * @param commentId 评论id
     * @param content   内容
     */
    @Override
    public void replyReaderMsg(final long newsId, final long commentId, String content) {
        if (commentId <= 0) {
            return;
        }
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (!TextUtils.isEmpty(readerId)) {
            mView.showMsgProgressDialog("发送中...");

            Subscription subscription = InformationRepository.getInstance().replyMsgForReturnComment(commentId, content, readerId, -1)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<CommentBean>() {
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
                                        case 30506:
                                            mView.showNoCommentDialog(false, commentId);
                                            break;
                                        default:
                                            mView.showToastMsg(R.string.publish_reply_failure);
                                            break;
                                    }
                                } else {
                                    mView.showToastMsg(R.string.publish_reply_failure);
                                }
                            }
                        }

                        @Override
                        public void onNext(CommentBean childBean) {
                            if (null != mView) {
                                mView.dismissMsgProgressDialog();
                                mView.publishReaderMsgSuccess();
                                mView.setCommentReplyInfo(childBean, commentId);
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }

    /**
     * 删除自己的评论
     *
     * @param commentId 评论id
     * @param position  位置
     */
    @Override
    public void delOwnReaderMsg(long commentId, final int position) {
        if (commentId <= 0) {
            return;
        }
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (!TextUtils.isEmpty(readerId)) {
            mView.showMsgProgressDialog("删除中...");

            Subscription subscription = InformationRepository.getInstance().delReaderMsg(commentId, readerId)
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
                                    if (((ApiException) e).getCode() == 30100) {
                                        mView.pleaseLoginTip();
                                    } else {
                                        mView.showDialogMsg(R.string.network_fault);
                                    }
                                } else {
                                    mView.showDialogMsg(R.string.network_fault);
                                }
                            }
                        }

                        @Override
                        public void onNext(Boolean aBoolean) {
                            if (null != mView) {
                                mView.dismissMsgProgressDialog();
                                mView.delReaderMsgSuccess(position);
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }

    /**
     * 点赞列表中的评论
     *
     * @param commentId 评论id
     * @param position  位置
     */
    @Override
    public void praiseReaderMsg(final long commentId, final int position, final int praisedCount) {
        if (commentId <= 0) {
            return;
        }
        mView.setCommentPraiseStatus(position, praisedCount);
        Subscription subscription = InformationRepository.getInstance().commentPraise(commentId)
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
                                if (((ApiException) e).getCode() == 30506) {
                                    mView.showNoCommentDialog(false, commentId);
                                }
                            }
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {

                    }
                });
        addSubscrebe(subscription);
    }

}
