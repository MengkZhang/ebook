package com.tzpt.cloudlibrary.base;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.widget.recyclerview.EasyRecyclerView;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.OnLoadMoreListener;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;
import com.tzpt.cloudlibrary.widget.recyclerview.swipe.OnRefreshListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 列表fragment基类
 * Created by ZhiqiangJia on 2017-08-17.
 */
public abstract class BaseListFragment<T> extends BaseFragment implements OnLoadMoreListener,
        OnRefreshListener, RecyclerArrayAdapter.OnItemClickListener {

    @BindView(R.id.recycler_view)
    protected EasyRecyclerView mRecyclerView;
    protected RecyclerArrayAdapter<T> mAdapter;


    protected void initAdapter(boolean refreshable, boolean loadmoreable) {
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
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getSupportActivity()));
            //mRecyclerView.setItemDecoration(ContextCompat.getColor(this, R.color.color_E3E3E3), 2, 0, 0);
            mRecyclerView.setAdapterWithProgress(mAdapter);
        }
    }
}
