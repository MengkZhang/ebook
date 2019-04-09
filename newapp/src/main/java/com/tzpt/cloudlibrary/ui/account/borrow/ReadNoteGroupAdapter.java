package com.tzpt.cloudlibrary.ui.account.borrow;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.business_bean.ReadNoteBean;
import com.tzpt.cloudlibrary.business_bean.ReadNoteGroupBean;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.GroupRecyclerArrayAdapter;

/**
 * Created by Administrator on 2018/11/7.
 */

public class ReadNoteGroupAdapter extends GroupRecyclerArrayAdapter<ReadNoteGroupBean> {
    private View.OnClickListener mDelClickListener;

    public ReadNoteGroupAdapter(Context context) {
        super(context);
    }

    public void setDelChildItemListener(View.OnClickListener listener) {
        mDelClickListener = listener;
    }

    @Override
    protected int getChildCount(ReadNoteGroupBean group) {
        return group.mNoteList.size();
    }

    @Override
    protected BaseViewHolder onCreateGroupViewHolder(ViewGroup parent) {
        return new BaseViewHolder<ReadNoteGroupBean>(parent, R.layout.view_reader_notes_title_item);
    }

    @Override
    protected BaseViewHolder onCreateChildViewHolder(ViewGroup parent) {
        return new BaseViewHolder<ReadNoteGroupBean>(parent, R.layout.view_reader_notes_item);
    }

    @Override
    protected void onBindGroupViewHolder(BaseViewHolder holder, int itemPosition, int groupPosition) {
        if (groupPosition == 0) {
            holder.setVisible(R.id.reader_notes_title_divider_view, View.GONE);
        } else {
            holder.setVisible(R.id.reader_notes_title_divider_view, View.VISIBLE);
        }
        ReadNoteGroupBean item = getItem(groupPosition);
        holder.setText(R.id.reader_notes_title_tv, TextUtils.isEmpty(item.mBook.mName) ? "" : item.mBook.mName);
    }

    @Override
    protected void onBindChildViewHolder(BaseViewHolder holder, int itemPosition, final int groupPosition, final int childPosition) {
        ReadNoteGroupBean item = getItem(groupPosition);
        ReadNoteBean noteItem = item.mNoteList.get(childPosition);
        holder.setText(R.id.reader_notes_content_tv, TextUtils.isEmpty(noteItem.mNote.mContent) ? "" : noteItem.mNote.mContent);
        holder.setText(R.id.reader_notes_date_tv, TextUtils.isEmpty(noteItem.mNote.mModifyDate) ? "" : noteItem.mNote.mModifyDate);

        holder.setTag(R.id.reader_notes_del_tv, itemPosition);
        holder.setOnClickListener(R.id.reader_notes_del_tv, mDelClickListener);
    }

    @Override
    protected void removeChild(ReadNoteGroupBean group, int childPosition) {
        group.mNoteList.remove(childPosition);
    }
}
