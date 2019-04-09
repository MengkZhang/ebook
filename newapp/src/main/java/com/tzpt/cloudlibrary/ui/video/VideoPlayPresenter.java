package com.tzpt.cloudlibrary.ui.video;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.tzpt.cloudlibrary.CloudLibraryApplication;
import com.tzpt.cloudlibrary.Installation;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.bean.VideoBean;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.VideoRepository;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;
import com.tzpt.cloudlibrary.rxbus.RxBus;

import org.json.JSONArray;

import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * 本地视频播放协议
 * Created by tonyjia on 2018/7/19.
 */
public class VideoPlayPresenter extends RxPresenter<VideoPlayContract.View> implements
        VideoPlayContract.Presenter {

    private RxBus mRxBus;

    public VideoPlayPresenter(RxBus rxBus) {
        this.mRxBus = rxBus;
    }

    private List<VideoBean> mVideoBeanList;
    private long mVideoSetId;
    private int mPlayVideoIndex = 0;
    private long mVideoId = 0;

    @Override
    public boolean isLogin() {
        return UserRepository.getInstance().isLogin();
    }

    @Override
    public void setVideoSetId(long videoSetId) {
        this.mVideoSetId = videoSetId;
    }

    /**
     * 获取视频完成列表
     */
    @Override
    public void getVideoCompleteList() {
        if (mVideoSetId > 0) {
            Subscription subscription = VideoRepository.getInstance().getDownloadVideoList(mVideoSetId)
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
                                    mVideoBeanList = videoBeans;
                                    mVideoId = mVideoBeanList.get(mPlayVideoIndex).getId();
                                    mView.playVideoByPlayId(mVideoBeanList.get(mPlayVideoIndex).getName(),
                                            mVideoBeanList.get(mPlayVideoIndex).getUrl(),
                                            mVideoBeanList.get(mPlayVideoIndex).getPath(),
                                            mVideoBeanList.get(mPlayVideoIndex).getPlayedTime());
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }

    /**
     * 上报观看视频记录
     */
    @Override
    public void recordWatchVideo() {
        if (mVideoId <= 0) {
            return;
        }
        ArrayMap<String, Object> map = new ArrayMap<>();
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (!TextUtils.isEmpty(readerId)) {
            map.put("watcher", readerId);
        } else {
            map.put("watcher", Installation.id(CloudLibraryApplication.getAppContext()));
        }
        Subscription subscription = VideoRepository.getInstance().recordWatch(mVideoId, map)
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

    /**
     * 播放下一个视频
     */
    @Override
    public void playNextVideo() {
        if (null != mView) {
            if (null != mVideoBeanList && mVideoBeanList.size() > 0) {
                if (mPlayVideoIndex + 1 < mVideoBeanList.size()) {
                    mPlayVideoIndex = mPlayVideoIndex + 1;
                    mVideoId = mVideoBeanList.get(mPlayVideoIndex).getId();
                    mView.playVideoByPlayId(mVideoBeanList.get(mPlayVideoIndex).getName(),
                            mVideoBeanList.get(mPlayVideoIndex).getUrl(),
                            mVideoBeanList.get(mPlayVideoIndex).getPath(),
                            mVideoBeanList.get(mPlayVideoIndex).getPlayedTime());
                } else {
                    mView.playLastVideoComplete();
                }
            } else {
                mView.playLastVideoComplete();
            }
        }
    }

    /**
     * 清空已下载的视频合集
     */
    @Override
    public void clearCompleteVideoList() {
        if (null != mVideoBeanList) {
            mVideoBeanList.clear();
            mVideoBeanList = null;
        }
    }

    /**
     * 设置播放视频下标
     *
     * @param playVideoIndex 下标
     */
    @Override
    public void setPlayVideoIndex(int playVideoIndex) {
        mPlayVideoIndex = playVideoIndex;
    }

    /**
     * 修改内存中的播放时间
     * 保存数据库视频播放时间
     *
     * @param playedTime 播放时间
     * @param totalTime  总共时间
     */
    @Override
    public void saveVideoPlayedTime(final long playedTime, final long totalTime) {
        if (mVideoId <= 0 || playedTime == 0) {
            return;
        }
        VideoRepository.getInstance().updateLocalVideoPlayedTime(mVideoSetId, mVideoId, playedTime, totalTime);
        Subscription subscription = Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                VideoRepository.getInstance().insertOrUpdateVideoPlayedTime(mVideoId, playedTime, totalTime);
            }
        })
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
}
