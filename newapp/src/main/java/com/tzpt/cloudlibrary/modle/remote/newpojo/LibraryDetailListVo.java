package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 图书馆详情列表
 * Created by tonyjia on 2018/11/21.
 */
public class LibraryDetailListVo {

    @SerializedName("book")
    @Expose
    public LibBookVo book;
    @SerializedName("ebook")
    @Expose
    public LibEBookVo ebook;
    @SerializedName("video")
    @Expose
    public LibVideoVo video;
    @SerializedName("news")
    @Expose
    public LibNewsVo news;
    @SerializedName("activity")
    @Expose
    public LibActivityVo activity;

    public class LibBookVo {
        @SerializedName("total")
        @Expose
        public int total;
        @SerializedName("list")
        @Expose
        public List<PaperBookVo> list;
    }

    public class LibEBookVo {
        @SerializedName("total")
        @Expose
        public int total;
        @SerializedName("list")
        @Expose
        public List<EBookVo> list;
    }

    public class LibVideoVo {
        @SerializedName("total")
        @Expose
        public int total;
        @SerializedName("list")
        @Expose
        public List<VideoVo> list;
    }

    public class LibNewsVo {
        @SerializedName("total")
        @Expose
        public int total;
        @SerializedName("list")
        @Expose
        public List<NewsVo> list;
    }

    public class LibActivityVo {
        @SerializedName("total")
        @Expose
        public int total;
        @SerializedName("list")
        @Expose
        public List<ActivityVo> list;
    }
}
