package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 馆号列表（借书统计，藏书统计，还书统计，赔书统计,在借统计）
 * Created by ZhiqiangJia on 2017-07-09.
 */
public class StatisticsHallCodeListVo {

    @SerializedName("status")
    @Expose
    public int status;
    @SerializedName("data")
    @Expose
    public RequestData data; //馆号列表

    public class RequestData {
        @SerializedName("errorCode")
        @Expose
        public int errorCode;
        @SerializedName("list")
        @Expose
        public List<String> list;

    }
}
