package com.tzpt.cloudlibrary.modle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tzpt.cloudlibrary.CloudLibraryApplication;
import com.tzpt.cloudlibrary.Installation;
import com.tzpt.cloudlibrary.base.data.Note;
import com.tzpt.cloudlibrary.bean.ActionInfoBean;
import com.tzpt.cloudlibrary.bean.BannerInfo;
import com.tzpt.cloudlibrary.bean.CommentBean;
import com.tzpt.cloudlibrary.bean.DepositBalanceBean;
import com.tzpt.cloudlibrary.bean.HomeDataBean;
import com.tzpt.cloudlibrary.bean.InformationBean;
import com.tzpt.cloudlibrary.bean.OverdueMsgBean;
import com.tzpt.cloudlibrary.bean.SearchHotBean;
import com.tzpt.cloudlibrary.bean.SelfBookInfoBean;
import com.tzpt.cloudlibrary.bean.UserDepositBean;
import com.tzpt.cloudlibrary.bean.UserLibraryDepositBean;
import com.tzpt.cloudlibrary.bean.VideoSetBean;
import com.tzpt.cloudlibrary.business_bean.AliPayInfoBean;
import com.tzpt.cloudlibrary.business_bean.AvailableBalanceBean;
import com.tzpt.cloudlibrary.business_bean.BaseListResultData;
import com.tzpt.cloudlibrary.business_bean.BookBean;
import com.tzpt.cloudlibrary.business_bean.BorrowBookBean;
import com.tzpt.cloudlibrary.business_bean.BoughtBookBean;
import com.tzpt.cloudlibrary.business_bean.DepositCategoryBean;
import com.tzpt.cloudlibrary.business_bean.DepositType;
import com.tzpt.cloudlibrary.business_bean.EBookBean;
import com.tzpt.cloudlibrary.business_bean.LibraryBean;
import com.tzpt.cloudlibrary.business_bean.OperateReservationBookResultBean;
import com.tzpt.cloudlibrary.business_bean.PayDepositBean;
import com.tzpt.cloudlibrary.business_bean.PenaltyDealResultBean;
import com.tzpt.cloudlibrary.business_bean.ReadNoteBean;
import com.tzpt.cloudlibrary.business_bean.ReadNoteGroupBean;
import com.tzpt.cloudlibrary.business_bean.RefundDepositBean;
import com.tzpt.cloudlibrary.business_bean.ReservationBookBean;
import com.tzpt.cloudlibrary.business_bean.SelfHelpBookBean;
import com.tzpt.cloudlibrary.business_bean.SelfHelpBookInfoBean;
import com.tzpt.cloudlibrary.business_bean.SelfHelpReaderBean;
import com.tzpt.cloudlibrary.business_bean.WXPayInfoBean;
import com.tzpt.cloudlibrary.modle.local.SharedPreferencesUtil;
import com.tzpt.cloudlibrary.modle.remote.CloudLibraryApi;
import com.tzpt.cloudlibrary.modle.remote.exception.ExceptionEngine;
import com.tzpt.cloudlibrary.modle.remote.exception.ServerException;
import com.tzpt.cloudlibrary.modle.remote.newdownload.DownloadListener;
import com.tzpt.cloudlibrary.modle.remote.newdownload.DownloadTask;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.cause.EndCause;
import com.tzpt.cloudlibrary.modle.remote.newpojo.ActionListItemVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.ActionListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.ActivityVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.AliPayInfoVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.ApplyActionResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BannerDataListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BannerNewListItemVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseDataResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseResultEntityVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BillInfoVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BookBelongLibVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BookCategoryVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BookDetailInfoNewVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BookInLibListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BookListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BorrowBookDetailInfoVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BorrowBookListItemVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BorrowBookListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.CityVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.CommonReturnBookLibListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.CompensateDepositInfoVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.DepositCategoryVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.DistrictVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.EBookDetailInfoVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.EBookListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.EBookRecommendationsListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.EBookVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.HomeInfoVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.HtmlUrlVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.LastDistrictVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.LibDepositListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.LibInfoVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.LibIntroduceVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.LibraryVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.MainBranchLibraryVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.ModifyPwdResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.MyMessageVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.NewsVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.NoteListItemVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.NoteListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.NoteModifyResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.OperateReservationBookResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.OverdueMsgListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.PaperBookVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.PayPenaltyResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.PayResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.PraiseBookResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.ProvinceVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.RankingBookListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.RankingHomeVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.RecommendBookLibListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.RecommendBookResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.RenewBorrowBookResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.ReservationBookListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.ResetPwdResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.SearchHotResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.SelfBorrowBookInfoVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.SelfBorrowBookResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.SelfBorrowScanFirstVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.SelfBuyBookDetailVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.SelfBuyBookResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.SelfBuyBookShelfVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.SelfBuyBookVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.UserDepositLimitRegulationVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.UserDepositVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.VerifyCodeVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.VerifyIDCardVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.VersionUpdateVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.VideoVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.WXPayInfoVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.WithdrawResultVo;
import com.tzpt.cloudlibrary.ui.bookstore.BookStoreFilterType;
import com.tzpt.cloudlibrary.ui.bookstore.BookStoreFragment;
import com.tzpt.cloudlibrary.ui.bookstore.BookStorePresenter;
import com.tzpt.cloudlibrary.ui.ebook.EBookFilterType;
import com.tzpt.cloudlibrary.ui.ebook.EBookFragment;
import com.tzpt.cloudlibrary.ui.ebook.EBookPresenter;
import com.tzpt.cloudlibrary.ui.information.InformationFragment;
import com.tzpt.cloudlibrary.ui.information.InformationPresenter;
import com.tzpt.cloudlibrary.ui.library.LibraryFilterType;
import com.tzpt.cloudlibrary.ui.library.LibraryFragment;
import com.tzpt.cloudlibrary.ui.library.LibraryPresenter;
import com.tzpt.cloudlibrary.ui.paperbook.PaperBookFilterType;
import com.tzpt.cloudlibrary.ui.paperbook.PaperBookPresenter;
import com.tzpt.cloudlibrary.ui.paperbook.PaperBooksFragment;
import com.tzpt.cloudlibrary.ui.readers.ActivityListFragment;
import com.tzpt.cloudlibrary.ui.readers.ActivityListPresenter;
import com.tzpt.cloudlibrary.ui.search.SearchManager;
import com.tzpt.cloudlibrary.ui.video.VideoFilterType;
import com.tzpt.cloudlibrary.ui.video.VideoListFragment;
import com.tzpt.cloudlibrary.ui.video.VideoListPresenter;
import com.tzpt.cloudlibrary.utils.DateUtils;
import com.tzpt.cloudlibrary.utils.HtmlFormatUtil;
import com.tzpt.cloudlibrary.utils.ImageUrlUtils;
import com.tzpt.cloudlibrary.utils.MoneyUtils;
import com.tzpt.cloudlibrary.utils.StringUtils;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Module层提供给Presenter的接口
 * Created by Administrator on 2017/6/2.
 */

public class DataRepository {
    private static DataRepository mInstance;

    public static DataRepository getInstance() {
        if (mInstance == null) {
            mInstance = new DataRepository();
        }
        return mInstance;
    }

    private DataRepository() {

    }

    private static class HttpResultFunc<T> implements Func1<Throwable, Observable<T>> {
        @Override
        public Observable<T> call(Throwable throwable) {
            return Observable.error(ExceptionEngine.handleException(throwable));
        }
    }

    public Observable<BaseResultEntityVo<VersionUpdateVo>> checkVersion(String version) {
        return CloudLibraryApi.getInstance().checkVersion(version, 0);
    }

    public Observable<BaseResultEntityVo<BookListVo>> getHotBookList(Map<String, String> parameters) {
        return CloudLibraryApi.getInstance().getHotBookList(parameters);
    }

    public Observable<BaseResultEntityVo<BookListVo>> getNewBookList(Map<String, String> parameters) {
        return CloudLibraryApi.getInstance().getNewBookList(parameters);
    }

    public Observable<BaseResultEntityVo<BookListVo>> getSearchBookList(Map<String, String> parameters) {
        return CloudLibraryApi.getInstance().getSearchBookList(parameters);
    }

    public Observable<BaseResultEntityVo<BookListVo>> getLibrarySearchBookList(String libCode, Map<String, String> parameters) {
        return CloudLibraryApi.getInstance().getLibrarySearchBookList(libCode, parameters);
    }

    public Observable<BaseResultEntityVo<RankingHomeVo>> getRankingList() {
        return CloudLibraryApi.getInstance().getRankingList();
    }

    public Observable<BaseResultEntityVo<RankingBookListVo>> getRankingBorrowList(int pageNum, int pageCount, int sortType, int categoryId) {
        return CloudLibraryApi.getInstance().getRankingBorrowList(pageNum, pageCount, sortType, categoryId);
    }

    public Observable<BaseResultEntityVo<RankingBookListVo>> getRankingRecommendList(int pageNum, int pageCount, int sortType, int categoryId) {
        return CloudLibraryApi.getInstance().getRankingRecommendList(pageNum, pageCount, sortType, categoryId);
    }

