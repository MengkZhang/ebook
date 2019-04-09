package com.tzpt.cloudlibrary.modle;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;

import com.tzpt.cloudlibrary.CloudLibraryApplication;
import com.tzpt.cloudlibrary.bean.ClassifyTwoLevelBean;
import com.tzpt.cloudlibrary.bean.VideoBean;
import com.tzpt.cloudlibrary.bean.VideoSetBean;
import com.tzpt.cloudlibrary.bean.VideoSetTotalBean;
import com.tzpt.cloudlibrary.bean.VideoTOCTree;
import com.tzpt.cloudlibrary.business_bean.LibraryBean;
import com.tzpt.cloudlibrary.modle.local.SPKeyConstant;
import com.tzpt.cloudlibrary.modle.local.SharedPreferencesUtil;
import com.tzpt.cloudlibrary.modle.local.db.DBManager;
import com.tzpt.cloudlibrary.modle.local.db.DownInfoColumns;
import com.tzpt.cloudlibrary.modle.local.db.VideoColumns;
import com.tzpt.cloudlibrary.modle.local.db.VideoPlayColumns;
import com.tzpt.cloudlibrary.modle.local.db.VideoSetColumns;
import com.tzpt.cloudlibrary.modle.remote.CloudLibraryApi;
import com.tzpt.cloudlibrary.modle.remote.exception.ExceptionEngine;
import com.tzpt.cloudlibrary.modle.remote.exception.ServerException;
import com.tzpt.cloudlibrary.modle.remote.newdownload.DownloadListener;
import com.tzpt.cloudlibrary.modle.remote.newdownload.DownloadStatus;
import com.tzpt.cloudlibrary.modle.remote.newdownload.DownloadTask;
import com.tzpt.cloudlibrary.modle.remote.newdownload.PDownload;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.breakpoint.BreakpointInfo;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.cause.EndCause;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseResultEntityVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.ClassifyTwoLevelVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.ServerTimeVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.VideoBelongLibVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.VideoCatalogVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.VideoCollectListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.VideoCollectStatusVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.VideoDetailVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.VideoFavoritesVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.VideoListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.VideoSearchListVo;
import com.tzpt.cloudlibrary.receiver.NetStatusReceiver;
import com.tzpt.cloudlibrary.rxbus.RxBus;
import com.tzpt.cloudlibrary.utils.MD5Utils;
import com.tzpt.cloudlibrary.utils.StringUtils;

import org.greenrobot.greendao.annotation.NotNull;
import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func3;

/**
 * 视频相关数据
 * Created by Administrator on 2018/6/13.
 */

public class VideoRepository {
    private static VideoRepository mInstance;

    private List<VideoSetBean> mLocalVideoSetList = new ArrayList<>();
    private VideoSetBean mRemoteVideoSet = new VideoSetBean();

    private List<VideoBean> mVideoTempList = new ArrayList<>();

    private List<VideoBean> mDownloadingVideoList = new ArrayList<>();
    private List<VideoBean> mCatalogVideoList = new ArrayList<>();

    private RxBus sRxBus;

    private boolean isMobileNetAble = false;

    public static VideoRepository getInstance() {
        if (mInstance == null) {
            mInstance = new VideoRepository();
        }
        return mInstance;
    }

    private VideoRepository() {
        sRxBus = CloudLibraryApplication.mRxBus;
        isMobileNetAble = SharedPreferencesUtil.getInstance().getBoolean(SPKeyConstant.DOWNLOAD_MOBILE_NET);
    }

    private class HttpResultFunc<T> implements Func1<Throwable, Observable<T>> {
        @Override
        public Observable<T> call(Throwable throwable) {
            return Observable.error(ExceptionEngine.handleException(throwable));
        }
    }

    public boolean checkMobileNetAble() {
        return isMobileNetAble;
    }

    public boolean checkFirstMobileNetTip() {
        return CloudLibraryApplication.mMobileNetTip;
    }

    public void setMobileNetAble(boolean isAble) {
        isMobileNetAble = isAble;
        SharedPreferencesUtil.getInstance().putBoolean(SPKeyConstant.DOWNLOAD_MOBILE_NET, isMobileNetAble);
    }

    public void setFirstMobileNetTip() {
        CloudLibraryApplication.mMobileNetTip = false;
    }

    /**
     * 获取某个视频合集下的视频
     *
     * @param setId 合集ID
     * @return 视频列表
     */
    public Observable<List<VideoBean>> getDownloadVideoList(final long setId) {
        List<VideoBean> videoList = new ArrayList<>();
        for (VideoSetBean setItem : mLocalVideoSetList) {
            if (setItem.getId() == setId) {
                videoList = setItem.getVideoList();
            }
        }
        return Observable.just(videoList);
    }

    /**
     * 获取所有未下载完成的视频
     *
     * @return 视频列表
     */
    @NotNull
    public List<VideoBean> getAllDownloadingVideoList() {
        return new ArrayList<>(mDownloadingVideoList);
    }

    /**
     * 获取已下载文件大小
     *
     * @return 已下载文件大小
     */
    public Observable<Long> getLoadSize() {
        return DBManager.getInstance().getDownInfoList().map(new Func1<List<DownInfoColumns>, Long>() {
            @Override
            public Long call(List<DownInfoColumns> downInfoColumns) {
                long loadByte = 0;
                for (DownInfoColumns item : downInfoColumns) {
                    if (item.getFileType().equals(DownloadTask.VIDEO)
                            && item.getCountLength() > 0
                            && item.getReadLength() == item.getCountLength()) {
                        loadByte += item.getCountLength();
                    }
                }
                return loadByte;
            }
        });
    }

    /**
     * 获取正在下载个数
     *
     * @return 个数
     */
    public int getDownloadingCount() {
        if (mDownloadingVideoList != null && mDownloadingVideoList.size() > 0)
            return mDownloadingVideoList.size();
        return 0;
    }

