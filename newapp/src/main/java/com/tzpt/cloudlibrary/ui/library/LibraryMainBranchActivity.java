package com.tzpt.cloudlibrary.ui.library;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.BaseListActivity;
import com.tzpt.cloudlibrary.bean.LibraryMainBranchBean;
import com.tzpt.cloudlibrary.ui.permissions.Permission;
import com.tzpt.cloudlibrary.ui.permissions.PermissionsDialogFragment;
import com.tzpt.cloudlibrary.ui.permissions.RxPermissions;
import com.tzpt.cloudlibrary.widget.recyclerview.swipe.OnRefreshListener;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * 总分馆
 */
public class LibraryMainBranchActivity extends BaseListActivity<LibraryMainBranchBean> implements
        LibraryMainBranchContract.View {

    private static final String LIBRARY_CODE = "library_code";

    public static void startActivity(Context context, String libCode) {
        Intent intent = new Intent(context, LibraryMainBranchActivity.class);
        intent.putExtra(LIBRARY_CODE, libCode);
        context.startActivity(intent);
    }

    @BindView(R.id.reader_location_tv)
    TextView mReaderLocationTv;
    @BindView(R.id.reader_location_refresh_iv)
    ImageView mReaderLocationRefreshIv;

    private PermissionsDialogFragment mSetLocationDialogFragment;
    private LibraryMainBranchPresenter mPresenter;

    @OnClick({R.id.titlebar_left_btn, R.id.reader_location_refresh_iv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.reader_location_refresh_iv:
                mPresenter.refreshLocation();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_library_main_branch;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
    }

    @Override
    public void initDatas() {
        mPresenter = new LibraryMainBranchPresenter();
        mPresenter.attachView(this);

        String libCode = getIntent().getStringExtra(LIBRARY_CODE);
        mCommonTitleBar.setTitle("总分馆");
        mPresenter.setCurrentLibCode(libCode);
    }

    @Override
    public void configViews() {
        mAdapter = new LibraryMainBranchAdapter(this);
        initAdapter(false, false);
        mPresenter.getLocationInfo();
        mPresenter.getMainBranchLibraryList();
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onItemClick(int position) {
        LibraryMainBranchBean item = mAdapter.getItem(position);
        if (item.mLevel == 1 && null != item.mLibraryBean) {
            LibraryDetailActivity.startActivity(this, item.mLibraryBean.mLibrary.mCode, item.mLibraryBean.mLibrary.mName);
        }
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void setGPSLowStatus() {
        mReaderLocationTv.setText(R.string.reader_current_position_weak);
        mReaderLocationRefreshIv.setVisibility(View.VISIBLE);
        refreshLibraryDistanceColor(false);
    }

    @Override
    public void setNoLocationPermissionStatus() {
        mReaderLocationTv.setText(R.string.reader_current_position_no_permission);
        mReaderLocationRefreshIv.setVisibility(View.VISIBLE);
        refreshLibraryDistanceColor(false);
    }

    @Override
    public void setLocationInfo(String info) {
        mReaderLocationTv.setText(getString(R.string.reader_current_position, info));
        mReaderLocationRefreshIv.setVisibility(View.GONE);
        refreshLibraryDistanceColor(true);
    }

    //设置颜色
    private void refreshLibraryDistanceColor(boolean isNormalColor) {
        ((LibraryMainBranchAdapter) mAdapter).refreshLibraryDistanceColor(isNormalColor);
    }

    @Override
    public void showLocationPermission() {
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

    @Override
    public void setNetError() {
        mRecyclerView.showError();
        mRecyclerView.setRetryRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getMainBranchLibraryList();
            }
        });
    }

    @Override
    public void setMainBranchLibraryList(List<LibraryMainBranchBean> branchLibraryList, int totalCount) {
        mAdapter.clear();
        mAdapter.addAll(branchLibraryList);

        mRecyclerView.showToastTv(String.valueOf(totalCount));
    }

    @Override
    public void setMainBranchLibraryEmpty() {
        mAdapter.clear();
        mRecyclerView.showEmpty();
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
        mSetLocationDialogFragment.show(this.getFragmentManager(), "PermissionsDialogFragment");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }
}
