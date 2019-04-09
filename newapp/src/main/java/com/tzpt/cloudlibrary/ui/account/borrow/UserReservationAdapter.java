package com.tzpt.cloudlibrary.ui.account.borrow;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.business_bean.ReservationBookBean;
import com.tzpt.cloudlibrary.utils.DateUtils;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 预约列表
 * Created by ZhiqiangJia on 2017-08-17.
 */
public class UserReservationAdapter extends RecyclerArrayAdapter<ReservationBookBean> {

    private View.OnClickListener mOnCancelClickListener;
    private View.OnClickListener mOnEnterBookHomeClickListener;

    public UserReservationAdapter(Context context, View.OnClickListener cancelListener) {
        super(context);
        this.mOnCancelClickListener = cancelListener;
    }
    public UserReservationAdapter(Context context, View.OnClickListener cancelListener,View.OnClickListener enterBookHomeClickListener) {
        super(context);
        this.mOnCancelClickListener = cancelListener;
        this.mOnEnterBookHomeClickListener = enterBookHomeClickListener;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<ReservationBookBean>(parent, R.layout.view_reservation_book_item) {
            @Override
            public void setData(ReservationBookBean item) {
                //预约时间和当前时间的差值
                //获取当前时间戳
                long currentTimeMillis = System.currentTimeMillis();
                long hour = DateUtils.betweenTwoTimes(item.mAppointTimeEnd, DateUtils.formatDateSureToSecond(currentTimeMillis));
                String  spareTime = mContext.getResources().getString(R.string.have_other_time) + hour + mContext.getResources().getString(R.string.have_other_time_hour);

                int position = getAdapterPosition();

                setImageUrl(R.id.item_book_image, item.mBook.mCoverImg, R.drawable.color_default_image);

                setText(R.id.item_book_author_con, item.mBook.mIsbn)
                        .setText(R.id.item_book_publishing_year_con, item.mBook.mBarNumber)
                        .setText(R.id.item_book_publishing_company_con, item.mBook.mCallNumber)
                        .setText(R.id.item_book_isbn_con, item.mFrameCode)
                        .setText(R.id.item_book_category_con, item.mCategory.mName)
                        .setText(R.id.item_book_publishing_library_con, item.mLibrary.mName);

                setText(R.id.item_book_title, item.mBook.mName)
                        .setText(R.id.item_book_author, mContext.getString(R.string.book_list_isbn))
                        .setText(R.id.item_book_publishing_year, mContext.getString(R.string.bar_number))
                        .setText(R.id.item_book_publishing_company, mContext.getString(R.string.call_number2))
                        .setText(R.id.item_book_isbn, item.mStoreRoomName)
                        .setText(R.id.item_book_category, mContext.getString(R.string.book_list_publish_category_name))
                        .setText(R.id.item_book_publishing_library, mContext.getString(R.string.stay_library2))
                        .setText(R.id.end_time_book_tv, spareTime);
                setVisible(R.id.item_book_category, TextUtils.isEmpty(item.mCategory.mName) ? View.GONE : View.VISIBLE);
                setVisible(R.id.item_book_category_con, TextUtils.isEmpty(item.mCategory.mName) ? View.GONE : View.VISIBLE);

                //取消预约监听
                holder.setTag(R.id.item_book_cancel_reservation, position);
                setOnClickListener(R.id.item_book_cancel_reservation, mOnCancelClickListener);
                //进入该馆监听
                holder.setTag(R.id.item_book_enter_book_home, position);
                setOnClickListener(R.id.item_book_enter_book_home, mOnEnterBookHomeClickListener);

            }
        };
    }

}
