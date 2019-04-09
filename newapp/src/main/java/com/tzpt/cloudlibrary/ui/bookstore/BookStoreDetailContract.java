package com.tzpt.cloudlibrary.ui.bookstore;

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
 * 图书馆详情
 */
public interface BookStoreDetailContract {

    interface View extends BaseContract.BaseView {

        void setPresenter(Presenter presenter);

        void showBannerInfoList(List<BannerInfo> bannerInfoList);

        void showBannerInfoError();

        void showLibModelList(List<ModelMenu> modelList);

        void showLibModelError();

        void showLibProgress();

        void setContentView();

        void needLogin();

        void showChangeAttentionTip(String attentionLib);

        void showToastTip(int msgId);

        void changeAttentionStatus(boolean isAttention);

        void setBookStoreInfo(LibraryBean libInfo);

        void showTodayOpenTime(String openInfo);

        void showCancelAttentionTip();

        void setPaperBookList(List<BookBean> paperBookBeanList, int totalCount);

        void hidePaperBookList();

        void setEBookList(List<EBookBean> eBookBeanList, int totalCount);

        void hideEBookList();

        void setVideoList(List<VideoSetBean> videoBeanList, int totalCount);

        void hideVideoList();

        void setActivityList(List<ActionInfoBean> activityBeanList, int totalCount);

        void hideActivityList();

        void setInformationList(List<InformationBean> informationBeanList, int totalCount);

        void hideInformationList();

        void pleaseLoginTip();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void setStoreName(String libName);

        void setStoreCode(String libraryCode);

        void setFromSearch(int fromSearch);

        int getFromSearch();

        String getStoreCode();

        void getStoreNewsList();

        void getStoreMenuList();

        void checkAttentionStatus();

        void dealAttentionMenuItem();

        void attentionLib();

        void cancelAttentionLib();

        void clearCacheData();

    }
}
