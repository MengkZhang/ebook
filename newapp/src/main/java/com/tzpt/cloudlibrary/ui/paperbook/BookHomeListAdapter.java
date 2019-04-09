package com.tzpt.cloudlibrary.ui.paperbook;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.adapter.recyclerview.EasyRVAdapter;
import com.tzpt.cloudlibrary.base.adapter.recyclerview.EasyRVHolder;
import com.tzpt.cloudlibrary.business_bean.BookBean;
import com.tzpt.cloudlibrary.utils.glide.GlideApp;

/**
 * Created by tonyjia on 2018/11/15.
 */

public class BookHomeListAdapter extends EasyRVAdapter<BookBean> {


    public BookHomeListAdapter(Context context) {
        super(context, R.layout.view_book_home_list_item);
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
    }
}
