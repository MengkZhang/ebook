package com.tzpt.cloundlibrary.manager.base;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.EasyRecyclerView;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.OnLoadMoreListener;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.RecyclerArrayAdapter;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.swipe.OnRefreshListener;

import java.lang.reflect.Constructor;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/7/3.
 */

public abstract class BaseListFragment<T> extends BaseFragment implements OnLoadMoreListener,
        OnRefreshListener, RecyclerArrayAdapter.OnItemClickListener {
    protected EasyRecyclerView mRecyclerView;
    protected RecyclerArrayAdapter<T> mAdapter;


    protected void initAdapter(boolean refreshable, boolean loadmoreable) {
        mRecyclerView = ButterKnife.findById(getActivity(), R.id.recycler_view);
        if (mAdapter != null) {
            mAdapter.setOnItemClickListener(this);
            if (loadmoreable) {
                mAdapter.setCustomMoreView(R.layout.common_footer_more_view,
                        R.layout.common_footer_nomore_view,
                        R.layout.common_footer_error_view,
                        this);
            }
            if (refreshable && mRecyclerView != null) {
                mRecyclerView.setRefreshListener(this);
            }
        }

        if (mRecyclerView != null) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//            mRecyclerView.setItemDecoration(ContextCompat.getColor(getActivity(), R.color.common_divider_narrow), 1, 0, 0);
            mRecyclerView.setAdapterWithProgress(mAdapter);
        }
    }

    protected void initAdapter(Class<? extends RecyclerArrayAdapter<T>> clazz, boolean refreshable, boolean loadmoreable) {
        mAdapter = (RecyclerArrayAdapter) createInstance(clazz);
        initAdapter(refreshable, loadmoreable);
    }

    public Object createInstance(Class<?> cls) {
        Object obj;
        try {
            Constructor c1 = cls.getDeclaredConstructor(Context.class);
            c1.setAccessible(true);
            obj = c1.newInstance(mContext);
        } catch (Exception e) {
            obj = null;
        }
        return obj;
    }
}
