package com.tzpt.cloudlibrary.widget.recyclerview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.tzpt.cloudlibrary.widget.recyclerview.EasyRecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Administrator on 2018/10/31.
 */

public abstract class GroupRecyclerArrayAdapter<G> extends RecyclerView.Adapter<BaseViewHolder> {

    static final int INVALID_POSITION = -1;

    private static final int TYPE_GROUP = 1;
    private static final int TYPE_CHILD = 2;

    private List<G> mGroups;
    private int mItemCount;

    protected EventDelegate mEventDelegate;
    protected ArrayList<ItemView> headers = new ArrayList<>();
    protected ArrayList<ItemView> footers = new ArrayList<>();

    private RecyclerView.AdapterDataObserver mObserver;

    private boolean mNotifyOnChange = true;

    private final Object mLock = new Object();

    private Context mContext;

    protected OnItemClickListener mItemClickListener;

    /**
     * Constructor
     *
     * @param context The current context.
     */
    public GroupRecyclerArrayAdapter(Context context) {
        init(context, new ArrayList<G>());
    }

    /**
     * Constructor
     *
     * @param context The current context.
     * @param objects The objects to represent in the ListView.
     */
    public GroupRecyclerArrayAdapter(Context context, List<G> objects) {
        init(context, objects);
    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        if (observer instanceof EasyRecyclerView.EasyDataObserver) {
            mObserver = observer;
        } else {
            super.registerAdapterDataObserver(observer);
        }
    }

    private void init(Context context, List<G> objects) {
        mContext = context;
        mGroups = objects;
    }

    public void stopMore() {
        if (mEventDelegate == null)
            throw new NullPointerException("You should invoking setLoadMore() first");
        mEventDelegate.stopLoadMore();
    }

    public void pauseMore() {
        if (mEventDelegate == null)
            throw new NullPointerException("You should invoking setLoadMore() first");
        mEventDelegate.pauseLoadMore();
    }

    public void resumeMore() {
        if (mEventDelegate == null)
            throw new NullPointerException("You should invoking setLoadMore() first");
        mEventDelegate.resumeLoadMore();
    }

    private EventDelegate getEventDelegate() {
        if (mEventDelegate == null) {
            mEventDelegate = new DefaultEventDelegate(mContext);
            addFooter(mEventDelegate.getEventFooter());
        }

        return mEventDelegate;
    }


    public void addFooter(ItemView view) {
        if (view == null)
            throw new NullPointerException("ItemView can't be null");
        footers.add(view);
        notifyItemInserted(headers.size() + getCount() + footers.size() - 1);
    }


    public View setMore(final int res, final OnLoadMoreListener listener) {
        FrameLayout container = new FrameLayout(mContext);
        container.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LayoutInflater.from(mContext).inflate(res, container);
        getEventDelegate().setMore(container, listener);
        return container;
    }

    public View setMore(final View view, OnLoadMoreListener listener) {
        getEventDelegate().setMore(view, listener);
        return view;
    }

    public View setNoMore(final int res) {
        FrameLayout container = new FrameLayout(mContext);
        container.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LayoutInflater.from(mContext).inflate(res, container);
        getEventDelegate().setNoMore(container);
        return container;
    }

    public View setNoMore(final View view) {
        getEventDelegate().setNoMore(view);
        return view;
    }

    public View setError(final int res) {
        FrameLayout container = new FrameLayout(mContext);
        container.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LayoutInflater.from(mContext).inflate(res, container);
        getEventDelegate().setErrorMore(container);
        return container;
    }

    public View setError(final View view) {
        getEventDelegate().setErrorMore(view);
        return view;
    }

    /**
     * 这个函数包含了头部和尾部view的个数，不是真正的item个数。
     */
    @Deprecated
    @Override
    public final int getItemCount() {
        return getCount() + headers.size() + footers.size();
    }

    /**
     * 应该使用这个获取item个数
     */
    public int getGroupCount() {
        return mGroups.size();
    }

    public int getChildTotalCount() {
        int childCount = 0;
        for (G g : mGroups) {
            childCount += getChildCount(g);
        }
        return childCount;
    }

    public G getGroup(int groupPosition) {
        return mGroups.get(groupPosition);
    }

    public int getCount() {
        return mItemCount;
    }

    protected abstract int getChildCount(G group);

