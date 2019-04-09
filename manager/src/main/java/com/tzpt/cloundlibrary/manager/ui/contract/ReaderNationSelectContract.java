package com.tzpt.cloundlibrary.manager.ui.contract;

import com.tzpt.cloundlibrary.manager.base.BaseContract;

import java.util.List;

/**
 * Created by tonyjia on 2018/9/10.
 * 民族选择
 */
public interface ReaderNationSelectContract {

    interface View extends BaseContract.BaseView {

        void setReaderNationList(List<String> nationList);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void getReaderNationList();
    }
}
