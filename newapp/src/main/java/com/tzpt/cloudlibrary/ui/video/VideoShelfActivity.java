package com.tzpt.cloudlibrary.ui.video;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.BaseListActivity;
import com.tzpt.cloudlibrary.bean.VideoBean;
import com.tzpt.cloudlibrary.bean.VideoSetBean;
import com.tzpt.cloudlibrary.modle.remote.newdownload.DownloadStatus;
import com.tzpt.cloudlibrary.rxbus.event.VideoEvent;
import com.tzpt.cloudlibrary.utils.StringUtils;
import com.tzpt.cloudlibrary.widget.CustomDialog;
import com.tzpt.cloudlibrary.widget.multistatelayout.MultiStateLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * 视频架
 */
public class VideoShelfActivity extends BaseListActivity<VideoSetBean> implements VideoShelfContract.View {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, VideoShelfActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.choose_video_all_tv)
    TextView mChooseVideoAllTv;
    @BindView(R.id.del_video_tv)
    TextView mDelVideoTv;
    @BindView(R.id.operate_video_ll)
    LinearLayout mOperateVideoLl;
    @BindView(R.id.memory_space_tip_tv)
    TextView mMemorySpaceTipTv;
    @BindView(R.id.video_set_downloading_title_tv)
    TextView mDownloadingTitleTv;
    @BindView(R.id.video_set_complete_title_tv)
    TextView mCompleteTitleTv;
    @BindView(R.id.video_set_downloading_rl)
    RelativeLayout mDownloadingRl;
    @BindView(R.id.video_set_edit_cb)
    CheckBox mEditCb;
    @BindView(R.id.video_cache_name_tv)
    TextView mDownloadingNameTv;
    @BindView(R.id.video_cache_pb)
    ProgressBar mDownloadingProBar;
    @BindView(R.id.video_cache_speed_tv)
    TextView mDownloadingSpeedTv;
    @BindView(R.id.video_cache_status_tv)
    TextView mDownloadingStatusTv;
    @BindView(R.id.video_cache_load_total_tv)
    TextView mDownloadingLoadTotalTv;
    @BindView(R.id.multi_state_layout)
    MultiStateLayout mMultiStateLayout;
    @BindView(R.id.video_downloading_count_tv)
    TextView mVideoDownloadingCountTv;

    private VideoShelfPresenter mPresenter;

    private boolean mIsShowingDownloading;

    private VideoBean mCurrentDownloadingVideo;

    @OnClick({R.id.titlebar_left_btn, R.id.titlebar_right_txt_btn, R.id.choose_video_all_tv,
            R.id.del_video_tv, R.id.video_set_downloading_rl})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.titlebar_right_txt_btn:
                if (!((LocalVideoSetAdapter) mAdapter).isEditMode()) {
                    mCommonTitleBar.setRightBtnText("取消");
                    ((LocalVideoSetAdapter) mAdapter).setEditMode(true);
                    mOperateVideoLl.setVisibility(View.VISIBLE);
                    if (mIsShowingDownloading) {
                        mEditCb.setVisibility(View.VISIBLE);
                    }
                } else {
                    ((LocalVideoSetAdapter) mAdapter).checkAllOrNone(false);
                    mCommonTitleBar.setRightBtnText("编辑");
                    ((LocalVideoSetAdapter) mAdapter).setEditMode(false);
                    mOperateVideoLl.setVisibility(View.GONE);
                    if (mIsShowingDownloading) {
                        mEditCb.setVisibility(View.GONE);
                    }
                    mEditCb.setChecked(false);
                }
                break;
            case R.id.choose_video_all_tv:
                if (((LocalVideoSetAdapter) mAdapter).isAllChoose() && (!mIsShowingDownloading || mEditCb.isChecked())) {
                    ((LocalVideoSetAdapter) mAdapter).checkAllOrNone(false);
                    if (mIsShowingDownloading) {
                        mEditCb.setChecked(false);
                    }
                } else {
                    ((LocalVideoSetAdapter) mAdapter).checkAllOrNone(true);
                    if (mIsShowingDownloading) {
                        mEditCb.setChecked(true);
                    }
                }
                break;
            case R.id.del_video_tv:
                List<Long> idList = new ArrayList<>();
                for (int i = ((LocalVideoSetAdapter) mAdapter).mSparseItemChecked.size() - 1; i >= 0; i--) {
                    int position = ((LocalVideoSetAdapter) mAdapter).mSparseItemChecked.keyAt(i);
                    if (((LocalVideoSetAdapter) mAdapter).mSparseItemChecked.valueAt(i)) {
                        idList.add(mAdapter.getItem(position).getId());
                        mAdapter.remove(position);
                        ((LocalVideoSetAdapter) mAdapter).mSparseItemChecked.delete(position);
                    }
                }
                if (idList.size() > 0 && mIsShowingDownloading && mEditCb.isChecked()) {
                    mPresenter.delVideoSetAndAllDownloadingVideo(idList);
                } else if (idList.size() > 0) {
                    mPresenter.delVideoSet(idList);
                } else if (mIsShowingDownloading && mEditCb.isChecked()) {
                    mPresenter.delAllDownloadingVideo();
                }
                break;
            case R.id.video_set_downloading_rl:
                if (((LocalVideoSetAdapter) mAdapter).isEditMode()) {
                    mEditCb.setChecked(!mEditCb.isChecked());
                    dealChecked();
                } else {
                    VideoCacheListActivity.startActivity(this);
                }
                break;
        }
    }


    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_video_shelf;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initToolBar();
        initDatas();
        configViews();
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setTitle("视频架");
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setRightBtnText("编辑");
        mCommonTitleBar.setRightTxtBtnVisibility(View.GONE);
    }

    @Override
    public void initDatas() {
        mPresenter = new VideoShelfPresenter();
        mPresenter.attachView(this);
        mPresenter.registerRxBus(VideoBean.class, new Action1<VideoBean>() {
            @Override
            public void call(VideoBean videoBean) {
                if (mCurrentDownloadingVideo != null && mCurrentDownloadingVideo.getUrl().equals(videoBean.getUrl())) {
                    mCurrentDownloadingVideo.setLoadBytes(videoBean.getLoadBytes());
                    mCurrentDownloadingVideo.setTotalBytes(videoBean.getTotalBytes());
                    mCurrentDownloadingVideo.setStatus(videoBean.getStatus());
                    mCurrentDownloadingVideo.setLoadSpeed(videoBean.getLoadSpeed());

                    refreshDownloadingUI(mCurrentDownloadingVideo);
                }
                if (videoBean.getStatus() == DownloadStatus.COMPLETE) {
                    mPresenter.getLocalVideoSet();
                }
            }
        });
        mPresenter.registerRxBus(VideoEvent.class, new Action1<VideoEvent>() {
            @Override
            public void call(VideoEvent videoEvent) {
                dealChecked();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.getLocalVideoSet();
    }

    private void dealChecked() {
        int count = ((LocalVideoSetAdapter) mAdapter).getCheckedCount();
        if (mEditCb.isChecked()) {
            count++;
        }
        int totalCount = mAdapter.getCount();
        if (mIsShowingDownloading) {
            totalCount++;
        }

        if (count > 0) {
            mDelVideoTv.setClickable(true);
            mDelVideoTv.setTextColor(getResources().getColor(R.color.color_ee7853));
            mDelVideoTv.setText(getString(R.string.del_count, count));
        } else {
            mDelVideoTv.setClickable(false);
            mDelVideoTv.setTextColor(getResources().getColor(R.color.color_80ee7853));
            mDelVideoTv.setText("删除");
        }

        if (count == totalCount) {
            mChooseVideoAllTv.setText("取消全选");
        } else {
            mChooseVideoAllTv.setText("全选");
        }
        if (totalCount == 0) {
            mCommonTitleBar.setRightTxtBtnVisibility(View.GONE);
        }
    }

    @Override
    public void configViews() {
        mAdapter = new LocalVideoSetAdapter(this);
        initAdapter(false, false);
    }

    @Override
    public void onItemClick(int position) {
        VideoSetBean item = mAdapter.getItem(position);
        VideoCacheListActivity.startActivity(this, item.getId(), item.getCoverImg(), true);
    }

    private void setDownloading(int count, VideoBean video) {
        mIsShowingDownloading = true;
        mDownloadingRl.setVisibility(View.VISIBLE);
        mDownloadingTitleTv.setVisibility(View.VISIBLE);
        mVideoDownloadingCountTv.setText(String.valueOf(count));

        mCurrentDownloadingVideo = video;
        refreshDownloadingUI(video);
    }

    private void refreshDownloadingUI(VideoBean video) {
        switch (video.getStatus()) {
            case DownloadStatus.WAIT:
                mDownloadingStatusTv.setText("等待下载");
                mDownloadingNameTv.setText(video.getName());
                mDownloadingProBar.setVisibility(View.GONE);
                mDownloadingSpeedTv.setVisibility(View.GONE);

                mDownloadingStatusTv.setVisibility(View.VISIBLE);
                break;
            case DownloadStatus.CONNECTING:
                mDownloadingStatusTv.setText("正在连接");
                mDownloadingNameTv.setText(video.getName());
                mDownloadingProBar.setVisibility(View.GONE);
                mDownloadingSpeedTv.setVisibility(View.GONE);

                mDownloadingStatusTv.setVisibility(View.VISIBLE);
                break;
            case DownloadStatus.START:
                mDownloadingStatusTv.setText("开始下载");
                mDownloadingNameTv.setText(video.getName());
                mDownloadingProBar.setVisibility(View.GONE);
                mDownloadingSpeedTv.setVisibility(View.GONE);

                mDownloadingStatusTv.setVisibility(View.VISIBLE);
                break;
            case DownloadStatus.ERROR:
                mDownloadingStatusTv.setText("下载失败");
                mDownloadingNameTv.setText(video.getName());
                mDownloadingProBar.setVisibility(View.GONE);
                mDownloadingSpeedTv.setVisibility(View.GONE);
                mDownloadingLoadTotalTv.setVisibility(View.GONE);

                mDownloadingStatusTv.setVisibility(View.VISIBLE);
                break;
            case DownloadStatus.STOP:
                mDownloadingStatusTv.setText("暂停");
                mDownloadingNameTv.setText(video.getName());
                mDownloadingProBar.setVisibility(View.GONE);
                mDownloadingSpeedTv.setVisibility(View.GONE);
                mDownloadingLoadTotalTv.setVisibility(View.GONE);

                mDownloadingStatusTv.setVisibility(View.VISIBLE);
                break;
            case DownloadStatus.DOWNLOADING:
                mDownloadingNameTv.setText(video.getName());
                mDownloadingProBar.setVisibility(View.VISIBLE);
                mDownloadingProBar.setMax((int) video.getTotalBytes());
                mDownloadingProBar.setProgress((int) video.getLoadBytes());

                mDownloadingLoadTotalTv.setText(getString(R.string.download_load_total_size,
                        StringUtils.convertStorageNoB(video.getLoadBytes()),
                        StringUtils.convertStorageNoB(video.getTotalBytes())));

                mDownloadingSpeedTv.setVisibility(View.VISIBLE);
                mDownloadingSpeedTv.setText(video.getLoadSpeed());

                mDownloadingStatusTv.setVisibility(View.GONE);
                break;
            case DownloadStatus.COMPLETE:
                mCurrentDownloadingVideo = null;
                break;
            case DownloadStatus.MOBILE_NET_ERROR:
                mDownloadingStatusTv.setText("运营商网络暂停下载");
                mDownloadingNameTv.setText(video.getName());
                mDownloadingProBar.setVisibility(View.GONE);
                mDownloadingSpeedTv.setVisibility(View.GONE);
                mDownloadingLoadTotalTv.setVisibility(View.GONE);

                mDownloadingStatusTv.setVisibility(View.VISIBLE);
                break;
            case DownloadStatus.NO_NET_ERROR:
                mDownloadingStatusTv.setText("搜索网络...");
                mDownloadingNameTv.setText(video.getName());
                mDownloadingProBar.setVisibility(View.GONE);
                mDownloadingSpeedTv.setVisibility(View.GONE);
                mDownloadingLoadTotalTv.setVisibility(View.GONE);

                mDownloadingStatusTv.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void hideDownloadingUI() {
        mIsShowingDownloading = false;
        mDownloadingRl.setVisibility(View.GONE);
        mDownloadingTitleTv.setVisibility(View.GONE);
    }

    private void setVideoSetData(List<VideoSetBean> data) {
        mCompleteTitleTv.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mAdapter.clear();
        mAdapter.addAll(data);
        mAdapter.notifyDataSetChanged();
        mAdapter.stopMore();
    }

    private void hideVideoSetView() {
        mCompleteTitleTv.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void showAllData(List<VideoSetBean> data, int count, VideoBean video) {
        mCommonTitleBar.setRightTxtBtnVisibility(View.VISIBLE);
        mMultiStateLayout.showContentView();

        setVideoSetData(data);
        setDownloading(count, video);
    }

    @Override
    public void showDownloading(int count, VideoBean video) {
        mCommonTitleBar.setRightTxtBtnVisibility(View.VISIBLE);
        mMultiStateLayout.showContentView();
        hideVideoSetView();

        setDownloading(count, video);
    }

    @Override
    public void showVideoSetView(List<VideoSetBean> data) {
        mCommonTitleBar.setRightTxtBtnVisibility(View.VISIBLE);
        mMultiStateLayout.showContentView();
        hideDownloadingUI();

        setVideoSetData(data);
    }

    @Override
    public void showEmptyView() {
        hideDownloadingUI();
        hideVideoSetView();

        mCommonTitleBar.setRightTxtBtnVisibility(View.GONE);
        mMultiStateLayout.showEmpty();
    }

    @Override
    public void setStorageInfo(String downSize, String surplusSize) {
        mMemorySpaceTipTv.setText(getString(R.string.download_memory_space_tip, downSize, surplusSize));
    }

    @Override
    public void setUnableEdit() {
        ((LocalVideoSetAdapter) mAdapter).checkAllOrNone(false);
        mCommonTitleBar.setRightBtnText("编辑");
        ((LocalVideoSetAdapter) mAdapter).setEditMode(false);
        mOperateVideoLl.setVisibility(View.GONE);
        if (mIsShowingDownloading) {
            mEditCb.setVisibility(View.GONE);
        }
        mEditCb.setChecked(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.clearMemoryCache();
        mPresenter.unregisterRxBus();
        mPresenter.detachView();
    }
}
