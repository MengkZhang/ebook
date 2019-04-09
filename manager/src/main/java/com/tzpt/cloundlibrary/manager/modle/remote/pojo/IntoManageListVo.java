package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 流入管理信息
 * Created by ZhiqiangJia on 2017-07-09.
 */
public class IntoManageListVo {

    @SerializedName("conperson")
    @Expose
    public String conperson;    //负责人
    @SerializedName("id")
    @Expose
    public String id;           //id
    @SerializedName("outLibraryName")
    @Expose
    public String outLibraryName;         //名称
    @SerializedName("outCode")
    @Expose
    public String outCode;      //流出单号
    @SerializedName("outDate")
    @Expose
    public String outDate;      //流出日期
    @SerializedName("outHallCode")
    @Expose
    public String outHallCode;  //流出馆号
    @SerializedName("inLibraryPhone")
    @Expose
    public String inLibraryPhone;        //电话
    @SerializedName("signPerson")
    @Expose
    public String signPerson;   //负责人编号
    @SerializedName("sumPrice")
    @Expose
    public double sumPrice;   //总价格
    @SerializedName("bookNum")
    @Expose
    public String bookNum;     //总数量
    @SerializedName("signUserName")
    @Expose
    public String signUserName;  //操作员名称
    @SerializedName("isReturn")
    @Expose
    public int isReturn;       //通还标记
    @SerializedName("circulateStatus")
    @Expose
    public ResponseStatus circulateStatus;

    public class ResponseStatus {
        @SerializedName("index")
        @Expose
        public int index;
        @SerializedName("desc")
        @Expose
        public String desc;
        @SerializedName("name")
        @Expose
        public String name;
    }
}
