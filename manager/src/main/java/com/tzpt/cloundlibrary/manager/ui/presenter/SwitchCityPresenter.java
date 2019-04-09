package com.tzpt.cloundlibrary.manager.ui.presenter;

import android.text.TextUtils;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseResponseCode;
import com.tzpt.cloundlibrary.manager.base.RxPresenter;
import com.tzpt.cloundlibrary.manager.bean.LocationBean;
import com.tzpt.cloundlibrary.manager.bean.SwitchCityBean;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.SwitchCityVo;
import com.tzpt.cloundlibrary.manager.ui.contract.SwitchCityContract;
import com.tzpt.cloundlibrary.manager.utils.Utils;
import com.tzpt.cloundlibrary.manager.utils.location.LocationService;

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
        SwitchCityContract.Presenter,
        BaseResponseCode {

    @Override
    public void getCurrentLocation() {
        LocationService.getInstance().startLocation(Utils.getContext(), mAddressListener);
    }


    private LocationService.LocationAddressListener mAddressListener = new LocationService.LocationAddressListener() {
        @Override
        public void setLocationAddressSuccess(LocationBean bean) {
            if (null != mView) {
                mView.setLocationAddressSuccess(bean);
            }
        }

        @Override
        public void setLocationAddressFailure(int errorType) {
            if (null != mView) {
                switch (errorType) {
                    case 0: //定位失败
                        mView.setLocationFailed(R.string.positioning_failure);
                        break;
                    case 1://获取定位权限失败，打开定位权限界面
                        mView.showDialogForPermissions(R.string.positioning_prompt);
                        break;
                }
            }
        }
    };

    @Override
    public void getProvince() {
        //清除临时城市列表
        DataRepository.getInstance().clearTempCityListList();

        List<SwitchCityBean> tempProvinceList = DataRepository.getInstance().getTempCityList(1);
        if (null != tempProvinceList && tempProvinceList.size() > 0) {
            mView.setCityList(tempProvinceList, 0, "选择省");
            return;
        }
        Subscription subscription = DataRepository.getInstance().getProvinceList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SwitchCityVo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.setErrorMsg(R.string.network_fault);
                        }
                    }

                    @Override
                    public void onNext(SwitchCityVo switchCityVoBaseResultEntity) {
                        if (null != mView) {
                            if (switchCityVoBaseResultEntity.status == CODE_SUCCESS) {
                                if (null != switchCityVoBaseResultEntity.data
                                        && switchCityVoBaseResultEntity.data.list != null
                                        && switchCityVoBaseResultEntity.data.list.size() > 0) {
                                    List<SwitchCityBean> switchProvinceList = new ArrayList<SwitchCityBean>();
                                    for (SwitchCityVo.City item : switchCityVoBaseResultEntity.data.list) {
                                        SwitchCityBean bean = new SwitchCityBean();
                                        bean.mCode = item.code;
                                        bean.mName = item.name;
                                        switchProvinceList.add(bean);
                                    }
                                    mView.setCityList(switchProvinceList, 0, "选择省");
                                    //0 默认区域列表 1省列表 2城市列表
                                    DataRepository.getInstance().saveTempCityList(switchProvinceList, 1);
                                } else {
                                    mView.setCityListEmpty();
                                }
                            }
                        }
                    }
                });

        addSubscrebe(subscription);
    }

    @Override
    public void getCity(String provinceCode, final String provinceName) {
        if (TextUtils.isEmpty(provinceCode)) {
            return;
        }
        List<SwitchCityBean> tempCityList = DataRepository.getInstance().getTempCityList(2);
        if (null != tempCityList && tempCityList.size() > 0) {
            mView.setCityList(tempCityList, 1, provinceName);
            return;
        }
        Subscription subscription = DataRepository.getInstance().getCityList(provinceCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SwitchCityVo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.setErrorMsg(R.string.network_fault);
                        }
                    }

                    @Override
                    public void onNext(SwitchCityVo switchCityVoBaseResultEntity) {
                        if (null != mView) {
                            if (switchCityVoBaseResultEntity.status == CODE_SUCCESS) {
                                if (null != switchCityVoBaseResultEntity.data
                                        && switchCityVoBaseResultEntity.data.list != null
                                        && switchCityVoBaseResultEntity.data.list.size() > 0) {
                                    List<SwitchCityBean> switchCityList = new ArrayList<SwitchCityBean>();
                                    for (SwitchCityVo.City item : switchCityVoBaseResultEntity.data.list) {
                                        SwitchCityBean bean = new SwitchCityBean();
                                        bean.mCode = item.code;
                                        bean.mName = item.name;
                                        switchCityList.add(bean);
                                    }
                                    mView.setCityList(switchCityList, 1, provinceName);
                                    //0 默认区域列表 1省列表 2城市列表
                                    DataRepository.getInstance().saveTempCityList(switchCityList, 2);
                                } else {
                                    mView.setCityListEmpty();
                                }
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void getDistrict(final String city, final int type) {
        if (TextUtils.isEmpty(city)) {
            return;
        }
        if (type == 0) {
            List<SwitchCityBean> tempDistrictList = DataRepository.getInstance().getTempCityList(0);
            if (null != tempDistrictList && tempDistrictList.size() > 0) {
                mView.setCityList(tempDistrictList, 2, city);
                return;
            }
        }
        Subscription subscription = DataRepository.getInstance().getDistrictList(city)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SwitchCityVo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.setErrorMsg(R.string.network_fault);
                        }
                    }

                    @Override
                    public void onNext(SwitchCityVo switchCityVoBaseResultEntity) {
                        if (null != mView) {
                            if (switchCityVoBaseResultEntity.status == CODE_SUCCESS) {
                                if (null != switchCityVoBaseResultEntity.data
                                        && switchCityVoBaseResultEntity.data.list != null
                                        && switchCityVoBaseResultEntity.data.list.size() > 0) {
                                    List<SwitchCityBean> switchDistrictList = new ArrayList<SwitchCityBean>();
                                    for (SwitchCityVo.City item : switchCityVoBaseResultEntity.data.list) {
                                        SwitchCityBean bean = new SwitchCityBean();
                                        bean.mCode = item.code;
                                        bean.mName = item.name;
                                        bean.mParentCityName = city;
                                        switchDistrictList.add(bean);
                                    }
                                    //2 首页区域 3 切换城市区域
                                    mView.setCityList(switchDistrictList, type == 0 ? 2 : 3, city);
                                    if (type == 0) {//0 默认区域列表 1省列表 2城市列表
                                        DataRepository.getInstance().saveTempCityList(switchDistrictList, 0);
                                    }
                                } else {
                                    mView.setCityListEmpty();
                                }
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    //清除所有数据
    @Override
    public void clearTempCityData() {
        DataRepository.getInstance().clearTempDefaultDistinctList();
        DataRepository.getInstance().clearTempProvinceList();
        DataRepository.getInstance().clearTempCityListList();
    }


}
