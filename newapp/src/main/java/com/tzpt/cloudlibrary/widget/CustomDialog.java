package com.tzpt.cloudlibrary.widget;

import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;

/**
 * 自定义对话框
 */
public class CustomDialog extends Dialog implements View.OnClickListener {
    private String mShowText = null;
    private TextView mContentTv = null;
    private View line;
    private TextView mCancelTv;
    private TextView mOKTv;
    private TextView mTitleTv;

    public CustomDialog(Context context) {
        super(context);
    }

    public CustomDialog(Context context, int style, String msg) {
        this(context, 320, 160, style, msg);
    }

    public CustomDialog(Context context, int style) {
        this(context, 320, 160, style, "");
    }

    public CustomDialog(Context context, int width, int height, int style, String msg) {
        super(context, style);
        // 设置内容
        setContentView(R.layout.view_dialog_layout);
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
        mTitleTv = (TextView) findViewById(R.id.dlg_tv_title);
        mOKTv = (TextView) findViewById(R.id.btn_ok);
        mCancelTv = (TextView) findViewById(R.id.btn_cancel);
        mContentTv = (TextView) findViewById(R.id.dlg_tv_text);
        line = findViewById(R.id.item_line);
        mContentTv.setText(mShowText);
        mCancelTv.setOnClickListener(this);
        mOKTv.setOnClickListener(this);
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
        mCancelTv.setVisibility(hasCancel ? View.VISIBLE : View.GONE);
        if (hasCancel) {
            mOKTv.setBackgroundResource(R.drawable.btn_right);
        } else {
            mOKTv.setBackgroundResource(R.drawable.btn_center);
        }
    }

    public void setTitleColor(int textColor) {
        mContentTv.setTextColor(textColor);
    }

    public void setText(Spanned message) {
        mContentTv.setText(message);
    }

    /**
     * 设置按钮text
     */
    public void setButtonTextConfirmOrYes(boolean show) {
        mOKTv.setText(show ? "确定" : "是");
        mCancelTv.setText(show ? "取消" : "否");
    }

    public void setTextForHtml(String message) {
        mContentTv.setText(Html.fromHtml(message));
    }

    /**
     * 设置拨打电话button text
     */
    public void setButtonTextForCallPhone() {
        mOKTv.setText("拨打");
        mCancelTv.setText("取消");
    }

    /**
     * 重新登录按钮
     */
    public void setLoginAnewBtn() {
        mOKTv.setText("重新登录");
        mCancelTv.setText("取消");
    }

    public void setBtnOKAndBtnCancelTxt(String okStr, String cancelStr) {
        mOKTv.setText(okStr);
        mCancelTv.setText(cancelStr);
    }

    public void setOkText(String okText) {
        mOKTv.setText(okText);
    }

    public void setCancelText(String cancelText) {
        mCancelTv.setText(cancelText);
    }

    public void setText(String message) {
        mContentTv.setText(message);
    }

    public void setText(CharSequence text) {
        mContentTv.setText(text);
    }

    public void setTitle(String title) {
        mTitleTv.setVisibility(View.VISIBLE);
        mTitleTv.setText(title);
    }

    /**
     * 设置标题文字style
     *
     * @param textStyle
     */
    public void setTitleTextStyle(int textStyle) {
        mTitleTv.setTextAppearance(getContext(), textStyle);
    }

    /**
     * 设置内容文字style
     *
     * @param textStyle
     */
    public void setContentTextStyle(int textStyle) {
        mContentTv.setTextAppearance(getContext(), textStyle);
    }
}
