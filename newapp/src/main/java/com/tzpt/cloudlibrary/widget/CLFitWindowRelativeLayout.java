package com.tzpt.cloudlibrary.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.WindowInsets;
import android.widget.RelativeLayout;

/**
 * 解决fitsSystemWindows="true"，与全屏冲突问题，将System Window top 设置为0
 * 注：全屏模式下，SOFT_INPUT_ADJUST_RESIZE 会失效。所以必须设置fitSystemWindows == true
 * Created by tonyjia on 2018/12/27.
 */
public class CLFitWindowRelativeLayout extends RelativeLayout {

    private int[] mInsets = new int[4];

    public CLFitWindowRelativeLayout(Context context) {
        super(context);
    }

    public CLFitWindowRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CLFitWindowRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected boolean fitSystemWindows(Rect insets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mInsets[0] = insets.left;
            mInsets[1] = insets.top;
            mInsets[2] = insets.right;

            insets.left = 0;
            insets.top = 0;
            insets.right = 0;
        }
        return super.fitSystemWindows(insets);
    }

    @Override
    public final WindowInsets onApplyWindowInsets(WindowInsets insets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            mInsets[0] = insets.getSystemWindowInsetLeft();
            mInsets[1] = insets.getSystemWindowInsetTop();
            mInsets[2] = insets.getSystemWindowInsetRight();
            return super.onApplyWindowInsets(insets.replaceSystemWindowInsets(0, 0, 0,
                    insets.getSystemWindowInsetBottom()));
        } else {
            return insets;
        }
    }
}