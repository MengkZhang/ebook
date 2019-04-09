package com.tzpt.cloudlibrary.base.data;

/**
 * 图书
 * Created by Administrator on 2018/10/30.
 */

public class Book {
    public long mId;            //图书编目库ID
    public long mBookId;        //图书基库图书ID
    public String mName;        //书名
    public String mIsbn;        //ISBN
    public String mBarNumber;   //条码号
    public String mCallNumber;  //设置索书号
    public String mPublishDate; //出版年
    public String mCoverImg;    //封面
    public String mSummary;     //简介
    public String mCatalog;     //目录
    public double mPrice;       //金额
    public double mFixedPrice;  //码洋
}
