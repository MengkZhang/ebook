package com.tzpt.cloudlibrary.ui.map;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.bean.SwitchCityBean;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;

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
                holder.setText(R.id.switch_city_name_tv, TextUtils.isEmpty(item.mShowName) ? "" : item.mShowName);
            }
        };
    }
}
