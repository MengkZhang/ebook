package com.tzpt.cloudlibrary.app.ebook.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tzpt.cloudlibrary.app.ebook.R;
import com.tzpt.cloudlibrary.app.ebook.books.model.Chapter;
import com.tzpt.cloudlibrary.app.ebook.books.model.ReaderSettings;
import com.tzpt.cloudlibrary.app.ebook.books.parser.EpubParser;

import java.util.ArrayList;


/**
 * 目录适配器
 *
 * @author Administrator
 */
public class CloudLibraryTOCAdapter extends BaseAdapter {

    private final ItemListener itemListener = new ItemListener();
    private final CollapseListener collapseListener = new CollapseListener();
    private final Context context;
    private final SparseArray<Chapter> objects;
    private final ArrayList<EpubParser.NavPoint> navMap;
    private final TOCItemState[] states;
    private final SparseIntArray mapping = new SparseIntArray();
    private int currentId;
    private final ReaderSettings settings;
    private Typeface typeFace;

    public CloudLibraryTOCAdapter(final Context context,
                                  ReaderSettings settings,
                                  final SparseArray<Chapter> objects,
                                  ArrayList<EpubParser.NavPoint> navMap,
                                  final int curChapterIndex,
                                  Typeface typeFace) {
        this.context = context;
        this.settings = settings;
        this.navMap = navMap;
        this.objects = objects;
        this.states = new TOCItemState[this.navMap.size()];
        this.typeFace = typeFace;
        boolean treeFound = false;
        for (int i = 0; i < this.navMap.size(); i++) {
            mapping.put(i, i);
            final int next = i + 1;
            if (next < this.navMap.size() && this.navMap.get(i).navLevel < this.navMap.get(next).navLevel) {
                states[i] = TOCItemState.COLLAPSED;
                treeFound = true;
            } else {
                states[i] = TOCItemState.LEAF;
            }
        }
        currentId = curChapterIndex;
        if (treeFound) {
            for (int parent = getParentId(currentId); parent != -1; parent = getParentId(parent)) {
                states[parent] = TOCItemState.EXPANDED;
            }
            rebuild();
            if (getCount() == 1 && states[0] == TOCItemState.COLLAPSED) {
                states[0] = TOCItemState.EXPANDED;
                rebuild();
            }
        }
    }

    /**
     * 设置当前文章所在的currentId
     *
     * @param mCurrentId
     */
    public void setCurrentId(int mCurrentId) {
        this.currentId = mCurrentId;
        notifyDataSetChanged();
    }

    public int getParentId(final int id) {
        final int level = (int) objects.get(id).navLevel;
        Chapter chapter = objects.get(id);
        int index = 0;
        for (; index < navMap.size(); ++index) {
            if (chapter.id != null && chapter.id.equals(navMap.get(index))) {
                break;
            }
        }
        for (int i = index - 1; i >= 0; i--) {
            if (navMap.get(i).navLevel < level) {
                return i;
            }
        }
        return -1;
    }

