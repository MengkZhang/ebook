package com.tzpt.cloudlibrary.ui.ebook;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.business_bean.EBookBean;
import com.tzpt.cloudlibrary.utils.StringUtils;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 电子书
 * Created by ZhiqiangJia on 2017-08-08.
 */
public class EBookAdapter extends RecyclerArrayAdapter<EBookBean> {

    private boolean mIsRankEBookList;

    public EBookAdapter(Context context, boolean isRankEBookList) {
        super(context);
        this.mIsRankEBookList = isRankEBookList;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<EBookBean>(parent, R.layout.view_item_ebook) {
            @Override
            public void setData(final EBookBean item) {
                if (null != item) {
                    holder.setImageUrl(R.id.ebook_item_img, item.mEBook.mCoverImg, R.drawable.color_default_image);
                    holder.setText(R.id.ebook_item_title, TextUtils.isEmpty(item.mEBook.mName) ? "" : item.mEBook.mName)
                            .setText(R.id.ebook_item_anthor, TextUtils.isEmpty(item.mAuthor.mName) ? "" : item.mAuthor.mName)
                            .setText(R.id.ebook_item_content, TextUtils.isEmpty(item.mEBook.mSummary) ? "" : item.mEBook.mSummary);

                    setVisible(R.id.ebook_item_new_flag_tv, item.mHasNewEBookFlag ? View.VISIBLE : View.GONE);

                    //排行榜内容
                    if (mIsRankEBookList) {
                        holder.setVisible(R.id.ebook_item_preview_count_tv, View.GONE);
                        holder.setVisible(R.id.ebook_item_no_icon_preview_count_tv, View.VISIBLE);
                        holder.setText(R.id.ebook_item_no_icon_preview_count_tv, mContext.getResources().getString(R.string.statistics_read_count, item.mReadCount));

                        setVisible(R.id.ebook_item_rank_list_rl, View.VISIBLE);

                        int position = holder.getAdapterPosition();
                        setText(R.id.ebook_item_rank_tv, String.valueOf(position + 1));
                        if (position < 10) {
                            setVisible(R.id.ebook_item_rank_tv, View.VISIBLE);
                            if (position == 0) {
                                setVisible(R.id.ebook_item_rank_list_three_head, View.VISIBLE);
                                setBackgroundColorRes(R.id.ebook_item_rank_tv, R.mipmap.ic_ranking_first);
                            } else if (position == 1) {
                                setVisible(R.id.ebook_item_rank_list_three_head, View.VISIBLE);
                                setBackgroundColorRes(R.id.ebook_item_rank_tv, R.mipmap.ic_ranking_second);
                            } else if (position == 2) {
                                setVisible(R.id.ebook_item_rank_list_three_head, View.VISIBLE);
                                setBackgroundColorRes(R.id.ebook_item_rank_tv, R.mipmap.ic_ranking_third);
                            } else {
                                setVisible(R.id.ebook_item_rank_list_three_head, View.INVISIBLE);
                                setBackgroundColorRes(R.id.ebook_item_rank_tv, R.mipmap.ic_ranking_more);
                            }
                        } else {
                            setVisible(R.id.ebook_item_rank_list_rl, View.GONE);
                        }
                    } else {
                        holder.setText(R.id.ebook_item_preview_count_tv, StringUtils.formatBorrowCount(item.mReadCount));
                    }
                }
            }
        };
    }
}
