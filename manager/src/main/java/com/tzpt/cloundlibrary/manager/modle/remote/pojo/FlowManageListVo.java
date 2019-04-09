package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 流出管理列表
 * Created by ZhiqiangJia on 2017-07-09.
 */

public class FlowManageListVo {

    @SerializedName("auditDate")
    @Expose
    public String auditDate;        //审核日期
    @SerializedName("auditPerson")
    @Expose
    public String auditPerson;      //审核人
    @SerializedName("inLibraryConperson")
    @Expose
    public String inLibraryConperson; //负责人
    @SerializedName("id")
    @Expose
    public String id;               //id
    @SerializedName("inHallCode")
    @Expose
    public String inHallCode;       //流入馆
    @SerializedName("inLibraryName")
    @Expose
    public String inLibraryName;    //流入馆名称
    @SerializedName("operUser")
    @Expose
    public String operUser;         //操作员编号
    @SerializedName("code")
    @Expose
    public String code;             //流出单号
    @SerializedName("outDate")
    @Expose
    public String outDate;          //流出日期
    @SerializedName("outHallCode")
    @Expose
    public String outHallCode;      //流出馆号

    @SerializedName("inLibraryPhone")
    @Expose
    public String inLibraryPhone;  //电话号码
    @SerializedName("sumPrice")
    @Expose
    public double sumPrice;       //价格
    @SerializedName("outOperUserId")
    @Expose
    public String outOperUserId;        //操作员id
    @SerializedName("outOperUserName")
    @Expose
    public String outOperUserName;      //操作员
    @SerializedName("bookNum")
    @Expose
    public int bookNum;                 //书籍数量
    @SerializedName("isReturn")
    @Expose
    public int isReturn;                //是否通还
    @SerializedName("circulateStatus")
    @Expose
    public CirculateStatus circulateStatus;  //状态


    public class CirculateStatus {
        @SerializedName("index")
        @Expose
        public int index;//1未发送，2已发送，3.被否决 4.已批准5.被拒绝6.已完成 -1通还
        @SerializedName("desc")
        @Expose
        public String desc;
        @SerializedName("name")
        @Expose
        public String name;
    }
}
