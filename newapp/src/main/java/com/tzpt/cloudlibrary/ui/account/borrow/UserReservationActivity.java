package com.tzpt.cloudlibrary.ui.account.borrow;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseListActivity;
import com.tzpt.cloudlibrary.business_bean.ReservationBookBean;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.ui.library.LibraryDetailActivity;
import com.tzpt.cloudlibrary.utils.ToastUtils;
import com.tzpt.cloudlibrary.widget.CustomDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.OnClick;

/**
 * 我的预约
 */
public class UserReservationActivity extends BaseListActivity<ReservationBookBean> implements
        UserReservationContract.View {

    private UserReservationPresenter mPresenter;
    private int mCurrentPage = 1;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, UserReservationActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_user_reservation;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("我的预约");
    }

    @Override
    public void initDatas() {
        mPresenter = new UserReservationPresenter();
        mPresenter.attachView(this);
        mPresenter.getUserReservationList(1);

        EventBus.getDefault().register(this);
    }

    @Override
    public void configViews() {
        mAdapter = new UserReservationAdapter(this, mCancelClickListener,mEnterBookHomeClickListener);
        initAdapter(false, true);
        mRecyclerView.setDividerDrawable(R.drawable.divider_rv_vertical_three);
    }

    View.OnClickListener mEnterBookHomeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //进入该馆
            int position = (int) v.getTag();
            String libCode = mAdapter.getItem(position).mLibrary.mCode;
            String libName = mAdapter.getItem(position).mLibrary.mName;
            LibraryDetailActivity.startActivity(UserReservationActivity.this, libCode, libName);
        }
    };

    View.OnClickListener mCancelClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            final CustomDialog dialog = new CustomDialog(UserReservationActivity.this, R.style.DialogTheme,
                    getString(R.string.cancel_reservation_tip));
            dialog.setCancelable(false);
            dialog.hasNoCancel(true);
            dialog.show();
            dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
                @Override
                public void onClickOk() {
                    dialog.dismiss();
                    int position = (int) v.getTag();
                    long bookId = mAdapter.getItem(position).mBook.mBookId;
                    String isbn = mAdapter.getItem(position).mBook.mIsbn;
                    String libCode = mAdapter.getItem(position).mLibrary.mCode;
                    mPresenter.cancelReservation(isbn,libCode);
                }

                @Override
                public void onClickCancel() {
                    dialog.dismiss();
                }
            });
        }
    };

    @OnClick({R.id.titlebar_left_btn})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
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
            mPresenter = null;
        }
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void setNetError(boolean refresh) {
        if (mAdapter.getCount() < 1) {
            mAdapter.clear();
            mRecyclerView.showError();
            mRecyclerView.setRetryRefreshListener(this);
        } else {
            mAdapter.pauseMore();
        }
    }

    @Override
    public void setUserReservationList(List<ReservationBookBean> borrowBookBeanList, int totalCount, boolean refresh) {
        if (refresh) {
            mCurrentPage = 1;
            mAdapter.clear();
        } else {
            mCurrentPage = mCurrentPage + 1;
        }
        mAdapter.addAll(borrowBookBeanList);
        mAdapter.notifyDataSetChanged();
        if (mAdapter.getCount() > 0) {
            mRecyclerView.showToastTv(getString(R.string.book_list_tips_for_borrow, mAdapter.getCount(), totalCount));
        }
        if (mAdapter.getCount() >= totalCount) {
            mAdapter.stopMore();
        }
    }

    @Override
    public void setUserReservationEmpty(boolean refresh) {
        if (refresh) {
            mAdapter.clear();
            mRecyclerView.showEmpty();
        } else {
            mAdapter.stopMore();
        }
    }

    @Override
    public void showToastError(String msg) {
        ToastUtils.showSingleToast(msg);
    }

    @Override
    public void showToastError(int resId) {
        ToastUtils.showSingleToast(resId);
    }

    @Override
    public void pleaseLoginTip() {
        AccountMessage accountMessage = new AccountMessage();
        accountMessage.mIsLoginOut = true;
        accountMessage.mIsToUserCenter = true;
        EventBus.getDefault().post(accountMessage);

    }

    @Override
    public void onRefresh() {
        mPresenter.getUserReservationList(1);
    }

    @Override
    public void onLoadMore() {
        mPresenter.getUserReservationList(mCurrentPage + 1);
    }

    @Override
    public void onItemClick(int position) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receviceLoginOut(AccountMessage loginMessage) {
        if (null != mPresenter && loginMessage.mIsLoginOut) {
            finish();
        }
    }
}
