package com.tzpt.cloudlibrary.ui.information;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.tzpt.cloudlibrary.utils.KeyboardUtils;
import com.tzpt.cloudlibrary.utils.StringUtils;
import com.tzpt.cloudlibrary.utils.ToastUtils;
import com.tzpt.cloudlibrary.widget.CustomDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 评论和留言详情
 */
public class InformationCommentDetailsActivity extends BaseListActivity<CommentBean> implements
        InformationCommentDetailsContract.View {

    private static final int TO_LOGIN_REQUEST_CODE = 1000;
    private static final String LIB_CODE = "lib_code";
    private static final String COMMENT_ID = "comment_id";
    private static final String COMMENT_REPLY_ID = "comment_reply_id";

    public static void startActivityForResult(Activity activity, long commentId, long replyId, String libCode, int requestCode) {
        Intent intent = new Intent(activity, InformationCommentDetailsActivity.class);
        intent.putExtra(COMMENT_ID, commentId);
        intent.putExtra(COMMENT_REPLY_ID, replyId);
        intent.putExtra(LIB_CODE, libCode);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void startActivityForResultFragment(NormalMsgFragment fragment, long commentId, long replyId, String libCode, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), InformationCommentDetailsActivity.class);
        intent.putExtra(COMMENT_ID, commentId);
        intent.putExtra(COMMENT_REPLY_ID, replyId);
        intent.putExtra(LIB_CODE, libCode);
        fragment.startActivityForResult(intent, requestCode);
    }

    @BindView(R.id.comment_publish_default_rl)
    RelativeLayout mCommentPublishDefaultRl;
    @BindView(R.id.comment_publish_ll)
    LinearLayout mCommentPublishLl;
    @BindView(R.id.discuss_comment_content_tv)
    TextView mDiscussCommentContentTv;
    @BindView(R.id.no_login_cover_view)
    View mDiscussNoLoginView;
    @BindView(R.id.comment_publish_content_et)
    EditText mCommentPublishContentEt;

    private InformationCommentDetailsPresenter mPresenter;
    private int mCurrentPage = 1;
    private int mRealPosition = -1;

    private boolean mIsChangePraisedCount = false;  //是否更改了点赞数量
    private boolean mIsChangeCommentData = false;   //是否更改了回复评论数据

    private long mCommentId;
    private long mReplyId;
    private long mReplyRepliedId;

    private String mLibCode = null;
    private boolean mIsLibMsgBoard;
    private boolean mIsHeadComment = true;

    @OnClick({R.id.titlebar_left_btn, R.id.titlebar_right_txt_btn, R.id.discuss_comment_content_tv,
            R.id.no_login_cover_view, R.id.comment_publish_cover_view, R.id.comment_publish_send_btn})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.titlebar_right_txt_btn:
                if (mPresenter.getNewsId() == 0) {
                    return;
                }
                InformationDetailDiscussActivity.startActivity(this, mPresenter.getNewsId());
                break;
            case R.id.discuss_comment_content_tv:
                mCommentPublishLl.setVisibility(View.VISIBLE);
                mCommentPublishContentEt.requestFocus();
                KeyboardUtils.showSoftInput(mCommentPublishContentEt);
                break;
            case R.id.no_login_cover_view:
                if (!mPresenter.isLogin()) {
                    LoginActivity.startActivityForResult(InformationCommentDetailsActivity.this, TO_LOGIN_REQUEST_CODE);
                }
                break;
            case R.id.comment_publish_cover_view:
                mCommentPublishLl.setVisibility(View.GONE);
                KeyboardUtils.hideSoftInput(this);
                break;
            case R.id.comment_publish_send_btn:
                String content = mCommentPublishContentEt.getText().toString().trim();
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
                    if (mIsHeadComment) {
                        mPresenter.replyComment(mIsLibMsgBoard, mLibCode, mCommentId, content);
                    } else {
                        mPresenter.replyRepliedComment(mIsLibMsgBoard, mLibCode, mCommentId, mReplyRepliedId, content);
                    }
                }
                break;

        }
    }

    private InformationCommentDetailsAdapter.ReaderCommentReplyListener readerCommentReplyListener =
            new InformationCommentDetailsAdapter.ReaderCommentReplyListener() {

                @Override
                public void replyReaderMsg(long id, String readerName, final int position) {
                    mIsHeadComment = position == 0;

                    if (!mPresenter.isLogin()) {
                        LoginActivity.startActivityForResult(InformationCommentDetailsActivity.this, TO_LOGIN_REQUEST_CODE);
                        return;
                    }//检测用户回复内容，是否同一个用户
                    if (mReplyRepliedId != id) {
                        mCommentPublishContentEt.setText("");
                    }
                    mReplyRepliedId = id;

                    KeyboardUtils.showSoftInput(InformationCommentDetailsActivity.this);
                    mCommentPublishLl.setVisibility(View.VISIBLE);
                    mCommentPublishContentEt.requestFocus();
                    if (position == 0) {//回复他人评论
                        mCommentPublishContentEt.setHint(mIsLibMsgBoard ? "写留言" : "写评论");
                    } else {
                        mCommentPublishContentEt.setHint("回复" + readerName);
                    }
                }

                @Override
                public void delReaderMsg(final long id, final int position) {
                    final CustomDialog dialog = new CustomDialog(InformationCommentDetailsActivity.this, R.style.DialogTheme, "确定删除？");
                    dialog.setCancelable(false);
                    dialog.hasNoCancel(true);
                    dialog.setOkText(getString(R.string.confirm));
                    dialog.setCancelText(getString(R.string.cancel));
                    dialog.show();
                    dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
                        @Override
                        public void onClickOk() {
                            dialog.dismiss();
                            if (position == 0) {//删除自己评论
                                mPresenter.delOwnMsg(mIsLibMsgBoard, mCommentId);
                            } else {
                                mPresenter.delReplyOwnMsg(mIsLibMsgBoard, id);
                            }
                        }

                        @Override
                        public void onClickCancel() {
                            dialog.dismiss();
                        }
                    });
                }

                @Override
                public void praiseMsg(long id, int position, boolean isPraised, int praisedCount) {
                    if (position == 0) {
                        mPresenter.praiseReaderMsg(mIsLibMsgBoard, mCommentId, praisedCount);
                    } else {
                        mPresenter.replyReaderPraise(mIsLibMsgBoard, id, praisedCount);
                    }
                }

                @Override
                public void onImageClicked(String imagePath, ImageView imageView) {
                    ArrayList<String> imageList = new ArrayList<>();
                    imageList.add(imagePath);

                    int location[] = new int[2];
                    imageView.getLocationOnScreen(location);
                    GalleyActivity.startActivity(InformationCommentDetailsActivity.this, imageList, location[0], location[1], imageView.getWidth(), imageView.getHeight());
                }
            };

    @Override
    public int getLayoutId() {
        return R.layout.activity_information_comment_details;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
    }

    @Override
    public void initDatas() {
        mPresenter = new InformationCommentDetailsPresenter();
        mPresenter.attachView(this);
        EventBus.getDefault().register(this);

        mReplyId = getIntent().getLongExtra(COMMENT_REPLY_ID, -1);
        mCommentId = getIntent().getLongExtra(COMMENT_ID, -1);
        mLibCode = getIntent().getStringExtra(LIB_CODE);
        mIsLibMsgBoard = !TextUtils.isEmpty(mLibCode);

        if (mIsLibMsgBoard) {
            mCommonTitleBar.setTitle("留言详情");
        } else {
            mCommonTitleBar.setTitle("评论详情");
            if (mReplyId > 0) {
                mCommonTitleBar.setRightBtnText(R.string.look_original);
            }
        }
        mPresenter.getCommentDetailList(mIsLibMsgBoard, mCommentId, mReplyId, 1);
    }

    @Override
    public void configViews() {
        //setSupportSlideBack(false);
        mAdapter = new InformationCommentDetailsAdapter(this, readerCommentReplyListener);
        initAdapter(false, true);

        mRecyclerView.setDividerDrawable(R.drawable.divider_rv_vertical_default);
        mDiscussCommentContentTv.setText(mIsLibMsgBoard ? "写留言" : "写评论");
        mCommentPublishContentEt.setHint(mIsLibMsgBoard ? "写留言" : "写评论");
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mAdapter.getCount() > 0) {
                    if (mRealPosition != -1 && mRealPosition < mAdapter.getCount()) {
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

    }

    @Override
    public void onRefresh() {
        mPresenter.getCommentDetailList(mIsLibMsgBoard, mCommentId, mReplyId, 1);
    }

    @Override
    public void onLoadMore() {
        mPresenter.getCommentDetailList(mIsLibMsgBoard, mCommentId, mReplyId, mCurrentPage + 1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        UmengHelper.setUmengResume(this);
        setLoginStatus(mPresenter.isLogin());
    }

    @Override
    protected void onPause() {
        super.onPause();
        UmengHelper.setUmengPause(this);
    }

    private void setLoginStatus(boolean isLogin) {
        if (isLogin) {
            mDiscussNoLoginView.setVisibility(View.GONE);
        } else {
            mDiscussNoLoginView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setNetError(boolean refresh) {
        if (refresh) {
            mAdapter.clear();
            mRecyclerView.showError();
            mRecyclerView.setRetryRefreshListener(this);
        } else {
            mAdapter.pauseMore();
        }
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
                LoginActivity.startActivityForResult(InformationCommentDetailsActivity.this, TO_LOGIN_REQUEST_CODE);
                finish();
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveLoginOut(AccountMessage loginMessage) {
        if (null != mPresenter && loginMessage.mIsLoginOut) {
            mPresenter.getCommentDetailList(mIsLibMsgBoard, mCommentId, mReplyId, 1);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveLoginInfo(AccountMessage loginMessage) {
        if (null != mPresenter && loginMessage.success) {
            mPresenter.getCommentDetailList(mIsLibMsgBoard, mCommentId, mReplyId, 1);
        }
    }

    @Override
    public void setDiscussCommentList(List<CommentBean> replyBeanList, int totalCount, boolean refresh) {
        if (refresh) {
            mAdapter.clear();
            mCurrentPage = 1;
        } else {
            mCurrentPage = mCurrentPage + 1;
        }
        mAdapter.addAll(replyBeanList);
        mAdapter.notifyDataSetChanged();
        if (mAdapter.getCount() >= totalCount) {
            mAdapter.stopMore();
        }
    }

    @Override
    public void setDiscussCommentListEmpty(boolean refresh) {
        if (refresh) {
            mAdapter.clear();
            mRecyclerView.showEmpty();
        } else {
            mAdapter.stopMore();
        }
    }

    @Override
    public void showMsgProgressDialog(String msg) {
        KeyboardUtils.hideSoftInput(this);
        showDialog(msg);
    }

    @Override
    public void dismissMsgProgressDialog() {
        dismissDialog();
    }

    @Override
    public void showToastMsg(int msgId) {
        ToastUtils.showSingleToast(msgId);
    }

    @Override
    public void delReaderMsgSuccess(boolean isOwn, long replyId) {
        if (isOwn) {
            sendDelOwnMsg();
        } else {
            mIsChangeCommentData = true;
            int position = findPositionByMsgId(replyId);
            if (position > -1 && position < mAdapter.getCount()) {

                mAdapter.remove(position);
                int totalCount = mAdapter.getItem(0).mReplyCount;
                mAdapter.getItem(0).mReplyCount = totalCount < 1 ? 1 : totalCount - 1;
                mAdapter.notifyItemChanged(0);      //更新第一条数据
                mAdapter.notifyItemChanged(position);       //更新指定数据

                resetUserReplyPosition();
            }
        }
    }

    /**
     * 返回评论列表操作
     * 删除操作 修改自己点赞数量 更新二级列表和回复数量
     */
    private void checkCommentData() {
        //如果有其中一个修改了，则返回数据对象
        if (mIsChangePraisedCount || mIsChangeCommentData) {
            CommentMsgBean bean = new CommentMsgBean();
            if (mIsChangePraisedCount) {//更新点赞操作数量
                bean.mIsChangePraisedCount = true;
                bean.mIsOwnPraised = mAdapter.getItem(0).mIsPraised;
                bean.mPraisedCount = mAdapter.getItem(0).mPraisedCount;
            }
            if (mIsChangeCommentData) {// 更新二级列表和回复数量
                bean.mIsChangeCommentData = true;
                bean.mReplyCount = mAdapter.getItem(0).mReplyCount;
                List<CommentBean> replyList = new ArrayList<>();
                for (int i = 1; i < (mAdapter.getCount() > 3 ? 3 : mAdapter.getCount()); i++) {
                    replyList.add(mAdapter.getItem(i));
                }
                bean.mReplyList = replyList;
            }
            bean.mMsgId = mCommentId;
            Intent intent = new Intent();
            intent.putExtra(AppIntentGlobalName.COMMENT_MSG_BEAN, bean);
            setResult(RESULT_OK, intent);
        }
    }

    @Override
    public void publishReaderMsgSuccess(int resId) {
        resetUserReplyPosition();

        ToastUtils.showSingleToast(resId);
        KeyboardUtils.hideSoftInput(mCommentPublishContentEt);
        mIsChangeCommentData = true;
    }

    @Override
    public void setCommentPraiseStatus(long replyId, int praisedCount) {
        mIsChangePraisedCount = true;
        int position = findPositionByMsgId(replyId);
        if (position > -1 && position < mAdapter.getCount()) {
            mAdapter.getItem(position).mIsPraised = true;
            mAdapter.getItem(position).mPraisedCount = praisedCount + 1;
            mAdapter.notifyItemChanged(position);
        }
    }

    @Override
    public void setDiscussLocationAndCurrentPage(int discussIndex, int currentPage) {
        mCurrentPage = currentPage;
        mRealPosition = discussIndex;
        ((LinearLayoutManager) mRecyclerView.getRecyclerView().getLayoutManager()).scrollToPositionWithOffset(discussIndex < 0 ? 0 : discussIndex, 0);
        mAdapter.getItem(discussIndex).mIsLight = true;
        mAdapter.notifyItemChanged(discussIndex);//含有header, position + 1
    }

    @Override
    public void showNoCommentDialog(final long replyId, final boolean finish) {
        String msg;
        if (replyId >= 0) {
            msg = getString(R.string.this_reply_deleted);
        } else {
            if (TextUtils.isEmpty(mLibCode)) {
                msg = getString(R.string.this_discuss_deleted);
            } else {
                msg = getString(R.string.this_message_deleted);
            }
        }
        final CustomDialog dialog = new CustomDialog(InformationCommentDetailsActivity.this, R.style.DialogTheme, msg);
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                if (finish) {
                    sendDelOwnMsg();
                } else {
                    mIsChangeCommentData = true;

                    int position = findPositionByMsgId(replyId);
                    if (position > -1 && position < mAdapter.getCount()) {
                        mAdapter.remove(position);
                        int totalCount = mAdapter.getItem(0).mReplyCount;
                        mAdapter.getItem(0).mReplyCount = totalCount < 1 ? 1 : totalCount - 1;
                        mAdapter.notifyDataSetChanged();
                    }
                    resetUserReplyPosition();
                    KeyboardUtils.hideSoftInput(mCommentPublishContentEt);
                }
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });

    }

    /**
     * 发送删除自己评论的消息
     */
    private void sendDelOwnMsg() {
        Intent intent = new Intent();
        intent.putExtra(AppIntentGlobalName.COMMENT_MSG_ID, mCommentId);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void showCommentView() {
        mCommentPublishDefaultRl.setVisibility(View.VISIBLE);
    }

    /**
     * 恢复评论位置
     */
    public void resetUserReplyPosition() {
        mCommentPublishContentEt.setText("");
        mIsHeadComment = true;
    }

    @Override
    public void finish() {
        checkCommentData();
        super.finish();
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        //登录成功，重新获取数据
//        if (requestCode == TO_LOGIN_REQUEST_CODE && resultCode == RESULT_OK) {
//            mPresenter.getCommentDetailList(mFromType, mCommentId, mReplyId, 1);
//        }
//    }

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
        EventBus.getDefault().unregister(this);
        mAdapter.clear();
        mPresenter.detachView();
    }
}
