package com.tzpt.cloundlibrary.manager.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.adapter.recyclerview.EasyRVAdapter;
import com.tzpt.cloundlibrary.manager.base.adapter.recyclerview.EasyRVHolder;
import com.tzpt.cloundlibrary.manager.bean.StatisticsConditionBean;

import java.util.List;

/**
 * 流入流出选择适配器
 * Created by ZhiqiangJia on 2017-07-10.
 */
public class FlowIntoSearchTypeAdapter extends EasyRVAdapter<StatisticsConditionBean> {
    private View.OnClickListener mOnClickListener;

    public FlowIntoSearchTypeAdapter(Context context, List<StatisticsConditionBean> list, View.OnClickListener onClickListener) {
        super(context, list, R.layout.view_common_item);
        mOnClickListener = onClickListener;
    }

    @Override
    protected void onBindData(EasyRVHolder viewHolder, int position, StatisticsConditionBean item) {
        if (null != item) {
            viewHolder.setText(R.id.msg_tv, TextUtils.isEmpty(item.getTitle()) ? "" : item.getTitle());
            viewHolder.setTag(R.id.msg_tv, position);
            viewHolder.setOnClickListener(R.id.msg_tv, mOnClickListener);
        }
    }
}
