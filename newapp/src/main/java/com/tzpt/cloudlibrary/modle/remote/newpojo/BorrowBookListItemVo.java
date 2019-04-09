package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/11/17.
 */

public class BorrowBookListItemVo {
    @SerializedName("author")
    @Expose
    public String author;

    @SerializedName("bookId")
    @Expose
    public int bookId;

    @SerializedName("categoryId")
    @Expose
    public long categoryId;

    @SerializedName("categoryName")
    @Expose
    public String categoryName;

    @SerializedName("bookName")
    @Expose
    public String bookName;

    @SerializedName("borrowDays")
    @Expose
    public int borrowDays;

    @SerializedName("borrowTime")
    @Expose
    public String borrowTime;

    @SerializedName("borrowTimeStr")
    @Expose
    public String borrowTimeStr;

    @SerializedName("borrowerNoteId")
    @Expose
    public int borrowerNoteId;

    @SerializedName("daysRemaining")
    @Expose
    public int daysRemaining;

    @SerializedName("id")
    @Expose
    public int id;

    @SerializedName("image")
    @Expose
    public String image;

    @SerializedName("isPraiseD")
    @Expose
    public int isPraiseD;

    @SerializedName("isbn")
    @Expose
    public String isbn;

    @SerializedName("libName")
    @Expose
    public String libName;
    @SerializedName("libCode")
    @Expose
    public String libCode;

    @SerializedName("libraryBookId")
    @Expose
    public int libraryBookId;

    @SerializedName("libraryStatus")
    @Expose
    public int libraryStatus;

    @SerializedName("overDue")
    @Expose
    public int overDue;

    @SerializedName("publishDate")
    @Expose
    public String publishDate;

    @SerializedName("publisher")
    @Expose
    public String publisher;

    @SerializedName("renewTimes")
    @Expose
    public long renewTimes;

    @SerializedName("returnTime")
    @Expose
    public String returnTime;

    @SerializedName("returnTimeStr")
    public String returnTimeStr;

    @SerializedName("mayRenew")
    @Expose
    public int mayRenew; //canRenew	是否有续借功能(1:有,0:无)

    @SerializedName("borrowState")
    @Expose
    public int borrowState;

    @SerializedName("canOverduePay")
    @Expose
    public int canOverduePay;

    @SerializedName("usedDeposit")
    @Expose
    public double usedDeposit;

    @SerializedName("usedDepositType")
    @Expose
    public int usedDepositType;         //押金占用账户类型 1:共享押金; 2:馆押金; -1:没有

    @SerializedName("compensatePrice")
    @Expose
    public double compensatePrice;

    @SerializedName("borrowAgreement")
    @Expose
    public int borrowAgreement;

    @SerializedName("expirationTimeStr")
    @Expose
    public String expirationTimeStr;

}
