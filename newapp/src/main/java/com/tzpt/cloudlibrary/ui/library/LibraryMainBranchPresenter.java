package com.tzpt.cloudlibrary.ui.library;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.bean.LibraryMainBranchBean;
import com.tzpt.cloudlibrary.business_bean.LibraryBean;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseResultEntityVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.MainBranchLibraryVo;
import com.tzpt.cloudlibrary.modle.remote.pojo.LibraryOpenTimeVo;
import com.tzpt.cloudlibrary.ui.map.LocationBean;
import com.tzpt.cloudlibrary.ui.map.LocationManager;
import com.tzpt.cloudlibrary.utils.DateUtils;
import com.tzpt.cloudlibrary.utils.ImageUrlUtils;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 总分馆
 */
public class LibraryMainBranchPresenter extends RxPresenter<LibraryMainBranchContract.View> implements
        LibraryMainBranchContract.Presenter {

    private String mLibCode;

    @Override
    public void setCurrentLibCode(String libCode) {
        this.mLibCode = libCode;
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
    public void getMainBranchLibraryList() {
        if (!TextUtils.isEmpty(mLibCode)) {
            Subscription subscription = DataRepository.getInstance().getAllLibrary(mLibCode, LocationManager.getInstance().getLngLat())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseResultEntityVo<MainBranchLibraryVo>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (null != mView) {
                                mView.setNetError();
                            }
                        }

                        @Override
                        public void onNext(BaseResultEntityVo<MainBranchLibraryVo> branchLibraryVo) {
                            if (null != mView) {
                                if (branchLibraryVo.status == 200) {
                                    List<LibraryMainBranchBean> branchBeanList = new ArrayList<>();
                                    //上级馆
                                    if (null != branchLibraryVo.data.supper) {
                                        branchBeanList.add(new LibraryMainBranchBean(0, "上级馆"));

                                        LibraryBean bean = new LibraryBean();
                                        bean.mLibrary.mId = branchLibraryVo.data.supper.libId;
                                        bean.mLibrary.mCode = branchLibraryVo.data.supper.libCode;
                                        bean.mLibrary.mName = branchLibraryVo.data.supper.libName;
                                        bean.mLibrary.mAddress = branchLibraryVo.data.supper.address;
                                        bean.mLibrary.mLngLat = branchLibraryVo.data.supper.lngLat;
                                        bean.mLibrary.mLogo = ImageUrlUtils.getDownloadOriginalImagePath(branchLibraryVo.data.supper.logo);
//                                        bean.mLibrary.mLighten = branchLibraryVo.data.supper.lighten;
                                        bean.mLibrary.mBookCount = branchLibraryVo.data.supper.bookNum;
                                        bean.mLibrary.mHeatCount = branchLibraryVo.data.supper.hotTip;
                                        bean.mIsOpen = libraryIsOpen(branchLibraryVo.data.supper.lightTime, branchLibraryVo.data.supper.serviceTime);
                                        bean.mDistance = branchLibraryVo.data.supper.distance;
                                        branchBeanList.add(new LibraryMainBranchBean(1, bean));
                                    }
                                    //一级分馆
                                    if (null != branchLibraryVo.data.oneLevel && branchLibraryVo.data.oneLevel.size() > 0) {
                                        branchBeanList.add(new LibraryMainBranchBean(0, "一级分馆"));
                                        for (MainBranchLibraryVo.LibraryVo item : branchLibraryVo.data.oneLevel) {
                                            LibraryBean bean = new LibraryBean();
                                            bean.mLibrary.mId = item.libId;
                                            bean.mLibrary.mAddress = item.address;
                                            bean.mLibrary.mName = item.libName;
                                            bean.mLibrary.mLngLat = item.lngLat;
                                            bean.mLibrary.mCode = item.libCode;
                                            bean.mLibrary.mLogo = ImageUrlUtils.getDownloadOriginalImagePath(item.logo);
//                                            bean.mLibrary.mLighten = item.lighten;
                                            bean.mLibrary.mBookCount = item.bookNum;
                                            bean.mLibrary.mHeatCount = item.hotTip;
                                            bean.mDistance = item.distance;
                                            bean.mIsOpen = libraryIsOpen(item.lightTime, item.serviceTime);
                                            branchBeanList.add(new LibraryMainBranchBean(1, bean));
                                        }
                                    }
                                    //二级分馆
                                    if (null != branchLibraryVo.data.twoLevel && branchLibraryVo.data.twoLevel.size() > 0) {
                                        branchBeanList.add(new LibraryMainBranchBean(0, "二级分馆"));
                                        for (MainBranchLibraryVo.LibraryVo item : branchLibraryVo.data.twoLevel) {
                                            LibraryBean bean = new LibraryBean();
                                            bean.mLibrary.mId = item.libId;
                                            bean.mLibrary.mAddress = item.address;
                                            bean.mLibrary.mName = item.libName;
                                            bean.mLibrary.mLngLat = item.lngLat;
                                            bean.mLibrary.mCode = item.libCode;
                                            bean.mLibrary.mLogo = ImageUrlUtils.getDownloadOriginalImagePath(item.logo);
//                                            bean.mLibrary.mLighten = item.lighten;
                                            bean.mLibrary.mBookCount = item.bookNum;
                                            bean.mLibrary.mHeatCount = item.hotTip;
                                            bean.mDistance = item.distance;
                                            bean.mIsOpen = libraryIsOpen(item.lightTime, item.serviceTime);
                                            branchBeanList.add(new LibraryMainBranchBean(1, bean));
                                        }
                                    }
                                    if (branchBeanList.size() > 0) {
                                        mView.setMainBranchLibraryList(branchBeanList, branchLibraryVo.data.count);
                                    } else {
                                        mView.setMainBranchLibraryEmpty();
                                    }
                                } else {
                                    mView.setNetError();
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }

    /**
     * 判断图书馆是否开放
     *
     * @param lightTime 开放时间
     * @param time      当前时间
     * @return 图书馆是否开放
     */
    private boolean libraryIsOpen(String lightTime, String time) {
        boolean isOpen = false;
        if (null != lightTime) {
            LibraryOpenTimeVo mLibraryOpenTimeVo = new Gson().fromJson(lightTime, LibraryOpenTimeVo.class);
            if (null != mLibraryOpenTimeVo) {
                LibraryOpenTimeVo.DayTime mDayTime = mLibraryOpenTimeVo.dayTime;
                LibraryOpenTimeVo.AM am = mDayTime.am;
                LibraryOpenTimeVo.PM pm = mDayTime.pm;
                if (null != time && !TextUtils.isEmpty(time)) {
                    boolean isAm = DateUtils.timeIsAM(time);
                    StringBuilder builder = new StringBuilder();
                    if (isAm) {
                        builder.append(am.begin).append("-").append(am.end);
                    } else {
                        builder.append(pm.begin).append("-").append(pm.end);
                    }
                    String sourceDate = builder.toString();
                    //判断系统当前时间是否在指定时间范围内
                    isOpen = DateUtils.isInTime(sourceDate, DateUtils.formatTime(time));
                } else {
                    isOpen = false;
                }
            } else {
                isOpen = false;
            }
        }
        return isOpen;
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
                        getMainBranchLibraryList();
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
