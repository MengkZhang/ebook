package com.tzpt.cloudlibrary.ui.account.borrow;

import android.text.TextUtils;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 读书笔记
 * Created by ZhiqiangJia on 2017-08-29.
 */
public class ReadingNoteEditPresenter extends RxPresenter<ReadingNoteEditContract.View> implements
        ReadingNoteEditContract.Presenter {

    @Override
    public void saveUserReadingNote(long borrowBookId, long buyBookId, final long noteId, final String content) {
        if (TextUtils.isEmpty(content)) {
            mView.showToastTips(R.string.note_content_empty);
            return;
        }
        if (noteId <= 0) {
            String idCard = UserRepository.getInstance().getLoginUserIdCard();
            if (TextUtils.isEmpty(idCard)) {
                return;
            }
            if (content.length() > 1000) {
                mView.showDialogTips("字数不能超过1000字！");
                return;
            }
            mView.showProgressDialog();
            Subscription subscription = DataRepository.getInstance().noteAdd(borrowBookId, buyBookId, idCard, content)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Long>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (mView != null) {
                                mView.dismissProgressDialog();
                                if (e instanceof ApiException) {
                                    if (((ApiException) e).getCode() == 30100) {
                                        mView.pleaseLoginTip();
                                    } else {
                                        mView.showToastTips(R.string.save_failure);
                                    }
                                } else {
                                    mView.showToastTips(R.string.save_failure);
                                }
                            }
                        }

                        @Override
                        public void onNext(Long aLong) {
                            if (mView != null) {
                                mView.dismissProgressDialog();
                                if (aLong > 0) {
                                    mView.saveUserReadingNoteSuccess(aLong);
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        } else {
            if (content.length() > 1000) {
                mView.showDialogTips("字数不能超过1000字！");
                return;
            }
            mView.showProgressDialog();
            Subscription subscription = DataRepository.getInstance().noteModify(noteId, content)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (mView != null) {
                                mView.dismissProgressDialog();
                                if (e instanceof ApiException) {
                                    if (((ApiException) e).getCode() == 30100) {
                                        mView.pleaseLoginTip();
                                    } else {
                                        mView.showToastTips(R.string.save_failure);
                                    }
                                } else {
                                    mView.showToastTips(R.string.save_failure);
                                }
                            }
                        }

                        @Override
                        public void onNext(Boolean aBoolean) {
                            if (mView != null) {
                                mView.dismissProgressDialog();
                                if (aBoolean) {
                                    mView.saveUserReadingNoteSuccess(noteId);
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }

//
//    @Override
//    public void getUserNote(int id) {
//        mView.showProgressDialog();
//        if (mFromType == 2) {
//            Subscription subscription = DataRepository.getInstance().getSelfBuyBookNote(id)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Observer<BaseResultEntityVo<NoteListItemVo>>() {
//                        @Override
//                        public void onCompleted() {
//
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                            if (null != mView) {
//                                mView.dismissProgressDialog();
//                                mView.showErrorMsg(R.string.network_fault);
//                            }
//                        }
//
//                        @Override
//                        public void onNext(BaseResultEntityVo<NoteListItemVo> noteListItemVo) {
//                            if (mView != null) {
//                                mView.dismissProgressDialog();
//                                if (noteListItemVo.status == 200) {
//                                    if (noteListItemVo.data != null && !TextUtils.isEmpty(noteListItemVo.data.readingNote)) {
//                                        mView.setUserReadingNote(noteListItemVo.data.id, noteListItemVo.data.readingNote);
//                                    }
//                                } else if (noteListItemVo.status == 401) {
//                                    if (noteListItemVo.data.errorCode == 30100) {
//                                        DataRepository.getInstance().quit();
//                                        mView.pleaseLoginTip();
//                                    }
//                                } else {
//                                    mView.showErrorMsg(R.string.network_fault);
//                                }
//                            }
//                        }
//                    });
//            addSubscrebe(subscription);
//        } else {
//            Subscription subscription = DataRepository.getInstance().getBorrowBookDetail(id)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Observer<BaseResultEntityVo<BorrowBookDetailInfoVo>>() {
//                        @Override
//                        public void onCompleted() {
//
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                            if (null != mView) {
//                                mView.dismissProgressDialog();
//                                mView.showErrorMsg(R.string.network_fault);
//                            }
//                        }
//
//                        @Override
//                        public void onNext(BaseResultEntityVo<BorrowBookDetailInfoVo> borrowBookDetailInfoVoBaseResultEntityVo) {
//                            if (mView != null) {
//                                mView.dismissProgressDialog();
//                                if (borrowBookDetailInfoVoBaseResultEntityVo.status == 200) {
//                                    if (borrowBookDetailInfoVoBaseResultEntityVo.data.libraryBorrowerNote != null &&
//                                            !TextUtils.isEmpty(borrowBookDetailInfoVoBaseResultEntityVo.data.libraryBorrowerNote.readingNote)) {
//                                        mView.setUserReadingNote(borrowBookDetailInfoVoBaseResultEntityVo.data.libraryBorrowerNote.id,
//                                                borrowBookDetailInfoVoBaseResultEntityVo.data.libraryBorrowerNote.readingNote);
//                                    }
//                                } else if (borrowBookDetailInfoVoBaseResultEntityVo.status == 401) {
//                                    if (borrowBookDetailInfoVoBaseResultEntityVo.data.errorCode == 30100) {
//                                        DataRepository.getInstance().quit();
//                                        mView.pleaseLoginTip();
//                                    }
//                                } else {
//                                    mView.showErrorMsg(R.string.network_fault);
//                                }
//                            }
//                        }
//                    });
//            addSubscrebe(subscription);
//        }
//    }
}
