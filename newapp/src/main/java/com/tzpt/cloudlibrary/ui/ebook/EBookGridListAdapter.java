package com.tzpt.cloudlibrary.ui.ebook;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.adapter.recyclerview.EasyRVAdapter;
import com.tzpt.cloudlibrary.base.adapter.recyclerview.EasyRVHolder;
import com.tzpt.cloudlibrary.business_bean.EBookBean;
import com.tzpt.cloudlibrary.utils.glide.GlideApp;

/**
 * 电子书Adapter
 * Created by Administrator on 2017/6/7.
 */

public class EBookGridListAdapter extends EasyRVAdapter<EBookBean> {

    private boolean mIsRankList;

    public EBookGridListAdapter(Context context, boolean isRankList) {
        super(context, R.layout.view_rank_book_list_item);
        this.mIsRankList = isRankList;
    }

    @Override
    protected void onBindData(EasyRVHolder viewHolder, int position, EBookBean item) {
        if (item != null) {
            GlideApp.with(mContext)
                    .load(item.mEBook.mCoverImg)
                    .placeholder(R.drawable.color_default_image)
                    .error(R.mipmap.ic_nopicture)
                    .centerCrop()
                    .into((ImageView) viewHolder.getView(R.id.item_book_image));

            viewHolder.setText(R.id.item_book_title, TextUtils.isEmpty(item.mEBook.mName) ? "" : item.mEBook.mName);

            if (mIsRankList) {
                if (position < 9) {
                    if (position == 0) {
                        viewHolder.setVisible(R.id.item_book_rank_list_three_head, View.VISIBLE);
                        viewHolder.setBackgroundColorRes(R.id.item_book_rank_list_tv, R.mipmap.ic_ranking_first);
                    } else if (position == 1) {
                        viewHolder.setVisible(R.id.item_book_rank_list_three_head, View.VISIBLE);
                        viewHolder.setBackgroundColorRes(R.id.item_book_rank_list_tv, R.mipmap.ic_ranking_second);
                    } else if (position == 2) {
                        viewHolder.setVisible(R.id.item_book_rank_list_three_head, View.VISIBLE);
                        viewHolder.setBackgroundColorRes(R.id.item_book_rank_list_tv, R.mipmap.ic_ranking_third);
                    } else {
                        viewHolder.setVisible(R.id.item_book_rank_list_three_head, View.INVISIBLE);
                        viewHolder.setBackgroundColorRes(R.id.item_book_rank_list_tv, R.mipmap.ic_ranking_more);
                    }
                    viewHolder.setText(R.id.item_book_rank_list_tv, String.valueOf(position + 1));
                    viewHolder.setVisible(R.id.item_book_rank_list_rl, View.VISIBLE);
                } else {
                    viewHolder.setVisible(R.id.item_book_rank_list_rl, View.GONE);
                }
            }
        }
    }
}
