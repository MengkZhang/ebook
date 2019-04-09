package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 图书馆管理员的登录信息
 * Created by ZhiqiangJia on 2017-10-26.
 */
public class VerifyLibraryOperatorPswVo {

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
        public boolean value;
    }
}
