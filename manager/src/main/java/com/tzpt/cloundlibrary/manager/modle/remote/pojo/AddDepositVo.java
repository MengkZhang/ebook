package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 交押金
 * Created by Administrator on 2017/7/8.
 */

public class AddDepositVo {
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
        @SerializedName("errorData")
        @Expose
        public ErrorData errorData;
    }

    public class ErrorData {
        @SerializedName("availablePay")
        @Expose
        public double availablePay;
    }
}
