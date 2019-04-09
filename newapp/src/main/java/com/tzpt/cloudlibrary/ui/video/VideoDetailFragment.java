package com.tzpt.cloudlibrary.ui.video;

import android.text.TextUtils;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.BaseFragment;
import com.tzpt.cloudlibrary.widget.multistatelayout.MultiStateLayout;

import butterknife.BindView;

/**
 * 视频详情
 * Created by tonyjia on 2018/6/25.
 */
public class VideoDetailFragment extends BaseFragment implements
        CLVideoDetailContract.View {

    @BindView(R.id.multi_state_layout)
    MultiStateLayout mMultiStateLayout;
    @BindView(R.id.video_detail_title_tv)
    TextView mVideoDetailTitleTv;
    @BindView(R.id.video_detail_watch_time_tv)
    TextView mVideoDetailWatchTimeTv;
    @BindView(R.id.video_detail_content_tv)
    TextView mVideoDetailContentTv;

    private CLVideoDetailContract.Presenter mPresenter;
    private boolean mIsPrepared;
    private boolean mIsFirstLoad = true;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_video_detail;
    }

    @Override
    public void initDatas() {
        mIsPrepared = true;
        lazyLoad();
    }

    @Override
    public void configViews() {

    }

    @Override
    protected void lazyLoad() {
        if (!mIsVisible || !mIsPrepared) {
            return;
        }
        if (null != mPresenter && mIsFirstLoad) {
            this.mIsFirstLoad = false;
            mPresenter.getVideoDetail();
        }
    }

    @Override
    public void setPresenter(CLVideoDetailContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void setVideoDetail(String videoTitle, String videoContent, String videoWatchTimes) {
        mMultiStateLayout.showContentView();
        mVideoDetailTitleTv.setText(TextUtils.isEmpty(videoTitle) ? "" : videoTitle);
        mVideoDetailContentTv.setText(TextUtils.isEmpty(videoContent) ? "" : videoContent);
        mVideoDetailWatchTimeTv.setText(getString(R.string.video_watch_times, TextUtils.isEmpty(videoWatchTimes) ? "0" : videoWatchTimes));
    }

    @Override
    public void setVideoDetailEmptyView() {
        mMultiStateLayout.showEmpty();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != mPresenter) {
            mPresenter.clearData();
            mPresenter.detachView();
        }
    }
}
