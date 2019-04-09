package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 视频,电子书二级分类
 * Created by tonyjia on 2018/6/22.
 */
public class ClassifyTwoLevelVo {

    @SerializedName("id")
    @Expose
    public int id;                          //一级分类ID
    @SerializedName("name")
    @Expose
    public String name;                     //一级分类名称
    @SerializedName("parentId")
    @Expose
    public int parentId;                    //父类ID
    @SerializedName("sortNum")
    @Expose
    public int sortNum;                     //排序号
    @SerializedName("subcategories")
    @Expose
    public List<ClassifyTwoLevelVo> subcategories;//二级分类

}
