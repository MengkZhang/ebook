package com.tzpt.cloundlibrary.manager.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.bean.StatisticsClassifyBean;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 统计分析
 * Created by ZhiqiangJia on 2017-07-14.
 */
public class StatisticsListAdapter extends RecyclerArrayAdapter<StatisticsClassifyBean> {

    public StatisticsListAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<StatisticsClassifyBean>(parent, R.layout.view_statistics_item) {
            @Override
            public void setData(StatisticsClassifyBean item) {
                //set data
                holder.setText(R.id.msg_tv, item.getTitle());
            }
        };
    }
}
