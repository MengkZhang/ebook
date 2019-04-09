package com.tzpt.cloudlibrary.ui.main;

import android.text.TextUtils;

import com.tzpt.cloudlibrary.CloudLibraryApplication;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.business_bean.UserInfoBean;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.local.SharedPreferencesUtil;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;
import com.tzpt.cloudlibrary.utils.DateUtils;

import java.util.Arrays;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 用户信息
 * Created by ZhiqiangJia on 2017-08-18.
 */
public class UserPresenter extends RxPresenter<UserContract.View> implements UserContract.Presenter {

    private static final String NOTICE_BORROW_OVERDUE_TIME = "notice_borrow_overdue_time";

    UserPresenter() {
        UserRepository.getInstance().registerRxBus(UserInfoBean.class, new Action1<UserInfoBean>() {
            @Override
            public void call(UserInfoBean userInfoBean) {
                if (mView != null) {
                    //判断是否存在即将逾期，如果存在，则判断今天是否提示过,提示用户的即将逾期消息
                    if (userInfoBean.mIsBorrowOverdue && userInfoBean.mUpcomingOverdueCount > 0) {
                        String nowDate = DateUtils.formatNowDate(System.currentTimeMillis());
                        String noticeDate = SharedPreferencesUtil.getInstance().getString(NOTICE_BORROW_OVERDUE_TIME);
                        if (null == noticeDate || !noticeDate.equals(nowDate)) {
                            SharedPreferencesUtil.getInstance().putString(NOTICE_BORROW_OVERDUE_TIME, nowDate);
                            mView.setUserBorrowOverdueMsg(userInfoBean.mUpcomingOverdueCount);
                        }
                    }

                    setUserInfo();

                    mView.setUserUnreadMsgCount(userInfoBean.mUnreadMsgCount + userInfoBean.mUnreadOverdueMsgCount);
                    mView.setUserBorrowOverdueSum(userInfoBean.mOverdueUnReadSum);

                }
            }
        });
    }

    @Override
    public boolean isLogin() {
        return UserRepository.getInstance().isLogin();
    }

