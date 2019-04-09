package com.tzpt.cloudlibrary.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;

/**
 * Created by tonyjia on 2018/10/30.
 */
public class ExpandedTextLayout extends LinearLayout {
    public ExpandedTextLayout(Context context) {
        this(context, null);
    }

    public ExpandedTextLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandedTextLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context, attrs);
    }

    private TextView mExpandedTitleTv;
    private TextView mExpandedContentTv;
    private ImageView mExpandedArrowIv;
    private boolean mExpanded = false;
    private int mLimitMaxLines = Integer.MAX_VALUE;
    private Context mContext;
    private Animation operatingAnim1;
    private Animation operatingAnim2;

    private OnClickListener mArrowClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (null == operatingAnim1 || null == operatingAnim2) {
                initAnimation();
            }
            if (mExpanded) {
                mExpanded = false;
                mExpandedContentTv.setMaxLines(mLimitMaxLines);
                mExpandedArrowIv.startAnimation(operatingAnim2);
            } else {
                mExpanded = true;
                mExpandedContentTv.setMaxLines(Integer.MAX_VALUE);
                mExpandedArrowIv.startAnimation(operatingAnim1);
            }
            mExpandedContentTv.setEllipsize(TextUtils.TruncateAt.END);
            mExpandedContentTv.requestLayout();
        }
    };

    private void initViews(Context context, AttributeSet attrs) {
        mContext = context;
        inflate(context, R.layout.view_expanded_text, this);
        mExpandedTitleTv = (TextView) findViewById(R.id.expanded_title_tv);
        mExpandedContentTv = (TextView) findViewById(R.id.expanded_content_tv);
        mExpandedArrowIv = (ImageView) findViewById(R.id.expanded_arrow_iv);

        mExpandedArrowIv.setOnClickListener(mArrowClickListener);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ExpandedTextLayout);
        try {
            int limitMaxLines = ta.getInteger(R.styleable.ExpandedTextLayout_expanded_text_limit_lines, Integer.MAX_VALUE);
            String name = ta.getString(R.styleable.ExpandedTextLayout_expanded_text_title);
            boolean hasArrow = ta.getBoolean(R.styleable.ExpandedTextLayout_expanded_text_has_arrow, true);

            setArrowVisibility(hasArrow);
            if (limitMaxLines != 0) {
                setLimitMaxLines(limitMaxLines);
            } else {
                setLimitMaxLines(Integer.MAX_VALUE);
            }
            if (!TextUtils.isEmpty(name)) {
                mExpandedTitleTv.setText(name);
            }
        } finally {
            ta.recycle();
        }
    }

    public void setTitle(String title) {
        mExpandedTitleTv.setText(title);
    }

    public void setContent(CharSequence text) {
        mExpandedContentTv.setText(text);
    }

    public void setLimitMaxLines(int limitMaxLines) {
        mLimitMaxLines = limitMaxLines;
        mExpanded = false;
        mExpandedContentTv.setMaxLines(limitMaxLines);
        mExpandedContentTv.requestLayout();
    }

    public void setArrowVisibility(boolean visibility) {
        mExpandedArrowIv.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    public void hideExpandedView() {
        this.setVisibility(View.GONE);
    }

    public void hideContentArrowView() {
        mExpandedContentTv.setVisibility(View.GONE);
        mExpandedArrowIv.setVisibility(View.GONE);
    }

    /**
     * 初始化动画
     */
    private void initAnimation() {
        operatingAnim1 = AnimationUtils.loadAnimation(mContext, R.anim.roate_0_180);
        operatingAnim2 = AnimationUtils.loadAnimation(mContext, R.anim.roate_180_360);
        LinearInterpolator lin1 = new LinearInterpolator();
        LinearInterpolator lin2 = new LinearInterpolator();
        operatingAnim1.setInterpolator(lin1);
        operatingAnim1.setFillAfter(true);
        operatingAnim2.setInterpolator(lin2);
        operatingAnim2.setFillAfter(true);
    }

    /**
     * 是否大于行数限制
     *
     * @param limitCount 限制行数
     * @return
     */
    public boolean isLimitLineCount(int limitCount) {
        return mExpandedContentTv.getLineCount() <= limitCount;
    }

}
