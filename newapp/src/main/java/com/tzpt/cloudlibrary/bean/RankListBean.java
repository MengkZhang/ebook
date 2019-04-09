package com.tzpt.cloudlibrary.bean;

/**
 * 排行榜
 * Created by ZhiqiangJia on 2017-12-05.
 */
public class RankListBean {
    //title信息
    public int mRankType;       //排行榜类型
    public String mTitle;       //分类title

    //书籍信息
    public int mParentId;       //分类id
    public String mId;          //id
    public String mBookId;      //图书id
    public String mBookName;    //图书名称
    public String mImage;       //图书图片
    public String mIsbn;        //图书isbn
    public String mAuthor;      //图书作者
    public int mPosition;       //图书位置
}
