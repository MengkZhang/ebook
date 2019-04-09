package com.tzpt.cloudlibrary.ui.video;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.tzpt.cloudlibrary.CloudLibraryApplication;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.bean.VideoSetBean;
import com.tzpt.cloudlibrary.bean.VideoSetTotalBean;
import com.tzpt.cloudlibrary.modle.VideoRepository;
import com.tzpt.cloudlibrary.rxbus.RxBus;

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
public class VideoListPresenter extends RxPresenter<VideoListContract.View> implements
        VideoListContract.Presenter {

    private RxBus mRxBus;

    public VideoListPresenter(VideoListContract.View view) {
        attachView(view);
        mView.setPresenter(this);
        mRxBus = CloudLibraryApplication.mRxBus;
    }

    private Subscription mHotVideoSubscription;
    private Subscription mNewVideoSubscription;
    private ArrayMap<String, Object> mParameterMap;
    private String mKeyWord;
    private String mLibCode;
    private int mFilterType;
    private int mFirstCategory = 0;
    private int mSecondCategory = 0;

    @Override
    public void setLibCode(String libCode) {
        this.mLibCode = libCode;
    }

    @Override
    public String getLibCode() {
        return mLibCode;
    }

    @Override
    public void setFilterType(int filterType) {
        this.mFilterType = filterType;
    }

    @Override
    public void setCategoryId(int firstCategory, int secondCategory) {
        this.mFirstCategory = firstCategory;
        this.mSecondCategory = secondCategory;
    }

    @Override
    public void mustShowProgressLoading() {
        if (null != mView) {
            mView.mustShowProgressLoading();
        }
    }

    @Override
    public void setKeyWord(String keyWord) {
        this.mKeyWord = keyWord;
    }

    @Override
    public void getVideoList(int pageNo) {
        if (null == mParameterMap) {
            mParameterMap = new ArrayMap<>();
        }
        mParameterMap.clear();
        mParameterMap.put("pageNo", pageNo);
        mParameterMap.put("pageCount", 20);
        switch (mFilterType) {
            case VideoFilterType.WEEK_HOT_VIDEO_LIST:   //一周热门视频集列表
                if (mFirstCategory > 0) {
                    mParameterMap.put("firstCategory", mFirstCategory);
                }
                if (mSecondCategory > 0) {
                    mParameterMap.put("secondCategory", mSecondCategory);
                }
                if (!TextUtils.isEmpty(mLibCode)) {
                    mParameterMap.put("libCode", mLibCode);
                }
                mParameterMap.put("isNeedPage", 1);
                getHotVideoSetList(pageNo);
                break;
            case VideoFilterType.NEW_VIDEO_LIST:        //最新上架视频集列表
                if (mFirstCategory > 0) {
                    mParameterMap.put("firstCategory", mFirstCategory);
                }
                if (mSecondCategory > 0) {
                    mParameterMap.put("secondCategory", mSecondCategory);
                }
                if (!TextUtils.isEmpty(mLibCode)) {
                    mParameterMap.put("libCode", mLibCode);
                }
                mParameterMap.put("isNeedPage", 1);
                getNewVideoSetList(pageNo);
                break;
            case VideoFilterType.SEARCH_VIDEO_LIST:     //搜索视频集列表
                if (!TextUtils.isEmpty(mKeyWord)) {
                    mParameterMap.put("keywords", mKeyWord);
                    searchVideoSetList(pageNo);
                }
                break;
        }
    }

    @Override
    public void saveSearchBrowseRecord(long videosId) {
        ArrayMap<String, Object> map = new ArrayMap<>();
        map.put("type", 6);//1:图书 2:电子书 3:图书馆 4:活动 5:资讯 6:视频
        map.put("value", videosId);
        Subscription subscription = VideoRepository.getInstance().saveSearchBrowseRecord(map)
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

    /**
     * 获取热门搜索视频集列表
     *
     * @param pageNo 页码
     */
    private void getHotVideoSetList(final int pageNo) {
        if (null != mHotVideoSubscription && !mHotVideoSubscription.isUnsubscribed()) {
            mHotVideoSubscription.unsubscribe();
        }
        mHotVideoSubscription = VideoRepository.getInstance().getHotVideoSetList(mParameterMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<VideoSetTotalBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.setNetError(pageNo == 1);
                        }
                    }

                    @Override
                    public void onNext(VideoSetTotalBean videoSetTotalBean) {
                        if (null != mView) {
                            if (null != videoSetTotalBean && null != videoSetTotalBean.mVideoSetList) {
                                mView.setVideoList(videoSetTotalBean.mVideoSetList, videoSetTotalBean.mTotalCount, pageNo == 1);
                            } else {
                                mView.setVideoEmptyList(pageNo == 1);
                            }
                        }
                    }
                });
        addSubscrebe(mHotVideoSubscription);
    }

    /**
     * 最新上架视频集列表
     *
     * @param pageNo 页码
     */
    private void getNewVideoSetList(final int pageNo) {
        if (null != mNewVideoSubscription && !mNewVideoSubscription.isUnsubscribed()) {
            mNewVideoSubscription.unsubscribe();
        }
        mNewVideoSubscription = VideoRepository.getInstance().getNewVideoSetList(mParameterMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<VideoSetTotalBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.setNetError(pageNo == 1);
                        }
                    }

                    @Override
                    public void onNext(VideoSetTotalBean videoSetTotalBean) {
                        if (null != mView) {
                            if (null != videoSetTotalBean && null != videoSetTotalBean.mVideoSetList) {
                                mView.setVideoList(videoSetTotalBean.mVideoSetList, videoSetTotalBean.mTotalCount, pageNo == 1);
                            } else {
                                mView.setVideoEmptyList(pageNo == 1);
                            }
                        }
                    }
                });
        addSubscrebe(mNewVideoSubscription);
    }

    //搜索视频集列表
    private void searchVideoSetList(final int pageNo) {
        Subscription subscription = VideoRepository.getInstance().searchVideoSetList(mLibCode, mParameterMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<VideoSetBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.setNetError(pageNo == 1);
                        }
                    }

                    @Override
                    public void onNext(List<VideoSetBean> videoSetBeans) {
                        if (null != mView) {
                            if (null != videoSetBeans) {
                                mView.isSearchVideoSetList();
                                mView.setVideoList(videoSetBeans, 0, pageNo == 1);
                            } else {
                                mView.setVideoEmptyList(pageNo == 1);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

}
