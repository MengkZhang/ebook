package com.tzpt.cloudlibrary.ui.account.borrow;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.business_bean.ReadNoteGroupBean;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.utils.ToastUtils;
import com.tzpt.cloudlibrary.widget.CustomDialog;
import com.tzpt.cloudlibrary.widget.recyclerview.EasyRecyclerView;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.GroupRecyclerArrayAdapter;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.OnLoadMoreListener;
import com.tzpt.cloudlibrary.widget.recyclerview.swipe.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 读书笔记列表
 */
public class ReaderNotesActivity extends BaseActivity implements
        ReaderNotesContract.View, OnLoadMoreListener,
        OnRefreshListener, GroupRecyclerArrayAdapter.OnItemClickListener {
    private static final int REQUEST_CODE_BOOK_DETAIL = 1000;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, ReaderNotesActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private ReadNoteGroupAdapter mAdapter;

    private ReaderNotesPresenter mPresenter;
    private int mCurrentPage = 1;
    private int mPositionTag = 0;

    @OnClick(R.id.titlebar_left_btn)
    public void onViewClicked() {
        finish();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_reader_notes;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("读书笔记");
    }

    @Override
    public void initDatas() {
        mPresenter = new ReaderNotesPresenter();
        mPresenter.attachView(this);
        mPresenter.getReaderNotesList(1);
        EventBus.getDefault().register(this);
    }

    @Override
    public void configViews() {
        mAdapter = new ReadNoteGroupAdapter(this);
        initAdapter();

        mAdapter.setDelChildItemListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPositionTag = (int) v.getTag();
                delNoteTip();
            }
        });
    }

    /**
     * 删除笔记提示
     */
    private void delNoteTip() {
        final CustomDialog dialog = new CustomDialog(ReaderNotesActivity.this, R.style.DialogTheme, "确认删除该条笔记？");
        dialog.setCancelable(false);
        dialog.hasNoCancel(true);
        dialog.setOkText("删除");
        dialog.setCancelText("取消");
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                GroupRecyclerArrayAdapter.Position position = mAdapter.getGroupChildPosition(mPositionTag);
                ReadNoteGroupBean item = mAdapter.getGroup(position.group);
                if (item != null) {
                    mPresenter.delNote(item.mNoteList.get(position.child).mNote.mId);
                }
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    private void initAdapter() {
        if (mAdapter != null) {
            mAdapter.setOnItemClickListener(this);
            mAdapter.setError(R.layout.common_rv_error_view).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAdapter.resumeMore();
                }
            });
            mAdapter.setMore(R.layout.common_rv_more_view, this);
            mAdapter.setNoMore(R.layout.common_rv_nomore_view);
        }

        if (mRecyclerView != null) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerView.setAdapterWithProgress(mAdapter);
        }
    }

    @Override
    public void onRefresh() {
        mPresenter.getReaderNotesList(1);
    }

    @Override
    public void onLoadMore() {
        mPresenter.getReaderNotesList(mCurrentPage + 1);
    }

    @Override
    public void onChildItemClick(View view, int itemPosition) {
        GroupRecyclerArrayAdapter.Position position = mAdapter.getGroupChildPosition(itemPosition);
        ReadNoteGroupBean bean = mAdapter.getGroup(position.group);
        if (null != bean && bean.mNoteList != null) {
            if (bean.mNoteList.get(position.child).mBorrowBookId != 0) {
                DataRepository.getInstance().saveNoteGroupBean(bean);
                BorrowBookDetailActivity.startActivityForResult(this, bean.mNoteList.get(position.child).mBorrowBookId, REQUEST_CODE_BOOK_DETAIL);
            } else {
                BorrowBookDetailActivity.startActivityForBoughtResult(this, bean.mNoteList.get(position.child).mBuyBookId, REQUEST_CODE_BOOK_DETAIL);
            }
            this.mPositionTag = itemPosition;
        }
    }

    @Override
    public void setNetError() {
        mRecyclerView.setRefreshing(false);
        if (mAdapter.getCount() < 1) {
            mAdapter.clear();
            mRecyclerView.showError();
            mRecyclerView.setRetryRefreshListener(this);
        } else {
            mAdapter.pauseMore();
        }
    }

    @Override
    public void setReaderNotesList(List<ReadNoteGroupBean> readerNotesList, int totalBookCount, int totalNoteCount, boolean refresh) {
        if (refresh) {
            mCurrentPage = 1;
            mAdapter.clear();
        } else {
            mCurrentPage = mCurrentPage + 1;
        }
        mAdapter.addAll(readerNotesList);
        mRecyclerView.setRefreshing(false);
        if (mAdapter.getChildTotalCount() > 0) {
            mRecyclerView.showToastTv(getString(R.string.library_list_tips, mAdapter.getChildTotalCount(), totalNoteCount));
        }
        if (mAdapter.getGroupCount() >= totalBookCount) {
            mAdapter.stopMore();
        }
    }

    @Override
    public void setReaderNotesEmpty(boolean refresh) {
        mRecyclerView.setRefreshing(false);
        if (refresh) {
            mAdapter.clear();
            mRecyclerView.showEmpty();
        } else {
            mAdapter.stopMore();
        }
    }

    @Override
    public void pleaseLoginTip() {
        AccountMessage accountMessage = new AccountMessage();
        accountMessage.mIsLoginOut = true;
        accountMessage.mIsToUserCenter = true;
        EventBus.getDefault().post(accountMessage);
    }

    @Override
    public void showToastMsg(String msg) {
        ToastUtils.showSingleToast(msg);
    }

    @Override
    public void showToastMsg(int msgId) {
        ToastUtils.showSingleToast(msgId);
    }

    @Override
    public void delNoteSuccess() {
        mAdapter.remove(mPositionTag);
        mAdapter.notifyDataSetChanged();
        if (mAdapter.getCount() == 0) {
            mRecyclerView.showEmpty();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_BOOK_DETAIL) {
            if (resultCode == RESULT_OK) {
                ReadNoteGroupBean noteGroupBean = DataRepository.getInstance().getReadNoteGroupBean();
                mAdapter.removeGroup(mPositionTag);
                mAdapter.notifyDataSetChanged();
                if (noteGroupBean.mNoteList.size() > 0) {
                    mAdapter.add(mPositionTag, noteGroupBean);
                    mAdapter.notifyDataSetChanged();
                }
                if (mAdapter.getCount() == 0) {
                    mRecyclerView.showEmpty();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        UmengHelper.setUmengResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        UmengHelper.setUmengPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mPresenter) {
            mPresenter.detachView();
        }
        EventBus.getDefault().unregister(this);
        DataRepository.getInstance().delReadNoteGroupBean();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receviceLoginOut(AccountMessage loginMessage) {
        if (null != mPresenter && loginMessage.mIsLoginOut) {
            finish();
        }
    }
}
