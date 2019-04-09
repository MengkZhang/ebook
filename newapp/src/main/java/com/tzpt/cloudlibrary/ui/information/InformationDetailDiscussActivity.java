package com.tzpt.cloudlibrary.ui.information;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tzpt.cloudlibrary.AppIntentGlobalName;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.bean.CommentBean;
import com.tzpt.cloudlibrary.bean.CommentMsgBean;
import com.tzpt.cloudlibrary.bean.InformationBean;
import com.tzpt.cloudlibrary.bean.NetWordMsg;
import com.tzpt.cloudlibrary.bean.ShareBean;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.ui.account.LoginActivity;
import com.tzpt.cloudlibrary.ui.account.interaction.NormalMsgFragment;
import com.tzpt.cloudlibrary.ui.share.ShareActivity;
import com.tzpt.cloudlibrary.utils.CheckInternetUtil;
import com.tzpt.cloudlibrary.utils.DisplayUtils;
import com.tzpt.cloudlibrary.utils.DpPxUtils;
import com.tzpt.cloudlibrary.utils.KeyboardUtils;
import com.tzpt.cloudlibrary.utils.StatusBarUtil;
import com.tzpt.cloudlibrary.utils.StringUtils;
import com.tzpt.cloudlibrary.utils.ToastUtils;
import com.tzpt.cloudlibrary.widget.CustomDialog;
import com.tzpt.cloudlibrary.widget.CustomLoadingDialog;
import com.tzpt.cloudlibrary.widget.CustomWebView;
import com.tzpt.cloudlibrary.widget.InformationWebView;
import com.tzpt.cloudlibrary.widget.multistatelayout.MultiStateLayout;
import com.tzpt.cloudlibrary.widget.recyclerview.EasyRecyclerView;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.OnLoadMoreListener;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;
import com.tzpt.cloudlibrary.widget.recyclerview.swipe.OnRefreshListener;
import com.tzpt.cloudlibrary.widget.titlebar.TitleBarView;
import com.tzpt.cloudlibrary.widget.video.CLVideoView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.tzpt.cloudlibrary.AppIntentGlobalName.COMMENT_MSG_ID;

/**
 * 资讯详情展示界面含有评论
 */
