package com.tzpt.cloudlibrary.ui.paperbook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.bean.ShareBean;
import com.tzpt.cloudlibrary.business_bean.BookBean;
import com.tzpt.cloudlibrary.business_bean.LibraryBean;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.ui.account.LoginActivity;
import com.tzpt.cloudlibrary.ui.account.interaction.RecommendNewBookDialogFragment;
import com.tzpt.cloudlibrary.ui.library.LibraryDetailActivity;
import com.tzpt.cloudlibrary.ui.share.ShareActivity;
import com.tzpt.cloudlibrary.utils.StringUtils;
import com.tzpt.cloudlibrary.utils.ToastUtils;
import com.tzpt.cloudlibrary.utils.glide.GlideApp;
import com.tzpt.cloudlibrary.widget.CustomDialog;
import com.tzpt.cloudlibrary.widget.DrawableCenterTextView;
import com.tzpt.cloudlibrary.widget.ExpandedTextLayout;
import com.tzpt.cloudlibrary.widget.LoadingProgressView;
import com.tzpt.cloudlibrary.widget.multistatelayout.MultiStateLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 图书详情
 */
public class BookDetailActivity extends BaseActivity implements
        BookDetailContract.View {

    private static final String FROM_TYPE = "from_type";
    private static final String FROM_SEARCH = "from_search";
    private static final String BOOK_ISBN = "book_isbn";
    private static final String BOOK_LIBRARY_CODE = "book_library_code";
    private static final String BOOK_LIBRARY_NAME = "book_library_name";

    /**
     * 进入图书详情
     *
     * @param fromType 0首页进入 1详情图书馆列表进入 2图书馆详情进入 3推荐排行榜 4点赞排行榜
     */
    public static void startActivity(Context context, String isbn, String libCode, String libName, int fromType) {
        Intent intent = new Intent(context, BookDetailActivity.class);
        intent.putExtra(FROM_TYPE, fromType);
        intent.putExtra(BOOK_ISBN, isbn);
        intent.putExtra(BOOK_LIBRARY_CODE, libCode);
        intent.putExtra(BOOK_LIBRARY_NAME, libName);
        context.startActivity(intent);
    }

    public static void startActivityForSearchResult(Context context, String isbn, String libCode, int fromType) {
        Intent intent = new Intent(context, BookDetailActivity.class);
        intent.putExtra(FROM_TYPE, fromType);
        intent.putExtra(FROM_SEARCH, 1);
        intent.putExtra(BOOK_ISBN, isbn);
        intent.putExtra(BOOK_LIBRARY_CODE, libCode);
        context.startActivity(intent);
    }

    @BindView(R.id.book_image)
    ImageView mBookInfoImg;
    @BindView(R.id.book_title)
    TextView mBookInfoTitle;
    @BindView(R.id.book_type)
    TextView mBookInfoPublishType;
    @BindView(R.id.book_type_con)
    TextView mBookInfoPublishTypeCon;
    @BindView(R.id.book_isbn)
    TextView mBookInfoPublishIsbn;
    @BindView(R.id.book_isbn_con)
    TextView mBookInfoPublishIsbnCon;
    @BindView(R.id.book_publish_year)
    TextView mBookInfoPublishYear;
    @BindView(R.id.book_publish_year_con)
    TextView mBookInfoPublishYearCon;
    @BindView(R.id.book_publishing_house)
    TextView mBookInfoPublishHouse;
    @BindView(R.id.book_publishing_house_con)
    TextView mBookInfoPublishHouseCon;
    @BindView(R.id.book_author)
    TextView mBookInfoAuthor;
    @BindView(R.id.book_author_con)
    TextView mBookInfoAuthorCon;
    @BindView(R.id.book_detail_in_pavilion)
    TextView mBookDetailInPavilion;
    @BindView(R.id.book_detail_in_pavilion_arrow)
    ImageView mBookDetailInPavilionArrow;
    @BindView(R.id.multi_state_layout)
    MultiStateLayout mMultiStateLayout;
    @BindView(R.id.titlebar_right_btn)
    ImageButton mTitleBarBtn;
    @BindView(R.id.book_detail_stay_lib_rl)
    RelativeLayout mStayLibRl;
    @BindView(R.id.order_progress_layout)
    LoadingProgressView mLoadingView;

    @BindView(R.id.common_book_thumbs_up_tv)
    TextView mBookThumbsUpTv;
    @BindView(R.id.common_book_borrow_tv)
    TextView mBookBorrowTv;
    @BindView(R.id.common_book_share_tv)
    TextView mBookShareTv;
    @BindView(R.id.book_detail_root_view)
    RelativeLayout mRootViewRl;
    @BindView(R.id.common_book_share_item)
    LinearLayout mBookShareItem;

    @BindView(R.id.book_detail_introduction_tv)
    ExpandedTextLayout mBookDetailIntroductionTv;
    @BindView(R.id.book_detail_author_tv)
    ExpandedTextLayout mBookDetailAuthorTv;
    @BindView(R.id.book_detail_catalog_tv)
    ExpandedTextLayout mBookDetailCatalogTv;

    @BindView(R.id.book_detail_reservation_btn)
    DrawableCenterTextView mReservationBtn;

    private BookDetailPresenter mPresenter;

    private RecommendNewBookDialogFragment mFragment;
    private BookBottomLibraryDialog mStayLibraryDialog;

    private int mFromType;
    private String mBookIsbn;
    private String mBookName;
    private String mBookCover;

    private String mShareUrl;
    private String mShareContent;

    private String mLibCode;
    private String mLibName;
    private List<LibraryBean> mLibLists = new ArrayList<>();


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 101) {
                if (mStayLibraryDialog == null) {
                    mStayLibraryDialog = new BookBottomLibraryDialog(mContext);
                    if (mFromType == 2) {
                        mStayLibraryDialog.setTitle("其他馆");
                    }
                    mStayLibraryDialog.initAdapter(true);
                    mStayLibraryDialog.addData(mLibLists);
                    mStayLibraryDialog.setLibraryListener(new BookBottomLibraryDialog.LibraryListener() {
                        @Override
                        public void libraryClick(String libCode, String libName) {
                            BookDetailActivity.startActivity(mContext, mBookIsbn, libCode, libName, 1);
                        }
                    });
                }
                mStayLibraryDialog.show();
            }
        }
    };

    @OnClick({R.id.titlebar_left_btn, R.id.book_detail_in_pavilion,
            R.id.book_detail_recommend_btn, R.id.book_detail_share_btn, R.id.book_detail_reservation_btn})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.book_detail_in_pavilion:
                if (mFromType == 1) {
                    //进入图书馆详情
                    LibraryDetailActivity.startActivity(this, mLibCode, mLibName);
                    return;
                }
                mHandler.sendEmptyMessageDelayed(101, 100);
                break;
            case R.id.book_detail_recommend_btn:
                if (!mPresenter.isLoginStatus()) {
                    LoginActivity.startActivity(BookDetailActivity.this);
                    return;
                }
                if (null == mFragment) {
                    mFragment = new RecommendNewBookDialogFragment();
                    mFragment.setNewBookDialogListener(mNewBookDialogListener);
                }
                mFragment.show(getSupportFragmentManager(), "");
                mFragment.setBookIsbn(mBookIsbn);
                break;
            case R.id.book_detail_share_btn:
                String shareImg;
                if (TextUtils.isEmpty(mBookCover)) {
                    shareImg = "http://img.ytsg.cn/images/htmlPage/ic_logo.png";
                } else {
                    shareImg = mBookCover;
                }
                ShareBean shareBean = new ShareBean();
                shareBean.shareTitle = mBookName;
                shareBean.shareContent = mShareContent;
                shareBean.shareUrl = mShareUrl;
                shareBean.shareUrlForWX = mShareUrl;
                shareBean.shareImagePath = shareImg;
                shareBean.mNeedCopy = true;
                ShareActivity.startActivity(BookDetailActivity.this, shareBean);
                //上报图书分享
                mPresenter.reportBookShare();
                break;
            case R.id.book_detail_reservation_btn:
                mPresenter.orderBook(mBookIsbn, mLibCode);
                break;
        }

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_book_detail;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("图书详情");
//        mCommonTitleBar.setRightBtnIcon(R.drawable.bg_btn_share);
//        mCommonTitleBar.setRightBtnClickAble(false);
    }

    @Override
    public void initDatas() {
        mPresenter = new BookDetailPresenter();
        mPresenter.attachView(this);
        requestData();
    }

    private void requestData() {
        Intent intent = getIntent();
        mFromType = intent.getIntExtra(FROM_TYPE, 0);
        mBookIsbn = intent.getStringExtra(BOOK_ISBN);
        mLibCode = intent.getStringExtra(BOOK_LIBRARY_CODE);
        mLibName = intent.getStringExtra(BOOK_LIBRARY_NAME);
        int fromSearch = intent.getIntExtra(FROM_SEARCH, 0);

        //0首页进入 1详情图书馆列表进入 2图书馆详情进入 3推荐排行榜 4点赞排行榜
        switch (mFromType) {
            case 0:
            case 3:
            case 4:
                mReservationBtn.setVisibility(View.GONE);
                mPresenter.getBookDetail(mBookIsbn, null, true, false, fromSearch);
                break;
            case 1:
                mReservationBtn.setVisibility(View.VISIBLE);
                mPresenter.getBookDetail(mBookIsbn, mLibCode, false, true, fromSearch);
                setLibraryItemInfo();
                break;
            case 2:
                mReservationBtn.setVisibility(View.VISIBLE);
                mBookDetailInPavilion.setText("其他馆");
                mPresenter.getBookDetail(mBookIsbn, mLibCode, true, true, fromSearch);
                break;
        }
    }

    @Override
    public void configViews() {

    }

    //设置图书馆item
    private void setLibraryItemInfo() {
        mStayLibRl.setVisibility(View.VISIBLE);
        mBookDetailInPavilion.setText(Html.fromHtml(
                "<html><font color='#945f30'>" + mLibCode + "  " + mLibName + "</font></html>"));
        mBookDetailInPavilion.setTextSize(15);
        mBookDetailInPavilionArrow.setImageResource(R.mipmap.ic_arrow_forlibrary);
    }

    //设置图书基本信息
    @Override
    public void setBookBaseInfo(BookBean bean) {
        mBookName = bean.mBook.mName;
        mBookCover = bean.mBook.mCoverImg;

        GlideApp.with(mContext).load(bean.mBook.mCoverImg).placeholder(R.drawable.color_default_image).error(R.mipmap.ic_nopicture).centerCrop().into(mBookInfoImg);

        mBookInfoTitle.setText(TextUtils.isEmpty(bean.mBook.mName) ? "" : bean.mBook.mName);
        mBookInfoAuthor.setText(getString(R.string.book_list_author));
        mBookInfoAuthorCon.setText(TextUtils.isEmpty(bean.mAuthor.mName) ? "" : bean.mAuthor.mName);
        mBookInfoPublishHouse.setText(getString(R.string.book_list_publisher));
        mBookInfoPublishHouseCon.setText(TextUtils.isEmpty(bean.mPress.mName) ? "" : bean.mPress.mName);
        mBookInfoPublishIsbn.setText(getString(R.string.book_list_isbn));
        mBookInfoPublishIsbnCon.setText(TextUtils.isEmpty(bean.mBook.mIsbn) ? "" : bean.mBook.mIsbn);
        mBookInfoPublishType.setText(getString(R.string.book_list_publish_category_name));
        mBookInfoPublishTypeCon.setText(TextUtils.isEmpty(bean.mCategory.mName) ? ("文学") : bean.mCategory.mName);
        mBookInfoPublishYear.setText(getString(R.string.book_list_publish_date));
        mBookInfoPublishYearCon.setText(bean.mBook.mPublishDate);
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
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            requestData();
        }
    }

    @Override
    protected void onDestroy() {
        if (mStayLibraryDialog != null) {
            mStayLibraryDialog.clear();
        }
        mLibLists.clear();
        super.onDestroy();
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
        }

        if (null != mPresenter) {
            mPresenter.detachView();
            mPresenter = null;
        }
    }

    @Override
    public void showProgressDialog() {
        mMultiStateLayout.showProgress();
    }

    @Override
    public void dismissProgressDialog() {
        mMultiStateLayout.showContentView();
//        mCommonTitleBar.setRightBtnClickAble(true);
    }

    @Override
    public void showLoadingView() {
        mLoadingView.showProgressLayout();
    }

    @Override
    public void dismissLoadingView() {
        mLoadingView.hideProgressLayout();
    }

    @Override
    public void showErrorMsg(int msgId) {
        ToastUtils.showSingleToast(msgId);
    }

    //设置图书简介
    @Override
    public void setBookSummerInfo(String authorInfo, String summer, String catalog, String url) {
        if (TextUtils.isEmpty(authorInfo) && TextUtils.isEmpty(summer) && TextUtils.isEmpty(catalog)) {
            //暂无简介
            mBookDetailIntroductionTv.setTitle(getString(R.string.no_summary));
            mBookDetailIntroductionTv.hideContentArrowView();
            mBookDetailAuthorTv.hideExpandedView();
            mBookDetailCatalogTv.hideExpandedView();
        } else {
            //当仅有任意一个模块时，展示该模块的全部内容，
            if (!TextUtils.isEmpty(authorInfo) && TextUtils.isEmpty(summer) && TextUtils.isEmpty(catalog)) {
                mBookDetailAuthorTv.setLimitMaxLines(Integer.MAX_VALUE);
                mBookDetailAuthorTv.setArrowVisibility(false);
            } else if (TextUtils.isEmpty(authorInfo) && !TextUtils.isEmpty(summer) && TextUtils.isEmpty(catalog)) {
                mBookDetailIntroductionTv.setLimitMaxLines(Integer.MAX_VALUE);
                mBookDetailIntroductionTv.setArrowVisibility(false);
            }

            //设置简介
            if (!TextUtils.isEmpty(summer)) {
                mBookDetailIntroductionTv.setContent(Html.fromHtml(StringUtils.formatStringForEBook(summer)));
                if (mBookDetailIntroductionTv.isLimitLineCount(3)) {
                    mBookDetailIntroductionTv.setArrowVisibility(false);
                    mBookDetailIntroductionTv.setLimitMaxLines(Integer.MAX_VALUE);
                }
            } else {
                mBookDetailIntroductionTv.hideExpandedView();
            }
            //设置作者信息
            if (!TextUtils.isEmpty(authorInfo)) {
                mBookDetailAuthorTv.setContent(Html.fromHtml(StringUtils.formatStringForEBook(authorInfo)));
                if (mBookDetailAuthorTv.isLimitLineCount(3)) {
                    mBookDetailAuthorTv.setArrowVisibility(false);
                    mBookDetailAuthorTv.setLimitMaxLines(Integer.MAX_VALUE);
                }
            } else {
                mBookDetailAuthorTv.hideExpandedView();
            }
            //设置目录
            if (!TextUtils.isEmpty(catalog)) {
                mBookDetailCatalogTv.setContent(Html.fromHtml(StringUtils.formatString(catalog)));
            } else {
                mBookDetailCatalogTv.hideExpandedView();
            }

        }
        mShareUrl = url;
        if (TextUtils.isEmpty(summer)) {
            mShareContent = getString(R.string.no_summary);
        } else {
            mShareContent = summer;
        }

    }

    @Override
    public void setLibraryList(List<LibraryBean> libLists) {
        //设置图书馆列表
        if (null != libLists && libLists.size() > 0) {
            mStayLibRl.setVisibility(View.VISIBLE);
            mLibLists.clear();
            mLibLists.addAll(libLists);
        } else {
            mStayLibRl.setVisibility(View.GONE);
        }
    }

    @Override
    public void showNetError() {
        mMultiStateLayout.showError();
        mMultiStateLayout.showRetryError(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestData();
            }
        });
    }

    @Override
    public void showEmptyError() {
        mMultiStateLayout.showEmpty();
    }

    @Override
    public void pleaseLogin() {
        LoginActivity.startActivityForResult(this, 1000);
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
                LoginActivity.startActivityForResult(BookDetailActivity.this, 1000);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void orderBookSuccess(boolean notRegistered) {
        //携带身份证
        String tip = getString(R.string.order_book_success, notRegistered ? "携带身份证" : "");
        final CustomDialog customDialog = new CustomDialog(this, R.style.DialogTheme);
        customDialog.setCancelable(false);
        customDialog.hasNoCancel(true);
        customDialog.setBtnOKAndBtnCancelTxt("查看预约", "确定");
        customDialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                customDialog.dismiss();
                //TODO 预约列表
            }

            @Override
            public void onClickCancel() {
                customDialog.dismiss();
            }
        });

        SpannableString spannableString = new SpannableString(tip);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#f87257")), notRegistered ? 13 : 8, notRegistered ? 17 : 12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#f87257")), notRegistered ? 28 : 23, notRegistered ? 29 : 24, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#f87257")), notRegistered ? 34 : 29, notRegistered ? 35 : 30, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        customDialog.setText(spannableString);
        customDialog.show();

    }

    @Override
    public void orderBookFailed(int type) {
        final CustomDialog customDialog = new CustomDialog(this, R.style.DialogTheme);
        customDialog.setCancelable(false);
        customDialog.hasNoCancel(false);
        customDialog.setButtonTextConfirmOrYes(true);
        customDialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                customDialog.dismiss();
            }

            @Override
            public void onClickCancel() {
                customDialog.dismiss();
            }
        });

        String tip;
        switch (type) {
            case 1:
                tip = getString(R.string.order_book_failed_no_permission);
                SpannableString spannableOne = new SpannableString(tip);
                spannableOne.setSpan(new ForegroundColorSpan(Color.parseColor("#f87257")), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                customDialog.setText(spannableOne);
                break;
            case 2:
                tip = getString(R.string.order_book_failed_unable_borrow);
                SpannableString spannableTwo = new SpannableString(tip);
                spannableTwo.setSpan(new ForegroundColorSpan(Color.parseColor("#f87257")), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                customDialog.setText(spannableTwo);
                break;
            case 3:
                tip = getString(R.string.order_book_failed_no_book);
                SpannableString spannableThree = new SpannableString(tip);
                spannableThree.setSpan(new ForegroundColorSpan(Color.parseColor("#f87257")), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                customDialog.setText(spannableThree);
                break;
            case 4:
                tip = getString(R.string.order_book_failed_no_book);
                SpannableString spannableFour = new SpannableString(tip);
                spannableFour.setSpan(new ForegroundColorSpan(Color.parseColor("#f87257")), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                customDialog.setText(spannableFour);
                break;
            case 5:
                tip = getString(R.string.order_book_failed_too_more);
                SpannableString spannableFive = new SpannableString(tip);
                spannableFive.setSpan(new ForegroundColorSpan(Color.parseColor("#f87257")), 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableFive.setSpan(new ForegroundColorSpan(Color.parseColor("#f87257")), 12, 13, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableFive.setSpan(new ForegroundColorSpan(Color.parseColor("#f87257")), 18, 19, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                customDialog.setText(spannableFive);
                break;
            default:
                tip = getString(R.string.order_book_failed);
                customDialog.setText(tip);
                break;
        }
        customDialog.show();
    }

    /**
     * 展示点赞信息
     *
     * @param borrowNum 借阅数量
     * @param praiseNum 点赞数量
     * @param shareNum  分享数量
     */
    @Override
    public void showShareItem(int borrowNum, int praiseNum, int shareNum) {
        mBookBorrowTv.setText(StringUtils.formatCountPlus(borrowNum));
        mBookThumbsUpTv.setText(StringUtils.formatCountPlus(praiseNum));
        mBookShareTv.setText(StringUtils.formatCountPlus(shareNum));
    }

    @Override
    public void showShareItemEmpty() {
        mBookShareItem.setVisibility(View.GONE);
    }

    @Override
    public void setReservationBtnStatus(int status) {
        Drawable left;
        if (status == 1) {
            left = getResources().getDrawable(R.mipmap.ic_detail_reservation);
            mReservationBtn.setTextColor(Color.parseColor("#666666"));
            mReservationBtn.setText("预约");
            mReservationBtn.setClickable(true);
        } else if (status == 2) {
            left = getResources().getDrawable(R.mipmap.ic_detail_reservation_unable);
            mReservationBtn.setTextColor(Color.parseColor("#999999"));
            mReservationBtn.setText("预约");
            mReservationBtn.setClickable(true);
        } else {
            left = getResources().getDrawable(R.mipmap.ic_detail_reservationed);
            mReservationBtn.setTextColor(Color.parseColor("#666666"));
            mReservationBtn.setText("已预约");
            mReservationBtn.setClickable(false);
        }
        mReservationBtn.setCompoundDrawablesWithIntrinsicBounds(left, null, null, null);
    }

    private RecommendNewBookDialogFragment.NewBookDialogListener mNewBookDialogListener = new RecommendNewBookDialogFragment.NewBookDialogListener() {
        @Override
        public void callbackLoginOut() {
            pleaseLoginTip();
        }
    };
}
