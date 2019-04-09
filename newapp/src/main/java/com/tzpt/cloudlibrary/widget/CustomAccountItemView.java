package com.tzpt.cloudlibrary.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;

/**
 * 自定义账户列表item
 * TODO: document your custom view class.
 */
public class CustomAccountItemView extends FrameLayout {

    private ImageView mItemIcon;
    private TextView mItemNameTv;
    private TextView mItemNumberTv;
    private TextView mItemOverdueTv;
    private ImageView mItemArrowIcon;
    private ImageView mItemRedIcon;

    private String mItemName;
    private int mItemIconResource;
    private boolean mHasItemArrowIcon;

    public CustomAccountItemView(Context context) {
        this(context, null);
    }

    public CustomAccountItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomAccountItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View v = LayoutInflater.from(context).inflate(R.layout.view_custom_account_item, this);
        mItemIcon = (ImageView) v.findViewById(R.id.account_item_img);
        mItemArrowIcon = (ImageView) v.findViewById(R.id.account_item_arrow_img);
        mItemRedIcon = (ImageView) v.findViewById(R.id.account_item_red_iv);
        mItemNameTv = (TextView) v.findViewById(R.id.account_item_name_tv);
        mItemNumberTv = (TextView) v.findViewById(R.id.account_item_number_tv);
        mItemOverdueTv = (TextView) v.findViewById(R.id.account_item_overdue_tv);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomAccountItemView, 0, 0);
        try {
            mItemName = a.getString(R.styleable.CustomAccountItemView_itemName);
            mItemIconResource = a.getResourceId(R.styleable.CustomAccountItemView_itemIcon, -1);
            mHasItemArrowIcon = a.getBoolean(R.styleable.CustomAccountItemView_hasItemArrowIcon, false);
        } finally {
            a.recycle();
        }
        initData();
    }

    private void initData() {
        mItemNameTv.setText(mItemName);
        mItemIcon.setImageResource(mItemIconResource);
        mItemArrowIcon.setVisibility(mHasItemArrowIcon ? VISIBLE : GONE);
    }

    public void setItemNumber(String itemNumber) {
        mItemNumberTv.setText(itemNumber);
        if (TextUtils.isEmpty(itemNumber)) {
            mItemNumberTv.setVisibility(GONE);
            return;
        }
        mItemNumberTv.setVisibility(VISIBLE);

    }

    public void setItemOverdueInfo(String overdueInfo) {
        mItemOverdueTv.setText(overdueInfo);
        if (TextUtils.isEmpty(overdueInfo)) {
            mItemOverdueTv.setVisibility(GONE);
            return;
        }
        mItemOverdueTv.setVisibility(VISIBLE);
    }

    public void setRedIconVisibility(boolean visibility) {
        mItemRedIcon.setVisibility(visibility ? VISIBLE : GONE);
    }
}
