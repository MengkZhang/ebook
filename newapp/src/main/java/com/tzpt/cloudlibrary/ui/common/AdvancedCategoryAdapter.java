package com.tzpt.cloudlibrary.ui.common;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.bean.ClassifyInfo;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 通用字符串列表
 * Created by ZhiqiangJia on 2017-09-18.
 */
public class AdvancedCategoryAdapter extends RecyclerArrayAdapter<ClassifyInfo> {

    public AdvancedCategoryAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<ClassifyInfo>(parent, R.layout.view_advance_category_list_item) {
            @Override
            public void setData(ClassifyInfo item) {
                String name;
                if (TextUtils.isEmpty(item.code)) {
                    name = item.name;
                } else {
                    name = item.code + " " + item.name;
                }
                holder.setText(R.id.sel_item_title_tv, TextUtils.isEmpty(name) ? "" : name);
            }
        };
    }
}
