package com.tzpt.cloudlibrary.widget.searchview;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by ZhiqiangJia on 2017-09-27.
 */

public class DefaultTextView extends android.support.v7.widget.AppCompatTextView {


    private int mPadding10;

    public DefaultTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public DefaultTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DefaultTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        mPadding10 = dip2px(10);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float leftEdge = this.getLeft() - mPadding10 * 1.5f;
        this.setMinWidth(w);
        if (null != mListener) {
            mListener.callbackLeftEdge(leftEdge);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public interface LeftEdgeLister {
        void callbackLeftEdge(float leftEdge);
    }

    private LeftEdgeLister mListener;

    public void setTextLeftEdgeListener(LeftEdgeLister listener) {
        this.mListener = listener;
    }
}
