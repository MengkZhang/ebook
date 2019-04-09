package com.tzpt.cloudlibrary.ui.video;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.bean.VideoBean;

import java.util.List;

import rx.functions.Action1;

/**
 * Created by Administrator on 2018/7/2.
 */

public interface VideoCacheListContract {
    interface View extends BaseContract.BaseView {
        void setData(List<VideoBean> list);

        void showEmptyList();

        void setStorageInfo(String downSize, String surplusSize);

        void setAllDownloadBtnStatus(boolean status);

        void setUnableEdit();

        void showToastTip(int msgId);

        void showMobileTipDialog(int msgId, int okStrId, int cancelStrId);

        void checkStoragePermission();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        /**
         * 获取所有下载中的视频
         */
        void getVideoCacheList();

        /**
         * 检查所有下载中的视频状态
         */
        void checkVideoCacheStatus();

        /**
         * 获取某个合集已下载的视频
         *
         * @param setId 合集ID
         */
        void getVideoCompleteList(long setId);

        /**
         * 开始/暂停下载
         *
         * @param item 视频对象
         */
        void dealDownloadItem(VideoBean item);

        /**
         * 全部开始/暂停操作
         */
        void dealAllDownloadItem();

        /**
         * 删除下载
         *
         * @param videoBeans 视频
         */
        void delDownloadingVideo(List<VideoBean> videoBeans);

        /**
         * 删除已下载视频
         *
         * @param setId      合集ID
         * @param videoBeans 视频
         */
        void delCompleteVideo(long setId, List<VideoBean> videoBeans);

        /**
         * 获取存储情况
         */
        void getStorageInfo();

        /**
         * 继续下载
         */
        void resumeDownload();

        void setPermissionOk();

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
