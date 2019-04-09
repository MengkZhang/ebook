package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 流出管理新增-获取新增书籍信息
 * Created by ZhiqiangJia on 2017-07-09.
 */
public class FlowManageAddNewBookInfoVo {

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
        @SerializedName("value")
        @Expose
        public String value;              //设置流通ID
    }
}
