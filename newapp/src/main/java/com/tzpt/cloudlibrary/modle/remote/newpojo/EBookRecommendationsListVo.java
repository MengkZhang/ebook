package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 好书推荐
 * Created by TonyJia on 2018/5/31.
 */
public class EBookRecommendationsListVo {

    @SerializedName("pageCount")
    @Expose
    public int pageCount;
    @SerializedName("pageNo")
    @Expose
    public int pageNo;
    @SerializedName("totalCount")
    @Expose
    public int totalCount;
    @SerializedName("limitTotalCount")
    @Expose
    public int limitTotalCount;
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
    public List<EBookRecommendationsVo> resultList;

    public class EBookRecommendationsVo {
        @SerializedName("id")
        @Expose
        public long id;
        @SerializedName("author")
        @Expose
        public String author;
        @SerializedName("bookName")
        @Expose
        public String bookName;
        @SerializedName("image")
        @Expose
        public String image;
        @SerializedName("recommendReason")
        @Expose
        public String recommendReason;
        @SerializedName("recommendUnit")
        @Expose
        public String recommendUnit;
        @SerializedName("number")
        @Expose
        public int number;


    }
}
