package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 本馆押金
 * Created by ZhiqiangJia on 2017-11-21.
 */
public class LibraryAvailableBalanceVo {

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
    }
}
