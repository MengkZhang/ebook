package com.tzpt.cloudlibrary.ui.video;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.bean.VideoBean;
import com.tzpt.cloudlibrary.bean.VideoSetBean;

import java.util.List;

import rx.functions.Action1;

/**
 * Created by Administrator on 2018/7/12.
 */

public interface VideoShelfContract {
    interface View extends BaseContract.BaseView {
        /**
         * 展示所有数据已完成的视频合集、下载中的视频（根据条件筛选）
         *
         * @param data  已完成的视频合集
         * @param count 下载中的视频个数
         * @param video 下载中的视频
         */
        void showAllData(List<VideoSetBean> data, int count, VideoBean video);

        /**
         * 只展示下载中的视频（根据条件筛选）
         *
         * @param count 下载中的视频个数
         * @param video 下载中的视频
         */
        void showDownloading(int count, VideoBean video);

        /**
         * 只展示已完成的视频合集
         *
         * @param data 已完成的视频合集
         */
        void showVideoSetView(List<VideoSetBean> data);

        /**
         * 没有下载内容提示
         */
        void showEmptyView();

        /**
         * 显示存储信息
         *
         * @param downSize    下载大小
         * @param surplusSize 磁盘剩余空间
         */
        void setStorageInfo(String downSize, String surplusSize);

        /**
         * 取消编辑状态
         */
        void setUnableEdit();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        /**
         * 删除视频合集（已下载视频）和所有下载中的视频
         *
         * @param ids 视频合集Id
         */
        void delVideoSetAndAllDownloadingVideo(List<Long> ids);

        /**
         * 删除视频合集
         *
         * @param ids 视频合集Id
         */
        void delVideoSet(List<Long> ids);

        /**
         * 删除所有下载中的视频
         */
        void delAllDownloadingVideo();

        /**
         * 获取本地下载视频合计
         */
        void getLocalVideoSet();

        /**
         * 获取存储情况
         */
        void getStorageInfo();

        void clearMemoryCache();

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
