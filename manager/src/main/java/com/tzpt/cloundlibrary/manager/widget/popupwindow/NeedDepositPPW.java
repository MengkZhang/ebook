package com.tzpt.cloundlibrary.manager.widget.popupwindow;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import com.tzpt.cloundlibrary.manager.R;

/**
 * Created by Administrator on 2017/7/9.
 */

public class NeedDepositPPW extends PopupWindow implements View.OnClickListener {

    public NeedDepositPPW(Context context, View view) {
        View views = View.inflate(context, R.layout.ppw_need_deposit, null);

        setWidth(FrameLayout.LayoutParams.MATCH_PARENT);
        setHeight(FrameLayout.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new BitmapDrawable());
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);

        views.findViewById(R.id.shadow_layout_view).setOnClickListener(this);
        this.setAnimationStyle(R.style.PPWAnimation);
        setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });

        setContentView(views);
        showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.shadow_layout_view) {
            this.dismiss();
        }
    }


    /**
     * popUpWindow 显示位置
     *
     * @param anchorView
     * @param xoff
     * @param yoff
     */
    @Override
    public void showAsDropDown(View anchorView, int xoff, int yoff) {
        if (Build.VERSION.SDK_INT >= 24) {
            Rect rect = new Rect();
            anchorView.getGlobalVisibleRect(rect);
            int h = anchorView.getResources().getDisplayMetrics().heightPixels - rect.bottom;
            setHeight(h);
        }
        super.showAsDropDown(anchorView, xoff, yoff);
    }

    @Override
    public void showAsDropDown(View anchor) {
        if (Build.VERSION.SDK_INT >= 24) {
            Rect rect = new Rect();
            anchor.getGlobalVisibleRect(rect);
            int h = anchor.getResources().getDisplayMetrics().heightPixels - rect.bottom;
            setHeight(h);
        }
        super.showAsDropDown(anchor);
    }
}
