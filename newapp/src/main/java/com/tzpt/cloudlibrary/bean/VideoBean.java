package com.tzpt.cloudlibrary.bean;

/**
 * Created by Administrator on 2018/6/13.
 */

public class VideoBean {
    private long mId;
    private long mSetId;
    private String mName;
    private String mUrl;
    private String mPath;
    private long mPlayedTime;
    private long mTotalTime;
    private long mLoadBytes;
    private long mTotalBytes;
    private int mStatus;
    private String mLoadSpeed;

    private long mAddTime;
    private long mCompleteTime;

    public long getAddTime() {
        return mAddTime;
    }

    public void setAddTime(long addTime) {
        this.mAddTime = addTime;
    }

    public long getCompleteTime() {
        return mCompleteTime;
    }

    public void setCompleteTime(long completeTime) {
        this.mCompleteTime = completeTime;
    }


    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public long getSetId() {
        return mSetId;
    }

    public void setSetId(long setId) {
        this.mSetId = setId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        this.mPath = path;
    }

    public long getPlayedTime() {
        return mPlayedTime;
    }

    public void setPlayedTime(long playedTime) {
        this.mPlayedTime = playedTime;
    }

    public long getTotalTime() {
        return mTotalTime;
    }

    public void setTotalTime(long totalTime) {
        this.mTotalTime = totalTime;
    }

    public long getLoadBytes() {
        return mLoadBytes;
    }

    public void setLoadBytes(long mLoadBytes) {
        this.mLoadBytes = mLoadBytes;
    }

    public long getTotalBytes() {
        return mTotalBytes;
    }

    public void setTotalBytes(long totalBytes) {
        this.mTotalBytes = totalBytes;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        this.mStatus = status;
    }

    public String getLoadSpeed() {
        return mLoadSpeed;
    }

    public void setLoadSpeed(String loadSpeed) {
        this.mLoadSpeed = loadSpeed;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof VideoBean)) {
            return false;
        }
        VideoBean other = (VideoBean) obj;
        return mId == other.getId();
    }
}
