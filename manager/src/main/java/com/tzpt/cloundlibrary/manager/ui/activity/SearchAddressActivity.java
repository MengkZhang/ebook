package com.tzpt.cloundlibrary.manager.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseListActivity;
import com.tzpt.cloundlibrary.manager.bean.SearchAddressBean;
import com.tzpt.cloundlibrary.manager.bean.SendAddressMessage;
import com.tzpt.cloundlibrary.manager.permissions.Permission;
import com.tzpt.cloundlibrary.manager.permissions.PermissionsDialogFragment;
import com.tzpt.cloundlibrary.manager.permissions.RxPermissions;
import com.tzpt.cloundlibrary.manager.ui.adapter.SearchAddressAdapter;
import com.tzpt.cloundlibrary.manager.ui.contract.SearchAddressContract;
import com.tzpt.cloundlibrary.manager.ui.presenter.SearchAddressPresenter;
import com.tzpt.cloundlibrary.manager.utils.KeyboardUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * 搜索地址类
 */
public class SearchAddressActivity extends BaseListActivity<SearchAddressBean> implements
        SearchAddressContract.View {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SearchAddressActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.search_bar_type_tv)
    TextView mSearchBarTypeTv;
    @BindView(R.id.search_bar_center_content_et)
    EditText mSearchBarCenterContentEt;
    @BindView(R.id.search_bar_center_del_iv)
    ImageView mSearchBarCenterDelIv;

    private SearchAddressPresenter mPresenter;
    private int mCurrentPage = 1;
    private String mChooseCity;

    @OnClick({R.id.search_bar_left_btn, R.id.search_bar_type_tv, R.id.search_bar_center_del_iv,
            R.id.search_bar_right_confirm_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.search_bar_left_btn:
                finish();
                break;
            case R.id.search_bar_type_tv:
                initLocationPermission();
                break;
            case R.id.search_bar_center_del_iv:
                mSearchBarCenterContentEt.setText("");
                mSearchBarCenterContentEt.requestFocus();
                break;
            case R.id.search_bar_right_confirm_tv:
                searchAddress(1);
                break;
        }
    }

    private TextView.OnEditorActionListener mOnEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchAddress(1);
                return true;
            }
            return false;
        }


    };
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            mSearchBarCenterDelIv.setVisibility((s.length() > 0) ? View.VISIBLE : View.GONE);
        }
    };


    @Override
    public int getLayoutId() {
        return R.layout.activity_search_address;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void initDatas() {
        EventBus.getDefault().register(this);
        mPresenter = new SearchAddressPresenter();
        mPresenter.attachView(this);
        mPresenter.getLocationArea();
    }

    @Override
    public void configViews() {
        mSearchBarCenterContentEt.addTextChangedListener(mTextWatcher);
        mSearchBarCenterContentEt.setOnEditorActionListener(mOnEditorActionListener);

        mAdapter = new SearchAddressAdapter(this);
        initAdapter(false, true);
    }

    @Override
    public void onItemClick(int position) {
        SearchAddressBean bean = mAdapter.getItem(position);
        if (null != bean) {
            EventBus.getDefault().post(bean);
            finish();
        }
    }


    //接收地址
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveAddress(SendAddressMessage message) {
        if (null != message) {
            mChooseCity = message.mChooseCity;
            setLocationDistrict(message.mDistrict);
            mPresenter.searchAddressList(1, "", message.mDistrict);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mAdapter.clear();
        mPresenter.detachView();
    }

    //搜索地址
    private void searchAddress(int pageNum) {
        String searchContent = mSearchBarCenterContentEt.getText().toString().trim();
        mPresenter.searchAddressList(pageNum, searchContent, mSearchBarTypeTv.getText().toString().trim());
        mSearchBarCenterContentEt.setSelection(searchContent.length());
        KeyboardUtils.hideSoftInput(mSearchBarCenterContentEt);
    }

    @Override
    public void setSearchAddress(List<SearchAddressBean> list, String content, boolean refresh) {
        if (refresh) {
            mAdapter.clear();
            mCurrentPage = 1;
        } else {
            mCurrentPage = mCurrentPage + 1;
        }
        mAdapter.addAll(list);
        if (mAdapter instanceof SearchAddressAdapter) {
            SearchAddressAdapter adapter = (SearchAddressAdapter) mAdapter;
            adapter.setSearchContent(content);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void setSearchAddressEmpty(boolean refresh) {
        if (refresh) {
            mAdapter.clear();
            mRecyclerView.showEmpty();
        } else {
            mAdapter.stopMore();
        }
    }

    @Override
    public void setLocationDistrict(String district) {
        if (!TextUtils.isEmpty(district)) {
            if (district.length() > 3) {
                district = district.substring(0, 3) + "...";
            }
            mSearchBarTypeTv.setText(district);
        }
    }

    @Override
    public void showProgressDialog() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mRecyclerView.showProgress();
    }

    @Override
    public void setSearchAddressError(boolean refresh) {
        mRecyclerView.setRefreshing(false);
        if (refresh) {
            mAdapter.clear();
            mRecyclerView.showError();
        } else {
            mAdapter.pauseMore();
        }
    }

    @Override
    public void onLoadMore() {
        searchAddress(mCurrentPage + 1);
    }

    //初始化定位权限
    private void initLocationPermission() {
        if (Build.VERSION.SDK_INT < 23) {
            SwitchCityActivity.startActivity(this, mChooseCity);
            return;
        }
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.setLogging(true);
        rxPermissions.requestEach(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(new Action1<Permission>() {
                    @Override
                    public void call(Permission permission) {
                        if (permission.granted) {//权限已授权
                            SwitchCityActivity.startActivity(SearchAddressActivity.this, mChooseCity);
                        } else if (permission.shouldShowRequestPermissionRationale) {//下次继续提示(应该显示请求许可理由)

                        } else {//没有权限,不能使用权限模块-去设置权限
                            showCameraPermissionPopUpWindow();
                        }
                    }
                });
    }

    private PermissionsDialogFragment mLocationDialogFragment;

    //展示设置权限弹窗
    private void showCameraPermissionPopUpWindow() {
        if (null == mLocationDialogFragment) {
            mLocationDialogFragment = new PermissionsDialogFragment();
        }
        if (mLocationDialogFragment.isAdded()) {
            return;
        }
        mLocationDialogFragment.initPermissionUI(PermissionsDialogFragment.PERMISSION_LOCATION);
        mLocationDialogFragment.show(this.getFragmentManager(), "PermissionsDialogFragment");
    }
}
