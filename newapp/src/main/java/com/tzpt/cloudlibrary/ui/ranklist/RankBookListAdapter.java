package com.tzpt.cloudlibrary.ui.ranklist;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.adapter.recyclerview.EasyRVAdapter;
import com.tzpt.cloudlibrary.base.adapter.recyclerview.EasyRVHolder;
import com.tzpt.cloudlibrary.business_bean.BookBean;
import com.tzpt.cloudlibrary.utils.glide.GlideApp;

/**
 * 排行榜
 * Created by tonyjia on 2018/11/19.
 */
public class RankBookListAdapter extends EasyRVAdapter<BookBean> {

    public RankBookListAdapter(Context context) {
        super(context, R.layout.view_rank_book_list_item);
        setHasStableIds(true);
    }

    @Override
    protected void onBindData(EasyRVHolder viewHolder, int position, BookBean item) {

        GlideApp.with(mContext)
                .load(item.mBook.mCoverImg)
                .placeholder(R.drawable.color_default_image)
                .error(R.mipmap.ic_nopicture)
                .centerCrop()
                .into((ImageView) viewHolder.getView(R.id.item_book_image));

        viewHolder.setText(R.id.item_book_title, TextUtils.isEmpty(item.mBook.mName) ? "" : item.mBook.mName);

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
