package com.tzpt.cloundlibrary.manager.bean;

import com.tzpt.cloundlibrary.manager.modle.enums.EditTextType;

/**
 * 统计选项
 * Created by ZhiqiangJia on 2017-04-17.
 */
public class OptionBean {

    public int id;
    public String name;                              //名称
    public String key;                               //提交key值
    public EditTextType editTextType;                //输入框类型
    public int paramsType;                           //参数控件类型 0 日期 1两个value 2 一个value 3 选择值

    public OptionBean(int id, String name,
                      String key,
                      EditTextType editTextType,
                      int paramsType) {
        this.id = id;
        this.name = name;
        this.key = key;
        this.paramsType = paramsType;
        this.editTextType = editTextType;
    }
}
