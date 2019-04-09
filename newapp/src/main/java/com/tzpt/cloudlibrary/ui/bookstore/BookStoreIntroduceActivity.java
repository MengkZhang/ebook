package com.tzpt.cloudlibrary.ui.bookstore;

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
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.base.data.Library;
import com.tzpt.cloudlibrary.bean.LightLibraryOpenTimeInfo;
import com.tzpt.cloudlibrary.ui.map.MapNavigationActivity;
import com.tzpt.cloudlibrary.ui.permissions.Permission;
import com.tzpt.cloudlibrary.ui.permissions.PermissionsDialogFragment;
import com.tzpt.cloudlibrary.ui.permissions.RxPermissions;
import com.tzpt.cloudlibrary.utils.PhoneCallUtil;
import com.tzpt.cloudlibrary.utils.StringUtils;
import com.tzpt.cloudlibrary.widget.CustomDialog;
import com.tzpt.cloudlibrary.widget.CustomWebView;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * 书店介绍
 */
public class BookStoreIntroduceActivity extends BaseActivity {

    private static final String FROM_SEARCH = "from_search";
    private static final String BOOK_STORE = "book_store";
    private static final String DISTANCE = "distance";
    private static final String IS_VALID_LNG_LAT = "is_valid_lng_lat";

    public static void startActivity(Context context, Library bookStoreLib, int distance, boolean isValidLngLat, int fromSearch) {
        Intent intent = new Intent(context, BookStoreIntroduceActivity.class);
        intent.putExtra(BOOK_STORE, bookStoreLib);
        intent.putExtra(DISTANCE, distance);
        intent.putExtra(IS_VALID_LNG_LAT, isValidLngLat);
        intent.putExtra(FROM_SEARCH, fromSearch);
        context.startActivity(intent);
    }

    @BindView(R.id.address_tv)
    TextView mAddressTv;
    @BindView(R.id.hall_code_tv)
    TextView mHallCodeTv;
    @BindView(R.id.name_tv)
    TextView mNameTv;
    @BindView(R.id.tel_tv)
    TextView mTelTv;
    @BindView(R.id.mail_tv)
    TextView mMailTv;
    @BindView(R.id.net_tv)
    TextView mNetTv;
    @BindView(R.id.custom_webview)
    CustomWebView mCustomWebView;
    @BindView(R.id.hall_code_title)
    TextView mHallCodeTitle;
    @BindView(R.id.name_title)
    TextView mNameTitle;
    @BindView(R.id.tel_title)
    TextView mTelTitle;
    @BindView(R.id.mail_title)
    TextView mMailTitle;
    @BindView(R.id.net_title)
    TextView mNetTitle;
    @BindView(R.id.business_hours_title)
    TextView mBusinessHoursTitle;
    @BindView(R.id.business_hours_tv)
    TextView mBusinessHoursTv;

