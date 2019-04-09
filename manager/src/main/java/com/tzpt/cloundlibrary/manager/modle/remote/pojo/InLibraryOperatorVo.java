package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 流出管理-流入馆操作人员信息
 * Created by ZhiqiangJia on 2017-11-10.
 */
public class InLibraryOperatorVo {
    @SerializedName("status")
    @Expose
    public int status;
    @SerializedName("data")
    @Expose
    public ResponseData data;

    public class ResponseData {
        @SerializedName("errorCode")
        @Expose
        public int errorCode;
        @SerializedName("auditContactName")
        @Expose
        public String auditContactName;     //审核人名字
        @SerializedName("auditDate")
        @Expose
        public String auditDate;            //审核日期
        @SerializedName("inHallCode")
        @Expose
        public String inHallCode;           //流入馆号
        @SerializedName("inLibraryConperson")
        @Expose
        public String inLibraryConperson;    //流入馆负责人
        @SerializedName("inLibraryName")
        @Expose
        public String inLibraryName;        //流入馆名
        @SerializedName("inLibraryPhone")
        @Expose
        public String inLibraryPhone;       //流入馆负责人电话
        @SerializedName("signUserName")
        @Expose
        public String signUserName;         //签收人
    }
}
