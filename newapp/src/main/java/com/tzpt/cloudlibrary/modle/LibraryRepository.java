package com.tzpt.cloudlibrary.modle;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tzpt.cloudlibrary.CloudLibraryApplication;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.data.Library;
import com.tzpt.cloudlibrary.bean.ActionInfoBean;
import com.tzpt.cloudlibrary.bean.AttentionLib;
import com.tzpt.cloudlibrary.bean.BannerInfo;
import com.tzpt.cloudlibrary.bean.CommentBean;
import com.tzpt.cloudlibrary.bean.InformationBean;
import com.tzpt.cloudlibrary.bean.ModelMenu;
import com.tzpt.cloudlibrary.bean.VideoSetBean;
import com.tzpt.cloudlibrary.business_bean.BaseListResultData;
import com.tzpt.cloudlibrary.business_bean.BookBean;
import com.tzpt.cloudlibrary.business_bean.EBookBean;
import com.tzpt.cloudlibrary.business_bean.InformationCommentDetailBean;
import com.tzpt.cloudlibrary.business_bean.LibraryBean;
import com.tzpt.cloudlibrary.modle.local.SharedPreferencesUtil;
import com.tzpt.cloudlibrary.modle.remote.CloudLibraryApi;
import com.tzpt.cloudlibrary.modle.remote.exception.ExceptionEngine;
import com.tzpt.cloudlibrary.modle.remote.exception.ServerException;
import com.tzpt.cloudlibrary.modle.remote.newpojo.ActivityVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.AttentionLibResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BannerNewListItemVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseDataResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseResultEntityVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BookBelongLibVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.CommentIndexVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.EBookVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.LibInfoVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.LibIntroduceVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.LibListItemVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.LibListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.LibraryDetailListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.LibraryModelVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.MessageBoardDetailVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.MessageBoardReplyListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.MessageBoardReplyVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.MessageBoardVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.NewsVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.PaperBookVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.VideoVo;
import com.tzpt.cloudlibrary.modle.remote.pojo.LibraryOpenTimeVo;
import com.tzpt.cloudlibrary.rxbus.RxBus;
import com.tzpt.cloudlibrary.rxbus.event.AttentionLibEvent;
import com.tzpt.cloudlibrary.ui.map.LocationManager;
import com.tzpt.cloudlibrary.utils.DateUtils;
import com.tzpt.cloudlibrary.utils.HtmlFormatUtil;
import com.tzpt.cloudlibrary.utils.ImageUrlUtils;
import com.tzpt.cloudlibrary.utils.MapUtils;
import com.tzpt.cloudlibrary.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func4;

import static com.tzpt.cloudlibrary.modle.local.SPKeyConstant.ATTENTION_LIB;
import static com.tzpt.cloudlibrary.modle.local.SPKeyConstant.ATTENTION_LIB_ACTIVITY_LIST;
import static com.tzpt.cloudlibrary.modle.local.SPKeyConstant.ATTENTION_LIB_BANNER_LIST;
import static com.tzpt.cloudlibrary.modle.local.SPKeyConstant.ATTENTION_LIB_BASE_INFO;
import static com.tzpt.cloudlibrary.modle.local.SPKeyConstant.ATTENTION_LIB_BOOK_LIST;
import static com.tzpt.cloudlibrary.modle.local.SPKeyConstant.ATTENTION_LIB_EBOOK_LIST;
import static com.tzpt.cloudlibrary.modle.local.SPKeyConstant.ATTENTION_LIB_MENU_LIST;
import static com.tzpt.cloudlibrary.modle.local.SPKeyConstant.ATTENTION_LIB_NEWS_LIST;
import static com.tzpt.cloudlibrary.modle.local.SPKeyConstant.ATTENTION_LIB_VIDEO_SET_LIST;

/**
 * Created by Administrator on 2018/8/31.
 */

public class LibraryRepository {
    private static LibraryRepository mInstance;

    private int mTotalCount;
    private int mLimitTotalCount;
    private RxBus sRxBus;

    private List<BannerInfo> mTempLibBannerList = new ArrayList<>();
    private LibraryBean mLibraryBean = null;

    private LibraryRepository() {
        sRxBus = CloudLibraryApplication.mRxBus;
    }

    public static LibraryRepository getInstance() {
        if (mInstance == null) {
            mInstance = new LibraryRepository();
        }
        return mInstance;
    }

    private static class HttpResultFunc<T> implements Func1<Throwable, Observable<T>> {
        @Override
        public Observable<T> call(Throwable throwable) {
            return Observable.error(ExceptionEngine.handleException(throwable));
        }
    }

    /**
     * 获取用户信息，如果发现关注的图书馆信息为空，清空本地保存的关注图如果发现关注的图书馆信息为空，清空本地保存的关注图书馆；
     * 如果发现关注的图书馆和后台的不一样，重新保存新的关注图书馆
     *
     * @param libCode 馆号
     * @param libName 馆名
     */
    void saveAttentionLib(String libCode, String libName) {
        if (TextUtils.isEmpty(libCode)) {
            removeAttentionLib();
        } else if (TextUtils.isEmpty(getAttentionLibCode())
                || !getAttentionLibCode().equals(libCode)) {
            AttentionLib attentionLib = new AttentionLib(libName, libCode);
            removeAttentionLib();
            SharedPreferencesUtil.getInstance().putObject(ATTENTION_LIB, attentionLib);

            AttentionLibEvent attentionLibEvent = new AttentionLibEvent(true);
            sRxBus.post(attentionLibEvent);
        }
    }

    /**
     * 移除本地保存的关注图书馆
     */
    void removeAttentionLib() {
        SharedPreferencesUtil.getInstance().remove(ATTENTION_LIB);
        SharedPreferencesUtil.getInstance().remove(ATTENTION_LIB_BASE_INFO);
        SharedPreferencesUtil.getInstance().remove(ATTENTION_LIB_BANNER_LIST);
        SharedPreferencesUtil.getInstance().remove(ATTENTION_LIB_MENU_LIST);
        SharedPreferencesUtil.getInstance().remove(ATTENTION_LIB_BOOK_LIST);
        SharedPreferencesUtil.getInstance().remove(ATTENTION_LIB_EBOOK_LIST);
        SharedPreferencesUtil.getInstance().remove(ATTENTION_LIB_VIDEO_SET_LIST);
        SharedPreferencesUtil.getInstance().remove(ATTENTION_LIB_NEWS_LIST);
        SharedPreferencesUtil.getInstance().remove(ATTENTION_LIB_ACTIVITY_LIST);
        AttentionLibEvent attentionLibEvent = new AttentionLibEvent(true);
        CloudLibraryApplication.mRxBus.post(attentionLibEvent);
    }

    /**
     * 获取关注图书馆本地馆号
     *
     * @return 馆号
     */
    @Nullable
    public String getAttentionLibCode() {
        AttentionLib attentionLib = SharedPreferencesUtil.getInstance().getObject(ATTENTION_LIB, AttentionLib.class);
        if (attentionLib != null) {
            return attentionLib.mLibCode;
        }
        return null;
    }

    /**
     * 获取关注图书馆本地馆名
     *
     * @return 馆名
     */
    @Nullable
    public String getAttentionLibName() {
        AttentionLib attentionLib = SharedPreferencesUtil.getInstance().getObject(ATTENTION_LIB, AttentionLib.class);
        if (attentionLib != null) {
            return attentionLib.mName;
        }
        return null;
    }

