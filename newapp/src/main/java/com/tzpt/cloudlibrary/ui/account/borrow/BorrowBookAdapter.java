package com.tzpt.cloudlibrary.ui.account.borrow;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.business_bean.BorrowBookBean;
import com.tzpt.cloudlibrary.widget.DrawableCenterTextView;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 借阅列表
 * Created by ZhiqiangJia on 2017-08-17.
 */
public class BorrowBookAdapter extends RecyclerArrayAdapter<BorrowBookBean> {

    private View.OnClickListener mOnClickListener;
    private View.OnClickListener mThumpListener;
    private View.OnClickListener mNoteListener;
    private View.OnClickListener mOneKeyBorrowListener;
    private View.OnClickListener mLostBookClickListener;
    private boolean isContinueBorrow;

    public BorrowBookAdapter(Context context,
                             View.OnClickListener listener,
                             View.OnClickListener thumpListener,
                             View.OnClickListener noteListener,
                             View.OnClickListener oneKeyBorrowListener,
                             View.OnClickListener lostBookClickListener) {
        super(context);
        this.mOnClickListener = listener;
        this.mThumpListener = thumpListener;
        this.mNoteListener = noteListener;
        this.mOneKeyBorrowListener = oneKeyBorrowListener;
        this.mLostBookClickListener = lostBookClickListener;
    }

