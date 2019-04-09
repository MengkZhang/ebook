package com.tzpt.cloudlibrary.ui.account.selfhelp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.BaseListActivity;
import com.tzpt.cloudlibrary.business_bean.BoughtBookBean;
import com.tzpt.cloudlibrary.business_bean.ReadNoteBean;
import com.tzpt.cloudlibrary.ui.account.borrow.ReaderNotesAdapter;
import com.tzpt.cloudlibrary.ui.account.borrow.ReadingNoteEditActivity;
import com.tzpt.cloudlibrary.utils.MoneyUtils;
import com.tzpt.cloudlibrary.utils.glide.GlideApp;
import com.tzpt.cloudlibrary.widget.CustomDialog;
import com.tzpt.cloudlibrary.widget.DrawableCenterTextView;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.ItemView;

import org.greenrobot.eventbus.EventBus;

import butterknife.OnClick;

/**
 * 购书架详情
 */
public class SelfBuyBookShelfDetailActivity extends BaseListActivity<ReadNoteBean> implements
        SelfBuyBookShelfDetailContract.View {

    private static final String BUY_BOOK_ID = "buyBookId";
    private static final String RESULT_DATA_NEW_NOTE = "new_note";

    public static void startActivity(Context context, long buyBookId) {
        Intent intent = new Intent(context, SelfBuyBookShelfDetailActivity.class);
        intent.putExtra(BUY_BOOK_ID, buyBookId);
        context.startActivity(intent);
    }

    public static void startActivity(Activity context, long buyBookId, int requestCode) {
        Intent intent = new Intent(context, SelfBuyBookShelfDetailActivity.class);
        intent.putExtra(BUY_BOOK_ID, buyBookId);
        context.startActivityForResult(intent, requestCode);
    }

    private BookInfoView mBookInfoView;

    private SelfBuyBookShelfDetailPresenter mPresenter;

    private boolean mIsPraised;
    private long mBuyBookId;

    private int mPositionTag = -1;

    private View.OnClickListener mDelClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mPositionTag = (int) v.getTag();
            delNoteTip(mPositionTag);
        }
    };

    private void delNoteTip(final int position) {
        final CustomDialog dialog = new CustomDialog(SelfBuyBookShelfDetailActivity.this, R.style.DialogTheme, "确认删除该条笔记？");
        dialog.setCancelable(false);
        dialog.hasNoCancel(true);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                ReadNoteBean item = mAdapter.getItem(mPositionTag);
                if (item != null) {
                    mPresenter.delNote(item.mNote.mId);
                }
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    @OnClick({R.id.titlebar_left_btn})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_self_buy_book_shelf_detail;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("图书详情");
    }

    @Override
    public void initDatas() {
        EventBus.getDefault().register(this);
        mPresenter = new SelfBuyBookShelfDetailPresenter();
        mPresenter.attachView(this);

        mBuyBookId = getIntent().getLongExtra(BUY_BOOK_ID, 0);
        mPresenter.getSelfBuyBookShelfDetail(mBuyBookId);
    }

    @Override
    public void configViews() {
        mAdapter = new ReaderNotesAdapter(this, mDelClickListener);
        initAdapter(false, false);

        mBookInfoView = new BookInfoView();
        mBookInfoView.setPraiseClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.praiseSelfBuyBook(mBuyBookId, mIsPraised);
            }
        });
        mBookInfoView.setWriteNoteClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPositionTag = -1;
                ReadingNoteEditActivity.startActivityForResult(SelfBuyBookShelfDetailActivity.this, mBuyBookId, -1, "", 1000);
            }
        });
        mAdapter.addHeader(mBookInfoView);
    }


    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void showProgressDialog() {
//        mMultiStateLayout.showProgress();
    }

    @Override
    public void dismissProgressDialog() {
//        mMultiStateLayout.showContentView();
    }

    @Override
    public void setNetError() {
//        mMultiStateLayout.showError();
//        mMultiStateLayout.showRetryError(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mPresenter.getSelfBuyBookShelfDetail(mBuyBookId);
//            }
//        });
    }

    @Override
    public void setBuyBookDetail(BoughtBookBean bean) {
        mIsPraised = bean.mIsPraised;
        mBookInfoView.setBookInfo(bean);
    }

    @Override
    public void showMsgToast(int resId) {

    }

    @Override
    public void delNoteSuccess() {

    }

    @Override
    public void praiseBuyBookSuccess(boolean isPraised, int resId) {

    }

    @Override
    public void pleaseLoginTip() {

    }


    @Override
    public void onItemClick(int position) {

    }

    private class BookInfoView implements ItemView {
        private TextView mIndexTv;
        private View mMarginView;
        private ImageView mItemBookImage;
        private TextView mItemBookTitle;
        private TextView mItemBookAuthor;
        private TextView mItemBookPublishingCompany;
        private TextView mItemBookPublishingYear;
        private TextView mItemBookIsbn;
        private TextView mItemBookType;
        private TextView mItemBookLib;
        private TextView mOneKeyToReNew;
        private TextView mOverdueBuyTip;
        private DrawableCenterTextView mBookDetailPraiseBtn;
        private DrawableCenterTextView mBookDetailLostBtn;
        private DrawableCenterTextView mBookDetailWriteNoteBtn;
        private TextView mEmptyNotesTv;
        private RelativeLayout mPriceLayout;
        private TextView mBoughtBookPriceTv;
        private TextView mBoughtBookFixedPriceTv;

        private View mBookInfoView;

        BookInfoView() {
            mBookInfoView = LayoutInflater.from(SelfBuyBookShelfDetailActivity.this).inflate(R.layout.view_borrow_book_detail_head, null);
            mIndexTv = (TextView) mBookInfoView.findViewById(R.id.item_borrow_book_position_tv);
            mMarginView = mBookInfoView.findViewById(R.id.margin_view);
            mItemBookImage = (ImageView) mBookInfoView.findViewById(R.id.item_book_image);
            mItemBookTitle = (TextView) mBookInfoView.findViewById(R.id.item_book_title);
            mItemBookAuthor = (TextView) mBookInfoView.findViewById(R.id.item_book_author);
            mItemBookPublishingCompany = (TextView) mBookInfoView.findViewById(R.id.item_book_publishing_company);
            mItemBookPublishingYear = (TextView) mBookInfoView.findViewById(R.id.item_book_publishing_year);
            mItemBookIsbn = (TextView) mBookInfoView.findViewById(R.id.item_book_isbn);
            mItemBookType = (TextView) mBookInfoView.findViewById(R.id.item_book_category);
            mItemBookLib = (TextView) mBookInfoView.findViewById(R.id.item_book_publishing_library);
            mOneKeyToReNew = (TextView) mBookInfoView.findViewById(R.id.one_key_to_re_new);
            mOverdueBuyTip = (TextView) mBookInfoView.findViewById(R.id.borrow_book_overdue_buy_tip);
            mBookDetailPraiseBtn = (DrawableCenterTextView) mBookInfoView.findViewById(R.id.item_book_thumbs_up);
            mBookDetailLostBtn = (DrawableCenterTextView) mBookInfoView.findViewById(R.id.item_book_lost_tv);
            mBookDetailWriteNoteBtn = (DrawableCenterTextView) mBookInfoView.findViewById(R.id.item_book_write_note_tv);
            mPriceLayout = (RelativeLayout) mBookInfoView.findViewById(R.id.buy_book_money_rl);
            mBoughtBookPriceTv = (TextView) mBookInfoView.findViewById(R.id.buy_book_money_tv);
            mBoughtBookFixedPriceTv = (TextView) mBookInfoView.findViewById(R.id.buy_book_fixed_price_tv);
            mEmptyNotesTv = (TextView) mBookInfoView.findViewById(R.id.book_no_note_tip_tv);


            mIndexTv.setVisibility(View.GONE);
            mMarginView.setVisibility(View.VISIBLE);
        }

        @Override
        public View onCreateView(ViewGroup parent) {
            return mBookInfoView;
        }

        @Override
        public void onBindView(View headerView) {

        }

        void setEmptyNotesTip(boolean isEmpty) {
            if (isEmpty) {
                mEmptyNotesTv.setVisibility(View.VISIBLE);
            } else {
                mEmptyNotesTv.setVisibility(View.GONE);
            }
        }

        void setPraiseClickListener(View.OnClickListener listener) {
            mBookDetailPraiseBtn.setOnClickListener(listener);
        }

        void setWriteNoteClickListener(View.OnClickListener listener) {
            mBookDetailWriteNoteBtn.setOnClickListener(listener);
        }

        void setBookInfo(BoughtBookBean bean) {
            mPriceLayout.setVisibility(View.VISIBLE);

            mItemBookTitle.setText(bean.mBook.mName);
            mItemBookLib.setText(bean.mLibrary.mName);

            GlideApp.with(SelfBuyBookShelfDetailActivity.this).load(bean.mBook.mCoverImg).placeholder(R.mipmap.ic_nopicture).error(R.mipmap.ic_nopicture).centerCrop().into(mItemBookImage);

            mItemBookAuthor.setText(getString(R.string.book_list_author, bean.mAuthor.mName));
            mItemBookIsbn.setText(getString(R.string.book_list_isbn, bean.mBook.mIsbn));
            mItemBookPublishingCompany.setText(getString(R.string.book_list_publisher, bean.mPress.mName));
            mItemBookPublishingYear.setText(getString(R.string.book_list_publish_date, bean.mBook.mPublishDate));
            mItemBookType.setText(getString(R.string.book_list_publish_category_name, bean.mCategory.mName));

            mBoughtBookPriceTv.setText(getString(R.string.buy_price, MoneyUtils.formatMoney(bean.mBoughtPrice)));
            mBoughtBookFixedPriceTv.setText(getString(R.string.fixed_price, MoneyUtils.formatMoney(bean.mBook.mFixedPrice)));

            setThumbUpInfo(mIsPraised);
        }

        /**
         * 设置是否点赞
         */
        void setThumbUpInfo(boolean isPraised) {
            mBookDetailPraiseBtn.setText(isPraised ? "已赞" : "点赞");
            Drawable flup;
            if (isPraised) {
                flup = getResources().getDrawable(R.mipmap.ic_good);
            } else {
                flup = getResources().getDrawable(R.mipmap.ic_good1);
            }
            flup.setBounds(0, 0, flup.getMinimumWidth(), flup.getMinimumHeight());
            mBookDetailPraiseBtn.setCompoundDrawables(flup, null, null, null);
        }
    }
}
