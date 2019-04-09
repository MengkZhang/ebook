package com.tzpt.cloudlibrary.ui.information;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.bean.InformationBean;
import com.tzpt.cloudlibrary.business_bean.BaseResultData;
import com.tzpt.cloudlibrary.modle.InformationRepository;
import com.tzpt.cloudlibrary.ui.map.LocationManager;
import com.tzpt.cloudlibrary.ui.search.SearchManager;

import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 资讯
 * Created by ZhiqiangJia on 2017-08-17.
 */

public class InformationPresenter extends RxPresenter<InformationContract.View> implements
        InformationContract.Presenter {

    private String mKeyWord;
    private String mLibCode;
    private String mTitle;
    private String mSource;
    private String mCategoryId;
    private String mIndustryId;
    private int mFromSearch;

    public InformationPresenter(InformationContract.View view) {
        attachView(view);
        mView.setPresenter(this);
    }

    @Override
    public void setSearchParameter(String keyword, String libraryCode, String source, String title, String type, String industryId, int fromSearch) {
        mKeyWord = keyword;
        mLibCode = libraryCode;
        mSource = source;
        mCategoryId = type;
        mIndustryId = industryId;
        mTitle = title;
        mFromSearch = fromSearch;
    }

    @Override
    public void getInformationList(int pageNum) {
        if (TextUtils.isEmpty(mLibCode)) {
            getHomeInformationList(pageNum);
        } else {
            getLibInformationList(pageNum);
        }
    }

    private void getHomeInformationList(final int pageNum) {
        ArrayMap<String, String> mParameter = new ArrayMap<>();
        if (!TextUtils.isEmpty(mKeyWord)) {
            mParameter.put("keyword", mKeyWord);
        }
        if (!TextUtils.isEmpty(mTitle)) {
            mParameter.put("title", mTitle);
        }
        if (!TextUtils.isEmpty(mSource)) {
            mParameter.put("source", mSource);
        }
        if (!TextUtils.isEmpty(mCategoryId)) {
            mParameter.put("category", mCategoryId);
        }
        if (!TextUtils.isEmpty(mIndustryId)) {
            mParameter.put("industry", mIndustryId);
        }
        mParameter.put("pageNo", String.valueOf(pageNum));
        mParameter.put("pageCount", String.valueOf(20));

        mParameter.put("locationCode", LocationManager.getInstance().getLocationAdCode());

        Subscription subscription = InformationRepository.getInstance().getInformationList(mParameter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResultData<List<InformationBean>>>() {
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
                    public void onNext(BaseResultData<List<InformationBean>> listBaseResultData) {
                        if (mView != null) {
                            if (listBaseResultData != null) {
                                mView.setInformationList(listBaseResultData.resultList, listBaseResultData.mTotalCount, pageNum == 1);
                            } else {
                                mView.setInformationEmpty(pageNum == 1);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void getLibInformationList(final int pageNum) {
        ArrayMap<String, String> mParameter = new ArrayMap<>();
        if (!TextUtils.isEmpty(mKeyWord)) {
            mParameter.put("keyword", mKeyWord);
        }
        if (!TextUtils.isEmpty(mTitle)) {
            mParameter.put("title", mTitle);
        }
        if (!TextUtils.isEmpty(mSource)) {
            mParameter.put("source", mSource);
        }
        if (!TextUtils.isEmpty(mCategoryId)) {
            mParameter.put("category", mCategoryId);
        }
        if (!TextUtils.isEmpty(mIndustryId)) {
            mParameter.put("industry", mIndustryId);
        }
        mParameter.put("pageNo", String.valueOf(pageNum));
        mParameter.put("pageCount", String.valueOf(20));

        Subscription subscription = InformationRepository.getInstance().getLibInformationList(mLibCode, mParameter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResultData<List<InformationBean>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.setNetError(pageNum == 1);
                        }
                    }

                    @Override
                    public void onNext(BaseResultData<List<InformationBean>> listBaseResultData) {
                        if (mView != null) {
                            if (listBaseResultData != null) {
                                mView.setInformationList(listBaseResultData.resultList, listBaseResultData.mTotalCount, pageNum == 1);
                            } else {
                                mView.setInformationEmpty(pageNum == 1);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }


    @Override
    public void removeInformationList() {
        InformationRepository.getInstance().removeInformationList();
    }

    @Override
    public void saveInfoListCache(List<InformationBean> beanList) {
        InformationRepository.getInstance().addInformationList(beanList, true);
    }

    @Override
    public String getKeyword() {
        return mKeyWord;
    }

    @Override
    public String getLibCode() {
        return mLibCode;
    }

    @Override
    public String getSource() {
        return mSource;
    }

    @Override
    public String getCategoryId() {
        return mCategoryId;
    }

    @Override
    public String getSearchTitle() {
        return mTitle;
    }

    @Override
    public String getSearchIndustryId() {
        return mIndustryId;
    }

    @Override
    public int getFromSearch() {
        return mFromSearch;
    }

    @Override
    public void mustShowProgressLoading() {
        mView.showRefreshLoading();
    }

    @Override
    public void saveHistoryTag(String searchContent) {
        SearchManager.saveHistoryTag(3, searchContent);
    }
}
