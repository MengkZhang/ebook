package com.tzpt.cloudlibrary.ui.library;

import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseResultEntityVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.HtmlUrlVo;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 图书馆借阅须知
 * Created by tonyjia on 2018/8/27.
 */
public class BorrowingIntroducePresenter extends RxPresenter<BorrowingIntroduceContract.View> implements
        BorrowingIntroduceContract.Presenter {

    @Override
    public void getLibIntroduce(String libCode) {
        mView.showProgress();
        Subscription subscription = DataRepository.getInstance().getBorrowingIntroduces(libCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResultEntityVo<HtmlUrlVo>>() {
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
                    public void onNext(BaseResultEntityVo<HtmlUrlVo> htmlUrlVo) {
                        if (null != mView) {
                            if (htmlUrlVo.status == 200 && htmlUrlVo.data != null) {
                                mView.setLibIntroduce(htmlUrlVo.data.url);
                            } else {
                                mView.setNetError();
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }
}
