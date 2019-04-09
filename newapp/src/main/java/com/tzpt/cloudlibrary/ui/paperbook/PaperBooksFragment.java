package com.tzpt.cloudlibrary.ui.paperbook;

import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseFragment;
import com.tzpt.cloudlibrary.business_bean.BookBean;
import com.tzpt.cloudlibrary.widget.recyclerview.EasyRecyclerView;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.OnLoadMoreListener;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;
import com.tzpt.cloudlibrary.widget.recyclerview.swipe.OnRefreshListener;

import java.util.List;

import butterknife.BindView;

/**
 * 纸质书籍列表Fragment
 * Created by Administrator on 2017/6/6.
 */
public class PaperBooksFragment extends BaseFragment implements PaperBookContract.View,
        OnLoadMoreListener,
        OnRefreshListener,
        RecyclerArrayAdapter.OnItemClickListener {
    private PaperBookContract.Presenter mPresenter;

    @BindView(R.id.recyclerview)
    protected EasyRecyclerView mRecyclerView;
    protected RecyclerArrayAdapter<BookBean> mAdapter;

    private int mCurrentPage = 1;
    private boolean mIsPrepared;
    private boolean mIsFirstLoad = true;

    public PaperBooksFragment() {
        // Requires empty public constructor
    }

    public static PaperBooksFragment newInstance() {
        return new PaperBooksFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_paper_books;
    }

    protected void initAdapter(boolean refreshable, boolean loadmoreable) {
        mAdapter = new BookListAdapter(getActivity(), mPresenter.isBorrowRanking(), mPresenter.isPraiseRanking(), mPresenter.isRecommendRanking());

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
    public void onResume() {
        super.onResume();
        UmengHelper.setPageStart("PaperBooksFragment");
    }

    @Override
    public void onPause() {
        UmengHelper.setPageEnd("PaperBooksFragment");
        super.onPause();
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
        if (null != mPresenter && mIsFirstLoad) {
            this.mIsFirstLoad = false;
            mPresenter.getPaperBook(1);
        }
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
            initAdapter(false, true);
        }
    }

    @Override
    public void onItemClick(int position) {
        if (null != mPresenter) {
            BookBean bean = mAdapter.getItem(position);
            if (null != bean) {
                if (mPresenter.isRecommendRanking()) {
                    //推荐排行榜
                    BookDetailActivity.startActivity(getSupportActivity(), bean.mBook.mIsbn, mPresenter.getLibraryCode(), null, 3);
                } else if (mPresenter.isPraiseRanking()) {
                    //点赞排行榜
                    BookDetailActivity.startActivity(getSupportActivity(), bean.mBook.mIsbn, mPresenter.getLibraryCode(), null, 4);
                } else if (mPresenter.isLibraryBookList()) {
                    //图书馆(包括馆内搜索结果)图书列表详情进入
                    if (mPresenter.isSearchBookList()) {
                        BookDetailActivity.startActivityForSearchResult(getSupportActivity(), bean.mBook.mIsbn, mPresenter.getLibraryCode(), 2);
                    } else {
                        BookDetailActivity.startActivity(getSupportActivity(), bean.mBook.mIsbn, mPresenter.getLibraryCode(), null, 2);
                    }
                } else {
                    if (mPresenter.isSearchBookList()) {
                        BookDetailActivity.startActivityForSearchResult(getSupportActivity(), bean.mBook.mIsbn, null, 0);
                    } else {
                        BookDetailActivity.startActivity(getSupportActivity(), bean.mBook.mIsbn, null, null, 0);
                    }
                }
            }
        }
    }


    @Override
    public void onRefresh() {
        if (null != mPresenter) {
            mPresenter.getPaperBook(1);
        }
    }

    @Override
    public void onLoadMore() {
        if (null != mPresenter) {
            mPresenter.getPaperBook(mCurrentPage + 1);
        }
    }

    @Override
    public void showBookList(List<BookBean> list, int totalCount, int libraryTotalCount, int limitTotalCount, boolean refresh) {
        if (refresh) {
            mAdapter.clear();
            mCurrentPage = 1;
        } else {
            mCurrentPage = mCurrentPage + 1;
        }
        mAdapter.addAll(list);
        mRecyclerView.setRefreshing(false);

        if (mAdapter.getCount() > 0) {
            //当前是排行榜
            if (mPresenter.isRankingList()) {
                //排行榜限制显示条数
                mRecyclerView.showToastTv(getString(R.string.library_list_tips, mAdapter.getCount(), totalCount > limitTotalCount ? limitTotalCount : totalCount));

                limitLoadMore(limitTotalCount);
            } else {
                int currentType = mPresenter.getCurrentType();
                switch (currentType) {
                    case PaperBookFilterType.Hot_Book_List:             //一周热门
                    case PaperBookFilterType.New_Book_List:             //最新上架
                        mRecyclerView.showToastTv(String.valueOf(mAdapter.getCount()));
                        limitLoadMore(limitTotalCount);
                        break;
                    case PaperBookFilterType.Normal_Search_Book_List:   //平台搜索
                        mRecyclerView.showToastTv(getString(R.string.library_list_tips, mAdapter.getCount(), totalCount));
                        break;
                    case PaperBookFilterType.Library_Search_Book_List:  //馆搜索数据
                    case PaperBookFilterType.Library_Book_list:         //馆图书列表
                        mRecyclerView.showToastTv(getString(R.string.book_list_tips_for_library, mAdapter.getCount(), totalCount, libraryTotalCount));
                        break;
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

    //数据加载完毕
    @Override
    public void showBookListIsEmpty(boolean refresh) {
        mRecyclerView.setRefreshing(false);
        if (refresh) {
            mAdapter.clear();
            mRecyclerView.showEmpty();
        } else {
            mAdapter.stopMore();
        }
    }

    @Override
    public void setPresenter(PaperBookContract.Presenter presenter) {
        mPresenter = presenter;
    }

    //网络故障
    @Override
    public void showNetError(boolean refresh) {
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
    public void showRefreshLoading() {
        mAdapter.clear();
        mRecyclerView.showProgress();
    }

    /**
     * 处理分类点击
     *
     * @param id 分类ID
     */
    public void dealClassifyClick(int id) {
        mPresenter.mustShowProgressLoading();
        mPresenter.setPagerBookClassificationId(id);
        mPresenter.getPaperBook(1);
    }
}
