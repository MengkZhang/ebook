package com.tzpt.cloundlibrary.manager.modle;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tzpt.cloundlibrary.manager.ManagerApplication;
import com.tzpt.cloundlibrary.manager.bean.BookInfoBean;
import com.tzpt.cloundlibrary.manager.bean.EntranceGuardResult;
import com.tzpt.cloundlibrary.manager.bean.FlowManageDetailBookInfoBean;
import com.tzpt.cloundlibrary.manager.bean.FlowManageListBean;
import com.tzpt.cloundlibrary.manager.bean.LibraryInfo;
import com.tzpt.cloundlibrary.manager.bean.LostBookInfo;
import com.tzpt.cloundlibrary.manager.bean.OrderNumberInfo;
import com.tzpt.cloundlibrary.manager.bean.PenaltyBean;
import com.tzpt.cloundlibrary.manager.bean.PenaltyDealInfo;
import com.tzpt.cloundlibrary.manager.bean.ReaderDepositInfo;
import com.tzpt.cloundlibrary.manager.bean.ReaderInfo;
import com.tzpt.cloundlibrary.manager.bean.SameRangeLibraryBean;
import com.tzpt.cloundlibrary.manager.bean.StatisticsConditionBean;
import com.tzpt.cloundlibrary.manager.bean.SwitchCityBean;
import com.tzpt.cloundlibrary.manager.bean.UsedDepositType;
import com.tzpt.cloundlibrary.manager.modle.local.DataService;
import com.tzpt.cloundlibrary.manager.modle.local.SharedPreferencesUtil;
import com.tzpt.cloundlibrary.manager.modle.remote.ManagerApi;
import com.tzpt.cloundlibrary.manager.modle.remote.exception.ExceptionEngine;
import com.tzpt.cloundlibrary.manager.modle.remote.exception.ServerException;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.AddDepositVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.AliPayInfoVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.ApplyPenaltyFreeVo;
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
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.SwitchCityVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.UpdateAppVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.VerifyCodeVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.VerifyIdentityVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.VerifyLibraryOperatorPswVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.WXPayInfoVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.WithdrawDepositVo;
import com.tzpt.cloundlibrary.manager.utils.MoneyUtils;
import com.tzpt.cloundlibrary.manager.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.functions.Func2;

import static com.tzpt.cloundlibrary.manager.base.BaseResponseCode.CODE_SUCCESS;
import static com.tzpt.cloundlibrary.manager.base.BaseResponseCode.ERROR_CODE_10000;
import static com.tzpt.cloundlibrary.manager.base.BaseResponseCode.ERROR_CODE_10001;
import static com.tzpt.cloundlibrary.manager.base.BaseResponseCode.ERROR_CODE_10002;
import static com.tzpt.cloundlibrary.manager.base.BaseResponseCode.ERROR_CODE_10003;
import static com.tzpt.cloundlibrary.manager.base.BaseResponseCode.ERROR_CODE_10004;
import static com.tzpt.cloundlibrary.manager.base.BaseResponseCode.ERROR_CODE_10005;
import static com.tzpt.cloundlibrary.manager.base.BaseResponseCode.ERROR_CODE_10006;
import static com.tzpt.cloundlibrary.manager.base.BaseResponseCode.ERROR_CODE_10007;
import static com.tzpt.cloundlibrary.manager.base.BaseResponseCode.ERROR_CODE_2305;
import static com.tzpt.cloundlibrary.manager.base.BaseResponseCode.ERROR_CODE_3103;
import static com.tzpt.cloundlibrary.manager.base.BaseResponseCode.ERROR_CODE_KICK_OUT;

/**
 * Created by Administrator on 2017/6/21.
 */

public class DataRepository {
    private static DataRepository mInstance;
    private List<BookInfoBean> mBookList = new ArrayList<>();
    //订单号
    private OrderNumberInfo mOrderNumberInfo = new OrderNumberInfo();
    private LibraryInfo mLibraryInfo;

    private FlowManageListBean mFlowManageListBean;

    private List<FlowManageDetailBookInfoBean> mDetailBookList;

    private StatisticsConditionBean mStatisticsCondition;

    public static DataRepository getInstance() {
        if (mInstance == null) {
            mInstance = new DataRepository();
        }
        return mInstance;
    }

    private DataRepository() {
    }

    private class HttpResultFunc<T> implements Func1<Throwable, Observable<T>> {
        @Override
        public Observable<T> call(Throwable throwable) {
            return Observable.error(ExceptionEngine.handleException(throwable));
        }
    }

    private void saveToken(String token) {
        SharedPreferencesUtil.getInstance().putString("TOKEN", token);
    }

    public String getToken() {
        return SharedPreferencesUtil.getInstance().getString("TOKEN");
    }

    public void clearToken() {
        SharedPreferencesUtil.getInstance().remove("TOKEN");
    }

    public void setStatisticsCondition(StatisticsConditionBean condition) {
        mStatisticsCondition = condition;
    }

    public StatisticsConditionBean getStatisticsCondition() {
        return mStatisticsCondition;
    }

    public void delStatisticsCondition() {
        mStatisticsCondition = null;
    }

    //提现密码error
    public Observable<WithdrawDepositVo> refundAdminDepositError(final int status, final int errorCode) {
        return Observable.create(new Observable.OnSubscribe<WithdrawDepositVo>() {
            @Override
            public void call(Subscriber<? super WithdrawDepositVo> subscriber) {
                WithdrawDepositVo refundDepositVo = new WithdrawDepositVo();
                refundDepositVo.status = status;
                refundDepositVo.data = refundDepositVo.new ResponseData();
                refundDepositVo.data.errorCode = errorCode;
                subscriber.onNext(refundDepositVo);
                subscriber.onCompleted();
            }
        });
    }

    public LibraryInfo getLibraryInfo() {
        if (mLibraryInfo == null) {
            mLibraryInfo = SharedPreferencesUtil.getInstance().getObject("LibraryInfo", LibraryInfo.class);
        }
        return mLibraryInfo;
    }

    public void delLibraryInfo() {
        mLibraryInfo = null;
    }

    public Observable<Boolean> loginAdmin(String libName, String userName, String pwd) {
        return ManagerApi.getInstance().login(libName, userName, pwd)
                .flatMap(new Func1<LoginVo, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(LoginVo loginVo) {
                        if (loginVo.status == CODE_SUCCESS) {
                            ManagerApplication.TOKEN = loginVo.data.value;
                            DataRepository.getInstance().saveToken(loginVo.data.value);

                            return ManagerApi.getInstance().getLoginUserInfo()
                                    .map(new Func1<LibraryUserInfoVo, Boolean>() {
                                        @Override
                                        public Boolean call(LibraryUserInfoVo libraryUserInfoVo) {
                                            if (libraryUserInfoVo.status == CODE_SUCCESS) {
                                                //操作员是第一次登录,需要重新设置密码
                                                if (libraryUserInfoVo.data.isLogined == 1) {
                                                    throw new ServerException(ERROR_CODE_10007, "用户第一次登录");
                                                } else {
                                                    //保存登录操作员信息
                                                    saveLibraryInfo(libraryUserInfoVo);
                                                }
                                            } else {
                                                throw new ServerException(libraryUserInfoVo.data.errorCode, "");
                                            }
                                            return null;
                                        }
                                    });
                        } else {
                            throw new ServerException(loginVo.data.errorCode, "");
                        }
                    }
                })
                .onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    public Observable<VerifyLibraryOperatorPswVo> checkOperatorPsw(String libName, String userName, String pwd) {
        return ManagerApi.getInstance().checkOperatorPsw(libName, userName, pwd);
    }

