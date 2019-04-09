package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/1/9.
 */

public class RefundInfoVo {
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

        @SerializedName("availableBalance")
        @Expose
        public double availableBalance;

        @SerializedName("isLimit")
        @Expose
        public boolean isLimit;

        @SerializedName("limitMessage")
        @Expose
        public String limitMessage;

        @SerializedName("maxAmount")
        @Expose
        public double maxAmount;
    }
}