    /**
     * 刷新某一个item
     *
     * @param position
     */
    void notifySomeOneItem(int position, boolean isRefresh) {
        this.isContinueBorrow = isRefresh;
        notifyItemChanged(position);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<BorrowBookBean>(parent, R.layout.view_borrow_book_item) {
            @Override
            public void setData(BorrowBookBean item) {
                int position = getAdapterPosition();
                //借阅状态 5:在借 6:归还 7:赔偿 28:逾期购买
                setImageUrl(R.id.item_book_image, item.mBook.mCoverImg, R.drawable.color_default_image);
                holder.setText(R.id.item_book_author_con, item.mAuthor.mName);//作者
                holder.setText(R.id.item_book_publishing_year_con, item.mBook.mIsbn);//isbn
                holder.setText(R.id.item_book_publishing_company_con, item.mPress.mName);//出版社
                holder.setText(R.id.item_book_isbn_con, item.mLibrary.mName);//所在馆
                holder.setText(R.id.item_book_category_con, item.mHistoryBorrowDate);//借阅日

                holder.setText(R.id.item_book_title, item.mBook.mName)
                        .setText(R.id.item_book_author, mContext.getString(R.string.book_list_author))//作者
                        .setText(R.id.item_book_publishing_year, mContext.getString(R.string.book_list_isbn))//isbn
                        .setText(R.id.item_book_publishing_company, mContext.getString(R.string.book_list_publisher))//出版社
                        .setText(R.id.item_book_isbn, mContext.getString(R.string.stay_library2))//所在馆item_book_isbn
                        .setText(R.id.item_book_category, mContext.getString(R.string.borrow_book_date));//借阅日
                setVisible(R.id.item_book_publishing_library, View.GONE);
                setVisible(R.id.item_book_publishing_library_con, View.GONE);
                //全局监听
                holder.setTag(R.id.item_book_layout, position);
                setOnClickListener(R.id.item_book_layout, mOnClickListener);
                //点赞监听
                holder.setTag(R.id.item_book_thumbs_up, position);
                setOnClickListener(R.id.item_book_thumbs_up, mThumpListener);
                //读书笔记监听
                holder.setTag(R.id.item_book_write_note_tv, position);
                setOnClickListener(R.id.item_book_write_note_tv, mNoteListener);

                //是根据returnTime字段(item.mHistoryBackDate)来判断是否是历史记录的 如果已经还书 该字段有还书时间 否则该字段为null
                if (!TextUtils.isEmpty(item.mHistoryBackDate)) {
                    //历史借阅
                    setVisible(R.id.end_time_book_tv, View.GONE);
                    //历史借阅隐藏一件续借按钮
                    setVisible(R.id.one_key_to_re_new, View.GONE);
                    setOnClickListener(R.id.one_key_to_re_new, null);
                    //历史借阅隐藏 逾期作自动购买处理的提示
                    setVisible(R.id.item_book_over_due_to_buy_tv, View.GONE);

                    //在历史借阅中 显示 已还书 已赔书 或者 已购买 并且该按钮不能点击
                    setVisible(R.id.item_book_lost_tv, View.VISIBLE);
                    DrawableCenterTextView drawableCenterTextView = getView(R.id.item_book_lost_tv);
                    drawableCenterTextView.setEnabled(false);
                    setOnClickListener(R.id.item_book_lost_tv, null);
                    if (item.mIsBuy) {
                        holder.setText(R.id.item_book_lost_tv, "已购买");

                        setBookStatusDrawable(R.mipmap.ic_re_buy_book);
                    } else if (item.mIsLost) {
                        holder.setText(R.id.item_book_lost_tv, "已赔书");

                        //ic_re_buy_book
                        setBookStatusDrawable(R.mipmap.ic_re_buy_book);
                    } else {
                        holder.setText(R.id.item_book_lost_tv, "已还书");

                        //已还书背景
                        setBookStatusDrawable(R.mipmap.ic_have_return_back_book);
                    }

                } else {
                    //当前借阅
                    setVisible(R.id.end_time_book_tv, View.VISIBLE);
                    hasOtherTimeAndOneKeyContinueBorrow(item, position);

                    //在当前借阅中 显示 赔书 按钮可点击
                    //显示赔书UI
                    setVisible(R.id.item_book_lost_tv, View.VISIBLE);
                    holder.setText(R.id.item_book_lost_tv, "赔书");
                    setBookStatusDrawable(R.mipmap.ic_re_buy_book);
                    DrawableCenterTextView drawableCenterTextView = getView(R.id.item_book_lost_tv);
                    drawableCenterTextView.setEnabled(true);
                    setTag(R.id.item_book_lost_tv, position);
                    setOnClickListener(R.id.item_book_lost_tv, mLostBookClickListener);
                }
                //设置点赞状态
                setText(R.id.item_book_thumbs_up, item.mIsPraised ? "已赞" : "点赞");
                Drawable flup;
                if (item.mIsPraised) {
                    flup = mContext.getResources().getDrawable(R.mipmap.ic_click_have_praise);
                } else {
                    flup = mContext.getResources().getDrawable(R.mipmap.ic_click_praise);
                }
                flup.setBounds(0, 0, flup.getMinimumWidth(), flup.getMinimumHeight());
                ((TextView) getView(R.id.item_book_thumbs_up)).setCompoundDrawables(flup, null, null, null);
            }

            /**
             * 设置图书在几种状态下的图片
             * @param p ：资源图片
             */
            private void setBookStatusDrawable(int p) {
                Drawable drawable = mContext.getResources().getDrawable(p);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                ((TextView) getView(R.id.item_book_lost_tv)).setCompoundDrawables(drawable, null, null, null);
            }

            /**
             * 剩余借阅时间
             * @param item ：数据item
             * @param position : 位置
             */
            private void hasOtherTimeAndOneKeyContinueBorrow(final BorrowBookBean item, final int position) {
                String hasOtherDays;
                //如果当前借阅有逾期未归还 还有另外一个字段overDue来判断  //1逾期0未知2未逾期  剩余天数 是黄色 fbae4d  逾期和今日到期 是红色 fb755a
                if (item.mIsOverdue) {//已逾期

                    setVisible(R.id.item_book_over_due_to_buy_tv, View.VISIBLE);//显示逾期作自动购买处理
                    hasOtherDays = mContext.getResources().getString(R.string.over_due);
                    holder.setText(R.id.end_time_book_tv, hasOtherDays);
                    holder.getView(R.id.end_time_book_tv).setBackgroundColor(Color.parseColor("#fb755a"));//红色
                } else {//没有逾期

                    setVisible(R.id.item_book_over_due_to_buy_tv, View.GONE);//不显示逾期作自动购买处理
                    if (item.mHasDays == 0) {//今天到期
                        hasOtherDays = mContext.getResources().getString(R.string.have_other_time_today);
                        holder.setText(R.id.end_time_book_tv, hasOtherDays);
                        holder.getView(R.id.end_time_book_tv).setBackgroundColor(Color.parseColor("#fb755a"));//红色
                    } else {//显示剩余天数

                        //剩余时间分为续借剩余和未续借剩余
                        if (isContinueBorrow) {//续借后的剩余时间
                            if ((item.mBorrowDays - 1) > 0) {
                                hasOtherDays = mContext.getResources().getString(R.string.have_other_time) + (item.mBorrowDays - 1) + mContext.getResources().getString(R.string.have_other_time_day);
                            } else {
                                hasOtherDays = mContext.getResources().getString(R.string.over_due);
                            }
                            holder.setText(R.id.end_time_book_tv, hasOtherDays);
                        } else {//未续借的剩余时间
                            if (item.mHasDays > 0) {
                                hasOtherDays = mContext.getResources().getString(R.string.have_other_time) + item.mHasDays + mContext.getResources().getString(R.string.have_other_time_day);
                            } else {
                                hasOtherDays = mContext.getResources().getString(R.string.over_due);
                            }
                            holder.setText(R.id.end_time_book_tv, hasOtherDays);
                        }

                        holder.getView(R.id.end_time_book_tv).setBackgroundColor(Color.parseColor("#fbae4d"));//黄色
                    }
                    //一键续借是在当前借阅-没有逾期的情况下 才有的逻辑
                    if (item.mOneKeyToRenew) {
                        //在一键借阅中 如果 已经续借了 刷新后则不显示
                        if (isContinueBorrow) {
                            setVisible(R.id.one_key_to_re_new, View.GONE);
                            setOnClickListener(R.id.one_key_to_re_new, null);
                        } else {
                            setVisible(R.id.one_key_to_re_new, View.VISIBLE);
                            holder.setTag(R.id.one_key_to_re_new, position);
                            setOnClickListener(R.id.one_key_to_re_new, mOneKeyBorrowListener);
                        }
                    } else {
                        setVisible(R.id.one_key_to_re_new, View.GONE);
                        setOnClickListener(R.id.one_key_to_re_new, null);
                    }
                }
            }
        };
    }

}
