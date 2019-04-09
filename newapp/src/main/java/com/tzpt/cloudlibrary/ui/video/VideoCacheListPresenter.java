package com.tzpt.cloudlibrary.ui.video;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.bean.VideoBean;
import com.tzpt.cloudlibrary.modle.VideoRepository;
import com.tzpt.cloudlibrary.modle.remote.newdownload.DownloadStatus;
import com.tzpt.cloudlibrary.receiver.NetStatusReceiver;
import com.tzpt.cloudlibrary.rxbus.RxBus;
import com.tzpt.cloudlibrary.utils.StringUtils;
import com.tzpt.cloudlibrary.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/7/2.
 */

public class VideoCacheListPresenter extends RxPresenter<VideoCacheListContract.View>
        implements VideoCacheListContract.Presenter {

    private final RxBus mRxBus;

    private boolean mIsStartAll;

    private VideoBean mCurrentDownloadVideo;

    private int mType;

    VideoCacheListPresenter(RxBus rxBus) {
        mRxBus = rxBus;
    }

    @Override
    public void getVideoCacheList() {
        Subscription subscription = Observable.just(VideoRepository.getInstance().getAllDownloadingVideoList())
                .flatMap(new Func1<List<VideoBean>, Observable<VideoBean>>() {
                    @Override
                    public Observable<VideoBean> call(List<VideoBean> videoBeans) {
                        return Observable.from(videoBeans);
                    }
                })
                .toSortedList(new Func2<VideoBean, VideoBean, Integer>() {
                    @Override
                    public Integer call(VideoBean videoBean, VideoBean videoBean2) {
                        return (int) (videoBean2.getAddTime() - videoBean.getAddTime());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<VideoBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<VideoBean> videoBeans) {
                        if (mView != null) {
                            if (videoBeans != null && videoBeans.size() > 0) {
                                mView.setAllDownloadBtnStatus(hasDownloadingItem(videoBeans));
                                mView.setData(videoBeans);
                            } else {
                                mView.showEmptyList();
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private boolean hasDownloadingItem(List<VideoBean> videoBeans) {
        for (VideoBean item : videoBeans) {
            if (item.getStatus() == DownloadStatus.WAIT
                    || item.getStatus() == DownloadStatus.CONNECTING
                    || item.getStatus() == DownloadStatus.START
                    || item.getStatus() == DownloadStatus.DOWNLOADING
                    || item.getStatus() == DownloadStatus.ERROR
                    || item.getStatus() == DownloadStatus.MOBILE_NET_ERROR
                    || item.getStatus() == DownloadStatus.NO_NET_ERROR) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void checkVideoCacheStatus() {
        mView.setAllDownloadBtnStatus(hasDownloadingItem(VideoRepository.getInstance().getAllDownloadingVideoList()));
    }

    @Override
    public void getVideoCompleteList(long setId) {
        Subscription subscription = VideoRepository.getInstance().getDownloadVideoList(setId)
                .flatMap(new Func1<List<VideoBean>, Observable<VideoBean>>() {
                    @Override
                    public Observable<VideoBean> call(List<VideoBean> videoBeans) {
                        return Observable.from(videoBeans);
                    }
                }).toSortedList(new Func2<VideoBean, VideoBean, Integer>() {
                    @Override
                    public Integer call(VideoBean videoBean, VideoBean videoBean2) {
                        return (int) (videoBean2.getCompleteTime() - videoBean.getCompleteTime());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<VideoBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<VideoBean> videoBeans) {
                        if (mView != null) {
                            if (videoBeans != null && videoBeans.size() > 0) {
                                mView.setData(videoBeans);
                            } else {
                                mView.showEmptyList();
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void dealDownloadItem(VideoBean item) {
        mCurrentDownloadVideo = item;
        if (item.getStatus() == DownloadStatus.WAIT
                || item.getStatus() == DownloadStatus.CONNECTING
                || item.getStatus() == DownloadStatus.START
                || item.getStatus() == DownloadStatus.DOWNLOADING) {
            VideoRepository.getInstance().stopDownload(item);
        } else if (item.getStatus() == DownloadStatus.STOP
                || item.getStatus() == DownloadStatus.ERROR
                || item.getStatus() == DownloadStatus.MOBILE_NET_ERROR
                || item.getStatus() == DownloadStatus.NO_NET_ERROR) {
            if (NetStatusReceiver.mIsWifiNetConnected) {
                checkStoragePermissionForDownload(1);
            } else if (NetStatusReceiver.mIsMobileNetConnected) {
                boolean isMobileNetChecked = VideoRepository.getInstance().checkMobileNetAble();
                if (isMobileNetChecked) {
                    if (VideoRepository.getInstance().checkFirstMobileNetTip()) {
                        mIsStartAll = false;
                        mView.showMobileTipDialog(R.string.mobile_net_tip2, R.string.download_resume, R.string.change_setting);
                    } else {
                        checkStoragePermissionForDownload(1);
                    }
                } else {
                    mView.showToastTip(R.string.current_setting_only_wifi);
                }
            } else {
                mView.showToastTip(R.string.no_net_try_later);
            }
        }
    }

    private void checkStoragePermissionForDownload(int type) {
        if (mView != null) {
            mType = type;
            mView.checkStoragePermission();
        }
    }

    private void stopDownloadAll() {
        Subscription subscription = Observable.just(VideoRepository.getInstance().getAllDownloadingVideoList())
                .map(new Func1<List<VideoBean>, List<VideoBean>>() {
                    @Override
                    public List<VideoBean> call(List<VideoBean> videoBeans) {
                        List<String> urls = new ArrayList<>();
                        for (VideoBean item : videoBeans) {
                            if (item.getStatus() == DownloadStatus.WAIT
                                    || item.getStatus() == DownloadStatus.CONNECTING
                                    || item.getStatus() == DownloadStatus.START
                                    || item.getStatus() == DownloadStatus.DOWNLOADING) {
                                item.setStatus(DownloadStatus.STOP);
                                urls.add(item.getUrl());
                            } else if (item.getStatus() == DownloadStatus.ERROR
                                    || item.getStatus() == DownloadStatus.MOBILE_NET_ERROR
                                    || item.getStatus() == DownloadStatus.NO_NET_ERROR) {
                                item.setStatus(DownloadStatus.STOP);
                                mRxBus.post(item);
                            }
                        }
                        if (urls.size() > 0) {
                            VideoRepository.getInstance().stopDownload(urls);
                        }
                        return videoBeans;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<VideoBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<VideoBean> videoBeans) {
                        if (mView != null) {
                            if (videoBeans != null && videoBeans.size() > 0) {
                                mView.setAllDownloadBtnStatus(hasDownloadingItem(videoBeans));
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void dealAllDownloadItem() {
        if (hasDownloadingItem(VideoRepository.getInstance().getAllDownloadingVideoList())) {
            stopDownloadAll();
        } else {
            if (NetStatusReceiver.mIsWifiNetConnected) {
                checkStoragePermissionForDownload(0);
            } else if (NetStatusReceiver.mIsMobileNetConnected) {
                boolean isMobileNetChecked = VideoRepository.getInstance().checkMobileNetAble();
                if (isMobileNetChecked) {
                    if (VideoRepository.getInstance().checkFirstMobileNetTip()) {
                        mIsStartAll = true;
                        mView.showMobileTipDialog(R.string.mobile_net_tip2, R.string.download_resume, R.string.change_setting);
                    } else {
                        checkStoragePermissionForDownload(0);
                    }
                } else {
                    mView.showToastTip(R.string.current_setting_only_wifi);
                }
            } else {
                mView.showToastTip(R.string.no_net_try_later);
            }
        }
    }

    @Override
    public void delDownloadingVideo(List<VideoBean> videoBeans) {
        Subscription subscription = VideoRepository.getInstance().cancelDownload(videoBeans)
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
                            if (VideoRepository.getInstance().getDownloadingCount() == 0) {
                                mView.showEmptyList();
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void delCompleteVideo(final long setId, List<VideoBean> videoBeans) {
        Subscription subscription = VideoRepository.getInstance().delCompleteVideo(setId, videoBeans)
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
                            getVideoCompleteList(setId);
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
    public void resumeDownload() {
        VideoRepository.getInstance().setFirstMobileNetTip();
        if (mIsStartAll) {
            checkStoragePermissionForDownload(0);
        } else {
            //移动网络点击单个视频，允许下载以后，其他所有移动网络下暂停的视频都可以进行下载。
            VideoRepository.getInstance().dealDownloadWifiToMobile();
        }
    }

    @Override
    public void setPermissionOk() {
        if (mType == 0) {
            Subscription subscription = Observable.just(VideoRepository.getInstance().getAllDownloadingVideoList())
                    .map(new Func1<List<VideoBean>, List<VideoBean>>() {
                        @Override
                        public List<VideoBean> call(List<VideoBean> videoBeans) {
                            for (VideoBean item : videoBeans) {
                                if (item.getStatus() == DownloadStatus.STOP
                                        || item.getStatus() == DownloadStatus.ERROR
                                        || item.getStatus() == DownloadStatus.MOBILE_NET_ERROR
                                        || item.getStatus() == DownloadStatus.NO_NET_ERROR) {
                                    VideoRepository.getInstance().startDownload(item);
                                }
                            }
                            return videoBeans;
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<VideoBean>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(List<VideoBean> videoBeans) {
                            if (mView != null) {
                                if (videoBeans != null && videoBeans.size() > 0) {
                                    mView.setAllDownloadBtnStatus(hasDownloadingItem(videoBeans));
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        } else {
            VideoRepository.getInstance().startDownload(mCurrentDownloadVideo);
        }
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
