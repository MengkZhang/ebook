package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/11/14.
 */

public class SelfBorrowScanFirstVo {

    @SerializedName("bookInfo")
    @Expose
    public BookInfoVo bookInfo;

    @SerializedName("libraryInfo")
    @Expose
    public LibInfoVo libraryInfo;

    @SerializedName("readerInfo")
    @Expose
    public ReaderInfoVo readerInfo;

    @SerializedName("errorCode")
    @Expose
    public int errorCode;

    @SerializedName("message")
    @Expose
    public String message;

    public class BookInfoVo {
        @SerializedName("attachPrice")
        @Expose
        public float attachPrice;
        @SerializedName("barNumber")
        @Expose
        public String barNumber;
        @SerializedName("belongLibraryHallCode")
        @Expose
        public String belongLibraryHallCode;
        @SerializedName("deposit")
        @Expose
        public int deposit;
        @SerializedName("id")
        @Expose
        public int id;
        @SerializedName("price")
        @Expose
        public double price;
        @SerializedName("properTitle")
        @Expose
        public String properTitle;
        @SerializedName("stayLibraryHallCode")
        @Expose
        public String stayLibraryHallCode;

        @SerializedName("executeDeposit")
        @Expose
        public int executeDeposit;//是否执行本系统押金流程(1:是,0:否)

        @SerializedName("borrowDepositType")
        @Expose
        public int borrowDepositType;//-1:不需要押金办借,1:只能使用共享押金办借,2:只能用馆押金办借,3:先用共享押金后用馆押金一起
    }

    public class LibInfoVo {
        @SerializedName("borrowDays")
        @Expose
        public int borrowDays;
        @SerializedName("borrowNum")
        @Expose
        public int borrowNum;
        @SerializedName("deposit")
        @Expose
        public int deposit;
        @SerializedName("haveAgreement")
        @Expose
        public int haveAgreement;
        @SerializedName("libCode")
        @Expose
        public String libCode;
        @SerializedName("readerLimit")
        @Expose
        public int readerLimit;
        @SerializedName("rent")
        @Expose
        public double rent;
    }

    public class ReaderInfoVo {
        @SerializedName("accountPermission")
        @Expose
        public int accountPermission;           //押金账户使用权限 1:只能共享押金; 2:共享押金馆押金均可; 3:只能馆押金

        @SerializedName("availableOfflineDeposit")
        @Expose
        public double availableOfflineDeposit;  //线下可用押金(馆押金)

        @SerializedName("availableOnlineDeposit")
        @Expose
        public double availableOnlineDeposit;   //线上可用押金(共享押金)

        @SerializedName("bookSum")
        @Expose
        public int bookSum;                     //当前借书数

        @SerializedName("borrowerDeposit")
        @Expose
        public double borrowerDeposit;          //当前借书押金

        @SerializedName("canDeposit")
        @Expose
        public double canDeposit;               //可以押金

        @SerializedName("deposit")
        @Expose
        public double deposit;                  //总押金

        @SerializedName("depositPriority")
        @Expose
        public int depositPriority;             //押金账户优先级 1优先使用共享押金账户 2优先使用馆押金账户

        @SerializedName("penalty")
        @Expose
        public double penalty;                  //罚金
    }
}
