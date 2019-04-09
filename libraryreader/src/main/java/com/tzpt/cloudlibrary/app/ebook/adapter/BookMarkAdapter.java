package com.tzpt.cloudlibrary.app.ebook.adapter;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tzpt.cloudlibrary.app.ebook.R;
import com.tzpt.cloudlibrary.app.ebook.books.model.EpubBookMarks;
import com.tzpt.cloudlibrary.app.ebook.utils.HelpUtils;

/**
 * 图书适配器
 * Created by Administrator on 2016/1/13.
 */
public class BookMarkAdapter extends BaseRecyclerAdapter<EpubBookMarks> {

    private Typeface typeFace;

    public BookMarkAdapter(Typeface typeFace) {
        this.typeFace = typeFace;
    }

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_item_reader_bookmark, parent, false);
        return new EBookMenuHolder(view);
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int RealPosition, EpubBookMarks data) {
        if (viewHolder instanceof EBookMenuHolder) {
            EBookMenuHolder holder = (EBookMenuHolder) viewHolder;
            holder.itemView.setTag(RealPosition);
            if (null == data.title || TextUtils.isEmpty(data.title)) {
                holder.textViewBookName.setText(new StringBuilder().append("书签：").append(RealPosition + 1));
            } else {
                holder.textViewBookName.setText(data.title);
            }
            String pecent = HelpUtils.calcDivision(data.current_page, data.total_page);
            holder.textViewProgress.setText(new StringBuilder().append(pecent).append("%"));
            holder.textViewDate.setText(HelpUtils.getDateTime(String.valueOf(data.last_access_date)));//data.last_access_date
            try {
                if (!TextUtils.isEmpty(data.id)) {
                    holder.itemView.setOnLongClickListener(new DeleteListener(data.last_access_date, data.current_page));
                }
            } catch (Exception e) {
            }
        }
    }


    private class EBookMenuHolder extends Holder {

        TextView textViewBookName;
        TextView textViewProgress;
        TextView textViewDate;

        private EBookMenuHolder(View view) {
            super(view);
            textViewBookName = (TextView) view.findViewById(R.id.textViewBookName);
            textViewProgress = (TextView) view.findViewById(R.id.textViewProgress);
            textViewDate = (TextView) view.findViewById(R.id.textViewDate);
            if (null != typeFace) {
                textViewBookName.setTypeface(typeFace);
                textViewProgress.setTypeface(typeFace);
                textViewDate.setTypeface(typeFace);
            }
        }
    }

    private class DeleteListener implements View.OnLongClickListener {

        private long lastAccessTime;
        private long bookPageIndex;

        private DeleteListener(long lastAccessTime, long bookPageIndex) {
            this.lastAccessTime = lastAccessTime;
            this.bookPageIndex = bookPageIndex;
        }

        @Override
        public boolean onLongClick(View v) {
            if (null != callback) {
                callback.callbackItemLongClick(lastAccessTime, bookPageIndex);
            }
            return true;
        }
    }

    public void setOnItemDeleteLongClickListener(BookMarkDeleteInterface callback) {
        this.callback = callback;
    }

    public BookMarkDeleteInterface callback;

    public interface BookMarkDeleteInterface {

        void callbackItemLongClick(long time, long bookPageIndex);
    }

}

