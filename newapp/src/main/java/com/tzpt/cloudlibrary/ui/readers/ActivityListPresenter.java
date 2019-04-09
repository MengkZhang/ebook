package com.tzpt.cloudlibrary.ui.readers;

import android.text.TextUtils;

import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.bean.ActionInfoBean;
import com.tzpt.cloudlibrary.business_bean.BaseListResultData;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;
import com.tzpt.cloudlibrary.ui.map.LocationManager;
import com.tzpt.cloudlibrary.ui.search.SearchManager;

import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 活动列表
 * Created by tonyjia on 2018/3/21.
 */
public class ActivityListPresenter extends RxPresenter<ActivityListContract.View> implements
        ActivityListContract.Presenter {

    private String mLibCode;
    private String mKeyword;
    private boolean mIsFromSearch;
    private int mFromType;

    public ActivityListPresenter(ActivityListContract.View view) {
        attachView(view);
        mView.setPresenter(this);
    }

    @Override
    public int getFromSearchValue() {
        return mIsFromSearch ? 1 : 0;
    }

    @Override
    public String getKeyWord() {
        return mKeyword;
    }

    @Override
    public String getLibCode() {
        return mLibCode;
    }

    @Override
    public int getFromType() {
        return mFromType;
    }

    @Override
    public void setParameters(int fromType, boolean isFromSearch, String keyword, String libCode) {
        this.mFromType = fromType;
        this.mKeyword = keyword;
        this.mIsFromSearch = isFromSearch;
        this.mLibCode = libCode;
    }

    @Override
    public void getActivityList(int pageNum) {
        switch (mFromType) {
            case 0://活动列表或搜索
                if (mIsFromSearch) {
                    getActionList(pageNum, mKeyword);
                } else {
                    getActionList(pageNum, null);
                }
                break;
            case 1://我的报名
                getAppliedActionList(pageNum);
                break;
            case 2://本馆列表
                if (mIsFromSearch) {
                    getLibActionList(pageNum, mKeyword, mLibCode);
                } else {
                    getLibActionList(pageNum, null, mLibCode);
                }
                break;
        }
    }

    private void getAppliedActionList(final int pageNum) {
        String idCard = UserRepository.getInstance().getLoginUserIdCard();
        if (TextUtils.isEmpty(idCard)) {
            return;
        }
        Subscription subscription = DataRepository.getInstance().getAppliedActionList(idCard, pageNum, 20)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseListResultData<ActionInfoBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            if (e instanceof ApiException) {
                                if (((ApiException) e).getCode() == 30100) {
                                    mView.pleaseLogin();
                                } else {
                                    mView.setNetError(pageNum == 1);
                                }
                            } else {
                                mView.setNetError(pageNum == 1);
                            }
                        }
                    }

                    @Override
                    public void onNext(BaseListResultData<ActionInfoBean> actionInfoBeanBaseListResultData) {
                        if (mView != null) {
                            if (actionInfoBeanBaseListResultData != null
                                    && actionInfoBeanBaseListResultData.mResultList != null
                                    && actionInfoBeanBaseListResultData.mResultList.size() > 0) {
                                mView.setOurReadersList(actionInfoBeanBaseListResultData.mResultList,
                                        actionInfoBeanBaseListResultData.mTotalCount,
                                        pageNum == 1,
                                        mIsFromSearch);
                            } else {
                                mView.setOurReadersEmpty(pageNum == 1);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void saveLocalActionList(List<ActionInfoBean> list) {
        DataRepository.getInstance().saveActionList(list, true);
    }

    @Override
    public void clearLocalActionList() {
        DataRepository.getInstance().removeActionList();
    }

    @Override
    public void mustShowProgressLoading() {
        mView.showRefreshLoading();
    }

    @Override
    public void saveHistoryTag(String searchContent) {
        SearchManager.saveHistoryTag(4, searchContent);
    }

    /**
     * 平台活动
     *
     * @param pageNum 页码
     * @param keyWord 关键词
     */
    private void getActionList(final int pageNum, String keyWord) {
        Subscription subscription = DataRepository.getInstance().getOurReadersList(pageNum, 20, keyWord,
                LocationManager.getInstance().getLocationAdCode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseListResultData<ActionInfoBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.setNetError(pageNum == 1);
                        }
                    }

                    @Override
                    public void onNext(BaseListResultData<ActionInfoBean> actionInfoBeanBaseListResultData) {
                        if (mView != null) {
                            if (actionInfoBeanBaseListResultData != null
                                    && actionInfoBeanBaseListResultData.mResultList != null
                                    && actionInfoBeanBaseListResultData.mResultList.size() > 0) {
                                mView.setOurReadersList(actionInfoBeanBaseListResultData.mResultList,
                                        actionInfoBeanBaseListResultData.mTotalCount,
                                        pageNum == 1,
                                        mIsFromSearch);
                            } else {
                                mView.setOurReadersEmpty(pageNum == 1);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 获取本馆活动列表
     *
     * @param pageNum 页码
     * @param keyword 关键词
     * @param libCode 馆号
     */
    private void getLibActionList(final int pageNum, String keyword, String libCode) {
        Subscription subscription = DataRepository.getInstance().getLibActionList(libCode, pageNum, 20, keyword)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseListResultData<ActionInfoBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.setNetError(pageNum == 1);
                        }
                    }

                    @Override
                    public void onNext(BaseListResultData<ActionInfoBean> actionInfoBeanBaseListResultData) {
                        if (mView != null) {
                            if (actionInfoBeanBaseListResultData != null
                                    && actionInfoBeanBaseListResultData.mResultList != null
                                    && actionInfoBeanBaseListResultData.mResultList.size() > 0) {
                                mView.setOurReadersList(actionInfoBeanBaseListResultData.mResultList,
                                        actionInfoBeanBaseListResultData.mTotalCount,
                                        pageNum == 1,
                                        mIsFromSearch);
                            } else {
                                mView.setOurReadersEmpty(pageNum == 1);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }
}
