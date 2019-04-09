package com.tzpt.cloudlibrary.ui.search;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.bean.SearchHotBean;
import com.tzpt.cloudlibrary.bean.SearchTypeBean;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseResultEntityVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.SearchHotResultVo;
import com.tzpt.cloudlibrary.ui.bookstore.BookStoreFilterType;
import com.tzpt.cloudlibrary.ui.bookstore.BookStoreFragment;
import com.tzpt.cloudlibrary.ui.bookstore.BookStorePresenter;
import com.tzpt.cloudlibrary.ui.ebook.EBookFilterType;
import com.tzpt.cloudlibrary.ui.ebook.EBookFragment;
import com.tzpt.cloudlibrary.ui.ebook.EBookPresenter;
import com.tzpt.cloudlibrary.ui.information.InformationFragment;
import com.tzpt.cloudlibrary.ui.information.InformationPresenter;
import com.tzpt.cloudlibrary.ui.library.LibraryFilterType;
import com.tzpt.cloudlibrary.ui.library.LibraryFragment;
import com.tzpt.cloudlibrary.ui.library.LibraryPresenter;
import com.tzpt.cloudlibrary.ui.paperbook.PaperBookFilterType;
import com.tzpt.cloudlibrary.ui.paperbook.PaperBookPresenter;
import com.tzpt.cloudlibrary.ui.paperbook.PaperBooksFragment;
import com.tzpt.cloudlibrary.ui.readers.ActivityListFragment;
import com.tzpt.cloudlibrary.ui.readers.ActivityListPresenter;
import com.tzpt.cloudlibrary.ui.video.VideoFilterType;
import com.tzpt.cloudlibrary.ui.video.VideoListFragment;
import com.tzpt.cloudlibrary.ui.video.VideoListPresenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 搜索入口类
 * Created by ZhiqiangJia on 2017-09-28.
 */
