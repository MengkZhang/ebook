package com.tzpt.cloudlibrary.ui.library;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tzpt.cloudlibrary.AppIntentGlobalName;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseListActivity;
import com.tzpt.cloudlibrary.bean.CommentBean;
import com.tzpt.cloudlibrary.bean.CommentMsgBean;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.ui.account.LoginActivity;
import com.tzpt.cloudlibrary.ui.account.interaction.NormalMsgFragment;
import com.tzpt.cloudlibrary.ui.common.GalleyActivity;
import com.tzpt.cloudlibrary.ui.information.InformationCommentDetailsActivity;
import com.tzpt.cloudlibrary.utils.KeyboardUtils;
import com.tzpt.cloudlibrary.utils.StringUtils;
import com.tzpt.cloudlibrary.utils.ToastUtils;
import com.tzpt.cloudlibrary.widget.CustomDialog;
import com.tzpt.cloudlibrary.widget.LoadingProgressView;
import com.tzpt.cloudlibrary.widget.recyclerview.swipe.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.tzpt.cloudlibrary.AppIntentGlobalName.COMMENT_MSG_ID;

/**
 * 图书馆留言
 */
public class LibraryMessageBoardActivity extends BaseListActivity<CommentBean> implements LibraryMessageBoardContract.View {

    private static final int TO_MSG_REQUEST_CODE = 1000;
    private static final int TO_DETAIL_REQUEST_CODE = 1001;
    private static final int TO_LOGIN_REQUEST_CODE = 1002;
    private static final String LIBRARY_CODE = "library_code";
    private static final String LIBRARY_TITLE = "library_title";
    private static final String MSG_ID = "msgId";

    public static void startActivity(Context context, String libCode, String libTitle) {
        Intent intent = new Intent(context, LibraryMessageBoardActivity.class);
        intent.putExtra(LIBRARY_CODE, libCode);
        intent.putExtra(LIBRARY_TITLE, libTitle);
        context.startActivity(intent);
    }