    /**
     * 获取关注图书馆本地Banner
     *
     * @return Banner列表
     */
    public List<BannerInfo> getAttentionLibBannerList() {
        String bannerStrJson = SharedPreferencesUtil.getInstance().getString(ATTENTION_LIB_BANNER_LIST);
        if (!TextUtils.isEmpty(bannerStrJson)) {
            Gson gson = new Gson();
            return gson.fromJson(bannerStrJson, new TypeToken<List<BannerInfo>>() {
            }.getType());
        } else {
            return null;
        }
    }
//
//    /**
//     * 判断图书馆是否开放
//     *
//     * @param lightTime 开放时间
//     * @param time      当前时间
//     * @return 图书馆是否开放
//     */
//    private boolean libraryIsOpen(String lightTime, String time) {
//        boolean isOpen = false;
//        if (null != lightTime) {
//            LibraryOpenTimeVo mLibraryOpenTimeVo = new Gson().fromJson(lightTime, LibraryOpenTimeVo.class);
//            if (null != mLibraryOpenTimeVo) {
//                LibraryOpenTimeVo.DayTime mDayTime = mLibraryOpenTimeVo.dayTime;
//                LibraryOpenTimeVo.AM am = mDayTime.am;
//                LibraryOpenTimeVo.PM pm = mDayTime.pm;
//                if (null != time && !TextUtils.isEmpty(time)) {
//                    boolean isAm = DateUtils.timeIsAM(time);
//                    StringBuilder builder = new StringBuilder();
//                    if (isAm) {
//                        builder.append(am.begin).append("-").append(am.end);
//                    } else {
//                        builder.append(pm.begin).append("-").append(pm.end);
//                    }
//                    String sourceDate = builder.toString();
//                    //判断系统当前时间是否在指定时间范围内
//                    isOpen = DateUtils.isInTime(sourceDate, DateUtils.formatTime(time));
//                } else {
//                    isOpen = false;
//                }
//            } else {
//                isOpen = false;
//            }
//        }
//        return isOpen;
//    }

    /**
     * 获取图书馆列表总条数
     *
     * @return 条数
     */
    public int getTotalCount() {
        return mTotalCount;
    }

    /**
     * 获取图书馆限制条数
     *
     * @return
     */
    public int getLimitTotalCount() {
        return mLimitTotalCount;
    }

    /**
     * 获取图书馆列表
     *
     * @param map 请求参数
     * @return 图书馆列表
     */
    public Observable<List<LibraryBean>> getLibraryList(Map<String, Object> map) {
        return CloudLibraryApi.getInstance().getLibraryList(map)
                .map(new Func1<BaseResultEntityVo<LibListVo>, List<LibraryBean>>() {
                    @Override
                    public List<LibraryBean> call(BaseResultEntityVo<LibListVo> libListVoBaseResultEntityVo) {
                        return dealLibListVo(libListVoBaseResultEntityVo);
                    }
                }).onErrorResumeNext(new HttpResultFunc<List<LibraryBean>>());
    }

    /**
     * 获取分馆列表
     *
     * @return 图书馆列表
     */
    public Observable<List<LibraryBean>> getBranchLibraryList(int pageNum, int pageCount, String libCode, String lngLat) {
        return CloudLibraryApi.getInstance().getBranchLibraryList(pageNum, pageCount, libCode, lngLat)
                .map(new Func1<BaseResultEntityVo<LibListVo>, List<LibraryBean>>() {
                    @Override
                    public List<LibraryBean> call(BaseResultEntityVo<LibListVo> libListVoBaseResultEntityVo) {
                        return dealLibListVo(libListVoBaseResultEntityVo);
                    }
                }).onErrorResumeNext(new HttpResultFunc<List<LibraryBean>>());
    }

    /**
     * 获取上级馆列表
     *
     * @return 图书馆列表
     */
    public Observable<List<LibraryBean>> getSupLibraryList(int pageNum, int pageCount, String libCode, String lngLat) {
        return CloudLibraryApi.getInstance().getSupLibraryList(pageNum, pageCount, libCode, lngLat)
                .map(new Func1<BaseResultEntityVo<LibListVo>, List<LibraryBean>>() {
                    @Override
                    public List<LibraryBean> call(BaseResultEntityVo<LibListVo> libListVoBaseResultEntityVo) {
                        return dealLibListVo(libListVoBaseResultEntityVo);
                    }
                }).onErrorResumeNext(new HttpResultFunc<List<LibraryBean>>());
    }

    /**
     * 处理网络返回的数据
     *
     * @param libListVoBaseResultEntityVo 网络返回的数据
     * @return 图书馆列表
     */
    private List<LibraryBean> dealLibListVo(BaseResultEntityVo<LibListVo> libListVoBaseResultEntityVo) {
        if (libListVoBaseResultEntityVo.status == 200) {
            if (libListVoBaseResultEntityVo.data.resultList != null
                    && libListVoBaseResultEntityVo.data.resultList.size() > 0) {
                mTotalCount = libListVoBaseResultEntityVo.data.totalCount;
                mLimitTotalCount = libListVoBaseResultEntityVo.data.limitTotalCount;
                List<LibraryBean> libraryBeanList = new ArrayList<>();
                for (LibListItemVo item : libListVoBaseResultEntityVo.data.resultList) {
                    LibraryBean bean = new LibraryBean();
                    bean.mLibrary.mId = item.libId;
                    bean.mLibrary.mName = item.libName;
                    bean.mLibrary.mAddress = item.address;
                    bean.mLibrary.mLngLat = item.lngLat;
                    bean.mLibrary.mCode = item.libCode;
                    bean.mLibrary.mLogo = ImageUrlUtils.getDownloadOriginalImagePath(item.logo);
//                    bean.mLibrary.mLighten = item.lighten;
                    bean.mLibrary.mBookCount = item.bookNum;
                    bean.mLibrary.mHeatCount = item.hotTip;
                    bean.mLibrary.mLevelName = item.libLevel;
                    bean.mIsBookStore = item.libLevel != null && (item.libLevel.equals("书店") || item.libLevel.equals("共享书屋"));
                    bean.mIsOpen = StringUtils.libraryIsOpen(item.lightTime, item.serviceTime);//TODO
                    bean.mDistance = item.distance;
                    libraryBeanList.add(bean);
                }
                return libraryBeanList;
            } else {
                return null;
            }
        } else {
            throw new ServerException(libListVoBaseResultEntityVo.data.errorCode, libListVoBaseResultEntityVo.data.message);
        }
    }

    /**
     * 获取图书馆资讯广告
     *
     * @param libCode      馆号
     * @param locationCode 定位信息
     * @return 资讯广告
     */
    public Observable<List<BannerInfo>> getLibBannerNewsList(String libCode, String locationCode) {
        return CloudLibraryApi.getInstance().getLibBannerNewsList(libCode, locationCode)
                .map(new Func1<BaseResultEntityVo<List<BannerNewListItemVo>>, List<BannerInfo>>() {
                    @Override
                    public List<BannerInfo> call(BaseResultEntityVo<List<BannerNewListItemVo>> listBaseResultEntityVo) {
                        if (listBaseResultEntityVo.status == 200) {
                            if (listBaseResultEntityVo.data != null
                                    && listBaseResultEntityVo.data.size() > 0) {
                                List<BannerInfo> bannerInfoList = new ArrayList<>();
                                for (BannerNewListItemVo item : listBaseResultEntityVo.data) {
                                    BannerInfo info = new BannerInfo();
                                    info.mTitle = item.title;
                                    info.mImgUrl = ImageUrlUtils.getDownloadOriginalImagePath(item.image);
                                    info.mNewsId = item.newsId;
                                    bannerInfoList.add(info);
                                }

                                if (mTempLibBannerList != null) {
                                    mTempLibBannerList.clear();
                                    mTempLibBannerList.addAll(bannerInfoList);
                                }
                                return bannerInfoList;
                            } else {
                                return null;
                            }
                        } else {
                            throw new ServerException(listBaseResultEntityVo.status, "Server error");
                        }

                    }
                }).onErrorResumeNext(new HttpResultFunc<List<BannerInfo>>());
    }

