package com.tzpt.cloudlibrary.ui.video;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.bean.VideoSetBean;

import java.util.List;

import rx.functions.Action1;

/**
 * 视频列表
 * Created by tonyjia on 2018/6/21.
 */
public interface VideoCollectionListContract {

    interface View extends BaseContract.BaseView {

        void setVideoList(List<VideoSetBean> videoList, boolean refresh);

        void setVideoEmptyList(boolean refresh);

        void setNetError(boolean refresh);

        void showDelProgress();

        void dismissDelProgress();

        void showErrorMsg(int resId);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void getCollectVideoSetList(int pageNo, boolean isDelComplete);

        void cancelCollectionVideoList(List<Long> videoIdList);

        void setEditorAble(boolean hasAble);

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
