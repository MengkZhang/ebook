package com.tzpt.cloundlibrary.manager.modle.remote;


import android.support.v4.util.ArrayMap;

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

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * api
 * Created by Administrator on 2017/6/21.
 */
public interface ManagerApiService {
    @POST("libraryuser/login")
    Observable<LoginVo> login(@Body RequestBody body);

    @GET("libraryuser/getLoginUserInfo")
    Observable<LibraryUserInfoVo> getLoginUserInfo();

    @POST("libraryuser/checkLibraryUserPassword")
    Observable<VerifyLibraryOperatorPswVo> checkOperatorPsw(@Body RequestBody body);

    @GET("librarysopensetting/getAll")
    Observable<LightSelectVo> getLightSelect(@Query("libraryCode") String libraryCode);

    @POST("librarysopensetting/setting")
    Observable<LightSelectResultVo> setLightSelect(@Body RequestBody body);

    @POST("libraryuser/updateLibraryUserPassword")
    Observable<ChangeOperatorPwdVo> changeOperatorPwd(@Body RequestBody body);

    @GET("news/queryNews")
    Observable<MsgVo> getMsgList(@Query("pageNo") int pageNum, @Query("pageCount") int pageSize);

    @GET("news/{newsId}/setReadStatus")
    Observable<ReadMsgVo> setReadStatus(@Path("newsId") long newsId);

    @GET("news/getUnReadCount")
    Observable<NoReadMsgVo> getUnReadCount();

    @GET("libraryreader/{readerId}")
    Observable<ReaderLoginInfoVo> getReaderInfo(@Path("readerId") String readerId);

    @POST("libraryreader/login")
    Observable<ReaderLoginVo> readerLogin(@Body RequestBody body);

    @POST("libraryreader/readerScanRegister")
    Observable<RegisterVo> register(@Body RequestBody body);

    @GET("libraryreader/checkReaderAccount")
    Observable<CheckRegisterVo> checkReaderAccount(@Query("condition") String condition, @Query("hallCode") String hallCode, @Query("isScan") int isScan);

    @GET("libraryreader/checkPassReader")
    Observable<RegisterVo> checkReaderPsw(@Query("idCard") String idCard, @Query("idPassword") String idPassword);

    @GET("number/getCirculateNumber")
    Observable<OrderFromVo> getOrderFromNumber();

    @GET("number/getBorrowNumber")
    Observable<OrderFromVo> getBorrowNumber();

    @GET("number/getReturnNumber")
    Observable<OrderFromVo> getReturnNumber();

    @PUT("libraryreader/{readerId}/behalfPay")
    Observable<AddDepositVo> addDeposit(@Path("readerId") String readerId, @Body RequestBody body);


    @GET("librarybook/getBorrowBookByHallCodeAndBarNumber")
    Observable<BookInfoVo> getBookInfo(@Query("barNumber") String barNumber,
                                       @Query("readerId") String readerId,
                                       @Query("stayLibraryHallCode") String stayLibraryHallCode);

    @POST("libraryreader/{readerId}/borrowBooks")
    Observable<BorrowBookVo> borrowBook(@Path("readerId") String readerId, @Body RequestBody body);

    @POST("libraryreader/returnBook")
    Observable<ReturnBookVo> returnBook(@Body RequestBody body);

    @GET("libraryreader/getBorrowBooks")
    Observable<LostBookVo> getLostBookList(@Query("readerId") String readerId);

    @POST("libraryreader/{readerId}/compensateBookAlone")
    Observable<LostBookResultVo> lostBook(@Path("readerId") String readerId, @Body RequestBody body);

    @PUT("libraryreader/{readerId}/behalfWithdrawDeposit")
    Observable<ReturnDepositResultVo> refundDeposit(@Path("readerId") String readerId, @Body RequestBody body);

    @PUT("libraryreader/updateReaderPhoneWithPass")
    Observable<ReaderPwdModifyVo> modifyReaderPwdOrPhone(@Body RequestBody body);

    @GET("librarybookborrower/guard")
    Observable<EntranceGuardVo> entranceCheck(@Query("barNumber") String barNumber);

    //馆际流通
    //流出管理
    //查询流出状态列表
    @GET("statistics/getOutflow")
    Observable<SingleSelectionConditionVo> getFlowManageStateList();

    //查询本馆操作员列表 -(使用位置：1.流出流入管理选择操作员2.统计分析界面选择操作员)
    @GET("statistics/getHallCodeUserName")
    Observable<SingleSelectionConditionVo> getLibraryOperatorList(@Query("hallCode") String hallCode);

