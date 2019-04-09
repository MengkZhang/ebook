package com.tzpt.cloudlibrary.ui.video;

import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.bean.VideoSetBean;
import com.tzpt.cloudlibrary.utils.Utils;

/**
 * 视频详情
 * Created by tonyjia on 2018/6/28.
 */
public class CLVideoDetailPresenter extends RxPresenter<CLVideoDetailContract.View> implements
        CLVideoDetailContract.Presenter {

    public CLVideoDetailPresenter(CLVideoDetailContract.View view) {
        attachView(view);
        mView.setPresenter(this);
    }

    private VideoSetBean mVideoSetBean;

    @Override
    public void getVideoDetail() {
        if (null != mView) {
            //设置视频详情
            if (null != mVideoSetBean) {
                mView.setVideoDetail(mVideoSetBean.getTitle(), mVideoSetBean.getContent(), Utils.formatWatchTimes(mVideoSetBean.getWatchTimes()));
            } else {
                mView.setVideoDetailEmptyView();
            }
        }
    }

    /**
     * 设置视频详情数据
     *
     * @param videoSetBean 视频详情
     */
    public void setVideoDetail(VideoSetBean videoSetBean) {
        this.mVideoSetBean = videoSetBean;
    }

    @Override
    public void clearData() {
        if (null != mVideoSetBean) {
            mVideoSetBean = null;
        }
    }

}
