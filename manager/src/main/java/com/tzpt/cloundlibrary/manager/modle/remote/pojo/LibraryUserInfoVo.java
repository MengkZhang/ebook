package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 图书馆管理员的登录信息
 * Created by ZhiqiangJia on 2017-10-26.
 */
public class LibraryUserInfoVo {

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
        @SerializedName("hallCode")
        @Expose
        public String hallCode;
        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("menus")
        @Expose
        public List<ResponseMenus> menus;
        @SerializedName("username")
        @Expose
        public String username;
        @SerializedName("isLogined")
        @Expose
        public int isLogined;
        @SerializedName("library")
        @Expose
        public ResponseLibrary library;
    }

    public class ResponseLibrary {
        @SerializedName("conperson")
        @Expose
        public String conperson;        //管理员联系人

        @SerializedName("customerId")
        @Expose
        public String customerId;       //客户id

        @SerializedName("depositPriority")
        @Expose
        public int depositPriority;     //押金使用优先级(1:优先使用共享押金,2:优先使用馆押金)

        @SerializedName("hallCode")
        @Expose
        public String hallCode;         //馆号

        @SerializedName("areaAddress")
        @Expose
        public String areaAddress;      //推送地址

//        @SerializedName("haveAgreement")
//        @Expose
//        public int haveAgreement;       //是否有协议 0：否 1：是

        @SerializedName("libraryLevelName")
        @Expose
        public String libraryLevelName;  //图书馆等级

        @SerializedName("needDeposit")
        @Expose
        public int needDeposit;         //借书是否需要押金。0：否 1：是

        @SerializedName("mBorrowNum")
        @Expose
        public int mBorrowNum;          //本馆最大借阅数

        @SerializedName("libraryStatus")
        @Expose
        public ResponseStatus libraryStatus;// 1:正常 2:屏蔽 3:停用

        @SerializedName("name")
        @Expose
        public String name;             //图书馆名称

        @SerializedName("readerLimit")
        @Expose
        public int readerLimit;         //当前是否限制馆0 非限制馆 1 限制馆

        @SerializedName("haveRefundAccount")//是否绑定了退款账号
        @Expose
        public boolean haveRefundAccount;

        @SerializedName("customerAgreement")
        @Expose
        public EnumInfoVo customerAgreement;
    }

    public class ResponseStatus {
        @SerializedName("index")
        @Expose
        public int index;               // 1:正常 2:屏蔽 3:停用
    }

    public class ResponseMenus {
        @SerializedName("id")
        @Expose
        public int id;
        @SerializedName("isShow")
        @Expose
        public int isShow;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("permission")
        @Expose
        public String permission;
    }

}
