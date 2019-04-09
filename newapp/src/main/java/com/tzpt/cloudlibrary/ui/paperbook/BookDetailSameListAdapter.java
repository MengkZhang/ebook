package com.tzpt.cloudlibrary.ui.paperbook;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.bean.BookInLibInfo;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 相同图书在排架上的列表
 * Created by Administrator on 2017/6/7.
 */

public class BookDetailSameListAdapter extends RecyclerArrayAdapter<BookInLibInfo> {

    private View.OnClickListener mListener;

    public BookDetailSameListAdapter(Context context, View.OnClickListener listener) {
        super(context);
        this.mListener = listener;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(final ViewGroup parent, int viewType) {
        return new BaseViewHolder<BookInLibInfo>(parent, R.layout.view_book_detail_same_book_list) {
            @Override
            public void setData(BookInLibInfo item) {
                holder.setText(R.id.item_book_lib_number, TextUtils.isEmpty(item.mBarNumber) ? "" : mContext.getString(R.string.bar_number2, item.mBarNumber))
                        .setText(R.id.item_book_numbers,
                                TextUtils.isEmpty(item.mShelvingCode) ? "" : item.mShelvingCode)
                        .setText(R.id.item_book_call_number,
                                TextUtils.isEmpty(item.mCallNumber) ? "" : mContext.getString(R.string.call_number, item.mCallNumber));
                if (item.mStoreRoom == 1) {
                    holder.setText(R.id.item_book_status, "在馆")
                            .setText(R.id.item_book_need_deposit, "不外借")
                            .setText(R.id.item_book_store_room, item.mStoreRoomName);

                    holder.setVisible(R.id.item_book_order_tv, View.GONE);
                } else {
                    if (item.mStoreRoom == 2) {
                        //限制库
                        holder.setText(R.id.item_book_store_room, item.mStoreRoomName);
                    } else {
                        //通用库
                        holder.setText(R.id.item_book_store_room, item.mStoreRoomName);
                    }
                    switch (item.mBookStatus) {
                        case 0:
                            holder.setVisible(R.id.item_book_order_tv, View.GONE);

                            holder.setText(R.id.item_book_status, "在借")
                                    .setText(R.id.item_book_need_deposit, item.mIsDeposit ? "需押金" : "无押金");
                            break;
                        case 1:
                            holder.setText(R.id.item_book_status, "在馆")
                                    .setText(R.id.item_book_need_deposit, item.mIsDeposit ? "需押金" : "无押金");

                            holder.setText(R.id.item_book_order_tv, "预约").setTextColor(R.id.item_book_order_tv,
                                    Color.parseColor("#c67e3b")).setBackgroundColorRes(R.id.item_book_order_tv, R.drawable.bg_c67e3b);

                            if (item.mCanAppoint) {
                                holder.setVisible(R.id.item_book_order_tv, View.VISIBLE);
                            } else {
                                holder.setVisible(R.id.item_book_order_tv, View.GONE);
                            }

                            holder.getView(R.id.item_book_order_tv).setClickable(true);
                            setOnClickListener(R.id.item_book_order_tv, mListener);
                            break;
                        case 2:
                            holder.setText(R.id.item_book_order_tv, "已预约").setTextColor(R.id.item_book_order_tv,
                                    Color.parseColor("#999999")).setBackgroundColorRes(R.id.item_book_order_tv, R.drawable.bg_999999);
                            holder.setVisible(R.id.item_book_order_tv, View.VISIBLE);
                            holder.setText(R.id.item_book_status, "在馆")
                                    .setText(R.id.item_book_need_deposit, item.mIsDeposit ? "需押金" : "无押金");
                            holder.getView(R.id.item_book_order_tv).setClickable(false);
                            break;
                        case 3:
                            holder.setVisible(R.id.item_book_order_tv, View.GONE);
                            holder.setText(R.id.item_book_status, "在馆")
                                    .setText(R.id.item_book_need_deposit, item.mIsDeposit ? "需押金" : "无押金");
                            break;
                    }
                }
                setTag(R.id.item_book_order_tv, getAdapterPosition());
            }
        };
    }
}
