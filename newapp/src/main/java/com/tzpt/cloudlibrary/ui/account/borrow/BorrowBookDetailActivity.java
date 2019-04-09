package com.tzpt.cloudlibrary.ui.account.borrow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseListActivity;
import com.tzpt.cloudlibrary.base.data.Note;
import com.tzpt.cloudlibrary.business_bean.BaseBookBean;
import com.tzpt.cloudlibrary.business_bean.BorrowBookBean;
import com.tzpt.cloudlibrary.business_bean.BoughtBookBean;
import com.tzpt.cloudlibrary.business_bean.LibraryBean;
import com.tzpt.cloudlibrary.business_bean.ReadNoteBean;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.ui.library.LibraryDetailActivity;
import com.tzpt.cloudlibrary.ui.paperbook.BookBottomLibraryDialog;
import com.tzpt.cloudlibrary.utils.DateUtils;
import com.tzpt.cloudlibrary.utils.MoneyUtils;
import com.tzpt.cloudlibrary.utils.ToastUtils;
import com.tzpt.cloudlibrary.utils.glide.GlideApp;
import com.tzpt.cloudlibrary.widget.CustomDialog;
import com.tzpt.cloudlibrary.widget.DrawableCenterTextView;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.ItemView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 当前借阅历史借阅和读书笔记详情
 */
