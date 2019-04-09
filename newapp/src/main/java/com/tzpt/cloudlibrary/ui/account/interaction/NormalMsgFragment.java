package com.tzpt.cloudlibrary.ui.account.interaction;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.BaseListFragment;
import com.tzpt.cloudlibrary.bean.CommentBean;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.ui.information.InformationCommentDetailsActivity;
import com.tzpt.cloudlibrary.ui.information.InformationDetailDiscussActivity;
import com.tzpt.cloudlibrary.ui.library.LibraryMessageBoardActivity;
import com.tzpt.cloudlibrary.utils.KeyboardUtils;
import com.tzpt.cloudlibrary.utils.ToastUtils;
import com.tzpt.cloudlibrary.widget.CustomDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.tzpt.cloudlibrary.AppIntentGlobalName.COMMENT_MSG_ID;

/**
 * 其他消息列表
 * Created by Administrator on 2018/3/29.
 */

public class NormalMsgFragment extends BaseListFragment<CommentBean> implements NormalMsgContract.View {
    private static final int REQUEST_CODE_MESSAGE_BOARD = 1000;
    private static final int REQUEST_CODE_COMMENT_DETAIL = REQUEST_CODE_MESSAGE_BOARD + 1;
    private static final int REQUEST_CODE_COMMENT_LIST = REQUEST_CODE_COMMENT_DETAIL + 1;

    @BindView(R.id.comment_publish_ll)
    LinearLayout mCommentPublishLl;
    @BindView(R.id.comment_publish_content_et)
    EditText mCommentPublishContentEt;

    private boolean mIsPrepared;
    private boolean mIsFirstLoad = true;

    private NormalMsgPresenter mPresenter;
    private int mCurrentPage = 1;
    private long mReplyId;
    private String mLibCode;
    private long mId;

