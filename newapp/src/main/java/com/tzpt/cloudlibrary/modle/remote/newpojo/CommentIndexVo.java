package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 查询评论下标对象
 * Created by ZhiqiangJia on 2018-01-25.
 */
public class CommentIndexVo {

    @SerializedName("index")
    @Expose
    public int index;
    @SerializedName("errorCode")
    @Expose
    public int errorCode;

}
