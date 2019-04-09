package com.tzpt.cloudlibrary.ui.search;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.bean.SearchTypeBean;

import java.util.List;

/**
 * Created by Administrator on 2017/11/7.
 */

public class SearchClassifyAdapter extends BaseAdapter {
    private Context mContext;
    private List<SearchTypeBean> mData;

    public SearchClassifyAdapter(Context context, List<SearchTypeBean> list) {
        mContext = context;
        mData = list;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.view_search_type_item, null);
            holder.mSelItemTitleTv = (TextView) view.findViewById(R.id.sel_item_title_tv);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.mSelItemTitleTv.setText(mData.get(i).mTypeName);

        if (mData.get(i).mIsSelected) {
            holder.mSelItemTitleTv.setTextColor(Color.parseColor("#8a623d"));
        } else {
            holder.mSelItemTitleTv.setTextColor(Color.parseColor("#999999"));
        }
        return view;
    }

    class ViewHolder {
        TextView mSelItemTitleTv;
    }
}
