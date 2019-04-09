package com.tzpt.cloudlibrary.ui.ebook;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.business_bean.EBookBean;
import com.tzpt.cloudlibrary.utils.StringUtils;
import com.tzpt.cloudlibrary.widget.CenterAlignImageSpan;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 电子书
 * Created by ZhiqiangJia on 2017-08-08.
 */
public class EBookRecommendationsAdapter extends RecyclerArrayAdapter<EBookBean> {


    public EBookRecommendationsAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<EBookBean>(parent, R.layout.view_item_ebook_recommendations) {
            @Override
            public void setData(EBookBean item) {
                if (null != item) {
                    holder.setImageUrl(R.id.ebook_item_img, item.mEBook.mCoverImg, R.drawable.color_default_image);
                    holder.setText(R.id.ebook_item_title, TextUtils.isEmpty(item.mEBook.mName) ? "" : item.mEBook.mName)
                            .setText(R.id.ebook_item_anthor, TextUtils.isEmpty(item.mAuthor.mName) ? "" : item.mAuthor.mName)
                            .setText(R.id.ebook_item_preview_count_tv, StringUtils.formatBorrowCount(item.mReadCount));

                    if (!TextUtils.isEmpty(item.mRecommendReason)) {
                        setVisible(R.id.ebook_item_content, View.VISIBLE);
                        SpannableString sp = new SpannableString("[icon]    " + item.mRecommendReason);
                        Drawable drawable = getContext().getResources().getDrawable(R.mipmap.ic_recommend);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());

                        CenterAlignImageSpan imageSpan = new CenterAlignImageSpan(drawable);
                        sp.setSpan(imageSpan, 0, "[icon]".length(), ImageSpan.ALIGN_BASELINE);
                        TextView tv = getView(R.id.ebook_item_content);
                        tv.setText(sp);
                    } else {
                        setVisible(R.id.ebook_item_content, View.GONE);
                    }
                }
            }
        };
    }
}
