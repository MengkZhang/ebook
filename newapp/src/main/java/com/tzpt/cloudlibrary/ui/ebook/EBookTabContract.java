package com.tzpt.cloudlibrary.ui.ebook;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.bean.ClassifyTwoLevelBean;

import java.util.List;

/**
 * Created by tonyjia on 2018/5/21.
 */
public interface EBookTabContract {

    interface View extends BaseContract.BaseView {

        void showProgress();

        void setNetError();

        void setContentView();

        void setEBookClassificationList(List<ClassifyTwoLevelBean> bookClassifications);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void getEBookGradeList();
    }
}
