package com.tzpt.cloudlibrary.ui.video;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.BaseListActivity;
import com.tzpt.cloudlibrary.bean.VideoBean;
import com.tzpt.cloudlibrary.bean.VideoTOCTree;
import com.tzpt.cloudlibrary.modle.remote.newdownload.DownloadStatus;
import com.tzpt.cloudlibrary.rxbus.event.VideoEvent;
import com.tzpt.cloudlibrary.ui.account.setting.DownloadSettingActivity;
import com.tzpt.cloudlibrary.ui.main.AppUpdateManager;
import com.tzpt.cloudlibrary.ui.permissions.Permission;
import com.tzpt.cloudlibrary.ui.permissions.RxPermissions;
import com.tzpt.cloudlibrary.utils.DpPxUtils;
import com.tzpt.cloudlibrary.utils.ToastUtils;
import com.tzpt.cloudlibrary.widget.CustomDialog;
import com.tzpt.cloudlibrary.widget.recyclerview.EasyRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * 视频选择下载
 * Created by Administrator on 2018/7/9.
 */

public class VideoDownloadChooseActivity extends BaseListActivity<VideoTOCTree> implements VideoDownloadChooseContract.View {
    private static final String VIDEO_SET_ID = "video_set_id";

    public static void startActivity(Context context, long setId) {
        Intent intent = new Intent(context, VideoDownloadChooseActivity.class);
        intent.putExtra(VIDEO_SET_ID, setId);
        context.startActivity(intent);
    }

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;
    @BindView(R.id.choose_video_all_tv)
    TextView mChooseVideoAllTv;
    @BindView(R.id.download_video_tv)
    TextView mDownloadVideoTv;
    @BindView(R.id.operate_video_ll)
    LinearLayout mOperateVideoLl;
    @BindView(R.id.memory_space_tip_tv)
    TextView mMemorySpaceTipTv;

    private VideoDownloadChoosePresenter mPresenter;

    @OnClick({R.id.titlebar_left_btn, R.id.titlebar_right_txt_btn, R.id.choose_video_all_tv, R.id.download_video_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.choose_video_all_tv:
                if (((VideoDownloadChooseAdapter) mAdapter).isAllChoose()) {
                    ((VideoDownloadChooseAdapter) mAdapter).checkAllOrNone(false);
                } else {
                    ((VideoDownloadChooseAdapter) mAdapter).checkAllOrNone(true);
                }
                break;
            case R.id.download_video_tv:
                initSDCardPermission();
                break;
            case R.id.titlebar_right_txt_btn:
                VideoShelfActivity.startActivity(this);
                finish();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_video_download_choose;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("选择下载");
        mCommonTitleBar.setRightBtnText("下载管理");
        //setSupportSlideBack(false);
    }

    @Override
    public void initDatas() {
        long videoSetId = getIntent().getLongExtra(VIDEO_SET_ID, 0);
        mPresenter = new VideoDownloadChoosePresenter();
        mPresenter.attachView(this);
        mPresenter.getVideoList(videoSetId);
        mPresenter.getStorageInfo();
        mPresenter.registerRxBus(VideoEvent.class, new Action1<VideoEvent>() {
            @Override
            public void call(VideoEvent videoEvent) {
                int count = videoEvent.getCount();
                if (count > 0) {
                    mDownloadVideoTv.setClickable(true);
                    mDownloadVideoTv.setTextColor(getResources().getColor(R.color.color_333333));
                } else {
                    mDownloadVideoTv.setClickable(false);
                    mDownloadVideoTv.setTextColor(getResources().getColor(R.color.color_80999999));
                }

                if (((VideoDownloadChooseAdapter) mAdapter).isAllChoose()) {
                    mChooseVideoAllTv.setText("取消全选");
                } else {
                    mChooseVideoAllTv.setText("全选");
                }
            }
        });

        mPresenter.registerRxBus(VideoBean.class, new Action1<VideoBean>() {
            @Override
            public void call(VideoBean videoBean) {
                if (videoBean.getStatus() == DownloadStatus.COMPLETE) {
                    ((VideoDownloadChooseAdapter) mAdapter).updateDownloadToComplete(videoBean);
                }
            }
        });
    }

    @Override
    public void configViews() {
        mAdapter = new VideoDownloadChooseAdapter(this);
        initAdapter(false, false);
        mRecyclerView.setDividerDrawable(R.drawable.divider_rv_vertical_default);
    }

    @Override
    public void setData(List<VideoTOCTree> data) {
        mOperateVideoLl.setVisibility(View.VISIBLE);
        mAdapter.addAll(data);
        mAdapter.stopMore();
    }

    @Override
    public void setEmpty() {
        mRecyclerView.showEmpty();
    }

    @Override
    public void setNetError() {
        mRecyclerView.showError();
    }

    @Override
    public void setStorageInfo(String downSize, String surplusSize) {
        mMemorySpaceTipTv.setText(getString(R.string.download_memory_space_tip, downSize, surplusSize));
    }

    @Override
    public void showMobileTipDialog(int msgId, int okStrId, int cancelStrId, final boolean isResume) {
        final CustomDialog dialog = new CustomDialog(this, R.style.DialogTheme, getString(msgId));
        dialog.setCancelable(false);
        dialog.hasNoCancel(true);
        dialog.setBtnOKAndBtnCancelTxt(getString(okStrId), getString(cancelStrId));
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                if (isResume) {
                    mPresenter.resumeDownload();
                }
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
                DownloadSettingActivity.startActivity(VideoDownloadChooseActivity.this);
            }
        });
    }

    @Override
    public void showToastTip(int msg) {
        ToastUtils.showSingleToast(msg);
    }

    @Override
    public void updateVideoItem(VideoBean item) {
        ((VideoDownloadChooseAdapter) mAdapter).updateDownloadToDownloading(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.clearMemoryData();
        mPresenter.detachView();
        mPresenter.unregisterRxBus();
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onItemClick(int position) {

    }


    //初始化存储权限
    private void initSDCardPermission() {
        AppUpdateManager.getInstance().initContext(this);
        if (Build.VERSION.SDK_INT < 23) {
            startDownload();
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
                                startDownload();
                            } else {//没有权限,不能使用权限模块-去设置权限

                            }
                        }
                    });
        }
    }

    private void startDownload() {
        mPresenter.startDownload(((VideoDownloadChooseAdapter) mAdapter).getCheckedItem());
        ((VideoDownloadChooseAdapter) mAdapter).clearCheckedItem();
        mDownloadVideoTv.setClickable(false);
        mDownloadVideoTv.setTextColor(getResources().getColor(R.color.color_80999999));
    }
}
