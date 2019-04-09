package com.tzpt.cloudlibrary.modle.remote;


import android.support.v4.util.ArrayMap;

import com.tzpt.cloudlibrary.modle.remote.newpojo.ActionListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.AliPayInfoVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.ApplyActionResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.AttentionLibResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BarCodeReusltVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseDataResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseResultEntityVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BillInfoVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BorrowBookDetailInfoVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BorrowBookListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.CommentIndexVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.CommentInfoVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.CommentReplyVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.CommonReturnBookLibListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.CompensateDepositInfoVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.DepositCategoryVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.DiscussReplyVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.EBookListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.FaceImageVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.LibDepositListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.MessageBoardDetailVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.MessageBoardReplyListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.MessageBoardReplyVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.ModifyPwdResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.MyMessageVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.NewsPraiseResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.NoteListItemVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.NoteListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.NoteModifyResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.OperateReservationBookResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.OverdueMsgListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.PayPenaltyResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.PayResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.PraiseBookResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.PreUserInfoVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.PublishDiscussResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.RecommendBookLibListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.RecommendBookResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.RenewBorrowBookResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.ReservationBookListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.ResetPwdResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.SelfBorrowBookInfoVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.SelfBorrowBookResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.SelfBorrowScanFirstVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.SelfBuyBookDetailVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.SelfBuyBookResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.SelfBuyBookShelfVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.SelfBuyBookVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.UnreadMsgCountVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.UnreadOverdueMsgCountVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.UserDepositLimitRegulationVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.UserDepositVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.UserHeadListResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.UserInfoVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.VerifyCodeVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.VerifyIDCardVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.VideoCollectListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.VideoCollectStatusVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.VideoFavoritesVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.WXPayInfoVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.WithdrawResultVo;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by Administrator on 2017/11/14.
 */

public interface CloudLibraryUserApiService {

    @GET("userApp/reader/index")
    Observable<BaseResultEntityVo<UserInfoVo>> getUserInfo(@Query("idCard") String idCard);

    @GET("userApp/sms/modifySend")
    Observable<BaseResultEntityVo<VerifyCodeVo>> getVerifyCode(@Query("phone") String phone);

    @POST("/userApp/reader/modifyPhone")
    Observable<BaseResultEntityVo<VerifyCodeVo>> checkVerifyCode(@Body RequestBody body);

    @GET("userApp/reader/imgList")
    Observable<BaseResultEntityVo<UserHeadListResultVo>> getHeadImgList();

    @Multipart
    @POST("userApp/reader/updateImg")
    Observable<BaseResultEntityVo<BaseDataResultVo>> modifyHeadImg(@PartMap ArrayMap<String, RequestBody> map, @Part MultipartBody.Part file);


    @GET("userApp/readerBorrower/libBookReader")
    Observable<BaseResultEntityVo<SelfBorrowScanFirstVo>> selfBorrowFirstScan(@Query("barNumber") String barNumber,
                                                                              @Query("readerId") String readerId);

    @GET("userApp/readerBorrower/bookInfo")
    Observable<BaseResultEntityVo<SelfBorrowBookInfoVo>> selfBorrowBookInfo(@Query("barNumber") String barNumber,
                                                                            @Query("stayHallCode") String stayHallCode,
                                                                            @Query("readerId") String readerId);

    @POST("userApp/readerBorrower/borrowerbookAlone")
    Observable<BaseResultEntityVo<SelfBorrowBookResultVo>> selfBorrowBook(@Body RequestBody body);

    @POST("userApp/ReaderRecommend/recommendBook")
    Observable<BaseResultEntityVo<RecommendBookResultVo>> recommendBookByIsbn(@Body RequestBody body);

    @POST("userApp/ReaderRecommend/recommendLib")
    Observable<BaseResultEntityVo<RecommendBookLibListVo>> getRecommendBookLibList(@Body RequestBody body);

    @GET("userApp/reader/appointList")
    Observable<BaseResultEntityVo<ReservationBookListVo>> getReservationBookList(@Query("pageNo") int pageNo,
                                                                                 @Query("pageCount") int pageCount,
                                                                                 @Query("idCard") String idCard);

