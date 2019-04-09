package com.tzpt.cloudlibrary.business_bean;

import java.util.List;

/**
 * 已购买图书
 * Created by Administrator on 2018/11/8.
 */

public class BoughtBookBean extends BaseBookBean {
    public String mBoughtTime;          //购买时间
    public double mBoughtPrice;         //购买金额
    public long mBoughtId;              //购书Id
    public boolean mIsPraised;          //点赞
    public List<ReadNoteBean> mNoteList;//笔记列表
}