    @OnClick({R.id.comment_publish_cover_view, R.id.comment_publish_send_btn})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.comment_publish_cover_view:
                KeyboardUtils.hideSoftInput(mCommentPublishContentEt);
                mCommentPublishLl.setVisibility(View.GONE);
                break;
            case R.id.comment_publish_send_btn:
                KeyboardUtils.showSoftInput(mCommentPublishContentEt);
                String content = mCommentPublishContentEt.getText().toString().trim();
                mPresenter.publishMessage(mLibCode, content, mReplyId);
                break;
        }
    }

    //回复点击
    private View.OnClickListener mMsgClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            if (position < mAdapter.getCount()) {
                CommentBean commentBean = mAdapter.getItem(position);
                KeyboardUtils.showSoftInput(getSupportActivity());
                if (null != commentBean) {
                    mCommentPublishLl.setVisibility(View.VISIBLE);
                    mCommentPublishContentEt.setHint("回复" + commentBean.mCommentName);
                    mCommentPublishContentEt.requestFocus();

                    if (mId != commentBean.mId) {
                        mCommentPublishContentEt.setText("");
                    }

                    mLibCode = commentBean.mLibraryCode;
                    mReplyId = commentBean.mRepliedID;
                    mId = commentBean.mId;
                }
            }
        }
    };

    @Override
    public void onRefresh() {
        mPresenter.getMyMessageList(1);
        KeyboardUtils.hideSoftInput(getSupportActivity());
    }

    @Override
    public void onLoadMore() {
        mPresenter.getMyMessageList(mCurrentPage + 1);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_normal_msg;
    }

    @Override
    public void initDatas() {
        mPresenter = new NormalMsgPresenter();
        mPresenter.attachView(this);

        mIsPrepared = true;
        lazyLoad();
    }

    @Override
    public void configViews() {
        mAdapter = new MyMessageAdapter(getContext(), mMsgClickListener);
        initAdapter(true, true);
        mRecyclerView.setDividerDrawable(R.drawable.divider_rv_vertical_default);
    }

    @Override
    protected void lazyLoad() {
        if (!mIsVisible || !mIsPrepared) {
            return;
        }
        if (mPresenter != null && mIsFirstLoad) {//懒加载，只允许第一次加载
            this.mIsFirstLoad = false;
            mPresenter.getMyMessageList(1);
        }
    }

    @Override
    public void onItemClick(int position) {
        CommentBean bean = mAdapter.getItem(position);

        if (TextUtils.isEmpty(bean.mLibraryCode)) {
            //资讯
            if (bean.mType == 5) {
                //1.若点赞的是资讯评论内容，则进入评论列表定位
                InformationDetailDiscussActivity.startActivityForFragmentResult(this, bean.mNewsId, bean.mId, REQUEST_CODE_COMMENT_LIST);
            } else {
                //2.若点赞的是评论的回复内容，则进入评论详情页面定位 //他人回复
                InformationCommentDetailsActivity.startActivityForResultFragment(this, bean.mId, bean.mRepliedID, null, REQUEST_CODE_COMMENT_DETAIL);
            }
        } else {
            //留言板
            if (bean.mType == 5) {
                LibraryMessageBoardActivity.startActivityForResultFragment(this, bean.mLibraryCode, bean.mId, bean.mIsBookStore ? "顾客留言" : "读者留言", REQUEST_CODE_MESSAGE_BOARD);
            } else {
                //2.若点赞的是留言板的回复内容，则进入留言板详情页面定位 //他人回复
                InformationCommentDetailsActivity.startActivityForResultFragment(this, bean.mId, bean.mRepliedID, bean.mLibraryCode, REQUEST_CODE_COMMENT_DETAIL);
            }
        }
    }

    @Override
    public void showMsgDialog(int msgId) {
        final CustomDialog dialog = new CustomDialog(getContext(), R.style.DialogTheme, getString(msgId));
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
    public void setMyMessageList(List<CommentBean> beanList, int totalCount, boolean refresh) {
        mRecyclerView.setRefreshing(false);
        if (refresh) {
            mAdapter.clear();
            mCurrentPage = 1;
        } else {
            mCurrentPage = mCurrentPage + 1;
        }
        mAdapter.addAll(beanList);
        mAdapter.notifyDataSetChanged();
        if (mAdapter.getCount() >= totalCount) {
            mAdapter.stopMore();
        }
    }

    @Override
    public void setMyMessageListEmpty(boolean refresh) {
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
        //自定义对话框
        AccountMessage accountMessage = new AccountMessage();
        accountMessage.mIsLoginOut = true;
        accountMessage.mIsToUserCenter = true;
        EventBus.getDefault().post(accountMessage);
    }

    @Override
    public void showSendProgressDialog() {
        showDialog("发送中...");
        KeyboardUtils.hideSoftInput(getSupportActivity());
    }

    @Override
    public void dismissSendProgressDialog() {
        dismissDialog();
    }

    @Override
    public void publishMsgSuccess() {
        ToastUtils.showSingleToast(R.string.publish_reply_success);
        KeyboardUtils.hideSoftInput(mCommentPublishContentEt);
        mCommentPublishLl.setVisibility(View.GONE);
        mCommentPublishContentEt.setText("");
    }

    @Override
    public void publishMsgFailure() {
        showMsgDialog(R.string.publish_message_failure);
    }

    @Override
    public void showNoCommentDialog() {
        final CustomDialog dialog = new CustomDialog(getContext(),
                R.style.DialogTheme, getString(R.string.this_reply_deleted));
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                if (mId > -1) {
                    removeComment(mId);
                }
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_MESSAGE_BOARD:
            case REQUEST_CODE_COMMENT_DETAIL:
            case REQUEST_CODE_COMMENT_LIST:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    long id = data.getLongExtra(COMMENT_MSG_ID, -1);
                    if (id > -1) {
                        removeComment(id);
                    }
                }
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != mAdapter) {
            mAdapter.clear();
        }
        if (mPresenter != null) {
            mPresenter.detachView();
            mPresenter = null;
        }
    }

    private void removeComment(long id) {
        for (CommentBean item : mAdapter.getAllData()) {
            if (item.mId == id) {
                int position = mAdapter.getPosition(item);
                mAdapter.remove(position);
                mAdapter.notifyItemChanged(position);
                break;
            }
        }
        if (mAdapter.getCount() == 0) {
            mRecyclerView.showEmpty();
        }
    }
}
