package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/7/7.
 */

public class ReaderCurrentNumberVo {
    @SerializedName("obj")
    @Expose
    public Obj obj;
    @SerializedName("code")
    @Expose
    public int code;
    @SerializedName("msg")
    @Expose
    public String msg;

    public class Obj {
        //当前平台借书总押金
        @SerializedName("totalPlatPrice")
        @Expose
        public double totalPlatPrice;
        //平台借书总数
        @SerializedName("totalPlatSum")
        @Expose
        public int totalPlatSum;
        //客户借书总数
        @SerializedName("totalUserSum")
        @Expose
        public int totalUserSum;
        //当前借书总押金
        @SerializedName("totalUserPrice")
        @Expose
        public double totalUserPrice;
    }
}
