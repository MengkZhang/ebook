package com.tzpt.cloudlibrary.ui.common;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.bean.ClassifyInfo;
import com.tzpt.cloudlibrary.bean.ClassifyTwoLevelBean;
import com.tzpt.cloudlibrary.utils.DpPxUtils;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 通用字符串列表
 * Created by ZhiqiangJia on 2017-09-18.
 */
public class AdvancedTwoLevelCategoryAdapter extends RecyclerArrayAdapter<ClassifyTwoLevelBean> {

    private boolean mIsSubList = false;

    public AdvancedTwoLevelCategoryAdapter(Context context, boolean isSubList) {
        super(context);
        this.mIsSubList = isSubList;
    }

    private int mSelectPosition;

    public void setSelectPosition(int selectPosition) {
        this.mSelectPosition = selectPosition;
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<ClassifyTwoLevelBean>(parent, R.layout.view_advance_category_list_item) {
            @Override
            public void setData(ClassifyTwoLevelBean item) {
                holder.setText(R.id.sel_item_title_tv, TextUtils.isEmpty(item.mName) ? "" : item.mName);
                ((TextView) holder.getView(R.id.sel_item_title_tv)).setSingleLine();
                if (!mIsSubList) {
                    if (mSelectPosition == getAdapterPosition()) {
                        holder.setTextColor(R.id.sel_item_title_tv, mContext.getResources().getColor(R.color.color_8a623d));
                        setBackgroundColorRes(R.id.sel_item_title_tv, R.drawable.drawable_ffffff);
                    } else {
                        holder.setTextColor(R.id.sel_item_title_tv, mContext.getResources().getColor(R.color.color_333333));
                        setBackgroundColorRes(R.id.sel_item_title_tv, R.drawable.drawable_f4f4f4);
                    }
                } else {
                    holder.getView(R.id.sel_item_title_tv).setPadding(DpPxUtils.dp2px(60f), 0, 0, 0);
                    setBackgroundColorRes(R.id.sel_item_title_tv, R.drawable.drawable_ffffff);
                    if (mSelectPosition == getAdapterPosition()) {
                        holder.setTextColor(R.id.sel_item_title_tv, mContext.getResources().getColor(R.color.color_8a623d));
                    } else {
                        holder.setTextColor(R.id.sel_item_title_tv, mContext.getResources().getColor(R.color.color_333333));
                    }
                }
            }
        };
    }
}
