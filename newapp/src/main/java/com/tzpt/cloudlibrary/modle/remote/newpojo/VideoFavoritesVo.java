package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 收藏视频
 * Created by tonyjia on 2018/7/2.
 */
public class VideoFavoritesVo extends BaseDataResultVo {

    @SerializedName("result")
    @Expose
    public int result;
}
