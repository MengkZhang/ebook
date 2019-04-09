package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/11/22.
 */

public class UserHeadListResultVo extends BaseDataResultVo {
    @SerializedName("result")
    @Expose
    public List<UserHeadListItemVo> result;
}
