package com.tzpt.cloudlibrary.ui.video;

import com.tzpt.cloudlibrary.CloudLibraryApplication;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.bean.VideoBean;
import com.tzpt.cloudlibrary.bean.VideoTOCTree;
import com.tzpt.cloudlibrary.modle.VideoRepository;
import com.tzpt.cloudlibrary.modle.remote.newdownload.DownloadStatus;
import com.tzpt.cloudlibrary.receiver.NetStatusReceiver;
import com.tzpt.cloudlibrary.rxbus.RxBus;
import com.tzpt.cloudlibrary.utils.StringUtils;
import com.tzpt.cloudlibrary.utils.Utils;

import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/7/9.
 */

public class VideoDownloadChoosePresenter extends RxPresenter<VideoDownloadChooseContract.View>
        implements VideoDownloadChooseContract.Presenter {

    private final RxBus mRxBus;

    VideoDownloadChoosePresenter() {
        mRxBus = CloudLibraryApplication.mRxBus;
    }

    @Override
    public void getVideoList(long setId) {
        Subscription subscription = VideoRepository.getInstance().getVideoInfo(setId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<VideoTOCTree>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.setNetError();
                        }
                    }

                    @Override
                    public void onNext(List<VideoTOCTree> videoTOCTrees) {
                        if (mView != null) {
                            if (videoTOCTrees != null && videoTOCTrees.size() > 0) {
                                mView.setData(videoTOCTrees);
                            } else {
                                mView.setNetError();
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void startDownload(List<Long> videoIds) {
        if (videoIds == null || videoIds.size() == 0) {
            return;
        }
        if (NetStatusReceiver.mIsWifiNetConnected) {
            Subscription subscription = VideoRepository.getInstance()
                    .addToDownloadList(videoIds)
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
                                    for (VideoBean item : videoBeans) {
                                        mView.updateVideoItem(item);
                                    }
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        } else if (NetStatusReceiver.mIsMobileNetConnected) {
            boolean isMobileNetChecked = VideoRepository.getInstance().checkMobileNetAble();
            if (isMobileNetChecked) {
                if (CloudLibraryApplication.mMobileNetTip) {
                    mView.showMobileTipDialog(R.string.mobile_net_tip2, R.string.download_resume, R.string.change_setting, true);
                }
            } else {
                mView.showMobileTipDialog(R.string.mobile_net_tip1, R.string.download_only_wifi, R.string.change_setting, false);
            }
            Subscription subscription = VideoRepository.getInstance()
                    .addToDownloadList(videoIds)
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
                                    for (VideoBean item : videoBeans) {
                                        mView.updateVideoItem(item);
                                    }
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        } else {
            mView.showToastTip(R.string.no_net_try_later);
        }
    }

    @Override
    public void resumeDownload() {
        VideoRepository.getInstance().setFirstMobileNetTip();
        VideoRepository.getInstance().dealDownloadWifiToMobile();
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
                            mView.setStorageInfo(StringUtils.convertStorageNoB(aLong),
                                    StringUtils.convertStorageNoB(Utils.getFreeSpaceBytes()));
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void clearMemoryData() {
        VideoRepository.getInstance().clearVideoTempList();
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
