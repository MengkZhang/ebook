package com.tzpt.cloudlibrary.bean;

/**
 * Created by Administrator on 2018/3/31.
 */

public class TabMenuBean {
    private String mTitle;
    private int mCount;

    public TabMenuBean(String title){
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setCount(int count) {
        this.mCount = count;
    }

    public int getCount() {
        return mCount;
    }
}
