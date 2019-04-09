package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 借书统计-书籍信息
 * Created by ZhiqiangJia on 2017-07-09.
 */
public class BorrowBookInfoVo {

    @SerializedName("barNumber")
    @Expose
    public String barNumber;
    @SerializedName("belongLibraryHallCode")
    @Expose
    public String belongLibraryHallCode;
    @SerializedName("price")
    @Expose
    public double price;
    @SerializedName("properTitle")
    @Expose
    public String properTitle;

}
