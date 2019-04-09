package com.tzpt.cloudlibrary.modle.remote;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tzpt.cloudlibrary.CloudLibraryApplication;
import com.tzpt.cloudlibrary.Installation;
import com.tzpt.cloudlibrary.modle.remote.newpojo.*;
import com.tzpt.cloudlibrary.modle.remote.support.HeaderInterceptor;
import com.tzpt.cloudlibrary.modle.remote.support.UserApiHeaderInterceptor;
import com.tzpt.cloudlibrary.utils.MD5Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * 网络数据接口API
 * Created by Administrator on 2017/5/31.
 */

public class CloudLibraryApi {
    private static CloudLibraryApi mInstance;
    private static final int TIMEOUT_CONNECT = 60;
    private static final int TIMEOUT_READ = 100;
    private CloudLibraryApiService mService;
    private CloudLibraryUserApiService mUserService;
    private static final MediaType mMediaType = MediaType.parse("application/json; charset=utf-8");

    /**
     * App组
     * 内部
     * SYKJ
     * private static final String BASE_URL = "http://119.23.205.178:8077/";
     * private static final String BASE_URL = "http://47.106.164.212:18081/";
     * private static final String BASE_URL = "http://m.ytsg.cn/"
     */

//    public static final String BASE_URL = "http://m.ytsg.cn/";
    public static final String BASE_URL = "http://47.106.164.212:18081/";


    public static CloudLibraryApi getInstance() {
        if (mInstance == null) {
            mInstance = new CloudLibraryApi();
        }
        return mInstance;
    }

    private CloudLibraryApi() {
        Gson gson = new GsonBuilder().setDateFormat("yyyyMMddHH:mm:ss'Z'").create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(createOkHttpClient())
                .build();
        mService = retrofit.create(CloudLibraryApiService.class);

        Retrofit userRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(createOkUserHttpClient())
                .build();
        mUserService = userRetrofit.create(CloudLibraryUserApiService.class);
    }

