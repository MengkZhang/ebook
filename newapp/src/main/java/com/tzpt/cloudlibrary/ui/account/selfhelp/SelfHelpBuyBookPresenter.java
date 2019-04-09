package com.tzpt.cloudlibrary.ui.account.selfhelp;

import android.text.TextUtils;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.bean.SelfBookInfoBean;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseResultEntityVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.SelfBuyBookResultVo;
import com.tzpt.cloudlibrary.utils.MoneyUtils;

import org.json.JSONArray;

import java.util.ArrayList;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 自助购书
 * Created by tonyjia on 2018/8/14.
 */
public class SelfHelpBuyBookPresenter extends RxPresenter<SelfHelpBuyBookContract.View> implements
        SelfHelpBuyBookContract.Presenter {

    private String mStayLibraryHallCode = null;
    private ArrayList<SelfBookInfoBean> mBookList;

    /**
     * 获取本地书籍信息
     */
    @Override
    public void getLocalBookInfo() {
        //获取图书列表
        if (null != mBookList && mBookList.size() > 0) {
            mView.setBookList(mBookList);
            getTotalInfo();
        } else {
            //图书列表已清空
            mStayLibraryHallCode = null;
            mView.setEditStatus(true);
            mView.setBookEmpty();
        }
    }

    @Override
    public void getBookInfo(String barNumber) {
        if (hasRepetitionBooks(barNumber)) {
            mView.showDialogTips(R.string.scan_repeat_bar);
            return;
        }
        String userIdCard = UserRepository.getInstance().getLoginUserIdCard();
        if (!TextUtils.isEmpty(userIdCard)) {
            mView.showProgressDialog();
            Subscription subscription = DataRepository.getInstance().getSelfBuyBookInfo(barNumber, userIdCard, mStayLibraryHallCode)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<SelfBookInfoBean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (mView != null) {
                                mView.dismissProgressDialog();
                                if (e instanceof ApiException) {
                                    switch (((ApiException) e).getCode()) {
                                        //“购书扫描”失败的错误码和错误原因：
                                        case 500://条形码有误 !
                                        case 30900://条形码有误 !
                                            mView.showDialogTips(R.string.bar_code_not_exist);
                                            break;
                                        case 30901://本书不外售 !
                                            mView.showDialogTips(R.string.this_book_not_sell);
                                            break;
                                        case 30902://本书已预约 !
                                            mView.showDialogTips(R.string.have_make_appointment);
                                            break;
                                        case 30903://本书已售 !
                                            mView.showDialogTips(R.string.this_book_already_sell);
                                            break;
                                        case 30905://在借
                                            mView.showDialogTips(R.string.book_already_borrowed);
                                            break;
                                        case 30906://丢失
                                            mView.showDialogTips(R.string.have_lost);
                                            break;
                                        case 30907://剔旧
                                            mView.showDialogTips(R.string.have_stuck_between_old);
                                            break;
                                        case 30908://其它
                                            mView.showDialogTips(R.string.this_book_not_sell);
                                            break;
                                        case 30909://盘亏
                                            mView.showDialogTips(R.string.have_dish_deficient);
                                            break;
                                        case 30910://流出
                                            mView.showDialogTips(R.string.has_been_out_locked);
                                            break;
                                        case 30911://该馆已停用
                                            mView.showDialogTips(R.string.library_is_stop);
                                            break;
                                        case 30912://非本馆书籍
                                            mView.showDialogTips(R.string.not_library_book);
                                            break;
                                        case 30913://已流出
                                            mView.showDialogTips(R.string.has_been_out);
                                            break;
                                        case 30914://条形码不存在
                                            mView.showDialogTips(R.string.bar_code_not_exist);
                                            break;
                                        case 30100://踢下线
                                            mView.pleaseLoginTip();
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

                        @Override
                        public void onNext(SelfBookInfoBean selfBookInfoBean) {
                            if (mView != null) {
                                mView.dismissProgressDialog();
                                if (selfBookInfoBean != null) {
                                    if (mStayLibraryHallCode == null) {
                                        mStayLibraryHallCode = selfBookInfoBean.stayLibraryHallCode;
                                    }
                                    if (mBookList == null) {
                                        mBookList = new ArrayList<>();
                                    }
                                    mBookList.add(0, selfBookInfoBean);
                                    getLocalBookInfo();
                                } else {
                                    mView.showDialogTips(R.string.network_fault);
                                }

                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }

    /**
     * 提交用户密码，金额,购书书籍信息
     *
     * @param totalPrice 金额
     * @param readerPsw  读者密码
     */
    @Override
    public void confirmReaderPsw(String totalPrice, String readerPsw) {
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (!TextUtils.isEmpty(readerId)) {
            if (null != mBookList && mBookList.size() > 0) {
                JSONArray bookIds = new JSONArray();
                for (int i = 0; i < mBookList.size(); i++) {
                    SelfBookInfoBean item = mBookList.get(i);
                    bookIds.put(Long.valueOf(item.id));
                }
                mView.showProgressDialog();
                Subscription subscription = DataRepository.getInstance().selfBuyBook(readerId, bookIds, totalPrice, readerPsw)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<BaseResultEntityVo<SelfBuyBookResultVo>>() {
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
                            public void onNext(BaseResultEntityVo<SelfBuyBookResultVo> baseDataResultVo) {
                                if (null != mView) {
                                    mView.dismissProgressDialog();
                                    if (baseDataResultVo.status == 200) {
                                        mView.selfBuyBookSuccess(mBookList.size());
                                    } else {
                                        switch (baseDataResultVo.data.errorCode) {
                                            case 9004:
                                                mView.showDialogTips(R.string.password_error);
                                                break;
                                            case 9006:
                                                mView.showDialogTips(R.string.network_fault);
                                                break;
                                            case 9002:
                                            case 9003:
                                                mView.showDialogTips(R.string.this_book_not_sell);
                                                break;
                                            case 9007:
                                                mView.showChargeDialogTips(R.string.charge_deposit);
                                                break;
                                            case 9001://购物失败，显示购书失败原因
                                                //mView.showDialogTips(R.string.self_buy_book_fail);
                                                if (null != baseDataResultVo.data.errorData && baseDataResultVo.data.errorData.size() > 0) {
                                                    for (SelfBookInfoBean item : mBookList) {
                                                        for (SelfBuyBookResultVo.ErrorBookIdVo bookIdVo : baseDataResultVo.data.errorData) {
                                                            if (item.id == bookIdVo.libraryBookId) {
                                                                item.statusSuccess = false;
                                                                switch (bookIdVo.code) {
                                                                    case 2208:
                                                                        item.statusDesc = "本书已办借!";
                                                                        break;
                                                                    case 2209:
                                                                        item.statusDesc = "本书已亏盘!";
                                                                        break;
                                                                    case 2210:
                                                                        item.statusDesc = "本书流出锁定!";
                                                                        break;
                                                                    case 2211:
                                                                        item.statusDesc = "本书已赔书!";
                                                                        break;
                                                                    case 2212:
                                                                        item.statusDesc = "本书已剔旧!";
                                                                        break;
                                                                    case 2214:
                                                                        item.statusDesc = "本书已被预约!";
                                                                        break;
                                                                    case 2218:
                                                                        item.statusDesc = "本书已售出!";
                                                                        break;
                                                                    case 9005:
                                                                        item.statusDesc = "本书不外售! ";
                                                                        break;
                                                                    default:
                                                                        item.statusDesc = "";
                                                                        break;
                                                                }
                                                            }
                                                        }
                                                    }
                                                    mView.setEditStatus(false);
                                                    //重新获取本地书籍信息
                                                    getLocalBookInfo();
                                                }
                                                break;
                                            case 30100://踢下线
//                                                DataRepository.getInstance().quit();
                                                UserRepository.getInstance().logout();
                                                mView.pleaseLoginTip();
                                                break;
                                            default:
                                                mView.showDialogTips(R.string.self_buy_book_fail);
                                                break;
                                        }
                                    }
                                }
                            }
                        });
                addSubscrebe(subscription);
            }
        }
    }

    @Override
    public void removeDataByIndex(int position) {
        if (null != mBookList && position < mBookList.size()) {
            mBookList.remove(position);
        }
        getLocalBookInfo();
    }

    /**
     * 清空临时图书合集
     */
    @Override
    public void clearTempBook() {
//        DataRepository.getInstance().clearSelfBookList();
    }

    @Override
    public void getBookInfoTurnToScan() {
        if (null != mBookList && mBookList.size() > 0) {
            double totalPrice = 0.00;
            for (SelfBookInfoBean info : mBookList) {
                if (info.discountPrice > 0) {
                    totalPrice += info.discountPrice;
                } else {
                    totalPrice += (info.price + info.attachPrice);
                }
            }
            mView.turnToScan(mBookList.size(), totalPrice);
        } else {
            mView.turnToScan(0, 0.00);
        }
    }

    /**
     * 获取合计信息
     */
    private void getTotalInfo() {
//        List<SelfBookInfoBean> bookList = DataRepository.getInstance().getSelfBookList();
        if (null != mBookList && mBookList.size() > 0) {
            double totalPrice = 0.00;
            for (SelfBookInfoBean info : mBookList) {
                if (info.discountPrice > 0) {
                    totalPrice += info.discountPrice;
                } else {
                    totalPrice += (info.price + info.attachPrice);
                }
            }
            mView.showBookTotalInfo("数量 " + mBookList.size(), "金额 " + MoneyUtils.formatMoney(totalPrice), totalPrice);
        } else {
            mView.showBookTotalInfo("数量 0", "金额 0.00", 0.00);
        }
    }

    /**
     * 检查是否有重复书籍
     */
    private boolean hasRepetitionBooks(String libraryBarCode) {
//        List<SelfBookInfoBean> bookList = DataRepository.getInstance().getSelfBookList();
        if (mBookList == null || mBookList.size() == 0) {
            return false;
        }
        //本系统书籍
        if (libraryBarCode.contains("-")) {
            String libraryCode = libraryBarCode.split("-")[0];
            String libCode = libraryBarCode.split("-")[1];
            for (int i = 0; i < mBookList.size(); i++) {
                String belongLibraryHallCode = mBookList.get(i).belongLibraryHallCode;
                String barNumber = mBookList.get(i).barNumber;
                if (null != belongLibraryHallCode && null != barNumber) {
                    if (libraryCode.equals(belongLibraryHallCode) && libCode.equals(barNumber)) {
                        return true;
                    }
                }
            }
        } else {//图创图书
            for (int i = 0; i < mBookList.size(); i++) {
                String barNumber = mBookList.get(i).barNumber;
                if (null != barNumber && libraryBarCode.equals(barNumber)) {
                    return true;
                }
            }
        }
        return false;
    }

}
