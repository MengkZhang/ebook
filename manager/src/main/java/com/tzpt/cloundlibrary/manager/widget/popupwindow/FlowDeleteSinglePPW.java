package com.tzpt.cloundlibrary.manager.widget.popupwindow;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.bean.FlowManageListBean;

/**
 * 流出删除操作
 * Created by ZhiqiangJia on 2017-07-12.
 */
public class FlowDeleteSinglePPW extends PopupWindow {

    private Button mFlowDirectDelete;
    private Button mFlowSingleDelete;
    private View mFlowDeleteOutLayout;

    public FlowDeleteSinglePPW(Context context, View view, final FlowManageListBean item,
                               final CallbackOutflowDelete callback) {
        super(context);
        View views = View.inflate(context, R.layout.ppw_outflow_delete, null);
        setWidth(FrameLayout.LayoutParams.MATCH_PARENT);
        setHeight(FrameLayout.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new BitmapDrawable());
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        this.setAnimationStyle(R.style.PPWAnimation);
        setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(views);
        showAtLocation(view, Gravity.BOTTOM, 0, 0);

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
        mFlowDeleteOutLayout = views.findViewById(R.id.flow_delete_out_layout);
        mFlowDirectDelete = (Button) views.findViewById(R.id.flow_direct_delete);
        mFlowSingleDelete = (Button) views.findViewById(R.id.flow_single_delete);

        mFlowDirectDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != callback) {
                    callback.callbackDirectDelete(item);
                }
                dismiss();
            }
        });
        mFlowSingleDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != callback) {
                    callback.callbackSingleDelete(item);
                }
                dismiss();
            }
        });
        mFlowDeleteOutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }


    public interface CallbackOutflowDelete {

        void callbackDirectDelete(FlowManageListBean item);

        void callbackSingleDelete(FlowManageListBean item);
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
