package com.tzpt.cloudlibrary.ui.bookstore;

import android.support.v4.util.ArrayMap;

import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.business_bean.BaseResultData;
import com.tzpt.cloudlibrary.business_bean.LibraryBean;
import com.tzpt.cloudlibrary.modle.BookStoreRepository;
import com.tzpt.cloudlibrary.ui.map.LocationBean;
import com.tzpt.cloudlibrary.ui.map.LocationManager;
import com.tzpt.cloudlibrary.ui.search.SearchManager;

import java.util.List;
import java.util.Map;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 书店
 * Created by ZhiqiangJia on 2017-08-03.
 */
public class BookStorePresenter extends RxPresenter<BookStoreContract.View> implements
        BookStoreContract.Presenter {

    private ArrayMap<String, Object> mParameter;
    private int mBookStoreFilterType = BookStoreFilterType.BOOK_STORE_LIST;

    public BookStorePresenter(BookStoreContract.View view) {
        attachView(view);
        mView.setPresenter(this);
    }

    @Override
    public void setBookStoreFilterType(int filterType) {
        this.mBookStoreFilterType = filterType;
    }

    @Override
    public void setParameter(Map<String, Object> parameter) {
        mParameter = new ArrayMap<>();
        mParameter.putAll(parameter);
    }

    @Override
    public void getBookStoreList(int pageNum) {
        if (mParameter == null) {
            mParameter = new ArrayMap<>();
        }
        mParameter.put("pageNo", pageNum);
        mParameter.put("pageCount", 20);
        mParameter.put("lngLat", LocationManager.getInstance().getLngLat());
        mParameter.put("locationCode", LocationManager.getInstance().getLocationAdCode());
        mParameter.put("level", "书店");
        getBookStoreList(mParameter, pageNum);
    }

    /**
     * 是否搜索结果
     *
     * @return
     */
    @Override
    public boolean isSearchResultList() {
        return mBookStoreFilterType == BookStoreFilterType.BOOK_STORE_SEARCH_LIST
                || mBookStoreFilterType == BookStoreFilterType.BOOK_STORE_HIGH_SEARCH_LIST;
    }

    @Override
    public void mustShowProgressLoading() {
        mView.showRefreshLoading();
    }

    //保存书店搜索内容
    @Override
    public void saveHistoryTag(String searchContent) {
        SearchManager.saveHistoryTag(6, searchContent);
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

    //获取书店列表
    private void getBookStoreList(ArrayMap<String, Object> maps, final int pageNum) {

        Subscription subscription = BookStoreRepository.getInstance().getBookStoreList(maps)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResultData<List<LibraryBean>>>() {
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
                    public void onNext(BaseResultData<List<LibraryBean>> bookStoreBean) {
                        if (mView != null) {
                            if (bookStoreBean != null && bookStoreBean.resultList != null && bookStoreBean.resultList.size() > 0) {
                                mView.setBookStoreList(bookStoreBean.resultList, bookStoreBean.mTotalCount, bookStoreBean.mLimitCount, pageNum == 1);
                            } else {
                                mView.setBookStoreListEmpty(pageNum == 1);
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
                        getBookStoreList(1);
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
