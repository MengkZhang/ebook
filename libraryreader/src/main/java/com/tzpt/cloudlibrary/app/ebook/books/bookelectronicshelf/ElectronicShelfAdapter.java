package com.tzpt.cloudlibrary.app.ebook.books.bookelectronicshelf;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.tzpt.cloudlibrary.app.ebook.R;
import com.tzpt.cloudlibrary.app.ebook.adapter.BaseRecyclerAdapter;
import com.tzpt.cloudlibrary.app.ebook.books.model.BookLastMark;
import com.tzpt.cloudlibrary.app.ebook.books.model.EpubBookMarks;
import com.tzpt.cloudlibrary.app.ebook.books.model.LocalBook;
import com.tzpt.cloudlibrary.app.ebook.constant.EbookConstant;
import com.tzpt.cloudlibrary.app.ebook.utils.FileUtil;
import com.tzpt.cloudlibrary.app.ebook.utils.HelpUtils;
import com.tzpt.cloudlibrary.app.ebook.utils.ImageLoader;

/**
 * 电子书书架
 * Created by ZhiqiangJia on 2017-03-01.
 */
public class ElectronicShelfAdapter extends BaseRecyclerAdapter<LocalBook> {

    private Context mContext;

    public ElectronicShelfAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_item_reader_electionshelf, parent, false);
        return new HomeViewHolder(view);
    }


    /**
     * 设置为删除模式
     */
    public void showDeleteMode(boolean isDeleteMode) {
        int size = getDatas().size();
        if (size == 0) return;
        for (int i = 0; i < size; i++) {
            getDatas().get(i).isDeleteMode = isDeleteMode;
        }
        notifyDataSetChanged();
    }

    /**
     * 设置为全选
     *
     * @param selected
     */
    public void selectAllShelf(boolean selected) {
        int size = getDatas().size();
        if (size == 0) return;
        for (int i = 0; i < size; i++) {
            getDatas().get(i).selected = selected;
        }
        notifyDataSetChanged();
    }

    /**
     * 执行删除书架
     */
    public void deleteSelectElectronicShelf() {
        int size = getDatas().size();
        if (size == 0) return;
        for (int i = 0; i < size; i++) {
            LocalBook bean = getDatas().get(i);

            if (bean.selected) {
                deleteSelectElectronicShelfByBookId(bean.id, bean.author);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * 删除数据库中书架内容
     *
     * @param bookId
     */
    private void deleteSelectElectronicShelfByBookId(String bookId, String fileName) {
        new LocalBook(mContext).delete(bookId);
        new EpubBookMarks(mContext).delete(bookId);
        new BookLastMark(mContext).delete(bookId);
        //删除文件
        FileUtil.deleteFile(EbookConstant.FILE_SAVE_URL + fileName);
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int position, final LocalBook data) {
        if (viewHolder instanceof HomeViewHolder) {
            HomeViewHolder holder = (HomeViewHolder) viewHolder;
            holder.itemView.setTag(position);
            if (null != data) {
                String imagePath = data.medium_image;
                if (!TextUtils.isEmpty(imagePath)) {
                    showImage(imagePath, holder.itemBookImage);
                } else {
                    holder.itemBookImage.setImageResource(R.mipmap.ic_nopicture);
                }
                String pecent = HelpUtils.calcDivision(data.current_page, data.total_page);
                holder.textViewProgress.setText("阅读进度：" + pecent + "%");
                holder.textViewBookName.setText(TextUtils.isEmpty(data.title) ? "" : data.title);
                holder.checkBoxShelf.setChecked(data.selected);
                holder.checkBoxShelf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        data.selected = isChecked;
                    }
                });
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (null != listener) {
                            listener.callbackOnLongClick();
                        }
                        return false;
                    }
                });
                //如果是删除模式
                if (data.isDeleteMode) {
                    holder.checkBoxShelf.setVisibility(View.VISIBLE);
                    holder.itemBookShadow.setVisibility(View.VISIBLE);
                } else {
                    holder.checkBoxShelf.setVisibility(View.GONE);
                    holder.itemBookShadow.setVisibility(View.GONE);
                }
            }
        }
    }

    private class HomeViewHolder extends Holder {

        SimpleDraweeView itemBookImage;
        TextView textViewBookName;
        TextView textViewProgress;
        CheckBox checkBoxShelf;
        View itemBookShadow;

        private HomeViewHolder(View view) {
            super(view);
            itemBookImage = (SimpleDraweeView) view.findViewById(R.id.item_book_image);
            textViewBookName = (TextView) view.findViewById(R.id.textViewBookName);
            textViewProgress = (TextView) view.findViewById(R.id.textViewProgress);
            checkBoxShelf = (CheckBox) view.findViewById(R.id.checkBoxShelf);
            itemBookShadow = view.findViewById(R.id.item_book_shadow);
        }
    }

    private ElectrionicShelfInterface listener;

    public void setEletrionicShelfListener(ElectrionicShelfInterface listener) {
        this.listener = listener;
    }

    public interface ElectrionicShelfInterface {

        void callbackOnLongClick();
    }

    /**
     * 加载图片
     *
     * @param url
     * @param imageView
     */
    private void showImage(String url, final SimpleDraweeView imageView) {
        ImageLoader.loadImage(imageView, url);
    }
}
