package com.tzpt.cloudlibrary.widget.recyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.utils.AnimationUtils;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.GroupRecyclerArrayAdapter;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;
import com.tzpt.cloudlibrary.widget.recyclerview.swipe.OnRefreshListener;
import com.tzpt.cloudlibrary.widget.recyclerview.swipe.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/8.
 */

public class EasyRecyclerView extends FrameLayout {
    protected RecyclerView mRecycler;
    protected ViewGroup mProgressView;
    protected ViewGroup mEmptyView;
    protected ViewGroup mErrorView;
    protected TextView mShowToastTv;
    private int mProgressId;
    private int mEmptyId;
    private int mErrorId;

    protected boolean mClipToPadding;
    protected int mPadding;
    protected int mPaddingTop;
    protected int mPaddingBottom;
    protected int mPaddingLeft;
    protected int mPaddingRight;
    protected int mScrollbarStyle;
    protected int mScrollbar;
    protected int mOverScrollMode;

    private boolean mIsShowToastTv;

    protected RecyclerView.OnScrollListener mInternalOnScrollListener;
    protected RecyclerView.OnScrollListener mExternalOnScrollListener;

    protected SwipeRefreshLayout mPtrLayout;
    protected OnRefreshListener mRefreshListener;

    public List<RecyclerView.ItemDecoration> decorations = new ArrayList<>();

    public EasyRecyclerView(Context context) {
        this(context, null);
    }

