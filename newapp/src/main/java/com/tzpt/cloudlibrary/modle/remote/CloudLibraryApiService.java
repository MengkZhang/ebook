package com.tzpt.cloudlibrary.modle.remote;

import android.support.v4.util.ArrayMap;

import com.tzpt.cloudlibrary.modle.remote.newpojo.ActionListItemVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.ActionListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BannerDataListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BannerNewListItemVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseDataResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseResultEntityVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BookBelongLibVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BookCategoryVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BookDetailInfoNewVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BookInLibListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BookListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BookStoreListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.CityVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.ClassifyTwoLevelVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.DistrictVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.EBookCategoryVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.EBookDetailInfoVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.EBookListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.EBookRecommendationsListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.HomeInfoVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.HtmlUrlVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.LastDistrictVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.LibInfoVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.LibIntroduceVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.LibListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.LibraryDetailListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.LibraryModelVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.LoginInfoVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.MainBranchLibraryCountVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.MainBranchLibraryVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.MessageBoardVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.NewsDetailVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.NewsDiscussListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.NewsListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.ProvinceVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.RankingBookListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.RankingHomeVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.SearchHotResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.ServerTimeVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.VersionUpdateVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.VideoBelongLibVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.VideoCatalogVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.VideoDetailVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.VideoSearchListVo;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by Administrator on 2017/5/22.
 */

public interface CloudLibraryApiService {
    @GET("userApp/libraryBook/bookInfo")
    Observable<BaseResultEntityVo<BookDetailInfoNewVo>> getBookDetail(@Query("isbn") String isbn,
                                                                      @Query("identity") String identity,
                                                                      @Query("visitType") int visitType,
                                                                      @Query("fromSearch") int fromSearch,
                                                                      @Query("libCode") String libCode);

    @GET("userApp/version/lastest")
    Observable<BaseResultEntityVo<VersionUpdateVo>> checkVersion(@Query("version") String version, @Query("deviceType") int deviceType);

    @GET("userApp/advertising/getByArea")
    Observable<BaseResultEntityVo<BannerDataListVo>> getLauncherBanner(@Query("locationCode") String locationCode);

    @GET("userApp/libraryBook/bookStayLibraryInfo")
    Observable<BaseResultEntityVo<List<BookBelongLibVo>>> getBookBelongLib(@Query("isbn") String isbn, @Query("lngLat") String lngLat);


    @GET("userApp/libraryBook/weeklyHot")
    Observable<BaseResultEntityVo<BookListVo>> getHotBookList(@QueryMap Map<String, String> map);

    @GET("userApp/libraryBook/lastestCatalogue")
    Observable<BaseResultEntityVo<BookListVo>> getNewBookList(@QueryMap Map<String, String> map);

    @GET("userApp/libraryBook/search")
    Observable<BaseResultEntityVo<BookListVo>> getSearchBookList(@QueryMap Map<String, String> map);

    @GET("userApp/libraryBook/{libCode}")
    Observable<BaseResultEntityVo<BookListVo>> getLibrarySearchBookList(@Path("libCode") String libCode, @QueryMap Map<String, String> map);

    @GET("userApp/rankingList/homeRankingNew")
    Observable<BaseResultEntityVo<RankingHomeVo>> getRankingList();

    @GET("userApp/rankingList/borrowRankingNew")
    Observable<BaseResultEntityVo<RankingBookListVo>> getRankingBorrowList(@QueryMap ArrayMap<String, Object> parameterMap);

    @GET("userApp/rankingList/recommendRankingNew")
    Observable<BaseResultEntityVo<RankingBookListVo>> getRankingRecommendList(@QueryMap ArrayMap<String, Object> parameterMap);

    @GET("userApp/rankingList/praiseRankingNew")
    Observable<BaseResultEntityVo<RankingBookListVo>> getRankingPraiseList(@QueryMap ArrayMap<String, Object> parameterMap);

    @GET("userApp/libraryBook/category")
    Observable<BaseResultEntityVo<List<BookCategoryVo>>> getBookCategory();

    @GET("userApp/news/homeNews")
    Observable<BaseResultEntityVo<List<BannerNewListItemVo>>> getNewsList(@Query("locationCode") String locationCode);

    @GET("userApp/library/list")
    Observable<BaseResultEntityVo<LibListVo>> getLibraryList(@QueryMap Map<String, Object> map);

    @GET("userApp/library/branchLibrary")
    Observable<BaseResultEntityVo<LibListVo>> getBranchLibraryList(@Query("pageNo") int pageNum,
                                                                   @Query("pageCount") int pageCount,
                                                                   @Query("libCode") String libCode,
                                                                   @Query("lngLat") String lngLat);

    @GET("userApp/library/supLibrary")
    Observable<BaseResultEntityVo<LibListVo>> getSupLibraryList(@Query("pageNo") int pageNum,
                                                                @Query("pageCount") int pageCount,
                                                                @Query("libCode") String libCode,
                                                                @Query("lngLat") String lngLat);

