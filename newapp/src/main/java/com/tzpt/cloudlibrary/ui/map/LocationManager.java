package com.tzpt.cloudlibrary.ui.map;

import android.text.TextUtils;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.CoordinateConverter;
import com.amap.api.maps2d.model.LatLng;
import com.tzpt.cloudlibrary.CloudLibraryApplication;
import com.tzpt.cloudlibrary.modle.local.SharedPreferencesUtil;
import com.tzpt.cloudlibrary.utils.Utils;

import java.math.BigDecimal;

/**
 * 经纬度
 * Created by ZhiqiangJia on 2017-09-04.
 */

public class LocationManager {

    private static final String LOCATION = "LocationBean";

    private volatile static LocationManager mInstance;
    private LocationBean mLocationBean;

    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;

    private LocationManager() {
        mLocationBean = SharedPreferencesUtil.getInstance().getObject(LOCATION, LocationBean.class);
        if (mLocationBean == null) {
            mLocationBean = new LocationBean();
        }
        if (TextUtils.isEmpty(mLocationBean.mLngLat)) {
            mLocationBean.mLngLat = "104.065757,30.657328";
        }
        if (TextUtils.isEmpty(mLocationBean.mProvince)) {
            mLocationBean.mProvince = "四川省";
        }
        if (TextUtils.isEmpty(mLocationBean.mCity)) {
            mLocationBean.mCity = "成都市";
        }
        if (TextUtils.isEmpty(mLocationBean.mDistrict)) {
            mLocationBean.mDistrict = "青羊区";
        }
        if (TextUtils.isEmpty(mLocationBean.mAdCode)) {
            mLocationBean.mAdCode = "510105";
        }
        saveLocationBean(mLocationBean);
    }

    public static LocationManager getInstance() {
        synchronized (LocationManager.class) {
            if (mInstance == null) {
                mInstance = new LocationManager();
            }
        }
        return mInstance;
    }

    /**
     * 获取经纬度
     *
     * @return 经纬度
     */
    public String getLngLat() {
        String[] latLngStr = mLocationBean.mLngLat.split(",");
        return gaoDeLatLngConvert2BaiDuMapLatLng(Double.valueOf(latLngStr[0]), Double.valueOf(latLngStr[1]));
    }

    /**
     * 获取当前选择的地区标题
     *
     * @return 当前选择的地区标题
     */
    String getLastAreaTitle() {
        //-切换城市优先
        if (!TextUtils.isEmpty(mLocationBean.mSelectedAreaTitle)) {
            return mLocationBean.mSelectedAreaTitle;
        }
        //定位地址
        if (!TextUtils.isEmpty(mLocationBean.mCity)) {
            return mLocationBean.mCity;
        }
        return null;
    }

    /**
     * 获取最小行政单位，优先级：选择的行政单位->定位的区级单位->定位的市级单位
     *
     * @return 最小行政单位
     */
    public String getLastArea() {
        if (!TextUtils.isEmpty(mLocationBean.mSelectedArea)) {
            return mLocationBean.mSelectedArea;
        }
        if (!TextUtils.isEmpty(mLocationBean.mDistrict)) {
            return mLocationBean.mDistrict;
        }
        return mLocationBean.mCity;
    }

    /**
     * 获取行政代码，优先选择地址的行政代码，其次定位地址行政代码
     *
     * @return 行政代码
     */
    public String getLocationAdCode() {
        if (!TextUtils.isEmpty(mLocationBean.mSelectAdCode)) {
            return mLocationBean.mSelectAdCode;
        }
        return mLocationBean.mAdCode;
    }

    //保存当前地址信息
    private void saveLocationBean(LocationBean locationBean) {
        if (null != locationBean) {
            SharedPreferencesUtil.getInstance().putObject(LOCATION, locationBean);
        }
    }

    void saveSelectedArea(String area, String areaTitle, String adCode) {
        mLocationBean.mSelectAdCode = adCode;
        mLocationBean.mSelectedArea = area;
        mLocationBean.mSelectedAreaTitle = areaTitle;
        saveLocationBean(mLocationBean);
    }

    void dealLocationToSelected() {
        if (!TextUtils.isEmpty(mLocationBean.mDistrict)) {
            mLocationBean.mSelectedArea = mLocationBean.mDistrict;
            if (TextUtils.isEmpty(mLocationBean.mCity)
                    || mLocationBean.mCity.equals(mLocationBean.mProvince)) {
                mLocationBean.mSelectedAreaTitle = mLocationBean.mDistrict;
            } else {
                mLocationBean.mSelectedAreaTitle = mLocationBean.mCity;
            }
        } else {
            mLocationBean.mSelectedArea = mLocationBean.mCity;
            mLocationBean.mSelectedAreaTitle = mLocationBean.mCity;
        }
        mLocationBean.mSelectAdCode = mLocationBean.mAdCode;
        saveLocationBean(mLocationBean);
    }

    public int getLocationStatus() {
        if (mLocationBean != null) {
            return mLocationBean.mStatus;
        }
        return 2;
    }

    public String getLocationAddress() {
        return mLocationBean.mAddress;
    }

