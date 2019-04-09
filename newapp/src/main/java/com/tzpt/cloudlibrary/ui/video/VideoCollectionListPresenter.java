package com.tzpt.cloudlibrary.ui.video;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.bean.VideoSetBean;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.VideoRepository;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;
import com.tzpt.cloudlibrary.rxbus.RxBus;
import com.tzpt.cloudlibrary.rxbus.event.VideoSetEvent;
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
 * 视频集列表
 * Created by tonyjia on 2018/6/21.
 */
public class VideoCollectionListPresenter extends RxPresenter<VideoCollectionListContract.View> implements
        VideoCollectionListContract.Presenter {

    private ArrayMap<String, Object> mParameterMap;
    private final RxBus mRxBus;

    public VideoCollectionListPresenter(RxBus rxBus) {
        this.mRxBus = rxBus;
    }

    //收藏视频集列表
    @Override
    public void getCollectVideoSetList(final int pageNo, final boolean isDelComplete) {
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (!TextUtils.isEmpty(readerId)) {
            if (null == mParameterMap) {
                mParameterMap = new ArrayMap<>();
            }
            mParameterMap.clear();
            mParameterMap.put("pageNo", pageNo);
            mParameterMap.put("pageCount", 20);
            Subscription subscription = VideoRepository.getInstance().getCollectVideoSetList(readerId, mParameterMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<VideoSetBean>>() {
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
                                            mView.setNetError(pageNo == 1);
                                            break;
                                    }
                                }
                            }
                        }

                        @Override
                        public void onNext(List<VideoSetBean> videoBeanList) {
                            if (null != mView) {
                                if (null != videoBeanList) {
                                    mView.setVideoList(videoBeanList, pageNo == 1);
                                    if (pageNo == 1) {
                                        if (isDelComplete) {//如果删除成功，则恢复为默认编辑模式，取消选择模式
                                            mRxBus.post(new VideoSetEvent(2, true));
                                        } else {
                                            setEditorAble(true);
                                        }
                                    }
                                } else {
                                    mView.setVideoEmptyList(pageNo == 1);
                                    if (pageNo == 1) {
                                        if (isDelComplete) {//如果删除成功，则恢复为默认编辑模式，取消选择模式
                                            mRxBus.post(new VideoSetEvent(2, false));
                                        } else {
                                            setEditorAble(false);
                                        }
                                    }
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }

    @Override
    public void cancelCollectionVideoList(List<Long> videoIdList) {
        if (null == videoIdList || videoIdList.size() == 0) {
            return;
        }
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (!TextUtils.isEmpty(readerId)) {
            JSONArray videoIds = new JSONArray();
            for (long videoId : videoIdList) {
                videoIds.put(videoId);
            }
            mView.showDelProgress();
            Subscription subscription = VideoRepository.getInstance().cancelCollectionVideo(videoIds, readerId)
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
                                    getCollectVideoSetList(1, true);
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
        mRxBus.post(new VideoSetEvent(1, hasAble));
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
