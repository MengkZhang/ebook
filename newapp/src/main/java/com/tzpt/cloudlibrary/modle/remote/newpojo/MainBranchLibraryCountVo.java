package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 总分馆数量
 */
public class MainBranchLibraryCountVo {

    @SerializedName("count")
    @Expose
    public int count; //上级馆，一二级分馆数量总和,用于判断是否显示 总分馆模块大于0显示否则不显示
}
