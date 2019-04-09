package com.tzpt.cloudlibrary.ui.main;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.bean.ActionInfoBean;
import com.tzpt.cloudlibrary.bean.BannerInfo;
import com.tzpt.cloudlibrary.bean.InformationBean;
import com.tzpt.cloudlibrary.bean.ModelMenu;
import com.tzpt.cloudlibrary.bean.VideoSetBean;
import com.tzpt.cloudlibrary.business_bean.BookBean;
import com.tzpt.cloudlibrary.business_bean.EBookBean;
import com.tzpt.cloudlibrary.business_bean.LibraryBean;

import java.util.List;

/**
 * 首页
 * Created by tonyjia on 2018/10/17.
 */
public interface TabHomeContract {

    interface View extends BaseContract.BaseView {

        void showHomeDataProgress();

        void setBannerList(List<BannerInfo> list);

        void setBannerErr();

        void setHomeDataErr();

        void showHomeData();

        void setHomeModelList(List<ModelMenu> homeModelBeanList);

        void setNearLibraryList(List<LibraryBean> libraryBeanList);

        void hideNearLibraryList();

        void setPaperBookList(List<BookBean> paperBookBeanList);

        void hidePaperBookList();

        void setEBookList(List<EBookBean> eBookBeanList);

        void hideEBookList();

        void setVideoList(List<VideoSetBean> videoBeanList);

        void hideVideoList();

        void setActivityList(List<ActionInfoBean> activityBeanList);

        void hideActivityList();

        void setInformationList(List<InformationBean> informationBeanList);

        void hideInformationList();

        /**
         * 设置区域
         *
         * @param districtText 区域
         */
        void setDistrictText(String districtText);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void getBannerList();

        void getHomeInfoList();

    }
}
