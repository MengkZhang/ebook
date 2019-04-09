package com.tzpt.cloudlibrary.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.ImageView;

import com.tzpt.cloudlibrary.R;

/**
 * Created by Administrator on 2018/11/28.
 */

public class SquareImageView extends ImageView {

    protected int mImageMargin;

    public SquareImageView(Context context) {
        this(context, null);
    }

    public SquareImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SquareImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SquareImageView);
        mImageMargin = a.getDimensionPixelSize(R.styleable.SquareImageView_image_margin, 0);
        a.recycle();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));

        int childWidthSize = getMeasuredWidth();
        int childHeightSize = getMeasuredHeight();
        //高度和宽度一样
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        @SuppressLint("DrawAllocation")
        DisplayMetrics dm = new DisplayMetrics();
        assert wm != null;
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        if (childHeightSize < width - mImageMargin) {
            heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(childHeightSize, MeasureSpec.EXACTLY);
        } else {
            heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(width - mImageMargin, MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
