package com.tzpt.cloudlibrary.ui.video;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.bean.VideoSetBean;

import java.util.List;

import rx.functions.Action1;

/**
 * 视频列表
 * Created by tonyjia on 2018/6/21.
 */
public interface VideoListContract {

    interface View extends BaseContract.BaseView {
        void setPresenter(Presenter presenter);

        void setVideoList(List<VideoSetBean> videoList, int totalCount, boolean refresh);

        void setVideoEmptyList(boolean refresh);

        void setNetError(boolean refresh);

        void mustShowProgressLoading();

        void isSearchVideoSetList();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void setLibCode(String libCode);

        String getLibCode();

        void setFilterType(int filterType);

        void setCategoryId(int firstCategory, int secondCategory);

        void mustShowProgressLoading();

        void setKeyWord(String keyWord);

        void getVideoList(int pageNo);

        void saveSearchBrowseRecord(long videosId);

        /**
         * 注册
         *
         * @param eventType
         * @param <T>
         */
        <T> void registerRxBus(Class<T> eventType, Action1<T> action);

        /**
         * 注销
         */
        void unregisterRxBus();
    }
}
