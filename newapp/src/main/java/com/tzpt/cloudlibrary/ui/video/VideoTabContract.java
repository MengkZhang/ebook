package com.tzpt.cloudlibrary.ui.video;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.bean.ClassifyTwoLevelBean;

import java.util.List;

/**
 * 获取视频分类
 * Created by tonyjia on 2018/6/21.
 */
public interface VideoTabContract {

    interface View extends BaseContract.BaseView {

        void setVideoGradeList(List<ClassifyTwoLevelBean> videoGradeList);

        void showProgress();

        void setNetError();

        void setContentView();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void getVideoGradeList();
    }
}
