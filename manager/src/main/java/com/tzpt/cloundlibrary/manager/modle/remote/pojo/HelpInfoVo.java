package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 帮助信息
 * Created by ZhiqiangJia on 2018-01-08.
 */
public class HelpInfoVo {

    @SerializedName("status")
    @Expose
    public int status;
    @SerializedName("data")
    @Expose
    public ResponseData data;

    public class ResponseData {
        @SerializedName("title")
        @Expose
        public String title;
        @SerializedName("menus")
        @Expose
        public List<MenuBeanVo> menus;

        public class MenuBeanVo {
            @SerializedName("name")
            @Expose
            public String name;
            @SerializedName("hasChildren")
            @Expose
            public boolean hasChildren;
            @SerializedName("url")
            @Expose
            public String url;
            @SerializedName("childs")
            @Expose
            public ResponseData childs;
        }
    }
}
