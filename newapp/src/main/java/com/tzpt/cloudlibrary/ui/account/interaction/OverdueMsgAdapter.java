package com.tzpt.cloudlibrary.ui.account.interaction;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.bean.OverdueMsgBean;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;

import java.util.List;

/**
 * Created by Administrator on 2018/3/30.
 */

public class OverdueMsgAdapter extends RecyclerArrayAdapter<OverdueMsgBean> {

    public OverdueMsgAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<OverdueMsgBean>(parent, R.layout.view_overdue_msg_item) {
            @Override
            public void setData(OverdueMsgBean item) {
                holder.setText(R.id.overdue_msg_title_tv, item.mMsgContent)
                        .setText(R.id.overdue_msg_time_tv, item.mCreateTime);
                if (item.mState == 1) {
                    holder.setVisible(R.id.overdue_msg_state_iv, View.VISIBLE);
                } else {
                    holder.setVisible(R.id.overdue_msg_state_iv, View.INVISIBLE);
                }

            }
        };
    }
}