    public EasyRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EasyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (attrs != null) {
            initAttrs(attrs);
        }
        initView();
    }

    protected void initAttrs(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.superrecyclerview);
        try {
            mClipToPadding = a.getBoolean(R.styleable.superrecyclerview_recyclerClipToPadding, false);
            mPadding = (int) a.getDimension(R.styleable.superrecyclerview_recyclerPadding, -1.0f);
            mPaddingTop = (int) a.getDimension(R.styleable.superrecyclerview_recyclerPaddingTop, 0.0f);
            mPaddingBottom = (int) a.getDimension(R.styleable.superrecyclerview_recyclerPaddingBottom, 0.0f);
            mPaddingLeft = (int) a.getDimension(R.styleable.superrecyclerview_recyclerPaddingLeft, 0.0f);
            mPaddingRight = (int) a.getDimension(R.styleable.superrecyclerview_recyclerPaddingRight, 0.0f);
            mScrollbarStyle = a.getInteger(R.styleable.superrecyclerview_scrollbarStyle, -1);
            mOverScrollMode = a.getInteger(R.styleable.superrecyclerview_overScrollMode, -1);
            mScrollbar = a.getInteger(R.styleable.superrecyclerview_scrollbars, -1);

            mEmptyId = a.getResourceId(R.styleable.superrecyclerview_layout_empty, 0);
            mProgressId = a.getResourceId(R.styleable.superrecyclerview_layout_progress, 0);
            mErrorId = a.getResourceId(R.styleable.superrecyclerview_layout_error, R.layout.common_net_error_big_view);
        } finally {
            a.recycle();
        }
    }

    private void initView() {
        if (isInEditMode()) {
            return;
        }
        //生成主View
        View v = LayoutInflater.from(getContext()).inflate(R.layout.common_recyclerview, this);
        mPtrLayout = (SwipeRefreshLayout) v.findViewById(R.id.ptr_layout);
        mPtrLayout.setEnabled(false);

        mProgressView = (ViewGroup) v.findViewById(R.id.progress);
        if (mProgressId != 0) LayoutInflater.from(getContext()).inflate(mProgressId, mProgressView);
        mEmptyView = (ViewGroup) v.findViewById(R.id.empty);
        if (mEmptyId != 0) LayoutInflater.from(getContext()).inflate(mEmptyId, mEmptyView);
        mErrorView = (ViewGroup) v.findViewById(R.id.error);
        if (mErrorId != 0) LayoutInflater.from(getContext()).inflate(mErrorId, mErrorView);
        mShowToastTv = (TextView) v.findViewById(R.id.show_toast_tv);
        initRecyclerView(v);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return mPtrLayout.dispatchTouchEvent(ev);
    }

    public void setClipToPadding(boolean isClip) {
        mRecycler.setClipToPadding(isClip);
    }

    public RecyclerView getRecyclerView() {
        return mRecycler;
    }

    public void scrollToPosition(int position) {
        mRecycler.scrollToPosition(position);
    }

    /**
     * Implement this method to customize the AbsListView
     */
    private void initRecyclerView(View view) {
        mRecycler = (RecyclerView) view.findViewById(android.R.id.list);
        setItemAnimator(null);
        if (mRecycler != null) {
            mRecycler.setHasFixedSize(true);
            mRecycler.setClipToPadding(mClipToPadding);
            mInternalOnScrollListener = new RecyclerView.OnScrollListener() {

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (mExternalOnScrollListener != null)
                        mExternalOnScrollListener.onScrolled(recyclerView, dx, dy);

                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (mExternalOnScrollListener != null)
                        mExternalOnScrollListener.onScrollStateChanged(recyclerView, newState);
                    if (mIsShowToastTv) {
                        if (newState == RecyclerView.SCROLL_STATE_DRAGGING
                                || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                            if (mShowToastTv.getVisibility() == View.GONE) {
                                mShowToastTv.setVisibility(View.VISIBLE);
                                AnimationUtils.setShowAnimation(mShowToastTv);
                            }
                        } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            mShowToastTv.setVisibility(View.GONE);
                        }
                    }
                }
            };
            mRecycler.addOnScrollListener(mInternalOnScrollListener);

            if (mPadding != -1.0f) {
                mRecycler.setPadding(mPadding, mPadding, mPadding, mPadding);
            } else {
                mRecycler.setPadding(mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom);
            }
            if (mScrollbarStyle != -1) {
                mRecycler.setScrollBarStyle(mScrollbarStyle);
            }
            if (mOverScrollMode != -1) {
                mRecycler.setOverScrollMode(mOverScrollMode);
            }
            switch (mScrollbar) {
                case 0:
                    setVerticalScrollBarEnabled(false);
                    break;
                case 1:
                    setHorizontalScrollBarEnabled(false);
                    break;
                case 2:
                    setVerticalScrollBarEnabled(false);
                    setHorizontalScrollBarEnabled(false);
                    break;
            }
        }
    }

    @Override
    public void setVerticalScrollBarEnabled(boolean verticalScrollBarEnabled) {
        mRecycler.setVerticalScrollBarEnabled(verticalScrollBarEnabled);
    }

    @Override
    public void setHorizontalScrollBarEnabled(boolean horizontalScrollBarEnabled) {
        mRecycler.setHorizontalScrollBarEnabled(horizontalScrollBarEnabled);
    }

    /**
     * Set the layout manager to the recycler
     */
    public void setLayoutManager(RecyclerView.LayoutManager manager) {
        mRecycler.setLayoutManager(manager);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        mRecycler.addItemDecoration(itemDecoration);
    }
