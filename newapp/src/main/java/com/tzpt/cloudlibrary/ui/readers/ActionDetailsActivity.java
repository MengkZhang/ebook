package com.tzpt.cloudlibrary.ui.readers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.tzpt.cloudlibrary.AppIntentGlobalName;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.bean.ActionInfoBean;
import com.tzpt.cloudlibrary.bean.ShareBean;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.ui.account.LoginActivity;
import com.tzpt.cloudlibrary.ui.share.ShareActivity;
import com.tzpt.cloudlibrary.utils.ToastUtils;
import com.tzpt.cloudlibrary.widget.CustomDialog;
import com.tzpt.cloudlibrary.widget.CustomWebView;
import com.tzpt.cloudlibrary.widget.multistatelayout.MultiStateLayout;
import com.tzpt.cloudlibrary.widget.pullrefreshlayout.SmartRefreshLayout;
import com.tzpt.cloudlibrary.widget.pullrefreshlayout.footer.ActivityFooter;
import com.tzpt.cloudlibrary.widget.pullrefreshlayout.header.ActivityHeader;
import com.tzpt.cloudlibrary.widget.pullrefreshlayout.listener.OnRefreshLoadMoreListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 读友会展示界面
 */
public class ActionDetailsActivity extends BaseActivity implements ActionDetailContract.View {
    private static final String ACTION_KEY_WORD = "action_key_word";
    private static final String ACTION_LIB_CODE = "action_lib_code";
    private static final String ACTION_LIST_POSITION = "action_list_position";
    private static final String ACTION_LIST_TOTAL = "action_list_total";
    private static final String ACTION_LIST_PAGE_NUM = "action_list_page_num";
    private static final String ACTION_LIST_TYPE = "action_list_type";
    private static final String ACTION_ID = "action_id";
    private static final String ACTION_FROM_SEARCH = "action_from_search";

    public static void startActivity(ActivityListFragment fragment, int type, int position, int pageNum, int allTotal,
                                     String keyWord, String libCode, int fromSearch, int requestCode) {
        Intent intent = new Intent(fragment.getSupportActivity(), ActionDetailsActivity.class);
        intent.putExtra(ACTION_LIST_POSITION, position);
        intent.putExtra(ACTION_LIST_TOTAL, allTotal);
        intent.putExtra(ACTION_KEY_WORD, keyWord);
        intent.putExtra(ACTION_LIB_CODE, libCode);
        intent.putExtra(ACTION_LIST_PAGE_NUM, pageNum);
        intent.putExtra(ACTION_LIST_TYPE, type);
        intent.putExtra(ACTION_FROM_SEARCH, fromSearch);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void startActivityById(Context context, int actionId) {
        Intent intent = new Intent(context, ActionDetailsActivity.class);
        intent.putExtra(ACTION_ID, actionId);
        context.startActivity(intent);
    }

    public static void startActivityFromHotSearch(Context context, int actionId) {
        Intent intent = new Intent(context, ActionDetailsActivity.class);
        intent.putExtra(ACTION_ID, actionId);
        intent.putExtra(ACTION_FROM_SEARCH, 1);
        context.startActivity(intent);
    }

    @BindView(R.id.activity_header)
    ActivityHeader mActivityHeader;
    @BindView(R.id.activity_footer)
    ActivityFooter mActivityFooter;
    @BindView(R.id.custom_webview)
    CustomWebView mCustomWebView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;
    @BindView(R.id.multi_state_layout)
    MultiStateLayout mMultiStateLayout;
    @BindView(R.id.to_sign_up_tv)
    TextView mToSignUpTv;

    private ActionDetailPresenter mPresenter;
    private int mType;
    private int mPosition;
    private int mCurrentCount;
    private int mPageNum;
    private int mActionListTotalCount;
    private int mActionId;
    private String mLibCode;
    private String mKeyWord;
    private int mFromSearch;

    private ActionInfoBean mActionInfo;

    private boolean mSizeChange;
    private ArrayList<ActionInfoBean> mActionList;

    @OnClick({R.id.titlebar_left_btn, R.id.titlebar_right_btn, R.id.to_sign_up_tv})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.titlebar_right_btn:
                String content;
                if (TextUtils.isEmpty(mActionInfo.mSummary)) {
                    content = getString(R.string.no_summary);
                } else {
                    content = mActionInfo.mSummary;
                }
                ShareBean shareBean = new ShareBean();
                shareBean.shareTitle = mActionInfo.mTitle;
                shareBean.shareContent = content;
                shareBean.shareUrl = mActionInfo.mUrl;
                shareBean.shareUrlForWX = mActionInfo.mUrl;
                shareBean.shareImagePath = "http://img.ytsg.cn/images/htmlPage/ic_logo.png";
                shareBean.mNeedCopy = true;
                ShareActivity.startActivity(this, shareBean);
                break;
            case R.id.to_sign_up_tv:
                mPresenter.toSignUp(mActionId);
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_action_detail;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setTitle("活动详情");
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setRightBtnIcon(R.drawable.bg_btn_share);
        mCommonTitleBar.setRightBtnClickAble(false);
    }

