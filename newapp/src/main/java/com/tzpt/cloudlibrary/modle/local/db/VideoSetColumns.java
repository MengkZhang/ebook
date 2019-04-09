package com.tzpt.cloudlibrary.modle.local.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

/**
 * Created by Administrator on 2018/6/13.
 */
@Entity
public class VideoSetColumns {
    @Id    //视频唯一标识符
    private Long id;

    @Property(nameInDb = "set_title")
    private String setTitle;

    @Property(nameInDb = "cover_img")
    private String coverImg;

    @ToMany(referencedJoinProperty = "videoSetId")
    private List<VideoColumns> videos;

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

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1923228979)
    public synchronized void resetVideos() {
        videos = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 683746347)
    public List<VideoColumns> getVideos() {
        if (videos == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            VideoColumnsDao targetDao = daoSession.getVideoColumnsDao();
            List<VideoColumns> videosNew = targetDao._queryVideoSetColumns_Videos(id);
            synchronized (this) {
                if(videos == null) {
                    videos = videosNew;
                }
            }
        }
        return videos;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1514607040)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getVideoSetColumnsDao() : null;
    }

    /** Used for active entity operations. */
    @Generated(hash = 2143637090)
    private transient VideoSetColumnsDao myDao;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    public String getCoverImg() {
        return this.coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSetTitle() {
        return this.setTitle;
    }

    public void setSetTitle(String setTitle) {
        this.setTitle = setTitle;
    }

    @Generated(hash = 845547441)
    public VideoSetColumns(Long id, String setTitle, String coverImg) {
        this.id = id;
        this.setTitle = setTitle;
        this.coverImg = coverImg;
    }

    @Generated(hash = 1936543063)
    public VideoSetColumns() {
    }
}
