package com.tzpt.cloudlibrary.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RadioButton;

/**
 * Created by Administrator on 2017/6/12.
 */

public class DrawableCenterRadioButton extends RadioButton {
    public DrawableCenterRadioButton(Context context) {
        super(context);
    }

    public DrawableCenterRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawableCenterRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable[] drawables = getCompoundDrawables();
        Drawable drawableLeft = drawables[1];
        if (drawableLeft != null) {
            int textHeight = (int) (getPaint().descent() -  getPaint().ascent());
            int drawablePadding = getCompoundDrawablePadding();
            int drawableHeight = drawableLeft.getIntrinsicHeight();
            float bodyHeight = textHeight + drawableHeight + drawablePadding;
            canvas.translate(0, (getHeight() - bodyHeight) / 2);
        }
        super.onDraw(canvas);
    }
}
