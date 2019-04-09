package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 排行榜
 * Created by Administrator on 2017/11/8.
 */
public class RankingHomeVo {
    @SerializedName("borrows")
    @Expose
    public List<RankBookVo> borrows;
    @SerializedName("praises")
    @Expose
    public List<RankBookVo> praises;
    @SerializedName("recommends")
    @Expose
    public List<RankBookVo> recommends;
    @SerializedName("ebooks")
    @Expose
    public List<RankEBookVo> ebooks;


    public class RankBookVo {
        @SerializedName("id")
        @Expose
        public long id;
        @SerializedName("bookId")
        @Expose
        public long bookId;
        @SerializedName("bookName")
        @Expose
        public String bookName;
        @SerializedName("image")
        @Expose
        public String image;
        @SerializedName("isbn")
        @Expose
        public String isbn;
        @SerializedName("author")
        @Expose
        public String author;
        @SerializedName("publisher")
        @Expose
        public String publisher;
        @SerializedName("publishDate")
        @Expose
        public String publishDate;
    }

    public class RankEBookVo {
        @SerializedName("id")
        @Expose
        public long id;
        @SerializedName("bookName")
        @Expose
        public String bookName;
        @SerializedName("author")
        @Expose
        public String author;
        @SerializedName("image")
        @Expose
        public String image;
        @SerializedName("isbn")
        @Expose
        public String isbn;
        @SerializedName("publishDate")
        @Expose
        public String publishDate;
        @SerializedName("publisher")
        @Expose
        public String publisher;


    }
}
