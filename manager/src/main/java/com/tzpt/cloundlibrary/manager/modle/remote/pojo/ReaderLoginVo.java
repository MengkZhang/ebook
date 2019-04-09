package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 读者登录接口
 * Created by Administrator on 2017/7/7.
 */
public class ReaderLoginVo {
    @SerializedName("status")
    @Expose
    public int status;
    @SerializedName("data")
    @Expose
    public ResponseData data;

    public class ResponseData {
        @SerializedName("bookSum")
        @Expose
        public int bookSum;
        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("errorCode")
        @Expose
        public int errorCode;
        @SerializedName("idCard")
        @Expose
        public String idCard;       //身份证号码
        @SerializedName("cardName")
        @Expose
        public String cardName;     //姓名
        @SerializedName("gender")
        @Expose
        public String gender;       //性别
    }
}
