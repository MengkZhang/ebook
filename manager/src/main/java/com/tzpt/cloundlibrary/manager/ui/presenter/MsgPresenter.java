package com.tzpt.cloundlibrary.manager.ui.presenter;

import android.text.TextUtils;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseResponseCode;
import com.tzpt.cloundlibrary.manager.base.RxPresenter;
import com.tzpt.cloundlibrary.manager.bean.MsgInfo;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.MsgVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.ReadMsgVo;
import com.tzpt.cloundlibrary.manager.ui.contract.MessageContract;
import com.tzpt.cloundlibrary.manager.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 消息列表
 * Created by Administrator on 2017/7/3.
 */

public class MsgPresenter extends RxPresenter<MessageContract.View> implements
        MessageContract.Presenter,
        BaseResponseCode {

    @Override
    public void getMsgFromRemote(final int pageNum) {
        Subscription subscription = DataRepository.getInstance().getMsgList(pageNum)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MsgVo>() {
                    @Override
                    public void onCompleted() {
                        mView.complete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.showMsgDialog(R.string.network_fault, pageNum == 1);
                        }
                    }

                    @Override
                    public void onNext(MsgVo msgVo) {
                        if (mView != null) {
                            if (null != msgVo) {
                                if (msgVo.status == CODE_SUCCESS) {
                                    if (msgVo.data != null) {
                                        List<MsgVo.MsgList> msgList = msgVo.data.resultList;
                                        if (msgList != null && msgList.size() > 0) {
                                            List<MsgInfo> msgInfoList = new ArrayList<>();
                                            for (MsgVo.MsgList msg : msgList) {
                                                MsgInfo info = new MsgInfo();
                                                info.mId = msg.newsId;
                                                info.mMsg = msg.content;
                                                info.mDateValid = (DateUtils.compareEndDateThanTodayDate(msg.endDate));
                                                info.mDateInfo = (!TextUtils.isEmpty(msg.startDate) && !TextUtils.isEmpty(msg.endDate))
                                                        ? (msg.startDate + " - " + msg.endDate) : "";
                                                info.mIsRead = ("1".equals(msg.status));//消息状态1表示已读，0未读
                                                msgInfoList.add(info);
                                            }
                                            mView.showMsgList(msgInfoList, msgVo.data.totalCount, pageNum == 1);
                                        } else {
                                            mView.showMsgListEmpty(pageNum == 1);
                                        }
                                    }
                                } else {
                                    if (msgVo.data.errorCode == ERROR_CODE_KICK_OUT) {
                                        mView.setNoLoginPermission(R.string.kicked_offline);
                                    } else if (msgVo.data.errorCode == ERROR_CODE_1006) {
                                        mView.setNoLoginPermission(R.string.operate_timeout);
                                    } else {
                                        mView.showMsgDialog(R.string.error_code_500, pageNum == 1);
                                    }
                                }
                            } else {
                                mView.showMsgDialog(R.string.network_fault, pageNum == 1);
                            }
                        }
                    }
                });

        addSubscrebe(subscription);
    }

    @Override
    public void setReadMsgStatus(long newsId, final int position) {
        Subscription subscription = DataRepository.getInstance().setReadStatus(newsId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ReadMsgVo>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(ReadMsgVo readMsgVo) {
                        if (null != mView) {
                            mView.setReadMsgStatus(position);
                        }
                    }
                });
        addSubscrebe(subscription);
    }

//    @Override
//    public void delMsgByIdsArray(List<MsgInfo> list) {
//        if (null != list && list.size() > 0) {
//            JSONArray idJsonArray = new JSONArray();
//            for (MsgInfo item : list) {
//                if (item.mIsChoose) {
//                    idJsonArray.put(item.mId);
//                }
//            }
//            //如果没有id数组数据,则不提交删除接口
//            if (idJsonArray.length() < 1) {
//                return;
//            }
//            if (null != mView) {
//                mView.showProgressDialog();
//            }
//            Subscription subscription = DataRepository.getInstance().delMsgByIds(idJsonArray.toString())
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Observer<ReadMsgVo>() {
//                        @Override
//                        public void onCompleted() {
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                            if (null != mView) {
//                                mView.dismissProgressDialog();
//                            }
//                        }
//
//                        @Override
//                        public void onNext(ReadMsgVo readMsgVo) {
//                            if (null != mView) {
//                                mView.dismissProgressDialog();
//                                mView.deleteMsgSuccess();
//                            }
//                        }
//                    });
//            addSubscrebe(subscription);
//        }
//    }
}
