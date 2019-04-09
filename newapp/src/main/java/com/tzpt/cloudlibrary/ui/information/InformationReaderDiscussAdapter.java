package com.tzpt.cloudlibrary.ui.information;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.bean.CommentBean;
import com.tzpt.cloudlibrary.widget.LinearLayoutForListView;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 读者评论列表适配器
 * Created by ZhiqiangJia on 2017-10-12.
 */
public class InformationReaderDiscussAdapter extends RecyclerArrayAdapter<CommentBean> {

    private ReaderDiscussListener mReaderDiscussListener;

    public InformationReaderDiscussAdapter(Context context, ReaderDiscussListener listener) {
        super(context);
        this.mReaderDiscussListener = listener;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<CommentBean>(parent, R.layout.view_discuss_item) {
            @Override
            public void setData(final CommentBean item) {
                holder.setText(R.id.discuss_reader_name_tv, item.mCommentName)
                        .setText(R.id.discuss_reader_content_tv, item.mContent)
                        .setText(R.id.discuss_reader_time_tv, item.mPublishTime)
                        .setText(R.id.discuss_praise_status_count_tv, String.valueOf(item.mPraisedCount));
                holder.itemView.setBackgroundResource(item.mIsLight ? R.drawable.bg_item_f4f4f4 : R.drawable.bg_item_common);

                holder.setRoundImageUrl(R.id.discuss_reader_head_iv,
                        item.mCommentImage,
                        item.mIsMan ? R.mipmap.ic_head_mr : R.mipmap.ic_head_miss,
                        item.mIsMan ? R.mipmap.ic_head_mr : R.mipmap.ic_head_miss,
                        5);

                if (item.mIsPraised) {
                    Drawable left = mContext.getResources().getDrawable(R.mipmap.ic_discuss_list_praise_done);
                    holder.setCompoundLeftDrawables(R.id.discuss_praise_status_count_tv, left);
                    holder.setTextColor(R.id.discuss_praise_status_count_tv, mContext.getResources().getColor(R.color.color_aa7243));
                    holder.setOnClickListener(R.id.discuss_praise_status_count_tv, null);

                } else {
                    Drawable left = mContext.getResources().getDrawable(R.mipmap.ic_discuss_list_praise_no);
                    holder.setCompoundLeftDrawables(R.id.discuss_praise_status_count_tv, left);
                    holder.setTextColor(R.id.discuss_praise_status_count_tv, mContext.getResources().getColor(R.color.color_999999));
                    //点赞
                    holder.setOnClickListener(R.id.discuss_praise_status_count_tv, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (null != mReaderDiscussListener) {
                                mReaderDiscussListener.praiseMsg(item.mId, getAdapterPosition(), item.mIsPraised, item.mPraisedCount);
                            }
                        }
                    });
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != mReaderDiscussListener) {
                            mReaderDiscussListener.toMoreReplyDetail(item.mId, getAdapterPosition());
                        }
                    }
                });


                if (item.mIsOwn) {//删除
                    holder.setText(R.id.discuss_own_status_tv, "删除");
                    holder.setOnClickListener(R.id.discuss_own_status_tv, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (null != mReaderDiscussListener) {
                                mReaderDiscussListener.delOwnMsg(item.mId, getAdapterPosition());
                            }
                        }
                    });
                } else {//回复
                    holder.setText(R.id.discuss_own_status_tv, "回复");
                    holder.setOnClickListener(R.id.discuss_own_status_tv, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (null != mReaderDiscussListener) {
                                mReaderDiscussListener.replyReaderMsg(item.mId, item.mCommentName, getAdapterPosition());
                            }
                        }
                    });
                }

                if (item.mReplyContentList != null && item.mReplyContentList.size() > 0) {
                    holder.setVisible(R.id.discuss_reply_list_ll, View.VISIBLE);
                    InformationReaderDiscussReplyAdapter adapter = new InformationReaderDiscussReplyAdapter(mContext, item.mReplyContentList);
                    ((LinearLayoutForListView) holder.getView(R.id.discuss_reply_list)).setAdapter(adapter);
                    if (item.mReplyCount > 2) {
                        holder.setVisible(R.id.discuss_reply_more_tv, View.VISIBLE);
                        holder.setText(R.id.discuss_reply_more_tv, mContext.getResources().getString(R.string.discuss_more_reply, item.mReplyCount));
                        holder.setOnClickListener(R.id.discuss_reply_more_tv, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (null != mReaderDiscussListener) {
                                    mReaderDiscussListener.toMoreReplyDetail(item.mId, getAdapterPosition());
                                }
                            }
                        });
                    } else {
                        holder.setVisible(R.id.discuss_reply_more_tv, View.GONE);
                    }
                } else {
                    holder.setVisible(R.id.discuss_reply_list_ll, View.GONE);
                }
            }
        };
    }

    public interface ReaderDiscussListener {

        void replyReaderMsg(long commentId, String readerName, int position);

        void delOwnMsg(long commentId, int position);

        void praiseMsg(long commentId, int position, boolean isPraised, int praisedCount);

        void toMoreReplyDetail(long commentId, int position);

    }
}
