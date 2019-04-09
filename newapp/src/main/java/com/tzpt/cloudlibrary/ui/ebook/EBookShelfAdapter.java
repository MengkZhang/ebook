package com.tzpt.cloudlibrary.ui.ebook;

import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.bean.LocalEBook;
import com.tzpt.cloudlibrary.utils.glide.GlideApp;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 电子书架
 * Created by Administrator on 2017/11/20.
 */
public class EBookShelfAdapter extends RecyclerArrayAdapter<LocalEBook> {

    private EBookCheckListener mCheckListener;
    protected SparseBooleanArray mSparseItemChecked = new SparseBooleanArray();
    private boolean mIsEditModel;

    public EBookShelfAdapter(Context context, EBookCheckListener checkListener) {
        super(context);
        this.mCheckListener = checkListener;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<LocalEBook>(parent, R.layout.view_ebook_shelf_item) {
            @Override
            public void setData(final LocalEBook item) {
                String descContent;//简介
                if (item.mDescContent != null) {//更新表后的字段有简介
                    descContent = item.mDescContent;
                } else {//原数据表中没有简介 显示暂无简介
                    descContent = mContext.getResources().getString(R.string.no_summary);
                }
                GlideApp.with(mContext)
                        .load(item.mCoverImg)
                        .error(R.mipmap.ic_nopicture)
                        .into((ImageView) holder.getView(R.id.item_ebook_cover_iv));

                holder.setText(R.id.item_ebook_title_tv, item.mTitle);
                holder.setText(R.id.item_ebook_read_progress_tv, item.mProgress);
                holder.setText(R.id.item_ebook_desc_tv, descContent);
                holder.setText(R.id.item_ebook_author_tv, item.mAuthor);

                CheckBox delBox = holder.getView(R.id.item_ebook_select_cb);
                if (mIsEditModel) {
                    delBox.setVisibility(View.VISIBLE);
                    delBox.setChecked(mSparseItemChecked.get(holder.getAdapterPosition()));
                } else {
                    delBox.setVisibility(View.GONE);
                    delBox.setChecked(false);
                }
            }
        };
    }

    /**
     * 是否编辑模式
     *
     * @return
     */
    public boolean isEditModel() {
        return mIsEditModel;
    }

    /**
     * 设置是否编辑模式
     *
     * @param editModel 编辑模式
     */
    public void setEditModel(boolean editModel) {
        this.mIsEditModel = editModel;
        if (!mIsEditModel) {
            mSparseItemChecked.clear();
        }
        notifyDataSetChanged();
    }

    /**
     * 设置选中或者不选中
     *
     * @param position 下标
     */
    public void setItemChecked(int position) {
        mSparseItemChecked.put(position, !mSparseItemChecked.get(position));
        notifyItemChanged(position); //导致界面跳动
//        notifyDataSetChanged();
        getCheckCount();
    }

    /**
     * 设置全选或者取消全选
     *
     * @param isChecked
     */
    public void setAllOrNoneChecked(boolean isChecked) {
        for (int i = 0; i < getCount(); i++) {
            mSparseItemChecked.put(i, isChecked);
        }
        notifyDataSetChanged();
        getCheckCount();
    }

    /**
     * 获取选中数量
     */
    private void getCheckCount() {
        int checkCount = 0;
        for (int i = 0; i < getCount(); i++) {
            if (mSparseItemChecked.get(i, false)) {
                checkCount++;
            }
        }
        mCheckListener.setCheckCount(checkCount);
    }

    public interface EBookCheckListener {

        void setCheckCount(int count);

    }
}
