package com.tzpt.cloudlibrary.ui.information;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.bean.CommentBean;
import com.tzpt.cloudlibrary.utils.StringUtils;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 读者评论列表适配器
 * Created by ZhiqiangJia on 2017-10-12.
 */
public class InformationCommentDetailsAdapter extends RecyclerArrayAdapter<CommentBean> {

    private static final int COMMENT_HEAD = 0;
    private static final int COMMENT_CONTENT = 1;
    private ReaderCommentReplyListener mReaderDiscussListener;

    public InformationCommentDetailsAdapter(Context context, ReaderCommentReplyListener listener) {
        super(context);
        this.mReaderDiscussListener = listener;
    }

    @Override
    public int getViewType(int position) {
        if (position == 0) {
            return COMMENT_HEAD;
        } else {
            return COMMENT_CONTENT;
        }
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == COMMENT_HEAD) {
            return new BaseViewHolder<CommentBean>(parent, R.layout.view_comment_head_item) {
                @Override
                public void setData(final CommentBean item) {

                    holder.setText(R.id.discuss_reader_name_tv, StringUtils.formatCommentTitle(item.mCommentName))
                            .setText(R.id.discuss_reader_content_tv, item.mContent)
                            .setText(R.id.discuss_reader_time_tv, item.mPublishTime)
                            .setText(R.id.discuss_comment_reply_count_tv, mContext.getString(R.string.reply_number, item.mReplyCount))
                            .setText(R.id.discuss_praise_status_count_tv, String.valueOf(item.mPraisedCount));

                    holder.setRoundImageUrl(R.id.discuss_reader_head_iv,
                            item.mCommentImage,
                            item.mIsMan ? R.mipmap.ic_head_mr : R.mipmap.ic_head_miss,
                            item.mIsMan ? R.mipmap.ic_head_mr : R.mipmap.ic_head_miss,
                            5);

                    if (!TextUtils.isEmpty(item.mImagePath)) {
                        holder.setVisible(R.id.discuss_img_iv, View.VISIBLE);
                        holder.setImageUrl(R.id.discuss_img_iv, R.color.color_ffffff, item.mImagePath);


                        holder.setOnClickListener(R.id.discuss_img_iv, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mReaderDiscussListener != null) {
                                    mReaderDiscussListener.onImageClicked(item.mImagePath, (ImageView) holder.getView(R.id.discuss_img_iv));
                                }
                            }
                        });
                    } else {
                        holder.setVisible(R.id.discuss_img_iv, View.GONE);
                        holder.setOnClickListener(R.id.discuss_img_iv, null);
                    }

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

                    if (item.mIsOwn) {//删除
                        holder.setText(R.id.discuss_own_status_tv, "删除");
                        holder.setOnClickListener(R.id.discuss_own_status_tv, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (null != mReaderDiscussListener) {
                                    mReaderDiscussListener.delReaderMsg(item.mId, getAdapterPosition());
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
                }
            };
        } else {
            return new BaseViewHolder<CommentBean>(parent, R.layout.view_comment_content_item) {
                @Override
                public void setData(final CommentBean item) {
                    holder.setText(R.id.discuss_reader_name_tv, StringUtils.formatCommentTitle(item.mCommentName, item.mRepliedName))
                            .setText(R.id.discuss_reader_content_tv, item.mContent)
                            .setText(R.id.discuss_reader_time_tv, item.mPublishTime)
                            .setText(R.id.discuss_praise_status_count_tv, String.valueOf(item.mPraisedCount));
                    holder.itemView.setBackgroundColor(item.mIsLight ? Color.parseColor("#f4f4f4") : Color.parseColor("#ffffff"));

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

                    if (item.mIsOwn) {//删除
                        holder.setText(R.id.discuss_own_status_tv, "删除");
                        holder.setOnClickListener(R.id.discuss_own_status_tv, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (null != mReaderDiscussListener) {
                                    mReaderDiscussListener.delReaderMsg(item.mId, getAdapterPosition());
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
                }
            };
        }
    }

    public interface ReaderCommentReplyListener {

        void replyReaderMsg(long commentId, String readerName, int position);

        void delReaderMsg(long commentId, int position);

        void praiseMsg(long commentId, int position, boolean isPraised, int praisedCount);

        void onImageClicked(String imagePath, ImageView imageView);

    }
}
