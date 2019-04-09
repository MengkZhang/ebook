package com.tzpt.cloudlibrary.ui.account.borrow;

import android.text.TextUtils;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.base.data.Book;
import com.tzpt.cloudlibrary.base.data.Note;
import com.tzpt.cloudlibrary.business_bean.ReadNoteBean;
import com.tzpt.cloudlibrary.business_bean.ReadNoteGroupBean;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseResultEntityVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.NoteListItemVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.NoteListVo;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 读书笔记
 * Created by ZhiqiangJia on 2017-08-24.
 */
public class ReaderNotesPresenter extends RxPresenter<ReaderNotesContract.View> implements
        ReaderNotesContract.Presenter {

    @Override
    public void getReaderNotesList(final int pageNum) {
        String idCard = UserRepository.getInstance().getLoginUserIdCard();
        if (!TextUtils.isEmpty(idCard)) {
            Subscription subscription = DataRepository.getInstance().getReaderNotes(idCard, pageNum, 10)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseResultEntityVo<NoteListVo>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (null != mView) {
                                mView.setNetError();
                            }
                        }

                        @Override
                        public void onNext(BaseResultEntityVo<NoteListVo> noteListVoBaseResultEntityVo) {
                            if (mView != null) {
                                if (noteListVoBaseResultEntityVo.status == 200) {
                                    if (noteListVoBaseResultEntityVo.data != null
                                            && noteListVoBaseResultEntityVo.data.bookList != null
                                            && noteListVoBaseResultEntityVo.data.bookList.size() > 0) {
                                        List<ReadNoteGroupBean> readerNotesList = new ArrayList<>();
                                        for (NoteListVo.BookNoteItemVo item : noteListVoBaseResultEntityVo.data.bookList) {
                                            ReadNoteGroupBean bookBean = new ReadNoteGroupBean();
                                            bookBean.mBook = new Book();
                                            bookBean.mBook.mBookId = item.bookId;
                                            bookBean.mBook.mName = "《" + item.bookName + "》";
                                            if (item.resultList != null
                                                    && item.resultList.size() > 0) {
                                                List<ReadNoteBean> notesList = new ArrayList<>();
                                                for (NoteListItemVo noteItem : item.resultList) {
                                                    ReadNoteBean readNoteBean = new ReadNoteBean();
                                                    Note noteBean = new Note();
                                                    noteBean.mId = noteItem.id;
                                                    noteBean.mContent = noteItem.readingNote;
                                                    noteBean.mModifyDate = noteItem.noteDateStr;

                                                    readNoteBean.mNote = noteBean;
                                                    readNoteBean.mBorrowBookId = noteItem.borrowerBookId;
                                                    readNoteBean.mBuyBookId = noteItem.buyBookId;
                                                    notesList.add(readNoteBean);
                                                }
                                                bookBean.mNoteList = notesList;
                                                readerNotesList.add(bookBean);
                                            }
                                        }
                                        mView.setReaderNotesList(readerNotesList, noteListVoBaseResultEntityVo.data.totalCount, noteListVoBaseResultEntityVo.data.matchCount, pageNum == 1);
                                    } else {
                                        mView.setReaderNotesEmpty(pageNum == 1);
                                    }
                                } else if (noteListVoBaseResultEntityVo.status == 401) {
                                    if (noteListVoBaseResultEntityVo.data.errorCode == 30100) {
                                        mView.pleaseLoginTip();
                                    } else {
                                        mView.setNetError();
                                    }
                                } else {
                                    mView.setNetError();
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }

    @Override
    public void delNote(long noteId) {
        Subscription subscription = DataRepository.getInstance().delNote(noteId)
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
                                if (((ApiException) e).getCode() == 30100) {
                                    mView.pleaseLoginTip();
                                } else {
                                    mView.showToastMsg(R.string.failure);
                                }
                            } else {
                                mView.showToastMsg("删除失败");
                            }
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (mView != null) {
                            if (aBoolean) {
                                mView.delNoteSuccess();
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }
}
