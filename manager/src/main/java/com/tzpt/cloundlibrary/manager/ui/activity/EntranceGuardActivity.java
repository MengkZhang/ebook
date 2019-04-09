package com.tzpt.cloundlibrary.manager.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseActivity;
import com.tzpt.cloundlibrary.manager.ui.contract.EntranceGuardContract;
import com.tzpt.cloundlibrary.manager.ui.presenter.EntranceGuardPresenter;
import com.tzpt.cloundlibrary.manager.utils.KeyboardUtils;
import com.tzpt.cloundlibrary.manager.utils.SoundManager;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 门禁检查
 * Created by Administrator on 2017/7/12.
 */

public class EntranceGuardActivity extends BaseActivity implements EntranceGuardContract.View {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, EntranceGuardActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.reader_name_number_tv)
    TextView mReaderNameNumberTv;
    @BindView(R.id.borrowable_sum_tv)
    TextView mBorrowableSumTv;
    @BindView(R.id.usable_deposit_tv)
    TextView mUsableDepositTv;
    @BindView(R.id.edit_code_et)
    EditText mEditCodeEt;
    @BindView(R.id.scan_code_btn)
    ImageButton mScanCodeBtn;
    @BindView(R.id.flag_iv)
    ImageView mFlagIv;
    @BindView(R.id.info_tv)
    TextView mInfoTv;
    @BindView(R.id.confirm_btn)
    Button mConfirmBtn;

    private SoundManager mSoundManager;
    private EntranceGuardPresenter mPresenter;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1000) {
                setDefaultUI();
            }
        }
    };

    @OnClick({R.id.scan_code_btn, R.id.confirm_btn, R.id.titlebar_left_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.scan_code_btn:
                EntranceGuardScanActivity.startActivity(EntranceGuardActivity.this);
                break;
            case R.id.confirm_btn:
                setDefaultUI();
                break;
            case R.id.titlebar_left_btn:
                finish();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_entrance_guard;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setTitle("门禁检查");
        mCommonTitleBar.setLeftBtnIcon(R.mipmap.ic_arrow_left);
    }

    @Override
    public void initDatas() {
        mPresenter = new EntranceGuardPresenter();
        mPresenter.attachView(this);
        mPresenter.getLoginLibraryInfo();
        initSound();
    }

    @Override
    public void configViews() {
        mEditCodeEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                KeyboardUtils.hideSoftInput(mEditCodeEt);
                String barNumber = mEditCodeEt.getText().toString().trim();
                //执行自动补全
                mPresenter.entranceCheck(barNumber);
                return false;
            }
        });
    }

    /**
     * 初始化声音
     *
     * @param
     */
    private void initSound() {
        mSoundManager = new SoundManager();
        mSoundManager.initSound(this);
    }

    /**
     * 播放门禁声音0 1 2
     *
     * @param type
     */
    public void playSountForEntance(int type) {
        mSoundManager.startPlaySoundForEntrance(this, type);
        mSoundManager.stopPlaySoundForEntrance(this, type);
    }

    @Override
    public void setCodeEditTextHint(String hint) {
        mEditCodeEt.setHint(hint);
    }

    @Override
    public void setReaderName(String name) {
        mReaderNameNumberTv.setText(name);
    }

    @Override
    public void setBorrowableSum(String sum) {
        mBorrowableSumTv.setText(sum);
    }

    @Override
    public void setDepositOrPenalty(String info) {
        mUsableDepositTv.setText(info);
    }

    @Override
    public void setEntranceStatePass() {
        playSountForEntance(0);
        mInfoTv.setTextColor(Color.parseColor("#20A920"));
        mInfoTv.setText("通过！");
        mFlagIv.setImageResource(R.mipmap.ic_entrance_pass);
        mFlagIv.setVisibility(View.VISIBLE);
        mInfoTv.setVisibility(View.VISIBLE);
        mConfirmBtn.setVisibility(View.GONE);

        resetToDefaultUI();
    }

    @Override
    public void setEntranceStateError(int msgId) {
        playSountForEntance(1);
        mInfoTv.setTextColor(Color.parseColor("#ff0000"));
        mInfoTv.setText(getText(msgId));
        mFlagIv.setImageResource(R.mipmap.ic_entrance_error);
        mFlagIv.setVisibility(View.VISIBLE);
        mInfoTv.setVisibility(View.VISIBLE);
        mConfirmBtn.setVisibility(View.GONE);

        resetToDefaultUI();
    }

    @Override
    public void setEntranceStateLost(String readerInfo, String phone) {
        setDefaultUI();
        playSountForEntance(2);
        String lostLocalInfo = "办借已超3小时，请核实身份:";
        mInfoTv.setText(Html.fromHtml("<html>" + lostLocalInfo + "<font color='#333333'><br>" + readerInfo + "<br>" + phone + "<br>" + "</font></htm>"));
        mInfoTv.setTextColor(Color.parseColor("#ff0000"));
        mFlagIv.setVisibility(View.GONE);
        mConfirmBtn.setVisibility(View.VISIBLE);
        mInfoTv.setVisibility(View.VISIBLE);
    }

    @Override
    public void showDialogMsg(String msg) {
        showMessageDialog(msg);
    }

    @Override
    public void showDialogMsg(int msgId) {
        showMessageDialog(getString(msgId));
    }

    @Override
    public void noPermissionPrompt(int msgId) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, getString(msgId));
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                finish();
                LoginActivity.startActivity(EntranceGuardActivity.this);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    private void setDefaultUI() {
        mReaderNameNumberTv.setText("");
        mBorrowableSumTv.setText("");
        mUsableDepositTv.setText("");
        mFlagIv.setVisibility(View.GONE);
        mConfirmBtn.setVisibility(View.GONE);
        mInfoTv.setVisibility(View.GONE);
    }

    private void resetToDefaultUI() {
        mHandler.sendEmptyMessageDelayed(1000, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(1000);
        mPresenter.detachView();
    }
}
