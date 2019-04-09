package com.tzpt.cloudlibrary.ui.video;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.bean.VideoBean;

/**
 * 视频详情
 * Created by tonyjia on 2018/6/28.
 */
public interface CLVideoDetailContract {

    interface View extends BaseContract.BaseView {
        void setPresenter(Presenter presenter);

        void setVideoDetail(String videoTitle, String videoContent, String videoWatchTimes);

        void setVideoDetailEmptyView();

    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void getVideoDetail();

        void clearData();

    }
}
