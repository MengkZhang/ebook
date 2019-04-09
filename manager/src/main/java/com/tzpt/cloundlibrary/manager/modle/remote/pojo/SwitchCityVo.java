package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 切换城市
 * Created by ZhiqiangJia on 2017-09-07.
 */
public class SwitchCityVo {

    @SerializedName("status")
    @Expose
    public int status;
    @SerializedName("data")
    @Expose
    public ResponseData data;

    public class ResponseData {
        @SerializedName("list")
        @Expose
        public List<City> list;

    }

    public class City {
        @SerializedName("code")
        @Expose
        public String code;
        @SerializedName("name")
        @Expose
        public String name;
    }
}
