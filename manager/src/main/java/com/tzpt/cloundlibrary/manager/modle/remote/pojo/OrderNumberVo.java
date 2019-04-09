package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/7/7.
 */

public class OrderNumberVo {
    @SerializedName("attributes")
    @Expose
    public Attributes attributes;
    @SerializedName("code")
    @Expose
    public int code;
    @SerializedName("msg")
    @Expose
    public String msg;

    public class Attributes {
        @SerializedName("codeNumber")
        @Expose
        public String codeNumber;
        @SerializedName("date")
        @Expose
        public String date;
    }
}
