package com.tzpt.cloudlibrary.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.widget.recyclerview.NoScrollRecyclerView;

/**
 * Created by Administrator on 2019/1/15.
 */

public class RankingListItemView extends FrameLayout {
    private NoScrollRecyclerView mRecyclerView;
    private TextView mTitleTv;
    private TextView mSubTitleTv;

    public RankingListItemView(@NonNull Context context) {
        this(context, null);
    }

    public RankingListItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RankingListItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initViews(Context context) {
        inflate(context, R.layout.view_ranking_list_item, this);
        mTitleTv = (TextView) findViewById(R.id.ranking_item_title_tv);
        mSubTitleTv = (TextView) findViewById(R.id.ranking_item_sub_title_tv);
        mRecyclerView = (NoScrollRecyclerView) findViewById(R.id.ranking_item_cell_rv);
        mRecyclerView.setHasFixedSize(true);
        ViewCompat.stopNestedScroll(mRecyclerView);
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    public void setTitleListener(OnClickListener clickListener) {
        findViewById(R.id.ranking_item_title_rl).setOnClickListener(clickListener);
    }

    public void setTitle(String title) {
        mTitleTv.setText(title);
    }

    public void setSubTitle(String subTitle) {
        mSubTitleTv.setText(subTitle);
    }

    /**
     * 配置网格列表
     *
     * @param context   上下文
     * @param spanCount 数量
     */
    public void configGridLayoutManager(Context context, int spanCount) {
        HomeGridManager layoutManager = new HomeGridManager(context, spanCount);
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount));
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }


    public void clear() {
        mRecyclerView.setAdapter(null);
    }

    public void showHomeListItem() {
        if (getVisibility() == View.GONE) {
            setVisibility(View.VISIBLE);
        }
    }

    public void hideHomeListItem() {
        setVisibility(View.GONE);
    }
}
