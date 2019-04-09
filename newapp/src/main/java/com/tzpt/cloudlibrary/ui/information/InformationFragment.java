package com.tzpt.cloudlibrary.ui.information;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.tzpt.cloudlibrary.AppIntentGlobalName;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseFragment;
import com.tzpt.cloudlibrary.bean.InformationBean;
import com.tzpt.cloudlibrary.widget.recyclerview.EasyRecyclerView;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.OnLoadMoreListener;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;
import com.tzpt.cloudlibrary.widget.recyclerview.swipe.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 资讯列表界面
 * Created by Administrator on 2017/11/7.
 */

public class InformationFragment extends BaseFragment implements
        InformationContract.View,
        OnLoadMoreListener,
        OnRefreshListener,
        RecyclerArrayAdapter.OnItemClickListener {

    private static final int REQUEST_CODE = 1000;
    @BindView(R.id.recyclerview)
    EasyRecyclerView mRecyclerView;

    private InformationContract.Presenter mPresenter;
    protected RecyclerArrayAdapter<InformationBean> mAdapter;
    private int mCurrentPage = 1;

    private boolean mIsPrepared;
    private boolean mIsFirstLoad = true;

    private int mTotalCount;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_information;
    }

    @Override
    public void initDatas() {
        mIsPrepared = true;

        initAdapter(true, true);
        lazyLoad();
    }

    @Override
    public void configViews() {

    }

    private void initAdapter(boolean refreshable, boolean loadmoreable) {
        mAdapter = new InformationAdapter(getActivity());
        if (mRecyclerView != null) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getSupportActivity()));
            mRecyclerView.setAdapterWithProgress(mAdapter);
        }

        if (mAdapter != null) {
            mAdapter.setOnItemClickListener(this);
            mAdapter.setError(R.layout.common_rv_error_view).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAdapter.resumeMore();
                }
            });
            if (loadmoreable) {
                mAdapter.setMore(R.layout.common_rv_more_view, this);
                mAdapter.setNoMore(R.layout.common_rv_nomore_view);
            }
            if (mRecyclerView != null) {
                if (refreshable) {
                    mRecyclerView.setRefreshListener(this);
                }
            }
        }
    }

    @Override
    protected void lazyLoad() {
        if (!mIsVisible || !mIsPrepared) {
            return;
        }
        if (null != mPresenter && mIsFirstLoad) {//懒加载，只允许第一次加载
            this.mIsFirstLoad = false;
            mPresenter.getInformationList(1);
        }
    }

    @Override
    public void setPresenter(InformationContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void setInformationList(List<InformationBean> beanList, int totalCount, boolean refresh) {
        mTotalCount = totalCount;
        if (refresh) {
            mAdapter.clear();
            mCurrentPage = 1;
        } else {
            mCurrentPage = mCurrentPage + 1;
        }
        mAdapter.addAll(beanList);
        mRecyclerView.setRefreshing(false);
        if (mAdapter.getCount() > 0) {
            if (TextUtils.isEmpty(mPresenter.getLibCode())) {
                if (mPresenter.getFromSearch() == 1) {
                    mRecyclerView.showToastTv(getString(R.string.library_list_tips, mAdapter.getCount(), mTotalCount));
                } else {
                    mRecyclerView.showToastTv(String.valueOf(mAdapter.getCount()));
                }
            } else {
                mRecyclerView.showToastTv(getString(R.string.library_list_tips, mAdapter.getCount(), mTotalCount));
            }
        }

        //停止加载更多
        if (mAdapter.getCount() >= totalCount) {
            mAdapter.stopMore();
        }
    }

    @Override
    public void setInformationEmpty(boolean refresh) {
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
        } else {
            mAdapter.pauseMore();
        }
    }

    private void setLocalInformationList(List<InformationBean> beanList) {
        mAdapter.addAll(beanList);
        mRecyclerView.setRefreshing(false);
        if (mAdapter.getCount() > 0) {
            if (TextUtils.isEmpty(mPresenter.getLibCode())) {
                if (mPresenter.getFromSearch() == 1) {
                    mRecyclerView.showToastTv(getString(R.string.library_list_tips, mAdapter.getCount(), mTotalCount));
                } else {
                    mRecyclerView.showToastTv(String.valueOf(mAdapter.getCount()));
                }
            } else {
                mRecyclerView.showToastTv(getString(R.string.library_list_tips, mAdapter.getCount(), mTotalCount));
            }
        }
        if (mAdapter.getCount() >= mTotalCount) {
            mAdapter.stopMore();
        }

    }

    @Override
    public void showRefreshLoading() {
        mAdapter.clear();
        mRecyclerView.showProgress();
    }

    @Override
    public void onRefresh() {
        if (null != mPresenter) {
            mPresenter.getInformationList(1);
        }
    }

    @Override
    public void onLoadMore() {
        if (null != mPresenter) {
            mPresenter.getInformationList(mCurrentPage + 1);
        }
    }

    @Override
    public void onItemClick(int position) {
        mPresenter.saveInfoListCache(mAdapter.getAllData());
        InformationDetailDiscussActivity.startActivityForResult(this,
                position,
                mCurrentPage,
                mTotalCount,
                mPresenter.getKeyword(),
                mPresenter.getSource(),
                mPresenter.getSearchTitle(),
                mPresenter.getCategoryId(),
                mPresenter.getSearchIndustryId(),
                mPresenter.getLibCode(),
                mPresenter.getFromSearch(),
                REQUEST_CODE);
    }

    @Override
    public void onResume() {
        super.onResume();
        UmengHelper.setPageStart("InformationFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        UmengHelper.setPageEnd("InformationFragment");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.removeInformationList();
            mPresenter.detachView();
            mPresenter = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            mCurrentPage = data.getIntExtra(AppIntentGlobalName.PAGE_NUM, 1);
            //设置本地资讯数量
            ArrayList<InformationBean> beanList = data.getParcelableArrayListExtra(AppIntentGlobalName.INFORMATION_LIST);
            if (beanList != null) {
                setLocalInformationList(beanList);
            }
        }
    }
}