    //查询同流通范围的馆 -（位置：流出管理新增）
    @GET("circulate/getCirculateHallCodesByMatch")
    Observable<SameRangeLibraryListVo> searchSameRangeLibraryListByCondition(@Query("pageNo") int pageNo, @Query("pageCount") int pageCount,
                                                                             @Query("hallCode") String hallCode, @Query("grepValue") String grepValue);

    //增加流出单的书籍
    @POST("circulate/addLibraryCirculateMap")
    Observable<FlowManageAddNewBookInfoVo> getFlowManageAddNewBook(@Body RequestBody body);

    //删除流出单的书籍
    @DELETE("circulate/{circulateId}/map/{id}/deleteCirculateMap")
    Observable<FlowManageDeleteBookVo> deleteFlowManageBookInfo(@Path("circulateId") String circulateId, @Path("id") String id);

    //流出管理-发送添加的新书清单
    @PUT("circulate/{circulateId}/send")
    Observable<FlowManageSendBookResultVo> sendFlowManageNewBookList(@Path("circulateId") String circulateId, @Body RequestBody body);

    //查询流出管理列表
    @GET("circulate/getOutCirculates")
    Observable<FlowManagementListVo> getFlowManagementList(@QueryMap Map<String, String> map);

    /**
     * 查询每单详情(流入流出通用)
     *
     * @return
     */
    @GET("circulate/{id}/getCirculateBooksByCirculateId")
    Observable<FlowManageAddBookInfoVo> getFlowManageSingDetail(@Path("id") String circulateId, @Query("pageNo") int pageNum, @Query("pageCount") int pageSize);

    //获取流出管理-流入馆信息
    @GET("circulate/{id}/getInLibrary")
    Observable<InLibraryOperatorVo> getInLibraryOperatorInfo(@Path("id") String circulateId, @Query("circulateStatus") int circulateStatus);

    //获取流入管理-流出馆信息
    @GET("circulate/{id}/getOutLibrary")
    Observable<OutLibraryOperatorVo> getOutLibraryOperatorInfo(@Path("id") String circulateId);

    /**
     * 撤回本单
     *
     * @param circulateId 流通ID
     * @return
     */
    @PUT("circulate/{circulateId}/notsend")
    Observable<FlowManageOutReCallVo> withdrawThisSingle(@Path("circulateId") String circulateId, @Body RequestBody body);

    /**
     * 直接删除本单
     *
     * @param circulateId 流通ID
     * @return
     */
    @DELETE("circulate/{circulateId}/deleteCirculate")
    Observable<FlowManageDeleteSingleVo> deleteFlowManageSingle(@Path("circulateId") String circulateId);

    /**
     * 清点删除流出清单图书列表
     *
     * @return
     */
    @HTTP(method = "DELETE", path = "circulate/inventoryDelete", hasBody = true)
    Observable<FlowManageDeleteSingleVo> outDeleteFlowManageSingleCountBook(@Body RequestBody body);

    //流入管理
    //查询流入状态列表
    @GET("statistics/getInflow")
    Observable<SingleSelectionConditionVo> getIntoManageStateList();

    //查询流入管理列表
    @GET("circulate/getInCirculates")
    Observable<IntoManagementListVo> getIntoManageSingleList(@QueryMap Map<String, String> map);

    /**
     * 签收本单
     *
     * @param circulateId 流通单号id
     * @param body        签收人id
     * @return
     */
    @PUT("circulate/{circulateId}/sign")
    Observable<IntoManageSignThisSingleVo> signThisSingle(@Path("circulateId") String circulateId, @Body RequestBody body);

    /**
     * 拒绝本单
     *
     * @param circulateId 流通单号id
     * @param body        签收人id
     * @return
     */
    @PUT("circulate/{circulateId}/notsign")
    Observable<IntoManageSignThisSingleVo> rejectThisSingle(@Path("circulateId") String circulateId, @Body RequestBody body);

    //藏书统计馆号
    @GET("statistics/getHallCodeLibraryStatics")
    Observable<SingleSelectionConditionVo> getHallCodeLibraryStatics(@Query("hallCode") String hallCode);

    //在借统计馆号
    @GET("statistics/getHallCodeInBorrower")
    Observable<SingleSelectionConditionVo> getHallCodeInBorrower(@Query("hallCode") String hallCode);

    //藏书统计书籍状态
    @GET("statistics/getStatusLibraryStatics")
    Observable<SingleSelectionConditionVo> getStatusLibraryStatics();

