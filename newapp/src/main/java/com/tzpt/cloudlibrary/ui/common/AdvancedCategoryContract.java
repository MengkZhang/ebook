package com.tzpt.cloudlibrary.ui.common;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.bean.ClassifyInfo;
import com.tzpt.cloudlibrary.bean.ClassifyTwoLevelBean;

import java.util.List;

/**
 * 高级搜索协议
 * Created by ZhiqiangJia on 2017-09-25.
 */
public interface AdvancedCategoryContract {

    interface View extends BaseContract.BaseView {

        void setAdvancedGradeBeanList(List<ClassifyInfo> informationGradeList);

        void setEBookClassificationList(List<ClassifyTwoLevelBean> beanList);

        void showError();

    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void setFromType(int fromType);

        void getAdvancedCategoryList();
    }
}
