package com.tzpt.cloudlibrary.ui.library;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.tzpt.cloudlibrary.CloudLibraryApplication;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;
import com.tzpt.cloudlibrary.utils.BitmapCompressionUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 我要留言
 * Created by ZhiqiangJia on 2018-01-12.
 */
public class LibraryToLeaveMessagePresenter extends RxPresenter<LibraryToLeaveMessageContract.View> implements
        LibraryToLeaveMessageContract.Presenter {

    private String mLibraryCode;

    @Override
    public void setLibraryCode(String libraryCode) {
        this.mLibraryCode = libraryCode;
    }

    @Override
    public void pubMsg(String contact, String content, String imagePath) {
        if (TextUtils.isEmpty(content)) {
            mView.showMsgDialog(R.string.content_not_empty);
            return;
        }
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (TextUtils.isEmpty(readerId)) {
            mView.pleaseLogin();
            return;
        }
        if (null != mLibraryCode) {
            mView.showPostDialog();
            if (TextUtils.isEmpty(imagePath)) {
                commitMsg(contact, content, null);
            } else {
                savePicByTakePhoto(contact, content, imagePath);
            }
        }

    }

    private void commitMsg(String contact, String content, String imagePath) {
        String readerId = UserRepository.getInstance().getLoginReaderId();

        Subscription subscription = DataRepository.getInstance().pubMsg(contact, content, imagePath, mLibraryCode, Integer.parseInt(readerId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.dismissPostDialog();
                            if (e instanceof ApiException) {
                                if (((ApiException) e).getCode() == 30100) {
                                    mView.pleaseLoginTip();
                                } else {
                                    mView.showMsgDialog(R.string.leave_submit_failure);
                                }
                            } else {
                                mView.showMsgDialog(R.string.network_fault);
                            }
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (mView != null) {
                            mView.dismissPostDialog();
                            if (aBoolean) {
                                mView.postSuccess();
                            } else {
                                mView.showMsgDialog(R.string.leave_submit_failure);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void savePicByTakePhoto(final String contact, final String content, final String imagePath) {
        Subscription subscription = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                int angle = BitmapCompressionUtil.readPictureDegree(imagePath);
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                Bitmap photoBitmap = BitmapCompressionUtil.compressBitmap(bitmap);
                Bitmap bitmap1 = BitmapCompressionUtil.rotaingImageView(angle, photoBitmap);
                if (null != bitmap && !bitmap.isRecycled()) {
                    bitmap.recycle();
                    bitmap = null;
//                    BitmapCompressionUtil.deleteFileWithPath(imagePath);
                }
                String imgPath = saveJpeg(bitmap1);
                subscriber.onNext(imgPath);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.dismissPostDialog();
                            mView.showMsgDialog(R.string.network_fault);
                        }
                    }

                    @Override
                    public void onNext(String imgPath) {
                        if (null != mView) {
                            if (!TextUtils.isEmpty(imgPath)) {
                                commitMsg(contact, content, imgPath);
                            } else {
                                mView.dismissPostDialog();
                                mView.showMsgDialog(R.string.network_fault);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);

    }

    private String saveJpeg(Bitmap bm) {
        String jpegName = "";
//        String savePath = Utils.getContext().getExternalCacheDir() + "/ytsg/";
        String savePath = CloudLibraryApplication.getAppContext().getExternalCacheDir() + "/ytsg/";
        File folder = new File(savePath);
        if (!folder.exists()) {
            folder = new File(savePath);
            folder.mkdir();
        }
        long dataTake = System.currentTimeMillis();
        jpegName = savePath + dataTake + ".jpg";
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            bm.recycle();
        }
        return jpegName;
    }
}
