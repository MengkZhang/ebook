package com.tzpt.cloudlibrary.ui.video;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.bean.VideoSetBean;
import com.tzpt.cloudlibrary.bean.VideoTOCTree;

import java.util.List;

import rx.functions.Action1;

/**
 * 视频目录
 * Created by tonyjia on 2018/6/27.
 */
public interface VideoDetailContract {

    interface View extends BaseContract.BaseView {

        void showLoadingDialog();

        void setContentView();

        void setVideoDetail(VideoSetBean videoSetBean, List<VideoTOCTree> videoTOCTrees);

        void setNetError();

        void setVideoDetailEmptyList();

        void collectionVideoSuccess(boolean collection);

        void setVideoCollectionStatus(boolean saved);

        void showErrorMsg(int msgId);

        void showNoLoginDialog();

        void playVideoByPlayId(String videoUrl, String localVideoPath, long videoPlayedTime);

        void playFirstVideo(String videoUrl, String localVideoPath, long videoPlayedTime);

        void updateCatalogPlayInfo(long sectionId);

        void playLastVideoComplete();

        void playVideoError();

        void showBelongLibrary(String libCode, String libName);

        void hideBelongLibrary();

    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        boolean isSameVideoByVideoId(long videoId);

        boolean isLogin();

        void setVideosId(long videosId);

        void setVideosBelongLibCode(String beloneLibCode);

        void getVideoDetail();

        void getVideoCollectionStatus();

        void collectionOrCancelVideo();

        void collectionVideo();

        void recordWatchVideo(String hallCode);

        void clearVideoTempData();

        void saveVideoPlayedTime(long playedTime, long totalTime);

        /**
         * 播放下一集视频
         */
        void playNextVideo();

        void setPlayVideoIndex(long playVideoId);

        /**
         * 注册
         *
         * @param eventType
         * @param <T>
         */
        <T> void registerRxBus(Class<T> eventType, Action1<T> action);

        /**
         * 注销
         */
        void unregisterRxBus();

        /**
         * 在视频目录中找到视频本地地址
         *
         * @return
         */
        String getLocalVideoPathByTempVideoList();

        void checkUrlAndPlayVideo(final String playUrl, final String localPlayPath, final long playedTime);

        void retryToGetVideoRealUrl();

        /**
         * 同步内存中的播放时间
         *
         * @param playTime
         */
        void synchronizationPlayTime(int playTime);

    }
}