//    /**
//     * Set the ItemDecoration to the recycler
//     */
//    public void setItemDecoration(int color, int height, int paddingLeft, int paddingRight) {
//        DividerDecoration itemDecoration = new DividerDecoration(color, height, paddingLeft, paddingRight);
//        itemDecoration.setDrawLastItem(true);
//        decorations.add(itemDecoration);
//        mRecycler.addItemDecoration(itemDecoration);
//    }

    public void setDividerDrawable(int drawableId) {
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getContext(), drawableId));
        mRecycler.addItemDecoration(divider);
    }


    public static class EasyDataObserver extends RecyclerView.AdapterDataObserver {
        private EasyRecyclerView recyclerView;

        EasyDataObserver(EasyRecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            update();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            update();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            update();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            update();
        }

        @Override
        public void onChanged() {
            super.onChanged();
            update();
        }

        //自动更改Container的样式
        private void update() {
            int count;
            if (recyclerView.getAdapter() instanceof RecyclerArrayAdapter) {
                count = ((RecyclerArrayAdapter) recyclerView.getAdapter()).getCount();
            } else if (recyclerView.getAdapter() instanceof GroupRecyclerArrayAdapter) {
                count = ((GroupRecyclerArrayAdapter) recyclerView.getAdapter()).getGroupCount();
            } else {
                count = recyclerView.getAdapter().getItemCount();
            }

            if (count > 0) {
                recyclerView.showRecycler();
            }
        }
    }

    /**
     * 设置适配器，关闭所有副view。展示recyclerView
     * 适配器有更新，自动关闭所有副view。根据条数判断是否展示EmptyView
     */
    public void setAdapter(RecyclerView.Adapter adapter) {
        mRecycler.setAdapter(adapter);
        adapter.registerAdapterDataObserver(new EasyDataObserver(this));
        showRecycler();
    }

    /**
     * 设置适配器，关闭所有副view。展示进度条View
     * 适配器有更新，自动关闭所有副view。根据条数判断是否展示EmptyView
     */
    public void setAdapterWithProgress(RecyclerView.Adapter adapter) {
        mRecycler.setAdapter(adapter);
        adapter.registerAdapterDataObserver(new EasyDataObserver(this));
        //只有Adapter为空时才显示ProgressView
        if (adapter instanceof RecyclerArrayAdapter) {
            if (((RecyclerArrayAdapter) adapter).getCount() == 0) {
                showProgress();
            } else {
                showRecycler();
            }
        } else if (adapter instanceof GroupRecyclerArrayAdapter) {
            if (((GroupRecyclerArrayAdapter) adapter).getGroupCount() == 0) {
                showProgress();
            } else {
                showRecycler();
            }
        } else {
            if (adapter.getItemCount() == 0) {
                showProgress();
            } else {
                showRecycler();
            }
        }
    }

    public void showToastTv(String toast) {
        mShowToastTv.setText(toast);
        mIsShowToastTv = true;
    }

    /**
     * Remove the adapter from the recycler
     */
    public void clear() {
        mRecycler.setAdapter(null);
    }


    private void hideAll() {
        mEmptyView.setVisibility(View.GONE);
        mProgressView.setVisibility(View.GONE);
        mErrorView.setVisibility(GONE);
        mRecycler.setVisibility(View.INVISIBLE);
    }


    public void showError() {
        if (mErrorView.getChildCount() > 0) {
            hideAll();
            mErrorView.setVisibility(View.VISIBLE);
            mPtrLayout.setEnabled(false);
            mErrorView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProgress();
                    if (mRefreshListener != null) {
                        mRefreshListener.onRefresh();
                    }
                }
            });
        } else {
            showRecycler();
        }

    }

    public void showEmpty() {
        if (mEmptyView.getChildCount() > 0) {
            hideAll();
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            showRecycler();
        }
        mPtrLayout.setEnabled(false);
    }

    public void setRetryRefreshListener(OnRefreshListener listener) {
        this.mRefreshListener = listener;
    }

    public void showProgress() {
        if (mProgressView.getChildCount() > 0) {
            hideAll();
            mProgressView.setVisibility(View.VISIBLE);
        } else {
            showRecycler();
        }
    }

    public void showRecycler() {
        hideAll();
        mRecycler.setVisibility(View.VISIBLE);
    }

    /**
     * Set the listener when refresh is triggered and enable the SwipeRefreshLayout
     */
    public void setRefreshListener(OnRefreshListener listener) {
        mPtrLayout.setEnabled(true);
        mPtrLayout.setOnRefreshListener(listener);
        mRefreshListener = listener;
    }

    public void setRefreshing(final boolean isRefreshing) {
        mPtrLayout.post(new Runnable() {
            @Override
            public void run() {
                if (isRefreshing) { // 避免刷新的loadding和progressview 同时显示
                    mProgressView.setVisibility(View.GONE);
                }
                mPtrLayout.setRefreshing(isRefreshing);
            }
        });
    }

    /**
     * Set the scroll listener for the recycler
     */
    public void setOnScrollListener(RecyclerView.OnScrollListener listener) {
        mExternalOnScrollListener = listener;
    }

    /**
     * @return the recycler adapter
     */
    public RecyclerView.Adapter getAdapter() {
        return mRecycler.getAdapter();
    }


    public void setOnTouchListener(View.OnTouchListener listener) {
        mRecycler.setOnTouchListener(listener);
    }

    public void setItemAnimator(RecyclerView.ItemAnimator animator) {
        mRecycler.setItemAnimator(animator);
    }
}
