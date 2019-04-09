package com.tzpt.cloudlibrary.ui.map;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseFragment;
import com.tzpt.cloudlibrary.base.BaseListActivity;
import com.tzpt.cloudlibrary.bean.SwitchCityBean;
import com.tzpt.cloudlibrary.ui.permissions.Permission;
import com.tzpt.cloudlibrary.ui.permissions.PermissionsDialogFragment;
import com.tzpt.cloudlibrary.ui.permissions.RxPermissions;
import com.tzpt.cloudlibrary.utils.ToastUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * 选择区域
 */
public class SwitchCityActivity extends BaseListActivity<SwitchCityBean> implements
        SwitchCityContract.View {
    private static final String FROM_TYPE = "from_type";
    private static final String LAST_AREA_TITLE = "last_area_title";
    private static final String LAST_AREA_CODE = "last_area_code";

//    public static void startActivity(Context context) {
//        Intent intent = new Intent(context, SwitchCityActivity.class);
//        intent.putExtra(FROM_TYPE, 0);
//        context.startActivity(intent);
//    }

    public static void startActivityForResultFragment(BaseFragment fragment, int requestCode) {
        Intent intent = new Intent(fragment.getSupportActivity(), SwitchCityActivity.class);
        intent.putExtra(FROM_TYPE, 0);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void startActivityForResult(Activity activity, String title, String adCode, int requestCode) {
        Intent intent = new Intent(activity, SwitchCityActivity.class);
        intent.putExtra(FROM_TYPE, 1);
        intent.putExtra(LAST_AREA_TITLE, title);
        intent.putExtra(LAST_AREA_CODE, adCode);
        activity.startActivityForResult(intent, requestCode);
    }

    @BindView(R.id.switch_city_location)
    TextView mSwitchCityLocation;
    @BindView(R.id.switch_city_top_layout)
    RelativeLayout mSwitchCityTopLayout;
    @BindView(R.id.location_choose_tip_tv)
    TextView mLocationChooseTipTv;

    private SwitchCityPresenter mPresenter;

    //0:首页城市区县级列表 1:切换位置省级单位列表 2:切换位置市级单位列表 3:切换位置城市区县级列表
    private int mChooseType = 0;

    private int mFromType;
    private String mLastAreaTitle;
    private String mLastAreaCode;

    private PermissionsDialogFragment mSetLocationDialogFragment;


    @OnClick({R.id.titlebar_left_btn, R.id.titlebar_right_txt_btn, R.id.switch_city_top_layout})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                clickBack();
                break;
            case R.id.titlebar_right_txt_btn:
                if (mChooseType == 0) {
                    mPresenter.getProvince();
                }
                break;
            case R.id.switch_city_top_layout://点击确定定位
                mPresenter.selectLocationArea();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_switch_city;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
    }

    @Override
    public void initDatas() {
        mPresenter = new SwitchCityPresenter();
        mPresenter.attachView(this);

        mFromType = getIntent().getIntExtra(FROM_TYPE, 0);
        mLastAreaTitle = getIntent().getStringExtra(LAST_AREA_TITLE);
        mLastAreaCode = getIntent().getStringExtra(LAST_AREA_CODE);
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
    public void configViews() {
        mAdapter = new SwitchCityAdapter(this);
        initAdapter(false, false);
        mRecyclerView.setDividerDrawable(R.drawable.divider_rv_vertical_one);


        if (mFromType == 1) {//不显示定位布局
            mPresenter.getLastDistrict(true, mLastAreaTitle, mLastAreaCode);
            mSwitchCityTopLayout.setVisibility(View.GONE);
        } else {
            mPresenter.getLastDistrict();
            mPresenter.getLocationInfo();
        }
    }

    //点击返回
    private void clickBack() {
        switch (mChooseType) {
            case 0:
                finish();
                break;
            case 1:
                if (mFromType == 1) {
                    mPresenter.getLastDistrict(true, mLastAreaTitle, mLastAreaCode);
                } else {
                    mPresenter.getLastDistrict();
                }
                break;
            case 2:
                mPresenter.getProvince();
                break;
            case 3:
                mPresenter.getCity();
                break;
            default:
                finish();
                break;
        }

    }

    @Override
    public void onRefresh() {
        switch (mChooseType) {
            case 0:
                if (mFromType == 1) {
                    mPresenter.getLastDistrict(true, mLastAreaTitle, mLastAreaCode);
                } else {
                    mPresenter.getLastDistrict();
                }
                break;
            case 1:
                mPresenter.getProvince();
                break;
            case 2:
                mPresenter.getCity();
                break;
            case 3:
                mPresenter.getDistrict();
                break;
        }
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onItemClick(int position) {
        switch (mChooseType) {
            case 1://省
                SwitchCityBean beanProvince = mAdapter.getItem(position);
                mPresenter.getCity(beanProvince.mRealName, beanProvince.mCode);
                break;
            case 2://市
                SwitchCityBean beanCity = mAdapter.getItem(position);
                if (beanCity.mNoChild) {
                    dealSelectedArea(beanCity);
                } else {
                    mPresenter.getDistrict(beanCity.mRealName, beanCity.mCode);
                }
                break;
            case 0://区
            case 3://区
                SwitchCityBean beanDistrict = mAdapter.getItem(position);
                dealSelectedArea(beanDistrict);
                break;
        }
    }

    private void dealSelectedArea(SwitchCityBean bean) {
        switch (mFromType) {
            case 0://选择区域切换位置
                mPresenter.dealSelectedArea(bean.mRealName, bean.mCode, bean.mLevel);
                break;
            case 1://图书馆高级检索，选择城市
                mLastAreaTitle = mPresenter.getLastAreaTitle(bean.mRealName, bean.mLevel);

                Intent intent = new Intent();
                intent.putExtra("areaName", bean.mRealName);
                intent.putExtra("areaCode", bean.mCode);
                intent.putExtra("areaNameTitle", mLastAreaTitle);
                setResult(RESULT_OK, intent);
                break;
        }
        finish();
    }

    @Override
    public void changeDataList(int type, String title) {
        mChooseType = type;
        mCommonTitleBar.setTitle(title);
        mCommonTitleBar.setRightTxtBtnVisibility(type == 0 ? View.VISIBLE : View.INVISIBLE);
        mCommonTitleBar.setRightBtnClickAble(false);
        mAdapter.clear();
        mRecyclerView.showProgress();
    }

    @Override
    public void setDataList(List<SwitchCityBean> dataList) {
        mCommonTitleBar.setRightBtnClickAble(true);
        mAdapter.clear();
        mAdapter.addAll(dataList);
    }

    @Override
    public void setErrorMsg(int msgId) {
        mCommonTitleBar.setRightBtnClickAble(true);
        if (mAdapter.getCount() < 1) {
            mAdapter.clear();
            mRecyclerView.showError();
            mRecyclerView.setRetryRefreshListener(this);
        } else {
            ToastUtils.showSingleToast(msgId);
        }
    }

    @Override
    public void setGPSLoadingStatus() {
        mSwitchCityLocation.setText(R.string.location_loading);
        mLocationChooseTipTv.setVisibility(View.GONE);
    }

    @Override
    public void setGPSLowStatus() {
        mSwitchCityLocation.setText(R.string.reader_current_position_weak);
        mLocationChooseTipTv.setVisibility(View.GONE);
    }

    @Override
    public void setNoLocationPermissionStatus() {
        mSwitchCityLocation.setText(R.string.reader_current_position_no_permission);
        mLocationChooseTipTv.setVisibility(View.GONE);
    }

    @Override
    public void setLocationInfo(String info) {
        mSwitchCityLocation.setText(info);
        mLocationChooseTipTv.setVisibility(View.VISIBLE);
    }

    @Override
    public void finishActivity() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void showLocationPermission() {
        //定位权限
        if (Build.VERSION.SDK_INT < 23) {
            showSetPermissionPopUpWindow();
            return;
        }
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.setLogging(true);
        rxPermissions.requestEach(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(new Action1<Permission>() {
                    @Override
                    public void call(Permission permission) {
                        if (permission.granted) {//权限已授权
                            mPresenter.startLocation();
                        } else if (permission.shouldShowRequestPermissionRationale) {//下次继续提示(应该显示请求许可理由)

                        } else {//没有权限,不能使用权限模块-去设置权限
                            showSetPermissionPopUpWindow();
                        }
                    }
                });
    }

    //展示设置权限弹窗
    private void showSetPermissionPopUpWindow() {
        if (null == mSetLocationDialogFragment) {
            mSetLocationDialogFragment = new PermissionsDialogFragment();
        }
        if (mSetLocationDialogFragment.isAdded()) {
            return;
        }
        mSetLocationDialogFragment.initPermissionUI(PermissionsDialogFragment.PERMISSION_LOCATION);
        mSetLocationDialogFragment.show(getFragmentManager(), "PermissionsDialogFragment");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mPresenter) {
            //清除所有临时数据
            mPresenter.clearTempCityData();
            mPresenter.detachView();
        }
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
