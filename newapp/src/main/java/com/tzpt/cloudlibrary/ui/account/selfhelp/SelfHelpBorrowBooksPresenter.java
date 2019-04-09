package com.tzpt.cloudlibrary.ui.account.selfhelp;

import android.text.TextUtils;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.business_bean.AvailableBalanceBean;
import com.tzpt.cloudlibrary.business_bean.DepositType;
import com.tzpt.cloudlibrary.business_bean.SelfHelpBookBean;
import com.tzpt.cloudlibrary.business_bean.SelfHelpBookInfoBean;
import com.tzpt.cloudlibrary.business_bean.SelfHelpReaderBean;
import com.tzpt.cloudlibrary.business_bean.UsedDepositType;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseResultEntityVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.SelfBorrowBookResultVo;
import com.tzpt.cloudlibrary.utils.MoneyUtils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 自助借书
 */
public class SelfHelpBorrowBooksPresenter extends RxPresenter<SelfHelpBorrowBooksContract.View> implements
        SelfHelpBorrowBooksContract.Presenter {

    private boolean mIsDepositError = false;
    private ArrayList<SelfHelpBookInfoBean> mBookList;
    private SelfHelpReaderBean mReader;

    @Override
    public void getReaderAndBookInfo() {
        //获取读者信息
        if (null != mReader) {
            //获取图书列表
            if (null != mBookList && mBookList.size() > 0) {
                mView.setSelfBookList(mBookList);
                getTotalInfo(mReader.mHandleDeposit);
            } else {
                //图书列表已清空
                mView.setSelfBookEmpty();
            }
        }
    }

    @Override
    public void removeDataByIndex(int position) {
        if (null != mBookList && position < mBookList.size()) {
            mBookList.remove(position);
        }
        if (null != mReader) {
            int tempSum = mReader.mCanBorrowBookSum + 1;
            mReader.mCanBorrowBookSum = tempSum < 0 ? 0 : tempSum;
        }
        getReaderAndBookInfo();
    }

    //设置合计信息
    private void getTotalInfo(boolean handleDeposit) {
        if (null != mView) {
            if (null != mBookList && mBookList.size() > 0) {
                //数量
                //金额
                //占押金
                double totalPrice = 0.00;
                double depositTotalPrice = 0.00;
                double attachPriceTotal = 0.00;
                double attachPriceForDeposit = 0.00;
                for (SelfHelpBookInfoBean info : mBookList) {
                    totalPrice += info.mBook.mPrice;
                    attachPriceTotal += info.mAttachPrice;
                    if (info.mDeposit == 1) {
                        depositTotalPrice += info.mBook.mPrice;
                        attachPriceForDeposit += info.mAttachPrice;
                    }
                }
                boolean isShowDeposit = MoneyUtils.add(depositTotalPrice, attachPriceForDeposit) > 0;
                mView.showBookTotalInfo("数量 " + mBookList.size(),
                        "金额 " + MoneyUtils.addToStr(totalPrice, attachPriceTotal),
                        "占押金 " + (handleDeposit ? MoneyUtils.addToStr(depositTotalPrice, attachPriceForDeposit) : "0.00"),
                        isShowDeposit);
            } else {
                mView.showBookTotalInfo("数量 0", "金额 0.00", "占押金 0.00", false);
            }
        }
    }

    @Override
    public void submitBorrowBookList() {
        if (mReader == null) {
            return;
        }
        //判断可借书数量是否符合
        if (mReader.mCanBorrowBookSum < 0) {
            //超限值
            mView.showDialogTips(R.string.ultra_limit);
            return;
        }

        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (TextUtils.isEmpty(readerId)) {
            return;
        }

        if (null != mBookList && mBookList.size() > 0) {
            JSONArray bookIds = new JSONArray();
            for (int i = mBookList.size() - 1; i >= 0; i--) {
                SelfHelpBookInfoBean item = mBookList.get(i);
                bookIds.put(Long.valueOf(item.mId));
            }
            mView.showProgressDialog();
            Subscription subscription = DataRepository.getInstance().selfBorrowBook(bookIds, readerId, mReader.mCurrentLibCode)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseResultEntityVo<SelfBorrowBookResultVo>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (null != mView) {
                                mView.dismissProgressDialog();
                                mView.showDialogTips(R.string.network_fault);
                            }
                        }

                        @Override
                        public void onNext(BaseResultEntityVo<SelfBorrowBookResultVo> selfBorrowBookResultVo) {
                            if (mView != null) {
                                mView.dismissProgressDialog();
                                if (selfBorrowBookResultVo.status == 200) {
                                    if (null != selfBorrowBookResultVo.data
                                            && null != selfBorrowBookResultVo.data.resultList
                                            && selfBorrowBookResultVo.data.resultList.size() > 0) {

                                        List<SelfHelpBookInfoBean> bookInfoBeanList = new ArrayList<>();
                                        for (SelfBorrowBookResultVo.BookResultVo item : selfBorrowBookResultVo.data.resultList) {
                                            SelfHelpBookInfoBean selfBookInfoBean = new SelfHelpBookInfoBean();
                                            selfBookInfoBean.mId = item.id;
                                            selfBookInfoBean.mAttachPrice = item.attachPrice;
                                            selfBookInfoBean.mBook.mPrice = item.price;
                                            selfBookInfoBean.mBelongLibraryHallCode = item.belongLibraryHallCode;
                                            selfBookInfoBean.mStayLibraryHallCode = item.stayLibraryHallCode;
                                            selfBookInfoBean.mBook.mName = item.properTitle;
                                            selfBookInfoBean.mBook.mBarNumber = item.barNumber;
                                            selfBookInfoBean.mDeposit = item.deposit;
                                            if (null != item.borrowStatus) {//借书状态
                                                if (item.borrowStatus.index == 0) {
                                                    selfBookInfoBean.mStatusSuccess = true;
                                                    selfBookInfoBean.mStatusDesc = "";
                                                } else {
                                                    selfBookInfoBean.mStatusSuccess = false;
                                                    selfBookInfoBean.mStatusDesc = item.borrowStatus.desc;
                                                }
                                            }
                                            bookInfoBeanList.add(selfBookInfoBean);
                                        }
                                        mView.borrowSuccessRefreshBookList(bookInfoBeanList, selfBorrowBookResultVo.data.successNum, selfBorrowBookResultVo.data.failNum);
                                    } else {
                                        mView.borrowBooksSuccess();
                                    }
                                } else if (selfBorrowBookResultVo.status == 417) {
                                    switch (selfBorrowBookResultVo.data.errorCode) {
                                        case 30202://读者不存在
                                            mView.showDialogTips(R.string.reader_does_not_exist);
                                            break;
                                        case 30705://超出最大借阅数
                                            mView.showDialogTips(R.string.ultra_limit);
                                            break;
                                        case 30706://读者账户不存在(未在平台注册或者注册错误)
                                            mView.showDialogTips(R.string.reader_does_not_exist);
                                            break;
                                        case 30701://余额不足
                                            mView.showDialogTips(R.string.please_charge);
                                            break;
                                        case 30104://修改失败
                                        case 30114://添加失败
                                        case 30703://新增客户押金记录失败
                                            mView.showDialogTips(R.string.borrow_book_error);
                                            break;
                                        case 1027://读者无有效的图创读者证,
                                            mView.showDialogTips(R.string.not_library_reader);
                                            break;
                                        case 1066://图创借书失败
                                            mView.showDialogTips(R.string.borrow_book_error);
                                            break;
                                        case 2317:
                                            mView.showDialogTips("有罚金未处理，请先交罚金再借书！", true);
                                            mView.setSelfBookEmpty();
                                            break;
                                        default:
                                            mView.showDialogTips(R.string.borrow_book_error);
                                            break;
                                    }
                                } else if (selfBorrowBookResultVo.status == 401) {
                                    if (selfBorrowBookResultVo.data.errorCode == 30100) {
                                        mView.pleaseLoginTip();
                                    } else {
                                        mView.showDialogTips(R.string.borrow_book_error);
                                    }
                                } else {
                                    mView.showDialogTips(R.string.borrow_book_error);
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }

    @Override
    public void getBookInfo(String barCode) {
        if (TextUtils.isEmpty(barCode)) {
            return;
        }
        //如果获取押金信息错误，则重新获取押金信息
        if (mIsDepositError) {
            getReaderDeposit(barCode);
        } else {
            if (null == mReader) {
                getUserAndBookInfo(barCode);
            } else {
                getBookInfo(mReader.mCurrentLibCode, barCode);
            }
        }
    }

    /**
     * 获取可用押金信息 标记获取可用押金信息是否成功，如果不成功，则下次请求图书信息，则需要重新获取可用押金信息，然后请求获取图书接口
     */
    @Override
    public void getReaderDeposit(final String barCode) {
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (!TextUtils.isEmpty(readerId) && null != mReader && !TextUtils.isEmpty(mReader.mCurrentLibCode)) {
            Subscription subscription = DataRepository.getInstance().getAvailableBalance(mReader.mCurrentLibCode, readerId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<AvailableBalanceBean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            mIsDepositError = true;
                            if (null != mView) {
                                mView.dismissProgressDialog();
                                dealException(e);
                            }
                        }

                        @Override
                        public void onNext(AvailableBalanceBean availableBalanceBean) {
                            if (null != mView) {
                                mView.dismissProgressDialog();
                                if (availableBalanceBean != null) {
                                    mReader.mUsablePlatformDeposit = availableBalanceBean.mUsablePlatformDeposit;
                                    mReader.mUsableLibDeposit = availableBalanceBean.mUsableLibDeposit;
                                } else {
                                    mIsDepositError = true;
                                    mView.showDialogTips(R.string.network_fault);
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }

    @Override
    public void getBorrowInfoTurnToScan() {
        if (null != mBookList && mBookList.size() > 0) {
            //数量
            //金额
            //占押金
            double totalPrice = 0.00;
            double depositTotalPrice = 0.00;
            double attachPriceForDeposit = 0.00;
            for (SelfHelpBookInfoBean info : mBookList) {
                totalPrice += info.mBook.mPrice;
                if (info.mDeposit == 1) {
                    depositTotalPrice += info.mBook.mPrice;
                    attachPriceForDeposit += info.mAttachPrice;
                }
            }
            mView.turnToScan(mReader.mCanBorrowBookSum, mReader.mIsDepositType, mReader.mPenalty,
                    MoneyUtils.add(mReader.mUsableLibDeposit, mReader.mUsablePlatformDeposit),
                    mBookList.size(), totalPrice, MoneyUtils.add(depositTotalPrice, attachPriceForDeposit));

        } else {
            mView.turnToScan(0, false, 0, 0, 0, 0, 0);
        }
    }


    /**
     * 获取第一次读者和图书信息
     *
     * @param barCode 条码号
     */
    private void getUserAndBookInfo(final String barCode) {
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (TextUtils.isEmpty(readerId)) {
            return;
        }
        if (null != mView) {
            mView.showProgressDialog();
        }
        Subscription subscription = DataRepository.getInstance().getSelfHelpBookUserAndBookInfo(barCode, readerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SelfHelpBookBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.dismissProgressDialog();
                            dealException(e);
                        }
                    }

                    @Override
                    public void onNext(SelfHelpBookBean selfHelpBookBean) {
                        if (mView != null) {
                            mView.dismissProgressDialog();
                            if (selfHelpBookBean.mReaderInfo.mHandleDeposit
                                    && !dealPenalty(selfHelpBookBean.mReaderInfo.mDepositType, selfHelpBookBean.mReaderInfo.mPenalty, selfHelpBookBean.mReaderInfo.mCurrentLibCode, barCode)) {
                                //有押金书籍价格是否超过 可用押金,则显示此书需要押金，押金不足
                                if (selfHelpBookBean.mBookInfo.mDeposit == 1) {
                                    double allPrice = MoneyUtils.add(selfHelpBookBean.mBookInfo.mBook.mPrice, selfHelpBookBean.mBookInfo.mAttachPrice);
                                    if (selfHelpBookBean.mBookInfo.mDepositType == DepositType.PLATFORM_DEPOSIT) {
                                        if (selfHelpBookBean.mReaderInfo.mUsablePlatformDeposit < allPrice) {
                                            mView.showRechargeDialog(R.string.please_charge);
                                            return;
                                        } else {
                                            selfHelpBookBean.mBookInfo.mUsedDepositType = UsedDepositType.PLATFORM_DEPOSIT;
                                        }
                                    } else if (selfHelpBookBean.mBookInfo.mDepositType == DepositType.PLATFORM_LIB_DEPOSIT) {
                                        if (selfHelpBookBean.mReaderInfo.mUsablePlatformDeposit < allPrice
                                                && selfHelpBookBean.mReaderInfo.mUsableLibDeposit < allPrice) {
                                            mView.showRechargeDialog(R.string.please_charge);
                                            return;
                                        } else {
                                            if (selfHelpBookBean.mReaderInfo.mDepositSequenceType == SelfHelpReaderBean.DepositSequenceType.PLATFORM_DEPOSIT) {
                                                if (selfHelpBookBean.mReaderInfo.mUsablePlatformDeposit > allPrice) {
                                                    selfHelpBookBean.mBookInfo.mUsedDepositType = UsedDepositType.PLATFORM_DEPOSIT;
                                                } else {
                                                    selfHelpBookBean.mBookInfo.mUsedDepositType = UsedDepositType.LIB_DEPOSIT;
                                                }
                                            } else {
                                                if (selfHelpBookBean.mReaderInfo.mUsableLibDeposit > allPrice) {
                                                    selfHelpBookBean.mBookInfo.mUsedDepositType = UsedDepositType.LIB_DEPOSIT;
                                                } else {
                                                    selfHelpBookBean.mBookInfo.mUsedDepositType = UsedDepositType.PLATFORM_DEPOSIT;
                                                }
                                            }
                                        }
                                    } else {
                                        if (selfHelpBookBean.mReaderInfo.mUsableLibDeposit < allPrice) {
                                            mView.showDialogTips(R.string.please_to_library_dialog);
                                            return;
                                        } else {
                                            selfHelpBookBean.mBookInfo.mUsedDepositType = UsedDepositType.LIB_DEPOSIT;
                                        }
                                    }
                                } else {
                                    selfHelpBookBean.mBookInfo.mUsedDepositType = UsedDepositType.NO_DEPOSIT;
                                }

                                //保存用户信息
                                mReader = selfHelpBookBean.mReaderInfo;

                                if (selfHelpBookBean.mReaderInfo.mCanBorrowBookSum <= 0) {
                                    mView.showDialogTips(R.string.ultra_limit);
                                    return;
                                }

                                if (mBookList == null) {
                                    mBookList = new ArrayList<>();
                                }
                                mBookList.clear();
                                mBookList.add(0, selfHelpBookBean.mBookInfo);
                                //加入图书后重新可借数量
                                int tempSum = mReader.mCanBorrowBookSum - 1;
                                mReader.mCanBorrowBookSum = tempSum < 0 ? 0 : tempSum;

                                getReaderAndBookInfo();
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 获取图书信息(除了第一次的)
     *
     * @param currentLibCode 图书所在馆
     * @param barCode        条码号
     */
    private void getBookInfo(String currentLibCode, String barCode) {
        if (TextUtils.isEmpty(currentLibCode)) {
            return;
        }
        //检查是否有重复书籍
        if (hasRepetitionBooks(barCode)) {
            mView.showDialogTips(R.string.scan_repeat_bar);
            return;
        }
        if (null == mReader) {
            return;
        }
        //是否超过借书数量
        if (mReader.mCanBorrowBookSum <= 0) {
            mView.showDialogTips(R.string.ultra_limit);
            return;
        }
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (TextUtils.isEmpty(readerId)) {
            return;
        }
        mView.showProgressDialog();

        Subscription subscription = DataRepository.getInstance().getSelfBookInfo(barCode, currentLibCode, readerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SelfHelpBookInfoBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.dismissProgressDialog();
                            dealException(e);
                        }
                    }

                    @Override
                    public void onNext(SelfHelpBookInfoBean selfHelpBookInfoBean) {
                        if (mView != null) {
                            mView.dismissProgressDialog();
                            if (mReader.mHandleDeposit) {
                                if (selfHelpBookInfoBean.mDeposit == 1) {
                                    if (selfHelpBookInfoBean.mDepositType == DepositType.PLATFORM_DEPOSIT) {
                                        double allPrice = MoneyUtils.add(getAllBookPlatformDeposit(), selfHelpBookInfoBean.mBook.mPrice + selfHelpBookInfoBean.mAttachPrice);
                                        if (mReader.mUsablePlatformDeposit < allPrice) {
                                            mView.showRechargeDialog(R.string.please_charge);
                                            return;
                                        } else {
                                            selfHelpBookInfoBean.mUsedDepositType = UsedDepositType.PLATFORM_DEPOSIT;
                                        }
                                    } else if (selfHelpBookInfoBean.mDepositType == DepositType.PLATFORM_LIB_DEPOSIT) {
                                        double allPlatformPrice = MoneyUtils.add(getAllBookPlatformDeposit(), selfHelpBookInfoBean.mBook.mPrice + selfHelpBookInfoBean.mAttachPrice);
                                        double allLibPrice = MoneyUtils.add(getAllBookLibDeposit(), selfHelpBookInfoBean.mBook.mPrice + selfHelpBookInfoBean.mAttachPrice);
                                        if (mReader.mUsablePlatformDeposit < allPlatformPrice
                                                && mReader.mUsableLibDeposit < allLibPrice) {
                                            mView.showRechargeDialog(R.string.please_charge);
                                            return;
                                        } else {
                                            if (mReader.mDepositSequenceType == SelfHelpReaderBean.DepositSequenceType.PLATFORM_DEPOSIT) {
                                                if (mReader.mUsablePlatformDeposit > allPlatformPrice) {
                                                    selfHelpBookInfoBean.mUsedDepositType = UsedDepositType.PLATFORM_DEPOSIT;
                                                } else {
                                                    selfHelpBookInfoBean.mUsedDepositType = UsedDepositType.LIB_DEPOSIT;
                                                }
                                            } else {
                                                if (mReader.mUsableLibDeposit > allLibPrice) {
                                                    selfHelpBookInfoBean.mUsedDepositType = UsedDepositType.LIB_DEPOSIT;
                                                } else {
                                                    selfHelpBookInfoBean.mUsedDepositType = UsedDepositType.PLATFORM_DEPOSIT;
                                                }
                                            }
                                        }
                                    } else {
                                        double allPrice = MoneyUtils.add(getAllBookLibDeposit(), selfHelpBookInfoBean.mBook.mPrice + selfHelpBookInfoBean.mAttachPrice);
                                        if (mReader.mUsableLibDeposit < allPrice) {
                                            mView.showDialogTips(R.string.please_to_library_dialog);
                                            return;
                                        } else {
                                            selfHelpBookInfoBean.mUsedDepositType = UsedDepositType.LIB_DEPOSIT;
                                        }
                                    }
                                } else {
                                    selfHelpBookInfoBean.mUsedDepositType = UsedDepositType.NO_DEPOSIT;
                                }
                            }
                            mBookList.add(0, selfHelpBookInfoBean);
                            //加入图书后重新可借数量
                            int tempSum = mReader.mCanBorrowBookSum - 1;
                            mReader.mCanBorrowBookSum = tempSum < 0 ? 0 : tempSum;

                            getReaderAndBookInfo();
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 检查是否有重复书籍
     */
    private boolean hasRepetitionBooks(String libraryBarCode) {
        if (mBookList == null || mBookList.size() == 0) {
            return false;
        }
        //本系统书籍
        if (libraryBarCode.contains("-")) {
            String libraryCode = libraryBarCode.split("-")[0];
            String libCode = libraryBarCode.split("-")[1];
            for (int i = 0; i < mBookList.size(); i++) {
                String belongLibraryHallCode = mBookList.get(i).mBelongLibraryHallCode;
                String barNumber = mBookList.get(i).mBook.mBarNumber;
                if (null != belongLibraryHallCode && null != barNumber) {
                    if (libraryCode.equals(belongLibraryHallCode) && libCode.equals(barNumber)) {
                        return true;
                    }
                }
            }
        } else {//图创图书
            for (int i = 0; i < mBookList.size(); i++) {
                String barNumber = mBookList.get(i).mBook.mBarNumber;
                if (null != barNumber && libraryBarCode.equals(barNumber)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取书籍列表所有共享押金总价
     *
     * @return 共享押金总价
     */
    private double getAllBookPlatformDeposit() {
        double depositTotalPrice = 0.00;
        double attachPriceForDeposit = 0.00;
        for (SelfHelpBookInfoBean info : mBookList) {
            if (info.mDeposit == 1
                    && info.mUsedDepositType == UsedDepositType.PLATFORM_DEPOSIT) {
                depositTotalPrice += info.mBook.mPrice;
                attachPriceForDeposit += info.mAttachPrice;
            }
        }
        return MoneyUtils.add(depositTotalPrice, attachPriceForDeposit);
    }

    /**
     * 获取书籍列表所有馆押金总价
     *
     * @return 馆押金总价
     */
    private double getAllBookLibDeposit() {
        double depositTotalPrice = 0.00;
        double attachPriceForDeposit = 0.00;
        for (SelfHelpBookInfoBean info : mBookList) {
            if (info.mDeposit == 1
                    && info.mUsedDepositType == UsedDepositType.LIB_DEPOSIT) {
                depositTotalPrice += info.mBook.mPrice;
                attachPriceForDeposit += info.mAttachPrice;
            }
        }
        return MoneyUtils.add(depositTotalPrice, attachPriceForDeposit);
    }
//
//    /**
//     * 获取书籍列表押金总价
//     *
//     * @return 押金总价
//     */
//    private double getAllBookDeposit() {
//        double depositTotalPrice = 0.00;
//        double attachPriceForDeposit = 0.00;
//        for (SelfHelpBookInfoBean info : mBookList) {
//            if (info.mDeposit == 1) {
//                depositTotalPrice += info.mBook.mPrice;
//                attachPriceForDeposit += info.mAttachPrice;
//            }
//        }
//        return MoneyUtils.add(depositTotalPrice, attachPriceForDeposit);
//    }

    /**
     * 是否需要处理罚金
     *
     * @param penalty 罚金
     * @return true表示需要处理 false不需要处理
     */
    private boolean dealPenalty(DepositType type, double penalty, String libCode, String barCode) {
        if (penalty > 0) {
            dealPenalty(type, libCode, barCode);
            return true;
        }
        return false;
    }

    private void dealPenalty(final DepositType type, final String libCode, final String barCode) {
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (TextUtils.isEmpty(readerId)){
            return;
        }
        if (null != mView) {
            mView.showProgressDialog();
        }
        Subscription subscription = DataRepository.getInstance().dealUserPenaltyAuto(libCode, readerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Double>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.dismissProgressDialog();
                            dealException(e);
                        }
                    }

                    @Override
                    public void onNext(Double aDouble) {
                        if (mView != null) {
                            mView.dismissProgressDialog();
                            if (aDouble > 0) {
                                String penaltyInfo = "<html>欠逾期罚金 <font color='#ff0000'>"
                                        + MoneyUtils.formatMoney(aDouble)
                                        + "</font>元，" + "<br>请先交罚金！</html>";
                                if (type == DepositType.LIB_DEPOSIT) {
                                    mView.showPenaltyDialogTips(penaltyInfo, libCode, true);
                                } else {
                                    mView.showPenaltyDialogTips(penaltyInfo, libCode, false);
                                }
                            } else {
                                getUserAndBookInfo(barCode);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void dealException(Throwable e) {
        if (e instanceof ApiException) {
            ApiException exception = (ApiException) e;
            switch (exception.getCode()) {
                case 30301://条码错误
                    mView.showDialogTips(R.string.bar_code_not_exist);
                    break;
                case 30307://图书已被预约
                    mView.showDialogTips(R.string.have_make_appointment);
                    break;
                case 30202://限制馆未注册 ，读者不存在，非本馆读者
                    mView.showDialogTips(R.string.not_library_reader);
                    break;
                case 30309://已办借
                    mView.showDialogTips(R.string.book_already_borrowed);
                    break;
                case 30310://已盘亏
                    mView.showDialogTips(R.string.have_dish_deficient);
                    break;
                case 30311://流出锁定
                    mView.showDialogTips(R.string.has_been_out_locked);
                    break;
                case 30312://已赔书
                    mView.showDialogTips(R.string.have_lost);
                    break;
                case 30313://已剔旧
                    mView.showDialogTips(R.string.have_stuck_between_old);
                    break;
                case 30314://基藏库不外借
                    mView.showDialogTips(R.string.book_not_borrow);
                    break;
                case 30315://该馆已停用
                    mView.showDialogTips(R.string.library_is_stop);
                    break;
                case 30316://图书不在当前所在馆
                    mView.showDialogTips(R.string.not_library_book);
                    break;
                case 1027://非本馆读者！(该读者在图创无有效读者证)
                    mView.showDialogTips(R.string.not_library_reader);
                    break;
                case 1011://请至自助借还机上办借！(该书籍为图创书籍,请先到自助借还机上扫描入库)
                    mView.showDialogTips(R.string.please_to_self_borrowing_machine);
                    break;
                case 30205://手机注册读者和限注册馆注册的读者（未验证身份证的非正式读者）在自助借书扫描图书时
                    mView.showIdCardRegisterInfoDialog(R.string.please_complete_id_card_register);
                    break;
                case 500:
                    mView.showDialogTips(R.string.network_fault);
                    break;
                case 30712://有逾期未还书籍
                    mView.showHasOverdueBookTipsDialog();
                    break;
                case 3108:
                    mView.showDialogTips(R.string.not_library_reader);
                    break;
                case 30100:
                    mView.pleaseLoginTip();
                    break;
                case 30318://条码号不存在
                    mView.showDialogTips(R.string.bar_code_not_exist);
                    break;
                case 30319://已流出
                    mView.showDialogTips(R.string.has_been_out);
                    break;
                default:
                    mView.showDialogTips(R.string.network_fault);
                    break;
            }
        } else {
            mView.showDialogTips(R.string.network_fault);
        }
    }
}
