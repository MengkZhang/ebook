package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 借书管理code=2
 * Created by Administrator on 2017/7/10.
 */

public class BorrowBookListVo {
    @SerializedName("belongLibraryHallCode")
    @Expose
    public String belongLibraryHallCode;
    @SerializedName("belongLibraryHallCode")
    @Expose
    public String barNumber;
    @SerializedName("properTitle")
    @Expose
    public String properTitle;
    @SerializedName("bookState")
    @Expose
    public String bookState;
}
