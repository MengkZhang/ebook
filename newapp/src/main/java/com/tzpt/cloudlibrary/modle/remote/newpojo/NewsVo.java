package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tonyjia on 2018/11/21.
 */

public class NewsVo {

    @SerializedName("newsId")
    @Expose
    public long newsId;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("createDate")
    @Expose
    public String createDate;
    @SerializedName("source")
    @Expose
    public String source;
    @SerializedName("image")
    @Expose
    public String image;
    @SerializedName("videoUrl")
    @Expose
    public String videoUrl;
    @SerializedName("summary")
    @Expose
    public String summary;
    @SerializedName("videoDuration")
    @Expose
    public String videoDuration;
    @SerializedName("viewCount")
    @Expose
    public int viewCount;
    @SerializedName("htmlUrl")
    @Expose
    public String htmlUrl;
    @SerializedName("detailUrl")
    @Expose
    public String detailUrl;
    @SerializedName("createTime")
    @Expose
    public long createTime;
    @SerializedName("currentTime")
    @Expose
    public long currentTime;
}
