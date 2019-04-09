package com.tzpt.cloudlibrary.rxbus.event;

/**
 * 视频观看次数
 */
public class VideoWatchCountEvent {

    public boolean mAddWatchCount;

    public VideoWatchCountEvent(boolean addWatchCount) {
        this.mAddWatchCount = addWatchCount;
    }

}
