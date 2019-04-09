package com.tzpt.cloundlibrary.manager.ui.presenter;

import android.text.TextUtils;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseResponseCode;
import com.tzpt.cloundlibrary.manager.base.RxPresenter;
import com.tzpt.cloundlibrary.manager.bean.BookInfoBean;
import com.tzpt.cloundlibrary.manager.bean.FlowManageDetailBookInfoBean;
import com.tzpt.cloundlibrary.manager.bean.FlowManageListBean;
import com.tzpt.cloundlibrary.manager.bean.LibraryInfo;
import com.tzpt.cloundlibrary.manager.bean.OrderNumberInfo;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.FlowManageAddBookInfoVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.FlowManageAddNewBookInfoVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.FlowManageDeleteBookVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.FlowManageDeleteSingleVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.FlowManageOutReCallVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.FlowManageSendBookResultVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.InLibraryOperatorVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.OrderFromVo;
import com.tzpt.cloundlibrary.manager.ui.contract.FlowManagementOperationContract;
import com.tzpt.cloundlibrary.manager.utils.MoneyUtils;
import com.tzpt.cloundlibrary.manager.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 获取流出管理详情
 * Created by ZhiqiangJia on 2017-07-11.
 */
public class FlowManagementOperationPresenter extends RxPresenter<FlowManagementOperationContract.View> implements
        FlowManagementOperationContract.Presenter,
        BaseResponseCode {

    private boolean mMustBeRefresh = false;    //是否必须刷新其他内容
    private int mFromType = -1;
    private String mOutOperUserId = "";
    private boolean mDeleteSingleCountBook = false; //是否执行了清点删除操作

    @Override
    public boolean isRefresh() {
        return mMustBeRefresh;
    }

    @Override
    public void setFromType(int fromType) {
        this.mFromType = fromType;
        LibraryInfo libraryInfo = DataRepository.getInstance().getLibraryInfo();
        if (null != libraryInfo) {
            this.mOutOperUserId = libraryInfo.mOperaterId;
        }
    }


    /**
     * 设置流入馆对象
     *
     * @param bean
     */
    @Override
    public void setManageListBean(FlowManageListBean bean) {
        if (null != bean && null != mView) {
            DataRepository.getInstance().initFlowManageListBean(bean);
//            LibraryInfo library = DataRepository.getInstance().getLibraryInfo();
//            mView.setCodeEditTextHint("输入条码号如：" + library.mHallCode + "1");
            getInLibraryOperatorInfo(bean.id, bean.outState);
        }
    }

    //获取流入负责人信息
    private void getInLibraryOperatorInfo(String circulateId, int circulateStatus) {
        if (TextUtils.isEmpty(circulateId)) {
            return;
        }
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
                                    mView.setHeadInfo(libraryText, conPersonInfo);
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
     *
     * @param pageNumber
     */
    @Override
    public void getFlowManageDetail(final int pageNumber) {
        String circulateId = DataRepository.getInstance().getFlowManageInfo().id;
        if (null == circulateId || TextUtils.isEmpty(circulateId)) {
            return;
        }
        if (null != mView) {
            //获取详情为修改获取清单ID
            if (pageNumber == 1) {
                mView.showProgressLoading();
            }
            Subscription subscription = DataRepository.getInstance().getFlowManageSingDetail(pageNumber, 10, circulateId)
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
                                                bean.circulateMapId = flowBookInfo.circulateMapId;
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
                                            //如果是当前新增或者修改,需要操作内存中的图书列表
                                            if (mFromType == 0 || mFromType == 1 || mFromType == 2) {
                                                //如果pageNumber =1 则执行刷新
                                                if (pageNumber == 1) {
                                                    DataRepository.getInstance().refreshDetailBookList(flowManageDetailBookInfoList);
                                                } else {
                                                    DataRepository.getInstance().addDetailBookList(flowManageDetailBookInfoList);
                                                }
                                                getTotalInfo(DataRepository.getInstance().getDetailBookList());
                                            } else {
                                                //其他显示服务器数据合计信息
                                                double newPrice = StringUtils.StringFormatToDouble2bit(flowManageAddBookInfoVo.data.totalSumPrice);
                                                mView.setFlowDetailTotalSumInfo(flowManageAddBookInfoVo.data.totalCount, MoneyUtils.formatMoney(newPrice));
                                            }
                                            mView.setFlowDetailBookInfoList(flowManageDetailBookInfoList, flowManageAddBookInfoVo.data.totalCount, pageNumber == 1);
                                        } else {
                                            mView.setFlowDetailBookInfoListEmpty(pageNumber == 1, mDeleteSingleCountBook);
                                            if (pageNumber == 1) {//删除内存中所有的图书
                                                DataRepository.getInstance().delDetailBook();
                                                mView.setFlowDetailTotalSumInfo(0, MoneyUtils.formatMoney(0.00));
                                                if (mFromType == 0 || mFromType == 1 || mFromType == 2) {
                                                    mView.showTotalLayout(false, false);
                                                }
                                            }
                                        }
                                    } else {
                                        mView.setFlowDetailBookInfoListEmpty(pageNumber == 1, mDeleteSingleCountBook);
                                        if (pageNumber == 1) {//删除内存中所有的图书
                                            DataRepository.getInstance().delDetailBook();
                                            mView.setFlowDetailTotalSumInfo(0, MoneyUtils.formatMoney(0.00));
                                            if (mFromType == 0 || mFromType == 1 || mFromType == 2) {
                                                mView.showTotalLayout(false, false);
                                            }
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

    /**
     * 操作清单类型
     *
     * @param fromType 新增0，修改1，直接删单3，撤回4
     */
    @Override
    public void operationFlowManageSingle(int fromType) {
        String circulateId = DataRepository.getInstance().getFlowManageInfo().id;
        if (null == circulateId || TextUtils.isEmpty(circulateId)) {
            return;
        }
        if (null != mView) {
            switch (fromType) {
                case 0://新增0-发送
                    sendNewBookByCirculateId(circulateId);
                    break;
                case 1://修改1-发送
                    sendNewBookByCirculateId(circulateId);
                    break;
                case 3://直接删单3
                    directSingleByCirculateId(circulateId);
                    break;
                case 4://撤回4
                    mView.showReCallDialog(circulateId);
                    break;
            }
        }
    }

    /**
     * 新增流出发送清单
     *
     * @param circulateId 清单ID
     */
    private void sendNewBookByCirculateId(String circulateId) {
        if (TextUtils.isEmpty(mOutOperUserId)) {
            return;
        }
        mView.showProgressLoading();
        Subscription subscription = DataRepository.getInstance().sendFlowManageNewBookList(circulateId, mOutOperUserId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FlowManageSendBookResultVo>() {
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
                    public void onNext(FlowManageSendBookResultVo flowManageSendBookResultVo) {
                        if (null != mView) {
                            mView.dismissProgressLoading();
                            if (flowManageSendBookResultVo.status == CODE_SUCCESS) {
                                mMustBeRefresh = true;
                                mView.sendSuccess();
                            } else {
                                if (null != flowManageSendBookResultVo.data) {
                                    switch (flowManageSendBookResultVo.data.errorCode) {
                                        case ERROR_CODE_KICK_OUT:
                                            mView.noPermissionPrompt(R.string.kicked_offline);
                                            break;
                                        case ERROR_CODE_1006:
                                            mView.noPermissionPrompt(R.string.operate_timeout);
                                            break;
                                        case ERROR_CODE_2403://流通记录不存在
                                            mView.showMsgDialog(R.string.list_deleted);
                                            break;
                                        case ERROR_CODE_2408://不在同一个流通范围
                                            mView.showMsgDialog(R.string.circulation_without_authorization);
                                            break;
                                        default:
                                            mView.showMsgDialog(R.string.error_code_500);
                                            break;
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
     * 直接删单
     *
     * @param circulateId 清单ID
     */
    private void directSingleByCirculateId(String circulateId) {
        mView.showProgressLoading();
        Subscription subscription = DataRepository.getInstance().deleteFlowManageSingle(circulateId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FlowManageDeleteSingleVo>() {
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
                    public void onNext(FlowManageDeleteSingleVo flowManageDeleteSingleVo) {
                        if (null != mView) {
                            mView.dismissProgressLoading();
                            if (flowManageDeleteSingleVo.status == CODE_SUCCESS) {
                                mMustBeRefresh = true;
                                mView.directDeleteSingleSuccess();
                            } else {
                                if (null != flowManageDeleteSingleVo.data) {
                                    switch (flowManageDeleteSingleVo.data.errorCode) {
                                        case ERROR_CODE_KICK_OUT:
                                            mView.noPermissionPrompt(R.string.kicked_offline);
                                            break;
                                        case ERROR_CODE_1006:
                                            mView.noPermissionPrompt(R.string.operate_timeout);
                                            break;
                                        case ERROR_CODE_2403://流通记录不存在
                                            mView.showMsgDialog(R.string.list_deleted);
                                            break;
                                        case ERROR_CODE_2406:
                                            mView.showMsgDialog(R.string.delete_fail);
                                            break;
                                        default:
                                            mView.showMsgDialog(R.string.delete_fail);
                                            break;
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
     * 撤回清单
     */
    @Override
    public void reCallSingleByCirculateId() {
        String circulateId = DataRepository.getInstance().getFlowManageInfo().id;
        if (null == circulateId || TextUtils.isEmpty(circulateId)) {
            return;
        }
        if (TextUtils.isEmpty(mOutOperUserId)) {
            return;
        }
        mView.showProgressLoading();
        Subscription subscription = DataRepository.getInstance().withdrawThisSingle(circulateId, mOutOperUserId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FlowManageOutReCallVo>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.dismissProgressLoading();
                            mView.showMsgDialog(R.string.network_fault);
                        }
                    }

                    @Override
                    public void onNext(FlowManageOutReCallVo flowManageOutReCallVo) {
                        if (mView != null) {
                            mView.dismissProgressLoading();
                            if (flowManageOutReCallVo.status == CODE_SUCCESS) {
                                mMustBeRefresh = true;
                                mView.reCallSuccess();
                            } else {
                                if (null != flowManageOutReCallVo.data) {
                                    switch (flowManageOutReCallVo.data.errorCode) {
                                        case ERROR_CODE_KICK_OUT:
                                            mView.noPermissionPrompt(R.string.kicked_offline);
                                            break;
                                        case ERROR_CODE_1006:
                                            mView.noPermissionPrompt(R.string.operate_timeout);
                                            break;
                                        default:
                                            mView.showMsgDialog(R.string.failure);
                                            break;
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
     * 新增或修改部分-删除图书
     *
     * @param fromType
     * @param id       关联ID
     */
    @Override
    public void deleteFlowManageBookInfo(final int fromType, final String id) {
        String circulateId = DataRepository.getInstance().getFlowManageInfo().id;
        if (null == id || TextUtils.isEmpty(id) || TextUtils.isEmpty(circulateId)) {
            return;
        }
        if (null != mView) {
            mView.showProgressLoading();
            Subscription subscription = DataRepository.getInstance().deleteFlowManageBookInfo(circulateId, id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<FlowManageDeleteBookVo>() {
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
                        public void onNext(FlowManageDeleteBookVo flowManageDeleteBookVo) {
                            if (null != mView) {
                                mView.dismissProgressLoading();
                                if (flowManageDeleteBookVo.status == CODE_SUCCESS) {
                                    mMustBeRefresh = true;
                                    getFlowManageDetail(1);
                                } else {
                                    if (null != flowManageDeleteBookVo.data) {
                                        if (flowManageDeleteBookVo.data.errorCode == ERROR_CODE_KICK_OUT) {
                                            mView.noPermissionPrompt(R.string.kicked_offline);
                                        } else if (flowManageDeleteBookVo.data.errorCode == ERROR_CODE_1006) {
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

    /**
     * 获取条码号值，进行新增，修改，清单删除操作
     *
     * @param fromType 新增0，修改1，清点删单2
     */
    @Override
    public void operationFlowManageEditValueByFromType(String allBarNumber, int fromType) {
        if (TextUtils.isEmpty(allBarNumber)) {
            return;
        }
//        if (!StringUtils.isVerifycode(allBarNumber)) {
//            mView.showMsgDialog(R.string.bar_code_error);
//            return;
//        }
        switch (fromType) {
            case 0:
                flowManageAddNewBook(allBarNumber);
                break;
            case 1:
                updateFlowManageSingleBookList(allBarNumber);
                break;
            case 2:
                outDeleteFlowManageSingleCountBook(allBarNumber);
                break;
        }
    }


    /**
     * 流出新增
     *
     * @param allBarNumber 条形码
     */
    private void flowManageAddNewBook(final String allBarNumber) {
        final FlowManageListBean flowManageListBean = DataRepository.getInstance().getFlowManageInfo();
        if (flowManageListBean == null || TextUtils.isEmpty(flowManageListBean.inHallCode)) {
            return;
        }
//        final String belongLibraryHallCode = StringUtils.getLibCode(allBarNumber);
//        final String barNumber = StringUtils.getBarCode(allBarNumber);
        //判断是否有重复录入
//        FlowManageDetailBookInfoBean bean = new FlowManageDetailBookInfoBean();
//        BookInfoBean bookInfoBean = new BookInfoBean();
//        bookInfoBean.mBarNumber = barNumber;
//        bookInfoBean.mBelongLibraryHallCode = belongLibraryHallCode;
//        bean.mBookInfoBean = bookInfoBean;
//        List<FlowManageDetailBookInfoBean> list = DataRepository.getInstance().getDetailBookList();
//        if (list.contains(bean)) {
//            mView.showMsgDialog(R.string.duplicate_entry);
//            return;
//        }
        mView.showProgressLoading();
        final OrderNumberInfo orderNumberInfo = DataRepository.getInstance().getOrderNumberInfo();
        if (orderNumberInfo == null || TextUtils.isEmpty(orderNumberInfo.mCodeNumber)) {
            Subscription subscription = DataRepository.getInstance().getOrderFromNumber()
                    .flatMap(new Func1<OrderFromVo, Observable<FlowManageAddNewBookInfoVo>>() {
                        @Override
                        public Observable<FlowManageAddNewBookInfoVo> call(OrderFromVo orderFromVo) {
                            if (orderFromVo.status == CODE_SUCCESS) {
                                if (null != orderFromVo.data) {
                                    OrderNumberInfo orderNumber = new OrderNumberInfo();
                                    orderNumber.mCodeNumber = orderFromVo.data.value;
                                    DataRepository.getInstance().initOrderNumberInfo(orderNumber);
                                    return DataRepository.getInstance().getFlowManageAddNewBook(
                                            allBarNumber, flowManageListBean.id, flowManageListBean.inHallCode,
                                            orderFromVo.data.value, flowManageListBean.outHallCode, mOutOperUserId);
                                }
                            }
                            return null;
                        }
                    }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(addBookObserver);
            addSubscrebe(subscription);
        } else {
            Subscription subscription = DataRepository.getInstance().getFlowManageAddNewBook(
                    allBarNumber, flowManageListBean.id, flowManageListBean.inHallCode,
                    orderNumberInfo.mCodeNumber, flowManageListBean.outHallCode, mOutOperUserId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(addBookObserver);
            addSubscrebe(subscription);
        }
    }


    private Observer<FlowManageAddNewBookInfoVo> addBookObserver = new Observer<FlowManageAddNewBookInfoVo>() {

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
        public void onNext(FlowManageAddNewBookInfoVo flowManageAddNewBookInfoVo) {
            if (null != mView) {
                mView.dismissProgressLoading();
                if (flowManageAddNewBookInfoVo != null) {
                    if (flowManageAddNewBookInfoVo.status == CODE_SUCCESS) {
                        if (null != flowManageAddNewBookInfoVo.data) {
                            DataRepository.getInstance().setFlowManageID(flowManageAddNewBookInfoVo.data.value);
                        }
                        getFlowManageDetail(1);
                        mMustBeRefresh = true;
                    } else {
                        if (null != flowManageAddNewBookInfoVo.data) {
                            switch (flowManageAddNewBookInfoVo.data.errorCode) {
                                case ERROR_CODE_KICK_OUT:
                                    mView.noPermissionPrompt(R.string.kicked_offline);
                                    break;
                                case ERROR_CODE_1006:
                                    mView.noPermissionPrompt(R.string.operate_timeout);
                                    break;
                                case ERROR_CODE_2206:
                                case ERROR_CODE_2006://书籍无库房
                                    mView.showMsgDialog(R.string.network_fault);
                                    break;
                                case ERROR_CODE_2207:
                                    mView.showMsgDialog(R.string.bar_code_error);
                                    break;
                                case ERROR_CODE_2404://流通记录不是待发送状态,无法继续新增流通书籍
                                    mView.showMsgDialog(R.string.list_send);
                                    break;
                                case ERROR_CODE_2402://书籍在限制库或基藏库,无法流通
                                    mView.showMsgDialog(R.string.library_limit_book_circulation);
                                    break;
                                case ERROR_CODE_2208://书籍已在借
                                    mView.showMsgDialog(R.string.book_already_borrowed);
                                    break;
                                case ERROR_CODE_2209://书籍已盘亏
                                    mView.showMsgDialog(R.string.have_dish_deficient);
                                    break;
                                case ERROR_CODE_2211://书籍已丢失
                                    mView.showMsgDialog(R.string.have_lost);
                                    break;
                                case ERROR_CODE_2212://书籍已剔旧
                                    mView.showMsgDialog(R.string.have_stuck_between_old);
                                    break;
                                case ERROR_CODE_2214://书籍已被其他人预约
                                    mView.showMsgDialog(R.string.have_make_appointment);
                                    break;
                                case ERROR_CODE_2403://流通记录不存在
                                    mView.showMsgDialog(R.string.list_deleted);
                                    break;
                                case ERROR_CODE_2210://已流出
                                    mView.showMsgDialog(R.string.has_been_out);
                                    break;
                                case ERROR_CODE_2400://新增流通书籍失败
                                    mView.showMsgDialog(R.string.network_fault);
                                    break;
                                case ERROR_CODE_2315://借书馆和还书馆协议不同
                                    mView.showMsgDialog(R.string.not_pass_also_pavilion_book);
                                    break;
                                default:
                                    mView.showMsgDialog(R.string.bar_code_error);
                                    break;
                            }
                        } else {
                            mView.showMsgDialog(R.string.error_code_500);
                        }
                    }
                } else {
                    mView.setNoFlowManageNewBookInfo();
                }
            }
        }
    };


    /**
     * 流出管理修改-添加新书
     *
     * @param allBarNumber 条形码
     */
    private void updateFlowManageSingleBookList(String allBarNumber) {
        final FlowManageListBean flowManageListBean = DataRepository.getInstance().getFlowManageInfo();
        if (flowManageListBean == null || TextUtils.isEmpty(flowManageListBean.inHallCode)) {
            return;
        }
//        final String belongLibraryHallCode = StringUtils.getLibCode(allBarNumber);
//        final String barNumber = StringUtils.getBarCode(allBarNumber);

        Subscription subscription = DataRepository.getInstance().getFlowManageAddNewBook(allBarNumber,
                flowManageListBean.id, flowManageListBean.inHallCode,
                "", flowManageListBean.outHallCode, mOutOperUserId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(addBookObserver);
        addSubscrebe(subscription);
    }


    /**
     * 清点删单
     *
     * @param allBarNumber 条形码
     */
    private void outDeleteFlowManageSingleCountBook(String allBarNumber) {
        final FlowManageListBean flowManageListBean = DataRepository.getInstance().getFlowManageInfo();
        if (flowManageListBean == null || TextUtils.isEmpty(flowManageListBean.inHallCode)) {
            return;
        }
//        final String belongLibraryHallCode = StringUtils.getLibCode(allBarNumber);
//        final String barNumber = StringUtils.getBarCode(allBarNumber);

        Subscription subscription = DataRepository.getInstance().outDeleteFlowManageSingleCountBook(flowManageListBean.id, allBarNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FlowManageDeleteSingleVo>() {
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
                    public void onNext(FlowManageDeleteSingleVo flowManageDeleteSingleVo) {
                        if (null != mView) {
                            mView.dismissProgressLoading();
                            if (flowManageDeleteSingleVo.status == CODE_SUCCESS) {
                                mMustBeRefresh = true;
                                mDeleteSingleCountBook = true;
                                getFlowManageDetail(1);
                            } else {
                                if (null != flowManageDeleteSingleVo.data) {
                                    switch (flowManageDeleteSingleVo.data.errorCode) {
                                        case ERROR_CODE_KICK_OUT:
                                            mView.noPermissionPrompt(R.string.kicked_offline);
                                            break;
                                        case ERROR_CODE_1006:
                                            mView.noPermissionPrompt(R.string.operate_timeout);
                                            break;
                                        case ERROR_CODE_2405:
                                        case ERROR_CODE_2410:
                                            mView.showMsgDialog(R.string.list_no_book);
                                            break;
                                        case ERROR_CODE_2217://书籍已在馆
                                            mView.showMsgDialog(R.string.book_already_in_library);
                                            break;
                                        default:
                                            mView.showMsgDialog(R.string.delete_fail);
                                            break;
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

    //设置合计信息
    private void getTotalInfo(List<FlowManageDetailBookInfoBean> list) {
        double totalPrice = 0.00;
        if (null != list && list.size() > 0) {
            for (FlowManageDetailBookInfoBean bean : list) {
                if (null != bean.mBookInfoBean) {
                    totalPrice += bean.mBookInfoBean.mPrice;
                }
            }
            mView.setFlowDetailTotalSumInfo(list.size(), MoneyUtils.formatMoney(totalPrice));
            mView.showTotalLayout(true, mFromType != 2);
        } else {
            mView.setFlowDetailTotalSumInfo(0, MoneyUtils.formatMoney(0.00));
            mView.showTotalLayout(false, false);
        }
    }

    @Override
    public void delOrderNumber() {
        DataRepository.getInstance().delOrderNumberInfo();
    }

    @Override
    public void delDetailBookList() {
        DataRepository.getInstance().delDetailBook();
    }

    @Override
    public void delFlowManageListBean() {
        DataRepository.getInstance().delFlowManageListBean();
    }

    @Override
    public void getBookList() {
        List<FlowManageDetailBookInfoBean> list = DataRepository.getInstance().getDetailBookList();
        if (list != null && list.size() > 0) {
            double totalPrice = 0.00;
            for (FlowManageDetailBookInfoBean bean : list) {
                if (null != bean.mBookInfoBean) {
                    totalPrice += bean.mBookInfoBean.mPrice;
                }
            }
            mView.showTotalLayout(true, mFromType != 2);//除了清点删除，都有按钮
            mView.setFlowDetailTotalSumInfo(list.size(), MoneyUtils.formatMoney(totalPrice));
            mView.refreshBookList(list);
            mMustBeRefresh = true;
        } else {
            mView.showTotalLayout(true, mFromType != 2);//除了清点删除，都有按钮
            mView.setFlowDetailTotalSumInfo(0, "0.00");
            mMustBeRefresh = true;
            mView.setFlowDetailBookInfoListEmpty(true, mFromType == 2);
        }
    }
}
