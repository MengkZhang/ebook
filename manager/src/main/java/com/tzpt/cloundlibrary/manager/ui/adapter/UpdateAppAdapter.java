package com.tzpt.cloundlibrary.manager.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 更新APP日志
 * Created by ZhiqiangJia on 2017-12-07.
 */
public class UpdateAppAdapter extends RecyclerArrayAdapter<String> {

    public UpdateAppAdapter(Context context) {
        super(context);
    }
    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<String>(parent, R.layout.view_update_remark_item){
            @Override
            public void setData(String item) {
                setText(R.id.update_item_content_tv, item);
            }
        };
    }
}
