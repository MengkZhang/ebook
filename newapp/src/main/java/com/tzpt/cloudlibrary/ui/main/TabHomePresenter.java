package com.tzpt.cloudlibrary.ui.main;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.bean.BannerInfo;
import com.tzpt.cloudlibrary.bean.HomeDataBean;
import com.tzpt.cloudlibrary.bean.ModelMenu;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.remote.pojo.LibraryOpenTimeVo;
import com.tzpt.cloudlibrary.ui.map.LocationManager;
import com.tzpt.cloudlibrary.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 首页
 * Created by tonyjia on 2018/10/17.
 */
public class TabHomePresenter extends RxPresenter<TabHomeContract.View> implements
        TabHomeContract.Presenter {

    private boolean mHadDataCache = false;
    private boolean mHasBannerCache = false;

    @Override
    public void getHomeInfoList() {
        mView.showHomeDataProgress();
        mView.setDistrictText(LocationManager.getInstance().getLastArea());

        mHasBannerCache = false;
        mHadDataCache = false;
        getBannerList();
        getHomeModelList();
        getHomeDataList();
    }

    @Override
    public void getBannerList() {
        Subscription subscription = DataRepository.getInstance().getMainBannerList(LocationManager.getInstance().getLocationAdCode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread(), true)
                .subscribe(new Observer<List<BannerInfo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null && !mHasBannerCache) {
                            mView.setBannerErr();
                        }
                    }

                    @Override
                    public void onNext(List<BannerInfo> bannerInfos) {
                        if (mView != null) {
                            if (bannerInfos != null
                                    && bannerInfos.size() > 0) {
                                mHasBannerCache = true;
                                mView.setBannerList(bannerInfos);
                            } else {
                                if (!mHasBannerCache) {
                                    mView.setBannerErr();
                                }
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 获取首页模块列表
     */
    private void getHomeModelList() {
        List<ModelMenu> homeModelBeanList = new ArrayList<>();
        homeModelBeanList.add(new ModelMenu(0, "图书馆", R.drawable.btn_home_icon_lib, true));
        homeModelBeanList.add(new ModelMenu(7, "书店", R.drawable.btn_home_icon_book_store, true));
        homeModelBeanList.add(new ModelMenu(1, "图书", R.drawable.btn_home_icon_book, true));
        homeModelBeanList.add(new ModelMenu(2, "电子书", R.drawable.btn_home_icon_ebook, true));
        homeModelBeanList.add(new ModelMenu(5, "视频", R.drawable.btn_home_icon_video, true));
        homeModelBeanList.add(new ModelMenu(4, "活动", R.drawable.btn_home_icon_activity, true));
        homeModelBeanList.add(new ModelMenu(3, "资讯", R.drawable.btn_home_icon_news, true));
        homeModelBeanList.add(new ModelMenu(6, "排行榜", R.drawable.btn_home_icon_rank, true));
        mView.setHomeModelList(homeModelBeanList);
    }

    /**
     * 获取首页数据列表
     */
    private void getHomeDataList() {
        Subscription subscription = DataRepository.getInstance().getHomeInfo(LocationManager.getInstance().getLocationAdCode(),
                LocationManager.getInstance().getLngLat())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread(), true)
                .subscribe(new Observer<HomeDataBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            if (!mHadDataCache) {
                                mView.setHomeDataErr();
                            }
                        }
                    }

                    @Override
                    public void onNext(HomeDataBean homeDataBean) {
                        if (mView != null) {
                            if (homeDataBean != null) {
                                mHadDataCache = true;
                                setHomeData(homeDataBean);
                            } else {
                                if (!mHadDataCache) {
                                    mView.setHomeDataErr();
                                }
                            }
                        }
                    }
                });
        addSubscrebe(subscription);

    }

    /**
     * 设置首页数据
     *
     * @param dataBean 首页数据
     */
    private void setHomeData(HomeDataBean dataBean) {
        if (dataBean.libraryBeanList != null && !dataBean.libraryBeanList.isEmpty()) {
            mView.setNearLibraryList(dataBean.libraryBeanList);
        } else {
            mView.hideNearLibraryList();
        }
        if (dataBean.bookBeanList != null && !dataBean.bookBeanList.isEmpty()) {
            mView.setPaperBookList(dataBean.bookBeanList);
        } else {
            mView.hidePaperBookList();
        }
        if (dataBean.eBookBeanList != null && !dataBean.eBookBeanList.isEmpty()) {
            mView.setEBookList(dataBean.eBookBeanList);
        } else {
            mView.hideEBookList();
        }
        if (dataBean.videoSetBeanList != null && !dataBean.videoSetBeanList.isEmpty()) {
            mView.setVideoList(dataBean.videoSetBeanList);
        } else {
            mView.hideVideoList();
        }
        if (dataBean.activityBeanList != null && !dataBean.activityBeanList.isEmpty()) {
            mView.setActivityList(dataBean.activityBeanList);
        } else {
            mView.hideActivityList();
        }
        if (dataBean.informationBeanList != null && !dataBean.informationBeanList.isEmpty()) {
            mView.setInformationList(dataBean.informationBeanList);
        } else {
            mView.hideInformationList();
        }
        mView.showHomeData();
    }

}
