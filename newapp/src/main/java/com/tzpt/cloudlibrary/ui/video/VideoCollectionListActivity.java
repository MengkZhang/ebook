package com.tzpt.cloudlibrary.ui.video;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tzpt.cloudlibrary.CloudLibraryApplication;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.BaseListActivity;
import com.tzpt.cloudlibrary.bean.VideoSetBean;
import com.tzpt.cloudlibrary.rxbus.event.VideoSetEvent;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.utils.ToastUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * 视频收藏列表
 */
public class VideoCollectionListActivity extends BaseListActivity<VideoSetBean> implements
        VideoCollectionListContract.View {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, VideoCollectionListActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.titlebar_right_txt_btn)
    Button mRightTxtBtn;
    @BindView(R.id.collection_del_tv)
    TextView mCollectionDelTv;
    @BindView(R.id.collection_all_check_tv)
    TextView mCollectionAllCheckTv;
    @BindView(R.id.collection_editor_ll)
    LinearLayout mCollectionEditorLl;

    private VideoCollectionListPresenter mPresenter;
    private int mCurrentPage = 1;

    @OnClick({R.id.titlebar_left_btn, R.id.titlebar_right_txt_btn, R.id.collection_all_check_tv, R.id.collection_del_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.titlebar_right_txt_btn:
                if (mRightTxtBtn.getText().toString().equals("编辑")) {
                    setOperationBtnStatus(true);
                    showEditorModel();
                } else {
                    closeEditorModel();
                    setOperationBtnStatus(false);
                }
                break;
            case R.id.collection_all_check_tv:  //全选，取消全选
                if (mCollectionAllCheckTv.getText().toString().equals("全选")) {
                    mCollectionAllCheckTv.setText("取消全选");
                    ((VideoCollectionListAdapter) mAdapter).checkAllOrNone(true);
                } else {
                    ((VideoCollectionListAdapter) mAdapter).checkAllOrNone(false);
                    mCollectionAllCheckTv.setText("全选");
                }
                break;
            case R.id.collection_del_tv:        //删除
                delVideo();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_video_collection_list;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setTitle("收藏视频");
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
    }

    @Override
    public void initDatas() {
        mPresenter = new VideoCollectionListPresenter(CloudLibraryApplication.mRxBus);
        mPresenter.attachView(this);

        mPresenter.registerRxBus(VideoSetEvent.class, new Action1<VideoSetEvent>() {
            @Override
            public void call(VideoSetEvent videoSetEvent) {
                switch (videoSetEvent.getMsgType()) {
                    case 0://接收电子书数量信息
                        if (videoSetEvent.getCount() > 0) {
                            mCollectionDelTv.setTextColor(Color.parseColor("#ee7853"));
                            StringBuilder delCountStr = new StringBuilder();
                            delCountStr.append("删除");
                            delCountStr.append("(");
                            delCountStr.append(videoSetEvent.getCount());
                            delCountStr.append(")");
                            mCollectionDelTv.setText(delCountStr);
                            mCollectionDelTv.setClickable(true);
                            //如果选中数量=列表总数，则为可以取消全部状态
                            mCollectionAllCheckTv.setText(videoSetEvent.getCount() == mAdapter.getCount() ? "取消全选" : "全选");
                        } else {
                            mCollectionDelTv.setTextColor(Color.parseColor("#80999999"));
                            mCollectionDelTv.setClickable(false);
                            mCollectionDelTv.setText("删除");
                            mCollectionAllCheckTv.setText("全选");
                        }
                        break;
                    case 1://编辑状态，当前接收到为编辑状态，则取消全选
                        if (videoSetEvent.isEditorAble()) {
                            mCollectionDelTv.setTextColor(Color.parseColor("#80999999"));
                            ((VideoCollectionListAdapter) mAdapter).checkAllOrNone(false);
                            mCollectionDelTv.setClickable(false);
                            mCollectionDelTv.setText("删除");
                            mCollectionAllCheckTv.setText("全选");
                        }
                        if (mRightTxtBtn.getText().toString().equals("取消")) {
                            return;
                        }
                        checkEditFunction(videoSetEvent.isEditorAble());
                        break;
                    case 2://恢复为默认编辑样式，关闭选择状态
                        closeEditorModel();
                        setOperationBtnStatus(false);
                        checkEditFunction(videoSetEvent.isEditorAble());
                        break;
                }
            }
        });
    }

    @Override
    public void configViews() {
        mAdapter = new VideoCollectionListAdapter(this);
        initAdapter(true, true);

        mPresenter.getCollectVideoSetList(1, false);
    }

    @Override
    public void onRefresh() {
        mPresenter.getCollectVideoSetList(1, false);
    }

    @Override
    public void onLoadMore() {
        mPresenter.getCollectVideoSetList(mCurrentPage + 1, false);
    }

    @Override
    public void onItemClick(int position) {
        //编辑模式不可点击
        if (((VideoCollectionListAdapter) mAdapter).isEditMode()) {
            ((VideoCollectionListAdapter) mAdapter).chooseCollectionVideo(position);
            return;
        }
        VideoSetBean item = mAdapter.getItem(position);
        if (null != item) {
            Intent intent = new Intent(this, VideoDetailActivity.class);
            intent.putExtra(VideoDetailActivity.VIDEO_ID, item.getId());
            intent.putExtra(VideoDetailActivity.VIDEO_TITLE, item.getTitle());
            startActivityForResult(intent, 1000);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000
                && resultCode == Activity.RESULT_OK) {
            long videoId = data.getLongExtra(VideoDetailActivity.VIDEO_ID, -1);
            if (videoId != -1) {
                for (VideoSetBean item : mAdapter.getAllData()) {
                    if (item.getId() == videoId) {
                        mAdapter.remove(item);
                        break;
                    }
                }
            }
            if (mAdapter.getCount() == 0) {
                mPresenter.setEditorAble(false);
                mRecyclerView.showEmpty();
            }
        }
    }

    //<editor-fold desc="视频列表回调UI">
    @Override
    public void setVideoList(List<VideoSetBean> videoList, boolean refresh) {
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
    public void showDelProgress() {
        showDialog("删除中...");
    }

    @Override
    public void dismissDelProgress() {
        dismissDialog();
    }

    @Override
    public void showErrorMsg(int resId) {
        ToastUtils.showSingleToast(resId);
    }

    //编辑模式
    public void showEditorModel() {
        ((VideoCollectionListAdapter) mAdapter).setEditMode(true);
        mCollectionEditorLl.setVisibility(View.VISIBLE);
    }

    //关闭编辑模式
    public void closeEditorModel() {
        ((VideoCollectionListAdapter) mAdapter).setEditMode(false);
        mCollectionEditorLl.setVisibility(View.GONE);
        mCollectionDelTv.setClickable(false);
        mCollectionDelTv.setText("删除");
        mCollectionAllCheckTv.setText("全选");
    }

    //删除选中收藏视频
    public void delVideo() {
        List<Long> eBookIdList = new ArrayList<>();
        for (int i = ((VideoCollectionListAdapter) mAdapter).mSparseItemChecked.size() - 1; i >= 0; i--) {
            int position = ((VideoCollectionListAdapter) mAdapter).mSparseItemChecked.keyAt(i);
            if (((VideoCollectionListAdapter) mAdapter).mSparseItemChecked.valueAt(i)) {
                eBookIdList.add(mAdapter.getItem(position).getId());
            }
        }
        //需要批量删除
        mPresenter.cancelCollectionVideoList(eBookIdList);
    }

    /**
     * 设置编辑模式下的按钮状态
     *
     * @param editorModel 编辑模式
     */
    private void setOperationBtnStatus(boolean editorModel) {
        if (editorModel) {
            mCommonTitleBar.setRightBtnText("取消");
        } else {
            mCommonTitleBar.setRightBtnText("编辑");
        }
    }

    /**
     * 设置是否有编辑功能
     *
     * @param hasFunction
     */

    private void checkEditFunction(boolean hasFunction) {
        if (hasFunction) {
            mCommonTitleBar.setRightBtnText("编辑");
            mCommonTitleBar.setRightBtnClickAble(true);
        } else {
            mCommonTitleBar.clearRightBtnTxt();
            mCommonTitleBar.setRightBtnClickAble(false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveLoginOut(AccountMessage loginMessage) {
        if (null != mPresenter && loginMessage.mIsLoginOut) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mAdapter) {
            mAdapter.clear();
        }
        if (null != mPresenter) {
            mPresenter.unregisterRxBus();
            mPresenter.detachView();
        }
    }
}
