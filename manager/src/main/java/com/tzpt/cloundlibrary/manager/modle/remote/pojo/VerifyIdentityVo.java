package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/9/26.
 */

public class VerifyIdentityVo {
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
        public String errorData;

        @SerializedName("hallCode")
        @Expose
        public String hallCode;

        @SerializedName("id")
        @Expose
        public int id;

        @SerializedName("phone")
        @Expose
        public String phone;

        @SerializedName("username")
        @Expose
        public String username;
    }
}