    @POST("userApp/reader/appoint")
    Observable<BaseResultEntityVo<OperateReservationBookResultVo>> operateReservationBook(@Body RequestBody body);

    @POST("userApp/activity/applyList")
    Observable<BaseResultEntityVo<ActionListVo>> getAppliedActionList(@Body RequestBody body);

    @GET("userApp/activity/apply")
    Observable<BaseResultEntityVo<ApplyActionResultVo>> applyAction(@Query("activityId") int activityId,
                                                                    @Query("idCard") String idCard,
                                                                    @Query("name") String name,
                                                                    @Query("phone") String phone);

    @GET("userApp/readerBorrower/list")
    Observable<BaseResultEntityVo<BorrowBookListVo>> getBorrowBookList(@Query("idCard") String idCard,
                                                                       @Query("pageNo") int pageNo,
                                                                       @Query("pageCount") int pageCount,
                                                                       @Query("status") int status);

    //新接口0全部 1当前借阅 2历史借阅
    //新需求调整了借阅模块 以前历史借阅和当前借阅都是独立的接口 现在借阅一个接口 传不同入参borrowType 0全部 1当前借阅 2历史借阅
    @GET("userApp/readerBorrower/borrowList")
    Observable<BaseResultEntityVo<BorrowBookListVo>> getHistoryBookList(@Query("idCard") String idCard,
                                                                        @Query("pageNo") int pageNo,
                                                                        @Query("pageCount") int pageCount,
                                                                        @Query("borrowType") int type);

    @POST("userApp/readerBorrower/renew")
    Observable<BaseResultEntityVo<RenewBorrowBookResultVo>> renewBorrowBook(@Body RequestBody body);

    @POST("userApp/reader/support")
    Observable<BaseResultEntityVo<PraiseBookResultVo>> praiseBook(@Body RequestBody body);

    @GET("userApp/readerBorrower/{borrowId}")
    Observable<BaseResultEntityVo<BorrowBookDetailInfoVo>> getBorrowBookDetail(@Path("borrowId") long borrowerBookId);

    @GET("userApp/readerBorrower/lib")
    Observable<BaseResultEntityVo<CommonReturnBookLibListVo>> getReturnLibList(@Query("id") long booId, @Query("lngLat") String lngLat);

    @POST("userApp/reader/updateNote")
    Observable<BaseResultEntityVo<NoteModifyResultVo>> noteModify(@Body RequestBody body);

    @POST("userApp/reader/addNote")
    Observable<BaseResultEntityVo<NoteModifyResultVo>> noteAdd(@Body RequestBody body);

    @HTTP(method = "DELETE", path = "userApp/reader/delNote", hasBody = true)
    Observable<BaseResultEntityVo<BaseDataResultVo>> delNote(@Body RequestBody body);

    @GET("userApp/reader/notes")
    Observable<BaseResultEntityVo<NoteListVo>> getNoteList(@Query("idCard") String idCard,
                                                           @Query("pageNo") int pageNo,
                                                           @Query("pageCount") int pageCount);

    @GET("api/libraryreader/getCompensationDepositInfo")
    Observable<BaseResultEntityVo<CompensateDepositInfoVo>> getCompensateDepositInfo(@QueryMap ArrayMap<String, Object> map);

    @GET("api/libraryreader/getDepositInfo")
    Observable<BaseResultEntityVo<UserDepositVo>> getDepositInfo(@QueryMap ArrayMap<String, Object> map);

    @GET("api/account/getAccountLimitRegulation")
    Observable<BaseResultEntityVo<UserDepositLimitRegulationVo>> getAccountLimitRegulation(@Query("userId") String userId);

    @GET("/api/libraryreader/getReaderTransLogs")
    Observable<BaseResultEntityVo<BillInfoVo>> getUserBill(@QueryMap ArrayMap<String, Object> map);

    @GET("/api/libraryreader/getTransNameReaderTransLogs")
    Observable<BaseResultEntityVo<DepositCategoryVo>> getDepositCategory();

    @POST("userApp/reader/updatePassword")
    Observable<BaseResultEntityVo<ModifyPwdResultVo>> modifyPwd(@Body RequestBody body);

