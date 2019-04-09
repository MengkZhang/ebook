package com.tzpt.cloudlibrary.bean;

/**
 * 自助借书
 * Created by ZhiqiangJia on 2017-08-31.
 */

public class SelfBookInfoBean {

    public long id;
    public String belongLibraryHallCode;
    public String stayLibraryHallCode;
    public String barNumber;
    public String properTitle;
    public double price;
    public double discountPrice;    //折扣价
    public double attachPrice;
    public int deposit;
    public boolean statusSuccess;   //提交借阅后书籍的状态
    public String statusDesc;       //提交借阅后书籍的状态描述

    @Override
    public boolean equals(Object obj) {
        if (((SelfBookInfoBean) obj).id == this.id) {
            return true;
        }
        if (belongLibraryHallCode != null) {
            if (((SelfBookInfoBean) obj).belongLibraryHallCode.equals(this.belongLibraryHallCode)
                    && ((SelfBookInfoBean) obj).barNumber.equals(this.barNumber)) {
                return true;
            }
        }
        return super.equals(obj);
    }
}