    @GET("userApp/library/number")
    Observable<BaseResultEntityVo<LibInfoVo>> getLibraryNumber(@Query("libCode") String hallCode);

    @GET("/userApp/ebook/search")
    Observable<BaseResultEntityVo<EBookListVo>> getEBookList(@QueryMap Map<String, String> map);

    @GET("userApp/ebook/{libCode}")
    Observable<BaseResultEntityVo<EBookListVo>> getLibraryEBookList(@Path("libCode") String libCode, @QueryMap Map<String, String> map);

    @GET("userApp/ebook/weeklyHot")
    Observable<BaseResultEntityVo<EBookListVo>> getHotEBookList(@QueryMap Map<String, String> map);

    @GET("userApp/ebook/recommendGoodEBooks")
    Observable<BaseResultEntityVo<EBookRecommendationsListVo>> getRecommendationsEBookList(@QueryMap Map<String, String> map);

    @GET("userApp/ebook/lastestCatalogue")
    Observable<BaseResultEntityVo<EBookListVo>> getNewEBookList(@QueryMap Map<String, String> map);


    @GET("userApp/libraryBook/bookStayLibraryAllInfo")
    Observable<BaseResultEntityVo<BookInLibListVo>> getBookDetailSameBookList(@Query("isbn") String isbn,
                                                                              @Query("libCode") String libCode);

    @GET("userApp/library/info")
    Observable<BaseResultEntityVo<LibIntroduceVo>> getLibIntroduce(@QueryMap Map<String, Object> map);

    @GET("userApp/ebook/info")
    Observable<BaseResultEntityVo<EBookDetailInfoVo>> getEBookDetail(@Query("id") String id,
                                                                     @Query("libCode") String libCode,
                                                                     @Query("identity") String identity,
                                                                     @Query("visitType") int visitType,
                                                                     @Query("fromSearch") int fromSearch);

    @GET("userApp/ebook/belongLibrary")
    Observable<BaseResultEntityVo<BookBelongLibVo>> getEBookBelongLib(@Query("ebookId") String ebookId,
                                                                      @Query("lngLat") String lngLat,
                                                                      @Query("readerId") String readerId);

    @GET("userApp/news/newsList/region")
    Observable<BaseResultEntityVo<NewsListVo>> getInformationList(@QueryMap Map<String, String> map);

    @GET("userApp/news/newsList/{libCode}")
    Observable<BaseResultEntityVo<NewsListVo>> getLibInformationList(@Path("libCode") String libCode, @QueryMap Map<String, String> map);

    @GET("userApp/news/commentList")
    Observable<BaseResultEntityVo<NewsDiscussListVo>> getNewsDiscussList(@QueryMap Map<String, Object> map);

    @GET("userApp/activity/activities/region")
    Observable<BaseResultEntityVo<ActionListVo>> getOurReadersList(@QueryMap ArrayMap<String, Object> parameterMap);

    @GET("userApp/activity/info")
    Observable<BaseResultEntityVo<ActionListItemVo>> getActionDetailInfo(@Query("activityId") int activityId,
                                                                         @Query("fromSearch") int fromSearch,
                                                                         @Query("idCard") String idCard,
                                                                         @Query("keyword") String keyword,
                                                                         @Query("libCode") String libCode);

    @GET("userApp/area/province")
    Observable<BaseResultEntityVo<List<ProvinceVo>>> getProvinceList();

    @GET("userApp/area/city")
    Observable<BaseResultEntityVo<List<CityVo>>> getCityList(@Query("provinceCode") String code);

    @GET("userApp/area/area")
    Observable<BaseResultEntityVo<List<DistrictVo>>> getDistrictList(@Query("cityCode") String cityCode);

    @GET("userApp/area/lastLevel")
    Observable<BaseResultEntityVo<LastDistrictVo>> getLastDistrictList(@Query("locationCode") String locationCode);

    @GET("userApp/library/level")
    Observable<BaseResultEntityVo<List<String>>> getLibraryLevel();

    @GET("userApp/news/info")
    Observable<BaseResultEntityVo<NewsDetailVo>> getNewsDetailId(@Query("newsId") long newsId,
                                                                 @Query("identity") String identity,
                                                                 @Query("fromSearch") int fromSearch);

    @POST("userApp/ebook/addEbookReadRecord")
    Observable<BaseResultEntityVo<BaseDataResultVo>> reportEBookRead(@Body RequestBody body);

    @POST("userApp/reader/login")
    Observable<BaseResultEntityVo<LoginInfoVo>> login(@Body RequestBody body);

    @GET("userApp/hotSearch/start")
    Observable<BaseResultEntityVo<List<SearchHotResultVo>>> getHotSearchList(@Query("libCode") String libCode,
                                                                             @Query("type") String type);

    @GET("userApp/library/messageList")
    Observable<BaseResultEntityVo<MessageBoardVo>> getMessageBoardList(@QueryMap ArrayMap<String, Object> map);

    @GET("userApp/sms/registerSend")
    Observable<BaseResultEntityVo<BaseDataResultVo>> sendPhoneVerifyCode(@Query("phone") String phone);

