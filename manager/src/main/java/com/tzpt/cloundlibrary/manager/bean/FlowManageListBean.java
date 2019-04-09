package com.tzpt.cloundlibrary.manager.bean;

import java.io.Serializable;

/**
 * 流出管理列表
 * Created by ZhiqiangJia on 2017-07-11.
 */
public class FlowManageListBean implements Serializable {

    public String id;               //id
    public String inHallCode;       //流入馆
    public String name;             //流入馆名称
    public int outState;            //流出状态 1未发送，2已发送，3.被否决 4.已批准5.被拒绝6.已完成 -1通还
    public double totalPrice;       //价格
    public int totalSum;            //数量
    public String outDate;          //流出日期
    public String userName;         //操作员
    public String outOperUserId;    //操作员id
    public String phone;            //电话号码
    public String auditDate;        //审核日期
    public String auditPerson;      //审核人
    public String conperson;        //负责人
    //public String operUser;         //操作员编号
    //public String outCode;          //流出单号
    public String outHallCode;      //流出馆号
    public boolean onlyRead = false; //设置为只读属性
}
