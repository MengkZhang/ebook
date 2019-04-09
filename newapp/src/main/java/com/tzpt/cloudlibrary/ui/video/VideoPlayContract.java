package com.tzpt.cloudlibrary.ui.video;

import com.tzpt.cloudlibrary.base.BaseContract;

import rx.functions.Action1;

/**
 * 本地视频播放协议
 * Created by tonyjia on 2018/7/19.
 */
public interface VideoPlayContract {

    interface View extends BaseContract.BaseView {

        void showErrorMsg(int msgId);

        void playLastVideoComplete();

        void playVideoByPlayId(String videoTitle, String videoUrl, String localVideoPath, long playedTime);

    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        boolean isLogin();

        void setVideoSetId(long videoSetId);

        /**
         * 获取某个合集已下载的视频
         */
        void getVideoCompleteList();

        void recordWatchVideo();

        void playNextVideo();

        void clearCompleteVideoList();

        void setPlayVideoIndex(int playVideoIndex);

        void saveVideoPlayedTime(long playedTime, long totalTime);

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
    }
}
