package com.tzpt.cloudlibrary.widget.tablayout;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.bean.TabMenuBean;

import java.util.List;

/**
 * Created by Administrator on 2017/11/7.
 */

public class RecyclerTabLayout extends RecyclerView {

    protected static final long DEFAULT_SCROLL_DURATION = 400;
    protected static final float DEFAULT_POSITION_THRESHOLD = 0.6f;
    protected static final float POSITION_THRESHOLD_ALLOWABLE = 0.001f;

    protected Paint mIndicatorPaint;
    protected int mTabBackgroundResId;
    protected int mTabOnScreenLimit;
    protected int mTabMinWidth;
    protected int mTabMaxWidth;
    protected int mTabTextAppearance;
    protected int mTabSelectedTextColor;
    protected int mTabDefaultTextColor;
    protected boolean mTabSelectedTextColorSet;
    protected int mTabPaddingStart;
    protected int mTabPaddingTop;
    protected int mTabPaddingEnd;
    protected int mTabPaddingBottom;
    protected int mIndicatorHeight;
    protected int mIndicatorPadding;
    protected int mTabMarginStartEnd;

    protected LinearLayoutManager mLinearLayoutManager;
    protected RecyclerOnScrollListener mRecyclerOnScrollListener;
    protected ViewPager mViewPager;
    protected Adapter<?> mAdapter;

    protected int mIndicatorPosition;
    protected int mIndicatorGap;
    protected int mIndicatorScroll;
    private int mOldPosition;
    private int mOldScrollOffset;
    protected float mOldPositionOffset;
    protected float mPositionThreshold;
    protected boolean mRequestScrollToTab;
    protected boolean mScrollEnabled;
    private boolean mTabClickAble = true;

    public RecyclerTabLayout(Context context) {
        this(context, null);
    }