    /**
     * 获取图书馆详情信息
     *
     * @param libCode    馆号
     * @param fromSearch 是否来做搜索结果
     * @param flag       标记搜索为结果为图书馆还是书店，fromSearch=1生效-->1图书馆2书店 ，-1为关注馆，不确定类型
     * @return
     */
    public Observable<LibraryBean> getLibResourcesList(final String libCode, int fromSearch, final int flag) {
        return Observable.zip(
                CloudLibraryApi.getInstance().getLibIntroduce(LocationManager.getInstance().getLngLat(), libCode, fromSearch, flag),
                CloudLibraryApi.getInstance().getLibModelList(libCode),
                CloudLibraryApi.getInstance().getLibResourcesList(libCode),
                CloudLibraryApi.getInstance().getLibraryNumber(libCode),

                new Func4<BaseResultEntityVo<LibIntroduceVo>,
                        BaseResultEntityVo<List<LibraryModelVo>>,
                        BaseResultEntityVo<LibraryDetailListVo>,
                        BaseResultEntityVo<LibInfoVo>,
                        LibraryBean>() {
                    @Override
                    public LibraryBean call(BaseResultEntityVo<LibIntroduceVo> libIntroduceVo,
                                            BaseResultEntityVo<List<LibraryModelVo>> libraryModelListVo,
                                            BaseResultEntityVo<LibraryDetailListVo> libraryInfoVo,
                                            BaseResultEntityVo<LibInfoVo> libInfoVo

                    ) {
                        if (libIntroduceVo.status == 200
                                && libraryInfoVo.status == 200
                                && libraryModelListVo.status == 200
                                && libInfoVo.status == 200) {
                            mLibraryBean = new LibraryBean();
                            //设置本馆介绍
                            if (libInfoVo.data != null) {
                                mLibraryBean.mLibrary.mRegReaderCount = libInfoVo.data.registeredReaderNum;
                                mLibraryBean.mLibrary.mVisitReaderCount = libInfoVo.data.accessReadersNum;
                                mLibraryBean.mLibrary.mServerReaderCount = libInfoVo.data.serviceReaderNum;
                            }
                            mLibraryBean.mLibrary.mId = libIntroduceVo.data.info.libId;
                            mLibraryBean.mLibrary.mCode = libIntroduceVo.data.info.libCode;
                            mLibraryBean.mLibrary.mName = libIntroduceVo.data.info.libName;
                            mLibraryBean.mLibrary.mPhone = libIntroduceVo.data.info.phone;
                            mLibraryBean.mLibrary.mMail = libIntroduceVo.data.info.eamil;
                            mLibraryBean.mLibrary.mLngLat = libIntroduceVo.data.info.lngLat;
                            mLibraryBean.mLibrary.mNet = libIntroduceVo.data.info.webUrl;
                            mLibraryBean.mLibrary.mLogo = ImageUrlUtils.getDownloadOriginalImagePath(libIntroduceVo.data.info.logo);
                            mLibraryBean.mIsValidLngLat = (libIntroduceVo.data.info.isValidLngLat == 1);
                            mLibraryBean.mLibrary.mAddress = libIntroduceVo.data.info.address + (mLibraryBean.mIsValidLngLat ? "" : "(未精准定位)");
                            mLibraryBean.mLibrary.mLevelName = libIntroduceVo.data.info.libraryLevelName;
                            mLibraryBean.mIsBookStore = libIntroduceVo.data.info.libraryLevelName != null
                                    && (libIntroduceVo.data.info.libraryLevelName.equals("书店") || libIntroduceVo.data.info.libraryLevelName.equals("共享书屋"));

                            //判断是否书店
                            boolean isBookStore;
                            if (flag == -1) {
                                isBookStore = mLibraryBean.mIsBookStore;
                            } else {
                                isBookStore = flag == 2;
                            }
                            //是否有总分馆
                            mLibraryBean.mLibrary.mBranchLibCount = libIntroduceVo.data.info.branchLibraryNum;
                            mLibraryBean.mLibrary.mOpenRestrictionStr = libIntroduceVo.data.info.openRestrictionStr;
                            if (null != libIntroduceVo.data.openInfo) {
                                mLibraryBean.mLibrary.mLightTime = libIntroduceVo.data.openInfo.lightTime;
                            }
                            if (libIntroduceVo.data.htmlInfo != null) {
                                mLibraryBean.mLibrary.mIntroduceUrl = libIntroduceVo.data.htmlInfo.url;
                            }
                            mLibraryBean.mDistance = libIntroduceVo.data.info.distance;

                            //设置模块列表
                            List<ModelMenu> menuItemList = new ArrayList<>();
                            menuItemList.add(new ModelMenu(0, isBookStore ? "本店介绍" : "本馆介绍", R.drawable.btn_lib_icon_introduce, true));
                            //是否有总分馆
                            if (!isBookStore && libIntroduceVo.data.info.branchLibraryNum > 0) {
                                menuItemList.add(new ModelMenu(1, "总分馆", R.drawable.btn_lib_icon_branch_lib, true));
                            }
                            menuItemList.add(new ModelMenu(2, "图书", R.drawable.btn_lib_icon_book, true));
                            menuItemList.add(new ModelMenu(3, "电子书", R.drawable.btn_lib_icon_ebook, true));
                            menuItemList.add(new ModelMenu(4, "视频", R.drawable.btn_lib_icon_video, true));
                            menuItemList.add(new ModelMenu(5, "活动", R.drawable.btn_lib_icon_activity, true));
                            menuItemList.add(new ModelMenu(6, "资讯", R.drawable.btn_lib_icon_news, true));
                            menuItemList.add(new ModelMenu(7, isBookStore ? "顾客留言" : "读者留言", R.drawable.btn_lib_icon_msg, true));
                            if (!isBookStore) {
                                menuItemList.add(new ModelMenu(8, "借阅须知", R.drawable.btn_lib_icon_notice, true));
                            }
                            List<ModelMenu> libraryMenuItemList = new ArrayList<>();
                            if (null != libraryModelListVo.data) {
                                for (LibraryModelVo item : libraryModelListVo.data) {
                                    ModelMenu menuItem = new ModelMenu();
                                    menuItem.mIsBaseModel = false;
                                    menuItem.mName = item.modelName;
                                    menuItem.mUrl = item.appLink;

                                    int index = item.logoUrl.lastIndexOf(".");
                                    if (index != -1) {
                                        StringBuilder sb = new StringBuilder(item.logoUrl);
                                        sb.insert(index, "x3");
                                        menuItem.mLogoUrl = sb.toString();
                                    } else {
                                        menuItem.mLogoUrl = item.logoUrl;
                                    }
                                    libraryMenuItemList.add(menuItem);
                                }
                            }
                            if (libraryMenuItemList.size() > 0) {
                                menuItemList.addAll(menuItemList.size() - 1, libraryMenuItemList);
                            }
                            mLibraryBean.mMenuItemList = menuItemList;
                            //设置图书馆信息列表
                            //set paper book list
                            if (libraryInfoVo.data.book != null && null != libraryInfoVo.data.book.list && libraryInfoVo.data.book.list.size() > 0) {
                                List<BookBean> bookBeanList = new ArrayList<>();
                                for (PaperBookVo book : libraryInfoVo.data.book.list) {
                                    BookBean bookBean = new BookBean();
                                    bookBean.mAuthor.mName = book.author;
                                    bookBean.mBook.mName = book.bookName;
                                    bookBean.mBook.mIsbn = book.isbn;
                                    bookBean.mBook.mCoverImg = ImageUrlUtils.getDownloadOriginalImagePath(book.image);
                                    bookBeanList.add(bookBean);
                                }
                                mLibraryBean.mBookBeanList = bookBeanList;
                                mLibraryBean.mLibrary.mBookCount = libraryInfoVo.data.book.total;
                            }
                            //set ebook list
                            if (libraryInfoVo.data.ebook != null && null != libraryInfoVo.data.ebook.list && libraryInfoVo.data.ebook.list.size() > 0) {
                                List<EBookBean> eBookBeanList = new ArrayList<>();
                                for (EBookVo eBookVo : libraryInfoVo.data.ebook.list) {
                                    EBookBean bookInfoBean = new EBookBean();
                                    bookInfoBean.mEBook.mId = eBookVo.id;
                                    bookInfoBean.mEBook.mName = eBookVo.bookName;
                                    bookInfoBean.mEBook.mSummary = HtmlFormatUtil.delHTMLTag(eBookVo.summary);
                                    bookInfoBean.mEBook.mCoverImg = ImageUrlUtils.getDownloadOriginalImagePath(eBookVo.image);
                                    bookInfoBean.mAuthor.mName = eBookVo.author;
                                    bookInfoBean.mReadCount = eBookVo.number;
                                    eBookBeanList.add(bookInfoBean);
                                }
                                mLibraryBean.mEBookBeanList = eBookBeanList;
                                mLibraryBean.mLibrary.mEBookCount = libraryInfoVo.data.ebook.total;
                            }
                            //set video list
                            if (libraryInfoVo.data.video != null && null != libraryInfoVo.data.video.list && libraryInfoVo.data.video.list.size() > 0) {
                                List<VideoSetBean> videoSetBeanList = new ArrayList<>();
                                for (VideoVo video : libraryInfoVo.data.video.list) {
                                    VideoSetBean bean = new VideoSetBean();
                                    bean.setId(video.id);
                                    bean.setTitle(video.name);
                                    bean.setContent(video.content);
                                    bean.setCoverImg(video.image);
                                    bean.setWatchTimes(video.watchTotalNum);
                                    videoSetBeanList.add(bean);
                                }
                                mLibraryBean.mVideoSetBeanList = videoSetBeanList;
                                mLibraryBean.mLibrary.mVideoSetCount = libraryInfoVo.data.video.total;
                            }
                            //set activity list
                            if (libraryInfoVo.data.activity != null && null != libraryInfoVo.data.activity.list && libraryInfoVo.data.activity.list.size() > 0) {
                                List<ActionInfoBean> activityBeanList = new ArrayList<>();
                                for (ActivityVo activity : libraryInfoVo.data.activity.list) {
                                    ActionInfoBean actionInfoBean = new ActionInfoBean();

                                    actionInfoBean.mId = activity.id;
                                    actionInfoBean.mTitle = activity.title;
                                    actionInfoBean.mSummary = activity.summary;
                                    actionInfoBean.mStartDateTime = activity.startDate;
                                    actionInfoBean.mEndDateTime = activity.endDate;
                                    actionInfoBean.mImage = ImageUrlUtils.getDownloadOriginalImagePath(activity.image);
                                    actionInfoBean.mAddress = activity.address;
                                    actionInfoBean.mIsJoin = activity.isJoin == 1;
                                    activityBeanList.add(actionInfoBean);
                                }
                                mLibraryBean.mActivityBeanList = activityBeanList;
                                mLibraryBean.mLibrary.mActivityCount = libraryInfoVo.data.activity.total;
                            }
                            //set news list
                            if (libraryInfoVo.data.news != null && null != libraryInfoVo.data.news.list && libraryInfoVo.data.news.list.size() > 0) {
                                List<InformationBean> informationBeanList = new ArrayList<>();
                                for (NewsVo news : libraryInfoVo.data.news.list) {
                                    InformationBean informationBean = new InformationBean();
                                    informationBean.mId = news.newsId;
                                    informationBean.mTitle = news.title;
                                    informationBean.mSummary = news.summary;
                                    informationBean.mCreateDate = news.createDate;
                                    informationBean.mSource = news.source;
                                    informationBean.mImage = ImageUrlUtils.getDownloadOriginalImagePath(news.image);
                                    informationBean.mVideoDuration = news.videoDuration;
                                    informationBean.mVideoUrl = news.videoUrl;
                                    informationBean.mUrl = news.detailUrl;
                                    informationBean.mShareUrl = news.htmlUrl;
                                    informationBean.mReadCount = news.viewCount;
                                    informationBean.mCreateTime = DateUtils.formatNewsTime(news.currentTime, news.createTime);
                                    informationBeanList.add(informationBean);
                                }
                                mLibraryBean.mInformationBeanList = informationBeanList;
                                mLibraryBean.mLibrary.mNewsCount = libraryInfoVo.data.news.total;
                            }
                            return mLibraryBean;
                        } else if (libIntroduceVo.status != 200) {
                            throw new ServerException(libIntroduceVo.status, "");
                        } else if (libraryModelListVo.status != 200) {
                            throw new ServerException(libraryModelListVo.status, "");
                        } else {
                            throw new ServerException(libraryInfoVo.status, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<LibraryBean>());
    }

    /**
     * 关注图书馆
     *
     * @param libCode 馆号
     * @return true表示关注成功 false表示关注失败
     */
    public Observable<Boolean> attentionLib(final String libCode, final String libName) {
        return CloudLibraryApi.getInstance().attentionLib(libCode, UserRepository.getInstance().getLoginReaderId())
                .map(new Func1<BaseResultEntityVo<AttentionLibResultVo>, Boolean>() {
                    @Override
                    public Boolean call(BaseResultEntityVo<AttentionLibResultVo> attentionLibResultVoBaseResultEntityVo) {
                        if (attentionLibResultVoBaseResultEntityVo.status == 200) {
                            boolean result = attentionLibResultVoBaseResultEntityVo.data.status;
                            if (result) {
                                //保存本地缓存数据
                                saveLocalLibResourceCache();
                                //发送消息
                                AttentionLib attentionLib = new AttentionLib(libName, libCode);
                                SharedPreferencesUtil.getInstance().remove(ATTENTION_LIB);
                                SharedPreferencesUtil.getInstance().putObject(ATTENTION_LIB, attentionLib);
                                AttentionLibEvent attentionLibEvent = new AttentionLibEvent(true);
                                sRxBus.post(attentionLibEvent);
                            }
                            return result;
                        } else {
                            if (attentionLibResultVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(attentionLibResultVoBaseResultEntityVo.data.errorCode,
                                    attentionLibResultVoBaseResultEntityVo.data.message);
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    /**
     * 保存图书馆资源列表
     */
    public Observable<Boolean> saveLibResources() {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                saveLocalLibResourceCache();
                subscriber.onNext(true);
                subscriber.onCompleted();
            }
        });
    }

    /**
     * 保存本地图书馆缓存
     */
    private void saveLocalLibResourceCache() {
        if (mLibraryBean != null) {
            Gson gson = new Gson();
            //banner
            if (mTempLibBannerList != null && !mTempLibBannerList.isEmpty()) {
                String bannerStrJson = gson.toJson(mTempLibBannerList);
                SharedPreferencesUtil.getInstance().remove(ATTENTION_LIB_BANNER_LIST);
                SharedPreferencesUtil.getInstance().putString(ATTENTION_LIB_BANNER_LIST, bannerStrJson);
            } else {
                SharedPreferencesUtil.getInstance().remove(ATTENTION_LIB_BANNER_LIST);
            }
            //模块
            if (mLibraryBean != null && mLibraryBean.mMenuItemList != null) {
                String strJson = gson.toJson(mLibraryBean.mMenuItemList);
                SharedPreferencesUtil.getInstance().remove(ATTENTION_LIB_MENU_LIST);
                SharedPreferencesUtil.getInstance().putString(ATTENTION_LIB_MENU_LIST, strJson);
            } else {
                SharedPreferencesUtil.getInstance().remove(ATTENTION_LIB_MENU_LIST);
            }
            //图书馆信息
            if (mLibraryBean.mLibrary != null) {
                String strJson = gson.toJson(mLibraryBean.mLibrary);
                SharedPreferencesUtil.getInstance().remove(ATTENTION_LIB_BASE_INFO);
                SharedPreferencesUtil.getInstance().putString(ATTENTION_LIB_BASE_INFO, strJson);
            } else {
                SharedPreferencesUtil.getInstance().remove(ATTENTION_LIB_BASE_INFO);
            }
            //图书
            if (mLibraryBean.mBookBeanList != null && !mLibraryBean.mBookBeanList.isEmpty()) {
                String strJson = gson.toJson(mLibraryBean.mBookBeanList);
                SharedPreferencesUtil.getInstance().remove(ATTENTION_LIB_BOOK_LIST);
                SharedPreferencesUtil.getInstance().putString(ATTENTION_LIB_BOOK_LIST, strJson);
            } else {
                SharedPreferencesUtil.getInstance().remove(ATTENTION_LIB_BOOK_LIST);
            }
            //电子书
            if (mLibraryBean.mEBookBeanList != null && !mLibraryBean.mEBookBeanList.isEmpty()) {
                String strJson = gson.toJson(mLibraryBean.mEBookBeanList);
                SharedPreferencesUtil.getInstance().remove(ATTENTION_LIB_EBOOK_LIST);
                SharedPreferencesUtil.getInstance().putString(ATTENTION_LIB_EBOOK_LIST, strJson);
            } else {
                SharedPreferencesUtil.getInstance().remove(ATTENTION_LIB_EBOOK_LIST);
            }
            //视频
            if (mLibraryBean.mVideoSetBeanList != null && !mLibraryBean.mVideoSetBeanList.isEmpty()) {
                String strJson = gson.toJson(mLibraryBean.mVideoSetBeanList);
                SharedPreferencesUtil.getInstance().remove(ATTENTION_LIB_VIDEO_SET_LIST);
                SharedPreferencesUtil.getInstance().putString(ATTENTION_LIB_VIDEO_SET_LIST, strJson);
            } else {
                SharedPreferencesUtil.getInstance().remove(ATTENTION_LIB_VIDEO_SET_LIST);
            }
            //资讯
            if (mLibraryBean.mInformationBeanList != null && !mLibraryBean.mInformationBeanList.isEmpty()) {
                String strJson = gson.toJson(mLibraryBean.mInformationBeanList);
                SharedPreferencesUtil.getInstance().remove(ATTENTION_LIB_NEWS_LIST);
                SharedPreferencesUtil.getInstance().putString(ATTENTION_LIB_NEWS_LIST, strJson);
            } else {
                SharedPreferencesUtil.getInstance().remove(ATTENTION_LIB_NEWS_LIST);
            }
            //获取
            if (mLibraryBean.mActivityBeanList != null && !mLibraryBean.mActivityBeanList.isEmpty()) {
                String strJson = gson.toJson(mLibraryBean.mActivityBeanList);
                SharedPreferencesUtil.getInstance().remove(ATTENTION_LIB_ACTIVITY_LIST);
                SharedPreferencesUtil.getInstance().putString(ATTENTION_LIB_ACTIVITY_LIST, strJson);
            } else {
                SharedPreferencesUtil.getInstance().remove(ATTENTION_LIB_ACTIVITY_LIST);
            }
        }
    }

    /**
     * 取消关注图书馆
     *
     * @param libCode 馆号
     * @return true表示取消关注成功 false表示取消关注失败
     */
    public Observable<Boolean> cancelAttentionLib(String libCode) {
        return CloudLibraryApi.getInstance().unAttentionLib(UserRepository.getInstance().getLoginReaderId(), libCode)
                .map(new Func1<BaseResultEntityVo<AttentionLibResultVo>, Boolean>() {
                    @Override
                    public Boolean call(BaseResultEntityVo<AttentionLibResultVo> attentionLibResultVoBaseResultEntityVo) {
                        if (attentionLibResultVoBaseResultEntityVo.status == 200) {
                            boolean result = attentionLibResultVoBaseResultEntityVo.data.status;
                            if (result) {
                                removeAttentionLib();

                                AttentionLibEvent attentionLibEvent = new AttentionLibEvent(true);
                                sRxBus.post(attentionLibEvent);
                            }
                            return result;
                        } else {
                            if (attentionLibResultVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(attentionLibResultVoBaseResultEntityVo.data.errorCode,
                                    attentionLibResultVoBaseResultEntityVo.data.message);
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    /**
     * 清除缓存
     */
    public void clearCacheData() {
        if (mTempLibBannerList != null) {
            mTempLibBannerList.clear();
        }
        mLibraryBean = null;
    }


    /**
     * 获取本地图书馆缓存信息
     *
     * @return
     */
    public Observable<LibraryBean> getLocalLibraryInfo() {
        return Observable.create(new Observable.OnSubscribe<LibraryBean>() {
            @Override
            public void call(Subscriber<? super LibraryBean> subscriber) {
                String modelMenuStrJson = SharedPreferencesUtil.getInstance().getString(ATTENTION_LIB_MENU_LIST);
                if (modelMenuStrJson != null) {
                    Gson gson = new Gson();
                    LibraryBean libraryBean = new LibraryBean();
                    if (!TextUtils.isEmpty(modelMenuStrJson)) {
                        libraryBean.mMenuItemList = gson.fromJson(modelMenuStrJson, new TypeToken<List<ModelMenu>>() {
                        }.getType());
                    }
                    String libInfoStrJson = SharedPreferencesUtil.getInstance().getString(ATTENTION_LIB_BASE_INFO);
                    if (!TextUtils.isEmpty(libInfoStrJson)) {
                        libraryBean.mLibrary = gson.fromJson(libInfoStrJson, new TypeToken<Library>() {
                        }.getType());
                        //当前经纬度计算当前到图书馆距离
                        if (libraryBean.mLibrary.mLngLat != null && libraryBean.mLibrary.mLngLat.contains(",")) {
                            String[] libLngLat = libraryBean.mLibrary.mLngLat.split(",");
                            String[] userLngLat = LocationManager.getInstance().getLngLat().split(",");
                            double distance = MapUtils.getDistance(Double.parseDouble(userLngLat[1]), Double.parseDouble(userLngLat[0]), Double.parseDouble(libLngLat[1]), Double.parseDouble(libLngLat[0]));
                            libraryBean.mDistance = (int) distance;
                        }
                        //设置当前是图书馆还是书店
                        libraryBean.mIsBookStore = libraryBean.mLibrary.mLevelName != null && (libraryBean.mLibrary.mLevelName.equals("书店") || libraryBean.mLibrary.mLevelName.equals("共享书屋"));
                    }
                    String libBookStrJson = SharedPreferencesUtil.getInstance().getString(ATTENTION_LIB_BOOK_LIST);
                    if (!TextUtils.isEmpty(libBookStrJson)) {
                        libraryBean.mBookBeanList = gson.fromJson(libBookStrJson, new TypeToken<List<BookBean>>() {
                        }.getType());
                    }
                    String libEBookStrJson = SharedPreferencesUtil.getInstance().getString(ATTENTION_LIB_EBOOK_LIST);
                    if (!TextUtils.isEmpty(libEBookStrJson)) {
                        libraryBean.mEBookBeanList = gson.fromJson(libEBookStrJson, new TypeToken<List<EBookBean>>() {
                        }.getType());
                    }
                    String libVideoSetStrJson = SharedPreferencesUtil.getInstance().getString(ATTENTION_LIB_VIDEO_SET_LIST);
                    if (!TextUtils.isEmpty(libVideoSetStrJson)) {
                        libraryBean.mVideoSetBeanList = gson.fromJson(libVideoSetStrJson, new TypeToken<List<VideoSetBean>>() {
                        }.getType());
                    }
                    String libNewsStrJson = SharedPreferencesUtil.getInstance().getString(ATTENTION_LIB_NEWS_LIST);
                    if (!TextUtils.isEmpty(libNewsStrJson)) {
                        libraryBean.mInformationBeanList = gson.fromJson(libNewsStrJson, new TypeToken<List<InformationBean>>() {
                        }.getType());
                    }
                    String libActivityStrJson = SharedPreferencesUtil.getInstance().getString(ATTENTION_LIB_ACTIVITY_LIST);
                    if (!TextUtils.isEmpty(libActivityStrJson)) {
                        libraryBean.mActivityBeanList = gson.fromJson(libActivityStrJson, new TypeToken<List<ActionInfoBean>>() {
                        }.getType());
                    }
                    subscriber.onNext(libraryBean);
                } else {
                    subscriber.onNext(null);
                }
                subscriber.onCompleted();
            }
        });
    }

    /**
     * 关注馆是否书店
     *
     * @return
     */
    public Observable<Boolean> isAttentionBookStore() {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                Gson gson = new Gson();
                String libInfoStrJson = SharedPreferencesUtil.getInstance().getString(ATTENTION_LIB_BASE_INFO);
                if (!TextUtils.isEmpty(libInfoStrJson)) {
                    Library library = gson.fromJson(libInfoStrJson, new TypeToken<Library>() {
                    }.getType());
                    subscriber.onNext(library.mLevelName != null && (library.mLevelName.equals("书店") || library.mLevelName.equals("共享书屋")));
                } else {
                    subscriber.onNext(false);
                }
                subscriber.onCompleted();
            }
        });
    }

    /**
     * 获取留言板列表
     *
     * @param libCode 馆号
     * @param msgId   留言ID
     * @param pageNo  页码
     * @return
     */
    public Observable<InformationCommentDetailBean> getMessageBoardList(final String libCode, final long msgId, final int pageNo) {
        final String readerId;
        if (UserRepository.getInstance().isLogin()) {
            readerId = UserRepository.getInstance().getLoginReaderId();
        } else {
            readerId = null;
        }
        if (pageNo == 1) {
            if (msgId > -1) {
                return CloudLibraryApi.getInstance().getMessageBoardIndex(msgId)
                        .flatMap(new Func1<BaseResultEntityVo<CommentIndexVo>, Observable<InformationCommentDetailBean>>() {
                            @Override
                            public Observable<InformationCommentDetailBean> call(BaseResultEntityVo<CommentIndexVo> commentIndexVo) {
                                if (commentIndexVo.status == 200) {
                                    if (commentIndexVo.data != null && commentIndexVo.data.index >= 0) {
                                        return getMessageBoardList(libCode, readerId, pageNo, commentIndexVo.data.index);
                                    } else {
                                        return getMessageBoardList(libCode, readerId, pageNo, -1);
                                    }
                                } else {
                                    throw new ServerException(commentIndexVo.data.errorCode, "");
                                }
                            }
                        }).onErrorResumeNext(new HttpResultFunc<InformationCommentDetailBean>());
            } else {
                return getMessageBoardList(libCode, readerId, pageNo, -1)
                        .onErrorResumeNext(new HttpResultFunc<InformationCommentDetailBean>());
            }
        } else {
            return getMessageBoardList(libCode, readerId, pageNo, -1)
                    .onErrorResumeNext(new HttpResultFunc<InformationCommentDetailBean>());
        }
    }

    private Observable<InformationCommentDetailBean> getMessageBoardList(String libCode, String readerId, final int pageNo, final int targetIndex) {
        int pageCount = 20;
        if (targetIndex >= 0) {
            pageCount = (targetIndex / 20 + 1) * 20;
        }

        return CloudLibraryApi.getInstance().getMessageBoardList(libCode, readerId, pageNo, pageCount)
                .map(new Func1<BaseResultEntityVo<MessageBoardVo>, InformationCommentDetailBean>() {
                    @Override
                    public InformationCommentDetailBean call(BaseResultEntityVo<MessageBoardVo> baseResultEntityVo) {
                        if (baseResultEntityVo.status == 200) {
                            List<CommentBean> replyList = getMessageBoardList(baseResultEntityVo.data);
                            if (replyList != null && replyList.size() > 0) {
                                InformationCommentDetailBean informationCommentDetailBean = new InformationCommentDetailBean();
                                informationCommentDetailBean.mReplyCommentList = replyList;
                                informationCommentDetailBean.mTotalCount = baseResultEntityVo.data.totalCount;
                                informationCommentDetailBean.mTargetIndex = targetIndex;
                                if (targetIndex >= 0) {
                                    informationCommentDetailBean.mCurrentPage = targetIndex / 20 + 1;
                                } else {
                                    informationCommentDetailBean.mCurrentPage = pageNo;
                                }
                                return informationCommentDetailBean;
                            } else {
                                return null;
                            }
                        } else {
                            throw new ServerException(baseResultEntityVo.data.errorCode, "");
                        }
                    }
                });
    }

    /**
     * 获取回复列表数据
     *
     * @param data
     * @return
     */
    private List<CommentBean> getMessageBoardList(MessageBoardVo data) {
        if (null != data && null != data.resultList && data.resultList.size() > 0) {
            List<CommentBean> replyBeanList = new ArrayList<>();
            for (MessageBoardVo.MessageVo itemVo : data.resultList) {
                CommentBean commentBean = new CommentBean();
                commentBean.mCommentImage = ImageUrlUtils.getDownloadOriginalImagePath(itemVo.readerIcon);
                commentBean.mCommentName = !TextUtils.isEmpty(itemVo.nickName)
                        ? itemVo.nickName
                        : StringUtils.formatReaderNickName(itemVo.readerName, itemVo.readerGender);
                commentBean.mIsMan = itemVo.readerGender == 1;
                commentBean.mIsOwn = (itemVo.isOwn == 1 && UserRepository.getInstance().isLogin());

                commentBean.mId = itemVo.id;
                commentBean.mContent = itemVo.content;
                if (itemVo.imagePaths != null
                        && itemVo.imagePaths.size() > 0) {
                    commentBean.mImagePath = ImageUrlUtils.getDownloadOriginalImagePath(itemVo.imagePaths.get(0));
                }
                commentBean.mPublishTime = itemVo.createTime;
                commentBean.mIsPraised = itemVo.isPraise == 1;
                commentBean.mPraisedCount = itemVo.praiseCount;
                commentBean.mReplyCount = itemVo.replyCount;

                if (commentBean.mReplyCount > 0) {
                    commentBean.mReplyContentList = new ArrayList<>();
                    for (int i = 0; i < (itemVo.replies.size() > 2 ? 2 : itemVo.replies.size()); i++) {
                        MessageBoardReplyVo replyItemVo = itemVo.replies.get(i);
                        CommentBean replyCommentBean = new CommentBean();

                        replyCommentBean.mCommentName = !TextUtils.isEmpty(replyItemVo.replyNickName)
                                ? replyItemVo.replyNickName
                                : StringUtils.formatReaderNickName(replyItemVo.replyName, replyItemVo.replyGender);

                        replyCommentBean.mId = replyItemVo.id;
                        replyCommentBean.mContent = replyItemVo.content;
                        if (replyItemVo.imagePaths != null
                                && replyItemVo.imagePaths.size() > 0) {
                            replyCommentBean.mImagePath = ImageUrlUtils.getDownloadOriginalImagePath(replyItemVo.imagePaths.get(0));
                        }
                        replyCommentBean.mPublishTime = replyItemVo.createTime;
                        replyCommentBean.mIsPraised = replyItemVo.isPraise == 1;
                        replyCommentBean.mPraisedCount = replyItemVo.praiseCount;

                        if (replyItemVo.repliedType == 2) {
                            //2:被回复的是读者
                            replyCommentBean.mRepliedName = !TextUtils.isEmpty(replyItemVo.repliedNickName)
                                    ? replyItemVo.repliedNickName
                                    : StringUtils.formatReaderNickName(replyItemVo.repliedName, replyItemVo.replyGender);
                        } else {
                            //1:被回复的是平台
                            replyCommentBean.mRepliedName = replyItemVo.repliedName;
                        }

                        commentBean.mReplyContentList.add(replyCommentBean);
                    }
                }

                replyBeanList.add(commentBean);
            }
            return replyBeanList;
        } else {
            return null;
        }
    }

    public Observable<Boolean> delMsg(long msgId) {
        String readerId;
        if (UserRepository.getInstance().isLogin()) {
            readerId = UserRepository.getInstance().getLoginReaderId();
        } else {
            return Observable.create(new Observable.OnSubscribe<Boolean>() {
                @Override
                public void call(Subscriber<? super Boolean> subscriber) {
                    throw new ServerException(30100, "重新登录！");
                }
            }).onErrorResumeNext(new HttpResultFunc<Boolean>());
        }
        return CloudLibraryApi.getInstance().delOwnMsg(msgId, readerId)
                .map(new Func1<BaseResultEntityVo<BaseDataResultVo>, Boolean>() {
                    @Override
                    public Boolean call(BaseResultEntityVo<BaseDataResultVo> baseDataResultVoBaseResultEntityVo) {
                        if (baseDataResultVoBaseResultEntityVo.status == 200) {
                            return true;
                        } else {
                            if (baseDataResultVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(baseDataResultVoBaseResultEntityVo.data.errorCode, "");
                        }
                    }
                })
                .onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    /**
     * 留言板点赞
     *
     * @param msgId 留言ID
     * @return true表示点赞成功
     */
    public Observable<Boolean> messagePraise(long msgId) {
        long readerId = 0;
        if (UserRepository.getInstance().isLogin()) {
            readerId = Long.valueOf(UserRepository.getInstance().getLoginReaderId());
        }
        return CloudLibraryApi.getInstance().messagePraise(msgId, readerId)
                .map(new Func1<BaseResultEntityVo<BaseDataResultVo>, Boolean>() {
                    @Override
                    public Boolean call(BaseResultEntityVo<BaseDataResultVo> baseDataResultVoBaseResultEntityVo) {
                        if (baseDataResultVoBaseResultEntityVo.status == 200) {
                            return true;
                        } else {
                            if (baseDataResultVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(baseDataResultVoBaseResultEntityVo.data.errorCode, "");
                        }
                    }
                })
                .onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    /**
     * 留言板回复留言
     *
     * @param libCode 馆号
     * @param msgId   留言ID
     * @param content 内容
     * @return 反复内容
     */
    public Observable<CommentBean> replyMessage(String libCode, long msgId, String content) {
        if (!UserRepository.getInstance().isLogin()) {
            return Observable.create(new Observable.OnSubscribe<CommentBean>() {
                @Override
                public void call(Subscriber<? super CommentBean> subscriber) {
                    throw new ServerException(30100, "重新登录！");
                }
            }).onErrorResumeNext(new HttpResultFunc<CommentBean>());
        }
        String readerId = UserRepository.getInstance().getLoginReaderId();
        return CloudLibraryApi.getInstance().replyMessageBoard(readerId, msgId, -1, libCode, content)
                .map(new Func1<BaseResultEntityVo<MessageBoardReplyVo>, CommentBean>() {
                    @Override
                    public CommentBean call(BaseResultEntityVo<MessageBoardReplyVo> messageBoardReplyVoBaseResultEntityVo) {
                        if (messageBoardReplyVoBaseResultEntityVo.status == 200) {
                            if (null != messageBoardReplyVoBaseResultEntityVo.data) {
                                CommentBean childBean = new CommentBean();
                                MessageBoardReplyVo replyItem = messageBoardReplyVoBaseResultEntityVo.data;
                                childBean.mContent = replyItem.content;
                                if (replyItem.replyType == 2) {
                                    //2:读者的回复
                                    childBean.mCommentName = !TextUtils.isEmpty(replyItem.replyNickName)
                                            ? replyItem.replyNickName
                                            : StringUtils.formatReaderNickName(replyItem.replyName, replyItem.replyGender);
                                } else {
                                    //1:平台的回复
                                    childBean.mCommentName = replyItem.replyName;
                                }
                                if (replyItem.repliedType == 2) {
                                    //2:被回复的是读者
                                    childBean.mRepliedName = !TextUtils.isEmpty(replyItem.repliedNickName)
                                            ? replyItem.repliedNickName
                                            : StringUtils.formatReaderNickName(replyItem.repliedName, replyItem.repliedGender);
                                } else {
                                    //1:被回复的是平台
                                    childBean.mRepliedName = replyItem.repliedName;
                                }
                                return childBean;
                            } else {
                                return null;
                            }
                        } else {
                            if (messageBoardReplyVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(messageBoardReplyVoBaseResultEntityVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<CommentBean>());
    }

    /**
     * 回复留言接口
     *
     * @param readerId  读者ID
     * @param messageId 留言ID
     * @param replyId   回复ID
     * @param content   内容
     * @return
     */
    public Observable<Void> replyMessageBoard(String readerId, long messageId, long replyId, String libCode, String content) {
        return CloudLibraryApi.getInstance().replyMessageBoard(readerId, messageId, replyId, libCode, content)
                .map(new Func1<BaseResultEntityVo<MessageBoardReplyVo>, Void>() {
                    @Override
                    public Void call(BaseResultEntityVo<MessageBoardReplyVo> messageBoardReplyVo) {
                        if (messageBoardReplyVo.status != 200) {
                            if (messageBoardReplyVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(messageBoardReplyVo.data.errorCode, "");
                        }
                        return null;
                    }
                }).onErrorResumeNext(new HttpResultFunc<Void>());
    }

    /**
     * 获取留言板详情列表
     *
     * @param messageId 留言ID
     * @param replyId   回复ID
     * @param pageNo    页码
     * @return
     */
    public Observable<InformationCommentDetailBean> getMessageBoardCommentList(final long messageId, final long replyId, final int pageNo) {
        if (pageNo == 1) {
            if (replyId > 0) {
                return CloudLibraryApi.getInstance().getMessageBoardReplyIndex(replyId)
                        .flatMap(new Func1<BaseResultEntityVo<CommentIndexVo>, Observable<InformationCommentDetailBean>>() {
                            @Override
                            public Observable<InformationCommentDetailBean> call(BaseResultEntityVo<CommentIndexVo> commentIndexVo) {
                                if (commentIndexVo.status == 200) {
                                    if (commentIndexVo.data != null && commentIndexVo.data.index >= 0) {
                                        return getMessageBoardCommentList(messageId, commentIndexVo.data.index, pageNo);
                                    } else {
                                        return getMessageBoardCommentList(messageId, -1, pageNo);
                                    }
                                } else {
                                    throw new ServerException(commentIndexVo.data.errorCode, "");
                                }
                            }
                        }).onErrorResumeNext(new HttpResultFunc<InformationCommentDetailBean>());
            } else {
                return getMessageBoardCommentList(messageId, -1, pageNo)
                        .onErrorResumeNext(new HttpResultFunc<InformationCommentDetailBean>());
            }
        } else {
            String readerId = UserRepository.getInstance().getLoginReaderId();
            //String identity = Installation.id(CloudLibraryApplication.getAppContext());
            return CloudLibraryApi.getInstance().getMessageBoardDetailReplyList(messageId, readerId, pageNo, 20)
                    .map(new Func1<BaseResultEntityVo<MessageBoardReplyListVo>, InformationCommentDetailBean>() {
                        @Override
                        public InformationCommentDetailBean call(BaseResultEntityVo<MessageBoardReplyListVo> messageBoardReplyListVo) {
                            if (messageBoardReplyListVo.status == 200) {
                                List<CommentBean> replyList = formatReplyList(messageBoardReplyListVo.data);
                                if (replyList != null) {
                                    InformationCommentDetailBean detailBean = new InformationCommentDetailBean();
                                    detailBean.mReplyCommentList = replyList;
                                    detailBean.mTotalCount = messageBoardReplyListVo.data.totalCount;
                                    return detailBean;
                                } else {
                                    return null;
                                }
                            } else {
                                if (messageBoardReplyListVo.data.errorCode == 30100) {
                                    UserRepository.getInstance().logout();
                                }
                                throw new ServerException(messageBoardReplyListVo.data.errorCode, "");
                            }
                        }
                    }).onErrorResumeNext(new HttpResultFunc<InformationCommentDetailBean>());
        }
    }

    private Observable<InformationCommentDetailBean> getMessageBoardCommentList(long messageId, final int targetIndex, final int pageNo) {
        String readerId = UserRepository.getInstance().getLoginReaderId();
        //String identity = Installation.id(CloudLibraryApplication.getAppContext());

        int pageCount = 20;
        if (targetIndex >= 0) {
            pageCount = (targetIndex / 20 + 1) * 20;
        }
        return Observable.zip(CloudLibraryApi.getInstance().getMessageBoardDetail(messageId, readerId),
                CloudLibraryApi.getInstance().getMessageBoardDetailReplyList(messageId, readerId, pageNo, pageCount),
                new Func2<BaseResultEntityVo<MessageBoardDetailVo>, BaseResultEntityVo<MessageBoardReplyListVo>, InformationCommentDetailBean>() {
                    @Override
                    public InformationCommentDetailBean call(BaseResultEntityVo<MessageBoardDetailVo> baseResultEntityVo, BaseResultEntityVo<MessageBoardReplyListVo> listBaseResultEntityVo) {
                        if (baseResultEntityVo.status == 200 && listBaseResultEntityVo.status == 200) {
                            //添加
                            List<CommentBean> beanList = new ArrayList<>();
                            CommentBean replyBean = new CommentBean();
                            replyBean.mCommentName = !TextUtils.isEmpty(baseResultEntityVo.data.nickName)
                                    ? baseResultEntityVo.data.nickName
                                    : StringUtils.formatReaderNickName(baseResultEntityVo.data.readerName, baseResultEntityVo.data.readerGender);

                            replyBean.mId = baseResultEntityVo.data.id;
                            replyBean.mPublishTime = baseResultEntityVo.data.createTime;
                            replyBean.mContent = baseResultEntityVo.data.content;
                            replyBean.mCommentImage = ImageUrlUtils.getDownloadOriginalImagePath(baseResultEntityVo.data.readerIcon);
                            replyBean.mIsPraised = baseResultEntityVo.data.isPraise == 1;
                            replyBean.mIsOwn = baseResultEntityVo.data.isOwn == 1 && UserRepository.getInstance().isLogin();
                            replyBean.mPraisedCount = baseResultEntityVo.data.praiseCount;
                            replyBean.mReplyCount = listBaseResultEntityVo.data != null ? listBaseResultEntityVo.data.totalCount : 0;

                            if (baseResultEntityVo.data.imagePaths != null
                                    && baseResultEntityVo.data.imagePaths.size() > 0) {
                                replyBean.mImagePath = ImageUrlUtils.getDownloadOriginalImagePath(baseResultEntityVo.data.imagePaths.get(0));
                            }
                            beanList.add(replyBean);
                            List<CommentBean> replyList = formatReplyList(listBaseResultEntityVo.data);

                            if (replyList != null) {
                                beanList.addAll(replyList);
                            }

                            InformationCommentDetailBean detailBean = new InformationCommentDetailBean();
                            detailBean.mReplyCommentList = beanList;
                            detailBean.mTotalCount = listBaseResultEntityVo.data.totalCount;
                            detailBean.mTargetIndex = targetIndex;
                            if (targetIndex >= 0) {
                                detailBean.mCurrentPage = targetIndex / 20 + 1;
                            } else {
                                detailBean.mCurrentPage = pageNo;
                            }
                            return detailBean;
                        } else if (baseResultEntityVo.status != 200) {
                            if (baseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(baseResultEntityVo.data.errorCode, "");
                        } else {
                            if (listBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(listBaseResultEntityVo.data.errorCode, "");
                        }
                    }
                });
    }


    /**
     * 格式化回复列表
     *
     * @param data
     * @return
     */
    private List<CommentBean> formatReplyList(MessageBoardReplyListVo data) {
        if (data != null && data.resultList != null && data.resultList.size() > 0) {
            List<CommentBean> beanList = new ArrayList<>();
            for (MessageBoardReplyVo itemVo : data.resultList) {
                CommentBean replyBean = new CommentBean();
                replyBean.mCommentImage = ImageUrlUtils.getDownloadOriginalImagePath(itemVo.replyImage);
                replyBean.mIsMan = itemVo.replyGender == 1;
                replyBean.mIsOwn = (itemVo.isOwn == 1 && UserRepository.getInstance().isLogin());

                replyBean.mId = itemVo.id;
                replyBean.mContent = itemVo.content;
                replyBean.mPublishTime = itemVo.createTime;
                replyBean.mIsPraised = itemVo.isPraise == 1;
                replyBean.mPraisedCount = itemVo.praiseCount;
                //回复者名称
                String replyName;
                if (itemVo.replyType == 2) {
                    //2:读者的回复
                    replyName = !TextUtils.isEmpty(itemVo.replyNickName)
                            ? itemVo.replyNickName
                            : StringUtils.formatReaderNickName(itemVo.replyName, itemVo.replyGender);
                } else {
                    //1:平台的回复
                    replyName = itemVo.replyName;
                }
                replyBean.mCommentName = replyName;
                //被回复者名称
                String repliedName;
                if (itemVo.repliedType == 2) {
                    //2:被回复的是读者
                    repliedName = !TextUtils.isEmpty(itemVo.repliedNickName)
                            ? itemVo.repliedNickName
                            : StringUtils.formatReaderNickName(itemVo.repliedName, itemVo.repliedGender);
                } else {
                    //1:被回复的是平台
                    repliedName = itemVo.repliedName;
                }
                replyBean.mRepliedName = repliedName;
                beanList.add(replyBean);
            }
            return beanList;
        } else {
            return null;
        }
    }

    /**
     * 删除自己的留言
     *
     * @param msgId    留言
     * @param readerId 读者ID
     * @return
     */
    public Observable<Boolean> delOwnMsg(long msgId, String readerId) {
        return CloudLibraryApi.getInstance().delOwnMsg(msgId, readerId)
                .map(new Func1<BaseResultEntityVo<BaseDataResultVo>, Boolean>() {
                    @Override
                    public Boolean call(BaseResultEntityVo<BaseDataResultVo> baseDataResultVo) {
                        if (baseDataResultVo.status != 200) {
                            if (baseDataResultVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(baseDataResultVo.data.errorCode, "");
                        } else {
                            return null;
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    /**
     * 删除自己的留言回复消息
     *
     * @param replyId  回复ID
     * @param readerId 读者ID
     * @return
     */
    public Observable<Boolean> delOwnReplyMsg(long replyId, String readerId) {
        return CloudLibraryApi.getInstance().delOwnReplyMsg(replyId, readerId)
                .map(new Func1<BaseResultEntityVo<BaseDataResultVo>, Boolean>() {
                    @Override
                    public Boolean call(BaseResultEntityVo<BaseDataResultVo> baseDataResultVo) {
                        if (baseDataResultVo.status != 200) {
                            if (baseDataResultVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(baseDataResultVo.data.errorCode, "");
                        } else {
                            return null;
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    /**
     * 图书馆留言点赞
     *
     * @param replyId  回复ID
     * @param readerId 读者ID
     * @return
     */
    public Observable<Boolean> replyMsgPraise(long replyId, long readerId) {
        return CloudLibraryApi.getInstance().replyMsgPraise(replyId, readerId)
                .map(new Func1<BaseResultEntityVo<BaseDataResultVo>, Boolean>() {
                    @Override
                    public Boolean call(BaseResultEntityVo<BaseDataResultVo> baseDataResultVo) {
                        if (baseDataResultVo.status == 200) {
                            return true;
                        } else {
                            throw new ServerException(baseDataResultVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    public Observable<BaseListResultData<LibraryBean>> getBookBelongLib(String isbn, String lngLat, final String libCode) {
        return CloudLibraryApi.getInstance().getBookBelongLib(isbn, lngLat)
                .map(new Func1<BaseResultEntityVo<List<BookBelongLibVo>>, BaseListResultData<LibraryBean>>() {
                    @Override
                    public BaseListResultData<LibraryBean> call(BaseResultEntityVo<List<BookBelongLibVo>> listBaseResultEntityVo) {
                        if (listBaseResultEntityVo.status == 200) {
                            List<LibraryBean> libraryBeanList = new ArrayList<>();
                            for (BookBelongLibVo item : listBaseResultEntityVo.data) {
                                //1.图书馆item进入有libCode,如果当前有libCode则移除当前馆的libCode
                                if (!TextUtils.isEmpty(libCode) && libCode.equals(item.libCode)) {
                                    continue;
                                }
                                LibraryBean bean = new LibraryBean();
                                bean.mLibrary.mId = item.libId;
                                bean.mLibrary.mName = item.libName;
                                bean.mLibrary.mAddress = item.address;
                                bean.mLibrary.mCode = item.libCode;
//            bean.mLibrary.mLighten = item.lighten;
                                bean.mLibrary.mLngLat = item.lngLat;
                                bean.mLibrary.mInLib = String.valueOf(item.inLib);
                                bean.mLibrary.mOutLib = String.valueOf(item.outLib);
                                bean.mLibrary.mLogo = ImageUrlUtils.getDownloadOriginalImagePath(item.logo);
                                bean.mLibrary.mBookCount = item.bookNum;
                                bean.mLibrary.mHeatCount = item.hotTip;
                                bean.mIsOpen = StringUtils.libraryIsOpen(item.lightTime, item.serviceTime);
                                bean.mDistance = item.distance;
                                libraryBeanList.add(bean);
                            }
                            if (libraryBeanList.size() > 0) {
                                BaseListResultData<LibraryBean> resultData = new BaseListResultData<>();
                                resultData.mResultList = libraryBeanList;
                                resultData.mTotalCount = libraryBeanList.size();
                                return resultData;
                            } else {
                                return null;
                            }
                        } else {
                            return null;
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<BaseListResultData<LibraryBean>>());
    }
}
