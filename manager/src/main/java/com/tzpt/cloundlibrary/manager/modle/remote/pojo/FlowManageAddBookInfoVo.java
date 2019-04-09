package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 流出管理新增-获取单子详情信息
 * Created by ZhiqiangJia on 2017-07-09.
 */
public class FlowManageAddBookInfoVo {

    @SerializedName("status")
    @Expose
    public int status;

    @SerializedName("data")
    @Expose
    public ResponseData data;                    //设置流通ID

    public class ResponseData {
        @SerializedName("errorCode")
        @Expose
        public int errorCode;
        @SerializedName("totalSumPrice")
        @Expose
        public String totalSumPrice;             //合计价格
        @SerializedName("totalCount")
        @Expose
        public int totalCount;                  //合计数量
        @SerializedName("resultList")
        @Expose
        public List<FlowBookInfo> resultList;   //单子详情书籍列表信息
    }

    public class FlowBookInfo {
        @SerializedName("verietyCode")
        @Expose
        public String verietyCode;
        @SerializedName("frameCode")
        @Expose
        public String frameCode;
        @SerializedName("author")
        @Expose
        public String author;
        @SerializedName("press")
        @Expose
        public String press;
        @SerializedName("isbn")
        @Expose
        public String isbn;
        @SerializedName("barNumber")
        @Expose
        public String barNumber;            //条码号
        @SerializedName("belongLibraryHallCode")
        @Expose
        public String belongLibraryHallCode;//所属馆号
        @SerializedName("circulateId")
        @Expose
        public String circulateId;                   //id
        @SerializedName("circulateMapId")
        @Expose
        public String circulateMapId;                //关联id
        @SerializedName("libraryBookId")
        @Expose
        public String libraryBookId;                   //id
        @SerializedName("price")
        @Expose
        public double price;                //价格
        @SerializedName("attachPrice")
        @Expose
        public double attachPrice;
        @SerializedName("properTitle")
        @Expose
        public String properTitle;          //书籍名称
    }
}
