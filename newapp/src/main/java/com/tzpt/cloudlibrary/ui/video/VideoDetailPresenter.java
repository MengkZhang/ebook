package com.tzpt.cloudlibrary.ui.video;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.tzpt.cloudlibrary.CloudLibraryApplication;
import com.tzpt.cloudlibrary.Installation;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.bean.VideoBean;
import com.tzpt.cloudlibrary.bean.VideoDetailBean;
import com.tzpt.cloudlibrary.bean.VideoSetBean;
import com.tzpt.cloudlibrary.bean.VideoTOCTree;
import com.tzpt.cloudlibrary.business_bean.LibraryBean;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.VideoRepository;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;
import com.tzpt.cloudlibrary.modle.remote.newdownload.DownloadStatus;
import com.tzpt.cloudlibrary.rxbus.RxBus;
import com.tzpt.cloudlibrary.rxbus.event.VideoWatchCountEvent;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.ui.map.LocationManager;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;

import java.io.File;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * 视频详情
 * Created by tonyjia on 2018/6/27.
 */
public class VideoDetailPresenter extends RxPresenter<VideoDetailContract.View> implements
        VideoDetailContract.Presenter {

    private long mVideoSetId;
    private boolean mVideoStatusSaved;
    private final RxBus mRxBus;
    private int mPlayVideoIndex = 0;
    private long mVideoId = 0;
    private String mBelongLibCode;


    public VideoDetailPresenter(RxBus rxBus) {
        this.mRxBus = rxBus;
    }

    @Override
    public boolean isSameVideoByVideoId(long videoId) {
        return mVideoId == videoId;
    }

    @Override
    public boolean isLogin() {
        return UserRepository.getInstance().isLogin();
    }

    @Override
    public void setVideosId(long videosId) {
        mVideoSetId = videosId;
    }

    @Override
    public void setVideosBelongLibCode(String belongLibCode) {
        mBelongLibCode = belongLibCode;
    }

    //获取视频详情,目录,收藏状态
    @Override
    public void getVideoDetail() {
        if (mVideoSetId > 0) {
            mView.showLoadingDialog();
            Subscription subscription = Observable.zip(VideoRepository.getInstance().getVideoSetDetail(mVideoSetId),
                    VideoRepository.getInstance().getVideoCatalogList(mVideoSetId),
                    new Func2<VideoSetBean, List<VideoTOCTree>, VideoDetailBean>() {
                        @Override
                        public VideoDetailBean call(VideoSetBean videoSetBean, List<VideoTOCTree> videoTOCTrees) {
                            return new VideoDetailBean(videoSetBean, videoTOCTrees);
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<VideoDetailBean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (null != mView) {
                                mView.setNetError();
                            }
                        }

                        @Override
                        public void onNext(VideoDetailBean videoDetailBean) {
                            if (null != mView) {
                                if (null != videoDetailBean) {
                                    List<VideoTOCTree> videoTOCTrees = videoDetailBean.mVideoTOCTrees;
                                    if (null != videoTOCTrees
                                            && null != videoTOCTrees.get(0)
                                            && null != videoTOCTrees.get(0).subtrees()
                                            && null != videoTOCTrees.get(0).subtrees().get(0)) {

                                        //设置视频目录
                                        mView.setVideoDetail(videoDetailBean.mVideoSetBean, videoTOCTrees);
                                        //设置播放视频时间
                                        long playedTime = 0;
                                        String localVideoPath = null;
                                        List<VideoBean> videoCatalogList = VideoRepository.getInstance().getCatalogVideoTempList();
                                        if (null != videoCatalogList && videoCatalogList.size() > 0) {
                                            playedTime = videoCatalogList.get(0).getPlayedTime();
                                            //当前视频状态为已下载，才可播放本地视频
                                            if (videoCatalogList.get(0).getStatus() == DownloadStatus.COMPLETE) {
                                                localVideoPath = videoCatalogList.get(0).getPath();
                                            }
                                        }
                                        mVideoId = videoTOCTrees.get(0).subtrees().get(0).getId();
                                        //获取所属馆
                                        if (TextUtils.isEmpty(mBelongLibCode)) {
                                            getVideoBelongLibrary(videoTOCTrees.get(0).subtrees().get(0).getUrl(), localVideoPath, playedTime);
                                        } else {
                                            mView.hideBelongLibrary();
                                            mView.setContentView();
                                            mView.playFirstVideo(videoTOCTrees.get(0).subtrees().get(0).getUrl(), localVideoPath, playedTime);
                                        }
                                    } else {
                                        mView.setVideoDetailEmptyList();
                                    }
                                } else {
                                    mView.setVideoDetailEmptyList();
                                }

                            }
                        }
                    });

            addSubscrebe(subscription);
        }
    }

    /**
     * 获取视频所属馆
     *
     * @param videoUrl        视频播放地址
     * @param localVideoPath  视频本地地址
     * @param videoPlayedTime 视频播放时间
     */
    private void getVideoBelongLibrary(final String videoUrl, final String localVideoPath, final long videoPlayedTime) {
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (TextUtils.isEmpty(readerId)) {
            readerId = "0";
        }
        final Subscription subscription = VideoRepository.getInstance().getVideoBelongLibrary(
                LocationManager.getInstance().getLngLat(), mVideoSetId, readerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LibraryBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.hideBelongLibrary();
                            mView.setContentView();
                        }
                    }

                    @Override
                    public void onNext(LibraryBean libraryBean) {
                        if (null != mView) {
                            mView.setContentView();
                            mView.playFirstVideo(videoUrl, localVideoPath, videoPlayedTime);
                            //设置图书馆馆号
                            if (libraryBean != null && null != libraryBean.mLibrary.mCode) {
                                mView.showBelongLibrary(libraryBean.mLibrary.mCode, libraryBean.mLibrary.mName);
                            } else {
                                mView.hideBelongLibrary();
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 获取视频收藏状态
     */
    @Override
    public void getVideoCollectionStatus() {
        if (mVideoSetId > 0) {
            String readerId = UserRepository.getInstance().getLoginReaderId();
            if (!TextUtils.isEmpty(readerId)) {
                Subscription subscription = VideoRepository.getInstance().getCollectionVideoStatus(mVideoSetId, readerId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Boolean>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                if (null != mView) {
                                    if (e instanceof ApiException) {
                                        switch (((ApiException) e).getCode()) {
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
                                    mVideoStatusSaved = aBoolean;
                                    mView.setVideoCollectionStatus(aBoolean);
                                }
                            }
                        });
                addSubscrebe(subscription);
            }
        }
    }

    /**
     * 收藏或者取消收藏
     */
    @Override
    public void collectionOrCancelVideo() {
        if (mVideoStatusSaved) {
            cancelCollectionVideo();
        } else {
            collectionVideo();
        }
    }

    /**
     * 取消收藏视频
     */
    private void cancelCollectionVideo() {
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (!TextUtils.isEmpty(readerId) && mVideoSetId != 0) {
            JSONArray videosIds = new JSONArray();
            videosIds.put(mVideoSetId);
            Subscription subscription = VideoRepository.getInstance().cancelCollectionVideo(videosIds, readerId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (null != mView) {
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
                        public void onNext(Boolean success) {
                            if (null != mView) {
                                if (success) {
                                    mVideoStatusSaved = false;
                                    mView.collectionVideoSuccess(false);
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

    /**
     * 收藏视频
     */
    @Override
    public void collectionVideo() {
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (!TextUtils.isEmpty(readerId) && mVideoSetId != 0) {
            Subscription subscription = VideoRepository.getInstance().collectionVideo(readerId, mVideoSetId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (null != mView) {
                                if (e instanceof ApiException) {
                                    ApiException exception = (ApiException) e;
                                    switch (exception.getCode()) {
                                        case 30100:
                                            mView.showNoLoginDialog();
                                            break;
                                        default:
                                            mView.showErrorMsg(R.string.collect_video_fail);
                                            break;
                                    }
                                }

                            }
                        }

                        @Override
                        public void onNext(Boolean success) {
                            if (null != mView) {
                                if (success) {
                                    mVideoStatusSaved = true;
                                    mView.collectionVideoSuccess(true);
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

    /**
     * 记录观看视频接口
     *
     * @param hallCode 当前馆ID
     */
    @Override
    public void recordWatchVideo(String hallCode) {
        if (mVideoId <= 0) {
            return;
        }
        //发送观看消息增加信息
        mRxBus.post(new VideoWatchCountEvent(true));

        ArrayMap<String, Object> map = new ArrayMap<>();
        if (!TextUtils.isEmpty(hallCode)) {
            map.put("hallCode", hallCode);
        }
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
     * 清除视频目录临时缓存
     */
    @Override
    public void clearVideoTempData() {
        VideoRepository.getInstance().clearCatalogVideoTempList();
    }

    @Override
    public void saveVideoPlayedTime(final long playedTime, final long totalTime) {
        if (mVideoId <= 0 || playedTime == 0) {
            return;
        }
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

    /**
     * 设置视频下标
     *
     * @param playVideoId 视频ID
     */
    @Override
    public void setPlayVideoIndex(long playVideoId) {
        mVideoId = playVideoId;
        List<VideoBean> videoBeanList = VideoRepository.getInstance().getCatalogVideoTempList();
        int playVideoIndex = 0;
        for (int i = 0; i < videoBeanList.size(); i++) {
            if (playVideoId == videoBeanList.get(i).getId()) {
                playVideoIndex = i;
                break;
            }
        }
        mPlayVideoIndex = playVideoIndex;
    }

    /**
     * 播放下一集视频
     */
    @Override
    public void playNextVideo() {
        if (null != mView) {
            final List<VideoBean> videoBeanList = VideoRepository.getInstance().getCatalogVideoTempList();
            if (null != videoBeanList && videoBeanList.size() > 0) {
                if (mPlayVideoIndex + 1 < videoBeanList.size()) {
                    mPlayVideoIndex = mPlayVideoIndex + 1;
                    mVideoId = videoBeanList.get(mPlayVideoIndex).getId();

                    String localVideoPath = null;
                    if (videoBeanList.get(mPlayVideoIndex).getStatus() == DownloadStatus.COMPLETE) {
                        localVideoPath = videoBeanList.get(mPlayVideoIndex).getPath();
                    }
                    checkUrlAndPlayVideo(videoBeanList.get(mPlayVideoIndex).getUrl(),
                            localVideoPath,
                            videoBeanList.get(mPlayVideoIndex).getPlayedTime());
                } else {
                    mView.playLastVideoComplete();
                }
            } else {
                mView.playLastVideoComplete();
            }
        }
    }

    /**
     * 检查URL地址
     *
     * @param playUrl       播放地址
     * @param localPlayPath 本地地址
     * @param playedTime    播放时间
     */
    @Override
    public void checkUrlAndPlayVideo(final String playUrl, final String localPlayPath, final long playedTime) {
        //播放本地视频
        if (!TextUtils.isEmpty(localPlayPath) && new File(localPlayPath).exists()) {
            if (null != mView) {
                mView.playVideoByPlayId(playUrl, localPlayPath, playedTime);
                mView.updateCatalogPlayInfo(mVideoId);
            }
        } else {
            //检查网络视频
            Subscription subscription = VideoRepository.getInstance().getPlayRealUrl(playUrl)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (null != mView) {
                                mView.playVideoError();
                            }
                        }

                        @Override
                        public void onNext(String encryptionURL) {
                            if (null != mView) {
                                mView.playVideoByPlayId(encryptionURL, localPlayPath, playedTime);
                                mView.updateCatalogPlayInfo(mVideoId);
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }

    /**
     * 播放视频失败，重新获取视频真实地址
     */
    @Override
    public void retryToGetVideoRealUrl() {
        final List<VideoBean> videoBeanList = VideoRepository.getInstance().getCatalogVideoTempList();
        if (null != videoBeanList && videoBeanList.size() > 0) {
            if (mPlayVideoIndex < videoBeanList.size()) {
                mVideoId = videoBeanList.get(mPlayVideoIndex).getId();
                String localVideoPath = null;
                if (videoBeanList.get(mPlayVideoIndex).getStatus() == DownloadStatus.COMPLETE) {
                    localVideoPath = videoBeanList.get(mPlayVideoIndex).getPath();
                }
                checkUrlAndPlayVideo(videoBeanList.get(mPlayVideoIndex).getUrl(),
                        localVideoPath,
                        videoBeanList.get(mPlayVideoIndex).getPlayedTime());
            } else {
                mView.playLastVideoComplete();
            }
        } else {
            mView.playLastVideoComplete();
        }
    }

    /**
     * 同步内存中的播放时间
     *
     * @param playTime
     */
    @Override
    public void synchronizationPlayTime(int playTime) {
        VideoRepository.getInstance().synchronizationPlayTime(mPlayVideoIndex, playTime);
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
     * 获取本地视频地址,如果状态为已下载
     *
     * @return
     */
    @Override
    public String getLocalVideoPathByTempVideoList() {
        List<VideoBean> tempVideoList = VideoRepository.getInstance().getCatalogVideoTempList();
        if (null != tempVideoList && tempVideoList.size() > 0) {
            for (VideoBean videoBean : tempVideoList) {
                if (mVideoId == videoBean.getId() && videoBean.getStatus() == DownloadStatus.COMPLETE) {
                    return videoBean.getPath();
                }
                return null;
            }
        }
        return null;
    }
}
