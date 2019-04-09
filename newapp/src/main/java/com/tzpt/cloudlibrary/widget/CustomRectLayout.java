package com.tzpt.cloudlibrary.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 *
 */
public class CustomRectLayout extends RelativeLayout {
    public CustomRectLayout(Context context) {
        super(context);
    }

    public CustomRectLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRectLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        int childWidthSize = getMeasuredWidth();
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);

        heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (childWidthSize * 0.548), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
