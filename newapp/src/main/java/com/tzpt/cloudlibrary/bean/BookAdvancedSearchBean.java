package com.tzpt.cloudlibrary.bean;

import java.io.Serializable;

/**
 * 图书高级搜索对象参数
 * Created by ZhiqiangJia on 2017-09-25.
 */

public class BookAdvancedSearchBean implements Serializable {

    public String bookGradeId;
    public int oneLevelCategoryId;   //电子书一级分类
    public int twoLevelCategoryId;   //电子书二级分类
    public String bookGrade;
    public String bookIsbn;
    public String bookName;
    public String bookCompany;
    public String bookYear;
    public String bookAuthor;
    public String libCode;
    public String bookBarCode;
}
