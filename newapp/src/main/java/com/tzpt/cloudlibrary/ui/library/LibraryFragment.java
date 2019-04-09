package com.tzpt.cloudlibrary.ui.library;

import android.Manifest;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseListFragment;
import com.tzpt.cloudlibrary.business_bean.LibraryBean;
import com.tzpt.cloudlibrary.ui.bookstore.BookStoreDetailActivity;
import com.tzpt.cloudlibrary.ui.permissions.Permission;
import com.tzpt.cloudlibrary.ui.permissions.PermissionsDialogFragment;
import com.tzpt.cloudlibrary.ui.permissions.RxPermissions;
import com.tzpt.cloudlibrary.utils.DpPxUtils;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * 图书馆
 * Created by ZhiqiangJia on 2017-08-17.
 */
public class LibraryFragment extends BaseListFragment<LibraryBean> implements
        RecyclerArrayAdapter.OnItemClickListener,
        LibraryContract.View {

    @BindView(R.id.reader_location_tv)
    TextView mReaderLocationTv;
    @BindView(R.id.reader_location_refresh_iv)
    ImageView mReaderLocationRefreshIv;

    private PermissionsDialogFragment mSetLocationDialogFragment;
    private int mCurrentPage = 1;
    private LibraryContract.Presenter mPresenter;
    private boolean mIsPrepared;
    private boolean mIsFirstLoad = true;

    @OnClick(R.id.reader_location_refresh_iv)
    public void onViewClicked() {
        mPresenter.refreshLocation();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_library;
    }

    @Override
    public void initDatas() {
        mIsPrepared = true;
        lazyLoad();
    }

    @Override
    public void configViews() {
        mAdapter = new LibraryAdapter(getActivity(), false, false);
        initAdapter(true, true);
        mRecyclerView.setDividerDrawable(R.drawable.divider_rv_vertical_four);

    }

    //设置颜色
    private void refreshLibraryDistanceColor(boolean isNormalColor) {
        if (null != mAdapter && mAdapter instanceof LibraryAdapter) {
            LibraryAdapter adapter = (LibraryAdapter) mAdapter;
            adapter.refreshLibraryDistanceColor(isNormalColor);
        }
    }

    @Override
    public void onRefresh() {
        mPresenter.getLibraryList(1);
    }

    @Override
    public void onLoadMore() {
        mPresenter.getLibraryList(mCurrentPage + 1);
    }

    @Override
    public void onItemClick(int position) {
        LibraryBean libraryBean = mAdapter.getItem(position);
        if (null != libraryBean) {
            //书店
            if (libraryBean.mIsBookStore) {
                if (mPresenter.isSearchResultList()) {
                    BookStoreDetailActivity.startActivityForSearchResult(getActivity(), libraryBean.mLibrary.mCode, libraryBean.mLibrary.mName);
                } else {
                    BookStoreDetailActivity.startActivity(getActivity(), libraryBean.mLibrary.mCode, libraryBean.mLibrary.mName);
                }
            } else {
                //图书馆
                if (mPresenter.isSearchResultList()) {
                    LibraryDetailActivity.startActivityForSearchResult(getActivity(), libraryBean.mLibrary.mCode, libraryBean.mLibrary.mName);
                } else {
                    LibraryDetailActivity.startActivity(getActivity(), libraryBean.mLibrary.mCode, libraryBean.mLibrary.mName);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        UmengHelper.setPageStart("LibraryFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        UmengHelper.setPageEnd("LibraryFragment");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != mPresenter) {
            mPresenter.detachView();
            mPresenter = null;
        }
    }

    @Override
    protected void lazyLoad() {
        if (!mIsVisible || !mIsPrepared) {
            return;
        }
        if (null != mPresenter && mIsFirstLoad) {//懒加载，只允许第一次加载
            this.mIsFirstLoad = false;
            mPresenter.getLocationInfo();
            mPresenter.getLibraryList(1);
        }
    }

    @Override
    public void setPresenter(LibraryContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void setLibraryList(List<LibraryBean> libraryList, int totalCount, int limitTotalCount, boolean refresh) {
        if (refresh) {
            mAdapter.clear();
            mCurrentPage = 1;
        } else {
            mCurrentPage = mCurrentPage + 1;
        }
        mAdapter.addAll(libraryList);
        mRecyclerView.setRefreshing(false);
        if (mPresenter.isSearchResultList()) {
            mRecyclerView.showToastTv(getString(R.string.library_list_tips, mAdapter.getCount(), totalCount));
        } else {
            mRecyclerView.showToastTv(String.valueOf(mAdapter.getCount()));
            if (mAdapter.getCount() >= limitTotalCount) {
                mAdapter.stopMore();
                //todo 加载已经到底啦

            }
        }
    }

    @Override
    public void showNetError(boolean refresh) {
        mRecyclerView.setRefreshing(false);
        if (refresh) {
            mAdapter.clear();
            mRecyclerView.showError();
        } else {
            mAdapter.pauseMore();
        }
    }

    @Override
    public void setLibraryListEmpty(boolean refresh) {
        mRecyclerView.setRefreshing(false);
        if (refresh) {
            mAdapter.clear();
            mRecyclerView.showEmpty();
        } else {
            mAdapter.stopMore();
        }
    }

    @Override
    public void showRefreshLoading() {
        mAdapter.clear();
        mRecyclerView.showProgress();
    }

    @Override
    public void setGPSLoadingStatus() {
//        mSwitchCityLocation.setText(R.string.location_loading);
//        mLocationChooseTipTv.setVisibility(View.GONE);
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

    @Override
    public void showLocationPermission() {
        if (Build.VERSION.SDK_INT < 23) {
            showSetPermissionPopUpWindow();
            return;
        }
        RxPermissions rxPermissions = new RxPermissions(getActivity());
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
        mSetLocationDialogFragment.show(getActivity().getFragmentManager(), "PermissionsDialogFragment");
    }
}
