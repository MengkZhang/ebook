package com.tzpt.cloudlibrary.ui.account;

import android.text.TextUtils;

import com.tzpt.cloudlibrary.CloudLibraryApplication;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;

import java.util.Arrays;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by tonyjia on 2018/11/6.
 */
public class UserInfoPresenter extends RxPresenter<UserInfoContract.View> implements
        UserInfoContract.Presenter {
    @Override
    public void getUserInfo() {
        String idCard = UserRepository.getInstance().getLoginUserIdCard();
        String cardName = UserRepository.getInstance().getLoginCardName();
        String nickName = UserRepository.getInstance().getLoginNickName();
        String headImg = UserRepository.getInstance().getLoginHeadImage();
        String phone = UserRepository.getInstance().getLoginPhone();
        boolean isMan = UserRepository.getInstance().isMan();

        mView.setUserCardName(cardName);
        mView.setUserNickName(!TextUtils.isEmpty(nickName) ? nickName : formatCardName(cardName, isMan));
        mView.setUserIdCard(formatIdCard(idCard));
        mView.setUserHeadImage(headImg, isMan);
        mView.setUserPhone(phone);
    }

    @Override
    public void getUserPhone() {
        String phone = UserRepository.getInstance().getLoginPhone();
        if (!TextUtils.isEmpty(phone)) {
            mView.setUserPhone(phone);
        }
    }

    @Override
    public void submitUserHead(final String imagePath) {
        mView.showLoadingProgress();
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (TextUtils.isEmpty(readerId)) {
            return;
        }
        Subscription subscription = UserRepository.getInstance().modifyHeadImg(readerId, imagePath, true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.dismissLoadingProgress();
                            if (e instanceof ApiException) {
                                switch (((ApiException) e).getCode()) {
                                    case 30100:
                                        mView.pleaseLoginTips();
                                        break;
                                    default:
                                        mView.changeUserHeadFailed();
                                        break;
                                }
                            } else {
                                mView.changeUserHeadFailed();
                            }
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (mView != null) {
                            if (aBoolean) {
                                mView.dismissLoadingProgress();
                                mView.changeUserHeadSuccess();
                            } else {
                                mView.changeUserHeadFailed();
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 格式化身份证号码
     *
     * @param idCard
     */
    private String formatIdCard(String idCard) {
        if (!TextUtils.isEmpty(idCard) && idCard.length() >= 18) {
            String myIdCard = idCard.substring(10, 16);
            return idCard.replace(myIdCard, "****");
        }
        return idCard;
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
}
