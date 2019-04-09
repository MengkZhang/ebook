package com.tzpt.cloudlibrary.ui.library;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.bean.BannerInfo;
import com.tzpt.cloudlibrary.bean.LightLibraryOpenTimeInfo;
import com.tzpt.cloudlibrary.business_bean.LibraryBean;
import com.tzpt.cloudlibrary.modle.LibraryRepository;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;
import com.tzpt.cloudlibrary.ui.map.LocationManager;

import org.json.JSONObject;

import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 图书馆详情
 */
public class LibraryDetailPresenter extends RxPresenter<LibraryDetailContract.View> implements
        LibraryDetailContract.Presenter {

    private String mLibraryCode;
    private String mLibraryName;
    private int mFromSearch;
    private LibraryBean mAttentionLibBean;

    public LibraryDetailPresenter(LibraryDetailContract.View view) {
        attachView(view);
        mView.setPresenter(this);
    }

    @Override
    public void setLibraryName(String libName) {
        mLibraryName = libName;
    }

    @Override
    public void setLibraryCode(String libraryCode) {
        this.mLibraryCode = libraryCode;
    }

    @Override
    public void setFromSearch(int fromSearch) {
        this.mFromSearch = fromSearch;
    }

    @Override
    public int getFromSearch() {
        return mFromSearch;
    }

    @Override
    public String getLibraryCode() {
        return mLibraryCode;
    }

    /**
     * 图书馆资讯列表
     */
    @Override
    public void getLibNewsList() {
        Subscription subscription = LibraryRepository.getInstance().getLibBannerNewsList(mLibraryCode,
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

    /**
     * 图书馆模块
     */
    @Override
    public void getLibMenuList() {
        if (!TextUtils.isEmpty(mLibraryCode)) {
            mView.showLibProgress();
            getLibNewsList();

            Subscription subscription = LibraryRepository.getInstance().getLibResourcesList(mLibraryCode, mFromSearch, 1)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<LibraryBean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (null != mView) {
                                mView.showLibModelError();
                            }
                        }

                        @Override
                        public void onNext(LibraryBean libraryBean) {
                            if (mView != null) {
                                mAttentionLibBean = libraryBean;
                                //设置模块列表
                                mView.showLibModelList(libraryBean.mMenuItemList);
                                checkAttentionStatus();
                                mView.showLibraryInfo(libraryBean);
                                //设置今日开放时间
                                setTodayOpenTime(libraryBean.mLibrary.mLightTime);

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
                                mView.setContentView();
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }

    /**
     * 设置今日开放时间
     *
     * @param lightTime
     */
    private void setTodayOpenTime(String lightTime) {
        try {
            JSONObject jsonObject = new JSONObject(lightTime);
            LightLibraryOpenTimeInfo lightLibraryOpenTimeInfo = new Gson().fromJson(jsonObject.toString(), LightLibraryOpenTimeInfo.class);
            if (null == lightLibraryOpenTimeInfo.dayTime && lightLibraryOpenTimeInfo.weekTime == null) {
                mView.showTodayOpenTime("未设置！");
            } else if (null != lightLibraryOpenTimeInfo.dayTime) {
                String startTime = "";
                String endTime = "";
                LightLibraryOpenTimeInfo.AM tAm = lightLibraryOpenTimeInfo.dayTime.am;
                if (null != tAm) {
                    String begin = tAm.begin;
                    String end = tAm.end;
                    StringBuilder builder = new StringBuilder();
                    startTime = ((TextUtils.isEmpty(begin) || TextUtils.isEmpty(end)) ? "00:00-00:00"
                            : builder.append(begin).append("-").append(end).toString());

                }
                LightLibraryOpenTimeInfo.PM tPm = lightLibraryOpenTimeInfo.dayTime.pm;
                if (null != tPm) {
                    String begin = tPm.begin;
                    String end = tPm.end;
                    StringBuilder builder = new StringBuilder();
                    endTime = ((TextUtils.isEmpty(begin) || TextUtils.isEmpty(end)) ? "00:00-00:00"
                            : builder.append(begin).append("-").append(end).toString());
                }
                if (startTime.equals("00:00-00:00")
                        && endTime.equals("00:00-00:00")) {
                    if (null == lightLibraryOpenTimeInfo.week || lightLibraryOpenTimeInfo.week.length == 0) {
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

        } catch (Exception e) {
            mView.showTodayOpenTime("未设置！");
        }
    }

    @Override
    public void checkAttentionStatus() {
        String attentionLibCode = LibraryRepository.getInstance().getAttentionLibCode();
        if (!TextUtils.isEmpty(attentionLibCode) && attentionLibCode.equals(mLibraryCode)) {
            mView.changeAttentionStatus(true);
        } else {
            mView.changeAttentionStatus(false);
        }
    }

    @Override
    public void dealAttentionMenuItem() {
        if (UserRepository.getInstance().isLogin()) {
            String attentionLibCode = LibraryRepository.getInstance().getAttentionLibCode();
            if (TextUtils.isEmpty(attentionLibCode)) {
                attentionLib();
            } else {
                if (attentionLibCode.equals(mLibraryCode)) {
                    mView.showCancelAttentionTip();
                } else {
                    Subscription subscription = LibraryRepository.getInstance().isAttentionBookStore()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<Boolean>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    if (mView != null) {
                                        mView.showChangeAttentionTip("图书馆");
                                    }
                                }

                                @Override
                                public void onNext(Boolean aBoolean) {
                                    if (mView != null) {
                                        mView.showChangeAttentionTip(aBoolean ? "书店" : "图书馆");
                                    }
                                }
                            });
                    addSubscrebe(subscription);
                }
            }
        } else {
            mView.needLogin();
        }
    }

    @Override
    public void attentionLib() {
        Subscription subscription = LibraryRepository.getInstance().attentionLib(mLibraryCode, mLibraryName)
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
                        if (mView != null) {
                            if (aBoolean) {
                                mView.showToastTip(R.string.attention_success);
                                mView.changeAttentionStatus(true);
                            } else {
                                mView.showToastTip(R.string.attention_failed);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void cancelAttentionLib() {
        Subscription subscription = LibraryRepository.getInstance().cancelAttentionLib(mLibraryCode)
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
                        if (mView != null) {
                            if (aBoolean) {
                                mView.showToastTip(R.string.cancel_attention_success);
                                mView.changeAttentionStatus(false);
                            } else {
                                mView.showToastTip(R.string.cancel_attention_failed);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void clearCacheData() {
        LibraryRepository.getInstance().clearCacheData();
    }


}
