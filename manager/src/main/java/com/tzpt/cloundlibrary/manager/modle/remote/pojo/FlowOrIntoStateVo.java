package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 流出或者管理状态（通用对象）
 * Created by ZhiqiangJia on 2017-07-09.
 */

public class FlowOrIntoStateVo {

    @SerializedName("name")
    @Expose
    public String name;         //状态描述
    @SerializedName("value")
    @Expose
    public String value;        //状态值 ID
}
