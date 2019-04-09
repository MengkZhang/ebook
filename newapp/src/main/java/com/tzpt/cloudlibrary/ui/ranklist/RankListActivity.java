package com.tzpt.cloudlibrary.ui.ranklist;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewStub;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.base.adapter.recyclerview.OnRvItemClickListener;
import com.tzpt.cloudlibrary.business_bean.BookBean;
import com.tzpt.cloudlibrary.business_bean.EBookBean;
import com.tzpt.cloudlibrary.ui.ebook.EBookDetailActivity;
import com.tzpt.cloudlibrary.ui.ebook.EBookGridListAdapter;
import com.tzpt.cloudlibrary.ui.ebook.EBookTabActivity;
import com.tzpt.cloudlibrary.ui.paperbook.BookDetailActivity;
import com.tzpt.cloudlibrary.ui.paperbook.BookTabActivity;
import com.tzpt.cloudlibrary.utils.DpPxUtils;
import com.tzpt.cloudlibrary.widget.HomeListItemView;
import com.tzpt.cloudlibrary.widget.multistatelayout.MultiStateLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 排行榜
 */
public class RankListActivity extends BaseActivity implements
        RankListContract.View {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, RankListActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.multi_state_layout)
    MultiStateLayout mMultiStateLayout;
    @BindView(R.id.rank_thumbs_vs)
    ViewStub mRankThumbsVs;
    @BindView(R.id.rank_recommend_vs)
    ViewStub mRankRecommendVs;
    @BindView(R.id.rank_borrow_vs)
    ViewStub mRankBorrowVs;
    @BindView(R.id.rank_reading_vs)
    ViewStub mRankReadingVs;

    private HomeListItemView mRankThumbsIv;
    private HomeListItemView mRankRecommendIv;
    private HomeListItemView mRankBorrowsIv;
    private HomeListItemView mRankReadingIv;

    private RankBookListAdapter mThumbsUpAdapter;
    private RankBookListAdapter mRecommendAdapter;
    private RankBookListAdapter mBorrowAdapter;
    private EBookGridListAdapter mEBookGridListAdapter;
    private RankListPresenter mPresenter;

    @OnClick({R.id.titlebar_left_btn})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_rank_list;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("排行榜");
    }

    @Override
    public void initDatas() {
        mPresenter = new RankListPresenter();
        mPresenter.attachView(this);
        mPresenter.getRankList();
    }

    @Override
    public void configViews() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getRankList();
            }
        });
    }

    @Override
    public void showRankProgress(boolean isEmpty) {
        if (isEmpty) {
            mMultiStateLayout.showProgress();
        }
    }

    @Override
    public void showNetError() {
        mSwipeRefreshLayout.setRefreshing(false);
        mMultiStateLayout.showError();
        mMultiStateLayout.showRetryError(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getRankList();
            }
        });
    }

    @Override
    public void setRankBookContentView() {
        mSwipeRefreshLayout.setRefreshing(false);
        mMultiStateLayout.showContentView();
    }

    @Override
    public void setRankBookListEmpty() {
        mSwipeRefreshLayout.setRefreshing(false);
        mMultiStateLayout.showEmpty();
    }

    @Override
    public void setRankThumbsUpBookList(List<BookBean> thumbsUpRankBookList) {
        if (mRankThumbsIv == null) {
            View thumbsView = mRankThumbsVs.inflate();
            mRankThumbsIv = (HomeListItemView) thumbsView.findViewById(R.id.rank_thumbs_list_iv);
            mRankThumbsIv.setRecyclerViewMargin(DpPxUtils.dp2px(13.5f), 0, DpPxUtils.dp2px(13.5f), 0);

            mThumbsUpAdapter = new RankBookListAdapter(this);
            mRankThumbsIv.configGridLayoutManager(this, 4);
            mRankThumbsIv.setAdapter(mThumbsUpAdapter);
            mRankThumbsIv.setTitle("点赞排行榜");
            mThumbsUpAdapter.setOnItemClickListener(new OnRvItemClickListener<BookBean>() {
                @Override
                public void onItemClick(int position, BookBean data) {
                    if (data != null) {
                        BookDetailActivity.startActivity(RankListActivity.this, data.mBook.mIsbn, null, null, 4);
                    }
                }
            });
            mRankThumbsIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BookTabActivity.startActivity(RankListActivity.this, 3);
                }
            });
        } else {
            mRankThumbsIv.showHomeListItem();
        }
        mThumbsUpAdapter.clear();
        mThumbsUpAdapter.addAll(thumbsUpRankBookList);
    }

    @Override
    public void hideRankThumbsUpBookList() {
        if (mRankThumbsIv != null) {
            mRankThumbsIv.hideHomeListItem();
        }

    }

    @Override
    public void setRankRecommendBookList(List<BookBean> rankRecommendBookList) {
        if (mRankRecommendIv == null) {
            View recommendView = mRankRecommendVs.inflate();
            mRankRecommendIv = (HomeListItemView) recommendView.findViewById(R.id.rank_recommend_list_iv);
            mRankRecommendIv.setRecyclerViewMargin(DpPxUtils.dp2px(13.5f), 0, DpPxUtils.dp2px(13.5f), 0);

            mRecommendAdapter = new RankBookListAdapter(this);
            mRankRecommendIv.configGridLayoutManager(this, 4);
            mRankRecommendIv.setAdapter(mRecommendAdapter);
            mRankRecommendIv.setTitle("荐购排行榜");
            mRecommendAdapter.setOnItemClickListener(new OnRvItemClickListener<BookBean>() {
                @Override
                public void onItemClick(int position, BookBean data) {
                    if (data != null) {
                        BookDetailActivity.startActivity(RankListActivity.this, data.mBook.mIsbn, null, null, 3);
                    }
                }
            });
            mRankRecommendIv.setTitleListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BookTabActivity.startActivity(RankListActivity.this, 1);
                }
            });
        } else {
            mRankRecommendIv.showHomeListItem();
        }
        mRecommendAdapter.clear();
        mRecommendAdapter.addAll(rankRecommendBookList);
    }

    @Override
    public void hideRankRecommendBookList() {
        if (mRankRecommendIv != null) {
            mRankRecommendIv.hideHomeListItem();
        }
    }

    @Override
    public void setRankBorrowBookList(List<BookBean> borrowBookList) {
        if (mRankBorrowsIv == null) {
            View thumbsView = mRankBorrowVs.inflate();
            mRankBorrowsIv = (HomeListItemView) thumbsView.findViewById(R.id.rank_borrow_list_iv);
            mRankBorrowsIv.setRecyclerViewMargin(DpPxUtils.dp2px(13.5f), 0, DpPxUtils.dp2px(13.5f), 0);

            mBorrowAdapter = new RankBookListAdapter(this);
            mRankBorrowsIv.configGridLayoutManager(this, 4);
            mRankBorrowsIv.setAdapter(mBorrowAdapter);
            mRankBorrowsIv.setTitle("借阅排行榜");
            mBorrowAdapter.setOnItemClickListener(new OnRvItemClickListener<BookBean>() {
                @Override
                public void onItemClick(int position, BookBean data) {
                    if (data != null) {
                        BookDetailActivity.startActivity(RankListActivity.this, data.mBook.mIsbn, null, null, 0);
                    }
                }
            });
            mRankBorrowsIv.setTitleListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BookTabActivity.startActivity(RankListActivity.this, 4);
                }
            });
        } else {
            mRankBorrowsIv.showHomeListItem();
        }
        mBorrowAdapter.clear();
        mBorrowAdapter.addAll(borrowBookList);
    }

    @Override
    public void hideRankBorrowBookList() {
        if (mRankBorrowsIv != null) {
            mRankBorrowsIv.hideHomeListItem();
        }
    }

    @Override
    public void setRankReadingBookList(List<EBookBean> readingBookList) {
        if (mRankReadingIv == null) {
            View thumbsView = mRankReadingVs.inflate();
            mRankReadingIv = (HomeListItemView) thumbsView.findViewById(R.id.rank_reading_list_iv);
            mRankReadingIv.setRecyclerViewMargin(DpPxUtils.dp2px(13.5f), 0, DpPxUtils.dp2px(13.5f), 0);

            mEBookGridListAdapter = new EBookGridListAdapter(this, true);
            mRankReadingIv.configGridLayoutManager(this, 4);
            mRankReadingIv.setAdapter(mEBookGridListAdapter);
            mRankReadingIv.setTitle("阅读排行榜");
            mEBookGridListAdapter.setOnItemClickListener(new OnRvItemClickListener<EBookBean>() {
                @Override
                public void onItemClick(int position, EBookBean data) {
                    if (data != null) {
                        EBookDetailActivity.startActivity(RankListActivity.this, String.valueOf(data.mEBook.mId), null);
                    }
                }
            });
            mRankReadingIv.setTitleListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EBookTabActivity.startActivity(RankListActivity.this, 1);
                }
            });
        } else {
            mRankReadingIv.showHomeListItem();
        }
        mEBookGridListAdapter.clear();
        mEBookGridListAdapter.addAll(readingBookList);
    }

    @Override
    public void hideRankReadingBookList() {
        if (mRankReadingIv != null) {
            mRankReadingIv.hideHomeListItem();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        UmengHelper.setUmengResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        UmengHelper.setUmengPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mThumbsUpAdapter != null) {
            mThumbsUpAdapter.clear();
        }
        if (mRecommendAdapter != null) {
            mRecommendAdapter.clear();
        }
        if (mBorrowAdapter != null) {
            mBorrowAdapter.clear();
        }
        if (mEBookGridListAdapter != null) {
            mEBookGridListAdapter.clear();
        }
        if (mPresenter != null) {
            mPresenter.detachView();
            mPresenter = null;
        }

    }
}
