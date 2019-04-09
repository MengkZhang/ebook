package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 视频收藏状态
 * Created by tonyjia on 2018/6/29.
 */
public class VideoCollectStatusVo extends BaseDataResultVo {

    @SerializedName("favorStatus")
    @Expose
    public int favorStatus;     //1已收藏，0为收藏
}
