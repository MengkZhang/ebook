package com.tzpt.cloundlibrary.manager.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.bean.StatisticsClassifyBean;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 读者民族选择
 */
public class ReaderNationListAdapter extends RecyclerArrayAdapter<String> {

    private View.OnClickListener mItemClickListener;

    public ReaderNationListAdapter(Context context, View.OnClickListener itemClickListener) {
        super(context);
        this.mItemClickListener = itemClickListener;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<String>(parent, R.layout.view_reader_nation_item) {
            @Override
            public void setData(final String nation) {
                //set data
                holder.setText(R.id.reader_nation_name_tv, nation);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setTag(nation);
                        holder.setChecked(R.id.reader_nation_choose_ck, true);
                        mItemClickListener.onClick(v);
                    }
                });
            }
        };
    }
}
