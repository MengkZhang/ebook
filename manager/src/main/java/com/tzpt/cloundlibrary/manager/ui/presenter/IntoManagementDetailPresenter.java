package com.tzpt.cloundlibrary.manager.ui.presenter;

import android.text.TextUtils;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseResponseCode;
import com.tzpt.cloundlibrary.manager.base.RxPresenter;
import com.tzpt.cloundlibrary.manager.bean.BookInfoBean;
import com.tzpt.cloundlibrary.manager.bean.FlowManageDetailBookInfoBean;
import com.tzpt.cloundlibrary.manager.bean.LibraryInfo;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.FlowManageAddBookInfoVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.IntoManageSignThisSingleVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.OutLibraryOperatorVo;
import com.tzpt.cloundlibrary.manager.ui.contract.IntoManagementDetailContract;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 获取流入列表详情
 * Created by ZhiqiangJia on 2017-07-14.
 */
public class IntoManagementDetailPresenter extends RxPresenter<IntoManagementDetailContract.View> implements
        IntoManagementDetailContract.Presenter,
        BaseResponseCode {

    private String mCirculateId;

    @Override
    public void setIntoManagementListBeanId(String id) {
        this.mCirculateId = id;
        getOurLibraryOperatorInfo(mCirculateId);
    }

    private void getOurLibraryOperatorInfo(String circulateId) {
        if (TextUtils.isEmpty(circulateId)) {
            return;
        }
        Subscription subscription = DataRepository.getInstance().getOutLibraryOperatorInfo(circulateId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<OutLibraryOperatorVo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.showMsgDialog(R.string.network_fault);
                        }
                    }

                    @Override
                    public void onNext(OutLibraryOperatorVo inLibraryOperatorVo) {
                        if (null != mView) {
                            if (inLibraryOperatorVo.status == CODE_SUCCESS) {
                                if (null != inLibraryOperatorVo.data) {
                                    String outHallCode = inLibraryOperatorVo.data.outHallCode;
                                    String libraryName = inLibraryOperatorVo.data.outLibraryName;
                                    String conPerson = inLibraryOperatorVo.data.outLibraryConperson;
                                    String phone = inLibraryOperatorVo.data.outLibraryPhone;
                                    //图书馆信息
                                    StringBuilder libraryInfo = new StringBuilder()
                                            .append(TextUtils.isEmpty(outHallCode) ? "" : outHallCode)
                                            .append(" ")
                                            .append(TextUtils.isEmpty(libraryName) ? "" : libraryName);
                                    //用户信息
                                    StringBuilder libraryUser = new StringBuilder();
                                    if (!TextUtils.isEmpty(conPerson)) {
                                        if (conPerson.length() > 3) {
                                            conPerson = conPerson.substring(0, 3) + "...";
                                        }
                                    } else {
                                        conPerson = "";
                                    }
                                    libraryUser.append(conPerson);
                                    libraryUser.append(" ").append(TextUtils.isEmpty(phone) ? "" : phone);
                                    mView.setHeadInfo(libraryInfo, libraryUser);
                                }
                            } else {
                                if (null != inLibraryOperatorVo.data) {
                                    if (inLibraryOperatorVo.data.errorCode == ERROR_CODE_KICK_OUT) {
                                        mView.noPermissionPrompt(R.string.kicked_offline);
                                    } else if (inLibraryOperatorVo.data.errorCode == ERROR_CODE_1006) {
                                        mView.noPermissionPrompt(R.string.operate_timeout);
                                    } else {
                                        mView.showMsgDialog(R.string.error_code_500);
                                    }
                                } else {
                                    mView.showMsgDialog(R.string.error_code_500);
                                }
                            }
                        }
                    }
                });
        addSubscrebe(subscription);

    }

    /**
     * 获取流入列表详情
     *
     * @param pageNumber 页码
     */
    @Override
    public void getIntoManageSingDetail(final int pageNumber) {
        if (TextUtils.isEmpty(mCirculateId)) {
            return;
        }
        if (null != mView) {
            if (pageNumber == 1) {
                mView.showProgressLoading();
            }
            Subscription subscription = DataRepository.getInstance().getFlowManageSingDetail(pageNumber, 10, mCirculateId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<FlowManageAddBookInfoVo>() {
                        @Override
                        public void onCompleted() {
                            if (null != mView) {
                                mView.complete();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (null != mView) {
                                mView.showMsgDialog(R.string.network_fault);
                                mView.dismissProgressLoading();
                            }
                        }

                        @Override
                        public void onNext(FlowManageAddBookInfoVo flowManageAddBookInfoVo) {
                            if (null != mView && null != flowManageAddBookInfoVo) {
                                mView.dismissProgressLoading();
                                if (flowManageAddBookInfoVo.status == CODE_SUCCESS) {
                                    if (null != flowManageAddBookInfoVo.data) {
                                        List<FlowManageAddBookInfoVo.FlowBookInfo> list = flowManageAddBookInfoVo.data.resultList;
                                        if (null != list && list.size() > 0) {
                                            List<BookInfoBean> bookInfoBeanList = new ArrayList<>();
                                            for (FlowManageAddBookInfoVo.FlowBookInfo flowBookInfo : list) {
                                                BookInfoBean bookInfoBean = new BookInfoBean();
                                                bookInfoBean.mBelongLibraryHallCode = TextUtils.isEmpty(flowBookInfo.belongLibraryHallCode)
                                                        ? "" : flowBookInfo.belongLibraryHallCode;
                                                bookInfoBean.mProperTitle = TextUtils.isEmpty(flowBookInfo.properTitle) ? "" : flowBookInfo.properTitle;
                                                bookInfoBean.mBarNumber = TextUtils.isEmpty(flowBookInfo.barNumber) ? "" : flowBookInfo.barNumber;
                                                bookInfoBean.mPrice = flowBookInfo.price + flowBookInfo.attachPrice;
                                                bookInfoBeanList.add(bookInfoBean);
                                            }
                                            mView.setIntoDetailTotalSumInfo(flowManageAddBookInfoVo.data.totalCount, flowManageAddBookInfoVo.data.totalSumPrice);
                                            mView.setIntoDetailBookInfoList(bookInfoBeanList, flowManageAddBookInfoVo.data.totalCount, pageNumber == 1);
                                        } else {
                                            mView.setIntoDetailBookInfoListEmpty(pageNumber == 1);
                                        }
                                    } else {
                                        mView.setIntoDetailBookInfoListEmpty(pageNumber == 1);
                                    }
                                } else {
                                    if (null != flowManageAddBookInfoVo.data) {
                                        if (flowManageAddBookInfoVo.data.errorCode == ERROR_CODE_KICK_OUT) {
                                            mView.noPermissionPrompt(R.string.kicked_offline);
                                        } else if (flowManageAddBookInfoVo.data.errorCode == ERROR_CODE_1006) {
                                            mView.noPermissionPrompt(R.string.operate_timeout);
                                        } else {
                                            mView.showMsgDialog(R.string.network_fault);
                                        }
                                    } else {
                                        mView.showMsgDialog(R.string.network_fault);
                                    }
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }

    /**
     * 0签收本单 1拒收本单
     *
     * @param operationType 0签收 1拒收
     */
    @Override
    public void operationThisSingle(int operationType) {
        if (operationType == 0) {
            signThisSingle();
        } else if (operationType == 1) {
            rejectionThisSingle();
        }
    }

    /**
     * 签收本单
     */
    private void signThisSingle() {
        LibraryInfo libraryInfo = DataRepository.getInstance().getLibraryInfo();
        if (null == libraryInfo) {
            return;
        }
        if (TextUtils.isEmpty(mCirculateId)) {
            return;
        }
        if (null != mView) {
            String signUserId = libraryInfo.mOperaterId;
            mView.showProgressLoading();
            Subscription subscription = DataRepository.getInstance().signThisSingle(mCirculateId, signUserId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<IntoManageSignThisSingleVo>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (null != mView) {
                                mView.dismissProgressLoading();
                                mView.showMsgDialog(R.string.network_fault);
                            }
                        }

                        @Override
                        public void onNext(IntoManageSignThisSingleVo intoManageSignThisSingleVo) {
                            if (null != mView) {
                                mView.dismissProgressLoading();
                                if (intoManageSignThisSingleVo.status == CODE_SUCCESS) {
                                    mView.signThisSingleSuccess();
                                } else {
                                    if (null != intoManageSignThisSingleVo.data) {
                                        if (intoManageSignThisSingleVo.data.errorCode == ERROR_CODE_KICK_OUT){
                                            mView.noPermissionPrompt(R.string.kicked_offline);
                                        }else if (intoManageSignThisSingleVo.data.errorCode == ERROR_CODE_1006){
                                            mView.noPermissionPrompt(R.string.operate_timeout);
                                        }else {
                                            mView.showMsgDialog(R.string.error_code_500);
                                        }
                                    } else {
                                        mView.showMsgDialog(R.string.error_code_500);
                                    }
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }

    /**
     * 拒收本单
     */
    private void rejectionThisSingle() {
        LibraryInfo libraryInfo = DataRepository.getInstance().getLibraryInfo();
        if (null == libraryInfo) {
            return;
        }
        if (TextUtils.isEmpty(mCirculateId)) {
            return;
        }
        if (null != mView) {
            mView.showProgressLoading();
            Subscription subscription = DataRepository.getInstance().rejectThisSingle(mCirculateId, libraryInfo.mOperaterId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<IntoManageSignThisSingleVo>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (null != mView) {
                                mView.dismissProgressLoading();
                                mView.showMsgDialog(R.string.network_fault);
                            }
                        }

                        @Override
                        public void onNext(IntoManageSignThisSingleVo intoManageSignThisSingleVo) {
                            if (null != mView) {
                                mView.dismissProgressLoading();
                                if (intoManageSignThisSingleVo.status == CODE_SUCCESS) {
                                    mView.rejectionThisSingleSuccess();
                                } else {
                                    if (null != intoManageSignThisSingleVo.data) {
                                        if (intoManageSignThisSingleVo.data.errorCode == ERROR_CODE_KICK_OUT) {
                                            mView.noPermissionPrompt(R.string.kicked_offline);
                                        } else if (intoManageSignThisSingleVo.data.errorCode == ERROR_CODE_1006) {
                                            mView.noPermissionPrompt(R.string.operate_timeout);
                                        } else {
                                            mView.showMsgDialog(R.string.error_code_500);
                                        }
                                    } else {
                                        mView.showMsgDialog(R.string.error_code_500);
                                    }
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }
}
