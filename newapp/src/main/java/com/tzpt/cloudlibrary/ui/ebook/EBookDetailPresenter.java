package com.tzpt.cloudlibrary.ui.ebook;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.business_bean.EBookBean;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.EBookRepository;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseDataResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseResultEntityVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BookBelongLibVo;
import com.tzpt.cloudlibrary.modle.remote.pojo.LibraryOpenTimeVo;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.ui.map.LocationManager;
import com.tzpt.cloudlibrary.utils.DateUtils;
import com.tzpt.cloudlibrary.utils.ImageUrlUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 电子书详情
 * Created by ZhiqiangJia on 2017-08-16.
 */
public class EBookDetailPresenter extends RxPresenter<EBookDetailContract.View> implements
        EBookDetailContract.Presenter {

    private boolean mIsCollectedEBook = false;
    private String mEBookId;

    @Override
    public boolean isLogin() {
        return UserRepository.getInstance().isLogin();
    }

    @Override
    public void getEBookDetail(final String id, int fromSearch, final String libCode) {
        if (null != mView) {
            mView.showProgressDialog();
        }
        this.mEBookId = id;
        Subscription subscription = DataRepository.getInstance().getEBookDetail(id, libCode, fromSearch)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResultEntityVo<com.tzpt.cloudlibrary.modle.remote.newpojo.EBookDetailInfoVo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.showNetError();
                        }
                    }

                    @Override
                    public void onNext(BaseResultEntityVo<com.tzpt.cloudlibrary.modle.remote.newpojo.EBookDetailInfoVo> eBookDetailInfoVoBaseResultEntityVo) {
                        if (mView != null) {
                            if (eBookDetailInfoVoBaseResultEntityVo.status == 200
                                    && eBookDetailInfoVoBaseResultEntityVo.data != null) {
                                EBookBean bean = new EBookBean();
                                bean.mEBook.mId = eBookDetailInfoVoBaseResultEntityVo.data.id;
                                bean.mEBook.mName = eBookDetailInfoVoBaseResultEntityVo.data.bookName;
                                bean.mEBook.mFileDownloadPath = eBookDetailInfoVoBaseResultEntityVo.data.file;
                                bean.mEBook.mPublishDate = eBookDetailInfoVoBaseResultEntityVo.data.publishDate;
                                bean.mEBook.mSummary = eBookDetailInfoVoBaseResultEntityVo.data.summary;
                                bean.mEBook.mCoverImg = ImageUrlUtils.getDownloadOriginalImagePath(eBookDetailInfoVoBaseResultEntityVo.data.image);
                                bean.mEBook.mIsbn = eBookDetailInfoVoBaseResultEntityVo.data.isbn;

                                bean.mAuthor.mName = eBookDetailInfoVoBaseResultEntityVo.data.author;
                                bean.mCategory.mName = eBookDetailInfoVoBaseResultEntityVo.data.categoryName;
                                bean.mPress.mName = eBookDetailInfoVoBaseResultEntityVo.data.publisher;
                                bean.mReadCount = eBookDetailInfoVoBaseResultEntityVo.data.number;
                                bean.mShareNum = eBookDetailInfoVoBaseResultEntityVo.data.shareNum;
                                bean.mCollectNum = eBookDetailInfoVoBaseResultEntityVo.data.collectNum;
                                bean.mShareUrl = eBookDetailInfoVoBaseResultEntityVo.data.htmlUrl;

                                mView.setEBookDetailInfo(bean);
                                if (TextUtils.isEmpty(libCode)) {
                                    //平台设置电子书阅读，分享，收藏信息，本馆不设置
                                    mView.setEBookDetailShareInfo(bean.mReadCount, bean.mCollectNum, bean.mShareNum);
                                    getEBookBelongLib(id);
                                } else {
                                    mView.dismissProgressDialog();
                                    mView.hideEBookBelongLib();
                                }
                                //获取电子书收藏状态
                                getEBookCollectionStatus(id);
                            } else {
                                mView.showNetError();
                            }
                        }
                    }

                });
        addSubscrebe(subscription);
    }


    private void getEBookBelongLib(String id) {
        String lngLat = LocationManager.getInstance().getLngLat();
        String readerId = UserRepository.getInstance().getLoginReaderId();

        Subscription subscription = DataRepository.getInstance().getEBookBelongLib(id, lngLat, readerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResultEntityVo<BookBelongLibVo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.showNetError();
                        }
                    }

                    @Override
                    public void onNext(BaseResultEntityVo<BookBelongLibVo> bookBelongLibVoBaseResultEntityVo) {
                        if (mView != null) {
                            if (bookBelongLibVoBaseResultEntityVo.status == 200) {
                                mView.dismissProgressDialog();
                                if (bookBelongLibVoBaseResultEntityVo.data != null) {
                                    mView.setEBookBelongLib(bookBelongLibVoBaseResultEntityVo.data.libCode, bookBelongLibVoBaseResultEntityVo.data.libName);
                                } else {
                                    mView.hideEBookBelongLib();
                                }
                            } else {
                                mView.showNetError();
                            }

                        }
                    }
                });
        addSubscrebe(subscription);
    }


    @Override
    public void reportEBookRead(String ebookId, String libCode) {
        Subscription subscription = DataRepository.getInstance().reportEBookRead(ebookId, libCode)
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
                    public void onNext(BaseResultEntityVo<BaseDataResultVo> baseDataResultVo) {

                    }
                });
        addSubscrebe(subscription);
    }

    //收藏取消电子书
    @Override
    public void collectionOrCancelEBook() {
        if (mIsCollectedEBook) {//已收藏电子书，点击则取消
            cancelCollectionEBook();
        } else {//未收藏，点击收藏
            collectionEBook();
        }
    }

    //收藏电子书
    @Override
    public void collectionEBook() {
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (!TextUtils.isEmpty(mEBookId) && !TextUtils.isEmpty(readerId)) {
            ArrayMap<String, Object> map = new ArrayMap<>();
            map.put("ebookId", mEBookId);
            map.put("readerId", readerId);
            mView.showCollectProgress(R.string.collecting);
            Subscription subscription = EBookRepository.getInstance().collectionEBook(map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (null != mView) {
                                mView.dismissCollectProgress();
                                if (e instanceof ApiException) {
                                    ApiException exception = (ApiException) e;
                                    switch (exception.getCode()) {
                                        case 30100:
                                            mView.showNoLoginDialog();
                                            break;
                                        default:
                                            mView.showErrorMsg(R.string.network_fault);
                                            break;
                                    }
                                }
                            }
                        }

                        @Override
                        public void onNext(Boolean aBoolean) {
                            if (null != mView) {
                                mView.dismissCollectProgress();
                                if (aBoolean) {
                                    mIsCollectedEBook = true;
                                    mView.collectEBookSuccess(true);
                                    //收藏成功 通知个人中心刷新用户信息
                                    AccountMessage message = new AccountMessage();
                                    message.mIsRefreshUserInfo = true;
                                    EventBus.getDefault().post(message);
                                } else {
                                    mView.showErrorMsg(R.string.collect_video_fail);
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }

    @Override
    public void reportEBookShare() {
        if (!TextUtils.isEmpty(mEBookId)) {
            Subscription subscription = EBookRepository.getInstance().reportBookShare(mEBookId)
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
    }

    @Override
    public void setCollectionStatus(boolean isCollection) {
        this.mIsCollectedEBook = isCollection;
    }


    //取消收藏电子书
    private void cancelCollectionEBook() {
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (!TextUtils.isEmpty(mEBookId) && !TextUtils.isEmpty(readerId)) {
            JSONArray ebookIds = new JSONArray();
            ebookIds.put(mEBookId);

            mView.showCollectProgress(R.string.canceling);
            Subscription subscription = EBookRepository.getInstance().cancelCollectionEBook(ebookIds, readerId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (null != mView) {
                                mView.dismissCollectProgress();
                                if (e instanceof ApiException) {
                                    ApiException exception = (ApiException) e;
                                    switch (exception.getCode()) {
                                        case 30100:
                                            mView.showNoLoginDialog();
                                            break;
                                        default:
                                            mView.showErrorMsg(R.string.network_fault);
                                            break;
                                    }
                                }
                            }
                        }

                        @Override
                        public void onNext(Boolean aBoolean) {
                            if (null != mView) {
                                mView.dismissCollectProgress();
                                if (aBoolean) {
                                    mIsCollectedEBook = false;
                                    mView.collectEBookSuccess(false);
                                    //取消收藏成功 通知个人中心刷新用户信息
                                    AccountMessage message = new AccountMessage();
                                    message.mIsRefreshUserInfo = true;
                                    EventBus.getDefault().post(message);
                                } else {
                                    mView.showErrorMsg(R.string.network_fault);
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }

    //获取电子书收藏状态
    private void getEBookCollectionStatus(String ebookId) {
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (!TextUtils.isEmpty(readerId)) {
            Subscription subscription = EBookRepository.getInstance().getEBookCollectStatus(ebookId, readerId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (null != mView) {
                                mView.dismissCollectProgress();
                                if (e instanceof ApiException) {
                                    ApiException exception = (ApiException) e;
                                    switch (exception.getCode()) {
                                        case 30100:
                                            mView.showNoLoginDialog();
                                            break;
                                        default:
                                            mView.showErrorMsg(R.string.network_fault);
                                            break;
                                    }
                                }
                            }
                        }

                        @Override
                        public void onNext(Boolean aBoolean) {
                            if (null != mView) {
                                mIsCollectedEBook = aBoolean;
                                mView.setEBookCollectionStatus(mIsCollectedEBook);
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }
}
