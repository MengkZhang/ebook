package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 视频列表
 * Created by tonyjia on 2018/6/25.
 */
public class CollectVideoListVo {

    @SerializedName("videosId")
    @Expose
    public long videosId;             //id
    @SerializedName("content")
    @Expose
    public String content;      //内容
    @SerializedName("image")
    @Expose
    public String image;        //	图片
    @SerializedName("name")
    @Expose
    public String name;         //	视频集名称
    @SerializedName("watchTotalNum")
    @Expose
    public int watchTotalNum;   //	观看次数

    public List<VideoResultListVo> resultList;

    public class VideoResultListVo {

    }
    /*
    resultList		array<object>
        createTime	创建时间	string
        id	主键id	number
        readerId	读者id	number
        videosId	视频集id	number

    totalCount		number
    totalPage
     */
}
