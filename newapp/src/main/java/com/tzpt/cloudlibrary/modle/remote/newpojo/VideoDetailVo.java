package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 视频详情
 * Created by tonyjia on 2018/6/28.
 */
public class VideoDetailVo extends BaseDataResultVo {

    @SerializedName("author")
    @Expose
    public String author;           //作者
    @SerializedName("details")
    @Expose
    public String details;          //视频介绍详情
    @SerializedName("createBy")
    @Expose
    public String createBy;         //创建人
    @SerializedName("createTime")
    @Expose
    public String createTime;       //创建时间
    @SerializedName("endTime")
    @Expose
    public String endTime;          //结束时间
    @SerializedName("favorStatus")
    @Expose
    public int favorStatus;         //收藏状态（针对读者），1:已收藏，2:未收藏
    @SerializedName("firstCategory")
    @Expose
    public int firstCategory;       //一级分类ID
    @SerializedName("id")
    @Expose
    public int id;                  //视频ID
    @SerializedName("image")
    @Expose
    public String image;            //视频图片路径
    @SerializedName("name")
    @Expose
    public String name;             //视频名称
    @SerializedName("secondCategory")
    @Expose
    public int secondCategory;      //二级分类ID
    @SerializedName("startTime")
    @Expose
    public String startTime;        //开始时间
    @SerializedName("watchTotalNum")
    @Expose
    public int watchTotalNum;    //开始时间
    @SerializedName("shareUrl")
    @Expose
    public String shareUrl;        //视频分享链接
}
