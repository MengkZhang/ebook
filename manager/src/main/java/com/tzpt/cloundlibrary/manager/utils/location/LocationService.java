package com.tzpt.cloundlibrary.manager.utils.location;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.poisearch.PoiSearch;
import com.tzpt.cloundlibrary.manager.bean.LocationBean;
import com.tzpt.cloundlibrary.manager.utils.StringUtils;
import com.tzpt.cloundlibrary.manager.utils.Utils;

/**
 * 定位
 * Created by longchuanran on 2017/7/17.
 */
public class LocationService {

    private AMapLocationClient mLocationClient = null;
    private AMapLocationClientOption mLocationOption = null;
    private LocationAddressListener mListener;
    private double mLatitude;
    private double mLongitude;
    private static final String sSearchType = "13|14|15|19|99|11||10|09|08|07|06|05|04|03|02|01";
    private volatile static LocationService sInstance;

    private LocationService() {
    }

    public static LocationService getInstance() {

        if (sInstance == null) {
            synchronized (LocationService.class) {
                if (sInstance == null) {
                    sInstance = new LocationService();
                }
            }
        }
        return sInstance;
    }


    /**
     * 默认的定位参数
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        option.setGpsFirst(true);
        option.setHttpTimeOut(8000);
        option.setInterval(2000);
        option.setNeedAddress(true);
        option.setOnceLocation(false);
        option.setOnceLocationLatest(false);
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);
        option.setSensorEnable(false);
        option.setWifiScan(true);
        option.setLocationCacheEnable(true);
        return option;
    }

    /**
     * 定位监听
     */
    private AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (null != aMapLocation && null != mListener) {
                switch (aMapLocation.getErrorCode()) {
                    case 0:     //定位成功
                        String lngLat = gaoDeLatLngConvert2BaiduMapLatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                        mLatitude = aMapLocation.getLatitude();
                        mLongitude = aMapLocation.getLongitude();
                        LocationBean bean = new LocationBean();
                        bean.currentDistrictCode = aMapLocation.getAdCode();
                        bean.district = aMapLocation.getDistrict();
                        bean.lngLat = lngLat;
                        bean.currentLocation = aMapLocation.getProvince() + aMapLocation.getCity()
                                + aMapLocation.getDistrict() + aMapLocation.getStreet();
                         bean.currentPCDInfo = aMapLocation.getProvince() + aMapLocation.getCity()
                                + aMapLocation.getDistrict() + aMapLocation.getStreet();
                        bean.city = aMapLocation.getCity();

                        mListener.setLocationAddressSuccess(bean);
                        break;
                    case 10:
                    case 12:
                    case 13:    //为开启定位权限
                        mListener.setLocationAddressFailure(1);
                        break;
                    default:    //定位失败
                        mListener.setLocationAddressFailure(0);
                        break;
                }
                stopLocation();
            }
        }
    };

    /**
     * 高德经纬度装换为百度经纬度
     *
     * @param gaodeLat
     * @param gaodeLng
     * @return
     */
    private String gaoDeLatLngConvert2BaiduMapLatLng(double gaodeLat, double gaodeLng) {
        double x = gaodeLng, y = gaodeLat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * Math.PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * Math.PI);
        double bd_lon = z * Math.cos(theta) + 0.0065;
        double bd_lat = z * Math.sin(theta) + 0.006;
        return StringUtils.doubleToString(bd_lon, 6) + "," + StringUtils.doubleToString(bd_lat, 6);
    }

    /**
     * 开始定位
     *
     * @param context
     * @param listener
     */
    public void startLocation(Context context, LocationAddressListener listener) {
        this.mListener = listener;
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(context);
            mLocationOption = getDefaultOption();
            mLocationClient.setLocationListener(mLocationListener);
            mLocationClient.setLocationOption(mLocationOption);
        }
        mLocationClient.startLocation();
    }

    /**
     * 停止定位
     */
    private void stopLocation() {
        if (null != mLocationClient) {
            mLocationClient.unRegisterLocationListener(mLocationListener);
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
        mLocationOption = null;
    }


    /**
     * 定位回调
     */
    public interface LocationAddressListener {

        void setLocationAddressSuccess(LocationBean bean);

        /**
         * 0 定位失败 1 获取定位权限失败
         *
         * @param errorType
         */
        void setLocationAddressFailure(int errorType);
    }

    /**
     * 搜索地址
     *
     * @param keyWord
     */
    public void searchAddress(int pageNum, String area, String keyWord, PoiSearch.OnPoiSearchListener searchListener) {
        // 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        PoiSearch.Query query = new PoiSearch.Query(TextUtils.isEmpty(keyWord) ? area : keyWord, sSearchType, area);
        query.setPageSize(20);              // 设置每页最多返回多少条
        query.setPageNum(pageNum);          // 设置查第一页
        if (mLatitude != 0 && mLongitude != 0) {
            query.setLocation(new LatLonPoint(mLatitude, mLongitude));
        }
        query.setCityLimit(true);
        query.setDistanceSort(true);
        PoiSearch poiSearch = new PoiSearch(Utils.getContext(), query);
        poiSearch.setOnPoiSearchListener(searchListener);
        poiSearch.searchPOIAsyn();
    }


}
