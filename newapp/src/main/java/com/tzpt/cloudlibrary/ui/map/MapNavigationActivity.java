package com.tzpt.cloudlibrary.ui.map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.CoordinateConverter;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.utils.StringUtils;
import com.tzpt.cloudlibrary.utils.ToastUtils;
import com.tzpt.cloudlibrary.widget.AlignTextView;

import java.io.File;
import java.net.URISyntaxException;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 导航界面
 */
public class MapNavigationActivity extends BaseActivity implements
        LocationSource, AMapLocationListener {


    private static final String LIBRARY_BOOK_SUM = "bookNum";
    private static final String LIBRARY_NAME = "lib_name";
    private static final String LIBRARY_ADDRESS = "lib_address";
    private static final String LIBRARY_LNGLAT = "lib_lnglat";
    private static final String LIBRARY_DISTANCE = "lib_distance";

    public static void startActivity(Context context, String libName, String libAddress,
                                     String libLngLat, int distance, String bookNum) {
        Intent intent = new Intent(context, MapNavigationActivity.class);
        intent.putExtra(LIBRARY_NAME, libName);
        intent.putExtra(LIBRARY_ADDRESS, libAddress);
        intent.putExtra(LIBRARY_LNGLAT, libLngLat);
        intent.putExtra(LIBRARY_DISTANCE, distance);
        intent.putExtra(LIBRARY_BOOK_SUM, bookNum);
        context.startActivity(intent);
    }

    @BindView(R.id.map_view)
    MapView mMapView;
    @BindView(R.id.item_total_book_tv)
    TextView mItemTotalBookTv;
    @BindView(R.id.item_distance_tv)
    TextView mItemDistanceTv;
    @BindView(R.id.item_address_tv)
    AlignTextView mItemAddressTv;
    private AMap mAMap;

    private AMapLocationClient mLocationClient;
    private String mMyLocationAddress;
    private double mMyLatitude;
    private double mMyLongitude;
    private String mLibAddress;
    private String mLibName;
    private String mLibLng;

    @Override
    public int getLayoutId() {
        return R.layout.activity_map_navigation;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
    }

    @Override
    public void initDatas() {
        Intent intent = getIntent();
        String bookSum = intent.getStringExtra(LIBRARY_BOOK_SUM);
        mLibName = intent.getStringExtra(LIBRARY_NAME);
        mLibAddress = intent.getStringExtra(LIBRARY_ADDRESS);
        mLibLng = intent.getStringExtra(LIBRARY_LNGLAT);
        int libDistance = intent.getIntExtra(LIBRARY_DISTANCE, 0);

        mItemTotalBookTv.setText(getString(R.string.book_sum, bookSum));
        mItemDistanceTv.setText(StringUtils.mToKm(libDistance));
        mItemAddressTv.setText(mLibAddress);
        mCommonTitleBar.setTitle(mLibName);
    }

    @Override
    public void configViews() {
        mAMap = mMapView.getMap();
        mAMap.setLocationSource(this);//设置定位监听
        mAMap.getUiSettings().setMyLocationButtonEnabled(false);
        mAMap.setMyLocationEnabled(true);
        mAMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(30.555593, 104.061627)));//设置中心位置
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMapView.onCreate(savedInstanceState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        UmengHelper.setUmengResume(this);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        deactivate();
        UmengHelper.setUmengPause(this);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mMapView) {
            mMapView.onDestroy();
        }
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
        }
    }

    @OnClick({R.id.titlebar_left_btn, R.id.item_start_navigation_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.item_start_navigation_btn:
                openThirdMapNavigationApi();
                break;
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(this);
            AMapLocationClientOption locationOption = new AMapLocationClientOption();
            mLocationClient.setLocationListener(this);
            locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            locationOption.setOnceLocation(true);
            locationOption.setOnceLocationLatest(true);
            mLocationClient.setLocationOption(locationOption);
        }
        mLocationClient.startLocation();
    }

    @Override
    public void deactivate() {
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (null != aMapLocation && !TextUtils.isEmpty(mLibLng) && mLibLng.contains(",")) {
            String[] lngLats = mLibLng.split(",");
            double lat = Double.parseDouble(lngLats[1]);
            double lon = Double.parseDouble(lngLats[0]);

            //其他坐标转换为高德坐标-图书馆位置
            LatLng baiDuMapLngLat = LocationManager.convert2Amap(new LatLng(lat, lon), CoordinateConverter.CoordType.BAIDU);
            Marker libraryMarker = mAMap.addMarker(new MarkerOptions()
                    .position(baiDuMapLngLat)
                    .title(mLibName)
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.gps_point0)));
            libraryMarker.showInfoWindow();
            //我的位置
            mMyLatitude = aMapLocation.getLatitude();
            mMyLongitude = aMapLocation.getLongitude();
            mMyLocationAddress = aMapLocation.getAddress();

            LatLng myLocation = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
            mAMap.addMarker(new MarkerOptions()
                    .position(myLocation)
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.gps_point)));
            //设置两个坐标点的范围
            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            boundsBuilder.include(baiDuMapLngLat);
            boundsBuilder.include(myLocation);
            mAMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 250));
        }
    }

    //是否已安装地图
    private boolean isInstallByRead(String packgeName) {
        return new File("/data/data/" + packgeName).exists();
    }

    /**
     * 启动高德，百度地图
     */
    private void openThirdMapNavigationApi() {
        //点击后首先判断安装了那些地图:高德，百度，腾讯(g高德和腾讯的坐标系一样)
        if (!TextUtils.isEmpty(mLibLng) && mLibLng.contains(",")) {
            String[] packages = {"com.autonavi.minimap", "com.baidu.BaiduMap"};
            String libraryLatLng = mLibLng;//图书馆的经纬度
            String[] temp = libraryLatLng.split(",");
            double libLat = Double.parseDouble(temp[1]);
            double libLng = Double.parseDouble(temp[0]);
            //To do 距离太近换成步行
            if (isInstallByRead(packages[0])) {//如果已经安装高德地图
                try {
                    LatLng baiDuMapLngLat = LocationManager.convert2Amap(new LatLng(libLat, libLng), CoordinateConverter.CoordType.BAIDU);
                    StringBuilder gaoDeParameter = new StringBuilder();
                    gaoDeParameter
                            .append("amapuri://route/plan/")
                            .append("?sid=BGVIS1")
                            .append("&sourceApplication=云图书馆")
                            .append("&slat=")
                            .append(mMyLatitude)
                            .append("&slon=")
                            .append(mMyLongitude)
                            .append("&sname=")
                            .append(mMyLocationAddress)
                            .append("&did=BGVIS2")
                            .append("&dlat=")
                            .append(baiDuMapLngLat.latitude)
                            .append("&dlon=")
                            .append(baiDuMapLngLat.longitude)
                            .append("&dname=")
                            .append(mLibAddress)
                            .append("&dev=0")
                            .append("&t=0");
                    startActivity(Intent.getIntent(gaoDeParameter.toString()));

                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            } else if (isInstallByRead(packages[1])) {//如果已经安装百度地图
                try {
                    //转换为百度地图坐标
                    String baiLatLong = LocationManager.gaoDeLatLngConvert2BaiDuMapLatLng(mMyLatitude, mMyLongitude);

                    StringBuilder baiDuParameter = new StringBuilder();
                    baiDuParameter
                            .append("intent://map/direction")
                            .append("?origin=")
                            .append("latlng:")
                            .append(baiLatLong)
                            .append("|")
                            .append("name:")
                            .append(mMyLocationAddress)
                            .append("&destination=")
                            .append("latlng:")
                            .append(libLat)
                            .append(",")
                            .append(libLng)
                            .append("|")
                            .append("name:")
                            .append(mLibAddress)
                            .append("&mode=driving")
                            .append("&src=")
                            .append("跳蚤平台|云图书馆")
                            .append("#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                    startActivity(Intent.getIntent(baiDuParameter.toString()));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            } else {
                ToastUtils.showSingleToast("您的设备没有安装地图客户端!");
            }
        }
    }
}