    @Override
    public void initDatas() {
        mActionId = getIntent().getIntExtra(ACTION_ID, -1);
        mType = getIntent().getIntExtra(ACTION_LIST_TYPE, 0);
        mPosition = getIntent().getIntExtra(ACTION_LIST_POSITION, 0);
        mActionListTotalCount = getIntent().getIntExtra(ACTION_LIST_TOTAL, 0);
        mPageNum = getIntent().getIntExtra(ACTION_LIST_PAGE_NUM, 1);
        mLibCode = getIntent().getStringExtra(ACTION_LIB_CODE);
        mKeyWord = getIntent().getStringExtra(ACTION_KEY_WORD);
        mFromSearch = getIntent().getIntExtra(ACTION_FROM_SEARCH, 0);

        EventBus.getDefault().register(this);

        mPresenter = new ActionDetailPresenter();
        mPresenter.attachView(this);
        if (mActionId == -1) {
            mPresenter.getActionId(mPosition);
        } else {
            mPresenter.getActionDetailInfo(mActionId, mFromSearch, null, null);
            mSmartRefreshLayout.setEnableRefresh(false);
            mSmartRefreshLayout.setEnableLoadMore(false);
        }

    }

    @Override
    public void configViews() {
        mCustomWebView.setScrollAble(true);
        mCustomWebView.setLoadingListener(mLoadingListener);
        mSmartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {

            @Override
            public void onRefresh(@NonNull final com.tzpt.cloudlibrary.widget.pullrefreshlayout.api.RefreshLayout refreshLayout) {
                mSmartRefreshLayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.finishLoadMore(false);//恢复上拉状态
                        if (mPosition == 0) {
                            refreshLayout.finishRefresh(true);
                        } else {
                            mCommonTitleBar.setRightBtnClickAble(false);

                            mPosition--;
                            mPresenter.getActionId(mPosition);
                            refreshLayout.finishRefresh(false);
                        }
                        //call fresh -通知加载上一篇
                    }
                }, 500);
            }

            @Override
            public void onLoadMore(@NonNull final com.tzpt.cloudlibrary.widget.pullrefreshlayout.api.RefreshLayout refreshlayout) {
                mSmartRefreshLayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mPosition >= mActionListTotalCount - 1) {
                            refreshlayout.finishLoadMore(true);
                        } else {
                            refreshlayout.finishLoadMore(false);
                            if (mPosition >= mCurrentCount - 1) {
                                mPresenter.getActionList(mType, mPageNum + 1);
                            } else {
                                mCommonTitleBar.setRightBtnClickAble(false);

                                mPosition++;
                                mPresenter.getActionId(mPosition);
                            }
                        }
                        //call loadMore- 通知加载下一篇
                    }
                }, 500);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != mCustomWebView) {
            mCustomWebView.onResume();
        }
        UmengHelper.setUmengResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != mCustomWebView) {
            mCustomWebView.onPause();
        }
        UmengHelper.setUmengPause(this);
    }

    @Override
    public void finish() {
        if (mSizeChange) {
            Intent data = new Intent();
            data.putExtra(AppIntentGlobalName.ACTION_LIST_PAGE_NUM, mPageNum);
            data.putParcelableArrayListExtra(AppIntentGlobalName.ACTION_LIST, mActionList);
            setResult(RESULT_OK, data);
        }
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mCustomWebView) {
            mCustomWebView.destroyWebView();
            mCustomWebView = null;
        }
        if (null != mHandler) {
            mHandler.removeMessages(100);
        }
        if (mPresenter != null) {
            mPresenter.detachView();
            mPresenter = null;
        }
        //取消事件订阅
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void setActionId(int actionId, int currentCount) {
        mActionId = actionId;
        mCurrentCount = currentCount;
        mPresenter.getActionDetailInfo(mActionId, mFromSearch, mKeyWord, mLibCode);
    }

    @Override
    public void getActionListSuccess(List<ActionInfoBean> actionList) {
        mSizeChange = true;
        mPosition++;
        mPageNum++;
        mPresenter.getActionId(mPosition);

        if (mActionList == null) {
            mActionList = new ArrayList<>();
        }
        mActionList.addAll(actionList);
    }

    @Override
    public void showLoadingStatus() {
        mMultiStateLayout.showProgress();
    }

    @Override
    public void setActionDetailInfo(ActionInfoBean info) {
        mActionInfo = info;
        mToSignUpTv.setVisibility(View.GONE);
        mCustomWebView.loadUrl(info.mUrl);

        //判断是否第一页
        if (mPosition == 0) {
            mActivityHeader.setAlreadyNoOnePage(true);
        } else {
            mActivityHeader.setAlreadyNoOnePage(false);
        }
        //判断是否最后一页
        if (mPosition >= mActionListTotalCount - 1) {
            mActivityFooter.setNoMoreDataTag(true);
        } else {
            mActivityFooter.setNoMoreDataTag(false);
        }
    }

    @Override
    public void loadActionDetailFailed() {
        if (null != mMultiStateLayout) {
            mMultiStateLayout.showError();
            mMultiStateLayout.showRetryError(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPresenter != null) {
                        mPresenter.getActionDetailInfo(mActionId, mFromSearch, mKeyWord, mLibCode);
                    }
                }
            });
        }
    }

    @Override
    public void applyActionSuccess() {
        mToSignUpTv.setText("已报名");
        mToSignUpTv.setClickable(false);
        mToSignUpTv.setTextColor(Color.parseColor("#999999"));
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
    public void pleaseLogin() {
        LoginActivity.startActivity(this, mActionId);
    }

    @Override
    public void pleaseLoginTip() {
        AccountMessage accountMessage = new AccountMessage();
        accountMessage.mIsLoginOut = true;
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
                LoginActivity.startActivity(ActionDetailsActivity.this);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }


    private CustomWebView.CallbackWebViewLoading mLoadingListener = new CustomWebView.CallbackWebViewLoading() {
        @Override
        public void onPageStarted() {
            if (null != mCustomWebView) {
                mMultiStateLayout.showProgress();
                mCustomWebView.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPageFinished(boolean hasContent) {
            if (!hasContent) {
                onPageLoadingError();
                return;
            }
            if (null != mHandler) {
                mHandler.sendEmptyMessageDelayed(100, 500);
            }

        }

        @Override
        public void onPageLoadingError() {
            if (null != mCustomWebView) {
                //mCustomWebView.loadUrl("about:blank");
                mCustomWebView.setVisibility(View.GONE);
            }
            loadActionDetailFailed();
        }

    };

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                if (null != mMultiStateLayout) {
                    mCommonTitleBar.setRightBtnClickAble(true);
                    mMultiStateLayout.showContentView();

                    //(0,"可报名"), (1,"已报名"), (2,"报名截止"),(3,"活动结束"),(4,"活动不可以报名"),(5,"报名已满");
                    if (mActionInfo.mApplyStatus == 4) {
                        mToSignUpTv.setVisibility(View.GONE);
                    } else {
                        mToSignUpTv.setVisibility(View.VISIBLE);
                        switch (mActionInfo.mApplyStatus) {
                            case 0:
                                mToSignUpTv.setText("我要报名");
                                mToSignUpTv.setClickable(true);
                                mToSignUpTv.setTextColor(Color.parseColor("#9e724d"));
                                break;
                            case 1:
                                mToSignUpTv.setText("已报名");
                                mToSignUpTv.setClickable(false);
                                mToSignUpTv.setTextColor(Color.parseColor("#999999"));
                                break;
                            case 2:
                                mToSignUpTv.setText("报名截止");
                                mToSignUpTv.setClickable(false);
                                mToSignUpTv.setTextColor(Color.parseColor("#999999"));
                                break;
                            case 3:
                                mToSignUpTv.setText("已结束");
                                mToSignUpTv.setClickable(false);
                                mToSignUpTv.setTextColor(Color.parseColor("#999999"));
                                break;
                            case 5:
                                mToSignUpTv.setText("报名已满");
                                mToSignUpTv.setClickable(false);
                                mToSignUpTv.setTextColor(Color.parseColor("#999999"));
                                break;
                        }
                    }
                }
                if (null != mCustomWebView) {
                    mCustomWebView.setVisibility(View.VISIBLE);
                    mCustomWebView.scrollTo(0, 0);
                }
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receviceActionApplied(ApplyActionMessage applyMessage) {
        if (applyMessage != null && !TextUtils.isEmpty(applyMessage.mActionStatus) && mToSignUpTv != null) {
            mToSignUpTv.setText(applyMessage.mActionStatus);
            mToSignUpTv.setTextColor(Color.parseColor("#999999"));
        }
    }
}
