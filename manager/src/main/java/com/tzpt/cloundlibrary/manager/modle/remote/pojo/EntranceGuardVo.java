package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 门禁检查
 * Created by Administrator on 2017/7/12.
 */
public class EntranceGuardVo {

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

        @SerializedName("check")
        @Expose
        public String check;                //1:为办借,2:办借超时,3:通过

        @SerializedName("readerId")
        @Expose
        public long readerId;             //读者ID
    }
}
