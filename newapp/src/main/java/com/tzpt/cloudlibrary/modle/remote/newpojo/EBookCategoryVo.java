package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 电子书二级分类
 * Created by Administrator on 2017/11/9.
 */
public class EBookCategoryVo {

    @SerializedName("categoryName")
    @Expose
    public String categoryName;

    @SerializedName("categoryId")
    @Expose
    public int categoryId;

    @SerializedName("subList")
    @Expose
    public List<EBookCategoryVo> subList;

}
