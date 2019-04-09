package com.tzpt.cloudlibrary.ui.ebook;

import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.cbreader.bookmodel.TOCTree;
import com.tzpt.cloudlibrary.zlibrary.core.tree.ZLTree;

import java.util.ArrayList;
import java.util.List;

/**
 * 目录列表Adapter
 * Created by Administrator on 2017/10/23.
 */

public class EBookTocListAdapter extends BaseAdapter {
    private final ZLTree<?> mRoot;
    private ZLTree<?>[] mItems;
    private final List<ZLTree<?>> mOpenItems = new ArrayList<>();
    private int mCurrentIndex;
//    private Typeface mTypeface;

    public EBookTocListAdapter(TOCTree root, Typeface typeface) {
        mRoot = root;
        mItems = new ZLTree[root.getSize() - 1];
        mOpenItems.add(root);
//        mTypeface = typeface;
    }

    private int getCount(ZLTree<?> tree) {
        int count = 1;
        if (isOpen(tree)) {
            for (ZLTree<?> subtree : tree.subtrees()) {
                count += getCount(subtree);
            }
        }
        return count;
    }

    private void openTree(ZLTree<?> tree) {
        if (tree == null) {
            return;
        }
        while (!mOpenItems.contains(tree)) {
            mOpenItems.add(tree);
            tree = tree.Parent;
        }
    }

    private boolean isOpen(ZLTree<?> tree) {
        return mOpenItems.contains(tree);
    }

    public final void expandOrCollapseTree(ZLTree<?> tree) {
        if (!tree.hasChildren()) {
            return;
        }
        if (isOpen(tree)) {
            mOpenItems.remove(tree);
        } else {
            mOpenItems.add(tree);
        }
        notifyDataSetChanged();
    }


    private int indexByPosition(int position, ZLTree<?> tree) {
        if (position == 0) {
            return 0;
        }
        --position;
        int index = 1;
        for (ZLTree<?> subtree : tree.subtrees()) {
            int count = getCount(subtree);
            if (count <= position) {
                position -= count;
                index += subtree.getSize();
            } else {
                return index + indexByPosition(position, subtree);
            }
        }
        throw new RuntimeException("That's impossible!!!");
    }

    public final int selectItem(ZLTree<?> tree) {
        if (tree == null) {
            return 0;
        }
        openTree(tree.Parent);
        int position = 0;
        while (true) {
            ZLTree<?> parent = tree.Parent;
            if (parent == null) {
                break;
            }
            for (ZLTree<?> sibling : parent.subtrees()) {
                if (sibling == tree) {
                    break;
                }
                position += getCount(sibling);
            }
            tree = parent;
            ++position;
        }
        if (position > 0) {
            position = position - 1;
        }
        mCurrentIndex = indexByPosition(position + 1, mRoot) - 1;
        return position;
    }

    @Override
    public int getCount() {
        return getCount(mRoot) - 1;
    }

    @Override
    public ZLTree<?> getItem(int position) {
        final int index = indexByPosition(position + 1, mRoot) - 1;
        ZLTree<?> item = mItems[index];
        if (item == null) {
            item = mRoot.getTreeByParagraphNumber(index + 1);
            mItems[index] = item;
        }
        return item;
    }

    @Override
    public long getItemId(int position) {
        return indexByPosition(position + 1, mRoot);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_book_toc_list_item, parent, false);
            holder.mTocTitleTv = (TextView) convertView.findViewById(R.id.toc_tree_item_tv);
            holder.mDivider = (ImageView) convertView.findViewById(R.id.toc_list_divider);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final TOCTree tree = (TOCTree) getItem(position);
        holder.mTocTitleTv.setPadding((tree.Level - 1) * 60, 0, 0, 0);
        holder.mDivider.setPadding((tree.Level - 1) * 60, 0, 0, 0);
        holder.mTocTitleTv.setText(tree.getText());
        if (mCurrentIndex == indexByPosition(position + 1, mRoot) - 1) {
            holder.mTocTitleTv.setTextColor(Color.parseColor("#8a633d"));
        } else {
            holder.mTocTitleTv.setTextColor(Color.parseColor("#333333"));
        }
//
//        final View view = (convertView != null) ? convertView : LayoutInflater.from(parent.getContext()).inflate(R.layout.view_book_toc_list_item, parent, false);
//        final TOCTree tree = (TOCTree) getItem(position);
//        TextView tv = (TextView) view.findViewById(R.id.toc_tree_item_tv);
//        tv.setPadding((tree.Level - 1) * 60, 0, 0, 0);
//        tv.setText(tree.getText());
//        ImageView iv = (ImageView) view.findViewById(R.id.toc_list_divider);
//        if (mCurrentIndex == indexByPosition(position + 1, mRoot) - 1) {
//            tv.setTextColor(Color.parseColor("#8a633d"));
//        } else {
//            tv.setTextColor(Color.parseColor("#333333"));
//        }
        return convertView;
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        if (observer != null) {
            super.unregisterDataSetObserver(observer);
        }
    }

    private class ViewHolder {
        TextView mTocTitleTv;
        ImageView mDivider;
    }
}
