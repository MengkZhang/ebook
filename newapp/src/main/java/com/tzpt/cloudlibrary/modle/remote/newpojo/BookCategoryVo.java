package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/11/9.
 */

public class BookCategoryVo {
    @SerializedName("categoryCode")
    @Expose
    public String categoryCode;

    @SerializedName("categoryId")
    @Expose
    public int categoryId;

    @SerializedName("categoryName")
    @Expose
    public String categoryName;
}
