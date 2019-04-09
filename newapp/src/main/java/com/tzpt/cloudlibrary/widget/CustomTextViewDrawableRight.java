package com.tzpt.cloudlibrary.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Administrator on 2018/2/2.
 */

@SuppressLint("AppCompatCustomView")
public class CustomTextViewDrawableRight extends TextView {
    public CustomTextViewDrawableRight(Context context) {
        this(context, null);
    }

    public CustomTextViewDrawableRight(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomTextViewDrawableRight(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
            setPadding(0, 0, (int)(getWidth() - bodyWith), 0);
        }
        super.onDraw(canvas);
    }
}
