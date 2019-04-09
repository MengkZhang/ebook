package com.tzpt.cloudlibrary.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/11/17.
 */

public class DrawableCenterTextView extends TextView {
    public DrawableCenterTextView(Context context) {
        super(context);
    }

    public DrawableCenterTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawableCenterTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable[] drawables = getCompoundDrawables();
        Drawable drawableLeft = drawables[0];
        if (drawableLeft != null) {
            float textWith = getPaint().measureText(getText().toString());
            int drawablePadding = getCompoundDrawablePadding();
            int drawableWidth = drawableLeft.getIntrinsicWidth();
            float bodyWith = textWith + drawableWidth + drawablePadding;
            canvas.translate((getWidth() - bodyWith) / 2, 0);
        }
        Drawable drawableTop = drawables[1];
        if (drawableTop != null) {
            float textHeight = getPaint().descent() - getPaint().ascent();
            int drawablePadding = getCompoundDrawablePadding();
            int drawableHeight = drawableTop.getIntrinsicHeight();
            float bodyHeight = textHeight + drawableHeight + drawablePadding;
            canvas.translate(0, (getHeight() - bodyHeight) / 2);
        }
        Drawable drawableRight = drawables[2];
        if (drawableRight != null) {
            float textWith = getPaint().measureText(getText().toString());
            int drawablePadding = getCompoundDrawablePadding();
            int drawableWidth = drawableRight.getIntrinsicWidth();
            float bodyWith = textWith + drawableWidth + drawablePadding;
            canvas.translate(-(getWidth() - bodyWith) / 2, 0);
        }
        Drawable drawableBottom = drawables[3];
        if (drawableBottom != null) {
            float textHeight = getPaint().descent() - getPaint().ascent();
            int drawablePadding = getCompoundDrawablePadding();
            int drawableHeight = drawableBottom.getIntrinsicHeight();
            float bodyHeight = textHeight + drawableHeight + drawablePadding;
            canvas.translate(0, (getHeight() - bodyHeight) / 2);
        }

        super.onDraw(canvas);

    }
}
