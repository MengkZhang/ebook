package com.tzpt.cloudlibrary.ui.video;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tzpt.cloudlibrary.CloudLibraryApplication;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.BaseListActivity;
import com.tzpt.cloudlibrary.bean.VideoBean;
import com.tzpt.cloudlibrary.modle.remote.newdownload.DownloadStatus;
import com.tzpt.cloudlibrary.rxbus.event.VideoEvent;
import com.tzpt.cloudlibrary.ui.account.setting.DownloadSettingActivity;
import com.tzpt.cloudlibrary.ui.main.AppUpdateManager;
import com.tzpt.cloudlibrary.ui.permissions.Permission;
import com.tzpt.cloudlibrary.ui.permissions.RxPermissions;
import com.tzpt.cloudlibrary.utils.DpPxUtils;
import com.tzpt.cloudlibrary.utils.ToastUtils;
import com.tzpt.cloudlibrary.widget.CustomDialog;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.ItemView;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * 已下载/下载中视频
 * Created by Administrator on 2018/7/2.
 */

public class VideoCacheListActivity extends BaseListActivity<VideoBean> implements VideoCacheListContract.View {
    private static final String COMPLETE_VIDEO_LIST = "complete_video_list";
    private static final String VIDEO_SET_ID = "video_set_id";
    private static final String VIDEO_SET_IMAGE = "video_set_image";