    private Library mBookStore;
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
                if (mBookStore != null) {
                    MapNavigationActivity.startActivity(this,
                            mBookStore.mName,
                            mBookStore.mAddress,
                            mBookStore.mLngLat,
                            mDistance,
                            String.valueOf(mBookStore.mBookCount));
                }
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_book_store_introduce;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
    }

    @Override
    public void initDatas() {
        mCommonTitleBar.setTitle("本店介绍");

        mBookStore = (Library) getIntent().getSerializableExtra(BOOK_STORE);
        mDistance = getIntent().getIntExtra(DISTANCE, -1);
        boolean isValidLngLat = getIntent().getBooleanExtra(IS_VALID_LNG_LAT, false);
        if (mBookStore != null && mDistance > -1) {
            if (TextUtils.isEmpty(mBookStore.mCode)) {
                mHallCodeTitle.setVisibility(View.GONE);
                mHallCodeTv.setVisibility(View.GONE);
            } else {
                mHallCodeTitle.setVisibility(View.VISIBLE);
                mHallCodeTv.setVisibility(View.VISIBLE);
                mHallCodeTv.setText(mBookStore.mCode);
            }
            if (TextUtils.isEmpty(mBookStore.mName)) {
                mNameTitle.setVisibility(View.GONE);
                mNameTv.setVisibility(View.GONE);
            } else {
                mNameTitle.setVisibility(View.VISIBLE);
                mNameTv.setVisibility(View.VISIBLE);
                mNameTv.setText(mBookStore.mName);
            }

            if (TextUtils.isEmpty(mBookStore.mMail)) {
                mMailTitle.setVisibility(View.GONE);
                mMailTv.setVisibility(View.GONE);
            } else {
                mMailTitle.setVisibility(View.VISIBLE);
                mMailTv.setVisibility(View.VISIBLE);
                mMailTv.setText(mBookStore.mMail);
            }
            if (TextUtils.isEmpty(mBookStore.mPhone)) {
                mTelTitle.setVisibility(View.GONE);
                mTelTv.setVisibility(View.GONE);
            } else {
                mTelTitle.setVisibility(View.VISIBLE);
                mTelTv.setVisibility(View.VISIBLE);
                mTelTv.setText(mBookStore.mPhone);
            }
            if (TextUtils.isEmpty(mBookStore.mNet)) {
                mNetTitle.setVisibility(View.GONE);
                mNetTv.setVisibility(View.GONE);
            } else {
                mNetTitle.setVisibility(View.VISIBLE);
                mNetTv.setVisibility(View.VISIBLE);
                mNetTv.setText(mBookStore.mNet);
            }

            mAddressTv.setTextColor(isValidLngLat ? Color.parseColor("#666666") : Color.parseColor("#694A2C"));
            ImageSpan imgSpan = new ImageSpan(this, R.mipmap.ic_introduce_position, DynamicDrawableSpan.ALIGN_BASELINE);
            String addressDistance = mBookStore.mAddress + "position" + StringUtils.mToKm(mDistance);
            SpannableString spannableString = new SpannableString(addressDistance);
            spannableString.setSpan(imgSpan, mBookStore.mAddress.length(), mBookStore.mAddress.length() + 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#306cbb")), mBookStore.mAddress.length() + 1, addressDistance.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mAddressTv.setText(spannableString);

            if (TextUtils.isEmpty(mBookStore.mIntroduceUrl)) {
                mCustomWebView.setVisibility(View.GONE);
            } else {
                mCustomWebView.loadUrl(mBookStore.mIntroduceUrl);
                mCustomWebView.setLoadingListener(mWebViewLoading);
            }

            try {
                JSONObject jsonObject = new JSONObject(mBookStore.mLightTime);
                LightLibraryOpenTimeInfo lightLibraryOpenTimeInfo = new Gson().fromJson(jsonObject.toString(), LightLibraryOpenTimeInfo.class);
                if (null != lightLibraryOpenTimeInfo) {
                    setLongDayInfo(lightLibraryOpenTimeInfo.weekTime, lightLibraryOpenTimeInfo.week);
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
        setLongDayInfo(null, null);
    }

    /**
     * 设置营业时间
     */
    private void setLongDayInfo(LightLibraryOpenTimeInfo.WeekTime weekTime, Integer[] weeks) {
        if (null == weeks || weeks.length == 0) {
            setStoreBusinessTime("未设置！", "00:00-00:00", "00:00-00:00");
            return;
        }
        if (null == weekTime) {
            setStoreBusinessTime("未设置！", "09:00-12:00", "14:00-17:00");
            return;
        }
        String startTime = "";
        String endTime = "";
        LightLibraryOpenTimeInfo.AM wAm = weekTime.am;
        if (null != wAm) {
            String begin = wAm.begin;
            String end = wAm.end;
            StringBuilder builder = new StringBuilder();
            startTime = ((TextUtils.isEmpty(begin) || TextUtils.isEmpty(end)) ? "09:00-12:00"
                    : builder.append(begin).append("-").append(end).toString());

        }
        LightLibraryOpenTimeInfo.PM wPm = weekTime.pm;
        if (null != wPm) {
            String begin = wPm.begin;
            String end = wPm.end;
            StringBuilder builder = new StringBuilder();
            endTime = ((TextUtils.isEmpty(begin) || TextUtils.isEmpty(end)) ? "14:00-17:00"
                    : builder.append(begin).append("-").append(end).toString());
        }
        setStoreBusinessTime(null, startTime, endTime);
    }

    private void setStoreBusinessTime(String status, String startTime, String endTime) {
        if (TextUtils.isEmpty(status)) {
            mBusinessHoursTv.setText(new StringBuffer().append(startTime).append("\u3000").append(endTime));
        } else {
            mBusinessHoursTv.setText(status);
        }
    }

    @Override
    public void configViews() {

    }

    private CustomWebView.CallbackWebViewLoading mWebViewLoading = new CustomWebView.CallbackWebViewLoading() {
        @Override
        public void onPageStarted() {

        }

        @Override
        public void onPageFinished(boolean hasContent) {
        }

        @Override
        public void onPageLoadingError() {
            if (mCustomWebView != null) {
                mCustomWebView.setVisibility(View.GONE);
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
                            PhoneCallUtil.startPhoneCall(BookStoreIntroduceActivity.this, phoneNumber);
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
