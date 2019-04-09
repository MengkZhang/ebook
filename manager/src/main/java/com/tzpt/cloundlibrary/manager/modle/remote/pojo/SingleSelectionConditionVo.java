package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 单选条件
 * Created by Administrator on 2018/9/11.
 */

public class SingleSelectionConditionVo {

    @SerializedName("status")
    @Expose
    public int status;
    @SerializedName("data")
    @Expose
    public RequestData data;

    public class RequestData {
        @SerializedName("errorCode")
        @Expose
        public int errorCode;

        @SerializedName("list")
        @Expose
        public List<Condition> list;
    }

    public class Condition {
        @SerializedName("desc")
        @Expose
        public String desc;

        @SerializedName("name")
        @Expose
        public String name;

        @SerializedName("value")
        @Expose
        public String value;

        @SerializedName("id")
        @Expose
        public String id;          //id

        @SerializedName("userName")
        @Expose
        public String userName; //操作员
    }

}
