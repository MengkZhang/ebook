package com.tzpt.cloudlibrary.ui.account.borrow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseListActivity;
import com.tzpt.cloudlibrary.business_bean.BorrowBookBean;
import com.tzpt.cloudlibrary.business_bean.BorrowCategoryBean;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.utils.DpPxUtils;
import com.tzpt.cloudlibrary.utils.ToastUtils;
import com.tzpt.cloudlibrary.widget.CustomPopupWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;

/**
 * 借阅-当前借阅，历史借阅
 */
public class BorrowBookActivity extends BaseListActivity<BorrowBookBean> implements
        BorrowBookContract.View {

    private static final int PRAISE_REQUEST_CODE = 1000;
    private static final int COMPENSATE_REQUEST_CODE = 1001;
    private static final int NOTE_REQUEST_CODE = 1002;
    private static final String RESULT_DATA_PRAISE = "borrow_book_praise";
    private static final String RESULT_COMPENSATE_SUCCESS = "compensate_success";

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, BorrowBookActivity.class);
        context.startActivity(intent);
    }

    private BorrowBookPresenter mPresenter;

    private int mCurrentPage = 1;
    private int mPositionTag = -1;   //进入借阅详情下标签

    private CustomPopupWindow mTypeListPopupWindow;
    private BorrowCategoryAdapter mCategoryAdapter;
    private List<BorrowCategoryBean> mCategoryData = new ArrayList<>();
    private int mCategoryId = 0;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                if (mTypeListPopupWindow == null) {
                    createPopupWindow();
                }

                if (Build.VERSION.SDK_INT >= 24) {
                    mTypeListPopupWindow.showAtLocation(mCommonTitleBar, Gravity.TOP, 0, 0);
                } else {
                    mTypeListPopupWindow.showAsDropDown(mCommonTitleBar, 0, -(int) DpPxUtils.dipToPx(BorrowBookActivity.this, 48f));
                }

            }
        }
    };

    @OnClick({R.id.titlebar_left_btn, R.id.titlebar_right_txt_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.titlebar_right_txt_btn:
                if (mTypeListPopupWindow == null || !mTypeListPopupWindow.isShowing()) {
                    mHandler.sendEmptyMessageDelayed(100, 100);
                }
                break;
        }
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            BorrowBookBean bean = mAdapter.getItem(position);
            if (null != bean) {
                mPositionTag = position;
                BorrowBookDetailActivity.startActivityForResult(BorrowBookActivity.this, bean.mBorrowerId, PRAISE_REQUEST_CODE);
            }

        }
    };
    private View.OnClickListener mThumpClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            BorrowBookBean bean = mAdapter.getItem(position);
            if (null != bean && !bean.mIsPraised) {
                mPresenter.praiseBook(bean.mBorrowerId, position);
            }

        }
    };
    private View.OnClickListener mNoteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            BorrowBookBean bean = mAdapter.getItem(position);
            if (null != bean) {
                mPositionTag = position;
                ReadingNoteEditActivity.startActivityForResult(BorrowBookActivity.this, bean.mBorrowerId, NOTE_REQUEST_CODE);
            }
        }
    };
    private View.OnClickListener mOneKeyBorrowClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            BorrowBookBean bean = mAdapter.getItem(position);
            if (null != bean) {
                mPresenter.oneKeyToBorrowBook(bean.mBorrowerId,position);
            }
        }
    };
    private View.OnClickListener mLostBookClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            BorrowBookBean bean = mAdapter.getItem(position);
            if (null != bean) {
                mPositionTag = position;
                UserCompensateBookActivity.startActivityForResult(BorrowBookActivity.this, bean.mBorrowerId, COMPENSATE_REQUEST_CODE);
            }
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_borrow_book;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
    }

    @Override
    public void initDatas() {
        mCommonTitleBar.setTitle("借阅架");

        mPresenter = new BorrowBookPresenter();
        mPresenter.attachView(this);

        BorrowCategoryBean bean0 = new BorrowCategoryBean(0, "全部");
        mCategoryData.add(bean0);
        BorrowCategoryBean bean1 = new BorrowCategoryBean(1, "当前借阅");
        mCategoryData.add(bean1);
        BorrowCategoryBean bean2 = new BorrowCategoryBean(2, "历史借阅");
        mCategoryData.add(bean2);
        mPresenter.getHistoryBorrowBookList(1, mCategoryId);

        mCommonTitleBar.setRightBtnText("全部");
        mCommonTitleBar.setRightBtnTextIcon(R.mipmap.ic_title_bar_category);

        EventBus.getDefault().register(this);
    }

    @Override
    public void configViews() {
        mAdapter = new BorrowBookAdapter(this, mClickListener,
                mThumpClickListener, mNoteClickListener, mOneKeyBorrowClickListener, mLostBookClickListener);
        initAdapter(false, true);
        mRecyclerView.setDividerDrawable(R.drawable.divider_rv_vertical_three);
    }

    @Override
    public void onRefresh() {
        mPresenter.getHistoryBorrowBookList(1, mCategoryId);
    }

    @Override
    public void onLoadMore() {
        mPresenter.getHistoryBorrowBookList(mCurrentPage + 1, mCategoryId);
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void setNetError(int pageNum) {
        mRecyclerView.setRefreshing(false);
        if (pageNum == 1) {
            mAdapter.clear();
            mRecyclerView.showError();
            mRecyclerView.setRetryRefreshListener(this);
        } else {
            mAdapter.pauseMore();
        }
    }

    @Override
    public void setBorrowBookList(List<BorrowBookBean> borrowBookBeanList, int totalCount, boolean refresh) {
        if (refresh) {
            mCurrentPage = 1;
            mAdapter.clear();
        } else {
            mCurrentPage = mCurrentPage + 1;
        }
        mAdapter.addAll(borrowBookBeanList);
        mRecyclerView.setRefreshing(false);
        if (mAdapter.getCount() > 0) {
            mRecyclerView.showToastTv(getString(R.string.book_list_tips_for_borrow, mAdapter.getCount(), totalCount));
        }
        if (mAdapter.getCount() >= totalCount) {
            mAdapter.stopMore();
        }
    }

    @Override
    public void setBorrowBookEmpty(boolean refresh) {
        mRecyclerView.setRefreshing(false);
        if (refresh) {
            mAdapter.clear();
            mRecyclerView.showEmpty();
        } else {
            mAdapter.stopMore();
        }
    }

    //点赞成功，刷新position列表
    @Override
    public void praiseSuccess(int position) {
        ToastUtils.showSingleToast(R.string.success);
        mAdapter.getItem(position).mIsPraised = true;
        mAdapter.notifyItemChanged(position);
    }

    @Override
    public void showToastMsg(String msg) {
        ToastUtils.showSingleToast(msg);
    }

    @Override
    public void showToastMsg(int resId) {
        ToastUtils.showSingleToast(resId);
    }

    @Override
    public void pleaseLoginTip() {
        AccountMessage accountMessage = new AccountMessage();
        accountMessage.mIsLoginOut = true;
        accountMessage.mIsToUserCenter = true;
        EventBus.getDefault().post(accountMessage);
    }

    /**
     * 一键续借成功回调
     */
    @Override
    public void oneKeyContinueSuccess(int position) {
        //续借成功 需要影藏续借按钮 刷新剩余天数
        ((BorrowBookAdapter)mAdapter).notifySomeOneItem(position,true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PRAISE_REQUEST_CODE://店咨询
                    if (data != null && mPositionTag != -1 && mPositionTag < mAdapter.getCount()) {
                        mAdapter.getItem(mPositionTag).mIsPraised = data.getBooleanExtra(RESULT_DATA_PRAISE, false);
                        mAdapter.notifyItemChanged(mPositionTag);

                        boolean compensateSuccess = data.getBooleanExtra(RESULT_COMPENSATE_SUCCESS, false);
                        if (compensateSuccess) {
                            mPresenter.getBorrowingBookList(1);
                        }
                    }
                    break;
                case COMPENSATE_REQUEST_CODE://赔书
                    if (data != null) {
                        boolean isCompensateSuccess = data.getBooleanExtra(RESULT_COMPENSATE_SUCCESS, false);
                        if (isCompensateSuccess) {
                            mPresenter.getBorrowingBookList(1);
                        }
                    }
                    break;
                case NOTE_REQUEST_CODE://读书笔记，返回进入笔记详情
                    if (data != null) {
                        long noteId = data.getLongExtra("note_id", -1);
                        if (mPositionTag != -1 && noteId != -1 && mPositionTag < mAdapter.getCount()) {
                            BorrowBookBean bean = mAdapter.getItem(mPositionTag);
                            if (null != bean) {
                                BorrowBookDetailActivity.startActivityForResult(BorrowBookActivity.this, bean.mBorrowerId, PRAISE_REQUEST_CODE);
                            }
                        }
                    }
                    break;
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
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receviceLoginOut(AccountMessage loginMessage) {
        if (null != mPresenter && loginMessage.mIsLoginOut) {
            finish();
        }
    }


    private void createPopupWindow() {
        //设置选中状态
        for (BorrowCategoryBean bean : mCategoryData) {
            bean.mIsSelected = false;
        }
        int selectIndex = 0;
        int classifySize = mCategoryData.size();
        for (int i = 0; i < classifySize; i++) {
            if (mCategoryData.get(i).mCategoryId == mCategoryId) {
                selectIndex = i;
                break;
            }
        }
        mCategoryData.get(selectIndex).mIsSelected = true;

        mTypeListPopupWindow = new CustomPopupWindow(mContext);
        mCategoryAdapter = new BorrowCategoryAdapter(mContext, mCategoryData);
        View view = View.inflate(mContext, R.layout.ppw_titlebar_category, null);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTypeListPopupWindow.dismiss();
            }
        });
        ListView lv = (ListView) view.findViewById(R.id.category_lv);
        lv.setAdapter(mCategoryAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                BorrowCategoryBean item = mCategoryData.get(position);

                if (mCategoryId == item.mCategoryId) {
                    mTypeListPopupWindow.dismiss();
                    return;
                }

                for (BorrowCategoryBean bean : mCategoryData) {
                    bean.mIsSelected = false;
                }
                mCategoryData.get(position).mIsSelected = true;
                mCategoryAdapter.notifyDataSetChanged();

                mCategoryId = item.mCategoryId;
                mCommonTitleBar.setRightBtnText(item.mName);

                mPresenter.getHistoryBorrowBookList(1, mCategoryId);
                mTypeListPopupWindow.dismiss();
            }
        });
        mTypeListPopupWindow.setContentView(view);
        mTypeListPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mTypeListPopupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        mTypeListPopupWindow.setTouchable(true);
        mTypeListPopupWindow.setOutsideTouchable(true);
        mTypeListPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }
}