    protected void rebuild() {
        mapping.clear();
        int pos = 0;
        int level = Integer.MAX_VALUE;
        for (int cid = 0; cid < navMap.size(); cid++) {
            if (navMap.get(cid).navLevel <= level) {
                mapping.put(pos++, cid);
                if (states[cid] == TOCItemState.COLLAPSED) {
                    level = (int) navMap.get(cid).navLevel;
                } else {
                    level = Integer.MAX_VALUE;
                }
            }
        }
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public int getCount() {
        return mapping.size();
    }

    @Override
    public EpubParser.NavPoint getItem(final int position) {
        final int id = mapping.get(position, -1);
        if (id >= 0 && id < navMap.size()) {
            return navMap.get(id);
        }
        return null;
    }

    @Override
    public long getItemId(final int position) {
        return mapping.get(position, -1);
    }

    public int getItemPosition(final EpubParser.NavPoint item) {
        for (int i = 0, n = getCount(); i < n; i++) {
            if (item == getItem(i)) {
                return i;
            }
        }
        return -1;
    }

    public int getItemId(final Chapter item) {
        for (int i = 0, n = navMap.size(); i < n; i++) {
            if (item.id != null && item.id.equals(navMap.get(i).id)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        final int id = (int) getItemId(position);
        View container = null;
        ViewHolder holder = null;
        boolean firstTime = false;
        if (convertView == null) {
            container = LayoutInflater.from(context).inflate(R.layout.listview_toc_list_item_view, parent, false);
            holder = new ViewHolder();
            container.setTag(holder);
            holder.title = (TextView) container.findViewById(R.id.treeview_item_title);
            holder.pageNum = (TextView) container.findViewById(R.id.treeview_item_sub_title);
            holder.collapse = (ImageView) container.findViewById(R.id.treeview_collapse);
            holder.imageSelected = (ImageView) container.findViewById(R.id.imageSelected);
            if (null != typeFace) {
                holder.title.setTypeface(typeFace);
                holder.pageNum.setTypeface(typeFace);
            }
            firstTime = true;
        } else {
            container = convertView;
            holder = (ViewHolder) container.getTag();
        }

        final EpubParser.NavPoint item = getItem(position);
        if (!TextUtils.isEmpty(item.navLabel)) {
            holder.title.setText(item.navLabel.trim());
        } else {
            holder.title.setText(item.src.trim());
        }

        holder.title.setTag(position);
        holder.collapse.setTag(position);
        if (null != objects && ((Chapter) objects.get(item.chapterIndex)).chapterState == Chapter.ChapterState.READY) {
            holder.pageNum.setText(String.valueOf(item.pageIndex));
        } else {
            holder.pageNum.setText("");
        }
        if (firstTime) {
            holder.title.setOnClickListener(itemListener);
            container.setClickable(false);
            holder.title.setClickable(false);
        }
        if (this.settings.isThemeNight) {
            holder.title.setTextColor(Color.parseColor("#AAAAAA"));
            holder.pageNum.setTextColor(Color.parseColor("#AAAAAA"));
        } else {
            holder.title.setTextColor(Color.BLACK);
            holder.pageNum.setTextColor(Color.BLACK);
        }

        if (states[id] == TOCItemState.LEAF) {
            holder.collapse.setBackgroundColor(Color.TRANSPARENT);
        } else {
            holder.collapse.setOnClickListener(collapseListener);
            holder.collapse.setImageResource(states[id] == TOCItemState.EXPANDED ? R.mipmap.ic_arrows2
                    : R.mipmap.ic_arrows1);
        }
        //判断控件是否选中
        //holder.imageSelected.setVisibility(id == currentId ? View.VISIBLE : View.INVISIBLE);
        return container;
    }

    private static class ViewHolder {
        TextView title;
        TextView pageNum;
        ImageView collapse;
        ImageView imageSelected;
    }

    private enum TOCItemState {
        LEAF, EXPANDED, COLLAPSED;
    }

    private final class CollapseListener implements OnClickListener {

        @Override
        public void onClick(final View v) {
            {
                final int position = ((Integer) v.getTag()).intValue();
                final int id = (int) getItemId(position);
                final TOCItemState newState = states[id] == TOCItemState.EXPANDED ? TOCItemState.COLLAPSED
                        : TOCItemState.EXPANDED;
                states[id] = newState;
            }
            rebuild();

            v.post(new Runnable() {

                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }
    }

    private static final class ItemListener implements OnClickListener {

        @Override
        public void onClick(final View v) {
            for (ViewParent p = v.getParent(); p != null; p = p.getParent()) {
                if (p instanceof ListView) {
                    final ListView list = (ListView) p;
                    final OnItemClickListener l = list.getOnItemClickListener();
                    if (l != null) {
                        l.onItemClick(list, v, ((Integer) v.getTag()).intValue(), 0);
                    }
                    return;
                }
            }

        }
    }

}