public class SearchPresenter extends RxPresenter<SearchContract.View> implements
        SearchContract.Presenter {
    private List<SearchHotBean> mTempHotSearchList = new ArrayList<>();
    private Subscription mSubscription;
    private String mSearchType = null;

    @Override
    public void getSearchTypeList(boolean isLib, int searchType) {
        List<SearchTypeBean> searchTypeList = new ArrayList<>();
        if (isLib) {
            SearchTypeBean item1 = new SearchTypeBean(0, "图书");
            item1.mIsSelected = true;
            searchTypeList.add(item1);
            SearchTypeBean item2 = new SearchTypeBean(1, "电子书");
            searchTypeList.add(item2);
            SearchTypeBean item6 = new SearchTypeBean(3, "视频");
            searchTypeList.add(item6);
            SearchTypeBean item4 = new SearchTypeBean(4, "资讯");
            searchTypeList.add(item4);
            SearchTypeBean item5 = new SearchTypeBean(5, "活动");
            searchTypeList.add(item5);
        } else {
            SearchTypeBean item1 = new SearchTypeBean(0, "图书");
            item1.mIsSelected = true;
            searchTypeList.add(item1);
            SearchTypeBean item2 = new SearchTypeBean(1, "电子书");
            searchTypeList.add(item2);
            SearchTypeBean item6 = new SearchTypeBean(3, "视频");
            searchTypeList.add(item6);
            SearchTypeBean item4 = new SearchTypeBean(4, "资讯");
            searchTypeList.add(item4);
            SearchTypeBean item5 = new SearchTypeBean(5, "活动");
            searchTypeList.add(item5);
            SearchTypeBean item3 = new SearchTypeBean(2, "图书馆");
            searchTypeList.add(item3);
            SearchTypeBean item7 = new SearchTypeBean(6, "书店");
            searchTypeList.add(item7);
        }
        //获取选中对象
        SearchTypeBean searchTypeBean = null;
        for (SearchTypeBean item : searchTypeList) {
            if (item.mSearchType == searchType) {
                searchTypeBean = item;
                break;
            }
        }
        mView.setSearchTypeList(searchTypeList, searchTypeBean);
    }

    @Override
    public void delAllHistoryTag(int searchType) {
        DataRepository.getInstance().delAllSearchTag(searchType);
        getHistoryTag(searchType);
    }

    @Override
    public void delHistoryTag(int searchType, int position) {
        String[] tags = DataRepository.getInstance().delHistoryTag(searchType, position);
        mView.setHistoryTag(tags);
    }


    @Override
    public void getHistoryTag(int searchType) {
        String[] historyTagArray = DataRepository.getInstance().getHistoryTagList(searchType);
        mView.setHistoryTag(historyTagArray);
    }

    @Override
    public void clickToSearch(int searchType, int position) {
        String searchName = DataRepository.getInstance().clickToSearch(searchType, position);
        if (!TextUtils.isEmpty(searchName)) {
            mView.toSearchResult(searchName);
        }
    }

    @Override
    public void getHotSearchList(final int searchType) {
        mTempHotSearchList.clear();
        mSearchType = null;
        switch (searchType) {
            case 0:
                mSearchType = "1";
                break;
            case 1:
                mSearchType = "2";
                break;
            case 2:
                mSearchType = "3";
                break;
            case 3:
                mSearchType = "6";
                break;
            case 4:
                mSearchType = "5";
                break;
            case 5:
                mSearchType = "4";
                break;
            case 6:
                mSearchType = "7";
                break;
            case 7:
                mSearchType = "0";//附近搜索图书馆和书店混合列表
                break;
        }
        if (TextUtils.isEmpty(mSearchType)) {
            return;
        }
        //取消前一个接口的订阅，不允许先请求的接口数据来覆盖最新请求的数（防止类型与数据不统一）
        if (null != mSubscription && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
        mSubscription = DataRepository.getInstance().getHotSearchList(null, mSearchType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResultEntityVo<List<SearchHotResultVo>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            List<SearchHotBean> searchHotList = DataRepository.getInstance().getHotSearchList(searchType);
                            if (searchHotList != null && searchHotList.size() > 0) {
                                List<String> list = new ArrayList<>();
                                for (SearchHotBean item : searchHotList) {
                                    list.add(item.mTitle);
                                }
                                mTempHotSearchList.addAll(searchHotList);
                                mView.setHotSearchList(list);
                            } else {
                                mView.dismissHotSearchList();
                            }
                        }
                    }

                    @Override
                    public void onNext(BaseResultEntityVo<List<SearchHotResultVo>> listBaseResultEntityVo) {
                        if (mView != null) {
                            if (listBaseResultEntityVo.status == 200
                                    && listBaseResultEntityVo.data != null
                                    && listBaseResultEntityVo.data.size() > 0) {
                                List<String> list = new ArrayList<>();
                                List<SearchHotBean> hotSearchList = new ArrayList<>();
                                for (SearchHotResultVo item : listBaseResultEntityVo.data) {
                                    list.add(item.title);

                                    SearchHotBean info = new SearchHotBean();
                                    info.mTitle = item.title;
                                    info.mValue = item.value;
                                    info.mIsBookStore = item.libraryLevelName != null && item.libraryLevelName.equals("书店");
                                    hotSearchList.add(info);
                                }
                                mTempHotSearchList.addAll(hotSearchList);
                                DataRepository.getInstance().saveHotSearchList(searchType, hotSearchList);

                                mView.setHotSearchList(list);
                            } else {
                                List<SearchHotBean> searchHotList = DataRepository.getInstance().getHotSearchList(searchType);
                                if (searchHotList != null && searchHotList.size() > 0) {
                                    List<String> list = new ArrayList<>();
                                    for (SearchHotBean item : searchHotList) {
                                        list.add(item.mTitle);
                                    }
                                    mTempHotSearchList.addAll(searchHotList);
                                    mView.setHotSearchList(list);
                                } else {
                                    mView.dismissHotSearchList();
                                }
                            }
                        }
                    }
                });
        addSubscrebe(mSubscription);

    }

    @Override
    public void getLibHotSearchList(String libCode, int searchType) {
        mTempHotSearchList.clear();
        mSearchType = null;
        switch (searchType) {
            case 0:
                mSearchType = "1";
                break;
            case 1:
                mSearchType = "2";
                break;
            case 3:
                mSearchType = "6";
                break;
            case 4:
                mSearchType = "5";
                break;
        }
        if (TextUtils.isEmpty(mSearchType) || TextUtils.isEmpty(libCode)) {
            return;
        }
        //取消前一个接口的订阅，不允许先请求的接口数据来覆盖最新请求的数（防止类型与数据不统一）
        if (null != mSubscription && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }

        final Subscription subscription = DataRepository.getInstance().getHotSearchList(libCode, mSearchType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResultEntityVo<List<SearchHotResultVo>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.dismissHotSearchList();
                        }
                    }

                    @Override
                    public void onNext(BaseResultEntityVo<List<SearchHotResultVo>> listBaseResultEntityVo) {
                        if (mView != null) {
                            if (listBaseResultEntityVo.status == 200
                                    && listBaseResultEntityVo.data != null
                                    && listBaseResultEntityVo.data.size() > 0) {
                                List<String> list = new ArrayList<>();
                                List<SearchHotBean> hotSearchList = new ArrayList<>();
                                for (SearchHotResultVo item : listBaseResultEntityVo.data) {
                                    list.add(item.title);

                                    SearchHotBean info = new SearchHotBean();
                                    info.mTitle = item.title;
                                    info.mValue = item.value;
                                    hotSearchList.add(info);
                                }
                                mTempHotSearchList.addAll(hotSearchList);
                                mView.setHotSearchList(list);
                            } else {
                                mView.dismissHotSearchList();
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void getHotSearchValue(int position) {
        SearchHotBean item = mTempHotSearchList.get(position);
        mView.doHotSearch(item.mTitle, item.mValue, item.mIsBookStore);
    }

    private void putSearchValueBySearchType(int searchType, String searchValue) {
        DataRepository.getInstance().putSearchValueBySearchType(searchType, searchValue);
    }

    @Override
    public String getSearchValueBySearchType(int searchType) {
        return DataRepository.getInstance().getSearchValueBySearchType(searchType);
    }

    @Override
    public void clearSearchValue() {
        DataRepository.getInstance().clearSearchValue();
    }

    private void saveHistoryTag(int searchType, String content) {
        //保存历史记录-在model数据层操作
        DataRepository.getInstance().saveHistoryTag(searchType, content);
    }

    @Override
    public void searchOption(int searchType, String content) {
        putSearchValueBySearchType(searchType,content);
        saveHistoryTag(searchType, content);
        getHistoryTag(searchType);
    }


}
