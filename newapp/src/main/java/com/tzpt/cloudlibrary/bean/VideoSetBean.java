package com.tzpt.cloudlibrary.bean;

import com.tzpt.cloudlibrary.modle.remote.newdownload.DownloadStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 视频集合
 * Created by Administrator on 2018/6/13.
 */
public class VideoSetBean implements Serializable {
    private long mId;
    private String mCoverImg;
    private String mTitle;
    //list
    private String mContent;        //介绍
    private int mWatchTimes;     //观看次数
    //detail
    private int mFavorStatus;       //收藏状态 1:已收藏，2:未收藏
    private String mShareUrl;       //分享链接

    private List<VideoBean> mVideoList = new ArrayList<>();

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public String getCoverImg() {
        return mCoverImg;
    }

    public void setCoverImg(String coverImg) {
        this.mCoverImg = coverImg;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getContent() {
        return mContent;
    }

    public int getWatchTimes() {
        return mWatchTimes;
    }

    public void setContent(String content) {
        this.mContent = content;
    }

    public void setWatchTimes(int watchTimes) {
        this.mWatchTimes = watchTimes;
    }

    public void setShareUrl(String shareUrl) {
        this.mShareUrl = shareUrl;
    }

    public String getShareUrl() {
        return mShareUrl;
    }

    public void setFavorStatus(int favorStatus) {
        this.mFavorStatus = favorStatus;
    }

    public int getVideoFavorStatus() {
        return mFavorStatus;
    }

    public int getCount() {
        return mVideoList.size();
    }

    public long getTotalBytes() {
        long totalBytes = 0;
        for (VideoBean item : mVideoList) {
            if (item.getStatus() == DownloadStatus.COMPLETE) {
                totalBytes += item.getTotalBytes();
            }
        }
        return totalBytes;
    }

    public void addVideo(VideoBean item) {
        if (mVideoList != null && !mVideoList.contains(item)) {
            mVideoList.add(item);
        }
    }

    public void addVideoList(List<VideoBean> list) {
        mVideoList.addAll(list);
    }

    public void removeVideo(VideoBean item) {
        mVideoList.remove(item);
    }

    public void removeVideo(List<VideoBean> list) {
        mVideoList.removeAll(list);
    }

    public VideoBean findVideoItem(String url) {
        for (VideoBean item : mVideoList) {
            if (item.getUrl().equals(url)) {
                return item;
            }
        }
        return null;
    }

    public List<VideoBean> getVideoList() {
        return mVideoList;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof VideoSetBean)) {
            return false;
        }
        VideoSetBean other = (VideoSetBean) obj;
        return mId == other.getId();
    }
}
