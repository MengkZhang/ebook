package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/11/8.
 */

public class RankingBookListVo {
    @SerializedName("pageNo")
    @Expose
    public int pageNo;

    @SerializedName("pageCount")
    @Expose
    public int pageCount;

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
    public List<BookListItemVo> resultList;
}
