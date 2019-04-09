package com.tzpt.cloudlibrary.ui.paperbook;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.business_bean.BookBean;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 纸质书籍列表Adapter
 * Created by Administrator on 2017/6/7.
 */

public class BookListAdapter extends RecyclerArrayAdapter<BookBean> {
    private boolean mIsBorrowRanking;
    private boolean mIsPraiseRanking;
    private boolean mIsRecommendRanking;

    public BookListAdapter(Context context, boolean borrowRanking, boolean praiseRanking, boolean recommendRanking) {
        super(context);
        mIsBorrowRanking = borrowRanking;
        mIsPraiseRanking = praiseRanking;
        mIsRecommendRanking = recommendRanking;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<BookBean>(parent, R.layout.view_paper_book_list_item) {
            @Override
            public void setData(BookBean item) {
                holder.setImageUrl(R.id.item_book_image, item.mBook.mCoverImg, R.drawable.color_default_image);

                holder.setText(R.id.item_book_title, TextUtils.isEmpty(item.mBook.mName) ? "" : item.mBook.mName)
                        .setText(R.id.item_book_anthor, TextUtils.isEmpty(item.mAuthor.mName) ? "" : item.mAuthor.mName)
                        .setText(R.id.item_book_content, TextUtils.isEmpty(item.mBook.mSummary) ? "暂无简介" : item.mBook.mSummary);

                if (item.mIsShowBorrowNum) {
                    holder.setVisible(R.id.item_book_review_count, View.VISIBLE);
                } else {
                    holder.setVisible(R.id.item_book_review_count, View.INVISIBLE);
                }

                setVisible(R.id.item_new_flag_tv, item.mHasNewBookFlag ? View.VISIBLE : View.GONE);
                if (mIsBorrowRanking || mIsPraiseRanking || mIsRecommendRanking) {

                    holder.setVisible(R.id.item_book_rank_list_tv, View.VISIBLE);
                    int position = holder.getAdapterPosition();

                    if (position < 10) {
                        if (position == 0) {
                            holder.setVisible(R.id.item_book_rank_list_three_head, View.VISIBLE);
                            holder.setBackgroundColorRes(R.id.item_book_rank_list_tv, R.mipmap.ic_ranking_first);
                        } else if (position == 1) {
                            holder.setVisible(R.id.item_book_rank_list_three_head, View.VISIBLE);
                            holder.setBackgroundColorRes(R.id.item_book_rank_list_tv, R.mipmap.ic_ranking_second);
                        } else if (position == 2) {
                            holder.setVisible(R.id.item_book_rank_list_three_head, View.VISIBLE);
                            holder.setBackgroundColorRes(R.id.item_book_rank_list_tv, R.mipmap.ic_ranking_third);
                        } else {
                            holder.setVisible(R.id.item_book_rank_list_three_head, View.INVISIBLE);
                            holder.setBackgroundColorRes(R.id.item_book_rank_list_tv, R.mipmap.ic_ranking_more);
                        }
                        holder.setText(R.id.item_book_rank_list_tv, String.valueOf(position + 1));
                        holder.setVisible(R.id.item_book_rank_list_rl, View.VISIBLE);
                    } else {
                        holder.setVisible(R.id.item_book_rank_list_rl, View.GONE);
                    }

                    if (mIsPraiseRanking) {
                        holder.setText(R.id.item_book_review_count, mContext.getString(R.string.statistics_praise_count, item.mPraiseNum));
                    } else if (mIsRecommendRanking) {
                        holder.setText(R.id.item_book_review_count, mContext.getString(R.string.statistics_recommend_count, item.mRecommendNum));
                    } else {
                        holder.setText(R.id.item_book_review_count, mContext.getString(R.string.statistics_borrow_count, item.mBorrowNum));
                    }
                } else {
                    holder.setVisible(R.id.item_book_rank_list_rl, View.GONE);
                    holder.setText(R.id.item_book_review_count, mContext.getString(R.string.statistics_borrow_count, item.mBorrowNum));
                }

            }
        };
    }
}
