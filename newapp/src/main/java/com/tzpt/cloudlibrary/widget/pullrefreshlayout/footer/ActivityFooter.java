package com.tzpt.cloudlibrary.widget.pullrefreshlayout.footer;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.widget.pullrefreshlayout.api.RefreshFooter;
import com.tzpt.cloudlibrary.widget.pullrefreshlayout.api.RefreshLayout;
import com.tzpt.cloudlibrary.widget.pullrefreshlayout.constant.RefreshState;
import com.tzpt.cloudlibrary.widget.pullrefreshlayout.constant.SpinnerStyle;
import com.tzpt.cloudlibrary.widget.pullrefreshlayout.internal.ArrowDrawable;
import com.tzpt.cloudlibrary.widget.pullrefreshlayout.internal.InternalClassics;
import com.tzpt.cloudlibrary.widget.pullrefreshlayout.internal.ProgressDrawable;
import com.tzpt.cloudlibrary.widget.pullrefreshlayout.util.DensityUtil;

/**
 * 活动上拉底部组件
 */

@SuppressWarnings({"unused", "UnusedReturnValue", "deprecation"})
public class ActivityFooter extends InternalClassics<ActivityFooter> implements RefreshFooter {

    private static String REFRESH_FOOTER_LOAD_NEXT_PAGE = null;
    private static String REFRESH_FOOTER_ALREADY_LATEST_PAGE = null;

    protected boolean mNoMoreData = false;

    //<editor-fold desc="LinearLayout">
    public ActivityFooter(Context context) {
        this(context, null);
    }

    public ActivityFooter(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActivityFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (REFRESH_FOOTER_LOAD_NEXT_PAGE == null) {
            REFRESH_FOOTER_LOAD_NEXT_PAGE = context.getString(R.string.ac_load_next_page);
        }
        if (REFRESH_FOOTER_ALREADY_LATEST_PAGE == null) {
            REFRESH_FOOTER_ALREADY_LATEST_PAGE = context.getString(R.string.ac_already_latest_page);
        }

        final View thisView = this;
        final View arrowView = mArrowView;
        final View progressView = mProgressView;
        final DensityUtil density = new DensityUtil();

        mTitleText.setTextColor(context.getResources().getColor(R.color.color_999999));
        mTitleText.setText(REFRESH_FOOTER_LOAD_NEXT_PAGE);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ClassicsFooter);

        LayoutParams lpArrow = (LayoutParams) arrowView.getLayoutParams();
        LayoutParams lpProgress = (LayoutParams) progressView.getLayoutParams();
        lpProgress.rightMargin = ta.getDimensionPixelSize(R.styleable.ClassicsFooter_srlDrawableMarginRight, density.dip2px(20));
        lpArrow.rightMargin = lpProgress.rightMargin;

        lpArrow.width = ta.getLayoutDimension(R.styleable.ClassicsFooter_srlDrawableArrowSize, lpArrow.width);
        lpArrow.height = ta.getLayoutDimension(R.styleable.ClassicsFooter_srlDrawableArrowSize, lpArrow.height);
        lpProgress.width = ta.getLayoutDimension(R.styleable.ClassicsFooter_srlDrawableProgressSize, lpProgress.width);
        lpProgress.height = ta.getLayoutDimension(R.styleable.ClassicsFooter_srlDrawableProgressSize, lpProgress.height);

        lpArrow.width = ta.getLayoutDimension(R.styleable.ClassicsFooter_srlDrawableSize, lpArrow.width);
        lpArrow.height = ta.getLayoutDimension(R.styleable.ClassicsFooter_srlDrawableSize, lpArrow.height);
        lpProgress.width = ta.getLayoutDimension(R.styleable.ClassicsFooter_srlDrawableSize, lpProgress.width);
        lpProgress.height = ta.getLayoutDimension(R.styleable.ClassicsFooter_srlDrawableSize, lpProgress.height);

        mFinishDuration = ta.getInt(R.styleable.ClassicsFooter_srlFinishDuration, mFinishDuration);
        mSpinnerStyle = SpinnerStyle.values()[ta.getInt(R.styleable.ClassicsFooter_srlClassicsSpinnerStyle, mSpinnerStyle.ordinal())];

