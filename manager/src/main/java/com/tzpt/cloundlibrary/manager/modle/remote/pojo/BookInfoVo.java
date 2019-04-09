package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 借书书籍
 * Created by Administrator on 2017/7/8.
 */
public class BookInfoVo {
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
        @SerializedName("attachPrice")
        @Expose
        public double attachPrice;
        @SerializedName("barNumber")
        @Expose
        public String barNumber;
        @SerializedName("belongLibraryHallCode")
        @Expose
        public String belongLibraryHallCode;
        @SerializedName("id")
        @Expose
        public long id;
        @SerializedName("price")
        @Expose
        public double price;
        @SerializedName("properTitle")
        @Expose
        public String properTitle;

        @SerializedName("borrowDepositType")
        @Expose
        public int borrowDepositType;//-1:不需要押金办借,1:只能使用共享押金办借,2:只能用馆押金办借,3:先用共享押金后用馆押金一起
    }

}
