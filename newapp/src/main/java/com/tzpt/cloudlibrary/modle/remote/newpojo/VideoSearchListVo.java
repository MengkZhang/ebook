package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 视频搜索列表
 * Created by tonyjia on 2018/7/2.
 */
public class VideoSearchListVo extends BaseDataResultVo {

    @SerializedName("totalCount")
    @Expose
    public int totalCount;
    @SerializedName("resultList")
    @Expose
    public List<VideoListVo> resultList;
}
