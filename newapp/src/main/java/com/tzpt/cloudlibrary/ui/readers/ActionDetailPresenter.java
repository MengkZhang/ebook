package com.tzpt.cloudlibrary.ui.readers;

import android.text.TextUtils;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.bean.ActionInfoBean;
import com.tzpt.cloudlibrary.business_bean.BaseListResultData;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;
import com.tzpt.cloudlibrary.modle.remote.newpojo.ActionListItemVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.ActionListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseResultEntityVo;
import com.tzpt.cloudlibrary.ui.map.LocationManager;
import com.tzpt.cloudlibrary.utils.ImageUrlUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/11/13.
 */

public class ActionDetailPresenter extends RxPresenter<ActionDetailContract.View>
        implements ActionDetailContract.Presenter {
    private String mKeyword;
    private String mLibCode;

    @Override
    public void getActionId(int position) {
        if (DataRepository.getInstance().getActionList() != null
                && DataRepository.getInstance().getActionList().size() > 0) {
            mView.setActionId(DataRepository.getInstance().getActionList().get(position).mId, DataRepository.getInstance().getActionList().size());
        }
    }

    @Override
    public void getActionDetailInfo(int actionId, int fromSearch, String keyword, String libCode) {
        mKeyword = keyword;
        mLibCode = libCode;
        mView.showLoadingStatus();

        String idCard = UserRepository.getInstance().getLoginUserIdCard();

        Subscription subscription = DataRepository.getInstance().getActionDetailInfo(actionId, fromSearch, idCard, keyword, libCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResultEntityVo<ActionListItemVo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.loadActionDetailFailed();
                        }
                    }

                    @Override
                    public void onNext(BaseResultEntityVo<ActionListItemVo> actionListItemVoBaseResultEntityVo) {
                        if (mView != null) {
                            if (actionListItemVoBaseResultEntityVo.status == 200
                                    && actionListItemVoBaseResultEntityVo.data != null) {
                                ActionInfoBean actionInfo = new ActionInfoBean();
                                actionInfo.mId = actionListItemVoBaseResultEntityVo.data.id;
                                actionInfo.mAddress = actionListItemVoBaseResultEntityVo.data.address;
                                actionInfo.mAllowApplyNow = actionListItemVoBaseResultEntityVo.data.allowApplyNow;
                                actionInfo.mEndDateTime = actionListItemVoBaseResultEntityVo.data.endDate;
                                if (!TextUtils.isEmpty(actionListItemVoBaseResultEntityVo.data.htmlUrl)) {
                                    actionInfo.mUrl = actionListItemVoBaseResultEntityVo.data.htmlUrl;
                                }
                                actionInfo.mContactName = actionListItemVoBaseResultEntityVo.data.contactName;
                                actionInfo.mContactPhone = actionListItemVoBaseResultEntityVo.data.contactPhone;
                                actionInfo.mImage = ImageUrlUtils.getDownloadOriginalImagePath(actionListItemVoBaseResultEntityVo.data.image);
                                actionInfo.mEnrollment = actionListItemVoBaseResultEntityVo.data.enrollment;
                                actionInfo.mIsApply = actionListItemVoBaseResultEntityVo.data.isApply;
                                actionInfo.mSponsor = actionListItemVoBaseResultEntityVo.data.source;
                                actionInfo.mStartDateTime = actionListItemVoBaseResultEntityVo.data.startDate;
                                actionInfo.mStatus = actionListItemVoBaseResultEntityVo.data.status;
                                actionInfo.mSummary = actionListItemVoBaseResultEntityVo.data.summary;
                                actionInfo.mTitle = actionListItemVoBaseResultEntityVo.data.title;
                                actionInfo.mContent = actionListItemVoBaseResultEntityVo.data.content;
                                actionInfo.mApplyStatus = actionListItemVoBaseResultEntityVo.data.applyStatus;
                                if (TextUtils.isEmpty(actionInfo.mUrl)) {
                                    mView.loadActionDetailFailed();
                                } else {
                                    mView.setActionDetailInfo(actionInfo);
                                }
                            } else {
                                mView.loadActionDetailFailed();
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void getActionList(int type, final int pageNum) {
        if (type == 0) {
            if (TextUtils.isEmpty(mLibCode)) {
                Subscription subscription = DataRepository.getInstance().getOurReadersList(pageNum, 20, mKeyword,
                        LocationManager.getInstance().getLocationAdCode())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<BaseListResultData<ActionInfoBean>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                            }

                            @Override
                            public void onNext(BaseListResultData<ActionInfoBean> actionInfoBeanBaseListResultData) {
                                if (mView != null) {
                                    if (actionInfoBeanBaseListResultData != null
                                            && actionInfoBeanBaseListResultData.mResultList != null
                                            && actionInfoBeanBaseListResultData.mResultList.size() > 0) {
                                        DataRepository.getInstance().saveActionList(actionInfoBeanBaseListResultData.mResultList, pageNum == 1);
                                        mView.getActionListSuccess(actionInfoBeanBaseListResultData.mResultList);
                                    }
                                }
                            }
                        });
                addSubscrebe(subscription);
            } else {
                Subscription subscription = DataRepository.getInstance().getLibActionList(mLibCode, pageNum, 20, mKeyword)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<BaseListResultData<ActionInfoBean>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(BaseListResultData<ActionInfoBean> actionInfoBeanBaseListResultData) {
                                if (mView != null) {
                                    if (actionInfoBeanBaseListResultData != null
                                            && actionInfoBeanBaseListResultData.mResultList != null
                                            && actionInfoBeanBaseListResultData.mResultList.size() > 0) {
                                        DataRepository.getInstance().saveActionList(actionInfoBeanBaseListResultData.mResultList, pageNum == 1);
                                        mView.getActionListSuccess(actionInfoBeanBaseListResultData.mResultList);
                                    }
                                }
                            }
                        });
                addSubscrebe(subscription);
            }

        } else {
            String idCard = UserRepository.getInstance().getLoginUserIdCard();
            if (TextUtils.isEmpty(idCard)) {
                return;
            }
            Subscription subscription = DataRepository.getInstance().getAppliedActionList(idCard, pageNum, 20)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseListResultData<ActionInfoBean>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (mView != null) {
                                if (e instanceof ApiException) {
                                    if (((ApiException) e).getCode() == 30100) {
                                        mView.pleaseLogin();
                                    }
                                }
                                //TODO 加载失败
                            }
                        }

                        @Override
                        public void onNext(BaseListResultData<ActionInfoBean> actionInfoBeanBaseListResultData) {
                            if (mView != null) {
                                if (actionInfoBeanBaseListResultData != null
                                        && actionInfoBeanBaseListResultData.mResultList != null
                                        && actionInfoBeanBaseListResultData.mResultList.size() > 0) {
                                    mView.getActionListSuccess(actionInfoBeanBaseListResultData.mResultList);
                                    DataRepository.getInstance().saveActionList(actionInfoBeanBaseListResultData.mResultList, pageNum == 1);
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }

    @Override
    public void toSignUp(int activityId) {

        String idCard = UserRepository.getInstance().getLoginUserIdCard();
        if (TextUtils.isEmpty(idCard)) {
            mView.pleaseLogin();
            return;
        }
        Subscription subscription = DataRepository.getInstance().applyAction(activityId, idCard, null, null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            if (e instanceof ApiException) {
                                switch (((ApiException) e).getCode()) {
                                    case 5://报名已满
                                        mView.showToastMsg(R.string.the_registration_is_full);
                                        break;
                                    case 30602:
                                        mView.showToastMsg(R.string.action_been_start_cant_apply);
                                        break;
                                    case 30603:
                                        mView.showToastMsg(R.string.action_register_user_tip);
                                        break;
                                    case 30604:
                                        ApplyActionMessage apply1 = new ApplyActionMessage();
                                        apply1.mActionStatus = "已报名";
                                        EventBus.getDefault().post(apply1);
                                        mView.showToastMsg(R.string.action_been_apply);
                                        break;
                                    case 30605:// 报名截止
                                        ApplyActionMessage apply2 = new ApplyActionMessage();
                                        apply2.mActionStatus = "报名截止";
                                        EventBus.getDefault().post(apply2);
                                        mView.showToastMsg("报名截止！");
                                        break;
                                    case 30606:// 活动结束
                                        ApplyActionMessage apply3 = new ApplyActionMessage();
                                        apply3.mActionStatus = "已结束";
                                        EventBus.getDefault().post(apply3);
                                        mView.showToastMsg("已结束！");
                                        break;
//                                    case 30607:// 活动未开启报名
//                                        ApplyActionMessage apply4 = new ApplyActionMessage();
//                                        apply4.mActionStatus = "已报名";
//                                        EventBus.getDefault().post(apply4);
//                                        mView.showToastMsg("已报名！");
//                                        break;
                                    case 30608:// 名额已满
                                        ApplyActionMessage apply5 = new ApplyActionMessage();
                                        apply5.mActionStatus = "报名已满";
                                        EventBus.getDefault().post(apply5);
                                        mView.showToastMsg("报名已满！");
                                        break;
                                    case 30100:
                                        mView.pleaseLoginTip();
                                        break;
                                    default:
                                        mView.showToastMsg(R.string.network_fault);
                                        break;
                                }
                            } else {
                                mView.showToastMsg(R.string.network_fault);
                            }
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (mView != null) {
                            if (aBoolean) {
                                mView.showToastMsg(R.string.action_apply_success);
                                mView.applyActionSuccess();
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }
}