    public String getLocationProvinceCityDistrict() {
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(mLocationBean.mProvince)) {
            sb.append(mLocationBean.mProvince);
        }
        if (!TextUtils.isEmpty(mLocationBean.mCity)) {
            sb.append(mLocationBean.mCity);
        }
        if (!TextUtils.isEmpty(mLocationBean.mDistrict)) {
            sb.append(mLocationBean.mDistrict);
        }
        return sb.toString();
    }

    public void startLocation() {
        startLocation(null);
    }

    public void startLocation(LocationListener listener) {
        this.mListener = listener;
        if (mLocationClient == null) {
//            mLocationClient = new AMapLocationClient(Utils.getContext());
            mLocationClient = new AMapLocationClient(CloudLibraryApplication.getAppContext());
            mLocationOption = getDefaultOption();
            mLocationClient.setLocationListener(mLocationListener);
            mLocationClient.setLocationOption(mLocationOption);
        }
        mLocationClient.startLocation();
        if (mListener != null) {
            mListener.onLocationStart();
        }
    }

    public void stopLocation() {
        if (null != mLocationClient) {
            mLocationClient.unRegisterLocationListener(mLocationListener);
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        if (mListener != null) {
            mListener.onLocationStop();
        }
        mLocationClient = null;
        mLocationOption = null;
    }

    private AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (null != aMapLocation) {
                switch (aMapLocation.getErrorCode()) {
                    case 0:
                        mLocationBean.mStatus = 0;
                        mLocationBean.mAddress = aMapLocation.getStreet() + aMapLocation.getStreetNum() + aMapLocation.getPoiName();
                        mLocationBean.mLngLat = aMapLocation.getLongitude() + "," + aMapLocation.getLatitude();
                        mLocationBean.mProvince = aMapLocation.getProvince();
                        mLocationBean.mCity = aMapLocation.getCity();
                        mLocationBean.mDistrict = aMapLocation.getDistrict();
                        mLocationBean.mAdCode = aMapLocation.getAdCode();
                        mLocationBean.mErrorCode = aMapLocation.getErrorCode();
//                        Log.e("AMapManager", "**Province: " + mLocationBean.mProvince + " **City: " + mLocationBean.mCity + " **District: " + mLocationBean.mDistrict + " **AdCode: " + aMapLocation.getAdCode() + " **CityCode: " + aMapLocation.getCityCode());
                        saveLocationBean(mLocationBean);
                        if (null != mListener) {
                            mListener.onLocationResult(mLocationBean);
                        }
                        break;
                    case 10:
                    case 12:
                    case 13:
                        dealLocationException(1, aMapLocation.getErrorCode());
                        break;
                    default:
                        dealLocationException(2, aMapLocation.getErrorCode());
                        break;
                }
            }
        }
    };

    /**
     * 定位异常处理
     *
     * @param status    异常原因 1：没有权限，2：定位失败
     * @param errorCode 异常码
     */
    private void dealLocationException(int status, int errorCode) {
        mLocationBean.mStatus = status;
        mLocationBean.mErrorCode = errorCode;
        saveLocationBean(mLocationBean);
        if (mListener != null) {
            mListener.onLocationResult(mLocationBean);
        }
    }

    /**
     * 默认的定位参数
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mOption.setGpsFirst(false);
        mOption.setHttpTimeOut(8000);
        mOption.setInterval(1000);
        mOption.setNeedAddress(true);
        mOption.setOnceLocation(true);
        mOption.setOnceLocationLatest(false);
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);
        mOption.setSensorEnable(true);
        mOption.setWifiScan(true);
        mOption.setLocationCacheEnable(false);
        return mOption;
    }

    private LocationListener mListener;

    public interface LocationListener {

        void onLocationStart();

        void onLocationResult(LocationBean info);

        void onLocationStop();

    }


    /**
     * 其他地图坐标转换成高德坐标
     *
     * @param sourceLatLng
     * @param coord:BAIDU, MAPBAR, MAPABC,  SOSOMAP, ALIYUN,GPS;GOOGLE,
     * @return
     */
    public static LatLng convert2Amap(LatLng sourceLatLng, CoordinateConverter.CoordType coord) {
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(coord);
        converter.coord(sourceLatLng);
        return converter.convert();
    }

    /**
     * 高德经纬度装换为百度经纬度
     *
     * @param gaoDeLat
     * @param gaoDeLng
     * @return
     */
    public static String gaoDeLatLngConvert2BaiDuMapLatLng(double gaoDeLat, double gaoDeLng) {
        double x = gaoDeLng, y = gaoDeLat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * Math.PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * Math.PI);
        double bd_lon = z * Math.cos(theta) + 0.0065;
        double bd_lat = z * Math.sin(theta) + 0.006;
        return doubleToString(bd_lat, 6) + "," + doubleToString(bd_lon, 6);
    }

    /**
     * 精确到小数点N位
     *
     * @param value
     * @param bit
     * @return
     */
    private static String doubleToString(double value, int bit) {
        // 这里面的bd是自定义的变量，即最后取得小数点后若干位的数，2表示小数点后两位
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(bit, BigDecimal.ROUND_HALF_UP);
        return bd.toString();
    }

}
