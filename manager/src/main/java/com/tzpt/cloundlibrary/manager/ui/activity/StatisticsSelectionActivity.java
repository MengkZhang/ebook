package com.tzpt.cloundlibrary.manager.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseActivity;
import com.tzpt.cloundlibrary.manager.bean.SingleSelectionConditionBean;
import com.tzpt.cloundlibrary.manager.bean.StatisticsClassifyBean;
import com.tzpt.cloundlibrary.manager.bean.StatisticsConditionBean;
import com.tzpt.cloundlibrary.manager.ui.contract.StatisticsSelectionContract;
import com.tzpt.cloundlibrary.manager.ui.presenter.StatisticsSelectionPresenter;
import com.tzpt.cloundlibrary.manager.utils.KeyboardUtils;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;
import com.tzpt.cloundlibrary.manager.widget.popupwindow.FlowIntoSearchTypePPW;
import com.tzpt.cloundlibrary.manager.widget.popupwindow.FlowIntoSelectorPPW;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 选项界面
 */
public class StatisticsSelectionActivity extends BaseActivity implements
        StatisticsSelectionContract.View,
        DatePickerDialog.OnDateSetListener {

    private static final String STATICS_CLASSIFY = "statics_from_type";

    public static void startActivity(Context context, StatisticsClassifyBean classifyBean) {
        Intent intent = new Intent(context, StatisticsSelectionActivity.class);
        intent.putExtra(STATICS_CLASSIFY, classifyBean);
        context.startActivity(intent);
    }

    @BindView(R.id.library_selector_tv)
    public TextView mSingleSelectionConditionTitleTv;   //选择显示控件
    @BindView(R.id.library_selector_rl)
    public RelativeLayout mLibrarySelectorLayout; //请选择
    @BindView(R.id.start_content_ev)
    public EditText mStartContent;                //开始输入内容
    @BindView(R.id.end_content_ev)
    public EditText mEndContent;                  //结束输入内容
    @BindView(R.id.start_content_parent)
    public RelativeLayout mStartContentParent;    //开始选择控件
    @BindView(R.id.end_content_parent)
    public RelativeLayout mEndContentParent;      //结束选择控件
    @BindView(R.id.flow_selector_tv)
    public TextView mStatisticsSelectionTitleTv;  //选择显示控件
    @BindView(R.id.edit_ll_parent)
    public LinearLayout mEditLlParent;
    @BindView(R.id.date_start_selector_img)
    public ImageView mDateStartSelector;
    @BindView(R.id.date_end_selector_img)
    public ImageView mDateEndSelector;
    @BindView(R.id.content_space)
    public TextView mContentSpace;
    @BindView(R.id.add_new_btn)
    public Button mAddNewBtn;

    private FlowIntoSearchTypePPW mStatisticsConditionPPW;   //第一选择控件窗口
    private FlowIntoSelectorPPW mSingleSelectorPPW;       //第二选择控件窗口
    //choose Time selector
    private DatePickerDialog dpd;
    private static final int START_DATE = 0;
    private static final int END_DATE = 1;
    private int startStatus = START_DATE;
    private static final String DATE_PICKERD_DIALOG = "Datepickerdialog";
    private boolean setDateClickAble = true;

    private StatisticsSelectionPresenter mPresenter;
    private StatisticsConditionBean mCurrentStatisticsConditionBean;
    private SingleSelectionConditionBean mCurrentSingleSelectionConditionBean;
    private StatisticsClassifyBean mClassifyBean;
    private boolean mIsShowSingleSelectorPPW = false;

    @OnClick({R.id.titlebar_left_btn, R.id.search_btn, R.id.library_selector_rl,
            R.id.date_start_selector_img, R.id.date_end_selector_img, R.id.flow_selector_parent,
            R.id.start_content_ev, R.id.end_content_ev, R.id.add_new_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.search_btn:           //搜索
                startSearchFlowManageList();
                break;
            case R.id.library_selector_rl:  //请选择
                KeyboardUtils.hideSoftInput(StatisticsSelectionActivity.this);
                if (null != mSingleSelectorPPW && !mSingleSelectorPPW.isShowing()) {
                    if (mSingleSelectorPPW.getCount() > 0) {
                        mSingleSelectorPPW.showAsDropDown(mSingleSelectionConditionTitleTv, 0, 0);
                    } else {
                        mIsShowSingleSelectorPPW = true;
                        mCurrentStatisticsConditionBean.getSingleSelectionCondition();
                    }
                }
                break;
            case R.id.date_start_selector_img:  //开始选择日期
                if (!setDateClickAble) {
                    return;
                }
                this.startStatus = START_DATE;
                showDateDialog(START_DATE);
                break;
            case R.id.date_end_selector_img:    //结束选择日期
                if (!setDateClickAble) {
                    return;
                }
                this.startStatus = END_DATE;
                showDateDialog(END_DATE);
                break;
            case R.id.start_content_ev:         //开始选择日期
                if (!setDateClickAble) {
                    return;
                }
                this.startStatus = START_DATE;
                showDateDialog(START_DATE);
                break;
            case R.id.end_content_ev:           //结束选择日期
                if (!setDateClickAble) {
                    return;
                }
                this.startStatus = END_DATE;
                showDateDialog(END_DATE);
                break;
            case R.id.flow_selector_parent:     //弹出选择菜单
                //关闭软件盘
                KeyboardUtils.hideSoftInput(StatisticsSelectionActivity.this);
                if (null != mStatisticsConditionPPW && !mStatisticsConditionPPW.isShowing()) {
                    mStatisticsConditionPPW.showAsDropDown(mStatisticsSelectionTitleTv, 0, 0);
                }
                break;
            case R.id.add_new_btn:
                FlowManageInLibraryListActivity.startActivity(this);
                break;
        }
    }

    /**
     *
     */
    private FlowIntoSearchTypePPW.CallbackOptionBean callback = new FlowIntoSearchTypePPW.CallbackOptionBean() {
        @Override
        public void callbackOptionBean(StatisticsConditionBean bean) {
            if (null == bean) {
                return;
            }
            mStatisticsSelectionTitleTv.setText(bean.getTitle());
            //处理选择选项后
            handleSelectStatus(bean);
        }
    };

    /**
     * 第二选择结果
     */
    private FlowIntoSelectorPPW.CallbackSelectorOptionBean callResult = new FlowIntoSelectorPPW.CallbackSelectorOptionBean() {
        @Override
        public void callbackOptionBean(SingleSelectionConditionBean bean) {
            mSingleSelectionConditionTitleTv.setText(bean.getName());
            mCurrentSingleSelectionConditionBean = bean;
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        KeyboardUtils.hideSoftInput(StatisticsSelectionActivity.this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_statistics_selection;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.mipmap.ic_arrow_left);
    }

    @Override
    public void initDatas() {
        Intent intent = getIntent();
        mClassifyBean = (StatisticsClassifyBean) intent.getSerializableExtra(STATICS_CLASSIFY);
        mCommonTitleBar.setTitle(mClassifyBean.getTitle());
        mPresenter = new StatisticsSelectionPresenter();
        mPresenter.attachView(this);
    }

    @Override
    public void configViews() {
        mStatisticsConditionPPW = new FlowIntoSearchTypePPW(this, callback);
        mSingleSelectorPPW = new FlowIntoSelectorPPW(this, callResult);

        if (mClassifyBean.getId() == 9) {
            mAddNewBtn.setVisibility(View.VISIBLE);
        } else {
            mAddNewBtn.setVisibility(View.GONE);
        }

        //初始设置选项列表
        mPresenter.initOptionDataByFromType(mClassifyBean.getId());

    }

    /**
     * 初始化设置选项列表
     *
     * @param optionList 选项列表
     */
    @Override
    public void setStatisticsConditionList(List<StatisticsConditionBean> optionList) {
        if (null != mStatisticsConditionPPW && optionList.size() > 0) {
            mStatisticsConditionPPW.addDatas(optionList);
            //初始化第一个数据
            mStatisticsSelectionTitleTv.setText(optionList.get(0).getTitle());
            //处理选择选项后
            handleSelectStatus(optionList.get(0));
        }
    }

    private void handleSelectStatus(StatisticsConditionBean bean) {
        mCurrentStatisticsConditionBean = bean;

        if (null != mSingleSelectorPPW) {
            mSingleSelectorPPW.clearDatas();
        }

        clearAllEditValue();

        mStartContent.setFilters(bean.getInputFilters());
        mEndContent.setFilters(bean.getInputFilters());

        mStartContent.setInputType(bean.getKeyboardType());
        mEndContent.setInputType(bean.getKeyboardType());

        if (bean.getConditionType() == StatisticsConditionBean.ConditionType.DateSelection) {
            setDateClickAble = true;

            mStartContentParent.setVisibility(View.VISIBLE);
            mEndContentParent.setVisibility(View.VISIBLE);
            mDateStartSelector.setVisibility(View.VISIBLE);
            mDateEndSelector.setVisibility(View.VISIBLE);
            mLibrarySelectorLayout.setVisibility(View.GONE);
            mContentSpace.setVisibility(View.VISIBLE);
            mEditLlParent.setVisibility(View.VISIBLE);

            setEditTextFocus(mStartContent, false);
            setEditTextFocus(mEndContent, false);
        } else if (bean.getConditionType() == StatisticsConditionBean.ConditionType.DoubleInput) {
            setDateClickAble = false;

            mStartContentParent.setVisibility(View.VISIBLE);
            mEndContentParent.setVisibility(View.VISIBLE);
            mDateStartSelector.setVisibility(View.GONE);
            mDateEndSelector.setVisibility(View.GONE);
            mLibrarySelectorLayout.setVisibility(View.GONE);
            mContentSpace.setVisibility(View.VISIBLE);
            mEditLlParent.setVisibility(View.VISIBLE);

            setEditTextFocus(mStartContent, true);
            setEditTextFocus(mEndContent, true);
        } else if (bean.getConditionType() == StatisticsConditionBean.ConditionType.SingleInput) {
            setDateClickAble = false;

            mStartContentParent.setVisibility(View.VISIBLE);
            mEndContentParent.setVisibility(View.GONE);
            mDateStartSelector.setVisibility(View.GONE);
            mDateEndSelector.setVisibility(View.GONE);
            mLibrarySelectorLayout.setVisibility(View.GONE);
            mContentSpace.setVisibility(View.GONE);
            mEditLlParent.setVisibility(View.VISIBLE);

            setEditTextFocus(mStartContent, true);
            setEditTextFocus(mEndContent, true);
        } else if (bean.getConditionType() == StatisticsConditionBean.ConditionType.SingleSelection) {
            setDateClickAble = false;

            mStartContentParent.setVisibility(View.GONE);
            mEndContentParent.setVisibility(View.GONE);
            mDateStartSelector.setVisibility(View.GONE);
            mDateEndSelector.setVisibility(View.GONE);
            mLibrarySelectorLayout.setVisibility(View.VISIBLE);
            mContentSpace.setVisibility(View.GONE);
            mEditLlParent.setVisibility(View.GONE);

            setEditTextFocus(mStartContent, false);
            setEditTextFocus(mEndContent, false);

            bean.getSingleSelectionCondition();
        }
    }

    private void startSearchFlowManageList() {
        if (mCurrentStatisticsConditionBean.getConditionType() == StatisticsConditionBean.ConditionType.DateSelection) {
            String startContent = mStartContent.getText().toString().trim().toUpperCase();
            String endContent = mEndContent.getText().toString().trim().toUpperCase();
            if (TextUtils.isEmpty(startContent) && TextUtils.isEmpty(endContent)) {
                showMsgDialog(R.string.query_condition_is_empty);
                return;
            }
            mCurrentStatisticsConditionBean.setStartValue(startContent);
            mCurrentStatisticsConditionBean.setEndValue(endContent);
        } else if (mCurrentStatisticsConditionBean.getConditionType() == StatisticsConditionBean.ConditionType.DoubleInput) {
            String startContent = mStartContent.getText().toString().trim().toUpperCase();
            String endContent = mEndContent.getText().toString().trim().toUpperCase();
            if (TextUtils.isEmpty(startContent) && TextUtils.isEmpty(endContent)) {
                showMsgDialog(R.string.query_condition_is_empty);
                return;
            }

            mCurrentStatisticsConditionBean.setStartValue(startContent);
            mCurrentStatisticsConditionBean.setEndValue(endContent);
            if (mCurrentStatisticsConditionBean.isNeedCompare()
                    && !TextUtils.isEmpty(startContent)
                    && !TextUtils.isEmpty(endContent)) {
                if (!mCurrentStatisticsConditionBean.compare()) {
                    showMsgDialog(R.string.code_error);
                    return;
                }
            }
            if (mCurrentStatisticsConditionBean.isAutoCompletion()) {
                mCurrentStatisticsConditionBean.getAutoCompleteResult();
            }
        } else if (mCurrentStatisticsConditionBean.getConditionType() == StatisticsConditionBean.ConditionType.SingleSelection) {
            if (mCurrentSingleSelectionConditionBean == null) {
                showMsgDialog(R.string.query_condition_is_empty);
                return;
            } else {
                mCurrentStatisticsConditionBean.setSingleConditionKey(mCurrentSingleSelectionConditionBean.getKey());
                mCurrentStatisticsConditionBean.setSingleValue(mCurrentSingleSelectionConditionBean.getValue());
            }
        } else if (mCurrentStatisticsConditionBean.getConditionType() == StatisticsConditionBean.ConditionType.SingleInput) {
            String singleContent = mStartContent.getText().toString().trim().toUpperCase();
            if (TextUtils.isEmpty(singleContent)) {
                showMsgDialog(R.string.query_condition_is_empty);
                return;
            } else {
                mCurrentStatisticsConditionBean.setSingleValue(singleContent);
            }
        }
        mPresenter.saveStatisticsCondition(mCurrentStatisticsConditionBean);

        if (mClassifyBean.getId() < 9) {
            StatisticsResultListActivity.startActivity(this, mClassifyBean);
        } else if (mClassifyBean.getId() == 9) {
            Intent intent = new Intent();
            intent.setClass(this, FlowManagementListActivity.class);
            startActivity(intent);
        } else if (mClassifyBean.getId() == 10) {
            Intent intent = new Intent();
            intent.setClass(this, IntoManagementListActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public void setSingleSelectionCondition(List<SingleSelectionConditionBean> list) {
        mSingleSelectorPPW.addDatas(list);
        if (mIsShowSingleSelectorPPW) {
            mIsShowSingleSelectorPPW = false;
            if (null != mSingleSelectorPPW && !mSingleSelectorPPW.isShowing()) {
                mSingleSelectorPPW.showAsDropDown(mSingleSelectionConditionTitleTv, 0, 0);
            }
        }
    }

    @Override
    public void setNewStartContent(String beginCondition) {
        mStartContent.setText(beginCondition);
        mStartContent.setSelection(beginCondition.length());
    }

    @Override
    public void setNewEndContent(String endCondition) {
        mEndContent.setText(endCondition);
        mEndContent.setSelection(endCondition.length());
    }

    @Override
    public void setNoLoginPermission(int kickedOffline) {
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
                LoginActivity.startActivity(StatisticsSelectionActivity.this);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void showProgressLoading() {
        showDialog(getString(R.string.loading));
    }

    @Override
    public void dismissProgressLoading() {
        dismissDialog();
    }

    @Override
    public void showMsgDialog(String msg) {
        showMessageDialog(msg);
    }

    @Override
    public void showMsgDialog(int msgId) {
        showMessageDialog(getString(msgId));
    }

    private void clearAllEditValue() {
        mStartContent.setText("");
        mEndContent.setText("");
        mCurrentSingleSelectionConditionBean = null;
        mSingleSelectionConditionTitleTv.setText("请选择");
    }

    //=====================================编辑框================================================

    /**
     * 设置editText是否可编辑
     */
    private void setEditTextFocus(EditText editText, boolean canEditAble) {
        editText.setFocusableInTouchMode(canEditAble);
        editText.setFocusable(canEditAble);
        editText.setClickable(canEditAble);
        if (canEditAble) {
            //设置可编辑状态
            editText.requestFocus();
        } else {
            //设置不可编辑状态
            editText.clearFocus();
        }
    }

    //TODO======================================初始化时间选择控件=====================================

    /**
     * 初始化日期控件
     */
    private void initializeCalendar() {
        if (null == dpd) {
            Calendar now = Calendar.getInstance();
            dpd = DatePickerDialog.newInstance(
                    StatisticsSelectionActivity.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
            dpd.autoDismiss(true);
            dpd.setThemeDark(false);
            dpd.vibrate(true);
            dpd.dismissOnPause(true);
            dpd.showYearPickerFirst(false);
            dpd.setAccentColor(Color.parseColor("#8a633d"));
            //设置最小日期
            setMiniDate(dpd);
            //设置最大日期为今日
            dpd.setMaxDate(Calendar.getInstance());
        }
    }

    /**
     * 设置最小时间限制 为20160101
     *
     * @param dpd
     */
    private void setMiniDate(DatePickerDialog dpd) {
        Calendar date = Calendar.getInstance();
        date.set(Calendar.MONTH, 0);
        date.set(Calendar.YEAR, 2016);
        date.set(Calendar.DAY_OF_MONTH, 1);
        dpd.setMinDate(date);
    }

    /**
     * 展示日期控件
     */
    private void showDateDialog(int mDateType) {
        initializeCalendar();
        switch (mDateType) {
            case START_DATE:
                if (null != dpd && !dpd.isAdded()) {
                    setMaxSelectDate();
                    setMiniDate(dpd);
                    dpd.show(getFragmentManager(), DATE_PICKERD_DIALOG);
                }
                break;
            case END_DATE:
                if (null != dpd && !dpd.isAdded()) {
                    //设置最小日期为startDate
                    setMiniSelectDate();
                    //设置最大日期为今日
                    dpd.setMaxDate(Calendar.getInstance());
                    dpd.show(getFragmentManager(), DATE_PICKERD_DIALOG);
                }
                break;
        }
    }

    /**
     * 设置最小日期
     */
    private void setMiniSelectDate() {
        String startDate = mStartContent.getText().toString().trim();
        if (!TextUtils.isEmpty(startDate)) {
            String[] startDates = startDate.split("-");
            Calendar date = Calendar.getInstance();
            date.set(Calendar.YEAR, Integer.parseInt(startDates[0]));
            date.set(Calendar.MONTH, Integer.parseInt(startDates[1]) - 1);
            date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(startDates[2]));
            dpd.setMinDate(date);
        }
    }

    /**
     * 设置最大日期
     */
    private void setMaxSelectDate() {
        String endDate = mEndContent.getText().toString().trim();
        if (!TextUtils.isEmpty(endDate)) {
            String[] endDates = endDate.split("-");
            Calendar date = Calendar.getInstance();
            date.set(Calendar.YEAR, Integer.parseInt(endDates[0]));
            date.set(Calendar.MONTH, Integer.parseInt(endDates[1]) - 1);
            date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(endDates[2]));
            dpd.setMaxDate(date);
        }
    }

    /**
     * 时间选择回调
     *
     * @param view        The view associated with this listener.
     * @param year        The year that was set.
     * @param monthOfYear The month that was set (0-11) for compatibility
     *                    with {@link java.util.Calendar}.
     * @param dayOfMonth  The day of the month that was set.
     */
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = new StringBuffer().append(year).append("-").append(++monthOfYear).append("-").append(dayOfMonth).toString();
        if (startStatus == START_DATE) {
            mStartContent.setText(date);
        } else if (startStatus == END_DATE) {
            mEndContent.setText(date);
        }
        //设置最小日期
        setMiniDate(dpd);
        //设置最大日期为今日
        dpd.setMaxDate(Calendar.getInstance());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mPresenter) {
            mPresenter.clearTempData();
            mPresenter.detachView();
        }
    }

}
