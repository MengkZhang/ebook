package com.tzpt.cloundlibrary.manager.ui.presenter;

import com.tzpt.cloundlibrary.manager.base.RxPresenter;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.ui.contract.ReaderNationSelectContract;

/**
 * 民族选择
 * Created by tonyjia on 2018/9/10.
 */
public class ReaderNationSelectPresenter extends RxPresenter<ReaderNationSelectContract.View> implements
        ReaderNationSelectContract.Presenter {
    @Override
    public void getReaderNationList() {
        mView.setReaderNationList(DataRepository.getInstance().getReaderNationList());
    }
}
