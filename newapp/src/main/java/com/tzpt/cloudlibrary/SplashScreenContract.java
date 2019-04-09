package com.tzpt.cloudlibrary;

import com.tzpt.cloudlibrary.base.BaseContract;

/**
 * Created by Administrator on 2018/3/27.
 */

public interface SplashScreenContract {
    interface View extends BaseContract.BaseView {

        /**
         * 计时结束
         */
        void timeToComplete();

        /**
         * 加载广告页图片
         *
         * @param launchImg 广告页图片
         */
        void loadAdImg(String launchImg);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void startLocation();

        void getLaunchImgData();

        /**
         * 开始计时
         *
         * @param launchImg 广告页图片
         * @param time      时间
         */
        void startTimer(final String launchImg, final int time);
    }
}
