package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/11/11.
 */

public class NewsListVo {
    @SerializedName("pageCount")
    @Expose
    public int pageCount;

    @SerializedName("pageNo")
    @Expose
    public int pageNo;

    @SerializedName("totalCount")
    @Expose
    public int totalCount;

    @SerializedName("totalPage")
    @Expose
    public int totalPage;

    @SerializedName("errorCode")
    @Expose
    public int errorCode;

    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("resultList")
    @Expose
    public List<NewsItemVo> resultList;

    public class NewsItemVo {
        @SerializedName("createDate")
        @Expose
        public String createDate;
        @SerializedName("newsId")
        @Expose
        public int newsId;
        @SerializedName("source")
        @Expose
        public String source;
        @SerializedName("title")
        @Expose
        public String title;
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

}
