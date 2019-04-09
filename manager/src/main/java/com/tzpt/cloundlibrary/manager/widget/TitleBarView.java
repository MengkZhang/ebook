package com.tzpt.cloundlibrary.manager.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;

/**
 * Created by Administrator on 2017/6/20.
 */

public class TitleBarView extends LinearLayout {
    private TextView mTitleBarTv;
    private ImageButton mLeftBtn;
    private ImageButton mRightBtn;


    public TitleBarView(Context context) {
        this(context, null);
    }

    public TitleBarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        initAttrs(attrs);

    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.titlebar);
        try {
            int titleResId = a.getResourceId(R.styleable.titlebar_title_str, 0);
            if (titleResId != 0) {
                mTitleBarTv.setText(titleResId);
            }

            int leftBtnResId = a.getResourceId(R.styleable.titlebar_left_btn, 0);
            if (leftBtnResId != 0) {
                mLeftBtn.setImageResource(leftBtnResId);
            }

            int rightBtnResId = a.getResourceId(R.styleable.titlebar_right_btn, 0);
            if (rightBtnResId != 0) {
                mRightBtn.setImageResource(rightBtnResId);
            }

        } finally {
            a.recycle();
        }
    }

    private void initView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.view_title_bar, this);
        mTitleBarTv = (TextView) v.findViewById(R.id.titlebar_title_tv);
        mLeftBtn = (ImageButton) v.findViewById(R.id.titlebar_left_btn);
        mRightBtn = (ImageButton) v.findViewById(R.id.titlebar_right_btn);
    }

    public void setLeftBtnIcon(int resId) {
        mLeftBtn.setImageResource(resId);
    }

    public void setRightBtnIcon(int resId) {
        mRightBtn.setImageResource(resId);
    }

    public void setTitle(String title) {
        mTitleBarTv.setText(title);
    }

    public void setTitle(int resId) {
        mTitleBarTv.setText(resId);
    }

    public void setOnBtnClickListener(OnClickListener listener) {
        mLeftBtn.setOnClickListener(listener);
        mRightBtn.setOnClickListener(listener);
    }
}