    /**
     * 获取本地所有合集
     *
     * @return 合集列表
     */
    public Observable<List<VideoSetBean>> getLocalVideoSet() {
        if (mLocalVideoSetList == null || mLocalVideoSetList.size() == 0) {
            return DBManager.getInstance().getVideoSet()
                    .map(new Func1<List<VideoSetColumns>, List<VideoSetBean>>() {
                        @Override
                        public List<VideoSetBean> call(List<VideoSetColumns> videoSetColumns) {
                            if (mLocalVideoSetList == null) {
                                mLocalVideoSetList = new ArrayList<>();
                            }
                            mLocalVideoSetList.clear();
                            for (VideoSetColumns setItem : videoSetColumns) {
                                VideoSetBean setInfo = new VideoSetBean();
                                setInfo.setId(setItem.getId());
                                setInfo.setTitle(setItem.getSetTitle());
                                setInfo.setCoverImg(setItem.getCoverImg());

                                for (VideoColumns videoItem : setItem.getVideos()) {
                                    VideoBean videoInfo = new VideoBean();
                                    videoInfo.setId(videoItem.getId());
                                    videoInfo.setSetId(videoItem.getVideoSetId());
                                    videoInfo.setName(videoItem.getVideoName());
                                    videoInfo.setUrl(videoItem.getUrl());
                                    if (videoItem.getPlayRecord() != null) {
                                        videoInfo.setTotalTime(videoItem.getPlayRecord().getTotalTime());
                                        videoInfo.setPlayedTime(videoItem.getPlayRecord().getPlayTime());
                                    } else {
                                        videoInfo.setTotalTime(0);
                                        videoInfo.setPlayedTime(0);
                                    }
                                    if (videoItem.getDownInfo() != null) {
                                        videoInfo.setPath(videoItem.getDownInfo().getSavePath() + videoItem.getDownInfo().getName());
                                        videoInfo.setTotalBytes(videoItem.getDownInfo().getCountLength());
                                        videoInfo.setLoadBytes(videoItem.getDownInfo().getReadLength());
                                        videoInfo.setStatus(videoItem.getDownInfo().getStatus());
                                        if (videoItem.getDownInfo().getCountLength() > 0
                                                && videoItem.getDownInfo().getCountLength() == videoItem.getDownInfo().getReadLength()) {
                                            videoInfo.setStatus(DownloadStatus.COMPLETE);
                                            setInfo.addVideo(videoInfo);
                                        } else {
                                            if (!mDownloadingVideoList.contains(videoInfo)) {
                                                mDownloadingVideoList.add(videoInfo);
                                                if (videoInfo.getStatus() != DownloadStatus.STOP) {
                                                    if (NetStatusReceiver.mIsWifiNetConnected) {
                                                        addTaskToDownloadTaskList(videoInfo);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                mLocalVideoSetList.add(setInfo);
                            }
                            return mLocalVideoSetList;
                        }
                    });
        } else {
            return Observable.just(mLocalVideoSetList);
        }
    }

    /**
     * 删除正在下载的所有视频
     *
     * @return null
     */
    public Observable<Void> delAllDownloadingVideo() {
        return cancelDownload(mDownloadingVideoList);
    }

    /**
     * 删除正在下载的视频
     *
     * @param data 需要删除的视频
     * @return null
     */
    public Observable<Void> cancelDownload(List<VideoBean> data) {
        return Observable.just(data).map(new Func1<List<VideoBean>, Void>() {
            @Override
            public Void call(List<VideoBean> videoBeans) {
                List<Long> idList = new ArrayList<>();
                List<String> urlList = new ArrayList<>();
                List<Long> setIdList = new ArrayList<>();
                for (VideoBean item : videoBeans) {
                    //这些状态的视频不会在下载器中，需要手动删除文件和记录
                    if (item.getStatus() == DownloadStatus.STOP
                            || item.getStatus() == DownloadStatus.ERROR
                            || item.getStatus() == DownloadStatus.NO_NET_ERROR
                            || item.getStatus() == DownloadStatus.MOBILE_NET_ERROR) {
                        File tmpFile = new File(item.getPath());
                        if (tmpFile.exists()) {
                            tmpFile.delete();
                        }
                        DBManager.getInstance().deleteDownloadInfo(item.getUrl());
                    }
                    idList.add(item.getId());
                    urlList.add(item.getUrl());
                    VideoSetBean setItem = findLocalVideoSet(item.getSetId());
                    if (setItem != null
                            && setItem.getCount() == 0
                            && !isVideoSetDownloading(item.getSetId())) {
                        setIdList.add(item.getSetId());
                    }
                }
                mDownloadingVideoList.removeAll(videoBeans);

                PDownload.with().downloadDispatcher().cancel(urlList);

                DBManager.getInstance().delVideo(idList);
                DBManager.getInstance().delVideoSet(setIdList);
                return null;
            }
        });
    }

    /**
     * 删除某个视频合集下，已下载的视频
     *
     * @param setId 视频合集ID
     * @param data  需要删除的视频
     * @return null
     */
    public Observable<Void> delCompleteVideo(final long setId, final List<VideoBean> data) {
        return Observable.just(data).map(new Func1<List<VideoBean>, Void>() {
            @Override
            public Void call(List<VideoBean> videoBeans) {
                List<Long> idList = new ArrayList<>();
                List<String> urlList = new ArrayList<>();

                VideoSetBean setItem = findLocalVideoSet(setId);
                if (setItem == null) {
                    return null;
                }
                for (VideoBean item : videoBeans) {
                    File tmpFile = new File(item.getPath());
                    if (tmpFile.exists()) {
                        tmpFile.delete();
                    }
                    setItem.removeVideo(item);
                    idList.add(item.getId());
                    urlList.add(item.getUrl());
                }

                if (setItem.getCount() == 0
                        && !isVideoSetDownloading(setId)) {
                    mLocalVideoSetList.remove(setItem);
                    DBManager.getInstance().delVideoSet(setId);
                }

                DBManager.getInstance().deleteDownloadInfo(urlList);
                DBManager.getInstance().delVideo(idList);
                return null;
            }
        });
    }

    /**
     * 删除某个视频合集，并且包括所有已下载的视频
     *
     * @param ids 合集列表
     * @return null
     */
    public Observable<Void> delVideoSet(List<Long> ids) {
        return Observable.just(ids).map(new Func1<List<Long>, Void>() {
            @Override
            public Void call(List<Long> longs) {
                List<Long> setIdList = new ArrayList<>();
                List<Long> idList = new ArrayList<>();
                List<String> urlList = new ArrayList<>();

                for (long setId : longs) {
                    VideoSetBean setItem = findLocalVideoSet(setId);
                    if (setItem != null && setItem.getId() == setId) {
                        for (VideoBean video : setItem.getVideoList()) {
                            File tmpFile = new File(video.getPath());
                            if (tmpFile.exists()) {
                                tmpFile.delete();
                            }
                            idList.add(video.getId());
                            urlList.add(video.getUrl());
                        }
                        if (!isVideoSetDownloading(setId)) {
                            mLocalVideoSetList.remove(setItem);
                            setIdList.add(setId);
                        }
                    }
                }
                DBManager.getInstance().deleteDownloadInfo(urlList);
                DBManager.getInstance().delVideo(idList);
                DBManager.getInstance().delVideoSet(setIdList);
                return null;
            }
        });
    }

    public void clearMemoryData() {
        if (mLocalVideoSetList != null) {
            mLocalVideoSetList.clear();
            mLocalVideoSetList = null;
        }
    }

    public Observable<List<VideoBean>> addToDownloadList(List<Long> id) {
        return Observable.from(id).map(new Func1<Long, List<VideoBean>>() {
            @Override
            public List<VideoBean> call(Long aLong) {
                List<VideoBean> downloadVideo = new ArrayList<>();
                for (VideoBean item : mVideoTempList) {
                    if (aLong == item.getId()) {
                        VideoSetBean setItem = findLocalVideoSet(item.getSetId());
                        if (setItem == null) {
                            if (mRemoteVideoSet != null
                                    && mRemoteVideoSet.getId() == item.getSetId()) {
                                setItem = mRemoteVideoSet;
                            }
                        }
                        if (setItem == null) {
                            return null;
                        }
                        //插入或更新数据
                        VideoSetColumns videoSetColumns = new VideoSetColumns();
                        videoSetColumns.setId(setItem.getId());
                        videoSetColumns.setCoverImg(setItem.getCoverImg());
                        videoSetColumns.setSetTitle(setItem.getTitle());

                        VideoColumns videoColumns = new VideoColumns();
                        videoColumns.setId(item.getId());
                        videoColumns.setVideoName(item.getName());
                        videoColumns.setUrl(item.getUrl());
                        DBManager.getInstance().addVideo(videoSetColumns, videoColumns);

                        if (!mDownloadingVideoList.contains(item)) {
                            mDownloadingVideoList.add(item);
                        }

                        addTaskToDownloadTaskList(item);
                        downloadVideo.add(item);
                    }
                }
                return downloadVideo;
            }
        });
    }

    private static final String FILE_SAVE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ytsg/video/";

    private void addTaskToDownloadTaskList(VideoBean item) {
        File parentFile = new File(FILE_SAVE_PATH);
        DownloadTask newTask = new DownloadTask.Builder(item.getUrl(), parentFile, DownloadTask.VIDEO)
                .setFilename(StringUtils.clipFileName(item.getUrl()))
                .setTimelinessCheck(true)
                .setMinIntervalMillisCallbackProcess(1000)
                .build();
        newTask.enqueue(new DownloadListenerWrapper());
    }

    private class DownloadListenerWrapper implements DownloadListener {

        @Override
        public void taskWait(DownloadTask task) {
            Log.e("VideoRepository", "++++++++++++++++++++++taskWait");
            VideoBean item = updateVideoItem(task, DownloadStatus.WAIT);
            if (item != null) {
                sRxBus.post(item);
            }
            DBManager.getInstance().updateDownInfoStatus(task.getUrl(), DownloadStatus.NORMAL);
        }

        @Override
        public void taskStart(@NonNull DownloadTask task) {
            Log.e("VideoRepository", "++++++++++++++++++++++taskStart");
        }

        @Override
        public void connectTrialStart(@NonNull DownloadTask task) {
            Log.e("VideoRepository", "++++++++++++++++++++++connectTrialStart");
        }

        @Override
        public void connectTrialEnd(@NonNull DownloadTask task) {
            Log.e("VideoRepository", "++++++++++++++++++++++connectTrialEnd");
        }

        @Override
        public void connectStart(@NonNull DownloadTask task) {
            Log.e("VideoRepository", "++++++++++++++++++++++connectStart");
            VideoBean item = updateVideoItem(task, DownloadStatus.CONNECTING);
            if (item != null) {
                sRxBus.post(item);
            }
        }

        @Override
        public void connectEnd(@NonNull DownloadTask task) {
            Log.e("VideoRepository", "++++++++++++++++++++++connectEnd");
        }

        @Override
        public void fetchStart(@NonNull DownloadTask task) {
            Log.e("VideoRepository", "++++++++++++++++++++++fetchStart");
            VideoBean item = updateVideoItem(task, DownloadStatus.START);
            if (item != null) {
                sRxBus.post(item);
            }
        }

        @Override
        public void fetchProgress(@NonNull DownloadTask task) {
//            Log.e("VideoRepository", "++++++++++++++++++++++fetchProgress");
            VideoBean item = updateVideoItem(task, DownloadStatus.DOWNLOADING);
            if (item != null) {
                sRxBus.post(item);
            }
        }

        @Override
        public void fetchEnd(@NonNull DownloadTask task) {
            Log.e("VideoRepository", "++++++++++++++++++++++fetchEnd");
        }

        @Override
        public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause) {
            Log.e("VideoRepository", "++++++++++++++++++++++taskEnd" + cause);
            if (cause == EndCause.COMPLETED) {
                VideoBean item = updateVideoItem(task, DownloadStatus.COMPLETE);
                if (item != null) {
                    sRxBus.post(item);
                }
            } else if (cause == EndCause.CANCELED) {
                VideoBean item = updateVideoItem(task, DownloadStatus.STOP);
                if (item != null) {
                    sRxBus.post(item);
                }
                DBManager.getInstance().updateDownInfoStatus(task.getUrl(), DownloadStatus.STOP);
            } else if (cause == EndCause.WIFI_REQUIRE) {
                VideoBean item = updateVideoItem(task, DownloadStatus.MOBILE_NET_ERROR);
                if (item != null) {
                    sRxBus.post(item);
                }
            } else {
                VideoBean item = updateVideoItem(task, DownloadStatus.ERROR);
                if (item != null) {
                    sRxBus.post(item);
                }
            }
        }
    }

    public void startDownload(VideoBean item) {
        //启动下载
        addTaskToDownloadTaskList(item);
    }

    /**
     * WIFI连接下，启动所有异常下载对象
     */
    public void startDownloadAllError() {
        for (VideoBean item : mDownloadingVideoList) {
            if (item.getStatus() == DownloadStatus.ERROR
                    || item.getStatus() == DownloadStatus.MOBILE_NET_ERROR
                    || item.getStatus() == DownloadStatus.NO_NET_ERROR) {
                addTaskToDownloadTaskList(item);
            }
        }
    }

    /**
     * Mobile Net
     */
    public void dealDownloadWifiToMobile() {
        if (isMobileNetAble) {
            for (VideoBean item : mDownloadingVideoList) {
                if (item.getStatus() == DownloadStatus.ERROR
                        || item.getStatus() == DownloadStatus.NO_NET_ERROR
                        || item.getStatus() == DownloadStatus.MOBILE_NET_ERROR) {
                    addTaskToDownloadTaskList(item);
                }
            }
        } else {
            for (VideoBean item : mDownloadingVideoList) {
                if (item.getStatus() == DownloadStatus.ERROR
                        || item.getStatus() == DownloadStatus.NO_NET_ERROR) {
                    item.setStatus(DownloadStatus.MOBILE_NET_ERROR);
                    sRxBus.post(item);
                }
            }
        }
    }

    public void dealNoNetForDownload() {
        for (VideoBean item : mDownloadingVideoList) {
            if (item.getStatus() == DownloadStatus.ERROR) {
                item.setStatus(DownloadStatus.NO_NET_ERROR);
                sRxBus.post(item);
            }
        }
    }

    public void stopDownload(VideoBean item) {
        PDownload.with().downloadDispatcher().pause(item.getUrl());
    }

    public void stopDownload(List<String> urls) {
        PDownload.with().downloadDispatcher().pause(urls);
    }

    /**
     * 判断视频合集中的视频是否有正在下载的
     *
     * @param setId 合集id
     * @return true 合集中有视频自助下载 false 则反之
     */
    private boolean isVideoSetDownloading(long setId) {
        for (VideoBean item : mDownloadingVideoList) {
            if (item.getSetId() == setId) {
                return true;
            }
        }
        return false;
    }

    private VideoSetBean findLocalVideoSet(long setId) {
        if (mLocalVideoSetList != null) {
            for (VideoSetBean item : mLocalVideoSetList) {
                if (item.getId() == setId) {
                    return item;
                }
            }
        }
        return null;
    }

    private VideoBean updateVideoItem(DownloadTask task, int status) {
        VideoBean item = findVideo(task.getUrl());
        BreakpointInfo downloadInfo = task.getInfo();
        if (item != null && downloadInfo != null) {
            if (downloadInfo.getTotalLength() > 0) {
                item.setTotalBytes(downloadInfo.getTotalLength());
                item.setLoadBytes(downloadInfo.getTotalOffset());
                item.setLoadSpeed(downloadInfo.getSpeed());
            }
            item.setPath(downloadInfo.getFile().getPath());
            item.setStatus(status);

            if (item.getStatus() == DownloadStatus.COMPLETE) {
                VideoSetBean setItem = findLocalVideoSet(item.getSetId());
                if (setItem != null) {
                    setItem.addVideo(item);
                    if (mLocalVideoSetList != null && !mLocalVideoSetList.contains(setItem)) {
                        mLocalVideoSetList.add(setItem);
                    }
                }
                mDownloadingVideoList.remove(item);
            }
        }
        return item;
    }

    private VideoBean findVideo(String url) {
        for (VideoBean item : mDownloadingVideoList) {
            if (item.getUrl().equals(url)) {
                return item;
            }
        }
        return null;
    }

    public void clearVideoTempList() {
        if (mVideoTempList != null) {
            mVideoTempList.clear();
        }
    }

    public Observable<List<VideoTOCTree>> getVideoInfo(final long videosId) {
        return Observable.zip(CloudLibraryApi.getInstance().getVideoCatalogList(videosId),
                DBManager.getInstance().getVideoList(videosId),
                new Func2<BaseResultEntityVo<List<VideoCatalogVo>>, List<VideoColumns>, List<VideoTOCTree>>() {
                    @Override
                    public List<VideoTOCTree> call(BaseResultEntityVo<List<VideoCatalogVo>> listBaseResultEntityVo,
                                                   List<VideoColumns> videoColumns) {
                        if (listBaseResultEntityVo.status == 200) {
                            List<VideoTOCTree> tocList = new ArrayList<>();
                            mVideoTempList.clear();
                            VideoTOCTree node = new VideoTOCTree();
                            for (VideoCatalogVo item : listBaseResultEntityVo.data) {
                                VideoTOCTree node1 = new VideoTOCTree(node);
                                node1.setId(item.id);
                                node1.setName(item.name);
                                tocList.add(node1);
                                if (null != item.sections && item.sections.size() > 0) {
                                    for (VideoCatalogVo.SectionsVo sectionsVo : item.sections) {
                                        VideoTOCTree node2 = new VideoTOCTree(node1);
                                        node2.setId(sectionsVo.id);
                                        node2.setName(sectionsVo.name);
                                        node2.setStatus(setVideoDownloadStatus(sectionsVo.videoPath, videoColumns));
                                        node2.setUrl(sectionsVo.videoPath);
                                        tocList.add(node2);

                                        VideoBean videoBean = new VideoBean();
                                        videoBean.setId(sectionsVo.id);
                                        videoBean.setName(sectionsVo.name);
                                        videoBean.setSetId(videosId);
                                        videoBean.setUrl(sectionsVo.videoPath);
                                        videoBean.setStatus(setVideoDownloadStatus(videoBean.getUrl(), videoColumns));
                                        mVideoTempList.add(videoBean);
                                    }
                                }
                            }
                            return tocList;
                        }
                        return null;
                    }
                }).onErrorResumeNext(new HttpResultFunc<List<VideoTOCTree>>());

    }

    private int setVideoDownloadStatus(String url, List<VideoColumns> videoColumns) {//下载中 已下载 没有状态
        for (VideoColumns item : videoColumns) {
            if (url.equals(item.getUrl())) {
                if (item.getDownInfo().getCountLength() > 0
                        && item.getDownInfo().getCountLength() == item.getDownInfo().getReadLength()) {
                    return DownloadStatus.COMPLETE;
                } else {
                    return DownloadStatus.DOWNLOADING;
                }
            }
        }
        return -1;
    }

    /**
     * 设置本地已播放视频时间
     *
     * @param sectionsId   视频Id
     * @param videoColumns 本地视频列表
     * @return 播放时间
     */
    private long getVideoPlayTime(long sectionsId, List<VideoPlayColumns> videoColumns) {
        for (VideoPlayColumns item : videoColumns) {
            if (0 != item.getPlayTime() && sectionsId == item.getVideoId()) {
                return item.getPlayTime();
            }
        }
        return 0;
    }

    /**
     * 获取本地视频存放地址
     *
     * @param sectionsId   视频ID
     * @param videoColumns 本事视频信息
     * @return 视频本地地址
     */
    private String getVideoLocalPath(long sectionsId, List<VideoColumns> videoColumns) {
        for (VideoColumns item : videoColumns) {
            if (null != item.getDownInfo() && null != item.getDownInfo().getName()
                    && null != item.getDownInfo().getSavePath() && sectionsId == item.getId()) {
                return item.getDownInfo().getSavePath() + item.getDownInfo().getName();
            }
        }
        return "";
    }

    //获取视频二级分类
    public Observable<List<ClassifyTwoLevelBean>> getVideoGradeList() {
        return CloudLibraryApi.getInstance().getVideoGradeList()
                .map(new Func1<BaseResultEntityVo<List<ClassifyTwoLevelVo>>, List<ClassifyTwoLevelBean>>() {
                    @Override
                    public List<ClassifyTwoLevelBean> call(BaseResultEntityVo<List<ClassifyTwoLevelVo>> listBaseResultEntityVo) {
                        return getClassifyTwoLevel(listBaseResultEntityVo.data);
                    }
                }).onErrorResumeNext(new HttpResultFunc<List<ClassifyTwoLevelBean>>());
    }

    //二级列表解析
    private List<ClassifyTwoLevelBean> getClassifyTwoLevel(List<ClassifyTwoLevelVo> list) {
        if (null != list && list.size() > 0) {
            List<ClassifyTwoLevelBean> videoBeanList = new ArrayList<>();
            for (ClassifyTwoLevelVo item : list) {
                ClassifyTwoLevelBean bean = new ClassifyTwoLevelBean();
                bean.mId = item.id;
                bean.mName = item.name;
                if (null != item.subcategories && item.subcategories.size() > 0) {
                    List<ClassifyTwoLevelBean> subVideoList = new ArrayList<>();
                    for (ClassifyTwoLevelVo subItem : item.subcategories) {
                        ClassifyTwoLevelBean subBean = new ClassifyTwoLevelBean();
                        subBean.mId = subItem.id;
                        subBean.mName = subItem.name;
                        subVideoList.add(subBean);
                    }
                    bean.mSubList = subVideoList;
                }
                videoBeanList.add(bean);
            }
            return videoBeanList;
        }
        return null;
    }

    //获取视频目录-本地视频信息
    public Observable<List<VideoTOCTree>> getVideoCatalogList(final long videosId) {
        return Observable.zip(CloudLibraryApi.getInstance().getVideoCatalogList(videosId),
                DBManager.getInstance().getVideoPlayList(),
                DBManager.getInstance().getVideoList(videosId),
                new Func3<BaseResultEntityVo<List<VideoCatalogVo>>, List<VideoPlayColumns>, List<VideoColumns>, List<VideoTOCTree>>() {
                    @Override
                    public List<VideoTOCTree> call(BaseResultEntityVo<List<VideoCatalogVo>> listBaseResultEntityVo,
                                                   List<VideoPlayColumns> videoPlayColumns, List<VideoColumns> videoColumns) {
                        if (listBaseResultEntityVo.status == 200 && listBaseResultEntityVo.data != null
                                && listBaseResultEntityVo.data.size() > 0) {
                            List<VideoTOCTree> tocList = new ArrayList<>();
                            mCatalogVideoList.clear();
                            VideoTOCTree node = new VideoTOCTree();
                            for (VideoCatalogVo item : listBaseResultEntityVo.data) {
                                VideoTOCTree node1 = new VideoTOCTree(node);
                                node1.setId(item.id);
                                node1.setName(item.name);
                                tocList.add(node1);
                                if (null != item.sections && item.sections.size() > 0) {
                                    for (VideoCatalogVo.SectionsVo sectionsVo : item.sections) {
                                        VideoTOCTree node2 = new VideoTOCTree(node1);
                                        node2.setId(sectionsVo.id);
                                        node2.setName(sectionsVo.name);
                                        node2.setUrl(sectionsVo.videoPath);
                                        tocList.add(node2);

                                        VideoBean videoBean = new VideoBean();
                                        videoBean.setId(sectionsVo.id);
                                        videoBean.setName(sectionsVo.name);
                                        videoBean.setSetId(videosId);
                                        videoBean.setUrl(sectionsVo.videoPath);
                                        videoBean.setPath(getVideoLocalPath(sectionsVo.id, videoColumns));
                                        videoBean.setPlayedTime(getVideoPlayTime(sectionsVo.id, videoPlayColumns));
                                        videoBean.setStatus(setVideoDownloadStatus(videoBean.getUrl(), videoColumns));
                                        mCatalogVideoList.add(videoBean);
                                    }
                                }
                            }
                            return tocList;
                        }
                        return null;
                    }
                }).onErrorResumeNext(new HttpResultFunc<List<VideoTOCTree>>());
    }

    //收藏视频
    public Observable<Boolean> collectionVideo(String readerId, long videosId) {
        return CloudLibraryApi.getInstance().collectionVideo(readerId, videosId)
                .map(new Func1<BaseResultEntityVo<VideoFavoritesVo>, Boolean>() {
                    @Override
                    public Boolean call(BaseResultEntityVo<VideoFavoritesVo> baseDataResultVo) {
                        if (baseDataResultVo.status != 200) {
                            if (baseDataResultVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(baseDataResultVo.data.errorCode, baseDataResultVo.data.message);
                        }
                        return baseDataResultVo.data.result == 1;//1保存成功
                    }
                }).onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    //取消收藏视频
    public Observable<Boolean> cancelCollectionVideo(JSONArray videosIds, String readerId) {
        return CloudLibraryApi.getInstance().cancelCollectionVideo(videosIds, readerId)
                .map(new Func1<BaseResultEntityVo<VideoFavoritesVo>, Boolean>() {
                    @Override
                    public Boolean call(BaseResultEntityVo<VideoFavoritesVo> baseDataResultVo) {
                        if (baseDataResultVo.status != 200) {
                            if (baseDataResultVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(baseDataResultVo.data.errorCode, baseDataResultVo.data.message);
                        }
                        //删除后需要刷新用户信息(更新收藏数量)
                        UserRepository.getInstance().refreshUserInfo();
                        return baseDataResultVo.data.result == 1;//1删除成功，0无效删除
                    }
                }).onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    //获取视频收藏状态
    public Observable<Boolean> getCollectionVideoStatus(long videosId, String readerId) {
        return CloudLibraryApi.getInstance().getCollectionVideoStatus(videosId, readerId)
                .map(new Func1<BaseResultEntityVo<VideoCollectStatusVo>, Boolean>() {
                    @Override
                    public Boolean call(BaseResultEntityVo<VideoCollectStatusVo> baseDataResultVo) {
                        if (baseDataResultVo.status != 200) {
                            if (baseDataResultVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(baseDataResultVo.data.errorCode, baseDataResultVo.data.message);
                        }
                        return baseDataResultVo.data.favorStatus == 1;
                    }
                }).onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    //获取一周热门
    public Observable<VideoSetTotalBean> getHotVideoSetList(ArrayMap<String, Object> parameterMap) {
        return CloudLibraryApi.getInstance().getHotVideoSetList(parameterMap)
                .map(new Func1<BaseResultEntityVo<VideoSearchListVo>, VideoSetTotalBean>() {
                    @Override
                    public VideoSetTotalBean call(BaseResultEntityVo<VideoSearchListVo> listBaseResultEntityVo) {
                        if (listBaseResultEntityVo.status == 200 && null != listBaseResultEntityVo.data
                                && null != listBaseResultEntityVo.data.resultList && listBaseResultEntityVo.data.resultList.size() > 0) {
                            VideoSetTotalBean videoSetTotalBean = new VideoSetTotalBean();
                            List<VideoSetBean> beanList = new ArrayList<>();
                            for (VideoListVo item : listBaseResultEntityVo.data.resultList) {
                                VideoSetBean bean = new VideoSetBean();
                                bean.setId(item.id);
                                bean.setTitle(item.name);
                                bean.setContent(item.content);
                                bean.setCoverImg(item.image);
                                bean.setWatchTimes(item.watchTotalNum);
                                beanList.add(bean);
                            }
                            videoSetTotalBean.mTotalCount = listBaseResultEntityVo.data.totalCount;
                            videoSetTotalBean.mVideoSetList = beanList;
                            return videoSetTotalBean;
                        }
                        return null;
                    }
                }).onErrorResumeNext(new HttpResultFunc<VideoSetTotalBean>());
    }

    //获取最新上架
    public Observable<VideoSetTotalBean> getNewVideoSetList(ArrayMap<String, Object> parameterMap) {
        return CloudLibraryApi.getInstance().getNewVideoSetList(parameterMap)
                .map(new Func1<BaseResultEntityVo<VideoSearchListVo>, VideoSetTotalBean>() {
                    @Override
                    public VideoSetTotalBean call(BaseResultEntityVo<VideoSearchListVo> listBaseResultEntityVo) {
                        if (listBaseResultEntityVo.status == 200 && null != listBaseResultEntityVo.data
                                && null != listBaseResultEntityVo.data.resultList && listBaseResultEntityVo.data.resultList.size() > 0) {
                            VideoSetTotalBean videoSetTotalBean = new VideoSetTotalBean();
                            List<VideoSetBean> beanList = new ArrayList<>();
                            for (VideoListVo item : listBaseResultEntityVo.data.resultList) {
                                VideoSetBean bean = new VideoSetBean();
                                bean.setId(item.id);
                                bean.setTitle(item.name);
                                bean.setContent(item.content);
                                bean.setCoverImg(item.image);
                                bean.setWatchTimes(item.watchTotalNum);
                                beanList.add(bean);
                            }
                            videoSetTotalBean.mTotalCount = listBaseResultEntityVo.data.totalCount;
                            videoSetTotalBean.mVideoSetList = beanList;
                            return videoSetTotalBean;
                        }
                        return null;
                    }
                }).onErrorResumeNext(new HttpResultFunc<VideoSetTotalBean>());
    }

    //获取收藏视频列表
    public Observable<List<VideoSetBean>> getCollectVideoSetList(String readerId, ArrayMap<String, Object> parameterMap) {
        return CloudLibraryApi.getInstance().getCollectVideoSetList(readerId, parameterMap)
                .map(new Func1<BaseResultEntityVo<VideoCollectListVo>, List<VideoSetBean>>() {
                    @Override
                    public List<VideoSetBean> call(BaseResultEntityVo<VideoCollectListVo> listBaseResultEntityVo) {
                        if (listBaseResultEntityVo.status != 200) {
                            throw new ServerException(listBaseResultEntityVo.data.errorCode, listBaseResultEntityVo.data.message);
                        }
                        if (null != listBaseResultEntityVo.data && null != listBaseResultEntityVo.data.resultList
                                && listBaseResultEntityVo.data.resultList.size() > 0) {
                            List<VideoSetBean> beanList = new ArrayList<>();
                            for (VideoListVo item : listBaseResultEntityVo.data.resultList) {
                                VideoSetBean bean = new VideoSetBean();
                                bean.setId(item.id);
                                bean.setTitle(item.name);
                                bean.setContent(item.content);
                                bean.setCoverImg(item.image);
                                bean.setWatchTimes(item.watchTotalNum);
                                beanList.add(bean);
                            }
                            return beanList;
                        }
                        return null;
                    }
                }).onErrorResumeNext(new HttpResultFunc<List<VideoSetBean>>());
    }

    //搜索视频列表-平台或者本馆
    public Observable<List<VideoSetBean>> searchVideoSetList(String libCode, ArrayMap<String, Object> parameterMap) {
        if (TextUtils.isEmpty(libCode)) {
            //平台视频
            return CloudLibraryApi.getInstance().searchVideoSetList(parameterMap)
                    .map(new Func1<BaseResultEntityVo<VideoSearchListVo>, List<VideoSetBean>>() {
                        @Override
                        public List<VideoSetBean> call(BaseResultEntityVo<VideoSearchListVo> listBaseResultEntityVo) {
                            if (listBaseResultEntityVo.status != 200) {
                                throw new ServerException(listBaseResultEntityVo.data.errorCode, listBaseResultEntityVo.data.message);
                            }
                            if (null != listBaseResultEntityVo.data && null != listBaseResultEntityVo.data.resultList
                                    && listBaseResultEntityVo.data.resultList.size() > 0) {
                                List<VideoSetBean> beanList = new ArrayList<>();
                                for (VideoListVo item : listBaseResultEntityVo.data.resultList) {
                                    VideoSetBean bean = new VideoSetBean();
                                    bean.setId(item.id);
                                    bean.setTitle(item.name);
                                    bean.setContent(item.content);
                                    bean.setCoverImg(item.image);
                                    bean.setWatchTimes(item.watchTotalNum);
                                    beanList.add(bean);
                                }
                                return beanList;
                            }
                            return null;
                        }
                    }).onErrorResumeNext(new HttpResultFunc<List<VideoSetBean>>());
        } else {
            //馆内视频
            return CloudLibraryApi.getInstance().getLibVideoList(libCode, parameterMap)
                    .map(new Func1<BaseResultEntityVo<VideoSearchListVo>, List<VideoSetBean>>() {
                        @Override
                        public List<VideoSetBean> call(BaseResultEntityVo<VideoSearchListVo> listBaseResultEntityVo) {
                            if (listBaseResultEntityVo.status != 200) {
                                throw new ServerException(listBaseResultEntityVo.data.errorCode, listBaseResultEntityVo.data.message);
                            }
                            if (null != listBaseResultEntityVo.data && null != listBaseResultEntityVo.data.resultList
                                    && listBaseResultEntityVo.data.resultList.size() > 0) {
                                List<VideoSetBean> beanList = new ArrayList<>();
                                for (VideoListVo item : listBaseResultEntityVo.data.resultList) {
                                    VideoSetBean bean = new VideoSetBean();
                                    bean.setId(item.id);
                                    bean.setTitle(item.name);
                                    bean.setContent(item.content);
                                    bean.setCoverImg(item.image);
                                    bean.setWatchTimes(item.watchTotalNum);
                                    beanList.add(bean);
                                }
                                return beanList;
                            }
                            return null;
                        }
                    }).onErrorResumeNext(new HttpResultFunc<List<VideoSetBean>>());
        }
    }

    //获取视频集详情
    public Observable<VideoSetBean> getVideoSetDetail(long videosId) {
        return CloudLibraryApi.getInstance().getVideoSetDetail(videosId)
                .map(new Func1<BaseResultEntityVo<VideoDetailVo>, VideoSetBean>() {
                    @Override
                    public VideoSetBean call(BaseResultEntityVo<VideoDetailVo> videoDetailVo) {
                        if (videoDetailVo.status != 200) {
                            throw new ServerException(videoDetailVo.data.errorCode, videoDetailVo.data.message);
                        }
                        if (null != videoDetailVo.data) {
//                            VideoSetBean setBean = new VideoSetBean();
                            mRemoteVideoSet.setId(videoDetailVo.data.id);
                            mRemoteVideoSet.setTitle(videoDetailVo.data.name);
                            mRemoteVideoSet.setContent(videoDetailVo.data.details);
                            mRemoteVideoSet.setCoverImg(videoDetailVo.data.image);
                            mRemoteVideoSet.setWatchTimes(videoDetailVo.data.watchTotalNum);
                            mRemoteVideoSet.setShareUrl(videoDetailVo.data.shareUrl);
                            return mRemoteVideoSet;
                        }
                        return null;
                    }
                }).onErrorResumeNext(new HttpResultFunc<VideoSetBean>());
    }

    //保存查询浏览记录
    public Observable<Boolean> saveSearchBrowseRecord(ArrayMap<String, Object> arrayMap) {
        return CloudLibraryApi.getInstance().saveSearchBrowseRecord(arrayMap)
                .map(new Func1<BaseResultEntityVo, Boolean>() {
                    @Override
                    public Boolean call(BaseResultEntityVo baseResultEntityVo) {
                        return null;
                    }
                }).onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    //获取视频所属馆
    public Observable<LibraryBean> getVideoBelongLibrary(String lanLat, long videosId, String readerId) {
        return CloudLibraryApi.getInstance().getVideoBelongLibrary(lanLat, videosId, readerId)
                .map(new Func1<BaseResultEntityVo<VideoBelongLibVo>, LibraryBean>() {
                    @Override
                    public LibraryBean call(BaseResultEntityVo<VideoBelongLibVo> videoBelongLibVo) {
                        if (videoBelongLibVo.status == 200 && null != videoBelongLibVo.data) {
                            LibraryBean bean = new LibraryBean();
                            bean.mLibrary.mName = videoBelongLibVo.data.libName;
                            bean.mLibrary.mCode = videoBelongLibVo.data.libCode;
                            return bean;
                        }
                        return null;
                    }
                }).onErrorResumeNext(new HttpResultFunc<LibraryBean>());
    }

    //记录视频观看信息
    public Observable<Boolean> recordWatch(long sectionId, ArrayMap<String, Object> parameterMap) {
        return CloudLibraryApi.getInstance().recordWatch(sectionId, parameterMap)
                .map(new Func1<BaseResultEntityVo, Boolean>() {
                    @Override
                    public Boolean call(BaseResultEntityVo baseResultEntityVo) {
                        return null;
                    }
                });
    }

    /**
     * 保存视频播放时间
     *
     * @param videoId    视频ID
     * @param playedTime 视频已播放时间
     * @param totalTime  视频合计时间
     */
    public void insertOrUpdateVideoPlayedTime(long videoId, long playedTime, long totalTime) {
        DBManager.getInstance().insertOrUpdateRecordPlayTime(videoId, playedTime, totalTime);
    }

    /**
     * 修改内存中的播放时间
     *
     * @param setId      视频集合ID
     * @param videoId    视频ID
     * @param playedTime 视频已播放时间
     * @param totalTime  视频合计时间
     */
    public void updateLocalVideoPlayedTime(final long setId, final long videoId, long playedTime, long totalTime) {
        for (VideoSetBean setItem : mLocalVideoSetList) {
            if (setItem.getId() == setId) {
                for (VideoBean videoBean : setItem.getVideoList()) {
                    if (videoId == videoBean.getId()) {
                        videoBean.setPlayedTime(playedTime);
                        videoBean.setTotalTime(totalTime);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 获取真实视频播放地址
     *
     * @return 视频播放地址
     */
    public Observable<String> getPlayRealUrl(final String url) {
        return CloudLibraryApi.getInstance().getServerTime()
                .map(new Func1<ServerTimeVo, String>() {
                    @Override
                    public String call(ServerTimeVo serverTimeVo) {
                        if (serverTimeVo.status == 200 && null != serverTimeVo.data) {
                            long time = serverTimeVo.data.time;
                            String str = "YTSG" + String.valueOf(time);

                            OkHttpClient client = new OkHttpClient.Builder()
                                    .connectTimeout(60, TimeUnit.SECONDS)
                                    .readTimeout(100, TimeUnit.SECONDS)
                                    .followSslRedirects(false)
                                    .followRedirects(false)
                                    .build();
                            Request request = new Request.Builder().url(url + "&auth=" + MD5Utils.MD5(str) + "&type=1").build();

                            try {
                                Response response = client.newCall(request).execute();
                                if (!isRedirect(response.code())) {
                                    return url;
                                }

                                String realUrl = response.header("Location");
                                response.close();
                                return realUrl;
                            } catch (IOException e) {
                                e.printStackTrace();
                                return null;
                            }
                        }
                        return null;
                    }
                }).onErrorResumeNext(new HttpResultFunc<String>());
    }

    private boolean isRedirect(int code) {
        return code == HttpURLConnection.HTTP_MOVED_PERM
                || code == HttpURLConnection.HTTP_MOVED_TEMP
                || code == HttpURLConnection.HTTP_SEE_OTHER
                || code == HttpURLConnection.HTTP_MULT_CHOICE
                || code == 307
                || code == 308;
    }

    /**
     * 视频目录-视频缓存
     */
    public List<VideoBean> getCatalogVideoTempList() {
        return mCatalogVideoList;
    }

    /**
     * 清除视频列表
     */
    public void clearCatalogVideoTempList() {
        mCatalogVideoList.clear();
    }

    /**
     * 同步内存中的播放时间
     *
     * @param playTime
     */
    public void synchronizationPlayTime(int videoIndex, int playTime) {
        if (null != mCatalogVideoList && mCatalogVideoList.size() > 0 && videoIndex < mCatalogVideoList.size()) {
            mCatalogVideoList.get(videoIndex).setPlayedTime(playTime);
        }
    }

}
