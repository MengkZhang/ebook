package com.tzpt.cloudlibrary.ui.readers;

import android.app.Activity;
import android.content.Intent;

import com.tzpt.cloudlibrary.AppIntentGlobalName;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseListFragment;
import com.tzpt.cloudlibrary.bean.ActionInfoBean;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.widget.recyclerview.EasyRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 活动列表
 * Created by tonyjia on 2018/3/21.
 */
public class ActivityListFragment extends BaseListFragment<ActionInfoBean> implements
        ActivityListContract.View {

    private static final int REQUEST_CODE = 1001;

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private ActivityListContract.Presenter mPresenter;

    private int mCurrentPage = 1;
    private int mTotalCount;
    private boolean mIsPrepared;
    private boolean mIsFirstLoad = true;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_action_list;
    }

    @Override
    public void initDatas() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void configViews() {
        mAdapter = new ActionListAdapter(getSupportActivity());
        initAdapter(true, true);

        mIsPrepared = true;
        lazyLoad();
    }

    @Override
    protected void lazyLoad() {
        if (!mIsVisible || !mIsPrepared) {
            return;
        }
        if (null != mPresenter && mIsFirstLoad) {//懒加载，只允许第一次加载
            this.mIsFirstLoad = false;
            mPresenter.getActivityList(1);
        }
    }

    @Override
    public void onItemClick(int position) {
        mPresenter.saveLocalActionList(mAdapter.getAllData());
        ActionDetailsActivity.startActivity(this,
                mPresenter.getFromType(),
                position,
                mCurrentPage,
                mTotalCount,
                mPresenter.getKeyWord(),
                mPresenter.getLibCode(),
                mPresenter.getFromSearchValue(),
                REQUEST_CODE);
    }

    @Override
    public void onRefresh() {
        mPresenter.getActivityList(1);
    }

    @Override
    public void onLoadMore() {
        mPresenter.getActivityList(mCurrentPage + 1);
    }

    @Override
    public void setPresenter(ActivityListContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void setOurReadersList(List<ActionInfoBean> beanList, int totalCount, boolean refresh, boolean isFromSearch) {
        mTotalCount = totalCount;
        if (refresh) {
            mCurrentPage = 1;
            mAdapter.clear();
        } else {
            mCurrentPage = mCurrentPage + 1;
        }
        mAdapter.addAll(beanList);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.setRefreshing(false);
        if (mAdapter.getCount() > 0) {
            if (isFromSearch) {
                mRecyclerView.showToastTv(getString(R.string.library_list_tips, mAdapter.getCount(), totalCount));
            } else {
                mRecyclerView.showToastTv(String.valueOf(mAdapter.getCount()));
            }
        }
        if (mAdapter.getCount() >= totalCount) {
            mAdapter.stopMore();
        }
    }

    private void setLocalActionList(List<ActionInfoBean> beanList, boolean isFromSearch) {
        mAdapter.addAll(beanList);
        mRecyclerView.setRefreshing(false);
        if (mAdapter.getCount() > 0) {
            if (isFromSearch) {
                mRecyclerView.showToastTv(getString(R.string.library_list_tips, mAdapter.getCount(), mTotalCount));
            } else {
                mRecyclerView.showToastTv(String.valueOf(mAdapter.getCount()));
            }
        }
        if (mAdapter.getCount() >= mTotalCount) {
            mAdapter.stopMore();
        }
    }

    @Override
    public void setOurReadersEmpty(boolean refresh) {
        mRecyclerView.setRefreshing(false);
        if (refresh) {
            mAdapter.clear();
            mRecyclerView.showEmpty();
        } else {
            mAdapter.stopMore();
        }
    }

    @Override
    public void setNetError(boolean refresh) {
        mRecyclerView.setRefreshing(false);
        if (refresh) {
            mAdapter.clear();
            mRecyclerView.showError();
        } else {
            mAdapter.pauseMore();
        }
    }

    @Override
    public void pleaseLogin() {
        AccountMessage accountMessage = new AccountMessage();
        accountMessage.mIsLoginOut = true;
        accountMessage.mIsToUserCenter = true;
        EventBus.getDefault().post(accountMessage);
    }

    @Override
    public void showRefreshLoading() {
        mAdapter.clear();
        mRecyclerView.showProgress();
    }

    @Override
    public void onResume() {
        super.onResume();
        UmengHelper.setPageStart("ActivityListFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        UmengHelper.setPageEnd("ActivityListFragment");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.clear();
        EventBus.getDefault().unregister(this);
        if (null != mPresenter) {
            mPresenter.clearLocalActionList();
            mPresenter.detachView();
            mPresenter = null;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveLoginOut(AccountMessage loginMessage) {
        if (null != mPresenter && loginMessage.mIsLoginOut) {
            getSupportActivity().finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            mCurrentPage = data.getIntExtra(AppIntentGlobalName.ACTION_LIST_PAGE_NUM, 1);

            ArrayList<ActionInfoBean> actionInfoBeanList = data.getParcelableArrayListExtra(AppIntentGlobalName.ACTION_LIST);
            if (actionInfoBeanList != null) {
                setLocalActionList(actionInfoBeanList, mPresenter.getFromSearchValue() == 1);
            }
        }
    }
}
