package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/11/10.
 */

public class LibIntroduceVo extends BaseDataResultVo {

    @SerializedName("htmlInfo")
    @Expose
    public HtmlInfo htmlInfo;

    @SerializedName("info")
    @Expose
    public Info info;

    @SerializedName("openInfo")
    @Expose
    public OpenInfo openInfo;

    public class OpenInfo {
        @SerializedName("superviseTel")
        @Expose
        public String superviseTel;
        @SerializedName("phone")
        @Expose
        public String phone;
        @SerializedName("lightTime")
        @Expose
        public String lightTime;
    }

    public class HtmlInfo {
        @SerializedName("content")
        @Expose
        public String content;

        @SerializedName("image")
        @Expose
        public String image;

        @SerializedName("libId")
        @Expose
        public String libId;

        @SerializedName("libName")
        @Expose
        public String libName;

        @SerializedName("summary")
        @Expose
        public String summary;

        @SerializedName("url")
        @Expose
        public String url;
    }

    public class Info {
        @SerializedName("address")
        @Expose
        public String address;
        @SerializedName("distance")
        @Expose
        public int distance;
        @SerializedName("eamil")
        @Expose
        public String eamil;
        @SerializedName("libCode")
        @Expose
        public String libCode;
        @SerializedName("libId")
        @Expose
        public String libId;
        @SerializedName("libName")
        @Expose
        public String libName;
        @SerializedName("lngLat")
        @Expose
        public String lngLat;
        @SerializedName("phone")
        @Expose
        public String phone;
        @SerializedName("webUrl")
        @Expose
        public String webUrl;
        @SerializedName("isValidLngLat")
        @Expose
        public int isValidLngLat; //是否精准定位	0:否 1:是
        @SerializedName("branchLibraryNum")
        @Expose
        public int branchLibraryNum;//是否有总分馆
        @SerializedName("logo")
        @Expose
        public String logo;
        @SerializedName("openRestrictionStr")
        @Expose
        public String openRestrictionStr;//馆开放信息描述
        @SerializedName("libraryLevelName")
        @Expose
        public String libraryLevelName; //馆级别
    }
}
