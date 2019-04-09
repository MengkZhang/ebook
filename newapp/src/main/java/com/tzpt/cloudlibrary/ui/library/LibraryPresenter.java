package com.tzpt.cloudlibrary.ui.library;

import android.support.v4.util.ArrayMap;

import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.business_bean.LibraryBean;
import com.tzpt.cloudlibrary.modle.LibraryRepository;
import com.tzpt.cloudlibrary.ui.map.LocationBean;
import com.tzpt.cloudlibrary.ui.map.LocationManager;
import com.tzpt.cloudlibrary.ui.search.SearchManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 图书馆列表
 * Created by ZhiqiangJia on 2017-08-03.
 */
public class LibraryPresenter extends RxPresenter<LibraryContract.View> implements
        LibraryContract.Presenter {

    private Map<String, Object> mParameter;
    private boolean mIsSearchResult;
    private int mFilterType = LibraryFilterType.Near_Library_Book_Store_List;

    public LibraryPresenter(LibraryContract.View view) {
        attachView(view);
        mView.setPresenter(this);
    }

    @Override
    public void setLibFilterType(int filterType) {
        this.mFilterType = filterType;
    }

    @Override
    public void setParameter(Map<String, Object> parameter) {
        mParameter = new ArrayMap<>();
        mParameter.putAll(parameter);
    }

    //图书馆列表
    @Override
    public void getLibraryList(int pageNum) {
        if (mParameter == null) {
            mParameter = new HashMap<>();
        }
        mParameter.put("pageNo", String.valueOf(pageNum));
        mParameter.put("pageCount", String.valueOf(20));
        mParameter.put("lngLat", LocationManager.getInstance().getLngLat());
        mParameter.put("locationCode", LocationManager.getInstance().getLocationAdCode());
        if (mFilterType == LibraryFilterType.Library_List) {
            mParameter.put("level", "图书馆");
        }
        getLibraryList(mParameter, pageNum);
    }

    @Override
    public void setLibListType(boolean isSearch) {
        mIsSearchResult = isSearch;
    }

    @Override
    public boolean isSearchResultList() {
        return mIsSearchResult;
    }

    @Override
    public void mustShowProgressLoading() {
        mView.showRefreshLoading();
    }

    @Override
    public void saveHistoryTag(String searchContent) {
        SearchManager.saveHistoryTag(2, searchContent);
    }

    @Override
    public void getLocationInfo() {
        int status = LocationManager.getInstance().getLocationStatus();
        if (status == 2) {
            startLocation();
            mView.setGPSLowStatus();
        } else if (status == 1) {
            mView.showLocationPermission();
            mView.setNoLocationPermissionStatus();
        } else {
            mView.setLocationInfo(LocationManager.getInstance().getLocationAddress());
        }
    }

    @Override
    public void startLocation() {
        LocationManager.getInstance().startLocation(mLocationListener);
    }

    @Override
    public void refreshLocation() {
        switch (LocationManager.getInstance().getLocationStatus()) {
            case 1:
                mView.showLocationPermission();
                break;
            case 2:
                startLocation();
                break;
        }
    }

    //获取图书馆列表
    private void getLibraryList(Map<String, Object> maps, final int pageNum) {
        Subscription subscription = LibraryRepository.getInstance().getLibraryList(maps)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<LibraryBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.showNetError(pageNum == 1);
                        }
                    }

                    @Override
                    public void onNext(List<LibraryBean> libraryBeans) {
                        if (mView != null) {
                            if (libraryBeans != null && libraryBeans.size() > 0) {
                                mView.setLibraryList(libraryBeans,
                                        LibraryRepository.getInstance().getTotalCount(),
                                        LibraryRepository.getInstance().getLimitTotalCount(),
                                        pageNum == 1);
                            } else {
                                mView.setLibraryListEmpty(pageNum == 1);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private LocationManager.LocationListener mLocationListener = new LocationManager.LocationListener() {

        @Override
        public void onLocationStart() {

        }

        @Override
        public void onLocationResult(LocationBean info) {
            switch (info.mStatus) {
                case 0:
                    if (mView != null) {
                        getLibraryList(1);
                        mView.setLocationInfo(LocationManager.getInstance().getLocationAddress());
                    }
                    break;
                case 1:
                    if (mView != null) {
                        mView.setNoLocationPermissionStatus();
                    }
                    break;
                case 2:
                    if (mView != null) {
                        mView.setGPSLowStatus();
                    }
                    break;
            }
        }

        @Override
        public void onLocationStop() {

        }
    };
}
