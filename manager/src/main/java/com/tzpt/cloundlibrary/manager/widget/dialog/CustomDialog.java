package com.tzpt.cloundlibrary.manager.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;

/**
 * 自定义对话框
 * Created by Administrator on 2017/7/9.
 */

public class CustomDialog extends Dialog implements View.OnClickListener {
    private static int default_width = 320; // 默认宽度
    private static int default_height = 160;// 默认高度
    private String mShowText = null;
    private TextView mText = null;
    private View line;
    private TextView btnCancel;
    private TextView btnOK;

    public CustomDialog(Context context) {
        super(context);
    }

    public CustomDialog(Context context, int layout, int style, String msg) {
        this(context, default_width, default_height, layout, style, msg);
    }

    public CustomDialog(Context context, int width, int height, int layout,
                        int style, String msg) {
        super(context, style);
        // 设置内容
        setContentView(R.layout.dialog_layout);
        this.mShowText = msg;
        initWidgets();
        setCancelable(true);

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * 初始化提示框中的控件
     */
    private void initWidgets() {
        btnOK = (TextView) findViewById(R.id.btn_ok);
        btnCancel = (TextView) findViewById(R.id.btn_cancel);
        mText = (TextView) findViewById(R.id.dlg_tv_text);
        line = findViewById(R.id.item_line);
        mText.setText(mShowText);
        btnCancel.setOnClickListener(this);
        btnOK.setOnClickListener(this);
    }

    /**
     * 获取显示密度
     *
     * @param context
     * @return
     */
    public float getDensity(Context context) {
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        return dm.density;
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

    /**
     * 是否有cancel 控件
     *
     * @param hasCancel
     */
    public void hasNoCancel(Boolean hasCancel) {
        line.setVisibility(hasCancel ? View.VISIBLE : View.GONE);
        btnCancel.setVisibility(hasCancel ? View.VISIBLE : View.GONE);
        if (hasCancel) {
            btnOK.setBackgroundResource(R.drawable.bg_dialog_btn_right);
        } else {
            btnOK.setBackgroundResource(R.drawable.bg_dialog_btn_center);
        }
    }

    public void setTitleColor(int textColor) {
        mText.setTextColor(textColor);
    }

    public void setTextForHtml(CharSequence message) {
        mText.setText(message);
    }

    /**
     * 设置按钮text
     */
    public void setButtonTextConfirmOrYes(boolean show) {
        btnOK.setText(show ? "确定" : "是");
        btnCancel.setText(show ? "取消" : "否");
    }

    public void setButtonTextConfirmOrYes2(boolean show) {
        btnOK.setText("确定");
        btnCancel.setText(show ? "取消" : "退出");
    }

    /**
     * 确定删除
     */
    public void setButtonTextConfirmOrYesDelete() {
        btnOK.setText("确定删除");
        btnCancel.setText("");
    }

    public void setOkBtnTxt(String str) {
        btnOK.setText(str);
    }

    public void setCancelBtnTxt(String str) {
        btnCancel.setText(str);
    }

    public void setText(String message) {
        mText.setText(message);
    }

    public void setTextForHtml(String message) {
        mText.setText(Html.fromHtml(message));
    }

    public void setText(CharSequence text) {
        mText.setText(text);
    }

    public void setTextColor(int color) {
        mText.setTextColor(color);
    }
}