    public RecyclerTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerTabLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setWillNotDraw(false);
        mIndicatorPaint = new Paint();
        getAttributes(context, attrs, defStyle);
        mLinearLayoutManager = new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollHorizontally() {
                return mScrollEnabled;
            }
        };
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        setLayoutManager(mLinearLayoutManager);
        setItemAnimator(null);
        mPositionThreshold = DEFAULT_POSITION_THRESHOLD;
    }

    private void getAttributes(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RecyclerTabLayout,
                defStyle, R.style.RecyclerTabLayout);
        setIndicatorColor(a.getColor(R.styleable
                .RecyclerTabLayout_tabIndicatorColor, 0));
        setIndicatorHeight(a.getDimensionPixelSize(R.styleable
                .RecyclerTabLayout_tabIndicatorHeight, 0));
        setIndicatorPadding(a.getDimensionPixelSize(R.styleable
                .RecyclerTabLayout_tabIndicatorPadding, 0));

        mTabTextAppearance = a.getResourceId(R.styleable.RecyclerTabLayout_tabTextAppearance,
                R.style.RecyclerTabLayout_Tab);

        mTabPaddingStart = mTabPaddingTop = mTabPaddingEnd = mTabPaddingBottom = a
                .getDimensionPixelSize(R.styleable.RecyclerTabLayout_tabPadding, 0);
        mTabPaddingStart = a.getDimensionPixelSize(
                R.styleable.RecyclerTabLayout_tabPaddingStart, mTabPaddingStart);
        mTabPaddingTop = a.getDimensionPixelSize(
                R.styleable.RecyclerTabLayout_tabPaddingTop, mTabPaddingTop);
        mTabPaddingEnd = a.getDimensionPixelSize(
                R.styleable.RecyclerTabLayout_tabPaddingEnd, mTabPaddingEnd);
        mTabPaddingBottom = a.getDimensionPixelSize(
                R.styleable.RecyclerTabLayout_tabPaddingBottom, mTabPaddingBottom);
        mTabMarginStartEnd = a.getDimensionPixelSize(
                R.styleable.RecyclerTabLayout_tabMarginStartEnd, mTabMarginStartEnd);

        if (a.hasValue(R.styleable.RecyclerTabLayout_tabSelectedTextColor)) {
            mTabSelectedTextColor = a
                    .getColor(R.styleable.RecyclerTabLayout_tabSelectedTextColor, 0);
            mTabDefaultTextColor = a
                    .getColor(R.styleable.RecyclerTabLayout_tabDefaultTextColor, 0);
            mTabSelectedTextColorSet = true;
        }

        mTabOnScreenLimit = a.getInteger(
                R.styleable.RecyclerTabLayout_tabOnScreenLimit, 0);
        if (mTabOnScreenLimit == 0) {
            mTabMinWidth = a.getDimensionPixelSize(
                    R.styleable.RecyclerTabLayout_tabMinWidth, 0);
            mTabMaxWidth = a.getDimensionPixelSize(
                    R.styleable.RecyclerTabLayout_tabMaxWidth, 0);
        }

        mTabBackgroundResId = a
                .getResourceId(R.styleable.RecyclerTabLayout_tabBackground, 0);
        mScrollEnabled = a.getBoolean(R.styleable.RecyclerTabLayout_scrollEnabled, true);
        a.recycle();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mRecyclerOnScrollListener != null) {
            removeOnScrollListener(mRecyclerOnScrollListener);
            mRecyclerOnScrollListener = null;
        }
        super.onDetachedFromWindow();
    }

    //设置tab可点击状态
    public void setTabClickAble(boolean tabClickAble) {
        this.mTabClickAble = tabClickAble;
    }

    public void setIndicatorColor(int color) {
        mIndicatorPaint.setColor(color);
    }

    public void setIndicatorHeight(int indicatorHeight) {
        mIndicatorHeight = indicatorHeight;
    }

    public void setIndicatorPadding(int indicatorPadding) {
        mIndicatorPadding = indicatorPadding;
    }

    public void setAutoSelectionMode(boolean autoSelect) {
        if (mRecyclerOnScrollListener != null) {
            removeOnScrollListener(mRecyclerOnScrollListener);
            mRecyclerOnScrollListener = null;
        }
        if (autoSelect) {
            mRecyclerOnScrollListener = new RecyclerOnScrollListener(this, mLinearLayoutManager);
            addOnScrollListener(mRecyclerOnScrollListener);
        }
    }

    public void setPositionThreshold(float positionThreshold) {
        mPositionThreshold = positionThreshold;
    }

    public void setTabOnScreenLimit(int screenLimit) {
        mTabOnScreenLimit = screenLimit;
    }

    public void setUpWithViewPager(List<TabMenuBean> tabList, ViewPager viewPager) {
        DefaultAdapter adapter = new DefaultAdapter(tabList);
        adapter.setTabPadding(mTabPaddingStart, mTabPaddingTop, mTabPaddingEnd, mTabPaddingBottom);
        adapter.setTabTextAppearance(mTabTextAppearance);
        adapter.setTabSelectedTextColor(mTabSelectedTextColorSet, mTabSelectedTextColor);
        adapter.setTabMaxWidth(mTabMaxWidth);
        adapter.setTabMinWidth(mTabMinWidth);
        adapter.setTabBackgroundResId(mTabBackgroundResId);
        adapter.setTabOnScreenLimit(mTabOnScreenLimit);
        setUpWithAdapter(adapter, viewPager);
    }

    public void setUpWithAdapter(RecyclerTabLayout.Adapter<?> adapter, ViewPager viewPager) {
        mAdapter = adapter;
        mViewPager = viewPager;
        if (mViewPager.getAdapter() == null) {
            throw new IllegalArgumentException("ViewPager does not have a PagerAdapter set");
        }
        mViewPager.addOnPageChangeListener(new ViewPagerOnPageChangeListener(this));
        setAdapter(adapter);
        scrollToTab(mViewPager.getCurrentItem());
    }

    public void clearList() {
        if (null != mAdapter && null != mAdapter.getTabList()) {
            mAdapter.getTabList().clear();
        }
    }

    public void setCurrentItem(int position, boolean smoothScroll) {
        if (mViewPager != null) {
            mViewPager.setCurrentItem(position, smoothScroll);
            scrollToTab(mViewPager.getCurrentItem());
            return;
        }

        if (smoothScroll && position != mIndicatorPosition) {
            startAnimation(position);

        } else {
            scrollToTab(position);
        }
    }

    protected void startAnimation(final int position) {

        float distance = 1;

        View view = mLinearLayoutManager.findViewByPosition(position);
        if (view != null) {
            float currentX = view.getX() + view.getMeasuredWidth() / 2.f;
            float centerX = getMeasuredWidth() / 2.f;
            distance = Math.abs(centerX - currentX) / view.getMeasuredWidth();
        }

        ValueAnimator animator;
        if (position < mIndicatorPosition) {
            animator = ValueAnimator.ofFloat(distance, 0);
        } else {
            animator = ValueAnimator.ofFloat(-distance, 0);
        }
        animator.setDuration(DEFAULT_SCROLL_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scrollToTab(position, (float) animation.getAnimatedValue(), true);
            }
        });
        animator.start();
    }

    protected void scrollToTab(int position) {
        scrollToTab(position, 0, false);
        mAdapter.setCurrentIndicatorPosition(position);
        mAdapter.notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    protected void scrollToTab(int position, float positionOffset, boolean fitIndicator) {
        int scrollOffset = 0;

        View selectedView = mLinearLayoutManager.findViewByPosition(position);
        View nextView = mLinearLayoutManager.findViewByPosition(position + 1);

        if (selectedView != null) {
            int width = getMeasuredWidth();
            float sLeft = (position == 0) ? 0 : width / 2.f - selectedView.getMeasuredWidth() / 2.f; // left edge of selected tab
            float sRight = sLeft + selectedView.getMeasuredWidth(); // right edge of selected tab

            if (nextView != null) {
                float nLeft = width / 2.f - nextView.getMeasuredWidth() / 2.f; // left edge of next tab
                float distance = sRight - nLeft; // total distance that is needed to distance to next tab
                float dx = distance * positionOffset;
                scrollOffset = (int) (sLeft - dx);

                if (position == 0) {
                    float indicatorGap = (nextView.getMeasuredWidth() - selectedView.getMeasuredWidth()) / 2;
                    mIndicatorGap = (int) (indicatorGap * positionOffset);
                    mIndicatorScroll = (int) ((selectedView.getMeasuredWidth() + indicatorGap) * positionOffset);

                } else {
                    float indicatorGap = (nextView.getMeasuredWidth() - selectedView.getMeasuredWidth()) / 2;
                    mIndicatorGap = (int) (indicatorGap * positionOffset);
                    mIndicatorScroll = (int) dx;
                }

            } else {
                scrollOffset = (int) sLeft;
                mIndicatorScroll = 0;
                mIndicatorGap = 0;
            }
            if (fitIndicator) {
                mIndicatorScroll = 0;
                mIndicatorGap = 0;
            }

        } else {
            if (getMeasuredWidth() > 0 && mTabMaxWidth > 0 && mTabMinWidth == mTabMaxWidth) { //fixed size
                int width = mTabMinWidth;
                int offset = (int) (positionOffset * -width);
                int leftOffset = (int) ((getMeasuredWidth() - width) / 2.f);
                scrollOffset = offset + leftOffset;
            }
            mRequestScrollToTab = true;
        }

        updateCurrentIndicatorPosition(position, positionOffset - mOldPositionOffset, positionOffset);
        mIndicatorPosition = position;

        stopScroll();

        if (position != mOldPosition || scrollOffset != mOldScrollOffset) {
            mLinearLayoutManager.scrollToPositionWithOffset(position, scrollOffset);
        }
        if (mIndicatorHeight > 0) {
            invalidate();
        }

        mOldPosition = position;
        mOldScrollOffset = scrollOffset;
        mOldPositionOffset = positionOffset;
    }

    protected void updateCurrentIndicatorPosition(int position, float dx, float positionOffset) {
        if (mAdapter == null) {
            return;
        }
        int indicatorPosition = -1;
        if (dx > 0 && positionOffset >= mPositionThreshold - POSITION_THRESHOLD_ALLOWABLE) {
            indicatorPosition = position + 1;

        } else if (dx < 0 && positionOffset <= 1 - mPositionThreshold + POSITION_THRESHOLD_ALLOWABLE) {
            indicatorPosition = position;
        }
        if (indicatorPosition >= 0 && indicatorPosition != mAdapter.getCurrentIndicatorPosition()) {
            mAdapter.setCurrentIndicatorPosition(indicatorPosition);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        View view = mLinearLayoutManager.findViewByPosition(mIndicatorPosition);
        if (view == null) {
            if (mRequestScrollToTab) {
                mRequestScrollToTab = false;
                scrollToTab(mViewPager.getCurrentItem());
            }
            return;
        }
        mRequestScrollToTab = false;

        int left;
        int right;
        if (isLayoutRtl()) {
            left = view.getLeft() - mIndicatorScroll - mIndicatorGap;
            right = view.getRight() - mIndicatorScroll + mIndicatorGap;
        } else {
            left = view.getLeft() + mIndicatorScroll - mIndicatorGap;
            right = view.getRight() + mIndicatorScroll + mIndicatorGap;
        }

        int top = getHeight() - mIndicatorHeight;
        int bottom = getHeight();

        canvas.drawRect(left + mIndicatorPadding, top, right - mIndicatorPadding, bottom, mIndicatorPaint);
    }

    protected boolean isLayoutRtl() {
        return ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL;
    }

    protected static class RecyclerOnScrollListener extends OnScrollListener {

        protected RecyclerTabLayout mRecyclerTabLayout;
        protected LinearLayoutManager mLinearLayoutManager;

        public RecyclerOnScrollListener(RecyclerTabLayout recyclerTabLayout,
                                        LinearLayoutManager linearLayoutManager) {
            mRecyclerTabLayout = recyclerTabLayout;
            mLinearLayoutManager = linearLayoutManager;
        }

        public int mDx;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            mDx += dx;
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            switch (newState) {
                case SCROLL_STATE_IDLE:
                    if (mDx > 0) {
                        selectCenterTabForRightScroll();
                    } else {
                        selectCenterTabForLeftScroll();
                    }
                    mDx = 0;
                    break;
                case SCROLL_STATE_DRAGGING:
                case SCROLL_STATE_SETTLING:
            }
        }

        protected void selectCenterTabForRightScroll() {
            int first = mLinearLayoutManager.findFirstVisibleItemPosition();
            int last = mLinearLayoutManager.findLastVisibleItemPosition();
            int center = mRecyclerTabLayout.getWidth() / 2;
            for (int position = first; position <= last; position++) {
                View view = mLinearLayoutManager.findViewByPosition(position);
                if (view.getLeft() + view.getWidth() >= center) {
                    mRecyclerTabLayout.setCurrentItem(position, false);
                    break;
                }
            }
        }

        protected void selectCenterTabForLeftScroll() {
            int first = mLinearLayoutManager.findFirstVisibleItemPosition();
            int last = mLinearLayoutManager.findLastVisibleItemPosition();
            int center = mRecyclerTabLayout.getWidth() / 2;
            for (int position = last; position >= first; position--) {
                View view = mLinearLayoutManager.findViewByPosition(position);
                if (view.getLeft() <= center) {
                    mRecyclerTabLayout.setCurrentItem(position, false);
                    break;
                }
            }
        }
    }

    protected class ViewPagerOnPageChangeListener implements ViewPager.OnPageChangeListener {

        private final RecyclerTabLayout mRecyclerTabLayout;
        private int mScrollState;

        public ViewPagerOnPageChangeListener(RecyclerTabLayout recyclerTabLayout) {
            mRecyclerTabLayout = recyclerTabLayout;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            mRecyclerTabLayout.scrollToTab(position, positionOffset, false);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mScrollState = state;
        }

        @Override
        public void onPageSelected(int position) {
            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                if (mRecyclerTabLayout.mIndicatorPosition != position) {
                    mRecyclerTabLayout.scrollToTab(position);
                }
            }
        }
    }

    public static abstract class Adapter<T extends RecyclerView.ViewHolder>
            extends RecyclerView.Adapter<T> {

        private List<TabMenuBean> mTabList;
        protected int mIndicatorPosition;

        public Adapter(List<TabMenuBean> tabList) {
            mTabList = tabList;
        }

        public List<TabMenuBean> getTabList() {
            return mTabList;
        }

        public void setCurrentIndicatorPosition(int indicatorPosition) {
            mIndicatorPosition = indicatorPosition;
        }

        public int getCurrentIndicatorPosition() {
            return mIndicatorPosition;
        }
    }

    private class DefaultAdapter extends RecyclerTabLayout.Adapter<DefaultAdapter.ViewHolder> {

        protected static final int MAX_TAB_TEXT_LINES = 2;

        int mTabPaddingStart;
        int mTabPaddingTop;
        int mTabPaddingEnd;
        int mTabPaddingBottom;
        int mTabTextAppearance;
        boolean mTabSelectedTextColorSet;
        int mTabSelectedTextColor;
        private int mTabMaxWidth;
        private int mTabMinWidth;
        private int mTabBackgroundResId;
        private int mTabOnScreenLimit;

        public DefaultAdapter(List<TabMenuBean> tabList) {
            super(tabList);
        }

        @SuppressWarnings("deprecation")
        @Override
        public DefaultAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_tab_item, null);
            TextView tabTextView = (TextView) view.findViewById(R.id.tab_item_title_tv);
            tabTextView.setGravity(Gravity.CENTER);
            tabTextView.setMaxLines(MAX_TAB_TEXT_LINES);
            tabTextView.setEllipsize(TextUtils.TruncateAt.END);

            if (mTabOnScreenLimit > 0) {
                int width = parent.getMeasuredWidth() / mTabOnScreenLimit - mTabMarginStartEnd;//减去左右的margin 值
                tabTextView.setMaxWidth(width);
                tabTextView.setMinWidth(width);
                mTabMaxWidth = width;
                mTabMinWidth = width;
            }
