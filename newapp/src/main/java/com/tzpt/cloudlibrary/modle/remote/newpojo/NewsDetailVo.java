package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/11/21.
 */

public class NewsDetailVo {
    @SerializedName("content")
    @Expose
    public String content;
    @SerializedName("createDate")
    @Expose
    public String createDate;
    @SerializedName("id")
    @Expose
    public long id;
    @SerializedName("image")
    @Expose
    public String image;
    @SerializedName("isPraise")
    @Expose
    public String isPraise;
    @SerializedName("praiseCount")
    @Expose
    public int praiseCount;
    @SerializedName("source")
    @Expose
    public String source;
    @SerializedName("summary")
    @Expose
    public String summary;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("detailUrl")
    @Expose
    public String detailUrl;
    @SerializedName("viewCount")
    @Expose
    public int viewCount;
    @SerializedName("errorCode")
    @Expose
    public int errorCode;

}
