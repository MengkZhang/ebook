package com.tzpt.cloudlibrary.widget;

import android.content.Context;
import android.graphics.Rect;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

/**
 * Created by Administrator on 2018/2/3.
 */

public class ScrollEditText extends EditText {
    public ScrollEditText(Context context) {
        this(context, null);
    }

    public ScrollEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE)
            this.getParent().requestDisallowInterceptTouchEvent(true);
        return super.onTouchEvent(event);
    }
}
