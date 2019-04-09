package com.tzpt.cloundlibrary.manager.ui.presenter;

import android.text.TextUtils;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseResponseCode;
import com.tzpt.cloundlibrary.manager.base.RxPresenter;
import com.tzpt.cloundlibrary.manager.bean.BookInfoBean;
import com.tzpt.cloundlibrary.manager.bean.FlowManageDetailBookInfoBean;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.FlowManageAddBookInfoVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.InLibraryOperatorVo;
import com.tzpt.cloundlibrary.manager.ui.contract.FlowManagementDetailContract;
import com.tzpt.cloundlibrary.manager.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 获取流出管理详情
 * Created by ZhiqiangJia on 2017-07-11.
 */
public class FlowManagementDetailPresenter extends RxPresenter<FlowManagementDetailContract.View> implements
        FlowManagementDetailContract.Presenter,
        BaseResponseCode {

    private String mCirculateId;

    @Override
    public void setFlowManageListBeanIdAndState(String id, int outState) {
        this.mCirculateId = id;
        getInLibraryOperatorInfo(id, outState);
    }

    //获取流入负责人信息
    private void getInLibraryOperatorInfo(String circulateId, int circulateStatus) {
        if (circulateStatus == -1) {//设置通还为已完成的状态
            circulateStatus = 6;
        }
        Subscription subscription = DataRepository.getInstance().getInLibraryOperatorInfo(circulateId, circulateStatus)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<InLibraryOperatorVo>() {
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
                    public void onNext(InLibraryOperatorVo inLibraryOperatorVo) {
                        if (null != mView) {
                            if (inLibraryOperatorVo.status == CODE_SUCCESS) {
                                if (null != inLibraryOperatorVo.data) {
                                    String inHallCode = inLibraryOperatorVo.data.inHallCode;
                                    String libraryName = inLibraryOperatorVo.data.inLibraryName;
                                    String conPerson = inLibraryOperatorVo.data.inLibraryConperson;
                                    String phone = inLibraryOperatorVo.data.inLibraryPhone;
                                    String auditContactName = TextUtils.isEmpty(inLibraryOperatorVo.data.auditContactName)
                                            ? "" : inLibraryOperatorVo.data.auditContactName;
                                    String auditDate = DateUtils.formatToDate(inLibraryOperatorVo.data.auditDate);
                                    //图书馆信息
                                    StringBuilder libraryText = new StringBuilder()
                                            .append(TextUtils.isEmpty(inHallCode) ? "" : inHallCode)
                                            .append(" ")
                                            .append(TextUtils.isEmpty(libraryName) ? "" : libraryName);
                                    //用户信息
                                    StringBuilder conPersonInfo = new StringBuilder();
                                    if (!TextUtils.isEmpty(conPerson)) {
                                        if (conPerson.length() > 3) {
                                            conPerson = conPerson.substring(0, 3) + "...";
                                        }
                                    } else {
                                        conPerson = "";
                                    }
                                    conPersonInfo.append(conPerson);
                                    conPersonInfo.append(" ").append(TextUtils.isEmpty(phone) ? "" : phone);
                                    mView.setHeadInfo(libraryText, conPersonInfo, auditContactName, auditDate);
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
     * 获取流出管理详情列表
     */
    @Override
    public void getFlowManageDetail(final int pageNumber) {
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
                                            List<FlowManageDetailBookInfoBean> flowManageDetailBookInfoList = new ArrayList<>();
                                            for (FlowManageAddBookInfoVo.FlowBookInfo flowBookInfo : list) {
                                                FlowManageDetailBookInfoBean bean = new FlowManageDetailBookInfoBean();
                                                bean.id = flowBookInfo.circulateId;
                                                BookInfoBean bookInfoBean = new BookInfoBean();
                                                bookInfoBean.mBelongLibraryHallCode = TextUtils.isEmpty(flowBookInfo.belongLibraryHallCode)
                                                        ? "" : flowBookInfo.belongLibraryHallCode;
                                                bookInfoBean.mProperTitle = TextUtils.isEmpty(flowBookInfo.properTitle) ? "" : flowBookInfo.properTitle;
                                                bookInfoBean.mBarNumber = TextUtils.isEmpty(flowBookInfo.barNumber) ? "" : flowBookInfo.barNumber;
                                                bookInfoBean.mPrice = flowBookInfo.price + flowBookInfo.attachPrice;
                                                bean.mBookInfoBean = bookInfoBean;
                                                flowManageDetailBookInfoList.add(bean);
                                            }
                                            if (flowManageDetailBookInfoList.size() > 0) {
                                                mView.setFlowDetailTotalSumInfo(flowManageAddBookInfoVo.data.totalCount, flowManageAddBookInfoVo.data.totalSumPrice);
                                                mView.setFlowDetailBookInfoList(flowManageDetailBookInfoList, flowManageAddBookInfoVo.data.totalCount, pageNumber == 1);
                                            } else {
                                                mView.setFlowDetailBookInfoListEmpty(pageNumber == 1);
                                            }
                                        } else {
                                            mView.setFlowDetailBookInfoListEmpty(pageNumber == 1);
                                        }
                                    }
                                } else {
                                    if (null != flowManageAddBookInfoVo.data) {
                                        if (flowManageAddBookInfoVo.data.errorCode == ERROR_CODE_KICK_OUT) {
                                            mView.noPermissionPrompt(R.string.kicked_offline);
                                        } else if (flowManageAddBookInfoVo.data.errorCode == ERROR_CODE_1006) {
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
