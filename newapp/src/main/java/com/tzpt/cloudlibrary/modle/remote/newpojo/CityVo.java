package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/11/11.
 */

public class CityVo extends ProvinceVo {
    @SerializedName("provinceCode")
    @Expose
    public String provinceCode;

    @SerializedName("hasChild")
    @Expose
    public String hasChild;
}
