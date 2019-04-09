package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 留言板列表
 * Created by tonyjia on 2019/1/9.
 */
public class MessageBoardReplyListVo {

    @SerializedName("status")
    @Expose
    public int status;

    @SerializedName("errorCode")
    @Expose
    public int errorCode;
    @SerializedName("totalCount")
    @Expose
    public int totalCount;
    @SerializedName("limitTotalCount")
    @Expose
    public int limitTotalCount;

    @SerializedName("resultList")
    @Expose
    public List<MessageBoardReplyVo> resultList;
}
