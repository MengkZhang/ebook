package com.tzpt.cloudlibrary.bean;

/**
 * Created by Administrator on 2017/11/7.
 */

public class SearchTypeBean {
    public int mSearchType;
    public String mTypeName;
    public boolean mIsSelected;

    public SearchTypeBean(int type, String name) {
        mSearchType = type;
        mTypeName = name;
    }
}
