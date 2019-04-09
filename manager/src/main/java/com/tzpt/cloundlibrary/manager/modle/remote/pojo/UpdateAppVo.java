package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 更新APP
 * Created by ZhiqiangJia on 2017-07-16.
 */
public class UpdateAppVo {

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
        @SerializedName("forceUpdate")
        @Expose
        public int forceUpdate;      //1强制更新
        @SerializedName("updateDate")
        @Expose
        public String updateDate;       //更新时间
        @SerializedName("remark")
        @Expose
        public String remark;           //备注
        @SerializedName("href")
        @Expose
        public String href;             //地址
        @SerializedName("version")
        @Expose
        public String version;             //版本号
    }

}
