package com.tzpt.cloudlibrary.ui.search;

import android.content.Context;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.adapter.abslistview.EasyLVAdapter;
import com.tzpt.cloudlibrary.base.adapter.abslistview.EasyLVHolder;

import java.util.List;

/**
 * Created by Administrator on 2018/1/4.
 */

public class SearchHotAdapter extends EasyLVAdapter<String> {

    public SearchHotAdapter(Context context, List<String> list) {
        super(context, list, R.layout.view_search_hot_list_item);
    }

    @Override
    public void convert(EasyLVHolder holder, int position, String s) {
        holder.setText(R.id.position_tv, String.valueOf(position + 1));
        holder.setText(R.id.content_tv, s);
        if (position < 3) {
            holder.setBackgroundColorRes(R.id.position_tv, R.drawable.bg_round_8a633d);
        } else {
            holder.setBackgroundColorRes(R.id.position_tv, R.drawable.bg_round_cccccc);
        }
    }
}
