package com.tzpt.cloudlibrary.ui.paperbook;

import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.bean.ClassifyInfo;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseResultEntityVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BookCategoryVo;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by tonyjia on 2018/5/22.
 */
public class BookTabPresenter extends RxPresenter<BookTabContract.View> implements
        BookTabContract.Presenter {
    @Override
    public void getBookClassificationList() {
        mView.showProgress();
        Subscription subscription = DataRepository.getInstance().getBookCategory()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResultEntityVo<List<BookCategoryVo>>>() {
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
                    public void onNext(BaseResultEntityVo<List<BookCategoryVo>> listBaseResultEntityVo) {
                        if (mView != null) {
                            mView.setContentView();
                            if (listBaseResultEntityVo.status == 200) {
                                List<ClassifyInfo> bookClassifications = new ArrayList<>();
                                ClassifyInfo classify = new ClassifyInfo();
                                classify.id = 0;
                                classify.code = "";
                                classify.name = "全部分类";
                                bookClassifications.add(classify);
                                for (BookCategoryVo item : listBaseResultEntityVo.data) {
                                    ClassifyInfo classifyInfo = new ClassifyInfo();
                                    classifyInfo.code = item.categoryCode;
                                    classifyInfo.id = item.categoryId;
                                    classifyInfo.name = item.categoryName;
                                    bookClassifications.add(classifyInfo);
                                }
                                mView.setBookClassificationList(bookClassifications);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);

    }
}
