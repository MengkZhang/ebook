package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 返回阅读消息状态
 * Created by ZhiqiangJia on 2017-11-01.
 */
public class NoReadMsgVo {

    @SerializedName("status")
    @Expose
    public int status;
    @SerializedName("data")
    @Expose
    public ResponseData data;

    public class ResponseData {
        @SerializedName("value")
        @Expose
        public int value;
        @SerializedName("errorCode")
        @Expose
        public int errorCode;
    }
}
