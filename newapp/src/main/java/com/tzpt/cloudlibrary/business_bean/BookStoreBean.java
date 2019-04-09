package com.tzpt.cloudlibrary.business_bean;

import com.tzpt.cloudlibrary.base.data.BookStore;
import com.tzpt.cloudlibrary.bean.ActionInfoBean;
import com.tzpt.cloudlibrary.bean.InformationBean;
import com.tzpt.cloudlibrary.bean.ModelMenu;
import com.tzpt.cloudlibrary.bean.VideoSetBean;

import java.io.Serializable;
import java.util.List;

/**
 * 书店
 * Created by tonyjia on 2018/12/6.
 */
public class BookStoreBean implements Serializable {

    public BookStore mBookStore = new BookStore();
    public int bookExist;                               //本馆是否有图书
    public int recommendExist;                          //本馆是否已推荐
    public int mDistance;                               //距离
    public boolean mIsValidLngLat;                      //定位是否精准
    public boolean mIsOpen;                             //本馆是否开放
    public List<ModelMenu> mMenuItemList;               //菜单列表
    public List<BookBean> mBookBeanList;                //图书列表
    public List<EBookBean> mEBookBeanList;              //电子书列表
    public List<VideoSetBean> mVideoSetBeanList;        //视频列表
    public List<ActionInfoBean> mActivityBeanList;      //活动列表
    public List<InformationBean> mInformationBeanList;  //资讯列表

    @Override
    public boolean equals(Object obj) {
        if (mBookStore != null) {
            if (((LibraryBean) obj).mLibrary.mCode.equals(this.mBookStore.mCode)) {
                return true;
            }
        }
        return super.equals(obj);
    }
}
