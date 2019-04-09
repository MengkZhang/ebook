package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 购书架
 * Created by tonyjia on 2018/8/16.
 */
public class SelfBuyBookShelfVo {

    @SerializedName("errorCode")
    @Expose
    public int errorCode;
    @SerializedName("totalBuyPrice")
    @Expose
    public double totalBuyPrice;
    @SerializedName("totalFixedPrice")
    @Expose
    public double totalFixedPrice;
    @SerializedName("totalCount")
    @Expose
    public int totalCount;

    @SerializedName("resultList")
    @Expose
    public List<ShelfBookVo> resultList;

    public class ShelfBookVo {

        @SerializedName("author")
        @Expose
        public String author;
        @SerializedName("buyPrice")
        @Expose
        public double buyPrice;
        @SerializedName("fixedPrice")
        @Expose
        public double fixedPrice;
        @SerializedName("buyTime")
        @Expose
        public String buyTime;
        @SerializedName("categoryName")
        @Expose
        public String categoryName;
        @SerializedName("image")
        @Expose
        public String image;
        @SerializedName("isbn")
        @Expose
        public String isbn;
        @SerializedName("levelName")
        @Expose
        public String levelName;
        @SerializedName("press")
        @Expose
        public String press;
        @SerializedName("properTitle")
        @Expose
        public String properTitle;
        @SerializedName("publishDate")
        @Expose
        public String publishDate;
        @SerializedName("id")
        @Expose
        public int id;
        @SerializedName("isPraise")
        @Expose
        public int isPraise;        //是否点赞0：否，1：是
        @SerializedName("libBookId")
        @Expose
        public int libBookId;
        @SerializedName("noteId")
        @Expose
        public int noteId;          //读书笔记id，查看笔记详情传递
    }
}