    @GET("userApp/reader/customerDeposit")
    Observable<BaseResultEntityVo<LibDepositListVo>> getLibDeposit(@Query("idCard") String idCard,
                                                                   @Query("pageNo") int pageNo,
                                                                   @Query("pageCount") int pageCount);

    @GET("userApp/reader/validPhone")
    Observable<BaseResultEntityVo<VerifyIDCardVo>> verifyIDCard(@Query("idCard") String idCard);

    @GET("userApp/sms/resetPwdSend")
    Observable<BaseResultEntityVo<VerifyCodeVo>> getVerifyCodeForgetPwd(@Query("phone") String phone);

    @POST("userApp/reader/resetValidPhoneCode")
    Observable<BaseResultEntityVo<VerifyCodeVo>> checkVerifyCodeForgetPwd(@Body RequestBody body);

    @POST("userApp/reader/resetPassword")
    Observable<BaseResultEntityVo<ResetPwdResultVo>> resetForgetPwd(@Body RequestBody body);

    @POST("userApp/ReaderRecommend/recommendLibSearch")
    Observable<BaseResultEntityVo<RecommendBookLibListVo>> searchRecommendLib(@Body RequestBody body);

    @GET("userApp/news/praise")
    Observable<BaseResultEntityVo<NewsPraiseResultVo>> operateNewsPraise(@Query("identity") String identity, @Query("newsId") long newsId);

    @POST("userApp/news/pubComment")
    Observable<BaseResultEntityVo<PublishDiscussResultVo>> publishNewsDiscuss(@Body RequestBody body);

    @PUT("api/libraryreader/{readerId}/handlePenaltyAlone")
    Observable<BaseResultEntityVo<PayPenaltyResultVo>> dealUserLibPenalty(@Path("readerId") String readerId, @Body RequestBody body);

    @POST("api/libraryreader/{readerId}/compensateOneBook")
    Observable<BaseResultEntityVo<BaseDataResultVo>> userCompensateBooks(@Path("readerId") String readerId, @Body RequestBody body);

    @Multipart
    @POST("userApp/library/pubMsg")
    Observable<BaseResultEntityVo<BaseDataResultVo>> pubMsg(@PartMap ArrayMap<String, RequestBody> map, @Part MultipartBody.Part file);

    @POST("userApp/reader/myAllMessage")
    Observable<BaseResultEntityVo<MyMessageVo>> getMyMessageList(@Body ArrayMap<String, Object> map);

    @POST("api/pay/wechat/unifiedorder")
    Observable<BaseResultEntityVo<WXPayInfoVo>> requestWXPayInfo(@Body RequestBody body);

    @POST("api/pay/wechat/status")
    Observable<BaseResultEntityVo<PayResultVo>> requestWXPayResult(@Body RequestBody body);

    @POST("api/pay/ali/unifiedorder")
    Observable<BaseResultEntityVo<AliPayInfoVo>> requestAliPayInfo(@Body RequestBody body);

    @POST("api/pay/ali/status")
    Observable<BaseResultEntityVo<PayResultVo>> requestAliPayResult(@Body RequestBody body);

    @POST("userApp/news/pubReply")
    Observable<BaseResultEntityVo<CommentReplyVo>> replyMsg(@Body ArrayMap<String, Object> map);

    @POST("api/pay/transfersAccount")
    Observable<BaseResultEntityVo<WithdrawResultVo>> requestWithdraw(@Body RequestBody body);

    @POST("userApp/reader/unreadCountAll")
    Observable<BaseResultEntityVo<UnreadMsgCountVo>> getUnreadMsgCount(@Body RequestBody body);

    @POST("userApp/news/delComment")
    Observable<BaseResultEntityVo<BaseDataResultVo>> delReaderMsg(@Body ArrayMap<String, Object> map);

    @POST("userApp/news/delReply")
    Observable<BaseResultEntityVo<BaseDataResultVo>> delReplyReaderMsg(@Body ArrayMap<String, Object> map);

    @GET("userApp/news/commentPraise")
    Observable<BaseResultEntityVo<BaseDataResultVo>> commentPraise(@Query("commentId") long commentId, @Query("identity") String identity);

    @GET("userApp/news/replyPraise")
    Observable<BaseResultEntityVo<BaseDataResultVo>> replyPraise(@Query("replyId") long replyId, @Query("identity") String identity);

