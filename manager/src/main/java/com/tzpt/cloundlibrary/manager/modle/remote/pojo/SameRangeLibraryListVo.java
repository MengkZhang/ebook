package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 流出管理新增-相同范围图书馆列表
 * Created by ZhiqiangJia on 2017-07-09.
 */
public class SameRangeLibraryListVo {

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
        @SerializedName("totalCount")
        @Expose
        public int totalCount;                            //转入馆总条数
        @SerializedName("resultList")
        @Expose
        public List<SameRangeLibraryVo> resultList;           //相同范围的转入馆信息列表

    }
}
