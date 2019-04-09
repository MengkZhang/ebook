package com.tzpt.cloudlibrary.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2018/1/5.
 */

public class LinearLayoutForListView extends LinearLayout {
    private BaseAdapter mBaseAdapter;
    private OnItemClickListener mOnItemClickListener;

    public LinearLayoutForListView(Context context) {
        this(context, null);
    }

    public LinearLayoutForListView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LinearLayoutForListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void bindLinearLayout() {
        int count = mBaseAdapter.getCount();
        this.removeAllViews();
        for (int i = 0; i < count; i++) {
            View v = mBaseAdapter.getView(i, null, null);
            final int finalI = i;
            v.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onClick(finalI);
                    }
                }
            });
            addView(v, i);
        }
    }

    public void setAdapter(BaseAdapter adapter) {
        if (adapter != null) {
            mBaseAdapter = adapter;
        }
        bindLinearLayout();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }
}
