package com.tzpt.cloundlibrary.manager.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 帮助信息
 * Created by ZhiqiangJia on 2018-01-08.
 */
public class HelpInfoBean implements Serializable{

    public String mItemName;
    public String mUrl;
    public boolean mHasChildren;

    public String mTitle;
    public List<HelpInfoBean> mChildren;

}
