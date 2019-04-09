package com.tzpt.cloudlibrary.base;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.widget.recyclerview.EasyRecyclerView;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.OnLoadMoreListener;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;
import com.tzpt.cloudlibrary.widget.recyclerview.swipe.OnRefreshListener;

import butterknife.ButterKnife;

/**
 * 列表基类
 * Created by ZhiqiangJia on 2017-08-02.
 */
public abstract class BaseListActivity<T> extends BaseActivity implements OnLoadMoreListener,
        OnRefreshListener, RecyclerArrayAdapter.OnItemClickListener {

    protected EasyRecyclerView mRecyclerView;
    protected RecyclerArrayAdapter<T> mAdapter;

    protected void initAdapter(boolean refreshable, boolean loadmoreable) {
        mRecyclerView = ButterKnife.findById(this, R.id.recycler_view);
        if (mAdapter != null) {
            mAdapter.setOnItemClickListener(this);
            mAdapter.setError(R.layout.common_rv_error_view).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAdapter.resumeMore();
                }
            });
            if (loadmoreable) {
                mAdapter.setMore(R.layout.common_rv_more_view, this);
                mAdapter.setNoMore(R.layout.common_rv_nomore_view);
            }
            if (refreshable && mRecyclerView != null) {
                mRecyclerView.setRefreshListener(this);
            }
        }

        if (mRecyclerView != null) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerView.setAdapterWithProgress(mAdapter);
        }
    }
}