//            else {
//                if (mTabMaxWidth > 0) {
//                    tabTextView.setMaxWidth(mTabMaxWidth);
//                }
//                tabTextView.setMinWidth(mTabMinWidth);
//            }
            if (mTabSelectedTextColorSet) {
                final int[][] states = new int[2][];
                final int[] colors = new int[2];
                states[0] = SELECTED_STATE_SET;
                colors[0] = mTabSelectedTextColor;
                // Default enabled state
                states[1] = EMPTY_STATE_SET;
                //设置默认文字颜色
                colors[1] = mTabDefaultTextColor != 0 ? mTabDefaultTextColor : getResources().getColor(R.color.color_999999);
                ColorStateList colorStateList = new ColorStateList(states, colors);
                tabTextView.setTextColor(colorStateList);
            }
            if (mTabBackgroundResId != 0) {
                view.setBackgroundDrawable(
                        AppCompatResources.getDrawable(tabTextView.getContext(), mTabBackgroundResId));
            }
            view.setLayoutParams(createLayoutParamsForTabs(mTabMinWidth));
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(DefaultAdapter.ViewHolder holder, int position) {
            holder.title.setText(getTabList().get(position).getTitle());
            holder.title.setSelected(getCurrentIndicatorPosition() == position);
            if (getTabList().get(position).getCount() > 0) {
                holder.mCountTv.setVisibility(View.VISIBLE);
                if (getTabList().get(position).getCount() > 99) {
                    holder.mCountTv.setText("99+");
                } else {
                    holder.mCountTv.setText(String.valueOf(getTabList().get(position).getCount()));
                }
            } else {
                holder.mCountTv.setVisibility(View.GONE);
            }

        }

        @Override
        public int getItemCount() {
            return getTabList().size();
        }

        public void setTabPadding(int tabPaddingStart, int tabPaddingTop, int tabPaddingEnd,
                                  int tabPaddingBottom) {
            mTabPaddingStart = tabPaddingStart;
            mTabPaddingTop = tabPaddingTop;
            mTabPaddingEnd = tabPaddingEnd;
            mTabPaddingBottom = tabPaddingBottom;
        }

        public void setTabTextAppearance(int tabTextAppearance) {
            mTabTextAppearance = tabTextAppearance;
        }

        public void setTabSelectedTextColor(boolean tabSelectedTextColorSet,
                                            int tabSelectedTextColor) {
            mTabSelectedTextColorSet = tabSelectedTextColorSet;
            mTabSelectedTextColor = tabSelectedTextColor;
        }

        public void setTabMaxWidth(int tabMaxWidth) {
            mTabMaxWidth = tabMaxWidth;
        }

        public void setTabMinWidth(int tabMinWidth) {
            mTabMinWidth = tabMinWidth;
        }

        void setTabBackgroundResId(int tabBackgroundResId) {
            mTabBackgroundResId = tabBackgroundResId;
        }

        void setTabOnScreenLimit(int tabOnScreenLimit) {
            mTabOnScreenLimit = tabOnScreenLimit;
        }

        RecyclerView.LayoutParams createLayoutParamsForTabs(int width) {
            return new RecyclerView.LayoutParams(width, LayoutParams.MATCH_PARENT);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView title;
            public TextView mCountTv;

            public ViewHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.tab_item_title_tv);
                mCountTv = (TextView) itemView.findViewById(R.id.tab_item_msg_count_tv);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mTabClickAble) {
                            int pos = getAdapterPosition();
                            if (pos != NO_POSITION) {
                                mViewPager.setCurrentItem(pos, false);
                            }
                        }
                    }
                });
            }
        }
    }
}
