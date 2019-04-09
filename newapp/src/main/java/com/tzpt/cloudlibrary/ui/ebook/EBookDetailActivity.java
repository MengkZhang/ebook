package com.tzpt.cloudlibrary.ui.ebook;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.bean.ShareBean;
import com.tzpt.cloudlibrary.business_bean.EBookBean;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.ui.account.LoginActivity;
import com.tzpt.cloudlibrary.ui.library.LibraryDetailActivity;
import com.tzpt.cloudlibrary.ui.permissions.Permission;
import com.tzpt.cloudlibrary.ui.permissions.RxPermissions;
import com.tzpt.cloudlibrary.ui.share.ShareActivity;
import com.tzpt.cloudlibrary.utils.StringUtils;
import com.tzpt.cloudlibrary.utils.ToastUtils;
import com.tzpt.cloudlibrary.utils.glide.GlideApp;
import com.tzpt.cloudlibrary.widget.CustomDialog;
import com.tzpt.cloudlibrary.widget.multistatelayout.MultiStateLayout;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * 电子书详情
 */
public class EBookDetailActivity extends BaseActivity implements
        EBookDetailContract.View {

    private static final int LOGIN_REQUEST_CODE = 1000;
    private static final int SHARE_REQUEST_CODE = 1001;
    private static final int READ_REQUEST_CODE = 1002;
    private static final String FROM_SEARCH = "from_search";
    public static final String BOOK_ID = "book_id";
    private static final String BOOK_LIBRARY_CODE = "ebook_library_code";

    public static void startActivity(Context context, String id, String libCode) {
        Intent intent = new Intent(context, EBookDetailActivity.class);
        intent.putExtra(BOOK_ID, id);
        intent.putExtra(BOOK_LIBRARY_CODE, libCode);
        context.startActivity(intent);
    }

    public static void startActivityForSearchResult(Context context, String id, String libCode) {
        Intent intent = new Intent(context, EBookDetailActivity.class);
        intent.putExtra(FROM_SEARCH, 1);
        intent.putExtra(BOOK_ID, id);
        intent.putExtra(BOOK_LIBRARY_CODE, libCode);
        context.startActivity(intent);
    }

    @BindView(R.id.item_book_image)
    ImageView mBookInfoImg;
    @BindView(R.id.item_book_title)
    TextView mBookInfoTitle;
    @BindView(R.id.item_book_author)
    TextView mBookInfoAuthor;
    @BindView(R.id.item_book_author_con)
    TextView mBookInfoAuthorCon;
    @BindView(R.id.item_book_publishing_company)
    TextView mBookInfoPublishCompany;
    @BindView(R.id.item_book_publishing_company_con)
    TextView mBookInfoPublishCompanyCon;
    @BindView(R.id.item_book_publishing_year)
    TextView mBookInfoPublishYear;
    @BindView(R.id.item_book_publishing_year_con)
    TextView mBookInfoPublishYearCon;
    @BindView(R.id.item_book_isbn)
    TextView mBookInfoPublishIsbn;
    @BindView(R.id.item_book_isbn_con)
    TextView mBookInfoPublishIsbnCon;
    @BindView(R.id.item_book_type)
    TextView mBookInfoPublishType;
    @BindView(R.id.item_book_type_con)
    TextView mBookInfoPublishTypeCon;
    @BindView(R.id.book_detail_description)
    TextView mBookDetailDescription;
    @BindView(R.id.book_detail_content)
    TextView mBookDetailContent;
    @BindView(R.id.book_detail_in_pavilion)
    TextView mBookDetailInPavilion;
    @BindView(R.id.lin_start_reading)
    LinearLayout mStartReadingLL;

    @BindView(R.id.common_book_thumbs_up_tv)
    TextView mCollectCountTv;
    @BindView(R.id.common_book_thumbs_up_name_tv)
    TextView mCollectCountNameTv;
    @BindView(R.id.common_book_borrow_tv)
    TextView mReadCountTv;
    @BindView(R.id.common_book_borrow_name_tv)
    TextView mReadCountNameTv;
    @BindView(R.id.common_book_share_tv)
    TextView mShareCountTv;
    @BindView(R.id.ebook_share_item_layout)
    LinearLayout mEBookShareItemLayout;

    @BindView(R.id.multi_state_layout)
    MultiStateLayout mMultiStateLayout;
    private EBookDetailPresenter mPresenter;
    private EBookBean mEBookInfoBean;
    private String mLibCode;
    private String mLibName;
    private boolean mIsCollectionLock = true;
    private boolean mIsCollection = false;

    private String mBookId;

    @OnClick({R.id.titlebar_left_btn, R.id.titlebar_right_btn, R.id.lin_start_reading, R.id.book_detail_in_pavilion})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.titlebar_right_btn://分享
                if (null != mEBookInfoBean) {
                    String content;
                    if (TextUtils.isEmpty(mEBookInfoBean.mEBook.mSummary)) {
                        content = getString(R.string.no_summary);
                    } else {
                        content = mEBookInfoBean.mEBook.mSummary;
                    }
                    String shareImg;
                    if (TextUtils.isEmpty(mEBookInfoBean.mEBook.mCoverImg)) {
                        shareImg = "http://img.ytsg.cn/images/htmlPage/ic_logo.png";
                    } else {
                        shareImg = mEBookInfoBean.mEBook.mCoverImg;
                    }

                    ShareBean shareBean = new ShareBean();
                    shareBean.shareTitle = mEBookInfoBean.mEBook.mName;
                    shareBean.shareContent = content;
                    shareBean.shareUrl = mEBookInfoBean.mShareUrl;
                    shareBean.shareUrlForWX = mEBookInfoBean.mShareUrl;
                    shareBean.shareImagePath = shareImg;

                    shareBean.mNeedCopy = true;
                    shareBean.mNeedCollection = true;
                    shareBean.mIsCollection = mIsCollection;
                    ShareActivity.startActivityForResult(this, shareBean, SHARE_REQUEST_CODE);
                    //上报电子书分享
                    mPresenter.reportEBookShare();
                }
                break;
            case R.id.lin_start_reading:
                initStoragePermission();
                break;
            case R.id.book_detail_in_pavilion:
                //进入图书馆详情
                if (null != mLibCode && null != mLibName) {
                    LibraryDetailActivity.startActivity(this, mLibCode, mLibName);
                }
                break;

        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_ebook_detail;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setRightBtnIcon(R.drawable.bg_btn_share);
        mCommonTitleBar.setRightBtnClickAble(false);
        mCommonTitleBar.setTitle("电子书详情");
    }

    @Override
    public void initDatas() {
        mPresenter = new EBookDetailPresenter();
        mPresenter.attachView(this);
        requestData();
    }

    @Override
    public void configViews() {

    }

    private void requestData() {
        Intent intent = getIntent();
        int fromSearch = intent.getIntExtra(FROM_SEARCH, 0);
        mBookId = intent.getStringExtra(BOOK_ID);
        mLibCode = intent.getStringExtra(BOOK_LIBRARY_CODE);

        mPresenter.getEBookDetail(mBookId, fromSearch, mLibCode);
    }

    @Override
    protected void onResume() {
        super.onResume();
        UmengHelper.setUmengResume(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == LOGIN_REQUEST_CODE) {
            //登录以后收藏电子数
            mPresenter.collectionEBook();
        } else if (resultCode == RESULT_OK && requestCode == SHARE_REQUEST_CODE
                && data != null && data.getBooleanExtra("mIsCollection", false)) {
            //分享-收藏或者取消收藏电子书
            if (!mPresenter.isLogin()) {
                LoginActivity.startActivityForResult(EBookDetailActivity.this, LOGIN_REQUEST_CODE);
                return;
            }
            if (mIsCollectionLock) {
                return;
            }
            mPresenter.collectionOrCancelEBook();
        } else if (resultCode == RESULT_OK && requestCode == READ_REQUEST_CODE && data != null) {
            //设置收藏状态
            int readCollectStatus = data.getIntExtra("readCollectStatus", -1);
            if (readCollectStatus == 1) {
                this.mIsCollection = true;
                mPresenter.setCollectionStatus(true);
            } else if (readCollectStatus == 2) {
                this.mIsCollection = false;
                mPresenter.setCollectionStatus(false);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
    }

    @Override
    public void showErrorMsg(int msgId) {

    }

    @Override
    public void setEBookDetailInfo(EBookBean bean) {
        mEBookInfoBean = bean;

        mCommonTitleBar.setRightBtnClickAble(true);
        GlideApp.with(mContext).load(bean.mEBook.mCoverImg).placeholder(R.drawable.color_default_image).error(R.mipmap.ic_nopicture).centerCrop().into(mBookInfoImg);

        mBookInfoTitle.setText(TextUtils.isEmpty(bean.mEBook.mName) ? "" : bean.mEBook.mName);
        mBookInfoAuthor.setText(getString(R.string.book_list_author));
        mBookInfoAuthorCon.setText(TextUtils.isEmpty(bean.mAuthor.mName) ? ("") : bean.mAuthor.mName);
        mBookInfoPublishCompany.setText(getString(R.string.book_list_publisher));
        mBookInfoPublishCompanyCon.setText(TextUtils.isEmpty(bean.mPress.mName) ? ("") : (bean.mPress.mName));
        mBookInfoPublishIsbn.setText(getString(R.string.book_list_isbn));
        mBookInfoPublishIsbnCon.setText(TextUtils.isEmpty(bean.mEBook.mIsbn) ? ("") : (bean.mEBook.mIsbn));
        mBookInfoPublishType.setText(getString(R.string.book_list_publish_category_name));
        mBookInfoPublishTypeCon.setText(TextUtils.isEmpty(bean.mCategory.mName) ? ("文学"): bean.mCategory.mName);
        //出版年
        if (!TextUtils.isEmpty(bean.mEBook.mPublishDate)) {
            mBookInfoPublishYear.setText(getString(R.string.book_list_publish_date));
            int length = bean.mEBook.mPublishDate.length();
            if (length >= 4) {
                String date = bean.mEBook.mPublishDate.substring(0, 4);
                mBookInfoPublishYearCon.setText(date);
            } else {
                mBookInfoPublishYearCon.setText(bean.mEBook.mPublishDate);
            }
        } else {
            mBookInfoPublishYearCon.setText("暂无数据");
        }
        if (!TextUtils.isEmpty(bean.mEBook.mSummary)) {
            mBookDetailDescription.setVisibility(View.VISIBLE);
            mBookDetailContent.setVisibility(View.VISIBLE);
            mBookDetailContent.setText(Html.fromHtml(StringUtils.formatStringForEBook(bean.mEBook.mSummary)));
        }
    }

    /**
     * 设置收藏阅读分享信息
     *
     * @param readCount  阅读数量
     * @param collectNum 收藏数量
     * @param shareNum   分享数量
     */
    @Override
    public void setEBookDetailShareInfo(int readCount, int collectNum, int shareNum) {
        mEBookShareItemLayout.setVisibility(View.VISIBLE);
        mCollectCountNameTv.setText("收藏");
        mReadCountNameTv.setText("阅读");
        mReadCountTv.setText(StringUtils.formatCountPlus(readCount));
        mCollectCountTv.setText(StringUtils.formatCountPlus(collectNum));
        mShareCountTv.setText(StringUtils.formatCountPlus(shareNum));
    }

    @Override
    public void setEBookBelongLib(String libCode, String libName) {
        mLibCode = libCode;
        mLibName = libName;
        mBookDetailInPavilion.setVisibility(View.VISIBLE);
        String info = libCode + "  " + libName;
        mBookDetailInPavilion.setText(info);
    }

    @Override
    public void hideEBookBelongLib() {
        mBookDetailInPavilion.setVisibility(View.GONE);
    }

    @Override
    public void showNetError() {
        mMultiStateLayout.showRetryError(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestData();
            }
        });
    }

    //电子书收藏状态
    @Override
    public void setEBookCollectionStatus(boolean collection) {
        mIsCollectionLock = false;
        mIsCollection = collection;
    }

    @Override
    public void collectEBookSuccess(boolean collection) {
        mIsCollection = collection;
        if (collection) {
            ToastUtils.showSingleToast(R.string.collect_video_success);
        } else {
            ToastUtils.showSingleToast(R.string.cancel_collect_video);
        }
    }

    @Override
    public void finish() {
        if (!mIsCollection) {
            Intent data = new Intent();
            data.putExtra(BOOK_ID, mBookId);
            setResult(RESULT_OK, data);
        }
        super.finish();
    }

    @Override
    public void showCollectProgress(int resId) {
        showDialog(getString(resId));
    }

    @Override
    public void dismissCollectProgress() {
        dismissDialog();
    }

    @Override
    public void showNoLoginDialog() {
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
                LoginActivity.startActivity(EBookDetailActivity.this);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    //初始化权限
    private void initStoragePermission() {
        if (Build.VERSION.SDK_INT < 23) {
            EBookReaderActivity.startActivityForResult(this, String.valueOf(mEBookInfoBean.mEBook.mId), mEBookInfoBean.mEBook.mFileDownloadPath, mEBookInfoBean.mEBook.mName,
                    mEBookInfoBean.mAuthor.mName, mEBookInfoBean.mEBook.mCoverImg, mEBookInfoBean.mShareUrl, mEBookInfoBean.mEBook.mSummary, mLibCode, READ_REQUEST_CODE);
            return;
        }
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.setLogging(true);
        rxPermissions.requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Action1<Permission>() {
                    @Override
                    public void call(Permission permission) {
                        if (permission.granted) {//权限已授权
                            EBookReaderActivity.startActivityForResult(EBookDetailActivity.this, String.valueOf(mEBookInfoBean.mEBook.mId), mEBookInfoBean.mEBook.mFileDownloadPath, mEBookInfoBean.mEBook.mName,
                                    mEBookInfoBean.mAuthor.mName, mEBookInfoBean.mEBook.mCoverImg, mEBookInfoBean.mShareUrl, mEBookInfoBean.mEBook.mSummary, mLibCode, READ_REQUEST_CODE);
                        } else if (permission.shouldShowRequestPermissionRationale) {//下次继续提示(应该显示请求许可理由)

                        } else {//没有权限,不能使用权限模块-去设置权限

                        }
                    }
                });
    }
}