    @GET("userApp/news/replyList")
    Observable<BaseResultEntityVo<DiscussReplyVo>> replyList(@QueryMap ArrayMap<String, Object> map);

    @GET("userApp/reader/commentIndex")
    Observable<BaseResultEntityVo<CommentIndexVo>> commentIndex(@Query("commentId") long commentId);

    @GET("userApp/reader/replyIndex")
    Observable<BaseResultEntityVo<CommentIndexVo>> replyIndex(@Query("replyId") long replyId);

    @GET("userApp/news/commentInfo")
    Observable<BaseResultEntityVo<CommentInfoVo>> commentInfo(@QueryMap ArrayMap<String, Object> map);

    @POST("userApp/reader/readMessage")
    Observable<BaseResultEntityVo<BaseDataResultVo>> readMsg(@Body ArrayMap<String, Object> map);

    @GET("userApp/reader/preUserInfo")
    Observable<BaseResultEntityVo<PreUserInfoVo>> preUserInfo(@Query("phone") String phone);

    @GET("userApp/reader/getFaceImage")
    Observable<BaseResultEntityVo<FaceImageVo>> getFaceRecognitionImage(@Query("idCard") String idCard);

    @Multipart
    @POST("userApp/reader/uploadReaderFace")
    Observable<BaseResultEntityVo<BaseDataResultVo>> updateUserFaceImage(@PartMap ArrayMap<String, RequestBody> map, @Part MultipartBody.Part file);

    @GET("userApp/overdue/queryReaderMessagesByIdCard")
    Observable<BaseResultEntityVo<OverdueMsgListVo>> getOverdueMsg(@Query("idCard") String idCard,
                                                                   @Query("pageNo") int pageNo,
                                                                   @Query("pageCount") int pageCount);

    @POST("userApp/overdue/updateStatus")
    Observable<BaseResultEntityVo<BaseDataResultVo>> readOverdueMsg(@Body RequestBody body);

    @GET("userApp/overdue/getMessageCountByIdCard")
    Observable<BaseResultEntityVo<UnreadOverdueMsgCountVo>> getUnreadOverdueMsgCount(@Query("idCard") String idCard);

    @PUT("userApp/barCode/{readerId}/refresh")
    Observable<BaseResultEntityVo<BarCodeReusltVo>> refreshBarCode(@Path("readerId") String readerId);

    @PUT("userApp/barCode/{readerId}/report")
    Observable<BaseResultEntityVo<BaseDataResultVo>> reportBarCodeCount(@Path("readerId") String readerId, @Body RequestBody body);

    @POST("userApp/video/videoFavorites")
    Observable<BaseResultEntityVo<VideoFavoritesVo>> collectionVideo(@Body ArrayMap<String, Object> map);

    @POST("userApp/video/cancelVideoFavorites")
    Observable<BaseResultEntityVo<VideoFavoritesVo>> cancelCollectionVideo(@Body RequestBody body);

    @GET("userApp/video/favorStatus/{videosId}/{readerId}")
    Observable<BaseResultEntityVo<VideoCollectStatusVo>> getCollectionVideoStatus(@Path("videosId") long videosId, @Path("readerId") String readerId);

    @GET("userApp/video/videoFavorites/{readerId}")
    Observable<BaseResultEntityVo<VideoCollectListVo>> getCollectVideoSetList(@Path("readerId") String readerId, @QueryMap ArrayMap<String, Object> parameterMap);

    @GET("userApp/ebook/favorStatus/{ebookId}/{readerId}")
    Observable<BaseResultEntityVo<VideoCollectStatusVo>> getEBookCollectStatus(@Path("ebookId") String ebookId, @Path("readerId") String readerId);

    @GET("userApp/ebook/ebookFavorites/{readerId}")
    Observable<BaseResultEntityVo<EBookListVo>> getCollectEBookList(@Path("readerId") String readerId, @Query("pageNo") int pageNo, @Query("pageCount") int pageCount);

    @POST("userApp/ebook/ebookFavorites")
    Observable<BaseResultEntityVo<VideoFavoritesVo>> collectionEBook(@Body ArrayMap<String, Object> map);

