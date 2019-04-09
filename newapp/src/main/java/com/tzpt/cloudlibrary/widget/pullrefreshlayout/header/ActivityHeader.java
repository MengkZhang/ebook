package com.tzpt.cloudlibrary.widget.pullrefreshlayout.header;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.widget.pullrefreshlayout.api.RefreshHeader;
import com.tzpt.cloudlibrary.widget.pullrefreshlayout.api.RefreshLayout;
import com.tzpt.cloudlibrary.widget.pullrefreshlayout.constant.RefreshState;
import com.tzpt.cloudlibrary.widget.pullrefreshlayout.constant.SpinnerStyle;
import com.tzpt.cloudlibrary.widget.pullrefreshlayout.internal.ArrowDrawable;
import com.tzpt.cloudlibrary.widget.pullrefreshlayout.internal.InternalClassics;
import com.tzpt.cloudlibrary.widget.pullrefreshlayout.internal.ProgressDrawable;
import com.tzpt.cloudlibrary.widget.pullrefreshlayout.util.DensityUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * 活动下拉头部
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ActivityHeader extends InternalClassics<ActivityHeader> implements RefreshHeader {

    private static final byte ID_TEXT_UPDATE = 4;
    private static String REFRESH_HEADER_LOAD_LAST_PAGE = null;
    private static String REFRESH_HEADER_ALREADY_NO_ONE_PAGE = null;

    //<editor-fold desc="RelativeLayout">
    public ActivityHeader(Context context) {
        this(context, null);
    }

    public ActivityHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActivityHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (REFRESH_HEADER_LOAD_LAST_PAGE == null) {
            REFRESH_HEADER_LOAD_LAST_PAGE = context.getString(R.string.ac_load_last_page);
        }
        if (REFRESH_HEADER_ALREADY_NO_ONE_PAGE == null) {
            REFRESH_HEADER_ALREADY_NO_ONE_PAGE = context.getString(R.string.ac_already_no_one_page);
        }
        final View thisView = this;
        final View arrowView = mArrowView;
        final View progressView = mProgressView;
        final ViewGroup centerLayout = mCenterLayout;
        final DensityUtil density = new DensityUtil();

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ClassicsHeader);

        LayoutParams lpArrow = (LayoutParams) arrowView.getLayoutParams();
        LayoutParams lpProgress = (LayoutParams) progressView.getLayoutParams();
        LinearLayout.LayoutParams lpUpdateText = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lpUpdateText.topMargin = ta.getDimensionPixelSize(R.styleable.ClassicsHeader_srlTextTimeMarginTop, density.dip2px(0));
        lpProgress.rightMargin = ta.getDimensionPixelSize(R.styleable.ClassicsFooter_srlDrawableMarginRight, density.dip2px(20));
        lpArrow.rightMargin = lpProgress.rightMargin;

        lpArrow.width = ta.getLayoutDimension(R.styleable.ClassicsHeader_srlDrawableArrowSize, lpArrow.width);
        lpArrow.height = ta.getLayoutDimension(R.styleable.ClassicsHeader_srlDrawableArrowSize, lpArrow.height);
        lpProgress.width = ta.getLayoutDimension(R.styleable.ClassicsHeader_srlDrawableProgressSize, lpProgress.width);
        lpProgress.height = ta.getLayoutDimension(R.styleable.ClassicsHeader_srlDrawableProgressSize, lpProgress.height);

        lpArrow.width = ta.getLayoutDimension(R.styleable.ClassicsHeader_srlDrawableSize, lpArrow.width);
        lpArrow.height = ta.getLayoutDimension(R.styleable.ClassicsHeader_srlDrawableSize, lpArrow.height);
        lpProgress.width = ta.getLayoutDimension(R.styleable.ClassicsHeader_srlDrawableSize, lpProgress.width);
        lpProgress.height = ta.getLayoutDimension(R.styleable.ClassicsHeader_srlDrawableSize, lpProgress.height);

        mFinishDuration = ta.getInt(R.styleable.ClassicsHeader_srlFinishDuration, mFinishDuration);
        mSpinnerStyle = SpinnerStyle.values()[ta.getInt(R.styleable.ClassicsHeader_srlClassicsSpinnerStyle, mSpinnerStyle.ordinal())];

        if (ta.hasValue(R.styleable.ClassicsHeader_srlDrawableArrow)) {
            mArrowView.setImageDrawable(ta.getDrawable(R.styleable.ClassicsHeader_srlDrawableArrow));
        } else {
            mArrowDrawable = new ArrowDrawable();
            mArrowDrawable.setColor(context.getResources().getColor(R.color.color_999999));
            mArrowView.setImageDrawable(mArrowDrawable);
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlDrawableProgress)) {
            mProgressView.setImageDrawable(ta.getDrawable(R.styleable.ClassicsHeader_srlDrawableProgress));
        } else {
            mProgressDrawable = new ProgressDrawable();
            mProgressDrawable.setColor(context.getResources().getColor(R.color.color_999999));
            mProgressView.setImageDrawable(mProgressDrawable);
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlTextSizeTitle)) {
            mTitleText.setTextSize(TypedValue.COMPLEX_UNIT_PX, ta.getDimensionPixelSize(R.styleable.ClassicsHeader_srlTextSizeTitle, DensityUtil.dp2px(14)));
        } else {
            mTitleText.setTextSize(14);
        }

        if (ta.hasValue(R.styleable.ClassicsHeader_srlPrimaryColor)) {
            setPrimaryColor(ta.getColor(R.styleable.ClassicsHeader_srlPrimaryColor, 0));
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlAccentColor)) {
            setAccentColor(ta.getColor(R.styleable.ClassicsHeader_srlAccentColor, 0));
        }
        ta.recycle();
        mTitleText.setText(REFRESH_HEADER_LOAD_LAST_PAGE);
        try {//try 不能删除-否则会出现兼容性问题
            if (context instanceof FragmentActivity) {
                FragmentManager manager = ((FragmentActivity) context).getSupportFragmentManager();
                if (manager != null) {
                    @SuppressLint("RestrictedApi")
                    List<Fragment> fragments = manager.getFragments();
                    if (fragments != null && fragments.size() > 0) {
                        return;
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    //</editor-fold>

    //<editor-fold desc="RefreshHeader">
    //重写progress 方法，如果第一页则不执行progress动画
    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
        if (!mAlreadyNoOnePage) {
            super.onStartAnimator(refreshLayout, height, maxDragHeight);
        }
    }

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        if (success) {
            mTitleText.setText(REFRESH_HEADER_ALREADY_NO_ONE_PAGE);
        } else {
            mTitleText.setText(REFRESH_HEADER_LOAD_LAST_PAGE);
        }
        return super.onFinish(layout, success);//延迟500毫秒之后再弹回
    }

    private boolean mAlreadyNoOnePage = false;

    public void setAlreadyNoOnePage(boolean alreadyNoOnePage) {
        this.mAlreadyNoOnePage = alreadyNoOnePage;
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        final View arrowView = mArrowView;
        if (!mAlreadyNoOnePage) {
            switch (newState) {
                case None:
                case PullDownToRefresh:
                    mTitleText.setText(REFRESH_HEADER_LOAD_LAST_PAGE);
                    arrowView.setVisibility(VISIBLE);
                    arrowView.animate().rotation(0);
                    break;
                case Refreshing:
                case RefreshReleased:
                    mTitleText.setText(REFRESH_HEADER_LOAD_LAST_PAGE);
                    arrowView.setVisibility(GONE);
                    break;
                case ReleaseToRefresh:
                    mTitleText.setText(REFRESH_HEADER_LOAD_LAST_PAGE);
                    arrowView.animate().rotation(180);
                    break;
                case ReleaseToTwoLevel:
                    mTitleText.setText(REFRESH_HEADER_LOAD_LAST_PAGE);
                    arrowView.animate().rotation(0);
                    break;
                case Loading:
                    arrowView.setVisibility(GONE);
                    mTitleText.setText(REFRESH_HEADER_LOAD_LAST_PAGE);
                    break;
            }
        } else {
            arrowView.setVisibility(GONE);
            mTitleText.setText(REFRESH_HEADER_ALREADY_NO_ONE_PAGE);
        }
    }
    //</editor-fold>

    //<editor-fold desc="API">

    public ActivityHeader setAccentColor(@ColorInt int accentColor) {
        return super.setAccentColor(accentColor);
    }

    public ActivityHeader setTextSizeTime(float size) {
        if (mRefreshKernel != null) {
            mRefreshKernel.requestRemeasureHeightFor(this);
        }
        return this;
    }

    //</editor-fold>

}
