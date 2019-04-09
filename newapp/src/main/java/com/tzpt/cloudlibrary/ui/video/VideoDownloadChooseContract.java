package com.tzpt.cloudlibrary.ui.video;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.bean.VideoBean;
import com.tzpt.cloudlibrary.bean.VideoTOCTree;

import java.util.List;

import rx.functions.Action1;

/**
 * Created by Administrator on 2018/7/9.
 */

public interface VideoDownloadChooseContract {
    interface View extends BaseContract.BaseView {
        void setData(List<VideoTOCTree> data);

        void setEmpty();

        void setNetError();

        void setStorageInfo(String downSize, String surplusSize);

        void showMobileTipDialog(int msgId, int okStrId, int cancelStrId, boolean isResume);

        void showToastTip(int msgId);

        void updateVideoItem(VideoBean item);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        /**
         * 获取某个合集的所有视频
         *
         * @param setId 合集ID
         */
        void getVideoList(long setId);

        /**
         * 开始下载
         *
         * @param videoIds 下载的视频列表
         */
        void startDownload(List<Long> videoIds);

        /**
         * 继续下载
         */
        void resumeDownload();

        /**
         * 获取存储情况
         */
        void getStorageInfo();

        /**
         * 清空缓存数据
         */
        void clearMemoryData();

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
