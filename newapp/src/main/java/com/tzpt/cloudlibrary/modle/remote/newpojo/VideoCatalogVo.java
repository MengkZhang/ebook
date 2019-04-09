package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 视频列表
 * Created by tonyjia on 2018/6/25.
 */
public class VideoCatalogVo {

    @SerializedName("id")
    @Expose
    public int id;                      //章ID
    @SerializedName("name")
    @Expose
    public String name;                 //章名称
    @SerializedName("sections")
    @Expose
    public List<SectionsVo> sections;    //节

    public class SectionsVo {
        @SerializedName("chapterId")
        @Expose
        public int chapterId;           //章ID
        @SerializedName("id")
        @Expose
        public int id;                  //节ID
        @SerializedName("name")
        @Expose
        public String name;             //节名称
        @SerializedName("sortNum")
        @Expose
        public int sortNum;             //排序号
        @SerializedName("videoName")
        @Expose
        public String videoName;        //视频名称
        @SerializedName("videoPath")
        @Expose
        public String videoPath;        //视频路径
        @SerializedName("videosId")
        @Expose
        public int videosId;            //视频集ID
    }
}
