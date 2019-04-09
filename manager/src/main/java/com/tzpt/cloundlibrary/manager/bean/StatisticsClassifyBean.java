package com.tzpt.cloundlibrary.manager.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/4/12.
 */

public class StatisticsClassifyBean implements Serializable{
    private String mTitle;
    private int mId;

    public StatisticsClassifyBean(String title, int id) {
        mTitle = title;
        mId = id;
    }

    public String getTitle(){
        return mTitle;
    }

    public int getId(){
        return mId;
    }
}
