package com.tzpt.cloudlibrary.rxbus.event;

/**
 * Created by Administrator on 2018/7/4.
 */

public class VideoEvent {


    public VideoEvent(int count) {
        mCount = count;
    }

    public VideoEvent() {
    }

    private int mCount;
    private int mMsgType;           //0数量 1获取视频信息
    private long mVideoPlayId;      //视频播放ID
    private String mVideoPlayUrl;   //视频播放地址

    public int getMsgType() {
        return mMsgType;
    }

    public void setMsgType(int msgType) {
        this.mMsgType = msgType;
    }

    public int getCount() {
        return mCount;
    }

    public long getVideoPlayId() {
        return mVideoPlayId;
    }

    public void setVideoPlayId(long videoPlayId) {
        this.mVideoPlayId = videoPlayId;
    }

    public String getVideoPlayUrl() {
        return mVideoPlayUrl;
    }

    public void setVideoPlayUrl(String videoPlayUrl) {
        this.mVideoPlayUrl = videoPlayUrl;
    }
}