    public static void startActivityForResultFragment(NormalMsgFragment fragment, String libCode, long msgId, String libTitle, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), LibraryMessageBoardActivity.class);
        intent.putExtra(LIBRARY_CODE, libCode);
        intent.putExtra(LIBRARY_TITLE, libTitle);
        intent.putExtra(MSG_ID, msgId);
        fragment.startActivityForResult(intent, requestCode);
    }

    @BindView(R.id.message_board_my_msg_tv)
    TextView mMessageBoardMyMsgTv;
    @BindView(R.id.message_board_shadow_iv)
    ImageView mMessageBoardShadowIv;
    @BindView(R.id.comment_publish_ll)
    LinearLayout mCommentPublishLl;
    @BindView(R.id.comment_publish_content_et)
    EditText mCommentPublishContentEt;

    @BindView(R.id.progress_layout)
    LoadingProgressView mLoadingProgressView;

    private LibraryMessageBoardPresenter mPresenter;
    private int mCurrentPage = 1;
    private String mLibCode;

    private int mRealPosition = -1;
    private long mIndexMsgId = -1;
    private long mMsgId;

    private View.OnClickListener mTagOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int position = (int) v.getTag();
            final CommentBean bean = mAdapter.getItem(position);
            if (null != bean) {
                ArrayList<String> imageList = new ArrayList<>();
                imageList.add(bean.mImagePath);

                int location[] = new int[2];
                v.getLocationOnScreen(location);
                GalleyActivity.startActivity(LibraryMessageBoardActivity.this, imageList, location[0], location[1], v.getWidth(), v.getHeight());
            }
        }
    };

    private View.OnClickListener mDelOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final long messageId = (long) v.getTag();
            final CustomDialog dialog = new CustomDialog(LibraryMessageBoardActivity.this, R.style.DialogTheme, "确定删除？");
            dialog.setCancelable(false);
            dialog.hasNoCancel(true);
            dialog.setOkText("确定");
            dialog.setCancelText("取消");
            dialog.show();
            dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
                @Override
                public void onClickOk() {
                    dialog.dismiss();
                    mPresenter.delMsg(mLibCode, messageId);
                    //删除，清空回复内容
                    resetReplyContent();
                }

                @Override
                public void onClickCancel() {
                    dialog.dismiss();
                }
            });
        }
    };

    private View.OnClickListener mReplyClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //如果回复下标不是同一个下标，则清空内容
            long msgId = (long) v.getTag();
            if (mMsgId != msgId) {
                resetReplyContent();
            }
            mMsgId = msgId;
            int position = findPositionByMsgId(msgId);

            final CommentBean bean = mAdapter.getItem(position);
            if (mPresenter.isLogin()) {
                mCommentPublishLl.setVisibility(View.VISIBLE);
                mCommentPublishContentEt.requestFocus();
                mCommentPublishContentEt.setHint("回复" + bean.mCommentName);
                KeyboardUtils.showSoftInput(LibraryMessageBoardActivity.this);
            } else {
                LoginActivity.startActivityForResult(LibraryMessageBoardActivity.this, TO_LOGIN_REQUEST_CODE);
            }
        }
    };

    private View.OnClickListener mPraiseClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            final CommentBean bean = mAdapter.getItem(position);

            mPresenter.praise(bean.mId, position);
        }
    };

    private View.OnClickListener mMoreReplyClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            long msgId = (long) v.getTag();
            resetReplyContent();

            InformationCommentDetailsActivity.startActivityForResult(LibraryMessageBoardActivity.this, msgId, -1, mLibCode, TO_DETAIL_REQUEST_CODE);
        }
    };

    @OnClick({R.id.titlebar_left_btn, R.id.message_board_my_msg_tv, R.id.comment_publish_cover_view,
            R.id.comment_publish_send_btn})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.message_board_my_msg_tv:
                if (mPresenter.isLogin()) {
                    LibraryToLeaveMessageActivity.startActivity(this, mLibCode, TO_MSG_REQUEST_CODE);
                } else {
                    LoginActivity.startActivityForResult(this, TO_LOGIN_REQUEST_CODE);
                }
                break;
            case R.id.comment_publish_cover_view:
                mCommentPublishLl.setVisibility(View.GONE);
                KeyboardUtils.hideSoftInput(this);
                break;
            case R.id.comment_publish_send_btn:
                String content = mCommentPublishContentEt.getText().toString().trim();
                if (!TextUtils.isEmpty(content)) {
                    if (StringUtils.isHaveEmoji(content)) {
                        final CustomDialog dialog = new CustomDialog(this, R.style.DialogTheme, getString(R.string.emoji_tips));
                        dialog.setCancelable(false);
                        dialog.hasNoCancel(false);
                        dialog.setButtonTextConfirmOrYes(true);
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
                    } else {
                        if (mMsgId > 0) {
                            mPresenter.replyMessage(mLibCode, mMsgId, content);
                        }
                    }
                }
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_library_message_board;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
    }

    @Override
    public void initDatas() {
        mPresenter = new LibraryMessageBoardPresenter();
        mPresenter.attachView(this);

        mLibCode = getIntent().getStringExtra(LIBRARY_CODE);
        String title = getIntent().getStringExtra(LIBRARY_TITLE);
        mCommonTitleBar.setTitle(title);

        mIndexMsgId = getIntent().getLongExtra(MSG_ID, -1);
        mPresenter.getLibraryMessageBoardList(mLibCode, 1, mIndexMsgId);
    }

    @Override
    public void configViews() {
        mAdapter = new LibraryMessageBoardAdapter(this, mDelOnclickListener, mTagOnclickListener,
                mReplyClickListener, mPraiseClickListener, mMoreReplyClickListener);
        initAdapter(false, true);

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mAdapter.getCount() > 0) {
                    if (mRealPosition != -1) {
                        mAdapter.getItem(mRealPosition).mIsLight = false;
                        mAdapter.notifyItemChanged(mRealPosition);
                    }
                    mRealPosition = -1;
                }
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        resetReplyContent();

        CommentBean item = mAdapter.getItem(position);
        InformationCommentDetailsActivity.startActivityForResult(LibraryMessageBoardActivity.this, item.mId, -1, mLibCode, TO_DETAIL_REQUEST_CODE);
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {
        if (null != mPresenter) {
            mPresenter.getLibraryMessageBoardList(mLibCode, mCurrentPage + 1, -1);
        }
    }

    @Override
    public void setLibraryMessageBoardList(List<CommentBean> messageBoardList, int totalCount, boolean refresh) {
        showMessageBoardView(true);
        mRecyclerView.setRefreshing(false);
        if (refresh) {
            mAdapter.clear();
            mCurrentPage = 1;
        } else {
            mCurrentPage = mCurrentPage + 1;
        }
        mAdapter.addAll(messageBoardList);
        mAdapter.notifyDataSetChanged();

        if (mAdapter.getCount() >= totalCount) {
            mAdapter.stopMore();
        }
    }

    @Override
    public void setLibraryMessageBoardListEmpty(boolean refresh) {
        showMessageBoardView(true);
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
        if (refresh) {
            mAdapter.clear();
            mRecyclerView.showError();
            showMessageBoardView(false);
            mRecyclerView.setRetryRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mPresenter.getLibraryMessageBoardList(mLibCode, 1, mIndexMsgId);
                }
            });
        } else {
            mAdapter.pauseMore();
        }
    }

    @Override
    public void showProgressDialog() {
        mLoadingProgressView.showProgressLayout();
    }

    @Override
    public void dismissDelProgressDialog() {
        mLoadingProgressView.hideProgressLayout();
    }

    @Override
    public void showMessageTips(int msgId) {
        ToastUtils.showSingleToast(msgId);
    }

    @Override
    public void pleaseLoginTip() {
        AccountMessage accountMessage = new AccountMessage();
        accountMessage.mIsLoginOut = true;
        accountMessage.mIsToUserCenter = false;
        EventBus.getDefault().post(accountMessage);

        final CustomDialog dialog = new CustomDialog(this, R.style.DialogTheme, getString(R.string.account_login_other_device));
        dialog.setCancelable(false);
        dialog.hasNoCancel(true);
        dialog.setLoginAnewBtn();
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                LoginActivity.startActivityForResult(LibraryMessageBoardActivity.this, TO_LOGIN_REQUEST_CODE);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
                mPresenter.getLibraryMessageBoardList(mLibCode, 1, -1);
            }
        });
    }

    @Override
    public void praiseSuccess(int position) {
        final CommentBean bean = mAdapter.getItem(position);
        bean.mIsPraised = true;
        bean.mPraisedCount++;
        mAdapter.notifyItemChanged(position);
    }

    @Override
    public void replyMessageSuccess(CommentBean commentBean, long msgId) {
        int position = findPositionByMsgId(msgId);
        if (position > -1 && position < mAdapter.getCount()) {
            final CommentBean bean = mAdapter.getItem(position);
            bean.mReplyCount++;
            if (bean.mReplyContentList == null) {
                bean.mReplyContentList = new ArrayList<>();
                bean.mReplyContentList.add(commentBean);
            } else {
                if (bean.mReplyContentList.size() >= 2) {
                    bean.mReplyContentList.remove(bean.mReplyContentList.size() - 1);
                    bean.mReplyContentList.add(0, commentBean);
                } else {
                    bean.mReplyContentList.add(0, commentBean);
                }
            }
            mAdapter.notifyItemChanged(position);
        }
        mCommentPublishLl.setVisibility(View.GONE);
        resetReplyContent();
        KeyboardUtils.hideSoftInput(mCommentPublishContentEt);
    }

    @Override
    public void removeMsgBoard(long msgId) {
        int position = findPositionByMsgId(msgId);
        if (position > -1 && position < mAdapter.getCount()) {
            mAdapter.notifyItemRemoved(position);
        }
    }

    @Override
    public void setMessageHighLight(int targetIndex, int currentPage) {
        mCurrentPage = currentPage;
        mRealPosition = targetIndex;
        ((LinearLayoutManager) mRecyclerView.getRecyclerView().getLayoutManager()).scrollToPositionWithOffset(targetIndex < 0 ? 0 : targetIndex, 0);
        mAdapter.getItem(targetIndex).mIsLight = true;
        mAdapter.notifyItemChanged(targetIndex);
    }

    @Override
    public void showNoCommentDialog(final long msgId, final boolean finish) {
        final CustomDialog dialog = new CustomDialog(LibraryMessageBoardActivity.this, R.style.DialogTheme, getString(R.string.this_message_deleted));
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                if (finish) {
                    Intent data = new Intent();
                    data.putExtra(COMMENT_MSG_ID, msgId);
                    setResult(RESULT_OK, data);
                    finish();
                } else {
                    resetReplyContent();
                    int position = findPositionByMsgId(msgId);
                    if (position > -1 && position < mAdapter.getCount()) {
                        mAdapter.remove(position);
                        mAdapter.notifyDataSetChanged();
                    }
                    KeyboardUtils.hideSoftInput(mCommentPublishContentEt);
                }

            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TO_LOGIN_REQUEST_CODE:
                    mPresenter.refreshMessageBoardList(mLibCode);
                    break;
                //接收“我要留言”成功后，刷新留言列表
                case TO_MSG_REQUEST_CODE:
                    if (data != null) {
                        boolean saveMsgSuccess = data.getBooleanExtra(AppIntentGlobalName.SUBMIT_MSG_SUCCESS, false);
                        if (saveMsgSuccess) {
                            mPresenter.refreshMessageBoardList(mLibCode);
                        }
                    }
                    break;
                case TO_DETAIL_REQUEST_CODE:
                    if (data != null) {
                        long msgId = data.getLongExtra(COMMENT_MSG_ID, -1);
                        int index = findPositionByMsgId(msgId);
                        //删除操作
                        if (index > -1 && index < mAdapter.getCount()) {
                            mAdapter.remove(index);
                            mAdapter.notifyItemChanged(index);

                            if (mAdapter.getCount() == 0) {
                                mRecyclerView.showEmpty();
                            }
                        }
                        CommentMsgBean bean = (CommentMsgBean) data.getSerializableExtra(AppIntentGlobalName.COMMENT_MSG_BEAN);
                        if (null != mPresenter && null != bean) {
                            int position = findPositionByMsgId(bean.mMsgId);
                            if (position > -1 && position < mAdapter.getCount()) {
                                //点赞操作数量
                                if (bean.mIsChangePraisedCount) {
                                    mAdapter.getItem(position).mIsPraised = bean.mIsOwnPraised;
                                    mAdapter.getItem(position).mPraisedCount = bean.mPraisedCount;
                                    mAdapter.notifyItemChanged(position);
                                }
                                //更新二级列表和回复数量
                                if (bean.mIsChangeCommentData) {
                                    mAdapter.getItem(position).mReplyContentList = bean.mReplyList;
                                    mAdapter.getItem(position).mReplyCount = bean.mReplyCount;
                                    mAdapter.notifyItemChanged(position);
                                }
                            }
                        }
                    }
                    break;
            }
        }
    }

    private int findPositionByMsgId(long msgId) {
        int position = -1;
        for (int i = 0; i < mAdapter.getCount(); i++) {
            if (mAdapter.getAllData().get(i).mId == msgId) {
                position = i;
                break;
            }
        }
        return position;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
            mPresenter = null;
        }
    }

    private void showMessageBoardView(boolean show) {
        mMessageBoardMyMsgTv.setVisibility(show ? View.VISIBLE : View.GONE);
        mMessageBoardShadowIv.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * 清空回复内容
     */
    public void resetReplyContent() {
        mCommentPublishContentEt.setText("");
    }

}
