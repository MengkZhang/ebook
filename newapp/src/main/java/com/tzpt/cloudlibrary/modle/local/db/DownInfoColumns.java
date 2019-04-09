package com.tzpt.cloudlibrary.modle.local.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

/**
 * 下载记录数据库表
 * Created by Administrator on 2017/5/31.
 */
@Entity
public class DownInfoColumns {
    //存储位置
    @Property(nameInDb = "save_path")
    private String savePath;
    //文件总长度
    @Property(nameInDb = "count_length")
    private long countLength;
    //下载长度
    @Property(nameInDb = "read_length")
    private long readLength;
    //下载速度
    @Property(nameInDb = "speed")
    private float speed;
    //文件名字
    @Property(nameInDb = "file_name")
    private String name;
    //文件类型
    @Property(nameInDb = "file_type")
    private String fileType;
    //状态
    @Property(nameInDb = "status")
    private int status;
    //开始时间
    @Property(nameInDb = "add_time")
    private long addTime;
    //完成时间
    @Property(nameInDb = "complete_time")
    private long completeTime;
    //url
    @Property(nameInDb = "url")
    @Id
    private String url;

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getReadLength() {
        return this.readLength;
    }

    public void setReadLength(long readLength) {
        this.readLength = readLength;
    }

    public long getCountLength() {
        return this.countLength;
    }

    public void setCountLength(long countLength) {
        this.countLength = countLength;
    }

    public String getSavePath() {
        return this.savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public float getSpeed() {
        return this.speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCompleteTime() {
        return this.completeTime;
    }

    public void setCompleteTime(long completeTime) {
        this.completeTime = completeTime;
    }

    public long getAddTime() {
        return this.addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getFileType() {
        return this.fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    @Generated(hash = 1048986798)
    public DownInfoColumns(String savePath, long countLength, long readLength, float speed,
            String name, String fileType, int status, long addTime, long completeTime, String url) {
        this.savePath = savePath;
        this.countLength = countLength;
        this.readLength = readLength;
        this.speed = speed;
        this.name = name;
        this.fileType = fileType;
        this.status = status;
        this.addTime = addTime;
        this.completeTime = completeTime;
        this.url = url;
    }

    @Generated(hash = 287232890)
    public DownInfoColumns() {
    }
}
