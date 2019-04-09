package com.tzpt.cloudlibrary.bean;

import java.util.List;

/**
 * 视频,电子书二级分类
 * Created by tonyjia on 2018/6/21.
 */
public class ClassifyTwoLevelBean {

    public int mId;                             //一级分类ID
    public String mName;                        //一级分类名称
    //public int mParentId;                       //父类ID
    //public int mSortNum;                        //排序号
    public List<ClassifyTwoLevelBean> mSubList; //二级分类

}