    @Override
    public int getItemViewType(int position) {
        if (headers.size() != 0) {
            if (position < headers.size())
                return headers.get(position).hashCode();
        }
        if (footers.size() != 0) {
            int i = position - headers.size() - getCount();
            if (i >= 0) {
                return footers.get(i).hashCode();
            }
        }
        return getItemType(position - headers.size());
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = createSpViewByType(parent, viewType);
        if (view != null) {
            return new StateViewHolder(view);
        }


        if (viewType == TYPE_GROUP) {
            return onCreateGroupViewHolder(parent);
        } else {
            final BaseViewHolder viewHolder = onCreateChildViewHolder(parent);
            if (mItemClickListener != null) {
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final int itemPosition = viewHolder.getAdapterPosition();
                        mItemClickListener.onChildItemClick(v, itemPosition);
                    }
                });
            }
            return viewHolder;
        }

    }

    private View createSpViewByType(ViewGroup parent, int viewType) {
        for (ItemView headerView : headers) {
            if (headerView.hashCode() == viewType) {
                View view = headerView.onCreateView(parent);
                StaggeredGridLayoutManager.LayoutParams layoutParams;
                if (view.getLayoutParams() != null)
                    layoutParams = new StaggeredGridLayoutManager.LayoutParams(view.getLayoutParams());
                else
                    layoutParams = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setFullSpan(true);
                view.setLayoutParams(layoutParams);
                return view;
            }
        }
        for (ItemView footerview : footers) {
            if (footerview.hashCode() == viewType) {
                View view = footerview.onCreateView(parent);
                StaggeredGridLayoutManager.LayoutParams layoutParams;
                if (view.getLayoutParams() != null)
                    layoutParams = new StaggeredGridLayoutManager.LayoutParams(view.getLayoutParams());
                else
                    layoutParams = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setFullSpan(true);
                view.setLayoutParams(layoutParams);
                return view;
            }
        }
        return null;
    }

    private class StateViewHolder extends BaseViewHolder {

        public StateViewHolder(View itemView) {
            super(itemView);
        }
    }

    protected abstract BaseViewHolder onCreateGroupViewHolder(ViewGroup parent);

    protected abstract BaseViewHolder onCreateChildViewHolder(ViewGroup parent);

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int itemPosition) {
        holder.itemView.setId(itemPosition);
        if (headers.size() != 0 && itemPosition < headers.size()) {
            headers.get(itemPosition).onBindView(holder.itemView);
            return;
        }

        int i = itemPosition - headers.size() - getCount();
        if (footers.size() != 0 && i >= 0) {
            footers.get(i).onBindView(holder.itemView);
            return;
        }
//
//        OnBindViewHolder(holder, position - headers.size());

        Position position = getGroupChildPosition(itemPosition);
        if (position.child == -1) {
            onBindGroupViewHolder(holder, itemPosition, position.group);
        } else {
            onBindChildViewHolder(holder, itemPosition, position.group, position.child);
        }
    }

    public G getItem(int position) {
        return mGroups.get(position);
    }

    public Position getGroupChildPosition(int itemPosition) {
        int itemCount = 0;
        int childCount;
        final Position position = new Position();
        for (G g : mGroups) {
            if (itemPosition == itemCount) {
                position.child = INVALID_POSITION;
                return position;
            }
            itemCount++;
            position.child = itemPosition - itemCount;
            childCount = getChildCount(g);
            if (position.child < childCount) {
                return position;
            }
            itemCount += childCount;
            position.group++;
        }
        return position;
    }


    protected abstract void onBindGroupViewHolder(BaseViewHolder holder, int itemPosition, int groupPosition);

    protected abstract void onBindChildViewHolder(BaseViewHolder holder, int itemPosition, int groupPosition, int childPosition);

    private int getItemType(final int itemPosition) {
        int count = 0;
        for (G g : mGroups) {
            if (itemPosition == count) {
                return TYPE_GROUP;
            }
            count += 1;
            if (itemPosition == count) {
                return TYPE_CHILD;
            }
            count += getChildCount(g);
            if (itemPosition < count) {
                return TYPE_CHILD;
            }
        }
        return -1;
    }

    public static class Position {
        public int group;
        public int child = INVALID_POSITION;
    }

    private void updateItemCount() {
        int count = 0;
        for (G group : mGroups) {
            count += getChildCount(group) + 1;
        }
        mItemCount = count;
    }

    /**
     * Adds the specified Collection at the end of the array.
     *
     * @param collection The Collection to add at the end of the array.
     */
    public void addAll(Collection<? extends G> collection) {
        if (mEventDelegate != null)
            mEventDelegate.addData(collection == null ? 0 : collection.size());
        if (collection != null && collection.size() != 0) {
            synchronized (mLock) {
                mGroups.addAll(collection);
                updateItemCount();
            }
        }
        int count = 0;
        if (collection != null) {
            for (G group : collection) {
                count += getChildCount(group) + 1;
            }
        }
        if (mObserver != null)
            mObserver.onItemRangeInserted(getCount() - count + 1, count);
        if (mNotifyOnChange)
            notifyItemRangeInserted(headers.size() + getCount() - count + 1, count);
    }