    public Observable<List<LibraryBean>> searchRecommendLib(String idCard, String isbn, String keyword, String lngLat) {
        return CloudLibraryApi.getInstance().searchRecommendLib(idCard, isbn, keyword, lngLat)
                .map(new Func1<BaseResultEntityVo<RecommendBookLibListVo>, List<LibraryBean>>() {
                    @Override
                    public List<LibraryBean> call(BaseResultEntityVo<RecommendBookLibListVo> recommendBookLibListVoBaseResultEntityVo) {
                        if (recommendBookLibListVoBaseResultEntityVo.status == 200) {
                            if (recommendBookLibListVoBaseResultEntityVo.data != null
                                    && recommendBookLibListVoBaseResultEntityVo.data.list != null
                                    && recommendBookLibListVoBaseResultEntityVo.data.list.size() > 0) {
                                List<LibraryBean> libraryBeanList = new ArrayList<>();
                                for (RecommendBookLibListVo.RecommendLibraryListVo item : recommendBookLibListVoBaseResultEntityVo.data.list) {
                                    LibraryBean bean = new LibraryBean();
                                    bean.mLibrary.mId = item.libId;
                                    bean.mLibrary.mName = item.libName;
                                    bean.mLibrary.mAddress = item.address;
                                    bean.mLibrary.mCode = item.libCode;
//                                        bean.mLibrary.mLighten = item.lighten;
                                    bean.mLibrary.mBookCount = item.bookNum;
                                    bean.mLibrary.mLogo = ImageUrlUtils.getDownloadOriginalImagePath(item.logo);
                                    bean.mLibrary.mHeatCount = item.hotTip;
                                    bean.mIsOpen = StringUtils.libraryIsOpen(item.lightTime, item.serviceTime);
                                    bean.mDistance = item.distance;
                                    bean.recommendExist = item.recommondExist;
                                    bean.bookExist = item.bookExist;
                                    libraryBeanList.add(bean);
                                }
                                return libraryBeanList;
                            } else {
                                return null;
                            }
                        } else {
                            if (recommendBookLibListVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(recommendBookLibListVoBaseResultEntityVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<List<LibraryBean>>());
    }

    public Observable<BaseResultEntityVo<RankingBookListVo>> getRankingPraiseList(int pageNum, int pageCount, int sortType, int categoryId) {
        return CloudLibraryApi.getInstance().getRankingPraiseList(pageNum, pageCount, sortType, categoryId);
    }

    public Observable<BaseResultEntityVo<List<BannerNewListItemVo>>> getNewsList(String locationCode) {
        return CloudLibraryApi.getInstance().getNewList(locationCode);
    }

    public Observable<BaseResultEntityVo<List<BookCategoryVo>>> getBookCategory() {
        return CloudLibraryApi.getInstance().getBookCategory();
    }

    public Observable<BaseResultEntityVo<LibInfoVo>> getLibraryNumber(String hallCode) {
        return CloudLibraryApi.getInstance().getLibraryNumber(hallCode);
    }

    public Observable<BaseResultEntityVo<EBookListVo>> getEBookList(Map<String, String> parameters) {
        return CloudLibraryApi.getInstance().getEBookList(parameters);
    }

    public Observable<BaseResultEntityVo<EBookListVo>> getLibraryEBookList(String libCode, Map<String, String> parameters) {
        return CloudLibraryApi.getInstance().getLibraryEBookList(libCode, parameters);
    }

    public Observable<BaseResultEntityVo<EBookListVo>> getHotEBookList(Map<String, String> parameters) {
        return CloudLibraryApi.getInstance().getHotEBookList(parameters);
    }

    public Observable<BaseResultEntityVo<EBookRecommendationsListVo>> getRecommendationsEBookList(Map<String, String> parameters) {
        return CloudLibraryApi.getInstance().getRecommendationsEBookList(parameters);
    }

    public Observable<BaseResultEntityVo<EBookListVo>> getNewEBookList(Map<String, String> parameters) {
        return CloudLibraryApi.getInstance().getNewEBookList(parameters);
    }

    public Observable<BaseResultEntityVo<BookDetailInfoNewVo>> getBookDetail(String isbn, int fromSearch, String libCode) {
        String identity;
        if (UserRepository.getInstance().isLogin()) {
            identity = UserRepository.getInstance().getLoginUserIdCard();
        } else {
            identity = Installation.id(CloudLibraryApplication.getAppContext());
        }
        return CloudLibraryApi.getInstance().getBookDetail(isbn, identity, 1, fromSearch, libCode);
    }

    public Observable<BaseResultEntityVo<List<BookBelongLibVo>>> getBookBelongLib(String isbn, String lngLat) {
        return CloudLibraryApi.getInstance().getBookBelongLib(isbn, lngLat);
    }

    public Observable<BaseResultEntityVo<BookBelongLibVo>> getEBookBelongLib(String id, String lngLat, String readerId) {
        return CloudLibraryApi.getInstance().getEBookBelongLib(id, lngLat, readerId);
    }

    public Observable<BaseResultEntityVo<BookInLibListVo>> getBookDetailSameBookList(String isbn,
                                                                                     String libCode) {
        return CloudLibraryApi.getInstance().getBookDetailSameBookList(isbn, libCode);
    }

    public Observable<BaseResultEntityVo<LibIntroduceVo>> getLibIntroduce(String lngLat, String libCode, int fromSearch, int flag) {
        return CloudLibraryApi.getInstance().getLibIntroduce(lngLat, libCode, fromSearch, flag);
    }

    public Observable<BaseResultEntityVo<EBookDetailInfoVo>> getEBookDetail(String id, String libCode, int fromSearch) {
        String identity;
        if (UserRepository.getInstance().isLogin()) {
            identity = UserRepository.getInstance().getLoginUserIdCard();
        } else {
            identity = Installation.id(CloudLibraryApplication.getAppContext());
        }
        return CloudLibraryApi.getInstance().getEBookDetail(id, libCode, identity, 1, fromSearch);
    }

    public Observable<BaseResultEntityVo<ActionListItemVo>> getActionDetailInfo(int activityId,
                                                                                int fromSearch,
                                                                                String idCard,
                                                                                String keyword,
                                                                                String libCode) {
        return CloudLibraryApi.getInstance().getActionDetailInfo(activityId, fromSearch, keyword, idCard, libCode);
    }

    public Observable<BaseListResultData<ReservationBookBean>> getReservationBookList(int pageNo, int pageCount, String idCard) {
        return CloudLibraryApi.getInstance().getReservationBookList(pageNo, pageCount, idCard)
                .map(new Func1<BaseResultEntityVo<ReservationBookListVo>, BaseListResultData<ReservationBookBean>>() {
                    @Override
                    public BaseListResultData<ReservationBookBean> call(BaseResultEntityVo<ReservationBookListVo> reservationBookListVoBaseResultEntityVo) {
                        if (reservationBookListVoBaseResultEntityVo.status == 200) {
                            if (reservationBookListVoBaseResultEntityVo.data != null
                                    && reservationBookListVoBaseResultEntityVo.data.resultList != null
                                    && reservationBookListVoBaseResultEntityVo.data.resultList.size() > 0) {
                                List<ReservationBookBean> borrowBookBeanList = new ArrayList<>();
                                for (ReservationBookListVo.ReservationBookItemVo item : reservationBookListVoBaseResultEntityVo.data.resultList) {
                                    ReservationBookBean bean = new ReservationBookBean();
                                    bean.mBook.mBookId = item.id;
                                    bean.mBook.mName = item.bookName;
                                    bean.mBook.mIsbn = item.isbn;
                                    bean.mBook.mCoverImg = ImageUrlUtils.getDownloadOriginalImagePath(item.image);
                                    bean.mAuthor.mName = item.author;
                                    bean.mPress.mName = item.publisher;
                                    bean.mBook.mPublishDate = TextUtils.isEmpty(item.publishYear) ? "暂无数据" : item.publishYear;
                                    bean.mLibrary.mCode = item.libCode;
                                    bean.mLibrary.mName = item.libName;
                                    bean.mValidTime = DateUtils.formatDate(item.appointTime) + " - " + DateUtils.formatDate(item.appointTimeEnd);
                                    bean.mAppointTimeEnd = DateUtils.formatDateSureToSecond(item.appointTimeEnd);
                                    //排架信息
                                    bean.mBook.mBarNumber = item.barNumber;
                                    bean.mBook.mCallNumber = item.callNumber;
                                    bean.mFrameCode = TextUtils.isEmpty(item.frameCode) ? "" : item.frameCode;
                                    bean.mRemark = (item.isNeedIdCard == 1 ? "(需身份证)" : "");

                                    bean.mStoreRoom = item.storeRoom;
                                    bean.mStoreRoomName = TextUtils.isEmpty(item.storeRoomName) ? "" : item.storeRoomName;
                                    borrowBookBeanList.add(bean);
                                }
                                BaseListResultData<ReservationBookBean> baseResultData = new BaseListResultData<>();
                                baseResultData.mTotalCount = reservationBookListVoBaseResultEntityVo.data.totalCount;
                                baseResultData.mResultList = borrowBookBeanList;
                                return baseResultData;
                            } else {
                                return null;
                            }
                        } else {
                            if (reservationBookListVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(reservationBookListVoBaseResultEntityVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<BaseListResultData<ReservationBookBean>>());
    }

    public Observable<Boolean> modifyPwd(String newPassword, String id, String oldPassword) {
        return CloudLibraryApi.getInstance().modifyPwd(newPassword, id, oldPassword)
                .map(new Func1<BaseResultEntityVo<ModifyPwdResultVo>, Boolean>() {
                    @Override
                    public Boolean call(BaseResultEntityVo<ModifyPwdResultVo> modifyPwdResultVoBaseResultEntityVo) {
                        if (modifyPwdResultVoBaseResultEntityVo.status == 200) {
                            return true;
                        } else {
                            if (modifyPwdResultVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(modifyPwdResultVoBaseResultEntityVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    public Observable<OperateReservationBookResultBean> operateReservationBook(int appointType, long id, String idCard) {
        return CloudLibraryApi.getInstance().operateReservationBook(appointType, id, idCard)
                .map(new Func1<BaseResultEntityVo<OperateReservationBookResultVo>, OperateReservationBookResultBean>() {
                    @Override
                    public OperateReservationBookResultBean call(BaseResultEntityVo<OperateReservationBookResultVo> operateReservationBookResultVoBaseResultEntityVo) {
                        if (operateReservationBookResultVoBaseResultEntityVo.status == 200) {
                            OperateReservationBookResultBean result = new OperateReservationBookResultBean();
                            result.mIsOpeateSuccess = true;
                            if (operateReservationBookResultVoBaseResultEntityVo.data != null) {
                                result.mIsNeedIDCard = operateReservationBookResultVoBaseResultEntityVo.data.needIdCard == 1;
                            }
                            UserRepository.getInstance().refreshUserInfo();
                            return result;
                        } else {
                            if (operateReservationBookResultVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(operateReservationBookResultVoBaseResultEntityVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<OperateReservationBookResultBean>());
    }

    public Observable<Boolean> applyAction(int activityId, String idCard, String name, String phone) {
        return CloudLibraryApi.getInstance().applyAction(activityId, idCard, name, phone)
                .map(new Func1<BaseResultEntityVo<ApplyActionResultVo>, Boolean>() {
                    @Override
                    public Boolean call(BaseResultEntityVo<ApplyActionResultVo> applyActionResultVoBaseResultEntityVo) {
                        if (applyActionResultVoBaseResultEntityVo.status == 200) {
                            UserRepository.getInstance().refreshUserInfo();
                            return true;
                        } else {
                            if (applyActionResultVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(applyActionResultVoBaseResultEntityVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    public Observable<BaseListResultData<BorrowBookBean>> getBorrowBookList(String idCard, final int pageNo, int pageCount, int status) {
        return CloudLibraryApi.getInstance().getBorrowBookList(idCard, pageNo, pageCount, status)
                .map(new Func1<BaseResultEntityVo<BorrowBookListVo>, BaseListResultData<BorrowBookBean>>() {
                    @Override
                    public BaseListResultData<BorrowBookBean> call(BaseResultEntityVo<BorrowBookListVo> borrowBookListVoBaseResultEntityVo) {
                        if (borrowBookListVoBaseResultEntityVo.status == 200) {
                            if (pageNo == 1) {
                                if (UserRepository.getInstance().getUserBorrowOverdueUnReadSum() > 0) {
                                    //逾期消息数量清零
                                    UserRepository.getInstance().clearBorrowOverdueSum();
                                    UserRepository.getInstance().refreshUserInfo();
                                }
                            }
                            return getBorrowBookList(borrowBookListVoBaseResultEntityVo);
                        } else {
                            if (borrowBookListVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(borrowBookListVoBaseResultEntityVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<BaseListResultData<BorrowBookBean>>());
    }

    public Observable<BaseListResultData<BorrowBookBean>> getHistoryBookList(String idCard, int pageNo, int pageCount, int type) {
        return CloudLibraryApi.getInstance().getHistoryBookList(idCard, pageNo, pageCount, type)
                .map(new Func1<BaseResultEntityVo<BorrowBookListVo>, BaseListResultData<BorrowBookBean>>() {
                    @Override
                    public BaseListResultData<BorrowBookBean> call(BaseResultEntityVo<BorrowBookListVo> borrowBookListVoBaseResultEntityVo) {
                        if (borrowBookListVoBaseResultEntityVo.status == 200) {
                            return getBorrowBookList(borrowBookListVoBaseResultEntityVo);
                        } else {
                            if (borrowBookListVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(borrowBookListVoBaseResultEntityVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<BaseListResultData<BorrowBookBean>>());
    }

    private BaseListResultData<BorrowBookBean> getBorrowBookList(BaseResultEntityVo<BorrowBookListVo> borrowBookListVoBaseResultEntityVo) {
        if (borrowBookListVoBaseResultEntityVo.data != null
                && borrowBookListVoBaseResultEntityVo.data.resultList != null
                && borrowBookListVoBaseResultEntityVo.data.resultList.size() > 0) {
            List<BorrowBookBean> borrowBookBeanList = new ArrayList<>();
            for (BorrowBookListItemVo item : borrowBookListVoBaseResultEntityVo.data.resultList) {
                BorrowBookBean bean = new BorrowBookBean();
                bean.mBook.mCoverImg = ImageUrlUtils.getDownloadOriginalImagePath(item.image);
                bean.mBorrowerId = item.id;
                bean.mAuthor.mName = item.author;
                bean.mBook.mName = item.bookName;
                bean.mBook.mIsbn = item.isbn;
                bean.mCategory.mName = item.categoryName;
                bean.mPress.mName = item.publisher;

                if (null != item.publishDate && item.publishDate.length() >= 4) {
                    bean.mBook.mPublishDate = item.publishDate.substring(0, 4);
                } else {
                    bean.mBook.mPublishDate = "暂无数据";
                }
                bean.mLibrary.mName = item.libName;
                bean.mBook.mId = item.bookId;
                //是否点赞 点赞1 true  未点赞 0 null -false
                bean.mIsPraised = item.isPraiseD != 0;
                bean.mLibrary.mLibStatus = item.libraryStatus;

                //历史借阅
                //借书日期
                //还书日期
                //borrowState 借阅状态 5:在借 6:归还 7:赔偿 28:逾期购买
                //判断是历史借阅还是当前借阅 -- 目的是为了给item.mHistoryBorrowDate赋值用来区分
//                item.returnTimeStr = "20190320";
                if (!TextUtils.isEmpty(item.returnTimeStr)) {
                    //还书时间不为空 是历史借阅
                    bean.mHistoryBorrowDate = item.borrowTimeStr + "-" + item.returnTimeStr;
                } else {
                    //还书时间为空 就是当前借阅
                    bean.mHistoryBorrowDate = item.borrowTimeStr;
                }
                bean.mHistoryBackDate = item.returnTimeStr;
                bean.mIsBuy = item.borrowState == 28;
                bean.mIsLost = item.borrowState == 7;
                bean.mBorrowState = item.borrowState;
                bean.mHasDays = item.daysRemaining;//给剩余天数赋值
                if (item.mayRenew == 1) {//不能续借  mayRenew 0不能续借 1可以续借
                    //可以续借的状态 要看returnTime是否为null 如果被续借过 就不能再续借了
                    if (item.renewTimes != 0) {//被续借过 不能续借
                        bean.mOneKeyToRenew = false;
                    } else {//没有被续借过 可以续借
                        bean.mOneKeyToRenew = true;
                    }
                } else {//不能续借
                    bean.mOneKeyToRenew = false;
                }
                //给是否逾期赋值
                if (item.overDue == 1 || item.overDue == 0) {//1逾期0未知
                    bean.mIsOverdue = true;
                } else if (item.overDue == 2){//2未逾期
                    bean.mIsOverdue = false;
                }
                bean.mBorrowDays = item.borrowDays;
                borrowBookBeanList.add(bean);
            }
            BaseListResultData<BorrowBookBean> baseResultData = new BaseListResultData<>();
            baseResultData.mTotalCount = borrowBookListVoBaseResultEntityVo.data.totalCount;
            baseResultData.mResultList = borrowBookBeanList;
            return baseResultData;
        } else {
            return null;
        }
    }

    public Observable<BaseResultEntityVo<VerifyIDCardVo>> verifyIDCard(String idCard) {
        return CloudLibraryApi.getInstance().verifyIDCard(idCard);
    }

    public Observable<BaseResultEntityVo<VerifyCodeVo>> getVerifyCodeForgetPwd(String phone) {
        return CloudLibraryApi.getInstance().getVerifyCodeForgetPwd(phone);
    }

    public Observable<BaseResultEntityVo<VerifyCodeVo>> checkVerifyCodeForgetPwd(String code, String phone) {
        return CloudLibraryApi.getInstance().checkVerifyCodeForgetPwd(code, phone);
    }

    public Observable<BaseResultEntityVo<ResetPwdResultVo>> resetForgetPwd(String idCard, String newPassword) {
        return CloudLibraryApi.getInstance().resetForgetPwd(idCard, newPassword);
    }

    public Observable<BaseListResultData<UserDepositBean>> getUserBill(String readerId, DepositCategoryBean depositCategory, int pageNo, int pageCount) {
        ArrayMap<String, Object> map = new ArrayMap<>();
        map.put("readerId", readerId);
        if (!TextUtils.isEmpty(depositCategory.mName)
                && !TextUtils.isEmpty(depositCategory.mValue)) {
            map.put(depositCategory.mName, depositCategory.mValue);
        }
        map.put("pageNo", pageNo);
        map.put("pageCount", pageCount);
        return CloudLibraryApi.getInstance().getUserBill(map)
                .map(new Func1<BaseResultEntityVo<BillInfoVo>, BaseListResultData<UserDepositBean>>() {
                    @Override
                    public BaseListResultData<UserDepositBean> call(BaseResultEntityVo<BillInfoVo> billInfoVoBaseResultEntityVo) {
                        if (billInfoVoBaseResultEntityVo.status == 200) {
                            if (billInfoVoBaseResultEntityVo.data.resultList != null
                                    && billInfoVoBaseResultEntityVo.data.resultList.size() > 0) {
                                List<UserDepositBean> userDepositBeanList = new ArrayList<>();
                                for (BillInfoVo.BillListItemVo item : billInfoVoBaseResultEntityVo.data.resultList) {
                                    UserDepositBean userDepositBean = new UserDepositBean();
                                    userDepositBean.mOperation = item.transName;
                                    userDepositBean.mRemark = item.remark;
                                    userDepositBean.mOperationDate = DateUtils.formatDate(item.transTime);
                                    if (item.transMoney > 0) {
                                        userDepositBean.mMoney = "+" + MoneyUtils.formatMoney(item.transMoney);
                                    } else {
                                        userDepositBean.mMoney = MoneyUtils.formatMoney(item.transMoney);
                                    }
                                    userDepositBean.mOrderNum = item.orderNumber;
                                    userDepositBean.mOperateOrder = item.operOrder;
                                    userDepositBean.mPayRemark = item.payRemark;
                                    userDepositBean.mDeductionMoney = item.deductionMoney;
                                    userDepositBean.mIsRefund = item.isRefund == 1;
                                    userDepositBean.mComment = item.comment;
                                    if (item.transStatus != null &&
                                            (item.transStatus.index == 2 || item.transStatus.index == 3)) {
                                        userDepositBean.mStatus = item.payStatus;
                                    }
                                    if (item.transType != null) {
                                        userDepositBean.mTransactionType = item.transType.index;
                                    }
                                    userDepositBeanList.add(userDepositBean);
                                }

                                BaseListResultData<UserDepositBean> baseResultData = new BaseListResultData<>();
                                baseResultData.mTotalCount = billInfoVoBaseResultEntityVo.data.totalCount;
                                baseResultData.mResultList = userDepositBeanList;
                                return baseResultData;
                            } else {
                                return null;
                            }
                        } else {
                            if (billInfoVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(billInfoVoBaseResultEntityVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<BaseListResultData<UserDepositBean>>());
    }

    public Observable<BaseResultEntityVo<DepositCategoryVo>> getDepositCategory() {
        return CloudLibraryApi.getInstance().getDepositCategory();
    }

    public Observable<BaseListResultData<UserLibraryDepositBean>> getLibDeposit(String idCard, int pageNo, int pageCount) {
        return CloudLibraryApi.getInstance().getLibDeposit(idCard, pageNo, pageCount)
                .map(new Func1<BaseResultEntityVo<LibDepositListVo>, BaseListResultData<UserLibraryDepositBean>>() {
                    @Override
                    public BaseListResultData<UserLibraryDepositBean> call(BaseResultEntityVo<LibDepositListVo> libDepositListVoBaseResultEntityVo) {
                        if (libDepositListVoBaseResultEntityVo.status == 200) {
                            if (libDepositListVoBaseResultEntityVo.data.resultList != null
                                    && libDepositListVoBaseResultEntityVo.data.resultList.size() > 0) {
                                List<UserLibraryDepositBean> libraryDepositBeanList = new ArrayList<>();
                                for (LibDepositListVo.LibDepositItemVo item : libDepositListVoBaseResultEntityVo.data.resultList) {
                                    UserLibraryDepositBean bean = new UserLibraryDepositBean();
                                    bean.hallCode = item.customerHallCode;
                                    bean.name = item.customerName;
                                    bean.total = MoneyUtils.formatMoney(item.customerDeposit);
                                    bean.canUse = MoneyUtils.formatMoney(item.customerCanDeposit);
                                    libraryDepositBeanList.add(bean);
                                }
                                BaseListResultData<UserLibraryDepositBean> baseResultData = new BaseListResultData<>();
                                baseResultData.mTotalCount = libDepositListVoBaseResultEntityVo.data.totalCount;
                                baseResultData.mResultList = libraryDepositBeanList;
                                return baseResultData;
                            } else {
                                return null;
                            }
                        } else {
                            if (libDepositListVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(libDepositListVoBaseResultEntityVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<BaseListResultData<UserLibraryDepositBean>>());
    }

    public Observable<BaseResultEntityVo<NoteListVo>> getReaderNotes(String idCard, int pageCount, int pageNo) {
        return CloudLibraryApi.getInstance().getReaderNotes(idCard, pageCount, pageNo);
    }

    public Observable<Boolean> renewBorrowBook(String idCard, long borrowerBookId) {
        return CloudLibraryApi.getInstance().renewBorrowBook(idCard, borrowerBookId)
                .map(new Func1<BaseResultEntityVo<RenewBorrowBookResultVo>, Boolean>() {
                    @Override
                    public Boolean call(BaseResultEntityVo<RenewBorrowBookResultVo> renewBorrowBookResultVoBaseResultEntityVo) {
                        if (renewBorrowBookResultVoBaseResultEntityVo.status == 200) {
                            return true;
                        } else {
                            if (renewBorrowBookResultVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(renewBorrowBookResultVoBaseResultEntityVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    public Observable<Boolean> praiseBook(long borrowerBookId, int praise) {
        return CloudLibraryApi.getInstance().praiseBook(borrowerBookId, praise)
                .map(new Func1<BaseResultEntityVo<PraiseBookResultVo>, Boolean>() {
                    @Override
                    public Boolean call(BaseResultEntityVo<PraiseBookResultVo> praiseBookResultVoBaseResultEntityVo) {
                        if (praiseBookResultVoBaseResultEntityVo.status == 200) {
                            return true;
                        } else {
                            if (praiseBookResultVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(praiseBookResultVoBaseResultEntityVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    public Observable<BaseResultEntityVo<BorrowBookDetailInfoVo>> getBorrowBookDetail(long borrowerBookId) {
        return CloudLibraryApi.getInstance().getBorrowBookDetail(borrowerBookId);
    }

    public Observable<Boolean> noteModify(long id, String readingNote) {
        return CloudLibraryApi.getInstance().noteModify(id, readingNote)
                .map(new Func1<BaseResultEntityVo<NoteModifyResultVo>, Boolean>() {
                    @Override
                    public Boolean call(BaseResultEntityVo<NoteModifyResultVo> noteModifyResultVoBaseResultEntityVo) {
                        if (noteModifyResultVoBaseResultEntityVo.status == 200) {
                            return true;
                        } else {
                            if (noteModifyResultVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(noteModifyResultVoBaseResultEntityVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    public Observable<Long> noteAdd(long borrowerBookId, long buyBookId, String idCard, String readingNote) {
        return CloudLibraryApi.getInstance().noteAdd(borrowerBookId, buyBookId, idCard, readingNote)
                .map(new Func1<BaseResultEntityVo<NoteModifyResultVo>, Long>() {
                    @Override
                    public Long call(BaseResultEntityVo<NoteModifyResultVo> noteModifyResultVoBaseResultEntityVo) {
                        if (noteModifyResultVoBaseResultEntityVo.status == 200) {
                            return noteModifyResultVoBaseResultEntityVo.data.noteId;
                        } else {
                            if (noteModifyResultVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(noteModifyResultVoBaseResultEntityVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<Long>());
    }

    public Observable<Boolean> delNote(long noteId) {
        return CloudLibraryApi.getInstance().delNote(noteId)
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
                }).onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    public Observable<BaseResultEntityVo<CompensateDepositInfoVo>> getCompensateDepositInfo(double compensatePrice,
                                                                                            String hallCode,
                                                                                            String readerId,
                                                                                            int usedDepositType) {
        return CloudLibraryApi.getInstance().getCompensateDepositInfo(compensatePrice, hallCode, readerId, usedDepositType);
    }

    public Observable<BaseResultEntityVo<UserDepositVo>> getDepositInfo(String idCard, String hallCode) {
        return CloudLibraryApi.getInstance().getDepositInfo(idCard, hallCode);
    }

    public Observable<AvailableBalanceBean> getAvailableBalance(String hallCode, String readerId) {
        ArrayMap<String, Object> map = new ArrayMap<>();
        map.put("readerId", readerId);
        map.put("hallCode", hallCode);

        return CloudLibraryApi.getInstance().getDepositInfo(map)
                .map(new Func1<BaseResultEntityVo<UserDepositVo>, AvailableBalanceBean>() {
                    @Override
                    public AvailableBalanceBean call(BaseResultEntityVo<UserDepositVo> userDepositVoBaseResultEntityVo) {
                        if (userDepositVoBaseResultEntityVo.status == 200) {
                            AvailableBalanceBean availableBalanceBean = new AvailableBalanceBean();
                            for (int i = 0; i < userDepositVoBaseResultEntityVo.data.depositInfo.size(); i++) {
                                UserDepositVo.DepositInfoVo item = userDepositVoBaseResultEntityVo.data.depositInfo.get(i);
                                DepositBalanceBean info = new DepositBalanceBean();
                                info.mName = item.name;
                                info.mLibCode = item.code;
                                info.mIsUnusual = item.isUnusual;
                                info.mUsableDeposit = item.activeDeposit;
                                info.mDepositBalance = item.deposit;
                                info.mOccupyDeposit = item.usedDeposit;
                                info.mPenalty = item.penalty;
                                info.mPenaltyHandleType = item.penaltyHandleType;

                                if (item.activeDeposit > 0 && (item.code == null || item.code.equals(""))) {
                                    availableBalanceBean.mUsablePlatformDeposit = item.activeDeposit;
                                }
                                if (item.activeDeposit > 0 && (item.code != null && !item.code.equals(""))) {
                                    availableBalanceBean.mUsableLibDeposit += item.activeDeposit;
                                }
                            }
                            return availableBalanceBean;
                        } else {
                            if (userDepositVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(userDepositVoBaseResultEntityVo.data.errorCode,
                                    userDepositVoBaseResultEntityVo.data.message);
                        }

                    }
                })
                .onErrorResumeNext(new HttpResultFunc<AvailableBalanceBean>());
    }

    public Observable<List<DepositBalanceBean>> getDepositInfo(String readerId) {
        ArrayMap<String, Object> map = new ArrayMap<>();
        map.put("readerId", readerId);
        return CloudLibraryApi.getInstance().getDepositInfo(map)
                .map(new Func1<BaseResultEntityVo<UserDepositVo>, List<DepositBalanceBean>>() {
                    @Override
                    public List<DepositBalanceBean> call(BaseResultEntityVo<UserDepositVo> userDepositVoBaseResultEntityVo) {
                        if (userDepositVoBaseResultEntityVo.status == 200) {
                            if (userDepositVoBaseResultEntityVo.data.depositInfo != null
                                    && userDepositVoBaseResultEntityVo.data.depositInfo.size() > 0) {
                                List<DepositBalanceBean> list = new ArrayList<>();
                                for (UserDepositVo.DepositInfoVo item : userDepositVoBaseResultEntityVo.data.depositInfo) {
                                    DepositBalanceBean info = new DepositBalanceBean();
                                    info.mName = item.name;
                                    info.mLibCode = item.code;
                                    info.mIsUnusual = item.isUnusual;
                                    info.mDepositBalance = item.deposit;
                                    info.mOccupyDeposit = item.usedDeposit;
                                    info.mUsableDeposit = item.activeDeposit;
                                    info.mPenalty = item.penalty;
                                    info.mPenaltyHandleType = item.penaltyHandleType;
                                    list.add(info);
                                }
                                return list;
                            } else {
                                return null;
                            }
                        } else {
                            if (userDepositVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(userDepositVoBaseResultEntityVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<List<DepositBalanceBean>>());
    }

    public Observable<PayDepositBean> getPayDepositInfo(String readerId) {
        ArrayMap<String, Object> map = new ArrayMap<>();
        map.put("readerId", readerId);

        return Observable.zip(CloudLibraryApi.getInstance().getDepositInfo(map),
                CloudLibraryApi.getInstance().getAccountLimitRegulation(readerId),
                new Func2<BaseResultEntityVo<UserDepositVo>, BaseResultEntityVo<UserDepositLimitRegulationVo>, PayDepositBean>() {
                    @Override
                    public PayDepositBean call(BaseResultEntityVo<UserDepositVo> userDepositVoBaseResultEntityVo,
                                               BaseResultEntityVo<UserDepositLimitRegulationVo> userDepositLimitRegulationVoBaseResultEntityVo) {
                        if (userDepositVoBaseResultEntityVo.status == 200
                                && userDepositLimitRegulationVoBaseResultEntityVo.status == 200) {
                            PayDepositBean payDepositBean = new PayDepositBean();
                            payDepositBean.mDepositList = new ArrayList<>();
                            payDepositBean.mPenaltyList = new ArrayList<>();

                            for (int i = 0; i < userDepositVoBaseResultEntityVo.data.depositInfo.size(); i++) {
                                UserDepositVo.DepositInfoVo item = userDepositVoBaseResultEntityVo.data.depositInfo.get(i);
                                DepositBalanceBean info = new DepositBalanceBean();
                                info.mName = item.name;
                                info.mLibCode = item.code;
                                info.mIsUnusual = item.isUnusual;
                                info.mUsableDeposit = item.activeDeposit;
                                info.mDepositBalance = item.deposit;
                                info.mOccupyDeposit = item.usedDeposit;
                                info.mPenalty = item.penalty;
                                info.mPenaltyHandleType = item.penaltyHandleType;

                                payDepositBean.mDeposit += item.deposit;
                                payDepositBean.mUsedDeposit += item.usedDeposit;
                                payDepositBean.mPenalty += item.penalty;
                                payDepositBean.mActiveDeposit += item.activeDeposit;

                                if (item.deposit > 0) {
                                    payDepositBean.mDepositList.add(info);
                                }
                                if (item.penalty > 0) {
                                    payDepositBean.mPenaltyList.add(info);
                                }
                            }
                            payDepositBean.mLimitMoney = userDepositLimitRegulationVoBaseResultEntityVo.data.availablePay;
                            return payDepositBean;
                        } else if (userDepositVoBaseResultEntityVo.status != 200) {
                            if (userDepositVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(userDepositVoBaseResultEntityVo.data.errorCode,
                                    userDepositVoBaseResultEntityVo.data.message);
                        } else {
                            if (userDepositLimitRegulationVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(userDepositLimitRegulationVoBaseResultEntityVo.data.errorCode,
                                    userDepositLimitRegulationVoBaseResultEntityVo.data.message);
                        }
                    }
                })
                .onErrorResumeNext(new HttpResultFunc<PayDepositBean>());
    }

    public Observable<RefundDepositBean> getRefundDepositInfo(String readerId) {
        ArrayMap<String, Object> map = new ArrayMap<>();
        map.put("readerId", readerId);

        return Observable.zip(CloudLibraryApi.getInstance().getDepositInfo(map),
                CloudLibraryApi.getInstance().getAccountLimitRegulation(readerId),
                new Func2<BaseResultEntityVo<UserDepositVo>, BaseResultEntityVo<UserDepositLimitRegulationVo>, RefundDepositBean>() {
                    @Override
                    public RefundDepositBean call(BaseResultEntityVo<UserDepositVo> userDepositVoBaseResultEntityVo,
                                                  BaseResultEntityVo<UserDepositLimitRegulationVo> userDepositLimitRegulationVoBaseResultEntityVo) {
                        if (userDepositVoBaseResultEntityVo.status == 200
                                && userDepositLimitRegulationVoBaseResultEntityVo.status == 200) {
                            RefundDepositBean refundDeposit = new RefundDepositBean();
                            if (null != userDepositVoBaseResultEntityVo.data.depositInfo) {
                                refundDeposit.mOffLineDepositList = new ArrayList<>();
                                for (int i = 0; i < userDepositVoBaseResultEntityVo.data.depositInfo.size(); i++) {
                                    UserDepositVo.DepositInfoVo item = userDepositVoBaseResultEntityVo.data.depositInfo.get(i);
                                    DepositBalanceBean info = new DepositBalanceBean();
                                    info.mName = item.name;
                                    info.mLibCode = item.code;
                                    info.mIsUnusual = item.isUnusual;
                                    info.mUsableDeposit = item.activeDeposit;
                                    info.mDepositBalance = item.deposit;
                                    info.mOccupyDeposit = item.usedDeposit;
                                    info.mPenalty = item.penalty;
                                    info.mPenaltyHandleType = item.penaltyHandleType;

                                    if ((item.deposit > 0)
                                            && (item.code != null && !item.code.equals(""))) {
                                        refundDeposit.mOffLineDepositList.add(info);
                                    }
                                    if (item.deposit > 0 && (item.code == null || item.code.equals(""))) {
                                        refundDeposit.mPlatformAvailableBalance = item.activeDeposit;
                                    }
                                    if (item.deposit > 0 && (item.code != null && !item.code.equals(""))) {
                                        refundDeposit.mLibAvailableBalance += item.activeDeposit;
                                    }
                                }

                                refundDeposit.mIsNoWithDrawable = userDepositLimitRegulationVoBaseResultEntityVo.data.isLimit;
                                refundDeposit.mMaxAmount = userDepositLimitRegulationVoBaseResultEntityVo.data.maxAmount;
                                refundDeposit.mNoWithdrawMsg = userDepositLimitRegulationVoBaseResultEntityVo.data.limitMessage;
                                refundDeposit.mNeedDealPenalty = userDepositVoBaseResultEntityVo.data.needHandlePenalty;
                                refundDeposit.mNeedHandleFreePenalty = userDepositVoBaseResultEntityVo.data.needHandleFreePenalty;
                            }
                            return refundDeposit;
                        } else if (userDepositVoBaseResultEntityVo.status != 200) {
                            if (userDepositVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(userDepositVoBaseResultEntityVo.data.errorCode,
                                    userDepositVoBaseResultEntityVo.data.message);
                        } else {
                            if (userDepositLimitRegulationVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(userDepositLimitRegulationVoBaseResultEntityVo.data.errorCode,
                                    userDepositLimitRegulationVoBaseResultEntityVo.data.message);
                        }
                    }
                })
                .onErrorResumeNext(new HttpResultFunc<RefundDepositBean>());
    }

    public Observable<SelfHelpBookInfoBean> getSelfBookInfo(String barNumber, String stayHallCode, String readerId) {
        return CloudLibraryApi.getInstance().selfBorrowBookInfo(barNumber, stayHallCode, readerId)
                .map(new Func1<BaseResultEntityVo<SelfBorrowBookInfoVo>, SelfHelpBookInfoBean>() {
                    @Override
                    public SelfHelpBookInfoBean call(BaseResultEntityVo<SelfBorrowBookInfoVo> selfBorrowBookInfoVoBaseResultEntityVo) {
                        if (selfBorrowBookInfoVoBaseResultEntityVo.status == 200) {
                            SelfHelpBookInfoBean book = new SelfHelpBookInfoBean();
                            book.mAttachPrice = selfBorrowBookInfoVoBaseResultEntityVo.data.attachPrice;
                            book.mBelongLibraryHallCode = selfBorrowBookInfoVoBaseResultEntityVo.data.belongLibraryHallCode;
                            book.mStayLibraryHallCode = selfBorrowBookInfoVoBaseResultEntityVo.data.stayLibraryHallCode;
                            book.mBook.mBarNumber = selfBorrowBookInfoVoBaseResultEntityVo.data.barNumber;
                            book.mBook.mName = selfBorrowBookInfoVoBaseResultEntityVo.data.properTitle;
                            book.mBook.mPrice = selfBorrowBookInfoVoBaseResultEntityVo.data.price;
                            book.mId = selfBorrowBookInfoVoBaseResultEntityVo.data.id;
                            book.mDeposit = selfBorrowBookInfoVoBaseResultEntityVo.data.deposit;
                            if (selfBorrowBookInfoVoBaseResultEntityVo.data.borrowDepositType == 1) {
                                book.mDepositType = DepositType.PLATFORM_DEPOSIT;
                            } else if (selfBorrowBookInfoVoBaseResultEntityVo.data.borrowDepositType == 2) {
                                book.mDepositType = DepositType.LIB_DEPOSIT;
                            } else if (selfBorrowBookInfoVoBaseResultEntityVo.data.borrowDepositType == 3) {
                                book.mDepositType = DepositType.PLATFORM_LIB_DEPOSIT;
                            }
                            return book;
                        } else {
                            if (selfBorrowBookInfoVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(selfBorrowBookInfoVoBaseResultEntityVo.data.errorCode,
                                    selfBorrowBookInfoVoBaseResultEntityVo.data.message);
                        }
                    }
                })
                .onErrorResumeNext(new HttpResultFunc<SelfHelpBookInfoBean>());
    }

    public Observable<List<LibraryBean>> getReturnLibList(long bookId, String lngLat) {
        return CloudLibraryApi.getInstance().getReturnLibList(bookId, lngLat)
                .map(new Func1<BaseResultEntityVo<CommonReturnBookLibListVo>, List<LibraryBean>>() {
                    @Override
                    public List<LibraryBean> call(BaseResultEntityVo<CommonReturnBookLibListVo> commonReturnBookLibListVoBaseResultEntityVo) {
                        if (commonReturnBookLibListVoBaseResultEntityVo.status == 200) {
                            if (commonReturnBookLibListVoBaseResultEntityVo.data != null
                                    && commonReturnBookLibListVoBaseResultEntityVo.data.list != null
                                    && commonReturnBookLibListVoBaseResultEntityVo.data.list.size() > 0) {
                                List<LibraryBean> libraryBeanList = new ArrayList<>();
                                for (CommonReturnBookLibListVo.CommonReturnLibListVo item : commonReturnBookLibListVoBaseResultEntityVo.data.list) {
                                    LibraryBean libraryBean = new LibraryBean();
                                    libraryBean.mLibrary.mId = item.libId;
                                    libraryBean.mLibrary.mAddress = item.address;
                                    libraryBean.mLibrary.mName = item.libName;
                                    libraryBean.mLibrary.mLngLat = item.lngLat;
                                    libraryBean.mLibrary.mCode = item.libCode;
                                    libraryBean.mLibrary.mLogo = ImageUrlUtils.getDownloadOriginalImagePath(item.logo);
                                    libraryBean.mLibrary.mBookCount = item.bookNum;
                                    libraryBean.mLibrary.mHeatCount = item.hotTip;
                                    libraryBean.mIsOpen = StringUtils.libraryIsOpen(item.lightTime, item.serviceTime);
                                    libraryBean.mDistance = item.distance;
                                    libraryBeanList.add(libraryBean);
                                }
                                return libraryBeanList;
                            } else {
                                return null;
                            }
                        } else {
                            if (commonReturnBookLibListVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(commonReturnBookLibListVoBaseResultEntityVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<List<LibraryBean>>());
    }

    public Observable<BaseResultEntityVo<SelfBorrowBookResultVo>> selfBorrowBook(JSONArray bookIds, String readerId, String stayHallCode) {
        return CloudLibraryApi.getInstance().selfBorrowBook(bookIds, readerId, stayHallCode);
    }

    public Observable<SelfHelpBookBean> getSelfHelpBookUserAndBookInfo(String barNumber, String readerId) {
        return CloudLibraryApi.getInstance().selfBorrowFirstScan(barNumber, readerId)
                .map(new Func1<BaseResultEntityVo<SelfBorrowScanFirstVo>, SelfHelpBookBean>() {
                    @Override
                    public SelfHelpBookBean call(BaseResultEntityVo<SelfBorrowScanFirstVo> selfBorrowScanFirstVoBaseResultEntityVo) {
                        if (selfBorrowScanFirstVoBaseResultEntityVo.status == 200) {
                            SelfHelpBookBean selfHelpBookBean = new SelfHelpBookBean();
                            SelfHelpReaderBean readerInfoBean = new SelfHelpReaderBean();
                            //获取罚金
                            readerInfoBean.mPenalty = selfBorrowScanFirstVoBaseResultEntityVo.data.readerInfo.penalty;
                            //可借数量
                            readerInfoBean.mCanBorrowBookSum = selfBorrowScanFirstVoBaseResultEntityVo.data.libraryInfo.borrowNum
                                    - selfBorrowScanFirstVoBaseResultEntityVo.data.readerInfo.bookSum;
                            //当前馆的押金模式 0非押金模式 1押金模式
                            readerInfoBean.mIsDepositType = selfBorrowScanFirstVoBaseResultEntityVo.data.libraryInfo.deposit == 1;

                            switch (selfBorrowScanFirstVoBaseResultEntityVo.data.readerInfo.accountPermission) {
                                case 1:
                                    readerInfoBean.mDepositType = DepositType.PLATFORM_DEPOSIT;
                                    break;
                                case 2:
                                    readerInfoBean.mDepositType = DepositType.PLATFORM_LIB_DEPOSIT;
                                    break;
                                case 3:
                                    readerInfoBean.mDepositType = DepositType.LIB_DEPOSIT;
                                    break;
                            }

                            if (selfBorrowScanFirstVoBaseResultEntityVo.data.readerInfo.depositPriority == 1) {
                                readerInfoBean.mDepositSequenceType = SelfHelpReaderBean.DepositSequenceType.PLATFORM_DEPOSIT;
                            } else {
                                readerInfoBean.mDepositSequenceType = SelfHelpReaderBean.DepositSequenceType.LIB_DEPOSIT;
                            }

                            readerInfoBean.mUsablePlatformDeposit = selfBorrowScanFirstVoBaseResultEntityVo.data.readerInfo.availableOnlineDeposit;
                            readerInfoBean.mUsableLibDeposit = selfBorrowScanFirstVoBaseResultEntityVo.data.readerInfo.availableOfflineDeposit;
                            readerInfoBean.mCurrentLibCode = selfBorrowScanFirstVoBaseResultEntityVo.data.libraryInfo.libCode;
                            readerInfoBean.mHandleDeposit = (selfBorrowScanFirstVoBaseResultEntityVo.data.bookInfo.executeDeposit == 1);

                            //图书信息
                            SelfHelpBookInfoBean book = new SelfHelpBookInfoBean();
                            book.mAttachPrice = selfBorrowScanFirstVoBaseResultEntityVo.data.bookInfo.attachPrice;
                            book.mBelongLibraryHallCode = selfBorrowScanFirstVoBaseResultEntityVo.data.bookInfo.belongLibraryHallCode;
                            book.mStayLibraryHallCode = selfBorrowScanFirstVoBaseResultEntityVo.data.bookInfo.stayLibraryHallCode;
                            book.mBook.mBarNumber = selfBorrowScanFirstVoBaseResultEntityVo.data.bookInfo.barNumber;
                            book.mBook.mName = selfBorrowScanFirstVoBaseResultEntityVo.data.bookInfo.properTitle;
                            book.mBook.mPrice = selfBorrowScanFirstVoBaseResultEntityVo.data.bookInfo.price;
                            book.mId = selfBorrowScanFirstVoBaseResultEntityVo.data.bookInfo.id;
                            book.mDeposit = selfBorrowScanFirstVoBaseResultEntityVo.data.bookInfo.deposit;
                            if (selfBorrowScanFirstVoBaseResultEntityVo.data.bookInfo.borrowDepositType == 1) {
                                book.mDepositType = DepositType.PLATFORM_DEPOSIT;
                            } else if (selfBorrowScanFirstVoBaseResultEntityVo.data.bookInfo.borrowDepositType == 2) {
                                book.mDepositType = DepositType.LIB_DEPOSIT;
                            } else if (selfBorrowScanFirstVoBaseResultEntityVo.data.bookInfo.borrowDepositType == 3) {
                                book.mDepositType = DepositType.PLATFORM_LIB_DEPOSIT;
                            }

                            selfHelpBookBean.mReaderInfo = readerInfoBean;
                            selfHelpBookBean.mBookInfo = book;
                            return selfHelpBookBean;
                        } else {
                            if (selfBorrowScanFirstVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(selfBorrowScanFirstVoBaseResultEntityVo.data.errorCode,
                                    selfBorrowScanFirstVoBaseResultEntityVo.data.message);
                        }
                    }
                })
                .onErrorResumeNext(new HttpResultFunc<SelfHelpBookBean>());

    }


    public Observable<PenaltyDealResultBean> dealUserPenalty(String readerId, String hallCode) {
        return CloudLibraryApi.getInstance().dealUserLibPenalty(readerId, hallCode)
                .map(new Func1<BaseResultEntityVo<PayPenaltyResultVo>, PenaltyDealResultBean>() {
                    @Override
                    public PenaltyDealResultBean call(BaseResultEntityVo<PayPenaltyResultVo> payPenaltyResultVoBaseResultEntityVo) {
                        if (payPenaltyResultVoBaseResultEntityVo.status == 200) {
                            PenaltyDealResultBean penaltyDealResultBean = new PenaltyDealResultBean();
                            penaltyDealResultBean.mFailPenalty = payPenaltyResultVoBaseResultEntityVo.data.failPenalty;
                            penaltyDealResultBean.mSucceedPenalty = payPenaltyResultVoBaseResultEntityVo.data.succeedPenalty;
                            return penaltyDealResultBean;
                        } else {
                            if (payPenaltyResultVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(payPenaltyResultVoBaseResultEntityVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<PenaltyDealResultBean>());
    }

    public Observable<Double> dealUserPenaltyAuto(String hallCode, String readerId) {
        return CloudLibraryApi.getInstance().dealUserLibPenalty(readerId, hallCode)
                .map(new Func1<BaseResultEntityVo<PayPenaltyResultVo>, Double>() {
                    @Override
                    public Double call(BaseResultEntityVo<PayPenaltyResultVo> payPenaltyResultVoBaseResultEntityVo) {
                        if (payPenaltyResultVoBaseResultEntityVo.status == 200) {
                            return payPenaltyResultVoBaseResultEntityVo.data.failPenalty;
                        } else {
                            if (payPenaltyResultVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(payPenaltyResultVoBaseResultEntityVo.data.errorCode,
                                    payPenaltyResultVoBaseResultEntityVo.data.message);
                        }
                    }
                })
                .onErrorResumeNext(new HttpResultFunc<Double>());
    }

    public Observable<Double> dealUserPenaltyAuto(String readerId) {
        return dealUserPenaltyAuto(null, readerId);
    }

    public Observable<PenaltyDealResultBean> dealUserPenalty(String readerId) {
        return dealUserPenalty(readerId, null);
    }

    public Observable<Boolean> recommendBookByIsbn(String idCard, String isbn, String libCode) {
        return CloudLibraryApi.getInstance().recommendBookByIsbn(idCard, isbn, libCode)
                .map(new Func1<BaseResultEntityVo<RecommendBookResultVo>, Boolean>() {
                    @Override
                    public Boolean call(BaseResultEntityVo<RecommendBookResultVo> recommendBookResultVoBaseResultEntityVo) {
                        if (recommendBookResultVoBaseResultEntityVo.status == 200) {
                            return true;
                        } else {
                            if (recommendBookResultVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(recommendBookResultVoBaseResultEntityVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    public Observable<List<LibraryBean>> getRecommendBooLibList(String idCard, String isbn, String lngLat) {
        return CloudLibraryApi.getInstance().getRecommendBooLibList(idCard, isbn, lngLat)
                .map(new Func1<BaseResultEntityVo<RecommendBookLibListVo>, List<LibraryBean>>() {
                    @Override
                    public List<LibraryBean> call(BaseResultEntityVo<RecommendBookLibListVo> recommendBookLibListVoBaseResultEntityVo) {
                        if (recommendBookLibListVoBaseResultEntityVo.status == 200) {
                            if (recommendBookLibListVoBaseResultEntityVo.data != null
                                    && recommendBookLibListVoBaseResultEntityVo.data.list != null
                                    && recommendBookLibListVoBaseResultEntityVo.data.list.size() > 0) {
                                List<LibraryBean> libraryBeanList = new ArrayList<>();
                                for (RecommendBookLibListVo.RecommendLibraryListVo item : recommendBookLibListVoBaseResultEntityVo.data.list) {
                                    LibraryBean bean = new LibraryBean();
                                    bean.mLibrary.mId = item.libId;
                                    bean.mLibrary.mName = item.libName;
                                    bean.mLibrary.mAddress = item.address;
                                    bean.mLibrary.mCode = item.libCode;
//                                        bean.mLibrary.mLighten = item.lighten;
                                    bean.mLibrary.mLogo = ImageUrlUtils.getDownloadOriginalImagePath(item.logo);
                                    bean.mLibrary.mBookCount = item.bookNum;
                                    bean.mLibrary.mHeatCount = item.hotTip;
                                    bean.mIsOpen = StringUtils.libraryIsOpen(item.lightTime, item.serviceTime);
                                    bean.mDistance = item.distance;
                                    bean.recommendExist = item.recommondExist;
                                    bean.bookExist = item.bookExist;
                                    libraryBeanList.add(bean);
                                }
                                return libraryBeanList;
                            } else {
                                return null;
                            }
                        } else {
                            if (recommendBookLibListVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(recommendBookLibListVoBaseResultEntityVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<List<LibraryBean>>());
    }

    public Observable<BaseResultEntityVo<List<ProvinceVo>>> getProvinceList() {
        return CloudLibraryApi.getInstance().getProvinceList();
    }

    public Observable<BaseResultEntityVo<List<CityVo>>> getCityList(String code) {
        return CloudLibraryApi.getInstance().getCityList(code);
    }

    public Observable<BaseResultEntityVo<List<DistrictVo>>> getDistrictList(String cityCode) {
        return CloudLibraryApi.getInstance().getDistrictList(cityCode);
    }

    public Observable<BaseResultEntityVo<LastDistrictVo>> getLastDistrictList(String locationCode) {
        return CloudLibraryApi.getInstance().getLastDistrictList(locationCode);
    }

    public Observable<BaseResultEntityVo<List<String>>> getLibraryLevel() {
        return CloudLibraryApi.getInstance().getLibraryLevel();
    }

    public Observable<BaseResultEntityVo<BaseDataResultVo>> reportEBookRead(String ebookId, String libCode) {
        String identity;
        if (UserRepository.getInstance().isLogin()) {
            identity = UserRepository.getInstance().getLoginUserIdCard();
        } else {
            identity = Installation.id(CloudLibraryApplication.getAppContext());
        }
        return CloudLibraryApi.getInstance().reportEBookRead(ebookId, libCode, identity);
    }

    public Observable<BaseResultEntityVo<List<SearchHotResultVo>>> getHotSearchList(String libCode, String type) {
        return CloudLibraryApi.getInstance().getHotSearchList(libCode, type);
    }

    public Observable<WXPayInfoBean> requestWXPayInfo(double payMoney, String userIP) {
        return CloudLibraryApi.getInstance().requestWXPayInfo(payMoney, userIP)
                .map(new Func1<BaseResultEntityVo<WXPayInfoVo>, WXPayInfoBean>() {
                    @Override
                    public WXPayInfoBean call(BaseResultEntityVo<WXPayInfoVo> wxPayInfoVoBaseResultEntityVo) {
                        if (wxPayInfoVoBaseResultEntityVo.status == 200) {
                            WXPayInfoBean wxPayInfoBean = new WXPayInfoBean();
                            wxPayInfoBean.mAppid = wxPayInfoVoBaseResultEntityVo.data.appid;
                            wxPayInfoBean.mPartnerid = wxPayInfoVoBaseResultEntityVo.data.partnerid;
                            wxPayInfoBean.mPrepayid = wxPayInfoVoBaseResultEntityVo.data.prepayid;
                            wxPayInfoBean.mPackageName = wxPayInfoVoBaseResultEntityVo.data.packageName;
                            wxPayInfoBean.mNoncestr = wxPayInfoVoBaseResultEntityVo.data.noncestr;
                            wxPayInfoBean.mTimestamp = wxPayInfoVoBaseResultEntityVo.data.timestamp;
                            wxPayInfoBean.mSign = wxPayInfoVoBaseResultEntityVo.data.sign;
                            wxPayInfoBean.mOrderNum = wxPayInfoVoBaseResultEntityVo.data.orderNum;
                            return wxPayInfoBean;
                        } else {
                            if (wxPayInfoVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            if (wxPayInfoVoBaseResultEntityVo.data.errorCode == 6112) {
                                throw new ServerException(wxPayInfoVoBaseResultEntityVo.data.errorCode,
                                        wxPayInfoVoBaseResultEntityVo.data.errorData);
                            } else {
                                throw new ServerException(wxPayInfoVoBaseResultEntityVo.data.errorCode, "");
                            }

                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<WXPayInfoBean>());
    }

    public Observable<Boolean> requestWXPayResult(String orderNum) {
        return CloudLibraryApi.getInstance().requestWXPayResult(orderNum)
                .map(new Func1<BaseResultEntityVo<PayResultVo>, Boolean>() {
                    @Override
                    public Boolean call(BaseResultEntityVo<PayResultVo> payResultVoBaseResultEntityVo) {
                        if (payResultVoBaseResultEntityVo.status == 200) {
                            return payResultVoBaseResultEntityVo.data.value;
                        } else {
                            if (payResultVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(payResultVoBaseResultEntityVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    public Observable<AliPayInfoBean> requestAliPayInfo(double payMoney, String userIP) {
        return CloudLibraryApi.getInstance().requestAliPayInfo(payMoney, userIP)
                .map(new Func1<BaseResultEntityVo<AliPayInfoVo>, AliPayInfoBean>() {
                    @Override
                    public AliPayInfoBean call(BaseResultEntityVo<AliPayInfoVo> aliPayInfoVoBaseResultEntityVo) {
                        if (aliPayInfoVoBaseResultEntityVo.status == 200) {
                            AliPayInfoBean aliPayInfoBean = new AliPayInfoBean();
                            aliPayInfoBean.mPayParam = aliPayInfoVoBaseResultEntityVo.data.payParam;
                            aliPayInfoBean.mOrderNum = aliPayInfoVoBaseResultEntityVo.data.orderNum;
                            return aliPayInfoBean;
                        } else {
                            if (aliPayInfoVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            if (aliPayInfoVoBaseResultEntityVo.data.errorCode == 6112) {
                                throw new ServerException(aliPayInfoVoBaseResultEntityVo.data.errorCode,
                                        aliPayInfoVoBaseResultEntityVo.data.errorData);
                            } else {
                                throw new ServerException(aliPayInfoVoBaseResultEntityVo.data.errorCode, "");
                            }
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<AliPayInfoBean>());
    }

    public Observable<Boolean> requestAliPayResult(String orderNum) {
        return CloudLibraryApi.getInstance().requestAliPayResult(orderNum)
                .map(new Func1<BaseResultEntityVo<PayResultVo>, Boolean>() {
                    @Override
                    public Boolean call(BaseResultEntityVo<PayResultVo> payResultVoBaseResultEntityVo) {
                        if (payResultVoBaseResultEntityVo.status == 200) {
                            return payResultVoBaseResultEntityVo.data.value;
                        } else {
                            if (payResultVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(payResultVoBaseResultEntityVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    public Observable<Boolean> requestWithdraw(double totalAmount, String userIp) {
        return CloudLibraryApi.getInstance().requestWithdraw(totalAmount, userIp)
                .map(new Func1<BaseResultEntityVo<WithdrawResultVo>, Boolean>() {
                    @Override
                    public Boolean call(BaseResultEntityVo<WithdrawResultVo> withdrawResultVoBaseResultEntityVo) {
                        if (withdrawResultVoBaseResultEntityVo.status == 200) {
                            return withdrawResultVoBaseResultEntityVo.data.isSuccess;
                        } else {
                            if (withdrawResultVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(withdrawResultVoBaseResultEntityVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    public Observable<Boolean> readMsg(String readerId) {
        return CloudLibraryApi.getInstance().readMsg(readerId)
                .map(new Func1<BaseResultEntityVo<BaseDataResultVo>, Boolean>() {
                    @Override
                    public Boolean call(BaseResultEntityVo<BaseDataResultVo> baseDataResultVoBaseResultEntityVo) {
                        if (baseDataResultVoBaseResultEntityVo.status == 200) {
                            UserRepository.getInstance().readNormalMsg();
                            return true;
                        } else {
                            if (baseDataResultVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(baseDataResultVoBaseResultEntityVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    public Observable<Boolean> updateUserFaceImage(String idCard, String faceImage) {
        return CloudLibraryApi.getInstance().updateUserFaceImage(idCard, faceImage)
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
                }).onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    public Observable<BaseResultEntityVo<EBookListVo>> getEBookRankingList(Map<String, String> map) {
        return CloudLibraryApi.getInstance().getEBookRankingList(map);
    }

    public Observable<BaseListResultData<OverdueMsgBean>> getOverdueMsg(String idCard, int pageNo, int pageCount) {
        return CloudLibraryApi.getInstance().getOverdueMsg(idCard, pageNo, pageCount)
                .map(new Func1<BaseResultEntityVo<OverdueMsgListVo>, BaseListResultData<OverdueMsgBean>>() {
                    @Override
                    public BaseListResultData<OverdueMsgBean> call(BaseResultEntityVo<OverdueMsgListVo> overdueMsgListVoBaseResultEntityVo) {
                        if (overdueMsgListVoBaseResultEntityVo.status == 200) {
                            if (overdueMsgListVoBaseResultEntityVo.data != null
                                    && overdueMsgListVoBaseResultEntityVo.data.resultList != null
                                    && overdueMsgListVoBaseResultEntityVo.data.resultList.size() > 0) {
                                List<OverdueMsgBean> overdueMsgList = new ArrayList<>();
                                for (OverdueMsgListVo.OverdueMsgVo item : overdueMsgListVoBaseResultEntityVo.data.resultList) {
                                    OverdueMsgBean info = new OverdueMsgBean();
                                    info.mId = item.id;
                                    info.mBorrowId = item.borrowerId;
                                    info.mMsgContent = item.message;
                                    info.mCreateTime = DateUtils.format(item.nowTime, item.createTime);
                                    info.mState = item.status;

                                    info.mIsHistory = item.borrowState != 5;
                                    overdueMsgList.add(info);
                                }
                                BaseListResultData<OverdueMsgBean> baseResultData = new BaseListResultData<>();
                                baseResultData.mTotalCount = overdueMsgListVoBaseResultEntityVo.data.totalCount;
                                baseResultData.mResultList = overdueMsgList;
                                return baseResultData;
                            } else {
                                return null;
                            }
                        } else {
                            throw new ServerException(overdueMsgListVoBaseResultEntityVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<BaseListResultData<OverdueMsgBean>>());
    }

    public Observable<BaseResultEntityVo<BaseDataResultVo>> readOverdueMsg(int id) {
        return CloudLibraryApi.getInstance().readOverdueMsg(id);
    }

    public Observable<Boolean> userCompensateBooks(String readerId, String idCard, long borrowerId, String idPassword) {
        return CloudLibraryApi.getInstance().userCompensateBooks(readerId, idCard, borrowerId, idPassword)
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
                }).onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    public Observable<Boolean> pubMsg(String contact, String content, String imagePath, String libCode, int readerId) {
        return CloudLibraryApi.getInstance().pubMsg(contact, content, imagePath, libCode, readerId)
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
                }).onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    public Observable<BaseResultEntityVo<MainBranchLibraryVo>> getAllLibrary(String libCode, String lngLat) {
        return CloudLibraryApi.getInstance().getAllLibrary(libCode, lngLat);
    }

    public Observable<BaseListResultData<ActionInfoBean>> getOurReadersList(int pageNum,
                                                                            int pageCount,
                                                                            String keyword,
                                                                            String adCode) {
        return CloudLibraryApi.getInstance().getOurReadersList(pageNum, pageCount, keyword, adCode)
                .map(new Func1<BaseResultEntityVo<ActionListVo>, BaseListResultData<ActionInfoBean>>() {
                    @Override
                    public BaseListResultData<ActionInfoBean> call(BaseResultEntityVo<ActionListVo> actionListVoBaseResultEntityVo) {
                        if (actionListVoBaseResultEntityVo.status == 200) {
                            return dealActionData(actionListVoBaseResultEntityVo);
                        } else {
                            throw new ServerException(actionListVoBaseResultEntityVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<BaseListResultData<ActionInfoBean>>());
    }

    public Observable<BaseListResultData<ActionInfoBean>> getAppliedActionList(String idCard, int pageNo, int pageCount) {
        return CloudLibraryApi.getInstance().getAppliedActionList(idCard, pageNo, pageCount)
                .map(new Func1<BaseResultEntityVo<ActionListVo>, BaseListResultData<ActionInfoBean>>() {
                    @Override
                    public BaseListResultData<ActionInfoBean> call(BaseResultEntityVo<ActionListVo> actionListVoBaseResultEntityVo) {
                        if (actionListVoBaseResultEntityVo.status == 200) {
                            return dealActionData(actionListVoBaseResultEntityVo);
                        } else {
                            if (actionListVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(actionListVoBaseResultEntityVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<BaseListResultData<ActionInfoBean>>());
    }

    public Observable<BaseListResultData<ActionInfoBean>> getLibActionList(String libCode, int pageNum, int pageCount, String keyword) {
        return CloudLibraryApi.getInstance().getLibActionList(libCode, pageNum, pageCount, keyword)
                .map(new Func1<BaseResultEntityVo<ActionListVo>, BaseListResultData<ActionInfoBean>>() {
                    @Override
                    public BaseListResultData<ActionInfoBean> call(BaseResultEntityVo<ActionListVo> actionListVoBaseResultEntityVo) {
                        if (actionListVoBaseResultEntityVo.status == 200) {
                            return dealActionData(actionListVoBaseResultEntityVo);
                        } else {
                            throw new ServerException(actionListVoBaseResultEntityVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<BaseListResultData<ActionInfoBean>>());
    }

    private BaseListResultData<ActionInfoBean> dealActionData(BaseResultEntityVo<ActionListVo> actionListVoBaseResultEntityVo){
        if (actionListVoBaseResultEntityVo.data != null
                && actionListVoBaseResultEntityVo.data.resultList != null
                && actionListVoBaseResultEntityVo.data.resultList.size() > 0) {
            List<ActionInfoBean> list = new ArrayList<>();
            for (ActionListItemVo actionListVo : actionListVoBaseResultEntityVo.data.resultList) {
                ActionInfoBean actionInfo = new ActionInfoBean();
                actionInfo.mId = actionListVo.id;
                actionInfo.mImage = ImageUrlUtils.getDownloadOriginalImagePath(actionListVo.image);
                actionInfo.mAddress = actionListVo.address;
                actionInfo.mStartDateTime = actionListVo.startDate;
                actionInfo.mEndDateTime = actionListVo.endDate;
                actionInfo.mSponsor = actionListVo.source;
                actionInfo.mTitle = actionListVo.title;
                actionInfo.mShareUrl = actionListVo.htmlUrl;
                actionInfo.mUrl = actionListVo.detailUrl;
                actionInfo.mSummary = actionListVo.summary;
                actionInfo.mIsJoin = actionListVo.isJoin == 1;

                list.add(actionInfo);
            }
            BaseListResultData<ActionInfoBean> baseResultData = new BaseListResultData<>();
            baseResultData.mTotalCount = actionListVoBaseResultEntityVo.data.totalCount;
            baseResultData.mResultList = list;
            return baseResultData;
        } else {
            return null;
        }
    }

    public Observable<SelfBookInfoBean> getSelfBuyBookInfo(String fullBarNumber, String idCard, String stayLibraryHallCode) {
        return CloudLibraryApi.getInstance().getSelfBuyBookInfo(fullBarNumber, idCard, stayLibraryHallCode)
                .map(new Func1<BaseResultEntityVo<SelfBuyBookVo>, SelfBookInfoBean>() {
                    @Override
                    public SelfBookInfoBean call(BaseResultEntityVo<SelfBuyBookVo> selfBuyBookVoBaseResultEntityVo) {
                        if (selfBuyBookVoBaseResultEntityVo.status == 200) {
                            SelfBookInfoBean book = new SelfBookInfoBean();
                            book.stayLibraryHallCode = selfBuyBookVoBaseResultEntityVo.data.stayLibraryHallCode;
                            book.belongLibraryHallCode = selfBuyBookVoBaseResultEntityVo.data.belongLibraryHallCode;
                            book.barNumber = selfBuyBookVoBaseResultEntityVo.data.barNumber;
                            book.properTitle = selfBuyBookVoBaseResultEntityVo.data.properTitle;
                            book.id = selfBuyBookVoBaseResultEntityVo.data.id;
                            book.price = selfBuyBookVoBaseResultEntityVo.data.price + selfBuyBookVoBaseResultEntityVo.data.attachPrice;
                            book.discountPrice = selfBuyBookVoBaseResultEntityVo.data.discountPrice;
                            return book;
                        } else {
                            if (selfBuyBookVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(selfBuyBookVoBaseResultEntityVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<SelfBookInfoBean>());
    }

    public Observable<BaseResultEntityVo<SelfBuyBookResultVo>> selfBuyBook(String readerId, JSONArray libraryBookIds, String totalPrice, String payPwd) {
        return CloudLibraryApi.getInstance().selfBuyBook(readerId, libraryBookIds, totalPrice, payPwd);
    }

    public Observable<BaseResultEntityVo<SelfBuyBookShelfVo>> getSelfBuyBookShelfList(String readerId, int pageNo, int pageCount) {
        return CloudLibraryApi.getInstance().getSelfBuyBookShelfList(readerId, pageNo, pageCount)
                .map(new Func1<BaseResultEntityVo<SelfBuyBookShelfVo>, BaseResultEntityVo<SelfBuyBookShelfVo>>() {
                    @Override
                    public BaseResultEntityVo<SelfBuyBookShelfVo> call(BaseResultEntityVo<SelfBuyBookShelfVo> selfBuyBookShelfVoBaseResultEntityVo) {
                        if (selfBuyBookShelfVoBaseResultEntityVo.status == 200) {
                            return selfBuyBookShelfVoBaseResultEntityVo;
                        } else {
                            if (selfBuyBookShelfVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(selfBuyBookShelfVoBaseResultEntityVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<BaseResultEntityVo<SelfBuyBookShelfVo>>());
    }

    public Observable<Boolean> praiseSelfBuyBook(long buyId, int operateType) {
        return CloudLibraryApi.getInstance().praiseSelfBuyBook(buyId, operateType)
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
                }).onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    public Observable<BoughtBookBean> getSelfBuyBookDetail(long buyBookId) {
        return CloudLibraryApi.getInstance().getSelfBuyBookDetail(buyBookId)
                .map(new Func1<BaseResultEntityVo<SelfBuyBookDetailVo>, BoughtBookBean>() {
                    @Override
                    public BoughtBookBean call(BaseResultEntityVo<SelfBuyBookDetailVo> selfBuyBookDetailVo) {
                        if (selfBuyBookDetailVo.status == 200) {
                            if (selfBuyBookDetailVo.data != null) {
                                BoughtBookBean bean = new BoughtBookBean();
                                bean.mBoughtId = selfBuyBookDetailVo.data.id;
                                bean.mBook.mCoverImg = ImageUrlUtils.getDownloadOriginalImagePath(selfBuyBookDetailVo.data.image);
                                bean.mAuthor.mName = selfBuyBookDetailVo.data.author;
                                bean.mBook.mName = selfBuyBookDetailVo.data.properTitle;

                                if (null != selfBuyBookDetailVo.data.publishDate && selfBuyBookDetailVo.data.publishDate.length() >= 4) {
                                    bean.mBook.mPublishDate = selfBuyBookDetailVo.data.publishDate.substring(0, 4);
                                } else {
                                    bean.mBook.mPublishDate = "暂无数据";
                                }
                                bean.mPress.mName = selfBuyBookDetailVo.data.press;
                                bean.mBook.mIsbn = selfBuyBookDetailVo.data.isbn;
                                bean.mCategory.mName = selfBuyBookDetailVo.data.categoryName;
                                bean.mLibrary.mName = selfBuyBookDetailVo.data.libName;
                                //是否点赞 点赞1 true  未点赞 0 null -false
                                bean.mIsPraised = selfBuyBookDetailVo.data.isPraise != 0;
                                bean.mBoughtTime = selfBuyBookDetailVo.data.buyTime;
                                bean.mBoughtPrice = selfBuyBookDetailVo.data.buyPrice;
                                bean.mBook.mFixedPrice = selfBuyBookDetailVo.data.fixedPrice;
                                bean.mBook.mBookId = selfBuyBookDetailVo.data.libBookId;

                                //读书笔记
                                if (null != selfBuyBookDetailVo.data.libraryBorrowerNotes
                                        && selfBuyBookDetailVo.data.libraryBorrowerNotes.size() > 0) {
                                    bean.mNoteList = new ArrayList<>();
                                    for (NoteListItemVo item : selfBuyBookDetailVo.data.libraryBorrowerNotes) {
                                        ReadNoteBean readNoteBean = new ReadNoteBean();
                                        Note note = new Note();
                                        note.mId = item.id;
                                        note.mContent = item.readingNote;
                                        note.mModifyDate = DateUtils.formatDate(item.noteDate);
                                        readNoteBean.mNote = note;
                                        bean.mNoteList.add(readNoteBean);
                                    }
                                }
                                return bean;
                            } else {
                                return null;
                            }
                        } else {
                            if (selfBuyBookDetailVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(selfBuyBookDetailVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<BoughtBookBean>());
    }

    public Observable<BaseResultEntityVo<HtmlUrlVo>> getBorrowingIntroduces(String libCode) {
        return CloudLibraryApi.getInstance().getBorrowingIntroduces(libCode);
    }

    /**
     * 先获取缓存数据，再展示远程数据
     *
     * @param locationCode 定位
     * @param lngLat       经纬度
     * @return 首页数据
     */
    public Observable<HomeDataBean> getHomeInfo(String locationCode, String lngLat) {
        return Observable.concat(Observable.create(new Observable.OnSubscribe<HomeDataBean>() {
            @Override
            public void call(Subscriber<? super HomeDataBean> subscriber) {
                String strJson = SharedPreferencesUtil.getInstance().getString("HOME_DATA_LIST_INFO");
                HomeDataBean homeDataBean = null;
                if (!TextUtils.isEmpty(strJson)) {
                    Gson gson = new Gson();
                    homeDataBean = gson.fromJson(strJson, new TypeToken<HomeDataBean>() {
                    }.getType());
                }
                if (homeDataBean != null) {
                    subscriber.onNext(homeDataBean);
                }
                subscriber.onCompleted();
            }
        }), CloudLibraryApi.getInstance().getHomeInfo(locationCode, lngLat)
                .map(new Func1<BaseResultEntityVo<HomeInfoVo>, HomeDataBean>() {
                    @Override
                    public HomeDataBean call(BaseResultEntityVo<HomeInfoVo> homeInfoVo) {
                        if (homeInfoVo.status == 200) {
                            if (homeInfoVo.data != null) {
                                HomeDataBean homeDataBean = new HomeDataBean();
                                //set library list
                                if (homeInfoVo.data.library != null && homeInfoVo.data.library.size() > 0) {
                                    List<LibraryBean> libraryBeanList = new ArrayList<>();
                                    for (LibraryVo library : homeInfoVo.data.library) {
                                        LibraryBean libraryBean = new LibraryBean();
                                        libraryBean.mLibrary.mName = library.libName;
                                        libraryBean.mLibrary.mCode = library.libCode;
                                        libraryBean.mLibrary.mAddress = library.address;
                                        libraryBean.mLibrary.mLogo = ImageUrlUtils.getDownloadOriginalImagePath(library.logo);
//                                        libraryBean.mLibrary.mLighten = library.lighten;
                                        libraryBean.mLibrary.mBookCount = library.bookNum;
                                        libraryBean.mLibrary.mHeatCount = library.hotTip;
                                        libraryBean.mLibrary.mLevelName = library.libLevel;
                                        libraryBean.mIsBookStore = library.libLevel != null && (library.libLevel.equals("书店") || library.libLevel.equals("共享书屋"));
                                        libraryBean.mIsOpen = StringUtils.libraryIsOpen(library.lightTime, library.serviceTime);
                                        libraryBean.mDistance = library.distance;
                                        libraryBeanList.add(libraryBean);
                                    }
                                    homeDataBean.libraryBeanList = libraryBeanList;
                                }
                                //set paper book list
                                if (homeInfoVo.data.book != null && homeInfoVo.data.book.size() > 0) {
                                    List<BookBean> bookBeanList = new ArrayList<>();
                                    for (PaperBookVo book : homeInfoVo.data.book) {
                                        BookBean bookBean = new BookBean();
                                        bookBean.mAuthor.mName = book.author;
                                        bookBean.mBook.mName = book.bookName;
                                        bookBean.mBook.mIsbn = book.isbn;
                                        bookBean.mBook.mCoverImg = ImageUrlUtils.getDownloadOriginalImagePath(book.image);
                                        bookBeanList.add(bookBean);
                                    }
                                    homeDataBean.bookBeanList = bookBeanList;
                                }
                                //set ebook list
                                if (homeInfoVo.data.ebook != null && homeInfoVo.data.ebook.size() > 0) {
                                    List<EBookBean> eBookBeanList = new ArrayList<>();
                                    for (EBookVo eBookVo : homeInfoVo.data.ebook) {
                                        EBookBean bookInfoBean = new EBookBean();
                                        bookInfoBean.mEBook.mId = eBookVo.id;
                                        bookInfoBean.mEBook.mName = eBookVo.bookName;
                                        bookInfoBean.mEBook.mSummary = HtmlFormatUtil.delHTMLTag(eBookVo.summary);
                                        bookInfoBean.mEBook.mCoverImg = ImageUrlUtils.getDownloadOriginalImagePath(eBookVo.image);
                                        bookInfoBean.mAuthor.mName = eBookVo.author;
                                        bookInfoBean.mReadCount = eBookVo.number;
                                        eBookBeanList.add(bookInfoBean);
                                    }
                                    homeDataBean.eBookBeanList = eBookBeanList;
                                }
                                //set video list
                                if (homeInfoVo.data.video != null && homeInfoVo.data.video.size() > 0) {
                                    List<VideoSetBean> videoSetBeanList = new ArrayList<>();
                                    for (VideoVo video : homeInfoVo.data.video) {
                                        VideoSetBean bean = new VideoSetBean();
                                        bean.setId(video.id);
                                        bean.setTitle(video.name);
                                        bean.setContent(video.content);
                                        bean.setCoverImg(video.image);
                                        bean.setWatchTimes(video.watchTotalNum);
                                        videoSetBeanList.add(bean);
                                    }
                                    homeDataBean.videoSetBeanList = videoSetBeanList;
                                }
                                //set activity list
                                if (homeInfoVo.data.activity != null && homeInfoVo.data.activity.size() > 0) {
                                    List<ActionInfoBean> activityBeanList = new ArrayList<>();
                                    for (ActivityVo activity : homeInfoVo.data.activity) {
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
                                    homeDataBean.activityBeanList = activityBeanList;
                                }
                                //set news list
                                if (homeInfoVo.data.news != null && homeInfoVo.data.news.size() > 0) {
                                    List<InformationBean> informationBeanList = new ArrayList<>();
                                    for (NewsVo news : homeInfoVo.data.news) {
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
                                    homeDataBean.informationBeanList = informationBeanList;
                                }

                                Gson gson = new Gson();
                                String homeDataStr = gson.toJson(homeDataBean);
                                SharedPreferencesUtil.getInstance().remove("HOME_DATA_LIST_INFO");
                                SharedPreferencesUtil.getInstance().putString("HOME_DATA_LIST_INFO", homeDataStr);

                                return homeDataBean;
                            } else {
                                return null;
                            }
                        } else {
                            throw new ServerException(homeInfoVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<HomeDataBean>()));
    }

    public Observable<BaseResultEntityVo> reportBookShare(String bookId, int type) {
        return CloudLibraryApi.getInstance().reportBookShare(bookId, type);
    }

    public Observable<BaseListResultData<CommentBean>> getMyMessageList(int pageNo, int pageCount, String readerId) {
        return CloudLibraryApi.getInstance().getMyMessageList(pageNo, pageCount, readerId)
                .map(new Func1<BaseResultEntityVo<MyMessageVo>, BaseListResultData<CommentBean>>() {
                    @Override
                    public BaseListResultData<CommentBean> call(BaseResultEntityVo<MyMessageVo> baseResultEntityVo) {
                        if (baseResultEntityVo.status == 200) {
                            if (baseResultEntityVo.data != null
                                    && baseResultEntityVo.data.resultList != null
                                    && baseResultEntityVo.data.resultList.size() > 0) {
                                List<CommentBean> beanList = new ArrayList<>();
                                for (MyMessageVo.MessageResultVo resultVo : baseResultEntityVo.data.resultList) {
                                    CommentBean commentBean = new CommentBean();
                                    //消息类型 1:读者回复评论 2:读者回复读者的回复 3:平台回复评论 4:平台回复读者的回复 5:评论点赞 6:回复点赞
                                    commentBean.mType = resultVo.type;
                                    commentBean.mId = resultVo.commentId;
                                    commentBean.mContent = resultVo.content;
                                    commentBean.mNewsId = resultVo.newsId;
                                    commentBean.mPublishTime = resultVo.createTime;
                                    commentBean.mIsMan = (resultVo.gender == 1);
                                    commentBean.mRepliedID = resultVo.replyId;
                                    commentBean.mCommentImage = ImageUrlUtils.getDownloadOriginalImagePath(resultVo.image);
                                    commentBean.mCommentName = !TextUtils.isEmpty(resultVo.nickName) ?
                                            resultVo.nickName :
                                            StringUtils.formatNickName(0, resultVo.type, resultVo.name, (resultVo.gender == 1));
//                                    messageBean.mKind = resultVo.kind;
                                    commentBean.mLibraryCode = resultVo.libraryCode;
                                    commentBean.mIsBookStore = resultVo.libraryLevelName != null && (resultVo.libraryLevelName.equals("书店") || resultVo.libraryLevelName.equals("共享书屋"));

                                    beanList.add(commentBean);
                                }
                                BaseListResultData<CommentBean> baseResultData = new BaseListResultData<>();
                                baseResultData.mTotalCount = baseResultEntityVo.data.totalCount;
                                baseResultData.mResultList = beanList;
                                return baseResultData;
                            } else {
                                return null;
                            }
                        } else {
                            if (baseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(baseResultEntityVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<BaseListResultData<CommentBean>>());
    }

    //===============================活动列表===================================
    private List<ActionInfoBean> mActionList;

    public void saveActionList(List<ActionInfoBean> list, boolean refresh) {
        if (mActionList == null) {
            mActionList = new ArrayList<>();
        }
        if (refresh) {
            mActionList.clear();
        }
        mActionList.addAll(list);
    }

    public List<ActionInfoBean> getActionList() {
        return mActionList;
    }

    public void removeActionList() {
        if (mActionList != null) {
            mActionList.clear();
            mActionList = null;
        }
    }

    //===============================预约列表Id===================================
//    private List<String> mReservationBookIdList;
//
//    public void saveReservationBookList(List<String> list) {
//        if (mReservationBookIdList == null) {
//            mReservationBookIdList = new ArrayList<>();
//        }
//        mReservationBookIdList.clear();
//        mReservationBookIdList.addAll(list);
//    }
//
//    public void addReservationBookId(String id) {
//        if (mReservationBookIdList == null) {
//            mReservationBookIdList = new ArrayList<>();
//        }
//        mReservationBookIdList.add(id);
//    }
//
//    public void delReservationBookId(String id) {
//        if (mReservationBookIdList != null) {
//            mReservationBookIdList.remove(id);
//        }
//    }
//
//    public List<String> getReservationBookList() {
//        return mReservationBookIdList;
//    }
//
//    private void removeReservationBookList() {
//        if (mReservationBookIdList != null) {
//            mReservationBookIdList.clear();
//            mReservationBookIdList = null;
//        }
//    }

    /**
     * 获取首页Banner数据，先返回缓存数据，再返回网络数据
     *
     * @param locationCode 定位码
     * @return Banner数据
     */
    public Observable<List<BannerInfo>> getMainBannerList(String locationCode) {
        return Observable.concat(Observable.create(new Observable.OnSubscribe<List<BannerInfo>>() {
                    @Override
                    public void call(Subscriber<? super List<BannerInfo>> subscriber) {
                        String strJson = SharedPreferencesUtil.getInstance().getString("BANNER_LIST");
                        if (!TextUtils.isEmpty(strJson)) {
                            Gson gson = new Gson();
                            List<BannerInfo> bannerInfoList = gson.fromJson(strJson, new TypeToken<List<BannerInfo>>() {
                            }.getType());
                            subscriber.onNext(bannerInfoList);
                        }
                        subscriber.onCompleted();
                    }
                }),
                CloudLibraryApi.getInstance().getNewList(locationCode).map(new Func1<BaseResultEntityVo<List<BannerNewListItemVo>>, List<BannerInfo>>() {
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
                                Gson gson = new Gson();
                                String strJson = gson.toJson(bannerInfoList);
                                SharedPreferencesUtil.getInstance().remove("BANNER_LIST");
                                SharedPreferencesUtil.getInstance().putString("BANNER_LIST", strJson);
                                return bannerInfoList;
                            } else {
                                return null;
                            }
                        } else {
                            return null;
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<List<BannerInfo>>()));
    }

    public void clearHomeData() {
        SharedPreferencesUtil.getInstance().remove("BANNER_LIST");
        SharedPreferencesUtil.getInstance().remove("HOME_DATA_LIST_INFO");
    }

    //===============================搜索关键词===================================

    private ArrayMap<Integer, String> mKeyWordList;

    public void putSearchValueBySearchType(int searchType, String searchValue) {
        if (null == mKeyWordList) {
            mKeyWordList = new ArrayMap<>();
        }
        mKeyWordList.put(searchType, searchValue);

    }

    public String getSearchValueBySearchType(int searchType) {
        if (null == mKeyWordList || mKeyWordList.size() == 0) {
            return "";
        }
        return mKeyWordList.get(searchType);
    }

    public void clearSearchValue() {
        if (null != mKeyWordList && mKeyWordList.size() > 0) {
            mKeyWordList.clear();
            mKeyWordList = null;
        }
    }

    /**
     * 保存历史记录标签
     *
     * @param searchType
     * @param content
     */
    public void saveHistoryTag(int searchType, String content) {
        SearchManager.saveHistoryTag(searchType, content);
    }

    /**
     * 删除历史记录标签
     *
     * @param searchType
     * @param position
     * @return
     */
    public String[] delHistoryTag(int searchType, int position) {
        return SearchManager.delHistoryTag(searchType, position);
    }

    /**
     * 删除所有历史记录标签
     *
     * @param searchType
     */
    public void delAllSearchTag(int searchType) {
        SearchManager.delAllSearchTag(searchType);
    }

    /**
     * 获取历史记录标签
     *
     * @param searchType
     * @return
     */
    public String[] getHistoryTagList(int searchType) {
        return SearchManager.getHistoryTagList(searchType);
    }

    /**
     * 判断可以进入搜索的名称
     * @param searchType
     * @param position
     * @return
     */
    public String clickToSearch(int searchType, int position) {
        return SearchManager.clickToSearch(searchType, position);
    }

    /**
     * 获取热门搜索列表
     * @param searchType
     * @return
     */
    public List<SearchHotBean> getHotSearchList(int searchType) {
        return SearchManager.getHotSearchList(searchType);
    }

    /**
     * 保存热门搜索列表
     * @param searchType
     * @param list
     */
    public void saveHotSearchList(int searchType, List<SearchHotBean> list) {
        SearchManager.saveHotSearchList(searchType,list);
    }


    //===============================启动广告页===================================

    public void getLauncherBanner(String locationCode) {
        CloudLibraryApi.getInstance().getLauncherBanner(locationCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResultEntityVo<BannerDataListVo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(BaseResultEntityVo<BannerDataListVo> bannerDataListVoBaseResultEntityVo) {
                        if (bannerDataListVoBaseResultEntityVo.status == 200
                                && bannerDataListVoBaseResultEntityVo.data.url != null
                                && bannerDataListVoBaseResultEntityVo.data.url.size() > 0) {
                            String url = bannerDataListVoBaseResultEntityVo.data.url.get(0);

                            String name = url.substring(url.lastIndexOf("/"), url.lastIndexOf(".")) + ".temp";

//                            File parentFile = new File(Utils.getContext().getExternalCacheDir() + "/img/");
                            File parentFile = new File(CloudLibraryApplication.getAppContext().getExternalCacheDir() + "/img/");
                            DownloadTask newTask = new DownloadTask.Builder(url, parentFile, DownloadTask.IMG)
                                    .setFilename(name)
                                    .setFromStart()
                                    .setIsCheck(false)
                                    .setMinIntervalMillisCallbackProcess(1000)
                                    .build();
                            newTask.execute(new DownloadListenerWrapper());
                        } else {
                            SharedPreferencesUtil.getInstance().putString("LAUNCH_IMG", "");
                        }
                    }
                });
    }

    private class DownloadListenerWrapper implements DownloadListener {

        @Override
        public void taskWait(DownloadTask task) {

        }

        @Override
        public void taskStart(@NonNull DownloadTask task) {

        }

        @Override
        public void connectTrialStart(@NonNull DownloadTask task) {

        }

        @Override
        public void connectTrialEnd(@NonNull DownloadTask task) {

        }

        @Override
        public void connectStart(@NonNull DownloadTask task) {

        }

        @Override
        public void connectEnd(@NonNull DownloadTask task) {

        }

        @Override
        public void fetchStart(@NonNull DownloadTask task) {

        }

        @Override
        public void fetchProgress(@NonNull DownloadTask task) {

        }

        @Override
        public void fetchEnd(@NonNull DownloadTask task) {

        }

        @Override
        public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause) {
            if (cause == EndCause.COMPLETED) {
                String path = task.getFile().getPath();

                renameFileName(path);
            }
        }
    }

    /**
     * 重命名文件名称
     *
     * @param oldFilePath 文件名称
     */
    private void renameFileName(final String oldFilePath) {
        Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                File oldFile = new File(oldFilePath);
                String newFilePath = oldFilePath.substring(0, oldFilePath.lastIndexOf(".")) + ".jpg";
                oldFile.renameTo(new File(newFilePath));

                SharedPreferencesUtil.getInstance().putString("LAUNCH_IMG", newFilePath);
            }
        }).subscribeOn(Schedulers.io())
                .subscribe();
    }

    private ReadNoteGroupBean mReadNoteGroupBean;

    public void saveNoteGroupBean(ReadNoteGroupBean bean) {
        mReadNoteGroupBean = new ReadNoteGroupBean();
        mReadNoteGroupBean.mBook = bean.mBook;
        mReadNoteGroupBean.mNoteList = new ArrayList<>();
        mReadNoteGroupBean.mNoteList.addAll(bean.mNoteList);
    }

    public void addNote(ReadNoteBean note) {
        if (mReadNoteGroupBean != null) {
            mReadNoteGroupBean.mNoteList.add(0, note);
        }
    }

    public void modifyNote(ReadNoteBean note) {
        if (mReadNoteGroupBean != null && mReadNoteGroupBean.mNoteList.contains(note)) {
            mReadNoteGroupBean.mNoteList.remove(note);
            mReadNoteGroupBean.mNoteList.add(note);
        }
    }

    public void delNote(ReadNoteBean note) {
        if (mReadNoteGroupBean != null && mReadNoteGroupBean.mNoteList.contains(note)) {
            mReadNoteGroupBean.mNoteList.remove(note);
        }
    }

    public ReadNoteGroupBean getReadNoteGroupBean() {
        return mReadNoteGroupBean;
    }

    public void delReadNoteGroupBean() {
        if (mReadNoteGroupBean != null) {
            mReadNoteGroupBean.mNoteList.clear();
            mReadNoteGroupBean = null;
        }
    }
}
