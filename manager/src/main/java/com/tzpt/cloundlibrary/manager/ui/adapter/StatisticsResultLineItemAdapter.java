package com.tzpt.cloundlibrary.manager.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.adapter.recyclerview.EasyRVAdapter;
import com.tzpt.cloundlibrary.manager.base.adapter.recyclerview.EasyRVHolder;
import com.tzpt.cloundlibrary.manager.bean.StatisticsItem;
import com.tzpt.cloundlibrary.manager.utils.DisplayUtils;

import java.util.List;

/**
 * Created by Administrator on 2018/9/4.
 */

public class StatisticsResultLineItemAdapter extends EasyRVAdapter<StatisticsItem> {
    private CallPhoneListener mCallPhoneListener;

    public StatisticsResultLineItemAdapter(Context context, List<StatisticsItem> list, CallPhoneListener listener) {
        super(context, list, R.layout.view_statistics_list_row_item);
        mCallPhoneListener = listener;
    }

    @Override
    protected void onBindData(EasyRVHolder viewHolder, int position, final StatisticsItem item) {
        TextView infoTv = viewHolder.getView(R.id.info_tv);

        infoTv.setMaxWidth(DisplayUtils.dp2px(item.mWidth));
        infoTv.setMinWidth(DisplayUtils.dp2px(item.mWidth));
        infoTv.setWidth(DisplayUtils.dp2px(item.mWidth));
        infoTv.setGravity(item.mGravity);
//        if (item.mGravity == (Gravity.CENTER | Gravity.START)) {
//            infoTv.setPadding(, 0, 0, 0);
//        } else if (item.mGravity == (Gravity.CENTER | Gravity.END)) {
//
//        }
        infoTv.setPadding(DisplayUtils.dp2px(10), 0, DisplayUtils.dp2px(10), 0);
        if (TextUtils.isEmpty(item.mContent)) {
            viewHolder.setText(R.id.info_tv, item.mContentSequence);
            infoTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(item.mPhone)) {
                        mCallPhoneListener.call(item.mPhone);
                    }
                }
            });
        } else {
            viewHolder.setText(R.id.info_tv, item.mContent);
        }
        if (item.mLines == 1) {
            infoTv.setSingleLine(true);
        } else {
            infoTv.setSingleLine(false);
            infoTv.setMaxLines(item.mLines);
            infoTv.setMinLines(item.mLines);
        }
    }

    public interface CallPhoneListener {
        void call(String phone);
    }
}
