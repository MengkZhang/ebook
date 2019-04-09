package com.tzpt.cloundlibrary.manager.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseListActivity;
import com.tzpt.cloundlibrary.manager.bean.MsgInfo;
import com.tzpt.cloundlibrary.manager.ui.adapter.MsgListAdapter;
import com.tzpt.cloundlibrary.manager.ui.contract.MessageContract;
import com.tzpt.cloundlibrary.manager.ui.presenter.MsgPresenter;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;

import java.util.List;

import butterknife.OnClick;

/**
 * 消息列表
 */
public class MessageActivity extends BaseListActivity<MsgInfo> implements
        MessageContract.View {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, MessageActivity.class);
        context.startActivity(intent);
    }

    private MsgPresenter mPresenter;
    private int mCurrentPage = 1;

    @OnClick({R.id.titlebar_left_btn})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                this.finish();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_message;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.mipmap.ic_arrow_left);
        mCommonTitleBar.setTitle("消息");
    }

    @Override
    public void initDatas() {
    }

    @Override
    public void configViews() {
        mAdapter = new MsgListAdapter(this);
        initAdapter(true, true);

        mPresenter = new MsgPresenter();
        mPresenter.attachView(this);
        mPresenter.getMsgFromRemote(1);
    }

    @Override
    public void showMsgDialog(int msgId) {
        mRecyclerView.setRefreshing(false);
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, getString(msgId));
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void setNoLoginPermission(int msgId) {
        mRecyclerView.setRefreshing(false);
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, getString(msgId));
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                finish();
                LoginActivity.startActivity(MessageActivity.this);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void showMsgDialog(int msgId, boolean refresh) {
        mRecyclerView.setRefreshing(false);
        if (refresh) {
            mRecyclerView.showError();
        } else {
            mAdapter.stopMore();
        }
        showMsgDialog(msgId);
    }

    @Override
    public void showMsgList(List<MsgInfo> list, int totalCount, boolean refresh) {
        if (refresh) {
            mAdapter.clear();
            mCurrentPage = 1;
        } else {
            mCurrentPage = mCurrentPage + 1;
        }
        mAdapter.addAll(list);
        if (mAdapter.getCount() >= totalCount) {
            mAdapter.stopMore();
        }
    }

    @Override
    public void showMsgListEmpty(boolean refresh) {
        if (refresh) {
            mAdapter.clear();
            mRecyclerView.showEmpty();
        } else {
            mAdapter.stopMore();
        }
    }

    @Override
    public void complete() {
        mRecyclerView.setRefreshing(false);
    }

    //手动设置已读状态
    @Override
    public void setReadMsgStatus(int position) {
        if (null != mAdapter && position < mAdapter.getCount()) {
            mAdapter.getItem(position).mIsRead = true;
            mAdapter.notifyItemChanged(position);
        }
    }

    @Override
    public void onRefresh() {
        mPresenter.getMsgFromRemote(1);
    }

    @Override
    public void onLoadMore() {
        mPresenter.getMsgFromRemote(mCurrentPage + 1);
    }


    @Override
    public void onItemClick(int position) {
        MsgInfo bean = mAdapter.getItem(position);
        if (null != bean) {
            MessageDetailActivity.startActivity(this, bean);
            mPresenter.setReadMsgStatus(bean.mId, position);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.clear();
        mPresenter.detachView();
    }

}
