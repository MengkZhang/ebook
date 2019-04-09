package com.tzpt.cloudlibrary.ui.account.interaction;

import android.text.TextUtils;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.business_bean.LibraryBean;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;
import com.tzpt.cloudlibrary.ui.map.LocationManager;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 推荐新书
 * Created by ZhiqiangJia on 2017-09-04.
 */
public class RecommendNewBookDialogPresenter extends RxPresenter<RecommendNewBookDialogContract.View> implements
        RecommendNewBookDialogContract.Presenter {

    private List<LibraryBean> mTempLibraryList = new ArrayList<>();
    private boolean mFirstLoading = true;

    @Override
    public void getRecommendBookLibList(String isbn) {
        if (TextUtils.isEmpty(isbn)) {
            return;
        }
        String idCard = UserRepository.getInstance().getLoginUserIdCard();
        if (TextUtils.isEmpty(idCard)) {
            return;
        }
        Subscription subscription = DataRepository.getInstance().getRecommendBooLibList(idCard, isbn, LocationManager.getInstance().getLngLat())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<LibraryBean>>() {
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
                                    mView.showNetError();
                                }
                            } else {
                                mView.showNetError();
                            }
                        }
                    }

                    @Override
                    public void onNext(List<LibraryBean> libraryBeans) {
                        if (mView != null) {
                            if (libraryBeans != null
                                    && libraryBeans.size() > 0) {
                                if (mFirstLoading) {
                                    mFirstLoading = false;
                                    mTempLibraryList.clear();
                                    mTempLibraryList.addAll(libraryBeans);
                                }
                                mView.setLibraryList(libraryBeans);
                            } else {
                                mView.setLibraryListEmpty();
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void recommendNewBookToLibrary(String isbn, final String libCode) {
        if (TextUtils.isEmpty(isbn)) {
            return;
        }
        String idCard = UserRepository.getInstance().getLoginUserIdCard();
        if (TextUtils.isEmpty(idCard)) {
            return;
        }
        Subscription subscription = DataRepository.getInstance().recommendBookByIsbn(idCard, isbn, libCode)
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
                                switch (((ApiException) e).getCode()) {
                                    case 30305:
                                        mView.showErrorMsg(R.string.have_been_recommend);
                                        mView.recommendSuccess(libCode);
                                        break;
                                    case 30306:
                                        mView.showErrorMsg(R.string.no_more_recommend_count);
                                        break;
                                    case 30100:
                                        mView.pleaseLogin();
                                        break;
                                    default:
                                        mView.showErrorMsg(R.string.network_fault);
                                        break;
                                }
                            } else {
                                mView.showErrorMsg(R.string.network_fault);
                            }
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (mView != null) {
                            if (aBoolean) {
                                mView.recommendSuccess(libCode);
                            } else {
                                mView.showErrorMsg(R.string.network_fault);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void refreshTempRecommendLibraryInfo(String libCode) {
        if (null == mTempLibraryList || mTempLibraryList.size() == 0) {
            return;
        }
        int index = -1;
        int size = mTempLibraryList.size();
        for (int i = 0; i < size; i++) {
            if (mTempLibraryList.get(i).mLibrary.mCode.equals(libCode)) {
                index = i;
                break;
            }
        }
        if (index != -1 && index < size) {
            mTempLibraryList.get(index).recommendExist = 1;
        }

    }

    @Override
    public void searchLibByKeyWord(String isbn, String keyword) {
        String idCard = UserRepository.getInstance().getLoginUserIdCard();
        if (TextUtils.isEmpty(idCard)) {
            return;
        }
        mView.showLoadingProgress();
        Subscription subscription = DataRepository.getInstance().searchRecommendLib(idCard, isbn, keyword, LocationManager.getInstance().getLngLat())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<LibraryBean>>() {
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
                                    mView.showNetError();
                                }
                            } else {
                                mView.showNetError();
                            }
                        }
                    }

                    @Override
                    public void onNext(List<LibraryBean> libraryBeans) {
                        if (mView != null) {
                            if (libraryBeans != null
                                    && libraryBeans.size() > 0) {
                                if (mFirstLoading) {
                                    mFirstLoading = false;
                                    mTempLibraryList.clear();
                                    mTempLibraryList.addAll(libraryBeans);
                                }
                                mView.setLibraryList(libraryBeans);
                            } else {
                                mView.setLibraryListEmpty();
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }
}
