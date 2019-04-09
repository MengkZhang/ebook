package com.tzpt.cloudlibrary.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.ItemView;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 自定义资讯webView header
 * Created by ZhiqiangJia on 2017-10-12.
 */
public class InformationWebView extends LinearLayout implements ItemView {

    private CustomWebView mCustomWebView;
    private TextView mCustomDiscussPreviousTv;
    private TextView mCustomDiscussNextTv;
    private TextView mReadCountTv;
    private TextView mPraiseCountTv;
    private View mHeaderView;

    public InformationWebView(Context context) {
        this(context, null);
    }

    public InformationWebView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InformationWebView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mHeaderView = LayoutInflater.from(context).inflate(R.layout.view_information_webview, null);
        mCustomWebView = (CustomWebView) mHeaderView.findViewById(R.id.custom_webview);
        mCustomDiscussPreviousTv = (TextView) mHeaderView.findViewById(R.id.custom_discuss_previous_tv);
        mCustomDiscussNextTv = (TextView) mHeaderView.findViewById(R.id.custom_discuss_next_tv);
        mReadCountTv = (TextView) mHeaderView.findViewById(R.id.read_count_tv);
        mPraiseCountTv = (TextView) mHeaderView.findViewById(R.id.praise_count_tv);
    }

    public CustomWebView getCustomWebView() {
        return mCustomWebView;
    }

    public void setPreviousTvTxt(CharSequence text) {
        mCustomDiscussPreviousTv.setText(text);
    }

    public void showPreviousIcon(boolean isShow) {
        if (isShow) {
            Drawable previous = getResources().getDrawable(R.mipmap.ic_information_arrow_up);
            previous.setBounds(0, 0, previous.getMinimumWidth(), previous.getMinimumHeight());
            mCustomDiscussPreviousTv.setCompoundDrawables(previous, null, null, null);
        } else {
            mCustomDiscussPreviousTv.setCompoundDrawables(null, null, null, null);
        }
    }

    public void showNextIcon(boolean isShow) {
        if (isShow) {
            Drawable next = getResources().getDrawable(R.mipmap.ic_information_arrow_down);
            next.setBounds(0, 0, next.getMinimumWidth(), next.getMinimumHeight());
            mCustomDiscussNextTv.setCompoundDrawables(null, null, next, null);
        } else {
            mCustomDiscussNextTv.setCompoundDrawables(null, null, null, null);
        }
    }

    public void setPreviousTvClickable(boolean clickable) {
        mCustomDiscussPreviousTv.setClickable(clickable);
    }

    public void setPreviousTvVisibility(int visibility) {
        mCustomDiscussPreviousTv.setVisibility(visibility);
    }

    public void setPreviousTvOnClickListener(OnClickListener listener) {
        mCustomDiscussPreviousTv.setOnClickListener(listener);
    }

    public void setNextTvTxt(CharSequence text) {
        mCustomDiscussNextTv.setText(text);
    }

    public void setNextTvClickable(boolean clickable) {
        mCustomDiscussNextTv.setClickable(clickable);
    }

    public void setNextTvVisibility(int visibility) {
        mCustomDiscussNextTv.setVisibility(visibility);
    }

    public void setNextTvOnClickListener(OnClickListener listener) {
        mCustomDiscussNextTv.setOnClickListener(listener);
    }

    public TextView getPraiseCountTv() {
        return mPraiseCountTv;
    }

    public TextView getReadCountTv() {
        return mReadCountTv;
    }


    @Override
    public View onCreateView(ViewGroup parent) {
        return mHeaderView;
    }

    @Override
    public void onBindView(View headerView) {

    }
}
