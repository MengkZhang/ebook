package com.tzpt.cloudlibrary.business_bean;

import com.tzpt.cloudlibrary.base.data.Library;
import com.tzpt.cloudlibrary.bean.ActionInfoBean;
import com.tzpt.cloudlibrary.bean.InformationBean;
import com.tzpt.cloudlibrary.bean.ModelMenu;
import com.tzpt.cloudlibrary.bean.VideoSetBean;

import java.io.Serializable;
import java.util.List;

/**
 * 图书馆信息
 * Created by tonyjia on 2018/11/2.
 */
public class LibraryBean implements Serializable {

    public Library mLibrary = new Library();
    public int bookExist;                               //本馆是否有图书
    public int recommendExist;                          //本馆是否已推荐
    public int mDistance;                               //距离
    public boolean mIsValidLngLat;                      //定位是否精准
    public boolean mIsOpen;                             //本馆是否开放
    public boolean mIsBookStore;                        //书店下存在共享书屋
    public List<ModelMenu> mMenuItemList;               //菜单列表
    public List<BookBean> mBookBeanList;                //图书列表
    public List<EBookBean> mEBookBeanList;              //电子书列表
    public List<VideoSetBean> mVideoSetBeanList;        //视频列表
    public List<ActionInfoBean> mActivityBeanList;      //活动列表
    public List<InformationBean> mInformationBeanList;  //资讯列表

    @Override
    public boolean equals(Object obj) {
        if (mLibrary != null) {
            if (((LibraryBean) obj).mLibrary.mCode.equals(this.mLibrary.mCode)) {
                return true;
            }
        }
        return super.equals(obj);
    }
}
