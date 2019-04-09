package com.tzpt.cloudlibrary.bean;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * 图书详情-排架号信息
 * Created by ZhiqiangJia on 2017-08-14.
 */
public class BookInLibInfo implements Serializable, Comparable<BookInLibInfo> {
    public String mLibBookId;
    public String mBarNumber;               //条码号
    public int mStoreRoom;                  //库位(基藏库、限制库)1 基藏库不外借 2表示限制库 3其他通用库
    public String mStoreRoomName;           //库位名称
    public String mBelongLibCode;           //所在馆馆号
    public String mShelvingCode;            //排架号信息
    public int mBookStatus;                 //图书状态 0:在借 1:在馆 2:当前登录账户预约 3:其他账户预约
    public int mBookStatusForSort;          //排序 0:基藏-不可借 1:不可预约 2:在借 3.其他人已预约 4.自己已预约 5.可预约
    public boolean mIsDeposit;              //押金状态true需要押金false不需要押金
    public String mCallNumber;              //设置索书号
    public boolean mCanAppoint;             //是否有预约功能

    @Override
    public int compareTo(@NonNull BookInLibInfo o) {
        if (this.mBookStatusForSort <= o.mBookStatusForSort) {
            return 1;
        } else {
            return -1;
        }
    }
}
