package com.tzpt.cloudlibrary.ui.account.borrow;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.business_bean.BorrowCategoryBean;

import java.util.List;

/**
 * Created by Administrator on 2018/11/5.
 */

public class BorrowCategoryAdapter extends BaseAdapter {
    private Context mContext;
    private List<BorrowCategoryBean> mData;

    public BorrowCategoryAdapter(Context context, List<BorrowCategoryBean> list) {
        mContext = context;
        mData = list;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.view_titlebar_category, null);
            holder.mSelItemTitleTv = (TextView) convertView.findViewById(R.id.sel_item_title_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mSelItemTitleTv.setText(mData.get(position).mName);

        if (mData.get(position).mIsSelected) {
            holder.mSelItemTitleTv.setTextColor(Color.parseColor("#8a623d"));
        } else {
            holder.mSelItemTitleTv.setTextColor(Color.parseColor("#333333"));
        }
        return convertView;
    }

    class ViewHolder {
        TextView mSelItemTitleTv;
    }
}
