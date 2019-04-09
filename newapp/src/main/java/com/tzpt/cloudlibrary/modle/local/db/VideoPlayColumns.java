package com.tzpt.cloudlibrary.modle.local.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Administrator on 2018/6/29.
 */
@Entity
public class VideoPlayColumns {
    @Id
    private Long videoId;

    @Property(nameInDb = "play_time")
    private long playTime;

    @Property(nameInDb = "total_time")
    private long totalTime;

    public long getTotalTime() {
        return this.totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public long getPlayTime() {
        return this.playTime;
    }

    public void setPlayTime(long playTime) {
        this.playTime = playTime;
    }

    public Long getVideoId() {
        return this.videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    @Generated(hash = 755210108)
    public VideoPlayColumns(Long videoId, long playTime, long totalTime) {
        this.videoId = videoId;
        this.playTime = playTime;
        this.totalTime = totalTime;
    }

    @Generated(hash = 1896822977)
    public VideoPlayColumns() {
    }
}
