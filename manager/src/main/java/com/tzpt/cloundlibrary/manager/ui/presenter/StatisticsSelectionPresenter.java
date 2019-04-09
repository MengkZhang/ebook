package com.tzpt.cloundlibrary.manager.ui.presenter;

import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseResponseCode;
import com.tzpt.cloundlibrary.manager.base.RxPresenter;
import com.tzpt.cloundlibrary.manager.bean.SingleSelectionConditionBean;
import com.tzpt.cloundlibrary.manager.bean.StatisticsConditionBean;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.BranchLibVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.SingleSelectionConditionVo;
import com.tzpt.cloundlibrary.manager.ui.contract.StatisticsSelectionContract;
import com.tzpt.cloundlibrary.manager.utils.AllInputFilter;
import com.tzpt.cloundlibrary.manager.utils.CapitalLettersInputFilter;
import com.tzpt.cloundlibrary.manager.utils.CashierInputFilter;
import com.tzpt.cloundlibrary.manager.utils.IdCardInputFilter;
import com.tzpt.cloundlibrary.manager.utils.MoneyUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 统计分析选项
 * Created by ZhiqiangJia on 2017-07-14.
 */

public class StatisticsSelectionPresenter extends RxPresenter<StatisticsSelectionContract.View> implements
        StatisticsSelectionContract.Presenter,
        BaseResponseCode {

    private HashMap<String, List<SingleSelectionConditionBean>> mSingle = new HashMap<>();

    private String mKey;

    @Override
    public void clearTempData() {
        mSingle.clear();
    }

    @Override
    public void initOptionDataByFromType(final int fromType) {
        switch (fromType) {
            case 0://藏书统计
                collectBookStatistics();
                break;
            case 1://在借统计
                inBorrowStatistics();
                break;
            case 2://借书统计
                borrowStatistics();
                break;
            case 3://还书统计
                returnStatistics();
                break;
            case 4://赔书统计
                lostStatistics();
                break;
            case 5://销售统计
                sellStatistics();
                break;
            case 6://收款统计
                gatheringStatistics();
                break;
            case 7://读者统计
                queryPavilionLevel();
                break;
            case 8://免单记录
                chargeFreeStatistics();
                break;
            case 9://流出管理
                flowOutManagement();
                break;
            case 10://流入管理
                flowIntoManagement();
                break;
        }
    }

    /**
     * 获取藏书统计馆号
     */
    private void getHallCodeLibraryStatics() {
        mKey = "馆号";
        List<SingleSelectionConditionBean> hallCodeList = mSingle.get(mKey);
        if (hallCodeList != null && hallCodeList.size() > 0) {
            mView.setSingleSelectionCondition(hallCodeList);
            return;
        }

        String hallCode = DataRepository.getInstance().getLibraryInfo().mHallCode;
        if (null == hallCode) {
            return;
        }
        mView.showProgressLoading();
        Subscription subscription = DataRepository.getInstance().getHallCodeLibraryStatics(hallCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mStatisticsHallCodeObserver);
        addSubscrebe(subscription);
    }

    /**
     * 获取在借统计馆号
     */
    private void getHallCodeInBorrower() {
        mKey = "馆号";
        List<SingleSelectionConditionBean> hallCodeList = mSingle.get(mKey);
        if (hallCodeList != null && hallCodeList.size() > 0) {
            mView.setSingleSelectionCondition(hallCodeList);
            return;
        }

        String hallCode = DataRepository.getInstance().getLibraryInfo().mHallCode;
        if (null == hallCode) {
            return;
        }
        mView.showProgressLoading();
        Subscription subscription = DataRepository.getInstance().getHallCodeInBorrower(hallCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mStatisticsHallCodeObserver);
        addSubscrebe(subscription);
    }

    private void getHallCodeBookSells() {
        mKey = "馆号";
        List<SingleSelectionConditionBean> hallCodeList = mSingle.get(mKey);
        if (hallCodeList != null && hallCodeList.size() > 0) {
            mView.setSingleSelectionCondition(hallCodeList);
            return;
        }

        String hallCode = DataRepository.getInstance().getLibraryInfo().mHallCode;
        if (null == hallCode) {
            return;
        }
        mView.showProgressLoading();
        Subscription subscription = DataRepository.getInstance().getHallCodeBookSells(hallCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mStatisticsHallCodeObserver);
        addSubscrebe(subscription);
    }

    /**
     * 藏书统计书籍状态
     */
    private void getStatusLibraryStatics() {
        mKey = "状态";
        List<SingleSelectionConditionBean> hallCodeList = mSingle.get(mKey);
        if (hallCodeList != null && hallCodeList.size() > 0) {
            mView.setSingleSelectionCondition(hallCodeList);
            return;
        }

        mView.showProgressLoading();
        Subscription subscription = DataRepository.getInstance().getStatusLibraryStatics()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mStatisticsHallCodeObserver);
        addSubscrebe(subscription);
    }

    private void getStorerooms() {
        mKey = "库位";
        List<SingleSelectionConditionBean> hallCodeList = mSingle.get(mKey);
        if (hallCodeList != null && hallCodeList.size() > 0) {
            mView.setSingleSelectionCondition(hallCodeList);
            return;
        }

        mView.showProgressLoading();
        Subscription subscription = DataRepository.getInstance().getStorerooms()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mStatisticsHallCodeObserver);
        addSubscrebe(subscription);
    }

    private void getRfidLibraryStatics() {
        mKey = "RFID标签";
        List<SingleSelectionConditionBean> hallCodeList = mSingle.get(mKey);
        if (hallCodeList != null && hallCodeList.size() > 0) {
            mView.setSingleSelectionCondition(hallCodeList);
            return;
        }

        mView.showProgressLoading();
        Subscription subscription = DataRepository.getInstance().getRfidLibraryStatics()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mStatisticsHallCodeObserver);
        addSubscrebe(subscription);
    }

    private void getStatusInBorrower() {
        mKey = "状态";
        List<SingleSelectionConditionBean> hallCodeList = mSingle.get(mKey);
        if (hallCodeList != null && hallCodeList.size() > 0) {
            mView.setSingleSelectionCondition(hallCodeList);
            return;
        }

        mView.showProgressLoading();
        Subscription subscription = DataRepository.getInstance().getStatusInBorrower()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mStatisticsHallCodeObserver);
        addSubscrebe(subscription);
    }


    private void getHallCodeBorrowerBooks() {
        mKey = "馆号";
        List<SingleSelectionConditionBean> hallCodeList = mSingle.get(mKey);
        if (hallCodeList != null && hallCodeList.size() > 0) {
            mView.setSingleSelectionCondition(hallCodeList);
            return;
        }

        String hallCode = DataRepository.getInstance().getLibraryInfo().mHallCode;
        if (null == hallCode) {
            return;
        }
        mView.showProgressLoading();
        Subscription subscription = DataRepository.getInstance().getHallCodeBorrowerBooks(hallCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mStatisticsHallCodeObserver);
        addSubscrebe(subscription);
    }

    private void getUsers(String name, int valueEqualDesc) {
        mKey = "操作员";
        List<SingleSelectionConditionBean> hallCodeList = mSingle.get(mKey);
        if (hallCodeList != null && hallCodeList.size() > 0) {
            mView.setSingleSelectionCondition(hallCodeList);
            return;
        }

        String hallCode = DataRepository.getInstance().getLibraryInfo().mHallCode;
        if (null == hallCode) {
            return;
        }
        mView.showProgressLoading();
        Subscription subscription = DataRepository.getInstance().getUsers(hallCode, name, valueEqualDesc)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mStatisticsHallCodeObserver);
        addSubscrebe(subscription);
    }

    private void getUsersPenaltyFreeApply() {
        mKey = "操作员";
        List<SingleSelectionConditionBean> hallCodeList = mSingle.get(mKey);
        if (hallCodeList != null && hallCodeList.size() > 0) {
            mView.setSingleSelectionCondition(hallCodeList);
            return;
        }

        mView.showProgressLoading();
        Subscription subscription = DataRepository.getInstance().getUsersPenaltyFreeApply()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mStatisticsHallCodeObserver);
        addSubscrebe(subscription);
    }

    private void getHallCodeCompensateBooks() {
        mKey = "馆号";
        List<SingleSelectionConditionBean> hallCodeList = mSingle.get(mKey);
        if (hallCodeList != null && hallCodeList.size() > 0) {
            mView.setSingleSelectionCondition(hallCodeList);
            return;
        }

        String hallCode = DataRepository.getInstance().getLibraryInfo().mHallCode;
        if (null == hallCode) {
            return;
        }
        mView.showProgressLoading();
        Subscription subscription = DataRepository.getInstance().getHallCodeCompensateBooks(hallCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mStatisticsHallCodeObserver);
        addSubscrebe(subscription);
    }

    private void getOperationGatheringStatistics() {
        mKey = "项目";
        List<SingleSelectionConditionBean> hallCodeList = mSingle.get(mKey);
        if (hallCodeList != null && hallCodeList.size() > 0) {
            mView.setSingleSelectionCondition(hallCodeList);
            return;
        }

        mView.showProgressLoading();
        Subscription subscription = DataRepository.getInstance().getOperationGatheringStatistics()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mStatisticsHallCodeObserver);
        addSubscrebe(subscription);
    }

    private void getHallCodeReaderStatistics() {
        mKey = "馆号";
        List<SingleSelectionConditionBean> hallCodeList = mSingle.get(mKey);
        if (hallCodeList != null && hallCodeList.size() > 0) {
            mView.setSingleSelectionCondition(hallCodeList);
            return;
        }

        mView.showProgressLoading();
        Subscription subscription = DataRepository.getInstance().getHallCodeReaderStatistics()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mStatisticsHallCodeObserver);
        addSubscrebe(subscription);
    }

    private void getTypeReaderStatistics() {
        mKey = "读者类型";
        List<SingleSelectionConditionBean> hallCodeList = mSingle.get(mKey);
        if (hallCodeList != null && hallCodeList.size() > 0) {
            mView.setSingleSelectionCondition(hallCodeList);
            return;
        }

        mView.showProgressLoading();
        Subscription subscription = DataRepository.getInstance().getTypeReaderStatistics()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mStatisticsHallCodeObserver);
        addSubscrebe(subscription);
    }

    private void getStatusPenaltyFreeApplys() {
        mKey = "状态";
        List<SingleSelectionConditionBean> hallCodeList = mSingle.get(mKey);
        if (hallCodeList != null && hallCodeList.size() > 0) {
            mView.setSingleSelectionCondition(hallCodeList);
            return;
        }

        mView.showProgressLoading();
        Subscription subscription = DataRepository.getInstance().getStatusPenaltyFreeApplys()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mStatisticsHallCodeObserver);
        addSubscrebe(subscription);
    }

    private void getFlowManageStateList() {
        mKey = "状态";
        List<SingleSelectionConditionBean> hallCodeList = mSingle.get(mKey);
        if (hallCodeList != null && hallCodeList.size() > 0) {
            mView.setSingleSelectionCondition(hallCodeList);
            return;
        }

        mView.showProgressLoading();
        Subscription subscription = DataRepository.getInstance().getStatusOutCirculite()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mStatisticsHallCodeObserver);
        addSubscrebe(subscription);
    }

    private void getIntoManageStateList() {
        mKey = "状态";
        List<SingleSelectionConditionBean> hallCodeList = mSingle.get(mKey);
        if (hallCodeList != null && hallCodeList.size() > 0) {
            mView.setSingleSelectionCondition(hallCodeList);
            return;
        }

        mView.showProgressLoading();
        Subscription subscription = DataRepository.getInstance().getStatusInCirculite()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mStatisticsHallCodeObserver);
        addSubscrebe(subscription);
    }

    private Observer<SingleSelectionConditionVo> mStatisticsHallCodeObserver = new Observer<SingleSelectionConditionVo>() {
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
        public void onNext(SingleSelectionConditionVo singleSelectionConditionVo) {
            if (null != mView) {
                mView.dismissProgressLoading();
                if (singleSelectionConditionVo.status == CODE_SUCCESS) {
                    if (null != singleSelectionConditionVo.data) {
                        List<SingleSelectionConditionBean> singleSelectionConditionBeans = new ArrayList<>();
                        for (SingleSelectionConditionVo.Condition item : singleSelectionConditionVo.data.list) {
                            SingleSelectionConditionBean bean = new SingleSelectionConditionBean(item.name, item.value, item.desc);
                            singleSelectionConditionBeans.add(bean);
                        }
                        mSingle.put(mKey, singleSelectionConditionBeans);
                        mView.setSingleSelectionCondition(singleSelectionConditionBeans);
                    }
                } else {
                    if (null != singleSelectionConditionVo.data) {
                        if (singleSelectionConditionVo.data.errorCode == ERROR_CODE_KICK_OUT) {
                            mView.setNoLoginPermission(R.string.kicked_offline);
                        } else if (singleSelectionConditionVo.data.errorCode == ERROR_CODE_1006) {
                            mView.setNoLoginPermission(R.string.operate_timeout);
                        } else {
                            mView.showMsgDialog(R.string.error_code_500);
                        }
                    } else {
                        //服务器无响应
                        mView.showMsgDialog(R.string.error_code_500);
                    }
                }
            }
        }
    };

    private void collectBookStatistics() {
        List<StatisticsConditionBean> conditionList = new ArrayList<>();
        conditionList.add(new StatisticsConditionBean.Builder("馆号", StatisticsConditionBean.ConditionType.SingleSelection)
                .setSingleSelectionCondition(new StatisticsConditionBean.SingleSelectionCondition() {
                    @Override
                    public void getData() {
                        getHallCodeLibraryStatics();
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("条码号", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("startBarNumber")
                .setEndConditionKey("endBarNumber")
                .setKeyboardType(InputType.TYPE_CLASS_NUMBER)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(14)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        return Long.valueOf(arg0) <= Long.valueOf(arg1);
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("ISBN", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("startIsbn")
                .setEndConditionKey("endIsbn")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(20)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        return arg0.compareTo(arg1) <= 0;
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("书名", StatisticsConditionBean.ConditionType.SingleInput)
                .setSingleConditionKey("properTitle")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(200)})
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("出版社", StatisticsConditionBean.ConditionType.SingleInput)
                .setSingleConditionKey("press")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(200)})
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("定价", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("minPrice")
                .setEndConditionKey("maxPrice")
                .setKeyboardType(InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_SIGNED
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL)
                .setInputFilter(new InputFilter[]{new CashierInputFilter(false)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        double beginPrice = MoneyUtils.stringToDouble(arg0);
                        double endPrice = MoneyUtils.stringToDouble(arg1);
                        return beginPrice <= endPrice;
                    }
                })
                .setAutoCompletion(new StatisticsConditionBean.AutoCompletionResult() {
                    @Override
                    public void autoCompletion(String... arg) {
                        if (arg.length > 1) {
                            if (!TextUtils.isEmpty(arg[0])) {
                                arg[0] = MoneyUtils.formatMoney(arg[0]);
                                mView.setNewStartContent(arg[0]);
                            }
                            if (!TextUtils.isEmpty(arg[1])) {
                                arg[1] = MoneyUtils.formatMoney(arg[1]);
                                mView.setNewEndContent(arg[1]);
                            }
                        }
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("著者", StatisticsConditionBean.ConditionType.SingleInput)
                .setSingleConditionKey("author")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(200)})
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("索书号", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("startClassificationNumber")
                .setEndConditionKey("endClassificationNumber")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(20)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        return arg0.compareTo(arg1) <= 0;
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("排架号", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("startFrameCode")
                .setEndConditionKey("endFrameCode")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(20)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        return arg0.compareTo(arg1) <= 0;
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("年份", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("startPublishDate")
                .setEndConditionKey("endPublishDate")
                .setKeyboardType(InputType.TYPE_CLASS_NUMBER)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(4)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        return Integer.valueOf(arg0) <= Integer.valueOf(arg1);
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("状态", StatisticsConditionBean.ConditionType.SingleSelection)
                .setSingleConditionKey("bookState")
                .setSingleSelectionCondition(new StatisticsConditionBean.SingleSelectionCondition() {
                    @Override
                    public void getData() {
                        getStatusLibraryStatics();
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("库位", StatisticsConditionBean.ConditionType.SingleSelection)
                .setSingleSelectionCondition(new StatisticsConditionBean.SingleSelectionCondition() {
                    @Override
                    public void getData() {
                        getStorerooms();
                    }
                }).build());

        conditionList.add(new StatisticsConditionBean.Builder("RFID标签", StatisticsConditionBean.ConditionType.SingleSelection)
                .setSingleSelectionCondition(new StatisticsConditionBean.SingleSelectionCondition() {
                    @Override
                    public void getData() {
                        getRfidLibraryStatics();
                    }
                }).build());

        mView.setStatisticsConditionList(conditionList);
    }

    private void inBorrowStatistics() {
        List<StatisticsConditionBean> conditionList = new ArrayList<>();
        conditionList.add(new StatisticsConditionBean.Builder("馆号", StatisticsConditionBean.ConditionType.SingleSelection)
                .setSingleSelectionCondition(new StatisticsConditionBean.SingleSelectionCondition() {
                    @Override
                    public void getData() {
                        getHallCodeInBorrower();
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("借书日期", StatisticsConditionBean.ConditionType.DateSelection)
                .setStartConditionKey("startBorrowerDate")
                .setEndConditionKey("endBorrowerDate")
                .setKeyboardType(InputType.TYPE_CLASS_DATETIME)
                .build());

//                conditionList.add(new StatisticsConditionBean.Builder("单号", StatisticsConditionBean.ConditionType.DoubleInput)
//                        .setStartConditionKey("borrowNumberStart")
//                        .setEndConditionKey("borrowNumberEnd")
//                        .setKeyboardType(InputType.TYPE_CLASS_NUMBER)
//                        .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(8)})
//                        .setCompare(new StatisticsConditionBean.Compare() {
//                            @Override
//                            public boolean compare(String arg0, String arg1) {
//                                return Integer.valueOf(arg0) < Integer.valueOf(arg1);
//                            }
//                        })
//                        .build());

        conditionList.add(new StatisticsConditionBean.Builder("条码号", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("startBarNumber")
                .setEndConditionKey("endBarNumber")
                .setKeyboardType(InputType.TYPE_CLASS_NUMBER)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(14)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        return Long.valueOf(arg0) <= Long.valueOf(arg1);
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("ISBN", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("startIsbn")
                .setEndConditionKey("endIsbn")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(20)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        return arg0.compareTo(arg1) <= 0;
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("书名", StatisticsConditionBean.ConditionType.SingleInput)
                .setSingleConditionKey("properTitle")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(200)})
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("出版社", StatisticsConditionBean.ConditionType.SingleInput)
                .setSingleConditionKey("press")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(200)})
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("定价", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("minPrice")
                .setEndConditionKey("maxPrice")
                .setKeyboardType(InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_SIGNED
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL)
                .setInputFilter(new InputFilter[]{new CashierInputFilter(false)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        double beginPrice = MoneyUtils.stringToDouble(arg0);
                        double endPrice = MoneyUtils.stringToDouble(arg1);
                        return beginPrice <= endPrice;
                    }
                })
                .setAutoCompletion(new StatisticsConditionBean.AutoCompletionResult() {
                    @Override
                    public void autoCompletion(String... arg) {
                        if (arg.length > 1) {
                            if (!TextUtils.isEmpty(arg[0])) {
                                arg[0] = MoneyUtils.formatMoney(arg[0]);
                                mView.setNewStartContent(arg[0]);
                            }
                            if (!TextUtils.isEmpty(arg[1])) {
                                arg[1] = MoneyUtils.formatMoney(arg[1]);
                                mView.setNewEndContent(arg[1]);
                            }
                        }
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("读者信息", StatisticsConditionBean.ConditionType.SingleInput)
                .setSingleConditionKey("readerMessage")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(200)})
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("状态", StatisticsConditionBean.ConditionType.SingleSelection)
                .setSingleConditionKey("isOverdue")
                .setSingleSelectionCondition(new StatisticsConditionBean.SingleSelectionCondition() {
                    @Override
                    public void getData() {
                        getStatusInBorrower();
                    }
                })
                .build());

        mView.setStatisticsConditionList(conditionList);
    }

    private void borrowStatistics() {
        List<StatisticsConditionBean> conditionList = new ArrayList<>();
        conditionList.add(new StatisticsConditionBean.Builder("日期", StatisticsConditionBean.ConditionType.DateSelection)
                .setStartConditionKey("startBorrowTime")
                .setEndConditionKey("endBorrowTime")
                .setKeyboardType(InputType.TYPE_CLASS_DATETIME)
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("馆号", StatisticsConditionBean.ConditionType.SingleSelection)
                .setSingleSelectionCondition(new StatisticsConditionBean.SingleSelectionCondition() {
                    @Override
                    public void getData() {
                        getHallCodeBorrowerBooks();
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("单号", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("startBorrowNumber")
                .setEndConditionKey("endBorrowNumber")
                .setKeyboardType(InputType.TYPE_CLASS_NUMBER)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(8)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        return Integer.valueOf(arg0) <= Integer.valueOf(arg1);
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("条码号", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("startBarNumber")
                .setEndConditionKey("endBarNumber")
                .setKeyboardType(InputType.TYPE_CLASS_NUMBER)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(14)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        return Long.valueOf(arg0) <= Long.valueOf(arg1);
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("ISBN", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("startIsbn")
                .setEndConditionKey("endIsbn")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(20)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        return arg0.compareTo(arg1) <= 0;
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("书名", StatisticsConditionBean.ConditionType.SingleInput)
                .setSingleConditionKey("properTitle")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(200)})
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("出版社", StatisticsConditionBean.ConditionType.SingleInput)
                .setSingleConditionKey("press")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(200)})
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("定价", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("minPrice")
                .setEndConditionKey("maxPrice")
                .setKeyboardType(InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_SIGNED
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL)
                .setInputFilter(new InputFilter[]{new CashierInputFilter(false)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        double beginPrice = MoneyUtils.stringToDouble(arg0);
                        double endPrice = MoneyUtils.stringToDouble(arg1);
                        return beginPrice <= endPrice;
                    }
                })
                .setAutoCompletion(new StatisticsConditionBean.AutoCompletionResult() {
                    @Override
                    public void autoCompletion(String... arg) {
                        if (arg.length > 1) {
                            if (!TextUtils.isEmpty(arg[0])) {
                                arg[0] = MoneyUtils.formatMoney(arg[0]);
                                mView.setNewStartContent(arg[0]);
                            }
                            if (!TextUtils.isEmpty(arg[1])) {
                                arg[1] = MoneyUtils.formatMoney(arg[1]);
                                mView.setNewEndContent(arg[1]);
                            }
                        }
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("索书号", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("startClassificationNumber")
                .setEndConditionKey("endClassificationNumber")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(20)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        return arg0.compareTo(arg1) <= 0;
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("排架号", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("startFrameCode")
                .setEndConditionKey("endFrameCode")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(20)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        return arg0.compareTo(arg1) <= 0;
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("操作员", StatisticsConditionBean.ConditionType.SingleSelection)
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setSingleSelectionCondition(new StatisticsConditionBean.SingleSelectionCondition() {
                    @Override
                    public void getData() {
                        getUsers("operUser", 0);
                    }
                })
                .build());

        mView.setStatisticsConditionList(conditionList);
    }

    private void returnStatistics() {
        List<StatisticsConditionBean> conditionList = new ArrayList<>();
        conditionList.add(new StatisticsConditionBean.Builder("日期", StatisticsConditionBean.ConditionType.DateSelection)
                .setStartConditionKey("startReturnTime")
                .setEndConditionKey("endReturnTime")
                .setKeyboardType(InputType.TYPE_CLASS_DATETIME)
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("单号", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("startReturnNumber")
                .setEndConditionKey("endReturnNumber")
                .setKeyboardType(InputType.TYPE_CLASS_NUMBER)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(8)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        return Integer.valueOf(arg0) <= Integer.valueOf(arg1);
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("条码号", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("startBarNumber")
                .setEndConditionKey("endBarNumber")
                .setKeyboardType(InputType.TYPE_CLASS_NUMBER)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(14)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        return Long.valueOf(arg0) <= Long.valueOf(arg1);
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("ISBN", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("startIsbn")
                .setEndConditionKey("endIsbn")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(20)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        return arg0.compareTo(arg1) <= 0;
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("书名", StatisticsConditionBean.ConditionType.SingleInput)
                .setSingleConditionKey("properTitle")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(200)})
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("出版社", StatisticsConditionBean.ConditionType.SingleInput)
                .setSingleConditionKey("press")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(200)})
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("著者", StatisticsConditionBean.ConditionType.SingleInput)
                .setSingleConditionKey("author")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(200)})
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("定价", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("minPrice")
                .setEndConditionKey("maxPrice")
                .setKeyboardType(InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_SIGNED
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL)
                .setInputFilter(new InputFilter[]{new CashierInputFilter(false)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        double beginPrice = MoneyUtils.stringToDouble(arg0);
                        double endPrice = MoneyUtils.stringToDouble(arg1);
                        return beginPrice <= endPrice;
                    }
                })
                .setAutoCompletion(new StatisticsConditionBean.AutoCompletionResult() {
                    @Override
                    public void autoCompletion(String... arg) {
                        if (arg.length > 1) {
                            if (!TextUtils.isEmpty(arg[0])) {
                                arg[0] = MoneyUtils.formatMoney(arg[0]);
                                mView.setNewStartContent(arg[0]);
                            }
                            if (!TextUtils.isEmpty(arg[1])) {
                                arg[1] = MoneyUtils.formatMoney(arg[1]);
                                mView.setNewEndContent(arg[1]);
                            }
                        }
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("索书号", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("startClassificationNumber")
                .setEndConditionKey("endClassificationNumber")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(20)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        return arg0.compareTo(arg1) <= 0;
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("排架号", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("startFrameCode")
                .setEndConditionKey("endFrameCode")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(20)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        return arg0.compareTo(arg1) <= 0;
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("操作员", StatisticsConditionBean.ConditionType.SingleSelection)
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setSingleSelectionCondition(new StatisticsConditionBean.SingleSelectionCondition() {
                    @Override
                    public void getData() {
                        getUsers("operUser", 0);
                    }
                })
                .build());

        mView.setStatisticsConditionList(conditionList);
    }

    private void lostStatistics() {
        List<StatisticsConditionBean> conditionList = new ArrayList<>();
        conditionList.add(new StatisticsConditionBean.Builder("馆号", StatisticsConditionBean.ConditionType.SingleSelection)
                .setSingleSelectionCondition(new StatisticsConditionBean.SingleSelectionCondition() {
                    @Override
                    public void getData() {
                        getHallCodeCompensateBooks();
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("日期", StatisticsConditionBean.ConditionType.DateSelection)
                .setStartConditionKey("startCompensateDate")
                .setEndConditionKey("endCompensateDate")
                .setKeyboardType(InputType.TYPE_CLASS_DATETIME)
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("单号", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("startReturnCode")
                .setEndConditionKey("endReturnCode")
                .setKeyboardType(InputType.TYPE_CLASS_NUMBER)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(8)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        return Integer.valueOf(arg0) <= Integer.valueOf(arg1);
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("条码号", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("startBarNumber")
                .setEndConditionKey("endBarNumber")
                .setKeyboardType(InputType.TYPE_CLASS_NUMBER)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(14)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        return Long.valueOf(arg0) <= Long.valueOf(arg1);
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("ISBN", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("startIsbn")
                .setEndConditionKey("endIsbn")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(20)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        return arg0.compareTo(arg1) <= 0;
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("书名", StatisticsConditionBean.ConditionType.SingleInput)
                .setSingleConditionKey("properTitle")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(200)})
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("出版社", StatisticsConditionBean.ConditionType.SingleInput)
                .setSingleConditionKey("press")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(200)})
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("定价", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("minPrice")
                .setEndConditionKey("maxPrice")
                .setKeyboardType(InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_SIGNED
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL)
                .setInputFilter(new InputFilter[]{new CashierInputFilter(false)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        double beginPrice = MoneyUtils.stringToDouble(arg0);
                        double endPrice = MoneyUtils.stringToDouble(arg1);
                        return beginPrice <= endPrice;
                    }
                })
                .setAutoCompletion(new StatisticsConditionBean.AutoCompletionResult() {
                    @Override
                    public void autoCompletion(String... arg) {
                        if (arg.length > 1) {
                            if (!TextUtils.isEmpty(arg[0])) {
                                arg[0] = MoneyUtils.formatMoney(arg[0]);
                                mView.setNewStartContent(arg[0]);
                            }
                            if (!TextUtils.isEmpty(arg[1])) {
                                arg[1] = MoneyUtils.formatMoney(arg[1]);
                                mView.setNewEndContent(arg[1]);
                            }
                        }
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("金额", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("minMoneySum")
                .setEndConditionKey("maxMoneySum")
                .setKeyboardType(InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_SIGNED
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL)
                .setInputFilter(new InputFilter[]{new CashierInputFilter(false)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        double beginPrice = MoneyUtils.stringToDouble(arg0);
                        double endPrice = MoneyUtils.stringToDouble(arg1);
                        return beginPrice <= endPrice;
                    }
                })
                .setAutoCompletion(new StatisticsConditionBean.AutoCompletionResult() {
                    @Override
                    public void autoCompletion(String... arg) {
                        if (arg.length > 1) {
                            if (!TextUtils.isEmpty(arg[0])) {
                                arg[0] = MoneyUtils.formatMoney(arg[0]);
                                mView.setNewStartContent(arg[0]);
                            }
                            if (!TextUtils.isEmpty(arg[1])) {
                                arg[1] = MoneyUtils.formatMoney(arg[1]);
                                mView.setNewEndContent(arg[1]);
                            }
                        }
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("索书号", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("startClassificationNumber")
                .setEndConditionKey("endClassificationNumber")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(20)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        return arg0.compareTo(arg1) <= 0;
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("排架号", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("startFrameCode")
                .setEndConditionKey("endFrameCode")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(20)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        return arg0.compareTo(arg1) <= 0;
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("操作员", StatisticsConditionBean.ConditionType.SingleSelection)
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setSingleSelectionCondition(new StatisticsConditionBean.SingleSelectionCondition() {
                    @Override
                    public void getData() {
                        getUsers("operUser", 0);
                    }
                })
                .build());

        mView.setStatisticsConditionList(conditionList);
    }

    private void sellStatistics() {
        List<StatisticsConditionBean> conditionList = new ArrayList<>();
        conditionList.add(new StatisticsConditionBean.Builder("馆号", StatisticsConditionBean.ConditionType.SingleSelection)
                .setSingleSelectionCondition(new StatisticsConditionBean.SingleSelectionCondition() {
                    @Override
                    public void getData() {
                        getHallCodeBookSells();
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("日期", StatisticsConditionBean.ConditionType.DateSelection)
                .setStartConditionKey("startTime")
                .setEndConditionKey("endTime")
                .setKeyboardType(InputType.TYPE_CLASS_DATETIME)
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("条码号", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("startBarNumber")
                .setEndConditionKey("endBarNumber")
                .setKeyboardType(InputType.TYPE_CLASS_NUMBER)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(14)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        return Long.valueOf(arg0) <= Long.valueOf(arg1);
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("ISBN", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("startIsbn")
                .setEndConditionKey("endIsbn")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(20)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        return arg0.compareTo(arg1) <= 0;
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("书名", StatisticsConditionBean.ConditionType.SingleInput)
                .setSingleConditionKey("properTitle")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(200)})
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("定价", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("minPrice")
                .setEndConditionKey("maxPrice")
                .setKeyboardType(InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_SIGNED
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL)
                .setInputFilter(new InputFilter[]{new CashierInputFilter(false)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        double beginPrice = MoneyUtils.stringToDouble(arg0);
                        double endPrice = MoneyUtils.stringToDouble(arg1);
                        return beginPrice <= endPrice;
                    }
                })
                .setAutoCompletion(new StatisticsConditionBean.AutoCompletionResult() {
                    @Override
                    public void autoCompletion(String... arg) {
                        if (arg.length > 1) {
                            if (!TextUtils.isEmpty(arg[0])) {
                                arg[0] = MoneyUtils.formatMoney(arg[0]);
                                mView.setNewStartContent(arg[0]);
                            }
                            if (!TextUtils.isEmpty(arg[1])) {
                                arg[1] = MoneyUtils.formatMoney(arg[1]);
                                mView.setNewEndContent(arg[1]);
                            }
                        }
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("金额", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("minAmount")
                .setEndConditionKey("maxAmount")
                .setKeyboardType(InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_SIGNED
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL)
                .setInputFilter(new InputFilter[]{new CashierInputFilter(false)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        double beginPrice = MoneyUtils.stringToDouble(arg0);
                        double endPrice = MoneyUtils.stringToDouble(arg1);
                        return beginPrice <= endPrice;
                    }
                })
                .setAutoCompletion(new StatisticsConditionBean.AutoCompletionResult() {
                    @Override
                    public void autoCompletion(String... arg) {
                        if (arg.length > 1) {
                            if (!TextUtils.isEmpty(arg[0])) {
                                arg[0] = MoneyUtils.formatMoney(arg[0]);
                                mView.setNewStartContent(arg[0]);
                            }
                            if (!TextUtils.isEmpty(arg[1])) {
                                arg[1] = MoneyUtils.formatMoney(arg[1]);
                                mView.setNewEndContent(arg[1]);
                            }
                        }
                    }
                })
                .build());


        conditionList.add(new StatisticsConditionBean.Builder("索书号", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("startClassificationNumber")
                .setEndConditionKey("endClassificationNumber")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(20)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        return arg0.compareTo(arg1) <= 0;
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("排架号", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("startFrameCode")
                .setEndConditionKey("endFrameCode")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(20)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        return arg0.compareTo(arg1) <= 0;
                    }
                })
                .build());

        mView.setStatisticsConditionList(conditionList);
    }

    private void gatheringStatistics() {
        List<StatisticsConditionBean> conditionList = new ArrayList<>();
        conditionList.add(new StatisticsConditionBean.Builder("日期", StatisticsConditionBean.ConditionType.DateSelection)
                .setStartConditionKey("startOperDate")
                .setEndConditionKey("endOperDate")
                .setKeyboardType(InputType.TYPE_CLASS_DATETIME)
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("单号", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("startOperOrder")
                .setEndConditionKey("endOperOrder")
                .setKeyboardType(InputType.TYPE_CLASS_NUMBER)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(8)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        return Integer.valueOf(arg0) <= Integer.valueOf(arg1);
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("读者", StatisticsConditionBean.ConditionType.SingleInput)
                .setSingleConditionKey("cardName")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(100)})
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("身份证号", StatisticsConditionBean.ConditionType.SingleInput)
                .setSingleConditionKey("idcard")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new IdCardInputFilter(), new InputFilter.LengthFilter(18)})
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("在借数量", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("startBookSum")
                .setEndConditionKey("endBookSum")
                .setKeyboardType(InputType.TYPE_CLASS_NUMBER)
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        return Integer.valueOf(arg0) <= Integer.valueOf(arg1);
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("在借金额", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("startInBorrowerPrice")
                .setEndConditionKey("endInBorrowerPrice")
                .setKeyboardType(InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_SIGNED
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL)
                .setInputFilter(new InputFilter[]{new CashierInputFilter(false)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        double beginPrice = MoneyUtils.stringToDouble(arg0);
                        double endPrice = MoneyUtils.stringToDouble(arg1);
                        return beginPrice <= endPrice;
                    }
                })
                .setAutoCompletion(new StatisticsConditionBean.AutoCompletionResult() {
                    @Override
                    public void autoCompletion(String... arg) {
                        if (arg.length > 1) {
                            if (!TextUtils.isEmpty(arg[0])) {
                                arg[0] = MoneyUtils.formatMoney(arg[0]);
                                mView.setNewStartContent(arg[0]);
                            }
                            if (!TextUtils.isEmpty(arg[1])) {
                                arg[1] = MoneyUtils.formatMoney(arg[1]);
                                mView.setNewEndContent(arg[1]);
                            }
                        }
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("金额", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("startDeposit")
                .setEndConditionKey("endDeposit")
                .setKeyboardType(InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_SIGNED
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL)
                .setInputFilter(new InputFilter[]{new CashierInputFilter(true)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        double beginPrice = MoneyUtils.stringToDouble(arg0);
                        double endPrice = MoneyUtils.stringToDouble(arg1);
                        return beginPrice <= endPrice;
                    }
                })
                .setAutoCompletion(new StatisticsConditionBean.AutoCompletionResult() {
                    @Override
                    public void autoCompletion(String... arg) {
                        if (arg.length > 1) {
                            if (!TextUtils.isEmpty(arg[0])) {
                                arg[0] = MoneyUtils.formatMoney(arg[0]);
                                mView.setNewStartContent(arg[0]);
                            }
                            if (!TextUtils.isEmpty(arg[1])) {
                                arg[1] = MoneyUtils.formatMoney(arg[1]);
                                mView.setNewEndContent(arg[1]);
                            }
                        }
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("项目", StatisticsConditionBean.ConditionType.SingleSelection)
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setSingleSelectionCondition(new StatisticsConditionBean.SingleSelectionCondition() {
                    @Override
                    public void getData() {
                        getOperationGatheringStatistics();
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("操作员", StatisticsConditionBean.ConditionType.SingleSelection)
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setSingleSelectionCondition(new StatisticsConditionBean.SingleSelectionCondition() {
                    @Override
                    public void getData() {
                        getUsers("userId", 0);
                    }
                })
                .build());

        mView.setStatisticsConditionList(conditionList);
    }

    private void queryPavilionLevel() {
        String hallCode = DataRepository.getInstance().getLibraryInfo().mHallCode;
        if (null == hallCode) {
            return;
        }
        mView.showProgressLoading();
        Subscription subscription = DataRepository.getInstance().queryPavilionLevel(hallCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BranchLibVo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.dismissProgressLoading();
                            readerStatistics(false);
                        }
                    }

                    @Override
                    public void onNext(BranchLibVo branchLibVo) {
                        if (mView != null) {
                            mView.dismissProgressLoading();
                            if (branchLibVo.status == CODE_SUCCESS) {
                                if (branchLibVo.data.libList != null && branchLibVo.data.libList.size() > 0) {
                                    readerStatistics(true);
                                } else {
                                    readerStatistics(false);
                                }
                            } else {
                                if (null != branchLibVo.data) {
                                    if (branchLibVo.data.errorCode == ERROR_CODE_KICK_OUT) {
                                        mView.setNoLoginPermission(R.string.kicked_offline);
                                    } else if (branchLibVo.data.errorCode == ERROR_CODE_1006) {
                                        mView.setNoLoginPermission(R.string.operate_timeout);
                                    } else {
                                        readerStatistics(false);
                                    }
                                } else {
                                    readerStatistics(false);
                                }
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void readerStatistics(boolean hasHallCode) {
        List<StatisticsConditionBean> conditionList = new ArrayList<>();
        if (hasHallCode) {
            conditionList.add(new StatisticsConditionBean.Builder("馆号", StatisticsConditionBean.ConditionType.SingleSelection)
                    .setSingleSelectionCondition(new StatisticsConditionBean.SingleSelectionCondition() {
                        @Override
                        public void getData() {
                            getHallCodeReaderStatistics();
                        }
                    })
                    .build());
        }

        conditionList.add(new StatisticsConditionBean.Builder("读者类型", StatisticsConditionBean.ConditionType.SingleSelection)
                .setSingleSelectionCondition(new StatisticsConditionBean.SingleSelectionCondition() {
                    @Override
                    public void getData() {
                        getTypeReaderStatistics();
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("姓名", StatisticsConditionBean.ConditionType.SingleInput)
                .setSingleConditionKey("cardName")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(100)})
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("身份证号", StatisticsConditionBean.ConditionType.SingleInput)
                .setSingleConditionKey("idCard")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new IdCardInputFilter(), new InputFilter.LengthFilter(18)})
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("借阅证号", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("startBorrowCard")
                .setEndConditionKey("endBorrowCard")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_CLASS_NUMBER)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(18)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        return arg0.compareTo(arg1) <= 0;
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("建档日期", StatisticsConditionBean.ConditionType.DateSelection)
                .setStartConditionKey("startCreateTime")
                .setEndConditionKey("endCreateTime")
                .setKeyboardType(InputType.TYPE_CLASS_DATETIME)
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("借阅次数", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("startBorrowerCount")
                .setEndConditionKey("endBorrowerCount")
                .setKeyboardType(InputType.TYPE_CLASS_NUMBER)
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        return Integer.valueOf(arg0) <= Integer.valueOf(arg1);
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("馆押金", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("startDeposit")
                .setEndConditionKey("endDeposit")
                .setKeyboardType(InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_SIGNED
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL)
                .setInputFilter(new InputFilter[]{new CashierInputFilter(false)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        double beginPrice = MoneyUtils.stringToDouble(arg0);
                        double endPrice = MoneyUtils.stringToDouble(arg1);
                        return beginPrice <= endPrice;
                    }
                })
                .setAutoCompletion(new StatisticsConditionBean.AutoCompletionResult() {
                    @Override
                    public void autoCompletion(String... arg) {
                        if (arg.length > 1) {
                            if (!TextUtils.isEmpty(arg[0])) {
                                arg[0] = MoneyUtils.formatMoney(arg[0]);
                                mView.setNewStartContent(arg[0]);
                            }
                            if (!TextUtils.isEmpty(arg[1])) {
                                arg[1] = MoneyUtils.formatMoney(arg[1]);
                                mView.setNewEndContent(arg[1]);
                            }
                        }
                    }
                })
                .build());

        mView.setStatisticsConditionList(conditionList);
    }

    private void chargeFreeStatistics() {
        List<StatisticsConditionBean> conditionList = new ArrayList<>();
        conditionList.add(new StatisticsConditionBean.Builder("日期", StatisticsConditionBean.ConditionType.DateSelection)
                .setStartConditionKey("startApplyTime")
                .setEndConditionKey("endApplyTime")
                .setKeyboardType(InputType.TYPE_CLASS_DATETIME)
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("单号", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("startOperCode")
                .setEndConditionKey("endOperCode")
                .setKeyboardType(InputType.TYPE_CLASS_NUMBER)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(8)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        return Integer.valueOf(arg0) <= Integer.valueOf(arg1);
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("读者", StatisticsConditionBean.ConditionType.SingleInput)
                .setSingleConditionKey("readerName")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(100)})
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("身份证号", StatisticsConditionBean.ConditionType.SingleInput)
                .setSingleConditionKey("idCard")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new IdCardInputFilter(), new InputFilter.LengthFilter(18)})
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("金额", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("minAmount")
                .setEndConditionKey("maxAmount")
                .setKeyboardType(InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_SIGNED
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL)
                .setInputFilter(new InputFilter[]{new CashierInputFilter(false)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        double beginPrice = MoneyUtils.stringToDouble(arg0);
                        double endPrice = MoneyUtils.stringToDouble(arg1);
                        return beginPrice <= endPrice;
                    }
                })
                .setAutoCompletion(new StatisticsConditionBean.AutoCompletionResult() {
                    @Override
                    public void autoCompletion(String... arg) {
                        if (arg.length > 1) {
                            if (!TextUtils.isEmpty(arg[0])) {
                                arg[0] = MoneyUtils.formatMoney(arg[0]);
                                mView.setNewStartContent(arg[0]);
                            }
                            if (!TextUtils.isEmpty(arg[1])) {
                                arg[1] = MoneyUtils.formatMoney(arg[1]);
                                mView.setNewEndContent(arg[1]);
                            }
                        }
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("状态", StatisticsConditionBean.ConditionType.SingleSelection)
                .setSingleSelectionCondition(new StatisticsConditionBean.SingleSelectionCondition() {
                    @Override
                    public void getData() {
                        getStatusPenaltyFreeApplys();
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("操作员", StatisticsConditionBean.ConditionType.SingleSelection)
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setSingleSelectionCondition(new StatisticsConditionBean.SingleSelectionCondition() {
                    @Override
                    public void getData() {
                        getUsersPenaltyFreeApply();
                    }
                })
                .build());

        mView.setStatisticsConditionList(conditionList);
    }

    private void flowOutManagement() {
        List<StatisticsConditionBean> conditionList = new ArrayList<>();
        conditionList.add(new StatisticsConditionBean.Builder("日期", StatisticsConditionBean.ConditionType.DateSelection)
                .setStartConditionKey("outDateBegin")
                .setEndConditionKey("outDateEnd")
                .setKeyboardType(InputType.TYPE_CLASS_DATETIME)
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("单号", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("codeBegin")
                .setEndConditionKey("codeEnd")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new CapitalLettersInputFilter(true), new InputFilter.LengthFilter(10)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        return Integer.valueOf(arg0) < Integer.valueOf(arg1);
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("流向馆号", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("inHallCodeBegin")
                .setEndConditionKey("inHallCodeEnd")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new CapitalLettersInputFilter(true), new InputFilter.LengthFilter(8)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        return Integer.valueOf(arg0) < Integer.valueOf(arg1);
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("馆名", StatisticsConditionBean.ConditionType.SingleInput)
                .setSingleConditionKey("inLibraryName")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(20)})
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("操作员", StatisticsConditionBean.ConditionType.SingleSelection)
                .setSingleSelectionCondition(new StatisticsConditionBean.SingleSelectionCondition() {
                    @Override
                    public void getData() {
                        getUsers("operUserName", 1);
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("数量", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("bookNumBegin")
                .setEndConditionKey("bookNumEnd")
                .setKeyboardType(InputType.TYPE_CLASS_NUMBER)
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("码洋", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("minPrice")
                .setEndConditionKey("maxPrice")
                .setKeyboardType(InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_SIGNED
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL)
                .setInputFilter(new InputFilter[]{new CashierInputFilter(false), new InputFilter.LengthFilter(10)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        return arg0.compareTo(arg1) < 0;
                    }
                })
                .setAutoCompletion(new StatisticsConditionBean.AutoCompletionResult() {
                    @Override
                    public void autoCompletion(String... arg) {
                        if (arg.length > 1) {
                            if (!TextUtils.isEmpty(arg[0])) {
                                arg[0] = MoneyUtils.formatMoney(arg[0]);
                                mView.setNewStartContent(arg[0]);
                            }
                            if (!TextUtils.isEmpty(arg[1])) {
                                arg[1] = MoneyUtils.formatMoney(arg[1]);
                                mView.setNewEndContent(arg[1]);
                            }
                        }
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("状态", StatisticsConditionBean.ConditionType.SingleSelection)
                .setSingleSelectionCondition(new StatisticsConditionBean.SingleSelectionCondition() {
                    @Override
                    public void getData() {
                        getFlowManageStateList();
                    }
                })
                .build());

        mView.setStatisticsConditionList(conditionList);
    }

    private void flowIntoManagement() {
        List<StatisticsConditionBean> conditionList = new ArrayList<>();
        conditionList.add(new StatisticsConditionBean.Builder("日期", StatisticsConditionBean.ConditionType.DateSelection)
                .setStartConditionKey("outDateBegin")
                .setEndConditionKey("outDateEnd")
                .setKeyboardType(InputType.TYPE_CLASS_DATETIME)
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("单号", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("codeBegin")
                .setEndConditionKey("codeEnd")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new CapitalLettersInputFilter(true), new InputFilter.LengthFilter(10)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        return Integer.valueOf(arg0) < Integer.valueOf(arg1);
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("流出馆号", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("outHallCodeBegin")
                .setEndConditionKey("outHallCodeEnd")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new CapitalLettersInputFilter(true), new InputFilter.LengthFilter(8)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        return Integer.valueOf(arg0) < Integer.valueOf(arg1);
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("馆名", StatisticsConditionBean.ConditionType.SingleInput)
                .setSingleConditionKey("outLibraryName")
                .setKeyboardType(InputType.TYPE_CLASS_TEXT)
                .setInputFilter(new InputFilter[]{new AllInputFilter(), new InputFilter.LengthFilter(20)})
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("操作员", StatisticsConditionBean.ConditionType.SingleSelection)
                .setSingleSelectionCondition(new StatisticsConditionBean.SingleSelectionCondition() {
                    @Override
                    public void getData() {
                        getUsers("operUserName", 1);
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("数量", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("bookNumBegin")
                .setEndConditionKey("bookNumEnd")
                .setKeyboardType(InputType.TYPE_CLASS_NUMBER)
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("码洋", StatisticsConditionBean.ConditionType.DoubleInput)
                .setStartConditionKey("minPrice")
                .setEndConditionKey("maxPrice")
                .setKeyboardType(InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_SIGNED
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL)
                .setInputFilter(new InputFilter[]{new CashierInputFilter(false), new InputFilter.LengthFilter(10)})
                .setCompare(new StatisticsConditionBean.Compare() {
                    @Override
                    public boolean compare(String arg0, String arg1) {
                        return arg0.compareTo(arg1) < 0;
                    }
                })
                .setAutoCompletion(new StatisticsConditionBean.AutoCompletionResult() {
                    @Override
                    public void autoCompletion(String... arg) {
                        if (arg.length > 1) {
                            if (!TextUtils.isEmpty(arg[0])) {
                                arg[0] = MoneyUtils.formatMoney(arg[0]);
                                mView.setNewStartContent(arg[0]);
                            }
                            if (!TextUtils.isEmpty(arg[1])) {
                                arg[1] = MoneyUtils.formatMoney(arg[1]);
                                mView.setNewEndContent(arg[1]);
                            }
                        }
                    }
                })
                .build());

        conditionList.add(new StatisticsConditionBean.Builder("状态", StatisticsConditionBean.ConditionType.SingleSelection)
                .setSingleSelectionCondition(new StatisticsConditionBean.SingleSelectionCondition() {
                    @Override
                    public void getData() {
                        getIntoManageStateList();
                    }
                })
                .build());

        mView.setStatisticsConditionList(conditionList);
    }

    @Override
    public void saveStatisticsCondition(StatisticsConditionBean condition) {
        DataRepository.getInstance().setStatisticsCondition(condition);
    }
}
