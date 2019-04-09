package com.tzpt.cloudlibrary.ui.library;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.bean.CommentBean;
import com.tzpt.cloudlibrary.ui.information.InformationReaderDiscussReplyAdapter;
import com.tzpt.cloudlibrary.widget.LinearLayoutForListView;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 留言板
 * Created by ZhiqiangJia on 2018-01-10.
 */
public class LibraryMessageBoardAdapter extends RecyclerArrayAdapter<CommentBean> {

    private View.OnClickListener mDelClickListener;
    private View.OnClickListener mTagClickListener;
    private View.OnClickListener mReplyClickListener;
    private View.OnClickListener mPraiseClickListener;
    private View.OnClickListener mMoreReplyClickListener;

    public LibraryMessageBoardAdapter(Context context, View.OnClickListener delClickListener,
                                      View.OnClickListener tagClickListener,
                                      View.OnClickListener replyClickListener,
                                      View.OnClickListener praiseClickListener,
                                      View.OnClickListener moreReplyClickListener) {
        super(context);
        this.mDelClickListener = delClickListener;
        this.mTagClickListener = tagClickListener;
        this.mReplyClickListener = replyClickListener;
        this.mPraiseClickListener = praiseClickListener;
        this.mMoreReplyClickListener = moreReplyClickListener;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<CommentBean>(parent, R.layout.view_item_message_board) {
            @Override
            public void setData(CommentBean item) {
                holder.setText(R.id.item_board_msg_reader_name_tv, item.mCommentName)
                        .setText(R.id.item_board_msg_content_tv, item.mContent)
                        .setText(R.id.item_board_msg_time_tv, item.mPublishTime)
                        .setText(R.id.item_board_msg_praise_count_tv, String.valueOf(item.mPraisedCount));

                holder.itemView.setBackgroundResource(item.mIsLight ? R.drawable.bg_item_f4f4f4 : R.drawable.bg_item_common);

                holder.setRoundImageUrl(R.id.item_board_msg_head_img_iv,
                        item.mCommentImage,
                        item.mIsMan ? R.mipmap.ic_head_mr : R.mipmap.ic_head_miss,
                        item.mIsMan ? R.mipmap.ic_head_mr : R.mipmap.ic_head_miss,
                        5);

                if (!TextUtils.isEmpty(item.mImagePath)) {
                    setVisible(R.id.item_board_msg_img_fl, View.VISIBLE);
                    holder.setImageUrl(R.id.item_board_msg_img_iv, R.color.color_ffffff, item.mImagePath);
                    holder.setTag(R.id.item_board_msg_img_fl, getAdapterPosition());
                    setOnClickListener(R.id.item_board_msg_img_fl, mTagClickListener);
                } else {
                    setVisible(R.id.item_board_msg_img_fl, View.GONE);
                    setOnClickListener(R.id.item_board_msg_img_fl, null);
                }

                if (item.mIsPraised) {
                    Drawable left = mContext.getResources().getDrawable(R.mipmap.ic_discuss_list_praise_done);
                    holder.setCompoundLeftDrawables(R.id.item_board_msg_praise_count_tv, left);
                    setOnClickListener(R.id.item_board_msg_praise_count_tv, null);
                    holder.setTextColor(R.id.item_board_msg_praise_count_tv, mContext.getResources().getColor(R.color.color_aa7243));
                } else {
                    Drawable left = mContext.getResources().getDrawable(R.mipmap.ic_discuss_list_praise_no);
                    holder.setCompoundLeftDrawables(R.id.item_board_msg_praise_count_tv, left);
                    holder.setTag(R.id.item_board_msg_praise_count_tv, getAdapterPosition());
                    setOnClickListener(R.id.item_board_msg_praise_count_tv, mPraiseClickListener);
                    holder.setTextColor(R.id.item_board_msg_praise_count_tv, mContext.getResources().getColor(R.color.color_999999));
                }

                if (item.mIsOwn) {//删除
                    holder.setText(R.id.item_board_msg_del_tv, "删除");
                    holder.setTag(R.id.item_board_msg_del_tv, item.mId);
                    setOnClickListener(R.id.item_board_msg_del_tv, mDelClickListener);
                } else {//回复
                    holder.setText(R.id.item_board_msg_del_tv, "回复");
                    holder.setTag(R.id.item_board_msg_del_tv, item.mId);
                    setOnClickListener(R.id.item_board_msg_del_tv, mReplyClickListener);
                }

                //回复列表
                if (item.mReplyContentList != null && item.mReplyContentList.size() > 0) {
                    holder.setVisible(R.id.item_board_msg_reply_list_ll, View.VISIBLE);
                    InformationReaderDiscussReplyAdapter adapter = new InformationReaderDiscussReplyAdapter(mContext, item.mReplyContentList);
                    ((LinearLayoutForListView) holder.getView(R.id.item_board_msg_reply_list)).setAdapter(adapter);
                    if (item.mReplyCount > 2) {
                        holder.setVisible(R.id.item_board_msg_reply_more_tv, View.VISIBLE);
                        holder.setText(R.id.item_board_msg_reply_more_tv, mContext.getResources().getString(R.string.discuss_more_reply, item.mReplyCount));
                        holder.setTag(R.id.item_board_msg_reply_more_tv, item.mId);
                        holder.setOnClickListener(R.id.item_board_msg_reply_more_tv, mMoreReplyClickListener);
                    } else {
                        holder.setVisible(R.id.item_board_msg_reply_more_tv, View.GONE);
                    }
                } else {
                    holder.setVisible(R.id.item_board_msg_reply_list_ll, View.GONE);
                }
            }
        };
    }
}
