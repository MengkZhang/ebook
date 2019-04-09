package com.tzpt.cloudlibrary.ui.ebook;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.bean.BookMarkBean;

import java.util.List;

/**
 * Created by Administrator on 2017/10/23.
 */

class EBookMarkAdapter extends BaseAdapter {
    private Context mContext;
    private List<BookMarkBean> mData;

    EBookMarkAdapter(Context context, List<BookMarkBean> list) {
        mContext = context;
        mData = list;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(mData.size() - 1 - position);
    }

    @Override
    public long getItemId(int position) {
        return mData.size() - 1 - position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.view_book_mark_list_item, null);
            holder.mContentTv = (TextView) convertView.findViewById(R.id.content_tv);
            holder.mAddDateTv = (TextView) convertView.findViewById(R.id.add_date_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        BookMarkBean item = mData.get(mData.size() - 1 - position);
        holder.mContentTv.setText(item.getContent());
        holder.mAddDateTv.setText(item.getAddDate());

        return convertView;
    }

    class ViewHolder {
        TextView mContentTv;
        TextView mAddDateTv;
    }
}
