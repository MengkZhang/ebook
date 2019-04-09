package com.tzpt.cloudlibrary.ui.video;

import android.support.v4.content.ContextCompat;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseListFragment;
import com.tzpt.cloudlibrary.bean.VideoSetBean;
import com.tzpt.cloudlibrary.rxbus.event.VideoWatchCountEvent;
import com.tzpt.cloudlibrary.utils.DpPxUtils;

import java.util.List;

import rx.functions.Action1;

/**
 * 视频列表
 * Created by tonyjia on 2018/6/21.
 */
public class VideoListFragment extends BaseListFragment<VideoSetBean> implements
        VideoListContract.View {

    public VideoListFragment() {
    }

    public static VideoListFragment newInstance() {
        return new VideoListFragment();
    }

    private VideoListContract.Presenter mPresenter;
    private int mCurrentPage = 1;
    private boolean mIsPrepared;
    private boolean mIsFirstLoad = true;
    private boolean mIsSearchVideoSet = false;
    private int mClickPositionTag = -1;

    @Override
    public void setPresenter(VideoListContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_video_list;
    }

    @Override
    public void initDatas() {
        //监听列表观看次数更改
        if (null != mPresenter) {
            mPresenter.registerRxBus(VideoWatchCountEvent.class, new Action1<VideoWatchCountEvent>() {
                @Override
                public void call(VideoWatchCountEvent videoWatchCountEvent) {
                    if (mClickPositionTag > -1 && videoWatchCountEvent.mAddWatchCount) {
                        int watchTime = mAdapter.getItem(mClickPositionTag).getWatchTimes();
                        mAdapter.getItem(mClickPositionTag).setWatchTimes(watchTime + 1);
                        mAdapter.notifyItemChanged(mClickPositionTag);
                    }
                }
            });
        }
    }

    @Override
    public void configViews() {
        mAdapter = new VideoListAdapter(getSupportActivity());
        initAdapter(true, true);
        mIsPrepared = true;
        lazyLoad();
    }

    @Override
    protected void lazyLoad() {
        if (!mIsVisible || !mIsPrepared) {
            return;
        }
        if (null != mPresenter && mIsFirstLoad) {
            this.mIsFirstLoad = false;
            mPresenter.getVideoList(1);
        }
    }

    @Override
    public void onRefresh() {
        mPresenter.getVideoList(1);
    }

    @Override
    public void onLoadMore() {
        mPresenter.getVideoList(mCurrentPage + 1);
    }

    @Override
    public void onItemClick(int position) {
        mClickPositionTag = position;
        VideoSetBean item = mAdapter.getItem(position);
        if (null != item) {
            if (mIsSearchVideoSet) {
                mPresenter.saveSearchBrowseRecord(item.getId());
            }
            VideoDetailActivity.startActivity(getSupportActivity(), item.getId(), item.getTitle(), mPresenter.getLibCode());
        }
    }

    //<editor-fold desc="视频列表回调UI">
    @Override
    public void setVideoList(List<VideoSetBean> videoList, int totalCount, boolean refresh) {
        mRecyclerView.setRefreshing(false);
        if (refresh) {
            mCurrentPage = 1;
            mAdapter.clear();
        } else {
            mCurrentPage = mCurrentPage + 1;
        }
        mAdapter.addAll(videoList);
        mRecyclerView.showToastTv(String.valueOf(mAdapter.getCount()));
    }

    @Override
    public void setVideoEmptyList(boolean refresh) {
        mRecyclerView.setRefreshing(false);
        if (refresh) {
            mAdapter.clear();
            mRecyclerView.showEmpty();
        } else {
            mAdapter.stopMore();
        }
    }

    @Override
    public void setNetError(boolean refresh) {
        mRecyclerView.setRefreshing(false);
        if (refresh) {
            mAdapter.clear();
            mRecyclerView.showError();
            mRecyclerView.setRetryRefreshListener(this);
        } else {
            mAdapter.pauseMore();
        }
    }

    @Override
    public void mustShowProgressLoading() {
        mAdapter.clear();
        mRecyclerView.showProgress();
    }

    @Override
    public void isSearchVideoSetList() {
        this.mIsSearchVideoSet = true;
    }

    //处理分类数据
    public void dealClassifyClick(int parentClassifyId, int childClassifyId) {
        if (null != mPresenter && null != mAdapter) {
            mAdapter.clear();
            mRecyclerView.showProgress();
            mPresenter.setCategoryId(parentClassifyId, childClassifyId);
            mPresenter.getVideoList(1);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        UmengHelper.setPageStart("VideoListFragment");
        mClickPositionTag = -1;
    }

    @Override
    public void onPause() {
        UmengHelper.setPageEnd("VideoListFragment");
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != mAdapter) {
            mAdapter.clear();
        }
        if (null != mPresenter) {
            mPresenter.unregisterRxBus();
            mPresenter.detachView();
        }
    }

}
