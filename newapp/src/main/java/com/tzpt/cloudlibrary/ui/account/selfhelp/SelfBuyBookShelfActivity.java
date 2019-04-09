package com.tzpt.cloudlibrary.ui.account.selfhelp;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseListActivity;
import com.tzpt.cloudlibrary.business_bean.BoughtBookBean;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.ui.account.borrow.BorrowBookDetailActivity;
import com.tzpt.cloudlibrary.ui.account.borrow.ReadingNoteEditActivity;
import com.tzpt.cloudlibrary.utils.MoneyUtils;
import com.tzpt.cloudlibrary.utils.ToastUtils;
import com.tzpt.cloudlibrary.widget.recyclerview.swipe.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 购书架
 */
public class SelfBuyBookShelfActivity extends BaseListActivity<BoughtBookBean> implements
        SelfBuyBookShelfContract.View {

    private static final int PRAISE_REQUEST_CODE = 1001;
    private static final int NOTE_REQUEST_CODE = 1002;
    private static final String RESULT_DATA_PRAISE = "borrow_book_praise";

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SelfBuyBookShelfActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.self_buy_book_bottom_ll)
    LinearLayout mSelfBuyBookBottomLl;
    @BindView(R.id.self_buy_book_sum_tv)
    TextView mSelfBuyBookSumTv;
    @BindView(R.id.self_buy_price_tv)
    TextView mSelfBuyPriceTv;
    @BindView(R.id.self_fixed_price_tv)
    TextView mSelfFixedPriceTv;
    @BindView(R.id.self_buy_book_bottom_line)
    View mBottomLine;

    private SelfBuyBookShelfPresenter mPresenter;
    private int mCurrentPage = 1;
    private int mPositionTag = -1;

    @OnClick({R.id.titlebar_left_btn})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
        }
    }

    private View.OnClickListener mItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mPositionTag = (int) v.getTag();
            BoughtBookBean bean = mAdapter.getItem(mPositionTag);
            BorrowBookDetailActivity.startActivityForBoughtResult(SelfBuyBookShelfActivity.this, bean.mBoughtId, PRAISE_REQUEST_CODE);
        }
    };

    private View.OnClickListener mThumpListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            BoughtBookBean bean = mAdapter.getItem(position);
            mPresenter.praiseSelfBuyBook(bean.mBoughtId, bean.mIsPraised, position);

        }
    };
    private View.OnClickListener mNoteListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mPositionTag = (int) v.getTag();
            BoughtBookBean bean = mAdapter.getItem(mPositionTag);
            ReadingNoteEditActivity.startActivityForBoughtResult(SelfBuyBookShelfActivity.this, bean.mBoughtId, NOTE_REQUEST_CODE);
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_self_buy_book_shelf;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("购书架");
    }

    @Override
    public void initDatas() {
        EventBus.getDefault().register(this);
        mPresenter = new SelfBuyBookShelfPresenter();
        mPresenter.attachView(this);
    }

    @Override
    public void configViews() {
        mAdapter = new SelfBuyBookShelfAdapter(this, mItemClickListener, mThumpListener, mNoteListener);
        initAdapter(false, true);
        mRecyclerView.setDividerDrawable(R.drawable.divider_rv_vertical_three);
        mPresenter.getSelfBuyBookShelfList(1);
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {
        mPresenter.getSelfBuyBookShelfList(mCurrentPage + 1);
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void setNetError(boolean refresh) {
        if (refresh) {
            mAdapter.clear();
            mRecyclerView.showError();
            mRecyclerView.setRetryRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mPresenter.getSelfBuyBookShelfList(1);
                }
            });
        } else {
            mAdapter.pauseMore();
        }
    }

    @Override
    public void setShelfBookList(List<BoughtBookBean> bookList, boolean refresh) {
        if (refresh) {
            mAdapter.clear();
            mCurrentPage = 1;
        } else {
            mCurrentPage = mCurrentPage + 1;
        }
        mAdapter.addAll(bookList);
    }

    @Override
    public void setShelfBookEmpty(boolean refresh) {
        if (refresh) {
            mAdapter.clear();
            mRecyclerView.showEmpty();
        } else {
            mAdapter.stopMore();
        }
    }

    /**
     * 设置合计信息
     *
     * @param totalCount      图书数量
     * @param totalBuyPrice   购买价格
     * @param totalFixedPrice 码洋
     */
    @Override
    public void setShelfBookTotalInfo(int totalCount, double totalBuyPrice, double totalFixedPrice) {
        mSelfBuyBookBottomLl.setVisibility(View.VISIBLE);
        mBottomLine.setVisibility(View.VISIBLE);
        mSelfBuyBookSumTv.setText(getString(R.string.sum_text, String.valueOf(totalCount)));
        mSelfBuyPriceTv.setText(getString(R.string.money_text, MoneyUtils.formatMoney(totalBuyPrice)));
        mSelfFixedPriceTv.setText(getString(R.string.fixed_price2, MoneyUtils.formatMoney(totalFixedPrice)));
    }

    @Override
    public void hideBookTotalInfo() {
        mSelfBuyBookBottomLl.setVisibility(View.GONE);
        mBottomLine.setVisibility(View.GONE);
    }

    @Override
    public void showMsgToast(int resId) {
        ToastUtils.showSingleToast(resId);
    }

    @Override
    public void praiseBuyBookSuccess(boolean isPraised, int position, int resId) {
        mAdapter.getItem(position).mIsPraised = isPraised;
        mAdapter.notifyItemChanged(position);
        if (isPraised) {
            ToastUtils.showSingleToast(resId);
        }
    }

    /**
     * 被踢下线
     */
    @Override
    public void pleaseLoginTip() {
        AccountMessage accountMessage = new AccountMessage();
        accountMessage.mIsLoginOut = true;
        accountMessage.mIsToUserCenter = true;
        EventBus.getDefault().post(accountMessage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PRAISE_REQUEST_CODE://点赞
                    if (data != null && mPositionTag != -1 && mPositionTag < mAdapter.getCount()) {
                        mAdapter.getItem(mPositionTag).mIsPraised = data.getBooleanExtra(RESULT_DATA_PRAISE, false);
                        mAdapter.notifyItemChanged(mPositionTag);
                    }
                    break;
                case NOTE_REQUEST_CODE://笔记
                    if (data != null) {
                        long noteId = data.getLongExtra("note_id", -1);
                        if (mPositionTag != -1 && noteId != -1 && mPositionTag < mAdapter.getCount()) {
                            BoughtBookBean bean = mAdapter.getItem(mPositionTag);
                            BorrowBookDetailActivity.startActivityForBoughtResult(SelfBuyBookShelfActivity.this, bean.mBoughtId, PRAISE_REQUEST_CODE);
                        }
                    }
                    break;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveLoginOut(AccountMessage loginMessage) {
        if (null != mPresenter && loginMessage.mIsLoginOut) {
            finish();
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
        EventBus.getDefault().unregister(this);
        mAdapter.clear();
        mPresenter.detachView();
    }

}
