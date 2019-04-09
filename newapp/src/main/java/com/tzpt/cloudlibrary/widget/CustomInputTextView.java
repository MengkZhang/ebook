package com.tzpt.cloudlibrary.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.utils.AllCapTransformationMethod;

/**
 * 高级搜索文本控件
 * Created by tonyjia on 2018/12/7.
 */
public class CustomInputTextView extends LinearLayout {
    public CustomInputTextView(Context context) {
        this(context, null);
    }

    public CustomInputTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomInputTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
        initAttributeSet(context, attrs);
    }

    private TextView mInputNameTv;
    private TextView mInputContentTv;
    private EditText mInputContentEt;

    private void initViews(Context context) {
        inflate(context, R.layout.common_input_text_view, this);
        mInputNameTv = (TextView) findViewById(R.id.input_name_tv);
        mInputContentTv = (TextView) findViewById(R.id.input_content_tv);
        mInputContentEt = (EditText) findViewById(R.id.input_content_et);
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CustomInputTextView);
        String inputName = ta.getString(R.styleable.CustomInputTextView_input_name);
        setInputName(inputName);
        int contentLength = ta.getInt(R.styleable.CustomInputTextView_input_content_length, -1);
        if (contentLength != -1) {
            setInputEditLength(contentLength);
        }
        boolean showTextContentView = ta.getBoolean(R.styleable.CustomInputTextView_show_text_content, false);
        if (showTextContentView) {
            mInputContentTv.setVisibility(View.VISIBLE);
            mInputContentEt.setVisibility(View.GONE);
        }
        boolean showEditContentView = ta.getBoolean(R.styleable.CustomInputTextView_show_edit_content, false);
        if (showEditContentView) {
            mInputContentTv.setVisibility(View.GONE);
            mInputContentEt.setVisibility(View.VISIBLE);
            mInputContentEt.setCursorVisible(false);
        }
        ta.recycle();

        mInputContentEt.setOnClickListener(mCursorVisibleListener);
    }

    private OnClickListener mCursorVisibleListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            //显示游标
            mInputContentEt.setCursorVisible(true);
            mInputContentEt.setFocusable(true);
            mInputContentEt.requestFocus();
            mInputContentEt.setFocusableInTouchMode(true);
        }
    };

    /**
     * 设置模块内容
     *
     * @param name
     */
    public void setInputName(String name) {
        mInputNameTv.setText(name);
    }

    /**
     * 设置文本内容
     *
     * @param content
     */
    public void setInputTextContent(String content) {
        mInputContentTv.setText(content);
    }

    /**
     * 清空文本内容
     */
    public void clearInputContent() {
        mInputContentTv.setText("");
    }

    /**
     * 清空编辑文本内容
     */
    public void clearEditContent() {
        mInputContentEt.setText("");
    }

    /**
     * 设置编辑内容
     *
     * @param content
     */
    public void setInputEditContent(String content) {
        mInputContentEt.setText(content);
    }

    /**
     * 设置输入文本长度限制
     *
     * @param length
     */
    public void setInputEditLength(int length) {
        mInputContentEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
    }

    /**
     * 获取文本内容
     *
     * @return
     */
    public String getInputTextContent() {
        return mInputContentTv.getText().toString().trim();
    }

    /**
     * 设置输入文本内容
     *
     * @return
     */
    public String getInputEditContent() {
        return mInputContentEt.getText().toString().trim();
    }

    /**
     * 配置输入类型
     *
     * @param inputType
     */
    public void setInputType(int inputType) {
        mInputContentEt.setInputType(inputType);
    }


    /**
     * 配置大小写
     *
     * @param allCapTransformationMethod
     */
    public void setTransformationMethod(AllCapTransformationMethod allCapTransformationMethod) {
        mInputContentEt.setTransformationMethod(allCapTransformationMethod);
    }
}
