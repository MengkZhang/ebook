package com.tzpt.cloudlibrary.ui.map;

import android.text.TextUtils;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.bean.SwitchCityBean;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.local.SharedPreferencesUtil;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseResultEntityVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.CityVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.DistrictVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.LastDistrictVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.ProvinceVo;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 切换城市
 * Created by ZhiqiangJia on 2017-09-07.
 */
public class SwitchCityPresenter extends RxPresenter<SwitchCityContract.View> implements
        SwitchCityContract.Presenter {

    private List<SwitchCityBean> mSwitchProvinceList = new ArrayList<>();
    private List<SwitchCityBean> mSwitchCityList = new ArrayList<>();
    private List<SwitchCityBean> mFistCityDistrictList = new ArrayList<>();

    private String mProvinceCode;
    private String mProvinceName;
    private String mCityName;
    private String mCityCode;

    private boolean mIsLibAdvancedSearchArea;
    private String mLastAreaTitle;
    private int mType;

    @Override
    public void getProvince() {
        //清除临时城市列表
        if (mSwitchCityList != null) {
            mProvinceName = "";
            mProvinceCode = "";
            mSwitchCityList.clear();
        }
        mType = 1;
        mView.changeDataList(mType, "选择省");

        if (null != mSwitchProvinceList && mSwitchProvinceList.size() > 0) {
            mView.setDataList(mSwitchProvinceList);
            return;
        }
        Subscription subscription = DataRepository.getInstance().getProvinceList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResultEntityVo<List<ProvinceVo>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView && mType == 1) {
                            mView.setErrorMsg(R.string.network_fault);
                        }
                    }

                    @Override
                    public void onNext(BaseResultEntityVo<List<ProvinceVo>> listBaseResultEntityVo) {
                        if (mView != null && mType == 1) {
                            if (listBaseResultEntityVo.status == 200
                                    && listBaseResultEntityVo.data != null) {
                                List<SwitchCityBean> switchProvinceList = new ArrayList<>();
                                for (ProvinceVo item : listBaseResultEntityVo.data) {
                                    SwitchCityBean bean = new SwitchCityBean();
                                    bean.mCode = item.code;
                                    bean.mShowName = item.name;
                                    bean.mRealName = item.name;
                                    bean.mLevel = 1;
                                    switchProvinceList.add(bean);
                                }
                                mSwitchProvinceList.clear();
                                mSwitchProvinceList.addAll(switchProvinceList);
                                mView.setDataList(mSwitchProvinceList);
                            } else {
                                mView.setErrorMsg(R.string.network_fault);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void getCity(String provinceName, String provinceCode) {
        mProvinceName = provinceName;
        mProvinceCode = provinceCode;

        mCityName = "";
        mCityCode = "";

        mType = 2;
        mView.changeDataList(mType, provinceName);

        if (null != mSwitchCityList && mSwitchCityList.size() > 0) {
            mView.setDataList(mSwitchCityList);
            return;
        }
        Subscription subscription = DataRepository.getInstance().getCityList(provinceCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResultEntityVo<List<CityVo>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView && mType == 2) {
                            mView.setErrorMsg(R.string.network_fault);
                        }
                    }

                    @Override
                    public void onNext(BaseResultEntityVo<List<CityVo>> listBaseResultEntityVo) {
                        if (null != mView && mType == 2) {
                            if (listBaseResultEntityVo.status == 200
                                    && listBaseResultEntityVo.data != null) {
                                List<SwitchCityBean> switchCityList = new ArrayList<>();
                                for (CityVo item : listBaseResultEntityVo.data) {
                                    SwitchCityBean bean = new SwitchCityBean();
                                    bean.mCode = item.code;
                                    bean.mShowName = item.name;
                                    bean.mRealName = item.name;
                                    bean.mLevel = 2;
                                    bean.mNoChild = item.hasChild.equals("0");
                                    switchCityList.add(bean);
                                }
                                mSwitchCityList.clear();
                                mSwitchCityList.addAll(switchCityList);
                                mView.setDataList(mSwitchCityList);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void getCity() {
        getCity(mProvinceName, mProvinceCode);
    }

    @Override
    public void getDistrict(String cityName, String cityCode) {
        mCityName = cityName;
        mCityCode = cityCode;

        mType = 3;
        mView.changeDataList(mType, cityName);

        Subscription subscription = DataRepository.getInstance().getDistrictList(cityCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResultEntityVo<List<DistrictVo>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView && mType == 3) {
                            mView.setErrorMsg(R.string.network_fault);
                        }
                    }

                    @Override
                    public void onNext(BaseResultEntityVo<List<DistrictVo>> listBaseResultEntityVo) {
                        if (null != mView && mType == 3) {
                            if (listBaseResultEntityVo.status == 200
                                    && listBaseResultEntityVo.data != null) {
                                List<SwitchCityBean> switchDistrictList = new ArrayList<>();
                                if (mIsLibAdvancedSearchArea) {
                                    SwitchCityBean bean = new SwitchCityBean();
                                    bean.mCode = mCityCode;
                                    bean.mShowName = "全市";
                                    bean.mRealName = mCityName;
                                    bean.mLevel = 3;
                                    switchDistrictList.add(bean);
                                }
                                for (DistrictVo item : listBaseResultEntityVo.data) {
                                    SwitchCityBean bean = new SwitchCityBean();
                                    bean.mCode = item.code;
                                    bean.mShowName = item.name;
                                    bean.mRealName = item.name;
                                    bean.mLevel = 3;
                                    switchDistrictList.add(bean);
                                }
                                mView.setDataList(switchDistrictList);
                            } else {
                                mView.setErrorMsg(R.string.network_fault);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void getDistrict() {
        getDistrict(mCityName, mCityCode);
    }

    @Override
    public void getLastDistrict() {
        getLastDistrict(false, null, null);
    }

    @Override
    public void getLastDistrict(boolean isLibAdvancedSearchArea, String lastAreaTitle, String adCode) {
        mIsLibAdvancedSearchArea = isLibAdvancedSearchArea;
        mLastAreaTitle = lastAreaTitle;
        if (TextUtils.isEmpty(mLastAreaTitle)) {
            mLastAreaTitle = LocationManager.getInstance().getLastAreaTitle();
        }

        mType = 0;
        mView.changeDataList(mType, mLastAreaTitle);

        if (null != mFistCityDistrictList && mFistCityDistrictList.size() > 0) {
            mView.setDataList(mFistCityDistrictList);
            return;
        }

        if (TextUtils.isEmpty(adCode)) {
            adCode = LocationManager.getInstance().getLocationAdCode();
        }
        Subscription subscription = DataRepository.getInstance().getLastDistrictList(adCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResultEntityVo<LastDistrictVo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView && mType == 0) {
                            mView.setErrorMsg(R.string.network_fault);
                        }
                    }

                    @Override
                    public void onNext(BaseResultEntityVo<LastDistrictVo> lastDistrictVoBaseResultEntityVo) {
                        if (mView != null && mType == 0) {
                            if (lastDistrictVoBaseResultEntityVo.status == 200
                                    && lastDistrictVoBaseResultEntityVo.data != null
                                    && lastDistrictVoBaseResultEntityVo.data.list.size() > 0) {
                                List<SwitchCityBean> switchDistrictList = new ArrayList<>();
                                if (mIsLibAdvancedSearchArea && lastDistrictVoBaseResultEntityVo.data.other != null) {
                                    SwitchCityBean bean = new SwitchCityBean();
                                    bean.mCode = lastDistrictVoBaseResultEntityVo.data.other.code;
                                    bean.mShowName = "全市";
                                    bean.mRealName = lastDistrictVoBaseResultEntityVo.data.other.name;
                                    bean.mLevel = lastDistrictVoBaseResultEntityVo.data.other.level;
                                    switchDistrictList.add(bean);
                                }
                                for (LastDistrictVo.LastDistrictItemVo item : lastDistrictVoBaseResultEntityVo.data.list) {
                                    SwitchCityBean bean = new SwitchCityBean();
                                    bean.mCode = item.code;
                                    bean.mShowName = item.name;
                                    bean.mRealName = item.name;
                                    bean.mLevel = item.level;
                                    switchDistrictList.add(bean);
                                }
                                mFistCityDistrictList.clear();
                                mFistCityDistrictList.addAll(switchDistrictList);
                                mView.setDataList(mFistCityDistrictList);
                            } else {
                                mView.setErrorMsg(R.string.network_fault);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    //清除所有数据
    @Override
    public void clearTempCityData() {
        mSwitchProvinceList.clear();
        mSwitchCityList.clear();
        mFistCityDistrictList.clear();
    }

    @Override
    public void dealSelectedArea(String area, String adCode, int level) {
        if (level == 2) {
            LocationManager.getInstance().saveSelectedArea(area, area, adCode);
        } else {
            if (TextUtils.isEmpty(mCityName)) {
                LocationManager.getInstance().saveSelectedArea(area, LocationManager.getInstance().getLastAreaTitle(), adCode);
            } else {
                LocationManager.getInstance().saveSelectedArea(area, mCityName, adCode);
            }
        }
        getLaunchImgData();
        DataRepository.getInstance().clearHomeData();
        mView.finishActivity();
    }

    @Override
    public String getLastAreaTitle(String area, int level) {
        if (level == 2) {
            return area;
        } else {
            if (TextUtils.isEmpty(mCityName)) {
                if (TextUtils.isEmpty(mLastAreaTitle)) {
                    return LocationManager.getInstance().getLastAreaTitle();
                } else {
                    return mLastAreaTitle;
                }
            } else {
                return mCityName;
            }
        }
    }


    @Override
    public void getLocationInfo() {
        int status = LocationManager.getInstance().getLocationStatus();
        if (status == 2) {
            mView.setGPSLowStatus();
            startLocation();
        } else if (status == 1) {
            mView.showLocationPermission();
            mView.setNoLocationPermissionStatus();
        } else {
            mView.setLocationInfo(LocationManager.getInstance().getLocationProvinceCityDistrict());
        }
    }

    @Override
    public void startLocation() {
        LocationManager.getInstance().startLocation(mListener);
    }

    @Override
    public void selectLocationArea() {
        switch (LocationManager.getInstance().getLocationStatus()) {
            case 0:
                LocationManager.getInstance().dealLocationToSelected();
                getLaunchImgData();
                DataRepository.getInstance().clearHomeData();
                mView.finishActivity();
                break;
            case 1:
                mView.showLocationPermission();
                break;
            case 2:
                startLocation();
                break;
        }
    }

    private void getLaunchImgData() {
        String adCode = LocationManager.getInstance().getLocationAdCode();
        DataRepository.getInstance().getLauncherBanner(adCode);
    }


    private LocationManager.LocationListener mListener = new LocationManager.LocationListener() {

        @Override
        public void onLocationStart() {
            if (mView != null) {
                mView.setGPSLoadingStatus();
            }
        }

        @Override
        public void onLocationResult(LocationBean info) {
            switch (info.mStatus) {
                case 0:
                    String locationInfo = info.mProvince + info.mCity + info.mDistrict;
                    if (!TextUtils.isEmpty(locationInfo) && mView != null) {
                        mView.setLocationInfo(locationInfo);
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
