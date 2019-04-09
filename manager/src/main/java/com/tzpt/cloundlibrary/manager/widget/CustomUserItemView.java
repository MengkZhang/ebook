package com.tzpt.cloundlibrary.manager.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;

/**
 * 自定义账户列表item
 */
public class CustomUserItemView extends FrameLayout {

    private View mItemLine;
    private ImageView mItemIcon;
    private TextView mItemNameTv;
    private TextView mItemCountTv;

    private String mItemName;
    private int mItemIconResource;
    private boolean mHasItemLine;

    public CustomUserItemView(Context context) {
        super(context);
        init(null, 0);
    }

    public CustomUserItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CustomUserItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        if (null != attrs) {
            final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomUserItemView, defStyle, 0);
            try {
                mItemName = a.getString(R.styleable.CustomUserItemView_itemName);
                mItemIconResource = a.getResourceId(R.styleable.CustomUserItemView_itemIcon, -1);
                mHasItemLine = a.getBoolean(R.styleable.CustomUserItemView_hasItemLine, false);
            } finally {
                a.recycle();
            }
            initView();
            initData();
        }
    }

    private void initView() {
        if (isInEditMode()) {
            return;
        }
        View v = LayoutInflater.from(getContext()).inflate(R.layout.view_custom_user_item, this);
        mItemLine = v.findViewById(R.id.item_line_v);
        mItemIcon = (ImageView) v.findViewById(R.id.account_item_img);
        mItemNameTv = (TextView) v.findViewById(R.id.account_item_name_tv);
        mItemCountTv = (TextView) v.findViewById(R.id.account_item_count_tv);

    }

    private void initData() {
        mItemNameTv.setText(mItemName);
        mItemIcon.setImageResource(mItemIconResource);
        mItemLine.setVisibility(mHasItemLine ? VISIBLE : GONE);
    }

    public void setCountNumber(int count) {
        mItemCountTv.setVisibility(count != 0 ? VISIBLE : GONE);
        mItemCountTv.setText((count > 99) ? "99+" : String.valueOf(count));
    }
}
