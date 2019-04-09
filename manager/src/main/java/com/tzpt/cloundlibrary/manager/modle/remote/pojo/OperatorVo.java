package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 查询图书馆操作员
 * Created by ZhiqiangJia on 2017-07-09.
 */
public class OperatorVo {

    @SerializedName("id")
    @Expose
    public String id;          //id
    @SerializedName("userName")
    @Expose
    public String userName; //操作员
}
