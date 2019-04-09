package com.tzpt.cloudlibrary.bean;

/**
 * 图书分类
 * Created by Administrator on 2017/6/15.
 */
public class ClassifyInfo {

    public int id;
    public String name;
    public String code;

    public ClassifyInfo() {
    }

    public ClassifyInfo(int id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }
}
