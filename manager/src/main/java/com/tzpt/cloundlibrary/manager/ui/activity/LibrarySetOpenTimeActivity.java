package com.tzpt.cloundlibrary.manager.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseActivity;
import com.tzpt.cloundlibrary.manager.bean.SearchAddressBean;
import com.tzpt.cloundlibrary.manager.ui.contract.LibrarySetOpenTimeContract;
import com.tzpt.cloundlibrary.manager.ui.presenter.LibrarySetOpenTimePresenter;
import com.tzpt.cloundlibrary.manager.utils.DateUtils;
import com.tzpt.cloundlibrary.manager.utils.KeyboardUtils;
import com.tzpt.cloundlibrary.manager.widget.MultiStateLayout;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;
import com.tzpt.cloundlibrary.manager.widget.dialog.TimePickerDialog;
import com.tzpt.cloundlibrary.manager.widget.dialog.VerifyPasswordDialogFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 点亮时间设置
 * Created by Administrator on 2017/6/27.
 */

public class LibrarySetOpenTimeActivity extends BaseActivity implements
        LibrarySetOpenTimeContract.View {
    public static final int TODAY_AM_START_TIME = 0;
    public static final int TODAY_AM_END_TIME = 1;
    public static final int TODAY_PM_START_TIME = 2;
    public static final int TODAY_PM_END_TIME = 3;
    public static final int LONG_AM_START_TIME = 4;
    public static final int LONG_AM_END_TIME = 5;
    public static final int LONG_PM_START_TIME = 6;
    public static final int LONG_PM_END_TIME = 7;


    public static void startActivity(Context context) {
        Intent intent = new Intent(context, LibrarySetOpenTimeActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.multi_state_layout)
    MultiStateLayout mMultiStateLayout;
    @BindView(R.id.today_am_start_tv)
    TextView mTodayAmStartTv;
    @BindView(R.id.today_am_divider_tv)
    TextView mTodayAmDividerTv;
    @BindView(R.id.today_am_end_tv)
    TextView mTodayAmEndTv;
    @BindView(R.id.today_pm_start_tv)
    TextView mTodayPmStartTv;
    @BindView(R.id.today_pm_end_tv)
    TextView mTodayPmEndTv;
    @BindView(R.id.monday_cb)
    CheckBox mMondayCb;
    @BindView(R.id.tuesday_cb)
    CheckBox mTuesdayCb;
    @BindView(R.id.wednesday_cb)
    CheckBox mWednesdayCb;
    @BindView(R.id.thursday_cb)
    CheckBox mThursdayCb;
    @BindView(R.id.friday_cb)
    CheckBox mFridayCb;
    @BindView(R.id.saturday_cb)
    CheckBox mSaturdayCb;
    @BindView(R.id.sunday_cb)
    CheckBox mSundayCb;
    @BindView(R.id.regular_am_start_tv)
    TextView mRegularAmStartTv;
    @BindView(R.id.regular_am_end_tv)
    TextView mRegularAmEndTv;
    @BindView(R.id.regular_pm_start_tv)
    TextView mRegularPmStartTv;
    @BindView(R.id.regular_pm_end_tv)
    TextView mRegularPmEndTv;
    @BindView(R.id.phone_et)
    EditText mPhoneEt;
    @BindView(R.id.address_et)
    TextView mAddressEt;
    @BindView(R.id.house_num_et)
    EditText mHouseNumEt;

    private LibrarySetOpenTimePresenter mPresenter;
    private List<String> mWeeks = new ArrayList<>();

    private long mNowTime;

    @OnClick({R.id.today_am_start_tv, R.id.today_am_end_tv, R.id.today_pm_start_tv,
            R.id.today_pm_end_tv, R.id.monday_cb, R.id.tuesday_cb, R.id.wednesday_cb,
            R.id.thursday_cb, R.id.friday_cb, R.id.saturday_cb, R.id.sunday_cb,
            R.id.regular_am_start_tv, R.id.regular_am_end_tv, R.id.confirm_btn, R.id.titlebar_left_btn,
            R.id.regular_pm_start_tv, R.id.regular_pm_end_tv, R.id.address_et})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.today_am_start_tv:
                showTimeDialog(10, 0, mTodayAmStartTv.getText().toString().trim(), "选择今日早上开放时间", mNowTime);
                break;
            case R.id.today_am_end_tv:
                showTimeDialog(10, 1, mTodayAmEndTv.getText().toString().trim(), "选择今日早上关闭时间", mNowTime);
                break;
            case R.id.today_pm_start_tv:
                showTimeDialog(11, 2, mTodayPmStartTv.getText().toString().trim(), "选择今日下午开放时间", mNowTime);
                break;
            case R.id.today_pm_end_tv:
                showTimeDialog(11, 3, mTodayPmEndTv.getText().toString().trim(), "选择今日下午关闭时间", mNowTime);
                break;
            case R.id.regular_am_start_tv:
                showTimeDialog(12, 4, mRegularAmStartTv.getText().toString().trim(), "选择早上开放时间", 0);
                break;
            case R.id.regular_am_end_tv:
                showTimeDialog(12, 5, mRegularAmEndTv.getText().toString().trim(), "选择早上关闭时间", 0);
                break;
            case R.id.regular_pm_start_tv:
                showTimeDialog(13, 6, mRegularPmStartTv.getText().toString().trim(), "选择下午开放时间", 0);
                break;
            case R.id.regular_pm_end_tv:
                showTimeDialog(13, 7, mRegularPmEndTv.getText().toString().trim(), "选择下午关闭时间", 0);
                break;
            case R.id.confirm_btn:
                mPresenter.verifyOpenTimePhoneNumber(mPhoneEt.getText().toString().trim(),
                        mTodayAmStartTv.getText().toString().trim(), mTodayAmEndTv.getText().toString().trim(),
                        mTodayPmStartTv.getText().toString().trim(), mTodayPmEndTv.getText().toString().trim(),
                        mRegularAmStartTv.getText().toString().trim(), mRegularAmEndTv.getText().toString().trim());
                break;
            case R.id.address_et://进入定位界面
                SearchAddressActivity.startActivity(this);
                break;
        }
    }

    private VerifyPasswordDialogFragment.VerifyPasswordListener mVerifyPasswordListener =
            new VerifyPasswordDialogFragment.VerifyPasswordListener() {
                @Override
                public void onConfirmClickComplete(String password) {
                    mPresenter.setLightSelect(password, mPhoneEt.getText().toString().trim(),
                            mTodayAmStartTv.getText().toString().trim(), mTodayAmEndTv.getText().toString().trim(),
                            mTodayPmStartTv.getText().toString().trim(), mTodayPmEndTv.getText().toString().trim(),
                            mRegularAmStartTv.getText().toString().trim(), mRegularAmEndTv.getText().toString().trim(),
                            mRegularPmStartTv.getText().toString().trim(), mRegularPmEndTv.getText().toString().trim(),
                            mWeeks, mHouseNumEt.getText().toString().trim());
                    KeyboardUtils.hideSoftInput(LibrarySetOpenTimeActivity.this);
                }
            };

    private TimePickerDialog.OnTimeChangeListener mOnTimeChangeListener = new TimePickerDialog.OnTimeChangeListener() {
        @Override
        public void onChange(int type, String content) {
            setTimePickerInfo(type, content);
        }
    };

    /**
     * 设置选择信息
     *
     * @param chooseWheelViewType 选择时间类型
     * @param selectTime          选择时间
     */
    private void setTimePickerInfo(int chooseWheelViewType, final String selectTime) {
        switch (chooseWheelViewType) {
            case TODAY_AM_START_TIME:
                //如果选择的开始时间大于结束时间，则return  开始时间不能大于结束时间！
                String todayEndTime = mTodayAmEndTv.getText().toString().trim();
                if (!TextUtils.isEmpty(todayEndTime)) {
                    boolean startTimelittleThanEndTime = DateUtils.compareStartTimeAndEndTime(selectTime, todayEndTime);
                    if (startTimelittleThanEndTime || todayEndTime.equals("00:00")) {
                        mTodayAmStartTv.setText(selectTime);
                    } else {
                        showDialogMsg(R.string.Start_time_must_smaller_than_the_end_time);
                    }
                }
                break;
            case TODAY_AM_END_TIME:
                String todayStartTime = mTodayAmStartTv.getText().toString().trim();
                if (!TextUtils.isEmpty(todayStartTime)) {
                    boolean startTimelittleThanEndTime = DateUtils.compareStartTimeAndEndTime(todayStartTime, selectTime);
                    if (startTimelittleThanEndTime || todayStartTime.equals("00:00")) {
                        mTodayAmEndTv.setText(selectTime);
                    } else {
                        showDialogMsg(R.string.Start_time_must_smaller_than_the_end_time);
                    }
                }
                break;
            case TODAY_PM_START_TIME:
                String todayPMEndTime = mTodayPmEndTv.getText().toString().trim();
                if (!TextUtils.isEmpty(todayPMEndTime)) {
                    boolean startTimelittleThanEndTime = DateUtils.compareStartTimeAndEndTime(selectTime, todayPMEndTime);
                    if (startTimelittleThanEndTime || todayPMEndTime.equals("00:00")) {
                        mTodayPmStartTv.setText(selectTime);
                    } else {
                        showDialogMsg(R.string.Start_time_must_smaller_than_the_end_time);
                    }
                }
                break;
            case TODAY_PM_END_TIME:
                String todayPMStartTime = mTodayPmStartTv.getText().toString().trim();
                if (!TextUtils.isEmpty(todayPMStartTime)) {
                    boolean startTimelittleThanEndTime = DateUtils.compareStartTimeAndEndTime(todayPMStartTime, selectTime);
                    if (startTimelittleThanEndTime || todayPMStartTime.equals("00:00")) {
                        mTodayPmEndTv.setText(selectTime);
                    } else {
                        showDialogMsg(R.string.Start_time_must_smaller_than_the_end_time);
                    }
                }
                break;
            case LONG_AM_START_TIME:
                String longAMEndTime = mRegularAmEndTv.getText().toString().trim();
                if (!TextUtils.isEmpty(longAMEndTime)) {
                    boolean startTimelittleThanEndTime = DateUtils.compareStartTimeAndEndTime(selectTime, longAMEndTime);
                    if (startTimelittleThanEndTime || longAMEndTime.equals("00:00")) {
                        mRegularAmStartTv.setText(selectTime);
                    } else {
                        showDialogMsg(R.string.Start_time_must_smaller_than_the_end_time);
                    }
                }
                break;
            case LONG_AM_END_TIME:
                String mAMStartTime = mRegularAmStartTv.getText().toString().trim();
                if (!TextUtils.isEmpty(mAMStartTime)) {
                    boolean startTimelittleThanEndTime = DateUtils.compareStartTimeAndEndTime(mAMStartTime, selectTime);
                    if (startTimelittleThanEndTime || mAMStartTime.equals("00:00")) {
                        mRegularAmEndTv.setText(selectTime);
                    } else {
                        showDialogMsg(R.string.Start_time_must_smaller_than_the_end_time);
                    }
                }
                break;
            case LONG_PM_START_TIME:
                String longPMEndTime = mRegularPmEndTv.getText().toString().trim();
                if (!TextUtils.isEmpty(longPMEndTime)) {
                    boolean startTimelittleThanEndTime = DateUtils.compareStartTimeAndEndTime(selectTime, longPMEndTime);
                    if (startTimelittleThanEndTime || longPMEndTime.equals("00:00")) {
                        mRegularPmStartTv.setText(selectTime);
                    } else {
                        showDialogMsg(R.string.Start_time_must_smaller_than_the_end_time);
                    }
                }
                break;
            case LONG_PM_END_TIME:
                String mPMStartTime = mRegularPmStartTv.getText().toString().trim();
                if (!TextUtils.isEmpty(mPMStartTime)) {
                    boolean startTimelittleThanEndTime = DateUtils.compareStartTimeAndEndTime(mPMStartTime, selectTime);
                    if (startTimelittleThanEndTime || mPMStartTime.equals("00:00")) {
                        mRegularPmEndTv.setText(selectTime);
                    } else {
                        showDialogMsg(R.string.Start_time_must_smaller_than_the_end_time);
                    }
                }
                break;
        }
    }

    // 选择开放时间控件
    private void showTimeDialog(int am, int chooseType, String time, String tips, long nowTime) {
        TimePickerDialog dialog = new TimePickerDialog(this);
        String[] times = time.split(":");
        dialog.initDatas(am, chooseType, times[0], times[1], nowTime);
        dialog.setTitle(tips);
        dialog.setOnTimeChangeListener(mOnTimeChangeListener);
        dialog.show();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_set_open_time;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.mipmap.ic_arrow_left);

    }

    @Override
    public void initDatas() {
        mPresenter = new LibrarySetOpenTimePresenter();
        mPresenter.attachView(this);
        mPresenter.getLibraryInfo();
        mPresenter.getLibraryOpenTimeInfo();
        EventBus.getDefault().register(this);
    }

    @Override
    public void configViews() {
        mMondayCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mWeeks.add("1");
                } else {
                    mWeeks.remove("1");
                }
            }
        });

        mTuesdayCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mWeeks.add("2");
                } else {
                    mWeeks.remove("2");
                }
            }
        });

        mWednesdayCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mWeeks.add("3");
                } else {
                    mWeeks.remove("3");
                }
            }
        });

        mThursdayCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mWeeks.add("4");
                } else {
                    mWeeks.remove("4");
                }
            }
        });

        mFridayCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mWeeks.add("5");
                } else {
                    mWeeks.remove("5");
                }
            }
        });

        mSaturdayCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mWeeks.add("6");
                } else {
                    mWeeks.remove("6");
                }
            }
        });

        mSundayCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mWeeks.add("7");
                } else {
                    mWeeks.remove("7");
                }
            }
        });
    }

    @Override
    public void setTitle(String info) {
        mCommonTitleBar.setTitle(info);
    }

    @Override
    public void setLibraryName(String name) {

    }

    @Override
    public void setNowTime(long nowTime) {
        mNowTime = nowTime;
    }

    //设置联系电话
    @Override
    public void setContactTel(String telNum) {
        mPhoneEt.setText(telNum);
        mPhoneEt.setSelection(telNum.length());
    }

    //设置今日上午时间是否可用
    @Override
    public void setTodayAmAvailable(boolean isAm) {
        mTodayAmStartTv.setClickable(isAm);
        mTodayAmEndTv.setClickable(isAm);
        mTodayAmStartTv.setTextColor(isAm ? Color.parseColor("#333333") : Color.parseColor("#999999"));
        mTodayAmEndTv.setTextColor(isAm ? Color.parseColor("#333333") : Color.parseColor("#999999"));
        mTodayAmDividerTv.setTextColor(isAm ? Color.parseColor("#333333") : Color.parseColor("#999999"));
    }

    //设置选择星期集合
    @Override
    public void setWeekInfo(Integer[] week) {
        if (null != week && week.length > 0) {
            List<Integer> weekList = Arrays.asList(week);
            mMondayCb.setChecked(weekList.contains(1));
            mTuesdayCb.setChecked(weekList.contains(2));
            mWednesdayCb.setChecked(weekList.contains(3));
            mThursdayCb.setChecked(weekList.contains(4));
            mFridayCb.setChecked(weekList.contains(5));
            mSaturdayCb.setChecked(weekList.contains(6));
            mSundayCb.setChecked(weekList.contains(7));
        } else {
            mMondayCb.setChecked(false);
            mTuesdayCb.setChecked(false);
            mWednesdayCb.setChecked(false);
            mThursdayCb.setChecked(false);
            mFridayCb.setChecked(false);
            mSaturdayCb.setChecked(false);
            mSundayCb.setChecked(false);
        }
    }

    @Override
    public void setLongAMOpenTimeStart(String time) {
        mRegularAmStartTv.setText(time);
    }

    @Override
    public void setLongAMOpenTimeEnd(String time) {
        mRegularAmEndTv.setText(time);
    }

    @Override
    public void setLongPMTimeStart(String time) {
        mRegularPmStartTv.setText(time);
    }

    @Override
    public void setLongPMTimeEnd(String time) {
        mRegularPmEndTv.setText(time);
    }

    @Override
    public void setTodayAMOpenTimeStart(String time) {
        mTodayAmStartTv.setText(time);
    }

    @Override
    public void setTodayAMOpenTimeEnd(String time) {
        mTodayAmEndTv.setText(time);
    }

    @Override
    public void setTodayPMOpenTimeStart(String time) {
        mTodayPmStartTv.setText(time);
    }

    @Override
    public void setTodayPMOpenTimeEnd(String time) {
        mTodayPmEndTv.setText(time);
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
    public void showLoading() {
        showDialog("发送中...");
    }

    @Override
    public void hideLoading() {
        dismissDialog();
    }

    @Override
    public void showProgressLoading() {
        mMultiStateLayout.showProgress();
    }

    @Override
    public void hideProgressLoading() {
        mMultiStateLayout.showContentView();
    }

    @Override
    public void showDialogSetSuccess(int msgId) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, getString(msgId));
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                mPresenter.getLibraryOpenTimeInfo();
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 设置定位信息
     */
    public void setLocationAddress(String content) {
        mAddressEt.setText(content.trim());
        mAddressEt.setTextColor(Color.parseColor("#333333"));
    }

    /**
     * 设置门牌号
     *
     * @param number 门牌号
     */
    @Override
    public void setLocationAddressNumber(String number) {
        mHouseNumEt.setText(number);
        mHouseNumEt.setSelection(number.length());
    }

    //展示密码弹出框
    @Override
    public void showVerifyPasswordDialog() {
        VerifyPasswordDialogFragment dialogFragment = new VerifyPasswordDialogFragment();
        dialogFragment.show(getFragmentManager(), "VerifyPasswordDialog");
        dialogFragment.setVerifyPasswordListener(mVerifyPasswordListener);
    }

    @Override
    public void showError(int msgId) {
        mMultiStateLayout.showError();
        mMultiStateLayout.showRetryError(mRetryClickListener);
    }

    @Override
    public void noPermissionPrompt(int kickedOffline) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, getString(kickedOffline));
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                finish();
                LoginActivity.startActivity(LibrarySetOpenTimeActivity.this);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void showTimeoutDialog() {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, "操作超时！");
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                finish();
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    View.OnClickListener mRetryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mPresenter.getLibraryOpenTimeInfo();
        }
    };

    //接收选中区域地址
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveAddress(SearchAddressBean bean) {
        if (null != bean) {
            mPresenter.setSearchAddressBean(bean);
        }
    }

}
