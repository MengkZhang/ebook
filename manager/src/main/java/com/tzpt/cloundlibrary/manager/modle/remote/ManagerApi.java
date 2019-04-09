package com.tzpt.cloundlibrary.manager.modle.remote;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.AddDepositVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.AliPayInfoVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.ApplyPenaltyFreeVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.AutoProcessDepositPenaltyVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.BookInfoVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.BorrowBookStatisticsVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.BorrowBookVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.BorrowingBookStatisticsVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.BranchLibVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.ChangeOperatorPwdVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.ChangeRefundAccountVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.CheckRegisterVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.CollectingBookStatisticsVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.CollectingStatisticsVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.EntranceGuardVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.FlowManageAddBookInfoVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.FlowManageAddNewBookInfoVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.FlowManageDeleteBookVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.FlowManageDeleteSingleVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.FlowManageOutReCallVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.FlowManageSendBookResultVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.FlowManagementListVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.HelpInfoVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.InLibraryOperatorVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.IntoManageSignThisSingleVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.IntoManagementListVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.LibraryAvailableBalanceVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.LibraryDepositTransLogVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.LibraryUserInfoVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.LightSelectResultVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.LightSelectVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.LoginVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.LostBookResultVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.LostBookStatisticsVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.LostBookVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.MsgVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.NoReadMsgVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.OrderFromVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.OutLibraryOperatorVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.PayFineVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.PayResultVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.PenaltyDealResultVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.PenaltyFreeStatisticsVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.PenaltyListVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.ReadMsgVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.ReaderLoginInfoVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.ReaderLoginVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.ReaderPwdModifyVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.ReaderStatisticsVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.RefundInfoVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.RegisterVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.ResetPwdVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.ReturnBookStatisticsVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.ReturnBookVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.ReturnDepositResultVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.SameRangeLibraryListVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.SellBookStatisticsVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.SetFirstOperatorPswVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.SingleSelectionConditionVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.StatisticsHallCodeListVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.SwitchCityVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.UpdateAppVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.VerifyCodeVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.VerifyIdentityVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.VerifyLibraryOperatorPswVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.WXPayInfoVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.WithdrawDepositVo;
import com.tzpt.cloundlibrary.manager.modle.remote.support.HeaderInterceptor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * 地址管理
 * Created by Administrator on 2017/6/21.
 */
public class ManagerApi {
    private static ManagerApi mInstance;
    private static final int TIMEOUT = 30;
    private ManagerApiService mService;
    private static final MediaType mMediaType = MediaType.parse("application/json; charset=utf-8");
    /**
     * base url
     * private static final String API_BASE_URL = "http://39.108.170.48:8067/api/";
     */
//    private static final String API_BASE_URL = "http://192.168.28.10:8077/api/";
//    private static final String API_BASE_URL = "http://47.106.173.136:8067/api/";
    private static final String API_BASE_URL = "http://mms.ytsg.cn/api/";

    public static ManagerApi getInstance() {
        if (mInstance == null) {
            mInstance = new ManagerApi();
        }
        return mInstance;
    }

    private ManagerApi() {
        Gson gson = new GsonBuilder().setDateFormat("yyyyMMddHH:mm:ss'Z'").create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(createOkHttpClient())
                .build();
        mService = retrofit.create(ManagerApiService.class);
    }


