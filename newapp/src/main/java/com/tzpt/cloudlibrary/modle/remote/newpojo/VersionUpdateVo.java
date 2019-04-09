package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/11/29.
 */

public class VersionUpdateVo extends BaseDataResultVo {
    @SerializedName("forceUpdate")
    @Expose
    public int forceUpdate;

    @SerializedName("href")
    @Expose
    public String href;

    @SerializedName("id")
    @Expose
    public long id;

    @SerializedName("remark")
    @Expose
    public String remark;

    @SerializedName("updateDate")
    @Expose
    public long updateDate;

    @SerializedName("version")
    @Expose
    public String version;
}