    //库位
    @GET("statistics/getStorerooms")
    Observable<SingleSelectionConditionVo> getStorerooms();

    //是否绑定RFID
    @GET("statistics/getRfidLibraryStatics")
    Observable<SingleSelectionConditionVo> getRfidLibraryStatics();

    //在借统计书籍状态
    @GET("statistics/getStatusInBorrower")
    Observable<SingleSelectionConditionVo> getStatusInBorrower();

    //借书统计馆号
    @GET("statistics/getHallCodeBorrowerBooks")
    Observable<SingleSelectionConditionVo> getHallCodeBorrowerBooks(@Query("hallCode") String hallCode);

    //操作员
    @GET("statistics/getUsers")
    Observable<SingleSelectionConditionVo> getUsers(@Query("hallCode") String hallCode,
                                                    @Query("name") String name,
                                                    @Query("valueEqualDesc") int valueEqualDesc);

    //免单操作员
    @GET("statistics/getUsersPenaltyFreeApply")
    Observable<SingleSelectionConditionVo> getUsersPenaltyFreeApply();

    //流出状态
    @GET("statistics/getStatusOutCirculite")
    Observable<SingleSelectionConditionVo> getStatusOutCirculite();

    //流入状态
    @GET("statistics/getStatusInCirculite")
    Observable<SingleSelectionConditionVo> getStatusInCirculite();

    //收款统计项目条件
    @GET("statistics/getOperationGatheringStatistics")
    Observable<SingleSelectionConditionVo> getOperationGatheringStatistics();

    //陪书统计馆号
    @GET("statistics/getHallCodeCompensateBooks")
    Observable<SingleSelectionConditionVo> getHallCodeCompensateBooks(@Query("hallCode") String hallCode);

    //销售统计馆号
    @GET("statistics/getHallCodeBookSells")
    Observable<SingleSelectionConditionVo> getHallCodeBookSells(@Query("hallCode") String hallCode);

    //读者统计馆号
    @GET("statistics/getHallCodeReaderStatistics")
    Observable<SingleSelectionConditionVo> getHallCodeReaderStatistics();

    //读者统计读者类型
    @GET("statistics/getTypeReaderStatistics")
    Observable<SingleSelectionConditionVo> getTypeReaderStatistics();

    //免单统计状态
    @GET("statistics/getStatusPenaltyFreeApplys")
    Observable<SingleSelectionConditionVo> getStatusPenaltyFreeApplys();

    //借书统计,还书统计中的馆号
    @GET("statistics/getHallCode")
    Observable<StatisticsHallCodeListVo> getStatisticsHallCodeList(@Query("hallCode") String hallCode);

    //陪书统计
    @GET("statistics/queryHallCode")
    Observable<StatisticsHallCodeListVo> getLostStatisticsHallCodeList(@Query("hallCode") String hallCode);

    //销售统计
    @GET("statistics/querySoldHallCode")
    Observable<StatisticsHallCodeListVo> getSoldStatisticsHallCodeList(@Query("hallCode") String hallCode);

    //借书统计
    @GET("statistics/getBorrowerBooks")
    Observable<BorrowBookStatisticsVo> getBorrowBookStatisticsList(@QueryMap Map<String, String> map);

    //还书统计
    @GET("statistics/getReturnBooks")
    Observable<ReturnBookStatisticsVo> getReturnBookStatisticsList(@QueryMap Map<String, String> map);

    //读者统计
    @GET("statistics/getReaderStatistics")
    Observable<ReaderStatisticsVo> getReaderStatisticsList(@QueryMap Map<String, String> map);

    //赔书统计
    @GET("statistics/getCompensateBooks")
    Observable<LostBookStatisticsVo> getLostBookStatisticsList(@QueryMap Map<String, String> map);

    //销售统计
    @GET("statistics/getBookSells")
    Observable<SellBookStatisticsVo> getSellBookStatisticsList(@QueryMap Map<String, String> map);

    //在借统计
    @GET("statistics/getInBorrower")
    Observable<BorrowingBookStatisticsVo> getBorrowingBookStatisticsList(@QueryMap Map<String, String> map);

    //藏书统计
    @GET("statistics/getLibraryStatics")
    Observable<CollectingBookStatisticsVo> getCollectionBookStatisticsList(@QueryMap Map<String, String> map);

    //收款统计
    @GET("statistics/getGatheringStatistics")
    Observable<CollectingStatisticsVo> getCollectingStatisticsList(@QueryMap Map<String, String> map);

