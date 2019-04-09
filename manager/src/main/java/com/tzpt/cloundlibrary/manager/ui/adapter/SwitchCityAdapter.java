package com.tzpt.cloundlibrary.manager.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.bean.SwitchCityBean;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 资讯列表
 * Created by ZhiqiangJia on 2017-08-17.
 */
public class SwitchCityAdapter extends RecyclerArrayAdapter<SwitchCityBean> {

    public SwitchCityAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<SwitchCityBean>(parent, R.layout.view_switch_city_item) {
            @Override
            public void setData(SwitchCityBean item) {
                holder.setText(R.id.switch_city_name_tv, TextUtils.isEmpty(item.mName) ? "" : item.mName);
            }
        };
    }
}
