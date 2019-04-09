package com.tzpt.cloundlibrary.manager.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.bean.CollectingStatisticsMoneyBean;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 收款统计
 * Created by ZhiqiangJia on 2017-07-11.
 */
public class CollectingStatisticsAdapter extends RecyclerArrayAdapter<CollectingStatisticsMoneyBean> {

    public CollectingStatisticsAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<CollectingStatisticsMoneyBean>(parent, R.layout.view_item_collecting_staistics) {
            @Override
            public void setData(CollectingStatisticsMoneyBean item) {
                if (null != item) {
                    holder.setText(R.id.collecting_date, item.operDate)
                            .setText(R.id.collecting_order_number, item.operCode)
                            .setText(R.id.book_operator, item.userName)
                            .setText(R.id.collecting_project, item.desc);//设置赔款信息
                    //设置值是否退押金
                    setTextColor(R.id.book_price, item.isRed ? Color.RED : Color.parseColor("#333333"));
                    setText(R.id.book_price, item.deposit);
                }
            }
        };
    }
}