//    public void add(G object) {
//        if (mEventDelegate != null)
//            mEventDelegate.addData(object == null ? 0 : 1);
//        if (object != null) {
//            synchronized (mLock) {
//                mGroups.add(object);
//                updateItemCount();
//            }
//        }
//        if (mObserver != null)
//            mObserver.onItemRangeInserted(getCount() + 1, 1);
//        if (mNotifyOnChange)
//            notifyItemRangeInserted(headers.size() + getCount() + 1, getChildCount(object));
//    }

    public void add(int itemPosition, G object) {
        int groupIndex = 0;
        int index = 0;
        int count;
        synchronized (mLock) {
            Position position = getGroupChildPosition(itemPosition);
            for (int i = 0; i < position.group - 1; i++) {
                groupIndex++;
                G group = mGroups.get(i);
                index += getChildCount(group) + 1;
            }
            count = getChildCount(object);
            mGroups.add(groupIndex + 1, object);
            updateItemCount();
        }
        if (mObserver != null)
            mObserver.onItemRangeInserted(index, count);
        if (mNotifyOnChange)
            notifyItemRangeInserted(headers.size() + index, count);
    }

    public void addChild(int groupPosition) {
        int position = 0;
        for (int i = 0; i < groupPosition; i++) {
            G group = mGroups.get(i);
            position += getChildCount(group) + 1;
        }

        synchronized (mLock) {
            updateItemCount();
        }
        if (mObserver != null)
            mObserver.onItemRangeInserted(position + 1, 1);
        if (mNotifyOnChange)
            notifyItemRangeInserted(headers.size() + position + 1, 1);
    }

    protected abstract void removeChild(G group, int childPosition);

    public void removeGroup(int itemPosition) {
        int index = 0;
        int count;
        synchronized (mLock) {
            Position position = getGroupChildPosition(itemPosition);
            for (int i = 0; i < position.group; i++) {
                G group = mGroups.get(i);
                index += getChildCount(group) + 1;
            }
            count = getChildCount(getGroup(position.group));
            mGroups.remove(getGroup(position.group));
            updateItemCount();
        }
        if (mObserver != null) {
            mObserver.onItemRangeRemoved(index, count);
        }
        if (mNotifyOnChange) {
            notifyItemRangeRemoved(headers.size() + index, count);
        }
    }

    public void remove(int itemPosition) {
        boolean removeGroup = false;
        synchronized (mLock) {
            Position position = getGroupChildPosition(itemPosition);
            removeChild(getGroup(position.group), position.child);
            if (getChildCount(getGroup(position.group)) == 0) {
                mGroups.remove(getGroup(position.group));
                removeGroup = true;
            }
            updateItemCount();
        }
        if (mObserver != null) {
            if (removeGroup) {
                mObserver.onItemRangeRemoved(itemPosition, 1);
                mObserver.onItemRangeRemoved(itemPosition - 1, 1);
            } else {
                mObserver.onItemRangeRemoved(itemPosition, 1);
            }
        }

        if (mNotifyOnChange) {
            if (removeGroup) {
                notifyItemRemoved(headers.size() + itemPosition - 1);
                notifyItemRemoved(headers.size() + itemPosition);
            } else {
                notifyItemRemoved(headers.size() + itemPosition);
            }
        }

    }

    /**
     * 触发清空
     */
    public void clear() {
        int count = mItemCount;
        if (mEventDelegate != null) mEventDelegate.clear();
        synchronized (mLock) {
            mGroups.clear();
        }
        if (mObserver != null)
            mObserver.onItemRangeRemoved(0, count);
        if (mNotifyOnChange)
            notifyItemRangeRemoved(headers.size(), count);
    }

    public interface OnItemClickListener {
        void onChildItemClick(View view, int itemPosition);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

}
