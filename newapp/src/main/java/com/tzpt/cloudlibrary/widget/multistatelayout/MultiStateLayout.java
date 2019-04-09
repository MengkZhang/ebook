package com.tzpt.cloudlibrary.widget.multistatelayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.tzpt.cloudlibrary.R;

/**
 * 多种类型view
 * Created by Administrator on 2017/6/15.
 */
public class MultiStateLayout extends LinearLayout {
    public final static int STATUS_PROGRESS = 0;
    public final static int STATUS_EMTPY = 1;
    public final static int STATUS_ERROR = 2;

    protected ViewGroup mProgressView;
    protected ViewGroup mEmptyView;
    protected ViewGroup mErrorView;
    private int mProgressId;
    private int mEmptyId;
    private int mErrorId;
    private int mStatus = -1;

    public MultiStateLayout(Context context) {
        this(context, null);
    }

    public MultiStateLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiStateLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MultiStateLayout, defStyleAttr, 0);
            try {
                mEmptyId = a.getResourceId(R.styleable.MultiStateLayout_layout_empty_view, 0);
                mProgressId = a.getResourceId(R.styleable.MultiStateLayout_layout_progress_view, 0);
                mErrorId = a.getResourceId(R.styleable.MultiStateLayout_layout_error_view, 0);
            } finally {
                a.recycle();
            }
        }
        initView();
    }


    private void initView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.common_multilayout_view, this);
        mProgressView = (ViewGroup) v.findViewById(R.id.progress);
        if (mProgressId != 0) LayoutInflater.from(getContext()).inflate(mProgressId, mProgressView);
        mEmptyView = (ViewGroup) v.findViewById(R.id.empty);
        if (mEmptyId != 0) LayoutInflater.from(getContext()).inflate(mEmptyId, mEmptyView);
        mErrorView = (ViewGroup) v.findViewById(R.id.error);
        if (mErrorId != 0) LayoutInflater.from(getContext()).inflate(mErrorId, mErrorView);
        hideAll();
    }

    private void hideAll() {
        mEmptyView.setVisibility(View.GONE);
        mProgressView.setVisibility(View.GONE);
        mErrorView.setVisibility(GONE);
    }


    public void showError() {
        setVisibility(VISIBLE);
        if (mErrorView.getChildCount() > 0) {
            hideAll();
            mErrorView.setVisibility(View.VISIBLE);
            mStatus = STATUS_ERROR;
        } else {
            showContentView();
        }
    }

    public void showRetryError(OnClickListener listener) {
        setVisibility(VISIBLE);
        if (mErrorView.getChildCount() > 0) {
            hideAll();
            mErrorView.setVisibility(View.VISIBLE);
            mErrorView.setOnClickListener(listener);
        } else {
            showContentView();
        }
    }

    public void showEmpty() {
        setVisibility(VISIBLE);
        if (mEmptyView.getChildCount() > 0) {
            hideAll();
            mEmptyView.setVisibility(View.VISIBLE);
            mStatus = STATUS_EMTPY;
        } else {
            showContentView();
        }
    }


    public void showProgress() {
        setVisibility(VISIBLE);
        if (mProgressView.getChildCount() > 0) {
            hideAll();
            mProgressView.setVisibility(View.VISIBLE);
            mStatus = STATUS_PROGRESS;
        } else {
            showContentView();
        }
    }

    public void showContentView() {
        mStatus = -1;
        setVisibility(GONE);
        hideAll();
    }

    public int getStatus(){
        return mStatus;
    }
}
