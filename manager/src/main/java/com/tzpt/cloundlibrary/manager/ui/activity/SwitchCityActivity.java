package com.tzpt.cloundlibrary.manager.ui.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.BuildConfig;
import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseListActivity;
import com.tzpt.cloundlibrary.manager.bean.LocationBean;
import com.tzpt.cloundlibrary.manager.bean.SendAddressMessage;
import com.tzpt.cloundlibrary.manager.bean.SwitchCityBean;
import com.tzpt.cloundlibrary.manager.ui.adapter.SwitchCityAdapter;
import com.tzpt.cloundlibrary.manager.ui.contract.SwitchCityContract;
import com.tzpt.cloundlibrary.manager.ui.presenter.SwitchCityPresenter;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 选择区域
 */
public class SwitchCityActivity extends BaseListActivity<SwitchCityBean> implements
        SwitchCityContract.View {

    private static final String CHOOSE_CITY = "choose_city";

    public static void startActivity(Activity activity, String chooseCity) {
        Intent intent = new Intent(activity, SwitchCityActivity.class);
        intent.putExtra(CHOOSE_CITY, chooseCity);
        activity.startActivityForResult(intent, 1000);
    }

    @BindView(R.id.titlebar_title_tv)
    TextView mTitleBarTitleTv;
    @BindView(R.id.switch_city_location)
    TextView mSwitchCityLocation;
    @BindView(R.id.titlebar_right_tv)
    Button mTitleBarRightBtn;
    @BindView(R.id.switch_city_top_layout)
    RelativeLayout mSwitchCityTopLayout;

    private SwitchCityPresenter mPresenter;

    private int mChooseType = 2;
    private String mProvinceCode;
    private String mProvinceName;
    private String mCurrentCityName;
    private String mLngLat;
    private String mCurrentDistrictCode;
    private String mCurrentDistrict;
    private String mChooseCity;

    @OnClick({R.id.titlebar_left_btn, R.id.titlebar_right_tv, R.id.switch_city_top_layout})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                clickBack();
                break;
            case R.id.titlebar_right_tv:
                if (mChooseType == 2) {
                    mPresenter.getProvince();
                }
                break;
            case R.id.switch_city_top_layout://点击确定定位
                //发送刷新消息
                SendAddressMessage message = new SendAddressMessage();
                message.mChooseCity = mCurrentCityName;
                message.mDistrict = TextUtils.isEmpty(mCurrentDistrict) ? "青羊区" : mCurrentDistrict;
                message.mCode = TextUtils.isEmpty(mCurrentDistrictCode) ? "510105" : mCurrentDistrictCode;
                message.mLngLat = TextUtils.isEmpty(mLngLat) ? "104.061627,30.555593" : mLngLat;
                EventBus.getDefault().post(message);
                finish();
                break;
        }
    }

    //点击返回
    private void clickBack() {
        switch (mChooseType) {
            case 0:
                mPresenter.getDistrict(TextUtils.isEmpty(mChooseCity) ? mCurrentCityName : mChooseCity, 0);
                break;
            case 1:
                mPresenter.getProvince();
                break;
            case 2:
                finish();
                break;
            case 3:
                mPresenter.getCity(mProvinceCode, mProvinceName);
                break;
            default:
                finish();
                break;
        }

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_switch_city;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void initDatas() {
        mChooseCity = getIntent().getStringExtra(CHOOSE_CITY);
        mTitleBarTitleTv.setText(TextUtils.isEmpty(mChooseCity) ? "成都市" : mChooseCity);
        mTitleBarRightBtn.setText("切换位置");
        mPresenter = new SwitchCityPresenter();
        mPresenter.attachView(this);
        mPresenter.getCurrentLocation();
    }

    @Override
    public void configViews() {
        mAdapter = new SwitchCityAdapter(this);
        initAdapter(false, false);
        mRecyclerView.setItemDecoration(ContextCompat.getColor(this, R.color.color_E3E3E3), 2, 0, 0);
    }


    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onItemClick(int position) {
        switch (mChooseType) {
            case 0://省
                SwitchCityBean beanProvince = mAdapter.getItem(position);
                if (null != beanProvince && null != beanProvince.mCode) {
                    this.mProvinceCode = beanProvince.mCode;
                    this.mProvinceName = beanProvince.mName;
                    mPresenter.getCity(mProvinceCode, mProvinceName);
                }
                break;
            case 1://市
                SwitchCityBean beanCity = mAdapter.getItem(position);
                if (null != beanCity && null != beanCity.mName) {
                    mPresenter.getDistrict(beanCity.mName, 1);
                }
                break;
            case 2://区(0默认)
            case 3://区(1切换)
                SwitchCityBean beanDistrict = mAdapter.getItem(position);
                //发送刷新消息
                SendAddressMessage message = new SendAddressMessage();
                message.mChooseCity = beanDistrict.mParentCityName;
                message.mDistrict = beanDistrict.mName;
                message.mCode = beanDistrict.mCode;
                message.mLngLat = mLngLat;
                EventBus.getDefault().post(message);
                finish();
                break;
        }
    }

    @Override
    public void setCityList(List<SwitchCityBean> switchCityList, int type, String cityName) {
        mTitleBarTitleTv.setText(cityName);
        this.mChooseType = type;
        mTitleBarRightBtn.setVisibility(type == 2 ? View.VISIBLE : View.INVISIBLE);
        mAdapter.clear();
        mAdapter.addAll(switchCityList);
    }

    @Override
    public void setCityListEmpty() {
        mAdapter.clear();
        mRecyclerView.showEmpty();
    }

    @Override
    public void setErrorMsg(int msgId) {
        if (mAdapter.getCount() < 1) {
            mAdapter.clear();
            mRecyclerView.showError();
        } else {
            mAdapter.pauseMore();
        }
    }

    @Override
    public void setLocationAddressSuccess(LocationBean bean) {
        mLngLat = bean.lngLat;
        mCurrentCityName = bean.city;
        mCurrentDistrictCode = bean.currentDistrictCode;
        mCurrentDistrict = bean.district;
        mTitleBarTitleTv.setText(bean.city);
        mSwitchCityLocation.setText(bean.currentLocation);
        mSwitchCityLocation.setTextColor(Color.parseColor("#805f33"));
        //获取当前区域
        mPresenter.getDistrict(TextUtils.isEmpty(mChooseCity) ? mCurrentCityName : mChooseCity, 0);
    }

    @Override
    public void setLocationFailed(int msgId) {
        mSwitchCityLocation.setText(msgId);
        mSwitchCityLocation.setTextColor(Color.parseColor("#ff0000"));
        mPresenter.getDistrict(TextUtils.isEmpty(mChooseCity) ? "成都市" : mChooseCity, 0);
    }

    @Override
    public void showDialogForPermissions(int msgId) {
        mPresenter.getDistrict(TextUtils.isEmpty(mChooseCity) ? "成都市" : mChooseCity, 0);

        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, getString(msgId));
        dialog.setCancelable(false);
        dialog.hasNoCancel(true);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                gotoMiuiPermission();
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    /**
     * 跳转到miui的权限管理页面
     */
    private void gotoMiuiPermission() {
        Intent i = new Intent("miui.intent.action.APP_PERM_EDITOR");
        ComponentName componentName = new ComponentName("com.miui.securitycenter",
                "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        i.setComponent(componentName);
        i.putExtra("extra_pkgname", getPackageName());
        try {
            startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
            gotoMeizuPermission();
        }
    }

    /**
     * 跳转到魅族的权限管理系统
     */
    private void gotoMeizuPermission() {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            gotoHuaweiPermission();
        }
    }

    /**
     * 华为的权限管理页面
     */
    private void gotoHuaweiPermission() {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.huawei.systemmanager",
                    "com.huawei.permissionmanager.ui.MainActivity");//华为权限管理
            intent.setComponent(comp);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            startActivity(getAppDetailSettingIntent());
        }

    }

    /**
     * 获取应用详情页面intent
     *
     * @return
     */
    private Intent getAppDetailSettingIntent() {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 14) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
        }
        return localIntent;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.clearTempCityData();     //清除所有临时数据
        mAdapter.clear();
        mPresenter.detachView();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 如果按了返回键
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            clickBack();
            return true;
        }
        return false;
    }


}
