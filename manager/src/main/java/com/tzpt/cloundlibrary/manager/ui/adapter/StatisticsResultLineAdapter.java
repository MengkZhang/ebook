package com.tzpt.cloundlibrary.manager.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.bean.StatisticsItem;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.RecyclerArrayAdapter;

import java.util.List;

/**
 * Created by Administrator on 2018/9/3.
 */

public class StatisticsResultLineAdapter extends RecyclerArrayAdapter<List<StatisticsItem>> {
    private StatisticsResultLineItemAdapter.CallPhoneListener mCallPhoneListener;
    public StatisticsResultLineAdapter(Context context, StatisticsResultLineItemAdapter.CallPhoneListener listener) {
        super(context);
        mCallPhoneListener = listener;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<List<StatisticsItem>>(parent, R.layout.view_statistics_list_row) {
            @Override
            public void setData(List<StatisticsItem> item) {
                RecyclerView recyclerView = (RecyclerView) holder.getItemView();
                if (getAdapterPosition() % 2 == 0) {
                    recyclerView.setBackgroundColor(Color.parseColor("#f4f4f4"));
                } else {
                    recyclerView.setBackgroundColor(Color.parseColor("#ffffff"));
                }
                LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false) {
                    @Override
                    public boolean canScrollHorizontally() {
                        return false;
                    }
                };
                layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setNestedScrollingEnabled(false);

                StatisticsResultLineItemAdapter lineItemAdapter = new StatisticsResultLineItemAdapter(getContext(), item, mCallPhoneListener);
                recyclerView.setAdapter(lineItemAdapter);
            }
        };
    }

}