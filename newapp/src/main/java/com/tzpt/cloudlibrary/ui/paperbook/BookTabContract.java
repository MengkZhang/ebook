package com.tzpt.cloudlibrary.ui.paperbook;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.bean.ClassifyInfo;

import java.util.List;

/**
 * Created by tonyjia on 2018/5/22.
 */

public interface BookTabContract {

    interface View extends BaseContract.BaseView {

        void showProgress();

        void setNetError();

        void setContentView();

        void setBookClassificationList(List<ClassifyInfo> bookClassifications);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void getBookClassificationList();
    }
}
