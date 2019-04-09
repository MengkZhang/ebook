package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 收款统计-项目列表
 * Created by ZhiqiangJia on 2017-07-09.
 */
public class CollectingStatisticsProjectVo {

    @SerializedName("status")
    @Expose
    public int status;
    @SerializedName("data")
    @Expose
    public ResponseData data;

    public class ResponseData {
        @SerializedName("errorCode")
        @Expose
        public int errorCode;
        @SerializedName("list")
        @Expose
        public List<Project> list;
    }

    public class Project {
        @SerializedName("name")
        @Expose
        public String name;     //项目名称
        @SerializedName("value")
        @Expose
        public String value;    //项目ID
    }
}
