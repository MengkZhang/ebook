package com.tzpt.cloudlibrary.ui.ebook;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseFragment;
import com.tzpt.cloudlibrary.business_bean.EBookBean;
import com.tzpt.cloudlibrary.utils.DpPxUtils;
import com.tzpt.cloudlibrary.widget.recyclerview.EasyRecyclerView;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.OnLoadMoreListener;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;
import com.tzpt.cloudlibrary.widget.recyclerview.swipe.OnRefreshListener;

import java.util.List;

import butterknife.BindView;

/**
 * 电子书
 */
public class EBookFragment extends BaseFragment implements
        EBookContract.View,
        OnLoadMoreListener,
        OnRefreshListener,
        RecyclerArrayAdapter.OnItemClickListener {

    @BindView(R.id.recyclerview)
    protected EasyRecyclerView mRecyclerView;
    protected RecyclerArrayAdapter<EBookBean> mAdapter;
    private EBookContract.Presenter mPresenter;
    private int mCurrentPage = 1;
    private boolean mIsPrepared;
    private boolean mIsFirstLoad = true;

    public EBookFragment() {
    }

    public static EBookFragment newInstance() {
        return new EBookFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_ebook;
    }

    @Override
    public void initDatas() {
        //图书馆详情不显示progressBar
        mIsPrepared = true;
        lazyLoad();

    }

    @Override
    public void configViews() {
        if (null != mPresenter) {
            if (mPresenter.isRecommendEBook()) {
                mAdapter = new EBookRecommendationsAdapter(getSupportActivity());
            } else {
                mAdapter = new EBookAdapter(getActivity(), mPresenter.isRankEBookList());
            }

            mRecyclerView.setLayoutManager(new LinearLayoutManager(getSupportActivity()));
            //mRecyclerView.setDividerDrawable(R.drawable.divider_rv_vertical_one);
            mRecyclerView.setAdapterWithProgress(mAdapter);
            mAdapter.setOnItemClickListener(this);
            mAdapter.setError(R.layout.common_rv_error_view).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAdapter.resumeMore();
                }
            });
            mAdapter.setMore(R.layout.common_rv_more_view, this);
            mAdapter.setNoMore(R.layout.common_rv_nomore_view);
            mRecyclerView.setRefreshListener(this);
        }
    }


    @Override
    public void setPresenter(EBookContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void setEBookList(List<EBookBean> eBookList, int totalCount, int limitTotalCount, boolean refresh) {
        if (refresh) {
            mAdapter.clear();
            mCurrentPage = 1;
        } else {
            mCurrentPage = mCurrentPage + 1;
        }
        mAdapter.addAll(eBookList);
        mRecyclerView.setRefreshing(false);

        if (mAdapter.getCount() > 0) {
            //排行榜
            if (mPresenter.isRankEBookList()) {
                //排行榜限制显示条数
                mRecyclerView.showToastTv(getString(R.string.library_list_tips, mAdapter.getCount(), totalCount > limitTotalCount ? limitTotalCount : totalCount));
                limitLoadMore(limitTotalCount);
            } else {
                if (TextUtils.isEmpty(mPresenter.getLibraryCode())) {
                    //平台搜索电子书列表
                    if (mPresenter.isSearchResultList()) {
                        mRecyclerView.showToastTv(getString(R.string.library_list_tips, mAdapter.getCount(), totalCount));
                    } else {
                        //平台电子书列表
                        mRecyclerView.showToastTv(String.valueOf(mAdapter.getCount()));
                        limitLoadMore(limitTotalCount);
                    }
                } else {
                    //馆电子书列表
                    if (mPresenter.getFilterType() == EBookFilterType.Library_EBook_List) {
                        mRecyclerView.showToastTv(getString(R.string.book_list_tips, mAdapter.getCount(), totalCount));
                    } else {
                        mRecyclerView.showToastTv(String.valueOf(mAdapter.getCount()));
                    }
                }
            }
        }
    }

    /**
     * 停止加载更多
     *
     * @param limitTotalCount 限制条数
     */
    private void limitLoadMore(int limitTotalCount) {
        if (mAdapter.getCount() >= limitTotalCount) {
            mAdapter.stopMore();
            //todo 加载已经到底啦
        }
    }

    @Override
    public void setEBookListEmpty(boolean refresh) {
        mRecyclerView.setRefreshing(false);
        if (refresh) {
            mAdapter.clear();
            mRecyclerView.showEmpty();
        } else {
            mAdapter.stopMore();
        }
    }

    @Override
    public void setEBookListError(boolean refresh) {
        mRecyclerView.setRefreshing(false);
        if (refresh) {
            mAdapter.clear();
            mRecyclerView.showError();
        } else {
            mAdapter.pauseMore();
        }
    }

    @Override
    public void showRefreshLoading() {
        mAdapter.clear();
        mRecyclerView.showProgress();
    }

    @Override
    public void onResume() {
        super.onResume();
        UmengHelper.setPageStart("EBookFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        UmengHelper.setPageEnd("EBookFragment");
    }

    @Override
    public void onDestroyView() {
        if (null != mAdapter) {
            mAdapter.clear();
        }
        if (mPresenter != null) {
            mPresenter.detachView();
            mPresenter = null;
        }
        super.onDestroyView();
    }

    @Override
    protected void lazyLoad() {
        if (!mIsVisible || !mIsPrepared) {
            return;
        }
        if (null != mPresenter && mIsFirstLoad) {//懒加载，只允许第一次加载
            this.mIsFirstLoad = false;
            mPresenter.getEBookList(1);
        }
    }

    @Override
    public void onRefresh() {
        if (null != mPresenter) {
            mPresenter.getEBookList(1);
        }
    }

    @Override
    public void onLoadMore() {
        if (null != mPresenter) {
            mPresenter.getEBookList(mCurrentPage + 1);
        }
    }

    @Override
    public void onItemClick(int position) {
        if (null != mPresenter) {
            EBookBean bean = mAdapter.getItem(position);
            if (null != bean) {
                if (mPresenter.isSearchResultList()) {
                    EBookDetailActivity.startActivityForSearchResult(getSupportActivity(), String.valueOf(bean.mEBook.mId), mPresenter.getLibraryCode());
                } else {
                    EBookDetailActivity.startActivity(getSupportActivity(), String.valueOf(bean.mEBook.mId), mPresenter.getLibraryCode());
                }
            }
        }
    }

    /**
     * 分类点击
     *
     * @param parentClassifyId 一级分类ID
     * @param childClassifyId  二级分类ID
     */
    public void dealClassifyClick(int parentClassifyId, int childClassifyId) {
        mPresenter.mustShowProgressLoading();
        mPresenter.setEBookClassificationId(parentClassifyId, childClassifyId);
        mPresenter.getEBookList(1);
    }
}
