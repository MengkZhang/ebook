package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 流出管理列表
 * Created by ZhiqiangJia on 2017-07-09.
 */
public class IntoManagementListVo {

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
        @SerializedName("totalSumPrice")
        @Expose
        public String totalSumPrice;            //码洋
        @SerializedName("totalCount")
        @Expose
        public int totalCount;                //数量
        @SerializedName("resultList")
        @Expose
        public List<IntoManageListVo> resultList;   //流出管理列表
    }
}
