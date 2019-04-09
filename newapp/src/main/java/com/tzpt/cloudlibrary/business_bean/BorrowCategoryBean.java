package com.tzpt.cloudlibrary.business_bean;

/**
 * Created by Administrator on 2018/11/5.
 */

public class BorrowCategoryBean {
    public int mCategoryId;
    public String mName;
    public boolean mIsSelected;

    public BorrowCategoryBean(int id, String name) {
        mCategoryId = id;
        mName = name;
    }
}