    @Override
    public void getUserInfo() {
//        String idCard = DataRepository.getInstance().getLoginUserIdCard();
//        String readerId = DataRepository.getInstance().getLoginReaderId();
//        if (TextUtils.isEmpty(idCard) || TextUtils.isEmpty(readerId)) {
//            return;
//        }
        if (!UserRepository.getInstance().isLogin()) {
            return;
        }
        //设置用户信息
        setUserInfo();
        UserRepository.getInstance().refreshUserInfo();
//        Subscription subscription = UserRepository.getInstance().getUserInfo(true)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<UserInfoBean>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        if (mView != null) {
//                            if (e instanceof ApiException) {
//                                switch (((ApiException) e).getCode()) {
//                                    case 30100:
//                                        UserRepository.getInstance().logout();
//                                        mView.pleaseLoginTip();
//                                        break;
//                                    default:
//                                        mView.setNetError();
//                                        break;
//                                }
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onNext(UserInfoBean userInfoBean) {
//                        if (mView != null) {
//                            //判断是否存在即将逾期，如果存在，则判断今天是否提示过,提示用户的即将逾期消息
//                            if (userInfoBean.mIsBorrowOverdue && userInfoBean.mUpcomingOverdueCount > 0) {
//                                String nowDate = DateUtils.formatNowDate(System.currentTimeMillis());
//                                String noticeDate = SharedPreferencesUtil.getInstance().getString(NOTICE_BORROW_OVERDUE_TIME);
//                                if (null == noticeDate || !noticeDate.equals(nowDate)) {
//                                    SharedPreferencesUtil.getInstance().putString(NOTICE_BORROW_OVERDUE_TIME, nowDate);
//                                    mView.setUserBorrowOverdueMsg(userInfoBean.mUpcomingOverdueCount);
//                                }
//                            }
//
//                            setUserInfo();
//
//                            mView.setUserUnreadMsgCount(userInfoBean.mUnreadMsgCount + userInfoBean.mUnreadOverdueMsgCount);
//                            mView.setUserBorrowOverdueSum(userInfoBean.mOverdueUnReadSum);
//
////                            DataRepository.getInstance().modifyUserHeadImg(userInfo.mHeadImg);
//                        }
//                    }
//                });
//        addSubscrebe(subscription);
//        // 如果上一次接口正在请求，则取消上次接口返回的处理，重新获取接口
//        if (null != mSubscription && !mSubscription.isUnsubscribed()) {
//            mSubscription.unsubscribe();
//        }
//        Observable<BaseResultEntityVo<UserInfoVo>> observable1 = DataRepository.getInstance().getUserInfo(idCard);
//        Observable<BaseResultEntityVo<UnreadMsgCountVo>> observable2 = DataRepository.getInstance().getUnreadMsgCount(Integer.valueOf(readerId));
//        Observable<BaseResultEntityVo<UnreadOverdueMsgCountVo>> observable3 = DataRepository.getInstance().getUnreadOverdueMsgCount(idCard);
//        mSubscription = Observable.zip(observable1, observable2, observable3, new Func3<BaseResultEntityVo<UserInfoVo>, BaseResultEntityVo<UnreadMsgCountVo>, BaseResultEntityVo<UnreadOverdueMsgCountVo>, UserInfoMoreVo>() {
//            @Override
//            public UserInfoMoreVo call(BaseResultEntityVo<UserInfoVo> o1, BaseResultEntityVo<UnreadMsgCountVo> o2, BaseResultEntityVo<UnreadOverdueMsgCountVo> o3) {
//                return new UserInfoMoreVo(o1, o2, o3);
//            }
//        })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<UserInfoMoreVo>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        if (null != mView) {
//                            mView.setNetError();
//                        }
//                    }
//
//                    @Override
//                    public void onNext(UserInfoMoreVo userInfoMoreVo) {
//                        if (mView != null) {
//                            if (userInfoMoreVo.userInfo != null) {
//                                if (userInfoMoreVo.userInfo.status == 200
//                                        && userInfoMoreVo.userInfo.data != null) {
//
//                                    UserInfoBean userInfo = DataRepository.getInstance().getLoginInfo();
//                                    if (userInfo != null) {
//                                        userInfo.mActionCount = userInfoMoreVo.userInfo.data.activityCount;
//                                        userInfo.mAppointCount = userInfoMoreVo.userInfo.data.appointCount;
//                                        userInfo.mBorrowOverdueSum = userInfoMoreVo.userInfo.data.borrowOverdueSum;
//                                        userInfo.mOverdueUnReadSum = userInfoMoreVo.userInfo.data.borrowOverdueUnReadSum;
//                                        userInfo.mBorrowSum = userInfoMoreVo.userInfo.data.borrowSum;
//                                        userInfo.mBorrowTotal = userInfoMoreVo.userInfo.data.totalBorrowSum;
//                                        userInfo.mNoteCount = userInfoMoreVo.userInfo.data.noteCount;
//                                        userInfo.mIsBorrowOverdue = userInfoMoreVo.userInfo.data.borrowOverdueIsExist == 1;
//                                        if (userInfoMoreVo.unReadMsgCount != null
//                                                && userInfoMoreVo.unReadMsgCount.status == 200
//                                                && userInfoMoreVo.unReadMsgCount.data != null) {
//                                            userInfo.mUnreadMsgCount = userInfoMoreVo.unReadMsgCount.data.unreadCount;
//                                        }
//
//                                        if (userInfoMoreVo.unReadOverdueMsgCount != null
//                                                && userInfoMoreVo.unReadOverdueMsgCount.status == 200
//                                                && userInfoMoreVo.unReadOverdueMsgCount.data != null) {
//                                            userInfo.mUnreadOverdueMsgCount = userInfoMoreVo.unReadOverdueMsgCount.data.messageCount;
//                                        }
//
//                                        if (userInfoMoreVo.userInfo != null
//                                                && userInfoMoreVo.userInfo.data != null
//                                                && userInfoMoreVo.userInfo.data.userInfo != null) {
//                                            userInfo.mNickName = userInfoMoreVo.userInfo.data.userInfo.nickName;
//                                            userInfo.mPhone = userInfoMoreVo.userInfo.data.userInfo.phone;
//                                            //设置用户头像
//                                            if (!TextUtils.isEmpty(userInfoMoreVo.userInfo.data.userInfo.image)) {
//                                                String headImage = ImageUrlUtils.getDownloadOriginalImagePath(userInfoMoreVo.userInfo.data.userInfo.image);
//                                                if (userInfo.mHeadImg != null && !userInfo.mHeadImg.equals(headImage)) {
//                                                    userInfo.mHeadImg = headImage;
//                                                    mView.setUserHeadImage(headImage, DataRepository.getInstance().isMan());
//                                                }
//                                            }
//
//                                            //判断是否存在即将逾期，如果存在，则判断今天是否提示过,提示用户的即将逾期消息
//                                            if (userInfo.mIsBorrowOverdue && userInfoMoreVo.userInfo.data.upcomingOverdueBookNumber > 0) {
//                                                String nowDate = DateUtils.formatNowDate(System.currentTimeMillis());
//                                                String noticeDate = SharedPreferencesUtil.getInstance().getString(NOTICE_BORROW_OVERDUE_TIME);
//                                                if (null == noticeDate || !noticeDate.equals(nowDate)) {
//                                                    SharedPreferencesUtil.getInstance().putString(NOTICE_BORROW_OVERDUE_TIME, nowDate);
//                                                    mView.setUserBorrowOverdueMsg(userInfoMoreVo.userInfo.data.upcomingOverdueBookNumber);
//                                                }
//                                            }
//                                        }
//
//                                        mView.setUserHeadImage(userInfo.mHeadImg, DataRepository.getInstance().isMan());
//
//                                        DataRepository.getInstance().modifyUserHeadImg(userInfo.mHeadImg);
//
//
//                                        mView.setUserPhone(userInfo.mPhone);
//                                        mView.setUserUnreadMsgCount(userInfo.mUnreadMsgCount + userInfo.mUnreadOverdueMsgCount);
//                                        mView.setUserBorrowOverdueSum(userInfo.mOverdueUnReadSum);
//
//                                        mView.setRefreshUserInfo();
//
//
//                                    }
//                                } else if (userInfoMoreVo.userInfo.status == 401) {
//                                    if (userInfoMoreVo.userInfo.data.errorCode == 30100) {//未登录
//                                        DataRepository.getInstance().quit();
//                                        mView.pleaseLoginTip();
//                                    } else {
//                                        mView.setNetError();
//                                    }
//                                } else {
//                                    mView.setNetError();
//                                }
//
//                            }
//                        }
//                    }
//                });
//        addSubscrebe(mSubscription);
    }

    @Override
    public void getLocalUserInfo() {
        if (!UserRepository.getInstance().isLogin()) {
            return;
        }
        setUserInfo();
        //获取互动总数量
        int unreadMsgCount = UserRepository.getInstance().getInteractionMsgCount();
        mView.setUserUnreadMsgCount(unreadMsgCount);
        //获取逾期未读数量
        int borrowOverdueSum = UserRepository.getInstance().getUserBorrowOverdueUnReadSum();
        mView.setUserBorrowOverdueSum(borrowOverdueSum);
    }

    //设置用户信息
    private void setUserInfo() {
        String cardName = UserRepository.getInstance().getLoginCardName();
        String nickName = UserRepository.getInstance().getLoginNickName();
        String headImg = UserRepository.getInstance().getLoginHeadImage();
        String phone = UserRepository.getInstance().getLoginPhone();
        boolean isMan = UserRepository.getInstance().isMan();
        //设置昵称
        mView.setUserNickName(!TextUtils.isEmpty(nickName) ? nickName : formatCardName(cardName, isMan));
        mView.setUserHeadImage(headImg, isMan);
        mView.setUserPhone(phone);
        mView.setRefreshUserInfo();
    }

    @Override
    public void quit() {
//        DataRepository.getInstance().quit();
        UserRepository.getInstance().logout();
    }

    private String formatCardName(String cardName, boolean isMan) {
        if (TextUtils.isEmpty(cardName)) {
            cardName = "用户名";
        } else {
            if (cardName.length() >= 2) {
                //如果有复姓
                List<String> surnameList = Arrays.asList(CloudLibraryApplication.getAppContext().getResources().getStringArray(R.array.compound_surnames));
                String lastName = cardName.substring(0, 2);
                if (surnameList.contains(lastName)) {
                    cardName = lastName + (isMan ? "先生" : "女士");
                } else {
                    cardName = setOnLastName(cardName, isMan);
                }
            } else {
                cardName = setOnLastName(cardName, isMan);
            }
        }
        return cardName;
    }


    /**
     * 设置单姓
     *
     * @param userName 名称
     * @param isMan    身份证号
     * @return 单姓称谓
     */
    private String setOnLastName(String userName, boolean isMan) {
        //获取姓氏及性别,设置先生女士-暂时取单字
        String newName = userName.substring(0, 1);
        return newName + (isMan ? "先生" : "女士");
    }

    @Override
    public void detachView() {
        super.detachView();
        UserRepository.getInstance().unregisterRxBus();
    }
}
