package com.tzpt.cloundlibrary.manager.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.bean.HelpInfoBean;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 帮助列表item
 */
public class LibraryHelpListAdapter extends RecyclerArrayAdapter<HelpInfoBean> {

    public LibraryHelpListAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<HelpInfoBean>(parent, R.layout.view_library_help_item) {
            @Override
            public void setData(HelpInfoBean item) {
                //set data
                holder.setText(R.id.help_item_tv, item.mItemName);
            }
        };
    }
}
