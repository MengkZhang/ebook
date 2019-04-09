package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/1/11.
 */

public class WXPayInfoVo {
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

        @SerializedName("appid")
        @Expose
        public String appid;

        @SerializedName("noncestr")
        @Expose
        public String noncestr;

        @SerializedName("packageName")
        @Expose
        public String packageName;

        @SerializedName("partnerid")
        @Expose
        public String partnerid;

        @SerializedName("prepayid")
        @Expose
        public String prepayid;

        @SerializedName("sign")
        @Expose
        public String sign;

        @SerializedName("timestamp")
        @Expose
        public String timestamp;

        @SerializedName("orderNum")
        @Expose
        public String orderNum;
    }
}
