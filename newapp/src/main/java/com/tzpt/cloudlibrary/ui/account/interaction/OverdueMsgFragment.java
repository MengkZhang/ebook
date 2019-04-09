package com.tzpt.cloudlibrary.ui.account.interaction;

import android.support.v4.content.ContextCompat;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.BaseListFragment;
import com.tzpt.cloudlibrary.bean.OverdueMsgBean;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.ui.account.borrow.BorrowBookDetailActivity;
import com.tzpt.cloudlibrary.utils.DpPxUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * 逾期消息列表
 * Created by Administrator on 2018/3/29.
 */

public class OverdueMsgFragment extends BaseListFragment<OverdueMsgBean>
        implements OverdueMsgContract.View {
    private boolean mIsPrepared;
    private boolean mIsFirstLoad = true;

    private int mCurrentPage = 1;
    private OverdueMsgPresenter mPresenter;

    @Override
    public void onRefresh() {
        mPresenter.getOverdueMsg(1);
    }

    @Override
    public void onLoadMore() {
        mPresenter.getOverdueMsg(mCurrentPage + 1);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_overdue_msg;
    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {
        mAdapter = new OverdueMsgAdapter(getContext());
        initAdapter(true, true);
        mRecyclerView.setDividerDrawable(R.drawable.divider_rv_vertical_default);

        mIsPrepared = true;
        lazyLoad();
    }

    @Override
    protected void lazyLoad() {
        if (!mIsVisible || !mIsPrepared) {
            return;
        }
        if (mIsFirstLoad) {//懒加载，只允许第一次加载
            this.mIsFirstLoad = false;
            mPresenter = new OverdueMsgPresenter();
            mPresenter.attachView(this);
            mPresenter.getOverdueMsg(1);
        }
    }

    @Override
    public void onItemClick(int position) {
        OverdueMsgBean bean = mAdapter.getItem(position);
        if (bean.mState == 1) {
            modifyMsgState(position);
            mPresenter.readOverdueMsg(bean.mId);
        }
        BorrowBookDetailActivity.startActivity(getContext(), bean.mBorrowId);
    }


    @Override
    public void showNetError(boolean refresh) {
        mRecyclerView.setRefreshing(false);
        if (refresh) {
            mAdapter.clear();
            mRecyclerView.showError();
        } else {
            mAdapter.pauseMore();
        }
    }

    @Override
    public void setMyMessageList(List<OverdueMsgBean> list, int totalCount, boolean refresh) {
        mRecyclerView.setRefreshing(false);
        if (refresh) {
            mAdapter.clear();
            mCurrentPage = 1;
        } else {
            mCurrentPage = mCurrentPage + 1;
        }
        mAdapter.addAll(list);
        mAdapter.notifyDataSetChanged();
        if (mAdapter.getCount() >= totalCount) {
            mAdapter.stopMore();
        }
    }

    @Override
    public void showMyMessageListEmpty(boolean refresh) {
        mRecyclerView.setRefreshing(false);
        if (refresh) {
            mAdapter.clear();
            mRecyclerView.showEmpty();
        } else {
            mAdapter.stopMore();
        }
    }

    @Override
    public void modifyMsgState(int position) {
        mAdapter.getItem(position).mState = 2;
        mAdapter.notifyItemChanged(position);
    }

    @Override
    public void pleaseLoginTip() {
        AccountMessage accountMessage = new AccountMessage();
        accountMessage.mIsLoginOut = true;
        accountMessage.mIsToUserCenter = true;
        EventBus.getDefault().post(accountMessage);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mAdapter) {
            mAdapter.clear();
        }
        if (mPresenter != null) {
            mPresenter.detachView();
            mPresenter = null;
        }
    }
}
