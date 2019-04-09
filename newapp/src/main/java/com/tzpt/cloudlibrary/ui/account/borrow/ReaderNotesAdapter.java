package com.tzpt.cloudlibrary.ui.account.borrow;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.business_bean.ReadNoteBean;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 笔记列表
 * Created by ZhiqiangJia on 2017-08-17.
 */
public class ReaderNotesAdapter extends RecyclerArrayAdapter<ReadNoteBean> {

    private View.OnClickListener mDelListener;

    public ReaderNotesAdapter(Context context, View.OnClickListener delListener) {
        super(context);
        mDelListener = delListener;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {

        return new BaseViewHolder<ReadNoteBean>(parent, R.layout.view_reader_notes_item) {
            @Override
            public void setData(ReadNoteBean item) {
                holder.setText(R.id.reader_notes_content_tv, TextUtils.isEmpty(item.mNote.mContent) ? "" : item.mNote.mContent);
                holder.setText(R.id.reader_notes_date_tv, TextUtils.isEmpty(item.mNote.mModifyDate) ? "" : item.mNote.mModifyDate);
                holder.setTag(R.id.reader_notes_del_tv, ReaderNotesAdapter.this.getPosition(item));
                setOnClickListener(R.id.reader_notes_del_tv, mDelListener);
            }
        };
    }
}
