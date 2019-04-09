package com.tzpt.cloudlibrary.widget.searchview;

import android.content.Context;
import android.util.AttributeSet;

import com.nineoldandroids.view.ViewHelper;

/**
 * Created by ZhiqiangJia on 2017-09-15.
 */
public class CancelTextView extends android.support.v7.widget.AppCompatTextView {
    public CancelTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CancelTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CancelTextView(Context context) {
        super(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        ViewHelper.setTranslationX(this, w);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
