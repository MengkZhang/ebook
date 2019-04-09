package com.tzpt.cloudlibrary.ui.account.card;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.tzpt.cloudlibrary.CloudLibraryApplication;
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
 * 设置人脸识别-上传图片
 * Created by tonyjia on 2018/3/13.
 */
public class FacePreviewPresenter extends RxPresenter<FacePreviewContract.View> implements
        FacePreviewContract.Presenter {

    private String mLocalFaceImagePath;
    private String mRealFaceImg;
    private boolean mIsReUpload = false;

    @Override
    public void setLocalImagePath(String localImagePath) {
        this.mLocalFaceImagePath = localImagePath;
    }

    //压缩图片后上传
    @Override
    public void updateLoadFaceImage() {
        if (TextUtils.isEmpty(mLocalFaceImagePath)) {
            return;
        }
        final String idCard = UserRepository.getInstance().getLoginUserIdCard();
        if (!TextUtils.isEmpty(idCard)) {
            mView.showUploadProgressDialog();
            if (mIsReUpload && !TextUtils.isEmpty(mRealFaceImg)) {//重新上传
                uploadFaceImage(idCard, mRealFaceImg);
                return;
            }
            Subscription subscription = Observable.create(new Observable.OnSubscribe<String>() {
                @Override
                public void call(Subscriber<? super String> subscriber) {

                    int angle = BitmapCompressionUtil.readPictureDegree(mLocalFaceImagePath);
                    Bitmap bitmap = BitmapFactory.decodeFile(mLocalFaceImagePath);
                    Bitmap photoBitmap = BitmapCompressionUtil.compressBitmap(bitmap);
                    Bitmap faceBitmap = BitmapCompressionUtil.rotaingImageView(angle, photoBitmap);
                    if (null != bitmap && !bitmap.isRecycled()) {
                        bitmap.recycle();
                        bitmap = null;
                        BitmapCompressionUtil.deleteFileWithPath(mLocalFaceImagePath);
                    }
                    mRealFaceImg = saveJpeg(faceBitmap);
                    subscriber.onNext(mRealFaceImg);
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
                                mView.dismissUploadProgressDialog();
                                mView.uploadFail();
                                mIsReUpload = true;
                            }
                        }

                        @Override
                        public void onNext(String faceImage) {
                            if (!TextUtils.isEmpty(faceImage)) {
                                uploadFaceImage(idCard, faceImage);
                            } else {
                                mView.dismissUploadProgressDialog();
                                mView.uploadFail();
                                mIsReUpload = true;
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
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

    /**
     * 上传人脸图片
     *
     * @param idCard    身份证
     * @param faceImage 图片地址
     */
    private void uploadFaceImage(String idCard, String faceImage) {
        Subscription subscription = DataRepository.getInstance().updateUserFaceImage(idCard, faceImage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.dismissUploadProgressDialog();
                            if (e instanceof ApiException) {
                                if (((ApiException) e).getCode() == 30100) {
                                    mView.pleaseLoginTip();
                                } else {
                                    mView.uploadFail();
                                    mIsReUpload = true;
                                }
                            } else {
                                mView.uploadFail();
                                mIsReUpload = true;
                            }
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (mView != null) {
                            mView.dismissUploadProgressDialog();
                            if (aBoolean) {
                                mView.uploadSuccess();
                            } else {
                                mView.uploadFail();
                                mIsReUpload = true;
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }
}
