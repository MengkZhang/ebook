package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2018/11/6.
 */

public class DepositCategoryVo extends BaseDataResultVo {
    @SerializedName("list")
    @Expose
    public List<CategoryVo> categoryList;

    public class CategoryVo {
        @SerializedName("desc")
        @Expose
        public String desc;

        @SerializedName("name")
        @Expose
        public String name;

        @SerializedName("value")
        @Expose
        public String value;
    }
}