    public static void startActivity(Context context, long setId, String coverImg, boolean isComplete) {
        Intent intent = new Intent(context, VideoCacheListActivity.class);
        intent.putExtra(COMPLETE_VIDEO_LIST, isComplete);
        intent.putExtra(VIDEO_SET_ID, setId);
        intent.putExtra(VIDEO_SET_IMAGE, coverImg);
        context.startActivity(intent);
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, VideoCacheListActivity.class);
        intent.putExtra(COMPLETE_VIDEO_LIST, false);
        context.startActivity(intent);
    }

    @BindView(R.id.operate_video_ll)
    LinearLayout mOperateVideoLl;
    @BindView(R.id.choose_video_all_tv)
    TextView mChooseVideoAllTv;
    @BindView(R.id.del_video_tv)
    TextView mDelVideoTv;
    @BindView(R.id.memory_space_tip_tv)
    TextView mMemorySpaceTipTv;
    @BindView(R.id.video_download_more_ll)
    LinearLayout mVideoDownloadMoreLl;
    @BindView(R.id.video_cache_operate_all_ll)
    LinearLayout mVideoCacheOperateAllLl;
    @BindView(R.id.video_cache_operate_all_tv)
    TextView mVideoCacheOperateAllTv;

    private VideoCacheListPresenter mPresenter;
    private boolean mIsCompleteType;
    private long mVideoSetId;

    @OnClick({R.id.titlebar_left_btn, R.id.titlebar_right_txt_btn, R.id.choose_video_all_tv,
            R.id.del_video_tv, R.id.video_download_more_tv, R.id.video_cache_operate_all_tv})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.titlebar_right_txt_btn:
                if (!((BaseVideoDLAdapter) mAdapter).isEditMode()) {
                    mCommonTitleBar.setRightBtnText("取消");
                    ((BaseVideoDLAdapter) mAdapter).setEditMode(true);
                    mOperateVideoLl.setVisibility(View.VISIBLE);
                } else {
                    ((BaseVideoDLAdapter) mAdapter).checkAllOrNone(false);
                    mCommonTitleBar.setRightBtnText("编辑");
                    ((BaseVideoDLAdapter) mAdapter).setEditMode(false);
                    mOperateVideoLl.setVisibility(View.GONE);
                }
                break;
            case R.id.choose_video_all_tv:
                if (((BaseVideoDLAdapter) mAdapter).isAllChecked()) {
                    ((BaseVideoDLAdapter) mAdapter).checkAllOrNone(false);
                } else {
                    ((BaseVideoDLAdapter) mAdapter).checkAllOrNone(true);
                }
                break;
            case R.id.del_video_tv:
                List<VideoBean> list = new ArrayList<>();
                for (int i = ((BaseVideoDLAdapter) mAdapter).mSparseItemChecked.size() - 1; i >= 0; i--) {
                    int position = ((BaseVideoDLAdapter) mAdapter).mSparseItemChecked.keyAt(i);
                    if (((BaseVideoDLAdapter) mAdapter).mSparseItemChecked.valueAt(i)) {
                        list.add(mAdapter.getItem(position));
                        mAdapter.remove(position);
                        ((BaseVideoDLAdapter) mAdapter).mSparseItemChecked.delete(position);
                    }
                }
                if (mIsCompleteType) {
                    mPresenter.delCompleteVideo(mVideoSetId, list);
                } else {
                    mPresenter.delDownloadingVideo(list);
                }
                break;
            case R.id.video_download_more_tv:
                VideoDownloadChooseActivity.startActivity(this, mVideoSetId);
                break;
            case R.id.video_cache_operate_all_tv:
                mPresenter.dealAllDownloadItem();
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
        return R.layout.activity_video_cache_list;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setRightBtnText("编辑");
        mCommonTitleBar.setRightTxtBtnVisibility(View.GONE);
    }

    @Override
    public void initDatas() {
        mIsCompleteType = getIntent().getBooleanExtra(COMPLETE_VIDEO_LIST, false);
        mPresenter = new VideoCacheListPresenter(CloudLibraryApplication.mRxBus);
        mPresenter.attachView(this);

        if (mIsCompleteType) {
            mVideoDownloadMoreLl.setVisibility(View.VISIBLE);
            mCommonTitleBar.setTitle("已下载");
            mVideoSetId = getIntent().getLongExtra(VIDEO_SET_ID, 0);
            mPresenter.getVideoCompleteList(mVideoSetId);
            mVideoCacheOperateAllLl.setVisibility(View.GONE);
        } else {
            mPresenter.getVideoCacheList();
            mVideoDownloadMoreLl.setVisibility(View.GONE);
            mCommonTitleBar.setTitle("下载中");
            mVideoCacheOperateAllLl.setVisibility(View.VISIBLE);
        }

        mPresenter.getStorageInfo();
        mPresenter.registerRxBus(VideoBean.class, new Action1<VideoBean>() {
            @Override
            public void call(VideoBean videoBean) {
                ((VideoCacheAdapter) mAdapter).updateDownload(videoBean);
                if (videoBean.getStatus() != DownloadStatus.DOWNLOADING) {
                    mPresenter.checkVideoCacheStatus();
                }
                if (videoBean.getStatus() == DownloadStatus.COMPLETE
                        && mIsCompleteType) {
                    mPresenter.getStorageInfo();
                    mPresenter.getVideoCompleteList(mVideoSetId);
                }
            }
        });
        mPresenter.registerRxBus(VideoEvent.class, new Action1<VideoEvent>() {
            @Override
            public void call(VideoEvent videoEvent) {
                int count = videoEvent.getCount();
                if (count > 0) {
                    mDelVideoTv.setClickable(true);
                    mDelVideoTv.setTextColor(getResources().getColor(R.color.color_ee7853));
                    mDelVideoTv.setText(getString(R.string.del_count, count));
                } else {
                    mDelVideoTv.setClickable(false);
                    mDelVideoTv.setTextColor(getResources().getColor(R.color.color_80ee7853));
                    mDelVideoTv.setText("删除");
                }

                setChooseVideoAllStatus();
                if (mAdapter.getCount() == 0) {
                    showEmptyList();
                    mCommonTitleBar.setRightTxtBtnVisibility(View.GONE);
                }
            }
        });
    }

    private void setChooseVideoAllStatus() {
        if (((BaseVideoDLAdapter) mAdapter).isAllChecked()) {
            mChooseVideoAllTv.setText("取消全选");
        } else {
            mChooseVideoAllTv.setText("全选");
        }
    }

    @Override
    public void configViews() {
        if (mIsCompleteType) {
            mAdapter = new VideoCompleteAdapter(this);
            initAdapter(false, false);
            mAdapter.addHeader(new HeaderView());
        } else {
            mAdapter = new VideoCacheAdapter(this);
            initAdapter(false, false);
            mRecyclerView.setDividerDrawable(R.drawable.divider_rv_vertical_default);
        }
    }

    @Override
    public void onItemClick(int position) {
        if (((BaseVideoDLAdapter) mAdapter).isEditMode()) {
            return;
        }
        if (mIsCompleteType) {
            VideoBean item = mAdapter.getItem(position);
            if (TextUtils.isEmpty(item.getPath())) {
                ToastUtils.showSingleToast(R.string.local_video_not_exist);
                return;
            }
            File file = new File(item.getPath());
            if (!file.exists()) {
                ToastUtils.showSingleToast(R.string.local_video_not_exist);
                return;
            }
            String imagePath = getIntent().getStringExtra(VIDEO_SET_IMAGE);
            VideoPlayActivity.startActivity(this, item.getSetId(), imagePath, position);
        } else {
            VideoBean item = mAdapter.getItem(position);
            mPresenter.dealDownloadItem(item);
        }

    }

    @Override
    public void setData(List<VideoBean> list) {
        mCommonTitleBar.setRightTxtBtnVisibility(View.VISIBLE);
        mAdapter.clear();
        mAdapter.addAll(list);
        mAdapter.notifyDataSetChanged();
        mAdapter.stopMore();
        setChooseVideoAllStatus();
    }

    @Override
    public void showEmptyList() {
        mCommonTitleBar.setRightTxtBtnVisibility(View.GONE);
        mOperateVideoLl.setVisibility(View.GONE);
        mAdapter.clear();
        mRecyclerView.showEmpty();
        if (!mIsCompleteType) {
            mVideoCacheOperateAllLl.setVisibility(View.GONE);
        }
    }

    @Override
    public void setStorageInfo(String downSize, String surplusSize) {
        mMemorySpaceTipTv.setText(getString(R.string.download_memory_space_tip, downSize, surplusSize));
    }

    @Override
    public void setAllDownloadBtnStatus(boolean hasDownloadingItem) {
        if (hasDownloadingItem) {
            Drawable stop = getResources().getDrawable(R.mipmap.ic_video_download_stop_all);
            stop.setBounds(0, 0, stop.getMinimumWidth(), stop.getMinimumHeight());
            mVideoCacheOperateAllTv.setCompoundDrawables(stop, null, null, null);
            mVideoCacheOperateAllTv.setText("全部暂停");
        } else {
            Drawable start = getResources().getDrawable(R.mipmap.ic_video_download_start_all);
            start.setBounds(0, 0, start.getMinimumWidth(), start.getMinimumHeight());
            mVideoCacheOperateAllTv.setCompoundDrawables(start, null, null, null);
            mVideoCacheOperateAllTv.setText("全部开始");
        }
    }

    @Override
    public void setUnableEdit() {
        ((BaseVideoDLAdapter) mAdapter).checkAllOrNone(false);
        mCommonTitleBar.setRightBtnText("编辑");
        ((BaseVideoDLAdapter) mAdapter).setEditMode(false);
        mOperateVideoLl.setVisibility(View.GONE);

        mDelVideoTv.setClickable(false);
        mDelVideoTv.setTextColor(getResources().getColor(R.color.color_80ee7853));
        mDelVideoTv.setText("删除");
    }

    @Override
    public void showToastTip(int msgId) {
        ToastUtils.showSingleToast(msgId);
    }

    @Override
    public void showMobileTipDialog(int msgId, int okStrId, int cancelStrId) {
        final CustomDialog dialog = new CustomDialog(this, R.style.DialogTheme, getString(msgId));
        dialog.setCancelable(false);
        dialog.hasNoCancel(true);
        dialog.setBtnOKAndBtnCancelTxt(getString(okStrId), getString(cancelStrId));
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                mPresenter.resumeDownload();
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
                DownloadSettingActivity.startActivity(VideoCacheListActivity.this);
            }
        });
    }

    @Override
    public void checkStoragePermission() {
        AppUpdateManager.getInstance().initContext(this);
        if (Build.VERSION.SDK_INT < 23) {
            mPresenter.setPermissionOk();
            return;
        }
        if (Build.VERSION.SDK_INT >= 23) {
            RxPermissions rxPermissions = new RxPermissions(this);
            rxPermissions.setLogging(true);
            rxPermissions.requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Permission>() {
                        @Override
                        public void call(Permission permission) {
                            if (permission.granted) {//权限已授权
                                mPresenter.setPermissionOk();
                            } else {//没有权限,不能使用权限模块-去设置权限

                            }
                        }
                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
        mPresenter.unregisterRxBus();
    }

    private class HeaderView implements ItemView {
        private View mHeaderView;

        HeaderView() {
            mHeaderView = LayoutInflater.from(VideoCacheListActivity.this).inflate(R.layout.view_video_complete_header, null);
        }

        @Override
        public View onCreateView(ViewGroup parent) {
            return mHeaderView;
        }

        @Override
        public void onBindView(View headerView) {

        }
    }

}
