package com.tzpt.cloudlibrary.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Administrator on 2018/6/22.
 */

public class DrawableRightCenterTextView extends TextView {
    public DrawableRightCenterTextView(Context context) {
        super(context);
    }

    public DrawableRightCenterTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawableRightCenterTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable[] drawables = getCompoundDrawables();
        Drawable drawableRight = drawables[2];
        if (drawableRight != null) {
            float textWith = getPaint().measureText(getText().toString());
            int drawablePadding = getCompoundDrawablePadding();
            int drawableWidth = drawableRight.getIntrinsicWidth();
            float bodyWith = textWith + drawableWidth + drawablePadding;
            canvas.translate(-(getWidth() - bodyWith) / 2, 0);
        }
        super.onDraw(canvas);

    }
}
