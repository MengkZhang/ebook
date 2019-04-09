package com.tzpt.cloudlibrary.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;

/**
 * 个人信息item view
 * Created by tonyjia on 2018/11/6.
 */
public class CustomUserInfoItemView extends RelativeLayout {

    public CustomUserInfoItemView(Context context) {
        this(context, null);
    }

    public CustomUserInfoItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomUserInfoItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
        initAttributeSet(context, attrs);
    }

    private TextView mUserInfoModelNameTv;
    private TextView mUserInfoNameTv;
    private ImageView mUserInfoHeadIv;
    private ImageView mUserInfoArrowIv;

    private void initViews(Context context) {
        inflate(context, R.layout.view_common_user_info, this);
        mUserInfoModelNameTv = (TextView) findViewById(R.id.user_info_model_name_tv);
        mUserInfoNameTv = (TextView) findViewById(R.id.user_info_name_tv);
        mUserInfoHeadIv = (ImageView) findViewById(R.id.user_info_head_iv);
        mUserInfoArrowIv = (ImageView) findViewById(R.id.user_info_arrow_iv);
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CustomUserInfoItemView);
        try {
            int itemStyle = ta.getInteger(R.styleable.CustomUserInfoItemView_item_style, -1);
            switch (itemStyle) {
                case 1:
                    showHeadStyle();
                    break;
                case 2:
                    showOnlyTextStyle();
                    break;
                case 0:
                default:
                    showNormalStyle();
                    break;
            }
            String modelName = ta.getString(R.styleable.CustomUserInfoItemView_model_name);
            if (!TextUtils.isEmpty(modelName)) {
                setModelName(modelName);
            }
            String name1 = ta.getString(R.styleable.CustomUserInfoItemView_item_name1);
            if (!TextUtils.isEmpty(name1)) {
                setItemName(name1);
            }
            int maxLength = ta.getInteger(R.styleable.CustomUserInfoItemView_max_length, -1);
            if (maxLength != -1) {
                setNameMaxLength(maxLength);
            }
        } finally {
            ta.recycle();
        }
    }

    /**
     * 设置名称长度
     *
     * @param maxLength
     */
    private void setNameMaxLength(int maxLength) {
        mUserInfoNameTv.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
    }

    /**
     * 设置模块名称
     *
     * @param modelName 模块名称
     */

    public void setModelName(String modelName) {
        mUserInfoModelNameTv.setText(modelName);
    }

    /**
     * 设置名称
     *
     * @param itemName 名称
     */
    public void setItemName(String itemName) {
        mUserInfoNameTv.setText(itemName);
    }

    /**
     * 设置hint
     *
     * @param hint
     */
    public void setItemNameHint(String hint) {
        mUserInfoNameTv.setHint(hint);
    }

    public ImageView getImageView() {
        return mUserInfoHeadIv;
    }

    /**
     * 设置头像样式
     */
    public void showHeadStyle() {
        mUserInfoNameTv.setVisibility(View.GONE);
        mUserInfoHeadIv.setVisibility(View.VISIBLE);
        mUserInfoArrowIv.setVisibility(View.VISIBLE);
    }

    /**
     * 设置一般样式
     */
    public void showNormalStyle() {
        mUserInfoNameTv.setVisibility(View.VISIBLE);
        mUserInfoHeadIv.setVisibility(View.GONE);
        mUserInfoArrowIv.setVisibility(View.VISIBLE);
    }

    /**
     * 设置只文本样式
     */
    public void showOnlyTextStyle() {
        mUserInfoNameTv.setVisibility(View.VISIBLE);
        mUserInfoHeadIv.setVisibility(View.GONE);
        mUserInfoArrowIv.setVisibility(View.INVISIBLE);
    }

}
