package com.tzpt.cloudlibrary.ui.account.borrow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.utils.ToastUtils;
import com.tzpt.cloudlibrary.widget.CustomDialog;
import com.tzpt.cloudlibrary.widget.LoadingProgressView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 读书笔记编辑
 */
public class ReadingNoteEditActivity extends BaseActivity implements
        ReadingNoteEditContract.View {
    private static final String FROM_TYPE = "from_type";
    private static final String BORROW_ID = "borrow_id";
    private static final String NOTE_ID = "note_id";
    private static final String BUY_ID = "buy_id";
    private static final String NOTE_CONTENT = "note_content";
    private static final String RESULT_DATA_NEW_NOTE = "new_note";

    public static void startActivityForResult(Activity activity, long borrowId, int requestCode) {
        Intent intent = new Intent(activity, ReadingNoteEditActivity.class);
        intent.putExtra(BORROW_ID, borrowId);
        intent.putExtra(NOTE_ID, -1);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void startActivityForBoughtResult(Activity activity, long boughtId, int requestCode) {
        Intent intent = new Intent(activity, ReadingNoteEditActivity.class);
        intent.putExtra(BUY_ID, boughtId);
        intent.putExtra(NOTE_ID, -1);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void startActivityForResult(Activity context, long borrowId, long noteId, String content, int requestCode) {
        Intent intent = new Intent(context, ReadingNoteEditActivity.class);
        intent.putExtra(BORROW_ID, borrowId);
        intent.putExtra(NOTE_ID, noteId);
        intent.putExtra(NOTE_CONTENT, content);
        intent.putExtra(FROM_TYPE, 1);
        context.startActivityForResult(intent, requestCode);
    }

    public static void startActivityForResult(Activity context, long buyId, long noteId, int requestCode) {
        Intent intent = new Intent(context, ReadingNoteEditActivity.class);
        intent.putExtra(BUY_ID, buyId);
        intent.putExtra(NOTE_ID, noteId);
        intent.putExtra(FROM_TYPE, 2);
        context.startActivityForResult(intent, requestCode);
    }

    @BindView(R.id.reading_notes_content_et)
    EditText mReadingNotesContentEt;
    @BindView(R.id.progress_layout)
    LoadingProgressView mProgressLayout;

    private ReadingNoteEditPresenter mPresenter;

    private long mBorrowBookId;
    private long mBuyBookId;
    private long mNoteId = -1;
    private String mNewContent;

    @OnClick({R.id.titlebar_left_btn, R.id.titlebar_right_txt_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.titlebar_right_txt_btn:
                String content = mReadingNotesContentEt.getText().toString().trim();
                mPresenter.saveUserReadingNote(mBorrowBookId, mBuyBookId, mNoteId, content);
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_reading_note_edit;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("写笔记");
        mCommonTitleBar.setRightBtnText("保存");
    }

    @Override
    public void initDatas() {
        mPresenter = new ReadingNoteEditPresenter();
        mPresenter.attachView(this);
        Intent intent = getIntent();

        mBuyBookId = intent.getLongExtra(BUY_ID, 0);
        mBorrowBookId = intent.getLongExtra(BORROW_ID, 0);
        mNoteId = intent.getLongExtra(NOTE_ID, -1);

        String noteContent = intent.getStringExtra(NOTE_CONTENT);
        if (!TextUtils.isEmpty(noteContent)) {
            mReadingNotesContentEt.setText(noteContent);
            mReadingNotesContentEt.setSelection(noteContent.length());
            mReadingNotesContentEt.requestFocus();
        }
    }

    @Override
    public void configViews() {

    }

    @Override
    public void saveUserReadingNoteSuccess(long noteId) {
        mNoteId = noteId;
        mNewContent = mReadingNotesContentEt.getText().toString().trim();
        showToastTips(R.string.save_success);
        finish();
    }

    @Override
    public void showToastTips(int msgId) {
        ToastUtils.showSingleToast(msgId);
    }

    @Override
    public void showProgressDialog() {
        mProgressLayout.showProgressLayout();
    }

    @Override
    public void dismissProgressDialog() {
        mProgressLayout.hideProgressLayout();
    }

    @Override
    public void pleaseLoginTip() {
        AccountMessage accountMessage = new AccountMessage();
        accountMessage.mIsLoginOut = true;
        accountMessage.mIsToUserCenter = true;
        EventBus.getDefault().post(accountMessage);
        finish();
    }

    @Override
    public void finish() {
        if (!TextUtils.isEmpty(mNewContent)) {
            Intent intent = new Intent();
            intent.putExtra("note_id", mNoteId);
            intent.putExtra(RESULT_DATA_NEW_NOTE, mNewContent);
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
    public void showDialogTips(String tips) {
        if (TextUtils.isEmpty(tips)) {
            return;
        }
        //自定义对话框
        final CustomDialog customDialog = new CustomDialog(this,
                R.style.DialogTheme, tips);
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
        customDialog.setText(tips);
        customDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mPresenter) {
            mPresenter.detachView();
            mPresenter = null;
        }
    }
}
