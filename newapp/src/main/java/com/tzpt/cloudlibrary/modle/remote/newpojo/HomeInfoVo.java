package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 首页信息
 * Created by tonyjia on 2018/10/19.
 */
public class HomeInfoVo {
    @SerializedName("errorCode")
    @Expose
    public int errorCode;

    @SerializedName("library")
    @Expose
    public List<LibraryVo> library;

    @SerializedName("book")
    @Expose
    public List<PaperBookVo> book;

    @SerializedName("ebook")
    @Expose
    public List<EBookVo> ebook;

    @SerializedName("video")
    @Expose
    public List<VideoVo> video;

    @SerializedName("activity")
    @Expose
    public List<ActivityVo> activity;

    @SerializedName("news")
    @Expose
    public List<NewsVo> news;

}
