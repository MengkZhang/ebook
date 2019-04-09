package com.tzpt.cloundlibrary.manager.bean;

import java.io.Serializable;

/**
 * 流入管理信息
 * Created by ZhiqiangJia on 2017-07-14.
 */
public class IntoManagementListBean implements Serializable {

    public String id;           //id
    public int inState;         //流入状态
    public String outCode;      //流出单号
    public String outDate;      //流出日期
    public String signPerson;   //负责人编号
    public double totalPrice;   //总价格
    public String totalSum;     //总数量
    public String userName;     //操作员名称
    public String outHallCode;  //流出馆号
    public String name;         //名称
    public String conperson;    //负责人
    public String phone;        //电话
}
