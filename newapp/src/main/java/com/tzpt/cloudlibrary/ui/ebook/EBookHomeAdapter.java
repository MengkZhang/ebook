package com.tzpt.cloudlibrary.ui.ebook;

import android.content.Context;
import android.text.TextUtils;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.adapter.recyclerview.EasyRVAdapter;
import com.tzpt.cloudlibrary.base.adapter.recyclerview.EasyRVHolder;
import com.tzpt.cloudlibrary.business_bean.EBookBean;
import com.tzpt.cloudlibrary.utils.StringUtils;

/**
 * 电子书
 * Created by ZhiqiangJia on 2017-08-08.
 */
public class EBookHomeAdapter extends EasyRVAdapter<EBookBean> {

    public EBookHomeAdapter(Context context) {
        super(context, R.layout.view_item_ebook_home);
        setHasStableIds(true);
    }

    @Override
    protected void onBindData(EasyRVHolder holder, int position, EBookBean item) {
        holder.setImageUrl(R.id.ebook_item_img, item.mEBook.mCoverImg, R.drawable.color_default_image);
        holder.setText(R.id.ebook_item_title, TextUtils.isEmpty(item.mEBook.mName) ? "" : item.mEBook.mName)
                .setText(R.id.ebook_item_anthor, TextUtils.isEmpty(item.mAuthor.mName) ? "" : item.mAuthor.mName)
                .setText(R.id.ebook_item_content, TextUtils.isEmpty(item.mEBook.mSummary) ? "" : item.mEBook.mSummary)
                .setText(R.id.ebook_item_preview_count_tv, StringUtils.formatBorrowCount(item.mReadCount));
    }
}
