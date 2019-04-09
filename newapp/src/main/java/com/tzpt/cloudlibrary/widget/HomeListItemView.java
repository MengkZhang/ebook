package com.tzpt.cloudlibrary.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.utils.DpPxUtils;
import com.tzpt.cloudlibrary.widget.recyclerview.NoScrollRecyclerView;
import com.tzpt.cloudlibrary.widget.recyclerview.decoration.DividerDecoration;

/**
 * 首页列表
 * Created by tonyjia on 2018/10/16.
 */
public class HomeListItemView extends FrameLayout {

    private NoScrollRecyclerView mRecyclerView;
    private TextView mTitleTv;
    private TextView mSubTitleTv;

    public HomeListItemView(Context context) {
        this(context, null);
    }

    public HomeListItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HomeListItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initViews(Context context) {
        inflate(context, R.layout.view_home_list_item, this);
        mTitleTv = (TextView) findViewById(R.id.home_item_title_tv);
        mSubTitleTv = (TextView) findViewById(R.id.home_item_sub_title_tv);
        mRecyclerView = (NoScrollRecyclerView) findViewById(R.id.home_item_cell_rv);
        mRecyclerView.setHasFixedSize(true);

        ViewCompat.stopNestedScroll(mRecyclerView);
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    public void setTitleListener(OnClickListener clickListener) {
        findViewById(R.id.home_item_title_ll).setOnClickListener(clickListener);
    }

    public void setTitle(String title) {
        mTitleTv.setText(title);
    }

    public void setSubTitle(String subTitle) {
        mSubTitleTv.setText(subTitle);
    }

    /**
     * 配置列表
     * <p>
     * 嵌套避免显示不完全
     *
     * @param context 上下文
     */
    public void configLinearLayoutManager(Context context) {
        HomeListManager layoutManager = new HomeListManager(context);
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(layoutManager);
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

    public void setItemDecoration(int color, int height, int paddingLeft, int paddingRight) {
        DividerDecoration itemDecoration = new DividerDecoration(color, height, paddingLeft, paddingRight);
        itemDecoration.setDrawLastItem(true);
        mRecyclerView.addItemDecoration(itemDecoration);
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

    public void setRecyclerViewMargin(int left, int top, int right, int bottom) {
        ViewGroup.MarginLayoutParams params = (MarginLayoutParams) mRecyclerView.getLayoutParams();
        params.setMargins(left, top, right, bottom);
        mRecyclerView.setLayoutParams(params);
    }

    public void setRecyclerPadding(int left, int top, int right, int bottom) {
        mRecyclerView.setPadding(left, top, right, bottom);
    }

    public void hideHomeListItem() {
        setVisibility(View.GONE);
    }

}