    public Observable<Boolean> checkOperatorPsw(String pwd) {
        if (pwd == null || pwd.equals("")) {
            return Observable.create(new Observable.OnSubscribe<Boolean>() {
                @Override
                public void call(Subscriber<? super Boolean> subscriber) {
                    throw new ServerException(ERROR_CODE_10006, "密码不能为空！");
                }
            }).onErrorResumeNext(new HttpResultFunc<Boolean>());
        }
        return ManagerApi.getInstance().checkOperatorPsw(mLibraryInfo.mHallCode, mLibraryInfo.mOperaterName, pwd)
                .map(new Func1<VerifyLibraryOperatorPswVo, Boolean>() {
                    @Override
                    public Boolean call(VerifyLibraryOperatorPswVo verifyLibraryOperatorPswVo) {
                        if (verifyLibraryOperatorPswVo.status == CODE_SUCCESS) {
                            return verifyLibraryOperatorPswVo.data.value;
                        } else {
                            throw new ServerException(verifyLibraryOperatorPswVo.data.errorCode, "");
                        }
                    }
                })
                .onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    public Observable<UpdateAppVo> getLoginUserInfoAndCheckUpdate(String version) {
        return Observable.zip(ManagerApi.getInstance().getLoginUserInfo(),
                ManagerApi.getInstance().updateApp(version),
                new Func2<LibraryUserInfoVo, UpdateAppVo, UpdateAppVo>() {
                    @Override
                    public UpdateAppVo call(LibraryUserInfoVo libraryUserInfoVo, UpdateAppVo updateAppVo) {
                        if (libraryUserInfoVo.status == CODE_SUCCESS) {
                            saveLibraryInfo(libraryUserInfoVo);
                            return updateAppVo;
                        } else {
                            throw new ServerException(libraryUserInfoVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<UpdateAppVo>());
    }

    public Observable<LightSelectVo> getLightSelect(String libraryCode) {
        return ManagerApi.getInstance().getLightSelect(libraryCode);
    }

//    public Observable<LightSelectResultVo> setLightSelect(String requestData) {
//        return ManagerApi.getInstance().setLightSelect(requestData);
//    }

    public Observable<Boolean> setLightSelect(String pwd, final JSONObject objectData) {
        if (mLibraryInfo == null) {
            return Observable.create(new Observable.OnSubscribe<Boolean>() {
                @Override
                public void call(Subscriber<? super Boolean> subscriber) {
                    throw new ServerException(ERROR_CODE_KICK_OUT, "管理员登录异常！");
                }
            }).onErrorResumeNext(new HttpResultFunc<Boolean>());
        }
        try {
            objectData.put("libraryCode", mLibraryInfo.mHallCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ManagerApi.getInstance().checkOperatorPsw(mLibraryInfo.mHallCode, mLibraryInfo.mOperaterName, pwd)
                .flatMap(new Func1<VerifyLibraryOperatorPswVo, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(VerifyLibraryOperatorPswVo verifyLibraryOperatorPswVo) {
                        if (verifyLibraryOperatorPswVo.status == CODE_SUCCESS) {
                            if (verifyLibraryOperatorPswVo.data.value) {
                                return ManagerApi.getInstance().setLightSelect(objectData.toString())
                                        .map(new Func1<LightSelectResultVo, Boolean>() {
                                            @Override
                                            public Boolean call(LightSelectResultVo lightSelectResultVo) {
                                                if (lightSelectResultVo.status == CODE_SUCCESS) {
                                                    return true;
                                                } else {
                                                    throw new ServerException(lightSelectResultVo.data.errorCode, "");
                                                }
                                            }
                                        });
                            } else {
                                throw new ServerException(ERROR_CODE_3103, "密码错误");
                            }
                        } else {
                            throw new ServerException(verifyLibraryOperatorPswVo.data.errorCode, "");
                        }
                    }
                })
                .onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    public Observable<ChangeOperatorPwdVo> changeOperatorPwd(String oldPwd, String newPwd) {
        return ManagerApi.getInstance().changeOperatorPwd(oldPwd, newPwd);
    }

    public Observable<MsgVo> getMsgList(int pageNum) {
        return ManagerApi.getInstance().getMsgList(pageNum, 10);
    }

    public Observable<ReadMsgVo> setReadStatus(long newsId) {
        return ManagerApi.getInstance().setReadStatus(newsId);
    }

    public Observable<NoReadMsgVo> getUnReadCount() {
        return ManagerApi.getInstance().getUnReadCount();
    }

    public Observable<ReaderInfo> getReaderInfo(String readerId) {
        return ManagerApi.getInstance().getReaderInfo(readerId)
                .map(new Func1<ReaderLoginInfoVo, ReaderInfo>() {
                    @Override
                    public ReaderInfo call(ReaderLoginInfoVo readerLoginInfoVo) {
                        if (readerLoginInfoVo.status == CODE_SUCCESS) {
                            return getReaderInfo(readerLoginInfoVo);
                        } else {
                            throw new ServerException(readerLoginInfoVo.data.errorCode,
                                    readerLoginInfoVo.data.message);
                        }
                    }
                })
                .onErrorResumeNext(new HttpResultFunc<ReaderInfo>());
    }

    public Observable<Double> getReaderPenalty(String readerId) {
        return ManagerApi.getInstance().getReaderInfo(readerId)
                .map(new Func1<ReaderLoginInfoVo, Double>() {
                    @Override
                    public Double call(ReaderLoginInfoVo readerLoginInfoVo) {
                        if (readerLoginInfoVo.status == CODE_SUCCESS) {
                            return readerLoginInfoVo.data.readerDeposit.notApplyPenalty;
                        } else {
                            throw new ServerException(readerLoginInfoVo.data.errorCode,
                                    readerLoginInfoVo.data.message);
                        }
                    }
                })
                .onErrorResumeNext(new HttpResultFunc<Double>());
    }

    public Observable<CheckRegisterVo> checkReaderAccount(String condition, String hallCode) {
        return ManagerApi.getInstance().checkReaderAccount(condition, hallCode);
    }

    public Observable<RegisterVo> register(String number, String name, String telNum, String pwd,
                                           String image, String gender, String hallCode, String code) {
        return ManagerApi.getInstance().register(number, name, telNum, pwd, image, gender, hallCode, code, mLibraryInfo.mReaderLimit);
    }

    public Observable<ReaderLoginVo> readerLogin(String requestData) {
        return ManagerApi.getInstance().readerLogin(requestData);
    }

    public Observable<VerifyCodeVo> sendCode(String telNum, int isContinue) {
        return ManagerApi.getInstance().getMsgVerifyCode(telNum, isContinue);
    }

    public Observable<VerifyCodeVo> getAdminVerifyCode() {
        return ManagerApi.getInstance().getAdminVerifyCode();
    }

    public Observable<ChangeRefundAccountVo> changeRefundAccount(String code, String refundAccount, String refundName) {
        return ManagerApi.getInstance().changeRefundAccount(code, refundAccount, refundName);
    }

    public Observable<OrderFromVo> getOrderFromNumber() {
        return ManagerApi.getInstance().getOrderFromNumber();
    }

    public Observable<Double> getLibBalance() {
        return ManagerApi.getInstance().getAvailableBalance()
                .map(new Func1<LibraryAvailableBalanceVo, Double>() {
                    @Override
                    public Double call(LibraryAvailableBalanceVo libraryAvailableBalanceVo) {
                        if (libraryAvailableBalanceVo.status == CODE_SUCCESS) {
                            return libraryAvailableBalanceVo.data.availableBalance;
                        } else {
                            throw new ServerException(libraryAvailableBalanceVo.data.errorCode, "");
                        }
                    }
                })
                .onErrorResumeNext(new HttpResultFunc<Double>());
    }

    public Observable<LibraryDepositTransLogVo> getDepositTransLog(int pageNo, int pageCount) {
        return ManagerApi.getInstance().getDepositTransLog(pageNo, pageCount);
    }

    public Observable<BookInfoBean> getBookInfo(String barNumber, final ReaderInfo readerInfo) {
        if (mLibraryInfo == null) {
            return Observable.create(new Observable.OnSubscribe<BookInfoBean>() {
                @Override
                public void call(Subscriber<? super BookInfoBean> subscriber) {
                    throw new ServerException(ERROR_CODE_KICK_OUT, "管理员登录异常！");
                }
            }).onErrorResumeNext(new HttpResultFunc<BookInfoBean>());
        }
        if (readerInfo == null) {
            return Observable.create(new Observable.OnSubscribe<BookInfoBean>() {
                @Override
                public void call(Subscriber<? super BookInfoBean> subscriber) {
                    throw new ServerException(ERROR_CODE_10001, "读者登录异常！");
                }
            }).onErrorResumeNext(new HttpResultFunc<BookInfoBean>());
        }
        //是否超过借书数量
        if (mBookList.size() >= readerInfo.mBorrowableSum) {
            return Observable.create(new Observable.OnSubscribe<BookInfoBean>() {
                @Override
                public void call(Subscriber<? super BookInfoBean> subscriber) {
                    throw new ServerException(ERROR_CODE_10000, "超限值！");
                }
            }).onErrorResumeNext(new HttpResultFunc<BookInfoBean>());
        }
        return ManagerApi.getInstance().getBookInfo(barNumber, readerInfo.mReaderId, mLibraryInfo.mHallCode)
                .map(new Func1<BookInfoVo, BookInfoBean>() {
                    @Override
                    public BookInfoBean call(BookInfoVo bookInfoVo) {
                        if (bookInfoVo.status == CODE_SUCCESS) {
                            boolean isRepetition = hasRepetitionBooks(bookInfoVo.data.belongLibraryHallCode, bookInfoVo.data.barNumber);
                            if (isRepetition) {
                                throw new ServerException(ERROR_CODE_10003, "重复录入！");
                            } else {
                                BookInfoBean book = new BookInfoBean();
                                if (bookInfoVo.data.borrowDepositType != -1) {
                                    if (mLibraryInfo.mAgreementLevel == 1) {
                                        if (bookInfoVo.data.borrowDepositType == 2) {
                                            throw new ServerException(ERROR_CODE_10002, "押金不足！");
                                        }
                                        double allDeposit = MoneyUtils.add(getBookListDeposit(), bookInfoVo.data.price + bookInfoVo.data.attachPrice);
                                        if (allDeposit > readerInfo.getPlatformUsableDeposit()) {
                                            throw new ServerException(ERROR_CODE_10002, "押金不足！");
                                        }
                                        book.mUsedDepositType = UsedDepositType.PLATFORM_DEPOSIT;
                                    } else if (mLibraryInfo.mAgreementLevel == 2) {
                                        double allPlatformPrice = MoneyUtils.add(getBookListPlatformDeposit(), bookInfoVo.data.price + bookInfoVo.data.attachPrice);
                                        double allLibPrice = MoneyUtils.add(getBookListLibraryDeposit(), bookInfoVo.data.price + bookInfoVo.data.attachPrice);

                                        if (readerInfo.getPlatformUsableDeposit() < allPlatformPrice
                                                && bookInfoVo.data.borrowDepositType == 1) {
                                            throw new ServerException(ERROR_CODE_10004, "只可用共享押金！");
                                        }
                                        if (readerInfo.getPlatformUsableDeposit() < allPlatformPrice
                                                && readerInfo.getOfflineUsableDeposit() < allLibPrice) {
                                            throw new ServerException(ERROR_CODE_10002, "押金不足！");
                                        } else {
                                            if (mLibraryInfo.mDepositPriority == 1) {
                                                if (readerInfo.getPlatformUsableDeposit() > allPlatformPrice) {
                                                    book.mUsedDepositType = UsedDepositType.PLATFORM_DEPOSIT;
                                                } else {
                                                    book.mUsedDepositType = UsedDepositType.LIB_DEPOSIT;
                                                }
                                            } else {
                                                if (readerInfo.getOfflineUsableDeposit() > allLibPrice) {
                                                    book.mUsedDepositType = UsedDepositType.LIB_DEPOSIT;
                                                } else {
                                                    book.mUsedDepositType = UsedDepositType.PLATFORM_DEPOSIT;
                                                }
                                            }
                                        }
                                    } else if (mLibraryInfo.mAgreementLevel == 3) {
                                        double allPlatformPrice = MoneyUtils.add(getBookListPlatformDeposit(), bookInfoVo.data.price + bookInfoVo.data.attachPrice);
                                        double allLibPrice = MoneyUtils.add(getBookListLibraryDeposit(), bookInfoVo.data.price + bookInfoVo.data.attachPrice);

                                        if (readerInfo.getPlatformUsableDeposit() < allPlatformPrice
                                                && readerInfo.getOfflineUsableDeposit() < allLibPrice) {
                                            throw new ServerException(ERROR_CODE_10002, "押金不足！");
                                        } else {
                                            if (mLibraryInfo.mDepositPriority == 1) {
                                                if (readerInfo.getPlatformUsableDeposit() > allPlatformPrice) {
                                                    book.mUsedDepositType = UsedDepositType.PLATFORM_DEPOSIT;
                                                } else {
                                                    book.mUsedDepositType = UsedDepositType.LIB_DEPOSIT;
                                                }
                                            } else {
                                                if (readerInfo.getOfflineUsableDeposit() > allLibPrice) {
                                                    book.mUsedDepositType = UsedDepositType.LIB_DEPOSIT;
                                                } else {
                                                    book.mUsedDepositType = UsedDepositType.PLATFORM_DEPOSIT;
                                                }
                                            }
                                        }
                                    } else {
                                        double allDeposit = MoneyUtils.add(getBookListDeposit(), bookInfoVo.data.price + bookInfoVo.data.attachPrice);
                                        if (allDeposit > readerInfo.getOfflineUsableDeposit()) {
                                            throw new ServerException(ERROR_CODE_10002, "押金不足！");
                                        }
                                    }
                                    book.mDeposit = 1;
                                } else {
                                    book.mUsedDepositType = UsedDepositType.NO_DEPOSIT;
                                    book.mDeposit = 0;
                                }

                                book.mId = bookInfoVo.data.id;
                                book.mBelongLibraryHallCode = bookInfoVo.data.belongLibraryHallCode;
                                book.mBarNumber = bookInfoVo.data.barNumber;
                                book.mProperTitle = bookInfoVo.data.properTitle;
                                book.mPrice = bookInfoVo.data.price;
                                book.mAttachPrice = bookInfoVo.data.attachPrice;
                                book.mColorIsRed = false;

                                addBook(book, 0);
                                return book;
                            }
                        } else {
                            throw new ServerException(bookInfoVo.data.errorCode, "");
                        }
                    }
                })
                .onErrorResumeNext(new HttpResultFunc<BookInfoBean>());
    }

    public Observable<Boolean> borrowBook(final ReaderInfo readerInfo) {
        final JSONArray bookIdsArray = new JSONArray();
        List<BookInfoBean> bookList = DataRepository.getInstance().getBookList();
        for (int i = bookList.size() - 1; i >= 0; i--) {
            BookInfoBean info = bookList.get(i);
            bookIdsArray.put(info.mId);
        }
        if (readerInfo == null) {
            return Observable.create(new Observable.OnSubscribe<Boolean>() {
                @Override
                public void call(Subscriber<? super Boolean> subscriber) {
                    throw new ServerException(ERROR_CODE_10001, "读者登录异常！");
                }
            }).onErrorResumeNext(new HttpResultFunc<Boolean>());
        }
        return ManagerApi.getInstance().getBorrowNumber()
                .flatMap(new Func1<OrderFromVo, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(OrderFromVo orderFromVo) {
                        if (orderFromVo.status == CODE_SUCCESS) {
                            return ManagerApi.getInstance().borrowBook(readerInfo.mReaderId, bookIdsArray, orderFromVo.data.value)
                                    .map(new Func1<BorrowBookVo, Boolean>() {
                                        @Override
                                        public Boolean call(BorrowBookVo borrowBookVo) {
                                            if (borrowBookVo.status == CODE_SUCCESS) {
                                                return true;
                                            } else {
                                                if (borrowBookVo.data.errorCode == ERROR_CODE_2305) {
                                                    if (null != borrowBookVo.data.errorData && borrowBookVo.data.errorData.size() > 0) {
                                                        for (Long item : borrowBookVo.data.errorData) {
                                                            BookInfoBean info = new BookInfoBean();
                                                            info.mId = item;
                                                            int index = mBookList.indexOf(info);
                                                            if (index >= 0) {
                                                                mBookList.get(index).mColorIsRed = true;
                                                            }
                                                        }
                                                    }
                                                }
                                                throw new ServerException(borrowBookVo.data.errorCode, "");
                                            }
                                        }
                                    });
                        } else {
                            throw new ServerException(orderFromVo.data.errorCode, "");
                        }
                    }
                })
                .onErrorResumeNext(new HttpResultFunc<Boolean>());

    }

    private String mReturnBookOrderNum;

    public void delReturnBookOrderNum() {
        mReturnBookOrderNum = null;
    }

    public Observable<String> returnBook(final String barNumber, final String currentReaderId) {
        if (mReturnBookOrderNum == null || mReturnBookOrderNum.equals("")) {
            return ManagerApi.getInstance().getReturnNumber()
                    .flatMap(new Func1<OrderFromVo, Observable<String>>() {
                        @Override
                        public Observable<String> call(OrderFromVo orderFromVo) {
                            if (orderFromVo.status == CODE_SUCCESS) {
                                mReturnBookOrderNum = orderFromVo.data.value;
                                return returnBook(barNumber, mReturnBookOrderNum, currentReaderId);
                            } else {
                                throw new ServerException(orderFromVo.data.errorCode, "");
                            }
                        }
                    })
                    .onErrorResumeNext(new HttpResultFunc<String>());
        } else {
            return returnBook(barNumber, mReturnBookOrderNum, currentReaderId)
                    .onErrorResumeNext(new HttpResultFunc<String>());
        }
    }

    private Observable<String> returnBook(final String barNumber, String orderNum, final String currentReaderId) {
        return ManagerApi.getInstance().returnBook(barNumber, orderNum)
                .map(new Func1<ReturnBookVo, String>() {
                    @Override
                    public String call(ReturnBookVo returnBookVo) {
                        if (returnBookVo.status == CODE_SUCCESS) {
                            //1.如果对象返回为另一个用户，则删除图书列表和读者信息,重新添加数据
                            if (!TextUtils.isEmpty(currentReaderId)
                                    && !currentReaderId.equals(returnBookVo.data.readerId)) {
                                clearBookList();
                            }

                            //设置图书信息
                            BookInfoBean bookInfo = new BookInfoBean();
                            bookInfo.mId = returnBookVo.data.bookId;
                            bookInfo.mBelongLibraryHallCode = returnBookVo.data.belongLibraryHallCode;
                            bookInfo.mBarNumber = returnBookVo.data.barNumber;
                            bookInfo.mProperTitle = returnBookVo.data.properTitle;
                            bookInfo.mPrice = returnBookVo.data.price;
                            bookInfo.mPenalty = returnBookVo.data.penalty;
                            bookInfo.mPenaltyId = returnBookVo.data.penaltyId;
                            bookInfo.mHandlePenalty = (returnBookVo.data.penalty <= 0);
                            bookInfo.mAttachPrice = returnBookVo.data.attachPrice;
                            //检查是否有重复书籍
                            boolean isRepetition = hasRepetitionBooks(bookInfo.mBelongLibraryHallCode, bookInfo.mBarNumber);
                            if (isRepetition) {
                                throw new ServerException(ERROR_CODE_10003, "重复录入！");
                            } else {
                                addBook(bookInfo);
                                return returnBookVo.data.readerId;
                            }
                        } else {
                            throw new ServerException(returnBookVo.data.errorCode, "");
                        }
                    }
                });
    }


    public Observable<LostBookInfo> getBorrowingBookList(String readerId) {
        return Observable.zip(ManagerApi.getInstance().getReaderInfo(readerId),
                ManagerApi.getInstance().getLostBookList(readerId),
                new Func2<ReaderLoginInfoVo, LostBookVo, LostBookInfo>() {
                    @Override
                    public LostBookInfo call(ReaderLoginInfoVo readerLoginInfoVo, LostBookVo lostBookVo) {
                        if (readerLoginInfoVo.status == CODE_SUCCESS
                                && lostBookVo.status == CODE_SUCCESS) {
                            LostBookInfo lostBookInfo = new LostBookInfo();

                            lostBookInfo.mReaderInfo = getReaderInfo(readerLoginInfoVo);

                            lostBookInfo.mBookList = new ArrayList<>();
                            for (LostBookVo.BookInfoVo item : lostBookVo.data.list) {
                                BookInfoBean book = new BookInfoBean();
                                book.mId = item.libraryBookId;
                                book.mBelongLibraryHallCode = item.belongLibraryHallCode;
                                book.mBarNumber = item.barNumber;
                                book.mProperTitle = item.properTitle;
                                book.mPrice = item.price;
                                book.mAttachPrice = item.attachPrice;
                                book.mBorrowId = item.borrowId;
                                book.mDeposit = (item.deposit > 0) ? 1 : 0;
                                if (item.usedDepositType == 1) {
                                    book.mUsedDepositType = UsedDepositType.PLATFORM_DEPOSIT;
                                } else if (item.usedDepositType == 2) {
                                    book.mUsedDepositType = UsedDepositType.LIB_DEPOSIT;
                                } else {
                                    book.mUsedDepositType = UsedDepositType.NO_DEPOSIT;
                                }

                                lostBookInfo.mBookList.add(book);
                            }
                            return lostBookInfo;
                        } else if (readerLoginInfoVo.status != CODE_SUCCESS) {
                            throw new ServerException(readerLoginInfoVo.data.errorCode, "");
                        } else {
                            throw new ServerException(lostBookVo.data.errorCode, "");
                        }

                    }
                })
                .onErrorResumeNext(new HttpResultFunc<LostBookInfo>());
    }

    public Observable<PenaltyDealInfo> getPenaltyList(String readerId) {
        return Observable.zip(ManagerApi.getInstance().getReaderInfo(readerId),
                ManagerApi.getInstance().getPenaltyList(readerId),
                new Func2<ReaderLoginInfoVo, PenaltyListVo, PenaltyDealInfo>() {
                    @Override
                    public PenaltyDealInfo call(ReaderLoginInfoVo readerLoginInfoVo, PenaltyListVo penaltyListVo) {
                        if (readerLoginInfoVo.status == CODE_SUCCESS
                                && penaltyListVo.status == CODE_SUCCESS) {
                            PenaltyDealInfo penaltyDealInfo = new PenaltyDealInfo();
                            penaltyDealInfo.mReaderInfo = getReaderInfo(readerLoginInfoVo);
                            penaltyDealInfo.mPenaltyList = new ArrayList<>();
                            for (PenaltyListVo.PenaltyItemVo info : penaltyListVo.data.resultList) {
                                PenaltyBean penaltyBean = new PenaltyBean();
                                penaltyBean.mProperTitle = info.properTitle;
                                penaltyBean.mPenaltyId = info.penaltyId;
                                penaltyBean.mPenalty = info.penalty;
                                penaltyBean.mBarNumber = info.barNumber;
                                penaltyBean.mBelongLibraryHallCode = info.belongLibraryHallCode;
                                penaltyBean.mPrice = info.price;
                                penaltyBean.mAttachPrice = info.attachPrice;
                                penaltyBean.mReturnHallCode = info.returnHallCode;
                                penaltyDealInfo.mPenaltyList.add(penaltyBean);
                            }
                            return penaltyDealInfo;
                        } else if (readerLoginInfoVo.status != CODE_SUCCESS) {
                            throw new ServerException(readerLoginInfoVo.data.errorCode, "");
                        } else {
                            throw new ServerException(penaltyListVo.data.errorCode, "");
                        }

                    }
                })
                .onErrorResumeNext(new HttpResultFunc<PenaltyDealInfo>());
    }

    public Observable<Boolean> refundDeposit(String idCard, String readPwd, final String readerId, final double amount) {
        return ManagerApi.getInstance().checkReaderPsw(idCard, readPwd)
                .flatMap(new Func1<RegisterVo, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(RegisterVo registerVo) {
                        if (registerVo.status == 200) {
                            return ManagerApi.getInstance().refundDeposit(readerId, amount)
                                    .map(new Func1<ReturnDepositResultVo, Boolean>() {
                                        @Override
                                        public Boolean call(ReturnDepositResultVo returnDepositResultVo) {
                                            if (returnDepositResultVo.status == CODE_SUCCESS) {
                                                return true;
                                            } else {
                                                throw new ServerException(returnDepositResultVo.data.errorCode, "");
                                            }
                                        }
                                    });
                        } else {
                            throw new ServerException(registerVo.data.errorCode, "");
                        }
                    }
                })
                .onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    public Observable<BranchLibVo> queryPavilionLevel(String hallCode) {
        return ManagerApi.getInstance().queryPavilionLevel(hallCode);
    }

    public Observable<ReaderPwdModifyVo> modifyReaderPwdOrPhone(String requestData) {
        return ManagerApi.getInstance().modifyReaderPwdOrPhone(requestData);
    }

    public Observable<EntranceGuardResult> entranceCheck(String barNumber) {
        return ManagerApi.getInstance().entranceCheck(barNumber)
                .flatMap(new Func1<EntranceGuardVo, Observable<EntranceGuardResult>>() {
                    @Override
                    public Observable<EntranceGuardResult> call(final EntranceGuardVo entranceGuardVo) {
                        if (entranceGuardVo.status == CODE_SUCCESS) {
                            return ManagerApi.getInstance().getReaderInfo(String.valueOf(entranceGuardVo.data.readerId))
                                    .map(new Func1<ReaderLoginInfoVo, EntranceGuardResult>() {
                                        @Override
                                        public EntranceGuardResult call(ReaderLoginInfoVo readerLoginInfoVo) {
                                            if (readerLoginInfoVo.status == CODE_SUCCESS) {
                                                EntranceGuardResult result = new EntranceGuardResult();
                                                result.mReaderInfo = getReaderInfo(readerLoginInfoVo);
                                                result.mResult = entranceGuardVo.data.check;
                                                return result;
                                            } else {
                                                throw new ServerException(readerLoginInfoVo.data.errorCode, "");
                                            }
                                        }
                                    });
                        } else {
                            throw new ServerException(entranceGuardVo.data.errorCode, "");
                        }
                    }
                })
                .onErrorResumeNext(new HttpResultFunc<EntranceGuardResult>());
    }

    public Observable<SetFirstOperatorPswVo> setFirstPassword(String newPassword) {
        return ManagerApi.getInstance().setFirstPassword(newPassword);
    }

    private void addBook(BookInfoBean book) {
        if (mBookList == null) {
            mBookList = new ArrayList<>();
        }
        mBookList.add(book);
    }

    private void addBook(BookInfoBean book, int index) {
        if (mBookList == null) {
            mBookList = new ArrayList<>();
        }
        mBookList.add(index, book);
    }

    public void clearBookList() {
        if (mBookList != null) {
            mBookList.clear();
        }
    }

    public void removeBook(BookInfoBean book) {
        if (mBookList != null) {
            mBookList.remove(book);
        }
    }

    public List<BookInfoBean> getBookList() {
        return mBookList;
    }

    public void initOrderNumberInfo(OrderNumberInfo info) {
        if (mOrderNumberInfo == null) {
            mOrderNumberInfo = new OrderNumberInfo();
        }
        mOrderNumberInfo.mCodeNumber = info.mCodeNumber;
        mOrderNumberInfo.mDate = info.mDate;
    }

    public OrderNumberInfo getOrderNumberInfo() {
        if (mOrderNumberInfo == null) {
            mOrderNumberInfo = new OrderNumberInfo();
        }
        return mOrderNumberInfo;
    }

    public void delOrderNumberInfo() {
        mOrderNumberInfo = null;
    }

    public void initFlowManageListBean(FlowManageListBean info) {
        if (mFlowManageListBean == null) {
            mFlowManageListBean = new FlowManageListBean();
        }
        mFlowManageListBean.id = info.id;
        mFlowManageListBean.inHallCode = info.inHallCode;
        mFlowManageListBean.name = info.name;
        mFlowManageListBean.outDate = info.outDate;
        mFlowManageListBean.totalPrice = info.totalPrice;
        mFlowManageListBean.totalSum = info.totalSum;
        mFlowManageListBean.outDate = info.outDate;
        mFlowManageListBean.userName = info.userName;
        mFlowManageListBean.phone = info.phone;
        mFlowManageListBean.auditDate = info.auditDate;
        mFlowManageListBean.auditPerson = info.auditPerson;
        mFlowManageListBean.conperson = info.conperson;
        mFlowManageListBean.outHallCode = info.outHallCode;     //流出馆馆号-当前馆
        mFlowManageListBean.outOperUserId = info.outOperUserId; //操作员id
    }

    //设置流通id
    public void setFlowManageID(String id) {
        if (mFlowManageListBean == null) {
            return;
        }
        if (TextUtils.isEmpty(mFlowManageListBean.id)) {
            mFlowManageListBean.id = id;
        }
    }

    public FlowManageListBean getFlowManageInfo() {
        return mFlowManageListBean;
    }

    public void delFlowManageListBean() {
        mFlowManageListBean = null;
    }

    public void refreshDetailBookList(List<FlowManageDetailBookInfoBean> list) {
        if (mDetailBookList == null) {
            mDetailBookList = new ArrayList<>();
        }
        mDetailBookList.clear();
        mDetailBookList.addAll(list);
    }

    public void addDetailBookList(List<FlowManageDetailBookInfoBean> list) {
        if (mDetailBookList == null) {
            mDetailBookList = new ArrayList<>();
        }
        mDetailBookList.addAll(list);
    }

    public List<FlowManageDetailBookInfoBean> getDetailBookList() {
        if (mDetailBookList == null) {
            mDetailBookList = new ArrayList<>();
        }
        return mDetailBookList;
    }

    public void delDetailBook() {
        if (mDetailBookList != null) {
            mDetailBookList.clear();
            mDetailBookList = null;
        }
    }

//    //流出管理
//    //流出管理获取操作员列表
//    public Observable<SingleSelectionConditionVo> getLibraryOperatorList(String hallCode) {
//        return ManagerApi.getInstance().getLibraryOperatorList(hallCode);
//    }
//
//    //查询流出状态列表
//    public Observable<SingleSelectionConditionVo> getFlowManageStateList() {
//        return ManagerApi.getInstance().getFlowManageStateList();
//    }
//
//    //查询流入状态列表
//    public Observable<SingleSelectionConditionVo> getIntoManageStateList() {
//        return ManagerApi.getInstance().getIntoManageStateList();
//    }

    //查询流出列表
    public Observable<FlowManagementListVo> getFlowManagementList(ArrayMap<String, String> map) {
        return ManagerApi.getInstance().getFlowManagementList(map);
    }

    //获取每单详情
    public Observable<FlowManageAddBookInfoVo> getFlowManageSingDetail(int pageNumber, int pageSize, String circulateId) {
        return ManagerApi.getInstance().getFlowManageSingDetail(pageNumber, pageSize, circulateId);
    }

    public Observable<InLibraryOperatorVo> getInLibraryOperatorInfo(String circulateId, int circulateStatus) {
        return ManagerApi.getInstance().getInLibraryOperatorInfo(circulateId, circulateStatus);
    }

    public Observable<OutLibraryOperatorVo> getOutLibraryOperatorInfo(String circulateId) {
        return ManagerApi.getInstance().getOutLibraryOperatorInfo(circulateId);
    }

    //流出管理-发送清单列表
    public Observable<FlowManageSendBookResultVo> sendFlowManageNewBookList(String circulateId, String outOperUserId) {
        return ManagerApi.getInstance().sendFlowManageNewBookList(circulateId, outOperUserId);
    }

    //流出管理-直接删单
    public Observable<FlowManageDeleteSingleVo> deleteFlowManageSingle(String circulateId) {
        return ManagerApi.getInstance().deleteFlowManageSingle(circulateId);
    }

    //流出管理-清点删单
    public Observable<FlowManageDeleteSingleVo> outDeleteFlowManageSingleCountBook(String circulateId, String barNumber) {
        return ManagerApi.getInstance().outDeleteFlowManageSingleCountBook(circulateId, barNumber);
    }

    //流出管理-撤回清单
    public Observable<FlowManageOutReCallVo> withdrawThisSingle(String circulateId, String outOperUserId) {
        return ManagerApi.getInstance().withdrawThisSingle(circulateId, outOperUserId);
    }

    //删除已列表中的书籍
    public Observable<FlowManageDeleteBookVo> deleteFlowManageBookInfo(String circulateId, String id) {
        return ManagerApi.getInstance().deleteFlowManageBookInfo(circulateId, id);
    }

    //新增流出书籍
    public Observable<FlowManageAddNewBookInfoVo> getFlowManageAddNewBook(String barNumber, String circulateId,
                                                                          String inHallCode, String operCode,
                                                                          String outHallCode, String outOperUserId) {
        return ManagerApi.getInstance().getFlowManageAddNewBook(barNumber, circulateId, inHallCode, operCode, outHallCode, outOperUserId);
    }


    //搜索同流通范围的馆 -（位置：流出管理新增）
    public Observable<SameRangeLibraryListVo> searchSameRangeLibraryListByCondition(int pageNum, int pageSize, String hallCode, String grepValue) {
        return ManagerApi.getInstance().searchSameRangeLibraryListByCondition(pageNum, pageSize, hallCode, grepValue);
    }

    //流入管理
    //流入管理列表
    public Observable<IntoManagementListVo> getIntoManageSingleList(ArrayMap<String, String> map) {
        return ManagerApi.getInstance().getIntoManageSingleList(map);
    }

    //签收本单
    public Observable<IntoManageSignThisSingleVo> signThisSingle(String circulateId, String operCode) {
        return ManagerApi.getInstance().signThisSingle(circulateId, operCode);
    }

    //签收本单
    public Observable<IntoManageSignThisSingleVo> rejectThisSingle(String circulateId, String signUserId) {
        return ManagerApi.getInstance().rejectThisSingle(circulateId, signUserId);
    }

    /**
     * 申请免单
     *
     * @param applyRemark 申请理由
     * @return 申请免单
     */
    public Observable<Boolean> applyPenaltyFree(final String applyRemark, final List<Long> ids, final String readerId, String pwd) {
        if (mLibraryInfo == null) {
            return Observable.create(new Observable.OnSubscribe<Boolean>() {
                @Override
                public void call(Subscriber<? super Boolean> subscriber) {
                    throw new ServerException(ERROR_CODE_KICK_OUT, "管理员登录异常！");
                }
            }).onErrorResumeNext(new HttpResultFunc<Boolean>());
        }
        return ManagerApi.getInstance().checkOperatorPsw(mLibraryInfo.mHallCode, mLibraryInfo.mOperaterName, pwd)
                .flatMap(new Func1<VerifyLibraryOperatorPswVo, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(VerifyLibraryOperatorPswVo verifyLibraryOperatorPswVo) {
                        if (verifyLibraryOperatorPswVo.status == CODE_SUCCESS) {
                            if (verifyLibraryOperatorPswVo.data.value) {
                                JSONArray penaltyIdsArray = new JSONArray();
                                for (Long id : ids) {
                                    penaltyIdsArray.put(id);
                                }
                                return ManagerApi.getInstance().applyPenaltyFree(applyRemark, penaltyIdsArray, readerId)
                                        .map(new Func1<ApplyPenaltyFreeVo, Boolean>() {
                                            @Override
                                            public Boolean call(ApplyPenaltyFreeVo applyPenaltyFreeVo) {
                                                if (applyPenaltyFreeVo.status == CODE_SUCCESS) {
                                                    for (BookInfoBean item : mBookList) {
                                                        //TODO 检查判断
                                                        if (ids.contains(item.mPenaltyId)) {
                                                            item.mHandlePenalty = true;
                                                            break;
                                                        }
                                                    }
                                                    return true;
                                                } else {
                                                    throw new ServerException(applyPenaltyFreeVo.data.errorCode, "");
                                                }
                                            }
                                        });
                            } else {
                                throw new ServerException(ERROR_CODE_3103, "密码错误");
                            }
                        } else {
                            throw new ServerException(verifyLibraryOperatorPswVo.data.errorCode, "");
                        }
                    }
                })
                .onErrorResumeNext(new HttpResultFunc<Boolean>());

    }

    /**
     * 忘记密码，发送验证码
     *
     * @param hallCode 馆号
     * @param phone    手机号
     */
    public Observable<VerifyCodeVo> sendVerifyForgetPwd(String hallCode, String phone) {
        return ManagerApi.getInstance().sendVerifyForgetPwd(hallCode, phone);
    }

    /**
     * 忘记密码，验证身份
     *
     * @param code     验证码
     * @param hallCode 馆号
     * @param phone    手机号
     */
    public Observable<VerifyIdentityVo> verifyIdentity(String code, String hallCode, String phone) {
        return ManagerApi.getInstance().verifyIdentity(code, hallCode, phone);
    }

    /**
     * 重置密码
     *
     * @param id     管理员ID
     * @param newPwd 新密码
     */
    public Observable<ResetPwdVo> resetPwd(int id, String newPwd) {
        return ManagerApi.getInstance().resetPwd(id, newPwd);
    }

    //统计分析

    /**
     * 获取藏书统计馆号
     *
     * @param hallCode 馆号
     * @return 馆号列表
     */
    public Observable<SingleSelectionConditionVo> getHallCodeLibraryStatics(String hallCode) {
        return ManagerApi.getInstance().getHallCodeLibraryStatics(hallCode);
    }

    /**
     * 获取在借统计馆号
     *
     * @param hallCode 馆号
     * @return 馆号列表
     */
    public Observable<SingleSelectionConditionVo> getHallCodeInBorrower(String hallCode) {
        return ManagerApi.getInstance().getHallCodeInBorrower(hallCode);
    }

    /**
     * 获取销售统计馆号
     *
     * @param hallCode 馆号
     * @return 馆号列表
     */
    public Observable<SingleSelectionConditionVo> getHallCodeBookSells(String hallCode) {
        return ManagerApi.getInstance().getHallCodeBookSells(hallCode);
    }

    /**
     * 藏书统计书籍状态
     *
     * @return 书籍状态列表
     */
    public Observable<SingleSelectionConditionVo> getStatusLibraryStatics() {
        return ManagerApi.getInstance().getStatusLibraryStatics();
    }

    /**
     * 库位
     *
     * @return 库位列表
     */
    public Observable<SingleSelectionConditionVo> getStorerooms() {
        return ManagerApi.getInstance().getStorerooms();
    }

    /**
     * 是否绑定RFID
     *
     * @return 有/无
     */
    public Observable<SingleSelectionConditionVo> getRfidLibraryStatics() {
        return ManagerApi.getInstance().getRfidLibraryStatics();
    }

    /**
     * 在借统计书籍状态
     *
     * @return 书籍状态列表
     */
    public Observable<SingleSelectionConditionVo> getStatusInBorrower() {
        return ManagerApi.getInstance().getStatusInBorrower();
    }

    /**
     * 借书统计馆号
     *
     * @param hallCode 馆号
     * @return 馆号列表
     */
    public Observable<SingleSelectionConditionVo> getHallCodeBorrowerBooks(String hallCode) {
        return ManagerApi.getInstance().getHallCodeBorrowerBooks(hallCode);
    }

    /**
     * 操作员
     *
     * @param hallCode       馆号
     * @param name           userId:收款统计 operUserName:馆际流通 operUser:其余全部
     * @param valueEqualDesc 0:收款统计 1:馆际流通 0:其余全部
     * @return 操作员列表
     */
    public Observable<SingleSelectionConditionVo> getUsers(String hallCode, String name, int valueEqualDesc) {
        return ManagerApi.getInstance().getUsers(hallCode, name, valueEqualDesc);
    }

    /**
     * 免单操作员
     *
     * @return 操作员列表
     */
    public Observable<SingleSelectionConditionVo> getUsersPenaltyFreeApply() {
        return ManagerApi.getInstance().getUsersPenaltyFreeApply();
    }

    /**
     * 陪书统计馆号
     *
     * @param hallCode 馆号
     * @return 馆号列表
     */
    public Observable<SingleSelectionConditionVo> getHallCodeCompensateBooks(String hallCode) {
        return ManagerApi.getInstance().getHallCodeCompensateBooks(hallCode);
    }

    /**
     * 收款统计项目列表
     *
     * @return 项目列表
     */
    public Observable<SingleSelectionConditionVo> getOperationGatheringStatistics() {
        return ManagerApi.getInstance().getOperationGatheringStatistics();
    }

    /**
     * 读者统计馆号
     *
     * @return 馆号列表
     */
    public Observable<SingleSelectionConditionVo> getHallCodeReaderStatistics() {
        return ManagerApi.getInstance().getHallCodeReaderStatistics();
    }

    /**
     * 读者统计读者类型
     *
     * @return 读者类型列表
     */
    public Observable<SingleSelectionConditionVo> getTypeReaderStatistics() {
        return ManagerApi.getInstance().getTypeReaderStatistics();
    }

    /**
     * 免单统计状态列表
     *
     * @return 状态列表
     */
    public Observable<SingleSelectionConditionVo> getStatusPenaltyFreeApplys() {
        return ManagerApi.getInstance().getStatusPenaltyFreeApplys();
    }

    /**
     * 流出状态
     *
     * @return 状态列表
     */
    public Observable<SingleSelectionConditionVo> getStatusOutCirculite() {
        return ManagerApi.getInstance().getStatusOutCirculite();
    }

    /**
     * 流入状态
     *
     * @return 状态列表
     */
    public Observable<SingleSelectionConditionVo> getStatusInCirculite() {
        return ManagerApi.getInstance().getStatusInCirculite();
    }

    //借书统计
    public Observable<BorrowBookStatisticsVo> getBorrowBookStatisticsList(ArrayMap<String, String> map) {
        return ManagerApi.getInstance().getBorrowBookStatisticsList(map);
    }

    //还书统计
    public Observable<ReturnBookStatisticsVo> getReturnBookStatisticsList(ArrayMap<String, String> map) {
        return ManagerApi.getInstance().getReturnBookStatisticsList(map);
    }

    //读者统计
    public Observable<ReaderStatisticsVo> getReaderStatisticsList(ArrayMap<String, String> map) {
        return ManagerApi.getInstance().getReaderStatisticsList(map);
    }

    //赔书统计
    public Observable<LostBookStatisticsVo> getLostBookStatisticsList(ArrayMap<String, String> map) {
        return ManagerApi.getInstance().getLostBookStatisticsList(map);
    }

    /**
     * 销售统计
     */
    public Observable<SellBookStatisticsVo> getSellBookStatisticsList(ArrayMap<String, String> map) {
        return ManagerApi.getInstance().getSellBookStatisticsList(map);
    }

    /**
     * 在借统计
     */
    public Observable<BorrowingBookStatisticsVo> getBorrowingBookStatisticsList(ArrayMap<String, String> map) {
        return ManagerApi.getInstance().getBorrowingBookStatisticsList(map);
    }

    /**
     * 藏书统计
     */
    public Observable<CollectingBookStatisticsVo> getCollectionBookStatisticsList(ArrayMap<String, String> map) {
        return ManagerApi.getInstance().getCollectionBookStatisticsList(map);
    }

    /**
     * 收款统计
     */
    public Observable<CollectingStatisticsVo> getCollectingStatisticsList(ArrayMap<String, String> map) {
        return ManagerApi.getInstance().getCollectingStatisticsList(map);
    }

    /**
     * 免单统计
     */
    public Observable<PenaltyFreeStatisticsVo> getPenaltyFreeApplys(ArrayMap<String, String> map) {
        return ManagerApi.getInstance().getPenaltyFreeApplys(map);
    }

    /**
     * 更新APP
     */
    public Observable<UpdateAppVo> updateApp(String version) {
        return ManagerApi.getInstance().updateApp(version);
    }

    public Observable<Boolean> chargePenalty(String pwd, final String readerId, final double amount) {
        if (mLibraryInfo == null) {
            return Observable.create(new Observable.OnSubscribe<Boolean>() {
                @Override
                public void call(Subscriber<? super Boolean> subscriber) {
                    throw new ServerException(ERROR_CODE_KICK_OUT, "管理员登录异常！");
                }
            }).onErrorResumeNext(new HttpResultFunc<Boolean>());
        }
        return ManagerApi.getInstance().checkOperatorPsw(mLibraryInfo.mHallCode, mLibraryInfo.mOperaterName, pwd)
                .flatMap(new Func1<VerifyLibraryOperatorPswVo, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(VerifyLibraryOperatorPswVo verifyLibraryOperatorPswVo) {
                        if (verifyLibraryOperatorPswVo.status == CODE_SUCCESS) {
                            if (verifyLibraryOperatorPswVo.data.value) {
                                return ManagerApi.getInstance().addDeposit(readerId, 2, amount)
                                        .map(new Func1<AddDepositVo, Boolean>() {
                                            @Override
                                            public Boolean call(AddDepositVo addDepositVo) {
                                                if (addDepositVo.status == CODE_SUCCESS) {
                                                    return true;
                                                } else {
                                                    throw new ServerException(addDepositVo.data.errorCode, "");
                                                }
                                            }
                                        });
                            } else {
                                throw new ServerException(ERROR_CODE_3103, "密码错误");
                            }
                        } else {
                            throw new ServerException(verifyLibraryOperatorPswVo.data.errorCode, "");
                        }
                    }
                })
                .onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    public Observable<PenaltyDealResultVo> autoDealPenalty(String readerId) {
        return ManagerApi.getInstance().autoDealPenalty(readerId);
    }

    public Observable<Double> autoDealAllPenalty(String readerId) {
        return ManagerApi.getInstance().autoDealPenalty(readerId)
                .map(new Func1<PenaltyDealResultVo, Double>() {
                    @Override
                    public Double call(PenaltyDealResultVo penaltyDealResultVo) {
                        if (penaltyDealResultVo.status == CODE_SUCCESS) {
                            if (penaltyDealResultVo.data.succeedPenalty > 0) {
                                for (BookInfoBean item : mBookList) {
                                    //TODO 检查判断
                                    if (penaltyDealResultVo.data.succeedPenaltyIds.contains(item.mPenaltyId)) {
                                        item.mHandlePenalty = true;
                                        break;
                                    }
                                }
                            }
                            return penaltyDealResultVo.data.failPenalty;
                        } else {
                            throw new ServerException(penaltyDealResultVo.data.errorCode, "");
                        }

                    }
                })
                .onErrorResumeNext(new HttpResultFunc<Double>());
    }

    public Observable<Void> autoDealReturnBookPenalty(String readerId) {
        return ManagerApi.getInstance().autoDealPenalty(readerId)
                .map(new Func1<PenaltyDealResultVo, Void>() {
                    @Override
                    public Void call(PenaltyDealResultVo penaltyDealResultVo) {
                        if (penaltyDealResultVo.status == CODE_SUCCESS) {
                            if (penaltyDealResultVo.data.succeedPenalty > 0) {
                                for (BookInfoBean item : mBookList) {
                                    //TODO 检查判断
                                    if (penaltyDealResultVo.data.succeedPenaltyIds.contains(item.mPenaltyId)) {
                                        item.mHandlePenalty = true;
                                        break;
                                    }
                                }
                            }
                            return null;
                        } else {
                            throw new ServerException(penaltyDealResultVo.data.errorCode, "");
                        }
                    }
                })
                .onErrorResumeNext(new HttpResultFunc<Void>());
    }

    public Observable<SwitchCityVo> getProvinceList() {
        return ManagerApi.getInstance().getProvinceList();
    }

    public Observable<SwitchCityVo> getCityList(String code) {
        return ManagerApi.getInstance().getCityList(code);
    }

    public Observable<SwitchCityVo> getDistrictList(String cityName) {
        return ManagerApi.getInstance().getDistrictList(cityName);
    }

    public Observable<HelpInfoVo> getHelpList() {
        return ManagerApi.getInstance().getHelpList();
    }

    public Observable<WithdrawDepositVo> requestWithdrawDeposit(double applyMoney, String createIp) {
        return ManagerApi.getInstance().requestWithdrawDeposit(applyMoney, createIp);
    }

    public Observable<RefundInfoVo> requestRefundInfo() {
        return ManagerApi.getInstance().requestRefundInfo();
    }

    public Observable<WXPayInfoVo> requestWXPayInfo(double payMoney, String userIP) {
        return ManagerApi.getInstance().requestWXPayInfo(payMoney, userIP, 1, null);
    }

    public Observable<WXPayInfoVo> requestWXPayInfo(double payMoney, String userIP, String readerId) {
        return ManagerApi.getInstance().requestWXPayInfo(payMoney, userIP, 9, readerId);
    }

    public Observable<PayResultVo> requestWXPayResult(String orderNum) {
        return ManagerApi.getInstance().requestWXPayResult(orderNum, null);
    }

    public Observable<PayResultVo> requestWXPayResult(String orderNum, String readerId) {
        return ManagerApi.getInstance().requestWXPayResult(orderNum, readerId);
    }

    public Observable<AliPayInfoVo> requestAliPayInfo(double payMoney, String userIP) {
        return ManagerApi.getInstance().requestAliPayInfo(payMoney, userIP, 1, null);
    }

    public Observable<AliPayInfoVo> requestAliPayInfo(double payMoney, String userIP, String readerId) {
        return ManagerApi.getInstance().requestAliPayInfo(payMoney, userIP, 9, readerId);
    }

    public Observable<PayResultVo> requestAliPayResult(String orderNum) {
        return ManagerApi.getInstance().requestAliPayResult(orderNum, null);
    }

    public Observable<PayResultVo> requestAliPayResult(String orderNum, String readerId) {
        return ManagerApi.getInstance().requestAliPayResult(orderNum, readerId);
    }


    public Observable<RegisterVo> updateReaderInfo(ArrayMap<String, Object> map) {
        return ManagerApi.getInstance().updateReaderInfo(map);
    }

    public List<String> getReaderNationList() {
        return new Gson().fromJson(DataService.getNationStr(), new TypeToken<List<String>>() {
        }.getType());
    }

    private List<SwitchCityBean> mSwitchProvinceList = new ArrayList<>();
    private List<SwitchCityBean> mSwitchCityList = new ArrayList<>();
    private List<SwitchCityBean> mSwitchDefaultDistinctList = new ArrayList<>();

    /**
     * @param switchList 城市列表
     * @param saveType   0 默认区域列表 1省列表 2城市列表
     */
    public void saveTempCityList(List<SwitchCityBean> switchList, int saveType) {
        switch (saveType) {
            case 0:
                mSwitchDefaultDistinctList.clear();
                mSwitchDefaultDistinctList.addAll(switchList);
                break;
            case 1:
                mSwitchProvinceList.clear();
                mSwitchProvinceList.addAll(switchList);
                break;
            case 2:
                mSwitchCityList.clear();
                mSwitchCityList.addAll(switchList);
                break;
        }
    }

    public List<SwitchCityBean> getTempCityList(int saveType) {
        if (saveType == 0) {
            return mSwitchDefaultDistinctList;
        } else if (saveType == 1) {
            return mSwitchProvinceList;
        } else if (saveType == 2) {
            return mSwitchCityList;
        } else {
            return null;
        }
    }

    public void clearTempDefaultDistinctList() {
        if (null != mSwitchDefaultDistinctList && mSwitchDefaultDistinctList.size() > 0) {
            mSwitchDefaultDistinctList.clear();
        }
    }

    public void clearTempProvinceList() {
        if (null != mSwitchProvinceList && mSwitchProvinceList.size() > 0) {
            mSwitchProvinceList.clear();
        }
    }

    public void clearTempCityListList() {
        if (null != mSwitchCityList && mSwitchCityList.size() > 0) {
            mSwitchCityList.clear();
        }
    }

    //保存临时转入馆列表
    private List<SameRangeLibraryBean> mSameRangeLibraryBeanList;
    private int mLibraryTotalCount = 0;

    public void addSameRangeLibraryList(List<SameRangeLibraryBean> libraryList, int totalCount) {
        this.mLibraryTotalCount = totalCount;
        if (null == mSameRangeLibraryBeanList) {
            mSameRangeLibraryBeanList = new ArrayList<>();
        }
        mSameRangeLibraryBeanList.clear();
        mSameRangeLibraryBeanList.addAll(libraryList);
    }

    public List<SameRangeLibraryBean> getSameRangeLibraryList() {
        return mSameRangeLibraryBeanList;
    }

    public int getInLibraryTotalCount() {
        return mLibraryTotalCount;
    }

    public void clearSameRangeLibraryList() {
        if (null != mSameRangeLibraryBeanList) {
            mSameRangeLibraryBeanList.clear();
        }
        mLibraryTotalCount = 0;
    }

    public Observable<Boolean> useCashCompensateBook(String adminPwd, final String readerId,
                                                     final double amount, final List<BookInfoBean> list) {
        if (mLibraryInfo == null) {
            return Observable.create(new Observable.OnSubscribe<Boolean>() {
                @Override
                public void call(Subscriber<? super Boolean> subscriber) {
                    throw new ServerException(ERROR_CODE_KICK_OUT, "管理员登录异常！");
                }
            }).onErrorResumeNext(new HttpResultFunc<Boolean>());
        }
        return ManagerApi.getInstance().checkOperatorPsw(mLibraryInfo.mHallCode, mLibraryInfo.mOperaterName, adminPwd)
                .flatMap(new Func1<VerifyLibraryOperatorPswVo, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(VerifyLibraryOperatorPswVo verifyLibraryOperatorPswVo) {
                        if (verifyLibraryOperatorPswVo.status == CODE_SUCCESS) {
                            if (verifyLibraryOperatorPswVo.data.value) {
                                return ManagerApi.getInstance().addDeposit(readerId, 2, amount)
                                        .flatMap(new Func1<AddDepositVo, Observable<Boolean>>() {
                                            @Override
                                            public Observable<Boolean> call(AddDepositVo addDepositVo) {
                                                if (addDepositVo.status == CODE_SUCCESS) {
                                                    return compensateBook(readerId, list);
                                                } else {
                                                    throw new ServerException(addDepositVo.data.errorCode, "");
                                                }
                                            }
                                        });
                            } else {
                                throw new ServerException(ERROR_CODE_3103, "密码错误");
                            }
                        } else {
                            throw new ServerException(verifyLibraryOperatorPswVo.data.errorCode, "");
                        }
                    }
                })
                .onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    public Observable<Boolean> useDepositCompensateBook(String idCard, String readerPwd,
                                                        final String readerId, final List<BookInfoBean> list) {

        return ManagerApi.getInstance().checkReaderPsw(idCard, readerPwd)
                .flatMap(new Func1<RegisterVo, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(RegisterVo registerVo) {
                        if (registerVo.status == CODE_SUCCESS) {
                            return compensateBook(readerId, list);
                        } else {
                            throw new ServerException(registerVo.data.errorCode, "");
                        }

                    }
                })
                .onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    public Observable<Boolean> compensateBookDirect(final String readerId, List<BookInfoBean> list) {
        return compensateBook(readerId, list).onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    private Observable<Boolean> compensateBook(final String readerId, List<BookInfoBean> list) {
        final JSONArray borrowerIds = new JSONArray();
        if (list.size() > 0) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                if (list.get(i).mCompensateChoosed) {
                    borrowerIds.put(list.get(i).mBorrowId);
                }
            }
        } else {
            return Observable.create(new Observable.OnSubscribe<Boolean>() {
                @Override
                public void call(Subscriber<? super Boolean> subscriber) {
                    throw new ServerException(ERROR_CODE_10005, "暂无赔书信息！");
                }
            });
        }
        return ManagerApi.getInstance().getReturnNumber()
                .flatMap(new Func1<OrderFromVo, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(OrderFromVo orderFromVo) {
                        if (orderFromVo.status == CODE_SUCCESS) {
                            return ManagerApi.getInstance().lostBook(readerId, borrowerIds, orderFromVo.data.value)
                                    .map(new Func1<LostBookResultVo, Boolean>() {
                                        @Override
                                        public Boolean call(LostBookResultVo lostBookResultVo) {
                                            if (lostBookResultVo.status == CODE_SUCCESS) {
                                                return lostBookResultVo.data.successBorrowerIds.size() > 0;
                                            } else {
                                                throw new ServerException(lostBookResultVo.data.errorCode, "");
                                            }
                                        }
                                    });
                        } else {
                            throw new ServerException(orderFromVo.data.errorCode, "");
                        }
                    }
                });
    }

    public Observable<Boolean> chargeLibDeposit(String readerId, double amount) {
        return ManagerApi.getInstance().addDeposit(readerId, 2, amount)
                .map(new Func1<AddDepositVo, Boolean>() {
                    @Override
                    public Boolean call(AddDepositVo addDepositVo) {
                        if (addDepositVo.status == CODE_SUCCESS) {
                            return true;
                        } else {
                            throw new ServerException(addDepositVo.data.errorCode, "");
                        }
                    }
                })
                .onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    public Observable<Boolean> substituteChargeLibDeposit(String readerId, double amount) {
        return ManagerApi.getInstance().addDeposit(readerId, 1, amount)
                .map(new Func1<AddDepositVo, Boolean>() {
                    @Override
                    public Boolean call(AddDepositVo addDepositVo) {
                        if (addDepositVo.status == CODE_SUCCESS) {
                            return true;
                        } else {
                            throw new ServerException(addDepositVo.data.errorCode, "");
                        }
                    }
                })
                .onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    /**
     * 检查是否有重复书籍
     *
     * @param libraryCode    馆号
     * @param libraryBarCode 条码号
     * @return true表示重复  false表示没有重复
     */
    private boolean hasRepetitionBooks(String libraryCode, String libraryBarCode) {
        List<BookInfoBean> bookList = DataRepository.getInstance().getBookList();
        if (bookList == null || bookList.size() == 0) {
            return false;
        }
        for (int i = 0; i < bookList.size(); i++) {
            String belongLibraryHallCode = bookList.get(i).mBelongLibraryHallCode;
            String barNumber = bookList.get(i).mBarNumber;
            if (null != belongLibraryHallCode && null != barNumber) {
                if (libraryCode.equals(belongLibraryHallCode) && libraryBarCode.equals(barNumber)) {
                    return true;
                }
            }
        }
        return false;
    }


    private double getBookListDeposit() {
        List<BookInfoBean> bookList = DataRepository.getInstance().getBookList();
        if (bookList.size() > 0) {
            double depositTotalPrice = 0.00;
            double attachPriceForDeposit = 0.00;
            for (BookInfoBean info : bookList) {
                if (info.mDeposit == 1) {
                    depositTotalPrice = MoneyUtils.add(depositTotalPrice, info.mPrice);
                    attachPriceForDeposit = MoneyUtils.add(attachPriceForDeposit, info.mAttachPrice);
                }
            }
            return MoneyUtils.add(depositTotalPrice, attachPriceForDeposit);
        } else {
            return 0;
        }
    }

    private double getBookListPlatformDeposit() {
        List<BookInfoBean> bookList = DataRepository.getInstance().getBookList();
        if (bookList.size() > 0) {
            double depositTotalPrice = 0.00;
            double attachPriceForDeposit = 0.00;
            for (BookInfoBean info : bookList) {
                if (info.mDeposit == 1
                        && info.mUsedDepositType == UsedDepositType.PLATFORM_DEPOSIT) {
                    depositTotalPrice = MoneyUtils.add(depositTotalPrice, info.mPrice);
                    attachPriceForDeposit = MoneyUtils.add(attachPriceForDeposit, info.mAttachPrice);
                }
            }
            return MoneyUtils.add(depositTotalPrice, attachPriceForDeposit);
        } else {
            return 0;
        }
    }

    private double getBookListLibraryDeposit() {
        List<BookInfoBean> bookList = DataRepository.getInstance().getBookList();
        if (bookList.size() > 0) {
            double depositTotalPrice = 0.00;
            double attachPriceForDeposit = 0.00;
            for (BookInfoBean info : bookList) {
                if (info.mDeposit == 1
                        && info.mUsedDepositType == UsedDepositType.LIB_DEPOSIT) {
                    depositTotalPrice = MoneyUtils.add(depositTotalPrice, info.mPrice);
                    attachPriceForDeposit = MoneyUtils.add(attachPriceForDeposit, info.mAttachPrice);
                }
            }
            return MoneyUtils.add(depositTotalPrice, attachPriceForDeposit);
        } else {
            return 0;
        }
    }

    private void saveLibraryInfo(LibraryUserInfoVo libraryUserInfoVo) {
        mLibraryInfo = null;
        mLibraryInfo = new LibraryInfo();

        mLibraryInfo.mAgreementLevel = libraryUserInfoVo.data.library.customerAgreement.index;
        mLibraryInfo.mDepositPriority = libraryUserInfoVo.data.library.depositPriority;
        mLibraryInfo.mBorrowNum = libraryUserInfoVo.data.library.mBorrowNum;
        mLibraryInfo.mDeposit = libraryUserInfoVo.data.library.needDeposit;
        mLibraryInfo.mLibraryLev = libraryUserInfoVo.data.library.libraryLevelName;
        mLibraryInfo.mName = libraryUserInfoVo.data.library.name;
        mLibraryInfo.mLibraryStatus = libraryUserInfoVo.data.library.libraryStatus.index;
        mLibraryInfo.mHallCode = libraryUserInfoVo.data.hallCode;
        mLibraryInfo.mOperaterName = libraryUserInfoVo.data.username;
        mLibraryInfo.mOperaterId = libraryUserInfoVo.data.id;
        mLibraryInfo.mReaderLimit = libraryUserInfoVo.data.library.readerLimit;
        mLibraryInfo.mCustomerId = (null == libraryUserInfoVo.data.library.customerId)
                ? "0" : libraryUserInfoVo.data.library.customerId;
        mLibraryInfo.mHaveRefundAccount = libraryUserInfoVo.data.library.haveRefundAccount;

        if (null != libraryUserInfoVo.data.menus) {
            for (LibraryUserInfoVo.ResponseMenus menus : libraryUserInfoVo.data.menus) {
                //系统设置
                if (menus.id == 32) {//menus.permission.equals("system:timeoption")
                    mLibraryInfo.mOpenTimePermission = true;          //32 开放时间设置权限
                }
                if (menus.id == 25) {//menus.permission.equals("system:password")
                    mLibraryInfo.mPasswordManagePermission = true;    //25 操作员密码管理权限
                }
                //借还管理
                if (menus.id == 2 && mLibraryInfo.mLibraryStatus != 2) {//menus.permission.equals("borrow:index")
                    mLibraryInfo.mBorrowPermission = true;            //2借书管理权限 (注：如果当前馆被停用，则无借书管理权限)
                }
                if (menus.id == 3) {//menus.permission.equals("return:index")
                    mLibraryInfo.mReturnPermission = true;            //3还书管理权限
                }
                if (menus.id == 40) {//menus.permission.equals("reader:update")
                    mLibraryInfo.mReaderManagePermission = true;      //40读者管理权限-账户管理
                }
                //馆际流通
                if (menus.id == 9) {//menus.permission.equals("circulate:out")
                    mLibraryInfo.mCirculateOutPermission = true;      //9流出管理权限
                }
                if (menus.id == 10) {//menus.permission.equals("circulate:in")
                    mLibraryInfo.mCirculateInPermission = true;       //10流入管理权限
                }
                //退押金
                if (menus.id == 55) {//menus.permission.equals("circulate:out")
                    mLibraryInfo.mRefundDepositPermission = true;      //55退押金权限
                }
                //交押金
                if (menus.id == 54) {//menus.permission.equals("circulate:out")
                    mLibraryInfo.mChargeDepositPermission = true;      //54交押金权限
                }
                //统计分析
                if (menus.id == 13) {//menus.permission.equals("count:book")
                    mLibraryInfo.mCountBookPermission = true;         //13藏书统计权限
                }
                if (menus.id == 31) {//menus.permission.equals("count:debit")
                    mLibraryInfo.mCebitBookPermission = true;         //31在借统计权限
                }
                if (menus.id == 15) {//menus.permission.equals("count:borrow")
                    mLibraryInfo.mBorrowBookPermission = true;        //15借书统计权限
                }
                if (menus.id == 16) {//menus.permission.equals("count:return")
                    mLibraryInfo.mReturnBookPermission = true;        //16还书统计权限
                }
                if (menus.id == 17) {//menus.permission.equals("count:compensate")
                    mLibraryInfo.mDompensateBookPermission = true;    //17赔书统计权限
                }
                if (menus.id == 44) {//menus.permission.equals("count:sell")
                    mLibraryInfo.mSellPermission = true;              //44销售统计权限
                }
                if (menus.id == 20) {//menus.permission.equals("count:deposit")
                    mLibraryInfo.mDepositPermission = true;           //20收款统计权限
                }
                if (menus.id == 29) {//menus.permission.equals("count:reader")
                    mLibraryInfo.mReaderPermission = true;            //29读者统计权限
                }
                if (menus.id == 43) {
                    mLibraryInfo.mAppDepositPermission = true;        //43押金权限
                }
                if (menus.id == 52) {
                    mLibraryInfo.mPenaltyFreePermission = true;       //52免单权限
                }
                if (menus.id == 56) {
                    mLibraryInfo.mPenaltyFreeStatisticPermission = true;//56免单统计权限
                }
            }
        }
        SharedPreferencesUtil.getInstance().putObject("LibraryInfo", mLibraryInfo);

        setJPushParams(mLibraryInfo);
    }

    private void setJPushParams(LibraryInfo libraryInfo) {
        String platformId = "0";
        String customId = libraryInfo.mCustomerId;
        String province;
        String city;
        String area;
        String mLibraryLev;
        Set<String> tags = new HashSet<>();
        //配置JPush 参数
        if (null != libraryInfo.mAreaAddress) {
            String[] areas = libraryInfo.mAreaAddress.split("-");
            province = areas[0];
            city = areas[1];
            area = areas[2];
            mLibraryLev = TextUtils.isEmpty(libraryInfo.mLibraryLev) ? "" : libraryInfo.mLibraryLev;
        } else {
            province = "四川省";
            city = "成都市";
            area = "武侯区";
            mLibraryLev = "社区书屋";
        }
        tags.add(platformId + "_");
        tags.add(platformId + "_" + province);
        tags.add(platformId + "_" + province + "_" + city);
        tags.add(platformId + "_" + province + "_" + city + "_" + area);
        tags.add(platformId + "_" + mLibraryLev);
        tags.add(platformId + "_" + libraryInfo.mHallCode);

        tags.add(customId + "_");
        tags.add(customId + "_" + province);
        tags.add(customId + "_" + province + "_" + city);
        tags.add(customId + "_" + province + "_" + city + "_" + area);
        tags.add(customId + "_" + mLibraryLev);
        tags.add(customId + "_" + libraryInfo.mHallCode);
        JPushInterface.setAlias(Utils.getContext(), 0, "android_" + libraryInfo.mHallCode + "_" + libraryInfo.mOperaterName);
        JPushInterface.setTags(Utils.getContext(), 0, tags);
    }

    public ReaderInfo getReaderInfo(ReaderLoginInfoVo readerLoginInfoVo) {
        ReaderInfo readerInfo = new ReaderInfo();
        readerInfo.mReaderId = readerLoginInfoVo.data.id;
        readerInfo.mBorrowCard = readerLoginInfoVo.data.borrowCard;
        readerInfo.mCardName = readerLoginInfoVo.data.cardName;
        readerInfo.mGender = readerLoginInfoVo.data.gender;
        readerInfo.mIdCard = readerLoginInfoVo.data.idCard;
        readerInfo.mIdCardImage = readerLoginInfoVo.data.idcardImage;
        readerInfo.mPhone = readerLoginInfoVo.data.phone;
        readerInfo.mBorrowableSum = readerLoginInfoVo.data.canBorrowNum;
        readerInfo.mBorrowingNum = readerLoginInfoVo.data.currentBorrowNum;
        readerInfo.mOverdueNum = readerLoginInfoVo.data.overdueNum;
        readerInfo.mApplyPenalty = readerLoginInfoVo.data.readerDeposit.applyPenalty;
        readerInfo.mNotApplyPenalty = readerLoginInfoVo.data.readerDeposit.notApplyPenalty;

        readerInfo.mPlatformDeposit = new ReaderDepositInfo();
        readerInfo.mPlatformDeposit.mAvailableBalance = readerLoginInfoVo.data.readerDeposit.onlineDeposit.availableBalance;
        readerInfo.mPlatformDeposit.mBalance = readerLoginInfoVo.data.readerDeposit.onlineDeposit.balance;
        readerInfo.mPlatformDeposit.mOccupyDeposit = readerLoginInfoVo.data.readerDeposit.onlineDeposit.usedDeposit;

        readerInfo.mLibraryDeposit = new ReaderDepositInfo();
        readerInfo.mLibraryDeposit.mAvailableBalance = readerLoginInfoVo.data.readerDeposit.libraryDeposit.availableBalance;
        readerInfo.mLibraryDeposit.mBalance = readerLoginInfoVo.data.readerDeposit.libraryDeposit.balance;
        readerInfo.mLibraryDeposit.mOccupyDeposit = readerLoginInfoVo.data.readerDeposit.libraryDeposit.usedDeposit;

        readerInfo.mOfflineDeposit = new ReaderDepositInfo();
        readerInfo.mOfflineDeposit.mAvailableBalance = readerLoginInfoVo.data.readerDeposit.offlineDeposit.availableBalance;
        readerInfo.mOfflineDeposit.mBalance = readerLoginInfoVo.data.readerDeposit.offlineDeposit.balance;
        readerInfo.mOfflineDeposit.mOccupyDeposit = readerLoginInfoVo.data.readerDeposit.offlineDeposit.usedDeposit;
        return readerInfo;
    }
}
