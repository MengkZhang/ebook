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

import rx.functions.Action1;

/**
 * Created by Administrator on 2018/8/28.
 */

public interface AttentionLibContract {
    interface View extends BaseContract.BaseView {
        void showNoAttentionLib();

        void setTitle(String title);

        void showBannerInfoList(List<BannerInfo> bannerInfoList);

        void showBannerInfoError();

        void showLibModelList(List<ModelMenu> modelList, String libCode, String libName);

        void showLibModelError();

        void showLibProgress();

        void setContentView();

        void showToastTip(int msgId);

        void showLibraryInfo(LibraryBean libInfo);

        void showTodayOpenTime(String openInfo);

        void showBusinessTime(String businessTime);

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

        /**
         * 设置关注馆或者书店介绍标题
         * @param title 标题
         */
        void setAttentionIntroduceTitle(String title);

        void pleaseLoginTip();

    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        boolean isLogin();

        void cancelAttentionLib();

        void getAttentionLibBanner();

        void getAttentionLib();

        <T> void registerRxBus(Class<T> eventType, Action1<T> action);

        void unregisterRxBus();
    }
}
