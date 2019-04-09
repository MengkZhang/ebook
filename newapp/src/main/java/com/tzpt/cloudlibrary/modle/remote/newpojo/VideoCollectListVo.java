package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 视频收藏列表
 * Created by tonyjia on 2018/7/2.
 */
public class VideoCollectListVo extends BaseDataResultVo {

    @SerializedName("totalCount")
    @Expose
    public int totalCount;
    @SerializedName("resultList")
    @Expose
    public List<VideoListVo> resultList;
}