public class BorrowBookDetailActivity extends BaseListActivity<ReadNoteBean> implements
        BorrowBookDetailContract.View {
    private static final String BORROW_ID = "borrow_id";
    private static final String BUY_ID = "buy_id";
    private static final String RESULT_DATA_NEW_NOTE = "new_note";
    private static final String RESULT_DATA_PRAISE = "borrow_book_praise";
    private static final String RESULT_COMPENSATE_SUCCESS = "compensate_success";

    public static void startActivityForResult(Activity context, long borrowId, int requestCode) {
        Intent intent = new Intent(context, BorrowBookDetailActivity.class);
        intent.putExtra(BORROW_ID, borrowId);
        context.startActivityForResult(intent, requestCode);
    }

    public static void startActivityForBoughtResult(Activity context, long boughtId, int requestCode) {
        Intent intent = new Intent(context, BorrowBookDetailActivity.class);
        intent.putExtra(BUY_ID, boughtId);
        context.startActivityForResult(intent, requestCode);
    }

    public static void startActivity(Context context, long borrowId) {
        Intent intent = new Intent(context, BorrowBookDetailActivity.class);
        intent.putExtra(BORROW_ID, borrowId);
        context.startActivity(intent);
    }

    @BindView(R.id.book_detail_in_pavilion_layout)
    RelativeLayout mBookDetailInPavilionLayout;

    private BookInfoView mBookInfoView;

    private BookBottomLibraryDialog mStayLibraryDialog;
    private BorrowBookDetailPresenter mPresenter;
    private long mBorrowId;
    private long mBoughtId;
    private boolean mIsPraised;
    private boolean mIsPraiseDone;

    private boolean mIsModifyNote;

    private int mPositionTag = -1;
    private List<LibraryBean> mLibLists = new ArrayList<>();

    private View.OnClickListener mDelClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            mPositionTag = (int) v.getTag();
            delNoteTip();
        }
    };

    /**
     * 删除笔记提示
     */
    private void delNoteTip() {
        final CustomDialog dialog = new CustomDialog(BorrowBookDetailActivity.this, R.style.DialogTheme, "确认删除该条笔记？");
        dialog.setCancelable(false);
        dialog.hasNoCancel(true);
        dialog.setOkText("删除");
        dialog.setCancelText("取消");
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

    @OnClick({R.id.titlebar_left_btn, R.id.book_detail_in_pavilion})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.book_detail_in_pavilion:
                if (mStayLibraryDialog == null) {
                    mStayLibraryDialog = new BookBottomLibraryDialog(mContext);
                    mStayLibraryDialog.setTitle(getString(R.string.common_return_library));
                    mStayLibraryDialog.initAdapter(false);
                    mStayLibraryDialog.addData(mLibLists);
                    mStayLibraryDialog.setLibraryListener(new BookBottomLibraryDialog.LibraryListener() {
                        @Override
                        public void libraryClick(String libCode, String libName) {
                            //进入图书馆
                            LibraryDetailActivity.startActivity(BorrowBookDetailActivity.this, libCode, libName);
                        }
                    });
                }
                mStayLibraryDialog.show();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_borrow_book_detail;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("图书详情");
    }

    @Override
    public void initDatas() {
        Intent intent = getIntent();
        mPresenter = new BorrowBookDetailPresenter();
        mPresenter.attachView(this);

        mBorrowId = intent.getLongExtra(BORROW_ID, 0);
        mBoughtId = intent.getLongExtra(BUY_ID, 0);
        if (mBorrowId > 0) {
            mPresenter.getBorrowBookDetail(mBorrowId);
        }

        if (mBoughtId > 0) {
            mPresenter.getSelfBuyBookShelfDetail(mBoughtId);
        }

        //注册事件
        EventBus.getDefault().register(this);
    }

    @Override
    public void configViews() {
        mAdapter = new ReaderNotesAdapter(this, mDelClickListener);
        initAdapter(false, false);
        mBookInfoView = new BookInfoView();
        mBookInfoView.setLostBookClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserCompensateBookActivity.startActivityForResult(BorrowBookDetailActivity.this, mBorrowId, 1001);
            }
        });
        mBookInfoView.setOnKeyToRenewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBorrowId > 0) {
                    mPresenter.renewBorrowBook(mBorrowId);
                }
            }
        });
        mBookInfoView.setPraiseClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsPraised) {
                    mPresenter.praiseBook(mBorrowId, mBoughtId);
                }
            }
        });
        mBookInfoView.setWriteNoteClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPositionTag = -1;
                if (mBorrowId > 0) {
                    ReadingNoteEditActivity.startActivityForResult(BorrowBookDetailActivity.this, mBorrowId, -1, "", 1000);
                }
                if (mBoughtId > 0) {
                    ReadingNoteEditActivity.startActivityForResult(BorrowBookDetailActivity.this, mBoughtId, -1, 1000);
                }
            }
        });
        mAdapter.addHeader(mBookInfoView);

    }

    @Override
    public void onRefresh() {
        if (mBorrowId > 0) {
            mPresenter.getBorrowBookDetail(mBorrowId);
        }

        if (mBoughtId > 0) {
            mPresenter.getSelfBuyBookShelfDetail(mBoughtId);
        }
    }

    @Override
    public void onLoadMore() {

    }

    //进入图书馆
    @Override
    public void onItemClick(int position) {
        mPositionTag = position;
        ReadNoteBean item = mAdapter.getItem(mPositionTag);
        if (item != null) {
            ReadingNoteEditActivity.startActivityForResult(this, mBorrowId, item.mNote.mId, item.mNote.mContent, 1000);
        }
    }

    @Override
    public void dismissProgressDialog() {
        mRecyclerView.showRecycler();
    }

    /**
     * 详情页数据请求成功--都会执行这个回调
     * @param bean
     */
    @Override
    public void setBookBaseInfo(BorrowBookBean bean) {
        mIsPraised = bean.mIsPraised;

        mBookInfoView.setBorrowInfo(bean);
    }

    @Override
    public void setBuyBookDetail(BoughtBookBean bean) {
        mIsPraised = bean.mIsPraised;

        mBookInfoView.setBoughtInfo(bean);
    }

    //设置通还馆列表
    @Override
    public void setLibraryList(List<LibraryBean> libraryBeanList) {
        //设置图书馆列表
        mBookDetailInPavilionLayout.setVisibility(View.VISIBLE);
        mLibLists.clear();
        mLibLists.addAll(libraryBeanList);

    }

    //设置读书笔记
    @Override
    public void setUserReadingNote(List<ReadNoteBean> readNotes) {
        if (null != readNotes && readNotes.size() > 0) {
            mAdapter.addAll(readNotes);
        } else {
            setUserReadingNoteEmpty();
        }
    }

    @Override
    public void setUserReadingNoteEmpty() {
        mBookInfoView.setEmptyNotesTip(true);
    }

    @Override
    public void operatePraiseSuccess() {
        mIsPraiseDone = true;
        ToastUtils.showSingleToast(R.string.success);
        mIsPraised = true;
        mBookInfoView.setThumbUpInfo(true);
        BookPraiseMessage bookPraiseMessage = new BookPraiseMessage();
        bookPraiseMessage.isPraised = mIsPraised;
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
    public void showNetError() {
        mRecyclerView.setRefreshing(false);
        if (mAdapter.getCount() < 1) {
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
        accountMessage.mIsToUserCenter = true;
        EventBus.getDefault().post(accountMessage);
    }

    @Override
    public void delNoteSuccess() {
        DataRepository.getInstance().delNote(mAdapter.getItem(mPositionTag));
        mIsModifyNote = true;

        mAdapter.remove(mPositionTag);
        mAdapter.notifyDataSetChanged();
        if (mAdapter.getCount() == 0) {
            setUserReadingNoteEmpty();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && null != data) {
            switch (requestCode) {
                case 1000:
                    mIsModifyNote = true;
                    String content = data.getStringExtra(RESULT_DATA_NEW_NOTE);
                    long noteId = data.getLongExtra("note_id", -1);
                    if (mPositionTag == -1) {//新增笔记
                        ReadNoteBean readNoteBean = new ReadNoteBean();
                        Note item = new Note();
                        item.mId = noteId;
                        item.mContent = content;
                        item.mModifyDate = DateUtils.formatDate(System.currentTimeMillis());
                        readNoteBean.mNote = item;
                        if (mBorrowId > 0) {
                            readNoteBean.mBorrowBookId = mBorrowId;
                        }
                        if (mBoughtId > 0) {
                            readNoteBean.mBuyBookId = mBoughtId;
                        }

                        DataRepository.getInstance().addNote(readNoteBean);

                        mAdapter.add(0, readNoteBean);
                        mAdapter.notifyDataSetChanged();
                    } else {//修改笔记
                        ReadNoteBean readNote = mAdapter.getItem(mPositionTag);
                        if (readNote != null) {
                            readNote.mNote.mContent = content;
                            readNote.mNote.mId = noteId;
                            readNote.mNote.mModifyDate = DateUtils.formatDate(System.currentTimeMillis());

                            DataRepository.getInstance().modifyNote(readNote);

                            mAdapter.remove(mPositionTag);
                            mAdapter.add(0, readNote);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                    if (mAdapter.getCount() > 0) {
                        mBookInfoView.setEmptyNotesTip(false);
                    }
                    break;
                case 1001:
                    boolean isCompensateSuccess = data.getBooleanExtra(RESULT_COMPENSATE_SUCCESS, false);
                    if (isCompensateSuccess) {
                        Intent intent = new Intent();
                        intent.putExtra(RESULT_COMPENSATE_SUCCESS, true);
                        setResult(RESULT_OK, intent);
                    }
                    finish();
                    break;
            }
        }
    }

    @Override
    public void finish() {
        if (mIsModifyNote) {
            setResult(RESULT_OK);
        } else if (mIsPraiseDone) {
            Intent intent = new Intent();
            intent.putExtra(RESULT_DATA_PRAISE, mIsPraised);
            setResult(RESULT_OK, intent);
        }
        super.finish();
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
        if (mStayLibraryDialog != null) {
            mStayLibraryDialog.clear();
        }
        mLibLists.clear();
        super.onDestroy();
        if (null != mPresenter) {
            mPresenter.detachView();
            mPresenter = null;
        }
        //取消事件订阅
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receviceLoginOut(AccountMessage loginMessage) {
        if (null != mPresenter && loginMessage.mIsLoginOut) {
            finish();
        }
    }

    private class BookInfoView implements ItemView {
        private ImageView mItemBookImage;
        private TextView mItemBookTitle;
        private TextView mItemBookAuthor;
        private TextView mItemBookAuthorCon;
        private TextView mItemBookPublishingCompany;
        private TextView mItemBookPublishingCompanyCon;
        private TextView mItemBookPublishingYear;
        private TextView mItemBookPublishingYearCon;
        private TextView mItemBookIsbn;
        private TextView mItemBookIsbnCon;
        private TextView mItemBookType;
        private TextView mItemBookTypeCon;
        private RelativeLayout mOneKeyToReNew;
        private DrawableCenterTextView mBookDetailPraiseBtn;
        private DrawableCenterTextView mBookDetailLostBtn;//第一个赔书按钮
        private DrawableCenterTextView mBookDetailWriteNoteBtn;
        private TextView mEmptyNotesTv;
        private TextView mHasOtherDaysTv;//剩余借书时间
        private TextView mDueTimeBuyTipTv;//逾期购买提示

        private View mBookInfoView;

        BookInfoView() {
            mBookInfoView = LayoutInflater.from(BorrowBookDetailActivity.this).inflate(R.layout.view_borrow_book_detail_head, null);
            mHasOtherDaysTv = (TextView)mBookInfoView.findViewById(R.id.end_time_book_tv);
            mItemBookImage = (ImageView) mBookInfoView.findViewById(R.id.item_book_image);
            mItemBookTitle = (TextView) mBookInfoView.findViewById(R.id.item_book_title);
            mItemBookAuthor = (TextView) mBookInfoView.findViewById(R.id.item_book_author);
            mItemBookAuthorCon = (TextView) mBookInfoView.findViewById(R.id.item_book_author_con);
            mItemBookPublishingCompany = (TextView) mBookInfoView.findViewById(R.id.item_book_publishing_company);
            mItemBookPublishingCompanyCon = (TextView) mBookInfoView.findViewById(R.id.item_book_publishing_company_con);
            mItemBookPublishingYear = (TextView) mBookInfoView.findViewById(R.id.item_book_publishing_year);
            mItemBookPublishingYearCon = (TextView) mBookInfoView.findViewById(R.id.item_book_publishing_year_con);
            mItemBookIsbn = (TextView) mBookInfoView.findViewById(R.id.item_book_isbn);
            mItemBookIsbnCon = (TextView) mBookInfoView.findViewById(R.id.item_book_isbn_con);
            mItemBookType = (TextView) mBookInfoView.findViewById(R.id.item_book_category);
            mItemBookTypeCon = (TextView) mBookInfoView.findViewById(R.id.item_book_category_con);
            mOneKeyToReNew = (RelativeLayout) mBookInfoView.findViewById(R.id.one_key_to_re_new);
            mBookDetailPraiseBtn = (DrawableCenterTextView) mBookInfoView.findViewById(R.id.item_book_thumbs_up);
            mBookDetailLostBtn = (DrawableCenterTextView) mBookInfoView.findViewById(R.id.item_book_lost_tv);
            mBookDetailWriteNoteBtn = (DrawableCenterTextView) mBookInfoView.findViewById(R.id.item_book_write_note_tv);
            mEmptyNotesTv = (TextView) mBookInfoView.findViewById(R.id.book_no_note_tip_tv);
            mDueTimeBuyTipTv = (TextView) mBookInfoView.findViewById(R.id.item_book_over_due_to_buy_tv);
            mBookInfoView.findViewById(R.id.item_book_publishing_library).setVisibility(View.GONE);
            mBookInfoView.findViewById(R.id.item_book_publishing_library_con).setVisibility(View.GONE);

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

        void setOnKeyToRenewClickListener(View.OnClickListener listener) {
            mOneKeyToReNew.setOnClickListener(listener);
        }

        void setLostBookClickListener(View.OnClickListener listener) {
            mBookDetailLostBtn.setOnClickListener(listener);
        }

        private void setBookInfo(BaseBookBean bean) {
            mItemBookTitle.setText(bean.mBook.mName);
            GlideApp.with(BorrowBookDetailActivity.this).load(bean.mBook.mCoverImg).placeholder(R.mipmap.ic_nopicture).error(R.mipmap.ic_nopicture).centerCrop().into(mItemBookImage);

            mItemBookAuthor.setText(getString(R.string.book_list_author));
            mItemBookAuthorCon.setText(bean.mAuthor.mName);
            mItemBookIsbn.setText(getString(R.string.stay_library2));//所在馆
            mItemBookIsbnCon.setText(bean.mLibrary.mName);//所在馆
            mItemBookPublishingCompany.setText(getString(R.string.book_list_publisher));
            mItemBookPublishingCompanyCon.setText(bean.mPress.mName);
            mItemBookPublishingYear.setText(getString(R.string.book_list_isbn));//isbn
            mItemBookPublishingYearCon.setText(bean.mBook.mIsbn);//isbn
            setThumbUpInfo(mIsPraised);
        }

        private void setBuyBookInfo(BoughtBookBean bean) {
            mItemBookTitle.setText(bean.mBook.mName);
            GlideApp.with(BorrowBookDetailActivity.this).load(bean.mBook.mCoverImg).placeholder(R.mipmap.ic_nopicture).error(R.mipmap.ic_nopicture).centerCrop().into(mItemBookImage);
            // 顺序：作者 isbn 馆店 时间  金额
            //item_book_author
            //item_book_publishing_company
            //item_book_publishing_year
            //item_book_isbn
            //item_book_category
            //item_book_publishing_library

            //作者
            mItemBookAuthor.setText(getString(R.string.book_list_author));//作者
            mItemBookAuthorCon.setText(bean.mAuthor.mName);//作者
            //isbn
            mItemBookPublishingCompany.setText(getString(R.string.book_list_isbn));
            mItemBookPublishingCompanyCon.setText(bean.mBook.mIsbn);
            //馆店
            mItemBookPublishingYear.setText(getString(R.string.stay_library2));
            mItemBookPublishingYearCon.setText(bean.mLibrary.mName);
            //时间
            mItemBookIsbn.setText(getString(R.string.bought_time));
            mItemBookIsbnCon.setText(bean.mBoughtTime);
            //金额
            mItemBookType.setText(getString(R.string.bought_money));
            mItemBookTypeCon.setText(MoneyUtils.formatMoney(bean.mBoughtPrice));

            setThumbUpInfo(mIsPraised);

            //购书架不显示剩余借书时间
            mHasOtherDaysTv.setVisibility(View.GONE);
        }


        void setBorrowInfo(BorrowBookBean borrowInfo) {
            setBookInfo(borrowInfo);
            if (!TextUtils.isEmpty(borrowInfo.mHistoryBackDate)) {
                //历史借阅
                //历史借阅的借阅日 - 时间段mItemBookType
                mItemBookType.setText(getString(R.string.borrow_book_date));
                mItemBookTypeCon.setText(borrowInfo.mHistoryBorrowDate);
                //历史借阅不显示剩余借书时间
                mHasOtherDaysTv.setVisibility(View.GONE);
                //历史借阅要显示 已还书 已赔书 已购买 却按钮不可点击
                mBookDetailLostBtn.setVisibility(View.VISIBLE);
                mBookDetailLostBtn.setEnabled(false);
                if (borrowInfo.mIsBuy) {//已购买
                    mBookDetailLostBtn.setText(getResources().getString(R.string.have_bought));
                    setBookStatusDrawable(R.mipmap.ic_re_buy_book);
                } else if (borrowInfo.mIsLost) {//已赔书
                    mBookDetailLostBtn.setText(getResources().getString(R.string.have_re_buy));
                    setBookStatusDrawable(R.mipmap.ic_re_buy_book);
                } else {//已还书
                    mBookDetailLostBtn.setText(getResources().getString(R.string.have_return));
                    setBookStatusDrawable(R.mipmap.ic_have_return_back_book);
                }

            } else {
                //当前借阅
                //当前借阅的借阅日 - 时间点 具体某一天mItemBookType
                mItemBookType.setText(getString(R.string.borrow_book_date));
                mItemBookTypeCon.setText(borrowInfo.mHistoryBorrowDate);
                //当前借阅显示剩余借阅时间
                mHasOtherDaysTv.setVisibility(View.VISIBLE);
                //当前借阅分 余XX天 今日到期 和 已逾期   剩余天数 是黄色 fbae4d  逾期和今日到期 是红色 fb755a
                String hasOtherDays;
                if (borrowInfo.mIsOverdue) {//逾期
                    //逾期显示提示逾期购买
                    mDueTimeBuyTipTv.setVisibility(View.VISIBLE);
                    hasOtherDays = getResources().getString(R.string.have_other_time_due_time);
                    mHasOtherDaysTv.setText(hasOtherDays);
                    mHasOtherDaysTv.setBackgroundColor(Color.parseColor("#fb755a"));//红色
                } else {//没有逾期
                    //没有逾期不显示提示逾期购买
                    mDueTimeBuyTipTv.setVisibility(View.GONE);
                    if (borrowInfo.mHasDays == 0) {//今日到期
                        hasOtherDays = getResources().getString(R.string.have_other_time_today);
                        mHasOtherDaysTv.setText(hasOtherDays);
                        mHasOtherDaysTv.setBackgroundColor(Color.parseColor("#fb755a"));//红色
                    } else {//余XX天
                        hasOtherDays = getResources().getString(R.string.have_other_time) + borrowInfo.mHasDays + getResources().getString(R.string.have_other_time_day);
                        mHasOtherDaysTv.setText(hasOtherDays);
                        mHasOtherDaysTv.setBackgroundColor(Color.parseColor("#fbae4d"));//黄色
                    }
                }
                //当前借阅 显示 赔书 且按钮可以点击
                mBookDetailLostBtn.setVisibility(View.VISIBLE);
                mBookDetailLostBtn.setEnabled(true);
                //当前借阅
                mOneKeyToReNew.setVisibility(borrowInfo.mOneKeyToRenew ? View.VISIBLE : View.GONE);


            }
        }

        /**
         * 设置图书在几种状态下的图片
         * @param p ：资源图片
         */
        private void setBookStatusDrawable(int p) {
            Drawable drawable = mContext.getResources().getDrawable(p);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mBookDetailLostBtn.setCompoundDrawables(drawable, null, null, null);
        }

        void setBoughtInfo(BoughtBookBean boughtInfo) {
            setBuyBookInfo(boughtInfo);
            mOneKeyToReNew.setVisibility(View.GONE);
        }

        /**
         * 设置是否点赞
         */
        void setThumbUpInfo(boolean isPraised) {
            mBookDetailPraiseBtn.setText(isPraised ? "已赞" : "点赞");
            Drawable flup;
            if (isPraised) {
                flup = getResources().getDrawable(R.mipmap.ic_click_have_praise);
            } else {
                flup = getResources().getDrawable(R.mipmap.ic_click_praise);
            }
            flup.setBounds(0, 0, flup.getMinimumWidth(), flup.getMinimumHeight());
            mBookDetailPraiseBtn.setCompoundDrawables(flup, null, null, null);
        }
    }
}
