package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 流出或者管理状态（通用对象）
 * Created by ZhiqiangJia on 2017-07-09.
 */
public class FlowOrIntoManageStateVo {

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
        @SerializedName("list")
        @Expose
        public List<FlowOrIntoStateVo> list;
    }

}