        if (ta.hasValue(R.styleable.ClassicsFooter_srlDrawableArrow)) {
            mArrowView.setImageDrawable(ta.getDrawable(R.styleable.ClassicsFooter_srlDrawableArrow));
        } else {
            mArrowDrawable = new ArrowDrawable();
            mArrowDrawable.setColor(context.getResources().getColor(R.color.color_999999));
            mArrowView.setImageDrawable(mArrowDrawable);
        }

        if (ta.hasValue(R.styleable.ClassicsFooter_srlDrawableProgress)) {
            mProgressView.setImageDrawable(ta.getDrawable(R.styleable.ClassicsFooter_srlDrawableProgress));
        } else {
            mProgressDrawable = new ProgressDrawable();
            mProgressDrawable.setColor(context.getResources().getColor(R.color.color_999999));
            mProgressView.setImageDrawable(mProgressDrawable);
        }

        if (ta.hasValue(R.styleable.ClassicsFooter_srlTextSizeTitle)) {
            mTitleText.setTextSize(TypedValue.COMPLEX_UNIT_PX, ta.getDimensionPixelSize(R.styleable.ClassicsFooter_srlTextSizeTitle, DensityUtil.dp2px(16)));
        } else {
            mTitleText.setTextSize(16);
        }

        if (ta.hasValue(R.styleable.ClassicsFooter_srlPrimaryColor)) {
            setPrimaryColor(ta.getColor(R.styleable.ClassicsFooter_srlPrimaryColor, 0));
        }
        if (ta.hasValue(R.styleable.ClassicsFooter_srlAccentColor)) {
            setAccentColor(ta.getColor(R.styleable.ClassicsFooter_srlAccentColor, 0));
        }

        ta.recycle();

    }
    //</editor-fold>

    //<editor-fold desc="RefreshFooter">

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
        if (!mNoMoreData) {
            super.onStartAnimator(refreshLayout, height, maxDragHeight);
        }
    }

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        if (!mNoMoreData) {
            mTitleText.setText(success ? REFRESH_FOOTER_ALREADY_LATEST_PAGE : REFRESH_FOOTER_LOAD_NEXT_PAGE);
            return super.onFinish(layout, success);
        }
        return 0;
    }

    /**
     * ClassicsFooter 在(SpinnerStyle.FixedBehind)时才有主题色
     */
    @Override
    @Deprecated
    public void setPrimaryColors(@ColorInt int... colors) {
        if (mSpinnerStyle == SpinnerStyle.FixedBehind) {
            super.setPrimaryColors(colors);
        }
    }


    //设置是否加载更多
    public void setNoMoreDataTag(boolean noMoreData) {
        this.mNoMoreData = noMoreData;
    }

    /**
     * 设置数据全部加载完成，将不能再次触发加载功能
     */
    @Override
    public boolean setNoMoreData(boolean noMoreData) {
        if (mNoMoreData != noMoreData) {
            mNoMoreData = noMoreData;
            final View arrowView = mArrowView;
            if (noMoreData) {
                mTitleText.setText(REFRESH_FOOTER_ALREADY_LATEST_PAGE);
                arrowView.setVisibility(GONE);
            } else {
                mTitleText.setText(REFRESH_FOOTER_LOAD_NEXT_PAGE);
                arrowView.setVisibility(VISIBLE);
            }
        }
        return true;
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        final View arrowView = mArrowView;
        if (!mNoMoreData) {
            switch (newState) {
                case None:
                    arrowView.setVisibility(VISIBLE);
                case PullUpToLoad:
                    mTitleText.setText(REFRESH_FOOTER_LOAD_NEXT_PAGE);
                    arrowView.animate().rotation(180);
                    break;
                case Loading:
                case LoadReleased:
                    arrowView.setVisibility(GONE);
                    mTitleText.setText(REFRESH_FOOTER_LOAD_NEXT_PAGE);
                    break;
                case ReleaseToLoad:
                    mTitleText.setText(REFRESH_FOOTER_LOAD_NEXT_PAGE);
                    arrowView.animate().rotation(0);
                    break;
                case Refreshing:
                    mTitleText.setText(REFRESH_FOOTER_LOAD_NEXT_PAGE);
                    arrowView.setVisibility(GONE);
                    break;
            }
        } else {//设置为最后一页
            mTitleText.setText(REFRESH_FOOTER_ALREADY_LATEST_PAGE);
            arrowView.setVisibility(GONE);
        }
    }
    //</editor-fold>

}
