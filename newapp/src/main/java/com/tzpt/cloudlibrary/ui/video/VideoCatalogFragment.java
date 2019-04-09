package com.tzpt.cloudlibrary.ui.video;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.BaseFragment;
import com.tzpt.cloudlibrary.bean.VideoTOCTree;
import com.tzpt.cloudlibrary.utils.DpPxUtils;
import com.tzpt.cloudlibrary.widget.recyclerview.EasyRecyclerView;
import com.tzpt.cloudlibrary.widget.recyclerview.swipe.OnRefreshListener;

import java.util.List;

import butterknife.BindView;

/**
 * 视频详情目录
 * Created by tonyjia on 2018/6/25.
 */
public class VideoCatalogFragment extends BaseFragment implements
        VideoCatalogContract.View {

    @BindView(R.id.recycler_view)
    protected EasyRecyclerView mRecyclerView;
    private VideoCatalogAdapter mAdapter;
    private VideoCatalogContract.Presenter mPresenter;
    private boolean mIsPrepared;
    private boolean mIsFirstLoad = true;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_video_catalog;
    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {
        mAdapter = new VideoCatalogAdapter(getSupportActivity());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getSupportActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setDividerDrawable(R.drawable.divider_rv_vertical_seven);
        mRecyclerView.setAdapterWithProgress(mAdapter);

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
            mPresenter.getVideoCatalogList();
            //检查视频下标更新信息
            mPresenter.checkCatalogChooseSectionInfo();
        }
    }

    @Override
    public void setPresenter(VideoCatalogContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void setVideoCatalogList(List<VideoTOCTree> videoTOCTrees, long firstPlayId) {
        mAdapter.clear();
        mAdapter.addAll(videoTOCTrees, firstPlayId);
    }

    @Override
    public void setVideoCatalogEmptyList() {
        mAdapter.clear();
        mRecyclerView.showEmpty();
    }

    @Override
    public void updateCatalogChooseInfo(long sectionId) {
        mAdapter.notifyChooseData(sectionId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != mAdapter) {
            mAdapter.clear();
        }
        if (null != mPresenter) {
            mPresenter.detachView();
        }
    }
}
