package com.tzpt.cloudlibrary.modle;


import android.support.v4.util.ArrayMap;

import com.tzpt.cloudlibrary.bean.ClassifyTwoLevelBean;
import com.tzpt.cloudlibrary.business_bean.EBookBean;
import com.tzpt.cloudlibrary.modle.remote.CloudLibraryApi;
import com.tzpt.cloudlibrary.modle.remote.exception.ExceptionEngine;
import com.tzpt.cloudlibrary.modle.remote.exception.ServerException;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseResultEntityVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.EBookCategoryVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.EBookListItemVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.EBookListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.VideoCollectStatusVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.VideoFavoritesVo;
import com.tzpt.cloudlibrary.utils.HtmlFormatUtil;
import com.tzpt.cloudlibrary.utils.ImageUrlUtils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;

/**
 * 电子书model
 * Created by tonyjia on 2018/7/2.
 */
public class EBookRepository {

    private static EBookRepository mInstance;

    public static EBookRepository getInstance() {
        if (mInstance == null) {
            mInstance = new EBookRepository();
        }
        return mInstance;
    }

    private EBookRepository() {

    }

    private class HttpResultFunc<T> implements Func1<Throwable, Observable<T>> {
        @Override
        public Observable<T> call(Throwable throwable) {
            return Observable.error(ExceptionEngine.handleException(throwable));
        }
    }

    //获取电子书二级分类
    public Observable<List<ClassifyTwoLevelBean>> getEBookCategory() {
        return CloudLibraryApi.getInstance().getEBookCategory().map(new Func1<BaseResultEntityVo<List<EBookCategoryVo>>, List<ClassifyTwoLevelBean>>() {
            @Override
            public List<ClassifyTwoLevelBean> call(BaseResultEntityVo<List<EBookCategoryVo>> listBaseResultEntityVo) {
                return getClassifyTwoLevel(listBaseResultEntityVo.data);
            }
        }).onErrorResumeNext(new HttpResultFunc<List<ClassifyTwoLevelBean>>());
    }

    //电子书二级分类组装
    private List<ClassifyTwoLevelBean> getClassifyTwoLevel(List<EBookCategoryVo> list) {
        if (null != list && list.size() > 0) {
            List<ClassifyTwoLevelBean> videoBeanList = new ArrayList<>();
            for (EBookCategoryVo item : list) {
                ClassifyTwoLevelBean bean = new ClassifyTwoLevelBean();
                bean.mId = item.categoryId;
                bean.mName = item.categoryName;
                if (null != item.subList && item.subList.size() > 0) {
                    List<ClassifyTwoLevelBean> subVideoList = new ArrayList<>();
                    for (EBookCategoryVo subItem : item.subList) {
                        ClassifyTwoLevelBean subBean = new ClassifyTwoLevelBean();
                        subBean.mId = subItem.categoryId;
                        subBean.mName = subItem.categoryName;
                        subVideoList.add(subBean);
                    }
                    bean.mSubList = subVideoList;
                }
                videoBeanList.add(bean);
            }
            return videoBeanList;
        }
        return null;
    }

    //获取电子书收藏状态
    public Observable<Boolean> getEBookCollectStatus(String ebookId, String readerId) {
        return CloudLibraryApi.getInstance().getEBookCollectStatus(ebookId, readerId)
                .map(new Func1<BaseResultEntityVo<VideoCollectStatusVo>, Boolean>() {
                    @Override
                    public Boolean call(BaseResultEntityVo<VideoCollectStatusVo> videoCollectStatusVo) {
                        if (videoCollectStatusVo.status != 200) {
                            if (videoCollectStatusVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(videoCollectStatusVo.data.errorCode, videoCollectStatusVo.data.message);
                        }
                        return videoCollectStatusVo.data.favorStatus == 1;//1已收藏，0未收藏
                    }
                }).onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    //收藏电子书
    public Observable<Boolean> collectionEBook(ArrayMap<String, Object> map) {
        return CloudLibraryApi.getInstance().collectionEBook(map)
                .map(new Func1<BaseResultEntityVo<VideoFavoritesVo>, Boolean>() {
                    @Override
                    public Boolean call(BaseResultEntityVo<VideoFavoritesVo> videoFavoritesVo) {
                        if (videoFavoritesVo.status != 200) {
                            if (videoFavoritesVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(videoFavoritesVo.data.errorCode, videoFavoritesVo.data.message);
                        }
                        return videoFavoritesVo.data.result == 1;//1成功，0失败
                    }
                }).onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    //取消收藏电子书
    public Observable<Boolean> cancelCollectionEBook(JSONArray ebookIds, String readerId) {
        return CloudLibraryApi.getInstance().cancelCollectionEBook(ebookIds, readerId)
                .map(new Func1<BaseResultEntityVo<VideoFavoritesVo>, Boolean>() {
                    @Override
                    public Boolean call(BaseResultEntityVo<VideoFavoritesVo> videoFavoritesVo) {
                        if (videoFavoritesVo.status != 200) {
                            if (videoFavoritesVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(videoFavoritesVo.data.errorCode, videoFavoritesVo.data.message);
                        }
                        //删除后需要刷新用户信息(更新收藏数量)
                        UserRepository.getInstance().refreshUserInfo();
                        return videoFavoritesVo.data.result == 1;//1成功，0失败
                    }
                }).onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    //获取电子书收藏列表
    public Observable<List<EBookBean>> getCollectEBookList(String readerId, int pageNo, int pageCount) {
        return CloudLibraryApi.getInstance().getCollectEBookList(readerId, pageNo, pageCount)
                .map(new Func1<BaseResultEntityVo<EBookListVo>, List<EBookBean>>() {
                    @Override
                    public List<EBookBean> call(BaseResultEntityVo<EBookListVo> eBookListVoBaseResultEntityVo) {
                        if (eBookListVoBaseResultEntityVo.status != 200) {
                            throw new ServerException(eBookListVoBaseResultEntityVo.data.errorCode, eBookListVoBaseResultEntityVo.data.message);
                        }
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
                            return eBookInfoBeanList;
                        }
                        return null;
                    }
                }).onErrorResumeNext(new HttpResultFunc<List<EBookBean>>());
    }


    //电子书分享上报
    public Observable<Boolean> reportBookShare(String eBookId) {
        return CloudLibraryApi.getInstance().reportBookShare(eBookId, 2)
                .map(new Func1<BaseResultEntityVo, Boolean>() {
                    @Override
                    public Boolean call(BaseResultEntityVo baseResultEntityVo) {
                        return true;
                    }
                }).onErrorResumeNext(new HttpResultFunc<Boolean>());
    }
}
