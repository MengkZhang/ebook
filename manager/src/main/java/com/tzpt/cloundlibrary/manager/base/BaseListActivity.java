package com.tzpt.cloundlibrary.manager.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.EasyRecyclerView;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.OnLoadMoreListener;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.RecyclerArrayAdapter;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.swipe.OnRefreshListener;

import java.lang.reflect.Constructor;

import butterknife.ButterKnife;

/**
 * Created by   on 2017/2/10.
 */
public abstract class BaseListActivity<T> extends BaseActivity implements OnLoadMoreListener,
        OnRefreshListener, RecyclerArrayAdapter.OnItemClickListener {
    protected EasyRecyclerView mRecyclerView;
    protected RecyclerArrayAdapter<T> mAdapter;

    protected int start = 0;
    protected int limit = 20;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initAdapter(boolean refreshable, boolean loadmoreable) {
        mRecyclerView = ButterKnife.findById(this, R.id.recycler_view);
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
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerView.setItemDecoration(ContextCompat.getColor(this, R.color.common_divider_narrow), 2, 0, 0);
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

    @Override
    public void onLoadMore() {
        //if (!NetworkUtils.isConnected(getApplicationContext())) {
        mAdapter.pauseMore();
        //return;
        //}
    }

    @Override
    public void onRefresh() {
        start = 0;
        //if (!NetworkUtils.isConnected(getApplicationContext())) {
        mAdapter.pauseMore();
        return;
        //}
    }

    protected void loaddingError() {
        mAdapter.clear();
        mAdapter.pauseMore();
        mRecyclerView.setRefreshing(false);
    }
}
