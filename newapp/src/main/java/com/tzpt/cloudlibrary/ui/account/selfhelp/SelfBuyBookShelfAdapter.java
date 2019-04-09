package com.tzpt.cloudlibrary.ui.account.selfhelp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.business_bean.BoughtBookBean;
import com.tzpt.cloudlibrary.utils.MoneyUtils;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 借阅列表
 * Created by ZhiqiangJia on 2017-08-17.
 */
public class SelfBuyBookShelfAdapter extends RecyclerArrayAdapter<BoughtBookBean> {

    private View.OnClickListener mItemOnClickListener;
    private View.OnClickListener mThumpListener;
    private View.OnClickListener mNoteListener;

    public SelfBuyBookShelfAdapter(Context context,
                                   View.OnClickListener listener,
                                   View.OnClickListener thumpListener,
                                   View.OnClickListener noteListener) {
        super(context);
        this.mItemOnClickListener = listener;
        this.mThumpListener = thumpListener;
        this.mNoteListener = noteListener;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<BoughtBookBean>(parent, R.layout.view_borrow_book_item) {
            @Override
            public void setData(BoughtBookBean item) {
                int position = getAdapterPosition();
                //隐藏剩余时间
                setVisible(R.id.end_time_book_tv, View.GONE);
                setVisible(R.id.item_book_publishing_library, View.GONE);
                setVisible(R.id.item_book_publishing_library_con, View.GONE);
                setImageUrl(R.id.item_book_image, item.mBook.mCoverImg, R.drawable.color_default_image);
                holder.setText(R.id.item_book_author_con, item.mAuthor.mName)//作者
                        .setText(R.id.item_book_publishing_year_con, item.mLibrary.mName)//所在馆
                        .setText(R.id.item_book_publishing_company_con, item.mBook.mIsbn)//isbn
                        .setText(R.id.item_book_isbn_con, item.mBoughtTime)//购买时间
                        .setText(R.id.item_book_category_con, MoneyUtils.formatMoney(item.mBoughtPrice));//金额

                holder.setText(R.id.item_book_title, item.mBook.mName)
                        .setText(R.id.item_book_author, mContext.getString(R.string.book_list_author))//作者
                        .setText(R.id.item_book_publishing_year, mContext.getString(R.string.stay_library2))//所在馆
                        .setText(R.id.item_book_publishing_company, mContext.getString(R.string.book_list_isbn))//isbn
                        .setText(R.id.item_book_isbn, mContext.getString(R.string.bought_time))//购买时间
                        .setText(R.id.item_book_category, mContext.getString(R.string.bought_money));//金额

                //全局监听
                holder.setTag(R.id.item_book_layout, position);
                setOnClickListener(R.id.item_book_layout, mItemOnClickListener);
                //点赞监听
                holder.setTag(R.id.item_book_thumbs_up, position);
                setOnClickListener(R.id.item_book_thumbs_up, mThumpListener);
                //读书笔记监听
                holder.setTag(R.id.item_book_write_note_tv, position);
                setOnClickListener(R.id.item_book_write_note_tv, mNoteListener);

                //设置点赞状态
                setText(R.id.item_book_thumbs_up, item.mIsPraised ? "已赞" : "点赞");
                Drawable flup = null;
                if (item.mIsPraised) {
                    flup = mContext.getResources().getDrawable(R.mipmap.ic_click_have_praise);
                } else {
                    flup = mContext.getResources().getDrawable(R.mipmap.ic_click_praise);
                }
                flup.setBounds(0, 0, flup.getMinimumWidth(), flup.getMinimumHeight());
                ((TextView) getView(R.id.item_book_thumbs_up)).setCompoundDrawables(flup, null, null, null);
            }
        };
    }

}
