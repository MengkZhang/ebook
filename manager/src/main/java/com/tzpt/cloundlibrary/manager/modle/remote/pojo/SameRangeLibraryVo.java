package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 流出管理新增-相同范围图书馆列表-转入馆信息
 * Created by ZhiqiangJia on 2017-07-09.
 */
public class SameRangeLibraryVo {

    @SerializedName("inLibraryConperson")
    @Expose
    public String inLibraryConperson;    //负责人
    @SerializedName("inHallCode")
    @Expose
    public String inHallCode;           //转入馆馆号
    @SerializedName("inLibraryName")
    @Expose
    public String inLibraryName;         //转入馆馆名
    @SerializedName("inLibraryPhone")
    @Expose
    public String inLibraryPhone;        //转入馆电话

}
