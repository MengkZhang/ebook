package com.tzpt.cloudlibrary.ui.main;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.tzpt.cloudlibrary.CloudLibraryApplication;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.bean.BannerInfo;
import com.tzpt.cloudlibrary.bean.LightLibraryOpenTimeInfo;
import com.tzpt.cloudlibrary.business_bean.LibraryBean;
import com.tzpt.cloudlibrary.modle.LibraryRepository;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;
import com.tzpt.cloudlibrary.rxbus.RxBus;
import com.tzpt.cloudlibrary.ui.map.LocationManager;

import org.json.JSONObject;

import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/8/28.
 */

public class AttentionLibPresenter extends RxPresenter<AttentionLibContract.View>
        implements AttentionLibContract.Presenter {
    private RxBus mRxBus;

    AttentionLibPresenter() {
        mRxBus = CloudLibraryApplication.mRxBus;
    }

    @Override
    public boolean isLogin() {
        return UserRepository.getInstance().isLogin();
    }

    @Override
    public void cancelAttentionLib() {
        if (TextUtils.isEmpty(UserRepository.getInstance().getLoginReaderId())) {
            return;
        }
        Subscription subscription = LibraryRepository.getInstance().cancelAttentionLib(LibraryRepository.getInstance().getAttentionLibCode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            if (e instanceof ApiException) {
                                if (((ApiException) e).getCode() == 30100) {
                                    mView.pleaseLoginTip();
                                } else {
                                    mView.showToastTip(R.string.network_fault);
                                }
                            } else {
                                mView.showToastTip(R.string.network_fault);
                            }
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            mView.showToastTip(R.string.cancel_attention_success);
                            mView.showNoAttentionLib();
                        } else {
                            mView.showToastTip(R.string.cancel_attention_failed);
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void getAttentionLibBanner() {
        String libCode = LibraryRepository.getInstance().getAttentionLibCode();
        if (TextUtils.isEmpty(libCode)) {
            return;
        }
        Subscription subscription = LibraryRepository.getInstance().getLibBannerNewsList(libCode,
                LocationManager.getInstance().getLocationAdCode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<BannerInfo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.showBannerInfoError();
                        }
                    }

                    @Override
                    public void onNext(List<BannerInfo> bannerInfos) {
                        if (mView != null) {
                            if (bannerInfos != null && bannerInfos.size() > 0) {
                                mView.showBannerInfoList(bannerInfos);
                            } else {
                                mView.showBannerInfoError();
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void getAttentionLib() {
        if (UserRepository.getInstance().isLogin()) {
            final String attentionLibCode = LibraryRepository.getInstance().getAttentionLibCode();
            if (TextUtils.isEmpty(attentionLibCode)) {
                mView.showNoAttentionLib();
            } else {
                String libName = LibraryRepository.getInstance().getAttentionLibName();
                mView.setTitle(libName);
                //获取缓存
                Subscription subscription = LibraryRepository.getInstance().getLocalLibraryInfo()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<LibraryBean>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(LibraryBean libraryBean) {
                                if (mView != null) {
                                    if (libraryBean != null) {
                                        //设置轮播列表
                                        List<BannerInfo> bannerInfoList = LibraryRepository.getInstance().getAttentionLibBannerList();
                                        if (bannerInfoList != null && bannerInfoList.size() > 0) {
                                            mView.showBannerInfoList(bannerInfoList);
                                        } else {
                                            mView.showBannerInfoError();
                                        }
                                        setAttentionLibInfo(libraryBean);
                                        mView.setContentView();
                                    }

                                    //获取关注馆信息
                                    getAttentionLibMenu(attentionLibCode, libraryBean == null);
                                }
                            }
                        });
                addSubscrebe(subscription);
            }
        } else {
            mView.showNoAttentionLib();
        }
    }

    private void getAttentionLibMenu(final String libCode, final boolean needLoading) {
        if (needLoading) {
            mView.showLibProgress();
        }
        getAttentionLibBanner();
        Subscription subscription = LibraryRepository.getInstance().getLibResourcesList(libCode, 0, -1)//不是搜索结果，flag设置-1,不会传给后台
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LibraryBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView && needLoading) {
                            mView.showLibModelError();
                        }
                    }

                    @Override
                    public void onNext(LibraryBean libraryBean) {
                        if (mView != null) {
                            saveLibLocalCache();
                            setAttentionLibInfo(libraryBean);
                            mView.setContentView();
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    //缓存图书馆资源到本地
    private void saveLibLocalCache() {
        Subscription subscription = LibraryRepository.getInstance().saveLibResources()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {

                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 设置关注馆信息
     *
     * @param libraryBean 关注馆
     */
    private void setAttentionLibInfo(LibraryBean libraryBean) {
        //设置模块列表
        mView.showLibModelList(libraryBean.mMenuItemList, libraryBean.mLibrary.mCode, libraryBean.mLibrary.mName);
        mView.showLibraryInfo(libraryBean);
        //设置书店或者图书馆介绍UI
        if (libraryBean.mIsBookStore) {
            mView.setAttentionIntroduceTitle("本店介绍");
            setOpenTime(libraryBean.mLibrary.mLightTime, true);
        } else {
            //设置图书馆今日开放时间
            mView.setAttentionIntroduceTitle("本馆介绍");
            setOpenTime(libraryBean.mLibrary.mLightTime, false);
        }
        //设置图书馆信息列表
        if (libraryBean.mBookBeanList != null && !libraryBean.mBookBeanList.isEmpty()) {
            mView.setPaperBookList(libraryBean.mBookBeanList, libraryBean.mLibrary.mBookCount);
        } else {
            mView.hidePaperBookList();
        }
        if (libraryBean.mEBookBeanList != null && !libraryBean.mEBookBeanList.isEmpty()) {
            mView.setEBookList(libraryBean.mEBookBeanList, libraryBean.mLibrary.mEBookCount);
        } else {
            mView.hideEBookList();
        }
        if (libraryBean.mVideoSetBeanList != null && !libraryBean.mVideoSetBeanList.isEmpty()) {
            mView.setVideoList(libraryBean.mVideoSetBeanList, libraryBean.mLibrary.mVideoSetCount);
        } else {
            mView.hideVideoList();
        }
        if (libraryBean.mActivityBeanList != null && !libraryBean.mActivityBeanList.isEmpty()) {
            mView.setActivityList(libraryBean.mActivityBeanList, libraryBean.mLibrary.mActivityCount);
        } else {
            mView.hideActivityList();
        }
        if (libraryBean.mInformationBeanList != null && !libraryBean.mInformationBeanList.isEmpty()) {
            mView.setInformationList(libraryBean.mInformationBeanList, libraryBean.mLibrary.mNewsCount);
        } else {
            mView.hideInformationList();
        }
    }

    /**
     * 设置营业时间
     */
    private void showBusinessTime(LightLibraryOpenTimeInfo.WeekTime weekTime, Integer[] weeks) {
        if (null == weeks || weeks.length == 0) {
            mView.showBusinessTime("未设置！");
            return;
        }
        if (null == weekTime) {
            mView.showBusinessTime("未设置！");
            return;
        }
        String startTime = "";
        String endTime = "";
        LightLibraryOpenTimeInfo.AM wAm = weekTime.am;
        if (null != wAm) {
            String begin = wAm.begin;
            String end = wAm.end;
            StringBuilder builder = new StringBuilder();
            startTime = ((TextUtils.isEmpty(begin) || TextUtils.isEmpty(end)) ? "09:00-12:00"
                    : builder.append(begin).append("-").append(end).toString());

        }
        LightLibraryOpenTimeInfo.PM wPm = weekTime.pm;
        if (null != wPm) {
            String begin = wPm.begin;
            String end = wPm.end;
            StringBuilder builder = new StringBuilder();
            endTime = ((TextUtils.isEmpty(begin) || TextUtils.isEmpty(end)) ? "14:00-17:00"
                    : builder.append(begin).append("-").append(end).toString());
        }
        mView.showBusinessTime(startTime + "\u0020" + endTime);
    }

    /**
     * 设置今日开放时间
     *
     * @param lightTime
     */
    private void setOpenTime(String lightTime, boolean isBookStore) {
        try {
            JSONObject jsonObject = new JSONObject(lightTime);
            LightLibraryOpenTimeInfo lightLibraryOpenTimeInfo = new Gson().fromJson(jsonObject.toString(), LightLibraryOpenTimeInfo.class);
            if (isBookStore) {
                //设置书店营业时间
                showBusinessTime(lightLibraryOpenTimeInfo.weekTime, lightLibraryOpenTimeInfo.week);
            } else {
                //图书馆今日开放时间
                setLibTodayOpenTime(lightLibraryOpenTimeInfo.dayTime, lightLibraryOpenTimeInfo.weekTime, lightLibraryOpenTimeInfo.week);
            }
        } catch (Exception e) {
            if (isBookStore) {
                mView.showBusinessTime("未设置！");
            } else {
                mView.showTodayOpenTime("未设置！");
            }
        }
    }

    /**
     * 设置今日开放时间
     *
     * @param dayTime
     * @param weekTime
     * @param week
     */
    private void setLibTodayOpenTime(LightLibraryOpenTimeInfo.DayTime dayTime, LightLibraryOpenTimeInfo.WeekTime weekTime, Integer[] week) {
        if (null == dayTime && weekTime == null) {
            mView.showTodayOpenTime("未设置！");
        } else if (null != dayTime) {
            String startTime = "";
            String endTime = "";
            LightLibraryOpenTimeInfo.AM tAm = dayTime.am;
            if (null != tAm) {
                String begin = tAm.begin;
                String end = tAm.end;
                StringBuilder builder = new StringBuilder();
                startTime = ((TextUtils.isEmpty(begin) || TextUtils.isEmpty(end)) ? "00:00-00:00"
                        : builder.append(begin).append("-").append(end).toString());

            }
            LightLibraryOpenTimeInfo.PM tPm = dayTime.pm;
            if (null != tPm) {
                String begin = tPm.begin;
                String end = tPm.end;
                StringBuilder builder = new StringBuilder();
                endTime = ((TextUtils.isEmpty(begin) || TextUtils.isEmpty(end)) ? "00:00-00:00"
                        : builder.append(begin).append("-").append(end).toString());
            }
            if (startTime.equals("00:00-00:00")
                    && endTime.equals("00:00-00:00")) {
                if (null == week || week.length == 0) {
                    mView.showTodayOpenTime("未设置！");
                } else {
                    mView.showTodayOpenTime("闭馆！");
                }
            } else {
                mView.showTodayOpenTime(startTime + "\u0020" + endTime);
            }
        } else {
            mView.showTodayOpenTime("闭馆！");
        }
    }

    @Override
    public <T> void registerRxBus(Class<T> eventType, Action1<T> action) {
        Subscription subscription = mRxBus.doSubscribe(eventType, action, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {

            }
        });
        mRxBus.addSubscription(this, subscription);
    }

    @Override
    public void unregisterRxBus() {
        mRxBus.unSubscribe(this);
    }

}
