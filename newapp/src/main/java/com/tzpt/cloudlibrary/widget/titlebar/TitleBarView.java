package com.tzpt.cloudlibrary.widget.titlebar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.utils.DpPxUtils;

/**
 * Created by Administrator on 2017/6/15.
 */

public class TitleBarView extends LinearLayout {
    private Button mLeftTxtBtn;
    private TextView mTitleBarTv;
    private ImageButton mLeftBtn;
    private ImageButton mRightBtn;
    private Button mRightTxtBtn;
    private View mDivider;

    public TitleBarView(Context context) {
        this(context, null);
    }

    public TitleBarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.view_title_bar, this);
        mLeftTxtBtn = (Button) v.findViewById(R.id.titlebar_left_txt_btn);
        mTitleBarTv = (TextView) v.findViewById(R.id.titlebar_title_tv);
        mLeftBtn = (ImageButton) v.findViewById(R.id.titlebar_left_btn);
        mRightBtn = (ImageButton) v.findViewById(R.id.titlebar_right_btn);
        mRightTxtBtn = (Button) v.findViewById(R.id.titlebar_right_txt_btn);
        mDivider = v.findViewById(R.id.titlebar_divider);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TitleBarView);
        try {
            boolean rightBtnIsTxt = ta.getBoolean(R.styleable.TitleBarView_rightBtnIsTxt, false);
            String rightBtnStr = ta.getString(R.styleable.TitleBarView_rightBtnText);
            if (rightBtnIsTxt) {
                mRightTxtBtn.setVisibility(View.VISIBLE);
                mRightBtn.setVisibility(View.GONE);
                mRightTxtBtn.setText(rightBtnStr);
                mRightTxtBtn.measure(0, 0);
                int padding = mRightTxtBtn.getMeasuredWidth();
                if (padding > 0) {
                    mTitleBarTv.setPadding(padding, 0, padding, 0);
                }
            } else {
                mRightTxtBtn.setVisibility(View.GONE);
                mRightBtn.setVisibility(View.VISIBLE);
            }
        } finally {
            ta.recycle();
        }

    }

    public void setLeftBtnText(int resId) {
        mLeftTxtBtn.setText(resId);
        mLeftTxtBtn.setVisibility(View.VISIBLE);
        mLeftBtn.setVisibility(View.GONE);
    }

    public void setLeftBtnText(String txt) {
        mLeftTxtBtn.setText(txt);
        mLeftTxtBtn.setVisibility(View.VISIBLE);
        mLeftBtn.setVisibility(View.GONE);
    }

    public void clearLeftBtnTxt() {
        mLeftTxtBtn.setVisibility(View.GONE);
        mLeftBtn.setVisibility(View.VISIBLE);
    }

    public void setLeftBtnIcon(int resId) {
        mLeftBtn.setImageResource(resId);
    }

    public void setRightBtnIcon(int resId) {
        mRightBtn.setImageResource(resId);
        mRightTxtBtn.setVisibility(View.GONE);
        mRightBtn.setVisibility(View.VISIBLE);
    }

    public void setRightBtnClickAble(boolean clickAble) {
        mRightBtn.setClickable(clickAble);
    }

    public void setRightBtnTextClickAble(boolean clickAble) {
        mRightTxtBtn.setClickable(clickAble);
    }

    public void setTitle(String title) {
        mTitleBarTv.setText(title);
    }

    public void setTitle(int resId) {
        mTitleBarTv.setText(resId);
    }

    public void setLeftTxtBtnVisibility(int visibility) {
        mLeftBtn.setVisibility(visibility);
    }

    public void setRightTxtBtnVisibility(int visibility) {
        mRightTxtBtn.setVisibility(visibility);
        if (visibility == View.GONE
                || visibility == View.INVISIBLE) {
            mTitleBarTv.setPadding((int) DpPxUtils.dipToPx(getContext(), 16), 0,
                    (int) DpPxUtils.dipToPx(getContext(), 16), 0);
        } else {
            mRightTxtBtn.measure(0, 0);
            int padding = mRightTxtBtn.getMeasuredWidth();
            if (padding > 0) {
                mTitleBarTv.setPadding(padding, 0, padding, 0);
            }
        }
    }

    public void setRightBtnText(String txt) {
        mRightTxtBtn.setText(txt);
        mRightTxtBtn.setVisibility(View.VISIBLE);
        mRightBtn.setVisibility(View.GONE);
    }

    public void setRightBtnTextColor(int color) {
        mRightTxtBtn.setTextColor(color);
    }

    public void setRightBtnText(int resId) {
        mRightTxtBtn.setText(resId);
        mRightTxtBtn.setVisibility(View.VISIBLE);
        mRightBtn.setVisibility(View.GONE);
    }

    public void setRightBtnTextIcon(int resId) {
        Drawable flup = getContext().getResources().getDrawable(resId);
        flup.setBounds(0, 0, flup.getMinimumWidth(), flup.getMinimumHeight());
        mRightTxtBtn.setCompoundDrawablePadding(10);
        mRightTxtBtn.setCompoundDrawables(null, null, flup, null);
    }

    public void clearRightBtnTxt() {
        mRightTxtBtn.setVisibility(View.GONE);
        mRightBtn.setVisibility(View.VISIBLE);
    }

    public void clearRightBtnIcon() {
        mRightTxtBtn.setVisibility(View.VISIBLE);
        mRightBtn.setVisibility(View.GONE);
    }

    public void setDividerVisibility(int visibility) {
        mDivider.setVisibility(visibility);
    }

    public void setDividerColor(int colorId) {
        mDivider.setBackgroundColor(getResources().getColor(colorId));
    }
}
