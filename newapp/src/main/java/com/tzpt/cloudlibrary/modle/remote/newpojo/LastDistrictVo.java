package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2018/4/25.
 */

public class LastDistrictVo extends BaseDataResultVo {
    @SerializedName("list")
    @Expose
    public List<LastDistrictItemVo> list;

    @SerializedName("other")
    @Expose
    public LastDistrictOtherVo other;

    public class LastDistrictOtherVo {
        @SerializedName("code")
        @Expose
        public String code;

        @SerializedName("hasChild")
        @Expose
        public String hasChild;

        @SerializedName("name")
        @Expose
        public String name;

        @SerializedName("level")
        @Expose
        public int level;
    }

    public class LastDistrictItemVo {

        @SerializedName("code")
        @Expose
        public String code;

        @SerializedName("hasChild")
        @Expose
        public String hasChild;

        @SerializedName("name")
        @Expose
        public String name;

        @SerializedName("level")
        @Expose
        public int level;
    }
}
