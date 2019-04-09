package com.tzpt.cloudlibrary.ui.account.selfhelp;

import android.text.TextUtils;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.business_bean.BoughtBookBean;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseResultEntityVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.SelfBuyBookShelfVo;
import com.tzpt.cloudlibrary.utils.ImageUrlUtils;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 购书架
 * Created by tonyjia on 2018/8/16.
 */
public class SelfBuyBookShelfPresenter extends RxPresenter<SelfBuyBookShelfContract.View> implements
        SelfBuyBookShelfContract.Presenter {

    /**
     * 获取购书架列表
     *
     * @param pageNo 页码
     */
    @Override
    public void getSelfBuyBookShelfList(final int pageNo) {
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (!TextUtils.isEmpty(readerId)) {
            Subscription subscription = DataRepository.getInstance().getSelfBuyBookShelfList(readerId, pageNo, 20)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseResultEntityVo<SelfBuyBookShelfVo>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (null != mView) {
                                if (e instanceof ApiException) {
                                    if (((ApiException) e).getCode() == 30100) {
                                        mView.pleaseLoginTip();
                                    } else {
                                        mView.setNetError(pageNo == 1);
                                    }
                                } else {
                                    mView.setNetError(pageNo == 1);
                                }


                            }
                        }

                        @Override
                        public void onNext(BaseResultEntityVo<SelfBuyBookShelfVo> selfBuyBookShelfVo) {
                            if (null != mView) {
                                if (selfBuyBookShelfVo.data != null) {
                                    if (null != selfBuyBookShelfVo.data.resultList && selfBuyBookShelfVo.data.resultList.size() > 0) {
                                        List<BoughtBookBean> borrowBookBeanList = new ArrayList<>();
                                        for (SelfBuyBookShelfVo.ShelfBookVo item : selfBuyBookShelfVo.data.resultList) {
                                            BoughtBookBean bean = new BoughtBookBean();
                                            bean.mBook.mCoverImg = ImageUrlUtils.getDownloadOriginalImagePath(item.image);
                                            bean.mBoughtId = item.id;
                                            bean.mAuthor.mName = item.author;
                                            bean.mBook.mName = item.properTitle;
                                            bean.mBook.mIsbn = item.isbn;
                                            bean.mPress.mName = item.press;
                                            bean.mLibrary.mName = item.levelName;
                                            bean.mCategory.mName = item.categoryName;

                                            if (null != item.publishDate && item.publishDate.length() >= 4) {
                                                bean.mBook.mPublishDate = item.publishDate.substring(0, 4);
                                            } else {
                                                bean.mBook.mPublishDate = "暂无数据";
                                            }
                                            //是否点赞0：否，1：是
                                            bean.mIsPraised = item.isPraise != 0;
                                            bean.mBoughtTime = item.buyTime;
                                            bean.mBoughtPrice = item.buyPrice;
                                            bean.mBook.mFixedPrice = item.fixedPrice;
                                            bean.mBook.mBookId = item.libBookId;
                                            borrowBookBeanList.add(bean);
                                        }
                                        mView.setShelfBookList(borrowBookBeanList, pageNo == 1);
                                    } else {
                                        mView.setShelfBookEmpty(pageNo == 1);
                                    }

                                    if (selfBuyBookShelfVo.data.totalCount > 0) {
                                        mView.setShelfBookTotalInfo(selfBuyBookShelfVo.data.totalCount,
                                                selfBuyBookShelfVo.data.totalBuyPrice, selfBuyBookShelfVo.data.totalFixedPrice);
                                    } else {
                                        mView.hideBookTotalInfo();
                                    }
                                } else {
                                    mView.setNetError(pageNo == 1);
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }

    /**
     * 点赞接口
     *
     * @param buyBookId 购买图书ID
     * @param isPraised 是否已点赞
     * @param position  下标
     */
    @Override
    public void praiseSelfBuyBook(long buyBookId, final boolean isPraised, final int position) {
        Subscription subscription = DataRepository.getInstance().praiseSelfBuyBook(buyBookId, isPraised ? 0 : 1)
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
                                if (((ApiException) e).getCode() == 30100) {
                                    mView.pleaseLoginTip();
                                } else {
                                    mView.showMsgToast(R.string.network_fault);
                                }
                            } else {
                                mView.showMsgToast(R.string.network_fault);
                            }
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (mView != null) {
                            if (aBoolean) {
                                mView.praiseBuyBookSuccess(!isPraised, position, R.string.success);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }
}
