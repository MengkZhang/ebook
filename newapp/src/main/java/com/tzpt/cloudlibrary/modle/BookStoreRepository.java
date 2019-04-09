package com.tzpt.cloudlibrary.modle;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.tzpt.cloudlibrary.business_bean.BaseResultData;
import com.tzpt.cloudlibrary.business_bean.LibraryBean;
import com.tzpt.cloudlibrary.modle.remote.CloudLibraryApi;
import com.tzpt.cloudlibrary.modle.remote.exception.ExceptionEngine;
import com.tzpt.cloudlibrary.modle.remote.exception.ServerException;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseResultEntityVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BookStoreListItemVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BookStoreListVo;
import com.tzpt.cloudlibrary.modle.remote.pojo.LibraryOpenTimeVo;
import com.tzpt.cloudlibrary.utils.DateUtils;
import com.tzpt.cloudlibrary.utils.ImageUrlUtils;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by tonyjia on 2018/12/6.
 */
public class BookStoreRepository {

    private static BookStoreRepository mInstance;

    public static BookStoreRepository getInstance() {
        if (mInstance == null) {
            mInstance = new BookStoreRepository();
        }
        return mInstance;
    }

    private BookStoreRepository() {

    }

    /**
     * 获取书店列表
     *
     * @param parameters 参数
     */
    public Observable<BaseResultData<List<LibraryBean>>> getBookStoreList(ArrayMap<String, Object> parameters) {
        return CloudLibraryApi.getInstance().getBookStoreList(parameters)
                .map(new Func1<BaseResultEntityVo<BookStoreListVo>, BaseResultData<List<LibraryBean>>>() {
                    @Override
                    public BaseResultData<List<LibraryBean>> call(BaseResultEntityVo<BookStoreListVo> bookStoreListVo) {
                        if (bookStoreListVo.status == 200) {
                            if (bookStoreListVo.data.resultList != null
                                    && bookStoreListVo.data.resultList.size() > 0) {
                                BaseResultData<List<LibraryBean>> data = new BaseResultData<>();
                                data.mTotalCount = bookStoreListVo.data.totalCount;
                                data.mLimitCount = bookStoreListVo.data.limitTotalCount;

                                List<LibraryBean> libraryBeanList = new ArrayList<>();
                                for (BookStoreListItemVo item : bookStoreListVo.data.resultList) {
                                    LibraryBean bean = new LibraryBean();
                                    bean.mLibrary.mId = item.libId;
                                    bean.mLibrary.mName = item.libName;
                                    bean.mLibrary.mAddress = item.address;
                                    bean.mLibrary.mLngLat = item.lngLat;
                                    bean.mLibrary.mCode = item.libCode;
                                    bean.mLibrary.mLogo = ImageUrlUtils.getDownloadOriginalImagePath(item.logo);
//                                    bean.mLibrary.mLighten = item.lighten;
                                    bean.mLibrary.mBookCount = item.bookNum;
                                    bean.mLibrary.mHeatCount = item.hotTip;
                                    bean.mIsOpen = libraryIsOpen(item.lightTime, item.serviceTime);//TODO
                                    bean.mDistance = item.distance;
                                    libraryBeanList.add(bean);
                                }
                                data.resultList = libraryBeanList;
                                return data;
                            } else {
                                return null;
                            }
                        } else {
                            throw new ServerException(bookStoreListVo.data.errorCode, bookStoreListVo.data.message);
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<BaseResultData<List<LibraryBean>>>());
    }

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

    private static class HttpResultFunc<T> implements Func1<Throwable, Observable<T>> {
        @Override
        public Observable<T> call(Throwable throwable) {
            return Observable.error(ExceptionEngine.handleException(throwable));
        }
    }

}
