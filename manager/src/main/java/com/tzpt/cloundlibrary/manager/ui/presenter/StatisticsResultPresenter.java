package com.tzpt.cloundlibrary.manager.ui.presenter;

import android.support.v4.util.ArrayMap;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.Gravity;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseResponseCode;
import com.tzpt.cloundlibrary.manager.base.RxPresenter;
import com.tzpt.cloundlibrary.manager.bean.StatisticsConditionBean;
import com.tzpt.cloundlibrary.manager.bean.StatisticsItem;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.BorrowBookStatisticsVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.BorrowingBookStatisticsVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.CollectingBookInfoVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.CollectingBookStatisticsVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.CollectingStatisticsMoneyVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.CollectingStatisticsVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.LostBookStatisticsVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.PenaltyFreeStatisticsInfoVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.PenaltyFreeStatisticsVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.ReaderInfoVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.ReaderStatisticsVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.ReturnBookStatisticsVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.SellBookStatisticsVo;
import com.tzpt.cloundlibrary.manager.ui.contract.StatisticsResultListContract;
import com.tzpt.cloundlibrary.manager.utils.DateUtils;
import com.tzpt.cloundlibrary.manager.utils.MoneyUtils;
import com.tzpt.cloundlibrary.manager.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/9/28.
 */

public class StatisticsResultPresenter extends RxPresenter<StatisticsResultListContract.View>
        implements StatisticsResultListContract.Presenter, BaseResponseCode {
    private final int widthDp60 = 60;
    private final int widthDp70 = 70;
    private final int widthDp80 = 80;
    private final int widthDp85 = 85;
    private final int widthDp93 = 93;
    private final int widthDp95 = 95;
    private final int widthDp105 = 105;
    private final int widthDp110 = 110;
    private final int widthDp125 = 125;
    private final int widthDp135 = 135;
    private final int widthDp150 = 150;
    private final int widthDp185 = 185;
    private final int widthDp120 = 120;
    private int mIndexCount = 1;

    @Override
    public void getStatisticsResult(int type, int pageNumber) {
        mView.showLoadingProgress();
        StatisticsConditionBean condition = DataRepository.getInstance().getStatisticsCondition();
        if (condition == null) {
            mView.setStatisticsResultEmpty();
            return;
        }
        String hallCode = DataRepository.getInstance().getLibraryInfo().mHallCode;
        if (TextUtils.isEmpty(hallCode)) {
            mView.setStatisticsResultEmpty();
            return;
        }
        ArrayMap<String, String> map = new ArrayMap<>();
        map.put("hallCode", hallCode);
        map.put("pageNo", String.valueOf(pageNumber));
        map.put("pageCount", String.valueOf(20));

        if (condition.getConditionType() == StatisticsConditionBean.ConditionType.SingleSelection) {
            map.put(condition.getSingleConditionKey(), condition.getSingleValue());
        } else if (condition.getConditionType() == StatisticsConditionBean.ConditionType.SingleInput) {
            map.put(condition.getSingleConditionKey(), condition.getSingleValue());
        } else if (condition.getConditionType() == StatisticsConditionBean.ConditionType.DoubleInput) {
            map.put(condition.getStartConditionKey(), condition.getStartValue());
            map.put(condition.getEndConditionKey(), condition.getEndValue());
        } else if (condition.getConditionType() == StatisticsConditionBean.ConditionType.DateSelection) {
            map.put(condition.getStartConditionKey(), condition.getStartValue());
            map.put(condition.getEndConditionKey(), condition.getEndValue());
        }
        switch (type) {
            case 0://藏书统计
                getLibraryStatics(map, pageNumber);
                break;
            case 1://在借统计
                getInBorrower(map, pageNumber);
                break;
            case 2://借书统计
                getBorrowerBooks(map, pageNumber);
                break;
            case 3://还书统计
                getReturnBooks(map, pageNumber);
                break;
            case 4://赔书统计
                getLostBookStatisticsList(map, pageNumber);
                break;
            case 5://销售统计
                getBookSells(map, pageNumber);
                break;
            case 6://收款统计
                getGatheringStatistics(map, pageNumber);
                break;
            case 7://读者统计
                getReaderStatistics(map, pageNumber);
                break;
            case 8://免单记录
                getPenaltyFreeApplys(map, pageNumber);
                break;
        }

    }

    @Override
    public void delStatisticsCondition() {
        DataRepository.getInstance().delStatisticsCondition();
    }

    /**
     * 藏书统计
     */
    private void getLibraryStatics(ArrayMap<String, String> map, final int pageNumber) {
        final Subscription subscription = DataRepository.getInstance().getCollectionBookStatisticsList(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CollectingBookStatisticsVo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.setStatisticsResultError(pageNumber == 1);
                        }
                    }

                    @Override
                    public void onNext(CollectingBookStatisticsVo collectingBookStatisticsVo) {
                        if (null != mView) {
                            if (collectingBookStatisticsVo.status == CODE_SUCCESS) {
                                if (null != collectingBookStatisticsVo.data) {
                                    mView.setTotalInfo("合计数量 " + collectingBookStatisticsVo.data.totalCount,
                                            "合计码洋 " + MoneyUtils.formatMoney(collectingBookStatisticsVo.data.totalSumPrice));
                                    List<List<StatisticsItem>> statisticsResult = new ArrayList<>();
                                    List<StatisticsItem> statisticsResultColumnTitle = new ArrayList<>();
                                    for (int i = 0; i < collectingBookStatisticsVo.data.resultList.size(); i++) {
                                        CollectingBookInfoVo bookInfo = collectingBookStatisticsVo.data.resultList.get(i);
                                        List<StatisticsItem> statisticsResultRow = new ArrayList<>();
                                        //馆号（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("馆号",
                                                    widthDp80,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(bookInfo.belongLibraryHallCode) ? "" : bookInfo.belongLibraryHallCode,
                                                widthDp80,
                                                Gravity.CENTER));

                                        //条码号（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("条码号",
                                                    widthDp95,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItemTwoLines(TextUtils.isEmpty(bookInfo.barNumber) ? "" : bookInfo.barNumber,
                                                widthDp95,
                                                Gravity.CENTER | Gravity.START));

                                        //ISBN（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("ISBN",
                                                    widthDp135,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(bookInfo.isbn) ? "" : bookInfo.isbn,
                                                widthDp135,
                                                Gravity.CENTER));

                                        //书名（左对齐）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("书名",
                                                    widthDp150,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItemTwoLines(TextUtils.isEmpty(bookInfo.properTitle) ? "" : bookInfo.properTitle,
                                                widthDp150,
                                                Gravity.START | Gravity.CENTER));

                                        //出版社（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("出版社",
                                                    widthDp105,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItemTwoLines(TextUtils.isEmpty(bookInfo.press) ? "" : bookInfo.press,
                                                widthDp105,
                                                Gravity.CENTER | Gravity.START));

                                        //定价（右对齐）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("定价",
                                                    widthDp85,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(MoneyUtils.formatMoney(bookInfo.price),
                                                widthDp85,
                                                Gravity.END | Gravity.CENTER));

                                        //著者（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("著者",
                                                    widthDp105,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(bookInfo.author) ? "" : bookInfo.author,
                                                widthDp105,
                                                Gravity.CENTER | Gravity.START));

                                        //索书号（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("索书号",
                                                    widthDp95,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(bookInfo.classificationNumber) ? "" : bookInfo.classificationNumber,
                                                widthDp95,
                                                Gravity.CENTER));

                                        //排架号（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("排架号",
                                                    widthDp95,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(bookInfo.frameCode) ? "" : bookInfo.frameCode,
                                                widthDp95,
                                                Gravity.CENTER));


                                        //年份（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("年份",
                                                    widthDp60,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(bookInfo.publishDateYear) ? "" : bookInfo.publishDateYear,
                                                widthDp60,
                                                Gravity.CENTER));

                                        //状态（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("状态",
                                                    widthDp60,
                                                    Gravity.CENTER));
                                        }
                                        String bookState;
                                        if (null != bookInfo.bookState) {
                                            // 1在馆 2在借 25流出 5预约显示为在馆
                                            switch (bookInfo.bookState.index) {
                                                case 1://在馆
                                                case 5://预约
                                                    bookState = "在馆";
                                                    break;
                                                case 2:
                                                    bookState = "在借";
                                                    break;
                                                case 25:
                                                    bookState = "流出";
                                                    break;
                                                default:
                                                    bookState = "其他";
                                                    break;
                                            }
                                        } else {
                                            bookState = "其他";
                                        }
                                        statisticsResultRow.add(createStatisticsItem(bookState,
                                                widthDp60,
                                                Gravity.CENTER));

                                        statisticsResult.add(statisticsResultRow);
                                    }

                                    if (statisticsResult.size() > 0) {
                                        if (pageNumber == 1) {
                                            mView.setStatisticsResultColumnTitle(statisticsResultColumnTitle);
                                        }

                                        mView.setStatisticsResultList(statisticsResult,
                                                collectingBookStatisticsVo.data.totalCount,
                                                pageNumber == 1);
                                    } else {
                                        mView.setStatisticsResultEmpty();
                                    }
                                } else {
                                    mView.setStatisticsResultError(pageNumber == 1);
                                }
                            } else {
                                if (null != collectingBookStatisticsVo.data) {
                                    if (collectingBookStatisticsVo.data.errorCode == ERROR_CODE_KICK_OUT) {
                                        mView.noPermissionPrompt(R.string.kicked_offline);
                                    } else if (collectingBookStatisticsVo.data.errorCode == ERROR_CODE_1006) {
                                        mView.noPermissionPrompt(R.string.operate_timeout);
                                    } else {
                                        mView.setStatisticsResultError(pageNumber == 1);
                                    }
                                } else {
                                    mView.setStatisticsResultError(pageNumber == 1);
                                }
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 在借统计
     */
    private void getInBorrower(ArrayMap<String, String> map, final int pageNumber) {
        Subscription subscription = DataRepository.getInstance().getBorrowingBookStatisticsList(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BorrowingBookStatisticsVo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.setStatisticsResultError(pageNumber == 1);
                        }
                    }

                    @Override
                    public void onNext(BorrowingBookStatisticsVo borrowingBookStatisticsVo) {
                        if (null != mView) {
                            if (borrowingBookStatisticsVo.status == CODE_SUCCESS) {
                                if (null != borrowingBookStatisticsVo.data) {
                                    mView.setTotalInfo("合计数量 " + borrowingBookStatisticsVo.data.totalCount,
                                            "合计码洋 " + MoneyUtils.formatMoney(borrowingBookStatisticsVo.data.totalSumPrice));
                                    List<List<StatisticsItem>> statisticsResult = new ArrayList<>();
                                    List<StatisticsItem> statisticsResultColumnTitle = new ArrayList<>();
                                    for (int i = 0; i < borrowingBookStatisticsVo.data.resultList.size(); i++) {
                                        BorrowingBookStatisticsVo.BorrowingBookVo bookInfo = borrowingBookStatisticsVo.data.resultList.get(i);
                                        List<StatisticsItem> statisticsResultRow = new ArrayList<>();
                                        //借书日期（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("借书日期",
                                                    widthDp93,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(bookInfo.borrowTime) ? "" : bookInfo.borrowTime.replace("-", ""),
                                                widthDp93,
                                                Gravity.CENTER));

                                        //馆号（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("馆号",
                                                    widthDp80,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(bookInfo.hallCode) ? "" : bookInfo.hallCode,
                                                widthDp80,
                                                Gravity.CENTER));

                                        //条码号（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("条码号",
                                                    widthDp95,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItemTwoLines(TextUtils.isEmpty(bookInfo.barNumber) ? "" : bookInfo.barNumber,
                                                widthDp95,
                                                Gravity.CENTER | Gravity.START));

                                        //ISBN（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("ISBN",
                                                    widthDp135,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(bookInfo.isbn) ? "" : bookInfo.isbn,
                                                widthDp135,
                                                Gravity.CENTER));

                                        //书名（左对齐）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("书名",
                                                    widthDp150,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItemTwoLines(TextUtils.isEmpty(bookInfo.properTitle) ? "" : bookInfo.properTitle,
                                                widthDp150,
                                                Gravity.CENTER | Gravity.START));

                                        //出版社（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("出版社",
                                                    widthDp105,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItemTwoLines(TextUtils.isEmpty(bookInfo.press) ? "" : bookInfo.press,
                                                widthDp105,
                                                Gravity.CENTER | Gravity.START));

                                        //定价（右对齐）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("定价",
                                                    widthDp85,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(MoneyUtils.formatMoney(bookInfo.price),
                                                widthDp85,
                                                Gravity.CENTER | Gravity.END));

                                        //读者信息（居中）
                                        String readerInfo;
                                        String phone = null;
                                        if (!TextUtils.isEmpty(bookInfo.phone) && StringUtils.isMobileNumber(bookInfo.phone)) {
                                            phone = bookInfo.phone;
                                            readerInfo = bookInfo.cardName + "<br/>\r\n" + "<font color=#007aff>" + bookInfo.phone + " </font>";
                                        } else {
                                            readerInfo = bookInfo.cardName;
                                        }
                                        Spanned result = null;
                                        if (!TextUtils.isEmpty(readerInfo)) {
                                            result = Html.fromHtml(readerInfo);
                                        }

                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("读者信息",
                                                    widthDp125,
                                                    Gravity.CENTER));
                                        }
                                        StatisticsItem item = createStatisticsItemTwoLines(TextUtils.isEmpty(result) ? "" : result,
                                                widthDp125,
                                                Gravity.CENTER);
                                        item.mPhone = phone;
                                        statisticsResultRow.add(item);

                                        //状态（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("状态",
                                                    widthDp70,
                                                    Gravity.CENTER));
                                        }
                                        String state;
                                        switch (bookInfo.isOverdue) {
                                            case 1:
                                                state = "逾期";
                                                break;
                                            case 2:
                                                state = "超逾期";
                                                break;
                                            case 3:
                                                state = "未逾期";
                                                break;
                                            default:
                                                state = "未逾期";
                                                break;
                                        }
                                        statisticsResultRow.add(createStatisticsItem(state,
                                                widthDp70,
                                                Gravity.CENTER));

                                        statisticsResult.add(statisticsResultRow);
                                    }
                                    if (statisticsResult.size() > 0) {
                                        if (pageNumber == 1) {
                                            mView.setStatisticsResultColumnTitle(statisticsResultColumnTitle);
                                        }
                                        mView.setStatisticsResultList(statisticsResult, borrowingBookStatisticsVo.data.totalCount, pageNumber == 1);
                                    } else {
                                        mView.setStatisticsResultEmpty();
                                    }
                                } else {
                                    mView.setStatisticsResultEmpty();
                                }
                            } else {
                                if (null != borrowingBookStatisticsVo.data) {
                                    if (borrowingBookStatisticsVo.data.errorCode == ERROR_CODE_KICK_OUT) {
                                        mView.noPermissionPrompt(R.string.kicked_offline);
                                    } else if (borrowingBookStatisticsVo.data.errorCode == ERROR_CODE_1006) {
                                        mView.noPermissionPrompt(R.string.operate_timeout);
                                    } else {
                                        mView.setStatisticsResultError(pageNumber == 1);
                                    }
                                } else {
                                    mView.setStatisticsResultError(pageNumber == 1);
                                }
                            }
                        }
                    }

                });

        addSubscrebe(subscription);
    }

    /**
     * 借书统计
     */
    private void getBorrowerBooks(ArrayMap<String, String> map, final int pageNumber) {
        Subscription subscription = DataRepository.getInstance().getBorrowBookStatisticsList(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BorrowBookStatisticsVo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.setStatisticsResultError(pageNumber == 1);
                        }
                    }

                    @Override
                    public void onNext(BorrowBookStatisticsVo borrowBookStatisticsVo) {
                        if (null != mView) {
                            if (borrowBookStatisticsVo.status == CODE_SUCCESS) {
                                if (null != borrowBookStatisticsVo.data) {
                                    mView.setTotalInfo("合计数量 " + borrowBookStatisticsVo.data.totalCount,
                                            "合计码洋 " + MoneyUtils.formatMoney(borrowBookStatisticsVo.data.totalSumPrice));
                                    List<List<StatisticsItem>> statisticsResult = new ArrayList<>();
                                    List<StatisticsItem> statisticsResultColumnTitle = new ArrayList<>();
                                    for (int i = 0; i < borrowBookStatisticsVo.data.resultList.size(); i++) {
                                        BorrowBookStatisticsVo.BorrowingBookVo bookInfo = borrowBookStatisticsVo.data.resultList.get(i);

                                        List<StatisticsItem> statisticsResultRow = new ArrayList<>();
                                        //日期（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("日期",
                                                    widthDp93,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(bookInfo.borrowTime) ? "" : bookInfo.borrowTime.replace("-", ""),
                                                widthDp93,
                                                Gravity.CENTER));

                                        //单号（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("单号",
                                                    widthDp110,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(bookInfo.borrowNumber) ? "" : bookInfo.borrowNumber,
                                                widthDp110,
                                                Gravity.CENTER));

                                        //馆号（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("馆号",
                                                    widthDp80,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(bookInfo.belongLibraryHallCode) ? "" : bookInfo.belongLibraryHallCode,
                                                widthDp80,
                                                Gravity.CENTER));

                                        //条码号（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("条码号",
                                                    widthDp95,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItemTwoLines(TextUtils.isEmpty(bookInfo.barNumber) ? "" : bookInfo.barNumber,
                                                widthDp95,
                                                Gravity.CENTER | Gravity.START));

                                        //ISBN（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("ISBN",
                                                    widthDp135,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(bookInfo.isbn) ? "" : bookInfo.isbn,
                                                widthDp135,
                                                Gravity.CENTER));

                                        //书名（左对齐）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("书名",
                                                    widthDp150,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItemTwoLines(TextUtils.isEmpty(bookInfo.properTitle) ? "" : bookInfo.properTitle,
                                                widthDp150,
                                                Gravity.CENTER | Gravity.START));

                                        //出版社（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("出版社",
                                                    widthDp105,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItemTwoLines(TextUtils.isEmpty(bookInfo.press) ? "" : bookInfo.press,
                                                widthDp105,
                                                Gravity.CENTER | Gravity.START));

                                        //定价（右对齐）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("定价",
                                                    widthDp85,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(MoneyUtils.formatMoney(bookInfo.price),
                                                widthDp85,
                                                Gravity.CENTER | Gravity.END));


                                        //索书号（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("索书号",
                                                    widthDp95,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(bookInfo.classificationNumber) ? "" : bookInfo.classificationNumber,
                                                widthDp95,
                                                Gravity.CENTER));

                                        //排架号（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("排架号",
                                                    widthDp95,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(bookInfo.frameCode) ? "" : bookInfo.frameCode,
                                                widthDp95,
                                                Gravity.CENTER));

                                        //操作员（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("操作员",
                                                    widthDp110,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(bookInfo.userName) ? "" : bookInfo.userName,
                                                widthDp110,
                                                Gravity.CENTER));

                                        statisticsResult.add(statisticsResultRow);
                                    }
                                    if (statisticsResult.size() > 0) {
                                        if (pageNumber == 1) {
                                            mView.setStatisticsResultColumnTitle(statisticsResultColumnTitle);
                                        }
                                        mView.setStatisticsResultList(statisticsResult, borrowBookStatisticsVo.data.totalCount, pageNumber == 1);
                                    } else {
                                        mView.setStatisticsResultEmpty();
                                    }
                                } else {
                                    mView.setStatisticsResultEmpty();
                                }
                            } else {
                                if (null != borrowBookStatisticsVo.data) {
                                    if (borrowBookStatisticsVo.data.errorCode == ERROR_CODE_KICK_OUT) {
                                        mView.noPermissionPrompt(R.string.kicked_offline);
                                    } else if (borrowBookStatisticsVo.data.errorCode == ERROR_CODE_1006) {
                                        mView.noPermissionPrompt(R.string.operate_timeout);
                                    } else {
                                        mView.setStatisticsResultError(pageNumber == 1);
                                    }
                                } else {
                                    mView.setStatisticsResultError(pageNumber == 1);
                                }
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 还书统计
     */
    private void getReturnBooks(ArrayMap<String, String> map, final int pageNumber) {
        Subscription subscription = DataRepository.getInstance().getReturnBookStatisticsList(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ReturnBookStatisticsVo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.setStatisticsResultError(pageNumber == 1);
                        }
                    }

                    @Override
                    public void onNext(ReturnBookStatisticsVo returnBookStatisticsVo) {
                        if (null != mView) {
                            if (returnBookStatisticsVo.status == CODE_SUCCESS) {
                                if (null != returnBookStatisticsVo.data) {
                                    mView.setTotalInfo("合计数量 " + returnBookStatisticsVo.data.totalCount,
                                            "合计押金 " + MoneyUtils.formatMoney(returnBookStatisticsVo.data.totalSumPrice));
                                    List<List<StatisticsItem>> statisticsResult = new ArrayList<>();
                                    List<StatisticsItem> statisticsResultColumnTitle = new ArrayList<>();
                                    for (int i = 0; i < returnBookStatisticsVo.data.resultList.size(); i++) {
                                        ReturnBookStatisticsVo.BorrowingBookVo bookInfo = returnBookStatisticsVo.data.resultList.get(i);
                                        List<StatisticsItem> statisticsResultRow = new ArrayList<>();
                                        //日期（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("日期",
                                                    widthDp93,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(bookInfo.returnTime) ? "" : bookInfo.returnTime.replace("-", ""),
                                                widthDp93,
                                                Gravity.CENTER));

                                        //单号（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("单号",
                                                    widthDp110,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(bookInfo.returnNumber) ? "" : bookInfo.returnNumber,
                                                widthDp110,
                                                Gravity.CENTER));

                                        //条码号（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("条码号",
                                                    widthDp95,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItemTwoLines(TextUtils.isEmpty(bookInfo.barNumber) ? "" : bookInfo.barNumber,
                                                widthDp95,
                                                Gravity.CENTER | Gravity.START));

                                        //ISBN（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("ISBN",
                                                    widthDp135,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(bookInfo.isbn) ? "" : bookInfo.isbn,
                                                widthDp135,
                                                Gravity.CENTER));

                                        //书名（左对齐）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("书名",
                                                    widthDp150,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItemTwoLines(TextUtils.isEmpty(bookInfo.properTitle) ? "" : bookInfo.properTitle,
                                                widthDp150,
                                                Gravity.CENTER | Gravity.START));

                                        //出版社（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("出版社",
                                                    widthDp105,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItemTwoLines(TextUtils.isEmpty(bookInfo.press) ? "" : bookInfo.press,
                                                widthDp105,
                                                Gravity.CENTER | Gravity.START));

                                        //定价（右对齐）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("定价",
                                                    widthDp85,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(MoneyUtils.formatMoney(bookInfo.price),
                                                widthDp85,
                                                Gravity.CENTER | Gravity.END));

                                        //著者（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("著者",
                                                    widthDp105,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(bookInfo.author) ? "" : bookInfo.author,
                                                widthDp105,
                                                Gravity.CENTER | Gravity.START));

                                        //索书号（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("索书号",
                                                    widthDp95,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(bookInfo.classificationNumber) ? "" : bookInfo.classificationNumber,
                                                widthDp95,
                                                Gravity.CENTER));

                                        //排架号（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("排架号",
                                                    widthDp95,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(bookInfo.frameCode) ? "" : bookInfo.frameCode,
                                                widthDp95,
                                                Gravity.CENTER));

                                        //操作员（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("操作员",
                                                    widthDp110,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(bookInfo.userName) ? "" : bookInfo.userName,
                                                widthDp110,
                                                Gravity.CENTER));

                                        statisticsResult.add(statisticsResultRow);
                                    }
                                    if (statisticsResult.size() > 0) {
                                        if (pageNumber == 1) {
                                            mView.setStatisticsResultColumnTitle(statisticsResultColumnTitle);
                                        }
                                        mView.setStatisticsResultList(statisticsResult, returnBookStatisticsVo.data.totalCount, pageNumber == 1);
                                    } else {
                                        mView.setStatisticsResultEmpty();
                                    }
                                } else {
                                    mView.setStatisticsResultEmpty();
                                }
                            } else {
                                if (null != returnBookStatisticsVo.data) {
                                    if (returnBookStatisticsVo.data.errorCode == ERROR_CODE_KICK_OUT) {
                                        mView.noPermissionPrompt(R.string.kicked_offline);
                                    } else if (returnBookStatisticsVo.data.errorCode == ERROR_CODE_1006) {
                                        mView.noPermissionPrompt(R.string.operate_timeout);
                                    } else {
                                        mView.setStatisticsResultError(pageNumber == 1);
                                    }
                                } else {
                                    mView.setStatisticsResultError(pageNumber == 1);
                                }
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 赔书统计
     */
    private void getLostBookStatisticsList(ArrayMap<String, String> map, final int pageNumber) {
        Subscription subscription = DataRepository.getInstance().getLostBookStatisticsList(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LostBookStatisticsVo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.setStatisticsResultError(pageNumber == 1);
                        }
                    }

                    @Override
                    public void onNext(LostBookStatisticsVo lostBookStatisticsVo) {
                        if (null != mView) {
                            if (lostBookStatisticsVo.status == CODE_SUCCESS) {
                                if (null != lostBookStatisticsVo.data) {
                                    mView.setTotalInfo("合计数量 " + lostBookStatisticsVo.data.totalCount,
                                            "合计码洋 " + MoneyUtils.formatMoney(lostBookStatisticsVo.data.totalSumPrice));
                                    List<List<StatisticsItem>> statisticsResult = new ArrayList<>();
                                    List<StatisticsItem> statisticsResultColumnTitle = new ArrayList<>();
                                    for (int i = 0; i < lostBookStatisticsVo.data.resultList.size(); i++) {
                                        LostBookStatisticsVo.LostBookVo bookInfo = lostBookStatisticsVo.data.resultList.get(i);
                                        List<StatisticsItem> statisticsResultRow = new ArrayList<>();
                                        //日期（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("日期",
                                                    widthDp93,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(bookInfo.compensateDate) ? "" : bookInfo.compensateDate.replace("-", ""),
                                                widthDp93,
                                                Gravity.CENTER));

                                        //单号（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("单号",
                                                    widthDp110,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(bookInfo.returnCode) ? "" : bookInfo.returnCode,
                                                widthDp110,
                                                Gravity.CENTER));

                                        //条码号（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("条码号",
                                                    widthDp95,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItemTwoLines(TextUtils.isEmpty(bookInfo.barNumber) ? "" : bookInfo.barNumber,
                                                widthDp95,
                                                Gravity.CENTER | Gravity.START));

                                        //ISBN（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("ISBN",
                                                    widthDp135,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(bookInfo.isbn) ? "" : bookInfo.isbn,
                                                widthDp135,
                                                Gravity.CENTER));

                                        //书名（左对齐）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("书名",
                                                    widthDp150,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItemTwoLines(TextUtils.isEmpty(bookInfo.properTitle) ? "" : bookInfo.properTitle,
                                                widthDp150,
                                                Gravity.CENTER | Gravity.START));

                                        //出版社（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("出版社",
                                                    widthDp105,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItemTwoLines(TextUtils.isEmpty(bookInfo.press) ? "" : bookInfo.press,
                                                widthDp105,
                                                Gravity.CENTER | Gravity.START));

                                        //定价（右对齐）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("定价",
                                                    widthDp85,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(MoneyUtils.formatMoney(bookInfo.price),
                                                widthDp85,
                                                Gravity.CENTER | Gravity.END));

                                        //索书号（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("索书号",
                                                    widthDp95,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(bookInfo.classificationNumber) ? "" : bookInfo.classificationNumber,
                                                widthDp95,
                                                Gravity.CENTER));

                                        //排架号（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("排架号",
                                                    widthDp95,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(bookInfo.frameCode) ? "" : bookInfo.frameCode,
                                                widthDp95,
                                                Gravity.CENTER));

                                        //金额（右对齐）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("金额",
                                                    widthDp85,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(MoneyUtils.formatMoney(bookInfo.moneySum),
                                                widthDp85,
                                                Gravity.CENTER | Gravity.END));

                                        //操作员（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("操作员",
                                                    widthDp110,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(bookInfo.userName) ? "" : bookInfo.userName,
                                                widthDp110,
                                                Gravity.CENTER));

                                        statisticsResult.add(statisticsResultRow);
                                    }
                                    if (statisticsResult.size() > 0) {
                                        if (pageNumber == 1) {
                                            mView.setStatisticsResultColumnTitle(statisticsResultColumnTitle);
                                        }
                                        mView.setStatisticsResultList(statisticsResult, lostBookStatisticsVo.data.totalCount, pageNumber == 1);
                                    } else {
                                        mView.setStatisticsResultEmpty();
                                    }
                                } else {
                                    mView.setStatisticsResultEmpty();
                                }
                            } else {
                                if (null != lostBookStatisticsVo.data) {
                                    if (lostBookStatisticsVo.data.errorCode == ERROR_CODE_KICK_OUT) {
                                        mView.noPermissionPrompt(R.string.kicked_offline);
                                    } else if (lostBookStatisticsVo.data.errorCode == ERROR_CODE_1006) {
                                        mView.noPermissionPrompt(R.string.operate_timeout);
                                    } else {
                                        mView.setStatisticsResultError(pageNumber == 1);
                                    }
                                } else {
                                    mView.setStatisticsResultError(pageNumber == 1);
                                }
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 收款统计
     */
    private void getGatheringStatistics(ArrayMap<String, String> map, final int pageNumber) {
        Subscription subscription = DataRepository.getInstance().getCollectingStatisticsList(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CollectingStatisticsVo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.setStatisticsResultError(pageNumber == 1);
                        }
                    }

                    @Override
                    public void onNext(CollectingStatisticsVo collectingStatisticsVo) {
                        if (null != mView) {
                            if (collectingStatisticsVo.status == CODE_SUCCESS) {
                                if (null != collectingStatisticsVo.data && null != collectingStatisticsVo.data.resultList) {
                                    mView.setTotalInfo("合计数量 " + collectingStatisticsVo.data.totalCount,
                                            "合计码洋 " + MoneyUtils.formatMoney(collectingStatisticsVo.data.totalSumPrice));
                                    List<List<StatisticsItem>> statisticsResult = new ArrayList<>();
                                    List<StatisticsItem> statisticsResultColumnTitle = new ArrayList<>();
                                    for (int i = 0; i < collectingStatisticsVo.data.resultList.size(); i++) {
                                        CollectingStatisticsMoneyVo statisticsMoney = collectingStatisticsVo.data.resultList.get(i);
                                        List<StatisticsItem> statisticsResultRow = new ArrayList<>();
                                        //日期（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("日期",
                                                    widthDp93,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(statisticsMoney.operDate) ? "" : statisticsMoney.operDate.replace("-", ""),
                                                widthDp93,
                                                Gravity.CENTER));

                                        //单号（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("单号",
                                                    widthDp110,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(statisticsMoney.operOrder) ? "" : statisticsMoney.operOrder,
                                                widthDp110,
                                                Gravity.CENTER));

                                        //读者（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("读者",
                                                    widthDp110,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(statisticsMoney.cardName) ? "" : statisticsMoney.cardName,
                                                widthDp110,
                                                Gravity.CENTER));

                                        //身份证号（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("身份证号",
                                                    widthDp185,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(statisticsMoney.idcard) ? "" : statisticsMoney.idcard,
                                                widthDp185,
                                                Gravity.CENTER));

                                        //在借数量（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("在借数量",
                                                    widthDp95,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(String.valueOf(statisticsMoney.bookSum),
                                                widthDp95,
                                                Gravity.CENTER));

                                        //在借金额（右对齐）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("在借金额",
                                                    widthDp95,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(MoneyUtils.formatMoney(statisticsMoney.inBorrowerPrice),
                                                widthDp95,
                                                Gravity.CENTER | Gravity.END));

                                        //金额（右对齐）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("金额",
                                                    widthDp85,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(MoneyUtils.formatMoney(statisticsMoney.deposit),
                                                widthDp85,
                                                Gravity.CENTER | Gravity.END));

                                        //项目（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("项目",
                                                    widthDp70,
                                                    Gravity.CENTER));
                                        }
                                        String desc;
                                        if (!TextUtils.isEmpty(statisticsMoney.operation)) {
                                            //项目1:个人充值,2,"个人提现",3,"交罚金4,"交赔金5,"代读者充值6,"代读者提现
                                            int operation = Integer.parseInt(statisticsMoney.operation);
                                            switch (operation) {
                                                case 3:
                                                case 30:
                                                case 34:
                                                    desc = "收罚金";
                                                    break;
                                                case 4:
                                                case 31:
                                                case 33:
                                                    desc = "收赔金";
                                                    break;
                                                case 5:
                                                case 9:
                                                    desc = "收现金";
                                                    break;
                                                case 6:
                                                case 10:
                                                    desc = "退押金";
//                                                    isRed = true;
                                                    break;
                                                default:
                                                    desc = "其他";
                                                    break;
                                            }
                                        } else {
                                            desc = "其他";
                                        }
                                        statisticsResultRow.add(createStatisticsItem(desc,
                                                widthDp70,
                                                Gravity.CENTER));

                                        //操作员（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("操作员",
                                                    widthDp110,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(String.valueOf(statisticsMoney.userName),
                                                widthDp110,
                                                Gravity.CENTER));

                                        statisticsResult.add(statisticsResultRow);
                                    }
                                    if (statisticsResult.size() > 0) {
                                        if (pageNumber == 1) {
                                            mView.setStatisticsResultColumnTitle(statisticsResultColumnTitle);
                                        }
                                        mView.setStatisticsResultList(statisticsResult, collectingStatisticsVo.data.totalCount, pageNumber == 1);
                                    } else {
                                        mView.setStatisticsResultEmpty();
                                    }
                                } else {
                                    mView.setStatisticsResultEmpty();
                                }
                            } else {
                                if (null != collectingStatisticsVo.data) {
                                    if (collectingStatisticsVo.data.errorCode == ERROR_CODE_KICK_OUT) {
                                        mView.noPermissionPrompt(R.string.kicked_offline);
                                    } else if (collectingStatisticsVo.data.errorCode == ERROR_CODE_1006) {
                                        mView.noPermissionPrompt(R.string.operate_timeout);
                                    } else {
                                        mView.setStatisticsResultError(pageNumber == 1);
                                    }
                                } else {
                                    mView.setStatisticsResultError(pageNumber == 1);
                                }
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 读者统计
     */
    private void getReaderStatistics(ArrayMap<String, String> map, final int pageNumber) {
        if (pageNumber == 1) {
            mIndexCount = 1;
        }
        Subscription subscription = DataRepository.getInstance().getReaderStatisticsList(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ReaderStatisticsVo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.setStatisticsResultError(pageNumber == 1);
                        }
                    }

                    @Override
                    public void onNext(ReaderStatisticsVo readerStatisticsVo) {
                        if (null != mView) {
                            if (readerStatisticsVo.status == CODE_SUCCESS) {
                                if (null != readerStatisticsVo.data && readerStatisticsVo.data.resultList.size() > 0) {
                                    mView.setTotalInfo("合计数量 " + readerStatisticsVo.data.totalCount,
                                            "合计押金 " + MoneyUtils.formatMoney(readerStatisticsVo.data.totalSumPrice));
                                    List<List<StatisticsItem>> statisticsResult = new ArrayList<>();
                                    List<StatisticsItem> statisticsResultColumnTitle = new ArrayList<>();
                                    for (int i = 0; i < readerStatisticsVo.data.resultList.size(); i++) {
                                        ReaderInfoVo reader = readerStatisticsVo.data.resultList.get(i);
                                        List<StatisticsItem> statisticsResultRow = new ArrayList<>();
                                        //序号（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("序号",
                                                    widthDp60,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(String.valueOf(mIndexCount++),
                                                widthDp60,
                                                Gravity.CENTER));

                                        //读者类型（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("读者类型",
                                                    widthDp120,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(reader.isServiceReader == 1 ? "服务读者" : "注册读者",
                                                widthDp120,
                                                Gravity.CENTER));

                                        //姓名（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("姓名",
                                                    widthDp110,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(reader.cardName) ? "" : reader.cardName,
                                                widthDp110,
                                                Gravity.CENTER));

                                        //身份证号（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("身份证号",
                                                    widthDp185,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(reader.readerIdCard) ? "" : reader.readerIdCard,
                                                widthDp185,
                                                Gravity.CENTER));

                                        //借阅证号（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("借阅证号",
                                                    widthDp110,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(reader.borrowCard) ? "" : reader.borrowCard,
                                                widthDp110,
                                                Gravity.CENTER));

                                        //建档日期（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("建档日期",
                                                    widthDp93,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(reader.createTime) ? "" : reader.createTime.replace("-", ""),
                                                widthDp93,
                                                Gravity.CENTER));

                                        //借阅次数（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("借阅次数",
                                                    widthDp93,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(String.valueOf(reader.borrowerCount),
                                                widthDp93,
                                                Gravity.CENTER));

                                        //馆押金（右对齐）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("馆押金",
                                                    widthDp85,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(MoneyUtils.formatMoney(reader.balance),
                                                widthDp85,
                                                Gravity.CENTER | Gravity.END));

                                        //读者级别（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("读者级别",
                                                    widthDp95,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(reader.readerJiBie) ? "" : reader.readerJiBie,
                                                widthDp95,
                                                Gravity.CENTER));

                                        statisticsResult.add(statisticsResultRow);
                                    }
                                    if (statisticsResult.size() > 0) {
                                        if (pageNumber == 1) {
                                            mView.setStatisticsResultColumnTitle(statisticsResultColumnTitle);
                                        }
                                        mView.setStatisticsResultList(statisticsResult, readerStatisticsVo.data.totalCount, pageNumber == 1);
                                    } else {
                                        mView.setStatisticsResultEmpty();
                                    }
                                } else {
                                    mView.setStatisticsResultEmpty();
                                }
                            } else {
                                if (readerStatisticsVo.data.errorCode == ERROR_CODE_KICK_OUT) {
                                    mView.noPermissionPrompt(R.string.kicked_offline);
                                } else if (readerStatisticsVo.data.errorCode == ERROR_CODE_1006) {
                                    mView.noPermissionPrompt(R.string.operate_timeout);
                                } else {
                                    mView.setStatisticsResultError(pageNumber == 1);
                                }
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 销售统计
     */
    private void getBookSells(ArrayMap<String, String> map, final int pageNumber) {
        Subscription subscription = DataRepository.getInstance().getSellBookStatisticsList(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SellBookStatisticsVo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.setStatisticsResultError(pageNumber == 1);
                        }
                    }

                    @Override
                    public void onNext(SellBookStatisticsVo sellBookStatisticsVo) {
                        if (null != mView) {
                            if (sellBookStatisticsVo.status == CODE_SUCCESS) {
                                if (null != sellBookStatisticsVo.data) {
                                    mView.setTotalInfo("合计数量 " + sellBookStatisticsVo.data.totalCount,
                                            "合计码洋 " + MoneyUtils.formatMoney(sellBookStatisticsVo.data.totalSumPrice));
                                    List<List<StatisticsItem>> statisticsResult = new ArrayList<>();
                                    List<StatisticsItem> statisticsResultColumnTitle = new ArrayList<>();
                                    for (int i = 0; i < sellBookStatisticsVo.data.resultList.size(); i++) {
                                        SellBookStatisticsVo.SellBookVo bookInfo = sellBookStatisticsVo.data.resultList.get(i);
                                        List<StatisticsItem> statisticsResultRow = new ArrayList<>();
                                        //日期（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("日期",
                                                    widthDp93,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(bookInfo.createTime) ? "" : bookInfo.createTime.replace("-", ""),
                                                widthDp93,
                                                Gravity.CENTER));

                                        //条码号（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("条码号",
                                                    widthDp95,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItemTwoLines(TextUtils.isEmpty(bookInfo.barNumber) ? "" : bookInfo.barNumber,
                                                widthDp95,
                                                Gravity.CENTER | Gravity.START));

                                        //ISBN（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("ISBN",
                                                    widthDp135,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(bookInfo.isbn) ? "" : bookInfo.isbn,
                                                widthDp135,
                                                Gravity.CENTER));

                                        //书名（左对齐）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("书名",
                                                    widthDp150,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItemTwoLines(TextUtils.isEmpty(bookInfo.properTitle) ? "" : bookInfo.properTitle,
                                                widthDp150,
                                                Gravity.CENTER | Gravity.START));

                                        //出版社（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("出版社",
                                                    widthDp105,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItemTwoLines(TextUtils.isEmpty(bookInfo.press) ? "" : bookInfo.press,
                                                widthDp105,
                                                Gravity.CENTER | Gravity.START));

                                        //定价（右对齐）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("定价",
                                                    widthDp85,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(MoneyUtils.formatMoney(bookInfo.price),
                                                widthDp85,
                                                Gravity.CENTER | Gravity.END));

                                        //索书号（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("索书号",
                                                    widthDp95,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(bookInfo.classificationNumber) ? "" : bookInfo.classificationNumber,
                                                widthDp95,
                                                Gravity.CENTER));

                                        //排架号（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("排架号",
                                                    widthDp95,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(bookInfo.frameCode) ? "" : bookInfo.frameCode,
                                                widthDp95,
                                                Gravity.CENTER));

                                        //金额（右对齐）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("金额",
                                                    widthDp85,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(MoneyUtils.formatMoney(bookInfo.amount),
                                                widthDp85,
                                                Gravity.CENTER | Gravity.END));

                                        //操作员（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("操作员",
                                                    widthDp110,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(bookInfo.userName) ? "" : bookInfo.userName,
                                                widthDp110,
                                                Gravity.CENTER));

                                        statisticsResult.add(statisticsResultRow);
                                    }
                                    if (statisticsResult.size() > 0) {
                                        if (pageNumber == 1) {
                                            mView.setStatisticsResultColumnTitle(statisticsResultColumnTitle);
                                        }
                                        mView.setStatisticsResultList(statisticsResult, sellBookStatisticsVo.data.totalCount, pageNumber == 1);
                                    } else {
                                        mView.setStatisticsResultEmpty();
                                    }
                                } else {
                                    mView.setStatisticsResultEmpty();
                                }
                            } else {
                                if (null != sellBookStatisticsVo.data) {
                                    if (sellBookStatisticsVo.data.errorCode == ERROR_CODE_KICK_OUT) {
                                        mView.noPermissionPrompt(R.string.kicked_offline);
                                    } else if (sellBookStatisticsVo.data.errorCode == ERROR_CODE_1006) {
                                        mView.noPermissionPrompt(R.string.operate_timeout);
                                    } else {
                                        mView.setStatisticsResultError(pageNumber == 1);
                                    }
                                } else {
                                    mView.setStatisticsResultError(pageNumber == 1);
                                }
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 免单统计
     */
    private void getPenaltyFreeApplys(ArrayMap<String, String> map, final int pageNumber) {
        Subscription subscription = DataRepository.getInstance().getPenaltyFreeApplys(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PenaltyFreeStatisticsVo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.setStatisticsResultError(pageNumber == 1);
                        }
                    }

                    @Override
                    public void onNext(PenaltyFreeStatisticsVo penaltyFreeStatisticsVo) {
                        if (null != mView) {
                            if (penaltyFreeStatisticsVo.status == CODE_SUCCESS) {
                                if (null != penaltyFreeStatisticsVo.data) {
                                    mView.setTotalInfo("合计数量 " + penaltyFreeStatisticsVo.data.totalCount, null);
                                    List<List<StatisticsItem>> statisticsResult = new ArrayList<>();
                                    List<StatisticsItem> statisticsResultColumnTitle = new ArrayList<>();
                                    for (int i = 0; i < penaltyFreeStatisticsVo.data.resultList.size(); i++) {
                                        PenaltyFreeStatisticsInfoVo info = penaltyFreeStatisticsVo.data.resultList.get(i);
                                        List<StatisticsItem> statisticsResultRow = new ArrayList<>();

                                        //日期（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("日期",
                                                    widthDp93,
                                                    Gravity.CENTER));
                                        }
                                        String date = DateUtils.formatDate(info.applyTime);
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(date) ? "" : date,
                                                widthDp93,
                                                Gravity.CENTER));

                                        //单号（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("单号",
                                                    widthDp110,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(info.operCode) ? "" : info.operCode,
                                                widthDp110,
                                                Gravity.CENTER));

                                        //读者（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("读者",
                                                    widthDp110,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(info.readerName) ? "" : info.readerName,
                                                widthDp110,
                                                Gravity.CENTER));

                                        //身份证号（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("身份证号",
                                                    widthDp185,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(info.idCard) ? "" : info.idCard,
                                                widthDp185,
                                                Gravity.CENTER));

                                        //金额（右对齐）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("金额",
                                                    widthDp85,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(MoneyUtils.formatMoney(info.amount),
                                                widthDp85,
                                                Gravity.CENTER | Gravity.END));

                                        //状态（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("状态",
                                                    widthDp95,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(info.statusDesc) ? "" : info.statusDesc,
                                                widthDp95,
                                                Gravity.CENTER));

                                        //驳回理由（居左）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("驳回理由",
                                                    widthDp185,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItemTwoLines(TextUtils.isEmpty(info.auditRemark) ? "" : info.auditRemark,
                                                widthDp185,
                                                Gravity.CENTER | Gravity.START));

                                        //操作员（居中）
                                        if (i == 0) {
                                            statisticsResultColumnTitle.add(createStatisticsItem("操作员",
                                                    widthDp110,
                                                    Gravity.CENTER));
                                        }
                                        statisticsResultRow.add(createStatisticsItem(TextUtils.isEmpty(info.applyName) ? "" : info.applyName,
                                                widthDp110,
                                                Gravity.CENTER));

                                        statisticsResult.add(statisticsResultRow);
                                    }

                                    if (statisticsResult.size() > 0) {
                                        if (pageNumber == 1) {
                                            mView.setStatisticsResultColumnTitle(statisticsResultColumnTitle);
                                        }
                                        mView.setStatisticsResultList(statisticsResult, penaltyFreeStatisticsVo.data.totalCount, pageNumber == 1);
                                    } else {
                                        mView.setStatisticsResultEmpty();
                                    }
                                } else {
                                    mView.setStatisticsResultEmpty();
                                }
                            } else {
                                if (null != penaltyFreeStatisticsVo.data) {
                                    if (penaltyFreeStatisticsVo.data.errorCode == ERROR_CODE_KICK_OUT) {
                                        mView.noPermissionPrompt(R.string.kicked_offline);
                                    } else if (penaltyFreeStatisticsVo.data.errorCode == ERROR_CODE_1006) {
                                        mView.noPermissionPrompt(R.string.operate_timeout);
                                    } else {
                                        mView.setStatisticsResultError(pageNumber == 1);
                                    }
                                } else {
                                    mView.setStatisticsResultError(pageNumber == 1);
                                }
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private StatisticsItem createStatisticsItem(String content, int width, int gravity) {
        StatisticsItem bean = new StatisticsItem();
        bean.mContent = content;
        bean.mWidth = width;
        bean.mGravity = gravity;
        bean.mLines = 1;
        return bean;
    }

    private StatisticsItem createStatisticsItemTwoLines(CharSequence content, int width, int gravity) {
        StatisticsItem bean = new StatisticsItem();
        bean.mContentSequence = content;
        bean.mWidth = width;
        bean.mGravity = gravity;
        bean.mLines = 2;
        return bean;
    }

    private StatisticsItem createStatisticsItemTwoLines(String content, int width, int gravity) {
        StatisticsItem bean = new StatisticsItem();
        bean.mContent = content;
        bean.mWidth = width;
        bean.mGravity = gravity;
        bean.mLines = 2;
        return bean;
    }

}
