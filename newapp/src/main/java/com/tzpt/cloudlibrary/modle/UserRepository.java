package com.tzpt.cloudlibrary.modle;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.tzpt.cloudlibrary.CloudLibraryApplication;
import com.tzpt.cloudlibrary.base.data.User;
import com.tzpt.cloudlibrary.bean.UserHeadBean;
import com.tzpt.cloudlibrary.business_bean.UserInfoBean;
import com.tzpt.cloudlibrary.modle.local.SPKeyConstant;
import com.tzpt.cloudlibrary.modle.local.SharedPreferencesUtil;
import com.tzpt.cloudlibrary.modle.remote.CloudLibraryApi;
import com.tzpt.cloudlibrary.modle.remote.exception.ExceptionEngine;
import com.tzpt.cloudlibrary.modle.remote.exception.ServerException;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BarCodeReusltVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseDataResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseResultEntityVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.FaceImageVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.LoginInfoVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.PreUserInfoVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.UnreadMsgCountVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.UnreadOverdueMsgCountVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.UserHeadListItemVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.UserHeadListResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.UserInfoVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.VerifyCodeVo;
import com.tzpt.cloudlibrary.rxbus.RxBus;
import com.tzpt.cloudlibrary.utils.ImageUrlUtils;
import com.tzpt.cloudlibrary.utils.MD5Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/11/15.
 */

public class UserRepository {
    private static UserRepository mInstance;

    private User mUser;
    private UserInfoBean mUserInfo;
    private String mToken;

//    private List<String> mReservationBookIdList = new ArrayList<>();

    private RxBus mRxBus;

    private Subscription mUserInfoSub;

    private UserRepository() {
        mRxBus = CloudLibraryApplication.mRxBus;
        getUser();
    }

    public static UserRepository getInstance() {
        if (mInstance == null) {
            mInstance = new UserRepository();
        }
        return mInstance;
    }

    public static void initLoginUser() {
        if (mInstance == null) {
            mInstance = new UserRepository();
        }
    }

    public <T> void registerRxBus(Class<T> eventType, Action1<T> action) {
        Subscription subscription = mRxBus.doSubscribe(eventType, action, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {

            }
        });
        mRxBus.addSubscription(this, subscription);
    }

    public void unregisterRxBus() {
        mRxBus.unSubscribe(this);
    }

    private class HttpResultFunc<T> implements Func1<Throwable, Observable<T>> {
        @Override
        public Observable<T> call(Throwable throwable) {
            return Observable.error(ExceptionEngine.handleException(throwable));
        }
    }

    private void saveUser() {
        if (mUser != null) {
            SharedPreferencesUtil.getInstance().putObject(SPKeyConstant.LOGIN_USER, mUser);
        }
    }

    private void getUser() {
        mUser = SharedPreferencesUtil.getInstance().getObject(SPKeyConstant.LOGIN_USER, User.class);
        if (mUser != null) {
            mToken = SharedPreferencesUtil.getInstance().getString("TOKEN");
        }
    }

    private void saveToken(String token) {
        SharedPreferencesUtil.getInstance().putString("TOKEN", token);
    }

    public String getToken() {
        return mToken;
    }

    /**
     * 当前是否登陆
     *
     * @return true 已登录; false 未登录;
     */
    public boolean isLogin() {
        return mUser != null && !TextUtils.isEmpty(mToken);
    }

    /**
     * 获取读者身份证
     *
     * @return 身份证号码
     */
    public String getLoginUserIdCard() {
        if (isLogin()) {
            return mUser.mIdCard;
        }
        return null;
    }

    /**
     * 获取身份证名字
     *
     * @return 姓名
     */
    public String getLoginCardName() {
        if (mUser != null) {
            return mUser.mCardName;
        }
        return null;
    }

    /**
     * 获取用户昵称
     *
     * @return 昵称
     */
    public String getLoginNickName() {
        if (mUser != null) {
            return mUser.mNickName;
        }
        return null;
    }

    /**
     * 获取用户头像
     *
     * @return 头像地址
     */
    public String getLoginHeadImage() {
        if (mUser != null) {
            return mUser.mHeadImg;
        }
        return null;
    }

    /**
     * 获取用户电话
     *
     * @return 电话
     */
    public String getLoginPhone() {
        if (mUser != null) {
            return mUser.mPhone;
        }
        return null;
    }

    /**
     * 获取用户性别
     *
     * @return true 男性; false 女性;
     */
    public boolean isMan() {
        return mUser != null && mUser.mIsMan;
    }

    /**
     * 获取读者id
     *
     * @return 登录读者ID
     */
    public String getLoginReaderId() {
        if (isLogin()) {
            return String.valueOf(mUser.mReaderId);
        }
        return null;
    }

    /**
     * 获取读者id
     *
     * @return 登录读者ID
     */
    public long getLoginReaderIdL() {
        if (isLogin()) {
            return mUser.mReaderId;
        }
        return 0;
    }


