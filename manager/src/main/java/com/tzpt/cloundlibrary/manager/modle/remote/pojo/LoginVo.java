package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/6/21.
 */

public class LoginVo {

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
        public String value;
    }

}
