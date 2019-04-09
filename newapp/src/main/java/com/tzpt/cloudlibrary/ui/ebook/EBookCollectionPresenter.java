package com.tzpt.cloudlibrary.ui.ebook;

import android.text.TextUtils;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.business_bean.EBookBean;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.EBookRepository;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;
import com.tzpt.cloudlibrary.rxbus.RxBus;
import com.tzpt.cloudlibrary.rxbus.event.EBookEvent;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;

import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 电子书收藏列表
 * Created by tonyjia on 2018/7/11.
 */
public class EBookCollectionPresenter extends RxPresenter<EBookCollectionContract.View> implements
        EBookCollectionContract.Presenter {

    private final RxBus mRxBus;

    public EBookCollectionPresenter(RxBus rxBus) {
        this.mRxBus = rxBus;
    }

    /**
     * 获取电子书收藏列表
     *
     * @param pageNo 页码
     */
    @Override
    public void getCollectionEBookList(final int pageNo, final boolean isDelComplete) {
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (!TextUtils.isEmpty(readerId)) {
            Subscription subscription = EBookRepository.getInstance().getCollectEBookList(readerId, pageNo, 20)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<EBookBean>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (null != mView) {
                                if (pageNo == 1) {
                                    setEditorAble(false);
                                }
                                if (e instanceof ApiException) {
                                    ApiException exception = (ApiException) e;
                                    switch (exception.getCode()) {
                                        case 30100:
                                            AccountMessage accountMessage = new AccountMessage();
                                            accountMessage.mIsLoginOut = true;
                                            accountMessage.mIsToUserCenter = true;
                                            EventBus.getDefault().post(accountMessage);
                                            break;
                                        default:
                                            mView.setEBookListError(pageNo == 1);
                                            break;
                                    }
                                }
                            }
                        }

                        @Override
                        public void onNext(List<EBookBean> eBookInfoBeans) {
                            if (null != eBookInfoBeans) {
                                mView.setEBookList(eBookInfoBeans, pageNo == 1);
                                if (pageNo == 1) {
                                    if (isDelComplete) {//如果删除成功，则恢复为默认编辑模式，取消选择模式
                                        mRxBus.post(new EBookEvent(2, true));
                                    } else {
                                        setEditorAble(true);
                                    }
                                }
                            } else {
                                mView.setEBookListEmpty(pageNo == 1);
                                if (pageNo == 1) {
                                    if (isDelComplete) {//如果删除成功，则恢复为默认编辑模式，取消选择模式
                                        mRxBus.post(new EBookEvent(2, false));
                                    } else {
                                        setEditorAble(false);
                                    }
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }

    //取消收藏电子书列表
    @Override
    public void cancelCollectionEBookList(List<String> collectionEBookIdList) {
        if (null == collectionEBookIdList || collectionEBookIdList.size() == 0) {
            return;
        }
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (!TextUtils.isEmpty(readerId)) {
            JSONArray ebookIds = new JSONArray();
            for (String eBookId : collectionEBookIdList) {
                ebookIds.put(eBookId);
            }
            mView.showDelProgress();
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
                                mView.showErrorMsg(R.string.network_fault);
                                mView.dismissDelProgress();
                            }
                        }

                        @Override
                        public void onNext(Boolean aBoolean) {
                            if (null != mView) {
                                mView.dismissDelProgress();
                                if (aBoolean) {//如果删除成功，则重新获取电子书收藏列表
                                    getCollectionEBookList(1, true);
                                } else {
                                    mView.showErrorMsg(R.string.delete_fail);
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }

    //是否有编辑功能
    @Override
    public void setEditorAble(boolean hasAble) {
        mRxBus.post(new EBookEvent(1, hasAble));
    }


    @Override
    public <T> void registerRxBus(Class<T> eventType, Action1<T> action) {
        Subscription subscription = mRxBus.doSubscribe(eventType, action, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {

            }
        });
        mRxBus.addSubscription(this, subscription);
    }

    @Override
    public void unregisterRxBus() {
        mRxBus.unSubscribe(this);
    }
}
