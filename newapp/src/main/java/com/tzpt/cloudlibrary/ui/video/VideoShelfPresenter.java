package com.tzpt.cloudlibrary.ui.video;

import com.tzpt.cloudlibrary.CloudLibraryApplication;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.bean.VideoBean;
import com.tzpt.cloudlibrary.bean.VideoSetBean;
import com.tzpt.cloudlibrary.modle.VideoRepository;
import com.tzpt.cloudlibrary.modle.remote.newdownload.DownloadStatus;
import com.tzpt.cloudlibrary.rxbus.RxBus;
import com.tzpt.cloudlibrary.utils.StringUtils;
import com.tzpt.cloudlibrary.utils.Utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/7/11.
 */

public class VideoShelfPresenter extends RxPresenter<VideoShelfContract.View> implements VideoShelfContract.Presenter {
    private RxBus mRxBus;

    VideoShelfPresenter() {
        mRxBus = CloudLibraryApplication.mRxBus;
    }

    @Override
    public void delVideoSetAndAllDownloadingVideo(List<Long> ids) {
        Subscription subscription = Observable.concat(VideoRepository.getInstance().delAllDownloadingVideo(),
                VideoRepository.getInstance().delVideoSet(ids))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Void>() {
                    @Override
                    public void onCompleted() {
                        //合并处理了两个事件，只刷新一次UI
                        if (mView != null) {
                            mView.setUnableEdit();
                            getLocalVideoSet();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Void aVoid) {

                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void delVideoSet(List<Long> ids) {
        Subscription subscription = VideoRepository.getInstance().delVideoSet(ids)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Void>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Void aVoid) {
                        if (mView != null) {
                            mView.setUnableEdit();
                            getLocalVideoSet();
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void delAllDownloadingVideo() {
        Subscription subscription = VideoRepository.getInstance().delAllDownloadingVideo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Void>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Void aVoid) {
                        if (mView != null) {
                            mView.setUnableEdit();
                            getLocalVideoSet();
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    public void getLocalVideoSet() {
        Subscription subscription = VideoRepository.getInstance().getLocalVideoSet()
                .flatMap(new Func1<List<VideoSetBean>, Observable<VideoSetBean>>() {
                    @Override
                    public Observable<VideoSetBean> call(List<VideoSetBean> videoSetBeans) {
                        return Observable.from(videoSetBeans);
                    }
                })
                .filter(new Func1<VideoSetBean, Boolean>() {
                    @Override
                    public Boolean call(VideoSetBean videoSetBean) {
                        return videoSetBean.getCount() > 0;
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<VideoSetBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<VideoSetBean> videoSetBeans) {
                        if (mView != null) {
                            List<VideoBean> downloadingVideoList = VideoRepository.getInstance().getAllDownloadingVideoList();
                            VideoBean downloadingVideo = null;
                            if (downloadingVideoList.size() > 0) {
                                Collections.sort(downloadingVideoList, new Comparator<VideoBean>() {
                                    @Override
                                    public int compare(VideoBean o1, VideoBean o2) {
                                        return (int) (o2.getAddTime() - o1.getAddTime());
                                    }
                                });
                                for (VideoBean item : downloadingVideoList) {
                                    if (item.getStatus() == DownloadStatus.DOWNLOADING) {
                                        downloadingVideo = item;
                                        break;
                                    }
                                }
                                if (downloadingVideo == null) {
                                    downloadingVideo = downloadingVideoList.get(0);
                                }
                            }

                            if (videoSetBeans != null
                                    && videoSetBeans.size() > 0
                                    && downloadingVideo != null) {
                                mView.showAllData(videoSetBeans,
                                        VideoRepository.getInstance().getDownloadingCount(),
                                        downloadingVideo);
                            } else if (videoSetBeans != null
                                    && videoSetBeans.size() > 0) {
                                mView.showVideoSetView(videoSetBeans);
                            } else if (downloadingVideo != null) {
                                mView.showDownloading(VideoRepository.getInstance().getDownloadingCount(),
                                        downloadingVideo);
                            } else {
                                mView.showEmptyView();
                            }

                            getStorageInfo();
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void getStorageInfo() {
        Subscription subscription = VideoRepository.getInstance().getLoadSize()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        if (mView != null) {
                            mView.setStorageInfo(StringUtils.convertStorageNoB(aLong), StringUtils.convertStorageNoB(Utils.getFreeSpaceBytes()));
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void clearMemoryCache() {
        VideoRepository.getInstance().clearMemoryData();
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