    @POST("userApp/ebook/cancelEbookFavorites")
    Observable<BaseResultEntityVo<VideoFavoritesVo>> cancelCollectionEBook(@Body RequestBody body);

    @POST("userApp/video/{sectionId}/recordWatch")
    Observable<BaseResultEntityVo> recordWatch(@Path("sectionId") long sectionId, @Body ArrayMap<String, Object> parameterMap);

    @GET("userApp/selfBuy/{fullBarNumber}/{idCard}/scan")
    Observable<BaseResultEntityVo<SelfBuyBookVo>> getSelfBuyBookInfo(@Path("fullBarNumber") String fullBarNumber, @Path("idCard") String idCard, @Query("stayLibraryHallCode") String stayLibraryHallCode);

    @POST("userApp/selfBuy/{readerId}/buy")
    Observable<BaseResultEntityVo<SelfBuyBookResultVo>> selfBuyBook(@Path("readerId") String readerId, @Body RequestBody body);

    @GET("userApp/selfBuy/{readerId}/books")
    Observable<BaseResultEntityVo<SelfBuyBookShelfVo>> getSelfBuyBookShelfList(@Path("readerId") String readerId, @Query("pageNo") int pageNo, @Query("pageCount") int pageCount);

    @GET("userApp/selfBuy/{buyId}")
    Observable<BaseResultEntityVo<SelfBuyBookDetailVo>> getSelfBuyBookDetail(@Path("buyId") long buyId);

    @GET("userApp/reader/note/{noteId}")
    Observable<BaseResultEntityVo<NoteListItemVo>> getSelfBuyBookNote(@Path("noteId") int noteId);

    @POST("userApp/selfBuy/praise/{buyId}/{operateType}")
    Observable<BaseResultEntityVo<BaseDataResultVo>> praiseSelfBuyBook(@Path("buyId") long buyId, @Path("operateType") int operateType);

    @POST("userApp/reader/follow/lib")
    Observable<BaseResultEntityVo<AttentionLibResultVo>> attentionLib(@Body RequestBody body);

    @GET("userApp/reader/follow/del/{readerId}/{libCode}")
    Observable<BaseResultEntityVo<AttentionLibResultVo>> unAttentionLib(@Path("readerId") String readerId, @Path("libCode") String libCode);

    @PUT("userApp/reader/updateNickName")
    Observable<BaseResultEntityVo<BaseDataResultVo>> updateNickName(@Body RequestBody body);

    @GET("userApp/reader/messageIndex")
    Observable<BaseResultEntityVo<CommentIndexVo>> getMessageBoardIndex(@Query("messageId") long messageId);

    @GET("userApp/reader/messageReplyIndex")
    Observable<BaseResultEntityVo<CommentIndexVo>> getMessageBoardReplyIndex(@Query("replyId") long replyId);

    @POST("userApp/library/pubMsgReply")
    Observable<BaseResultEntityVo<MessageBoardReplyVo>> replyMessageBoard(@Body ArrayMap<String, Object> map);

    @POST("userApp/library/messagePraise")
    Observable<BaseResultEntityVo<BaseDataResultVo>> messagePraise(@Body RequestBody body);

    @GET("userApp/library/messageInfo")
    Observable<BaseResultEntityVo<MessageBoardDetailVo>> getMessageBoardDetail(@QueryMap ArrayMap<String, Object> map);

    @GET("userApp/library/messageReplyList")
    Observable<BaseResultEntityVo<MessageBoardReplyListVo>> getMessageBoardDetailReplyList(@QueryMap ArrayMap<String, Object> map);

    @POST("userApp/library/delMsgAndPraiseAndReply")
    Observable<BaseResultEntityVo<BaseDataResultVo>> delOwnMsg(@Body ArrayMap<String, Object> map);

    @POST("userApp/library/delReply")
    Observable<BaseResultEntityVo<BaseDataResultVo>> delOwnReplyMsg(@Body ArrayMap<String, Object> map);

    @POST("userApp/library/replyPraise")
    Observable<BaseResultEntityVo<BaseDataResultVo>> replyMsgPraise(@Body ArrayMap body);

    @POST("userApp/reader/appointByBackstage")
    Observable<BaseResultEntityVo<OperateReservationBookResultVo>> reservationBook(@Body RequestBody body);
}
