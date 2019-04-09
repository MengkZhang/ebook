package com.tzpt.cloudlibrary.modle.local.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * Created by Administrator on 2018/6/13.
 */
@Entity
public class VideoColumns {
    @Id    //视频唯一标识符
    private Long id;

    private Long videoSetId;

    @ToOne(joinProperty = "videoSetId")
    private VideoSetColumns videoSet;

    @Property(nameInDb = "video_name")
    private String videoName;

    @Property(nameInDb = "url")
    private String url;

    @ToOne(joinProperty = "url")
    private DownInfoColumns downInfo;

    @ToOne(joinProperty = "id")
    private VideoPlayColumns playRecord;

    @Generated(hash = 1736338356)
    private transient String downInfo__resolvedKey;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 72844676)
    private transient VideoColumnsDao myDao;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    @Generated(hash = 977333900)
    private transient Long playRecord__resolvedKey;

    @Generated(hash = 1305247237)
    private transient Long videoSet__resolvedKey;

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVideoName() {
        return this.videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public Long getVideoSetId() {
        return this.videoSetId;
    }

    public void setVideoSetId(Long videoSetId) {
        this.videoSetId = videoSetId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1800607431)
    public void setDownInfo(DownInfoColumns downInfo) {
        synchronized (this) {
            this.downInfo = downInfo;
            url = downInfo == null ? null : downInfo.getUrl();
            downInfo__resolvedKey = url;
        }
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 2004744716)
    public DownInfoColumns getDownInfo() {
        String __key = this.url;
        if (downInfo__resolvedKey == null || downInfo__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DownInfoColumnsDao targetDao = daoSession.getDownInfoColumnsDao();
            DownInfoColumns downInfoNew = targetDao.load(__key);
            synchronized (this) {
                downInfo = downInfoNew;
                downInfo__resolvedKey = __key;
            }
        }
        return downInfo;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 231360933)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getVideoColumnsDao() : null;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 1533511586)
    public void setPlayRecord(VideoPlayColumns playRecord) {
        synchronized (this) {
            this.playRecord = playRecord;
            id = playRecord == null ? null : playRecord.getVideoId();
            playRecord__resolvedKey = id;
        }
    }

    /**
     * To-one relationship, resolved on first access.
     */
    @Generated(hash = 2095868467)
    public VideoPlayColumns getPlayRecord() {
        Long __key = this.id;
        if (playRecord__resolvedKey == null || !playRecord__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            VideoPlayColumnsDao targetDao = daoSession.getVideoPlayColumnsDao();
            VideoPlayColumns playRecordNew = targetDao.load(__key);
            synchronized (this) {
                playRecord = playRecordNew;
                playRecord__resolvedKey = __key;
            }
        }
        return playRecord;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1759938010)
    public void setVideoSet(VideoSetColumns videoSet) {
        synchronized (this) {
            this.videoSet = videoSet;
            videoSetId = videoSet == null ? null : videoSet.getId();
            videoSet__resolvedKey = videoSetId;
        }
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 577920759)
    public VideoSetColumns getVideoSet() {
        Long __key = this.videoSetId;
        if (videoSet__resolvedKey == null || !videoSet__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            VideoSetColumnsDao targetDao = daoSession.getVideoSetColumnsDao();
            VideoSetColumns videoSetNew = targetDao.load(__key);
            synchronized (this) {
                videoSet = videoSetNew;
                videoSet__resolvedKey = __key;
            }
        }
        return videoSet;
    }

    public void setVideoSetId(long videoSetId) {
        this.videoSetId = videoSetId;
    }

    @Generated(hash = 1503573867)
    public VideoColumns(Long id, Long videoSetId, String videoName, String url) {
        this.id = id;
        this.videoSetId = videoSetId;
        this.videoName = videoName;
        this.url = url;
    }

    @Generated(hash = 1498763679)
    public VideoColumns() {
    }
}
