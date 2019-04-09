package com.tzpt.cloundlibrary.manager.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.adapter.recyclerview.EasyRVAdapter;
import com.tzpt.cloundlibrary.manager.base.adapter.recyclerview.EasyRVHolder;
import com.tzpt.cloundlibrary.manager.bean.BookInfoBean;
import com.tzpt.cloundlibrary.manager.utils.MoneyUtils;

import java.util.List;

/**
 * Created by Administrator on 2018/12/16.
 */

public class CompensateBookAdapter extends EasyRVAdapter<BookInfoBean> {
    private OnCheckedChangeListener mListener;

    public CompensateBookAdapter(Context context, List<BookInfoBean> list, OnCheckedChangeListener listener) {
        super(context, list, R.layout.view_compensate_book_list_item);
        mListener = listener;
    }

    @Override
    protected void onBindData(EasyRVHolder viewHolder, final int position, BookInfoBean item) {
        viewHolder.setText(R.id.library_number_tv, TextUtils.isEmpty(item.mBelongLibraryHallCode) ? "" : item.mBelongLibraryHallCode)
                .setText(R.id.code_number_tv, TextUtils.isEmpty(item.mBarNumber) ? "" : item.mBarNumber)
                .setText(R.id.book_name_tv, TextUtils.isEmpty(item.mProperTitle) ? "" : item.mProperTitle)
                .setText(R.id.book_price_tv, ((item.mDeposit == 0) ? "" : "押") + MoneyUtils.formatMoney(item.mPrice));

        viewHolder.setTextColor(R.id.code_number_tv, item.mColorIsRed ? Color.RED : Color.parseColor("#333333"));
        if (item.mAttachPrice > 0) {
            viewHolder.setText(R.id.attach_price_tv, "溢" + MoneyUtils.formatMoney(item.mAttachPrice));
            viewHolder.setVisible(R.id.attach_price_tv, View.VISIBLE);
        } else {
            viewHolder.setVisible(R.id.attach_price_tv, View.GONE);
        }

        ((CheckBox) viewHolder.getView(R.id.choose_book_cb)).setChecked(item.mCompensateChoosed);

        ((CheckBox) viewHolder.getView(R.id.choose_book_cb)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mListener.onCheckedChanged(position, isChecked);
            }
        });
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(int position, boolean isChecked);
    }
}
