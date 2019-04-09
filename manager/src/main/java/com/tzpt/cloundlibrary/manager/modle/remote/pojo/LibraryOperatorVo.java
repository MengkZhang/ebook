package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 查询图书馆管理员
 * Created by ZhiqiangJia on 2017-07-09.
 */
public class LibraryOperatorVo {

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
        public List<OperatorVo> list; //操作员列表
    }


}