    private OkHttpClient createOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(new HeaderInterceptor())
                .build();
    }

    public Observable<LoginVo> login(String libName, String userName, String pwd) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("forceLogin", true);
            requestData.put("hallCode", libName);
            requestData.put("username", userName);
            requestData.put("password", pwd);
        } catch (JSONException e) {
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mService.login(body);
    }

    public Observable<VerifyLibraryOperatorPswVo> checkOperatorPsw(String libName, String userName, String pwd) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("hallCode", libName);
            requestData.put("username", userName);
            requestData.put("password", pwd);
        } catch (JSONException e) {
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mService.checkOperatorPsw(body);

    }

    public Observable<LibraryUserInfoVo> getLoginUserInfo() {
        return mService.getLoginUserInfo();
    }

    public Observable<LightSelectVo> getLightSelect(String libraryCode) {
        return mService.getLightSelect(libraryCode);
    }

    public Observable<LightSelectResultVo> setLightSelect(String requestData) {
        RequestBody body = RequestBody.create(mMediaType, requestData);
        return mService.setLightSelect(body);
    }

    public Observable<ChangeOperatorPwdVo> changeOperatorPwd(String oldPwd, String newPwd) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("password", oldPwd);
            requestData.put("newPassword", newPwd);
        } catch (JSONException e) {
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mService.changeOperatorPwd(body);
    }

    public Observable<MsgVo> getMsgList(int pageNum, int pageSize) {
        return mService.getMsgList(pageNum, pageSize);
    }

    public Observable<ReadMsgVo> setReadStatus(long newsId) {
        return mService.setReadStatus(newsId);
    }

    public Observable<NoReadMsgVo> getUnReadCount() {
        return mService.getUnReadCount();
    }

    public Observable<ReaderLoginInfoVo> getReaderInfo(String readerId) {
        return mService.getReaderInfo(readerId);
    }

    public Observable<CheckRegisterVo> checkReaderAccount(String condition, String hallCode) {
        return mService.checkReaderAccount(condition, hallCode, 1);
    }

    public Observable<RegisterVo> register(String number, String name, String telNum, String pwd,
                                           String image, String gender, String hallCode, String code,
                                           int readerLimit) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("cardName", name);
            requestData.put("gender", gender);
            requestData.put("hallCode", hallCode);
            requestData.put("idCard", number);
            requestData.put("idCardImage", image);
            requestData.put("idpassword", pwd);
            requestData.put("type", readerLimit);
            if (!TextUtils.isEmpty(telNum) && !TextUtils.isEmpty(code)) {
                requestData.put("phone", telNum);
                requestData.put("code", code);
            }
        } catch (JSONException e) {
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mService.register(body);
    }

    public Observable<RegisterVo> checkReaderPsw(String idCard, String idPassword) {
        return mService.checkReaderPsw(idCard, idPassword);
    }

    public Observable<ReaderLoginVo> readerLogin(String requestData) {
        RequestBody body = RequestBody.create(mMediaType, requestData);
        return mService.readerLogin(body);
    }

    public Observable<OrderFromVo> getOrderFromNumber() {
        return mService.getOrderFromNumber();
    }

    public Observable<OrderFromVo> getBorrowNumber() {
        return mService.getBorrowNumber();
    }

    public Observable<OrderFromVo> getReturnNumber() {
        return mService.getReturnNumber();
    }


    public Observable<AddDepositVo> addDeposit(String readerId, String operOrder, String amount) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("operOrder", operOrder);
            requestData.put("amount", amount);
        } catch (JSONException e) {
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mService.addDeposit(readerId, body);
    }

    public Observable<AddDepositVo> addDeposit(String readerId, int accountType, double amount) {
        JSONObject typeData = new JSONObject();
        try {
            typeData.put("index", accountType);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject requestData = new JSONObject();
        try {
            requestData.put("accountType", typeData);
            requestData.put("amount", amount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mService.addDeposit(readerId, body);
    }

    public Observable<BookInfoVo> getBookInfo(String barNumber, String readerId, String stayLibraryHallCode) {
        return mService.getBookInfo(barNumber, readerId, stayLibraryHallCode);
    }

    public Observable<BorrowBookVo> borrowBook(String readerId, JSONArray bookIdsArray, String borrowNumber) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("bookIds", bookIdsArray);
            requestData.put("borrowNumber", borrowNumber);
        } catch (JSONException e) {
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mService.borrowBook(readerId, body);
    }

    public Observable<ReturnBookVo> returnBook(String barNumber, String orderNumber) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("returnNumber", orderNumber);
            requestData.put("barNumber", barNumber);
        } catch (JSONException e) {
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mService.returnBook(body);
    }

    public Observable<LostBookVo> getLostBookList(String readerId) {
        return mService.getLostBookList(readerId);
    }

    public Observable<LostBookResultVo> lostBook(String readerId, JSONArray borrowerIds, String returnNumber) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("borrowerIds", borrowerIds);
            requestData.put("returnNumber", returnNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mService.lostBook(readerId, body);
    }

    public Observable<ReturnDepositResultVo> refundDeposit(String readerId, double amount) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("amount", amount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mService.refundDeposit(readerId, body);
    }

    public Observable<ReaderPwdModifyVo> modifyReaderPwdOrPhone(String requestData) {
        RequestBody body = RequestBody.create(mMediaType, requestData);
        return mService.modifyReaderPwdOrPhone(body);
    }

    public Observable<EntranceGuardVo> entranceCheck(String barNumber) {
        return mService.entranceCheck(barNumber);
    }

//    //流入流出管理
//    //查询流出状态
//    public Observable<SingleSelectionConditionVo> getFlowManageStateList() {
//        return mService.getFlowManageStateList();
//    }
//
//    //查询流入状态
//    public Observable<SingleSelectionConditionVo> getIntoManageStateList() {
//        return mService.getIntoManageStateList();
//    }
//
//    //查询本馆操作员 -(使用位置：1.流出流入管理选择操作员2.统计分析界面选择操作员)
//    public Observable<SingleSelectionConditionVo> getLibraryOperatorList(String hallCode) {
//        return mService.getLibraryOperatorList(hallCode);
//    }

    /**
     * 搜索同流通范围的馆 -（位置：流出管理新增）
     *
     * @param pageNum   页码
     * @param pageSize  页数
     * @param grepValue 搜索内容
     * @return
     */
    public Observable<SameRangeLibraryListVo> searchSameRangeLibraryListByCondition(int pageNum, int pageSize, String hallCode, String grepValue) {
        return mService.searchSameRangeLibraryListByCondition(pageNum, pageSize, hallCode, grepValue);
    }


    /**
     * 增加流出单的书籍
     *
     * @param barNumber   条码号
     * @param circulateId 流通ID
     * @param inHallCode  流入馆馆号
     * @param operCode    设置单号
     * @return
     */
    public Observable<FlowManageAddNewBookInfoVo> getFlowManageAddNewBook(String barNumber, String circulateId,
                                                                          String inHallCode, String operCode,
                                                                          String outHallCode, String outOperUserId) {
        JSONObject requestData = new JSONObject();
        try {
            if (!TextUtils.isEmpty(circulateId)) {
                requestData.put("id", circulateId);
            }
            if (!TextUtils.isEmpty(operCode)) {
                requestData.put("circulateCode", operCode);
            }
            requestData.put("barNumber", barNumber);
            requestData.put("inHallCode", inHallCode);
            requestData.put("outHallCode", outHallCode);
            requestData.put("outOperUserId", outOperUserId);
        } catch (JSONException e) {
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mService.getFlowManageAddNewBook(body);
    }

    /**
     * 删除流出单的书籍
     *
     * @param id 流通与书籍关联ID
     * @return
     */
    public Observable<FlowManageDeleteBookVo> deleteFlowManageBookInfo(String circulateId, String id) {
        return mService.deleteFlowManageBookInfo(circulateId, id);
    }

    /**
     * 流出管理-发送清单列表
     *
     * @param circulateId 流通ID
     * @return
     */
    public Observable<FlowManageSendBookResultVo> sendFlowManageNewBookList(String circulateId, String outOperUserId) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("outOperUserId", outOperUserId);
        } catch (Exception e) {
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mService.sendFlowManageNewBookList(circulateId, body);
    }

    /**
     * 获取流出管理列表
     */
    public Observable<FlowManagementListVo> getFlowManagementList(ArrayMap<String, String> map) {
        return mService.getFlowManagementList(map);
    }

    /**
     * 流出管理-撤回清单列表
     *
     * @param circulateId 流通ID
     * @return
     */
    public Observable<FlowManageOutReCallVo> withdrawThisSingle(String circulateId, String outOperUserId) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("outOperUserId", outOperUserId);
        } catch (Exception e) {
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mService.withdrawThisSingle(circulateId, body);
    }

    /**
     * 获取每单详情(流入流出通用)
     *
     * @param pageNumber  页码
     * @param pageSize    页数
     * @param circulateId 流通ID
     * @return
     */
    public Observable<FlowManageAddBookInfoVo> getFlowManageSingDetail(int pageNumber, int pageSize, String circulateId) {
        return mService.getFlowManageSingDetail(circulateId, pageNumber, pageSize);
    }

    public Observable<InLibraryOperatorVo> getInLibraryOperatorInfo(String circulateId, int circulateStatus) {
        return mService.getInLibraryOperatorInfo(circulateId, circulateStatus);
    }

    public Observable<OutLibraryOperatorVo> getOutLibraryOperatorInfo(String circulateId) {
        return mService.getOutLibraryOperatorInfo(circulateId);
    }

    /**
     * 直接删单
     *
     * @param circulateId 流通ID
     * @return
     */
    public Observable<FlowManageDeleteSingleVo> deleteFlowManageSingle(String circulateId) {
        return mService.deleteFlowManageSingle(circulateId);
    }

    /**
     * 清点删单
     *
     * @param circulateId 流通ID
     * @param barNumber   条码号
     */
    public Observable<FlowManageDeleteSingleVo> outDeleteFlowManageSingleCountBook(String circulateId, String barNumber) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("circulateId", circulateId);
            requestData.put("barNumber", barNumber);
        } catch (JSONException e) {
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mService.outDeleteFlowManageSingleCountBook(body);
    }

    //流入管理

    /**
     * 获取流入管理列表
     *
     * @param pageNumber     页码
     * @param pageSize       页数
     * @param key            搜索key
     * @param beginCondition 开始条件
     * @param endCondition   结束条件
     * @return
     */
    public Observable<IntoManagementListVo> getIntoManageSingleList(ArrayMap<String, String> map) {
        return mService.getIntoManageSingleList(map);
    }

    /**
     * 签收本单
     *
     * @param circulateId 流通ID
     * @param signUserId  单号
     * @return
     */
    public Observable<IntoManageSignThisSingleVo> signThisSingle(String circulateId, String signUserId) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("signUserId", signUserId);
        } catch (Exception e) {
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mService.signThisSingle(circulateId, body);
    }

    /**
     * 拒绝本单
     *
     * @param circulateId 流通ID
     * @param signUserId  单号
     * @return
     */
    public Observable<IntoManageSignThisSingleVo> rejectThisSingle(String circulateId, String signUserId) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("signUserId", signUserId);
        } catch (Exception e) {
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mService.rejectThisSingle(circulateId, body);
    }

    //统计分析

    public Observable<SingleSelectionConditionVo> getHallCodeLibraryStatics(String hallCode) {
        return mService.getHallCodeLibraryStatics(hallCode);
    }

    public Observable<SingleSelectionConditionVo> getHallCodeInBorrower(String hallCode) {
        return mService.getHallCodeInBorrower(hallCode);
    }

    public Observable<SingleSelectionConditionVo> getHallCodeBookSells(String hallCode) {
        return mService.getHallCodeBookSells(hallCode);
    }

    public Observable<SingleSelectionConditionVo> getStatusLibraryStatics() {
        return mService.getStatusLibraryStatics();
    }

    public Observable<SingleSelectionConditionVo> getStorerooms() {
        return mService.getStorerooms();
    }

    public Observable<SingleSelectionConditionVo> getRfidLibraryStatics() {
        return mService.getRfidLibraryStatics();
    }

    public Observable<SingleSelectionConditionVo> getStatusInBorrower() {
        return mService.getStatusInBorrower();
    }

    public Observable<SingleSelectionConditionVo> getHallCodeBorrowerBooks(String hallCode) {
        return mService.getHallCodeBorrowerBooks(hallCode);
    }

    public Observable<SingleSelectionConditionVo> getUsers(String hallCode, String name, int valueEqualDesc) {
        return mService.getUsers(hallCode, name, valueEqualDesc);
    }

    public Observable<SingleSelectionConditionVo> getUsersPenaltyFreeApply() {
        return mService.getUsersPenaltyFreeApply();
    }

    public Observable<SingleSelectionConditionVo> getHallCodeCompensateBooks(String hallCode) {
        return mService.getHallCodeCompensateBooks(hallCode);
    }

    public Observable<SingleSelectionConditionVo> getOperationGatheringStatistics() {
        return mService.getOperationGatheringStatistics();
    }

    public Observable<SingleSelectionConditionVo> getHallCodeReaderStatistics() {
        return mService.getHallCodeReaderStatistics();
    }

    public Observable<SingleSelectionConditionVo> getTypeReaderStatistics() {
        return mService.getTypeReaderStatistics();
    }

    public Observable<SingleSelectionConditionVo> getStatusPenaltyFreeApplys() {
        return mService.getStatusPenaltyFreeApplys();
    }

    public Observable<SingleSelectionConditionVo> getStatusOutCirculite() {
        return mService.getStatusOutCirculite();
    }

    public Observable<SingleSelectionConditionVo> getStatusInCirculite() {
        return mService.getStatusInCirculite();
    }

    /**
     * 借书统计,还书统计中的馆号
     *
     * @return
     */
    public Observable<StatisticsHallCodeListVo> getStatisticsHallCodeList(String hallCode) {
        return mService.getStatisticsHallCodeList(hallCode);
    }

    /**
     * 陪书统计中的馆号
     *
     * @param hallCode
     * @return
     */
    public Observable<StatisticsHallCodeListVo> getLostStatisticsHallCodeList(String hallCode) {
        return mService.getLostStatisticsHallCodeList(hallCode);
    }

    /**
     * 销售统计
     */
    public Observable<StatisticsHallCodeListVo> getSoldStatisticsHallCodeList(String hallCode) {
        return mService.getSoldStatisticsHallCodeList(hallCode);
    }

    /**
     * 借书统计
     */
    public Observable<BorrowBookStatisticsVo> getBorrowBookStatisticsList(ArrayMap<String, String> map) {
        return mService.getBorrowBookStatisticsList(map);
    }

    /**
     * 还书统计
     */
    public Observable<ReturnBookStatisticsVo> getReturnBookStatisticsList(ArrayMap<String, String> map) {
        return mService.getReturnBookStatisticsList(map);
    }

    /**
     * 读者统计
     */
    public Observable<ReaderStatisticsVo> getReaderStatisticsList(ArrayMap<String, String> map) {
        return mService.getReaderStatisticsList(map);
    }

    /**
     * 赔书统计
     */
    public Observable<LostBookStatisticsVo> getLostBookStatisticsList(ArrayMap<String, String> map) {
        return mService.getLostBookStatisticsList(map);
    }

    /**
     * 销售统计
     */
    public Observable<SellBookStatisticsVo> getSellBookStatisticsList(ArrayMap<String, String> map) {
        return mService.getSellBookStatisticsList(map);
    }

    /**
     * 在借统计
     */
    public Observable<BorrowingBookStatisticsVo> getBorrowingBookStatisticsList(ArrayMap<String, String> map) {
        return mService.getBorrowingBookStatisticsList(map);
    }

    /**
     * 藏书统计
     */
    public Observable<CollectingBookStatisticsVo> getCollectionBookStatisticsList(ArrayMap<String, String> map) {
        return mService.getCollectionBookStatisticsList(map);
    }

    /**
     * 收款统计
     */
    public Observable<CollectingStatisticsVo> getCollectingStatisticsList(ArrayMap<String, String> map) {
        return mService.getCollectingStatisticsList(map);
    }

    /**
     * 免单统计
     */
    public Observable<PenaltyFreeStatisticsVo> getPenaltyFreeApplys(ArrayMap<String, String> map) {
        return mService.getPenaltyFreeApplys(map);
    }

    /**
     * 更新APP
     */
    public Observable<UpdateAppVo> updateApp(String version) {
        return mService.updateApp(version, 1);
    }

    /**
     * 自动触发押金处理罚金问题
     *
     * @param readerId 身份证号码
     * @return
     */
    public Observable<AutoProcessDepositPenaltyVo> automaticProcessingDepositPenalties(String readerId) {
        return mService.automaticProcessingDepositPenalties(readerId);
    }

    public Observable<PenaltyDealResultVo> autoDealPenalty(String readerId) {
        return mService.autoDealPenalty(readerId);
    }

    //发送短信验证码
    public Observable<VerifyCodeVo> getMsgVerifyCode(String phone, int isContinue) {
        return mService.getMsgVerifyCode(phone, isContinue);
    }

    public Observable<VerifyCodeVo> getAdminVerifyCode() {
        return mService.getAdminVerifyCode();
    }

    public Observable<ChangeRefundAccountVo> changeRefundAccount(String code, String refundAccount, String refundName) {
        ArrayMap<String, Object> map = new ArrayMap<>();
        map.put("code", code);
        map.put("refundAccount", refundAccount);
        map.put("refundName", refundName);
        return mService.changeRefundAccount(map);
    }

    public Observable<LibraryAvailableBalanceVo> getAvailableBalance() {
        return mService.getAvailableBalance();
    }

    public Observable<LibraryDepositTransLogVo> getDepositTransLog(int pageNo, int pageCount) {
        return mService.getDepositTransLog(pageNo, pageCount);
    }

    public Observable<SwitchCityVo> getProvinceList() {
        return mService.getProvinceList();
    }

    public Observable<SwitchCityVo> getCityList(String code) {
        return mService.getCityList(code);
    }

    public Observable<SwitchCityVo> getDistrictList(String cityName) {
        return mService.getDistrictList(cityName);
    }

    public Observable<PayFineVo> payFine(String readerId, String amount, String operOrder) {
        ArrayMap<String, String> map = new ArrayMap<>();
        map.put("amount", amount);
        map.put("operOrder", operOrder);
        return mService.payFine(readerId, map);
    }

    public Observable<PayFineVo> payFine(String readerId, double amount) {
        ArrayMap<String, String> map = new ArrayMap<>();
        map.put("amount", String.valueOf(amount));
        return mService.payFine(readerId, map);
    }

    public Observable<LostBookResultVo> payCompenStateBooks(String readerId, String amount, JSONArray borrowerIds, String returnNumber) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("amount", amount);
            requestData.put("borrowerIds", borrowerIds);
            requestData.put("returnNumber", returnNumber);
        } catch (JSONException e) {
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mService.payCompenStateBooks(readerId, body);
    }

    public Observable<HelpInfoVo> getHelpList() {
        return mService.getHelpList();
    }

    public Observable<WithdrawDepositVo> requestWithdrawDeposit(double applyMoney, String createIp) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("applyMoney", applyMoney);
            requestData.put("createIp", createIp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mService.requestWithdrawDeposit(body);
    }

    public Observable<RefundInfoVo> requestRefundInfo() {
        JSONObject requestData = new JSONObject();
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mService.requestRefundInfo(body);
    }

    public Observable<WXPayInfoVo> requestWXPayInfo(double payMoney, String userIP, int businessType, String readerId) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("payMoney", payMoney);
            requestData.put("userIP", userIP);
            requestData.put("businessType", businessType);
            if (businessType == 9) {
                requestData.put("helpUserId", readerId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mService.requestWXPayInfo(body);
    }

    public Observable<PayResultVo> requestWXPayResult(String orderNum, String readerId) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("orderNum", orderNum);
            if (readerId != null && !readerId.equals("")) {
                requestData.put("helpUserId", readerId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mService.requestWXPayResult(body);
    }

    public Observable<AliPayInfoVo> requestAliPayInfo(double payMoney, String userIP, int businessType, String readerId) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("payMoney", payMoney);
            requestData.put("userIP", userIP);
            requestData.put("businessType", businessType);
            if (businessType == 9) {
                requestData.put("helpUserId", readerId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mService.requestAliPayInfo(body);
    }

    public Observable<PayResultVo> requestAliPayResult(String orderNum, String readerId) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("orderNum", orderNum);
            if (readerId != null && !readerId.equals("")) {
                requestData.put("helpUserId", readerId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mService.requestAliPayResult(body);
    }

    public Observable<RegisterVo> updateReaderInfo(ArrayMap<String, Object> map) {
        return mService.updateReaderInfo(map);
    }

    public Observable<ApplyPenaltyFreeVo> applyPenaltyFree(String applyRemark, JSONArray penaltyIds, String readerId) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("applyRemark", applyRemark);
            requestData.put("penaltyIds", penaltyIds);
            requestData.put("readerId", readerId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mService.applyPenaltyFree(body);
    }

    public Observable<VerifyCodeVo> sendVerifyForgetPwd(String hallCode, String phone) {
        return mService.sendVerifyForgetPwd(hallCode, phone);
    }

    public Observable<VerifyIdentityVo> verifyIdentity(String code, String hallCode, String phone) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("code", code);
            requestData.put("hallCode", hallCode);
            requestData.put("phone", phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mService.verifyIdentity(body);
    }

    public Observable<ResetPwdVo> resetPwd(int id, String newPwd) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("id", id);
            requestData.put("newPassword", newPwd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mMediaType, requestData.toString());
        return mService.resetPwd(body);
    }

    public Observable<BranchLibVo> queryPavilionLevel(String hallCode) {
        return mService.queryPavilionLevel(hallCode);
    }

    public Observable<SetFirstOperatorPswVo> setFirstPassword(String newPassword) {
        ArrayMap<String, Object> map = new ArrayMap<>();
        map.put("newPassword", newPassword);
        return mService.setFirstPassword(map);
    }

    public Observable<PenaltyListVo> getPenaltyList(String readerId) {
        return mService.getPenaltyList(readerId);
    }
}
