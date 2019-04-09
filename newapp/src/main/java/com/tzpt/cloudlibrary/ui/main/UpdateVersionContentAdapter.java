package com.tzpt.cloudlibrary.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;

import java.util.List;

/**
 * Created by Administrator on 2017/12/7.
 */

public class UpdateVersionContentAdapter extends BaseAdapter {
    private List<String> mData;
    private Context mContext;

    public UpdateVersionContentAdapter(Context context, List<String> list) {
        mData = list;
        mContext = context;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.view_update_content_item, null);
            holder.mContentTv = (TextView) convertView.findViewById(R.id.update_content_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mContentTv.setText(mData.get(position));
        return convertView;
    }

    private class ViewHolder {
        TextView mContentTv;
    }
}