    //免单统计
    @GET("statistics/getPenaltyFreeApplys")
    Observable<PenaltyFreeStatisticsVo> getPenaltyFreeApplys(@QueryMap Map<String, String> map);

    //更新APP
    @GET("version/lastest")
    Observable<UpdateAppVo> updateApp(@Query("version") String version, @Query("deviceType") int deviceType);

    //自动触发押金处理罚金问题
    @PUT("libraryreader/{readerId}/handlepenalty")
    Observable<AutoProcessDepositPenaltyVo> automaticProcessingDepositPenalties(@Path("readerId") String readerId);

    @PUT("libraryreader/{readerId}/handlePenaltyAlone")
    Observable<PenaltyDealResultVo> autoDealPenalty(@Path("readerId") String readerId);

    @GET("areaManager/queryProvince")
    Observable<SwitchCityVo> getProvinceList();

    @GET("areaManager/queryCityByProvinceCode")
    Observable<SwitchCityVo> getCityList(@Query("provinceCode") String provinceCode);

    @GET("areaManager/queryAreaByCityName")
    Observable<SwitchCityVo> getDistrictList(@Query("cityName") String cityName);

    @GET("sms/send")
    Observable<VerifyCodeVo> getMsgVerifyCode(@Query("phone") String phone, @Query("isContinue") int isContinue);

    @GET("/api/sms/sendLibraryAdminPhoneCode")
    Observable<VerifyCodeVo> getAdminVerifyCode();

    @PUT("/api/library/updateLibraryRefundAccount")
    Observable<ChangeRefundAccountVo> changeRefundAccount(@Body ArrayMap<String, Object> map);

    @POST("account/getAvailableBalanceInfo")
    Observable<LibraryAvailableBalanceVo> getAvailableBalance();

    @GET("account/getAccountTransLogs")
    Observable<LibraryDepositTransLogVo> getDepositTransLog(@Query("pageNo") int pageNo, @Query("pageCount") int pageCount);

    @PUT("libraryreader/{readerId}/payPenalty")
    Observable<PayFineVo> payFine(@Path("readerId") String readerId, @Body ArrayMap<String, String> map);

    @POST("libraryreader/{readerId}/payCompensateBooks")
    Observable<LostBookResultVo> payCompenStateBooks(@Path("readerId") String readerId, @Body RequestBody body);

    @GET("sysHelp/guide")
    Observable<HelpInfoVo> getHelpList();

    @POST("account/applyOutMoney")
    Observable<WithdrawDepositVo> requestWithdrawDeposit(@Body RequestBody body);

    @POST("account/getAvailableBalanceInfo")
    Observable<RefundInfoVo> requestRefundInfo(@Body RequestBody body);

    @POST("pay/wechat/unifiedorder")
    Observable<WXPayInfoVo> requestWXPayInfo(@Body RequestBody body);

    @POST("pay/wechat/status")
    Observable<PayResultVo> requestWXPayResult(@Body RequestBody body);

    @POST("pay/ali/unifiedorder")
    Observable<AliPayInfoVo> requestAliPayInfo(@Body RequestBody body);

    @POST("pay/ali/status")
    Observable<PayResultVo> requestAliPayResult(@Body RequestBody body);

    @PUT("libraryreader/update")
    Observable<RegisterVo> updateReaderInfo(@Body ArrayMap<String, Object> map);

    @POST("penaltyfree/applyPenaltyFree")
    Observable<ApplyPenaltyFreeVo> applyPenaltyFree(@Body RequestBody body);

    @GET("sms/sendVCodeByforgetPwd")
    Observable<VerifyCodeVo> sendVerifyForgetPwd(@Query("hallCode") String hallCode, @Query("phone") String phone);

    @POST("libraryuser/checkHallCodeWithPhoneAndCode")
    Observable<VerifyIdentityVo> verifyIdentity(@Body RequestBody body);

    @POST("libraryuser/resetPassword")
    Observable<ResetPwdVo> resetPwd(@Body RequestBody body);

    //查询分馆
    @GET("statistics/queryPavilionLevel")
    Observable<BranchLibVo> queryPavilionLevel(@Query("hallCode") String hallCode);

    @PUT("libraryuser/setFirstPassword")
    Observable<SetFirstOperatorPswVo> setFirstPassword(@Body ArrayMap<String, Object> map);

    @GET("libraryreader/getPenaltyInfoList")
    Observable<PenaltyListVo> getPenaltyList(@Query("readerId") String readerId);
}
