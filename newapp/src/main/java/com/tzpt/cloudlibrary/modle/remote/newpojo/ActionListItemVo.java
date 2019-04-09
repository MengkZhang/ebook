package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/11/12.
 */

public class ActionListItemVo {
    @SerializedName("address")
    @Expose
    public String address;

    @SerializedName("allowApplyNow")
    @Expose
    public int allowApplyNow;

    @SerializedName("contactName")
    @Expose
    public String contactName;

    @SerializedName("contactPhone")
    @Expose
    public String contactPhone;

    @SerializedName("content")
    @Expose
    public String content;

    @SerializedName("endDate")
    @Expose
    public String endDate;

    @SerializedName("enrollment")
    @Expose
    public int enrollment;

    @SerializedName("htmlUrl")
    @Expose
    public String htmlUrl;

    @SerializedName("id")
    @Expose
    public int id;

    @SerializedName("image")
    @Expose
    public String image;

    @SerializedName("isApply")
    @Expose
    public int isApply;

    @SerializedName("nextId")
    @Expose
    public int nextId;

    @SerializedName("previousId")
    @Expose
    public int previousId;

    @SerializedName("source")
    @Expose
    public String source;

    @SerializedName("startDate")
    @Expose
    public String startDate;

    @SerializedName("status")
    @Expose
    public int status;

    @SerializedName("summary")
    @Expose
    public String summary;

    @SerializedName("title")
    @Expose
    public String title;

    @SerializedName("top")
    @Expose
    public int top;

    @SerializedName("isJoin")
    @Expose
    public int isJoin;
    @SerializedName("detailUrl")
    @Expose
    public String detailUrl;
    @SerializedName("applyStatus")
    @Expose
    public int applyStatus;
}
