package com.tzpt.cloudlibrary.widget.usertopbar;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.utils.StringUtils;

/**
 * 我的顶部工具栏-我的与名片公用
 * Created by tonyjia on 2018/11/7.
 */
public class UserCommonTopBar extends LinearLayout {
    public UserCommonTopBar(Context context) {
        this(context, null);
    }

    public UserCommonTopBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UserCommonTopBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    private ImageView mUserHeadIv;
    private ImageView mUserRightArrowIv;
    private TextView mUserNameTv;
    private TextView mUserNickNameTv;
    private TextView mUserPhoneTv;
    private TextView mUserPhoneNumberTv;

    private void initViews(Context context) {
        inflate(context, R.layout.view_common_user_top_bar, this);
        mUserHeadIv = (ImageView) findViewById(R.id.user_top_head_iv);
        mUserRightArrowIv = (ImageView) findViewById(R.id.user_top_arrow_iv);
        mUserNameTv = (TextView) findViewById(R.id.user_top_name_tv);
        mUserNickNameTv = (TextView) findViewById(R.id.user_top_nick_name_tv);
        mUserPhoneTv = (TextView) findViewById(R.id.user_top_phone_tv);
        mUserPhoneNumberTv = (TextView) findViewById(R.id.user_top_phone_number_tv);
    }

    /**
     * 获取用户头像imageView
     *
     * @return
     */
    public ImageView getUserHeadImageView() {
        return mUserHeadIv;
    }

    /**
     * 设置为未登录状态
     */
    public void setLoginOutState() {
        mUserNameTv.setText("点击登录");
        mUserNickNameTv.setText("");
        mUserPhoneTv.setText("");
        mUserPhoneTv.setVisibility(View.GONE);
        mUserPhoneNumberTv.setText("");
        mUserPhoneNumberTv.setVisibility(View.GONE);
        mUserHeadIv.setImageResource(R.mipmap.ic_photo_pressed);
    }

    /**
     * 设置用户昵称
     *
     * @param readerNickName 昵称
     */
    public void setUserNickName(String readerNickName) {
        mUserNameTv.setText("昵称\u3000");
        mUserNickNameTv.setText(readerNickName);
    }

    /**
     * 设置用户电话号码
     *
     * @param phone
     */
    public void setUserPhone(String phone) {
        mUserPhoneTv.setVisibility(View.VISIBLE);
        mUserPhoneNumberTv.setVisibility(View.VISIBLE);
        mUserPhoneTv.setText("手机号");
        mUserPhoneNumberTv.setText(TextUtils.isEmpty(phone) ? "暂未绑定" : StringUtils.formatTel(phone));
    }

    /**
     * 隐藏右边箭头
     */
    public void hideRightArrow() {
        mUserRightArrowIv.setVisibility(View.GONE);
    }

}
