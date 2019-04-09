package com.tzpt.cloundlibrary.manager.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.bean.SameRangeLibraryBean;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 流出管理新增流向馆列表
 * Created by Administrator on 2017/7/9.
 */
public class FlowManageInLibraryListAdapter extends RecyclerArrayAdapter<SameRangeLibraryBean> {

    public FlowManageInLibraryListAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<SameRangeLibraryBean>(parent, R.layout.view_flow_manage_in_library_list_item) {
            @Override
            public void setData(SameRangeLibraryBean item) {
                if (null != item) {
                    holder.setText(R.id.library_item_lib_code, TextUtils.isEmpty(item.hallCode) ? "" : item.hallCode)
                            .setText(R.id.library_item_lib_name, TextUtils.isEmpty(item.name) ? "" : item.name);
                    StringBuilder builder = new StringBuilder();
                    if (!TextUtils.isEmpty(item.conperson)) {
                        builder.append(item.conperson);
                    }
                    builder.append(" ");
                    if (!TextUtils.isEmpty(item.phone)) {
                        builder.append(item.phone);
                    }
                    holder.setText(R.id.library_item_lib_user, builder.toString());
                }
            }
        };
    }
}
