package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 更新APP日志
 * Created by ZhiqiangJia on 2017-12-07.
 */
public class UpdateAppRemarkVo {

    @SerializedName("title")
    @Expose
    public String title;           //标题
    @SerializedName("subtitle")
    @Expose
    public String subtitle;        //小标题
    @SerializedName("content")
    @Expose
    public List<String> content;   //日志内容列表
}