//
//    /**
//     * 获取本地消息总数量
//     * @return
//     */
//    public int getLocalMsgCount() {
//        if (isLogin() && mUserInfo != null) {
//            return mUserInfo.mOverdueUnReadSum + mUserInfo.mUnreadMsgCount + mUserInfo.mUnreadOverdueMsgCount;
//        }
//        return 0;
//    }

    /**
     * 互动消息的总数量
     *
     * @return 互动消息的总数量
     */
    public int getInteractionMsgCount() {
        if (mUserInfo != null) {
            return mUserInfo.mUnreadMsgCount + mUserInfo.mUnreadOverdueMsgCount;
        }
        return 0;
    }


    /**
     * 当前借阅逾期未读消息数量
     *
     * @return 当前借阅逾期未读消息数量
     */
    public int getUserBorrowOverdueUnReadSum() {
        if (mUserInfo != null) {
            return mUserInfo.mOverdueUnReadSum;
        }
        return 0;
    }


    /**
     * 减去一个逾期消息
     */
    public void readBorrowOverdueSum() {
        if (mUserInfo != null) {
            int borrowOverdueSum = mUserInfo.mBorrowOverdueSum - 1;
            mUserInfo.mBorrowOverdueSum = borrowOverdueSum < 0 ? 0 : borrowOverdueSum;
        }
    }

    /**
     * 清空逾期消息数量
     */
    public void clearBorrowOverdueSum() {
        if (mUserInfo != null) {
            mUserInfo.mOverdueUnReadSum = 0;
        }
    }

    /**
     * 我的报名数量
     *
     * @return 我的报名数量
     */
    public int getUserActionCount() {
        if (mUserInfo != null) {
            return mUserInfo.mActionCount;
        }
        return 0;
    }

    /**
     * 获取未读消息数量
     *
     * @return 获取未读消息数量
     */
    public int getUserUnreadMsgCount() {
        if (mUserInfo != null) {
            return mUserInfo.mUnreadMsgCount;
        }
        return 0;
    }

    /**
     * 未读消息数量
     */
    public void readNormalMsg() {
        if (null != mUserInfo) {
            mUserInfo.mUnreadMsgCount = 0;
        }
    }

    /**
     * 获取未读逾期消息数量
     *
     * @return 获取未读逾期消息数量
     */
    public int getUserUnreadOverdueMsgCount() {
        if (mUserInfo != null) {
            return mUserInfo.mUnreadOverdueMsgCount;
        }
        return 0;
    }

    /**
     * 读取读者逾期消息数量
     */
    public void readOverdueMsg() {
        if (null != mUserInfo) {
            mUserInfo.mUnreadOverdueMsgCount = mUserInfo.mUnreadOverdueMsgCount - 1;
        }
    }

    /**
     * 获取本地消息总数量
     * @return
     */
    public int getLocalMsgCount() {
        if (isLogin() && mUserInfo != null) {
            return mUserInfo.mOverdueUnReadSum + mUserInfo.mUnreadMsgCount + mUserInfo.mUnreadOverdueMsgCount;
        }
        return 0;
    }

    public void readBorrowSum() {
        if (mUserInfo != null) {
            int borrowSum = mUserInfo.mBorrowSum - 1;
            mUserInfo.mBorrowSum = borrowSum < 0 ? 0 : borrowSum;
        }
    }

    //当前预约数量
    public int getUserAppointCount() {
        if (mUserInfo != null) {
            return mUserInfo.mAppointCount;
        }
        return 0;
    }

    //当前借阅数量
    public int getUserBorrowSum() {
        if (mUserInfo != null) {
            return mUserInfo.mBorrowSum;
        }
        return 0;
    }

    //当前借阅逾期数量
    public int getUserBorrowOverdueSum() {
        if (mUserInfo != null) {
            return mUserInfo.mBorrowOverdueSum;
        }
        return 0;
    }

    //用户购书架总数
    public int getUserBuyBookShelfSum() {
        if (mUserInfo != null) {
            return mUserInfo.mBuyBookShelfSum;
        }
        return 0;
    }

    //用户收藏的电子书
    public int getUserCollectionEBookSum() {
        if (mUserInfo != null) {
            return mUserInfo.mCollectionEBookSum;
        }
        return 0;
    }

    //用户收藏的视频
    public int getUserCollectionVideoSum() {
        if (mUserInfo != null) {
            return mUserInfo.mCollectionVideoSum;
        }
        return 0;
    }

    /**
     * 读者登录接口
     *
     * @param phone    手机号
     * @param idCard   身份证
     * @param password 密码
     * @return 读者
     */
    public Observable<UserInfoBean> login(@Nullable String phone, String idCard, String password) {
        return CloudLibraryApi.getInstance().login(phone, idCard, MD5Utils.MD5(password))
                .map(new Func1<BaseResultEntityVo<LoginInfoVo>, UserInfoBean>() {
                    @Override
                    public UserInfoBean call(BaseResultEntityVo<LoginInfoVo> loginInfoVoBaseResultEntityVo) {
                        if (loginInfoVoBaseResultEntityVo.status == 200) {
                            if (mUser == null) {
                                mUser = new User();
                            }
                            mUser.mReaderId = Long.valueOf(loginInfoVoBaseResultEntityVo.data.readerId);//TODO 后续修改
                            mUser.mHeadImg = ImageUrlUtils.getDownloadOriginalImagePath(loginInfoVoBaseResultEntityVo.data.image);
                            mUser.mIsMan = loginInfoVoBaseResultEntityVo.data.gender == 1;
                            mUser.mPhone = loginInfoVoBaseResultEntityVo.data.phone;
                            mUser.mCardName = loginInfoVoBaseResultEntityVo.data.cardName;
                            mUser.mIdCard = loginInfoVoBaseResultEntityVo.data.idCard;
                            mUser.mNickName = loginInfoVoBaseResultEntityVo.data.nickName;

                            UserInfoBean userInfo = new UserInfoBean();
                            userInfo.mUser = mUser;
                            userInfo.mAttentionLibCode = loginInfoVoBaseResultEntityVo.data.libCode;
                            userInfo.mAttentionLibId = loginInfoVoBaseResultEntityVo.data.libId;
                            userInfo.mAttentionLibName = loginInfoVoBaseResultEntityVo.data.libName;

                            mToken = loginInfoVoBaseResultEntityVo.data.token;

                            //TODO 缓存关注的馆
                            LibraryRepository.getInstance().saveAttentionLib(userInfo.mAttentionLibCode, userInfo.mAttentionLibName);
                            //TODO 缓存登录读者
                            saveUser();
                            //TODO 缓存登录TOKEN
                            saveToken(mToken);

                            return userInfo;
                        } else {
                            throw new ServerException(loginInfoVoBaseResultEntityVo.data.errorCode,
                                    loginInfoVoBaseResultEntityVo.data.message);
                        }
                    }
                })
                .onErrorResumeNext(new HttpResultFunc<UserInfoBean>());
    }

    /**
     * 发送验证码
     *
     * @param phone 电话号码
     * @return 是否成功
     */
    public Observable<Boolean> sendPhoneVerifyCode(String phone) {
        return CloudLibraryApi.getInstance().sendPhoneVerifyCode(phone)
                .map(new Func1<BaseResultEntityVo<BaseDataResultVo>, Boolean>() {
                    @Override
                    public Boolean call(BaseResultEntityVo<BaseDataResultVo> baseDataResultVoBaseResultEntityVo) {
                        if (baseDataResultVoBaseResultEntityVo.status == 200) {
                            return true;
                        } else {
                            throw new ServerException(baseDataResultVoBaseResultEntityVo.data.errorCode,
                                    baseDataResultVoBaseResultEntityVo.data.message);
                        }
                    }
                })
                .onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    /**
     * 检查验证码
     *
     * @param phone 手机号
     * @param code  验证码
     * @return 是否成功
     */
    public Observable<Boolean> checkMsgCode(String phone, String code) {
        return CloudLibraryApi.getInstance().checkMsgCode(phone, code)
                .map(new Func1<BaseResultEntityVo<BaseDataResultVo>, Boolean>() {
                    @Override
                    public Boolean call(BaseResultEntityVo<BaseDataResultVo> baseDataResultVoBaseResultEntityVo) {
                        if (baseDataResultVoBaseResultEntityVo.status == 200) {
                            return true;
                        } else {
                            throw new ServerException(baseDataResultVoBaseResultEntityVo.data.errorCode,
                                    baseDataResultVoBaseResultEntityVo.data.message);
                        }
                    }
                })
                .onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    public Observable<Boolean> getVerifyCode(String phone) {
        return CloudLibraryApi.getInstance().getVerifyCode(phone)
                .map(new Func1<BaseResultEntityVo<VerifyCodeVo>, Boolean>() {
                    @Override
                    public Boolean call(BaseResultEntityVo<VerifyCodeVo> verifyCodeVoBaseResultEntityVo) {
                        if (verifyCodeVoBaseResultEntityVo.status == 200) {
                            return verifyCodeVoBaseResultEntityVo.data.result == 1002;
                        } else {
                            if (verifyCodeVoBaseResultEntityVo.data.errorCode == 30100) {
                                logout();
                            }
                            throw new ServerException(verifyCodeVoBaseResultEntityVo.data.errorCode,
                                    verifyCodeVoBaseResultEntityVo.data.message);
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    public Observable<Boolean> checkVerifyCode(String code, String idCard, final String phone) {
        return CloudLibraryApi.getInstance().checkVerifyCode(code, idCard, phone)
                .map(new Func1<BaseResultEntityVo<VerifyCodeVo>, Boolean>() {
                    @Override
                    public Boolean call(BaseResultEntityVo<VerifyCodeVo> verifyCodeVoBaseResultEntityVo) {
                        if (verifyCodeVoBaseResultEntityVo.status == 200) {
                            if (mUser != null) {
                                mUser.mPhone = phone;
                                SharedPreferencesUtil.getInstance().putObject(SPKeyConstant.LOGIN_USER, mUser);
                            }
                            return true;
                        } else {
                            if (verifyCodeVoBaseResultEntityVo.data.errorCode == 30100) {
                                logout();
                            }
                            throw new ServerException(verifyCodeVoBaseResultEntityVo.data.errorCode,
                                    verifyCodeVoBaseResultEntityVo.data.message);
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    public Observable<String> preUserInfo(String phone) {
        return CloudLibraryApi.getInstance().preUserInfo(phone)
                .map(new Func1<BaseResultEntityVo<PreUserInfoVo>, String>() {
                    @Override
                    public String call(BaseResultEntityVo<PreUserInfoVo> preUserInfoVoBaseResultEntityVo) {
                        if (preUserInfoVoBaseResultEntityVo.status == 200) {
                            return preUserInfoVoBaseResultEntityVo.data.baseInfo.idCard;
                        } else {
                            if (preUserInfoVoBaseResultEntityVo.data.errorCode == 30100) {
                                logout();
                            }
                            throw new ServerException(preUserInfoVoBaseResultEntityVo.data.errorCode,
                                    preUserInfoVoBaseResultEntityVo.data.message);
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<String>());
    }

    /**
     * 检查用户信息
     *
     * @param idCard   身份证号码，可以为空
     * @param nickName 昵称
     * @return 是否合法
     */
    public Observable<Boolean> checkUserInfo(@Nullable String idCard, String nickName) {
        return CloudLibraryApi.getInstance().checkReaderIdInfo(idCard, nickName)
                .map(new Func1<BaseResultEntityVo<BaseDataResultVo>, Boolean>() {
                    @Override
                    public Boolean call(BaseResultEntityVo<BaseDataResultVo> baseDataResultVoBaseResultEntityVo) {
                        if (baseDataResultVoBaseResultEntityVo.status == 200) {
                            return true;
                        } else {
                            throw new ServerException(baseDataResultVoBaseResultEntityVo.data.errorCode,
                                    baseDataResultVoBaseResultEntityVo.data.message);
                        }
                    }
                })
                .onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    /**
     * 读者注册
     *
     * @param cardName   身份证名字
     * @param idCard     身份证号码
     * @param idPassword 密码
     * @param phone      电话号码
     * @param nickName   昵称
     * @return 是否注册成功
     */
    public Observable<Boolean> readerRegister(String cardName, String idCard, String idPassword, String phone, String nickName) {
        return CloudLibraryApi.getInstance().readerRegister(cardName, idCard, idPassword, phone, nickName)
                .map(new Func1<BaseResultEntityVo<BaseDataResultVo>, Boolean>() {
                    @Override
                    public Boolean call(BaseResultEntityVo<BaseDataResultVo> baseDataResultVoBaseResultEntityVo) {
                        if (baseDataResultVoBaseResultEntityVo.status == 200) {
                            return true;
                        } else {
                            throw new ServerException(baseDataResultVoBaseResultEntityVo.data.errorCode,
                                    baseDataResultVoBaseResultEntityVo.data.message);
                        }
                    }
                })
                .onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    /**
     * 刷新用户信息
     */
    public void refreshUserInfo() {
        if (mUser == null) {
            return;
        }
        if (mUserInfoSub != null && !mUserInfoSub.isUnsubscribed()) {
            return;
        }
        Observable<BaseResultEntityVo<UserInfoVo>> observable1 = CloudLibraryApi.getInstance().getUserInfo(mUser.mIdCard);
        Observable<BaseResultEntityVo<UnreadMsgCountVo>> observable2 = CloudLibraryApi.getInstance().getUnreadMsgCount((int) mUser.mReaderId);//TODO mUser.mReaderId
        Observable<BaseResultEntityVo<UnreadOverdueMsgCountVo>> observable3 = CloudLibraryApi.getInstance().getUnreadOverdueMsgCount(mUser.mIdCard);
        mUserInfoSub = Observable.zip(observable1, observable2, observable3,
                new Func3<BaseResultEntityVo<UserInfoVo>,
                        BaseResultEntityVo<UnreadMsgCountVo>,
                        BaseResultEntityVo<UnreadOverdueMsgCountVo>, Void>() {
                    @Override
                    public Void call(BaseResultEntityVo<UserInfoVo> userInfoVoBaseResultEntityVo,
                                     BaseResultEntityVo<UnreadMsgCountVo> unreadMsgCountVoBaseResultEntityVo,
                                     BaseResultEntityVo<UnreadOverdueMsgCountVo> unreadOverdueMsgCountVoBaseResultEntityVo) {
                        if (userInfoVoBaseResultEntityVo.status == 200
                                && unreadMsgCountVoBaseResultEntityVo.status == 200
                                && unreadOverdueMsgCountVoBaseResultEntityVo.status == 200) {
                            if (mUserInfo == null) {
                                mUserInfo = new UserInfoBean();
                            }

                            mUser.mReaderId = userInfoVoBaseResultEntityVo.data.userInfo.readerId;//TODO 后续修改
                            mUser.mHeadImg = ImageUrlUtils.getDownloadOriginalImagePath(userInfoVoBaseResultEntityVo.data.userInfo.image);
                            mUser.mIsMan = userInfoVoBaseResultEntityVo.data.userInfo.gender == 1;
                            mUser.mPhone = userInfoVoBaseResultEntityVo.data.userInfo.phone;
                            mUser.mCardName = userInfoVoBaseResultEntityVo.data.userInfo.cardName;
                            mUser.mIdCard = userInfoVoBaseResultEntityVo.data.userInfo.idCard;
                            mUser.mNickName = userInfoVoBaseResultEntityVo.data.userInfo.nickName;

                            mUserInfo.mUser = mUser;

                            mUserInfo.mCollectionEBookSum = userInfoVoBaseResultEntityVo.data.ebookCollectionNum;
                            mUserInfo.mCollectionVideoSum = userInfoVoBaseResultEntityVo.data.videosCollectionNum;
                            mUserInfo.mBuyBookShelfSum = userInfoVoBaseResultEntityVo.data.buyCount;

                            mUserInfo.mActionCount = userInfoVoBaseResultEntityVo.data.activityCount;
                            mUserInfo.mAppointCount = userInfoVoBaseResultEntityVo.data.appointCount;
                            mUserInfo.mBorrowOverdueSum = userInfoVoBaseResultEntityVo.data.borrowOverdueSum;
                            mUserInfo.mOverdueUnReadSum = userInfoVoBaseResultEntityVo.data.borrowOverdueUnReadSum;
                            mUserInfo.mBorrowSum = userInfoVoBaseResultEntityVo.data.borrowSum;
                            mUserInfo.mBorrowTotal = userInfoVoBaseResultEntityVo.data.totalBorrowSum;
                            mUserInfo.mNoteCount = userInfoVoBaseResultEntityVo.data.noteCount;
                            mUserInfo.mIsBorrowOverdue = userInfoVoBaseResultEntityVo.data.borrowOverdueIsExist == 1;
                            mUserInfo.mUpcomingOverdueCount = userInfoVoBaseResultEntityVo.data.upcomingOverdueBookNumber;

                            mUserInfo.mUnreadMsgCount = unreadMsgCountVoBaseResultEntityVo.data.unreadCount;

                            mUserInfo.mUnreadOverdueMsgCount = unreadOverdueMsgCountVoBaseResultEntityVo.data.messageCount;

                            mRxBus.post(mUserInfo);
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new Observer<Void>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Void aVoid) {

                    }
                });
    }

//    /**
//     * 获取用户信息
//     *
//     * @return 用户信息
//     */
//    public UserInfoBean getUserInfo() {
//        if (mUserInfo == null || mUser == null) {
//            return null;
//        }
//        return mUserInfo;
//    }

    /**
     * 退出登录
     */
    public void logout() {
        //TODO 删除TOKEN
        //TODO 删除关注馆
        mToken = null;
        SharedPreferencesUtil.getInstance().remove("TOKEN");

        SharedPreferencesUtil.getInstance().putString("BAR_CODE_TOKEN", "");
        SharedPreferencesUtil.getInstance().putInt("BAR_CODE_TOKEN_VALID_NUM", 0);
        SharedPreferencesUtil.getInstance().putInt("TOKEN_ID", 0);
        LibraryRepository.getInstance().removeAttentionLib();

    }

    public Observable<List<UserHeadBean>> getUserHeadList() {
        return CloudLibraryApi.getInstance().getHeadImgList()
                .map(new Func1<BaseResultEntityVo<UserHeadListResultVo>, List<UserHeadBean>>() {
                    @Override
                    public List<UserHeadBean> call(BaseResultEntityVo<UserHeadListResultVo> userHeadListResultVoBaseResultEntityVo) {
                        if (userHeadListResultVoBaseResultEntityVo.status == 200) {
                            if (userHeadListResultVoBaseResultEntityVo.data != null
                                    && userHeadListResultVoBaseResultEntityVo.data.result != null
                                    && userHeadListResultVoBaseResultEntityVo.data.result.size() > 0) {
                                List<UserHeadBean> userHeadList = new ArrayList<>();
                                for (UserHeadListItemVo item : userHeadListResultVoBaseResultEntityVo.data.result) {
                                    UserHeadBean bean = new UserHeadBean();
                                    bean.id = item.id;
                                    bean.image = ImageUrlUtils.getDownloadOriginalImagePath(item.image);
                                    bean.tagImage = item.image;
                                    userHeadList.add(bean);
                                }
                                return userHeadList;
                            } else {
                                return null;
                            }
                        } else {
                            if (userHeadListResultVoBaseResultEntityVo.data.errorCode == 30100) {
                                logout();
                            }
                            throw new ServerException(userHeadListResultVoBaseResultEntityVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<List<UserHeadBean>>());
    }

    public Observable<Boolean> modifyHeadImg(String readerId, final String image, boolean isUploadFile) {
        return CloudLibraryApi.getInstance().modifyHeadImg(readerId, image, isUploadFile)
                .map(new Func1<BaseResultEntityVo<BaseDataResultVo>, Boolean>() {
                    @Override
                    public Boolean call(BaseResultEntityVo<BaseDataResultVo> baseDataResultVoBaseResultEntityVo) {
                        if (baseDataResultVoBaseResultEntityVo.status == 200) {
                            if (mUser != null) {
                                mUser.mHeadImg = image;
                                SharedPreferencesUtil.getInstance().putObject(SPKeyConstant.LOGIN_USER, mUser);
                            }
                            return true;
                        } else {
                            if (baseDataResultVoBaseResultEntityVo.data.errorCode == 30100) {
                                logout();
                            }
                            throw new ServerException(baseDataResultVoBaseResultEntityVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    public Observable<Boolean> updateNickName(String id, final String nickName) {
        return CloudLibraryApi.getInstance().updateNickName(id, nickName)
                .map(new Func1<BaseResultEntityVo<BaseDataResultVo>, Boolean>() {
                    @Override
                    public Boolean call(BaseResultEntityVo<BaseDataResultVo> baseDataResultVoBaseResultEntityVo) {
                        if (baseDataResultVoBaseResultEntityVo.status == 200) {
                            if (mUser != null) {
                                mUser.mNickName = nickName;
                                SharedPreferencesUtil.getInstance().putObject(SPKeyConstant.LOGIN_USER, mUser);
                            }
                            return true;
                        } else {
                            if (baseDataResultVoBaseResultEntityVo.data.errorCode == 30100) {
                                logout();
                            }
                            throw new ServerException(baseDataResultVoBaseResultEntityVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<Boolean>());
    }

    private void setTokenCount(int count) {
        SharedPreferencesUtil.getInstance().putInt("TOKEN_ID", count);
    }

    private int getTokenCount() {
        return SharedPreferencesUtil.getInstance().getInt("TOKEN_ID", 1);
    }

    private void setBarCodeToken(String token) {
        SharedPreferencesUtil.getInstance().putString("BAR_CODE_TOKEN", token);
    }

    private String getBarCodeToken() {
        return SharedPreferencesUtil.getInstance().getString("BAR_CODE_TOKEN");
    }

    private void setBarCodeTokenValidNum(int num) {
        SharedPreferencesUtil.getInstance().putInt("BAR_CODE_TOKEN_VALID_NUM", num);
    }

    private int getBarCodeTokenValidNum() {
        return SharedPreferencesUtil.getInstance().getInt("BAR_CODE_TOKEN_VALID_NUM");
    }

    public Observable<String> getUserTokenBarContent() {
        int tokenId = getTokenCount();
        String token = getBarCodeToken();
        int validNum = getBarCodeTokenValidNum();
        if (!TextUtils.isEmpty(token) && tokenId <= validNum) {
            String tokenBar = getTokenBar(token + tokenId);
            setTokenCount(tokenId + 1);
            return Observable.just(tokenBar);
        } else {
            if (mUser == null) {
                return Observable.create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        throw new ServerException(30100, "重新登录");
                    }
                }).onErrorResumeNext(new HttpResultFunc<String>());
            } else {
                return CloudLibraryApi.getInstance().refreshBarCode(String.valueOf(mUser.mReaderId))
                        .map(new Func1<BaseResultEntityVo<BarCodeReusltVo>, String>() {
                            @Override
                            public String call(BaseResultEntityVo<BarCodeReusltVo> barCodeReusltVoBaseResultEntityVo) {
                                if (barCodeReusltVoBaseResultEntityVo.status == 200) {
                                    setTokenCount(1);
                                    setBarCodeToken(barCodeReusltVoBaseResultEntityVo.data.barCodeToken);
                                    setBarCodeTokenValidNum(barCodeReusltVoBaseResultEntityVo.data.validNum);
                                    return getTokenBar(barCodeReusltVoBaseResultEntityVo.data.barCodeToken + 1);
                                } else {
                                    if (barCodeReusltVoBaseResultEntityVo.data.errorCode == 30100) {
                                        logout();
                                    }
                                    throw new ServerException(barCodeReusltVoBaseResultEntityVo.data.errorCode,
                                            barCodeReusltVoBaseResultEntityVo.data.message);
                                }
                            }
                        })
                        .onErrorResumeNext(new HttpResultFunc<String>());
            }
        }
    }


    private String getTokenBar(String key) {
        String finalCode;
        try {
            final MessageDigest msgDigest = MessageDigest.getInstance("SHA-384");
            msgDigest.update(key.getBytes());
            byte[] digest = msgDigest.digest();
            String otp = byte2Int(digest).substring(0, 7);

            String initialCode = String.valueOf((mUser.mReaderId * 10000000L) + Long.valueOf(otp));
            if (initialCode.length() < 17) {
                String random = randomNum(17 - initialCode.length());
                finalCode = random + "0" + initialCode;
            } else if (initialCode.length() == 17) {
                finalCode = "0" + initialCode;
            } else {
                finalCode = initialCode;
            }
        } catch (NoSuchAlgorithmException e) {
            finalCode = "";
        }
        return finalCode;
    }

    private String byte2Int(byte[] b) {
        StringBuilder iOutcome = new StringBuilder();

        for (byte aB : b) {
            iOutcome.append(Integer.toString(aB & 0xFF));
        }
        return iOutcome.toString();
    }

    private String randomNum(int length) {
        StringBuilder sb = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(rand.nextInt(9) + 1);
        }
        return sb.toString();
    }

    /**
     * 上报身份识别使用次数
     *
     * @param barCode 上报身份识别令牌内容
     */
    public Observable<Void> reportTokenCount(String barCode) {
        if (mUser == null) {
            return Observable.create(new Observable.OnSubscribe<Void>() {
                @Override
                public void call(Subscriber<? super Void> subscriber) {
                    throw new ServerException(30100, "重新登录");
                }
            }).onErrorResumeNext(new HttpResultFunc<Void>());
        } else {
            return CloudLibraryApi.getInstance().reportBarCodeCount(String.valueOf(mUser.mReaderId), barCode)
                    .map(new Func1<BaseResultEntityVo<BaseDataResultVo>, Void>() {
                        @Override
                        public Void call(BaseResultEntityVo<BaseDataResultVo> baseDataResultVoBaseResultEntityVo) {
                            if (baseDataResultVoBaseResultEntityVo.status == 200) {
                                return null;
                            } else {
                                if (baseDataResultVoBaseResultEntityVo.status == 401
                                        && baseDataResultVoBaseResultEntityVo.data.errorCode == 30100) {
                                    logout();
                                }
                                throw new ServerException(baseDataResultVoBaseResultEntityVo.data.errorCode,
                                        baseDataResultVoBaseResultEntityVo.data.message);
                            }
                        }
                    })
                    .onErrorResumeNext(new HttpResultFunc<Void>());
        }

    }

    /**
     * 获取人脸识别图片URL
     *
     * @return 图片URL
     */
    public Observable<String> getFaceRecognitionImage() {
        if (mUser == null) {
            return Observable.create(new Observable.OnSubscribe<String>() {
                @Override
                public void call(Subscriber<? super String> subscriber) {
                    throw new ServerException(30100, "重新登录");
                }
            }).onErrorResumeNext(new HttpResultFunc<String>());
        } else {
            return CloudLibraryApi.getInstance().getFaceRecognitionImage(String.valueOf(mUser.mIdCard))
                    .map(new Func1<BaseResultEntityVo<FaceImageVo>, String>() {
                        @Override
                        public String call(BaseResultEntityVo<FaceImageVo> faceImageVoBaseResultEntityVo) {
                            if (faceImageVoBaseResultEntityVo.status == 200) {
                                return ImageUrlUtils.getDownloadOriginalImagePath(faceImageVoBaseResultEntityVo.data.readerFaceImage);
                            } else {
                                if (faceImageVoBaseResultEntityVo.status == 401
                                        && faceImageVoBaseResultEntityVo.data.errorCode == 30100) {
                                    logout();
                                }
                                throw new ServerException(faceImageVoBaseResultEntityVo.data.errorCode, "");
                            }
                        }
                    })
                    .onErrorResumeNext(new HttpResultFunc<String>());
        }
    }
}
