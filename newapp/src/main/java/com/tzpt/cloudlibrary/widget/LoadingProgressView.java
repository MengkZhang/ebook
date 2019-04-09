package com.tzpt.cloudlibrary.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.tzpt.cloudlibrary.R;

/**
 * loading view
 * Created by Administrator on 2017/6/15.
 */
public class LoadingProgressView extends LinearLayout {

    private RelativeLayout mProgressLayout;
    private int mBackGroundColorId;

    public LoadingProgressView(Context context) {
        this(context, null);
    }

    public LoadingProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.LoadingProgressView, defStyleAttr, 0);
            try {
                mBackGroundColorId = a.getColor(R.styleable.LoadingProgressView_backgroundColor, Color.parseColor("#10000000"));
            } finally {
                a.recycle();
            }
        }
        initView();
    }


    private void initView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.common_progress_view, this);
        mProgressLayout = (RelativeLayout) v.findViewById(R.id.progress_parent);
        mProgressLayout.setBackgroundColor(mBackGroundColorId);
        hideProgressLayout();
    }

    public void showProgressLayout() {
        mProgressLayout.setVisibility(VISIBLE);
    }

    public void hideProgressLayout() {
        mProgressLayout.setVisibility(GONE);
    }

}
