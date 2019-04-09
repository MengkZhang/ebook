package com.tzpt.cloundlibrary.manager.ui.presenter;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.tzpt.cloundlibrary.manager.base.RxPresenter;
import com.tzpt.cloundlibrary.manager.bean.LocationBean;
import com.tzpt.cloundlibrary.manager.bean.SearchAddressBean;
import com.tzpt.cloundlibrary.manager.ui.contract.SearchAddressContract;
import com.tzpt.cloundlibrary.manager.utils.StringUtils;
import com.tzpt.cloundlibrary.manager.utils.Utils;
import com.tzpt.cloundlibrary.manager.utils.location.LocationService;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索地址
 * Created by ZhiqiangJia on 2017-10-23.
 */
public class SearchAddressPresenter extends RxPresenter<SearchAddressContract.View> implements
        SearchAddressContract.Presenter {

    private int mPageNum = 1;
    private String mSearchContent;

    @Override
    public void getLocationArea() {
        LocationService.getInstance().startLocation(Utils.getContext(), mAddressListener);
    }

    private LocationService.LocationAddressListener mAddressListener = new LocationService.LocationAddressListener() {
        @Override
        public void setLocationAddressSuccess(LocationBean bean) {
            if (null != mView) {
                mView.setLocationDistrict(bean.district);
                searchAddressList(1, "", bean.district);
            }
        }

        @Override
        public void setLocationAddressFailure(int errorType) {
            if (null != mView) {
                mView.setLocationDistrict("青羊区");
                searchAddressList(1, "", "青羊区");
            }
        }
    };

    @Override
    public void searchAddressList(int pageNum, final String content, String area) {
        if (null != mView) {
            if (pageNum == 1) {
                mView.showProgressDialog();
            }
            this.mPageNum = pageNum;
            this.mSearchContent = content;
            LocationService.getInstance().searchAddress(pageNum, area, content, mOnPoiSearchListener);
        }
    }

    private PoiSearch.OnPoiSearchListener mOnPoiSearchListener = new PoiSearch.OnPoiSearchListener() {
        @Override
        public void onPoiSearched(PoiResult poiResult, int rCode) {
            if (null != mView) {
                if (null != poiResult) {
                    List<PoiItem> poiItemList = poiResult.getPois();
                    if (null != poiItemList && poiItemList.size() > 0) {
                        List<SearchAddressBean> searchAddressBeanList = new ArrayList<>();
                        for (PoiItem item : poiItemList) {
                            SearchAddressBean bean = new SearchAddressBean();
                            bean.mAddressLocation = item.getProvinceName() + item.getCityName() + item.getAdName() + item.getSnippet();

                            String lngLat = gaoDeLatLngConvert2BaiduMapLatLng(item.getLatLonPoint().getLatitude(), item.getLatLonPoint().getLongitude());
                            bean.mLongitude = Double.parseDouble(lngLat.split(",")[0]);
                            bean.mLatitude = Double.parseDouble(lngLat.split(",")[1]);
                            bean.mAddressName = item.getTitle();
                            searchAddressBeanList.add(bean);
                        }
                        mView.setSearchAddress(searchAddressBeanList, mSearchContent, mPageNum == 1);
                    } else {
                        mView.setSearchAddressEmpty(mPageNum == 1);
                    }
                } else {
                    mView.setSearchAddressError(mPageNum == 1);
                }
            }
        }

        @Override
        public void onPoiItemSearched(PoiItem poiItem, int rCode) {

        }
    };

    private String gaoDeLatLngConvert2BaiduMapLatLng(double gaodeLat, double gaodeLng) {
        double x = gaodeLng, y = gaodeLat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * Math.PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * Math.PI);
        double bd_lon = z * Math.cos(theta) + 0.0065;
        double bd_lat = z * Math.sin(theta) + 0.006;
        return StringUtils.doubleToString(bd_lon, 6) + "," + StringUtils.doubleToString(bd_lat, 6);
    }
}