    @GET("userApp/reader/checkCode")
    Observable<BaseResultEntityVo<BaseDataResultVo>> checkMsgCode(@Query("phone") String phone, @Query("code") String code);

    @GET("userApp/reader/checkIdCardAndNickName")
    Observable<BaseResultEntityVo<BaseDataResultVo>> checkReaderIdInfo(@Query("idCard") String idCard, @Query("nickName") String nickName);

    @POST("userApp/reader/register")
    Observable<BaseResultEntityVo<BaseDataResultVo>> readerRegister(@Body ArrayMap<String, Object> parameterMap);

    @GET("userApp/rankingList/ebookRankingNew")
    Observable<BaseResultEntityVo<EBookListVo>> getEBookRankingList(@QueryMap Map<String, String> parameterMap);

    //视频二级分类接口
    @GET("userApp/video/categories")
    Observable<BaseResultEntityVo<List<ClassifyTwoLevelVo>>> getVideoGradeList();

    @GET("userApp/video/latestWeekHot")
    Observable<BaseResultEntityVo<VideoSearchListVo>> getHotVideoSetList(@QueryMap ArrayMap<String, Object> parameterMap);

    @GET("userApp/video/latestCatalogue")
    Observable<BaseResultEntityVo<VideoSearchListVo>> getNewVideoSetList(@QueryMap ArrayMap<String, Object> parameterMap);

    @GET("userApp/video/directories/{videosId}")
    Observable<BaseResultEntityVo<List<VideoCatalogVo>>> getVideoCatalogList(@Path("videosId") long videosId);

    @GET("userApp/video/searchByKeywords")
    Observable<BaseResultEntityVo<VideoSearchListVo>> searchVideoSetList(@QueryMap ArrayMap<String, Object> parameterMap);

    @GET("userApp/video/{videosId}")
    Observable<BaseResultEntityVo<VideoDetailVo>> getVideoSetDetail(@Path("videosId") long videosId);

    @POST("userApp/hotSearch/saveSearchBrowseRecord")
    Observable<BaseResultEntityVo> saveSearchBrowseRecord(@Body ArrayMap<String, Object> parameterMap);

    @GET("userApp/video/belongLibrary/{lanLat}/{videosId}/{readerId}")
    Observable<BaseResultEntityVo<VideoBelongLibVo>> getVideoBelongLibrary(@Path("lanLat") String lanLat, @Path("videosId") long videosId, @Path("readerId") String readerId);

    @GET("userApp/news/libBannerNews/{libCode}")
    Observable<BaseResultEntityVo<List<BannerNewListItemVo>>> getLibBannerNewsList(@Path("libCode") String libCode, @Query("locationCode") String locationCode);

    @GET("userApp/libraryModel/page/{libCode}")
    Observable<BaseResultEntityVo<List<LibraryModelVo>>> getLibModelList(@Path("libCode") String libCode);

    @GET("userApp/library/getAllLibrary")
    Observable<BaseResultEntityVo<MainBranchLibraryVo>> getAllLibrary(@QueryMap ArrayMap<String, Object> arrayMap);

    @GET("userApp/library/findLibCount/{libCode}")
    Observable<BaseResultEntityVo<MainBranchLibraryCountVo>> getMainBranchLibraryCount(@Path("libCode") String libCode);

    @GET("userApp/activity/activities/{libCode}")
    Observable<BaseResultEntityVo<ActionListVo>> getLibActionList(@Path("libCode") String libCode, @QueryMap ArrayMap<String, Object> parameterMap);

    @GET("userApp/video/searchByKeywordsAndHallCode/{libCode}")
    Observable<BaseResultEntityVo<VideoSearchListVo>> getLibVideoList(@Path("libCode") String libCode, @QueryMap ArrayMap<String, Object> parameterMap);

    @GET("userApp/common/getTime")
    Observable<ServerTimeVo> getServerTime();

    @GET("userApp/library/instruction/html/{libCode}")
    Observable<BaseResultEntityVo<HtmlUrlVo>> getBorrowingIntroduces(@Path("libCode") String libCode);

    @GET("userApp/ebook/category/all")
    Observable<BaseResultEntityVo<List<EBookCategoryVo>>> getEBookCategory();

    @GET("userApp/home/getDataNew")
    Observable<BaseResultEntityVo<HomeInfoVo>> getHomeInfo(@Query("locationCode") String locationCode, @Query("lngLat") String lngLat);

    @POST("userApp/share/{mappingId}/{type}")
    Observable<BaseResultEntityVo> reportBookShare(@Path("mappingId") String mappingId, @Path("type") int type);

    @GET("userApp/library/getDataNew")
    Observable<BaseResultEntityVo<LibraryDetailListVo>> getLibResourcesList(@Query("libCode") String libCode);

    //图书馆书店使用同一个接口
    @GET("userApp/library/list")
    Observable<BaseResultEntityVo<BookStoreListVo>> getBookStoreList(@QueryMap ArrayMap<String, Object> map);

}
