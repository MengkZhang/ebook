package com.tzpt.cloudlibrary.ui.ebook;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tzpt.cloudlibrary.CloudLibraryApplication;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.business_bean.EBookBean;
import com.tzpt.cloudlibrary.rxbus.RxBus;
import com.tzpt.cloudlibrary.rxbus.event.EBookEvent;
import com.tzpt.cloudlibrary.utils.StringUtils;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 收藏-电子书
 * Created by ZhiqiangJia on 2017-08-08.
 */
public class EBookCollectionAdapter extends RecyclerArrayAdapter<EBookBean> {

    private boolean mIsEditMode = false;
    protected SparseBooleanArray mSparseItemChecked = new SparseBooleanArray();
    private final RxBus mRxBus;

    public EBookCollectionAdapter(Context context) {
        super(context);
        mRxBus = CloudLibraryApplication.mRxBus;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<EBookBean>(parent, R.layout.view_item_ebook) {
            @Override
            public void setData(final EBookBean item) {
                if (null != item) {
                    holder.setImageUrl(R.id.ebook_item_img, item.mEBook.mCoverImg, R.drawable.color_default_image);
                    holder.setText(R.id.ebook_item_title, TextUtils.isEmpty(item.mEBook.mName) ? "" : item.mEBook.mName)
                            .setText(R.id.ebook_item_anthor, TextUtils.isEmpty(item.mAuthor.mName) ? "" : item.mAuthor.mName)
                            .setText(R.id.ebook_item_content, TextUtils.isEmpty(item.mEBook.mSummary) ? "" : item.mEBook.mSummary)
                            .setText(R.id.ebook_item_preview_count_tv, StringUtils.formatBorrowCount(item.mReadCount));

                    setVisible(R.id.ebook_item_new_flag_tv, item.mHasNewEBookFlag ? View.VISIBLE : View.GONE);
                    //设置收藏电子书选中状态
                    CheckBox delCB = holder.getView(R.id.ebook_item_cb);
                    if (mIsEditMode) {
                        delCB.setVisibility(View.VISIBLE);
                        delCB.setChecked(mSparseItemChecked.get(holder.getAdapterPosition()));
                    } else {
                        delCB.setVisibility(View.GONE);
                        delCB.setChecked(false);
                    }
                }
            }
        };
    }

    /**
     * 编辑模式设置选中item
     *
     * @param position
     */
    public void chooseCollectionEBook(int position) {
        mSparseItemChecked.put(position, !mSparseItemChecked.get(position));
        notifyItemChanged(position);
        handleCheckedChanged();
    }

    /**
     * 检查当前是否可编辑
     */
    public void checkEditorAble() {
        mRxBus.post(new EBookEvent(1, getCount() > 0));
    }

    public boolean isEditMode() {
        return mIsEditMode;
    }

    /**
     * 设置编辑模式
     *
     * @param editMode
     */
    public void setEditMode(boolean editMode) {
        mIsEditMode = editMode;
        if (!mIsEditMode) {
            mSparseItemChecked.clear();
        }
        notifyDataSetChanged();
    }

    /**
     * 处理选中事件
     */
    private void handleCheckedChanged() {
        int checkedCount = 0;
        for (int i = 0; i < getCount(); i++) {
            if (mSparseItemChecked.get(i, false)) {
                checkedCount++;
            }
        }
        // 通知 删除数量 更新界面
        mRxBus.post(new EBookEvent(0, checkedCount));
    }

    /**
     * 是否全选
     *
     * @param isChecked 全选中和全不选
     */
    public void checkAllOrNone(boolean isChecked) {
        for (int i = 0; i < getCount(); i++) {
            mSparseItemChecked.put(i, isChecked);
        }
        notifyDataSetChanged();
        handleCheckedChanged();
    }

}