    private OkHttpClient createOkUserHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_CONNECT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_READ, TimeUnit.SECONDS)
                .addInterceptor(new UserApiHeaderInterceptor())
                .build();
    }

    private OkHttpClient createOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_CONNECT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_READ, TimeUnit.SECONDS)
                .addInterceptor(new HeaderInterceptor())
                .build();
    }

    public Observable<BaseResultEntityVo<VersionUpdateVo>> checkVersion(String version, int deviceType) {
        return mService.checkVersion(version, deviceType);
    }

    public Observable<BaseResultEntityVo<BannerDataListVo>> getLauncherBanner(String locationCode) {
        return mService.getLauncherBanner(locationCode);
    }

    public Observable<BaseResultEntityVo<BookDetailInfoNewVo>> getBookDetail(String isbn, String identity, int visitType, int fromSearch, String libCode) {
        return mService.getBookDetail(isbn, identity, visitType, fromSearch, libCode);
    }

    public Observable<BaseResultEntityVo<BookListVo>> getHotBookList(Map<String, String> parameters) {
        return mService.getHotBookList(parameters);
    }

    public Observable<BaseResultEntityVo<BookListVo>> getNewBookList(Map<String, String> parameters) {
        return mService.getNewBookList(parameters);
    }

    public Observable<BaseResultEntityVo<BookListVo>> getSearchBookList(Map<String, String> parameters) {
        return mService.getSearchBookList(parameters);
    }

    public Observable<BaseResultEntityVo<BookListVo>> getLibrarySearchBookList(String libCode, Map<String, String> parameters) {
        return mService.getLibrarySearchBookList(libCode, parameters);
    }

    public Observable<BaseResultEntityVo<RankingHomeVo>> getRankingList() {
        return mService.getRankingList();
    }

    public Observable<BaseResultEntityVo<RankingBookListVo>> getRankingBorrowList(int pageNum, int pageCount, int sortType, int categoryId) {
        ArrayMap<String, Object> parameterMap = new ArrayMap<>();
        parameterMap.put("pageNo", pageNum);
        parameterMap.put("pageCount", pageCount);
        parameterMap.put("sortType", sortType);
        if (categoryId > 0) {
            parameterMap.put("categoryId", categoryId);
        }
        return mService.getRankingBorrowList(parameterMap);
    }

    public Observable<BaseResultEntityVo<RankingBookListVo>> getRankingRecommendList(int pageNum, int pageCount, int sortType, int categoryId) {
        ArrayMap<String, Object> parameterMap = new ArrayMap<>();
        parameterMap.put("pageNo", pageNum);
        parameterMap.put("pageCount", pageCount);
        parameterMap.put("sortType", sortType);
        if (categoryId > 0) {
            parameterMap.put("categoryId", categoryId);
        }
        return mService.getRankingRecommendList(parameterMap);
    }

    public Observable<BaseResultEntityVo<RankingBookListVo>> getRankingPraiseList(int pageNum, int pageCount, int sortType, int categoryId) {
        ArrayMap<String, Object> parameterMap = new ArrayMap<>();
        parameterMap.put("pageNo", pageNum);
        parameterMap.put("pageCount", pageCount);
        parameterMap.put("sortType", sortType);
        if (categoryId > 0) {
            parameterMap.put("categoryId", categoryId);
        }
        return mService.getRankingPraiseList(parameterMap);
    }

    public Observable<BaseResultEntityVo<List<BannerNewListItemVo>>> getNewList(String locationCode) {
        return mService.getNewsList(locationCode);
    }

    public Observable<BaseResultEntityVo<List<BookCategoryVo>>> getBookCategory() {
        return mService.getBookCategory();
    }

    public Observable<BaseResultEntityVo<LibListVo>> getLibraryList(Map<String, Object> parameters) {
        return mService.getLibraryList(parameters);
    }

    public Observable<BaseResultEntityVo<LibInfoVo>> getLibraryNumber(String hallCode) {
        return mService.getLibraryNumber(hallCode);
    }

    public Observable<BaseResultEntityVo<EBookListVo>> getEBookList(Map<String, String> parameters) {
        return mService.getEBookList(parameters);
    }

    public Observable<BaseResultEntityVo<EBookListVo>> getLibraryEBookList(String libCode, Map<String, String> parameters) {
        return mService.getLibraryEBookList(libCode, parameters);
    }

    public Observable<BaseResultEntityVo<EBookListVo>> getHotEBookList(Map<String, String> parameters) {
        return mService.getHotEBookList(parameters);
    }

    public Observable<BaseResultEntityVo<EBookRecommendationsListVo>> getRecommendationsEBookList(Map<String, String> parameters) {
        return mService.getRecommendationsEBookList(parameters);
    }

    public Observable<BaseResultEntityVo<EBookListVo>> getNewEBookList(Map<String, String> parameters) {
        return mService.getNewEBookList(parameters);
    }

    public Observable<BaseResultEntityVo<List<BookBelongLibVo>>> getBookBelongLib(String isbn, String lngLat) {
        return mService.getBookBelongLib(isbn, lngLat);
    }

    public Observable<BaseResultEntityVo<BookInLibListVo>> getBookDetailSameBookList(String isbn,
                                                                                     String libCode) {
        return mService.getBookDetailSameBookList(isbn, libCode);
    }

    public Observable<BaseResultEntityVo<LibIntroduceVo>> getLibIntroduce(String lngLat, String libCode, int fromSearch, int flag) {
        ArrayMap<String, Object> map = new ArrayMap<>();
        map.put("lngLat", lngLat);
        map.put("libCode", libCode);
        map.put("fromSearch", fromSearch);
        //如果是搜索结果，则传如flag ,1图书馆 2书店
        if (fromSearch == 1) {
            map.put("flag", flag);
        }
        return mService.getLibIntroduce(map);
    }

    public Observable<BaseResultEntityVo<EBookDetailInfoVo>> getEBookDetail(String id, String libCode, String identity, int visitType, int fromSearch) {
        return mService.getEBookDetail(id, libCode, identity, visitType, fromSearch);
    }

    public Observable<BaseResultEntityVo<BookBelongLibVo>> getEBookBelongLib(String id, String lngLat, String readerId) {
        return mService.getEBookBelongLib(id, lngLat, readerId);
    }

    public Observable<BaseResultEntityVo<NewsListVo>> getInformationList(Map<String, String> parameters) {
        return mService.getInformationList(parameters);
    }

    public Observable<BaseResultEntityVo<NewsListVo>> getLibInformationList(String libCode, Map<String, String> parameters) {
        return mService.getLibInformationList(libCode, parameters);
    }

    public Observable<BaseResultEntityVo<NewsDiscussListVo>> getNewsDiscussList(Map<String, Object> parameters) {
        return mService.getNewsDiscussList(parameters);
    }

    public Observable<BaseResultEntityVo<NewsPraiseResultVo>> operateNewsPraise(String identity, long newsId) {
        return mUserService.operateNewsPraise(identity, newsId);
    }

    public Observable<BaseResultEntityVo<PublishDiscussResultVo>> publishNewsDiscuss(String content, int readerId, long newsId) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("content", content);
            requestData.put("id", readerId);
            requestData.put("newsId", newsId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mUserService.publishNewsDiscuss(body);
    }

    public Observable<BaseResultEntityVo<ActionListVo>> getOurReadersList(int pageNum,
                                                                          int pageCount,
                                                                          String keyword,
                                                                          String adCode) {
        ArrayMap<String, Object> parameterMap = new ArrayMap<>();
        parameterMap.put("pageNo", pageNum);
        parameterMap.put("pageCount", pageCount);
        parameterMap.put("locationCode", adCode);
        if (!TextUtils.isEmpty(keyword)) {
            parameterMap.put("keyword", keyword);
        }
        return mService.getOurReadersList(parameterMap);
    }

    public Observable<BaseResultEntityVo<ActionListItemVo>> getActionDetailInfo(int activityId,
                                                                                int fromSearch,
                                                                                String idCard,
                                                                                String keyword,
                                                                                String libCode) {
        return mService.getActionDetailInfo(activityId, fromSearch, keyword, idCard, libCode);
    }

    public Observable<BaseResultEntityVo<BaseDataResultVo>> reportEBookRead(String ebookId, String libCode, String peruser) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("ebookId", ebookId);
            requestData.put("libCode", libCode);
            requestData.put("peruser", peruser);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mService.reportEBookRead(body);
    }

    public Observable<BaseResultEntityVo<UserInfoVo>> getUserInfo(String idCard) {
        return mUserService.getUserInfo(idCard);
    }

    public Observable<BaseResultEntityVo<LoginInfoVo>> login(String phone, String idCard, String password) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("idCard", idCard);
            requestData.put("idPassword", password);
            requestData.put("phone", phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mService.login(body);
    }

    public Observable<BaseResultEntityVo<UserHeadListResultVo>> getHeadImgList() {
        return mUserService.getHeadImgList();
    }

    public Observable<BaseResultEntityVo<VerifyCodeVo>> getVerifyCode(String phone) {
        return mUserService.getVerifyCode(phone);
    }

    public Observable<BaseResultEntityVo<VerifyCodeVo>> checkVerifyCode(String code, String idCard, String phone) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("code", code);
            requestData.put("idCard", idCard);
            requestData.put("phone", phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mUserService.checkVerifyCode(body);
    }

    public Observable<BaseResultEntityVo<BaseDataResultVo>> modifyHeadImg(String readerId, String img, boolean isUploadFile) {
        if (isUploadFile) {
            ArrayMap<String, RequestBody> map = new ArrayMap<>();
            map.put("id", toRequestBodyOfText(readerId));
            MultipartBody.Part body = null;
            if (!TextUtils.isEmpty(img)) {
                File file = new File(img);
                if (file.exists()) {//如果图片存在则上传
                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), file);
                    body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                }
            }
            return mUserService.modifyHeadImg(map, body);
        } else {
            ArrayMap<String, RequestBody> map = new ArrayMap<>();
            map.put("id", toRequestBodyOfText(readerId));
            map.put("image", toRequestBodyOfText(img));
            return mUserService.modifyHeadImg(map, null);
        }
    }

    public Observable<BaseResultEntityVo<RecommendBookResultVo>> recommendBookByIsbn(String idCard, String isbn, String libCode) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("idCard", idCard);
            requestData.put("isbn", isbn);
            if (!TextUtils.isEmpty(libCode)) {
                requestData.put("libCode", libCode);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mUserService.recommendBookByIsbn(body);
    }

    public Observable<BaseResultEntityVo<RecommendBookLibListVo>> getRecommendBooLibList(String idCard, String isbn, String lngLat) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("idCard", idCard);
            requestData.put("isbn", isbn);
            requestData.put("lngLat", lngLat);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mUserService.getRecommendBookLibList(body);
    }

    public Observable<BaseResultEntityVo<ModifyPwdResultVo>> modifyPwd(String newPassword, String id, String oldPassword) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("newPassword", newPassword);
            requestData.put("id", id);
            requestData.put("oldPassword", oldPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mUserService.modifyPwd(body);
    }

    public Observable<BaseResultEntityVo<SelfBorrowBookResultVo>> selfBorrowBook(JSONArray bookIds, String readerId, String stayHallCode) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("bookIds", bookIds);
            requestData.put("readerId", readerId);
            requestData.put("stayHallCode", stayHallCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mUserService.selfBorrowBook(body);
    }

    public Observable<BaseResultEntityVo<SelfBorrowScanFirstVo>> selfBorrowFirstScan(String barNumber, String readerId) {
        return mUserService.selfBorrowFirstScan(barNumber, readerId);
    }

    public Observable<BaseResultEntityVo<ReservationBookListVo>> getReservationBookList(int pageNo, int pageCount, String idCard) {
        return mUserService.getReservationBookList(pageNo, pageCount, idCard);
    }

    public Observable<BaseResultEntityVo<BorrowBookListVo>> getBorrowBookList(String idCard, int pageNo, int pageCount, int status) {
        return mUserService.getBorrowBookList(idCard, pageNo, pageCount, status);
    }

    public Observable<BaseResultEntityVo<BorrowBookListVo>> getHistoryBookList(String idCard, int pageNo, int pageCount, int type) {
        return mUserService.getHistoryBookList(idCard, pageNo, pageCount, type);
    }

    public Observable<BaseResultEntityVo<RenewBorrowBookResultVo>> renewBorrowBook(String idCard, long borrowerBookId) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("idCard", idCard);
            requestData.put("borrowerBookId", borrowerBookId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mUserService.renewBorrowBook(body);
    }

    public Observable<BaseResultEntityVo<BorrowBookDetailInfoVo>> getBorrowBookDetail(long borrowerBookId) {
        return mUserService.getBorrowBookDetail(borrowerBookId);
    }

    public Observable<BaseResultEntityVo<CommonReturnBookLibListVo>> getReturnLibList(long bookId, String lngLat) {
        return mUserService.getReturnLibList(bookId, lngLat);
    }

    public Observable<BaseResultEntityVo<NewsDetailVo>> getNewsDetail(long newsId, String identity, int fromSearch) {
        return mService.getNewsDetailId(newsId, identity, fromSearch);
    }

    public Observable<BaseResultEntityVo<PraiseBookResultVo>> praiseBook(long borrowerBookId, int praise) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("borrowerBookId", borrowerBookId);
            requestData.put("praise", praise);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mUserService.praiseBook(body);
    }

    public Observable<BaseResultEntityVo<NoteModifyResultVo>> noteModify(long id, String readingNote) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("id", id);
            requestData.put("readingNote", readingNote);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mUserService.noteModify(body);
    }

    public Observable<BaseResultEntityVo<NoteModifyResultVo>> noteAdd(long borrowerBookId, long buyBookId, String idCard, String readingNote) {
        JSONObject requestData = new JSONObject();
        try {
            if (borrowerBookId > 0) {
                requestData.put("borrowerBookId", borrowerBookId);
            }
            if (buyBookId > 0) {
                requestData.put("buyBookId", buyBookId);
            }
            requestData.put("idCard", idCard);
            requestData.put("readingNote", readingNote);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mUserService.noteAdd(body);
    }

    public Observable<BaseResultEntityVo<BaseDataResultVo>> delNote(long noteId) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("id", noteId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mUserService.delNote(body);
    }

    public Observable<BaseResultEntityVo<UserDepositLimitRegulationVo>> getAccountLimitRegulation(String userId) {
        return mUserService.getAccountLimitRegulation(userId);
    }

    public Observable<BaseResultEntityVo<UserDepositVo>> getDepositInfo(String idCard, String hallCode) {
        ArrayMap<String, Object> map = new ArrayMap<>();
        map.put("idCard", idCard);
        map.put("hallCode", hallCode);
        return mUserService.getDepositInfo(map);
    }

    public Observable<BaseResultEntityVo<CompensateDepositInfoVo>> getCompensateDepositInfo(double compensatePrice,
                                                                                            String hallCode,
                                                                                            String readerId,
                                                                                            int usedDepositType) {
        ArrayMap<String, Object> map = new ArrayMap<>();
        map.put("compensatePrice", compensatePrice);
        map.put("hallCode", hallCode);
        map.put("readerId", readerId);
        map.put("usedDepositType", usedDepositType);
        return mUserService.getCompensateDepositInfo(map);
    }


    public Observable<BaseResultEntityVo<UserDepositVo>> getDepositInfo(ArrayMap<String, Object> map) {
        return mUserService.getDepositInfo(map);
    }

    public Observable<BaseResultEntityVo<OperateReservationBookResultVo>> operateReservationBook(int appointType, long id, String idCard) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("appointType", appointType);
            requestData.put("id", id);
            requestData.put("idCard", idCard);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mUserService.operateReservationBook(body);
    }

    public Observable<BaseResultEntityVo<ActionListVo>> getAppliedActionList(String idCard, int pageNo, int pageCount) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("pageCount", pageCount);
            requestData.put("pageNo", pageNo);
            requestData.put("idCard", idCard);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mUserService.getAppliedActionList(body);
    }

    public Observable<BaseResultEntityVo<ApplyActionResultVo>> applyAction(int activityId, String idCard, String name, String phone) {
        return mUserService.applyAction(activityId, idCard, name, phone);
    }

    public Observable<BaseResultEntityVo<NoteListVo>> getReaderNotes(String idCard, int pageCount, int pageNo) {
        return mUserService.getNoteList(idCard, pageCount, pageNo);
    }

    public Observable<BaseResultEntityVo<VerifyIDCardVo>> verifyIDCard(String idCard) {
        return mUserService.verifyIDCard(idCard);
    }

    public Observable<BaseResultEntityVo<VerifyCodeVo>> getVerifyCodeForgetPwd(String phone) {
        return mUserService.getVerifyCodeForgetPwd(phone);
    }

    public Observable<BaseResultEntityVo<VerifyCodeVo>> checkVerifyCodeForgetPwd(String code, String phone) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("code", code);
            requestData.put("phone", phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mUserService.checkVerifyCodeForgetPwd(body);
    }

    public Observable<BaseResultEntityVo<ResetPwdResultVo>> resetForgetPwd(String idCard, String newPassword) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("idCard", idCard);
            requestData.put("newPassword", newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mUserService.resetForgetPwd(body);
    }

    public Observable<BaseResultEntityVo<RecommendBookLibListVo>> searchRecommendLib(String idCard, String isbn, String keyword, String lngLat) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("idCard", idCard);
            requestData.put("isbn", isbn);
            requestData.put("keyword", keyword);
            requestData.put("lngLat", lngLat);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mUserService.searchRecommendLib(body);
    }

    public Observable<BaseResultEntityVo<SelfBorrowBookInfoVo>> selfBorrowBookInfo(String barNumber, String stayHallCode, String readerId) {
        return mUserService.selfBorrowBookInfo(barNumber, stayHallCode, readerId);
    }


    public Observable<BaseResultEntityVo<BillInfoVo>> getUserBill(ArrayMap<String, Object> map) {
        return mUserService.getUserBill(map);
    }

    public Observable<BaseResultEntityVo<DepositCategoryVo>> getDepositCategory() {
        return mUserService.getDepositCategory();
    }

    public Observable<BaseResultEntityVo<LibDepositListVo>> getLibDeposit(String idCard, int pageNo, int pageCount) {
        return mUserService.getLibDeposit(idCard, pageNo, pageCount);
    }


    public Observable<BaseResultEntityVo<LibListVo>> getSupLibraryList(int pageNum, int pageCount, String libCode, String lngLat) {
        return mService.getSupLibraryList(pageNum, pageCount, libCode, lngLat);
    }

    public Observable<BaseResultEntityVo<LibListVo>> getBranchLibraryList(int pageNum, int pageCount, String libCode, String lngLat) {
        return mService.getBranchLibraryList(pageNum, pageCount, libCode, lngLat);
    }

    public Observable<BaseResultEntityVo<List<ProvinceVo>>> getProvinceList() {
        return mService.getProvinceList();
    }

    public Observable<BaseResultEntityVo<List<CityVo>>> getCityList(String code) {
        return mService.getCityList(code);
    }

    public Observable<BaseResultEntityVo<List<DistrictVo>>> getDistrictList(String cityCode) {
        return mService.getDistrictList(cityCode);
    }

    public Observable<BaseResultEntityVo<LastDistrictVo>> getLastDistrictList(String locationCode) {
        return mService.getLastDistrictList(locationCode);
    }

    public Observable<BaseResultEntityVo<List<String>>> getLibraryLevel() {
        return mService.getLibraryLevel();
    }

    public Observable<BaseResultEntityVo<PayPenaltyResultVo>> dealUserLibPenalty(String readerId, String hallCode) {
        JSONObject requestData = new JSONObject();
        try {
            if (hallCode != null && !hallCode.equals("")) {
                requestData.put("hallCode", hallCode);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mUserService.dealUserLibPenalty(readerId, body);
    }

//    public Observable<BaseResultEntityVo<PayPenaltyResultVo>> dealUserPenalty(String readerId) {
//        return mUserService.dealUserPenalty(readerId);
//    }

    public Observable<BaseResultEntityVo<BaseDataResultVo>> userCompensateBooks(String readerId, String idCard, long borrowerId, String idPassword) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("borrowerId", borrowerId);
            requestData.put("idCard", idCard);
            requestData.put("idPassword", idPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mUserService.userCompensateBooks(readerId, body);
    }

    public Observable<BaseResultEntityVo<BaseDataResultVo>> pubMsg(String contact, String content, String imagePath, String libCode, int readerId) {
        ArrayMap<String, RequestBody> map = new ArrayMap<>();
        map.put("content", toRequestBodyOfText(content));
        map.put("libCode", toRequestBodyOfText(libCode));
        map.put("readerId", toRequestBodyOfText(String.valueOf(readerId)));
        if (!TextUtils.isEmpty(contact)) {
            map.put("contact", toRequestBodyOfText(contact));
        }
        MultipartBody.Part body = null;
        if (!TextUtils.isEmpty(imagePath)) {
            File file = new File(imagePath);
            if (file.exists()) {//如果图片存在则上传
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), file);
                body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
            }
        }
        return mUserService.pubMsg(map, body);
    }

    private RequestBody toRequestBodyOfText(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }

    public Observable<BaseResultEntityVo<List<SearchHotResultVo>>> getHotSearchList(String libCode, String type) {
        return mService.getHotSearchList(libCode, type);
    }

    public Observable<BaseResultEntityVo<WXPayInfoVo>> requestWXPayInfo(double payMoney, String userIP) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("payMoney", payMoney);
            requestData.put("userIP", userIP);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mUserService.requestWXPayInfo(body);
    }

    public Observable<BaseResultEntityVo<PayResultVo>> requestWXPayResult(String orderNum) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("orderNum", orderNum);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mUserService.requestWXPayResult(body);
    }

    public Observable<BaseResultEntityVo<AliPayInfoVo>> requestAliPayInfo(double payMoney, String userIP) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("payMoney", payMoney);
            requestData.put("userIP", userIP);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mUserService.requestAliPayInfo(body);
    }

    public Observable<BaseResultEntityVo<PayResultVo>> requestAliPayResult(String orderNum) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("orderNum", orderNum);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mUserService.requestAliPayResult(body);
    }

    public Observable<BaseResultEntityVo<MessageBoardVo>> getMessageBoardList(String libCode, String readerId, int pageNum, int pageCount) {
        ArrayMap<String, Object> map = new ArrayMap<>();
        map.put("libCode", libCode);
        map.put("pageNo", pageNum);
        map.put("pageCount", pageCount);
        if (!TextUtils.isEmpty(readerId)) {
            map.put("readerId", readerId);
        } else {
            String identity = Installation.id(CloudLibraryApplication.getAppContext());
            map.put("identity", identity);
        }
        return mService.getMessageBoardList(map);
    }

    public Observable<BaseResultEntityVo<MyMessageVo>> getMyMessageList(int pageNo, int pageCount, String readerId) {
        ArrayMap<String, Object> map = new ArrayMap<>();
        map.put("pageNo", pageNo);
        map.put("pageCount", pageCount);
        map.put("readerId", readerId);
        return mUserService.getMyMessageList(map);
    }

    public Observable<BaseResultEntityVo<CommentReplyVo>> replyMsg(long commentId, String content, String readerId, long replyId) {
        ArrayMap<String, Object> map = new ArrayMap<>();
        map.put("content", content);
        map.put("readerId", readerId);
        if (commentId > 0) {
            map.put("commentId", commentId);
        }
        if (replyId > 0) {
            map.put("replyId", replyId);
        }
        return mUserService.replyMsg(map);
    }

    public Observable<BaseResultEntityVo<WithdrawResultVo>> requestWithdraw(double totalAmount, String userIp) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("totalAmount", totalAmount);
            requestData.put("userIP", userIp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mUserService.requestWithdraw(body);
    }

    public Observable<BaseResultEntityVo<UnreadMsgCountVo>> getUnreadMsgCount(int readerId) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("readerId", readerId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mUserService.getUnreadMsgCount(body);
    }

    public Observable<BaseResultEntityVo<BaseDataResultVo>> delReaderMsg(long commentId, String readerId) {
        ArrayMap<String, Object> map = new ArrayMap<>();
        map.put("commentId", commentId);
        map.put("readerId", readerId);
        return mUserService.delReaderMsg(map);
    }

    public Observable<BaseResultEntityVo<BaseDataResultVo>> delReplyReaderMsg(long replyId, String readerId) {
        ArrayMap<String, Object> map = new ArrayMap<>();
        map.put("replyId", replyId);
        map.put("readerId", readerId);
        return mUserService.delReplyReaderMsg(map);
    }

    public Observable<BaseResultEntityVo<BaseDataResultVo>> commentPraise(long commentId, String identity) {
        return mUserService.commentPraise(commentId, identity);
    }

    public Observable<BaseResultEntityVo<BaseDataResultVo>> replyPraise(long replyId, String identity) {
        return mUserService.replyPraise(replyId, identity);
    }

    public Observable<BaseResultEntityVo<DiscussReplyVo>> replyList(long commentId, String readerId, int pageCount, int pageNo, String identity) {
        ArrayMap<String, Object> map = new ArrayMap<>();
        map.put("commentId", commentId);
        map.put("pageCount", pageCount);
        map.put("pageNo", pageNo);
        if (!TextUtils.isEmpty(readerId)) {
            map.put("readerId", readerId);
        }
        if (!TextUtils.isEmpty(identity)) {
            map.put("identity", identity);
        }
        return mUserService.replyList(map);
    }

    public Observable<BaseResultEntityVo<BaseDataResultVo>> readMsg(String readerId) {
        ArrayMap<String, Object> map = new ArrayMap<>();
        map.put("readerId", readerId);
        return mUserService.readMsg(map);
    }

    public Observable<BaseResultEntityVo<CommentIndexVo>> commentIndex(long commentId) {
        return mUserService.commentIndex(commentId);
    }

    public Observable<BaseResultEntityVo<CommentIndexVo>> replyIndex(long replyId) {
        return mUserService.replyIndex(replyId);
    }

    public Observable<BaseResultEntityVo<CommentInfoVo>> commentInfo(long commentId, String identity, String readerId) {
        ArrayMap<String, Object> map = new ArrayMap<>();
        map.put("commentId", commentId);
        if (!TextUtils.isEmpty(readerId)) {
            map.put("readerId", readerId);
        }
        if (!TextUtils.isEmpty(identity)) {
            map.put("identity", identity);
        }
        return mUserService.commentInfo(map);
    }

    public Observable<BaseResultEntityVo<BaseDataResultVo>> sendPhoneVerifyCode(String phone) {
        return mService.sendPhoneVerifyCode(phone);
    }

    public Observable<BaseResultEntityVo<BaseDataResultVo>> checkMsgCode(String phone, String code) {
        return mService.checkMsgCode(phone, code);
    }

    public Observable<BaseResultEntityVo<BaseDataResultVo>> checkReaderIdInfo(String idCard, String nickName) {
        return mService.checkReaderIdInfo(idCard, nickName);
    }

    public Observable<BaseResultEntityVo<BaseDataResultVo>> readerRegister(String cardName, String idCard, String idPassword, String phone, String nickName) {
        ArrayMap<String, Object> parameterMap = new ArrayMap<>();
        parameterMap.put("cardName", cardName);
        parameterMap.put("idCard", idCard);
        parameterMap.put("idPassword", MD5Utils.MD5(idPassword));
        parameterMap.put("phone", phone);
        parameterMap.put("nickName", nickName);
        return mService.readerRegister(parameterMap);
    }

    public Observable<BaseResultEntityVo<PreUserInfoVo>> preUserInfo(String phone) {
        return mUserService.preUserInfo(phone);
    }

    public Observable<BaseResultEntityVo<FaceImageVo>> getFaceRecognitionImage(String idCard) {
        return mUserService.getFaceRecognitionImage(idCard);
    }

    public Observable<BaseResultEntityVo<BaseDataResultVo>> updateUserFaceImage(String idCard, String faceImage) {
        ArrayMap<String, RequestBody> map = new ArrayMap<>();
        map.put("idCard", toRequestBodyOfText(idCard));
        MultipartBody.Part body = null;
        if (!TextUtils.isEmpty(faceImage)) {
            File file = new File(faceImage);
            if (file.exists()) {//如果图片存在则上传
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), file);
                body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
            }
        }
        return mUserService.updateUserFaceImage(map, body);
    }

    public Observable<BaseResultEntityVo<EBookListVo>> getEBookRankingList(Map<String, String> parameterMap) {
        return mService.getEBookRankingList(parameterMap);
    }

    public Observable<BaseResultEntityVo<OverdueMsgListVo>> getOverdueMsg(String idCard, int pageNo, int pageCount) {
        return mUserService.getOverdueMsg(idCard, pageNo, pageCount);
    }

    public Observable<BaseResultEntityVo<BaseDataResultVo>> readOverdueMsg(int id) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mUserService.readOverdueMsg(body);
    }

    public Observable<BaseResultEntityVo<UnreadOverdueMsgCountVo>> getUnreadOverdueMsgCount(String idCard) {
        return mUserService.getUnreadOverdueMsgCount(idCard);
    }

    public Observable<BaseResultEntityVo<BarCodeReusltVo>> refreshBarCode(String readerId) {
        return mUserService.refreshBarCode(readerId);
    }

    public Observable<BaseResultEntityVo<BaseDataResultVo>> reportBarCodeCount(String readerId, String barCode) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("barCode", barCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mUserService.reportBarCodeCount(readerId, body);
    }

    public Observable<BaseResultEntityVo<List<ClassifyTwoLevelVo>>> getVideoGradeList() {
        return mService.getVideoGradeList();
    }

    public Observable<BaseResultEntityVo<VideoSearchListVo>> getHotVideoSetList(ArrayMap<String, Object> parameterMap) {
        return mService.getHotVideoSetList(parameterMap);
    }

    public Observable<BaseResultEntityVo<VideoSearchListVo>> getNewVideoSetList(ArrayMap<String, Object> parameterMap) {
        return mService.getNewVideoSetList(parameterMap);
    }

    public Observable<BaseResultEntityVo<List<VideoCatalogVo>>> getVideoCatalogList(long videosId) {
        return mService.getVideoCatalogList(videosId);
    }

    public Observable<BaseResultEntityVo<VideoFavoritesVo>> collectionVideo(String readerId, long videosId) {
        ArrayMap<String, Object> parameterMap = new ArrayMap<>();
        parameterMap.put("readerId", readerId);
        parameterMap.put("videosId", videosId);
        return mUserService.collectionVideo(parameterMap);
    }

    public Observable<BaseResultEntityVo<VideoFavoritesVo>> cancelCollectionVideo(JSONArray videosIds, String readerId) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("videosIds", videosIds);
            requestData.put("readerId", readerId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mUserService.cancelCollectionVideo(body);
    }

    public Observable<BaseResultEntityVo<VideoCollectStatusVo>> getCollectionVideoStatus(long videosId, String readerId) {
        return mUserService.getCollectionVideoStatus(videosId, readerId);
    }

    public Observable<BaseResultEntityVo<VideoCollectListVo>> getCollectVideoSetList(String readerId, ArrayMap<String, Object> parameterMap) {
        return mUserService.getCollectVideoSetList(readerId, parameterMap);
    }

    public Observable<BaseResultEntityVo<VideoSearchListVo>> searchVideoSetList(ArrayMap<String, Object> parameterMap) {
        return mService.searchVideoSetList(parameterMap);
    }

    public Observable<BaseResultEntityVo<VideoDetailVo>> getVideoSetDetail(long videosId) {
        return mService.getVideoSetDetail(videosId);
    }

    public Observable<BaseResultEntityVo> saveSearchBrowseRecord(ArrayMap<String, Object> parameterMap) {
        return mService.saveSearchBrowseRecord(parameterMap);
    }

    public Observable<BaseResultEntityVo<VideoBelongLibVo>> getVideoBelongLibrary(String lanLat, long videosId, String readerId) {
        return mService.getVideoBelongLibrary(lanLat, videosId, readerId);
    }

    public Observable<BaseResultEntityVo<VideoCollectStatusVo>> getEBookCollectStatus(String ebookId, String readerId) {
        return mUserService.getEBookCollectStatus(ebookId, readerId);
    }

    public Observable<BaseResultEntityVo<VideoFavoritesVo>> collectionEBook(ArrayMap<String, Object> map) {
        return mUserService.collectionEBook(map);
    }

    public Observable<BaseResultEntityVo<VideoFavoritesVo>> cancelCollectionEBook(JSONArray ebookIds, String readerId) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("ebookIds", ebookIds);
            requestData.put("readerId", readerId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mUserService.cancelCollectionEBook(body);
    }

    public Observable<BaseResultEntityVo<EBookListVo>> getCollectEBookList(String readerId, int pageNo, int pageCount) {
        return mUserService.getCollectEBookList(readerId, pageNo, pageCount);
    }

    public Observable<BaseResultEntityVo> recordWatch(long sectionId, ArrayMap<String, Object> parameterMap) {
        return mUserService.recordWatch(sectionId, parameterMap);
    }

    public Observable<BaseResultEntityVo<List<BannerNewListItemVo>>> getLibBannerNewsList(String libCode, String locationCode) {
        return mService.getLibBannerNewsList(libCode, locationCode);
    }

    public Observable<BaseResultEntityVo<List<LibraryModelVo>>> getLibModelList(String libCode) {
        return mService.getLibModelList(libCode);
    }

    public Observable<BaseResultEntityVo<MainBranchLibraryVo>> getAllLibrary(String libCode, String lngLat) {
        ArrayMap<String, Object> parameterMap = new ArrayMap<>();
        parameterMap.put("libCode", libCode);
        if (!TextUtils.isEmpty(lngLat)) {
            parameterMap.put("lngLat", lngLat);
        }
        return mService.getAllLibrary(parameterMap);
    }

    public Observable<BaseResultEntityVo<MainBranchLibraryCountVo>> getMainBranchLibraryCount(String libCode) {
        return mService.getMainBranchLibraryCount(libCode);
    }

    public Observable<BaseResultEntityVo<ActionListVo>> getLibActionList(String libCode, int pageNum, int pageCount, String keyword) {
        ArrayMap<String, Object> parameterMap = new ArrayMap<>();
        parameterMap.put("pageNo", pageNum);
        parameterMap.put("pageCount", pageCount);
        if (!TextUtils.isEmpty(keyword)) {
            parameterMap.put("keyword", keyword);
        }
        return mService.getLibActionList(libCode, parameterMap);
    }

    public Observable<BaseResultEntityVo<VideoSearchListVo>> getLibVideoList(String libCode, ArrayMap<String, Object> parameterMap) {
        return mService.getLibVideoList(libCode, parameterMap);
    }

    public Observable<BaseResultEntityVo<SelfBuyBookVo>> getSelfBuyBookInfo(String fullBarNumber, String idCard, String stayLibraryHallCode) {
        return mUserService.getSelfBuyBookInfo(fullBarNumber, idCard, stayLibraryHallCode);
    }

    public Observable<BaseResultEntityVo<SelfBuyBookResultVo>> selfBuyBook(String readerId, JSONArray libraryBookIds, String totalPrice, String payPwd) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("libraryBookIds", libraryBookIds);
            requestData.put("totalPrice", totalPrice);
            requestData.put("payPwd", MD5Utils.MD5(payPwd));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mUserService.selfBuyBook(readerId, body);
    }

    public Observable<BaseResultEntityVo<SelfBuyBookShelfVo>> getSelfBuyBookShelfList(String readerId, int pageNo, int pageCount) {
        return mUserService.getSelfBuyBookShelfList(readerId, pageNo, pageCount);
    }

    public Observable<BaseResultEntityVo<NoteListItemVo>> getSelfBuyBookNote(int noteId) {
        return mUserService.getSelfBuyBookNote(noteId);
    }

    public Observable<BaseResultEntityVo<BaseDataResultVo>> praiseSelfBuyBook(long buyId, int operateType) {
        return mUserService.praiseSelfBuyBook(buyId, operateType);
    }

    public Observable<BaseResultEntityVo<SelfBuyBookDetailVo>> getSelfBuyBookDetail(long buyBookId) {
        return mUserService.getSelfBuyBookDetail(buyBookId);
    }

    public Observable<ServerTimeVo> getServerTime() {
        return mService.getServerTime();
    }

    public Observable<BaseResultEntityVo<HtmlUrlVo>> getBorrowingIntroduces(String libCode) {
        return mService.getBorrowingIntroduces(libCode);
    }

    public Observable<BaseResultEntityVo<List<EBookCategoryVo>>> getEBookCategory() {
        return mService.getEBookCategory();
    }

    public Observable<BaseResultEntityVo<AttentionLibResultVo>> attentionLib(String libraryCode, String readerId) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("libraryCode", libraryCode);
            requestData.put("readerId", readerId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mUserService.attentionLib(body);
    }

    public Observable<BaseResultEntityVo<AttentionLibResultVo>> unAttentionLib(String readerId, String libraryCode) {
        return mUserService.unAttentionLib(readerId, libraryCode);
    }

    public Observable<BaseResultEntityVo<HomeInfoVo>> getHomeInfo(String locationCode, String lngLat) {
        return mService.getHomeInfo(locationCode, lngLat);
    }

    public Observable<BaseResultEntityVo> reportBookShare(String mappingId, int type) {
        return mService.reportBookShare(mappingId, type);
    }

    public Observable<BaseResultEntityVo<BaseDataResultVo>> updateNickName(String id, String nickName) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("id", id);
            requestData.put("nickName", nickName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mUserService.updateNickName(body);
    }

    public Observable<BaseResultEntityVo<LibraryDetailListVo>> getLibResourcesList(String libCode) {
        return mService.getLibResourcesList(libCode);
    }

    public Observable<BaseResultEntityVo<BookStoreListVo>> getBookStoreList(ArrayMap<String, Object> parameters) {
        return mService.getBookStoreList(parameters);
    }

    public Observable<BaseResultEntityVo<CommentIndexVo>> getMessageBoardIndex(long messageId) {
        return mUserService.getMessageBoardIndex(messageId);
    }

    public Observable<BaseResultEntityVo<CommentIndexVo>> getMessageBoardReplyIndex(long replyId) {
        return mUserService.getMessageBoardReplyIndex(replyId);
    }

    public Observable<BaseResultEntityVo<MessageBoardReplyVo>> replyMessageBoard(String readerId, long messageId, long replyId, String libCode, String content) {
        ArrayMap<String, Object> parameterMap = new ArrayMap<>();
        parameterMap.put("readerId", readerId);
        parameterMap.put("content", content);
        parameterMap.put("libCode", libCode);
        if (messageId > 0) {
            parameterMap.put("msgId", messageId);
        }
        if (replyId > 0) {
            parameterMap.put("replyId", replyId);
        }
        return mUserService.replyMessageBoard(parameterMap);
    }

    public Observable<BaseResultEntityVo<BaseDataResultVo>> messagePraise(long msgId, long readerId) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("msgId", msgId);
            if (readerId > 0) {
                requestData.put("readerId", readerId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mUserService.messagePraise(body);
    }

    public Observable<BaseResultEntityVo<MessageBoardDetailVo>> getMessageBoardDetail(long messageId, String readerId) {
        ArrayMap<String, Object> map = new ArrayMap<>();
        map.put("messageId", messageId);
        if (!TextUtils.isEmpty(readerId)) {
            map.put("readerId", readerId);
        }
        return mUserService.getMessageBoardDetail(map);
    }

    public Observable<BaseResultEntityVo<MessageBoardReplyListVo>> getMessageBoardDetailReplyList(long messageId, String readerId, int pageNo, int pageCount) {
        ArrayMap<String, Object> map = new ArrayMap<>();
        map.put("messageId", messageId);
        map.put("pageNo", pageNo);
        map.put("pageCount", pageCount);
        if (!TextUtils.isEmpty(readerId)) {
            map.put("readerId", readerId);
        }
        return mUserService.getMessageBoardDetailReplyList(map);
    }

    public Observable<BaseResultEntityVo<BaseDataResultVo>> delOwnMsg(long msgId, String readerId) {
        ArrayMap<String, Object> map = new ArrayMap<>();
        map.put("msgId", msgId);
        map.put("readerId", readerId);
        return mUserService.delOwnMsg(map);
    }

    public Observable<BaseResultEntityVo<BaseDataResultVo>> delOwnReplyMsg(long replyId, String readerId) {
        ArrayMap<String, Object> map = new ArrayMap<>();
        map.put("replyId", replyId);
        map.put("readerId", readerId);
        return mUserService.delOwnReplyMsg(map);
    }

    public Observable<BaseResultEntityVo<BaseDataResultVo>> replyMsgPraise(long replyId, long readerId) {
        ArrayMap<String, Object> map = new ArrayMap<>();
        map.put("replyId", replyId);
        if (readerId > 0) {
            map.put("readerId", readerId);
        }
        return mUserService.replyMsgPraise(map);
    }

    public Observable<BaseResultEntityVo<OperateReservationBookResultVo>> reservationBook(int appointType, String isbn, String libCode, long readerId) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("appointType", appointType);
            requestData.put("isbn", isbn);
            requestData.put("libCode", libCode);
            requestData.put("readerId", readerId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mUserService.reservationBook(body);
    }
}
