package com.tzpt.cloundlibrary.manager.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.bean.MsgInfo;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 消息适配器
 * Created by Administrator on 2017/7/3.
 */
public class MsgListAdapter extends RecyclerArrayAdapter<MsgInfo> {

    public MsgListAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {

        return new BaseViewHolder<MsgInfo>(parent, R.layout.view_msg_item) {
            @Override
            public void setData(MsgInfo item) {
                holder.setText(R.id.msg_tv, item.mMsg);
                holder.setImageResource(R.id.msg_read_flag_iv, item.mIsRead ? R.mipmap.ic_news_readed : R.mipmap.ic_news_no_read);
            }
        };
    }
}
