package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 流入管理-流出馆操作人员信息
 * Created by ZhiqiangJia on 2017-11-10.
 */
public class OutLibraryOperatorVo {
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
        @SerializedName("outHallCode")
        @Expose
        public String outHallCode;              //流出馆号
        @SerializedName("outLibraryConperson")
        @Expose
        public String outLibraryConperson;      //流出馆负责人
        @SerializedName("outLibraryName")
        @Expose
        public String outLibraryName;           //流出馆名
        @SerializedName("outLibraryPhone")
        @Expose
        public String outLibraryPhone;           //流出馆负责人电话
        @SerializedName("outOperUserName")
        @Expose
        public String outOperUserName;           //流出操作人
    }
}
