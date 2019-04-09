package com.tzpt.cloudlibrary.ui.account.interaction;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.bean.CommentBean;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 我的消息
 * Created by ZhiqiangJia on 2018-01-18.
 */
public class MyMessageAdapter extends RecyclerArrayAdapter<CommentBean> {

    private View.OnClickListener mMsgClickListener;

    private ClickableSpan mClickableSpan = new ClickableSpan() {
        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setColor(Color.parseColor("#3972be"));
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(View widget) {

        }
    };

    public MyMessageAdapter(Context context, View.OnClickListener clickListener) {
        super(context);
        this.mMsgClickListener = clickListener;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<CommentBean>(parent, R.layout.view_my_message_header_item) {
            @Override
            public void setData(CommentBean item) {
                final int realPosition = getAdapterPosition();

                holder.setRoundImageUrl(R.id.msg_reader_head_iv,
                        item.mCommentImage,
                        item.mIsMan ? R.mipmap.ic_head_mr : R.mipmap.ic_head_miss,
                        item.mIsMan ? R.mipmap.ic_head_mr : R.mipmap.ic_head_miss,
                        5);
                holder.setText(R.id.msg_reader_time_tv, item.mPublishTime);
                if (item.mType == 5 || item.mType == 6) {   //评论点赞,回复点赞
                    setVisible(R.id.msg_reader_content_tv, View.GONE);
                    setVisible(R.id.msg_reader_msg_icon_iv, View.GONE);


                    String content;
                    if (TextUtils.isEmpty(item.mLibraryCode)) {
                        content = (item.mType == 5 ? "点赞您的评论" : "点赞您的回复");
                    } else {
                        content = (item.mType == 5 ? "点赞您的留言" : "点赞您的回复");
                    }
                    SpannableString spannableString = new SpannableString(item.mCommentName + content);
                    spannableString.setSpan(mClickableSpan, 0, item.mCommentName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ((TextView) holder.getView(R.id.msg_reader_retry_name_tv)).setHighlightColor(Color.TRANSPARENT);
                    ((TextView) holder.getView(R.id.msg_reader_retry_name_tv)).setText(spannableString);
                    ((TextView) holder.getView(R.id.msg_reader_retry_name_tv)).setMovementMethod(LinkMovementMethod.getInstance());

                } else {//1读者回复评论 3 平台回复评论
                    setVisible(R.id.msg_reader_content_tv, View.VISIBLE);
                    setVisible(R.id.msg_reader_msg_icon_iv, View.VISIBLE);

                    setTag(R.id.msg_reader_msg_icon_iv, realPosition);
                    setOnClickListener(R.id.msg_reader_msg_icon_iv, mMsgClickListener);

                    holder.setText(R.id.msg_reader_content_tv, item.mContent);

                    SpannableString spannableString = new SpannableString(item.mCommentName + (TextUtils.isEmpty(item.mLibraryCode) ? "回复您的评论" : "回复您的留言"));
                    spannableString.setSpan(mClickableSpan, 0, item.mCommentName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ((TextView) holder.getView(R.id.msg_reader_retry_name_tv)).setHighlightColor(Color.TRANSPARENT);
                    ((TextView) holder.getView(R.id.msg_reader_retry_name_tv)).setText(spannableString);
                    ((TextView) holder.getView(R.id.msg_reader_retry_name_tv)).setMovementMethod(LinkMovementMethod.getInstance());
                }
            }
        };
    }
}
