package com.tzpt.cloudlibrary.ui.account.collection;

import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.modle.UserRepository;

/**
 * Created by Mengk on 2019/3/18
 * Describe : 收藏presenter实现类
 */
public class CollectionPresenter extends RxPresenter<CollectionContract.View> implements CollectionContract.Presenter {

    CollectionPresenter() {

    }

    @Override
    public void getLocalUserInfo() {
        //收藏的电子书数量
        int collectionEBookSum = UserRepository.getInstance().getUserCollectionEBookSum();
        mView.setCollectionEBookCount(collectionEBookSum);
        //收藏的视频数量
        int collectionVideoSum = UserRepository.getInstance().getUserCollectionVideoSum();
        mView.setCollectionVideoCount(collectionVideoSum);
    }
}
