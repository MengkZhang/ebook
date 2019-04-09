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
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.FlowManageDeleteSingleVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.OrderFromVo;
import com.tzpt.cloundlibrary.manager.ui.contract.FlowManagementOperationsScanContract;
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
 * 流出管理扫描
 * Created by Administrator on 2017/7/15.
 */

public class FlowManagementOperationsScanPresenter extends RxPresenter<FlowManagementOperationsScanContract.View>
        implements FlowManagementOperationsScanContract.Presenter,
        BaseResponseCode {

    private String mOutOperUserId = "";

    @Override
    public void getHeadInfo() {
        FlowManageListBean flowManageListBean = DataRepository.getInstance().getFlowManageInfo();
        if (flowManageListBean != null) {
            String outHallCode = flowManageListBean.inHallCode;
            String libraryName = flowManageListBean.name;
            String conPerson = flowManageListBean.conperson;
            String phone = flowManageListBean.phone;
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
            mView.setHeaderLibraryInfo(libraryInfo);
            mView.setHeaderUserInfo(libraryUser);
        }
    }

    @Override
    public void getBookList() {
        List<FlowManageDetailBookInfoBean> list = DataRepository.getInstance().getDetailBookList();
        if (list != null && list.size() > 0) {
            getTotalInfo(list);
        }
    }


    @Override
    public void operationFlowManageEditValueByFromType(String allBarNumber, int fromType) {
        LibraryInfo libraryInfo = DataRepository.getInstance().getLibraryInfo();
        if (null != libraryInfo) {
            this.mOutOperUserId = libraryInfo.mOperaterId;
        }
        if (TextUtils.isEmpty(allBarNumber) || TextUtils.isEmpty(mOutOperUserId)) {
            return;
        }
//        if (!StringUtils.isVerifycode(allBarNumber)) {
//            mView.setScanTips(R.string.bar_code_error);
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

//        FlowManageDetailBookInfoBean bean = new FlowManageDetailBookInfoBean();
//        BookInfoBean bookInfoBean = new BookInfoBean();
//        bookInfoBean.mBarNumber = barNumber;
//        bookInfoBean.mBelongLibraryHallCode = belongLibraryHallCode;
//        bean.mBookInfoBean = bookInfoBean;
//        List<FlowManageDetailBookInfoBean> list = DataRepository.getInstance().getDetailBookList();
//        if (list.contains(bean)) {
//            mView.setScanTips(R.string.duplicate_entry);
//            return;
//        }
        final OrderNumberInfo orderNumberInfo = DataRepository.getInstance().getOrderNumberInfo();
        if (orderNumberInfo == null || TextUtils.isEmpty(orderNumberInfo.mCodeNumber)) {
            Subscription subscription = DataRepository.getInstance().getOrderFromNumber()
                    .flatMap(new Func1<OrderFromVo, Observable<FlowManageAddNewBookInfoVo>>() {
                        @Override
                        public Observable<FlowManageAddNewBookInfoVo> call(OrderFromVo orderFromVo) {
                            if (orderFromVo.status == CODE_SUCCESS) {
                                OrderNumberInfo orderNumber = new OrderNumberInfo();
                                orderNumber.mCodeNumber = orderFromVo.data.value;
                                DataRepository.getInstance().initOrderNumberInfo(orderNumber);
                                return DataRepository.getInstance().getFlowManageAddNewBook(allBarNumber,
                                        flowManageListBean.id, flowManageListBean.inHallCode,
                                        orderFromVo.data.value, flowManageListBean.outHallCode, mOutOperUserId);
                            }
                            return null;
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(addBookObserver);
            addSubscrebe(subscription);
        } else {
            Subscription subscription = DataRepository.getInstance().getFlowManageAddNewBook(allBarNumber,
                    flowManageListBean.id, flowManageListBean.inHallCode, orderNumberInfo.mCodeNumber, flowManageListBean.outHallCode, mOutOperUserId)
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
                mView.setScanTips(R.string.network_fault);
            }
        }

        @Override
        public void onNext(FlowManageAddNewBookInfoVo flowManageAddNewBookInfoVo) {
            if (null != mView) {
                if (flowManageAddNewBookInfoVo != null) {
                    if (flowManageAddNewBookInfoVo.status == CODE_SUCCESS) {
                        if (null != flowManageAddNewBookInfoVo.data) {
                            DataRepository.getInstance().setFlowManageID(flowManageAddNewBookInfoVo.data.value);
                        }
                        getFlowManageDetailBookList();
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
                                    mView.setScanTips(R.string.network_fault);
                                    break;
                                case ERROR_CODE_2207:
                                    mView.setScanTips(R.string.bar_code_error);
                                    break;
                                case ERROR_CODE_2208://书籍已在借
                                    mView.setScanTips(R.string.book_already_borrowed);
                                    break;
                                case ERROR_CODE_2209://书籍已盘亏
                                    mView.setScanTips(R.string.have_dish_deficient);
                                    break;
                                case ERROR_CODE_2211://书籍已丢失
                                    mView.setScanTips(R.string.have_lost);
                                    break;
                                case ERROR_CODE_2212://书籍已剔旧
                                    mView.setScanTips(R.string.have_stuck_between_old);
                                    break;
                                case ERROR_CODE_2214://书籍已被其他人预约
                                    mView.setScanTips(R.string.have_make_appointment);
                                    break;
                                case ERROR_CODE_2210://已流出
                                    mView.setScanTips(R.string.has_been_out);
                                    break;
                                case ERROR_CODE_2400://新增流通书籍失败
                                    mView.setScanTips(R.string.network_fault);
                                    break;
                                case ERROR_CODE_2402://书籍在限制库或基藏库,无法流通
                                    mView.setScanTips(R.string.library_limit_book_circulation);
                                    break;
                                default:
                                    mView.setScanTips(R.string.bar_code_error);
                                    break;
                            }
                        } else {
                            mView.setScanTips(R.string.error_code_500);
                        }
                    }
                } else {
                    mView.setScanTips(R.string.bar_code_error);
                }
            }
        }
    };

    private void getTotalInfo(List<FlowManageDetailBookInfoBean> list) {
        double totalPrice = 0.00;
        if (list != null && list.size() > 0) {
            for (FlowManageDetailBookInfoBean bean : list) {
                if (null != bean.mBookInfoBean) {
                    totalPrice += bean.mBookInfoBean.mPrice;
                }
            }
            mView.setBookNumber("数量" + list.size());
            mView.setBookPrice("金额" + MoneyUtils.formatMoney(totalPrice));
        } else {
            mView.setBookNumber("数量" + 0);
            mView.setBookPrice("金额" + MoneyUtils.formatMoney(totalPrice));
        }
    }

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
        //判断是否有重复录入
//        FlowManageDetailBookInfoBean bean = new FlowManageDetailBookInfoBean();
//        BookInfoBean bookInfoBean = new BookInfoBean();
//        bookInfoBean.mBarNumber = barNumber;
//        bookInfoBean.mBelongLibraryHallCode = belongLibraryHallCode;
//        bean.mBookInfoBean = bookInfoBean;
//        List<FlowManageDetailBookInfoBean> list = DataRepository.getInstance().getDetailBookList();
//        if (list.contains(bean)) {
//            mView.setScanTips(R.string.duplicate_entry);
//            return;
//        }
        Subscription subscription = DataRepository.getInstance().getFlowManageAddNewBook(allBarNumber,
                flowManageListBean.id, flowManageListBean.inHallCode,
                "", flowManageListBean.outHallCode, mOutOperUserId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(addBookObserver);
        addSubscrebe(subscription);
    }

    private void getFlowManageDetailBookList() {
        final FlowManageListBean flowManageListBean = DataRepository.getInstance().getFlowManageInfo();
        if (flowManageListBean == null || TextUtils.isEmpty(flowManageListBean.inHallCode)) {
            return;
        }

        Subscription subscription = DataRepository.getInstance().getFlowManageSingDetail(1, 10, flowManageListBean.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FlowManageAddBookInfoVo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.setScanTips(R.string.network_fault);
                        }
                    }

                    @Override
                    public void onNext(FlowManageAddBookInfoVo flowManageAddBookInfoVo) {
                        if (null != mView && null != flowManageAddBookInfoVo) {
                            if (flowManageAddBookInfoVo.status == CODE_SUCCESS) {
                                mView.setScanTips("");
                                if (null != flowManageAddBookInfoVo.data) {
                                    List<FlowManageAddBookInfoVo.FlowBookInfo> list = flowManageAddBookInfoVo.data.resultList;
                                    List<FlowManageDetailBookInfoBean> flowManageDetailBookInfoList = new ArrayList<>();
                                    flowManageDetailBookInfoList.clear();
                                    for (FlowManageAddBookInfoVo.FlowBookInfo flowBookInfo : list) {
                                        FlowManageDetailBookInfoBean bean = new FlowManageDetailBookInfoBean();
                                        bean.circulateMapId = flowBookInfo.circulateMapId;
                                        bean.id = flowBookInfo.circulateId;

                                        BookInfoBean bookInfoBean = new BookInfoBean();
                                        bookInfoBean.mBelongLibraryHallCode = TextUtils.isEmpty(flowBookInfo.belongLibraryHallCode) ? "" : flowBookInfo.belongLibraryHallCode;
                                        bookInfoBean.mProperTitle = TextUtils.isEmpty(flowBookInfo.properTitle) ? "" : flowBookInfo.properTitle;
                                        bookInfoBean.mBarNumber = TextUtils.isEmpty(flowBookInfo.barNumber) ? "" : flowBookInfo.barNumber;
                                        bookInfoBean.mPrice = flowBookInfo.price + flowBookInfo.attachPrice;
                                        bean.mBookInfoBean = bookInfoBean;
                                        flowManageDetailBookInfoList.add(bean);
                                    }
                                    DataRepository.getInstance().refreshDetailBookList(flowManageDetailBookInfoList);
                                    getTotalInfo(flowManageDetailBookInfoList);
                                }
                            } else {
                                if (null != flowManageAddBookInfoVo.data) {
                                    if (flowManageAddBookInfoVo.data.errorCode == ERROR_CODE_KICK_OUT) {
                                        mView.noPermissionPrompt(R.string.kicked_offline);
                                    } else if (flowManageAddBookInfoVo.data.errorCode == ERROR_CODE_1006) {
                                        mView.noPermissionPrompt(R.string.operate_timeout);
                                    } else {
                                        mView.setScanTips(R.string.error_code_500);
                                    }
                                } else {
                                    mView.setScanTips(R.string.error_code_500);
                                }
                            }
                        }
                    }
                });
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
                            mView.setScanTips(R.string.network_fault);
                        }
                    }

                    @Override
                    public void onNext(FlowManageDeleteSingleVo flowManageDeleteSingleVo) {
                        if (null != mView) {
                            if (flowManageDeleteSingleVo.status == CODE_SUCCESS) {
                                getFlowManageDetailBookList();
                                mView.setScanTips("");
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
                                            mView.setScanTips(R.string.list_no_book);
                                            break;
                                        case ERROR_CODE_2217://书籍已在馆
                                            mView.setScanTips(R.string.book_already_in_library);
                                            break;
                                        default:
                                            mView.setScanTips(R.string.delete_fail);
                                            break;
                                    }
                                } else {
                                    mView.setScanTips(R.string.error_code_500);
                                }
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }
}
