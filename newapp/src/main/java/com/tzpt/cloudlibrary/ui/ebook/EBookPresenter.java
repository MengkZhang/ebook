package com.tzpt.cloudlibrary.ui.ebook;

import android.text.TextUtils;

import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.business_bean.EBookBean;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseResultEntityVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.EBookListItemVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.EBookListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.EBookRecommendationsListVo;
import com.tzpt.cloudlibrary.ui.search.SearchManager;
import com.tzpt.cloudlibrary.utils.DateUtils;
import com.tzpt.cloudlibrary.utils.HtmlFormatUtil;
import com.tzpt.cloudlibrary.utils.ImageUrlUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 电子书
 * Created by ZhiqiangJia on 2017-08-08.
 */
public class EBookPresenter extends RxPresenter<EBookContract.View> implements
        EBookContract.Presenter {

    private Subscription mRecommendationsSubscription;
    private Subscription mNewEBookSubscription;
    private Subscription mHotEBookSubscription;

    private Map<String, String> mParameter;
    private String mLibraryCode;                    //馆号
    private boolean mIsSearchResult;                //是否搜索结果
    private int mFilterType = 0;                    //电子书类型
    private int mParentClassifyId = 0;               //一级分类ID
    private int mChildClassifyId = 0;                //二级分类ID

    public EBookPresenter(EBookContract.View view) {
        attachView(view);
        mView.setPresenter(this);
    }

    @Override
    public void setFilterType(int type) {
        this.mFilterType = type;
        switch (type) {
            case EBookFilterType.Month_Rank_EBook_List:      //排行榜电子书
            case EBookFilterType.Quart_Rank_EBook_List:      //排行榜电子书
            case EBookFilterType.Year_Rank_EBook_List:       //排行榜电子书
                this.mIsSearchResult = false;
                break;
            case EBookFilterType.Library_Advanced_EBook_List://馆内高级搜索电子书
            case EBookFilterType.Advanced_EBook_List:        //高级搜索电子书
                this.mIsSearchResult = true;
                break;
            case EBookFilterType.Library_Search_EBook_List: //馆内搜索电子书
            case EBookFilterType.Search_EBook_List:         //搜索电子书
                this.mIsSearchResult = true;
                break;
            case EBookFilterType.One_Week_Hot:              //一周热门
            case EBookFilterType.New_EBook_List:            //最新上架
                this.mIsSearchResult = false;
                break;
            case EBookFilterType.Recommendations_EBook_List://好书推荐
                this.mIsSearchResult = false;
                break;
        }
    }

    @Override
    public int getFilterType() {
        return this.mFilterType;
    }

    @Override
    public void setParameter(Map<String, String> parameters) {
        if (mParameter == null) {
            mParameter = new HashMap<>();
        }
        mParameter.clear();
        mParameter.putAll(parameters);
        if (mParameter.containsKey("libCode")) {
            mLibraryCode = mParameter.get("libCode");
            mParameter.remove("libCode");
        }
    }

    @Override
    public void setEBookClassificationId(int parentClassifyId, int childClassifyId) {
        this.mParentClassifyId = parentClassifyId;
        this.mChildClassifyId = childClassifyId;
    }

    @Override
    public String getLibraryCode() {
        return mLibraryCode;
    }


    @Override
    public boolean isSearchResultList() {
        return mIsSearchResult;
    }

    @Override
    public void mustShowProgressLoading() {
        if (null != mView) {
            mView.showRefreshLoading();
        }
    }

    @Override
    public void saveHistoryTag(String searchContent) {
        SearchManager.saveHistoryTag(1, searchContent);
    }

    @Override
    public boolean isRankEBookList() {
        return mFilterType == EBookFilterType.Month_Rank_EBook_List
                || mFilterType == EBookFilterType.Quart_Rank_EBook_List
                || mFilterType == EBookFilterType.Year_Rank_EBook_List;
    }

    /**
     * 是否好书推荐
     *
     * @return
     */
    @Override
    public boolean isRecommendEBook() {
        return mFilterType == EBookFilterType.Recommendations_EBook_List;
    }

    @Override
    public void getEBookList(int pageNum) {
        if (null == mParameter) {
            mParameter = new HashMap<>();
        }
        mParameter.put("pageNo", String.valueOf(pageNum));
        mParameter.put("pageCount", String.valueOf(20));
        switch (mFilterType) {
            case EBookFilterType.Recommendations_EBook_List://好书推荐
                //一级目录id
                if (mParentClassifyId > 0) {
                    mParameter.put("oneLevelCategoryId", String.valueOf(mParentClassifyId));
                } else {
                    mParameter.remove("oneLevelCategoryId");
                }
                //二级目录id
                if (mChildClassifyId > 0) {
                    mParameter.put("twoLevelCategoryId", String.valueOf(mChildClassifyId));
                } else {
                    mParameter.remove("twoLevelCategoryId");
                }
                if (!TextUtils.isEmpty(mLibraryCode)) {
                    mParameter.put("libCode", mLibraryCode);
                }
                getRecommendationEBookList(pageNum);
                break;
            //首页电子书
            case EBookFilterType.One_Week_Hot:          //一周热门
                //一级目录id
                if (mParentClassifyId > 0) {
                    mParameter.put("oneLevelCategoryId", String.valueOf(mParentClassifyId));
                } else {
                    mParameter.remove("oneLevelCategoryId");
                }
                //二级目录id
                if (mChildClassifyId > 0) {
                    mParameter.put("twoLevelCategoryId", String.valueOf(mChildClassifyId));
                } else {
                    mParameter.remove("twoLevelCategoryId");
                }
                if (!TextUtils.isEmpty(mLibraryCode)) {
                    mParameter.put("libCode", mLibraryCode);
                }
                getHotEBookList(pageNum);
                break;
            case EBookFilterType.New_EBook_List:        //最新上架
            case EBookFilterType.Library_EBook_List:
                //一级目录id
                if (mParentClassifyId > 0) {
                    mParameter.put("oneLevelCategoryId", String.valueOf(mParentClassifyId));
                } else {
                    mParameter.remove("oneLevelCategoryId");
                }
                //二级目录id
                if (mChildClassifyId > 0) {
                    mParameter.put("twoLevelCategoryId", String.valueOf(mChildClassifyId));
                } else {
                    mParameter.remove("twoLevelCategoryId");
                }
                if (!TextUtils.isEmpty(mLibraryCode)) {
                    mParameter.put("libCode", mLibraryCode);
                }
                getNewEBookList(pageNum);
                break;
            //馆内电子书列表（包含馆内电子书列表，馆内电子书搜索，馆内电子书高级搜索）
            //case EBookFilterType.Library_EBook_List:    //图书馆内电子书
            case EBookFilterType.Library_Search_EBook_List://图书馆内搜索电子书
            case EBookFilterType.Library_Advanced_EBook_List://图书馆内高级搜索
                //一级目录id
                if (mParentClassifyId > 0) {
                    mParameter.put("oneLevelCategoryId", String.valueOf(mParentClassifyId));
                } else {
                    mParameter.remove("oneLevelCategoryId");
                }
                //二级目录id
                if (mChildClassifyId > 0) {
                    mParameter.put("twoLevelCategoryId", String.valueOf(mChildClassifyId));
                } else {
                    mParameter.remove("twoLevelCategoryId");
                }
                getLibraryEBooksList(pageNum);
                break;
            //搜索（包含电子书列表，全局搜索，全局高级搜索）
            case EBookFilterType.Advanced_EBook_List:   //高级搜索电子书
            case EBookFilterType.Search_EBook_List:     //搜索电子书
                getElectronicBooksList(pageNum);
                break;
            //排行榜
            case EBookFilterType.Month_Rank_EBook_List: //月榜
                getRankEBookList(pageNum, 1);
                break;
            case EBookFilterType.Quart_Rank_EBook_List: //季榜
                getRankEBookList(pageNum, 2);
                break;
            case EBookFilterType.Year_Rank_EBook_List:  //年榜
                getRankEBookList(pageNum, 3);
                break;
        }
    }

    /**
     * 好书推荐
     *
     * @param pageNo 页码
     */
    private void getRecommendationEBookList(final int pageNo) {
        if (null != mRecommendationsSubscription && !mRecommendationsSubscription.isUnsubscribed()) {
            mRecommendationsSubscription.unsubscribe();
        }
        mRecommendationsSubscription = DataRepository.getInstance().getRecommendationsEBookList(mParameter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResultEntityVo<EBookRecommendationsListVo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.setEBookListError(pageNo == 1);
                        }
                    }

                    @Override
                    public void onNext(BaseResultEntityVo<EBookRecommendationsListVo> eBookListVoBaseResultEntityVo) {
                        if (mView != null) {
                            if (eBookListVoBaseResultEntityVo.status == 200) {
                                if (eBookListVoBaseResultEntityVo.data.resultList != null
                                        && eBookListVoBaseResultEntityVo.data.resultList.size() > 0) {
                                    List<EBookBean> eBookInfoBeanList = new ArrayList<>();
                                    for (EBookRecommendationsListVo.EBookRecommendationsVo eBookVo : eBookListVoBaseResultEntityVo.data.resultList) {
                                        EBookBean bookInfoBean = new EBookBean();
                                        bookInfoBean.mEBook.mId = eBookVo.id;
                                        bookInfoBean.mEBook.mName = eBookVo.bookName;
                                        bookInfoBean.mEBook.mCoverImg = ImageUrlUtils.getDownloadOriginalImagePath(eBookVo.image);
                                        bookInfoBean.mAuthor.mName = eBookVo.author;
                                        bookInfoBean.mRecommendReason = eBookVo.recommendReason;
                                        bookInfoBean.mReadCount = eBookVo.number;

                                        eBookInfoBeanList.add(bookInfoBean);
                                    }
                                    mView.setEBookList(eBookInfoBeanList, eBookListVoBaseResultEntityVo.data.totalCount, eBookListVoBaseResultEntityVo.data.limitTotalCount, pageNo == 1);
                                } else {
                                    mView.setEBookListEmpty(pageNo == 1);
                                }
                            } else {
                                mView.setEBookListError(pageNo == 1);
                            }
                        }
                    }
                });
        addSubscrebe(mRecommendationsSubscription);
    }

    /**
     * 馆内图书列表
     *
     * @param pageNum 页码
     */
    private void getLibraryEBooksList(final int pageNum) {
        if (null != mLibraryCode) {
            Subscription subscription = DataRepository.getInstance().getLibraryEBookList(mLibraryCode, mParameter)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseResultEntityVo<com.tzpt.cloudlibrary.modle.remote.newpojo.EBookListVo>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (null != mView) {
                                mView.setEBookListError(pageNum == 1);
                            }
                        }

                        @Override
                        public void onNext(BaseResultEntityVo<com.tzpt.cloudlibrary.modle.remote.newpojo.EBookListVo> eBookListVoBaseResultEntityVo) {
                            if (mView != null) {
                                if (eBookListVoBaseResultEntityVo.status == 200) {
                                    if (eBookListVoBaseResultEntityVo.data.resultList != null
                                            && eBookListVoBaseResultEntityVo.data.resultList.size() > 0) {
                                        List<EBookBean> eBookInfoBeanList = new ArrayList<>();
                                        for (EBookListItemVo eBookVo : eBookListVoBaseResultEntityVo.data.resultList) {
                                            EBookBean bookInfoBean = new EBookBean();
                                            bookInfoBean.mEBook.mId = eBookVo.id;
                                            bookInfoBean.mEBook.mName = eBookVo.bookName;
                                            bookInfoBean.mEBook.mFileDownloadPath = eBookVo.file;
                                            bookInfoBean.mEBook.mCoverImg = ImageUrlUtils.getDownloadOriginalImagePath(eBookVo.image);
                                            bookInfoBean.mEBook.mPublishDate = eBookVo.publishDate;
                                            bookInfoBean.mEBook.mSummary = HtmlFormatUtil.delHTMLTag(eBookVo.summary);
                                            bookInfoBean.mEBook.mIsbn = eBookVo.isbn;

                                            bookInfoBean.mAuthor.mName = eBookVo.author;
                                            bookInfoBean.mCategory.mName = eBookVo.categoryName;
                                            bookInfoBean.mPress.mName = eBookVo.publisher;
                                            bookInfoBean.mReadCount = eBookVo.number;
                                            bookInfoBean.mHasNewEBookFlag = DateUtils.isWithinTimeLimit30Days(eBookVo.shelvesTime);
                                            eBookInfoBeanList.add(bookInfoBean);
                                        }
                                        mView.setEBookList(eBookInfoBeanList, eBookListVoBaseResultEntityVo.data.totalCount, 0, pageNum == 1);
                                    } else {
                                        mView.setEBookListEmpty(pageNum == 1);
                                    }
                                } else {
                                    mView.setEBookListError(pageNum == 1);
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }

    /**
     * 一周热门
     *
     * @param pageNum 页码
     */
    private void getHotEBookList(final int pageNum) {
        if (null != mHotEBookSubscription && !mHotEBookSubscription.isUnsubscribed()) {
            mHotEBookSubscription.unsubscribe();
        }
        mHotEBookSubscription = DataRepository.getInstance().getHotEBookList(mParameter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResultEntityVo<com.tzpt.cloudlibrary.modle.remote.newpojo.EBookListVo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.setEBookListError(pageNum == 1);
                        }
                    }

                    @Override
                    public void onNext(BaseResultEntityVo<com.tzpt.cloudlibrary.modle.remote.newpojo.EBookListVo> eBookListVoBaseResultEntityVo) {
                        if (mView != null) {
                            if (eBookListVoBaseResultEntityVo.status == 200) {
                                if (eBookListVoBaseResultEntityVo.data.resultList != null
                                        && eBookListVoBaseResultEntityVo.data.resultList.size() > 0) {
                                    List<EBookBean> eBookInfoBeanList = new ArrayList<>();
                                    for (EBookListItemVo eBookVo : eBookListVoBaseResultEntityVo.data.resultList) {
                                        EBookBean bookInfoBean = new EBookBean();
                                        bookInfoBean.mEBook.mId = eBookVo.id;
                                        bookInfoBean.mEBook.mName = eBookVo.bookName;
                                        bookInfoBean.mEBook.mFileDownloadPath = eBookVo.file;
                                        bookInfoBean.mEBook.mCoverImg = ImageUrlUtils.getDownloadOriginalImagePath(eBookVo.image);
                                        bookInfoBean.mEBook.mPublishDate = eBookVo.publishDate;
                                        bookInfoBean.mEBook.mSummary = HtmlFormatUtil.delHTMLTag(eBookVo.summary);
                                        bookInfoBean.mEBook.mIsbn = eBookVo.isbn;

                                        bookInfoBean.mAuthor.mName = eBookVo.author;
                                        bookInfoBean.mCategory.mName = eBookVo.categoryName;
                                        bookInfoBean.mPress.mName = eBookVo.publisher;
                                        bookInfoBean.mReadCount = eBookVo.number;
                                        eBookInfoBeanList.add(bookInfoBean);
                                    }
                                    mView.setEBookList(eBookInfoBeanList, eBookListVoBaseResultEntityVo.data.totalCount, eBookListVoBaseResultEntityVo.data.limitTotalCount, pageNum == 1);
                                } else {
                                    mView.setEBookListEmpty(pageNum == 1);
                                }
                            } else {
                                mView.setEBookListError(pageNum == 1);
                            }
                        }
                    }
                });
        addSubscrebe(mHotEBookSubscription);
    }

    /**
     * 最新上架
     *
     * @param pageNum 页码
     */
    private void getNewEBookList(final int pageNum) {
        if (null != mNewEBookSubscription && !mNewEBookSubscription.isUnsubscribed()) {//取消上一次接口返回的处理
            mNewEBookSubscription.unsubscribe();
        }
        mNewEBookSubscription = DataRepository.getInstance().getNewEBookList(mParameter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResultEntityVo<com.tzpt.cloudlibrary.modle.remote.newpojo.EBookListVo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.setEBookListError(pageNum == 1);
                        }
                    }

                    @Override
                    public void onNext(BaseResultEntityVo<com.tzpt.cloudlibrary.modle.remote.newpojo.EBookListVo> eBookListVoBaseResultEntityVo) {
                        if (mView != null) {
                            if (eBookListVoBaseResultEntityVo.status == 200) {
                                if (eBookListVoBaseResultEntityVo.data.resultList != null
                                        && eBookListVoBaseResultEntityVo.data.resultList.size() > 0) {
                                    List<EBookBean> eBookInfoBeanList = new ArrayList<>();
                                    for (EBookListItemVo eBookVo : eBookListVoBaseResultEntityVo.data.resultList) {
                                        EBookBean bookInfoBean = new EBookBean();
                                        bookInfoBean.mEBook.mId = eBookVo.id;
                                        bookInfoBean.mEBook.mName = eBookVo.bookName;
                                        bookInfoBean.mEBook.mFileDownloadPath = eBookVo.file;
                                        bookInfoBean.mEBook.mCoverImg = ImageUrlUtils.getDownloadOriginalImagePath(eBookVo.image);
                                        bookInfoBean.mEBook.mPublishDate = eBookVo.publishDate;
                                        bookInfoBean.mEBook.mSummary = HtmlFormatUtil.delHTMLTag(eBookVo.summary);
                                        bookInfoBean.mEBook.mIsbn = eBookVo.isbn;

                                        bookInfoBean.mAuthor.mName = eBookVo.author;
                                        bookInfoBean.mCategory.mName = eBookVo.categoryName;
                                        bookInfoBean.mPress.mName = eBookVo.publisher;
                                        bookInfoBean.mReadCount = eBookVo.number;
                                        //馆内显示电子书新标签
                                        if (!TextUtils.isEmpty(mLibraryCode)) {
                                            bookInfoBean.mHasNewEBookFlag = DateUtils.isWithinTimeLimit30Days(eBookVo.shelvesTime);
                                        }
                                        eBookInfoBeanList.add(bookInfoBean);
                                    }
                                    mView.setEBookList(eBookInfoBeanList, eBookListVoBaseResultEntityVo.data.totalCount, eBookListVoBaseResultEntityVo.data.limitTotalCount, pageNum == 1);
                                } else {
                                    mView.setEBookListEmpty(pageNum == 1);
                                }
                            } else {
                                mView.setEBookListError(pageNum == 1);
                            }
                        }
                    }
                });
        addSubscrebe(mNewEBookSubscription);
    }

    //获取电子书列表
    private void getElectronicBooksList(final int pageNum) {
        Subscription subscription = DataRepository.getInstance().getEBookList(mParameter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResultEntityVo<com.tzpt.cloudlibrary.modle.remote.newpojo.EBookListVo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.setEBookListError(pageNum == 1);
                        }
                    }

                    @Override
                    public void onNext(BaseResultEntityVo<com.tzpt.cloudlibrary.modle.remote.newpojo.EBookListVo> eBookListVoBaseResultEntityVo) {
                        if (mView != null) {
                            if (eBookListVoBaseResultEntityVo.status == 200) {
                                if (eBookListVoBaseResultEntityVo.data.resultList != null
                                        && eBookListVoBaseResultEntityVo.data.resultList.size() > 0) {
                                    List<EBookBean> eBookInfoBeanList = new ArrayList<>();
                                    for (EBookListItemVo eBookVo : eBookListVoBaseResultEntityVo.data.resultList) {
                                        EBookBean bookInfoBean = new EBookBean();
                                        bookInfoBean.mEBook.mId = eBookVo.id;
                                        bookInfoBean.mEBook.mName = eBookVo.bookName;
                                        bookInfoBean.mEBook.mFileDownloadPath = eBookVo.file;
                                        bookInfoBean.mEBook.mCoverImg = ImageUrlUtils.getDownloadOriginalImagePath(eBookVo.image);
                                        bookInfoBean.mEBook.mPublishDate = eBookVo.publishDate;
                                        bookInfoBean.mEBook.mSummary = HtmlFormatUtil.delHTMLTag(eBookVo.summary);
                                        bookInfoBean.mEBook.mIsbn = eBookVo.isbn;

                                        bookInfoBean.mAuthor.mName = eBookVo.author;
                                        bookInfoBean.mCategory.mName = eBookVo.categoryName;
                                        bookInfoBean.mPress.mName = eBookVo.publisher;
                                        bookInfoBean.mReadCount = eBookVo.number;
                                        eBookInfoBeanList.add(bookInfoBean);
                                    }
                                    mView.setEBookList(eBookInfoBeanList, eBookListVoBaseResultEntityVo.data.totalCount, eBookListVoBaseResultEntityVo.data.limitTotalCount, pageNum == 1);
                                } else {
                                    mView.setEBookListEmpty(pageNum == 1);
                                }
                            } else {
                                mView.setEBookListError(pageNum == 1);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    //获取排行榜电子书列表
    private void getRankEBookList(final int pageNum, int sortType) {
        mParameter.clear();
        mParameter.put("pageNo", String.valueOf(pageNum));
        mParameter.put("pageCount", "20");
        //一级目录id
        if (mParentClassifyId > 0) {
            mParameter.put("oneLevelCategoryId", String.valueOf(mParentClassifyId));
        } else {
            mParameter.remove("oneLevelCategoryId");
        }
        //二级目录id
        if (mChildClassifyId > 0) {
            mParameter.put("twoLevelCategoryId", String.valueOf(mChildClassifyId));
        } else {
            mParameter.remove("twoLevelCategoryId");
        }
        mParameter.put("sortType", String.valueOf(sortType));
        Subscription subscription = DataRepository.getInstance().getEBookRankingList(mParameter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResultEntityVo<EBookListVo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.setEBookListError(pageNum == 1);
                        }
                    }

                    @Override
                    public void onNext(BaseResultEntityVo<EBookListVo> eBookListVoBaseResultEntityVo) {
                        if (mView != null) {
                            if (eBookListVoBaseResultEntityVo.status == 200) {
                                if (eBookListVoBaseResultEntityVo.data.resultList != null
                                        && eBookListVoBaseResultEntityVo.data.resultList.size() > 0) {
                                    List<EBookBean> eBookInfoBeanList = new ArrayList<>();
                                    for (EBookListItemVo eBookVo : eBookListVoBaseResultEntityVo.data.resultList) {
                                        EBookBean bookInfoBean = new EBookBean();
                                        bookInfoBean.mEBook.mId = eBookVo.id;
                                        bookInfoBean.mEBook.mName = eBookVo.bookName;
                                        bookInfoBean.mEBook.mFileDownloadPath = eBookVo.file;
                                        bookInfoBean.mEBook.mCoverImg = ImageUrlUtils.getDownloadOriginalImagePath(eBookVo.image);
                                        bookInfoBean.mEBook.mPublishDate = eBookVo.publishDate;
                                        bookInfoBean.mEBook.mSummary = HtmlFormatUtil.delHTMLTag(eBookVo.summary);
                                        bookInfoBean.mEBook.mIsbn = eBookVo.isbn;

                                        bookInfoBean.mAuthor.mName = eBookVo.author;
                                        bookInfoBean.mCategory.mName = eBookVo.categoryName;
                                        bookInfoBean.mPress.mName = eBookVo.publisher;
                                        bookInfoBean.mReadCount = eBookVo.number;
                                        eBookInfoBeanList.add(bookInfoBean);
                                    }
                                    mView.setEBookList(eBookInfoBeanList, eBookListVoBaseResultEntityVo.data.totalCount, eBookListVoBaseResultEntityVo.data.limitTotalCount, pageNum == 1);
                                } else {
                                    mView.setEBookListEmpty(pageNum == 1);
                                }
                            } else {
                                mView.setEBookListError(pageNum == 1);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }
}
