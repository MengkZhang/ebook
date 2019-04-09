package com.tzpt.cloudlibrary.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;

/**
 * Created by Administrator on 2017/12/20.
 */

public class CustomUserGridMenu extends LinearLayout {
    private ImageView mMenuIcon;
    private TextView mMenuNameTv;
    private TextView mMenuInfoTv;

    public CustomUserGridMenu(Context context) {
        this(context, null);
    }

    public CustomUserGridMenu(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomUserGridMenu(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.view_user_grid_menu, this);
        mMenuIcon = (ImageView) v.findViewById(R.id.grid_menu_icon_iv);
        mMenuNameTv = (TextView) v.findViewById(R.id.grid_menu_name_tv);
        mMenuInfoTv = (TextView) v.findViewById(R.id.grid_menu_info_tv);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.UserGridMenu);
        try {
            int resId = ta.getResourceId(R.styleable.UserGridMenu_menu_icon, 0);
            if (resId != 0) {
                mMenuIcon.setImageResource(resId);
            }
            String name = ta.getString(R.styleable.UserGridMenu_menu_name);
            if (!TextUtils.isEmpty(name)) {
                mMenuNameTv.setText(name);
            }
        } finally {
            ta.recycle();
        }

    }

    public void setInfo(CharSequence info) {
        mMenuInfoTv.setText(info);
    }

    /**
     * 加载html 便于给同一个TXT设置不同的颜色
     *
     * @param isHtml ：是否是html
     * @param info   ：html的内容
     */
    public void setInfo(boolean isHtml, String info) {
        if (isHtml) {
            mMenuInfoTv.setText(Html.fromHtml(info));
        }
    }
}
