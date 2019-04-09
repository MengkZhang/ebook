package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 开放时间设置
 * Created by Administrator on 2017/6/26.
 */
public class LightSelectVo {
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
        @SerializedName("address")
        @Expose
        public String address;
        @SerializedName("createBy")
        @Expose
        public int createBy;        //创建人
        @SerializedName("createTime")
        @Expose
        public String createTime;   //创建时间
        @SerializedName("houseNumber")
        @Expose
        public String houseNumber;
        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("latitude")
        @Expose
        public double latitude;
        @SerializedName("longitude")
        @Expose
        public double longitude;
        @SerializedName("libraryCode")
        @Expose
        public String libraryCode;

        @SerializedName("phone")
        @Expose
        public String phone;
        @SerializedName("updateBy")
        @Expose
        public String updateBy;
        @SerializedName("updateTime")
        @Expose
        public String updateTime;
        @SerializedName("currentTime")
        @Expose
        public long currentTime;        //服务器当前时间
        @SerializedName("lightOption")
        @Expose
        public String lightOption;  //点亮时间

    }

}
