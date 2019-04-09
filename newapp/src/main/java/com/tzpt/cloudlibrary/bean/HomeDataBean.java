package com.tzpt.cloudlibrary.bean;

import com.tzpt.cloudlibrary.business_bean.BookBean;
import com.tzpt.cloudlibrary.business_bean.EBookBean;
import com.tzpt.cloudlibrary.business_bean.LibraryBean;

import java.util.List;

/**
 * 首页混合列表
 * Created by tonyjia on 2018/10/23.
 */
public class HomeDataBean {

    public List<LibraryBean> libraryBeanList;
    public List<BookBean> bookBeanList;
    public List<EBookBean> eBookBeanList;
    public List<VideoSetBean> videoSetBeanList;
    public List<ActionInfoBean> activityBeanList;
    public List<InformationBean> informationBeanList;
}
