package com.tzpt.cloudlibrary.ui.bookstore;

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
public class BookStoreDetailPresenter extends RxPresenter<BookStoreDetailContract.View> implements
        BookStoreDetailContract.Presenter {

    private String mStoreCode;
    private String mStoreName;
    private int mFromSearch;

    public BookStoreDetailPresenter(BookStoreDetailContract.View view) {
        attachView(view);
        mView.setPresenter(this);
    }

    @Override
    public void setStoreName(String storeName) {
        mStoreName = storeName;
    }

    @Override
    public void setStoreCode(String storeCode) {
        this.mStoreCode = storeCode;
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
    public String getStoreCode() {
        return mStoreCode;
    }

    /**
     * 书店banner列表
     */
    @Override
    public void getStoreNewsList() {
        Subscription subscription = LibraryRepository.getInstance().getLibBannerNewsList(mStoreCode,
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
     * 书店模块
     */
    @Override
    public void getStoreMenuList() {
        if (!TextUtils.isEmpty(mStoreCode)) {
            mView.showLibProgress();
            getStoreNewsList();

            Subscription subscription = LibraryRepository.getInstance().getLibResourcesList(mStoreCode, mFromSearch, 2)
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
                        public void onNext(LibraryBean bookStoreBean) {
                            if (mView != null) {
                                //设置模块列表
                                mView.showLibModelList(bookStoreBean.mMenuItemList);
                                checkAttentionStatus();
                                mView.setBookStoreInfo(bookStoreBean);
                                //设置营业时间
                                setTodayOpenTime(bookStoreBean.mLibrary.mLightTime);
                                //设置图书馆信息列表
                                if (bookStoreBean.mBookBeanList != null && !bookStoreBean.mBookBeanList.isEmpty()) {
                                    mView.setPaperBookList(bookStoreBean.mBookBeanList, bookStoreBean.mLibrary.mBookCount);
                                } else {
                                    mView.hidePaperBookList();
                                }
                                if (bookStoreBean.mEBookBeanList != null && !bookStoreBean.mEBookBeanList.isEmpty()) {
                                    mView.setEBookList(bookStoreBean.mEBookBeanList, bookStoreBean.mLibrary.mEBookCount);
                                } else {
                                    mView.hideEBookList();
                                }
                                if (bookStoreBean.mVideoSetBeanList != null && !bookStoreBean.mVideoSetBeanList.isEmpty()) {
                                    mView.setVideoList(bookStoreBean.mVideoSetBeanList, bookStoreBean.mLibrary.mVideoSetCount);
                                } else {
                                    mView.hideVideoList();
                                }
                                if (bookStoreBean.mActivityBeanList != null && !bookStoreBean.mActivityBeanList.isEmpty()) {
                                    mView.setActivityList(bookStoreBean.mActivityBeanList, bookStoreBean.mLibrary.mActivityCount);
                                } else {
                                    mView.hideActivityList();
                                }
                                if (bookStoreBean.mInformationBeanList != null && !bookStoreBean.mInformationBeanList.isEmpty()) {
                                    mView.setInformationList(bookStoreBean.mInformationBeanList, bookStoreBean.mLibrary.mNewsCount);
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
     * 设置营业时间
     */
    private void setLongDayInfo(LightLibraryOpenTimeInfo.WeekTime weekTime, Integer[] weeks) {
        if (null == weeks || weeks.length == 0) {
            mView.showTodayOpenTime("未设置！");
            return;
        }
        if (null == weekTime) {
            mView.showTodayOpenTime("未设置！");
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
        mView.showTodayOpenTime(startTime + "\u0020" + endTime);
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
            if (lightLibraryOpenTimeInfo != null) {
                setLongDayInfo(lightLibraryOpenTimeInfo.weekTime, lightLibraryOpenTimeInfo.week);
            } else {
                mView.showTodayOpenTime("未设置！");
            }
        } catch (Exception e) {
            mView.showTodayOpenTime("未设置！");
        }
    }

    @Override
    public void checkAttentionStatus() {
        String attentionLibCode = LibraryRepository.getInstance().getAttentionLibCode();
        if (!TextUtils.isEmpty(attentionLibCode) && attentionLibCode.equals(mStoreCode)) {
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
                if (attentionLibCode.equals(mStoreCode)) {
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
        Subscription subscription = LibraryRepository.getInstance().attentionLib(mStoreCode, mStoreName)
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
        Subscription subscription = LibraryRepository.getInstance().cancelAttentionLib(mStoreCode)
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
