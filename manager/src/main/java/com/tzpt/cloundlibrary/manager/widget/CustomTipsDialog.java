package com.tzpt.cloundlibrary.manager.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;

/**
 * Created by Administrator on 2018/3/21.
 */

public class CustomTipsDialog extends Dialog implements View.OnClickListener {
    private static int default_width = 320; // 默认宽度
    private static int default_height = 160;// 默认高度
    private String mMsg;
    private TextView mShowSubTv;
    private TextView mBtnCancel;
    private TextView mBtnOK;

    public CustomTipsDialog(Context context) {
        super(context);
    }

    public CustomTipsDialog(Context context, int style, String msg) {
        this(context, default_width, default_height, style, msg);
    }

    public CustomTipsDialog(Context context, int style) {
        this(context, default_width, default_height, style, "");
    }

    public CustomTipsDialog(Context context, int width, int height, int style, String msg) {
        super(context, style);
        setContentView(R.layout.view_tips_dialog_layout);
        this.mMsg = msg;
        initWidgets();
        setCancelable(true);
    }

    /**
     * 初始化提示框中的控件
     */
    private void initWidgets() {
        mBtnOK = (TextView) findViewById(R.id.btn_ok);
        mBtnCancel = (TextView) findViewById(R.id.btn_cancel);
        mShowSubTv = (TextView) findViewById(R.id.dlg_tv_text);

        mBtnCancel.setOnClickListener(this);
        mBtnOK.setOnClickListener(this);
    }

    public void setSubTips(String subTips) {
        mShowSubTv.setText(subTips);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                if (listener != null) {
                    listener.onClickOk();
                }
                break;
            case R.id.btn_cancel:
                if (listener != null) {
                    listener.onClickCancel();
                }
                break;
            default:
                break;
        }
    }

    public void setOnClickBtnListener(OnClickBtnListener listener) {
        this.listener = listener;
    }

    private OnClickBtnListener listener = null;

    public interface OnClickBtnListener {

        void onClickOk();

        void onClickCancel();
    }
}
