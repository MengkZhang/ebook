package com.tzpt.cloudlibrary.ui.library;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.base.data.Library;
import com.tzpt.cloudlibrary.bean.LightLibraryOpenTimeInfo;
import com.tzpt.cloudlibrary.business_bean.LibraryBean;
import com.tzpt.cloudlibrary.ui.map.MapNavigationActivity;
import com.tzpt.cloudlibrary.ui.permissions.Permission;
import com.tzpt.cloudlibrary.ui.permissions.PermissionsDialogFragment;
import com.tzpt.cloudlibrary.ui.permissions.RxPermissions;
import com.tzpt.cloudlibrary.widget.CenterAlignHtmlImageSpan;
import com.tzpt.cloudlibrary.utils.PhoneCallUtil;
import com.tzpt.cloudlibrary.utils.StringUtils;
import com.tzpt.cloudlibrary.widget.CustomDialog;
import com.tzpt.cloudlibrary.widget.CustomWebView;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * 图书馆详情介绍
 */
public class LibraryIntroduceActivity extends BaseActivity implements
        LibraryIntroduceContract.View {

    private static final String FROM_SEARCH = "from_search";
    private static final String LIBRARY = "library";
    private static final String DISTANCE = "distance";
    private static final String IS_VALID_LNG_LAT = "is_valid_lng_lat";

    public static void startActivity(Context context, Library library, int distance, boolean isValidLngLat, int fromSearch) {
        Intent intent = new Intent(context, LibraryIntroduceActivity.class);
        intent.putExtra(LIBRARY, library);
        intent.putExtra(DISTANCE, distance);
        intent.putExtra(IS_VALID_LNG_LAT, isValidLngLat);
        intent.putExtra(FROM_SEARCH, fromSearch);
        context.startActivity(intent);
    }

    @BindView(R.id.address_tv)
    TextView mAddressTv;
    @BindView(R.id.hall_code_tv)
    TextView mHallCodeTv;
    @BindView(R.id.open_mode_tv)
    TextView mOpenModeTv;
    @BindView(R.id.name_tv)
    TextView mNameTv;
    @BindView(R.id.tel_tv)
    TextView mTelTv;
    @BindView(R.id.mail_tv)
    TextView mMailTv;
    @BindView(R.id.net_tv)
    TextView mNetTv;
//    @BindView(R.id.intro_multi_state_layout)
//    MultiStateLayout mMultiStateLayout;
    @BindView(R.id.custom_webview)
    CustomWebView mCustomWebView;
    @BindView(R.id.hall_code_title)
    TextView mHallCodeTitle;
    @BindView(R.id.open_mode_title)
    TextView mOpenModeTitle;
    @BindView(R.id.name_title)
    TextView mNameTitle;
    @BindView(R.id.tel_title)
    TextView mTelTitle;
    @BindView(R.id.mail_title)
    TextView mMailTitle;
    @BindView(R.id.net_title)
    TextView mNetTitle;
    @BindView(R.id.address_title)
    TextView mAddressTitle;
    @BindView(R.id.library_today_start_open_time_tv)
    TextView mLibraryTodayStartOpenTimeTv;
    @BindView(R.id.library_today_end_open_time_tv)
    TextView mLibraryTodayEndOpenTimeTv;
    @BindView(R.id.library_long_open_time_start_time_tv)
    TextView mLibraryLongOpenTimeStartTimeTv;
    @BindView(R.id.library_long_open_time_end_time_tv)
    TextView mLibraryLongOpenTimeEndTimeTv;
    @BindView(R.id.library_long_open_time_week_tv)
    TextView mLibraryLongOpenTimeWeekTv;
    //本馆资源
    @BindView(R.id.lib_source_rl)
    RelativeLayout mLibSourceRl;
    //图书数量
    @BindView(R.id.book_num_tv)
    TextView mBookNumTv;
    //电子书数量
    @BindView(R.id.ebook_num_tv)
    TextView mEBookNumTv;
    //视频数量
    @BindView(R.id.av_num_tv)
    TextView mAvNumTv;
    //本馆读者
    @BindView(R.id.lib_reader_rl)
    RelativeLayout mLibReaderRl;
    //注册读者数量
    @BindView(R.id.reg_reader_num_tv)
    TextView mRegReaderNumTv;
    //服务读者数量
    @BindView(R.id.visit_reader_num_tv)
    TextView mVisitReaderNumTv;
    //访问读者数量
    @BindView(R.id.server_reader_num_tv)
    TextView mServerReaderNumTv;


    private Library mLibrary;
    private int mDistance;

    @OnClick({R.id.titlebar_left_btn, R.id.tel_tv, R.id.net_tv, R.id.address_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.tel_tv:
                startPhoneCall(getString(R.string.call_phone_number), mTelTv.getText().toString().trim());
                break;
            case R.id.net_tv:
                String netStr = mNetTv.getText().toString().trim();
                if (!TextUtils.isEmpty(netStr)) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(mNetTv.getText().toString().trim());
                    intent.setData(content_url);
                    startActivity(intent);
                }
                break;
            case R.id.address_tv:
                if (mLibrary != null) {
                    MapNavigationActivity.startActivity(this,
                            mLibrary.mName,
                            mLibrary.mAddress,
                            mLibrary.mLngLat,
                            mDistance,
                            String.valueOf(mLibrary.mBookCount));
                }
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_library_introduce;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
    }

    @Override
    public void initDatas() {
        mCommonTitleBar.setTitle("本馆介绍");

        mLibrary = (Library) getIntent().getSerializableExtra(LIBRARY);
        if (mLibrary != null) {
            String bookNum = mLibrary.mBookCount + getResources().getString(R.string.book_num);
            String avNum = mLibrary.mVideoSetCount + getResources().getString(R.string.av_num);
            String ebookNum = mLibrary.mEBookCount + getResources().getString(R.string.ebook_num);
            String regReaderNum = mLibrary.mRegReaderCount + getResources().getString(R.string.book_people);
            String serverReaderNum = mLibrary.mServerReaderCount + getResources().getString(R.string.book_people);
            String visitReaderNum = mLibrary.mVisitReaderCount + getResources().getString(R.string.book_people);
            mLibSourceRl.setVisibility(View.VISIBLE);
            mLibReaderRl.setVisibility(View.VISIBLE);
            mBookNumTv.setText(bookNum);
            mEBookNumTv.setText(ebookNum);
            mAvNumTv.setText(avNum);
            mRegReaderNumTv.setText(regReaderNum);
            mVisitReaderNumTv.setText(serverReaderNum);
            mServerReaderNumTv.setText(visitReaderNum);
        } else {
            //需要隐藏本馆资源 本馆读者
            mLibSourceRl.setVisibility(View.GONE);
            mLibReaderRl.setVisibility(View.GONE);
        }
        mDistance = getIntent().getIntExtra(DISTANCE, -1);
        boolean isValidLngLat = getIntent().getBooleanExtra(IS_VALID_LNG_LAT, false);
        if (mLibrary != null && mDistance > -1) {
            if (TextUtils.isEmpty(mLibrary.mCode)) {
                mHallCodeTitle.setVisibility(View.GONE);
                mHallCodeTv.setVisibility(View.GONE);
            } else {
                mHallCodeTitle.setVisibility(View.VISIBLE);
                mHallCodeTv.setVisibility(View.VISIBLE);
                mHallCodeTv.setText(mLibrary.mCode);
            }
            if (TextUtils.isEmpty(mLibrary.mName)) {
                mNameTitle.setVisibility(View.GONE);
                mNameTv.setVisibility(View.GONE);
            } else {
                mNameTitle.setVisibility(View.VISIBLE);
                mNameTv.setVisibility(View.VISIBLE);
                mNameTv.setText(mLibrary.mName);
            }
            if (TextUtils.isEmpty(mLibrary.mOpenRestrictionStr)) {
                mOpenModeTitle.setVisibility(View.GONE);
                mOpenModeTv.setVisibility(View.GONE);
            } else {
                mOpenModeTitle.setVisibility(View.VISIBLE);
                mOpenModeTv.setVisibility(View.VISIBLE);
                mOpenModeTv.setText(mLibrary.mOpenRestrictionStr);
            }
            if (TextUtils.isEmpty(mLibrary.mMail)) {
                mMailTitle.setVisibility(View.GONE);
                mMailTv.setVisibility(View.GONE);
            } else {
                mMailTitle.setVisibility(View.VISIBLE);
                mMailTv.setVisibility(View.VISIBLE);
                mMailTv.setText(mLibrary.mMail);
            }
            if (TextUtils.isEmpty(mLibrary.mPhone)) {
                mTelTitle.setVisibility(View.GONE);
                mTelTv.setVisibility(View.GONE);
            } else {
                mTelTitle.setVisibility(View.VISIBLE);
                mTelTv.setVisibility(View.VISIBLE);
                mTelTv.setText(mLibrary.mPhone);
            }
            if (TextUtils.isEmpty(mLibrary.mNet)) {
                mNetTitle.setVisibility(View.GONE);
                mNetTv.setVisibility(View.GONE);
            } else {
                mNetTitle.setVisibility(View.VISIBLE);
                mNetTv.setVisibility(View.VISIBLE);
                mNetTv.setText(mLibrary.mNet);
            }

            mAddressTv.setTextColor(isValidLngLat ? Color.parseColor("#666666") : Color.parseColor("#694A2C"));
            //自定义的CenterAlignHtmlImageSpan解决了 用textView加载Html时给TextView设置行间距导致图片文字不能对齐的问题
            CenterAlignHtmlImageSpan imgSpan = new CenterAlignHtmlImageSpan(this, R.mipmap.ic_introduce_position, DynamicDrawableSpan.ALIGN_BASELINE);
            String addressDistance = mLibrary.mAddress + "position" + StringUtils.mToKm(mDistance);
            SpannableString spannableString = new SpannableString(addressDistance);
            spannableString.setSpan(imgSpan, mLibrary.mAddress.length(), mLibrary.mAddress.length() + 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#306cbb")), mLibrary.mAddress.length() + 1, addressDistance.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mAddressTv.setText(spannableString);

            if (TextUtils.isEmpty(mLibrary.mIntroduceUrl)) {
                mCustomWebView.setVisibility(View.GONE);
            } else {
                mCustomWebView.loadUrl(mLibrary.mIntroduceUrl);
                mCustomWebView.setLoadingListener(mWebViewLoading);
            }

            try {
                JSONObject jsonObject = new JSONObject(mLibrary.mLightTime);
                LightLibraryOpenTimeInfo lightLibraryOpenTimeInfo = new Gson().fromJson(jsonObject.toString(), LightLibraryOpenTimeInfo.class);
                if (null != lightLibraryOpenTimeInfo) {
                    //设置今日工作时间
                    setTodayInfo(lightLibraryOpenTimeInfo.dayTime, lightLibraryOpenTimeInfo.week);
                    //设置长期工作时间
                    setLongDayInfo(lightLibraryOpenTimeInfo.weekTime, lightLibraryOpenTimeInfo.week);
                    //设置开放工作日
                    setWeekInfo1(lightLibraryOpenTimeInfo.week);
                } else {
                    setLightLibraryOpenTimeEmpty();
                }
            } catch (Exception e) {
                setLightLibraryOpenTimeEmpty();
            }
        } else {
            finish();
        }
    }

    private void setLightLibraryOpenTimeEmpty() {
        setTodayInfo(null, null);
        setLongDayInfo(null, null);
        setWeekInfo1(null);
    }

    /**
     * 设置今日工作时间
     *
     * @param mDayTime
     */
    private void setTodayInfo(LightLibraryOpenTimeInfo.DayTime mDayTime, Integer[] weeks) {
        if (null == mDayTime && (null == weeks || weeks.length == 0)) {
            //如果 今日开放时间是未设置时，显示：未设置
            mLibraryTodayStartOpenTimeTv.setText("未设置！");
        } else if (mDayTime != null) {
            String startTime = "";
            String endTime = "";
            LightLibraryOpenTimeInfo.AM tAm = mDayTime.am;
            if (null != tAm) {
                String begin = tAm.begin;
                String end = tAm.end;
                StringBuilder builder = new StringBuilder();
                startTime = ((TextUtils.isEmpty(begin) || TextUtils.isEmpty(end)) ? "00:00\r\r\r\r-\r\r\r\r00:00"
                        : builder.append(begin).append("\r\r\r\r-\r\r\r\r").append(end).toString());

            }
            LightLibraryOpenTimeInfo.PM tPm = mDayTime.pm;
            if (null != tPm) {
                String begin = tPm.begin;
                String end = tPm.end;
                StringBuilder builder = new StringBuilder();
                endTime = ((TextUtils.isEmpty(begin) || TextUtils.isEmpty(end)) ? "00:00\r\r\r\r-\r\r\r\r00:00"
                        : builder.append(begin).append("\r\r\r\r-\r\r\r\r").append(end).toString());
            }
            if (startTime.equals("00:00\r\r\r\r-\r\r\r\r00:00")
                    && endTime.equals("00:00\r\r\r\r-\r\r\r\r00:00")) {
                //如果 今日开放时间是未设置时，显示：未设置
                mLibraryTodayStartOpenTimeTv.setText("未设置！");
            } else {
                setLibraryTodayTime(null, startTime, endTime);
            }

        } else {
            //如果 今日开放时间是未设置时，显示：未设置
            mLibraryTodayStartOpenTimeTv.setText("未设置！");
        }


    }

    /**
     * 设置长期工作时间
     *
     * @param weekTime
     * @param weeks
     */
    private void setLongDayInfo(LightLibraryOpenTimeInfo.WeekTime weekTime, Integer[] weeks) {
        if (null == weeks || weeks.length == 0) {
            setLibraryLongTime("00:00\r\r\r\r-\r\r\r\r00:00", "00:00\r\r\r\r-\r\r\r\r00:00");
            return;
        }
        if (null == weekTime) {
            setLibraryLongTime("09:00\r\r\r\r-\r\r\r\r12:00", "14:00\r\r\r\r-\r\r\r\r17:00");
            return;
        }
        String startTime = "";
        String endTime = "";
        LightLibraryOpenTimeInfo.AM wAm = weekTime.am;
        if (null != wAm) {
            String begin = wAm.begin;
            String end = wAm.end;
            StringBuilder builder = new StringBuilder();
            startTime = ((TextUtils.isEmpty(begin) || TextUtils.isEmpty(end)) ? "09:00\r\r\r\r-\r\r\r\r12:00"
                    : builder.append(begin).append("\r\r\r\r-\r\r\r\r").append(end).toString());

        }
        LightLibraryOpenTimeInfo.PM wPm = weekTime.pm;
        if (null != wPm) {
            String begin = wPm.begin;
            String end = wPm.end;
            StringBuilder builder = new StringBuilder();
            endTime = ((TextUtils.isEmpty(begin) || TextUtils.isEmpty(end)) ? "14:00\r\r\r\r-\r\r\r\r17:00"
                    : builder.append(begin).append("\r\r\r\r-\r\r\r\r").append(end).toString());
        }
        setLibraryLongTime(startTime, endTime);
    }

    /**
     * 设置开放工作日
     *
     * @param week
     */
    private void setWeekInfo1(Integer[] week) {
        if (null != week && week.length > 0) {
            List<Integer> weekList = Arrays.asList(week);
            StringBuilder weekBuilder = new StringBuilder();
            //按照新需求设计图 去掉 周 把星期天放在最前面
            if (weekList.size() > 0) {
                mLibraryLongOpenTimeStartTimeTv.setVisibility(View.VISIBLE);
                mLibraryLongOpenTimeEndTimeTv.setVisibility(View.VISIBLE);
                if (weekList.contains(7)) {
                    weekBuilder.append("日\r\r");
                }
                if (weekList.contains(1)) {
                    weekBuilder.append("一\r\r");
                }
                if (weekList.contains(2)) {
                    weekBuilder.append("二\r\r");
                }
                if (weekList.contains(3)) {
                    weekBuilder.append("三\r\r");
                }
                if (weekList.contains(4)) {
                    weekBuilder.append("四\r\r");
                }
                if (weekList.contains(5)) {
                    weekBuilder.append("五\r\r");
                }
                if (weekList.contains(6)) {
                    weekBuilder.append("六");
                }

            }
            setWeekInfo(weekList.size() == 0 ? "" : weekBuilder.toString());
        } else {
            setWeekInfo("未设置！");
            mLibraryLongOpenTimeStartTimeTv.setVisibility(View.GONE);
            mLibraryLongOpenTimeEndTimeTv.setVisibility(View.GONE);
        }
    }

    @Override
    public void configViews() {

    }

    @Override
    public void showProgressDialog() {
//        mMultiStateLayout.showProgress();
    }

    @Override
    public void dismissProgressDialog() {
//        if (null != mMultiStateLayout) {
//            mMultiStateLayout.showContentView();
//        }
    }

    @Override
    public void showErrorMsg(int msgId) {
//        if (null != mMultiStateLayout) {
//            mMultiStateLayout.showError();
//            mMultiStateLayout.showRetryError(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mPresenter.getLibraryInfo();
//                }
//            });
//        }
    }

    @Override
    public void showLibraryInfo(LibraryBean library) {
//        mLibrary = library;
//        if (TextUtils.isEmpty(library.mLibrary.mCode)) {
//            mHallCodeTitle.setVisibility(View.GONE);
//            mHallCodeTv.setVisibility(View.GONE);
//        } else {
//            mHallCodeTitle.setVisibility(View.VISIBLE);
//            mHallCodeTv.setVisibility(View.VISIBLE);
//            mHallCodeTv.setText(library.mLibrary.mCode);
//        }
//        if (TextUtils.isEmpty(library.mLibrary.mName)) {
//            mNameTitle.setVisibility(View.GONE);
//            mNameTv.setVisibility(View.GONE);
//        } else {
//            mNameTitle.setVisibility(View.VISIBLE);
//            mNameTv.setVisibility(View.VISIBLE);
//            mNameTv.setText(library.mLibrary.mName);
//        }
//        if (TextUtils.isEmpty(library.mLibrary.mMail)) {
//            mMailTitle.setVisibility(View.GONE);
//            mMailTv.setVisibility(View.GONE);
//        } else {
//            mMailTitle.setVisibility(View.VISIBLE);
//            mMailTv.setVisibility(View.VISIBLE);
//            mMailTv.setText(library.mLibrary.mMail);
//        }
//        if (TextUtils.isEmpty(library.mLibrary.mPhone)) {
//            mTelTitle.setVisibility(View.GONE);
//            mTelTv.setVisibility(View.GONE);
//        } else {
//            mTelTitle.setVisibility(View.VISIBLE);
//            mTelTv.setVisibility(View.VISIBLE);
//            mTelTv.setText(library.mLibrary.mPhone);
//        }
//        if (TextUtils.isEmpty(library.mLibrary.mNet)) {
//            mNetTitle.setVisibility(View.GONE);
//            mNetTv.setVisibility(View.GONE);
//        } else {
//            mNetTitle.setVisibility(View.VISIBLE);
//            mNetTv.setVisibility(View.VISIBLE);
//            mNetTv.setText(library.mLibrary.mNet);
//        }
//
//        mAddressTv.setTextColor(library.mIsValidLngLat ? Color.parseColor("#666666") : Color.parseColor("#694A2C"));
//        ImageSpan imgSpan = new ImageSpan(this, R.mipmap.ic_introduce_position, DynamicDrawableSpan.ALIGN_BASELINE);
//        String addressDistance = library.mLibrary.mAddress + "position" + StringUtils.mToKm(library.mDistance);
//        SpannableString spannableString = new SpannableString(addressDistance);
//        spannableString.setSpan(imgSpan, library.mLibrary.mAddress.length(), library.mLibrary.mAddress.length() + 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#3996CF")), library.mLibrary.mAddress.length() + 1, addressDistance.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        mAddressTv.setText(spannableString);
//
//        if (TextUtils.isEmpty(library.mLibrary.mIntroduceUrl)) {
//            mCustomWebView.setVisibility(View.GONE);
//        } else {
//            mCustomWebView.loadUrl(library.mLibrary.mIntroduceUrl);
//            mCustomWebView.setLoadingListener(mWebViewLoading);
//        }
    }

    @Override
    public void setLibraryTodayTime(String status, String startTime, String endTime) {
        mLibraryTodayStartOpenTimeTv.setText(startTime);
        mLibraryTodayEndOpenTimeTv.setText(endTime);
    }

    @Override
    public void setLibraryLongTime(String startTime, String endTime) {
        mLibraryLongOpenTimeStartTimeTv.setText(startTime);
        mLibraryLongOpenTimeEndTimeTv.setText(endTime);
    }

    @Override
    public void setWeekInfo(String weekInfo) {
        mLibraryLongOpenTimeWeekTv.setText(weekInfo);
    }

    private CustomWebView.CallbackWebViewLoading mWebViewLoading = new CustomWebView.CallbackWebViewLoading() {
        @Override
        public void onPageStarted() {

        }

        @Override
        public void onPageFinished(boolean hasContent) {
//            dismissProgressDialog();
        }

        @Override
        public void onPageLoadingError() {
            if (mCustomWebView != null) {
                mCustomWebView.setVisibility(View.GONE);
//                dismissProgressDialog();
            }
        }
    };

    /**
     * 拨打电话
     *
     * @param phoneNumber
     */
    private void startPhoneCall(String message, final String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return;
        }
        //自定义对话框
        final CustomDialog callPhoneDialog = new CustomDialog(this, R.style.DialogTheme, message);
        callPhoneDialog.setCancelable(false);
        callPhoneDialog.hasNoCancel(true);
        callPhoneDialog.setButtonTextForCallPhone();
        callPhoneDialog.setText(message);
        callPhoneDialog.show();
        callPhoneDialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                callPhoneDialog.dismiss();
                initCallPhonePermission(phoneNumber);
            }

            @Override
            public void onClickCancel() {
                callPhoneDialog.dismiss();
            }
        });
    }

    //检查打电话权限
    private void initCallPhonePermission(final String phoneNumber) {
        if (Build.VERSION.SDK_INT < 23) {
            PhoneCallUtil.startPhoneCall(this, phoneNumber);
            return;
        }
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.requestEach(Manifest.permission.CALL_PHONE)
                .subscribe(new Action1<Permission>() {
                    @Override
                    public void call(Permission permission) {
                        if (permission.granted) {
                            PhoneCallUtil.startPhoneCall(LibraryIntroduceActivity.this, phoneNumber);
                        } else if (permission.shouldShowRequestPermissionRationale) {//下次继续提示(应该显示请求许可理由)

                        } else {
                            showSetPermissionPopUpWindow();
                        }
                    }
                });
    }

    private PermissionsDialogFragment mCallPhoneDialogFragment;

    //展示设置权限弹窗
    private void showSetPermissionPopUpWindow() {
        if (null == mCallPhoneDialogFragment) {
            mCallPhoneDialogFragment = new PermissionsDialogFragment();
        }
        if (mCallPhoneDialogFragment.isAdded()) {
            return;
        }
        mCallPhoneDialogFragment.initPermissionUI(PermissionsDialogFragment.PERMISSION_CALL_PHONE);
        mCallPhoneDialogFragment.show(this.getFragmentManager(), "PermissionsDialogFragment");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != mCustomWebView) {
            mCustomWebView.onResume();
        }
        UmengHelper.setUmengResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != mCustomWebView) {
            mCustomWebView.onPause();
        }
        UmengHelper.setUmengPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mCustomWebView) {
            mCustomWebView.setLoadingListener(null);
            mCustomWebView.clearWebCache();
            mCustomWebView.destroyWebView();
            mCustomWebView = null;
        }
    }


}
