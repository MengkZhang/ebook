package com.tzpt.cloundlibrary.manager.bean;

/**
 * 流出管理详情图书列表对象
 * Created by ZhiqiangJia on 2017-07-11.
 */
public class FlowManageDetailBookInfoBean {

    public String id;                   //id
    public String circulateMapId;       //关联id
    public BookInfoBean mBookInfoBean;  //图书信息

    @Override
    public boolean equals(Object obj) {
        BookInfoBean bookInfoBean = ((FlowManageDetailBookInfoBean) obj).mBookInfoBean;
        if (null != mBookInfoBean && null != bookInfoBean) {
            if (null != mBookInfoBean.mBelongLibraryHallCode) {
                if (bookInfoBean.mBelongLibraryHallCode.equals(this.mBookInfoBean.mBelongLibraryHallCode)
                        && (bookInfoBean.mBarNumber.equals(this.mBookInfoBean.mBarNumber))) {
                    return true;
                }
            }
        }
        return super.equals(obj);
    }
}