package com.tzpt.cloudlibrary.bean;

import com.tzpt.cloudlibrary.business_bean.LibraryBean;

/**
 * 总分馆
 */
public class LibraryMainBranchBean {

    public int mLevel;              //等级
    public String mGroupTitle;      //分组名称
    public LibraryBean mLibraryBean;//图书馆

    public LibraryMainBranchBean(int level, String groupTitle) {
        this.mLevel = level;
        this.mGroupTitle = groupTitle;
    }

    public LibraryMainBranchBean(int level, LibraryBean libraryBean) {
        this.mLevel = level;
        this.mLibraryBean = libraryBean;
    }
}