public class InformationDetailDiscussActivity extends AppCompatActivity implements
        DisplayInformationDiscussContract.View,
        OnLoadMoreListener,
        OnRefreshListener,
        RecyclerArrayAdapter.OnItemClickListener {

    private static final int SHOW_WEB_VIEW = 1000;
    private static final int SHOW_VIDEO_VIEW = 1001;
    private static final int TO_DETAIL_REQUEST_CODE = 1002;

    private static final String INFO_POSITION = "current_position";
    private static final String INFO_CURRENT_PAGE = "current_page";
    private static final String LIBRARY_CODE = "library_code";
    private static final String INFO_KEYWORD = "info_keyword";
    private static final String INFO_SOURCE = "info_source";
    private static final String INFO_SEARCH_TITLE = "info_search_title";
    private static final String INFO_CATEGORY_ID = "info_category_id";
    private static final String INFO_SEARCH_INDUSTRY_ID = "info_search_industry_id";
    private static final String INFO_ALL_TOTAL = "info_all_total";
    private static final String INFO_ID = "info_id";
    private static final String INFO_FROM_SEARCH = "info_from_search";
    private static final String INFO_COMMENT_ID = "info_comment_id";

    public static void startActivityForResult(InformationFragment fragment, int position, int currentPage, int totalCount,
                                              String keyword, String source, String title, String cId,
                                              String industryId, String libCode, int fromSearch, int requestCode) {
        Intent intent = new Intent(fragment.getSupportActivity(), InformationDetailDiscussActivity.class);
        intent.putExtra(INFO_POSITION, position);
        intent.putExtra(INFO_CURRENT_PAGE, currentPage);
        intent.putExtra(LIBRARY_CODE, libCode);
        intent.putExtra(INFO_KEYWORD, keyword);
        intent.putExtra(INFO_SOURCE, source);
        intent.putExtra(INFO_SEARCH_TITLE, title);
        intent.putExtra(INFO_CATEGORY_ID, cId);
        intent.putExtra(INFO_SEARCH_INDUSTRY_ID, industryId);
        intent.putExtra(INFO_ALL_TOTAL, totalCount);
        intent.putExtra(INFO_FROM_SEARCH, fromSearch);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void startActivity(Context context, long infoId) {
        Intent intent = new Intent(context, InformationDetailDiscussActivity.class);
        intent.putExtra(INFO_ID, infoId);
        context.startActivity(intent);
    }

    public static void startActivityForFragmentResult(NormalMsgFragment fragment, long infoId, long commentId, int requestCode) {
        Intent intent = new Intent(fragment.getSupportActivity(), InformationDetailDiscussActivity.class);
        intent.putExtra(INFO_ID, infoId);
        intent.putExtra(INFO_COMMENT_ID, commentId);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void startActivityFromHotSearch(Context context, long infoId) {
        Intent intent = new Intent(context, InformationDetailDiscussActivity.class);
        intent.putExtra(INFO_ID, infoId);
        intent.putExtra(INFO_FROM_SEARCH, 1);
        context.startActivity(intent);
    }

    @BindView(R.id.status_bar_v)
    View mStatusBarV;
    @BindView(R.id.common_toolbar)
    TitleBarView mCommonTitleBar;
    @BindView(R.id.multi_state_layout)
    MultiStateLayout mMultiStateLayout;
    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;
    @BindView(R.id.discuss_discuss_list_num_tv)
    TextView mCountTv;
    @BindView(R.id.comment_publish_ll)
    LinearLayout mCommentPublishLl;
    @BindView(R.id.comment_publish_content_et)
    EditText mContentEt;
    @BindView(R.id.comment_publish_send_btn)
    Button mCommentSendBtn;

    private Unbinder unbinder;
    protected RecyclerArrayAdapter<CommentBean> mAdapter;
    private CustomWebView mCustomWebView;
    private TextView mReadCountTv;
    private TextView mPraiseCountTv;
    private DisplayInformationDiscussPresenter mPresenter;
    private InformationWebView mHeaderWebView;

    private int mPosition;
    private int mCurrentPageNum;
    private int mCurrentTotal;
    private int mAllTotal;

    private int mCurrentDiscussPage;

    private String mSearchKeyWord;
    private String mSearchLibCode;
    private String mSearchSource;
    private String mSearchCategoryId;
    private String mSearchTitle;
    private String mSearchIndustryId;
    private long mNewsId;
    private int mFromSearch;

    private String mTitle;
    private String mShareContent;
    private String mShareUrl;
    private long mCommentId;
    private long mMsgId = -1;

    private boolean mIsDiscussInformation = true; //是否评论资讯
    private int mRealPosition = -1;
    private int mDiscussTotalCount;

    private boolean mIsToShare = false;
    private boolean mNeedPublishPosition = false;
    private boolean mSizeChange;
    private ArrayList<InformationBean> mInformationList;

    @OnClick({R.id.titlebar_left_btn, R.id.titlebar_right_btn, R.id.discuss_comment_content_tv,
            R.id.discuss_discuss_list_btn, R.id.comment_publish_send_btn, R.id.comment_publish_cover_view})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                KeyboardUtils.hideSoftInput(this);
                finish();
                break;
            case R.id.titlebar_right_btn:
                mIsToShare = true;
                ShareBean shareBean = new ShareBean();
                shareBean.shareTitle = mTitle;
                shareBean.shareContent = mShareContent;
                shareBean.shareUrl = mShareUrl;
                shareBean.shareUrlForWX = mShareUrl;
                shareBean.shareImagePath = "http://img.ytsg.cn/images/htmlPage/ic_logo.png";
                shareBean.mNeedCopy = true;
                ShareActivity.startActivity(this, shareBean);
                break;
            case R.id.discuss_comment_content_tv:
                if (!mPresenter.isLogin()) {
                    LoginActivity.startActivity(this);
                } else {
                    mContentEt.setHint("写评论");
                    mContentEt.requestFocus();
                    mCommentPublishLl.setVisibility(View.VISIBLE);
                    KeyboardUtils.showSoftInput(this);
                }
                break;
            case R.id.discuss_discuss_list_btn:
                scrollToPosition();
                break;
            case R.id.comment_publish_send_btn:
                String content = mContentEt.getText().toString().trim();
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
                        if (mIsDiscussInformation) {
                            mPresenter.publishDiscuss(mNewsId, content);
                        } else {
                            mPresenter.replyReaderMsg(mNewsId, mCommentId, content);
                        }
                    }
                }
                break;
            case R.id.comment_publish_cover_view:
                KeyboardUtils.hideSoftInput(this);
                mCommentPublishLl.setVisibility(View.GONE);
                break;
        }
    }

    private InformationReaderDiscussAdapter.ReaderDiscussListener mReaderDiscussListener =
            new InformationReaderDiscussAdapter.ReaderDiscussListener() {
                @Override
                public void replyReaderMsg(long commentId, String readerName, int position) {
                    if (!mPresenter.isLogin()) {
                        LoginActivity.startActivity(InformationDetailDiscussActivity.this);
                        return;
                    }
                    if (mCommentId != commentId) {
                        mContentEt.setText("");
                    }

                    mCommentId = commentId;
                    mIsDiscussInformation = false;

                    mCommentPublishLl.setVisibility(View.VISIBLE);
                    mContentEt.requestFocus();
                    mContentEt.setHint("回复" + readerName);
                    KeyboardUtils.showSoftInput(InformationDetailDiscussActivity.this);
                }

                @Override
                public void delOwnMsg(final long commentId, final int position) {
                    final CustomDialog dialog = new CustomDialog(InformationDetailDiscussActivity.this, R.style.DialogTheme, "确定删除？");
                    dialog.setCancelable(false);
                    dialog.hasNoCancel(true);
                    dialog.setOkText(getString(R.string.delete));
                    dialog.setCancelText(getString(R.string.cancel));
                    dialog.show();
                    dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
                        @Override
                        public void onClickOk() {
                            dialog.dismiss();
                            int realPosition = position - 1;
                            mPresenter.delOwnReaderMsg(commentId, realPosition < 0 ? 0 : realPosition);
                            //清空回复内容
                            mContentEt.setText("");
                        }

                        @Override
                        public void onClickCancel() {
                            dialog.dismiss();
                        }
                    });

                }

                @Override
                public void praiseMsg(long commentId, int position, boolean isPraised, int praisedCount) {
                    int discussIndex = position - 1;
                    mPresenter.praiseReaderMsg(commentId, discussIndex < 0 ? 0 : discussIndex, praisedCount);
                }

                @Override
                public void toMoreReplyDetail(long commentId, int position) {
                    hideVideoPlayView();
                    InformationCommentDetailsActivity.startActivityForResult(InformationDetailDiscussActivity.this, commentId, -1, null, TO_DETAIL_REQUEST_CODE);
                    //清空回复内容
                    mContentEt.setText("");
                }
            };
    private CustomWebView.CallbackWebViewLoading mLoadingListener = new CustomWebView.CallbackWebViewLoading() {
        @Override
        public void onPageStarted() {
            if (null != mCustomWebView) {
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
                mHandler.sendEmptyMessageDelayed(SHOW_WEB_VIEW, 500);
            }
        }

        @Override
        public void onPageLoadingError() {
            if (null != mCustomWebView) {
                mCustomWebView.loadUrl("about:blank");
                mCustomWebView.setVisibility(View.GONE);
            }
            if (null != mMultiStateLayout) {
                showDetailLoadError();
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_WEB_VIEW://展示网页信息
                    if (null != mMultiStateLayout) {
                        mCommonTitleBar.setRightBtnClickAble(true);
                        mMultiStateLayout.showContentView();
                        mRecyclerView.scrollToPosition(0);
                        mPresenter.getDiscussList(mMsgId, 1, mNewsId);
                    }
                    if (null != mCustomWebView) {
                        mCustomWebView.setVisibility(View.VISIBLE);
                    }
                    break;
                case SHOW_VIDEO_VIEW://展示视频控件
                    String videoUrl = (String) msg.obj;
                    if (!TextUtils.isEmpty(videoUrl)) {
                        initVideoView(videoUrl);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_web_details);

        unbinder = ButterKnife.bind(this);

        initToolBar();
        initDatas();
        configViews();
    }

    private void initToolBar() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            //针对5.0-5.1状态设置为半透明主题颜色，在白色背景下可以看清楚状态栏文字
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
                StatusBarUtil.setStatusBarColor(this, R.color.color_half000000);
            } else {
                StatusBarUtil.setStatusBarColor(this, R.color.color_ffffff);
            }
        } else {
            StatusBarUtil.setStatusBarColor(this, R.color.color_translate);
        }
        mCommonTitleBar.setTitle("资讯详情");
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setRightBtnClickAble(false);
        mCommonTitleBar.setRightBtnIcon(R.drawable.bg_btn_share);
    }

    private void initDatas() {
        mPresenter = new DisplayInformationDiscussPresenter();
        mPresenter.attachView(this);
        EventBus.getDefault().register(this);
        requestData();
    }

    private void requestData() {
        Intent intent = getIntent();
        mNewsId = intent.getLongExtra(INFO_ID, -1);
        mPosition = intent.getIntExtra(INFO_POSITION, -1);
        mFromSearch = intent.getIntExtra(INFO_FROM_SEARCH, 0);
        if (mNewsId == -1) {
            mCurrentPageNum = intent.getIntExtra(INFO_CURRENT_PAGE, 1);
            mSearchKeyWord = intent.getStringExtra(INFO_KEYWORD);
            mSearchLibCode = intent.getStringExtra(LIBRARY_CODE);
            mSearchSource = intent.getStringExtra(INFO_SOURCE);
            mSearchCategoryId = intent.getStringExtra(INFO_CATEGORY_ID);
            mSearchTitle = intent.getStringExtra(INFO_SEARCH_TITLE);
            mSearchIndustryId = intent.getStringExtra(INFO_SEARCH_INDUSTRY_ID);
            mAllTotal = intent.getIntExtra(INFO_ALL_TOTAL, 0);
            mPresenter.getInfoId(mPosition);
        } else {
            mMsgId = intent.getLongExtra(INFO_COMMENT_ID, -1);
            mPresenter.getInformationDetail(mNewsId, mFromSearch);
        }
    }

    private void configViews() {
        //获取标题栏状态栏高度
        int statusBarHeight = DisplayUtils.getStatusBarHeight(this);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mStatusBarV.getLayoutParams();
        layoutParams.height = statusBarHeight;
        mStatusBarV.setLayoutParams(layoutParams);
        setScreenFull(false);
        initDiscussAdapter();
        mCustomWebView.setLoadingListener(mLoadingListener);

    }

    private void initDiscussAdapter() {
        mAdapter = new InformationReaderDiscussAdapter(this, mReaderDiscussListener);
        initAdapter(false, true);

        mHeaderWebView = new InformationWebView(this);
        mCustomWebView = mHeaderWebView.getCustomWebView();
        mReadCountTv = mHeaderWebView.getReadCountTv();
        mPraiseCountTv = mHeaderWebView.getPraiseCountTv();
        if (mPosition == -1) {
            mHeaderWebView.setPreviousTvVisibility(View.GONE);
            mHeaderWebView.setNextTvVisibility(View.GONE);
        } else {
            mHeaderWebView.setPreviousTvVisibility(View.VISIBLE);
            mHeaderWebView.setNextTvVisibility(View.VISIBLE);
        }
        mHeaderWebView.setNextTvOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideVideoPlayView();
                loadNextPage();
            }
        });
        mHeaderWebView.setPreviousTvOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideVideoPlayView();
                loadPreviousPage();
            }
        });

        mAdapter.addHeader(mHeaderWebView);
        if (mRecyclerView != null) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerView.setAdapterWithProgress(mAdapter);
            mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (mRecyclerView != null && mRecyclerView.getRecyclerView() != null && mRecyclerView.getRecyclerView().getLayoutManager() != null) {
                        getPositionAndOffset();
                    }
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && mAdapter.getCount() > 0) {
                        if (mRealPosition != -1) {
                            mAdapter.getItem(mRealPosition).mIsLight = false;
                            mAdapter.notifyItemChanged(mRealPosition + 1);
                            mRealPosition = -1;
                        }
                    }
                }
            });
        }
    }

    protected void initAdapter(boolean refreshable, boolean loadmoreable) {

        if (mAdapter != null) {
            mAdapter.setOnItemClickListener(this);
            mAdapter.setError(R.layout.common_rv_error_view).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAdapter.resumeMore();
                }
            });
            if (loadmoreable) {
                mAdapter.setMore(R.layout.common_rv_more_view, this);
                mAdapter.setNoMore(R.layout.common_rv_nomore_view);
            }
            if (refreshable && mRecyclerView != null) {
                mRecyclerView.setRefreshListener(this);
            }
        }

        if (mRecyclerView != null) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerView.setAdapterWithProgress(mAdapter);
        }
    }

    private boolean mIsScrolled;

    /**
     * 记录RecyclerView当前位置
     */
    private void getPositionAndOffset() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getRecyclerView().getLayoutManager();
        //获取可视的第一个view
        View topView = layoutManager.getChildAt(0);
        if (topView != null) {
            //得到该View的数组位置
            int lastPosition = layoutManager.getPosition(topView);
            mIsScrolled = lastPosition > 0;
        }
    }

    private void scrollToPosition() {
        if (mIsScrolled) {
            ((LinearLayoutManager) mRecyclerView.getRecyclerView().getLayoutManager()).scrollToPositionWithOffset(0, 0);
        } else {
            ((LinearLayoutManager) mRecyclerView.getRecyclerView().getLayoutManager()).scrollToPositionWithOffset(1, 0);
        }
        mIsScrolled = !mIsScrolled;
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {
        mPresenter.getDiscussList(mMsgId, mCurrentDiscussPage + 1, mNewsId);
    }

    @Override
    public void onItemClick(int position) {

    }


    @SuppressLint("AddJavascriptInterface")
    @Override
    public void setInformationDetail(InformationBean information) {
        mTitle = information.mTitle;
        if (TextUtils.isEmpty(information.mSummary)) {
            mShareContent = getString(R.string.no_summary);
        } else {
            mShareContent = information.mSummary;
        }

        mShareUrl = information.mShareUrl;
        mReadCountTv.setText(getString(R.string.read_count, information.mReadCount));
        mPraiseCountTv.setText(String.valueOf(information.mPraiseCount));

        mCustomWebView.loadWebUrl(information.mUrl);
        mCustomWebView.addJavascriptInterface(new JsInterface(), "informationVideoUrlListener");

        if (mAllTotal <= 1) {
            mHeaderWebView.setPreviousTvVisibility(View.GONE);
            mHeaderWebView.setNextTvVisibility(View.GONE);
        } else {
            if (mPosition == 0) {
                mHeaderWebView.setPreviousTvTxt("已是第一篇");
                mHeaderWebView.showPreviousIcon(false);
                mHeaderWebView.setPreviousTvClickable(false);
            } else {
                mHeaderWebView.setPreviousTvTxt("上一篇");
                mHeaderWebView.showPreviousIcon(true);
                mHeaderWebView.setPreviousTvClickable(true);
            }

            if (mPosition == mAllTotal - 1) {
                mHeaderWebView.setNextTvTxt("已是最后一篇");
                mHeaderWebView.showNextIcon(false);
                mHeaderWebView.setNextTvClickable(false);
            } else {
                mHeaderWebView.setNextTvTxt("下一篇");
                mHeaderWebView.showNextIcon(true);
                mHeaderWebView.setNextTvClickable(true);
            }
        }
    }

    public class JsInterface {

        @JavascriptInterface
        public void openInformationVideoUrl(final String videoUrl) {
            Message msg = new Message();
            msg.obj = videoUrl;
            msg.what = SHOW_VIDEO_VIEW;
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public void showDetailLoadProgress() {
        mMultiStateLayout.showProgress();
//        mCustomDiscussView.setDiscussNumber(0);//加载网页时，不显示评论数量
        setCommentCount(0);
    }

    @Override
    public void showDetailLoadError() {
        if (mMultiStateLayout != null) {
            mMultiStateLayout.showRetryError(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestData();
                }
            });
        }
    }

    @Override
    public void setDiscussList(List<CommentBean> discussList, int totalCount, boolean refresh) {
        mDiscussTotalCount = totalCount;
        setCommentCount(totalCount);
        if (refresh) {
            mCurrentDiscussPage = 1;
            mAdapter.clear();
        } else {
            mCurrentDiscussPage = mCurrentDiscussPage + 1;
        }
        mAdapter.addAll(discussList);
        //mAdapter.notifyDataSetChanged();
        mCommonTitleBar.setRightBtnClickAble(true);
        if (mAdapter.getCount() >= totalCount) {
            mAdapter.setNoMore(R.layout.common_rv_nomore_view);
            mAdapter.stopMore();
        }

        if (refresh && mNeedPublishPosition) {
            mNeedPublishPosition = false;
            ((LinearLayoutManager) mRecyclerView.getRecyclerView().getLayoutManager()).scrollToPosition(1);
        }
    }

    @Override
    public void setDiscussEmpty(boolean refresh) {
        if (refresh) {
            mAdapter.clear();
            mAdapter.setNoMore(R.layout.common_rv_empty_discuss_view);
            mAdapter.stopMore();
        } else {
            mAdapter.stopMore();
        }
    }

    @Override
    public void setDiscussError() {
        mAdapter.pauseMore();
    }

    @Override
    public void setNewsIdCurrentCount(long newsId, int currentCount) {
        mCurrentTotal = currentCount;
        mNewsId = newsId;
        mPresenter.getInformationDetail(mNewsId, mFromSearch);
    }

    @Override
    public void setLoadInfoListSuccess(List<InformationBean> beanList) {
        mSizeChange = true;
        mPosition++;
        mCurrentPageNum++;
        mPresenter.getInfoId(mPosition);

        //设置资讯列表数据
        if (mInformationList == null) {
            mInformationList = new ArrayList<>();
        }
        mInformationList.addAll(beanList);
    }

    @Override
    public void showToastMsg(int resId) {
        ToastUtils.showSingleToast(resId);
    }

    @Override
    public void showDialogMsg(int resId) {
        final CustomDialog dialog = new CustomDialog(this, R.style.DialogTheme, getString(resId));
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
    public void setPraiseBtnStatus(boolean isPraised) {
        if (isPraised) {
            Drawable left = getResources().getDrawable(R.mipmap.ic_discuss_list_praise_done);
            mPraiseCountTv.setCompoundDrawablesWithIntrinsicBounds(left, null, null, null);
            mPraiseCountTv.setTextColor(getResources().getColor(R.color.color_aa7243));
            mPraiseCountTv.setOnClickListener(null);

        } else {
            Drawable left = getResources().getDrawable(R.mipmap.ic_discuss_list_praise_no);
            mPraiseCountTv.setCompoundDrawablesWithIntrinsicBounds(left, null, null, null);
            mPraiseCountTv.setTextColor(getResources().getColor(R.color.color_999999));
            mPraiseCountTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.operateNewsPraise(mNewsId, true);
                }
            });
        }
    }

    @Override
    public void changePraiseCount(boolean isPraise) {
        try {
            int count = Integer.valueOf(mPraiseCountTv.getText().toString());
            if (isPraise) {
                mPraiseCountTv.setText(String.valueOf(count + 1));
            } else {
                if (count > 1) {
                    mPraiseCountTv.setText(String.valueOf(count - 1));
                } else {
                    mPraiseCountTv.setText("0");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void pleaseLoginTip() {
        AccountMessage accountMessage = new AccountMessage();
        accountMessage.mIsLoginOut = true;
        EventBus.getDefault().post(accountMessage);

        mPresenter.getDiscussList(mMsgId, 1, mNewsId);
        final CustomDialog dialog = new CustomDialog(this, R.style.DialogTheme, getString(R.string.account_login_other_device));
        dialog.setCancelable(false);
        dialog.hasNoCancel(true);
        dialog.setLoginAnewBtn();
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                LoginActivity.startActivity(InformationDetailDiscussActivity.this);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void setSendDiscussBtnStatus(boolean canSend) {
        mCommentSendBtn.setClickable(canSend);
    }

    @Override
    public void publishDiscussSuccess() {
        ToastUtils.showSingleToast(R.string.publish_discuss_success);
        dismissPublishComment();
        this.mNeedPublishPosition = true;
    }

    CustomLoadingDialog mCustomLoadingDialog;

    @Override
    public void showMsgProgressDialog(String msg) {
        KeyboardUtils.hideSoftInput(this);
        if (null != mCustomLoadingDialog && mCustomLoadingDialog.isShowing()) {
            return;
        }
        mCustomLoadingDialog = CustomLoadingDialog.instance(this, msg);
        mCustomLoadingDialog.setCancelable(false);
        mCustomLoadingDialog.show();
    }

    @Override
    public void dismissMsgProgressDialog() {
        if (mCustomLoadingDialog != null) {
            mCustomLoadingDialog.dismiss();
            mCustomLoadingDialog = null;
        }
    }

    /**
     * 发布评论成功
     */
    @Override
    public void publishReaderMsgSuccess() {
        ToastUtils.showSingleToast(R.string.publish_reply_success);
        dismissPublishComment();
        mIsDiscussInformation = true;
        mCommentId = -1;
    }

    //删除自己评论成功
    @Override
    public void delReaderMsgSuccess(int position) {
        mAdapter.remove(position);
        mAdapter.notifyItemChanged(position + 1);
        mDiscussTotalCount = mDiscussTotalCount - 1;
        setCommentCount(mDiscussTotalCount);
        //如果数据为0，则显示空布局
        if (mAdapter.getCount() == 0) {
            setCommentCount(0);
            setDiscussEmpty(true);
        }
    }

    /**
     * 设置评论列表中的点赞状态
     *
     * @param position     位置 (备注：有header的view 数据需要 position -1, 但是UI更新需要当前的position)
     * @param praisedCount 点赞数量
     */
    @Override
    public void setCommentPraiseStatus(int position, int praisedCount) {
        mAdapter.getItem(position).mIsPraised = true;
        mAdapter.getItem(position).mPraisedCount = praisedCount + 1;
        mAdapter.notifyItemChanged(position + 1);
    }


    @Override
    public void setDiscussLocationAndCurrentPage(int discussIndex, int currentPage) {
        mCurrentDiscussPage = currentPage;
        mRealPosition = discussIndex - 1;
        ((LinearLayoutManager) mRecyclerView.getRecyclerView().getLayoutManager()).scrollToPosition(discussIndex < 0 ? 0 : discussIndex);

        mAdapter.getItem(mRealPosition).mIsLight = true;
        mAdapter.notifyItemChanged(discussIndex);
    }

    @Override
    public void showNoCommentDialog(final boolean noDiscuss, final long commentId) {
        final CustomDialog dialog = new CustomDialog(InformationDetailDiscussActivity.this, R.style.DialogTheme, getString(R.string.this_discuss_deleted));
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                if (noDiscuss) {
                    Intent data = new Intent();
                    data.putExtra(COMMENT_MSG_ID, commentId);
                    setResult(RESULT_OK);
                    finish();
                } else {
                    int position = findPositionByMsgId(commentId);
                    delReaderMsgSuccess(position);
                    dismissPublishComment();
                }
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void setCommentReplyInfo(CommentBean childBean, long commentId) {
        int position = findPositionByMsgId(commentId);
        if (position > -1 && position < mAdapter.getCount()) {

            if (null == mAdapter.getItem(position).mReplyContentList) {
                mAdapter.getItem(position).mReplyContentList = new ArrayList<>();
            }
            if (mAdapter.getItem(position).mReplyContentList.size() == 2) {
                mAdapter.getItem(position).mReplyContentList.remove(1);
            }
            mAdapter.getItem(position).mReplyContentList.add(0, childBean);
            int replyCount = mAdapter.getItem(position).mReplyCount;
            mAdapter.getItem(position).mReplyCount = replyCount + 1;
            mAdapter.notifyItemChanged(position + 1);
        }
    }

    //call fresh -通知加载上一篇
    private void loadPreviousPage() {
        if (mPosition == 0) {
            ToastUtils.showSingleToast("已是第一页");
        } else {
            mCommonTitleBar.setRightBtnClickAble(false);

            mPosition--;
            mPresenter.getInfoId(mPosition);
            mRecyclerView.scrollToPosition(0);
        }

    }

    //call loadMore- 通知加载下一篇
    private void loadNextPage() {
        if (mPosition >= mAllTotal - 1) {
            ToastUtils.showSingleToast("已是最后一页");
        } else {
            if (mPosition >= mCurrentTotal - 1) {
                mPresenter.getInfoList(mSearchKeyWord, mSearchLibCode, mSearchSource, mSearchTitle,
                        mSearchCategoryId, mSearchIndustryId, mCurrentPageNum + 1);
            } else {
                mCommonTitleBar.setRightBtnClickAble(false);

                mPosition++;
                mPresenter.getInfoId(mPosition);
                mRecyclerView.scrollToPosition(0);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //0删除操作 1点赞操作数量 2更新二级列表和回复数量
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TO_DETAIL_REQUEST_CODE:
                    if (data != null) {
                        //删除操作
                        long msgId = data.getLongExtra(COMMENT_MSG_ID, -1);
                        int index = findPositionByMsgId(msgId);
                        if (index > -1 && index < mAdapter.getCount()) {
                            mAdapter.remove(index);
                            mAdapter.notifyItemChanged(index + 1);
                            mDiscussTotalCount = mDiscussTotalCount - 1;

                            setCommentCount(mDiscussTotalCount);
                            //如果数据为0，则显示空布局
                            if (mAdapter.getCount() == 0) {
                                setCommentCount(0);
                                setDiscussEmpty(true);
                            }
                        }
                        CommentMsgBean bean = (CommentMsgBean) data.getSerializableExtra(AppIntentGlobalName.COMMENT_MSG_BEAN);
                        if (null != bean) {
                            int position = findPositionByMsgId(bean.mMsgId);
                            if (position > -1 && position < mAdapter.getCount()) {
                                //点赞操作数量
                                if (bean.mIsChangePraisedCount) {
                                    mAdapter.getItem(position).mIsPraised = bean.mIsOwnPraised;
                                    mAdapter.getItem(position).mPraisedCount = bean.mPraisedCount;
                                    mAdapter.notifyItemChanged(position + 1);
                                }
                                //更新二级列表和回复数量
                                if (bean.mIsChangeCommentData) {
                                    mAdapter.getItem(position).mReplyContentList = bean.mReplyList;
                                    mAdapter.getItem(position).mReplyCount = bean.mReplyCount;
                                    mAdapter.notifyItemChanged(position + 1);
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

    private void setCommentCount(int count) {
        if (count > 0) {
            mCountTv.setVisibility(View.VISIBLE);
            mCountTv.setText(String.valueOf(count));
        } else {
            mCountTv.setVisibility(View.GONE);
            mCountTv.setText(String.valueOf(0));
        }
    }

    private void dismissPublishComment() {
        KeyboardUtils.hideSoftInput(this);
        mCommentPublishLl.setVisibility(View.GONE);
        mContentEt.setText("");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveLoginInfo(AccountMessage loginMessage) {
        if (null != mPresenter && loginMessage.success) {
            mPresenter.getDiscussList(-1, 1, mNewsId);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveLoginOut(AccountMessage loginMessage) {
        if (null != mPresenter && loginMessage.mIsLoginOut) {
            mPresenter.getDiscussList(-1, 1, mNewsId);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != mCustomWebView) {
            mCustomWebView.onResume();
        }
        UmengHelper.setUmengResume(this);

        if (null != mCLVideoView && mCLVideoView.getVisibility() == View.VISIBLE) {
            if (mIsToShare) {
                mIsToShare = false;
            } else {
                int netStatus = CheckInternetUtil.checkNetWork(this, false);
                mCLVideoView.setNetWorkState(netStatus);
                if (netStatus != CheckInternetUtil.NET_WORK_MOBILE || mIsAllow4GPlayVideo) {
                    mCLVideoView.rePlay();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != mCustomWebView) {
            mCustomWebView.onPause();
        }
        UmengHelper.setUmengPause(this);
        if (null != mCLVideoView) {
            if (!mIsToShare) {
                mCLVideoView.pause();
            }
        }
    }

    @Override
    public void finish() {
        if (mSizeChange) {
            Intent data = new Intent();
            data.putExtra(AppIntentGlobalName.PAGE_NUM, mCurrentPageNum);
            data.putParcelableArrayListExtra(AppIntentGlobalName.INFORMATION_LIST, mInformationList);
            setResult(RESULT_OK, data);
        }
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.clear();
        EventBus.getDefault().unregister(this);
        if (null != mHandler) {
            mHandler.removeMessages(SHOW_WEB_VIEW);
            mHandler.removeMessages(SHOW_VIDEO_VIEW);
        }
        if (null != mPresenter) {
            mPresenter.detachView();
            mPresenter = null;
        }
        if (null != mCustomWebView) {
            mCustomWebView.destroyWebView();
            mCustomWebView = null;
        }
        if (null != mCLVideoView) {
            mCLVideoView.releaseVideoView();
        }
        unbinder.unbind();
    }

    //=======================视频播放部分========================================
    private CustomDialog mCustomDialog;
    private CLVideoView mCLVideoView;
    private boolean mIsLock = false;
    private boolean mIsAllow4GPlayVideo = false;    //是否提示过流量播放
    private boolean mIsInited = false;

    private CLVideoView.VideoControllerListener mVideoControllerListener = new CLVideoView.VideoControllerListener() {
        @Override
        public void onFinishClick() {
            dealBackClick();
        }

        @Override
        public void onLockClick(boolean isLock) {
            mIsLock = isLock;
            if (!isLock) {
                setFullScreen(false);
            }
        }

        @Override
        public void onChangeScreenClick() {
            if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }

        @Override
        public void changeBrightness(float brightness) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.screenBrightness = lp.screenBrightness + brightness / 255.0f;
            if (lp.screenBrightness > 1) {
                lp.screenBrightness = 1;
            } else if (lp.screenBrightness < 0.01) {
                lp.screenBrightness = (float) 0.01;
            }
            getWindow().setAttributes(lp);
            mCLVideoView.setBrightness((int) (lp.screenBrightness * 100));
        }

        @Override
        public void setFullScreen(boolean screenFull) {
            setScreenFull(mIsLock || screenFull);
        }

        @Override
        public void playComplete() {
            if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            hideVideoPlayView();
        }

        @Override
        public void show4GNoticeDialog() {
            if (null != mCustomDialog && mCustomDialog.isShowing()) {
                return;
            }
            setMobileNetTips();
        }

        @Override
        public void retryGetVideoRealUrlAndPlay() {
            mCLVideoView.startVideo();
        }
    };

    /**
     * 初始化视频UI
     *
     * @param videoUrl 视频地址
     */
    private void initVideoView(final String videoUrl) {
        if (null != mCLVideoView && mCLVideoView.getVisibility() == View.VISIBLE) {
            return;
        }
        if (null == mCLVideoView) {
            initVideo(videoUrl);
        } else {
            mCLVideoView.setVisibility(View.VISIBLE);
        }
        //网络情况播放
        int netStatus = CheckInternetUtil.checkNetWork(InformationDetailDiscussActivity.this, false);
        mCLVideoView.setNetWorkState(netStatus);
        switch (netStatus) {
            case CheckInternetUtil.NET_WORK_MOBILE:
                if (mIsAllow4GPlayVideo) {
                    mCLVideoView.startVideo();
                    return;
                }
                if (null != mCustomDialog && mCustomDialog.isShowing()) {
                    return;
                }
                setMobileNetTips();
                break;
            case CheckInternetUtil.NET_WORK_WIFI:
                if (null != mCustomDialog && mCustomDialog.isShowing()) {
                    mCustomDialog.dismiss();
                }
                mCLVideoView.startVideo();
                break;
            case CheckInternetUtil.NET_WORK_NONE:
                mCLVideoView.setNetErrorUI();
                break;
        }
    }

    private void initVideo(String videoUrl) {
        ViewStub viewStub = (ViewStub) findViewById(R.id.information_video_view_vs);
        viewStub.inflate();
        mCLVideoView = (CLVideoView) findViewById(R.id.cl_video_view);
        configPortrait();
        mCLVideoView.setVideoControllerListener(mVideoControllerListener);
        mCLVideoView.setVideoUrl(videoUrl);
        mCLVideoView.showDelIcon();
        mCLVideoView.hideShareBtn();
        mIsInited = true;
    }

    /**
     * 配置横竖屏
     *
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mIsLock) {
            return;
        }
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //横屏
            configLandscape();
            if (mCLVideoView.isShowToolBar()) {
                setScreenFull(false);
            } else {
                setScreenFull(true);
            }
        } else {
            //竖屏
            configPortrait();
            setScreenFull(false);
        }
    }

    /**
     * 接收网络状态改变信息，对不同网络状态下的处理
     *
     * @param msg
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void reciveNetInfo(NetWordMsg msg) {
        if (!mIsInited) {
            return;
        }
        mCLVideoView.setNetWorkState(msg.mNetWorkState);
        switch (msg.mNetWorkState) {
            case CheckInternetUtil.NET_WORK_MOBILE:
                //如果允许4G播放，则直接开始播放
                if (mIsAllow4GPlayVideo) {
                    mCLVideoView.startVideo();
                    return;
                }
                if (null != mCustomDialog && mCustomDialog.isShowing()) {
                    return;
                }
                setMobileNetTips();
                break;
            case CheckInternetUtil.NET_WORK_WIFI:
                //如果不是流量播放，则取消显示
                if (null != mCustomDialog && mCustomDialog.isShowing()) {
                    mCustomDialog.dismiss();
                }
                mCLVideoView.startVideo();
                break;
            case CheckInternetUtil.NET_WORK_NONE:
                mCLVideoView.setNetErrorUI();
                break;
        }
    }

    /**
     * 设置横屏
     */
    private void configLandscape() {
        StatusBarUtil.setStatusBarColor(this, R.color.color_translate);
        mStatusBarV.setVisibility(View.GONE);
        mCommonTitleBar.setVisibility(View.GONE);
        mCLVideoView.setCurrentOrientation(Configuration.ORIENTATION_LANDSCAPE, true);
        float widthPixels = DpPxUtils.getRealWidthPixels(this);
        float heightPixels = DpPxUtils.getHeightPixels();
        mCLVideoView.getLayoutParams().width = (int) widthPixels;
        mCLVideoView.getLayoutParams().height = (int) heightPixels;

        mCLVideoView.setSpaceLayoutParams((int) widthPixels - DpPxUtils.getWidthPixels());
    }

    /**
     * 设置竖屏
     */
    private void configPortrait() {
        mStatusBarV.setVisibility(View.VISIBLE);
        mCommonTitleBar.setVisibility(View.VISIBLE);
        mCLVideoView.setCurrentOrientation(Configuration.ORIENTATION_PORTRAIT, false);
        float width = DpPxUtils.getWidthPixels();
        float height = DpPxUtils.dp2px(165.f);
        mCLVideoView.getLayoutParams().width = (int) width;
        mCLVideoView.getLayoutParams().height = (int) height;

        mCLVideoView.setSpaceLayoutParams(0);
    }

    /**
     * 隐藏视频播放UI
     */
    private void hideVideoPlayView() {
        if (mCLVideoView != null && mCLVideoView.getVisibility() == View.VISIBLE) {
            mCLVideoView.stopPlayback();
            mCLVideoView.setVisibility(View.GONE);
        }
    }

    /**
     * 设置全屏
     *
     * @param isFull 是否全屏
     */
    private void setScreenFull(boolean isFull) {
        if (isFull) {
            if (Build.VERSION.SDK_INT < 16) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                    getWindow().getDecorView().setSystemUiVisibility(uiOptions);
                }
            }
        } else {
            if (Build.VERSION.SDK_INT < 16) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            } else {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

                    getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE);
                } else {
                    getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE);
                }
            }
        }
    }

    /**
     * 返回点击处理，切换为竖屏，然后退出
     */
    private void dealBackClick() {
        if (mIsLock) {
            return;
        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            dealBackClick();
        }//音量减小
        if (mCLVideoView != null) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && event.getAction() == KeyEvent.ACTION_DOWN) {
                mCLVideoView.setVolumePercent(-0.005f, false, true);
            }//音量增大
            else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP && event.getAction() == KeyEvent.ACTION_DOWN) {
                mCLVideoView.setVolumePercent(0.005f, true, true);
            }
        }
        return true;
    }

    /**
     * 提示流量是否播放
     */
    private void setMobileNetTips() {
        mCLVideoView.stopPlay();
        mCustomDialog = new CustomDialog(this, R.style.DialogTheme, "");
        mCustomDialog.setCancelable(false);
        mCustomDialog.hasNoCancel(true);
        mCustomDialog.setOkText("继续播放");
        mCustomDialog.setCancelText("暂不播放");
        mCustomDialog.setText("当前非WIFI环境，播放将产生\n" +
                "流量费用，是否继续？");
        mCustomDialog.show();
        mCustomDialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                mCustomDialog.dismiss();
                mCLVideoView.setAllow4GPlayVideo(true);
                mIsAllow4GPlayVideo = true;//已经提示流量播放，下一次直接播放
                mCLVideoView.startVideo();
            }

            @Override
            public void onClickCancel() {
                mCustomDialog.dismiss();
            }
        });
    }

}
